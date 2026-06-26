package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// --- AUNIO.AI Official Brand Colors mapped to dynamic AppThemeManager values ---
val BrandGreenPrimary: Color get() = AppThemeManager.currentColors.primary
val BrandGreenSecondary: Color get() = AppThemeManager.currentColors.primary
val BrandBackground: Color get() = AppThemeManager.currentColors.background
val BrandSecondaryBackground: Color get() = AppThemeManager.currentColors.surfaceVariant
val BrandCard: Color get() = AppThemeManager.currentColors.surface
val BrandBorder: Color get() = AppThemeManager.currentColors.border
val BrandTextPrimary: Color get() = AppThemeManager.currentColors.textPrimary
val BrandTextSecondary: Color get() = AppThemeManager.currentColors.textSecondary
val BrandDanger: Color get() = Color(0xFFFF6B6B)
val BrandWarning: Color get() = Color(0xFFFFB74D)
val BrandSuccess: Color get() = AppThemeManager.currentColors.primary

// --- Backward Compatibility Map (Seamless Integration with existing views) ---
val FrostedBg: Color get() = BrandBackground
val FrostedCard: Color get() = BrandCard
val FrostedContainer: Color get() = BrandSecondaryBackground
val FrostedBorder: Color get() = BrandBorder

val LavenderPrimary: Color get() = BrandGreenPrimary
val LavenderSecondary: Color get() = BrandTextSecondary
val LavenderTertiary: Color get() = BrandGreenSecondary
val LavenderLight: Color get() = BrandTextPrimary
val LavenderMuted: Color get() = Color(0xFF888888)

val DarkPurpleText: Color get() = Color(0xFF000000) // High-contrast black text on Primary Green buttons
val DeepPurpleAccent: Color get() = Color(0xFF101010)
val ContainerPurpleLight: Color get() = BrandGreenPrimary.copy(alpha = 0.12f)

// Status Accents
val SuccessLuxury: Color get() = BrandSuccess
val ErrorLuxury: Color get() = BrandDanger

// Old backwards mapping
val ObsidianBg: Color get() = FrostedBg
val ObsidianCard: Color get() = FrostedCard
val GoldAccent: Color get() = LavenderPrimary
val GoldBackground: Color get() = FrostedContainer
val GoldMuted: Color get() = LavenderMuted
val TextLight: Color get() = LavenderLight
val TextMuted: Color get() = LavenderSecondary
val GoldLight: Color get() = LavenderTertiary
