package com.example.ui.settings

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AppearanceSettingsScreen(isArabic: Boolean) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var activeTheme by remember { AppThemeManager.themeState }
    var activeDarkMode by remember { AppThemeManager.darkModeState }
    var activeAccent by remember { AppThemeManager.accentState }
    var activeFontSize by remember { AppThemeManager.fontSizeState }
    var activeFontFamily by remember { AppThemeManager.fontFamilyState }
    var activeDensity by remember { AppThemeManager.densityState }
    var activeBubbleStyle by remember { AppThemeManager.bubbleStyleState }
    var activeAnimSpeed by remember { AppThemeManager.animSpeedState }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Header Section ---
        Column {
            Text(
                text = if (isArabic) "مظهر النظام والتخصيص" else "System Appearance & Styling",
                color = BrandTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "خصص Aunio.ai بالكامل مع تحديث فوري وبدون إعادة تشغيل" else "Tailor Aunio.ai with real-time UI propagation",
                color = BrandTextSecondary,
                fontSize = 12.sp
            )
        }

        // --- 1. Luxury Theme Carousel ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "🎨 السمات الاحترافية" else "🎨 Premium Themes",
                    color = BrandTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val themes = listOf(
                    ThemeOption(AppTheme.AUNIO_GREEN, "AUNIO Green", "الأخضر الافتراضي", Color(0xFF63FF63), Color(0xFF060B06)),
                    ThemeOption(AppTheme.MIDNIGHT_BLACK, "Midnight Black", "الأسود الحالك", Color(0xFFFFFFFF), Color(0xFF000000)),
                    ThemeOption(AppTheme.OCEAN_BLUE, "Ocean Blue", "الأزرق الهادئ", Color(0xFF5294E2), Color(0xFF050B11)),
                    ThemeOption(AppTheme.PURPLE, "Imperial Purple", "البنفسجي الإمبراطوري", Color(0xFFB388FF), Color(0xFF0A0512)),
                    ThemeOption(AppTheme.SUNSET_ORANGE, "Sunset Orange", "البرتقالي الدافئ", Color(0xFFFFAB40), Color(0xFF0E0804)),
                    ThemeOption(AppTheme.CRIMSON_RED, "Crimson Red", "الأحمر القرمزي", Color(0xFFFF5252), Color(0xFF0F0404)),
                    ThemeOption(AppTheme.SYSTEM_THEME, "System Adaptive", "متوافق مع النظام", Color(0xFF888888), Color(0xFF1E1E1E))
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(themes) { opt ->
                        val isSelected = activeTheme == opt.theme
                        Box(
                            modifier = Modifier
                                .width(125.dp)
                                .height(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(opt.bgRep)
                                .border(
                                    BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) BrandGreenPrimary else BrandBorder.copy(alpha = 0.5f)
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    activeTheme = opt.theme
                                    AppThemeManager.save(context)
                                }
                                .padding(10.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(opt.primaryRep)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = BrandGreenPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isArabic) opt.labelAr else opt.labelEn,
                                    color = if (isSelected) BrandTextPrimary else BrandTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Independent Accent Color Switcher ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "⚡ لون التأكيد المستقل" else "⚡ Independent Accent Override",
                    color = BrandTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isArabic) "قم بتغيير لون الأزرار والتوهج والواجهة دون التأثير على تباين السمات" else "Colorize buttons, indicators, and focus rings independently",
                    color = BrandTextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                val accents = listOf(
                    AccentOption(AccentColor.THEME_DEFAULT, "Default", "تلقائي", BrandGreenPrimary),
                    AccentOption(AccentColor.GREEN, "Emerald", "زمردي", Color(0xFF63FF63)),
                    AccentOption(AccentColor.WHITE_BLACK, "Classic", "كلاسيكي", Color(0xFFFFFFFF)),
                    AccentOption(AccentColor.BLUE, "Sapphire", "ياقوتي", Color(0xFF5294E2)),
                    AccentOption(AccentColor.PURPLE, "Amethyst", "جمشت", Color(0xFFB388FF)),
                    AccentOption(AccentColor.ORANGE, "Amber", "كهرماني", Color(0xFFFFAB40)),
                    AccentOption(AccentColor.RED, "Ruby", "ياقوت أحمر", Color(0xFFFF5252))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    accents.forEach { opt ->
                        val isSelected = activeAccent == opt.accent
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (opt.accent == AccentColor.WHITE_BLACK) Color.DarkGray else opt.color.copy(alpha = 0.25f))
                                .border(
                                    BorderStroke(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) opt.color else BrandBorder
                                    ),
                                    CircleShape
                                )
                                .clickable {
                                    activeAccent = opt.accent
                                    AppThemeManager.save(context)
                                }
                                .testTag("accent_${opt.accent.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(opt.color)
                            )
                        }
                    }
                }
            }
        }

        // --- 3. Light / Dark Mode Toggle ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "🌗 وضع الإضاءة" else "🌗 Light / Dark Mode",
                    color = BrandTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modes = listOf(
                        ModeOption(DarkMode.LIGHT, "Light", "نهاري"),
                        ModeOption(DarkMode.DARK, "Dark", "ليلي"),
                        ModeOption(DarkMode.SYSTEM, "System", "تلقائي")
                    )

                    modes.forEach { opt ->
                        val isSelected = activeDarkMode == opt.mode
                        Button(
                            onClick = {
                                activeDarkMode = opt.mode
                                AppThemeManager.save(context)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) BrandGreenPrimary else BrandSecondaryBackground,
                                contentColor = if (isSelected) Color.Black else BrandTextSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("mode_${opt.mode.name.lowercase()}"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (isArabic) opt.labelAr else opt.labelEn,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // --- 4. Sizing, Density & Styles Section ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = if (isArabic) "⚙️ إعدادات التفاصيل والخط" else "⚙️ Density & Text Customization",
                    color = BrandTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // Font Family Customizer
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isArabic) "نوع الخط (عربي)" else "Arabic Font Family", color = BrandTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(text = activeFontFamily.name, color = BrandGreenPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AppFontFamily.values().forEach { font ->
                            val isSelected = activeFontFamily == font
                            val label = when (font) {
                                AppFontFamily.SYSTEM_DEFAULT -> if (isArabic) "الأساسي" else "Default"
                                AppFontFamily.CAIRO -> "Cairo"
                                AppFontFamily.TAJAWAL -> "Tajawal"
                                AppFontFamily.ALMARAI -> "Almarai"
                            }
                            Button(
                                onClick = {
                                    activeFontFamily = font
                                    AppThemeManager.save(context)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) BrandGreenPrimary.copy(alpha = 0.15f) else BrandSecondaryBackground,
                                    contentColor = if (isSelected) BrandGreenPrimary else BrandTextSecondary
                                ),
                                border = if (isSelected) BorderStroke(1.dp, BrandGreenPrimary) else null,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(label, fontSize = 10.sp, maxLines = 1)
                            }
                        }
                    }
                }

                Divider(color = BrandBorder.copy(alpha = 0.4f))

                // Font Size Customizer
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isArabic) "حجم الخط" else "Font Size", color = BrandTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(text = activeFontSize.name, color = BrandGreenPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AppFontSize.values().forEach { size ->
                            val isSelected = activeFontSize == size
                            Button(
                                onClick = {
                                    activeFontSize = size
                                    AppThemeManager.save(context)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) BrandGreenPrimary.copy(alpha = 0.15f) else BrandSecondaryBackground,
                                    contentColor = if (isSelected) BrandGreenPrimary else BrandTextSecondary
                                ),
                                border = if (isSelected) BorderStroke(1.dp, BrandGreenPrimary) else null,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(size.name, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Divider(color = BrandBorder.copy(alpha = 0.4f))

                // Interface Density Customizer
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isArabic) "كثافة واجهة المستخدم" else "Interface Density", color = BrandTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(text = activeDensity.name, color = BrandGreenPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AppDensity.values().forEach { density ->
                            val isSelected = activeDensity == density
                            Button(
                                onClick = {
                                    activeDensity = density
                                    AppThemeManager.save(context)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) BrandGreenPrimary.copy(alpha = 0.15f) else BrandSecondaryBackground,
                                    contentColor = if (isSelected) BrandGreenPrimary else BrandTextSecondary
                                ),
                                border = if (isSelected) BorderStroke(1.dp, BrandGreenPrimary) else null,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(density.name, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Divider(color = BrandBorder.copy(alpha = 0.4f))

                // Chat Bubble Style Customizer
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isArabic) "نمط فقاعة المحادثة" else "Chat Bubble Style", color = BrandTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(text = activeBubbleStyle.name, color = BrandGreenPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        BubbleStyle.values().forEach { style ->
                            val isSelected = activeBubbleStyle == style
                            Button(
                                onClick = {
                                    activeBubbleStyle = style
                                    AppThemeManager.save(context)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) BrandGreenPrimary.copy(alpha = 0.15f) else BrandSecondaryBackground,
                                    contentColor = if (isSelected) BrandGreenPrimary else BrandTextSecondary
                                ),
                                border = if (isSelected) BorderStroke(1.dp, BrandGreenPrimary) else null,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(style.name, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Divider(color = BrandBorder.copy(alpha = 0.4f))

                // Animation Speed Customizer
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isArabic) "سرعة المؤثرات الحركية" else "Animation Velocity", color = BrandTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(text = activeAnimSpeed.name, color = BrandGreenPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AnimSpeed.values().forEach { speed ->
                            val isSelected = activeAnimSpeed == speed
                            Button(
                                onClick = {
                                    activeAnimSpeed = speed
                                    AppThemeManager.save(context)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) BrandGreenPrimary.copy(alpha = 0.15f) else BrandSecondaryBackground,
                                    contentColor = if (isSelected) BrandGreenPrimary else BrandTextSecondary
                                ),
                                border = if (isSelected) BorderStroke(1.dp, BrandGreenPrimary) else null,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(speed.name, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

private data class ThemeOption(
    val theme: AppTheme,
    val labelEn: String,
    val labelAr: String,
    val primaryRep: Color,
    val bgRep: Color
)

private data class AccentOption(
    val accent: AccentColor,
    val labelEn: String,
    val labelAr: String,
    val color: Color
)

private data class ModeOption(
    val mode: DarkMode,
    val labelEn: String,
    val labelAr: String
)
