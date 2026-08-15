package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

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
    val equippedPetJson: String = "",
    val petLevel: Int = 1,
    val petExp: Int = 0,
    val petSatiety: Int = 100,
    val petEquippedWeaponJson: String = "",
    val petEquippedArmorJson: String = "",
    val petEquippedAccessoryJson: String = "",
    val inventoryJson: String = "[]",
    val talentsJson: String = "[]",
    val skillsJson: String = "[]",
    val completedQuestsJson: String = "[]",
    val mapPointsExploredJson: String = "[]",
    val mapPointsClearedJson: String = "[]",
    val highestUnlockedDungeon: Int = 10,
    val completedDungeonsJson: String = "[]",
    val dungeonCheckpointsJson: String = "{}",
    val currentX: Int = 0,
    val currentY: Int = 0,
    val hasAdvancedClass: Boolean = false,
    val advancedClassName: String = "",
    val activeQuestsJson: String = "[]",
    val petRosterJson: String = "[]",
    val activePetId: String = "",
    val bestiaryJson: String = "{}",
    val materialsJson: String = "{}",
    val expeditionJson: String = "",
    val minigameStatsJson: String = "{}",
    val settingsJson: String = "",
    val contractsJson: String = "[]",
    val torchStock: Int = 3,
    val totalKills: Int = 0,
    val bossKills: Int = 0,
    val dungeonsCleared: Int = 0,
    val deepestDepth: Int = 0
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

@Database(entities = [GameProgress::class], version = 9, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameProgressDao(): GameProgressDao

    companion object {
        val MIGRATION_7_8 = object : androidx.room.migration.Migration(7, 8) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE game_progress ADD COLUMN activeQuestsJson TEXT NOT NULL DEFAULT '[]'")
            }
        }

        val MIGRATION_8_9 = object : androidx.room.migration.Migration(8, 9) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE game_progress ADD COLUMN petRosterJson TEXT NOT NULL DEFAULT '[]'")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN activePetId TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN bestiaryJson TEXT NOT NULL DEFAULT '{}'")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN materialsJson TEXT NOT NULL DEFAULT '{}'")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN expeditionJson TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN minigameStatsJson TEXT NOT NULL DEFAULT '{}'")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN settingsJson TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN contractsJson TEXT NOT NULL DEFAULT '[]'")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN torchStock INTEGER NOT NULL DEFAULT 3")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN totalKills INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN bossKills INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN dungeonsCleared INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE game_progress ADD COLUMN deepestDepth INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}

class GameProgressRepository(
    private val dao: GameProgressDao,
    private val context: Context? = null
) {
    val progressFlow: Flow<GameProgress?> = dao.getActiveGameProgress()
    val allCharactersFlow: Flow<List<GameProgress>> = dao.getAllGameProgress()

    /**
     * Lectura PURA de la fila activa: no restaura la copia de seguridad ni escribe
     * nada. Quien necesite saber "qué queda" tras un borrado debe usar esta y no
     * [getProgress], que resucita al personaje desde el backup automático.
     */
    suspend fun getActiveProgressSync(): GameProgress? = try {
        dao.getActiveGameProgressSync()
    } catch (e: Exception) {
        null
    }

    suspend fun getProgress(): GameProgress? {
        val current = try {
            dao.getActiveGameProgressSync()
        } catch (e: Exception) {
            null
        }

        if ((current == null || !current.hasActiveChar) && context != null) {
            val backup = GameBackupManager.loadBackup(context)
            if (backup != null && backup.hasActiveChar) {
                try {
                    dao.deactivateAllCharacters()
                    val newId = dao.saveGameProgress(backup.copy(isActiveChar = true))
                    return dao.getGameProgressById(newId.toInt()) ?: backup
                } catch (e: Exception) {
                    return backup
                }
            }
        }
        return current
    }

    suspend fun saveProgress(progress: GameProgress): Long {
        val id = try {
            dao.saveGameProgress(progress)
        } catch (e: Exception) {
            -1L
        }
        context?.let { ctx ->
            if (progress.hasActiveChar) {
                // La copia escribe dos ficheros completos: nunca en el hilo principal.
                withContext(Dispatchers.IO) { GameBackupManager.saveBackup(ctx, progress) }
            }
        }
        return id
    }

    suspend fun exportManualBackup(): Boolean {
        if (context == null) return false
        val current = getProgress() ?: return false
        return GameBackupManager.saveBackup(context, current)
    }

    suspend fun restoreManualBackup(): GameProgress? {
        if (context == null) return null
        val backup = GameBackupManager.loadBackup(context) ?: return null
        try {
            dao.deactivateAllCharacters()
            val newId = dao.saveGameProgress(backup.copy(id = 0, isActiveChar = true))
            return dao.getGameProgressById(newId.toInt()) ?: backup
        } catch (e: Exception) {
            return backup
        }
    }

    suspend fun getBackupStatusText(): String {
        val ctx = context ?: return "Copia de seguridad deshabilitada"
        return withContext(Dispatchers.IO) { GameBackupManager.getBackupInfo(ctx) }
    }

    suspend fun deactivateAll() {
        try { dao.deactivateAllCharacters() } catch (_: Exception) {}
    }

    suspend fun setActive(id: Int) {
        try {
            dao.deactivateAllCharacters()
            dao.setActiveCharacter(id)
        } catch (_: Exception) {}
    }

    suspend fun deleteCharacter(id: Int) {
        try { dao.deleteCharacterById(id) } catch (_: Exception) {}
        // El backup automático conserva la fila borrada con su id original: si no se
        // invalida, la siguiente lectura de `getProgress()` la reinsertaría entera.
        context?.let { ctx ->
            withContext(Dispatchers.IO) {
                val backup = GameBackupManager.loadBackup(ctx)
                if (backup != null && backup.id == id) GameBackupManager.clearBackup(ctx)
            }
        }
    }

    suspend fun deleteProgress() {
        try { dao.clearGameProgress() } catch (_: Exception) {}
        context?.let { ctx ->
            withContext(Dispatchers.IO) { GameBackupManager.clearBackup(ctx) }
        }
    }
}

