package com.example.scene3d

import android.content.Context
import android.graphics.Color as AndroidColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.github.sceneview.Scene
import io.github.sceneview.math.*
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import kotlinx.coroutines.launch

/**
 * Componente SceneView para renderizado 3D isométrico en Eldoria RPG
 * Vista isométrica clásica (ángulo 35.264°, rotación 45°)
 */
@Composable
fun IsometricSceneView(
    modifier: Modifier = Modifier,
    playerModel: Player3DModel? = null,
    enemies: List<Enemy3DModel> = emptyList(),
    terrain: List<TerrainNode> = emptyList(),
    decorations: List<DecorationNode> = emptyList(),
    onPlayerMoved: ((Float, Float) -> Unit)? = null,
    context: Context
) {
    val scene = rememberScene()
    val camera = rememberCamera()
    var assetManager by remember { mutableStateOf<AssetManager3D?>(null) }
    
    // Inicializar AssetManager
    DisposableEffect(context) {
        assetManager = AssetManager3D(context)
        assetManager?.preloadCriticalModels(kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main))
        onDispose { }
    }
    
    // Configurar cámara isométrica perfecta
    DisposableEffect(Unit) {
        // Ángulo isométrico perfecto: 35.264° vertical, 45° horizontal
        val isoAngle = 35.264f
        val distance = 25f
        
        camera.position = Position(
            x = kotlin.math.cos(Math.toRadians(45.0)) * distance,
            y = kotlin.math.sin(Math.toRadians(isoAngle.toDouble())).toFloat() * distance,
            z = kotlin.math.sin(Math.toRadians(45.0)) * distance
        )
        camera.lookAt(Position(0f, 0f, 0f))
        camera.fieldOfView = 50f
        camera.nearPlane = 0.1f
        camera.farPlane = 500f
        
        onDispose { }
    }
    
    // Configurar iluminación y ambiente
    DisposableEffect(Unit) {
        scene.environment?.let { env ->
            env.intensity = 80000f
            env.color = AndroidColor.parseColor("#FFF5E6")
        }
        scene.skybox?.let { sky ->
            sky.color = AndroidColor.parseColor("#87CEEB")
        }
        onDispose { }
    }
    
    LaunchedEffect(playerModel, enemies, terrain, decorations) {
        val root = scene.rootNode
        root.removeAllChildren()
        
        // Agregar terreno
        terrain.forEach { t ->
            val node = createTerrainNode(t.biome, t.position, t.scale)
            root.addChild(node)
        }
        
        // Agregar decoración
        decorations.forEach { d ->
            assetManager?.getModel(d.modelType)?.let { model ->
                val clone = model.clone() as Node
                clone.position = d.position
                clone.rotation = d.rotation
                clone.scale = d.scale
                root.addChild(clone)
            }
        }
        
        // Agregar jugador
        playerModel?.let { p ->
            assetManager?.getModel("player_warrior")?.let { model ->
                val playerNode = model.clone() as Node
                playerNode.position = p.position
                playerNode.rotation = p.rotation
                playerNode.scale = p.scale
                root.addChild(playerNode)
            }
        }
        
        // Agregar enemigos
        enemies.forEach { e ->
            assetManager?.getModel(e.modelType)?.let { model ->
                val enemyNode = model.clone() as Node
                enemyNode.position = e.position
                enemyNode.rotation = e.rotation
                enemyNode.scale = e.scale
                root.addChild(enemyNode)
            }
        }
    }
    
    androidx.compose.ui.viewinterop.AndroidView(
        factory = { ctx ->
            Scene(ctx).apply {
                this.scene = scene
                this.camera = camera
            }
        },
        modifier = modifier,
        update = { view -> }
    )
}

private fun createTerrainNode(biome: String, position: Position, scale: Scale): Node {
    val node = Node()
    node.position = position
    node.scale = scale
    
    val color = when (biome.uppercase()) {
        "FOREST", "BOSQUE" -> 0xFF228B22.toInt()
        "DESERT", "DESIERTO" -> 0xFFEDC9AF.toInt()
        "MOUNTAIN", "MONTAÑA" -> 0xFF808080.toInt()
        "WATER", "AGUA" -> 0xFF4169E1.toInt()
        "SNOW", "NIEVE" -> 0xFFFFFAFA.toInt()
        "VOLCANIC", "VOLCÁNICO" -> 0xFF8B0000.toInt()
        "SWAMP", "PANTANO" -> 0xFF2F4F4F.toInt()
        else -> 0xFF228B22.toInt()
    }
    
    node.setTag("color", color)
    node.setTag("primitive", "plane")
    return node
}

// Modelos de datos
data class Player3DModel(
    val position: Position = Position(0f, 0f, 0f),
    val rotation: Rotation = Rotation(0f, 45f, 0f),
    val scale: Scale = Scale(1f, 1f, 1f),
    val characterClass: String = "Guerrero"
)

data class Enemy3DModel(
    val name: String,
    val position: Position = Position(5f, 0f, 0f),
    val rotation: Rotation = Rotation(0f, 180f, 0f),
    val scale: Scale = Scale(1f, 1f, 1f),
    val modelType: String = "enemy_goblin",
    val isBoss: Boolean = false
)

data class TerrainNode(
    val biome: String,
    val position: Position = Position(0f, 0f, 0f),
    val scale: Scale = Scale(2f, 0.5f, 2f)
)

data class DecorationNode(
    val modelType: String,
    val position: Position = Position(0f, 0f, 0f),
    val rotation: Rotation = Rotation(0f, 0f, 0f),
    val scale: Scale = Scale(1f, 1f, 1f)
)

// Utilidades de conversión isométrica
object IsometricConverter {
    private const val TILE_SIZE = 2.0f
    
    fun mapToPosition(mapX: Int, mapY: Int): Position {
        val x = (mapX - mapY) * TILE_SIZE / 2
        val z = (mapX + mapY) * TILE_SIZE / 2
        return Position(x, 0f, z)
    }
    
    fun positionToMap(position: Position): Pair<Int, Int> {
        val mapX = ((position.x / TILE_SIZE) + (position.z / TILE_SIZE)).toInt()
        val mapY = ((position.z / TILE_SIZE) - (position.x / TILE_SIZE)).toInt()
        return Pair(mapX, mapY)
    }
    
    fun screenToIso(screenX: Float, screenY: Float): Pair<Float, Float> {
        val isoX = (screenX / TILE_SIZE) - (screenY / TILE_SIZE)
        val isoY = (screenY / TILE_SIZE) + (screenX / TILE_SIZE)
        return Pair(isoX, isoY)
    }
}
