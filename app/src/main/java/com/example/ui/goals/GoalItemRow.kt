package com.example.ui.goals

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.GoalEntity
import com.example.ui.theme.LavenderLight
import com.example.ui.theme.LavenderMuted
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.LavenderSecondary

@Composable
fun GoalItemRow(
    goal: GoalEntity,
    onGoalCheckChange: (String, Boolean) -> Unit,
    onDeleteGoal: (String) -> Unit
) {
    val isChecked = goal.status == "COMPLETED"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onGoalCheckChange(goal.id, it) },
            colors = CheckboxDefaults.colors(
                checkedColor = LavenderPrimary,
                uncheckedColor = LavenderSecondary
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        androidx.compose.material3.Text(
            text = goal.title,
            color = if (isChecked) LavenderMuted else LavenderLight,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = { onDeleteGoal(goal.id) },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete",
                tint = LavenderMuted,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
