package com.secureflux.wallet.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Outcome of a charge attempt against the wristband wallet. */
sealed interface ChargeResult {
    data class Success(val txn: Transaction) : ChargeResult
    /** Same idempotency key already processed - returns the original, no second charge. */
    data class Duplicate(val txn: Transaction) : ChargeResult
    /** Balance too low - caller routes to the Stripe Tap to Pay fallback. */
    data class InsufficientBalance(val shortfallCents: Long) : ChargeResult
}

/**
 * Single source of truth for wallet state. In production the charge/top-up
 * calls hit the WooCommerce Wallet REST endpoints; here they are mocked with
 * a local Room ledger so the flow is fully demoable on an emulator.
 */
class WalletRepository(private val dao: TransactionDao) {

    private val _wristband = MutableStateFlow(
        Wristband(id = "00X-FF42", balanceCents = 42_000, lastSyncedLabel = "02:14 PM")
    )
    val wristband: StateFlow<Wristband> = _wristband.asStateFlow()

    private val _online = MutableStateFlow(false)
    val online: StateFlow<Boolean> = _online.asStateFlow()

    val transactions: Flow<List<Transaction>> = dao.observeAll()
    val unsynced: Flow<List<Transaction>> = dao.observeUnsynced()

    private var seq = 882_915L
    private fun nextId() = "TXN_${seq++}"

    /**
     * Charge the wallet. The idempotency key makes a double-tap or a network
     * retry safe: the second call with the same key returns [ChargeResult.Duplicate]
     * instead of deducting twice.
     */
    suspend fun charge(amountCents: Long, idempotencyKey: String): ChargeResult {
        dao.findByIdempotencyKey(idempotencyKey)?.let { return ChargeResult.Duplicate(it) }

        val before = _wristband.value.balanceCents
        if (amountCents > before) {
            return ChargeResult.InsufficientBalance(amountCents - before)
        }

        // PENDING -> network round trip -> SETTLED
        val pending = Transaction(
            id = nextId(),
            idempotencyKey = idempotencyKey,
            type = TxnType.CHARGE,
            status = TxnStatus.PENDING,
            syncState = SyncState.LOCAL_ONLY,
            amountCents = -amountCents,
            balanceBeforeCents = before,
            balanceAfterCents = before - amountCents,
            label = "Merchant Payment",
            wristbandId = _wristband.value.id,
            createdAt = nowSeed(),
        )
        dao.insert(pending)
        delay(700) // simulated authorization latency
        val after = before - amountCents
        _wristband.value = _wristband.value.copy(balanceCents = after)
        val settled = pending.copy(
            status = TxnStatus.SETTLED,
            syncState = if (_online.value) SyncState.SYNCED else SyncState.LOCAL_ONLY,
        )
        dao.update(settled)
        return ChargeResult.Success(settled)
    }

    /** Card payment for the shortfall. Does not touch the wristband balance. */
    suspend fun stripeFallback(amountCents: Long): Transaction {
        delay(1500) // reader connect + tap
        val txn = Transaction(
            id = nextId(),
            idempotencyKey = "STR-${seq}",
            type = TxnType.STRIPE_FALLBACK,
            status = TxnStatus.SETTLED,
            syncState = if (_online.value) SyncState.SYNCED else SyncState.LOCAL_ONLY,
            amountCents = -amountCents,
            balanceBeforeCents = _wristband.value.balanceCents,
            balanceAfterCents = _wristband.value.balanceCents,
            label = "Stripe Tap to Pay",
            wristbandId = _wristband.value.id,
            createdAt = nowSeed(),
        )
        dao.insert(txn)
        return txn
    }

    suspend fun topUp(amountCents: Long): Transaction {
        val before = _wristband.value.balanceCents
        val after = before + amountCents
        _wristband.value = _wristband.value.copy(balanceCents = after)
        val txn = Transaction(
            id = nextId(),
            idempotencyKey = "TOP-${seq}",
            type = TxnType.TOPUP,
            status = TxnStatus.SETTLED,
            syncState = SyncState.LOCAL_ONLY,
            amountCents = amountCents,
            balanceBeforeCents = before,
            balanceAfterCents = after,
            label = "Wallet Top-up",
            wristbandId = _wristband.value.id,
            createdAt = nowSeed(),
        )
        dao.insert(txn)
        return txn
    }

    fun setOnline(value: Boolean) { _online.value = value }

    /** Reconcile every local row with WooCommerce. Emits progress 0f..1f. */
    suspend fun forceSync(onProgress: suspend (Float) -> Unit) {
        var p = 0f
        while (p < 1f) {
            p = (p + 0.18f).coerceAtMost(1f)
            onProgress(p)
            delay(350)
        }
        dao.getUnsynced().forEach {
            dao.update(it.copy(syncState = SyncState.SYNCED, status = resolved(it)))
        }
    }

    private fun resolved(t: Transaction) =
        if (t.status == TxnStatus.QUEUED || t.status == TxnStatus.FAILED) TxnStatus.SETTLED else t.status

    private var seedClock = 1_715_000_000L
    private fun nowSeed(): Long { seedClock += 137; return seedClock * 1000 }

    /** Seed the ledger with the mock rows shown in the Stitch design. */
    suspend fun seedIfEmpty(existing: List<Transaction>) {
        if (existing.isNotEmpty()) return
        val base = 1_714_900_000L
        listOf(
            Transaction("TXN-882193-CL", "IDEM-CL-1", TxnType.CHARGE, TxnStatus.SETTLED,
                SyncState.SYNCED, -1_500, 1_249_750, 1_248_250, "Payment to Coffee Loop", "00X-FF42", (base + 600) * 1000),
            Transaction("TXN_882911", "IDEM-TU-1", TxnType.TOPUP, TxnStatus.PENDING,
                SyncState.LOCAL_ONLY, 50_000, 1_198_250, 1_248_250, "Bank Transfer Credit", "00X-FF42", (base + 500) * 1000),
            Transaction("TXN_882913", "IDEM-SF-1", TxnType.STRIPE_FALLBACK, TxnStatus.FAILED,
                SyncState.LOCAL_ONLY, -9_900, 1_248_250, 1_248_250, "AWS Subscription", "00X-FF42", (base + 400) * 1000),
            Transaction("TXN_882910", "IDEM-RV-1", TxnType.REVERSAL, TxnStatus.REVERSED,
                SyncState.SYNCED, -450, 1_248_700, 1_248_250, "Refund: Transit Pass", "00X-FF42", (base + 300) * 1000),
            Transaction("TXN_882914", "IDEM-PC-1", TxnType.TOPUP, TxnStatus.QUEUED,
                SyncState.LOCAL_ONLY, 120_000, 1_128_250, 1_248_250, "POS Credit", "00X-FF42", (base + 200) * 1000),
        ).forEach { dao.insert(it) }
    }
}
