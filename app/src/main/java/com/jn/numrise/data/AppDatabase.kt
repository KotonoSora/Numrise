package com.jn.numrise.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [LevelEntity::class, PlayerStatsEntity::class, HistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun levelDao(): LevelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "numrise_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.levelDao())
                }
            }
        }

        suspend fun populateDatabase(levelDao: LevelDao) {
            // Initial player stats
            levelDao.insertPlayerStats(PlayerStatsEntity(coins = 100))

            // Initial levels
            val levels = mutableListOf<LevelEntity>()
            for (i in 1..20) {
                levels.add(
                    LevelEntity(
                        id = i,
                        gridSize = if (i <= 5) 3 else if (i <= 12) 4 else 5,
                        isUnlocked = i == 1 // Only level 1 is unlocked initially
                    )
                )
            }
            levelDao.insertLevels(levels)
        }
    }
}
