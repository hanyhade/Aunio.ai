package com.example.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ReminderEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RemindersScreen(
    remindersList: List<ReminderEntity>,
    isArabic: Boolean,
    onCreateReminder: (String, Int) -> Unit,
    onDeleteReminder: (String) -> Unit
) {
    var showAddReminderDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isArabic) "دفتر التذكيرات الذكية" else "Smart Reminders Ledger",
                    color = BrandTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isArabic) "إدارة المواعيد والتنبيهات المجدولة تلقائياً" else "Active triggers scheduled via natural language prompts",
                    color = BrandTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { showAddReminderDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreenPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isArabic) "إضافة تذكير" else "New", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (remindersList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Empty",
                        tint = BrandTextSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isArabic) "لا توجد تذكيرات نشطة" else "No active reminders",
                        color = BrandTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) "اطلب من الرفيق تذكيرك بالمهام وسجلها هنا." else "Trigger notifications naturally through chat dialog sessions.",
                        color = BrandTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(remindersList, key = { it.id }) { reminder ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    val dateStr = sdf.format(Date(reminder.fireTime))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = BrandCard),
                        border = BorderStroke(0.5.dp, BrandBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reminder.title,
                                    color = BrandTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateStr,
                                    color = BrandGreenPrimary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = { onDeleteReminder(reminder.id) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = BrandDanger.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Create Custom Reminder Dialog ---
        if (showAddReminderDialog) {
            var reminderTitle by remember { mutableStateOf("") }
            var delayMinutes by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddReminderDialog = false },
                containerColor = BrandCard,
                title = {
                    Text(
                        text = if (isArabic) "إضافة تذكير ذكي جديد" else "Create Custom Reminder",
                        color = BrandTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = reminderTitle,
                            onValueChange = { reminderTitle = it },
                            label = { Text(if (isArabic) "عنوان التذكير" else "Reminder Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = BrandTextPrimary,
                                unfocusedTextColor = BrandTextPrimary,
                                focusedBorderColor = BrandGreenPrimary,
                                unfocusedBorderColor = BrandBorder,
                                focusedLabelColor = BrandGreenPrimary,
                                unfocusedLabelColor = BrandTextSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = delayMinutes,
                            onValueChange = { delayMinutes = it },
                            label = { Text(if (isArabic) "تنبيه بعد (دقائق)" else "Delay in Minutes") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = BrandTextPrimary,
                                unfocusedTextColor = BrandTextPrimary,
                                focusedBorderColor = BrandGreenPrimary,
                                unfocusedBorderColor = BrandBorder,
                                focusedLabelColor = BrandGreenPrimary,
                                unfocusedLabelColor = BrandTextSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val delayVal = delayMinutes.toIntOrNull() ?: 5
                            if (reminderTitle.isNotEmpty()) {
                                onCreateReminder(reminderTitle, delayVal)
                                showAddReminderDialog = false
                            }
                        }
                    ) {
                        Text(text = if (isArabic) "حفظ التنبيه" else "Schedule", color = BrandGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddReminderDialog = false }) {
                        Text(text = if (isArabic) "إلغاء" else "Cancel", color = BrandTextPrimary)
                    }
                }
            )
        }
    }
}
