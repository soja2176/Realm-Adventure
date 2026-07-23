package com.example.data

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

import androidx.room.Ignore

@Entity(tableName = "game_progress")
data class GameProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isActiveChar: Boolean = true,
    val charName: String = "",
    val charRace: String = "",
    val charClass: String = "",
    val charLevel: Int = 1,
    val charExp: Int = 0,
    val charGold: Int = 100,
    val statStr: Int = 10,
    val statDex: Int = 10,
    val statInt: Int = 10,
    val statCon: Int = 10,
    val statPointsAvailable: Int = 0,
    val talentPointsSpent: Int = 0,
    val talentPointsAvailable: Int = 0,
    val maxHp: Int = 100,
    val currentHp: Int = 100,
    val maxMp: Int = 50,
    val currentMp: Int = 50,
    val equippedHelmetJson: String = "",
    val equippedWingsJson: String = "",
    val equippedWeaponJson: String = "",
    val equippedShieldJson: String = "",
    val equippedArmorJson: String = "",
    val equippedGlovesJson: String = "",
    val equippedBootsJson: String = "",
    val equippedRingJson: String = "",
    val equippedEarringJson: String = "",
    val equippedRelicJson: String = "",
    val inventoryJson: String = "[]",
    val talentsJson: String = "[]",
    val skillsJson: String = "[]",
    val completedQuestsJson: String = "[]",
    val mapPointsExploredJson: String = "[]",
    val highestUnlockedDungeon: Int = 1,
    val completedDungeonsJson: String = "[]",
    val currentX: Int = 0,
    val currentY: Int = 0
) {
    @get:Ignore
    val hasActiveChar: Boolean get() = isActiveChar && charName.isNotBlank()
}

@Dao
interface GameProgressDao {
    @Query("SELECT * FROM game_progress WHERE isActiveChar = 1 LIMIT 1")
    fun getActiveGameProgress(): Flow<GameProgress?>

    @Query("SELECT * FROM game_progress WHERE isActiveChar = 1 LIMIT 1")
    suspend fun getActiveGameProgressSync(): GameProgress?

    @Query("SELECT * FROM game_progress ORDER BY id DESC")
    fun getAllGameProgress(): Flow<List<GameProgress>>

    @Query("SELECT * FROM game_progress WHERE id = :id LIMIT 1")
    suspend fun getGameProgressById(id: Int): GameProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameProgress(progress: GameProgress): Long

    @Query("UPDATE game_progress SET isActiveChar = 0")
    suspend fun deactivateAllCharacters()

    @Query("UPDATE game_progress SET isActiveChar = 1 WHERE id = :id")
    suspend fun setActiveCharacter(id: Int)

    @Query("DELETE FROM game_progress WHERE id = :id")
    suspend fun deleteCharacterById(id: Int)

    @Query("DELETE FROM game_progress")
    suspend fun clearGameProgress()
}

@Database(entities = [GameProgress::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameProgressDao(): GameProgressDao
}

class GameProgressRepository(private val dao: GameProgressDao) {
    val progressFlow: Flow<GameProgress?> = dao.getActiveGameProgress()
    val allCharactersFlow: Flow<List<GameProgress>> = dao.getAllGameProgress()

    suspend fun getProgress(): GameProgress? {
        return dao.getActiveGameProgressSync()
    }

    suspend fun saveProgress(progress: GameProgress): Long {
        return dao.saveGameProgress(progress)
    }

    suspend fun deactivateAll() {
        dao.deactivateAllCharacters()
    }

    suspend fun setActive(id: Int) {
        dao.deactivateAllCharacters()
        dao.setActiveCharacter(id)
    }

    suspend fun deleteCharacter(id: Int) {
        dao.deleteCharacterById(id)
    }

    suspend fun deleteProgress() {
        dao.clearGameProgress()
    }
}

