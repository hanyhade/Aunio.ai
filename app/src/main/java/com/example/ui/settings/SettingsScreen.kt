package com.example.ui.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.MemoryEntity
import com.example.data.db.ReminderEntity
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandGreenPrimary
import com.example.ui.theme.BrandGreenSecondary
import com.example.ui.theme.BrandSecondaryBackground
import com.example.ui.theme.BrandTextPrimary
import com.example.ui.theme.BrandTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    remindersList: List<ReminderEntity>,
    memoriesList: List<MemoryEntity>,
    passphrase: String,
    isArabic: Boolean,
    onPassphraseChange: (String) -> Unit,
    onExportBackup: ((String?) -> Unit) -> Unit,
    onImportBackup: (String, (Boolean) -> Unit) -> Unit,
    onDeleteAllMemories: () -> Unit,
    onCreateReminder: (String, Int) -> Unit,
    onDeleteReminder: (String) -> Unit
) {
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var backupOutputString by remember { mutableStateOf("") }
    var restoreInputString by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- 1. Smart Reminders Ledger ---
        Surface(
            color = BrandCard,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(0.5.dp, BrandBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Reminders",
                            tint = BrandGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "المنبهات الذكية" else "Active Reminders Ledger",
                                color = BrandTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "منبهات جهاز النظام النشطة" else "Precisely registered alarm processes",
                                color = BrandTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = { showAddReminderDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add reminder",
                            tint = BrandGreenPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (remindersList.isEmpty()) {
                    Text(
                        text = if (isArabic) "لا توجد منبهات نشطة." else "No active device alarms configured.",
                        color = BrandTextSecondary.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                } else {
                    val sdf = SimpleDateFormat("h:mm a, dd MMM yyyy", Locale.getDefault())
                    remindersList.forEach { alarm ->
                        Surface(
                            color = BrandSecondaryBackground,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(0.5.dp, BrandBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alarm.title,
                                        color = BrandTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Triggers: ${sdf.format(Date(alarm.fireTime))}",
                                        color = BrandTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (alarm.isTriggered) BrandBorder else BrandGreenPrimary.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (alarm.isTriggered) "TRIGGERED" else "ACTIVE",
                                            color = if (alarm.isTriggered) BrandTextSecondary else BrandGreenPrimary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { onDeleteReminder(alarm.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Cancel reminder",
                                            tint = BrandDanger.copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Cognitive Memory Vault Settings ---
        Surface(
            color = BrandCard,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(0.5.dp, BrandBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = "Memory Vault",
                        tint = BrandGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "إدارة مستودع الذاكرة" else "Memory Vault Administration",
                            color = BrandTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "التحكم في معلومات الذاكرة المستخلصة" else "Configure or wipe custom memory concepts",
                            color = BrandTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Storage used display
                val memoryCount = memoriesList.size
                // Approx 0.3 KB per structured entity
                val kbUsed = String.format(Locale.US, "%.2f", memoryCount * 0.32)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandSecondaryBackground, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Storage",
                        tint = BrandTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "حجم تخزين الذاكرة" else "Active Memory Volume",
                            color = BrandTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic)
                                "المساحة المستغلة: $kbUsed كيلوبايت ($memoryCount ذكريات)"
                            else
                                "Storage footprint: $kbUsed KB ($memoryCount items registered)",
                            color = BrandTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Delete All Memories trigger
                Button(
                    onClick = { showDeleteAllDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandSecondaryBackground,
                        contentColor = BrandDanger
                    ),
                    border = BorderStroke(0.5.dp, BrandDanger.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Wipe", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "مسح جميع معلومات الذاكرة" else "Wipe All Memories",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- 3. Zero-Knowledge Cryptographic Backup ---
        Surface(
            color = BrandCard,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(0.5.dp, BrandBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security Vault",
                        tint = BrandGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "النسخ الاحتياطي المشفر AES-256" else "AES-256 Zero-Knowledge Backup",
                            color = BrandTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "التشفير التام لكامل بيانات الذاكرة محليًا" else "Cryptographic client-side backup envelope",
                            color = BrandTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Master Passphrase Text Field
                OutlinedTextField(
                    value = passphrase,
                    onValueChange = onPassphraseChange,
                    label = { Text(text = if (isArabic) "كلمة المرور الرئيسية للتشفير" else "Master Crypto Passphrase") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreenPrimary,
                        unfocusedBorderColor = BrandBorder,
                        focusedTextColor = BrandTextPrimary,
                        unfocusedTextColor = BrandTextPrimary,
                        focusedLabelColor = BrandGreenPrimary,
                        unfocusedLabelColor = BrandTextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Export section
                Text(
                    text = if (isArabic) "تصدير مغلف الذاكرة المشفر" else "Export Encrypted Backup",
                    color = BrandTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        onExportBackup { base64 ->
                            if (base64 != null) {
                                backupOutputString = base64
                                clipboardManager.setText(AnnotatedString(base64))
                                Toast.makeText(context, "Encrypted backup copied to clipboard! 🔒", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Please enter a valid master passphrase", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreenPrimary,
                        contentColor = BrandBackground
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isArabic) "تشفير ونسخ الرمز" else "Encrypt & Copy Payload", fontWeight = FontWeight.Bold)
                }

                if (backupOutputString.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isArabic) "الرمز المشفر المنسوخ:" else "Backup Payload String (copied):",
                        color = BrandGreenSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = BrandSecondaryBackground,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(0.5.dp, BrandBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            text = backupOutputString,
                            color = BrandTextSecondary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(8.dp),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Import section
                Text(
                    text = if (isArabic) "استيراد وفك تشفير الذاكرة" else "Restore Encrypted Backup",
                    color = BrandTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = restoreInputString,
                    onValueChange = { restoreInputString = it },
                    placeholder = {
                        Text(
                            text = if (isArabic) "الصق الرمز المشفر هنا..." else "Paste encrypted base64 payload here...",
                            color = BrandTextSecondary.copy(alpha = 0.4f),
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreenPrimary,
                        unfocusedBorderColor = BrandBorder,
                        focusedTextColor = BrandTextPrimary,
                        unfocusedTextColor = BrandTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onImportBackup(restoreInputString) { success ->
                            if (success) {
                                Toast.makeText(context, "Memory ledger restored successfully! 🚀", Toast.LENGTH_LONG).show()
                                restoreInputString = ""
                            } else {
                                Toast.makeText(context, "Decryption failed. Check passphrase or backup string.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandSecondaryBackground,
                        contentColor = BrandGreenPrimary
                    ),
                    border = BorderStroke(0.5.dp, BrandBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isArabic) "فك تشفير واستيراد" else "Decrypt & Import Payload", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // New Manual Reminder Dialog
    if (showAddReminderDialog) {
        var reminderTitle by remember { mutableStateOf("") }
        var reminderDelayMinutes by remember { mutableStateOf("5") }

        AlertDialog(
            onDismissRequest = { showAddReminderDialog = false },
            containerColor = BrandCard,
            modifier = Modifier.border(BorderStroke(0.5.dp, BrandBorder), RoundedCornerShape(16.dp)),
            title = {
                Text(
                    text = if (isArabic) "جدولة منبه يدوي" else "Schedule Alarm Manually",
                    color = BrandTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = reminderTitle,
                        onValueChange = { reminderTitle = it },
                        label = { Text(text = if (isArabic) "عنوان المنبه" else "Reminder Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGreenPrimary,
                            unfocusedBorderColor = BrandBorder,
                            focusedTextColor = BrandTextPrimary,
                            unfocusedTextColor = BrandTextPrimary,
                            focusedLabelColor = BrandGreenPrimary,
                            unfocusedLabelColor = BrandTextSecondary
                        )
                    )
                    OutlinedTextField(
                        value = reminderDelayMinutes,
                        onValueChange = { reminderDelayMinutes = it },
                        label = { Text(text = if (isArabic) "الوقت بالدقائق من الآن" else "Trigger delay in minutes") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGreenPrimary,
                            unfocusedBorderColor = BrandBorder,
                            focusedTextColor = BrandTextPrimary,
                            unfocusedTextColor = BrandTextPrimary,
                            focusedLabelColor = BrandGreenPrimary,
                            unfocusedLabelColor = BrandTextSecondary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = reminderDelayMinutes.toIntOrNull() ?: 5
                        onCreateReminder(reminderTitle, mins)
                        showAddReminderDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreenPrimary, contentColor = BrandBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isArabic) "جدولة" else "Schedule", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddReminderDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandTextSecondary)
                ) {
                    Text(text = if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }

    // Safety delete memories confirmation dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            containerColor = BrandCard,
            modifier = Modifier.border(BorderStroke(0.5.dp, BrandDanger.copy(alpha = 0.5f)), RoundedCornerShape(16.dp)),
            title = {
                Text(
                    text = if (isArabic) "هل أنت متأكد تمامًا؟" else "Wipe All Memory Ledgers?",
                    color = BrandDanger,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isArabic)
                        "سيؤدي هذا الإجراء إلى حذف جميع معلومات الذاكرة المستخلصة نهائيًا. لن يتمكن رفيق الذكاء الاصطناعي من تذكر تفضيلاتك وسياقاتك السابقة."
                    else
                        "This will permanently destroy all custom structured memories extracted by AUNIO.AI. AUNIO.AI will lose all learned personalization context.",
                    color = BrandTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAllMemories()
                        showDeleteAllDialog = false
                        Toast.makeText(context, if (isArabic) "تم مسح الذاكرة بالكامل! 🧹" else "All structured memories wiped clean! 🧹", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDanger, contentColor = BrandTextPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isArabic) "نعم، امسح الكل" else "Yes, Wipe All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAllDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandTextSecondary)
                ) {
                    Text(text = if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }
}
