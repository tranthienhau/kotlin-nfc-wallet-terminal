package com.secureflux.wallet.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.ui.graphics.vector.ImageVector

enum class Dest(val route: String, val label: String, val icon: ImageVector) {
    Scan("scan", "Scan", Icons.Outlined.Nfc),
    Charge("charge", "Charge", Icons.Outlined.Payments),
    History("history", "History", Icons.Outlined.ReceiptLong),
    Sync("sync", "Sync", Icons.Outlined.Sync);

    companion object {
        const val Fallback = "fallback"
        val bottom = listOf(Scan, Charge, History, Sync)
    }
}
