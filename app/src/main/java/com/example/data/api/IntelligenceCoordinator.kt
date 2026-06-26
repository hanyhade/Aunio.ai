package com.example.data.api

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.AunioRepository
import com.example.data.db.AppDatabase
import com.example.data.db.GoalEntity
import com.example.data.db.MemoryEntity
import com.example.data.db.ProjectEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class IntelligenceCoordinator(
    private val context: Context,
    private val repository: AunioRepository
) {
    private val coordinatorScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentJob: Job? = null
    
    private val db = AppDatabase.getDatabase(context)
    private val memoryDao = db.memoryDao()
    private val projectDao = db.projectDao()
    private val goalDao = db.goalDao()
    private val reminderDao = db.reminderDao()
    private val chatMessageDao = db.chatMessageDao()

    fun processMessageBackground(userMessage: String, apiKey: String) {
        // Prevent duplicate executions and cancel unnecessary background tasks if a new message arrives
        currentJob?.cancel()
        
        currentJob = coordinatorScope.launch {
            try {
                // Priority 1: Memory Engine
                Log.d("IntelligenceCoordinator", "Coordinator: Running Memory Engine...")
                runMemoryEngine(userMessage, apiKey)
                
                yield()

                // Priority 2: Reminder Engine
                Log.d("IntelligenceCoordinator", "Coordinator: Running Reminder Engine...")
                runReminderEngine(userMessage, apiKey)
                
                yield()

                // Priority 3: Knowledge Engine (Placeholder if any)
                Log.d("IntelligenceCoordinator", "Coordinator: Running Knowledge Engine...")
                
                yield()

                // Priority 4: Project & Goal Engine
                Log.d("IntelligenceCoordinator", "Coordinator: Running Project & Goal Engine...")
                runProjectAndGoalEngine(userMessage, apiKey)
                
                yield()

                // Priority 5: Personality Engine
                Log.d("IntelligenceCoordinator", "Coordinator: Running Personality Engine...")
                runPersonalityEngine(userMessage)

                yield()
                
                // Priority 6: Behavior / Prediction Engine
                Log.d("IntelligenceCoordinator", "Coordinator: Running Behavior / Prediction Engine...")
                runPredictionEngine(userMessage)
                
                yield()

                // Priority 7: Reflection Engine
                Log.d("IntelligenceCoordinator", "Coordinator: Running Reflection Engine...")
                runReflectionEngine(userMessage)

            } catch (e: CancellationException) {
                Log.d("IntelligenceCoordinator", "Coordinator: Background processing cancelled for new message.")
            } catch (e: Exception) {
                Log.e("IntelligenceCoordinator", "Coordinator: Error in background processing", e)
            }
        }
    }

    private suspend fun runMemoryEngine(userMessage: String, apiKey: String) {
        try {
            val existingMemories = memoryDao.getAllMemories().first()
            val result = MemoryProcessor.processAndExtractMemoryAndMerge(userMessage, existingMemories, apiKey)
            if (result != null) {
                if (result.isUpdateOrMerge == true && !result.targetMemoryIdToReplace.isNullOrBlank()) {
                    val oldMemoryId = result.targetMemoryIdToReplace
                    val oldMemory = existingMemories.find { it.id == oldMemoryId }
                    if (oldMemory != null) {
                        val mergedConcept = result.keyConcept ?: oldMemory.keyConcept
                        var mergedDetails = result.valueDetails ?: oldMemory.valueDetails
                        
                        if (!result.relatedMemoryIds.isNullOrEmpty()) {
                            val relatedConcepts = existingMemories
                                .filter { result.relatedMemoryIds.contains(it.id) }
                                .map { it.keyConcept }
                            if (relatedConcepts.isNotEmpty()) {
                                mergedDetails += " (Related to: ${relatedConcepts.joinToString(", ")})"
                            }
                        }
                        
                        val updatedMemory = oldMemory.copy(
                            keyConcept = mergedConcept,
                            valueDetails = mergedDetails,
                            category = result.category ?: oldMemory.category,
                            score = result.confidenceScore ?: oldMemory.score,
                            lastAccessed = System.currentTimeMillis()
                        )
                        memoryDao.insertMemory(updatedMemory)
                        
                        if (!result.relatedMemoryIds.isNullOrEmpty()) {
                            for (relatedId in result.relatedMemoryIds) {
                                val relatedMem = existingMemories.find { it.id == relatedId }
                                if (relatedMem != null && !relatedMem.valueDetails.contains(mergedConcept)) {
                                    val updatedRelated = relatedMem.copy(
                                        valueDetails = "${relatedMem.valueDetails} (Related to: $mergedConcept)"
                                    )
                                    memoryDao.insertMemory(updatedRelated)
                                }
                            }
                        }
                    } else {
                        val newMemory = MemoryEntity(
                            id = java.util.UUID.randomUUID().toString(),
                            keyConcept = result.keyConcept ?: "Memory",
                            valueDetails = result.valueDetails ?: "",
                            category = result.category ?: "FACT",
                            score = result.confidenceScore ?: 0.7f,
                            lastAccessed = System.currentTimeMillis(),
                            createdAt = System.currentTimeMillis()
                        )
                        memoryDao.insertMemory(newMemory)
                    }
                } else {
                    val newId = java.util.UUID.randomUUID().toString()
                    val concept = result.keyConcept ?: "Memory"
                    var details = result.valueDetails ?: ""
                    
                    if (!result.relatedMemoryIds.isNullOrEmpty()) {
                        val relatedConcepts = existingMemories
                            .filter { result.relatedMemoryIds.contains(it.id) }
                            .map { it.keyConcept }
                        if (relatedConcepts.isNotEmpty()) {
                            details += " (Related to: ${relatedConcepts.joinToString(", ")})"
                        }
                    }
                    
                    val newMemory = MemoryEntity(
                        id = newId,
                        keyConcept = concept,
                        valueDetails = details,
                        category = result.category ?: "FACT",
                        score = result.confidenceScore ?: 0.7f,
                        lastAccessed = System.currentTimeMillis(),
                        createdAt = System.currentTimeMillis()
                    )
                    memoryDao.insertMemory(newMemory)
                    
                    if (!result.relatedMemoryIds.isNullOrEmpty()) {
                        for (relatedId in result.relatedMemoryIds) {
                            val relatedMem = existingMemories.find { it.id == relatedId }
                            if (relatedMem != null && !relatedMem.valueDetails.contains(concept)) {
                                val updatedRelated = relatedMem.copy(
                                    valueDetails = "${relatedMem.valueDetails} (Related to: $concept)"
                                )
                                memoryDao.insertMemory(updatedRelated)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("IntelligenceCoordinator", "Error in memory engine", e)
        }
    }

    private suspend fun runReminderEngine(userMessage: String, apiKey: String) {
        try {
            val extractedReminder = ReminderProcessor.extractReminder(userMessage, apiKey)
            if (extractedReminder != null) {
                repository.addReminder(extractedReminder)
                Log.d("IntelligenceCoordinator", "Extracted and scheduled new reminder: ${extractedReminder.title}")
            }
        } catch (e: Exception) {
            Log.e("IntelligenceCoordinator", "Error in reminder engine", e)
        }
    }

    private suspend fun runProjectAndGoalEngine(userMessage: String, apiKey: String) {
        try {
            val activeProjects = projectDao.getAllProjects().first()
            val projectsJson = activeProjects.joinToString("\n") { "- ID: ${it.id}, Title: ${it.title}" }

            val draftsContainer = DraftStore.loadDrafts(context)
            val draftsJson = if (draftsContainer.projectDrafts.isEmpty() && draftsContainer.goalDrafts.isEmpty()) {
                "No drafts currently exist."
            } else {
                val projDraftsStr = draftsContainer.projectDrafts.joinToString("\n") {
                    "- ID: ${it.id}, Title: ${it.title}, Mentions/Confidence: ${it.mentions}"
                }
                val goalDraftsStr = draftsContainer.goalDrafts.joinToString("\n") {
                    "- ID: ${it.id}, Title: ${it.title}, ProjectDraftId: ${it.projectId}, Mentions/Confidence: ${it.mentions}"
                }
                "Project Drafts:\n$projDraftsStr\n\nGoal Drafts:\n$goalDraftsStr"
            }

            val detection = ProjectGoalProcessor.detectProjectAndGoal(userMessage, projectsJson, draftsJson, apiKey)
            if (detection != null) {
                val updatedProjectDrafts = draftsContainer.projectDrafts.toMutableList()
                val updatedGoalDrafts = draftsContainer.goalDrafts.toMutableList()
                
                var processedProjectDraftId: String? = null
                var processedGoalDraftId: String? = null

                if (detection.hasSuggestedProject && !detection.projectTitle.isNullOrBlank()) {
                    val title = detection.projectTitle.trim()
                    val desc = detection.projectDescription ?: ""
                    val duration = detection.projectDurationDays ?: 14
                    
                    val existingIndex = if (!detection.matchedProjectDraftId.isNullOrBlank()) {
                        updatedProjectDrafts.indexOfFirst { it.id == detection.matchedProjectDraftId }
                    } else {
                        updatedProjectDrafts.indexOfFirst { it.title.equals(title, ignoreCase = true) || title.contains(it.title, ignoreCase = true) || it.title.contains(title, ignoreCase = true) }
                    }

                    if (existingIndex != -1) {
                        val oldDraft = updatedProjectDrafts[existingIndex]
                        val updatedDraft = oldDraft.copy(
                            title = title,
                            description = if (desc.isNotEmpty()) desc else oldDraft.description,
                            durationDays = duration,
                            mentions = oldDraft.mentions + 1,
                            lastMentionedAt = System.currentTimeMillis()
                        )
                        updatedProjectDrafts[existingIndex] = updatedDraft
                        processedProjectDraftId = updatedDraft.id
                    } else {
                        val newId = java.util.UUID.randomUUID().toString()
                        val newDraft = ProjectDraft(
                            id = newId,
                            title = title,
                            description = desc,
                            durationDays = duration,
                            mentions = 1
                        )
                        updatedProjectDrafts.add(newDraft)
                        processedProjectDraftId = newId
                    }
                }

                if (detection.hasSuggestedGoal && !detection.goalTitle.isNullOrBlank()) {
                    val title = detection.goalTitle.trim()
                    val target = detection.goalTargetDays ?: 7
                    val targetProjId = detection.goalProjectId ?: processedProjectDraftId
                    
                    val existingIndex = if (!detection.matchedGoalDraftId.isNullOrBlank()) {
                        updatedGoalDrafts.indexOfFirst { it.id == detection.matchedGoalDraftId }
                    } else {
                        updatedGoalDrafts.indexOfFirst { it.title.equals(title, ignoreCase = true) || title.contains(it.title, ignoreCase = true) || it.title.contains(title, ignoreCase = true) }
                    }

                    if (existingIndex != -1) {
                        val oldDraft = updatedGoalDrafts[existingIndex]
                        val updatedDraft = oldDraft.copy(
                            title = title,
                            projectId = targetProjId ?: oldDraft.projectId,
                            targetDays = target,
                            mentions = oldDraft.mentions + 1,
                            lastMentionedAt = System.currentTimeMillis()
                        )
                        updatedGoalDrafts[existingIndex] = updatedDraft
                        processedGoalDraftId = updatedDraft.id
                    } else {
                        val newId = java.util.UUID.randomUUID().toString()
                        val newDraft = GoalDraft(
                            id = newId,
                            projectId = targetProjId,
                            title = title,
                            targetDays = target,
                            mentions = 1
                        )
                        updatedGoalDrafts.add(newDraft)
                        processedGoalDraftId = newId
                    }
                }

                DraftStore.saveDrafts(context, DraftsContainer(updatedProjectDrafts, updatedGoalDrafts))

                val draftsToCreateProjects = updatedProjectDrafts.filter { it.mentions >= 2 }
                val draftsToCreateGoals = updatedGoalDrafts.filter { it.mentions >= 2 }

                val projectDraftToRealIdMap = mutableMapOf<String, String>()

                for (draft in draftsToCreateProjects) {
                    val realProjId = java.util.UUID.randomUUID().toString()
                    projectDraftToRealIdMap[draft.id] = realProjId
                    
                    val deadlineMillis = System.currentTimeMillis() + (draft.durationDays * 24 * 60 * 60 * 1000L)
                    val project = ProjectEntity(
                        id = realProjId,
                        title = draft.title,
                        description = draft.description,
                        status = "ACTIVE",
                        deadline = deadlineMillis,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    projectDao.insertProject(project)
                    
                    val notifyMsg = "🎯 *AUNIO Auto-Discovery*:\nI've detected strong repeated interest in project **\"${draft.title}\"**. I have automatically created this project in your pipeline so you can track it!"
                    repository.addChatMessage(notifyMsg, "ai")
                }

                for (goalDraft in draftsToCreateGoals) {
                    var mappedProjId = goalDraft.projectId
                    if (mappedProjId != null && projectDraftToRealIdMap.containsKey(mappedProjId)) {
                        mappedProjId = projectDraftToRealIdMap[mappedProjId]
                    }
                    
                    val finalProjId = if (mappedProjId != null) {
                        mappedProjId
                    } else {
                        val latestProjects = projectDao.getAllProjects().first()
                        latestProjects.firstOrNull()?.id
                    }

                    if (finalProjId != null) {
                        val targetMillis = System.currentTimeMillis() + (goalDraft.targetDays * 24 * 60 * 60 * 1000L)
                        val goal = GoalEntity(
                            id = java.util.UUID.randomUUID().toString(),
                            projectId = finalProjId,
                            title = goalDraft.title,
                            status = "ACTIVE",
                            targetDate = targetMillis,
                            createdAt = System.currentTimeMillis()
                        )
                        goalDao.insertGoal(goal)
                        
                        val notifyMsg = "🎯 *AUNIO Goal Activation*:\nSince you mentioned goal **\"${goalDraft.title}\"** repeatedly, I have automatically activated and linked it to your active project!"
                        repository.addChatMessage(notifyMsg, "ai")
                    }
                }

                val remainingProjects = updatedProjectDrafts.filter { it.mentions < 2 }
                val remainingGoals = updatedGoalDrafts.filter { it.mentions < 2 }.map { g ->
                    val draftProjId = g.projectId
                    if (draftProjId != null && projectDraftToRealIdMap.containsKey(draftProjId)) {
                        g.copy(projectId = projectDraftToRealIdMap[draftProjId])
                    } else {
                        g
                    }
                }
                DraftStore.saveDrafts(context, DraftsContainer(remainingProjects, remainingGoals))
            }
        } catch (e: Exception) {
            Log.e("IntelligenceCoordinator", "Error in project/goal engine", e)
        }
    }

    private suspend fun runPersonalityEngine(userMessage: String) {
        try {
            Log.d("IntelligenceCoordinator", "Executing background personality learning for message: $userMessage")
        } catch (e: Exception) {
            Log.e("IntelligenceCoordinator", "Error in personality learning", e)
        }
    }

    private suspend fun runPredictionEngine(userMessage: String) {
        try {
            Log.d("IntelligenceCoordinator", "Executing background user behavior prediction")
        } catch (e: Exception) {
            Log.e("IntelligenceCoordinator", "Error in prediction", e)
        }
    }

    private suspend fun runReflectionEngine(userMessage: String) {
        try {
            Log.d("IntelligenceCoordinator", "Executing background reflection analysis")
        } catch (e: Exception) {
            Log.e("IntelligenceCoordinator", "Error in reflection", e)
        }
    }
}
