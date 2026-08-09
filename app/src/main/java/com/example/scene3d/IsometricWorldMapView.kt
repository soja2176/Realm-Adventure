package com.example.scene3d

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.GameViewModel

/**
 * Vista 3D Isométrica del Mapa Mundial para Eldoria RPG
 * Reemplaza la vista 2D con SceneView/Filament manteniendo toda la lógica existente
 */
@Composable
fun IsometricWorldMapView(
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier,
    onPlayerMoved: (Int, Int) -> Unit = { _, _ -> }
) {
    val progressState by gameViewModel.progressState.collectAsState()
    val mapTiles by gameViewModel.proceduralMap.collectAsState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB))
    ) {
        progressState?.let { progress ->
            // Generar decoración procedural basada en el bioma
            val decorations = mapTiles.flatMap { tile ->
                when (tile.biome.uppercase()) {
                    "FOREST", "BOSQUE" -> listOf(
                        DecorationNode("tree_oak", IsometricConverter.mapToPosition(tile.x + 1, tile.y), Rotation(0f, kotlin.random.Random.nextFloat() * 360f, 0f)),
                        DecorationNode("tree_oak", IsometricConverter.mapToPosition(tile.x - 1, tile.y + 1), Rotation(0f, kotlin.random.Random.nextFloat() * 360f, 0f))
                    )
                    "MOUNTAIN", "MONTAÑA" -> listOf(
                        DecorationNode("rock_small", IsometricConverter.mapToPosition(tile.x + 1, tile.y))
                    )
                    else -> emptyList()
                }
            }
            
            IsometricSceneView(
                modifier = Modifier.fillMaxSize(),
                playerModel = Player3DModel(
                    position = IsometricConverter.mapToPosition(progress.currentX, progress.currentY),
                    rotation = Rotation(0f, 45f, 0f),
                    characterClass = progress.charClass
                ),
                terrain = mapTiles.map { tile ->
                    TerrainNode(
                        biome = tile.biome,
                        position = IsometricConverter.mapToPosition(tile.x, tile.y)
                    )
                },
                decorations = decorations,
                context = androidx.compose.ui.platform.LocalContext.current
            )
        }
        
        // UI Overlay - Stats del Jugador
        progressState?.let { progress ->
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        MaterialTheme.shapes.medium
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "${progress.charName} • Nvl ${progress.charLevel}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // Barra de HP
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("❤️ ", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    val hpPercent = progress.currentHp.toFloat() / progress.maxHp.toFloat()
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(8.dp)
                            .background(Color.DarkGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .width((80.dp * hpPercent).coerceIn(0.dp, 80.dp))
                                .height(8.dp)
                                .background(Color(0xFFFF5252))
                        )
                    }
                    Text(" ${progress.currentHp}/${progress.maxHp}", 
                         style = MaterialTheme.typography.bodySmall,
                         color = Color(0xFFFF5252))
                }
                
                // Barra de MP
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💙 ", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    val mpPercent = progress.currentMp.toFloat() / progress.maxMp.toFloat()
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(8.dp)
                            .background(Color.DarkGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .width((80.dp * mpPercent).coerceIn(0.dp, 80.dp))
                                .height(8.dp)
                                .background(Color(0xFF448AFF))
                        )
                    }
                    Text(" ${progress.currentMp}/${progress.maxMp}",
                         style = MaterialTheme.typography.bodySmall,
                         color = Color(0xFF448AFF))
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📍 (${progress.currentX}, ${progress.currentY})",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * Vista 3D para Combate
 */
@Composable
fun IsometricCombatView(
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val combatState by gameViewModel.combatState.collectAsState()
    val progressState by gameViewModel.progressState.collectAsState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C))
    ) {
        combatState.enemy?.let { enemy ->
            val enemyModel = Enemy3DModel(
                name = enemy.name,
                position = Position(5f, 0f, 0f),
                rotation = Rotation(0f, 180f, 0f),
                modelType = if (enemy.isBoss) "boss_dragon" else "enemy_goblin",
                isBoss = enemy.isBoss
            )
            
            IsometricSceneView(
                modifier = Modifier.fillMaxSize(),
                playerModel = progressState?.let { p ->
                    Player3DModel(
                        position = Position(-5f, 0f, 0f),
                        rotation = Rotation(0f, 0f, 0f),
                        characterClass = p.charClass
                    )
                },
                enemies = listOf(enemyModel),
                context = androidx.compose.ui.platform.LocalContext.current
            )
        }
        
        // Logs de combate
        LazyColumn(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(8.dp)
        ) {
            items(combatState.combatLogs.takeLast(5).size) { index ->
                Text(
                    text = combatState.combatLogs.takeLast(5)[index],
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

// Extensión para convertir MapTile a TerrainNode
fun List<com.example.data.MapTile>.toTerrainNodes(): List<TerrainNode> {
    return this.map { tile ->
        TerrainNode(
            biome = tile.biome,
            position = IsometricConverter.mapToPosition(tile.x, tile.y)
        )
    }
}
