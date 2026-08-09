package com.example.eldoria.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.eldoria.core.content.Quest
import com.example.eldoria.core.content.RandomEvent
import com.example.eldoria.systems.DialogueSystem
import com.example.eldoria.systems.EventResult
import com.example.eldoria.systems.QuestUpdate

/**
 * COMPONENTES UI MEDIEVALES DE FANTASÍA
 * Diseño consistente con temática oscura, paneles de madera y colores épicos.
 */

// Paleta de colores medieval
object MedievalColors {
    val DarkWood = Color(0xFF3E2723)
    val LightWood = Color(0xFF5D4037)
    val GoldAccent = Color(0xFFFFD700)
    val BloodRed = Color(0xFF8B0000)
    val MagicPurple = Color(0xFF4A148C)
    val ForestGreen = Color(0xFF1B5E20)
    val IceBlue = Color(0xFFB3E5FC)
    val StoneGray = Color(0xFF424242)
    val Parchment = Color(0xFFF5F5DC)
    val TextDark = Color(0xFF212121)
    val TextLight = Color(0xFFFAFAFA)
}

/**
 * Panel decorativo con borde estilo medieval
 */
@Composable
fun MedievalPanel(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MedievalColors.DarkWood,
    borderColor: Color = MedievalColors.GoldAccent,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(backgroundColor, backgroundColor.copy(alpha = 0.8f))
                )
            )
            .padding(16.dp),
        content = content
    )
}

/**
 * Tarjeta de misión con progreso animado
 */
@Composable
fun QuestCard(
    quest: Quest,
    progress: Pair<Int, Int>,
    onProgressUpdate: (QuestUpdate) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val progressPercent = if (progress.second > 0) 
        (progress.first.toFloat() / progress.second * 100).coerceIn(0f, 100f) 
    else 0f
    
    AnimatedVisibility(
        visible = true,
        enter = slideInHorizontally(initialOffsetX = { -100 }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { 100 }) + fadeOut()
    ) {
        MedievalPanel(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quest.title,
                        color = MedievalColors.GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = quest.description,
                        color = MedievalColors.Parchment,
                        fontSize = 12.sp,
                        maxLines = if (expanded) Int.MAX_VALUE else 2
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Barra de progreso
                    LinearProgressIndicator(
                        progress = progressPercent / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MedievalColors.GoldAccent,
                        trackColor = MedievalColors.StoneGray
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${progress.first}/${progress.second} - ${quest.targetEntity ?: "Explorar"}",
                        color = MedievalColors.Parchment.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
                
                if (progress.first >= progress.second && !quest.isCompleted) {
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedievalColors.ForestGreen
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Completar", fontSize = 12.sp)
                    }
                }
            }
            
            // Recompensas
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "XP: +${quest.rewardXp}",
                    color = MedievalColors.IceBlue,
                    fontSize = 11.sp
                )
                Text(
                    text = "Oro: +${quest.rewardGold}",
                    color = MedievalColors.GoldAccent,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Diálogo de evento aleatorio con animación dramática
 */
@Composable
fun EventDialog(
    event: RandomEvent,
    result: EventResult?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rememberedResult by remember { mutableStateOf(result) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = event.title,
                color = MedievalColors.GoldAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                Text(
                    text = event.description,
                    color = MedievalColors.Parchment,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (rememberedResult != null) {
                    val resultText = when (val r = rememberedResult) {
                        is EventResult.Heal -> "✨ Sanaste $r HP"
                        is EventResult.Damage -> "💔 Recibiste $r de daño"
                        is EventResult.Buff -> "⚡ ${r.type} aumentado en ${r.value}"
                        is EventResult.Gold -> if (r.amount > 0) "💰 Encontraste ${r.amount} oro" else "💸 Perdiste ${-r.amount} oro"
                        is EventResult.Ambush -> "⚔️ ¡Emboscada! Prepararse para combatir"
                        is EventResult.Nothing -> "Nada sucede..."
                        null -> ""
                    }
                    
                    MedievalPanel(
                        backgroundColor = when (rememberedResult) {
                            is EventResult.Heal, is EventResult.Gold -> MedievalColors.ForestGreen
                            is EventResult.Damage, is EventResult.Ambush -> MedievalColors.BloodRed
                            else -> MedievalColors.StoneGray
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = resultText,
                            color = MedievalColors.Parchment,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedievalColors.GoldAccent
                )
            ) {
                Text("Continuar", color = MedievalColors.TextDark)
            }
        },
        containerColor = MedievalColors.DarkWood,
        tonalElevation = 8.dp
    )
}

/**
 * Sistema de diálogo con NPCs
 */
@Composable
fun DialogueBox(
    conversation: DialogueSystem.Conversation,
    currentOptions: List<DialogueSystem.DialogueOption>,
    onOptionSelected: (DialogueSystem.DialogueOption) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentLines by remember { mutableStateOf(conversation.openingLines) }
    var showOptions by remember { mutableStateOf(false) }
    
    MedievalPanel(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        backgroundColor = MedievalColors.StoneGray
    ) {
        LazyColumn {
            items(currentLines) { line ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "${line.speaker}: ",
                        color = MedievalColors.GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = line.text,
                        color = MedievalColors.Parchment,
                        fontSize = 14.sp
                    )
                }
                Divider(color = MedievalColors.GoldAccent.copy(alpha = 0.3f), thickness = 1.dp)
            }
            
            if (showOptions || currentLines == conversation.openingLines) {
                items(currentOptions) { option ->
                    Button(
                        onClick = {
                            currentLines = option.responseLines
                            showOptions = false
                            onOptionSelected(option)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedievalColors.MagicPurple
                        )
                    ) {
                        Text(
                            text = option.text,
                            color = MedievalColors.Parchment,
                            fontSize = 13.sp
                        )
                    }
                }

                item {
                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedievalColors.BloodRed
                        )
                    ) {
                        Text("Despedirse", color = MedievalColors.Parchment)
                    }
                }
            }
        }
        
        // Header del NPC
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${conversation.npcName} - ${conversation.npcTitle}",
                color = MedievalColors.GoldAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Notificación flotante de recompensa
 */
@Composable
fun RewardNotification(
    xp: Int,
    gold: Int,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -100 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -100 }) + fadeOut(),
        modifier = modifier
    ) {
        MedievalPanel(
            backgroundColor = MedievalColors.ForestGreen,
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (xp > 0) {
                    Text(
                        text = "✨ +$xp XP",
                        color = MedievalColors.IceBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                if (gold > 0) {
                    Text(
                        text = "💰 +$gold Oro",
                        color = MedievalColors.GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

/**
 * Barra de estado estilo RPG
 */
@Composable
fun RpgStatusBar(
    current: Int,
    max: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = MedievalColors.Parchment,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$current/$max",
                color = MedievalColors.Parchment,
                fontSize = 12.sp
            )
        }
        
        LinearProgressIndicator(
            progress = current.toFloat() / max,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = color,
            trackColor = MedievalColors.StoneGray
        )
    }
}

/**
 * Lista de misiones activas
 */
@Composable
fun QuestList(
    quests: List<Quest>,
    questProgressMap: Map<String, Pair<Int, Int>>,
    onQuestComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(quests, key = { it.id }) { quest ->
            val progress = questProgressMap[quest.id] ?: Pair(0, quest.targetCount)
            QuestCard(
                quest = quest,
                progress = progress,
                onProgressUpdate = {},
                onComplete = { onQuestComplete(quest.id) }
            )
        }
        
        if (quests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay misiones activas.\nVisita a los NPCs para obtener nuevas aventuras.",
                        color = MedievalColors.Parchment.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
