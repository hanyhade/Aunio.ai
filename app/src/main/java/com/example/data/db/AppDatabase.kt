package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ChatMessageEntity::class,
        MemoryEntity::class,
        ProjectEntity::class,
        GoalEntity::class,
        ReminderEntity::class,
        UserProfileEntity::class,
        SettingsEntity::class,
        BackupMetadataEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun memoryDao(): MemoryDao
    abstract fun projectDao(): ProjectDao
    abstract fun goalDao(): GoalDao
    abstract fun reminderDao(): ReminderDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun settingsDao(): SettingsDao
    abstract fun backupMetadataDao(): BackupMetadataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create user_profile table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `user_profile` (" +
                        "`id` TEXT NOT NULL PRIMARY KEY, " +
                        "`name` TEXT NOT NULL, " +
                        "`email` TEXT NOT NULL, " +
                        "`avatarUri` TEXT, " +
                        "`bio` TEXT, " +
                        "`updatedAt` INTEGER NOT NULL" +
                    ")"
                )

                // Create settings table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `settings` (" +
                        "`id` TEXT NOT NULL PRIMARY KEY, " +
                        "`isArabic` INTEGER NOT NULL, " +
                        "`isDarkMode` INTEGER NOT NULL, " +
                        "`biometricLock` INTEGER NOT NULL, " +
                        "`voiceType` TEXT NOT NULL, " +
                        "`autoBackup` INTEGER NOT NULL, " +
                        "`updatedAt` INTEGER NOT NULL" +
                    ")"
                )

                // Create backup_metadata table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `backup_metadata` (" +
                        "`id` TEXT NOT NULL PRIMARY KEY, " +
                        "`backupTimestamp` INTEGER NOT NULL, " +
                        "`passphraseHash` TEXT NOT NULL, " +
                        "`fileSize` INTEGER NOT NULL, " +
                        "`recordCount` INTEGER NOT NULL, " +
                        "`backupStatus` TEXT NOT NULL, " +
                        "`checksum` TEXT" +
                    ")"
                )

                // Create indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_timestamp` ON `chat_messages` (`timestamp`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_memories_category` ON `memories` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_memories_lastAccessed` ON `memories` (`lastAccessed`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_backup_metadata_backupTimestamp` ON `backup_metadata` (`backupTimestamp`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aunio_ai_database"
                )
                .addMigrations(MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

