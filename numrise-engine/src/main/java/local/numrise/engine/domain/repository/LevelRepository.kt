package local.numrise.engine.domain.repository

import kotlinx.coroutines.flow.Flow
import local.numrise.engine.domain.model.Level

interface LevelRepository {
    fun levelsFlow(): Flow<List<Level>>
    suspend fun getLevelById(levelId: Int): Level?
    suspend fun updateLevel(level: Level)
}
