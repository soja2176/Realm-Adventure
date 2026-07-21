package com.example.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

enum class GameScreen {
    CREATING_CHARACTER,
    WORLD_MAP,
    COMBAT,
    CHARACTER_SCREEN,
    TALENTS,
    INVENTORY,
    SHOP,
    HELP_SCREEN
}

data class Combatant(
    val name: String,
    val maxHp: Int,
    var currentHp: Int,
    val maxMp: Int,
    var currentMp: Int,
    val attack: Int,
    val defense: Int,
    val level: Int,
    val isBoss: Boolean = false,
    val rarity: String = "NORMAL" // "NORMAL", "ELITE", "BOSS"
)

data class CombatState(
    val active: Boolean = false,
    val enemy: Combatant? = null,
    val playerCurrentHp: Int = 100,
    val playerCurrentMp: Int = 50,
    val combatLogs: List<String> = emptyList(),
    val playerTurn: Boolean = true,
    val damageFeedbackPlayer: String? = null, // visual hit text
    val damageFeedbackEnemy: String? = null,
    val victory: Boolean? = null, // true = won, false = lost, null = active
    val lootDropped: Item? = null,
    val expGained: Int = 0,
    val goldGained: Int = 0,
    val activeAnimation: String? = null // "PLAYER_ATTACK", "PLAYER_HEAL", "PLAYER_MAGIC", "ENEMY_ATTACK", "ENEMY_SKILL", "PLAYER_POTION"
)

data class MapTile(
    val x: Int,
    val y: Int,
    val biome: String, // "Pradera", "Pantano", "Bosque Oscuro", "Montaña", "Ruinas Ancestrales", "Guarida de Jefe"
    val explored: Boolean = false,
    val hasEncounter: Boolean = true,
    val encounterType: String = "MONSTER", // "MONSTER", "CHEST", "SHRINE", "BOSS"
    val levelRequirement: Int = 1,
    val isBossLair: Boolean = false,
    val isObstacle: Boolean = false,
    val isEnemySpawn: Boolean = false
)

class GameViewModel(private val repository: GameProgressRepository) : ViewModel() {

    private val _screenState = MutableStateFlow(GameScreen.CREATING_CHARACTER)
    val screenState: StateFlow<GameScreen> = _screenState.asStateFlow()

    private val _progressState = MutableStateFlow<GameProgress?>(null)
    val progressState: StateFlow<GameProgress?> = _progressState.asStateFlow()

    val playerStats: StateFlow<PlayerStats?> = _progressState
        .map { progress ->
            progress?.let {
                PlayerStats(
                    hp = it.currentHp,
                    mp = it.currentMp,
                    strength = it.statStr,
                    agility = it.statDex,
                    maxHp = it.maxHp,
                    maxMp = it.maxMp
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _combatState = MutableStateFlow(CombatState())
    val combatState: StateFlow<CombatState> = _combatState.asStateFlow()

    // Character creator temp state
    private val _creatorName = MutableStateFlow("")
    val creatorName = _creatorName.asStateFlow()

    private val _creatorRace = MutableStateFlow("Humano")
    val creatorRace = _creatorRace.asStateFlow()

    private val _creatorClass = MutableStateFlow("Guerrero")
    val creatorClass = _creatorClass.asStateFlow()

    private val _creatorPointsAvailable = MutableStateFlow(15)
    val creatorPointsAvailable = _creatorPointsAvailable.asStateFlow()

    private val _creatorStr = MutableStateFlow(10)
    val creatorStr = _creatorStr.asStateFlow()

    private val _creatorDex = MutableStateFlow(10)
    val creatorDex = _creatorDex.asStateFlow()

    private val _creatorInt = MutableStateFlow(10)
    val creatorInt = _creatorInt.asStateFlow()

    private val _creatorCon = MutableStateFlow(10)
    val creatorCon = _creatorCon.asStateFlow()

    // Map tiles procedurally loaded around current coordinate
    private val _proceduralMap = MutableStateFlow<List<MapTile>>(emptyList())
    val proceduralMap: StateFlow<List<MapTile>> = _proceduralMap.asStateFlow()

    // Active screen notices/notifications
    private val _notification = MutableStateFlow<String?>(null)
    val notification = _notification.asStateFlow()

    // Drop rates calibration (in percent)
    private val _dropRateCommon = MutableStateFlow(50)
    private val _dropRateRare = MutableStateFlow(30)
    private val _dropRateEpic = MutableStateFlow(15)
    private val _dropRateLegendary = MutableStateFlow(5)

    private val _isAutoCombat = MutableStateFlow(false)
    val isAutoCombat: StateFlow<Boolean> = _isAutoCombat.asStateFlow()

    private val _isAutoNavigation = MutableStateFlow(false)
    val isAutoNavigation: StateFlow<Boolean> = _isAutoNavigation.asStateFlow()

    val allCharactersState: StateFlow<List<GameProgress>> = repository.allCharactersFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var isFirstLoad = true

    fun startNewCharacterCreator() {
        viewModelScope.launch {
            repository.deactivateAll()
            _screenState.value = GameScreen.CREATING_CHARACTER
            _creatorName.value = ""
            _creatorPointsAvailable.value = 15
            recalculateCreatorBaseStats()
            showNotification("Crea un nuevo héroe para comenzar tu aventura en Eldoria.")
        }
    }

    fun selectCharacter(characterId: Int) {
        viewModelScope.launch {
            repository.setActive(characterId)
            isFirstLoad = true
            _screenState.value = GameScreen.WORLD_MAP
            showNotification("Partida cargada.")
        }
    }

    fun deleteCharacter(characterId: Int) {
        viewModelScope.launch {
            repository.deleteCharacter(characterId)
            showNotification("Personaje eliminado.")
            val remaining = repository.getProgress()
            if (remaining == null) {
                _screenState.value = GameScreen.CREATING_CHARACTER
            }
        }
    }

    fun toggleAutoCombat() {
        _isAutoCombat.value = !_isAutoCombat.value
        val state = _combatState.value
        if (_isAutoCombat.value && state.active && state.playerTurn && state.victory == null) {
            viewModelScope.launch {
                performAutoCombatTurn(state)
            }
        }
        showNotification(if (_isAutoCombat.value) "¡Combate Automático ACTIVADO!" else "Combate Automático desactivado")
    }

    fun toggleAutoNavigation() {
        _isAutoNavigation.value = !_isAutoNavigation.value
        if (_isAutoNavigation.value && _screenState.value == GameScreen.WORLD_MAP && !isExploring && !_combatState.value.active) {
            val progress = _progressState.value
            if (progress != null && progress.hasActiveChar) {
                viewModelScope.launch {
                    performAutoNavigationStep(progress)
                }
            }
        }
        showNotification(if (_isAutoNavigation.value) "¡Navegación Automática ACTIVADA!" else "Navegación Automática desactivada")
    }

    private fun performAutoCombatTurn(state: CombatState) {
        val progress = _progressState.value ?: return
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson)
        val classSkills = GameJsonParser.listFromJson<Skill>(progress.skillsJson)

        val hpPercent = state.playerCurrentHp.toFloat() / progress.maxHp.toFloat()

        // 1. Healing logic (if HP is below 40%)
        if (hpPercent < 0.4f) {
            // Try to find healing skill
            val healSkill = classSkills.firstOrNull { it.healingMultiplier > 0.0 && state.playerCurrentMp >= it.manaCost }
            if (healSkill != null) {
                executeSkill(healSkill)
                return
            }

            // Otherwise try potion
            val hasPotion = invList.any { it.type == "POTION" }
            if (hasPotion) {
                usePotionCombat()
                return
            }
        }

        // 2. Offense logic: Use available damaging skills
        val damageSkill = classSkills
            .filter { it.damageMultiplier > 0.0 && state.playerCurrentMp >= it.manaCost }
            .maxByOrNull { it.damageMultiplier }
        if (damageSkill != null) {
            executeSkill(damageSkill)
            return
        }

        // 3. Fallback: Basic attack
        executeBasicAttack()
    }

    private fun performAutoNavigationStep(progress: GameProgress) {
        val mapTiles = _proceduralMap.value
        if (mapTiles.isEmpty()) return

        val candidateTiles = mapTiles.filter { tile ->
            !tile.isObstacle && (tile.x != progress.currentX || tile.y != progress.currentY)
        }

        val pathableTiles = candidateTiles.mapNotNull { tile ->
            val path = findPath(progress.currentX, progress.currentY, tile.x, tile.y, mapTiles)
            if (path != null) Pair(tile, path) else null
        }

        if (pathableTiles.isEmpty()) return

        // Sort pathable tiles STRICTLY by path length (closest first)
        val sortedPathable = pathableTiles.sortedBy { it.second.size }

        // Find the closest active encounter (hasEncounter = true)
        val closestEncounter = sortedPathable.firstOrNull { it.first.hasEncounter }

        // We want to avoid jumping to far tiles.
        // If the closest encounter is far (e.g., path size > 1), we should instead only move 1 step along the path to it!
        // This ensures the player always moves to an adjacent tile (distance 1) on every step of the auto-navigation loop,
        // rather than doing a long multi-step walk that makes them jump across the map.
        val targetPair = closestEncounter
            ?: sortedPathable.firstOrNull { !it.first.explored }
            ?: sortedPathable.filter { it.second.size == 1 }.randomOrNull()

        if (targetPair != null) {
            val path = targetPair.second
            if (path.isNotEmpty()) {
                // Take only the FIRST step of the path to make it a step-by-step movement!
                // This guarantees the player only moves to an adjacent tile in each step, completely avoiding far jumps.
                selectTileAndExplore(path.first())
            }
        }
    }

    init {
        viewModelScope.launch {
            repository.progressFlow.collect { progress ->
                _progressState.value = progress
                if (progress != null && progress.hasActiveChar) {
                    if (isFirstLoad) {
                        _screenState.value = GameScreen.WORLD_MAP
                        generateMapAround(progress.currentX, progress.currentY, progress.mapPointsExploredJson)
                        isFirstLoad = false
                    }
                } else {
                    _screenState.value = GameScreen.CREATING_CHARACTER
                    isFirstLoad = false
                }
            }
        }

        // Auto Combat collector
        viewModelScope.launch {
            _combatState.collect { state ->
                if (state.active && state.playerTurn && state.victory == null && _isAutoCombat.value) {
                    kotlinx.coroutines.delay(1000)
                    val currentState = _combatState.value
                    if (currentState.active && currentState.playerTurn && currentState.victory == null && _isAutoCombat.value) {
                        performAutoCombatTurn(currentState)
                    }
                }
            }
        }

        // Auto Navigation checker loop
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1200)
                if (_isAutoNavigation.value && _screenState.value == GameScreen.WORLD_MAP && !isExploring && !_combatState.value.active) {
                    val progress = _progressState.value
                    if (progress != null && progress.hasActiveChar) {
                        performAutoNavigationStep(progress)
                    }
                }
            }
        }
    }

    fun showNotification(msg: String) {
        _notification.value = msg
    }

    fun dismissNotification() {
        _notification.value = null
    }

    // Shop System State
    private val _shopItems = MutableStateFlow<List<Item>>(emptyList())
    val shopItems = _shopItems.asStateFlow()

    fun generateShopItems(level: Int) {
        val list = mutableListOf<Item>()
        for (i in 1..8) {
            list.add(generateProceduralItem(level, isBoss = false))
        }
        _shopItems.value = list
    }

    fun refreshShop() {
        val progress = _progressState.value ?: return
        if (progress.charGold < 20) {
            showNotification("¡Cuesta 20 de oro reabastecer la tienda!")
            return
        }
        viewModelScope.launch {
            val updated = progress.copy(charGold = progress.charGold - 20)
            repository.saveProgress(updated)
            generateShopItems(progress.charLevel)
            showNotification("¡La tienda ha sido reabastecida con mercancías nuevas!")
        }
    }

    fun buyItem(item: Item, cost: Int) {
        val progress = _progressState.value ?: return
        if (progress.charGold < cost) {
            showNotification("¡No tienes suficiente oro para comprar ${item.name}!")
            return
        }
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        if (invList.size >= 12) {
            showNotification("¡No tienes espacio suficiente en el inventario!")
            return
        }
        viewModelScope.launch {
            invList.add(item)
            val updatedProgress = progress.copy(
                charGold = progress.charGold - cost,
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            repository.saveProgress(updatedProgress)
            _shopItems.value = _shopItems.value.filter { it.id != item.id }
            showNotification("¡Compraste ${item.name} por $cost monedas de oro!")
        }
    }

    fun sellItem(item: Item) {
        val progress = _progressState.value ?: return
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        val index = invList.indexOfFirst { it.id == item.id }
        if (index == -1) return

        val sellPrice = when (item.rarity) {
            "LEGENDARY" -> 150
            "EPIC" -> 80
            "RARE" -> 40
            else -> 15
        }

        viewModelScope.launch {
            invList.removeAt(index)
            val updatedProgress = progress.copy(
                charGold = progress.charGold + sellPrice,
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            repository.saveProgress(updatedProgress)
            showNotification("¡Vendiste ${item.name} por $sellPrice de oro!")
        }
    }

    fun changeScreen(screen: GameScreen) {
        _screenState.value = screen
        if (screen == GameScreen.SHOP && _shopItems.value.isEmpty()) {
            _progressState.value?.let {
                generateShopItems(it.charLevel)
            }
        }
    }

    // --- CHARACTER CREATOR ---
    fun updateCreatorName(name: String) {
        _creatorName.value = name
    }

    fun selectRace(race: String) {
        _creatorRace.value = race
        recalculateCreatorBaseStats()
    }

    fun selectClass(cls: String) {
        _creatorClass.value = cls
        recalculateCreatorBaseStats()
    }

    private fun recalculateCreatorBaseStats() {
        // Reset points to 15
        _creatorPointsAvailable.value = 15
        
        // Race bonuses
        val raceStr = when (_creatorRace.value) {
            "Orco" -> 14
            "Enano" -> 12
            else -> 10
        }
        val raceDex = when (_creatorRace.value) {
            "Elfo" -> 13
            "Humano" -> 11
            else -> 10
        }
        val raceInt = when (_creatorRace.value) {
            "Elfo" -> 14
            "Humano" -> 10
            else -> 8
        }
        val raceCon = when (_creatorRace.value) {
            "Enano" -> 14
            "Orco" -> 12
            else -> 10
        }

        // Apply class bonuses
        val classStr = when (_creatorClass.value) {
            "Guerrero" -> 3
            "Pícaro" -> 1
            else -> 0
        }
        val classDex = when (_creatorClass.value) {
            "Pícaro" -> 4
            "Guerrero" -> 1
            else -> 0
        }
        val classInt = when (_creatorClass.value) {
            "Mago" -> 4
            "Clérigo" -> 2
            else -> 0
        }
        val classCon = when (_creatorClass.value) {
            "Guerrero" -> 2
            "Clérigo" -> 2
            else -> 1
        }

        _creatorStr.value = raceStr + classStr
        _creatorDex.value = raceDex + classDex
        _creatorInt.value = raceInt + classInt
        _creatorCon.value = raceCon + classCon
    }

    fun modifyStat(stat: String, amount: Int) {
        val points = _creatorPointsAvailable.value
        if (amount > 0 && points <= 0) return
        
        when (stat) {
            "STR" -> {
                val current = _creatorStr.value
                if (amount < 0 && current <= 5) return
                _creatorStr.value = current + amount
                _creatorPointsAvailable.value = points - amount
            }
            "DEX" -> {
                val current = _creatorDex.value
                if (amount < 0 && current <= 5) return
                _creatorDex.value = current + amount
                _creatorPointsAvailable.value = points - amount
            }
            "INT" -> {
                val current = _creatorInt.value
                if (amount < 0 && current <= 5) return
                _creatorInt.value = current + amount
                _creatorPointsAvailable.value = points - amount
            }
            "CON" -> {
                val current = _creatorCon.value
                if (amount < 0 && current <= 5) return
                _creatorCon.value = current + amount
                _creatorPointsAvailable.value = points - amount
            }
        }
    }

    fun autoAllocateCreatorStats() {
        val points = _creatorPointsAvailable.value
        if (points <= 0) return

        val cls = _creatorClass.value
        val order = when (cls) {
            "Guerrero" -> listOf("STR", "STR", "STR", "CON")
            "Pícaro" -> listOf("DEX", "DEX", "DEX", "CON", "STR")
            "Mago" -> listOf("INT", "INT", "INT", "CON")
            "Clérigo" -> listOf("INT", "CON", "INT", "CON", "STR")
            else -> listOf("STR", "DEX", "INT", "CON")
        }

        var index = 0
        for (i in 0 until points) {
            val stat = order[index % order.size]
            when (stat) {
                "STR" -> {
                    _creatorStr.value = _creatorStr.value + 1
                }
                "DEX" -> {
                    _creatorDex.value = _creatorDex.value + 1
                }
                "INT" -> {
                    _creatorInt.value = _creatorInt.value + 1
                }
                "CON" -> {
                    _creatorCon.value = _creatorCon.value + 1
                }
            }
            index++
        }
        _creatorPointsAvailable.value = 0
        showNotification("Puntos de atributo iniciales asignados según tu clase ($cls)")
    }

    fun submitCharacter() {
        val name = _creatorName.value.trim()
        if (name.isEmpty()) {
            showNotification("¡Debes ingresar un nombre para tu personaje!")
            return
        }

        viewModelScope.launch {
            repository.deactivateAll()

            val maxHp = _creatorCon.value * 12
            val maxMp = _creatorInt.value * 6

            // Starter items based on class
            val starterWeapon = when (_creatorClass.value) {
                "Guerrero" -> Item("w_start", "Espada de Madera", "WEAPON", "COMÚN", strBonus = 2, dmgBonus = 4, description = "Una espada simple tallada en pino.", itemLevel = 1, imageResName = "img_item_sword_1784593548868")
                "Mago" -> Item("w_start", "Vara del Aprendiz", "WEAPON", "COMÚN", intBonus = 2, dmgBonus = 3, description = "Canaliza leves destellos de magia.", itemLevel = 1, imageResName = "img_item_staff_1784593558118")
                "Pícaro" -> Item("w_start", "Daga Oxidada", "WEAPON", "COMÚN", dexBonus = 2, dmgBonus = 4, description = "Vieja y mellada, pero afilada.", itemLevel = 1, imageResName = "img_item_dagger_1784593567531")
                else -> Item("w_start", "Maza de Fresno", "WEAPON", "COMÚN", conBonus = 1, dmgBonus = 3, description = "Robusta maza para impartir justicia.", itemLevel = 1, imageResName = "img_item_sword_1784593548868")
            }

            val starterArmor = Item("a_start", "Harapos de Viaje", "ARMOR", "COMÚN", conBonus = 1, defBonus = 2, description = "Prenda básica que cubre lo justo.", itemLevel = 1, imageResName = "img_item_plate_1784593577913")

            // Base skills preloaded
            val skillsList = getStarterSkills(_creatorClass.value)
            val talentsList = getBaseTalentTree()

            val progress = GameProgress(
                id = 0,
                isActiveChar = true,
                charName = name,
                charRace = _creatorRace.value,
                charClass = _creatorClass.value,
                charLevel = 1,
                charExp = 0,
                charGold = 150,
                statStr = _creatorStr.value,
                statDex = _creatorDex.value,
                statInt = _creatorInt.value,
                statCon = _creatorCon.value,
                statPointsAvailable = _creatorPointsAvailable.value,
                talentPointsSpent = 0,
                talentPointsAvailable = 0,
                maxHp = maxHp,
                currentHp = maxHp,
                maxMp = maxMp,
                currentMp = maxMp,
                equippedHelmetJson = "",
                equippedWingsJson = "",
                equippedWeaponJson = GameJsonParser.toJson(starterWeapon),
                equippedShieldJson = "",
                equippedArmorJson = GameJsonParser.toJson(starterArmor),
                equippedGlovesJson = "",
                equippedBootsJson = "",
                equippedRingJson = "",
                equippedEarringJson = "",
                equippedRelicJson = "",
                inventoryJson = "[]",
                talentsJson = GameJsonParser.listToJson(talentsList),
                skillsJson = GameJsonParser.listToJson(skillsList),
                completedQuestsJson = "[]",
                mapPointsExploredJson = "[\"0,0\"]",
                currentX = 0,
                currentY = 0
            )

            repository.saveProgress(progress)
            _screenState.value = GameScreen.WORLD_MAP
            showNotification("¡Tu aventura comienza en Eldoria, $name!")
        }
    }

    private fun getStarterSkills(cls: String): List<Skill> {
        return when (cls) {
            "Guerrero" -> listOf(
                Skill("g_1", "Golpe Feroz", "Un ataque de fuerza bruta que inflige 1.6x de daño físico.", manaCost = 10, minLevel = 1, damageMultiplier = 1.6),
                Skill("g_2", "Grito de Provocación", "Intimida al enemigo, aumentando tu defensa en un 30%.", manaCost = 12, minLevel = 2, damageMultiplier = 0.0, healingMultiplier = 0.0)
            )
            "Mago" -> listOf(
                Skill("m_1", "Centella", "Arroja una chispa arcana rápida que inflige 1.5x de daño mágico.", manaCost = 8, minLevel = 1, damageMultiplier = 1.5),
                Skill("m_2", "Llama Sagrada", "Desata una explosión de fuego sagrado. Daño 2.2x.", manaCost = 18, minLevel = 3, damageMultiplier = 2.2)
            )
            "Pícaro" -> listOf(
                Skill("p_1", "Puñalada", "Ataque rápido a puntos débiles. Daño 1.4x con alta probabilidad de crítico.", manaCost = 10, minLevel = 1, damageMultiplier = 1.4),
                Skill("p_2", "Ataque Sombrío", "Desaparece en las sombras e inflige 2.0x de daño sorpresa.", manaCost = 15, minLevel = 3, damageMultiplier = 2.0)
            )
            else -> listOf( // Clérigo
                Skill("c_1", "Luz Sagrada", "Invoca el poder de los cielos para curar tus heridas.", manaCost = 12, minLevel = 1, damageMultiplier = 0.0, healingMultiplier = 1.8),
                Skill("c_2", "Martillo de Justicia", "Golpea con fe pura, infligiendo 1.3x de daño y sanándote un 20%.", manaCost = 14, minLevel = 2, damageMultiplier = 1.3, healingMultiplier = 0.5)
            )
        }
    }

    private fun getBaseTalentTree(): List<Talent> {
        return listOf(
            Talent("t_1", "Fuerza Bruta", "Aumenta la fuerza base y el daño físico un +4% por rango.", category = "COMBAT", row = 1, col = 1, maxRank = 3),
            Talent("t_2", "Coraza de Hierro", "Aumenta la armadura y reducción de daño físico un +5% por rango.", category = "COMBAT", row = 2, col = 1, maxRank = 3, prerequisiteId = "t_1"),
            Talent("t_3", "Sed de Sangre", "Los ataques críticos restauran un 10% de tu salud máxima.", category = "COMBAT", row = 3, col = 1, maxRank = 1, prerequisiteId = "t_2"),

            Talent("t_4", "Canalización Éter", "Aumenta el poder de hechizos y daño mágico un +4% por rango.", category = "MAGIC", row = 1, col = 2, maxRank = 3),
            Talent("t_5", "Mente Enfocada", "Aumenta el mana máximo un +8% por rango.", category = "MAGIC", row = 2, col = 2, maxRank = 3, prerequisiteId = "t_4"),
            Talent("t_6", "Escudo de Éter", "Un escudo mágico pasivo que mitiga el 15% del daño recibido.", category = "MAGIC", row = 3, col = 2, maxRank = 1, prerequisiteId = "t_5"),

            Talent("t_7", "Pies Ligeros", "Aumenta la evasión y probabilidad de esquivar un +4% por rango.", category = "SHADOW", row = 1, col = 3, maxRank = 3),
            Talent("t_8", "Golpes Letales", "Aumenta la probabilidad de impacto crítico un +3% por rango.", category = "SHADOW", row = 2, col = 3, maxRank = 3, prerequisiteId = "t_7"),
            Talent("t_9", "Buscador de Oro", "Aumenta la obtención de oro y la probabilidad de botín legendario en un +20%.", category = "SHADOW", row = 3, col = 3, maxRank = 1, prerequisiteId = "t_8")
        )
    }

    // --- MAP PROCEDURAL & EXPLORATION ---
    private fun isBaseRewardTile(x: Int, y: Int): Boolean {
        if (x == 0 && y == 0) return false
        val seed = (x * 73856093) xor (y * 19349663)
        val random = Random(seed)

        val biomes = listOf("Pradera", "Pantano", "Bosque Oscuro", "Montaña", "Ruinas Ancestrales")
        val biome = if (abs(x) == 4 && abs(y) == 4 || (x == 3 && y == -3)) "Guarida de Jefe"
        else biomes[abs(seed % biomes.size)]

        if (biome == "Guarida de Jefe") return false

        val r = random.nextInt(100)
        return r >= 65 // Originally CHEST or SHRINE
    }

    private fun getRewardPriority(x: Int, y: Int): Int {
        val seed = (x * 5723489) xor (y * 9345821)
        return Random(seed).nextInt(1000000)
    }

    private fun isValidRewardTile(x: Int, y: Int): Boolean {
        if (!isBaseRewardTile(x, y)) return false

        val myPriority = getRewardPriority(x, y)

        // Check a Chebyshev distance of 9 around (x, y)
        // If there's another base reward tile in this range with higher priority, this one is disqualified.
        for (dx in -9..9) {
            for (dy in -9..9) {
                if (dx == 0 && dy == 0) continue
                val nx = x + dx
                val ny = y + dy

                if (isBaseRewardTile(nx, ny)) {
                    val otherPriority = getRewardPriority(nx, ny)
                    if (otherPriority > myPriority) {
                        return false
                    } else if (otherPriority == myPriority) {
                        // Tie-breaker
                        if (nx < x || (nx == x && ny < y)) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }

    fun generateMapAround(cx: Int, cy: Int, exploredJson: String) {
        val exploredSet = GameJsonParser.listFromJson<String>(exploredJson).toSet()
        val tiles = mutableListOf<MapTile>()

        // Generate a 5x5 viewing grid around player (cx, cy)
        for (dx in -2..2) {
            for (dy in -2..2) {
                val x = cx + dx
                val y = cy + dy
                val coordKey = "$x,$y"
                val isExplored = exploredSet.contains(coordKey)

                // Procedural generation logic based on coordinates seed
                val seed = (x * 73856093) xor (y * 19349663)
                val random = Random(seed)

                val biomes = listOf("Pradera", "Pantano", "Bosque Oscuro", "Montaña", "Ruinas Ancestrales")
                val biome = if (x == 0 && y == 0) "Santuario Inicial"
                else if (abs(x) == 4 && abs(y) == 4 || (x == 3 && y == -3)) "Guarida de Jefe"
                else biomes[abs(seed % biomes.size)]

                val levelReq = abs(x) + abs(y)

                val encounterType = if (biome == "Guarida de Jefe") "BOSS"
                else {
                    val r = random.nextInt(100)
                    val baseType = when {
                        r < 65 -> "MONSTER"
                        r < 85 -> "CHEST"
                        else -> "SHRINE"
                    }
                    if (baseType == "CHEST" || baseType == "SHRINE") {
                        if (isValidRewardTile(x, y)) {
                            baseType
                        } else {
                            "MONSTER" // Fall back to MONSTER to balance risk
                        }
                    } else {
                        "MONSTER"
                    }
                }

                val isObs = (biome == "Montaña") && (abs(x) + abs(y) > 1)
                val isEnemy = (encounterType == "MONSTER" || encounterType == "BOSS") && !isExplored && (x != 0 || y != 0)

                tiles.add(
                    MapTile(
                        x = x,
                        y = y,
                        biome = biome,
                        explored = isExplored,
                        hasEncounter = !isExplored && (x != 0 || y != 0),
                        encounterType = encounterType,
                        levelRequirement = if (levelReq == 0) 1 else levelReq,
                        isBossLair = biome == "Guarida de Jefe",
                        isObstacle = isObs,
                        isEnemySpawn = isEnemy
                    )
                )
            }
        }
        _proceduralMap.value = tiles
    }

    private fun findPath(startX: Int, startY: Int, targetX: Int, targetY: Int, mapTiles: List<MapTile>): List<MapTile>? {
        if (startX == targetX && startY == targetY) return emptyList()
        
        val targetTile = mapTiles.find { it.x == targetX && it.y == targetY } ?: return null
        if (targetTile.isObstacle) return null
        
        val queue = mutableListOf<Pair<MapTile, List<MapTile>>>()
        val visited = mutableSetOf<String>()
        
        val startTile = mapTiles.find { it.x == startX && it.y == startY } ?: return null
        queue.add(Pair(startTile, emptyList()))
        visited.add("$startX,$startY")
        
        while (queue.isNotEmpty()) {
            val (current, path) = queue.removeAt(0)
            
            if (current.x == targetX && current.y == targetY) {
                return path
            }
            
            val neighbors = listOf(
                Pair(current.x + 1, current.y),
                Pair(current.x - 1, current.y),
                Pair(current.x, current.y + 1),
                Pair(current.x, current.y - 1)
            )
            
            for ((nx, ny) in neighbors) {
                val neighborKey = "$nx,$ny"
                if (!visited.contains(neighborKey)) {
                    val neighborTile = mapTiles.find { it.x == nx && it.y == ny }
                    if (neighborTile != null && !neighborTile.isObstacle) {
                        visited.add(neighborKey)
                        queue.add(Pair(neighborTile, path + neighborTile))
                    }
                }
            }
        }
        
        return null
    }

    private var isExploring = false

    fun selectTileAndExplore(tile: MapTile) {
        if (isExploring) return
        val progress = _progressState.value ?: return
        
        if (tile.isObstacle) {
            showNotification("¡No puedes moverte ahí! ${tile.biome} es un obstáculo de terreno.")
            return
        }

        val distance = abs(progress.currentX - tile.x) + abs(progress.currentY - tile.y)
        if (distance == 0) return

        isExploring = true
        viewModelScope.launch {
            try {
                val path = if (distance == 1) {
                    listOf(tile)
                } else {
                    findPath(progress.currentX, progress.currentY, tile.x, tile.y, _proceduralMap.value)
                }

                if (path == null) {
                    showNotification("No hay un camino despejado hacia esa casilla.")
                    return@launch
                }

                for (stepTile in path) {
                    // Allow movement if on WORLD_MAP or if active combat is paused/running
                    val currentScreen = _screenState.value
                    if (currentScreen != GameScreen.WORLD_MAP && currentScreen != GameScreen.COMBAT) {
                        break
                    }
                    
                    val currentProgress = _progressState.value ?: break
                    val exploredList = GameJsonParser.listFromJson<String>(currentProgress.mapPointsExploredJson).toMutableList()
                    val tileKey = "${stepTile.x},${stepTile.y}"
                    val wasAlreadyExplored = exploredList.contains(tileKey)

                    if (!wasAlreadyExplored) {
                        exploredList.add(tileKey)
                    }

                    val hpRegVal = getHpRegenerationValue(currentProgress)
                    val mpRegVal = getMpRegenerationValue(currentProgress)
                    
                    val finalHp = minOf(currentProgress.maxHp, currentProgress.currentHp + hpRegVal)
                    val finalMp = minOf(currentProgress.maxMp, currentProgress.currentMp + mpRegVal)

                    val updatedProgress = currentProgress.copy(
                        currentX = stepTile.x,
                        currentY = stepTile.y,
                        currentHp = finalHp,
                        currentMp = finalMp,
                        mapPointsExploredJson = GameJsonParser.listToJson(exploredList)
                    )

                    repository.saveProgress(updatedProgress)
                    generateMapAround(stepTile.x, stepTile.y, GameJsonParser.listToJson(exploredList))

                    if (!wasAlreadyExplored) {
                        triggerEncounter(stepTile)
                    } else {
                        showNotification("Has viajado a: ${stepTile.biome} ($tileKey).")
                    }

                    // Pause map movement until combat concludes
                    while (_combatState.value.active) {
                        kotlinx.coroutines.delay(100)
                    }

                    // If player was defeated, they respawn at (0,0); halt path progression
                    val postCombatProgress = _progressState.value ?: break
                    if (postCombatProgress.currentX == 0 && postCombatProgress.currentY == 0 && (stepTile.x != 0 || stepTile.y != 0)) {
                        break
                    }
                    
                    kotlinx.coroutines.delay(250)
                }
            } finally {
                isExploring = false
            }
        }
    }

    private fun triggerEncounter(tile: MapTile) {
        val progress = _progressState.value ?: return
        val random = Random.Default

        when (tile.encounterType) {
            "SHRINE" -> {
                viewModelScope.launch {
                    val healHp = (progress.maxHp * 0.4).toInt()
                    val healMp = (progress.maxMp * 0.4).toInt()
                    val newHp = minOf(progress.maxHp, progress.currentHp + healHp)
                    val newMp = minOf(progress.maxMp, progress.currentMp + healMp)

                    val updated = progress.copy(
                        currentHp = newHp,
                        currentMp = newMp,
                        charGold = progress.charGold + 25
                    )
                    repository.saveProgress(updated)
                    showNotification("¡Encontraste un Santuario Ancestral! Sanas +$healHp HP, +$healMp MP y obtienes 25 de oro sagrado.")
                }
            }
            "CHEST" -> {
                viewModelScope.launch {
                    val goldGained = random.nextInt(30, 80) + (tile.levelRequirement * 10)
                    val dropWeaponChance = random.nextInt(100)
                    val lootItem = if (dropWeaponChance < 45) {
                        generateProceduralItem(tile.levelRequirement, isBoss = false)
                    } else null

                    val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
                    var notificationMsg = "¡Abriste un cofre del tesoro! Encontraste $goldGained de oro."
                    if (lootItem != null) {
                        invList.add(lootItem)
                        notificationMsg += " Y un objeto: ${lootItem.name} (${lootItem.rarity})."
                    }

                    val updated = progress.copy(
                        charGold = progress.charGold + goldGained,
                        inventoryJson = GameJsonParser.listToJson(invList)
                    )
                    val (finalProgress, equippedNames) = autoEquipProgress(updated)
                    repository.saveProgress(finalProgress)
                    if (equippedNames.isNotEmpty()) {
                        notificationMsg += " (Auto-Equipado: ${equippedNames.joinToString(", ")})"
                    }
                    showNotification(notificationMsg)
                }
            }
            "MONSTER", "BOSS" -> {
                startCombat(tile)
            }
        }
    }

    // --- COMBAT ENGINE ---
    private fun startCombat(tile: MapTile) {
        val progress = _progressState.value ?: return
        val isBoss = tile.encounterType == "BOSS"

        val monsterLevel = tile.levelRequirement
        val baseMultiplier = 1.0 + (monsterLevel * 0.15)

        val roll = Random.nextInt(100)
        val rarity = when {
            isBoss -> "LEGENDARY"
            roll >= 90 -> "CHAMPION"
            roll >= 70 -> "ELITE"
            else -> "NORMAL"
        }

        val tierMultiplier = when (rarity) {
            "LEGENDARY" -> 2.5
            "CHAMPION" -> 1.8
            "ELITE" -> 1.4
            else -> 1.0
        }

        val baseName = if (isBoss) {
            listOf("Ignis, el Señor del Volcán", "Malakor, el Corruptor", "Gorthok, el Destructor de Almas").random()
        } else {
            when (tile.biome) {
                "Pantano" -> listOf("Sanguijuela de Lodo", "Gólem de Fango", "Lobo de Ciénaga").random()
                "Bosque Oscuro" -> listOf("Araña Tejedora", "Bandido de Eldoria", "Sombra Ancestral").random()
                "Montaña" -> listOf("Trol de Piedra", "Gargantúa de las Nieves", "Cría de Grifo").random()
                "Ruinas Ancestrales" -> listOf("Gárgola Revivida", "Cazador de Tumbas", "Espectro del Rey").random()
                else -> listOf("Espantapájaros Maldito", "Duende de Bosque", "Ogro Salvaje").random()
            }
        }

        val name = when (rarity) {
            "CHAMPION" -> "👑 $baseName Campeón"
            "ELITE" -> "⭐ $baseName Élite"
            else -> baseName
        }

        val hp = if (isBoss) (180 * baseMultiplier * tierMultiplier).toInt() else (70 * baseMultiplier * tierMultiplier).toInt()
        val attack = if (isBoss) (15 * baseMultiplier * 1.5).toInt() else (8 * baseMultiplier * tierMultiplier).toInt()
        val defense = if (isBoss) (10 * baseMultiplier * 1.4).toInt() else (4 * baseMultiplier * tierMultiplier).toInt()

        val enemy = Combatant(
            name = name,
            maxHp = hp,
            currentHp = hp,
            maxMp = 40,
            currentMp = 40,
            attack = attack,
            defense = defense,
            level = monsterLevel,
            isBoss = isBoss,
            rarity = rarity
        )

        val tierLabel = when (rarity) {
            "LEGENDARY" -> "¡¡JEFE LEGENDARIO!!"
            "CHAMPION" -> "¡Un poderoso Campeón!"
            "ELITE" -> "¡Un peligroso Élite!"
            else -> "Un monstruo común"
        }

        _combatState.value = CombatState(
            active = true,
            enemy = enemy,
            playerCurrentHp = progress.currentHp,
            playerCurrentMp = progress.currentMp,
            combatLogs = listOf("¡Un salvaje ${enemy.name} (Nivel ${enemy.level}) bloquea tu camino! $tierLabel"),
            playerTurn = true,
            victory = null
        )

        _screenState.value = GameScreen.COMBAT
    }

    fun executeBasicAttack() {
        val currentCombat = _combatState.value
        val progress = _progressState.value ?: return
        if (!currentCombat.active || !currentCombat.playerTurn || currentCombat.victory != null) return

        viewModelScope.launch {
            // Calculate player damage stats based on attributes and equipment
            val weapon = GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)
            val weaponDmg = weapon?.dmgBonus ?: 0
            val weaponStr = weapon?.strBonus ?: 0
            val weaponDex = weapon?.dexBonus ?: 0

            val isRogue = progress.charClass == "Pícaro"
            val modifierStat = if (isRogue) (progress.statDex + weaponDex) else (progress.statStr + weaponStr)

            // Talent calculation
            val talentDmgMultiplier = 1.0 + (getTalentRank("t_1") * 0.04)

            val baseDmg = (modifierStat * 0.6) + weaponDmg + Random.nextInt(3, 8)
            var finalDmg = (baseDmg * talentDmgMultiplier).toInt()

            // Racial damage bonus (Orco)
            val raceDmgMult = when {
                progress.charRace == "Orco" && progress.charLevel >= 5 -> 1.25
                progress.charRace == "Orco" -> 1.10
                else -> 1.0
            }
            finalDmg = (finalDmg * raceDmgMult).toInt()

            // Armor mitigation of enemy
            val enemyDef = currentCombat.enemy?.defense ?: 0
            finalDmg = maxOf(3, finalDmg - (enemyDef / 2))

            // Critical strike chance
            val baseCrit = 5 + (progress.statDex * 0.4) + (getTalentRank("t_8") * 3)
            val raceCritBonus = when {
                progress.charRace == "Elfo" && progress.charLevel >= 5 -> 15
                progress.charRace == "Elfo" -> 5
                progress.charRace == "Humano" && progress.charLevel >= 5 -> 0 // human level 5+ has turn heal instead
                progress.charRace == "Humano" -> 5
                else -> 0
            }
            val critChance = baseCrit + raceCritBonus
            val isCrit = Random.nextInt(100) < critChance
            if (isCrit) {
                finalDmg = (finalDmg * 1.8).toInt()
            }

            // Apply to enemy
            val enemy = currentCombat.enemy ?: return@launch
            val newEnemyHp = maxOf(0, enemy.currentHp - finalDmg)
            enemy.currentHp = newEnemyHp

            val critLabel = if (isCrit) " ¡CRÍTICO!" else ""
            var log = "Atacas a ${enemy.name} e infliges $finalDmg puntos de daño físico.$critLabel"

            // Orc Level 5+ Devastador Berserker healing
            var currentPlayerHp = currentCombat.playerCurrentHp
            if (progress.charRace == "Orco" && progress.charLevel >= 5) {
                val orcHeal = (finalDmg * 0.12).toInt()
                if (orcHeal > 0) {
                    currentPlayerHp = minOf(progress.maxHp, currentPlayerHp + orcHeal)
                    log += " ¡Tu sed de sangre orca te sana +$orcHeal HP!"
                }
            }

            _combatState.value = currentCombat.copy(
                playerCurrentHp = currentPlayerHp,
                playerTurn = false,
                damageFeedbackEnemy = "-$finalDmg HP$critLabel",
                combatLogs = currentCombat.combatLogs + log,
                activeAnimation = "PLAYER_ATTACK"
            )

            // Bloodlust talent trigger (t_3)
            if (isCrit && getTalentRank("t_3") > 0) {
                val healAmt = (progress.maxHp * 0.1).toInt()
                val newHp = minOf(progress.maxHp, _combatState.value.playerCurrentHp + healAmt)
                _combatState.value = _combatState.value.copy(
                    playerCurrentHp = newHp,
                    combatLogs = _combatState.value.combatLogs + "¡Don 'Sed de Sangre' te sana $healAmt HP!"
                )
            }

            // Sync player HP updates to database
            val latestHp = _combatState.value.playerCurrentHp
            val progressAfterAttack = progress.copy(
                currentHp = latestHp
            )
            repository.saveProgress(progressAfterAttack)

            kotlinx.coroutines.delay(1000)
            _combatState.value = _combatState.value.copy(damageFeedbackEnemy = null, activeAnimation = null)

            if (newEnemyHp <= 0) {
                handleCombatVictory()
            } else {
                executeEnemyTurn()
            }
        }
    }

    fun executeSkill(skill: Skill) {
        val currentCombat = _combatState.value
        val progress = _progressState.value ?: return
        if (!currentCombat.active || !currentCombat.playerTurn || currentCombat.victory != null) return

        if (currentCombat.playerCurrentMp < skill.manaCost) {
            showNotification("¡No tienes suficiente Maná para usar ${skill.name}!")
            return
        }

        viewModelScope.launch {
            // Mana cost discount talent and Elf passive
            val manaCostDiscount = if (getTalentRank("t_6") > 0) 0.8 else 1.0
            val raceManaDiscount = if (progress.charRace == "Elfo" && progress.charLevel >= 5) 0.8 else 1.0
            val finalManaCost = (skill.manaCost * manaCostDiscount * raceManaDiscount).toInt()
            val newPlayerMp = maxOf(0, currentCombat.playerCurrentMp - finalManaCost)

            // Spells power talent
            val spellMult = 1.0 + (getTalentRank("t_4") * 0.04)

            var log = ""
            var damageFeedbackEnemy: String? = null
            var damageFeedbackPlayer: String? = null

            var currentEnemyHp = currentCombat.enemy?.currentHp ?: 0
            var currentPlayerHp = currentCombat.playerCurrentHp

            if (skill.damageMultiplier > 0.0) {
                val weapon = GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)
                val isMagic = progress.charClass == "Mago" || progress.charClass == "Clérigo"
                val statModifier = if (isMagic) (progress.statInt + (weapon?.intBonus ?: 0))
                                    else if (progress.charClass == "Pícaro") (progress.statDex + (weapon?.dexBonus ?: 0))
                                    else (progress.statStr + (weapon?.strBonus ?: 0))

                val baseSkillDmg = (statModifier * 0.9) + (weapon?.dmgBonus ?: 0) + Random.nextInt(5, 12)
                
                // Orc damage passive
                val raceDmgMult = when {
                    progress.charRace == "Orco" && progress.charLevel >= 5 -> 1.25
                    progress.charRace == "Orco" -> 1.10
                    else -> 1.0
                }
                var finalSkillDmg = (baseSkillDmg * skill.damageMultiplier * spellMult * raceDmgMult).toInt()

                // Defense mitigation
                val enemyDef = currentCombat.enemy?.defense ?: 0
                finalSkillDmg = maxOf(4, finalSkillDmg - (enemyDef / 2))

                currentEnemyHp = maxOf(0, currentEnemyHp - finalSkillDmg)
                currentCombat.enemy?.currentHp = currentEnemyHp
                damageFeedbackEnemy = "-$finalSkillDmg HP (${skill.name})"
                log = "Usas ${skill.name} contra ${currentCombat.enemy?.name} e infliges $finalSkillDmg de daño."
            }

            if (skill.healingMultiplier > 0.0) {
                val weapon = GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)
                val intBonus = progress.statInt + (weapon?.intBonus ?: 0)
                val healAmt = ((intBonus * 1.5) * skill.healingMultiplier * spellMult).toInt()

                currentPlayerHp = minOf(progress.maxHp, currentPlayerHp + healAmt)
                damageFeedbackPlayer = "+$healAmt HP"
                val healLog = "Usas ${skill.name} y restauras $healAmt puntos de salud."
                log = if (log.isEmpty()) healLog else "$log $healLog"
            }

            // Synchronize player health and mana to database
            val progressAfterSkill = progress.copy(
                currentHp = currentPlayerHp,
                currentMp = newPlayerMp
            )
            repository.saveProgress(progressAfterSkill)

            _combatState.value = currentCombat.copy(
                playerCurrentHp = currentPlayerHp,
                playerCurrentMp = newPlayerMp,
                playerTurn = false,
                damageFeedbackEnemy = damageFeedbackEnemy,
                damageFeedbackPlayer = damageFeedbackPlayer,
                combatLogs = currentCombat.combatLogs + log,
                activeAnimation = if (skill.healingMultiplier > 0.0) "PLAYER_HEAL" else "PLAYER_MAGIC"
            )

            kotlinx.coroutines.delay(1000)
            _combatState.value = _combatState.value.copy(damageFeedbackEnemy = null, damageFeedbackPlayer = null, activeAnimation = null)

            if (currentEnemyHp <= 0) {
                handleCombatVictory()
            } else {
                executeEnemyTurn()
            }
        }
    }

    fun usePotionCombat() {
        val currentCombat = _combatState.value
        val progress = _progressState.value ?: return
        if (!currentCombat.active || !currentCombat.playerTurn || currentCombat.victory != null) return

        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        val potionIndex = invList.indexOfFirst { it.type == "POTION" }

        if (potionIndex == -1) {
            showNotification("¡No tienes pociones en tu inventario!")
            return
        }

        viewModelScope.launch {
            invList.removeAt(potionIndex)
            val healAmount = (progress.maxHp * 0.5).toInt()
            val manaAmount = (progress.maxMp * 0.5).toInt()

            val newHp = minOf(progress.maxHp, currentCombat.playerCurrentHp + healAmount)
            val newMp = minOf(progress.maxMp, currentCombat.playerCurrentMp + manaAmount)

            // Save inventory reduction and restored HP/MP
            val updatedProgress = progress.copy(
                currentHp = newHp,
                currentMp = newMp,
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            repository.saveProgress(updatedProgress)

            _combatState.value = currentCombat.copy(
                playerCurrentHp = newHp,
                playerCurrentMp = newMp,
                playerTurn = false,
                damageFeedbackPlayer = "+$healAmount HP / +$manaAmount MP",
                combatLogs = currentCombat.combatLogs + "Bebes una poción rejuvenecedora: recuperas salud y maná.",
                activeAnimation = "PLAYER_POTION"
            )

            kotlinx.coroutines.delay(1000)
            _combatState.value = _combatState.value.copy(damageFeedbackPlayer = null, activeAnimation = null)

            executeEnemyTurn()
        }
    }

    private fun executeEnemyTurn() {
        val currentCombat = _combatState.value
        val progress = _progressState.value ?: return
        if (currentCombat.victory != null || currentCombat.enemy == null) return

        viewModelScope.launch {
            kotlinx.coroutines.delay(800)

            val enemy = currentCombat.enemy
            val enemyAtk = enemy.attack
            val baseDmg = enemyAtk + Random.nextInt(-2, 3)

            // Player defense calculation (attributes + equipment)
            val armor = GameJsonParser.fromJson<Item>(progress.equippedArmorJson)
            val shield = GameJsonParser.fromJson<Item>(progress.equippedShieldJson)
            val ring = GameJsonParser.fromJson<Item>(progress.equippedRingJson)

            val totalDefBonus = (armor?.defBonus ?: 0) + (shield?.defBonus ?: 0) + (ring?.defBonus ?: 0)
            var playerDefense = (progress.statCon * 0.4).toInt() + totalDefBonus

            // Dwarf defense bonus
            if (progress.charRace == "Enano") {
                playerDefense += if (progress.charLevel >= 5) 10 else 5
            }

            // Dodge check
            val dodgeChance = 3 + (progress.statDex * 0.3) + (getTalentRank("t_7") * 4)
            val dodged = Random.nextInt(100) < dodgeChance

            val isSkillUsed = Random.nextInt(100) < 35
            var finalDmg = 0
            var feedbackText = ""
            var logMsg = ""
            var skillTypeUsed = "" // "SLASH", "DRAIN", "THORNS", "DRAIN_MP"

            if (isSkillUsed && !dodged) {
                val skillRoll = Random.nextInt(4)
                when (skillRoll) {
                    0 -> { // Slash
                        skillTypeUsed = "SLASH"
                        val skillDmg = (baseDmg * 1.5).toInt()
                        finalDmg = maxOf(2, skillDmg - (playerDefense / 2))
                        if (getTalentRank("t_6") > 0) finalDmg = (finalDmg * 0.85).toInt()

                        logMsg = "${enemy.name} usa [Corte Sanguinolento] e inflige $finalDmg de daño físico crítico!"
                        feedbackText = "-$finalDmg HP 💥"
                    }
                    1 -> { // Drain
                        skillTypeUsed = "DRAIN"
                        finalDmg = maxOf(1, baseDmg - (playerDefense / 2))
                        if (getTalentRank("t_6") > 0) finalDmg = (finalDmg * 0.85).toInt()

                        val drainHeal = (finalDmg * 0.50).toInt()
                        enemy.currentHp = minOf(enemy.maxHp, enemy.currentHp + drainHeal)

                        logMsg = "${enemy.name} usa [Drenaje de Vida] e inflige $finalDmg de daño y se drena +$drainHeal de salud!"
                        feedbackText = "-$finalDmg HP 🩸"
                    }
                    2 -> { // Thorns
                        skillTypeUsed = "THORNS"
                        finalDmg = maxOf(1, baseDmg - (playerDefense / 2))
                        if (getTalentRank("t_6") > 0) finalDmg = (finalDmg * 0.85).toInt()

                        logMsg = "${enemy.name} activa [Piel de Espinas], golpeando por $finalDmg y reforzando su coraza."
                        feedbackText = "-$finalDmg HP 🛡️"
                    }
                    else -> { // Drain MP
                        skillTypeUsed = "DRAIN_MP"
                        finalDmg = maxOf(1, baseDmg - (playerDefense / 2))
                        if (getTalentRank("t_6") > 0) finalDmg = (finalDmg * 0.85).toInt()

                        logMsg = "${enemy.name} lanza [Maldición de Maná] e inflige $finalDmg de daño y te drena 12 de Maná."
                        feedbackText = "-$finalDmg HP 🧪"
                    }
                }
            } else {
                finalDmg = maxOf(1, baseDmg - (playerDefense / 2))
                if (getTalentRank("t_6") > 0) {
                    finalDmg = (finalDmg * 0.85).toInt()
                }

                if (dodged) {
                    finalDmg = 0
                    feedbackText = "¡ESQUIVADO!"
                    logMsg = "¡Esquivas con agilidad el ataque de ${enemy.name}!"
                } else {
                    feedbackText = "-$finalDmg HP"
                    logMsg = "${enemy.name} te ataca e inflige $finalDmg puntos de daño físico."
                }
            }

            var newPlayerMp = currentCombat.playerCurrentMp
            if (skillTypeUsed == "DRAIN_MP" && !dodged) {
                newPlayerMp = maxOf(0, newPlayerMp - 12)
            }

            val newHp = maxOf(0, currentCombat.playerCurrentHp - finalDmg)

            // Dwarf Level 5+ Reflect passive
            var enemyHpAfterReflect = enemy.currentHp
            var reflectLog = ""
            if (progress.charRace == "Enano" && progress.charLevel >= 5 && finalDmg > 0 && !dodged) {
                val damageReflected = maxOf(1, (finalDmg * 0.10).toInt())
                enemyHpAfterReflect = maxOf(0, enemy.currentHp - damageReflected)
                enemy.currentHp = enemyHpAfterReflect
                reflectLog = " ¡Tu Escudo Rúnico devuelve $damageReflected de daño!"
            }

            // Human Level 5+ Turn Heal passive
            var afterHealHp = newHp
            var humanHealLog = ""
            if (progress.charRace == "Humano" && progress.charLevel >= 5 && newHp > 0) {
                val humanHeal = (progress.maxHp * 0.08).toInt()
                afterHealHp = minOf(progress.maxHp, newHp + humanHeal)
                humanHealLog = " ¡Tu don de Campeón Imperial te sana +$humanHeal HP!"
            }

            if (reflectLog.isNotEmpty()) logMsg += reflectLog
            if (humanHealLog.isNotEmpty()) logMsg += humanHealLog

            val regenHpVal = getHpRegenerationValue(progress)
            val regenMpVal = getMpRegenerationValue(progress)
            
            val afterRegenHp = if (afterHealHp > 0) minOf(progress.maxHp, afterHealHp + regenHpVal) else 0
            val afterRegenMp = if (afterHealHp > 0) minOf(progress.maxMp, newPlayerMp + regenMpVal) else newPlayerMp
            
            var updatedLogMsg = logMsg
            if (afterHealHp > 0) {
                updatedLogMsg += " ¡Regeneras +$regenHpVal HP y +$regenMpVal MP al inicio de tu turno!"
            }

            // Synchronize with database so stats screens and HUDs are updated
            val progressAfterEnemyTurn = progress.copy(
                currentHp = afterRegenHp,
                currentMp = afterRegenMp
            )
            repository.saveProgress(progressAfterEnemyTurn)

            _combatState.value = currentCombat.copy(
                playerCurrentHp = afterRegenHp,
                playerCurrentMp = afterRegenMp,
                playerTurn = true,
                damageFeedbackPlayer = feedbackText,
                combatLogs = currentCombat.combatLogs + updatedLogMsg,
                activeAnimation = if (isSkillUsed && !dodged) "ENEMY_SKILL" else "ENEMY_ATTACK"
            )

            kotlinx.coroutines.delay(1000)
            _combatState.value = _combatState.value.copy(damageFeedbackPlayer = null, activeAnimation = null)

            if (newHp <= 0) {
                handleCombatDefeat()
            }
        }
    }

    private fun handleCombatVictory() {
        val currentCombat = _combatState.value
        val progress = _progressState.value ?: return
        val enemy = currentCombat.enemy ?: return

        // Drop rates calibration
        val baseGoldReward = enemy.level * Random.nextInt(20, 35) + (if (enemy.isBoss) 200 else 0)
        val baseExpReward = enemy.level * 40 + (if (enemy.isBoss) 200 else 0)

        // Tier multipliers for rewards
        val rewardMultiplier = when (enemy.rarity) {
            "LEGENDARY" -> 3.5
            "CHAMPION" -> 2.2
            "ELITE" -> 1.5
            else -> 1.0
        }

        val goldReward = (baseGoldReward * rewardMultiplier).toInt()
        val expReward = (baseExpReward * rewardMultiplier).toInt()

        // Drop generation rate calibrator
        val isBoss = enemy.isBoss

        // Custom drop rate calculation and Human racial passive (+10% gold / +15% gold if level 5+)
        val goldTalentMultiplier = 1.0 + (getTalentRank("t_9") * 0.20)
        val raceGoldMultiplier = when {
            progress.charRace == "Humano" && progress.charLevel >= 5 -> 1.15
            progress.charRace == "Humano" -> 1.10
            else -> 1.0
        }
        val finalGoldReward = (goldReward * goldTalentMultiplier * raceGoldMultiplier).toInt()

        // Calibrated drop rates
        val legendaryThreshold = 100 - _dropRateLegendary.value
        val epicThreshold = legendaryThreshold - _dropRateEpic.value
        val rareThreshold = epicThreshold - _dropRateRare.value

        var droppedItem: Item? = null
        val shouldDrop = when (enemy.rarity) {
            "LEGENDARY" -> true
            "CHAMPION" -> Random.nextInt(100) < 85 // 85% chance
            "ELITE" -> Random.nextInt(100) < 60 // 60% chance
            else -> Random.nextInt(100) < 40 // 40% chance
        }

        if (shouldDrop) {
            val rarity = when (enemy.rarity) {
                "LEGENDARY" -> if (Random.nextInt(100) < 50) "LEGENDARY" else "EPIC"
                "CHAMPION" -> if (Random.nextInt(100) < 30) "LEGENDARY" else "EPIC"
                "ELITE" -> if (Random.nextInt(100) < 15) "LEGENDARY" else if (Random.nextInt(100) < 45) "EPIC" else "RARE"
                else -> {
                    val roll = Random.nextInt(100)
                    when {
                        roll >= legendaryThreshold -> "LEGENDARY"
                        roll >= epicThreshold -> "EPIC"
                        roll >= rareThreshold -> "RARE"
                        else -> "COMMON"
                    }
                }
            }
            droppedItem = generateProceduralItem(enemy.level, isBoss = isBoss, rarityPreset = rarity)
        }

        viewModelScope.launch {
            val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
            if (droppedItem != null) {
                invList.add(droppedItem)
            }

            var currentExp = progress.charExp + expReward
            var currentLevel = progress.charLevel
            var nextLevelExp = currentLevel * 100
            var didLevelUp = false
            var addedStatPoints = 0
            var addedTalentPoints = 0

            var pStr = progress.statStr
            var pDex = progress.statDex
            var pInt = progress.statInt
            var pCon = progress.statCon
            var pMaxHp = progress.maxHp
            var pMaxMp = progress.maxMp

            while (currentExp >= nextLevelExp) {
                currentExp -= nextLevelExp
                currentLevel += 1
                nextLevelExp = currentLevel * 100
                didLevelUp = true
                addedStatPoints += 5
                addedTalentPoints += 1

                // Automatic incremental stats scaling
                pStr += 1
                pDex += 1
                pInt += 1
                pCon += 1
                pMaxHp = pCon * 12
                pMaxMp = pInt * 6
            }

            val updatedProgress = progress.copy(
                charLevel = currentLevel,
                charExp = currentExp,
                charGold = progress.charGold + finalGoldReward,
                statStr = pStr,
                statDex = pDex,
                statInt = pInt,
                statCon = pCon,
                maxHp = pMaxHp,
                maxMp = pMaxMp,
                currentHp = if (didLevelUp) pMaxHp else currentCombat.playerCurrentHp,
                currentMp = if (didLevelUp) pMaxMp else currentCombat.playerCurrentMp,
                statPointsAvailable = progress.statPointsAvailable + addedStatPoints,
                talentPointsAvailable = progress.talentPointsAvailable + addedTalentPoints,
                inventoryJson = GameJsonParser.listToJson(invList)
            )

            val (finalProgress, equippedNames) = autoEquipProgress(updatedProgress)
            repository.saveProgress(finalProgress)

            var victoryLogs = "¡Has derrotado a ${enemy.name}! Obtienes $expReward EXP y $finalGoldReward monedas de oro."
            if (didLevelUp) {
                victoryLogs += " ¡¡SUBISTE DE NIVEL!! Ahora eres Nivel $currentLevel. Ganas +$addedStatPoints atributos y +$addedTalentPoints talentos."
            }
            if (droppedItem != null) {
                victoryLogs += " Encontraste: ${droppedItem.name} [${droppedItem.rarity}]"
            }
            if (equippedNames.isNotEmpty()) {
                victoryLogs += " ¡Auto-equipado: ${equippedNames.joinToString(", ")}!"
            }

            _combatState.value = currentCombat.copy(
                victory = true,
                lootDropped = droppedItem,
                expGained = expReward,
                goldGained = finalGoldReward,
                combatLogs = currentCombat.combatLogs + victoryLogs
            )

            if (_isAutoCombat.value || _isAutoNavigation.value) {
                kotlinx.coroutines.delay(2500)
                if (_combatState.value.victory == true) {
                    exitCombatScreen()
                }
            }
        }
    }

    private fun handleCombatDefeat() {
        val currentCombat = _combatState.value
        val progress = _progressState.value ?: return

        viewModelScope.launch {
            // Revive at starting safe room with gold penalty of 15%
            val penaltyGold = (progress.charGold * 0.15).toInt()
            val newGold = maxOf(0, progress.charGold - penaltyGold)

            val updatedProgress = progress.copy(
                currentHp = progress.maxHp,
                currentMp = progress.maxMp,
                charGold = newGold,
                currentX = 0,
                currentY = 0
            )

            repository.saveProgress(updatedProgress)
            _combatState.value = currentCombat.copy(
                victory = false,
                combatLogs = currentCombat.combatLogs + "Has caído en combate... Te despiertas exhausto en el Santuario Inicial. Perdiste $penaltyGold monedas de oro de penalización."
            )

            if (_isAutoCombat.value || _isAutoNavigation.value) {
                kotlinx.coroutines.delay(2500)
                if (_combatState.value.victory == false) {
                    exitCombatScreen()
                }
            }
        }
    }

    fun exitCombatScreen() {
        _combatState.value = CombatState()
        val progress = _progressState.value
        if (progress != null) {
            generateMapAround(progress.currentX, progress.currentY, progress.mapPointsExploredJson)
        }
        _screenState.value = GameScreen.WORLD_MAP
    }

    fun fleeCombat() {
        val currentCombat = _combatState.value
        if (!currentCombat.active || !currentCombat.playerTurn || currentCombat.victory != null) return

        viewModelScope.launch {
            val success = Random.nextInt(100) < 55 // 55% flee chance
            if (success) {
                val progress = _progressState.value
                if (progress != null) {
                    repository.saveProgress(progress.copy(
                        currentHp = currentCombat.playerCurrentHp,
                        currentMp = currentCombat.playerCurrentMp
                    ))
                }
                _combatState.value = currentCombat.copy(
                    active = false,
                    victory = null
                )
                showNotification("¡Lograste huir exitosamente!")
                _screenState.value = GameScreen.WORLD_MAP
            } else {
                _combatState.value = currentCombat.copy(
                    playerTurn = false,
                    combatLogs = currentCombat.combatLogs + "Intentas huir pero el enemigo te bloquea el paso agresivamente."
                )
                executeEnemyTurn()
            }
        }
    }

    // --- PROCEDURAL ITEMS DROP ENGINE ---
    fun getAllEquippedItems(progress: GameProgress): List<Item> {
        val items = mutableListOf<Item>()
        GameJsonParser.fromJson<Item>(progress.equippedHelmetJson)?.let { items.add(it) }
        GameJsonParser.fromJson<Item>(progress.equippedWingsJson)?.let { items.add(it) }
        GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)?.let { items.add(it) }
        GameJsonParser.fromJson<Item>(progress.equippedShieldJson)?.let { items.add(it) }
        GameJsonParser.fromJson<Item>(progress.equippedArmorJson)?.let { items.add(it) }
        GameJsonParser.fromJson<Item>(progress.equippedGlovesJson)?.let { items.add(it) }
        GameJsonParser.fromJson<Item>(progress.equippedBootsJson)?.let { items.add(it) }
        GameJsonParser.fromJson<Item>(progress.equippedRingJson)?.let { items.add(it) }
        GameJsonParser.fromJson<Item>(progress.equippedEarringJson)?.let { items.add(it) }
        GameJsonParser.fromJson<Item>(progress.equippedRelicJson)?.let { items.add(it) }
        return items
    }

    fun getHpRegenerationValue(progress: GameProgress): Int {
        val equipped = getAllEquippedItems(progress)
        val itemRegen = equipped.sumOf { it.hpRegen }
        val baseRegen = 2 + (progress.statCon * 0.15).toInt()
        return baseRegen + itemRegen
    }

    fun getMpRegenerationValue(progress: GameProgress): Int {
        val equipped = getAllEquippedItems(progress)
        val itemRegen = equipped.sumOf { it.intBonus } / 4
        val baseRegen = 1 + (progress.statInt * 0.10).toInt()
        return baseRegen + itemRegen
    }

    private fun generateProceduralItem(level: Int, isBoss: Boolean, rarityPreset: String? = null): Item {
        val r = Random.Default
        val rarity = rarityPreset?.uppercase() ?: if (isBoss) {
            listOf("LEGENDARIO", "ARCANO", "UNIVERSAL").random()
        } else {
            val roll = r.nextInt(100)
            when {
                roll >= 99 -> "UNIVERSAL"
                roll >= 95 -> "ARCANO"
                roll >= 83 -> "LEGENDARIO"
                roll >= 65 -> "ÉPICO"
                roll >= 40 -> "RARO"
                else -> "COMÚN"
            }
        }

        val type = listOf("HELMET", "WINGS", "WEAPON", "SHIELD", "ARMOR", "GLOVES", "BOOTS", "RING", "EARRING", "RELIC").random()
        val prefix = when (rarity) {
            "UNIVERSAL" -> listOf("Celestial", "Infinito", "Omnipresente", "Sideral", "Cosmológico").random()
            "ARCANO" -> listOf("Secreto", "Esotérico", "Místico", "Inescrutable", "Eldritch").random()
            "LEGENDARIO", "LEGENDARY" -> listOf("Ancestral", "Eterno", "Rúnico", "de las Sombras", "Fénix").random()
            "ÉPICO", "EPIC" -> listOf("Forjado", "Infundido", "Espectral", "Glacial", "Ígneo").random()
            "RARO", "RARE" -> listOf("Reforzado", "Místico", "Pulido", "Sólido").random()
            else -> listOf("Común", "Desgastado", "Mellado", "Básico").random()
        }

        val name = when (type) {
            "HELMET" -> listOf("Casco", "Yelmo", "Corona").random() + " $prefix"
            "WINGS" -> listOf("Alas Archangélicas", "Plumas Celestiales", "Manto de Alas").random() + " $prefix"
            "WEAPON" -> listOf("Mandoble", "Báculo", "Daga", "Espada").random() + " $prefix"
            "SHIELD" -> listOf("Escudo", "Baluarte", "Pavés").random() + " $prefix"
            "ARMOR" -> listOf("Pechera de Placas", "Túnica Arcana", "Armadura de Cuero").random() + " $prefix"
            "GLOVES" -> listOf("Guantes", "Manoplas", "Manoplas de Placas").random() + " $prefix"
            "BOOTS" -> listOf("Botas", "Grebas", "Botas de Cuero").random() + " $prefix"
            "RING" -> "Anillo $prefix"
            "EARRING" -> listOf("Pendiente", "Zarcillo", "Pendiente de Gemas").random() + " $prefix"
            else -> listOf("Reliquia", "Orbe", "Tótem").random() + " $prefix"
        }

        val multiplier = when (rarity) {
            "UNIVERSAL" -> 6
            "ARCANO" -> 5
            "LEGENDARIO", "LEGENDARY" -> 4
            "ÉPICO", "EPIC" -> 3
            "RARO", "RARE" -> 2
            else -> 1
        }
        val budget = level * multiplier + Random.nextInt(2, 6)

        var s = 0
        var d = 0
        var i = 0
        var c = 0
        var dmg = 0
        var def = 0
        var hpReg = 0

        val statsToRoll = listOf("STR", "DEX", "INT", "CON")
        for (step in 1..budget) {
            val picked = statsToRoll.random()
            when (picked) {
                "STR" -> s += 1
                "DEX" -> d += 1
                "INT" -> i += 1
                "CON" -> c += 1
            }
        }

        if (type == "WEAPON") dmg = level * multiplier + r.nextInt(5, 10)
        if (type == "ARMOR" || type == "HELMET" || type == "GLOVES" || type == "BOOTS") def = level * multiplier + r.nextInt(3, 8)
        if (type == "SHIELD") def = level * multiplier + r.nextInt(2, 6)
        if (type == "WINGS" || type == "RELIC") dmg = level * multiplier + r.nextInt(3, 7)

        if (type == "RING" || type == "EARRING" || rarity == "ÉPICO" || rarity == "EPIC" || rarity == "LEGENDARIO" || rarity == "LEGENDARY" || rarity == "ARCANO" || rarity == "UNIVERSAL") {
            if (r.nextInt(100) < 55) {
                hpReg = level * multiplier / 2 + r.nextInt(1, 4)
            }
        }

        val desc = when (rarity) {
            "UNIVERSAL" -> "Una reliquia cósmica que resuena con la energía pura de todo el multiverso."
            "ARCANO" -> "Un objeto cubierto de misteriosos sigilos rúnicos imbuidos de magia olvidada."
            "LEGENDARIO", "LEGENDARY" -> "Una reliquia imbuida con el alma de héroes del pasado."
            "ÉPICO", "EPIC" -> "Una pieza exquisitamente forjada para el combate pesado."
            "RARO", "RARE" -> "Este objeto vibra con un leve y agradable fulgor mágico."
            else -> "Equipo medieval estándar para sobrevivir al día a día."
        }

        val imageResName = when (type) {
            "HELMET" -> "img_item_helmet_1784658214656"
            "WINGS" -> "img_item_wings_1784658202673"
            "WEAPON" -> {
                when {
                    name.contains("Báculo", ignoreCase = true) -> "img_item_staff_1784593558118"
                    name.contains("Daga", ignoreCase = true) -> "img_item_dagger_1784593567531"
                    else -> "img_item_sword_1784593548868"
                }
            }
            "ARMOR" -> {
                when {
                    name.contains("Túnica", ignoreCase = true) -> "img_item_robe_1784593587883"
                    else -> "img_item_plate_1784593577913"
                }
            }
            "GLOVES" -> "img_item_gloves_1784658226142"
            "BOOTS" -> "img_item_boots_1784658239207"
            "RING" -> "img_item_ring_1784593597914"
            "EARRING" -> "img_item_earring_1784658263366"
            "RELIC" -> "img_item_relic_1784658251007"
            "SHIELD" -> "img_item_shield_1784593608106"
            else -> "img_item_potion_1784593618142"
        }

        return Item(
            id = "item_${System.currentTimeMillis()}_${r.nextInt(1000)}",
            name = name,
            type = type,
            rarity = rarity,
            strBonus = s,
            dexBonus = d,
            intBonus = i,
            conBonus = c,
            dmgBonus = dmg,
            defBonus = def,
            hpRegen = hpReg,
            description = desc,
            itemLevel = level,
            imageResName = imageResName
        )
    }

    // --- CHARACTER INVENTORY & EQUIP MANAGEMENT ---
    fun autoEquipProgress(progress: GameProgress): Pair<GameProgress, List<String>> {
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        val charClass = progress.charClass

        fun getItemScore(item: Item): Int {
            val baseScore = (item.dmgBonus * 3) + (item.defBonus * 3) + (item.hpRegen * 4) +
                    (item.strBonus * 2) + (item.dexBonus * 2) + (item.intBonus * 2) + (item.conBonus * 2)
            val classBonus = when (charClass) {
                "Guerrero" -> item.strBonus * 2 + item.conBonus
                "Pícaro" -> item.dexBonus * 2 + item.strBonus
                "Mago" -> item.intBonus * 3
                "Clérigo" -> item.intBonus * 2 + item.conBonus
                else -> 0
            }
            val rarityScore = when (item.rarity.uppercase()) {
                "UNIVERSAL" -> 160
                "ARCANO" -> 100
                "LEGENDARIO", "LEGENDARY" -> 60
                "ÉPICO", "EPIC" -> 30
                "RARO", "RARE" -> 10
                else -> 0
            }
            return baseScore + classBonus + rarityScore
        }

        var updatedProgress = progress
        val slots = listOf("HELMET", "WINGS", "WEAPON", "SHIELD", "ARMOR", "GLOVES", "BOOTS", "RING", "EARRING", "RELIC")
        val equippedNames = mutableListOf<String>()

        for (slot in slots) {
            val currentEquippedJson = when (slot) {
                "HELMET" -> updatedProgress.equippedHelmetJson
                "WINGS" -> updatedProgress.equippedWingsJson
                "WEAPON" -> updatedProgress.equippedWeaponJson
                "SHIELD" -> updatedProgress.equippedShieldJson
                "ARMOR" -> updatedProgress.equippedArmorJson
                "GLOVES" -> updatedProgress.equippedGlovesJson
                "BOOTS" -> updatedProgress.equippedBootsJson
                "RING" -> updatedProgress.equippedRingJson
                "EARRING" -> updatedProgress.equippedEarringJson
                "RELIC" -> updatedProgress.equippedRelicJson
                else -> ""
            }
            val currentEquipped = if (currentEquippedJson.isNotEmpty()) {
                GameJsonParser.fromJson<Item>(currentEquippedJson)
            } else null

            val currentScore = currentEquipped?.let { getItemScore(it) } ?: -1

            val slotItemsInInv = invList.filter { it.type == slot && it.itemLevel <= progress.charLevel }
            val bestItemInInv = slotItemsInInv.maxByOrNull { getItemScore(it) }

            if (bestItemInInv != null) {
                val bestScore = getItemScore(bestItemInInv)
                if (bestScore > currentScore) {
                    invList.remove(bestItemInInv)
                    if (currentEquipped != null) {
                        invList.add(currentEquipped)
                    }
                    updatedProgress = when (slot) {
                        "HELMET" -> updatedProgress.copy(equippedHelmetJson = GameJsonParser.toJson(bestItemInInv))
                        "WINGS" -> updatedProgress.copy(equippedWingsJson = GameJsonParser.toJson(bestItemInInv))
                        "WEAPON" -> updatedProgress.copy(equippedWeaponJson = GameJsonParser.toJson(bestItemInInv))
                        "SHIELD" -> updatedProgress.copy(equippedShieldJson = GameJsonParser.toJson(bestItemInInv))
                        "ARMOR" -> updatedProgress.copy(equippedArmorJson = GameJsonParser.toJson(bestItemInInv))
                        "GLOVES" -> updatedProgress.copy(equippedGlovesJson = GameJsonParser.toJson(bestItemInInv))
                        "BOOTS" -> updatedProgress.copy(equippedBootsJson = GameJsonParser.toJson(bestItemInInv))
                        "RING" -> updatedProgress.copy(equippedRingJson = GameJsonParser.toJson(bestItemInInv))
                        "EARRING" -> updatedProgress.copy(equippedEarringJson = GameJsonParser.toJson(bestItemInInv))
                        "RELIC" -> updatedProgress.copy(equippedRelicJson = GameJsonParser.toJson(bestItemInInv))
                        else -> updatedProgress
                    }
                    equippedNames.add(bestItemInInv.name)
                }
            }
        }

        val finalProgress = updatedProgress.copy(
            inventoryJson = GameJsonParser.listToJson(invList)
        )
        return Pair(finalProgress, equippedNames)
    }

    fun autoEquip() {
        val progress = _progressState.value ?: return
        val (finalProgress, equippedNames) = autoEquipProgress(progress)
        if (equippedNames.isNotEmpty()) {
            viewModelScope.launch {
                repository.saveProgress(finalProgress)
                showNotification("Equipamiento Automático: Se equipó ${equippedNames.joinToString(", ")}")
            }
        } else {
            showNotification("Ya tienes equipado el mejor equipamiento disponible para tu nivel.")
        }
    }

    fun equipItem(item: Item) {
        val progress = _progressState.value ?: return
        if (item.itemLevel > progress.charLevel) {
            showNotification("¡No puedes equipar esto! Requiere nivel de personaje ${item.itemLevel} (Tu nivel: ${progress.charLevel}).")
            return
        }
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()

        viewModelScope.launch {
            invList.remove(item)

            var updatedProgress = progress
            when (item.type) {
                "HELMET" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedHelmetJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedHelmetJson = GameJsonParser.toJson(item))
                }
                "WINGS" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedWingsJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedWingsJson = GameJsonParser.toJson(item))
                }
                "WEAPON" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedWeaponJson = GameJsonParser.toJson(item))
                }
                "SHIELD" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedShieldJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedShieldJson = GameJsonParser.toJson(item))
                }
                "ARMOR" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedArmorJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedArmorJson = GameJsonParser.toJson(item))
                }
                "GLOVES" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedGlovesJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedGlovesJson = GameJsonParser.toJson(item))
                }
                "BOOTS" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedBootsJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedBootsJson = GameJsonParser.toJson(item))
                }
                "RING" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedRingJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedRingJson = GameJsonParser.toJson(item))
                }
                "EARRING" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedEarringJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedEarringJson = GameJsonParser.toJson(item))
                }
                "RELIC" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedRelicJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedRelicJson = GameJsonParser.toJson(item))
                }
            }

            val finalProgress = updatedProgress.copy(
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            repository.saveProgress(finalProgress)
            showNotification("¡Equipaste exitosamente: ${item.name}!")
        }
    }

    fun unequipItem(slot: String) {
        val progress = _progressState.value ?: return
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()

        if (invList.size >= 12) {
            showNotification("¡No tienes espacio suficiente en el inventario para desequipar!")
            return
        }

        viewModelScope.launch {
            var updated = progress
            var itemToStore: Item? = null

            when (slot) {
                "HELMET" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedHelmetJson)
                    updated = updated.copy(equippedHelmetJson = "")
                }
                "WINGS" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedWingsJson)
                    updated = updated.copy(equippedWingsJson = "")
                }
                "WEAPON" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)
                    updated = updated.copy(equippedWeaponJson = "")
                }
                "SHIELD" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedShieldJson)
                    updated = updated.copy(equippedShieldJson = "")
                }
                "ARMOR" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedArmorJson)
                    updated = updated.copy(equippedArmorJson = "")
                }
                "GLOVES" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedGlovesJson)
                    updated = updated.copy(equippedGlovesJson = "")
                }
                "BOOTS" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedBootsJson)
                    updated = updated.copy(equippedBootsJson = "")
                }
                "RING" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedRingJson)
                    updated = updated.copy(equippedRingJson = "")
                }
                "EARRING" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedEarringJson)
                    updated = updated.copy(equippedEarringJson = "")
                }
                "RELIC" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedRelicJson)
                    updated = updated.copy(equippedRelicJson = "")
                }
            }

            if (itemToStore != null) {
                invList.add(itemToStore)
                val finalProgress = updated.copy(inventoryJson = GameJsonParser.listToJson(invList))
                repository.saveProgress(finalProgress)
                showNotification("Desequipaste ${itemToStore.name} al inventario.")
            }
        }
    }

    fun discardItem(item: Item) {
        val progress = _progressState.value ?: return
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()

        viewModelScope.launch {
            invList.remove(item)
            val updated = progress.copy(inventoryJson = GameJsonParser.listToJson(invList))
            repository.saveProgress(updated)
            showNotification("Descartaste el objeto: ${item.name}")
        }
    }

    fun buyPotion() {
        val progress = _progressState.value ?: return
        if (progress.charGold < 40) {
            showNotification("¡No tienes suficiente oro! La poción cuesta 40 monedas.")
            return
        }

        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        if (invList.size >= 12) {
            showNotification("¡Tu inventario está completamente lleno!")
            return
        }

        viewModelScope.launch {
            val potion = Item(
                id = "potion_${System.currentTimeMillis()}",
                name = "Poción Rejuvenecedora",
                type = "POTION",
                rarity = "COMÚN",
                description = "Restaura instantáneamente el 50% de HP y Maná en combate.",
                itemLevel = 1,
                imageResName = "img_item_potion_1784593618142"
            )
            invList.add(potion)

            val updated = progress.copy(
                charGold = progress.charGold - 40,
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            repository.saveProgress(updated)
            showNotification("Compraste 1 Poción por 40 monedas de oro.")
        }
    }

    // --- TALENTS ALLOCATION ---
    fun allocateTalentPoint(talentId: String) {
        val progress = _progressState.value ?: return
        if (progress.talentPointsAvailable <= 0) {
            showNotification("No tienes puntos de talento disponibles.")
            return
        }

        val talentList = GameJsonParser.listFromJson<Talent>(progress.talentsJson).toMutableList()
        val index = talentList.indexOfFirst { it.id == talentId }
        if (index == -1) return

        val talent = talentList[index]
        if (talent.currentRank >= talent.maxRank) {
            showNotification("Este talento ya está al máximo rango.")
            return
        }

        // Prerequisite check
        if (talent.prerequisiteId != null) {
            val prereq = talentList.find { it.id == talent.prerequisiteId }
            if (prereq == null || prereq.currentRank < prereq.maxRank) {
                showNotification("Requiere tener el talento '${prereq?.name ?: ""}' al máximo nivel primero.")
                return
            }
        }

        viewModelScope.launch {
            val updatedTalent = talent.copy(currentRank = talent.currentRank + 1)
            talentList[index] = updatedTalent

            val updatedProgress = progress.copy(
                talentPointsAvailable = progress.talentPointsAvailable - 1,
                talentPointsSpent = progress.talentPointsSpent + 1,
                talentsJson = GameJsonParser.listToJson(talentList)
            )
            repository.saveProgress(updatedProgress)
            showNotification("¡Asignaste un punto al talento: ${talent.name}!")
        }
    }

    private fun getTalentRank(talentId: String): Int {
        val progress = _progressState.value ?: return 0
        val list = GameJsonParser.listFromJson<Talent>(progress.talentsJson)
        return list.find { it.id == talentId }?.currentRank ?: 0
    }

    // --- MANUAL ATTRIBUTES SPENDING ---
    fun allocateAttributePoint(attribute: String) {
        // We use talent points or just level up allocated points?
        // Let's implement leveling up attribute points!
        // Wait, how do players allocate the +5 points on level up?
        // Let's keep a record of how many manual attribute points are available!
        // We can calculate manually: available points = (level - 1) * 5 - (extraStr + extraDex + extraInt + extraCon spent).
        // To keep it simple, we can store it or calculate it!
        // Let's add an easy attribute point allocation.
        // Let's say: when level up, the player can click '+' in the Character Screen!
        // But how many are available? Let's check our attributes spent versus earned.
        // Let's say: we can check if they have unspent attribute points. To make it extremely simple and bulletproof,
        // we can calculate `attributePointsAvailable = (Level * 5) + 15` minus the sum of stats compared to base!
        // Or simpler, we can dynamically add unspent attribute points in `GameProgress`!
        // Wait, `GameProgress` doesn't have an `attributePointsAvailable` field in Room. But wait, we can store it in some field or we can use the `talentPointsAvailable` as a shared pool or we can easily calculate it from the Level!
        // Wait, we can define that allocating stats can be done if the total stats are less than:
        // Starting stats (40) + level-up increments (level-1 * 4) + level-up free stats (level-1 * 5).
        // Let's check: starting stats are Str + Dex + Int + Con. We know the race/class starting stats, but to make it completely safe and easy:
        // Let's calculate the available stat points as:
        // `totalEarnedStatPoints = (charLevel - 1) * 5`
        // We can track a state of "unspentAttributePoints" inside the ViewModel, which gets saved in our session or computed!
        // Let's computed it: `progress.charLevel` gives how many points they earned.
        // Let's define the allocation:
        // When they level up, they get +5 stat points. Let's add them directly to their stats! Or let's let them click to assign!
        // Wait, let's write a simple method to increase stats in Character Screen:
        // Let's make it extremely elegant: when they click, we check if they have spent less than `(level - 1) * 5`.
        // Let's check how many total attribute points were spent by the player:
        // We can save the allocated points count or calculate them!
        // Let's look at `GameProgress` entity definition:
        // `val statStr: Int, val statDex: Int, val statInt: Int, val statCon: Int`
        // It's saved in DB! So if we increase `statStr`, it is stored.
        // Let's say: when you level up, you can spend points. To make it super simple, let's add a `statPointsAvailable` property? Wait, we can use `talentPointsAvailable` for talents, and we can calculate or save the stats.
        // Let's allow the player to spend stat points if they have any. We can calculate:
        // Total stats = statStr + statDex + statInt + statCon.
        // Expected base stats for level $L$ = ClassBaseStats + $(L - 1) \times 4$.
        // Unspent points = $(L - 1) \times 5$ - extra stats allocated.
        // That is mathematically 100% accurate and requires NO extra DB fields!
        // Let's implement this calculation!
        // Let's write `getUnspentStatPoints(progress: GameProgress): Int`:
        val startingStatsSum = when (_progressState.value?.charClass) {
            "Guerrero" -> 48 // 10+10+10+10 + race (approx 4) + class (approx 4)
            "Mago" -> 46
            "Pícaro" -> 46
            else -> 46 // Cleric
        }
        // Let's just store a field or let them distribute directly. Even better: we can let them distribute +5 points directly!
    }

    fun allocateStatPoint(stat: String) {
        val progress = _progressState.value ?: return
        
        if (progress.statPointsAvailable <= 0) {
            showNotification("No tienes puntos de atributo para asignar.")
            return
        }

        viewModelScope.launch {
            val updated = when (stat) {
                "STR" -> progress.copy(
                    statStr = progress.statStr + 1,
                    statPointsAvailable = progress.statPointsAvailable - 1
                )
                "DEX" -> progress.copy(
                    statDex = progress.statDex + 1,
                    statPointsAvailable = progress.statPointsAvailable - 1
                )
                "INT" -> {
                    val newInt = progress.statInt + 1
                    progress.copy(
                        statInt = newInt,
                        maxMp = newInt * 6,
                        statPointsAvailable = progress.statPointsAvailable - 1
                    )
                }
                else -> { // CON
                    val newCon = progress.statCon + 1
                    progress.copy(
                        statCon = newCon,
                        maxHp = newCon * 12,
                        statPointsAvailable = progress.statPointsAvailable - 1
                    )
                }
            }
            repository.saveProgress(updated)
            showNotification("¡Aumentaste tu ${stat} en +1!")
        }
    }

    fun getUnspentStatPoints(progress: GameProgress): Int {
        return progress.statPointsAvailable
    }

    fun autoAllocateAllStatPoints() {
        val progress = _progressState.value ?: return
        val pointsToAllocate = progress.statPointsAvailable
        if (pointsToAllocate <= 0) {
            showNotification("No tienes puntos de atributo para asignar.")
            return
        }

        viewModelScope.launch {
            var currentProgress = progress
            val cls = progress.charClass
            
            val order = when (cls) {
                "Guerrero" -> listOf("STR", "STR", "STR", "CON")
                "Pícaro" -> listOf("DEX", "DEX", "DEX", "CON", "STR")
                "Mago" -> listOf("INT", "INT", "INT", "CON")
                "Clérigo" -> listOf("INT", "CON", "INT", "CON", "STR")
                else -> listOf("STR", "DEX", "INT", "CON")
            }

            var index = 0
            var strAdded = 0
            var dexAdded = 0
            var intAdded = 0
            var conAdded = 0

            for (i in 0 until pointsToAllocate) {
                val stat = order[index % order.size]
                when (stat) {
                    "STR" -> strAdded++
                    "DEX" -> dexAdded++
                    "INT" -> intAdded++
                    "CON" -> conAdded++
                }
                index++
            }

            val newStr = currentProgress.statStr + strAdded
            val newDex = currentProgress.statDex + dexAdded
            val newInt = currentProgress.statInt + intAdded
            val newCon = currentProgress.statCon + conAdded
            val newMaxHp = newCon * 12
            val newMaxMp = newInt * 6

            val updated = currentProgress.copy(
                statStr = newStr,
                statDex = newDex,
                statInt = newInt,
                statCon = newCon,
                maxHp = newMaxHp,
                maxMp = newMaxMp,
                statPointsAvailable = 0
            )

            repository.saveProgress(updated)
            
            val summary = buildString {
                append("¡Asignados automáticamente: ")
                val parts = mutableListOf<String>()
                if (strAdded > 0) parts.add("Fuerza +$strAdded")
                if (dexAdded > 0) parts.add("Destreza +$dexAdded")
                if (intAdded > 0) parts.add("Inteligencia +$intAdded")
                if (conAdded > 0) parts.add("Constitución +$conAdded")
                append(parts.joinToString(", "))
                append("!")
            }
            showNotification(summary)
        }
    }

    // --- GAME RESET / DELETION ---
    fun deleteCharacterAndReset() {
        viewModelScope.launch {
            repository.deleteProgress()
            _screenState.value = GameScreen.CREATING_CHARACTER
            _creatorName.value = ""
            _creatorPointsAvailable.value = 15
            recalculateCreatorBaseStats()
            showNotification("Tu partida ha sido borrada. ¡Crea un nuevo héroe!")
        }
    }

    fun getEvolvedRaceName(race: String, level: Int): String {
        if (level < 5) return race
        return when (race) {
            "Humano" -> "Campeón Imperial"
            "Elfo" -> "Guardián Astral"
            "Enano" -> "Señor de las Runas"
            "Orco" -> "Devastador Berserker"
            else -> race
        }
    }

    fun getRacePassiveDescription(race: String, level: Int): String {
        return if (level < 5) {
            when (race) {
                "Humano" -> "Determinación Humana: +10% de oro obtenido en combate y +5% de golpe crítico."
                "Elfo" -> "Sentidos Élficos: +5% de golpe crítico y +10% de Maná Máximo."
                "Enano" -> "Piel de Piedra: +10% de Vida Máxima y +5 de defensa."
                "Orco" -> "Furia Berserker: +10% de daño físico y mágico infligido."
                else -> ""
            }
        } else {
            when (race) {
                "Humano" -> "Espíritu Triunfante (EVOLUCIONADO): +15% de oro obtenido, y recuperas un 8% de tu Vida Máxima al final de cada turno en combate."
                "Elfo" -> "Sabiduría Eterna (EVOLUCIONADO): +15% de golpe crítico y reduce todos los costos de Maná de tus habilidades en un 20%."
                "Enano" -> "Escudo Rúnico (EVOLUCIONADO): +15% de Vida Máxima, +10 de defensa y devuelves un 10% del daño recibido al atacante."
                "Orco" -> "Furia Incontenible (EVOLUCIONADO): +25% de daño infligido y tus ataques básicos te curan un 12% del daño causado."
                else -> ""
            }
        }
    }
}
