package com.example.ui.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.GoalEntity
import com.example.data.db.ProjectEntity
import com.example.ui.goals.GoalItemRow
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
fun ProjectCard(
    project: ProjectEntity,
    goals: List<GoalEntity>,
    isArabic: Boolean,
    onDeleteProject: () -> Unit,
    onAddGoalClick: () -> Unit,
    onGoalCheckChange: (String, Boolean) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onUpdateProjectStatus: (String, String) -> Unit,
    onScheduleFollowUp: (String, Int) -> Unit
) {
    val completedCount = goals.count { it.status == "COMPLETED" }
    val progress = if (goals.isNotEmpty()) completedCount.toFloat() / goals.size else 0.0f
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var showFollowUpDialog by remember { mutableStateOf(false) }

    Surface(
        color = BrandCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, BrandGreenPrimary.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Project Title + Delete Project Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        color = BrandTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isArabic) "ينتهي في: ${sdf.format(Date(project.deadline))}" else "Deadline: ${sdf.format(Date(project.deadline))}",
                            color = BrandTextSecondary.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                        ProjectStatusBadge(
                            status = project.status,
                            isArabic = isArabic,
                            onStatusChange = { newStatus -> onUpdateProjectStatus(project.id, newStatus) }
                        )
                    }
                }
                IconButton(onClick = onDeleteProject) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete project",
                        tint = BrandDanger.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.description,
                color = BrandTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "التقدم العام" else "Aggregate Progress",
                    color = BrandTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = BrandGreenPrimary,
                    fontSize = 12.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = BrandGreenPrimary,
                trackColor = BrandSecondaryBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sub goals checklist header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "الأهداف الفرعية (${goals.size})" else "Goal Milestones (${goals.size})",
                    color = BrandTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Schedule Follow-up trigger
                    TextButton(
                        onClick = { showFollowUpDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = BrandGreenPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Schedule Follow-up",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "متابعة" else "Follow-up",
                            fontSize = 11.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(
                        onClick = onAddGoalClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = BrandGreenPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = "Add Goal",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "أضف هدف" else "Add Goal",
                            fontSize = 11.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
            }

            // Checklist list
            if (goals.isEmpty()) {
                Text(
                    text = if (isArabic) "لا توجد أهداف فرعية مضافة بعد." else "No goals added yet.",
                    color = BrandTextSecondary.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                goals.forEach { goal ->
                    GoalItemRow(
                        goal = goal,
                        onGoalCheckChange = onGoalCheckChange,
                        onDeleteGoal = onDeleteGoal
                    )
                }
            }
        }
    }

    // Follow-up Reminders dialog
    if (showFollowUpDialog) {
        AlertDialog(
            onDismissRequest = { showFollowUpDialog = false },
            containerColor = BrandCard,
            title = {
                Text(
                    text = if (isArabic) "جدولة تذكير متابعة" else "Schedule Follow-up",
                    color = BrandTextPrimary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isArabic)
                            "اختر الموعد المناسب لتذكيرك بمتابعة تقدم هذا المشروع:"
                        else
                            "Choose a duration after which you want to check progress on this project:",
                        color = BrandTextSecondary,
                        fontSize = 13.sp
                    )
                    val choices = listOf(
                        Pair(5, if (isArabic) "بعد ٥ دقائق (للتجربة السريعة)" else "In 5 Minutes (Quick Test)"),
                        Pair(1440, if (isArabic) "بعد يوم واحد" else "In 1 Day"),
                        Pair(4320, if (isArabic) "بعد ٣ أيام" else "In 3 Days"),
                        Pair(10080, if (isArabic) "بعد أسبوع" else "In 1 Week")
                    )
                    choices.forEach { (minutes, label) ->
                        Button(
                            onClick = {
                                onScheduleFollowUp(project.title, minutes)
                                showFollowUpDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandSecondaryBackground,
                                contentColor = BrandGreenPrimary
                            ),
                            border = BorderStroke(0.5.dp, BrandBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = label, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showFollowUpDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandTextSecondary)
                ) {
                    Text(text = if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun ProjectStatusBadge(
    status: String,
    isArabic: Boolean,
    onStatusChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayStatus = when (status) {
        "ACTIVE" -> if (isArabic) "نشط" else "ACTIVE"
        "COMPLETED" -> if (isArabic) "مكتمل" else "COMPLETED"
        "PAUSED" -> if (isArabic) "مؤجل" else "PAUSED"
        "ARCHIVED" -> if (isArabic) "مؤرشف" else "ARCHIVED"
        else -> status
    }
    // Strict Black + Green + White brand palette
    val badgeColor = when (status) {
        "ACTIVE" -> BrandGreenPrimary
        "COMPLETED" -> BrandGreenSecondary
        "PAUSED" -> BrandTextSecondary
        "ARCHIVED" -> BrandTextSecondary.copy(alpha = 0.5f)
        else -> BrandTextSecondary
    }

    Box {
        Surface(
            color = badgeColor.copy(alpha = 0.12f),
            border = BorderStroke(0.5.dp, badgeColor.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { expanded = true }
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = displayStatus,
                color = badgeColor,
                fontSize = 9.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = BrandCard,
            modifier = Modifier.border(BorderStroke(0.5.dp, BrandBorder), RoundedCornerShape(8.dp))
        ) {
            listOf("ACTIVE", "COMPLETED", "PAUSED", "ARCHIVED").forEach { st ->
                val label = when (st) {
                    "ACTIVE" -> if (isArabic) "نشط" else "ACTIVE"
                    "COMPLETED" -> if (isArabic) "مكتمل" else "COMPLETED"
                    "PAUSED" -> if (isArabic) "مؤجل" else "PAUSED"
                    "ARCHIVED" -> if (isArabic) "مؤرشف" else "ARCHIVED"
                    else -> st
                }
                DropdownMenuItem(
                    text = { Text(text = label, color = BrandTextPrimary, fontSize = 12.sp) },
                    onClick = {
                        onStatusChange(st)
                        expanded = false
                    }
                )
            }
        }
    }
}
