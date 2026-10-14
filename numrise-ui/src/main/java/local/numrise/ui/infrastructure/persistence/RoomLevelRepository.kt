package local.numrise.ui.infrastructure.persistence

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import local.numrise.engine.domain.model.Level
import local.numrise.engine.domain.repository.LevelRepository

class RoomLevelRepository(private val levelDao: LevelDao) : LevelRepository {
    override fun levelsFlow(): Flow<List<Level>> =
        levelDao.getAllLevels().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getLevelById(levelId: Int): Level? =
        levelDao.getLevelById(levelId)?.toDomain()

    override suspend fun updateLevel(level: Level) =
        levelDao.updateLevel(level.toEntity())

    private fun LevelEntity.toDomain(): Level = Level(
        id = id,
        gridSize = gridSize,
        highScore = highScore,
        bestTimeSeconds = bestTimeSeconds,
        isUnlocked = isUnlocked,
        stars = stars
    )

    private fun Level.toEntity(): LevelEntity = LevelEntity(
        id = id,
        gridSize = gridSize,
        isUnlocked = isUnlocked,
        highScore = highScore,
        bestTimeSeconds = bestTimeSeconds,
        stars = stars
    )
}
