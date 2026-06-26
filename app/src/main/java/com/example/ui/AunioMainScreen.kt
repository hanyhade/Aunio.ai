package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.chat.ChatScreen
import com.example.ui.components.AunioBackgroundOrbs
import com.example.ui.components.AunioTopAppBar
import com.example.ui.memory.MemoriesScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.projects.ProjectsScreen
import com.example.ui.settings.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class CustomDrawerItem(
    val labelEn: String,
    val labelAr: String,
    val emoji: String,
    val tabTarget: AunioTab
)

@Composable
fun AunioMainScreen(viewModel: AunioViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Onboarding experience check
    if (!isOnboardingCompleted) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            OnboardingScreen(
                isArabic = isArabic,
                onOnboardingComplete = { viewModel.completeOnboarding() }
            )
        }
        return
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // --- Precise Drawer Item List defined by User Intent ---
    val section1Items = remember {
        listOf(
            CustomDrawerItem("AUNIO.AI Chat", "Aunio.ai والمحادثة", "💬", AunioTab.CHAT),
            CustomDrawerItem("Memory Ledger", "الذاكرة المعرفية", "🧠", AunioTab.MEMORIES),
            CustomDrawerItem("Strategic Projects", "المشاريع الاستراتيجية", "📂", AunioTab.PROJECTS),
            CustomDrawerItem("Goal Milestones", "الأهداف والخطوات", "🎯", AunioTab.GOALS),
            CustomDrawerItem("Reminders Ledger", "دفتر التذكيرات", "🔔", AunioTab.REMINDERS)
        )
    }

    val section2Items = remember {
        listOf(
            CustomDrawerItem("Appearance", "مظهر التطبيق", "🎨", AunioTab.APPEARANCE),
            CustomDrawerItem("Language", "لغة الواجهة", "🌐", AunioTab.LANGUAGE),
            CustomDrawerItem("AUNIO.AI Profile", "الملف الشخصي", "👤", AunioTab.ACCOUNT),
            CustomDrawerItem("AES-256 Backup", "النسخ الاحتياطي", "🔒", AunioTab.BACKUP),
            CustomDrawerItem("System Storage", "إعدادات النظام", "⚙️", AunioTab.SETTINGS),
            CustomDrawerItem("Help Center", "دليل المساعدة", "❓", AunioTab.HELP)
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = BrandSecondaryBackground,
                    drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                    modifier = Modifier
                        .width(290.dp)
                        .fillMaxHeight()
                        .border(
                            BorderStroke(0.5.dp, BrandBorder),
                            RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Section containing header + navigation groups
                        Column {
                            // Drawer Brand Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp, start = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(BrandGreenPrimary)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "AUNIO.AI",
                                    color = BrandTextPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 2.sp
                                )
                            }

                            // --- Drawer Group 1: Workspace Core ---
                            Text(
                                text = if (isArabic) "مساحة العمل" else "CORE WORKSPACE",
                                color = BrandGreenPrimary.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                section1Items.forEach { item ->
                                    val isSelected = selectedTab == item.tabTarget
                                    NavigationDrawerItem(
                                        label = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = item.emoji,
                                                    fontSize = 18.sp,
                                                    modifier = Modifier.padding(end = 12.dp)
                                                )
                                                Text(
                                                    text = if (isArabic) item.labelAr else item.labelEn,
                                                    color = if (isSelected) BrandGreenPrimary else BrandTextSecondary,
                                                    fontSize = 14.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }
                                        },
                                        selected = isSelected,
                                        onClick = {
                                            scope.launch { drawerState.close() }
                                            viewModel.selectTab(item.tabTarget)
                                        },
                                        colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = BrandCard,
                                            unselectedContainerColor = Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(44.dp).testTag("drawer_item_${item.tabTarget.name.lowercase()}")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = BrandBorder.copy(alpha = 0.3f), thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 12.dp))
                            Spacer(modifier = Modifier.height(16.dp))

                            // --- Drawer Group 2: System Settings ---
                            Text(
                                text = if (isArabic) "النظام والتفضيلات" else "SYSTEM & PREFERENCES",
                                color = BrandGreenPrimary.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                section2Items.forEach { item ->
                                    val isSelected = selectedTab == item.tabTarget
                                    NavigationDrawerItem(
                                        label = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = item.emoji,
                                                    fontSize = 18.sp,
                                                    modifier = Modifier.padding(end = 12.dp)
                                                )
                                                Text(
                                                    text = if (isArabic) item.labelAr else item.labelEn,
                                                    color = if (isSelected) BrandGreenPrimary else BrandTextSecondary,
                                                    fontSize = 14.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }
                                        },
                                        selected = isSelected,
                                        onClick = {
                                            scope.launch { drawerState.close() }
                                            viewModel.selectTab(item.tabTarget)
                                        },
                                        colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = BrandCard,
                                            unselectedContainerColor = Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(44.dp).testTag("drawer_item_${item.tabTarget.name.lowercase()}")
                                    )
                                }
                            }
                        }

                        // Bottom Footer
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = "AUNIO.AI",
                                color = BrandTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Version 1.1.2 - Premium",
                                color = BrandTextSecondary.copy(alpha = 0.5f),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        ) {
            val appBarTitle = when (selectedTab) {
                AunioTab.CHAT -> if (isArabic) "Aunio.ai والمحادثة" else "AUNIO.AI"
                AunioTab.MEMORIES -> if (isArabic) "الذاكرة المعرفية" else "Memory Ledger"
                AunioTab.PROJECTS -> if (isArabic) "المشاريع الاستراتيجية" else "Strategic Blueprints"
                AunioTab.GOALS -> if (isArabic) "الأهداف والخطوات" else "Goal Milestones"
                AunioTab.REMINDERS -> if (isArabic) "دفتر التذكيرات" else "Smart Reminders"
                AunioTab.APPEARANCE -> if (isArabic) "مظهر التطبيق" else "Appearance"
                AunioTab.LANGUAGE -> if (isArabic) "لغة الواجهة" else "Language"
                AunioTab.ACCOUNT -> if (isArabic) "الملف الشخصي" else "AUNIO.AI Profile"
                AunioTab.BACKUP -> if (isArabic) "النسخ الاحتياطي" else "Secure Backup"
                AunioTab.SETTINGS -> if (isArabic) "إعدادات النظام" else "System Storage"
                AunioTab.HELP -> if (isArabic) "دليل المساعدة" else "Help Guide"
            }

            Scaffold(
                topBar = {
                    AunioTopAppBar(
                        title = appBarTitle,
                        isArabic = isArabic,
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        onLanguageToggle = { viewModel.toggleLanguage() },
                        onProfileClick = { viewModel.selectTab(AunioTab.ACCOUNT) }
                    )
                },
                containerColor = BrandBackground
            ) { innerPadding ->
                AunioBackgroundOrbs {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val memoriesList by viewModel.memories.collectAsState()
                        val projectsList by viewModel.projects.collectAsState()
                        val goalsList by viewModel.goals.collectAsState()
                        val remindersList by viewModel.reminders.collectAsState()
                        val passphrase by viewModel.backupPassphrase.collectAsState()

                        when (selectedTab) {
                            AunioTab.CHAT -> {
                                val messages by viewModel.chatMessages.collectAsState()
                                val chatInput by viewModel.chatInput.collectAsState()
                                val isGenerating by viewModel.isGenerating.collectAsState()
                                val suggestedProject by viewModel.suggestedProject.collectAsState()
                                val suggestedGoal by viewModel.suggestedGoal.collectAsState()
                                val suggestedActions by viewModel.suggestedActions.collectAsState()

                                ChatScreen(
                                    messages = messages,
                                    chatInput = chatInput,
                                    isGenerating = isGenerating,
                                    isArabic = isArabic,
                                    suggestedProject = suggestedProject,
                                    suggestedGoal = suggestedGoal,
                                    suggestedActions = suggestedActions,
                                    onConfirmProject = { proj ->
                                        viewModel.createProject(proj.title, proj.description, proj.durationDays)
                                        viewModel.dismissProjectSuggestion()
                                    },
                                    onDismissProject = { viewModel.dismissProjectSuggestion() },
                                    onConfirmGoal = { goal ->
                                        viewModel.createGoal(goal.projectId ?: "", goal.title, goal.targetDays)
                                        viewModel.dismissGoalSuggestion()
                                    },
                                    onDismissGoal = { viewModel.dismissGoalSuggestion() },
                                    onInputChange = { viewModel.updateChatInput(it) },
                                    onSendClick = { viewModel.sendChatMessage() },
                                    onLoadMore = { viewModel.loadMoreChatMessages() }
                                )
                            }
                            AunioTab.MEMORIES -> {
                                MemoriesScreen(
                                    memoriesList = memoriesList,
                                    isArabic = isArabic,
                                    onDeleteMemory = { viewModel.deleteMemory(it) },
                                    onSaveMemory = { viewModel.saveMemory(it) }
                                )
                            }
                            AunioTab.PROJECTS -> {
                                ProjectsScreen(
                                    projectsList = projectsList,
                                    goalsList = goalsList,
                                    isArabic = isArabic,
                                    showOnlyGoals = false,
                                    onCreateProject = { title, desc, days -> viewModel.createProject(title, desc, days) },
                                    onDeleteProject = { viewModel.deleteProject(it) },
                                    onCreateGoal = { projId, title, days -> viewModel.createGoal(projId, title, days) },
                                    onGoalCheckChange = { goalId, check -> viewModel.updateGoalStatus(goalId, check) },
                                    onDeleteGoal = { viewModel.deleteGoal(it) },
                                    onUpdateProjectStatus = { projId, status -> viewModel.updateProjectStatus(projId, status) },
                                    onScheduleFollowUp = { title, mins -> viewModel.scheduleProjectFollowUp(title, mins) }
                                )
                            }
                            AunioTab.GOALS -> {
                                ProjectsScreen(
                                    projectsList = projectsList,
                                    goalsList = goalsList,
                                    isArabic = isArabic,
                                    showOnlyGoals = true,
                                    onCreateProject = { title, desc, days -> viewModel.createProject(title, desc, days) },
                                    onDeleteProject = { viewModel.deleteProject(it) },
                                    onCreateGoal = { projId, title, days -> viewModel.createGoal(projId, title, days) },
                                    onGoalCheckChange = { goalId, check -> viewModel.updateGoalStatus(goalId, check) },
                                    onDeleteGoal = { viewModel.deleteGoal(it) },
                                    onUpdateProjectStatus = { projId, status -> viewModel.updateProjectStatus(projId, status) },
                                    onScheduleFollowUp = { title, mins -> viewModel.scheduleProjectFollowUp(title, mins) }
                                )
                            }
                            AunioTab.REMINDERS -> {
                                RemindersScreen(
                                    remindersList = remindersList,
                                    isArabic = isArabic,
                                    onCreateReminder = { title, delayMins -> viewModel.createReminder(title, delayMins) },
                                    onDeleteReminder = { viewModel.deleteReminder(it) }
                                )
                            }
                            AunioTab.APPEARANCE -> {
                                AppearanceSettingsScreen(isArabic = isArabic)
                            }
                            AunioTab.LANGUAGE -> {
                                LanguageSettingsScreen(
                                    isArabic = isArabic,
                                    onLanguageToggle = { viewModel.toggleLanguage() }
                                )
                            }
                            AunioTab.ACCOUNT -> {
                                val completedCount = goalsList.count { it.status == "COMPLETED" }
                                AccountSettingsScreen(
                                    isArabic = isArabic,
                                    memoryCount = memoriesList.size,
                                    projectCount = projectsList.size,
                                    goalsCompletedCount = completedCount
                                )
                            }
                            AunioTab.BACKUP -> {
                                BackupRestoreScreen(
                                    passphrase = passphrase,
                                    isArabic = isArabic,
                                    onPassphraseChange = { viewModel.updateBackupPassphrase(it) },
                                    onExportBackup = { callback -> viewModel.exportBackup(callback) },
                                    onImportBackup = { token, callback -> viewModel.importBackup(token, callback) }
                                )
                            }
                            AunioTab.SETTINGS -> {
                                SystemSettingsScreen(
                                    isArabic = isArabic,
                                    onDeleteAllMemories = { viewModel.deleteAllMemories() }
                                )
                            }
                            AunioTab.HELP -> {
                                HelpScreen(isArabic = isArabic)
                            }
                        }
                    }
                }
            }
        }
    }
}
