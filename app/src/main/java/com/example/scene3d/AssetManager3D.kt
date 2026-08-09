package com.example.scene3d

import android.content.Context
import android.util.Log
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import kotlinx.coroutines.*
import java.io.File
import java.net.URL

/**
 * Gestor de Assets 3D para Eldoria RPG
 * Descarga y cachea modelos GLB desde fuentes open source (Kenney.nl - CC0)
 * Genera modelos procedurales cuando no hay assets disponibles
 */
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
    private val loadedModels = mutableMapOf<String, ModelNode>()
    private val downloadQueue = mutableListOf<String>()
    
    suspend fun getModel(modelKey: String): ModelNode? {
        loadedModels[modelKey]?.let { return it }
        
        val url = MODELS[modelKey]
        if (url.isNullOrEmpty()) return generateProceduralModel(modelKey)
        
        val file = getOrDownloadFile(url, modelKey)
        if (!file.exists() || file.length() == 0L) return generateProceduralModel(modelKey)
        
        return try {
            withContext(Dispatchers.IO) {
                val scene = Scene(context)
                scene.loadModel(file.absolutePath)
                val modelNode = scene.scene.rootNode.children.firstOrNull() as? ModelNode
                    ?: return@withContext generateProceduralModel(modelKey)
                modelNode.name = modelKey
                loadedModels[modelKey] = modelNode
                modelNode
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading $modelKey: ${e.message}")
            generateProceduralModel(modelKey)
        }
    }
    
    private suspend fun getOrDownloadFile(url: String, fileName: String): File {
        val sanitizedFileName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_") + ".glb"
        val file = File(cacheDir, sanitizedFileName)
        
        if (file.exists() && file.length() > 0) return file
        
        synchronized(downloadQueue) {
            if (downloadQueue.contains(url)) {
                while (downloadQueue.contains(url)) Thread.sleep(100)
                return file
            }
            downloadQueue.add(url)
        }
        
        return try {
            withContext(Dispatchers.IO) {
                URL(url).openStream().use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                Log.d(TAG, "Downloaded: $fileName (${file.length() / 1024}KB)")
                file
            }
        } catch (e: Exception) {
            Log.e(TAG, "Download failed: $fileName")
            file.createNewFile()
            file
        } finally {
            synchronized(downloadQueue) { downloadQueue.remove(url) }
        }
    }
    
    private fun generateProceduralModel(modelKey: String): ModelNode {
        val node = ModelNode()
        node.name = modelKey
        
        when {
            modelKey.contains("player") -> {
                val color = getClassColor(modelKey)
                node.addChild(createPrimitive("capsule", color, 0.5f, 1.5f, 0.5f))
                val head = createPrimitive("sphere", color, 0.35f, 0.35f, 0.35f)
                head.position = Position(0f, 1.6f, 0f)
                node.addChild(head)
            }
            modelKey.contains("enemy") -> {
                node.addChild(createPrimitive("box", 0xFF8B0000.toInt(), 0.7f, 1.3f, 0.7f))
            }
            modelKey.contains("boss") -> {
                node.addChild(createPrimitive("box", 0xFF4A0000.toInt(), 1.5f, 2.5f, 1.5f))
                val wing = createPrimitive("box", 0xFF8B0000.toInt(), 0.3f, 1.5f, 2.0f)
                wing.position = Position(0.8f, 1.5f, 0f)
                wing.rotation = Rotation(0f, 0f, -30f)
                node.addChild(wing)
            }
            modelKey.contains("tree") -> {
                node.addChild(createPrimitive("cylinder", 0xFF8B4513.toInt(), 0.25f, 1.5f, 0.25f))
                val foliage = createPrimitive("cone", 0xFF228B22.toInt(), 1.0f, 2.0f, 1.0f)
                foliage.position = Position(0f, 1.5f, 0f)
                node.addChild(foliage)
            }
            modelKey.contains("rock") -> {
                val scale = if (modelKey.contains("large")) 1.5f else 0.7f
                node.addChild(createPrimitive("sphere", 0xFF808080.toInt(), scale, scale, scale))
            }
            modelKey.contains("chest") -> {
                val color = if (modelKey.contains("gold")) 0xFFFFD700.toInt() else 0xFFC0C0C0.toInt()
                node.addChild(createPrimitive("box", color, 0.6f, 0.4f, 0.4f))
            }
            else -> node.addChild(createPrimitive("box", 0xFF808080.toInt(), 0.5f, 0.5f, 0.5f))
        }
        
        loadedModels[modelKey] = node
        return node
    }
    
    private fun createPrimitive(type: String, color: Int, x: Float, y: Float, z: Float): Node {
        val node = Node()
        node.scale = Scale(x, y, z)
        node.setTag("color", color)
        node.setTag("primitive", type)
        return node
    }
    
    private fun getClassColor(modelKey: String): Int = when {
        modelKey.contains("warrior") -> 0xFF8B0000.toInt()
        modelKey.contains("mage") -> 0xFF00008B.toInt()
        modelKey.contains("rogue") -> 0xFF006400.toInt()
        modelKey.contains("cleric") -> 0xFFFFD700.toInt()
        else -> 0xFF808080.toInt()
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
        return mapOf("count" to files.size, "sizeBytes" to totalSize, "sizeMB" to "%.2f".format(totalSize / 1024.0 / 1024.0))
    }
}
