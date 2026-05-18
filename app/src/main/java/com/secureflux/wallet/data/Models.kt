package com.secureflux.wallet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Transaction lifecycle for a money-handling terminal.
 *
 * Legal transitions:
 *   QUEUED   -> PENDING  -> SETTLED
 *                       \-> FAILED   -> (retry) PENDING
 *   SETTLED  -> REVERSED  (duplicate-tap / refund)
 */
enum class TxnStatus { QUEUED, PENDING, SETTLED, FAILED, REVERSED }

enum class TxnType { CHARGE, TOPUP, STRIPE_FALLBACK, REVERSAL }

/** Whether the local row has been reconciled with the WooCommerce ledger. */
enum class SyncState { LOCAL_ONLY, SYNCING, SYNCED }

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String,
    /** Idempotency key - server dedups on this so a retry never double-charges. */
    val idempotencyKey: String,
    val type: TxnType,
    val status: TxnStatus,
    val syncState: SyncState,
    val amountCents: Long,
    val balanceBeforeCents: Long,
    val balanceAfterCents: Long,
    val label: String,
    val wristbandId: String,
    val createdAt: Long,
)

data class Wristband(
    val id: String,
    val balanceCents: Long,
    val lastSyncedLabel: String,
)

fun Long.toMoney(): String {
    val sign = if (this < 0) "-" else ""
    val abs = kotlin.math.abs(this)
    return "$sign$%d.%02d".format(abs / 100, abs % 100)
}
