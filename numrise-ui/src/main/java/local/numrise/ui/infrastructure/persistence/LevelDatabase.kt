package local.numrise.ui.infrastructure.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [LevelEntity::class], version = 1, exportSchema = false)
abstract class LevelDatabase : RoomDatabase() {
    abstract fun levelDao(): LevelDao

    companion object {
        @Volatile
        private var INSTANCE: LevelDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LevelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LevelDatabase::class.java,
                    "numrise_levels.db"
                )
                    .addCallback(LevelDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class LevelDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val levels = mutableListOf<LevelEntity>()
                    for (i in 1..20) {
                        levels.add(
                            LevelEntity(
                                id = i,
                                gridSize = if (i <= 5) 3 else if (i <= 12) 4 else 5,
                                isUnlocked = i == 1
                            )
                        )
                    }
                    database.levelDao().insertLevels(levels)
                }
            }
        }
    }
}
