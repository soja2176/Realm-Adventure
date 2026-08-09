package com.example.scene3d

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class AssetManager3D(private val context: Context) {

    companion object {
        private const val TAG = "AssetManager3D"
        private const val GITHUB_RAW = "https://raw.githubusercontent.com/Kenney-nl/kenney-prototype-textures/main/Models/"

        val MODELS = mapOf(
            "player_warrior" to "${GITHUB_RAW}character.glb",
            "player_mage" to "${GITHUB_RAW}character.glb",
            "player_rogue" to "${GITHUB_RAW}character.glb",
            "player_cleric" to "${GITHUB_RAW}character.glb",
            "enemy_goblin" to "${GITHUB_RAW}enemy.glb",
            "enemy_orc" to "${GITHUB_RAW}enemy.glb",
            "boss_dragon" to "${GITHUB_RAW}boss.glb",
            "tree_oak" to "https://raw.githubusercontent.com/Kenney-nl/kenney-nl-assets/main/3D%20Models/nature/tree.glb",
            "rock_small" to "https://raw.githubusercontent.com/Kenney-nl/kenney-nl-assets/main/3D%20Models/nature/rock.glb",
            "chest_gold" to "https://raw.githubusercontent.com/Kenney-nl/kenney-nl-assets/main/3D%20Models/props/chest.glb"
        )
    }

    private val cacheDir = File(context.cacheDir, "models_3d").apply { mkdirs() }
    private val loadedModels = mutableMapOf<String, File>()

    suspend fun getModel(modelKey: String): File? {
        loadedModels[modelKey]?.let { return it }
        val url = MODELS[modelKey] ?: return null
        val file = getOrDownloadFile(url, modelKey)
        return file.takeIf { it.exists() && it.length() > 0L }?.also {
            loadedModels[modelKey] = it
        }
    }

    private suspend fun getOrDownloadFile(url: String, fileName: String): File {
        val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_") + ".glb"
        val file = File(cacheDir, sanitizedFileName)
        if (file.exists() && file.length() > 0L) return file

        return try {
            withContext(Dispatchers.IO) {
                URL(url).openStream().use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                Log.d(TAG, "Downloaded: $fileName (${file.length() / 1024}KB)")
                file
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Download failed: $fileName", exception)
            file
        }
    }

    fun preloadCriticalModels(scope: CoroutineScope) {
        scope.launch {
            listOf("player_warrior", "enemy_goblin", "tree_oak", "rock_small").forEach { key ->
                launch { getModel(key) }
            }
        }
    }

    fun clearCache() {
        loadedModels.clear()
        cacheDir.deleteRecursively()
        cacheDir.mkdirs()
    }

    fun getCacheStats(): Map<String, Any> {
        val files = cacheDir.listFiles()?.filter { it.isFile && it.extension == "glb" } ?: emptyList()
        val totalSize = files.sumOf { it.length() }
        return mapOf(
            "count" to files.size,
            "sizeBytes" to totalSize,
            "sizeMB" to "%.2f".format(totalSize / 1024.0 / 1024.0)
        )
    }
}
