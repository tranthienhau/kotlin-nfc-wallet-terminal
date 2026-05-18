package com.secureflux.wallet.ui.theme

import androidx.compose.ui.graphics.Color

// Secure Flux design system - sourced from design/secure_flux/DESIGN.md
val Primary = Color(0xFF0429BA)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFF2E47D1)
val OnPrimaryContainer = Color(0xFFC5CBFF)
val Secondary = Color(0xFF4648D4)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFF6063EE)
val OnSecondaryContainer = Color(0xFFFFFBFF)
val Tertiary = Color(0xFF762600)
val TertiaryContainer = Color(0xFF9D3500)
val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)

val Background = Color(0xFFF8F9FF)
val OnBackground = Color(0xFF0B1C30)
val Surface = Color(0xFFF8F9FF)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerLow = Color(0xFFEFF4FF)
val SurfaceContainer = Color(0xFFE5EEFF)
val SurfaceContainerHigh = Color(0xFFDCE9FF)
val SurfaceContainerHighest = Color(0xFFD3E4FE)
val SurfaceVariant = Color(0xFFD3E4FE)
val OnSurface = Color(0xFF0B1C30)
val OnSurfaceVariant = Color(0xFF444654)
val Outline = Color(0xFF757686)
val OutlineVariant = Color(0xFFC5C5D7)

// Semantic status colors (transaction state machine)
val StatusSettled = Primary
val StatusPending = Color(0xFF9D3500)   // amber/tertiary
val StatusFailed = Error
val StatusReversed = Outline
