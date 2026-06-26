package com.example.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SystemSettingsScreen(
    isArabic: Boolean,
    onDeleteAllMemories: () -> Unit
) {
    var showDeleteAllDialog by remember { mutableStateOf(false) }

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
                text = if (isArabic) "إعدادات النظام والأمان" else "System Settings & Storage",
                color = BrandTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "إدارة مساحة التخزين المحلية وقاعدة المعرفة" else "Monitor local storage volumes and purge cognitive history",
                color = BrandTextSecondary,
                fontSize = 12.sp
            )
        }

        // --- 1. Database Storage Details ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Storage, contentDescription = "Storage", tint = BrandGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "حجم البيانات المعرفية" else "Cognitive Database Storage",
                        color = BrandTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = if (isArabic) "نوع المحرك المحلي" else "Local DB Engine", color = BrandTextSecondary, fontSize = 12.sp)
                    Text(text = "SQLite / Room ORM", color = BrandTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = if (isArabic) "مستوى حماية البيانات" else "Zero-Cloud Encryption", color = BrandTextSecondary, fontSize = 12.sp)
                    Text(text = "AES-256 Enabled", color = BrandGreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 2. Purge Action ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "⚠️ منطقة الخطر" else "⚠️ Danger Zone",
                    color = BrandDanger,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isArabic) "حذف جميع الذكريات وقاعدة المعرفة بشكل نهائي وغير قابل للاسترجاع." else "Irreversibly wipe out all memories, chat history, and plans.",
                    color = BrandTextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDeleteAllDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandDanger.copy(alpha = 0.2f),
                        contentColor = BrandDanger
                    ),
                    border = BorderStroke(1.dp, BrandDanger),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Purge")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isArabic) "مسح جميع البيانات نهائياً" else "Purge All Local Data", fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 3. System Info ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "System Info", tint = BrandGreenPrimary, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "AUNIO.AI", color = BrandTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Client Build version 1.0.4", color = BrandTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "Platform runtime: Android Native (Kotlin/Compose)", color = BrandTextSecondary, fontSize = 10.sp)
                }
            }
        }

        // --- Delete Confirmation Dialog ---
        if (showDeleteAllDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAllDialog = false },
                containerColor = BrandCard,
                title = {
                    Text(
                        text = if (isArabic) "هل أنت متأكد من مسح البيانات؟" else "Are you absolutely sure?",
                        color = BrandTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = if (isArabic)
                            "هذا الإجراء سيقوم بمسح كافة الذكريات والملاحظات وسجل المحادثات بالكامل محلياً ولا يمكن التراجع عنه."
                        else
                            "This action will permanently purge all accumulated context, memories, project schedules, and logs. It cannot be undone.",
                        color = BrandTextSecondary,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteAllMemories()
                            showDeleteAllDialog = false
                        }
                    ) {
                        Text(text = if (isArabic) "نعم، امسح كل شيء" else "Yes, Purge All", color = BrandDanger, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAllDialog = false }) {
                        Text(text = if (isArabic) "إلغاء" else "Cancel", color = BrandTextPrimary)
                    }
                }
            )
        }
    }
}
