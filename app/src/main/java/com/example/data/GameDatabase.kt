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

@Entity(tableName = "game_progress")
data class GameProgress(
    @PrimaryKey val id: Int = 1,
    val hasActiveChar: Boolean = false,
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
    val equippedWeaponJson: String = "",
    val equippedArmorJson: String = "",
    val equippedRingJson: String = "",
    val equippedShieldJson: String = "",
    val inventoryJson: String = "[]",
    val talentsJson: String = "[]",
    val skillsJson: String = "[]",
    val completedQuestsJson: String = "[]",
    val mapPointsExploredJson: String = "[]",
    val currentX: Int = 0,
    val currentY: Int = 0
)

@Dao
interface GameProgressDao {
    @Query("SELECT * FROM game_progress WHERE id = 1 LIMIT 1")
    fun getGameProgress(): Flow<GameProgress?>

    @Query("SELECT * FROM game_progress WHERE id = 1 LIMIT 1")
    suspend fun getGameProgressSync(): GameProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameProgress(progress: GameProgress)

    @Query("DELETE FROM game_progress WHERE id = 1")
    suspend fun clearGameProgress()
}

@Database(entities = [GameProgress::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameProgressDao(): GameProgressDao
}

class GameProgressRepository(private val dao: GameProgressDao) {
    val progressFlow: Flow<GameProgress?> = dao.getGameProgress()

    suspend fun getProgress(): GameProgress? {
        return dao.getGameProgressSync()
    }

    suspend fun saveProgress(progress: GameProgress) {
        dao.saveGameProgress(progress)
    }

    suspend fun deleteProgress() {
        dao.clearGameProgress()
    }
}
