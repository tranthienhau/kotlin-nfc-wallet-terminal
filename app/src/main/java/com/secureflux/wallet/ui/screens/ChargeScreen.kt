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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.secureflux.wallet.data.toMoney
import com.secureflux.wallet.ui.theme.BodyMd
import com.secureflux.wallet.ui.theme.DisplayCurrency
import com.secureflux.wallet.ui.theme.Error
import com.secureflux.wallet.ui.theme.ErrorContainer
import com.secureflux.wallet.ui.theme.HeadlineMd
import com.secureflux.wallet.ui.theme.LabelCaps
import com.secureflux.wallet.ui.theme.OnErrorContainer
import com.secureflux.wallet.ui.theme.OnSurface
import com.secureflux.wallet.ui.theme.OnSurfaceVariant
import com.secureflux.wallet.ui.theme.OutlineVariant
import com.secureflux.wallet.ui.theme.Primary
import com.secureflux.wallet.ui.theme.SurfaceContainerHighest
import com.secureflux.wallet.ui.theme.SurfaceContainerLowest
import com.secureflux.wallet.vm.ChargePhase

@Composable
fun ChargeScreen(
    phase: ChargePhase,
    amount: String,
    idempotencyKey: String,
    balanceCents: Long,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onCharge: () -> Unit,
    onFallback: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BalanceHero("Available Balance", balanceCents)
        Spacer(Modifier.height(16.dp))

        // Idempotency key badge - the core money-safety primitive.
        Row(
            Modifier.clip(RoundedCornerShape(999.dp)).background(SurfaceContainerHighest)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Key, null, tint = Primary, modifier = Modifier.size(14.dp))
            Text(idempotencyKey, style = LabelCaps, color = Primary,
                modifier = Modifier.padding(start = 8.dp))
        }
        Spacer(Modifier.height(16.dp))

        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp)),
        ) {
            Column(
                Modifier.fillMaxWidth().background(SurfaceContainerLowest).heightIn(min = 140.dp)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("CHARGE AMOUNT", style = LabelCaps, color = OnSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$", style = DisplayCurrency, color = OnSurfaceVariant.copy(alpha = 0.5f))
                    Text(amount, style = DisplayCurrency, color = OnSurface)
                }
            }
            Keypad(onDigit, onBackspace)
        }

        if (phase == ChargePhase.INSUFFICIENT) {
            Spacer(Modifier.height(16.dp))
            InsufficientBanner(onFallback)
        }

        Spacer(Modifier.height(16.dp))
        val processing = phase == ChargePhase.PROCESSING
        PrimaryButton(
            text = if (processing) "Processing..." else "Charge Wallet",
            enabled = !processing && phase != ChargePhase.INSUFFICIENT,
            onClick = onCharge,
            leading = { Icon(Icons.Default.Bolt, null, tint = Color.White) },
        )
        if (processing) {
            Spacer(Modifier.height(12.dp))
            Text("ID: $idempotencyKey (Idempotent Key Active)",
                style = LabelCaps, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun Keypad(onDigit: (String) -> Unit, onBackspace: () -> Unit) {
    val rows = listOf(
        listOf("1", "2", "3"), listOf("4", "5", "6"),
        listOf("7", "8", "9"), listOf(".", "0", "<"),
    )
    Column(
        Modifier.fillMaxWidth().background(OutlineVariant),
        verticalArrangement = spacedBy(1.dp),
    ) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = spacedBy(1.dp)) {
                row.forEach { key ->
                    Box(
                        Modifier.weight(1f).height(64.dp).background(Color.White)
                            .clickableNoRipple { if (key == "<") onBackspace() else onDigit(key) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (key == "<") Icon(Icons.Outlined.Backspace, null, tint = OnSurfaceVariant)
                        else Text(key, style = HeadlineMd, color = OnSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun InsufficientBanner(onFallback: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(ErrorContainer).border(1.dp, Error, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Row {
            Icon(Icons.Default.ErrorOutline, null, tint = Error)
            Column(Modifier.padding(start = 16.dp)) {
                Text("Insufficient Balance", color = OnErrorContainer,
                    fontWeight = FontWeight.Bold, style = BodyMd)
                Text("The charge amount exceeds the available wallet balance.",
                    color = OnErrorContainer.copy(alpha = 0.8f), style = BodyMd,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
                Row(
                    Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(8.dp))
                        .background(Error).clickableNoRipple(onClick = onFallback),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Default.Contactless, null, tint = Color.White)
                    Text("Use Stripe Tap to Pay Fallback", color = Color.White,
                        fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
