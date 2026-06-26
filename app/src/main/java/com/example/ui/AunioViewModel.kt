package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AunioRepository
import com.example.data.db.ChatMessageEntity
import com.example.data.db.GoalEntity
import com.example.data.db.MemoryEntity
import com.example.data.db.ProjectEntity
import com.example.data.db.ReminderEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.UUID

import android.content.Context
import com.example.ui.theme.AppThemeManager

enum class AunioTab {
    CHAT,
    MEMORIES,
    PROJECTS,
    GOALS,
    REMINDERS,
    APPEARANCE,
    LANGUAGE,
    ACCOUNT,
    BACKUP,
    SETTINGS,
    HELP
}

class AunioViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AunioRepository(application)
    private val coordinator = com.example.data.api.IntelligenceCoordinator(application, repository)

    // Onboarding Experience State
    private val sharedPrefs = application.getSharedPreferences("aunio_prefs", Context.MODE_PRIVATE)
    private val _isOnboardingCompleted = MutableStateFlow(sharedPrefs.getBoolean("onboarding_completed", false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    fun completeOnboarding() {
        sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
        _isOnboardingCompleted.value = true
    }

    // Dynamic Navigation Tab
    private val _selectedTab = MutableStateFlow(AunioTab.CHAT)
    val selectedTab: StateFlow<AunioTab> = _selectedTab.asStateFlow()

    // Database states directly bound to reactive UI Flows
    private val _chatLimit = MutableStateFlow(30)
    val chatLimit: StateFlow<Int> = _chatLimit.asStateFlow()

    fun loadMoreChatMessages() {
        _chatLimit.value += 30
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _chatLimit
        .flatMapLatest { limit ->
            repository.getChatMessagesWithLimit(limit)
        }
        .map { list -> list.reversed() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<ReminderEntity>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Language controller (Arabic/English toggle)
    private val _isArabic = MutableStateFlow(false)
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    // Chat UI variables
    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Suggestions states (for auto project/goal detection)
    data class ProjectSuggestion(
        val title: String,
        val description: String,
        val durationDays: Int
    )

    data class GoalSuggestion(
        val title: String,
        val projectId: String?,
        val targetDays: Int
    )

    data class ActionSuggestion(
        val title: String,
        val subtitle: String? = null,
        val iconType: String, // e.g., "Plan", "Memory", "Project", "Search", "Summarize", "Study", "Image"
        val prompt: String
    )

    private val _suggestedProject = MutableStateFlow<ProjectSuggestion?>(null)
    val suggestedProject: StateFlow<ProjectSuggestion?> = _suggestedProject.asStateFlow()

    private val _suggestedGoal = MutableStateFlow<GoalSuggestion?>(null)
    val suggestedGoal: StateFlow<GoalSuggestion?> = _suggestedGoal.asStateFlow()

    private val _suggestedActions = MutableStateFlow<List<ActionSuggestion>>(emptyList())
    val suggestedActions: StateFlow<List<ActionSuggestion>> = _suggestedActions.asStateFlow()


    fun dismissProjectSuggestion() {
        _suggestedProject.value = null
    }

    fun dismissGoalSuggestion() {
        _suggestedGoal.value = null
    }

    // Encrypted Backup UX Variables
    private val _backupPassphrase = MutableStateFlow("")
    val backupPassphrase: StateFlow<String> = _backupPassphrase.asStateFlow()

    init {
        AppThemeManager.init(application)
        viewModelScope.launch {
            // Update suggestions dynamically whenever messages change
            repository.chatMessages.collect { msgs ->
                updateSuggestedActions(msgs)
            }
        }
        viewModelScope.launch {
            // Seed a welcoming, intelligent greeting if the history is completely clean
            val currentMsg = repository.chatMessages.first()
            if (currentMsg.isEmpty()) {
                repository.addChatMessage(
                    "Welcome to **AUNIO.AI**! 🚀\nI am AUNIO.AI with unified long-term memory, goal tracking, and smart reminder scheduling. " +
                            "I speak English and Arabic equally, including native Egyptian dialect (Masri).\n\n" +
                            "Try saying: \"Remind me to study machine learning tomorrow at 6 PM\" or \"أنا بحب القهوة السادة من غير سكر\". " +
                            "I will automatically extract your preferences into my memory vault or schedule exact reminders!",
                    "ai"
                )
            }
        }
    }

    private fun updateSuggestedActions(messages: List<ChatMessageEntity>) {
        if (messages.isEmpty()) {
            _suggestedActions.value = emptyList()
            return
        }
        val lastMsg = messages.last()
        // Determine context heuristically based on the last message
        val lowerText = lastMsg.text.lowercase()
        val actions = mutableListOf<ActionSuggestion>()

        when {
            lowerText.contains("study") || lowerText.contains("learn") || lowerText.contains("ذاكر") || lowerText.contains("اتعلم") -> {
                actions.add(ActionSuggestion(
                    title = if (_isArabic.value) "خطة دراسة" else "Study Plan",
                    subtitle = if (_isArabic.value) "إنشاء جدول" else "Create a schedule",
                    iconType = "Study",
                    prompt = if (_isArabic.value) "أريد إنشاء خطة دراسة لهذا الموضوع." else "I want to create a study plan for this topic."
                ))
                actions.add(ActionSuggestion(
                    title = if (_isArabic.value) "اختبرني" else "Quiz Me",
                    subtitle = if (_isArabic.value) "أسئلة سريعة" else "Quick questions",
                    iconType = "Memory",
                    prompt = if (_isArabic.value) "اختبرني في هذا الموضوع." else "Quiz me on this topic."
                ))
                actions.add(ActionSuggestion(
                    title = if (_isArabic.value) "تلخيص" else "Summarize",
                    subtitle = if (_isArabic.value) "أهم النقاط" else "Key points",
                    iconType = "Summarize",
                    prompt = if (_isArabic.value) "لخص لي أهم النقاط في هذا الموضوع." else "Summarize the key points for me."
                ))
            }
            lowerText.contains("project") || lowerText.contains("task") || lowerText.contains("مشروع") || lowerText.contains("مهمة") -> {
                actions.add(ActionSuggestion(
                    title = if (_isArabic.value) "متابعة المشروع" else "Continue Project",
                    subtitle = if (_isArabic.value) "ما هي الخطوة التالية؟" else "What's the next step?",
                    iconType = "Project",
                    prompt = if (_isArabic.value) "ما هي الخطوة التالية في هذا المشروع؟" else "What's the next step for this project?"
                ))
                actions.add(ActionSuggestion(
                    title = if (_isArabic.value) "إضافة مهمة" else "Create Task",
                    subtitle = if (_isArabic.value) "إلى قائمة المهام" else "To your to-do list",
                    iconType = "Plan",
                    prompt = if (_isArabic.value) "أريد إضافة مهمة جديدة لهذا المشروع." else "I want to create a new task for this project."
                ))
                actions.add(ActionSuggestion(
                    title = if (_isArabic.value) "تحديد موعد" else "Set Deadline",
                    subtitle = if (_isArabic.value) "للمشروع الحالي" else "For current work",
                    iconType = "Plan",
                    prompt = if (_isArabic.value) "أريد تحديد موعد نهائي لهذا المشروع." else "I want to set a deadline for this project."
                ))
            }
            else -> {
                // Casual chat or general: hide most, maybe show something generic or empty
                if (lastMsg.sender == "ai") {
                    actions.add(ActionSuggestion(
                        title = if (_isArabic.value) "خطة اليوم" else "Plan Today",
                        subtitle = if (_isArabic.value) "تنظيم المهام" else "Organize tasks",
                        iconType = "Plan",
                        prompt = if (_isArabic.value) "ما هي خطتي لليوم؟" else "What should I plan for today?"
                    ))
                    actions.add(ActionSuggestion(
                        title = if (_isArabic.value) "تذكر هذا" else "Remember This",
                        subtitle = if (_isArabic.value) "حفظ في الذاكرة" else "Save to vault",
                        iconType = "Memory",
                        prompt = if (_isArabic.value) "احفظ هذه المعلومات في ذاكرتك." else "Please remember this information."
                    ))
                    actions.add(ActionSuggestion(
                        title = if (_isArabic.value) "توليد صورة" else "Create Image",
                        subtitle = if (_isArabic.value) "باستخدام AI" else "Using AI",
                        iconType = "Image",
                        prompt = if (_isArabic.value) "قم بتوليد صورة بناءً على هذا." else "Please create an image based on this."
                    ))
                }
            }
        }
        _suggestedActions.value = actions
    }

    fun selectTab(tab: AunioTab) {
        _selectedTab.value = tab
    }

    fun toggleLanguage() {
        _isArabic.value = !_isArabic.value
    }

    fun updateChatInput(text: String) {
        _chatInput.value = text
    }

    fun updateBackupPassphrase(text: String) {
        _backupPassphrase.value = text
    }

    // --- Message execution ---
    fun sendChatMessage() {
        val message = _chatInput.value.trim()
        if (message.isEmpty() || _isGenerating.value) return

        _chatInput.value = ""
        _isGenerating.value = true

        viewModelScope.launch {
            try {
                repository.generateAiResponse(message, viewModelScope)

                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
                    coordinator.processMessageBackground(message, apiKey)
                }
            } catch (e: Exception) {
                Log.e("AunioViewModel", "Error sending message", e)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
            repository.addChatMessage(
                "Conversational history has been wiped clean. How can I assist you now?",
                "ai"
            )
        }
    }

    // --- Memory Operations ---
    fun deleteMemory(id: String) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun saveMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            repository.addMemory(memory)
        }
    }

    fun deleteAllMemories() {
        viewModelScope.launch {
            repository.deleteAllMemories()
        }
    }

    // --- Projects & Goals Operations ---
    fun createProject(title: String, description: String, deadlineDays: Int) {
        val deadlineMillis = System.currentTimeMillis() + (deadlineDays * 24 * 60 * 60 * 1000L)
        val project = ProjectEntity(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            description = description.trim(),
            status = "ACTIVE",
            deadline = deadlineMillis,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.addProject(project)
            
            // Automatically schedule a progress follow-up reminder for this project (e.g. in 3 days)
            val followUpTitle = "Follow-up: Check progress on '${title.trim()}' 🎯"
            val fireTime = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L) // 3 days
            val followUpReminder = ReminderEntity(
                id = UUID.randomUUID().toString(),
                title = followUpTitle,
                fireTime = fireTime,
                isTriggered = false,
                type = "AI_EXTRACTED"
            )
            repository.addReminder(followUpReminder)
        }
    }

    fun updateProjectStatus(id: String, status: String) {
        viewModelScope.launch {
            repository.updateProjectStatus(id, status)
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            repository.deleteProject(id)
        }
    }

    fun createGoal(projectId: String, title: String, targetDays: Int) {
        val targetMillis = System.currentTimeMillis() + (targetDays * 24 * 60 * 60 * 1000L)
        val goal = GoalEntity(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            title = title.trim(),
            status = "ACTIVE",
            targetDate = targetMillis,
            createdAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.addGoal(goal)
        }
    }

    fun updateGoalStatus(id: String, isCompleted: Boolean) {
        val newStatus = if (isCompleted) "COMPLETED" else "ACTIVE"
        viewModelScope.launch {
            repository.updateGoalStatus(id, newStatus)
        }
    }

    fun deleteGoal(id: String) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    fun scheduleProjectFollowUp(projectTitle: String, delayMinutes: Int) {
        val followUpTitle = "Follow-up: Check progress on '$projectTitle' 🎯"
        val fireTime = System.currentTimeMillis() + (delayMinutes * 60 * 1000L)
        val reminder = ReminderEntity(
            id = UUID.randomUUID().toString(),
            title = followUpTitle,
            fireTime = fireTime,
            isTriggered = false,
            type = "MANUAL"
        )
        viewModelScope.launch {
            repository.addReminder(reminder)
        }
    }

    // --- Reminders Operations ---
    fun createReminder(title: String, delayMinutes: Int) {
        val fireTime = System.currentTimeMillis() + (delayMinutes * 60 * 1000L)
        val reminder = ReminderEntity(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            fireTime = fireTime,
            isTriggered = false,
            type = "MANUAL"
        )
        viewModelScope.launch {
            repository.addReminder(reminder)
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            repository.deleteReminder(id)
        }
    }

    // --- AES-256 encrypted local backup export/import ---
    fun exportBackup(onResult: (String?) -> Unit) {
        val passphrase = _backupPassphrase.value.trim()
        if (passphrase.isEmpty()) {
            onResult(null)
            return
        }
        viewModelScope.launch {
            val base64Backup = repository.exportEncryptedBackup(passphrase)
            onResult(base64Backup)
        }
    }

    fun importBackup(backupString: String, onResult: (Boolean) -> Unit) {
        val passphrase = _backupPassphrase.value.trim()
        if (passphrase.isEmpty() || backupString.trim().isEmpty()) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            val success = repository.importEncryptedBackup(passphrase, backupString.trim())
            onResult(success)
        }
    }
}
