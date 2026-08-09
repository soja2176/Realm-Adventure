package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GameViewModel
import com.example.scene3d.IsometricWorldMapView
import com.example.scene3d.IsometricCombatView

// Colores de la paleta medieval
val MedievalDarkBg = Color(0xFF0F111A)
val MedievalCardBg = Color(0xFF161A26)
val MedievalGold = Color(0xFFFFC107)
val MedievalGoldDark = Color(0xFFC79100)
val MedievalCrimson = Color(0xFFE53935)

/**
 * Pantalla del Mapa Mundial en 3D Isométrico
 * Reemplaza WorldMapScreen() para usar SceneView/Filament
 */
@Composable
fun WorldMapScreen3D(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val progress by viewModel.progressState.collectAsState()
    val notification by viewModel.notification.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MedievalDarkBg)
    ) {
        // Vista 3D del mapa
        IsometricWorldMapView(
            gameViewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
        
        // Botón de regreso
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .offset(x = 120.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Regresar",
                tint = Color.White
            )
        }
        
        // Notificación toast
        notification?.let { msg ->
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MedievalGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = msg,
                    color = MedievalDarkBg,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2500)
                viewModel.dismissNotification()
            }
        }
        
        // Controles inferiores (auto-navegación, etc.)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.toggleAutoNavigation() },
                colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🗺️ Auto Navegar", color = MedievalDarkBg)
            }
            
            Button(
                onClick = { /* Inventario */ },
                colors = ButtonDefaults.buttonColors(containerColor = MedievalCardBg),
                border = BorderStroke(1.dp, MedievalGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🎒 Inventario", color = MedievalGold)
            }
        }
    }
}

/**
 * Pantalla de Combate en 3D Isométrico
 * Reemplaza CombatScreen() para usar SceneView/Filament
 */
@Composable
fun CombatScreen3D(
    viewModel: GameViewModel,
    onCombatEnd: () -> Unit = {}
) {
    val combatState by viewModel.combatState.collectAsState()
    val progress by viewModel.progressState.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MedievalDarkBg)
    ) {
        // Vista 3D del combate
        IsometricCombatView(
            gameViewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
        
        // UI de controles de combate
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Barras de vida
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Vida del jugador
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = progress?.charName ?: "Jugador",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = (progress?.currentHp ?: 100).toFloat() / (progress?.maxHp ?: 100),
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFFFF5252),
                        trackColor = Color.DarkGray
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Vida del enemigo
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = combatState.enemy?.name ?: "Enemigo",
                        color = Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = (combatState.enemy?.currentHp ?: 100).toFloat() / (combatState.enemy?.maxHp ?: 100),
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFFFF5252),
                        trackColor = Color.DarkGray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { /* Atacar */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalCrimson),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Text("⚔️ Atacar", color = Color.White)
                }
                
                Button(
                    onClick = { /* Habilidad */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Text("✨ Magia", color = MedievalDarkBg)
                }
                
                Button(
                    onClick = { /* Poción */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Text("🧪 Poción", color = Color.White)
                }
            }
            
            // Botón de huir
            Button(
                onClick = onCombatEnd,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("🏃 Huir", color = Color.White)
            }
        }
        
        // Dialogo de victoria/derrota
        combatState.victory?.let { won ->
            Dialog(onDismissRequest = {}) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (won) MedievalGold else MedievalCrimson
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (won) "¡VICTORIA!" else "DERROTA",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = if (won) MedievalDarkBg else Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (won) {
                            Text(
                                text = "+${combatState.expGained} XP\n+${combatState.goldGained} Oro",
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MedievalDarkBg
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = onCombatEnd,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (won) MedievalDarkBg else MedievalGold
                            )
                        ) {
                            Text("Continuar", color = if (won) MedievalGold else MedievalDarkBg)
                        }
                    }
                }
            }
        }
    }
}
