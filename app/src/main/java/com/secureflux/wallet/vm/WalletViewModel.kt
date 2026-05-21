package com.secureflux.wallet.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.secureflux.wallet.data.AppDatabase
import com.secureflux.wallet.data.ChargeResult
import com.secureflux.wallet.data.Transaction
import com.secureflux.wallet.data.WalletRepository
import com.secureflux.wallet.data.Wristband
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScanPhase { READY, COMPLETE }
enum class ChargePhase { ENTRY, PROCESSING, INSUFFICIENT, DONE }
enum class FallbackPhase { READY, CONNECTING, SUCCESS }

class WalletViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = WalletRepository(AppDatabase.get(app).transactionDao())

    val wristband: StateFlow<Wristband> = repo.wristband
    val online: StateFlow<Boolean> = repo.online

    val transactions: StateFlow<List<Transaction>> =
        repo.transactions.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val unsynced: StateFlow<List<Transaction>> =
        repo.unsynced.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // --- Scan ---
    private val _scan = MutableStateFlow(ScanPhase.READY)
    val scan = _scan.asStateFlow()
    fun simulateScan() { _scan.value = ScanPhase.COMPLETE }
    fun resetScan() { _scan.value = ScanPhase.READY }

    // --- Charge ---
    private val _charge = MutableStateFlow(ChargePhase.ENTRY)
    val charge = _charge.asStateFlow()
    private val _amount = MutableStateFlow("0.00")
    val amount = _amount.asStateFlow()
    val idempotencyKey = MutableStateFlow("TXN_8829_IDEM")
    private var charging = false

    fun appendDigit(d: String) {
        if (_charge.value == ChargePhase.PROCESSING) return
        val cur = _amount.value
        _amount.value = when {
            d == "." && cur.contains(".") -> cur
            cur == "0.00" -> if (d == ".") "0." else d
            else -> cur + d
        }
        revalidate()
    }

    fun backspace() {
        val cur = _amount.value
        _amount.value = if (cur.length <= 1) "0.00" else cur.dropLast(1)
        revalidate()
    }

    fun clearAmount() { _amount.value = "0.00"; revalidate() }

    private fun amountCents(): Long {
        val v = _amount.value.toDoubleOrNull() ?: 0.0
        return Math.round(v * 100)
    }

    private fun revalidate() {
        if (amountCents() > wristband.value.balanceCents) _charge.value = ChargePhase.INSUFFICIENT
        else if (_charge.value == ChargePhase.INSUFFICIENT) _charge.value = ChargePhase.ENTRY
    }

    /** Idempotent: reentrant taps with the same key collapse to one charge. */
    fun submitCharge(onResult: (ChargeResult) -> Unit) {
        if (charging) return
        val cents = amountCents()
        if (cents <= 0) return
        if (cents > wristband.value.balanceCents) { _charge.value = ChargePhase.INSUFFICIENT; return }
        charging = true
        _charge.value = ChargePhase.PROCESSING
        viewModelScope.launch {
            val r = repo.charge(cents, idempotencyKey.value)
            _charge.value = ChargePhase.DONE
            charging = false
            onResult(r)
        }
    }

    fun resetCharge() {
        _amount.value = "0.00"
        _charge.value = ChargePhase.ENTRY
        idempotencyKey.value = "TXN_${(8830..8899).random()}_IDEM"
    }

    fun pendingShortfallCents(): Long =
        (amountCents() - wristband.value.balanceCents).coerceAtLeast(0)

    // --- Stripe fallback ---
    private val _fallback = MutableStateFlow(FallbackPhase.READY)
    val fallback = _fallback.asStateFlow()
    fun tapCard() {
        if (_fallback.value != FallbackPhase.READY) return
        _fallback.value = FallbackPhase.CONNECTING
        viewModelScope.launch {
            repo.stripeFallback(pendingShortfallCents().coerceAtLeast(1_250))
            _fallback.value = FallbackPhase.SUCCESS
        }
    }
    fun resetFallback() { _fallback.value = FallbackPhase.READY }

    // --- Sync ---
    private val _syncProgress = MutableStateFlow(-1f) // -1 = idle
    val syncProgress = _syncProgress.asStateFlow()
    fun toggleOnline() = repo.setOnline(!online.value)
    fun forceSync() {
        if (!online.value || _syncProgress.value in 0f..0.999f) return
        viewModelScope.launch {
            repo.forceSync { _syncProgress.value = it }
            _syncProgress.value = 1f
        }
    }

    // --- Deterministic state hooks for screenshot capture (debug only) ---
    fun demoScanComplete() { _scan.value = ScanPhase.COMPLETE }
    fun demoAmount(value: String) {
        _amount.value = value
        revalidate()
    }
    fun demoFallbackSuccess() { _fallback.value = FallbackPhase.SUCCESS }
    fun demoSync(progress: Float, online: Boolean) {
        repo.setOnline(online)
        _syncProgress.value = progress
    }

    init {
        viewModelScope.launch {
            // seed once
            kotlinx.coroutines.delay(50)
            repo.seedIfEmpty(repo.transactionsSnapshot())
        }
    }
}
