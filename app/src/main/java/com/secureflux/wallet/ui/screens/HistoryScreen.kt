package com.secureflux.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CreditCardOff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.secureflux.wallet.data.Transaction
import com.secureflux.wallet.data.TxnStatus
import com.secureflux.wallet.data.TxnType
import com.secureflux.wallet.data.toMoney
import com.secureflux.wallet.ui.theme.BodyMd
import com.secureflux.wallet.ui.theme.Error
import com.secureflux.wallet.ui.theme.HeadlineLgMobile
import com.secureflux.wallet.ui.theme.LabelCaps
import com.secureflux.wallet.ui.theme.NumericData
import com.secureflux.wallet.ui.theme.OnSurface
import com.secureflux.wallet.ui.theme.OnSurfaceVariant
import com.secureflux.wallet.ui.theme.Outline
import com.secureflux.wallet.ui.theme.OutlineVariant
import com.secureflux.wallet.ui.theme.Secondary
import com.secureflux.wallet.ui.theme.Surface
import com.secureflux.wallet.ui.theme.SurfaceContainerLow

@Composable
fun HistoryScreen(transactions: List<Transaction>, balanceCents: Long) {
    LazyColumn(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 24.dp),
    ) {
        item { BalanceHero("Available Balance", balanceCents) }
        item {
            Text("Transaction History", style = HeadlineLgMobile, color = OnSurface)
        }
        items(transactions) { TxnCard(it) }
    }
}

@Composable
private fun TxnCard(t: Transaction) {
    var expanded by remember { mutableStateOf(false) }
    val (icon, tint) = iconFor(t)
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Surface)
            .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
            .clickableNoRipple { expanded = !expanded }.padding(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }
                Column(Modifier.padding(start = 16.dp)) {
                    Text(t.label, style = NumericData, color = OnSurface)
                    Text(subtitleFor(t), style = BodyMd, color = OnSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    t.amountCents.toMoney(),
                    style = NumericData,
                    color = if (t.amountCents >= 0) Secondary else amountTint(t),
                    textDecoration = if (t.status == TxnStatus.REVERSED) TextDecoration.LineThrough else null,
                )
                Spacer(Modifier.size(4.dp))
                StatusChip(t.status)
            }
        }
        if (expanded) {
            Spacer(Modifier.size(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LedgerCell("BEFORE", t.balanceBeforeCents.toMoney(), Modifier.weight(1f))
                LedgerCell("AFTER", t.balanceAfterCents.toMoney(), Modifier.weight(1f))
            }
            Spacer(Modifier.size(12.dp))
            Text("Ledger Reference: #${t.id}  •  Idem: ${t.idempotencyKey}",
                style = BodyMd, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun LedgerCell(label: String, value: String, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(8.dp)).background(SurfaceContainerLow).padding(12.dp)) {
        Text(label, style = LabelCaps, color = OnSurfaceVariant)
        Text(value, style = NumericData, color = OnSurface)
    }
}

private fun iconFor(t: Transaction) = when (t.type) {
    TxnType.TOPUP -> Icons.Default.AddCircle to Secondary
    TxnType.STRIPE_FALLBACK -> Icons.Default.CreditCardOff to Error
    TxnType.REVERSAL -> Icons.Default.History to Outline
    TxnType.CHARGE -> Icons.Default.Payments to Error
}

private fun amountTint(t: Transaction) = when (t.status) {
    TxnStatus.FAILED, TxnStatus.REVERSED -> OnSurfaceVariant
    else -> Error
}

private fun subtitleFor(t: Transaction): String {
    val kind = when (t.type) {
        TxnType.TOPUP -> "Top-up"
        TxnType.STRIPE_FALLBACK -> "Stripe Fallback"
        TxnType.REVERSAL -> "Reversal"
        TxnType.CHARGE -> "Deduction"
    }
    return "$kind • ${t.id}"
}
