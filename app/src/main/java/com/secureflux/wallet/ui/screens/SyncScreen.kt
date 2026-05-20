package com.secureflux.wallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.secureflux.wallet.data.Transaction
import com.secureflux.wallet.data.TxnStatus
import com.secureflux.wallet.data.toMoney
import com.secureflux.wallet.ui.theme.BodyMd
import com.secureflux.wallet.ui.theme.Error
import com.secureflux.wallet.ui.theme.ErrorContainer
import com.secureflux.wallet.ui.theme.HeadlineLg
import com.secureflux.wallet.ui.theme.HeadlineMd
import com.secureflux.wallet.ui.theme.LabelCaps
import com.secureflux.wallet.ui.theme.NumericData
import com.secureflux.wallet.ui.theme.OnBackground
import com.secureflux.wallet.ui.theme.OnSurface
import com.secureflux.wallet.ui.theme.OnSurfaceVariant
import com.secureflux.wallet.ui.theme.OutlineVariant
import com.secureflux.wallet.ui.theme.Primary
import com.secureflux.wallet.ui.theme.SurfaceContainer
import com.secureflux.wallet.ui.theme.SurfaceContainerHighest
import com.secureflux.wallet.ui.theme.SurfaceContainerLow

@Composable
fun SyncScreen(unsynced: List<Transaction>, online: Boolean, progress: Float,
               onToggleOnline: () -> Unit, onForceSync: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 24.dp),
    ) {
        item {
            // Network pill toggle (mock connectivity)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Row(
                    Modifier.clip(RoundedCornerShape(999.dp)).background(SurfaceContainerLow)
                        .border(1.dp, if (online) Primary else OutlineVariant, RoundedCornerShape(999.dp))
                        .clickableNoRipple(onClick = onToggleOnline)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(if (online) Primary else Error))
                    Text(if (online) "Online" else "Offline", style = LabelCaps,
                        color = if (online) Primary else OnSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        item { SyncHero(unsynced.size, online, progress, onForceSync) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Sync Queue", style = HeadlineMd, color = OnSurface)
                Box(Modifier.clip(RoundedCornerShape(999.dp)).background(SurfaceContainerHighest)
                    .padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text("Priority: High", style = LabelCaps, color = Primary)
                }
            }
        }
        if (unsynced.isEmpty()) item { QueueCleared() }
        else items(unsynced) { QueueRow(it) }
    }
}

@Composable
private fun SyncHero(count: Int, online: Boolean, progress: Float, onForceSync: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceContainer)
            .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp)).padding(24.dp),
    ) {
        Text("CURRENT STATE", style = LabelCaps, color = Primary)
        Text(if (count == 0) "All Systems Reconciled" else "$count Transactions Pending Sync",
            style = HeadlineLg, color = OnBackground, modifier = Modifier.padding(vertical = 4.dp))
        Text("Local data is waiting to be reconciled with WooCommerce.",
            style = BodyMd, color = OnSurfaceVariant)

        if (progress in 0f..0.999f) {
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Reconciling with WooCommerce...", style = BodyMd, color = Primary,
                    fontWeight = FontWeight.SemiBold)
                Text("${(progress * 100).toInt()}%", style = LabelCaps, color = OnSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(999.dp))
                .background(SurfaceContainerHighest)) {
                Box(Modifier.fillMaxWidth(progress).height(12.dp)
                    .clip(RoundedCornerShape(999.dp)).background(Primary))
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(8.dp))
                .background(if (online) Primary else Primary.copy(alpha = 0.5f))
                .clickableNoRipple(enabled = online, onClick = onForceSync),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(Icons.Default.Sync, null, tint = Color.White)
            Text(if (online) "Force Sync Now" else "Offline - waiting for connection",
                color = Color.White, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun QueueRow(t: Transaction) {
    val failed = t.status == TxnStatus.FAILED
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White)
            .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(CircleShape)
                .background(if (failed) ErrorContainer else SurfaceContainerHighest),
                contentAlignment = Alignment.Center) {
                Icon(if (failed) Icons.Default.Warning else Icons.Default.HourglassEmpty, null,
                    tint = if (failed) Error else Primary, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.padding(start = 16.dp)) {
                Text(t.id, style = NumericData, color = OnSurface)
                Text("${t.label} • ${t.amountCents.toMoney()}", style = BodyMd, color = OnSurfaceVariant)
            }
        }
        if (failed) {
            Row(
                Modifier.clip(RoundedCornerShape(8.dp)).border(1.dp, Primary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Refresh, null, tint = Primary, modifier = Modifier.size(18.dp))
                Text("Retry", style = BodyMd, color = Primary, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp))
            }
        } else {
            Box(Modifier.clip(RoundedCornerShape(4.dp)).background(SurfaceContainer)
                .padding(horizontal = 8.dp, vertical = 2.dp)) {
                Text("QUEUED", style = LabelCaps, color = OnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun QueueCleared() {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(64.dp).clip(CircleShape).background(Primary),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Queue Cleared", style = HeadlineMd, color = OnSurface)
        Text("All transactions synced to WooCommerce.", style = BodyMd, color = OnSurfaceVariant)
    }
}
