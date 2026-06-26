package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "chat_messages",
    indices = [Index(value = ["timestamp"])]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val sender: String, // "user" or "ai"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "memories",
    indices = [
        Index(value = ["category"]),
        Index(value = ["lastAccessed"])
    ]
)
data class MemoryEntity(
    @PrimaryKey val id: String,
    val keyConcept: String,
    val valueDetails: String,
    val category: String, // "PREFERENCE", "GOAL", "RELATIONSHIP", "FACT"
    val score: Float, // Confidence / emotional score
    val lastAccessed: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val status: String, // "PLANNED", "ACTIVE", "COMPLETED", "ON_HOLD"
    val deadline: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class GoalEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val status: String, // "PLANNED", "ACTIVE", "COMPLETED", "ON_HOLD"
    val targetDate: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val title: String,
    val fireTime: Long,
    val isTriggered: Boolean = false,
    val type: String // "MANUAL", "AI_EXTRACTED"
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "current_user",
    val name: String,
    val email: String,
    val avatarUri: String? = null,
    val bio: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: String = "default_settings",
    val isArabic: Boolean = false,
    val isDarkMode: Boolean = true,
    val biometricLock: Boolean = false,
    val voiceType: String = "default",
    val autoBackup: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "backup_metadata",
    indices = [Index(value = ["backupTimestamp"])]
)
data class BackupMetadataEntity(
    @PrimaryKey val id: String,
    val backupTimestamp: Long,
    val passphraseHash: String,
    val fileSize: Long,
    val recordCount: Int,
    val backupStatus: String, // "SUCCESS", "FAILED"
    val checksum: String? = null
)

