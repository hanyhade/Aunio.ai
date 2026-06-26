package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val CairoFont = FontFamily(
    Font(googleFont = GoogleFont("Cairo"), fontProvider = provider)
)
val TajawalFont = FontFamily(
    Font(googleFont = GoogleFont("Tajawal"), fontProvider = provider)
)
val AlmaraiFont = FontFamily(
    Font(googleFont = GoogleFont("Almarai"), fontProvider = provider)
)

fun getAppTypography(fontFamilyPreference: FontFamily, fontScale: Float = 1.0f): Typography {
    return Typography(
        bodyLarge = TextStyle(
            fontFamily = fontFamilyPreference,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * fontScale).sp,
            lineHeight = (24 * fontScale).sp,
            letterSpacing = 0.5.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamilyPreference,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * fontScale).sp,
            lineHeight = (20 * fontScale).sp,
            letterSpacing = 0.25.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamilyPreference,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * fontScale).sp,
            lineHeight = (16 * fontScale).sp,
            letterSpacing = 0.4.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamilyPreference,
            fontWeight = FontWeight.Normal,
            fontSize = (22 * fontScale).sp,
            lineHeight = (28 * fontScale).sp,
            letterSpacing = 0.sp
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamilyPreference,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * fontScale).sp,
            lineHeight = (20 * fontScale).sp,
            letterSpacing = 0.1.sp
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamilyPreference,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * fontScale).sp,
            lineHeight = (16 * fontScale).sp,
            letterSpacing = 0.5.sp
        )
    )
}

// Set of Material typography styles to start with
val Typography = getAppTypography(FontFamily.Default)
