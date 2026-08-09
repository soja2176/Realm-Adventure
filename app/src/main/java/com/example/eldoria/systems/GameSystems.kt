package com.example.eldoria.systems

import com.example.eldoria.core.content.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * SISTEMA DE MISIONES
 * Gestiona el ciclo de vida de misiones: generación, seguimiento, completado y recompensas.
 */

class QuestSystem {
    
    private val _activeQuests = MutableStateFlow<List<Quest>>(emptyList())
    val activeQuests: StateFlow<List<Quest>> = _activeQuests.asStateFlow()
    
    private val _completedQuests = MutableStateFlow<List<Quest>>(emptyList())
    val completedQuests: StateFlow<List<Quest>> = _completedQuests.asStateFlow()
    
    private val questProgress = mutableMapOf<String, Int>() // questId -> progreso actual
    
    /**
     * Acepta una nueva misión
     */
    fun acceptQuest(quest: Quest) {
        if (_activeQuests.value.any { it.id == quest.id }) return
        
        val currentList = _activeQuests.value.toMutableList()
        if (currentList.size >= 3) {
            // Máximo 3 misiones activas
            return
        }
        
        currentList.add(quest)
        _activeQuests.value = currentList
        questProgress[quest.id] = 0
    }
    
    /**
     * Progresar una misión (ej: matar un enemigo del tipo requerido)
     */
    fun progressQuest(targetEntity: String, amount: Int = 1): List<QuestUpdate> {
        val updates = mutableListOf<QuestUpdate>()
        val currentList = _activeQuests.value.toMutableList()
        
        currentList.forEach { quest ->
            if (quest.isCompleted) return@forEach
            
            val isRelevant = when (quest.type) {
                QuestType.HUNT, QuestType.BOSS_HUNT -> {
                    quest.targetEntity?.contains(targetEntity, ignoreCase = true) == true ||
                    targetEntity.contains(quest.targetEntity ?: "", ignoreCase = true)
                }
                QuestType.COLLECT -> {
                    // Simulado: cualquier item cuenta si no hay especificación
                    quest.targetEntity == null || targetEntity.contains(quest.targetEntity, ignoreCase = true)
                }
                QuestType.EXPLORE -> false // Se maneja manualmente
                QuestType.SURVIVAL -> false // Se maneja por turnos
            }
            
            if (isRelevant) {
                val currentProgress = questProgress[quest.id] ?: 0
                val newProgress = (currentProgress + amount).coerceAtMost(quest.targetCount)
                questProgress[quest.id] = newProgress
                
                updates.add(QuestUpdate(quest.id, newProgress, quest.targetCount))
                
                // Verificar completado
                if (newProgress >= quest.targetCount) {
                    completeQuest(quest.id)
                }
            }
        }
        
        _activeQuests.value = currentList
        return updates
    }
    
    /**
     * Completar una misión y entregar recompensas
     */
    fun completeQuest(questId: String): QuestReward? {
        val quest = _activeQuests.value.find { it.id == questId } ?: return null
        
        // Mover a completadas
        val completedQuest = quest.copy(isCompleted = true)
        _completedQuests.value = _completedQuests.value + completedQuest
        _activeQuests.value = _activeQuests.value.filter { it.id != questId }
        questProgress.remove(questId)
        
        return QuestReward(
            xp = quest.rewardXp,
            gold = quest.rewardGold,
            questTitle = quest.title
        )
    }
    
    /**
     * Generar nuevas misiones disponibles basadas en el reino y nivel
     */
    fun generateAvailableQuests(realm: Realm, playerLevel: Int, count: Int = 3): List<Quest> {
        return (1..count).map {
            QuestGenerator.generateQuest(realm, playerLevel)
        }
    }
    
    /**
     * Abandonar una misión
     */
    fun abandonQuest(questId: String) {
        _activeQuests.value = _activeQuests.value.filter { it.id != questId }
        questProgress.remove(questId)
    }
    
    /**
     * Obtener progreso de una misión específica
     */
    fun getQuestProgress(questId: String): Pair<Int, Int> {
        val quest = _activeQuests.value.find { it.id == questId } ?: return Pair(0, 0)
        val progress = questProgress[questId] ?: 0
        return Pair(progress, quest.targetCount)
    }
}

data class QuestUpdate(
    val questId: String,
    val currentProgress: Int,
    val targetProgress: Int
)

data class QuestReward(
    val xp: Int,
    val gold: Int,
    val questTitle: String
)

/**
 * SISTEMA DE EVENTOS ALEATORIOS
 * Gestiona encuentros aleatorios durante el viaje y combate.
 */

class EventSystem {
    
    private val _pendingEvent = MutableStateFlow<RandomEvent?>(null)
    val pendingEvent: StateFlow<RandomEvent?> = _pendingEvent.asStateFlow()
    
    /**
     * Generar un evento aleatorio basado en la peligrosidad del área
     */
    fun triggerRandomEvent(dangerLevel: Float): RandomEvent {
        val event = EventGenerator.generateEvent(dangerLevel)
        _pendingEvent.value = event
        return event
    }
    
    /**
     * Resolver el efecto del evento
     */
    fun resolveEvent(event: RandomEvent, playerStats: PlayerStats): EventResult {
        _pendingEvent.value = null
        
        return when (event.effect) {
            EventEffect.HEAL_FULL -> {
                playerStats.hp = playerStats.maxHp
                EventResult.Heal(playerStats.maxHp)
            }
            EventEffect.HEAL_HALF -> {
                val healAmount = (playerStats.maxHp * 0.5).toInt()
                playerStats.hp = (playerStats.hp + healAmount).coerceAtMost(playerStats.maxHp)
                EventResult.Heal(healAmount)
            }
            EventEffect.DAMAGE_PLAYER -> {
                val damage = (playerStats.maxHp * 0.15).toInt().coerceAtLeast(5)
                playerStats.hp = (playerStats.hp - damage).coerceAtLeast(1)
                EventResult.Damage(damage)
            }
            EventEffect.BUFF_ATTACK -> {
                playerStats.attackBuff = (playerStats.attackBuff + 10).coerceAtMost(50)
                EventResult.Buff("attack", 10)
            }
            EventEffect.BUFF_DEFENSE -> {
                playerStats.defenseBuff = (playerStats.defenseBuff + 10).coerceAtMost(50)
                EventResult.Buff("defense", 10)
            }
            EventEffect.FIND_GOLD -> {
                val goldFound = (50..150).random()
                playerStats.gold += goldFound
                EventResult.Gold(goldFound)
            }
            EventEffect.LOSE_GOLD -> {
                val goldLost = (playerStats.gold * 0.2).toInt().coerceAtLeast(10)
                playerStats.gold = (playerStats.gold - goldLost).coerceAtLeast(0)
                EventResult.Gold(-goldLost)
            }
            EventEffect.SPAWN_AMBUSH -> {
                EventResult.Ambush
            }
            EventEffect.NOTHING -> {
                EventResult.Nothing
            }
        }
    }
    
    /**
     * Limpiar evento pendiente sin resolver
     */
    fun clearEvent() {
        _pendingEvent.value = null
    }
}

sealed class EventResult {
    data class Heal(val amount: Int) : EventResult()
    data class Damage(val amount: Int) : EventResult()
    data class Buff(val type: String, val value: Int) : EventResult()
    data class Gold(val amount: Int) : EventResult() // Positivo o negativo
    object Ambush : EventResult()
    object Nothing : EventResult()
}

/**
 * Estadísticas del jugador que pueden ser modificadas por eventos
 */
class PlayerStats(
    var hp: Int = 100,
    val maxHp: Int = 100,
    var gold: Int = 0,
    var attackBuff: Int = 0,
    var defenseBuff: Int = 0
)

/**
 * SISTEMA DE MAZMORRAS
 * Genera mazmorras procedurales con múltiples salas y recompensas épicas.
 */

class DungeonSystem {
    
    data class DungeonRoom(
        val roomNumber: Int,
        val enemyType: EnemyType,
        val enemyCount: Int,
        val isBossRoom: Boolean,
        val rewardMultiplier: Float
    )
    
    data class GeneratedDungeon(
        val name: String,
        val totalRooms: Int,
        val rooms: List<DungeonRoom>,
        val finalRewardXp: Int,
        val finalRewardGold: Int
    )
    
    /**
     * Generar una mazmorra procedural basada en el nivel del jugador
     */
    fun generateDungeon(playerLevel: Int): GeneratedDungeon {
        val totalRooms = (3..6).random().coerceAtMost(3 + (playerLevel / 10))
        val rooms = mutableListOf<DungeonRoom>()
        
        for (i in 1..totalRooms) {
            val isLastRoom = i == totalRooms
            val availableEnemies = EnemyType.values().filter { 
                !it.isElite && it.requiredRealm != Realm.STARTER 
            }
            
            val enemy = availableEnemies.random()
            val enemyCount = if (isLastRoom) 1 else (2..4).random()
            val rewardMult = 1.0f + (i * 0.5f)
            
            rooms.add(
                DungeonRoom(
                    roomNumber = i,
                    enemyType = if (isLastRoom) getBossForLevel(playerLevel) else enemy,
                    enemyCount = enemyCount,
                    isBossRoom = isLastRoom,
                    rewardMultiplier = rewardMult
                )
            )
        }
        
        val baseXp = rooms.sumOf { it.enemyType.xpValue * it.enemyCount }
        val baseGold = rooms.sumOf { it.enemyType.goldValue * it.enemyCount }
        
        return GeneratedDungeon(
            name = "Mazmorra de Nivel ${playerLevel}",
            totalRooms = totalRooms,
            rooms = rooms,
            finalRewardXp = (baseXp * GameBalance.getEliteXpMultiplier()).toInt(),
            finalRewardGold = (baseGold * GameBalance.getEliteGoldMultiplier()).toInt()
        )
    }
    
    private fun getBossForLevel(level: Int): EnemyType {
        return when {
            level < 10 -> EnemyType.GOBLIN_KING
            level < 20 -> EnemyType.ANCIENT_TREANT
            level < 30 -> EnemyType.FROST_LORD
            level < 40 -> EnemyType.MALAKOR_AVATAR
            else -> EnemyType.CRYSTAL_GUARDIAN
        }
    }
}

/**
 * SISTEMA DE DIÁLOGOS Y LORE
 * Gestiona conversaciones con NPCs y descubrimiento de historia.
 */

class DialogueSystem {
    
    data class DialogueLine(
        val speaker: String,
        val text: String,
        val emotion: Emotion = Emotion.NEUTRAL
    )
    
    enum class Emotion {
        NEUTRAL, HAPPY, SAD, ANGRY, FEARFUL, MYSTERIOUS
    }
    
    data class Conversation(
        val npcName: String,
        val npcTitle: String,
        val openingLines: List<DialogueLine>,
        val options: List<DialogueOption>
    )
    
    data class DialogueOption(
        val text: String,
        val responseLines: List<DialogueLine>,
        val reward: QuestReward? = null,
        val closesConversation: Boolean = false
    )
    
    /**
     * Generar diálogo contextual basado en el NPC y el estado del juego
     */
    fun getNpcDialogue(npcType: NpcType, playerLevel: Int, discoveredLore: Set<String>): Conversation {
        return when (npcType) {
            NpcType.ELDER -> createElderDialogue(playerLevel, discoveredLore)
            NpcType.MERCHANT -> createMerchantDialogue()
            NpcType.WARRIOR -> createWarriorDialogue(playerLevel)
            NpcType.MYSTIC -> createMysticDialogue(discoveredLore)
        }
    }
    
    private fun createElderDialogue(level: Int, lore: Set<String>): Conversation {
        val hasDiscoveredForest = lore.contains("FOREST")
        
        return Conversation(
            npcName = "Eldrin",
            npcTitle = "Anciano del Valle",
            openingLines = listOf(
                DialogueLine("Eldrin", "Ah, otro aventurero buscando gloria... o quizás, redención.", Emotion.MYSTERIOUS),
                DialogueLine("Eldrin", "El Cristal de Aethelgard nos ha abandonado, joven. ¿Crees poder traerlo de vuelta?")
            ),
            options = listOf(
                DialogueOption(
                    text = "¿Qué sabes sobre el Cristal?",
                    responseLines = listOf(
                        DialogueLine("Eldrin", WorldLore.introduction, Emotion.SAD),
                        DialogueLine("Eldrin", "Cuatro fragmentos... cuatro reinos caídos. Tu camino será arduo.")
                    )
                ),
                DialogueOption(
                    text = "Necesito una misión.",
                    responseLines = listOf(
                        DialogueLine("Eldrin", "Valiente decisión. Los goblins se agrupan en el bosque...", Emotion.NEUTRAL),
                        DialogueLine("Eldrin", "Demuestra tu valor y serás recompensado.")
                    ),
                    closesConversation = true
                ),
                DialogueOption(
                    text = "Debo irme.",
                    responseLines = listOf(
                        DialogueLine("Eldrin", "Que la luz te guíe... mientras dure.", Emotion.SAD)
                    ),
                    closesConversation = true
                )
            )
        )
    }
    
    private fun createMerchantDialogue(): Conversation {
        return Conversation(
            npcName = "Grek",
            npcTitle = "Mercader Itinerante",
            openingLines = listOf(
                DialogueLine("Grek", "¡Bienvenido, bienvenido! ¿Buscas equipo? ¿Pociones? ¿Información?", Emotion.HAPPY),
                DialogueLine("Grek", "Todo tiene un precio, amigo mío. Todo.")
            ),
            options = listOf(
                DialogueOption(
                    text = "¿Qué noticias traes?",
                    responseLines = listOf(
                        DialogueLine("Grek", "He oído rumores... un guerrero esquelético merodea las montañas.", Emotion.FEARFUL),
                        DialogueLine("Grek", "Dicen que guarda un tesoro antiguo. Pero nadie que fue a buscarlo regresó.")
                    )
                ),
                DialogueOption(
                    text = "Comprar suministros",
                    responseLines = listOf(
                        DialogueLine("Grek", "¡Excelente elección! Déjame mostrarte mis mejores productos.", Emotion.HAPPY)
                    ),
                    reward = QuestReward(0, -50, "Compra en tienda"),
                    closesConversation = true
                ),
                DialogueOption(
                    text = "Adiós",
                    responseLines = listOf(
                        DialogueLine("Grek", "¡Vuelve pronto! Tengo nuevos productos cada semana.", Emotion.HAPPY)
                    ),
                    closesConversation = true
                )
            )
        )
    }
    
    private fun createWarriorDialogue(level: Int): Conversation {
        return Conversation(
            npcName = "Kael",
            npcTitle = "Guerrero Veterano",
            openingLines = listOf(
                DialogueLine("Kael", "Saludos, camarada. Veo fuego en tus ojos. ¿Buscas batalla?", Emotion.NEUTRAL),
                DialogueLine("Kael", "Te respeto. Pocos tienen el valor de enfrentar la oscuridad que se avecina.")
            ),
            options = listOf(
                DialogueOption(
                    text = "Enséñame a luchar mejor.",
                    responseLines = listOf(
                        DialogueLine("Kael", "La fuerza no lo es todo. La disciplina... eso es lo que separa a los vivos de los muertos.", Emotion.NEUTRAL),
                        DialogueLine("Kael", "Observa mi postura. Siente el flujo de la batalla.")
                    ),
                    reward = QuestReward(50, 0, "Entrenamiento de combate"),
                    closesConversation = true
                ),
                DialogueOption(
                    text = "¿Has visto enemigos fuertes?",
                    responseLines = listOf(
                        DialogueLine("Kael", "Los he visto... y he huido de ellos. No hay vergüenza en vivir para luchar otro día.", Emotion.SAD)
                    )
                ),
                DialogueOption(
                    text = "Hasta luego",
                    responseLines = listOf(
                        DialogueLine("Kael", "Que tu espada sea afilada y tu escudo resistente.", Emotion.NEUTRAL)
                    ),
                    closesConversation = true
                )
            )
        )
    }
    
    private fun createMysticDialogue(lore: Set<String>): Conversation {
        val hasLore = lore.isNotEmpty()
        
        return Conversation(
            npcName = "Lyra",
            npcTitle = "Vidente de los Susurros",
            openingLines = listOf(
                DialogueLine("Lyra", "Te veo... en mis sueños. Caminas entre sombras y luz.", Emotion.MYSTERIOUS),
                DialogueLine("Lyra", "El destino teje hilos a tu alrededor. ¿Los cortarás o los seguirás?")
            ),
            options = listOf(
                DialogueOption(
                    text = "¿Qué ves en mi futuro?",
                    responseLines = listOf(
                        if (hasLore) {
                            DialogueLine("Lyra", "Veo... fragmentos. Has comenzado a entender. Continúa.", Emotion.MYSTERIOUS)
                        } else {
                            DialogueLine("Lyra", "Niebla... solo niebla. Aún no estás listo para ver la verdad.", Emotion.SAD)
                        }
                    )
                ),
                DialogueOption(
                    text = "¿Dónde está el siguiente fragmento?",
                    responseLines = listOf(
                        DialogueLine("Lyra", "Las estrellas guardan sus secretos celosamente...", Emotion.MYSTERIOUS),
                        DialogueLine("Lyra", "Pero siento... frío. Hielo antiguo. Busca donde el aliento se congela.")
                    )
                ),
                DialogueOption(
                    text = "Gracias por tu visión",
                    responseLines = listOf(
                        DialogueLine("Lyra", "El conocimiento es una carga. Llévala con sabiduría.", Emotion.NEUTRAL)
                    ),
                    closesConversation = true
                )
            )
        )
    }
}

enum class NpcType {
    ELDER, MERCHANT, WARRIOR, MYSTIC
}
