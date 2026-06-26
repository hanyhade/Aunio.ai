package com.example.ui.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun BackupRestoreScreen(
    passphrase: String,
    isArabic: Boolean,
    onPassphraseChange: (String) -> Unit,
    onExportBackup: ((String?) -> Unit) -> Unit,
    onImportBackup: (String, (Boolean) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    var backupOutputString by remember { mutableStateOf("") }
    var restoreInputString by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Section ---
        Column {
            Text(
                text = if (isArabic) "النسخ الاحتياطي الآمن (AES-256)" else "AES-256 Encrypted Backup",
                color = BrandTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "قم بتصدير واستيراد ذكرياتك وبياناتك بشكل مشفر بالكامل وآمن" else "Safely serialize and restore your entire memory ledger offline",
                color = BrandTextSecondary,
                fontSize = 12.sp
            )
        }

        // --- 1. Passphrase Configuration Card ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Security", tint = BrandGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "مفتاح تشفير النسخة الاحتياطية" else "Secured Passphrase Key",
                        color = BrandTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isArabic) "تنبيه: لن تتمكن من استرجاع البيانات بدون كلمة السر هذه!" else "Note: Decryption requires exact password matchup",
                    color = BrandTextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = passphrase,
                    onValueChange = onPassphraseChange,
                    label = { Text(if (isArabic) "كلمة سر التشفير" else "Passphrase Password") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrandTextPrimary,
                        unfocusedTextColor = BrandTextPrimary,
                        focusedBorderColor = BrandGreenPrimary,
                        unfocusedBorderColor = BrandBorder,
                        focusedLabelColor = BrandGreenPrimary,
                        unfocusedLabelColor = BrandTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // --- 2. Export Database ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "📤 تصدير البيانات" else "📤 Export Ledger",
                    color = BrandTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (passphrase.length < 4) {
                            Toast.makeText(context, if (isArabic) "كلمة السر قصيرة للغاية" else "Passphrase too short (Min 4 chars)", Toast.LENGTH_SHORT).show()
                        } else {
                            onExportBackup { base64 ->
                                if (base64 != null) {
                                    backupOutputString = base64
                                    Toast.makeText(context, if (isArabic) "تم التشفير والتوليد بنجاح!" else "AES encrypted successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, if (isArabic) "فشل التصدير" else "Failed to export", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreenPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Upload, contentDescription = "Export")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isArabic) "توليد كود النسخ الاحتياطي" else "Generate Encrypted Token", fontWeight = FontWeight.Bold)
                }

                if (backupOutputString.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = if (isArabic) "كود النسخة الاحتياطية المشفر:" else "Encrypted Backup Token String:", color = BrandTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandSecondaryBackground, RoundedCornerShape(8.dp))
                            .border(BorderStroke(0.5.dp, BrandBorder), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = backupOutputString,
                            color = BrandTextPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(backupOutputString))
                                Toast.makeText(context, if (isArabic) "تم النسخ للحافظة!" else "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = BrandGreenPrimary)
                        }
                    }
                }
            }
        }

        // --- 3. Import Database ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(0.5.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "📥 استيراد البيانات" else "📥 Import Ledger",
                    color = BrandTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = restoreInputString,
                    onValueChange = { restoreInputString = it },
                    label = { Text(if (isArabic) "الصق كود التشفير هنا" else "Paste Encrypted Token Here") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrandTextPrimary,
                        unfocusedTextColor = BrandTextPrimary,
                        focusedBorderColor = BrandGreenPrimary,
                        unfocusedBorderColor = BrandBorder,
                        focusedLabelColor = BrandGreenPrimary,
                        unfocusedLabelColor = BrandTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Quick Paste Button
                    Button(
                        onClick = {
                            val clipText = clipboardManager.getText()?.text
                            if (!clipText.isNullOrEmpty()) {
                                restoreInputString = clipText
                                Toast.makeText(context, if (isArabic) "تم اللصق بنجاح!" else "Pasted successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, if (isArabic) "الحافظة فارغة" else "Clipboard empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandSecondaryBackground,
                            contentColor = BrandTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isArabic) "لصق تلقائي" else "Paste Clipboard", fontSize = 12.sp)
                    }

                    // Restore Trigger
                    Button(
                        onClick = {
                            if (restoreInputString.isEmpty()) {
                                Toast.makeText(context, if (isArabic) "يرجى لصق كود البيانات أولاً" else "Paste token string first", Toast.LENGTH_SHORT).show()
                            } else if (passphrase.isEmpty()) {
                                Toast.makeText(context, if (isArabic) "يرجى إدخال كلمة سر لفك التشفير" else "Enter decrypt passphrase first", Toast.LENGTH_SHORT).show()
                            } else {
                                onImportBackup(restoreInputString) { success ->
                                    if (success) {
                                        Toast.makeText(context, if (isArabic) "تم استرجاع ذكرياتك بنجاح!" else "Workspace reconstructed perfectly!", Toast.LENGTH_SHORT).show()
                                        restoreInputString = ""
                                    } else {
                                        Toast.makeText(context, if (isArabic) "كلمة السر خاطئة أو الكود تالف" else "Incorrect password or corrupt signature", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreenPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Restore", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isArabic) "استرجاع الآن" else "Restore Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
