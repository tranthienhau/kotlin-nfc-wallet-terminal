package com.secureflux.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.secureflux.wallet.ui.nav.Dest
import com.secureflux.wallet.ui.screens.ChargeScreen
import com.secureflux.wallet.ui.screens.FallbackScreen
import com.secureflux.wallet.ui.screens.HistoryScreen
import com.secureflux.wallet.ui.screens.ScanScreen
import com.secureflux.wallet.ui.screens.SyncScreen
import com.secureflux.wallet.ui.screens.TopBar
import com.secureflux.wallet.ui.screens.clickableNoRipple
import com.secureflux.wallet.ui.theme.LabelCaps
import com.secureflux.wallet.ui.theme.OnSurfaceVariant
import com.secureflux.wallet.ui.theme.OutlineVariant
import com.secureflux.wallet.ui.theme.PrimaryContainer
import com.secureflux.wallet.ui.theme.OnPrimaryContainer
import com.secureflux.wallet.ui.theme.SecureFluxTheme
import com.secureflux.wallet.ui.theme.Surface as SurfaceColor
import com.secureflux.wallet.vm.WalletViewModel

/** Optional intent extras used by the screenshot harness to land on a fixed state. */
data class Demo(val screen: String?, val variant: String?)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val demo = Demo(intent.getStringExtra("screen"), intent.getStringExtra("variant"))
        setContent { SecureFluxTheme { App(demo = demo) } }
    }
}

@Composable
private fun App(vm: WalletViewModel = viewModel(), demo: Demo = Demo(null, null)) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val showBars = route != Dest.Fallback

    androidx.compose.runtime.LaunchedEffect(demo) {
        kotlinx.coroutines.delay(150)
        when (demo.screen) {
            "scan" -> if (demo.variant == "result") vm.demoScanComplete()
            "charge" -> {
                nav.navigate(Dest.Charge.route)
                if (demo.variant == "insufficient") vm.demoAmount("999.00")
                else vm.demoAmount("18.50")
            }
            "fallback" -> {
                nav.navigate(Dest.Fallback)
                if (demo.variant == "success") vm.demoFallbackSuccess()
            }
            "history" -> nav.navigate(Dest.History.route)
            "sync" -> {
                nav.navigate(Dest.Sync.route)
                vm.demoSync(if (demo.variant == "progress") 0.55f else -1f, online = true)
            }
        }
    }

    Surface(Modifier.fillMaxSize(), color = SurfaceColor) {
        Column(Modifier.fillMaxSize()) {
            if (showBars) TopBar()
            Box(Modifier.weight(1f).fillMaxWidth()) {
                NavGraph(nav, vm)
            }
            if (showBars) BottomBar(nav, route)
        }
    }
}

@Composable
private fun NavGraph(nav: NavHostController, vm: WalletViewModel) {
    NavHost(nav, startDestination = Dest.Scan.route) {
        composable(Dest.Scan.route) {
            val phase by vm.scan.collectAsState()
            val w by vm.wristband.collectAsState()
            ScanScreen(phase, w, vm::simulateScan, vm::resetScan,
                onNext = { nav.navigate(Dest.Charge.route) })
        }
        composable(Dest.Charge.route) {
            val phase by vm.charge.collectAsState()
            val amount by vm.amount.collectAsState()
            val key by vm.idempotencyKey.collectAsState()
            val w by vm.wristband.collectAsState()
            ChargeScreen(
                phase = phase, amount = amount, idempotencyKey = key, balanceCents = w.balanceCents,
                onDigit = vm::appendDigit, onBackspace = vm::backspace,
                onCharge = { vm.submitCharge { vm.resetCharge() } },
                onFallback = { nav.navigate(Dest.Fallback) },
            )
        }
        composable(Dest.History.route) {
            val txns by vm.transactions.collectAsState()
            val w by vm.wristband.collectAsState()
            HistoryScreen(txns, w.balanceCents)
        }
        composable(Dest.Sync.route) {
            val unsynced by vm.unsynced.collectAsState()
            val online by vm.online.collectAsState()
            val progress by vm.syncProgress.collectAsState()
            SyncScreen(unsynced, online, progress, vm::toggleOnline, vm::forceSync)
        }
        composable(Dest.Fallback) {
            val phase by vm.fallback.collectAsState()
            FallbackScreen(
                phase = phase, shortfallCents = vm.pendingShortfallCents(),
                onTap = vm::tapCard,
                onBack = { vm.resetFallback(); nav.popBackStack() },
                onFinish = { vm.resetFallback(); vm.resetCharge(); nav.popBackStack() },
            )
        }
    }
}

@Composable
private fun BottomBar(nav: NavHostController, route: String?) {
    Box(Modifier.fillMaxWidth().background(OutlineVariant).padding(top = 1.dp)) {
        Row(
            Modifier.fillMaxWidth().height(80.dp).background(SurfaceColor),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Dest.bottom.forEach { d ->
                val selected = route == d.route
                Column(
                    Modifier.clip(RoundedCornerShape(12.dp))
                        .background(if (selected) PrimaryContainer else Color.Transparent)
                        .clickableNoRipple {
                            if (!selected) nav.navigate(d.route) {
                                popUpTo(Dest.Scan.route); launchSingleTop = true
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(d.icon, d.label,
                        tint = if (selected) OnPrimaryContainer else OnSurfaceVariant,
                        modifier = Modifier.size(24.dp))
                    Text(d.label, style = LabelCaps,
                        color = if (selected) OnPrimaryContainer else OnSurfaceVariant)
                }
            }
        }
    }
}
