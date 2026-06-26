package com.example.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppTheme {
    AUNIO_GREEN,
    MIDNIGHT_BLACK,
    OCEAN_BLUE,
    PURPLE,
    SUNSET_ORANGE,
    CRIMSON_RED,
    SYSTEM_THEME
}

enum class DarkMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class AccentColor {
    GREEN,
    WHITE_BLACK,
    BLUE,
    PURPLE,
    ORANGE,
    RED,
    THEME_DEFAULT
}

enum class AppFontSize {
    SMALL,
    MEDIUM,
    LARGE
}

enum class AppDensity {
    COMFORTABLE,
    COMPACT
}

enum class BubbleStyle {
    MODERN,
    ROUNDED,
    COMPACT
}

enum class AppFontFamily {
    SYSTEM_DEFAULT,
    CAIRO,
    TAJAWAL,
    ALMARAI
}

enum class AnimSpeed {
    NORMAL,
    FAST,
    REDUCED,
    OFF
}

data class ThemeColors(
    val primary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color
) {
    companion object {
        val GREEN_DARK = ThemeColors(
            primary = Color(0xFF63FF63),
            background = Color(0xFF060B06),
            surface = Color(0xFF111A11),
            surfaceVariant = Color(0xFF091009),
            border = Color(0x1F63FF63),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFB8C4B8)
        )
        val GREEN_LIGHT = ThemeColors(
            primary = Color(0xFF008A00),
            background = Color(0xFFF4F9F4),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE8F2E8),
            border = Color(0x33008A00),
            textPrimary = Color(0xFF0F1E0F),
            textSecondary = Color(0xFF556655)
        )
        val BLACK_DARK = ThemeColors(
            primary = Color(0xFFFFFFFF),
            background = Color(0xFF000000),
            surface = Color(0xFF121212),
            surfaceVariant = Color(0xFF1C1C1C),
            border = Color(0x22FFFFFF),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFAAAAAA)
        )
        val BLACK_LIGHT = ThemeColors(
            primary = Color(0xFF000000),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFF5F5F5),
            surfaceVariant = Color(0xFFEEEEEE),
            border = Color(0x22000000),
            textPrimary = Color(0xFF000000),
            textSecondary = Color(0xFF666666)
        )
        val BLUE_DARK = ThemeColors(
            primary = Color(0xFF5294E2),
            background = Color(0xFF050B11),
            surface = Color(0xFF0E1A27),
            surfaceVariant = Color(0xFF091119),
            border = Color(0x1F5294E2),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFB4C6D8)
        )
        val BLUE_LIGHT = ThemeColors(
            primary = Color(0xFF1E5BB0),
            background = Color(0xFFF0F5FA),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE1EDF7),
            border = Color(0x331E5BB0),
            textPrimary = Color(0xFF0D1B2A),
            textSecondary = Color(0xFF4E6178)
        )
        val PURPLE_DARK = ThemeColors(
            primary = Color(0xFFB388FF),
            background = Color(0xFF0A0512),
            surface = Color(0xFF160E25),
            surfaceVariant = Color(0xFF0E0918),
            border = Color(0x1FB388FF),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFC7B8E0)
        )
        val PURPLE_LIGHT = ThemeColors(
            primary = Color(0xFF7C4DFF),
            background = Color(0xFFF7F3FC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFEDE4F9),
            border = Color(0x337C4DFF),
            textPrimary = Color(0xFF1C0D35),
            textSecondary = Color(0xFF5F4E7A)
        )
        val ORANGE_DARK = ThemeColors(
            primary = Color(0xFFFFAB40),
            background = Color(0xFF0E0804),
            surface = Color(0xFF1D1109),
            surfaceVariant = Color(0xFF140C06),
            border = Color(0x1FFFAB40),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFE5C8B4)
        )
        val ORANGE_LIGHT = ThemeColors(
            primary = Color(0xFFD84315),
            background = Color(0xFFFCF6F2),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF6E8DE),
            border = Color(0x33D84315),
            textPrimary = Color(0xFF331600),
            textSecondary = Color(0xFF7A5844)
        )
        val RED_DARK = ThemeColors(
            primary = Color(0xFFFF5252),
            background = Color(0xFF0F0404),
            surface = Color(0xFF200909),
            surfaceVariant = Color(0xFF160606),
            border = Color(0x1FFF5252),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFE0B0B0)
        )
        val RED_LIGHT = ThemeColors(
            primary = Color(0xFFC62828),
            background = Color(0xFFFDF2F2),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF8DFDF),
            border = Color(0x33C62828),
            textPrimary = Color(0xFF3B0707),
            textSecondary = Color(0xFF824D4D)
        )
    }
}

object AppThemeManager {
    // Current theme colors State
    var currentColorsState = mutableStateOf(ThemeColors.GREEN_DARK)
    
    var currentColors: ThemeColors
        get() = currentColorsState.value
        set(value) {
            currentColorsState.value = value
        }
        
    val currentPrimary: Color
        get() = currentColors.primary

    // Customization states
    val themeState = mutableStateOf(AppTheme.AUNIO_GREEN)
    val darkModeState = mutableStateOf(DarkMode.DARK)
    val accentState = mutableStateOf(AccentColor.THEME_DEFAULT)
    val fontSizeState = mutableStateOf(AppFontSize.MEDIUM)
    val fontFamilyState = mutableStateOf(AppFontFamily.SYSTEM_DEFAULT)
    val densityState = mutableStateOf(AppDensity.COMFORTABLE)
    val bubbleStyleState = mutableStateOf(BubbleStyle.MODERN)
    val animSpeedState = mutableStateOf(AnimSpeed.NORMAL)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("appearance_prefs", Context.MODE_PRIVATE)
        themeState.value = AppTheme.valueOf(prefs.getString("theme", AppTheme.AUNIO_GREEN.name) ?: AppTheme.AUNIO_GREEN.name)
        darkModeState.value = DarkMode.valueOf(prefs.getString("dark_mode", DarkMode.DARK.name) ?: DarkMode.DARK.name)
        accentState.value = AccentColor.valueOf(prefs.getString("accent", AccentColor.THEME_DEFAULT.name) ?: AccentColor.THEME_DEFAULT.name)
        fontSizeState.value = AppFontSize.valueOf(prefs.getString("font_size", AppFontSize.MEDIUM.name) ?: AppFontSize.MEDIUM.name)
        fontFamilyState.value = AppFontFamily.valueOf(prefs.getString("font_family", AppFontFamily.SYSTEM_DEFAULT.name) ?: AppFontFamily.SYSTEM_DEFAULT.name)
        densityState.value = AppDensity.valueOf(prefs.getString("density", AppDensity.COMFORTABLE.name) ?: AppDensity.COMFORTABLE.name)
        bubbleStyleState.value = BubbleStyle.valueOf(prefs.getString("bubble_style", BubbleStyle.MODERN.name) ?: BubbleStyle.MODERN.name)
        animSpeedState.value = AnimSpeed.valueOf(prefs.getString("anim_speed", AnimSpeed.NORMAL.name) ?: AnimSpeed.NORMAL.name)
    }

    fun save(context: Context) {
        val prefs = context.getSharedPreferences("appearance_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("theme", themeState.value.name)
            .putString("dark_mode", darkModeState.value.name)
            .putString("accent", accentState.value.name)
            .putString("font_size", fontSizeState.value.name)
            .putString("font_family", fontFamilyState.value.name)
            .putString("density", densityState.value.name)
            .putString("bubble_style", bubbleStyleState.value.name)
            .putString("anim_speed", animSpeedState.value.name)
            .apply()
    }

    // Dynamic scale getters for components
    val fontScale: Float
        get() = when (fontSizeState.value) {
            AppFontSize.SMALL -> 0.85f
            AppFontSize.MEDIUM -> 1.0f
            AppFontSize.LARGE -> 1.18f
        }

    val paddingNormal: Dp
        get() = when (densityState.value) {
            AppDensity.COMFORTABLE -> 16.dp
            AppDensity.COMPACT -> 8.dp
        }

    val paddingSmall: Dp
        get() = when (densityState.value) {
            AppDensity.COMFORTABLE -> 8.dp
            AppDensity.COMPACT -> 4.dp
        }

    val spacing: Dp
        get() = when (densityState.value) {
            AppDensity.COMFORTABLE -> 12.dp
            AppDensity.COMPACT -> 6.dp
        }

    fun resolveColors(theme: AppTheme, isDark: Boolean, accent: AccentColor): ThemeColors {
        val baseColors = when (theme) {
            AppTheme.AUNIO_GREEN -> if (isDark) ThemeColors.GREEN_DARK else ThemeColors.GREEN_LIGHT
            AppTheme.MIDNIGHT_BLACK -> if (isDark) ThemeColors.BLACK_DARK else ThemeColors.BLACK_LIGHT
            AppTheme.OCEAN_BLUE -> if (isDark) ThemeColors.BLUE_DARK else ThemeColors.BLUE_LIGHT
            AppTheme.PURPLE -> if (isDark) ThemeColors.PURPLE_DARK else ThemeColors.PURPLE_LIGHT
            AppTheme.SUNSET_ORANGE -> if (isDark) ThemeColors.ORANGE_DARK else ThemeColors.ORANGE_LIGHT
            AppTheme.CRIMSON_RED -> if (isDark) ThemeColors.RED_DARK else ThemeColors.RED_LIGHT
            AppTheme.SYSTEM_THEME -> if (isDark) ThemeColors.GREEN_DARK else ThemeColors.GREEN_LIGHT
        }
        
        val resolvedAccent = when (accent) {
            AccentColor.GREEN -> if (isDark) Color(0xFF63FF63) else Color(0xFF008A00)
            AccentColor.WHITE_BLACK -> if (isDark) Color(0xFFFFFFFF) else Color(0xFF000000)
            AccentColor.BLUE -> if (isDark) Color(0xFF5294E2) else Color(0xFF1E5BB0)
            AccentColor.PURPLE -> if (isDark) Color(0xFFB388FF) else Color(0xFF7C4DFF)
            AccentColor.ORANGE -> if (isDark) Color(0xFFFFAB40) else Color(0xFFD84315)
            AccentColor.RED -> if (isDark) Color(0xFFFF5252) else Color(0xFFC62828)
            AccentColor.THEME_DEFAULT -> baseColors.primary
        }
        
        return baseColors.copy(
            primary = resolvedAccent,
            border = resolvedAccent.copy(alpha = if (isDark) 0.12f else 0.25f)
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val currentTheme = AppThemeManager.themeState.value
    val currentDarkMode = AppThemeManager.darkModeState.value
    val currentAccent = AppThemeManager.accentState.value
    
    val isDark = when (currentDarkMode) {
        DarkMode.LIGHT -> false
        DarkMode.DARK -> true
        DarkMode.SYSTEM -> darkTheme
    }
    
    val resolvedColors = AppThemeManager.resolveColors(currentTheme, isDark, currentAccent)
    AppThemeManager.currentColors = resolvedColors
    
    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = resolvedColors.primary,
            onPrimary = Color.Black,
            background = resolvedColors.background,
            onBackground = resolvedColors.textPrimary,
            surface = resolvedColors.surface,
            onSurface = resolvedColors.textPrimary,
            surfaceVariant = resolvedColors.surfaceVariant,
            onSurfaceVariant = resolvedColors.textSecondary,
            outline = resolvedColors.border
        )
    } else {
        lightColorScheme(
            primary = resolvedColors.primary,
            onPrimary = Color.White,
            background = resolvedColors.background,
            onBackground = resolvedColors.textPrimary,
            surface = resolvedColors.surface,
            onSurface = resolvedColors.textPrimary,
            surfaceVariant = resolvedColors.surfaceVariant,
            onSurfaceVariant = resolvedColors.textSecondary,
            outline = resolvedColors.border
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = getAppTypography(
            fontFamilyPreference = when (AppThemeManager.fontFamilyState.value) {
                AppFontFamily.CAIRO -> CairoFont
                AppFontFamily.TAJAWAL -> TajawalFont
                AppFontFamily.ALMARAI -> AlmaraiFont
                else -> androidx.compose.ui.text.font.FontFamily.Default
            },
            fontScale = AppThemeManager.fontScale
        ),
        content = content
    )
}

