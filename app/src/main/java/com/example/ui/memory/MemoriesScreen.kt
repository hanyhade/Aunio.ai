package com.example.ui.memory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.MemoryEntity
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandGreenPrimary
import com.example.ui.theme.BrandSecondaryBackground
import com.example.ui.theme.BrandTextPrimary
import com.example.ui.theme.BrandTextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreen(
    memoriesList: List<MemoryEntity>,
    isArabic: Boolean,
    onDeleteMemory: (String) -> Unit,
    onSaveMemory: (MemoryEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var editingMemory by remember { mutableStateOf<MemoryEntity?>(null) }
    val focusManager = LocalFocusManager.current

    val categories = remember(isArabic) {
        listOf(
            "ALL" to (if (isArabic) "الكل" else "All"),
            "PREFERENCE" to (if (isArabic) "التفضيلات" else "Preferences"),
            "GOAL" to (if (isArabic) "الأهداف" else "Goals"),
            "RELATIONSHIP" to (if (isArabic) "العلاقات" else "Relationships"),
            "EDUCATION" to (if (isArabic) "التعليم" else "Education"),
            "NAME" to (if (isArabic) "الهوية" else "Identity")
        )
    }

    // Filtered memories based on Search Query and Selected Category
    val filteredMemories = remember(memoriesList, searchQuery, selectedCategory) {
        memoriesList.filter { mem ->
            val matchesCategory = selectedCategory == "ALL" || mem.category.uppercase() == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    mem.keyConcept.contains(searchQuery, ignoreCase = true) ||
                    mem.valueDetails.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(16.dp)
    ) {
        // --- Smart Memory Explanatory Banner ---
        Surface(
            color = BrandSecondaryBackground,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(0.5.dp, BrandBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Smart Memory",
                    tint = BrandGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isArabic) "مستودع المعرفة الإدراكية" else "Cognitive Knowledge Space",
                        color = BrandTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic)
                            "الرفيق يستخلص تلقائياً التفضيلات والأهداف التي تشاركها، ليتفادى التكرار المزعج ويبني حواراً مخصصاً."
                        else
                            "AUNIO.AI auto-extracts values and preferences you share in chats, avoiding repetitive prompts while maintaining a customized workspace.",
                        color = BrandTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // --- Search bar ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = if (isArabic) "ابحث في ذكريات الرفيق..." else "Search cognitive database...",
                    fontSize = 13.sp,
                    color = BrandTextSecondary.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = BrandTextSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = BrandTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = BrandTextPrimary,
                unfocusedTextColor = BrandTextPrimary,
                focusedContainerColor = BrandCard,
                unfocusedContainerColor = BrandCard,
                focusedBorderColor = BrandGreenPrimary,
                unfocusedBorderColor = BrandBorder,
                cursorColor = BrandGreenPrimary
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // --- Category Filters (Row of Premium custom chips) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (catId, catLabel) ->
                val isSelected = selectedCategory == catId
                Surface(
                    color = if (isSelected) BrandGreenPrimary else BrandCard,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = 0.5.dp,
                        color = if (isSelected) BrandGreenPrimary else BrandBorder
                    ),
                    modifier = Modifier.clickable { selectedCategory = catId }
                ) {
                    Text(
                        text = catLabel,
                        color = if (isSelected) Color.Black else BrandTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // --- Memory List ---
        if (filteredMemories.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Empty memory",
                        tint = BrandTextSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isArabic) "لم يتم العثور على أي ذكريات" else "No cognitive entries found",
                        color = BrandTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) "حاول تغيير كلمات البحث أو الفلاتر النشطة." else "Try modifying search query or category filters.",
                        color = BrandTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredMemories, key = { it.id }) { memory ->
                    Box(modifier = Modifier.clickable { editingMemory = memory }) {
                        MemoryItemCard(
                            memory = memory,
                            onDelete = { onDeleteMemory(memory.id) }
                        )
                    }
                }
            }
        }
    }

    // --- Natural Memory Editor Dialog (Section 7) ---
    if (editingMemory != null) {
        val memory = editingMemory!!
        var currentDetails by remember { mutableStateOf(memory.valueDetails) }

        Dialog(onDismissRequest = { editingMemory = null }) {
            Surface(
                color = BrandCard,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BrandGreenPrimary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isArabic) "تعديل الذاكرة" else "Edit Personal Insight",
                        color = BrandTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${memory.category.uppercase(Locale.US)} : ${memory.keyConcept}",
                        color = BrandGreenPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = currentDetails,
                        onValueChange = { currentDetails = it },
                        textStyle = LocalTextStyle.current.copy(color = BrandTextPrimary, fontSize = 13.sp),
                        placeholder = {
                            Text(
                                text = if (isArabic) "أدخل تفاصيل الذاكرة المحدّثة..." else "Enter updated insight details...",
                                color = BrandTextSecondary,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BrandTextPrimary,
                            unfocusedTextColor = BrandTextPrimary,
                            focusedContainerColor = BrandCard,
                            unfocusedContainerColor = BrandCard,
                            focusedBorderColor = BrandGreenPrimary,
                            unfocusedBorderColor = BrandBorder,
                            cursorColor = BrandGreenPrimary
                        ),
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { editingMemory = null }) {
                            Text(
                                text = if (isArabic) "إلغاء" else "Cancel",
                                color = BrandTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                val updatedMemory = memory.copy(
                                    valueDetails = currentDetails.trim(),
                                    lastAccessed = System.currentTimeMillis()
                                )
                                onSaveMemory(updatedMemory)
                                editingMemory = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreenPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (isArabic) "حفظ التغييرات" else "Save Changes",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
