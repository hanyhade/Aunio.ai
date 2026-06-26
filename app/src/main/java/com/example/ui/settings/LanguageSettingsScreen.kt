package com.example.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LanguageSettingsScreen(
    isArabic: Boolean,
    onLanguageToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Section ---
        Column {
            Text(
                text = if (isArabic) "اللغة والترجمة" else "Language & Localization",
                color = BrandTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "اختر لغة واجهة النظام والتواصل مع الرفيق الذكي" else "Select your preferred layout direction and dialogue localized tongue",
                color = BrandTextSecondary,
                fontSize = 12.sp
            )
        }

        // Language Cards
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // English Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!isArabic) BrandGreenPrimary.copy(alpha = 0.12f) else BrandSecondaryBackground)
                        .clickable { if (isArabic) onLanguageToggle() }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🇺🇸", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "English", color = BrandTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Standard English Dialogue & RTL layout off", color = BrandTextSecondary, fontSize = 11.sp)
                        }
                    }
                    if (!isArabic) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Active", tint = BrandGreenPrimary)
                    }
                }

                // Arabic/Masri Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isArabic) BrandGreenPrimary.copy(alpha = 0.12f) else BrandSecondaryBackground)
                        .clickable { if (!isArabic) onLanguageToggle() }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🇪🇬", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "العربية (مصر)", color = BrandTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "الحوار بالعامية المصرية وتفعيل اتجاه اليمين لليسار (RTL)", color = BrandTextSecondary, fontSize = 11.sp)
                        }
                    }
                    if (isArabic) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Active", tint = BrandGreenPrimary)
                    }
                }
            }
        }

        // Dialect Information
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Dialect",
                    tint = BrandGreenPrimary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (isArabic) "ميزة الذكاء اللغوي" else "Bilingual Adaptability",
                        color = BrandTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic)
                            "يتفاعل الرفيق تلقائياً باللغة التي ترسل بها الرسائل، ويفهم اللهجات المختلفة بسلاسة وذكاء."
                        else
                            "AUNIO's context ledger recognizes Arabic dialects, including Egyptian Masri, and replies dynamically.",
                        color = BrandTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
