package com.example.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HelpScreen(isArabic: Boolean) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        Column {
            Text(
                text = if (isArabic) "دليل المساعدة والاستخدام" else "AUNIO Help Guide & FAQ",
                color = BrandTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "كيفية الاستفادة القصوى من رفيقك الذكي ذو الذاكرة المعرفية" else "Master the natural dialogue flows and smart recall operations",
                color = BrandTextSecondary,
                fontSize = 12.sp
            )
        }

        // --- 1. How It Works ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Concept", tint = BrandGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "💡 كيف يعمل AUNIO؟" else "💡 How AUNIO Thinks",
                        color = BrandTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isArabic)
                        "AUNIO ليس مجرد بوت دردشة تقليدي. إنه رفيق ذكي يسجل ذكرياتك وتفضيلاتك تلقائياً أثناء الحديث معه، ويقوم بتصنيفها إلى (شخصية، عمل، اهتمامات، أهداف).\n\n" +
                                "كما يستخلص المواعيد تلقائياً ليجدول تنبيهات وتذكيرات ذكية لك."
                    else
                        "AUNIO continuously updates a persistent semantic graph containing your workspace facts, personal interests, active blueprints, and goals.\n\n" +
                                "Every dialogue is evaluated to extract actionable reminders or long-term cognitive ledger entries.",
                    color = BrandTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // --- 2. Arabic & Egyptian Dialect Examples ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Dialect", tint = BrandGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "🇪🇬 دعم اللهجة المصرية والعربية" else "🇪🇬 Multilingual & Dialect Examples",
                        color = BrandTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                val examples = listOf(
                    ExampleItem(
                        prompt = "فكرني أراجع الكود بكرة الساعة ٨ بليل",
                        explanation = if (isArabic) "يقوم بجدولة تنبيه تلقائي في الموعد المحدد ومسح العنوان" else "Automatically schedules a calendar reminder at 8 PM tomorrow"
                    ),
                    ExampleItem(
                        prompt = "أنا بحب الشاي الأخضر من غير سكر خالص",
                        explanation = if (isArabic) "يحفظ تفضيل المشروبات تلقائياً في الذاكرة لتخصيص الردود مستقبلاً" else "Stores tea sugar preference securely in your memory ledger"
                    ),
                    ExampleItem(
                        prompt = "عندي بروجكت تطوير تطبيق موبايل هخلصه في أسبوعين",
                        explanation = if (isArabic) "يقترح عليك إنشاء مخطط مشروع وجدول أعمال بمهام فرعية متكاملة" else "Suggests compiling a 14-day software blueprint"
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    examples.forEach { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BrandSecondaryBackground, RoundedCornerShape(8.dp))
                                .border(BorderStroke(0.5.dp, BrandBorder), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(text = "\"${item.prompt}\"", color = BrandGreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.explanation, color = BrandTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // --- 3. Frequently Asked Questions ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = "FAQ", tint = BrandGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "❓ الأسئلة الشائعة" else "❓ Frequently Asked Questions",
                        color = BrandTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                val faqs = listOf(
                    FaqItem(
                        q = if (isArabic) "هل بياناتي آمنة محلياً؟" else "Is my data completely safe?",
                        a = if (isArabic)
                            "نعم، يتم تخزين كافة البيانات والذكريات والتنبيهات محلياً بنسبة ١٠٠٪ باستخدام قاعدة بيانات Room المشفرة على جهازك."
                        else
                            "Yes, everything is stored locally within Room SQLite databases. Your personal insights and plans never leak to external third parties."
                    ),
                    FaqItem(
                        q = if (isArabic) "كيف يمكنني عمل نسخة احتياطية؟" else "How do backups work?",
                        a = if (isArabic)
                            "توجه لقسم 'النسخ الاحتياطي' في القائمة الجانبية، أدخل كلمة سر التشفير ثم اضغط توليد الكود. يمكنك نسخ الكود وحفظه في أي مكان واسترجاعه متى تشاء."
                        else
                            "Go to the 'Backup' drawer tab, configure an AES password, and click Generate. You can copy the token anywhere and restore it on other devices instantly."
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    faqs.forEach { item ->
                        Column {
                            Text(text = item.q, color = BrandTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.a, color = BrandTextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

private data class ExampleItem(val prompt: String, val explanation: String)
private data class FaqItem(val q: String, val a: String)
