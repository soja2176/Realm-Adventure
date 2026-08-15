package com.example.data

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GameBackupManager {
    private const val TAG = "GameBackupManager"
    private const val BACKUP_FILENAME = "eldoria_backup.json"

    private fun getBackupFiles(context: Context): List<File> {
        val files = mutableListOf<File>()
        
        // Primary: External Files Dir (persistent across reinstalls if in app storage or backup)
        try {
            val externalDir = context.getExternalFilesDir("backups")
            if (externalDir != null) {
                if (!externalDir.exists()) externalDir.mkdirs()
                files.add(File(externalDir, BACKUP_FILENAME))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resolve external backup dir", e)
        }

        // Secondary: Internal Files Dir subfolder
        try {
            val internalDir = File(context.filesDir, "backups")
            if (!internalDir.exists()) internalDir.mkdirs()
            files.add(File(internalDir, BACKUP_FILENAME))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resolve internal backup dir", e)
        }

        return files
    }

    fun saveBackup(context: Context, progress: GameProgress): Boolean {
        if (!progress.hasActiveChar) return false
        val json = GameJsonParser.toJson(progress)
        if (json.isBlank()) return false

        var success = false
        val files = getBackupFiles(context)
        for (file in files) {
            try {
                file.parentFile?.mkdirs()
                file.writeText(json)
                success = true
                Log.d(TAG, "Backup saved successfully to ${file.absolutePath}")
            } catch (e: Exception) {
                Log.e(TAG, "Error writing backup to ${file.absolutePath}", e)
            }
        }
        return success
    }

    fun loadBackup(context: Context): GameProgress? {
        val files = getBackupFiles(context)
        for (file in files) {
            try {
                if (file.exists() && file.length() > 0) {
                    val json = file.readText()
                    val progress = GameJsonParser.fromJson<GameProgress>(json)
                    if (progress != null && progress.charName.isNotBlank()) {
                        Log.d(TAG, "Successfully loaded backup from ${file.absolutePath}")
                        return progress
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading backup from ${file.absolutePath}", e)
            }
        }
        return null
    }

    /**
     * Borra la copia automática. Necesario al eliminar un personaje: mientras el
     * fichero exista, [GameProgressRepository.getProgress] lo reinsertaría en la
     * base de datos y el borrado sería imposible.
     */
    fun clearBackup(context: Context): Boolean {
        var removed = false
        for (file in getBackupFiles(context)) {
            try {
                if (file.exists() && file.delete()) removed = true
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting backup at ${file.absolutePath}", e)
            }
        }
        return removed
    }

    fun getBackupInfo(context: Context): String {
        val files = getBackupFiles(context)
        for (file in files) {
            if (file.exists() && file.length() > 0) {
                val lastModified = file.lastModified()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                return "Copia disponible: ${dateFormat.format(Date(lastModified))} (${file.length() / 1024} KB)"
            }
        }
        return "No hay copia de seguridad creada aún"
    }
}
