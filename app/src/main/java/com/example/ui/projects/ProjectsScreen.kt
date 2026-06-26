package com.example.ui.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.GoalEntity
import com.example.data.db.ProjectEntity
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandGreenPrimary
import com.example.ui.theme.BrandSecondaryBackground
import com.example.ui.theme.BrandTextPrimary
import com.example.ui.theme.BrandTextSecondary

@Composable
fun ProjectsScreen(
    projectsList: List<ProjectEntity>,
    goalsList: List<GoalEntity>,
    isArabic: Boolean,
    showOnlyGoals: Boolean = false,
    onCreateProject: (String, String, Int) -> Unit,
    onDeleteProject: (String) -> Unit,
    onCreateGoal: (String, String, Int) -> Unit,
    onGoalCheckChange: (String, Boolean) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onUpdateProjectStatus: (String, String) -> Unit,
    onScheduleFollowUp: (String, Int) -> Unit
) {
    var showAddProjectDialog by remember { mutableStateOf(false) }
    var showAddGoalDialogForProjId by remember { mutableStateOf<String?>(null) }

    if (showOnlyGoals) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBackground)
                .padding(16.dp)
        ) {
            // Screen Header
            Text(
                text = if (isArabic) "الأهداف والخطوات الاستراتيجية" else "Strategic Goal Milestones",
                color = BrandTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isArabic) "قائمة مبسطة بكافة المهام والأهداف عبر مشاريعك" else "Simplified checklist of all milestones across active blueprints",
                color = BrandTextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (goalsList.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) "لا توجد أهداف نشطة حالياً" else "No active goals found",
                        color = BrandTextSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(goalsList, key = { it.id }) { goal ->
                        val parentProject = projectsList.find { it.id == goal.projectId }
                        val projectTitle = parentProject?.title ?: (if (isArabic) "مشروع غير معروف" else "Unknown Project")
                        Card(
                            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = BrandCard),
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = goal.status == "COMPLETED",
                                        onCheckedChange = { isChecked ->
                                            onGoalCheckChange(goal.id, isChecked)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = BrandGreenPrimary,
                                            uncheckedColor = BrandTextSecondary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = goal.title,
                                            color = BrandTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                textDecoration = if (goal.status == "COMPLETED") androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${if (isArabic) "المشروع: " else "Project: "} $projectTitle",
                                            color = BrandTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                IconButton(onClick = { onDeleteGoal(goal.id) }) {
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
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(16.dp)
    ) {
        // Screen Header + Create Project Trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isArabic) "لوحة الأهداف والمشاريع" else "Projects & Goals Hub",
                    color = BrandTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isArabic) "تتبع خططك الاستراتيجية والمهام الفرعية" else "Formulate high-level plans and track checkmarks",
                    color = BrandTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { showAddProjectDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreenPrimary,
                    contentColor = BrandBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add project", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isArabic) "مشروع جديد" else "New Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (projectsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Empty goals",
                        tint = BrandTextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isArabic) "لا توجد خطط حالية" else "No active strategic plans",
                        color = BrandTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic) "أنشئ مشروعًا جديدًا لإضافة أهداف فرعية وتتبع سير العمل." else "Create a project blueprint to catalog custom milestones and checklists.",
                        color = BrandTextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(projectsList, key = { it.id }) { proj ->
                    val childGoals = goalsList.filter { it.projectId == proj.id }
                    ProjectCard(
                        project = proj,
                        goals = childGoals,
                        isArabic = isArabic,
                        onDeleteProject = { onDeleteProject(proj.id) },
                        onAddGoalClick = { showAddGoalDialogForProjId = proj.id },
                        onGoalCheckChange = onGoalCheckChange,
                        onDeleteGoal = onDeleteGoal,
                        onUpdateProjectStatus = onUpdateProjectStatus,
                        onScheduleFollowUp = onScheduleFollowUp
                    )
                }
            }
        }
    }

    // New Project Dialog
    if (showAddProjectDialog) {
        var projTitle by remember { mutableStateOf("") }
        var projDesc by remember { mutableStateOf("") }
        var deadlineDays by remember { mutableStateOf("14") }

        AlertDialog(
            onDismissRequest = { showAddProjectDialog = false },
            containerColor = BrandCard,
            modifier = Modifier.border(BorderStroke(0.5.dp, BrandBorder), RoundedCornerShape(16.dp)),
            title = {
                Text(
                    text = if (isArabic) "إنشاء مشروع استراتيجي" else "Create Strategic Project",
                    color = BrandTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = projTitle,
                        onValueChange = { projTitle = it },
                        label = { Text(text = if (isArabic) "اسم المشروع" else "Project Title") },
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
                        value = projDesc,
                        onValueChange = { projDesc = it },
                        label = { Text(text = if (isArabic) "الوصف" else "Description") },
                        maxLines = 3,
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
                        value = deadlineDays,
                        onValueChange = { deadlineDays = it },
                        label = { Text(text = if (isArabic) "المدة بالأيام" else "Duration in Days") },
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
                        val days = deadlineDays.toIntOrNull() ?: 14
                        onCreateProject(projTitle, projDesc, days)
                        showAddProjectDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreenPrimary, contentColor = BrandBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isArabic) "إضافة" else "Create", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddProjectDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandTextSecondary)
                ) {
                    Text(text = if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }

    // New Goal Dialog
    if (showAddGoalDialogForProjId != null) {
        var goalTitle by remember { mutableStateOf("") }
        var targetDays by remember { mutableStateOf("7") }

        AlertDialog(
            onDismissRequest = { showAddGoalDialogForProjId = null },
            containerColor = BrandCard,
            modifier = Modifier.border(BorderStroke(0.5.dp, BrandBorder), RoundedCornerShape(16.dp)),
            title = {
                Text(
                    text = if (isArabic) "إضافة هدف فرعي" else "Add Goal Milestone",
                    color = BrandTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text(text = if (isArabic) "الهدف الفرعي" else "Goal Milestone") },
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
                        value = targetDays,
                        onValueChange = { targetDays = it },
                        label = { Text(text = if (isArabic) "الأيام المستهدفة" else "Target Days to Complete") },
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
                        val days = targetDays.toIntOrNull() ?: 7
                        onCreateGoal(showAddGoalDialogForProjId!!, goalTitle, days)
                        showAddGoalDialogForProjId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreenPrimary, contentColor = BrandBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isArabic) "إضافة" else "Add", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddGoalDialogForProjId = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandTextSecondary)
                ) {
                    Text(text = if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }
}
