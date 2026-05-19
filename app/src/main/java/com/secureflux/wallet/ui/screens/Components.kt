package com.secureflux.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShieldMoon
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.secureflux.wallet.data.SyncState
import com.secureflux.wallet.data.TxnStatus
import com.secureflux.wallet.data.toMoney
import com.secureflux.wallet.ui.theme.BodyMd
import com.secureflux.wallet.ui.theme.DisplayCurrency
import com.secureflux.wallet.ui.theme.HeadlineMd
import com.secureflux.wallet.ui.theme.LabelCaps
import com.secureflux.wallet.ui.theme.OnSurfaceVariant
import com.secureflux.wallet.ui.theme.OutlineVariant
import com.secureflux.wallet.ui.theme.Primary
import com.secureflux.wallet.ui.theme.StatusFailed
import com.secureflux.wallet.ui.theme.StatusPending
import com.secureflux.wallet.ui.theme.StatusReversed
import com.secureflux.wallet.ui.theme.StatusSettled
import com.secureflux.wallet.ui.theme.SurfaceContainerLowest

@Composable
fun TopBar(trailing: @Composable () -> Unit = { Avatar() }) {
    Row(
        Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Menu, null, tint = Primary)
            Text("FinTech Agent", style = HeadlineMd, color = Primary,
                fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp))
        }
        trailing()
    }
}

@Composable
fun Avatar() {
    Box(
        Modifier.size(36.dp).clip(CircleShape).background(Primary),
        contentAlignment = Alignment.Center,
    ) { Text("AP", color = Color.White, style = LabelCaps) }
}

/** Indigo-tinted hero balance card used at the top of most screens. */
@Composable
fun BalanceHero(label: String, balanceCents: Long, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Primary.copy(alpha = 0.05f))
            .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label.uppercase(), style = LabelCaps, color = OnSurfaceVariant)
        Text("$" + balanceCents.toMoney().removePrefix("$"),
            style = DisplayCurrency, color = Primary,
            modifier = Modifier.padding(top = 4.dp))
    }
}

data class ChipStyle(val fg: Color, val label: String)

fun statusChip(status: TxnStatus) = when (status) {
    TxnStatus.SETTLED -> ChipStyle(StatusSettled, "SETTLED")
    TxnStatus.PENDING -> ChipStyle(StatusPending, "PENDING")
    TxnStatus.QUEUED -> ChipStyle(OnSurfaceVariant, "QUEUED")
    TxnStatus.FAILED -> ChipStyle(StatusFailed, "FAILED")
    TxnStatus.REVERSED -> ChipStyle(StatusReversed, "REVERSED")
}

@Composable
fun StatusChip(status: TxnStatus) {
    val s = statusChip(status)
    Box(
        Modifier.clip(RoundedCornerShape(999.dp))
            .background(s.fg.copy(alpha = 0.10f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) { Text(s.label, style = LabelCaps, color = s.fg) }
}

@Composable
fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit,
                  leading: (@Composable () -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) Primary else Primary.copy(alpha = 0.5f))
            .clickableNoRipple(enabled, onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        leading?.let { it(); androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp)) }
        Text(text, color = Color.White, style = BodyMd, fontWeight = FontWeight.Bold)
    }
}

fun Modifier.clickableNoRipple(enabled: Boolean = true, onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable(enabled = enabled) { onClick() })
