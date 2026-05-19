package com.secureflux.wallet.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.secureflux.wallet.data.Wristband
import com.secureflux.wallet.data.toMoney
import com.secureflux.wallet.ui.theme.BodyMd
import com.secureflux.wallet.ui.theme.HeadlineLgMobile
import com.secureflux.wallet.ui.theme.LabelCaps
import com.secureflux.wallet.ui.theme.NumericData
import com.secureflux.wallet.ui.theme.OnSurface
import com.secureflux.wallet.ui.theme.OnSurfaceVariant
import com.secureflux.wallet.ui.theme.OutlineVariant
import com.secureflux.wallet.ui.theme.Primary
import com.secureflux.wallet.ui.theme.Secondary
import com.secureflux.wallet.ui.theme.SecondaryContainer
import com.secureflux.wallet.ui.theme.SurfaceContainerLow
import com.secureflux.wallet.vm.ScanPhase

@Composable
fun ScanScreen(phase: ScanPhase, wristband: Wristband, onSimulate: () -> Unit,
               onReset: () -> Unit, onNext: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (phase == ScanPhase.READY) ReadyState(onSimulate)
        else ResultState(wristband, onReset, onNext)
    }
}

@Composable
private fun ReadyState(onSimulate: () -> Unit) {
    val t = rememberInfiniteTransition(label = "nfc")
    val pulse by t.animateFloat(
        1f, 1.12f,
        infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "pulse")
    Box(Modifier.size(288.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.size(200.dp).scale(pulse).clip(CircleShape)
            .background(Primary.copy(alpha = 0.06f)))
        Box(
            Modifier.size(160.dp).clip(CircleShape)
                .background(SurfaceContainerLow)
                .border(1.dp, OutlineVariant, CircleShape),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Outlined.Nfc, null, tint = Primary, modifier = Modifier.size(80.dp)) }
    }
    Spacer(Modifier.height(24.dp))
    Text("Ready to Scan", style = HeadlineLgMobile, color = OnSurface)
    Text("Hold the wristband near the back of the device to verify balance.",
        style = BodyMd, color = OnSurfaceVariant, textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp))
    Spacer(Modifier.height(24.dp))
    Row(
        Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp))
            .background(SecondaryContainer).clickableNoRipple(onClick = onSimulate),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Outlined.TouchApp, null, tint = Color.White)
        Text("Simulate Tap", color = Color.White, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun ResultState(w: Wristband, onReset: () -> Unit, onNext: () -> Unit) {
    Box(
        Modifier.size(120.dp).clip(CircleShape).background(Secondary.copy(alpha = 0.1f))
            .border(1.dp, Secondary, CircleShape),
        contentAlignment = Alignment.Center,
    ) { Icon(Icons.Default.CheckCircle, null, tint = Secondary, modifier = Modifier.size(56.dp)) }
    Spacer(Modifier.height(16.dp))
    Text("Scan Complete", style = HeadlineLgMobile, color = OnSurface)
    Spacer(Modifier.height(24.dp))

    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow).border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
            .padding(24.dp),
    ) {
        Text("AVAILABLE BALANCE", style = LabelCaps, color = OnSurfaceVariant)
        Text(w.balanceCents.toMoney(),
            style = com.secureflux.wallet.ui.theme.DisplayCurrency, color = Primary)
    }
    Spacer(Modifier.height(16.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        InfoCell("WRISTBAND ID", "#${w.id}", Modifier.weight(1f))
        InfoCell("LAST SYNCED", w.lastSyncedLabel, Modifier.weight(1f))
    }
    Spacer(Modifier.height(24.dp))
    PrimaryButton("Next: Charge Sale", onClick = onNext)
    Spacer(Modifier.height(8.dp))
    Text("Scan Another Device", style = BodyMd, color = Primary, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(12.dp).clickableNoRipple(onClick = onReset))
}

@Composable
private fun InfoCell(label: String, value: String, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(12.dp)).background(SurfaceContainerLow)
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp)).padding(16.dp),
    ) {
        Text(label, style = LabelCaps, color = OnSurfaceVariant)
        Text(value, style = NumericData, color = OnSurface, modifier = Modifier.padding(top = 4.dp))
    }
}
