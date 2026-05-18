package com.secureflux.wallet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Design uses Inter; system sans-serif is the closest always-available fallback.
private val Inter = FontFamily.SansSerif

// Named roles from DESIGN.md typography scale.
val DisplayCurrency = TextStyle(
    fontFamily = Inter, fontWeight = FontWeight.Bold,
    fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = (-0.8).sp,
)
val HeadlineLg = TextStyle(
    fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 34.sp,
)
val HeadlineLgMobile = TextStyle(
    fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp,
)
val HeadlineMd = TextStyle(
    fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp,
)
val BodyLg = TextStyle(
    fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp,
)
val BodyMd = TextStyle(
    fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp,
)
val NumericData = TextStyle(
    fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp,
)
val LabelCaps = TextStyle(
    fontFamily = Inter, fontWeight = FontWeight.Bold,
    fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.6.sp,
)

val AppTypography = Typography(
    headlineLarge = HeadlineLg,
    headlineMedium = HeadlineMd,
    titleLarge = HeadlineLgMobile,
    bodyLarge = BodyLg,
    bodyMedium = BodyMd,
    labelSmall = LabelCaps,
)
