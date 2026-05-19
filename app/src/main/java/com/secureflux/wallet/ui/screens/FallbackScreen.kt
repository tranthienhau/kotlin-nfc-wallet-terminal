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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Contactless
import androidx.compose.material3.CircularProgressIndicator
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
import com.secureflux.wallet.data.toMoney
import com.secureflux.wallet.ui.theme.BodyLg
import com.secureflux.wallet.ui.theme.BodyMd
import com.secureflux.wallet.ui.theme.DisplayCurrency
import com.secureflux.wallet.ui.theme.HeadlineLgMobile
import com.secureflux.wallet.ui.theme.LabelCaps
import com.secureflux.wallet.ui.theme.NumericData
import com.secureflux.wallet.ui.theme.OnSurface
import com.secureflux.wallet.ui.theme.OnSurfaceVariant
import com.secureflux.wallet.ui.theme.OutlineVariant
import com.secureflux.wallet.ui.theme.Primary
import com.secureflux.wallet.ui.theme.Secondary
import com.secureflux.wallet.ui.theme.SecondaryContainer
import com.secureflux.wallet.vm.FallbackPhase

@Composable
fun FallbackScreen(phase: FallbackPhase, shortfallCents: Long,
                   onTap: () -> Unit, onBack: () -> Unit, onFinish: () -> Unit) {
    if (phase == FallbackPhase.SUCCESS) { SuccessOverlay(onFinish); return }

    Column(
        Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(Primary.copy(alpha = 0.05f))
                .border(1.dp, Primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("REMAINING TO COLLECT", style = LabelCaps, color = OnSurfaceVariant)
            Text(shortfallCents.coerceAtLeast(1_250).toMoney(),
                style = DisplayCurrency, color = Primary, modifier = Modifier.padding(top = 8.dp))
            Text("Fallback payment required", style = BodyMd,
                color = OnSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(24.dp))

        val t = rememberInfiniteTransition(label = "ring")
        val s by t.animateFloat(0.85f, 1.25f,
            infiniteRepeatable(tween(2000), RepeatMode.Restart), label = "s")
        val a by t.animateFloat(0.5f, 0f,
            infiniteRepeatable(tween(2000), RepeatMode.Restart), label = "a")
        Box(Modifier.size(256.dp).clickableNoRipple(onClick = onTap),
            contentAlignment = Alignment.Center) {
            Box(Modifier.size(256.dp).scale(s).alpha(a).clip(CircleShape)
                .border(2.dp, Primary, CircleShape))
            Box(
                Modifier.size(192.dp).clip(CircleShape).background(Color.White)
                    .border(1.dp, OutlineVariant, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Contactless, null, tint = Primary,
                        modifier = Modifier.size(64.dp))
                    Text("TAP CARD HERE", style = LabelCaps, color = Primary,
                        modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        when (phase) {
            FallbackPhase.READY -> Text("Hold the card or phone near the center to pay the shortfall.",
                style = BodyMd, color = OnSurfaceVariant, textAlign = TextAlign.Center)
            FallbackPhase.CONNECTING -> Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(20.dp), color = Primary, strokeWidth = 2.dp)
                Text("Connecting to Reader...", style = NumericData, color = Primary,
                    modifier = Modifier.padding(start = 12.dp))
            }
            else -> {}
        }

        Spacer(Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(8.dp))
                .border(1.dp, Primary, RoundedCornerShape(8.dp)).clickableNoRipple(onClick = onBack),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(Icons.Default.ArrowBack, null, tint = Primary, modifier = Modifier.size(20.dp))
            Text("Return to Wallet Flow", color = Primary, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun SuccessOverlay(onFinish: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(96.dp).clip(CircleShape).background(SecondaryContainer),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Default.Check, null, tint = Secondary, modifier = Modifier.size(48.dp)) }
        Spacer(Modifier.height(24.dp))
        Text("Payment Complete", style = HeadlineLgMobile, color = OnSurface)
        Text("Card charged via Stripe Tap to Pay - TXN STR-9921-X",
            style = BodyLg, color = OnSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.height(48.dp))
        Box(
            Modifier.size(width = 256.dp, height = 56.dp).clip(RoundedCornerShape(8.dp))
                .background(Primary).clickableNoRipple(onClick = onFinish),
            contentAlignment = Alignment.Center,
        ) { Text("Finish", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}
