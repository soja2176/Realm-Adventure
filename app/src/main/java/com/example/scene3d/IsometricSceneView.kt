package com.example.scene3d

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

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
    Canvas(modifier = modifier) {
        drawRect(Color(0xFF87CEEB))
        terrain.forEach { tile -> drawTerrain(tile) }
        decorations.forEach { decoration ->
            drawCircle(
                color = decorationColor(decoration.modelType),
                radius = 8f * decoration.scale.x,
                center = isometricOffset(decoration.position)
            )
        }
        playerModel?.let { player ->
            drawCircle(Color(0xFFFFC107), radius = 14f * player.scale.x, center = isometricOffset(player.position))
        }
        enemies.forEach { enemy ->
            drawCircle(
                color = if (enemy.isBoss) Color(0xFF7B1FA2) else Color(0xFFE53935),
                radius = if (enemy.isBoss) 18f else 12f,
                center = isometricOffset(enemy.position)
            )
        }
    }
}

private fun DrawScope.drawTerrain(tile: TerrainNode) {
    val center = isometricOffset(tile.position)
    val halfWidth = 20f * tile.scale.x
    val halfHeight = 10f * tile.scale.z
    val diamond = Path().apply {
        moveTo(center.x, center.y - halfHeight)
        lineTo(center.x + halfWidth, center.y)
        lineTo(center.x, center.y + halfHeight)
        lineTo(center.x - halfWidth, center.y)
        close()
    }
    drawPath(diamond, terrainColor(tile.biome))
}

private fun DrawScope.isometricOffset(position: Position): Offset = Offset(
    x = size.width / 2f + (position.x - position.z) * 18f,
    y = size.height * 0.55f + (position.x + position.z) * 9f - position.y * 32f
)

private fun terrainColor(biome: String): Color = when (biome.uppercase()) {
    "FOREST", "BOSQUE" -> Color(0xFF2E7D32)
    "MOUNTAIN", "MONTAÑA" -> Color(0xFF90A4AE)
    "WATER", "AGUA" -> Color(0xFF1976D2)
    "SNOW", "NIEVE" -> Color(0xFFECEFF1)
    "DESERT", "DESIERTO" -> Color(0xFFD4A373)
    else -> Color(0xFF66BB6A)
}

private fun decorationColor(modelType: String): Color = when {
    modelType.contains("tree") -> Color(0xFF1B5E20)
    modelType.contains("rock") -> Color(0xFF616161)
    modelType.contains("chest") -> Color(0xFFFFC107)
    else -> Color(0xFF795548)
}

data class Position(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f)

data class Rotation(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f)

data class Scale(val x: Float = 1f, val y: Float = 1f, val z: Float = 1f)

data class Player3DModel(
    val position: Position = Position(),
    val rotation: Rotation = Rotation(0f, 45f, 0f),
    val scale: Scale = Scale(),
    val characterClass: String = "Guerrero"
)

data class Enemy3DModel(
    val name: String,
    val position: Position = Position(5f, 0f, 0f),
    val rotation: Rotation = Rotation(0f, 180f, 0f),
    val scale: Scale = Scale(),
    val modelType: String = "enemy_goblin",
    val isBoss: Boolean = false
)

data class TerrainNode(
    val biome: String,
    val position: Position = Position(),
    val scale: Scale = Scale(2f, 0.5f, 2f)
)

data class DecorationNode(
    val modelType: String,
    val position: Position = Position(),
    val rotation: Rotation = Rotation(),
    val scale: Scale = Scale()
)

object IsometricConverter {
    private const val TILE_SIZE = 2.0f

    fun mapToPosition(mapX: Int, mapY: Int): Position {
        val x = (mapX - mapY) * TILE_SIZE / 2f
        val z = (mapX + mapY) * TILE_SIZE / 2f
        return Position(x, 0f, z)
    }

    fun positionToMap(position: Position): Pair<Int, Int> {
        val mapX = ((position.x / TILE_SIZE) + (position.z / TILE_SIZE)).toInt()
        val mapY = ((position.z / TILE_SIZE) - (position.x / TILE_SIZE)).toInt()
        return mapX to mapY
    }

    fun screenToIso(screenX: Float, screenY: Float): Pair<Float, Float> =
        (screenX / TILE_SIZE - screenY / TILE_SIZE) to (screenY / TILE_SIZE + screenX / TILE_SIZE)
}
