package com.example.data

import com.example.audio.SoundManager
import com.example.eldoria.systems.QuestSystem
import com.example.eldoria.systems.EventSystem
import com.example.eldoria.systems.DungeonSystem
import com.example.eldoria.systems.DialogueSystem
import com.example.eldoria.systems.QuestUpdate
import com.example.eldoria.systems.QuestReward
import com.example.eldoria.systems.EventResult
import com.example.eldoria.core.content.Quest
import com.example.eldoria.core.content.RandomEvent
import com.example.eldoria.core.content.Realm
import com.example.eldoria.core.content.EnemyType
import com.example.eldoria.core.content.GameBalance
import com.example.eldoria.systems.Achievement
import com.example.eldoria.systems.AchievementDefinitions
import com.example.eldoria.systems.DailyRewardState
import com.example.eldoria.systems.NpcType
import com.example.eldoria.systems.canClaimDailyReward
import com.example.eldoria.systems.claimDailyReward
import com.example.data.content.EldoriaBestiary
import com.example.data.content.KingdomAtlas
import com.example.ui.art.EldoriaArt
import com.example.data.engine.EldoriaBalance
import com.example.data.engine.EldoriaDungeonBalance
import com.example.data.engine.EldoriaPassives
import com.example.data.content.EldoriaExpeditions
import com.example.data.content.EldoriaPotions
import com.example.data.content.PotionEffect
import com.example.data.content.EldoriaPets
import com.example.data.content.EldoriaTalentEngine
import com.example.data.content.EldoriaTalents
import com.example.data.content.TalentContext
import com.example.data.content.TalentKind
import com.example.data.content.TalentLoadout
import com.example.data.engine.EldoriaHost
import com.example.data.model.ExpeditionState
import com.example.data.engine.EldoriaSystemsController
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
    DUNGEON,
    COMBAT,
    CHARACTER_SCREEN,
    TALENTS,
    INVENTORY,
    SHOP,
    PET_SCREEN,
    HELP_SCREEN,
    ACHIEVEMENTS,
    CRAFTING,
    DAILY_REWARDS,
    MAIN_MENU,
    SETTINGS,
    EXPEDITION,
    PET_SANCTUARY,
    MINIGAMES,
    BESTIARY,
    CONTRACTS
}

data class EnemyPet(
    val name: String,
    val level: Int,
    val attack: Int,
    val imageResName: String
)

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
    val rarity: String = "NORMAL", // "NORMAL", "ELITE", "BOSS"
    val pet: EnemyPet? = null,
    /**
     * Drawable exacto de la especie, tal y como lo declara el bestiario.
     * Sin esto la UI tenía que adivinar el retrato buscando palabras dentro del
     * nombre ya decorado ("⭐ Musgoso Devorador Feroz Élite"), y acababa
     * enseñando el ogro genérico para media docena de criaturas.
     */
    val artKey: String = ""
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
    val enemyAntiHealTurns: Int = 0,
    val activeAnimation: String? = null, // "PLAYER_ATTACK", "PLAYER_HEAL", "PLAYER_MAGIC", "ENEMY_ATTACK", "ENEMY_SKILL", "PLAYER_POTION"
    // === Renacer de Eldoria: campos nuevos, TODOS con default y al final ===
    val enemyIntent: String? = null,
    val enemyIntentIcon: String = "",
    val enemyArchetype: String = "BRUTO",
    val enemyAffixes: List<String> = emptyList(),
    val enemySpeciesId: String = "",
    val reactionWindow: Boolean = false,
    val reactionDeadline: Long = 0L,
    val momentum: Int = 0,
    val petCooldown: Int = 0,
    val inExpedition: Boolean = false,
    val expeditionDepth: Int = 0,
    val expeditionRoomLabel: String = "",
    /**
     * Id de la última habilidad lanzada por el jugador. La UI lo usa para elegir
     * el efecto visual (fuego, veneno, sagrado…): sin esto, todas las habilidades
     * se verían igual, y con auto-combate la pantalla no tendría forma de saberlo.
     */
    val lastSkillId: String = "",

    // ─── Pasivas de objeto: estado que sólo vive dentro del combate ───
    /** Daño que aún absorbe el Escudo Rúnico antes de que te toquen. */
    val runeShieldLeft: Int = 0,
    /** El Segundo Aliento sólo salva una vez por combate. */
    val secondWindUsed: Boolean = false,
    /** Turnos peleados: alimenta la Furia Creciente. */
    val turnsFought: Int = 0,
    /** Nombres de las pasivas activas, para enseñarlas en el HUD de combate. */
    val activePassives: List<String> = emptyList(),

    // ─── Mecánicas de jefe ───
    /** Fase actual del jefe (1..3). Sube al bajarle la vida. */
    val bossPhase: Int = 1,
    /** Turnos que al jefe le queda el enfurecimiento activo. */
    val enrageTurns: Int = 0,
    /** Acumulación de sangrado sobre el héroe: daña al inicio de su turno. */
    val bleedStacks: Int = 0,

    // ─── Efectos de poción ───
    //
    // Cada uno lleva turnos restantes y magnitud por separado: la magnitud la
    // fija el frasco que bebiste, así que un Filtro de Furia épico y uno
    // legendario pueden durar lo mismo y pegar distinto sin tocar el motor.
    /** Turnos y fracción de vida máxima que cura la regeneración por turno. */
    val regenTurns: Int = 0,
    val regenPotency: Double = 0.0,
    /** Turnos y fracción de daño extra que hace el héroe. */
    val damageBuffTurns: Int = 0,
    val damageBuffPotency: Double = 0.0,
    /** Turnos y probabilidad de esquivar por completo el golpe enemigo. */
    val evasionTurns: Int = 0,
    val evasionPotency: Double = 0.0,
    /** Turnos y fracción en que se reduce el daño recibido. */
    val wardTurns: Int = 0,
    val wardPotency: Double = 0.0,

    // ─── Talentos de un solo disparo ───
    //
    // No se reutiliza [secondWindUsed] a propósito: el Segundo Aliento es una
    // pasiva de objeto y el Último Aliento un talento. Compartir la bandera
    // haría que tener las dos cosas salvara UNA vez en vez de dos, y el jugador
    // que invirtió puntos en el talento no entendería por qué no se dispara.
    /** El Último Aliento (talento) sólo salva una vez por combate. */
    val lastBreathUsed: Boolean = false,
    /** Ya se gastó el crítico garantizado del primer golpe. */
    val firstStrikeUsed: Boolean = false
)

data class SpecialMerchantItem(
    val item: Item,
    val originalPrice: Int,
    val discountPrice: Int,
    val discountPercent: Int
)

data class SpecialMerchantState(
    val active: Boolean = false,
    val merchantName: String = "",
    val kingdomName: String = "",
    val dialogue: String = "",
    val items: List<SpecialMerchantItem> = emptyList()
)

data class CastleState(
    val active: Boolean = false,
    val castleName: String = "",
    val kingdomName: String = "",
    val kingdomColor: String = "#4CAF50",
    val description: String = "",
    val blessingClaimed: Boolean = false,
    val tile: MapTile? = null
)

/**
 * Hito de reino: el evento único de cada tierra. Vive en memoria como el
 * castillo — una vez reclamado, la casilla queda marcada como limpia y el
 * estado sólo sirve para volver a leer su lore.
 */
data class LandmarkState(
    val active: Boolean = false,
    val kingdomId: String = "",
    val kingdomName: String = "",
    val name: String = "",
    val lore: String = "",
    val boon: String = "",
    val claimed: Boolean = false,
    val x: Int = 0,
    val y: Int = 0
)

// === CRÓNICAS DE ELDORIA: modelos de misiones, eventos y NPCs ===

data class ActiveBuff(
    val type: String, // "ATTACK" | "DEFENSE"
    val value: Int,
    val label: String
)

data class TravelEventOutcome(
    val title: String,
    val description: String,
    val resultText: String,
    val isPositive: Boolean,
    val ambush: Boolean = false,
    val ambushTile: MapTile? = null
)

data class NpcEncounterState(
    val npcType: NpcType,
    val npcName: String,
    val npcTitle: String,
    val tileKey: String
)

data class PersistedQuest(
    val quest: Quest,
    val progress: Int
)

val NPC_ROSTER = listOf(
    Pair("Eldrin", "Anciano del Valle"),
    Pair("Grek", "Mercader Itinerante"),
    Pair("Kael", "Guerrero Veterano"),
    Pair("Lyra", "Vidente de los Susurros")
)

data class MapTile(
    val x: Int,
    val y: Int,
    val biome: String,
    val explored: Boolean = false,
    val cleared: Boolean = false,
    val hasEncounter: Boolean = true,
    val encounterType: String = "MONSTER", // "MONSTER", "CHEST", "SHRINE", "BOSS", "CASTLE", "SPECIAL_MERCHANT", "TREASURE"
    val levelRequirement: Int = 1,
    val isBossLair: Boolean = false,
    val isObstacle: Boolean = false,
    val isEnemySpawn: Boolean = false,
    val kingdomName: String = "Reino de Eldoria",
    val kingdomColor: String = "#4CAF50",
    val specialName: String = ""
)

class GameViewModel(private val repository: GameProgressRepository) : ViewModel(), EldoriaHost {

    private val _screenState = MutableStateFlow(GameScreen.MAIN_MENU)
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

    private val _dungeonRunState = MutableStateFlow(DungeonRunState())
    val dungeonRunState: StateFlow<DungeonRunState> = _dungeonRunState.asStateFlow()

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

    private val _specialMerchantState = MutableStateFlow(SpecialMerchantState())
    val specialMerchantState: StateFlow<SpecialMerchantState> = _specialMerchantState.asStateFlow()

    private val _castleState = MutableStateFlow(CastleState())
    val castleState: StateFlow<CastleState> = _castleState.asStateFlow()

    private val _landmarkState = MutableStateFlow(LandmarkState())
    val landmarkState: StateFlow<LandmarkState> = _landmarkState.asStateFlow()

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

    /** Marcha automática DENTRO del descenso (el mapa de salas, no el del mundo). */
    private val _isAutoExpedition = MutableStateFlow(false)
    val isAutoExpedition: StateFlow<Boolean> = _isAutoExpedition.asStateFlow()

    val allCharactersState: StateFlow<List<GameProgress>> = repository.allCharactersFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var isFirstLoad = true

    fun startNewCharacterCreator() {
        viewModelScope.launch {
            // Abrir el creador NO desactiva al héroe anterior: si el jugador se
            // arrepiente y pulsa VOLVER, su partida sigue activa. `submitCharacter()`
            // ya llama a `deactivateAll()` justo antes de insertar al nuevo héroe.
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
            // Lectura PURA: `getProgress()` restauraría el backup del héroe recién
            // borrado y lo dejaría otra vez como activo.
            val remaining = repository.getActiveProgressSync()
            if (remaining == null) {
                // Al menú principal, no al creador: desde allí se ve el resto del
                // Salón de Héroes y también se puede crear uno nuevo.
                _screenState.value = GameScreen.MAIN_MENU
            }
        }
    }

    fun toggleAutoCombat() {
        _isAutoCombat.value = !_isAutoCombat.value
        val state = _combatState.value
        if (_isAutoCombat.value && isAutoCombatBlocked(state)) {
            _isAutoCombat.value = false
            systems.showToast("⚔️ Aquí no hay piloto automático: esta sala se gana a mano.", "EMBER")
            return
        }
        if (_isAutoCombat.value && state.active && state.playerTurn && state.victory == null) {
            viewModelScope.launch {
                performAutoCombatTurn(state)
            }
        }
        systems.showToast(
            if (_isAutoCombat.value) "⚔️ Combate automático activado." else "⚔️ Combate automático desactivado.",
            "GOLD"
        )
    }

    /**
     * Marcha automática del descenso. Recorre el mapa de salas sola y deja que el
     * combate automático resuelva lo que salga; se para cuando hay que decidir
     * algo (una bendición, un evento) y sigue en cuanto elijas.
     */
    fun toggleAutoExpedition() {
        val run = systems.expedition.value
        if (!_isAutoExpedition.value && (!run.active || run.finished)) {
            systems.showToast("🧭 No hay ningún descenso en curso.", "IRON")
            return
        }
        _isAutoExpedition.value = !_isAutoExpedition.value
        if (_isAutoExpedition.value) {
            // Sin piloto de combate la marcha se quedaría clavada en la primera
            // sala de pelea, así que van juntos.
            if (!_isAutoCombat.value) _isAutoCombat.value = true
            systems.showToast("🧭 Marcha automática: el descenso se recorre solo.", "GOLD")
        } else {
            systems.showToast("🧭 Marcha automática detenida.", "IRON")
        }
    }

    /**
     * Siguiente sala del piloto. Con la antorcha corta o el héroe tocado busca
     * hoguera; si no, evita al jefe mientras queden otras salas, porque entrar al
     * jefe es una decisión y no un paso más del camino.
     */
    private fun pickAutoExpeditionRoom(run: ExpeditionState): Int? {
        val options = run.availableRoomIds.mapNotNull { id -> run.rooms.firstOrNull { it.id == id } }
        if (options.isEmpty()) return null

        val progress = _progressState.value
        val hpPct = if (progress != null && progress.maxHp > 0) {
            run.persistentHp.toDouble() / progress.maxHp
        } else 1.0

        if (run.torch <= 30 || hpPct < 0.45) {
            options.firstOrNull { it.kind == EldoriaExpeditions.KIND_CAMPFIRE }?.let { return it.id }
        }

        val notBoss = options.filter { it.kind != EldoriaExpeditions.KIND_BOSS }
        return (if (notBoss.isNotEmpty()) notBoss else options).random().id
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
        systems.showToast(
            if (_isAutoNavigation.value) "🧭 Navegación automática activada." else "🧭 Navegación automática desactivada.",
            "GOLD"
        )
    }

    /**
     * El piloto automático ya no se apaga en las salas serias del descenso.
     *
     * Antes se bloqueaba en ÉLITE y JEFE porque el piloto no sabía responder a la
     * ventana de reacción y se comía todos los golpes enteros. Ahora la resuelve
     * él (ver [autoResolveReaction]), así que puede llevar la run completa. Se
     * queda como gancho por si alguna sala futura tiene que ganarse a mano.
     */
    private fun isAutoCombatBlocked(state: CombatState): Boolean = false

    /**
     * Parada automática. A propósito NO es perfecta: un piloto que desviase
     * siempre haría del combate manual una tontería. Reparte paradas buenas la
     * mayoría de las veces, alguna perfecta y algún fallo; la asistencia de
     * reacción de los ajustes le mejora la mano, igual que al jugador.
     */
    private fun autoResolveReaction() {
        val assisted = systems.settings.value.reactionAssist
        val roll = Random.nextInt(100)
        val quality = when {
            roll < (if (assisted) 30 else 20) -> "PERFECTO"
            roll < (if (assisted) 85 else 75) -> "BUENO"
            else -> "FALLO"
        }
        executeReaction(quality)
    }

    /**
     * Golpe aproximado del héroe, para decidir — no para mostrar.
     *
     * Ignora el azar, el crítico y a la mascota a propósito: el piloto sólo
     * necesita saber si el enemigo cae ESTE turno, y una estimación optimista le
     * haría malgastar la definitiva en un enemigo que sobrevive con un hilo.
     * Quedarse corto sólo cuesta un turno; pasarse cuesta el recurso.
     */
    private fun estimateHeroHit(progress: GameProgress, state: CombatState, skill: Skill?): Int {
        val weapon = GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)
        val isMagic = progress.charClass == "Mago" || progress.charClass == "Clérigo"
        val stat = when {
            skill != null && isMagic -> progress.statInt + (weapon?.intBonus ?: 0)
            progress.charClass == "Pícaro" -> progress.statDex + (weapon?.dexBonus ?: 0)
            else -> progress.statStr + (weapon?.strBonus ?: 0)
        }
        val base = (stat * (if (skill != null) 0.9 else 0.6)) + (weapon?.dmgBonus ?: 0)
        val mult = (skill?.damageMultiplier ?: 1.0) *
            (1.0 + state.momentum / 200.0) *
            (if (state.damageBuffTurns > 0) 1.0 + state.damageBuffPotency else 1.0)
        val outputScale = EldoriaBalance.measureHero(progress) { id -> getTalentRank(id) }.outputScale
        val raw = EldoriaBalance.scaleHeroDamage((base * mult).toInt(), outputScale)
        val enemy = state.enemy ?: return raw
        return EldoriaBalance.mitigate(raw, enemy.defense, progress.charLevel).coerceAtLeast(1)
    }

    /**
     * Turno del piloto automático.
     *
     * El piloto viejo bebía "la primera poción del zurrón", y eso convertía un
     * Bálsamo de Piedra de 500 de oro en un frasco de curar mal. Ahora cada
     * decisión mira la situación: cuánta vida queda, cuánto le queda al enemigo,
     * si la pelea va a ser larga y qué efectos hay ya puestos. El orden de las
     * reglas ES la prioridad: lo que decide el combate va antes que lo que lo
     * mejora.
     */
    private fun performAutoCombatTurn(state: CombatState) {
        val progress = _progressState.value ?: return
        val enemy = state.enemy ?: return
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson)
        val classSkills = GameJsonParser.listFromJson<Skill>(progress.skillsJson)

        val hpPct = state.playerCurrentHp.toDouble() / progress.maxHp.coerceAtLeast(1)
        val enemyPct = enemy.currentHp.toDouble() / enemy.maxHp.coerceAtLeast(1)
        // "Pelea grande" = la que se va a alargar. Es el criterio que separa el
        // frasco que te salva ahora del que te mantiene vivo diez turnos.
        val bigFight = enemy.isBoss || enemy.rarity == "ELITE" || enemy.rarity == "CHAMPION" ||
            enemy.rarity == "LEGENDARY" || enemy.rarity == "UNIVERSAL"

        // Frascos ya identificados: sin esto habría que adivinar por el nombre en
        // cada regla, y las pociones de partidas viejas no tienen id de catálogo.
        val flasks = invList
            .filter { it.type == "POTION" }
            .map { it to EldoriaPotions.fromItem(it.id, it.name) }
        fun flasksOf(effect: PotionEffect) = flasks.filter { it.second.effect == effect }

        val healSkill = classSkills
            .filter { it.healingMultiplier > 0.0 && state.playerCurrentMp >= it.manaCost }
            .maxByOrNull { it.healingMultiplier }

        // ── 1. Rematar. Un enemigo muerto no pega: si cae este turno, cualquier
        // otra jugada (beber, buffear, curarse) es un turno regalado.
        if (estimateHeroHit(progress, state, null) >= enemy.currentHp) {
            // El básico ya basta: no se gasta maná en lo que ya está hecho.
            executeBasicAttack()
            return
        }
        val affordableDamage = classSkills
            .filter { it.damageMultiplier > 0.0 && state.playerCurrentMp >= it.manaCost }
        val finisher = affordableDamage
            // A igualdad de daño se prefiere la definitiva: es la que tiene el
            // multiplicador alto y la que menos ocasiones tiene de usarse bien.
            .sortedWith(compareByDescending<Skill> { it.damageMultiplier }.thenByDescending { it.isUltimate })
            .firstOrNull { estimateHeroHit(progress, state, it) >= enemy.currentHp }
        if (finisher != null) {
            executeSkill(finisher)
            return
        }

        // ── 2. Vida crítica: se cura con lo más gordo que haya. Aquí no se
        // economiza — la Gran Poción guardada "para luego" no sirve de nada si
        // el luego no llega.
        if (hpPct < 0.25) {
            val biggest = flasksOf(PotionEffect.RESTORE).maxByOrNull { it.second.healPct }
            if (biggest != null) {
                usePotionCombat(biggest.first.id)
                return
            }
            if (healSkill != null) {
                executeSkill(healSkill)
                return
            }
        }

        // ── 3. Curación normal. La habilidad va antes que el frasco: el maná se
        // recupera solo entre combates y los frascos hay que comprarlos.
        if (hpPct < 0.40 && healSkill != null) {
            executeSkill(healSkill)
            return
        }

        // ── 4. Vida media-baja en pelea larga: regeneración. Curar de golpe aquí
        // sería desperdiciar la mitad del frasco, porque el daño va a seguir
        // llegando; el elixir cubre los turnos que quedan.
        val longFight = bigFight || enemyPct > 0.50
        if (hpPct < 0.45 && longFight && state.regenTurns == 0) {
            val regen = flasksOf(PotionEffect.REGEN).firstOrNull()
            if (regen != null) {
                usePotionCombat(regen.first.id)
                return
            }
        }
        // Sin elixir a mano, el frasco de curar sigue siendo mejor que morirse.
        if (hpPct < 0.40) {
            val restore = flasksOf(PotionEffect.RESTORE).maxByOrNull { it.second.healPct }
            if (restore != null) {
                usePotionCombat(restore.first.id)
                return
            }
        }

        // ── 5. Rematar con Filtro de Furia. Se bebe cuando el enemigo ya está
        // por debajo del 35 %: ahí los cuatro turnos de buff se aprovechan
        // enteros, mientras que beberlo al principio se gasta en la fase larga.
        if (enemyPct < 0.35 && state.damageBuffTurns == 0) {
            val fury = flasksOf(PotionEffect.DAMAGE).firstOrNull()
            if (fury != null) {
                usePotionCombat(fury.first.id)
                return
            }
        }

        // ── 6. Preparación contra un enemigo grande, y sólo con la vida sana:
        // un buff defensivo cuesta un turno, y ese turno sólo se puede pagar
        // cuando no hay una emergencia encima. Nunca se bebe un efecto que ya
        // está activo, porque no se acumula: sería tirar el frasco.
        if (bigFight && hpPct >= 0.60) {
            if (state.wardTurns == 0) {
                val stone = flasksOf(PotionEffect.DEFENSE).firstOrNull()
                if (stone != null) {
                    usePotionCombat(stone.first.id)
                    return
                }
            }
            if (state.evasionTurns == 0) {
                val shadow = flasksOf(PotionEffect.EVASION).firstOrNull()
                if (shadow != null) {
                    usePotionCombat(shadow.first.id)
                    return
                }
            }
        }

        // ── 7. Anti-curación contra lo que se cura. Un enemigo que regenera
        // convierte el combate en una carrera imposible: cortarle la curación
        // vale más que un turno de daño, aunque el número que sale sea menor.
        val enemyHeals = bigFight ||
            state.enemyArchetype == "SANADOR_CORRUPTO" || state.enemyArchetype == "NO_MUERTO"
        if (enemyHeals && state.enemyAntiHealTurns == 0) {
            val curse = affordableDamage.firstOrNull { it.isAntiHeal }
            if (curse != null) {
                executeSkill(curse)
                return
            }
        }

        // ── 8. Ofensiva con gestión de maná.
        //
        // Si la mejor habilidad no se puede pagar, se pega a mano en vez de
        // quemar el maná en una habilidad mediocre: guardar dos turnos para
        // lanzar la buena hace más daño total que gastar en la mala tres veces.
        // Y si hay curación disponible, se le reserva su coste: quedarse sin
        // maná para curar es la forma más habitual de perder un combate ganado.
        val bestOverall = classSkills.filter { it.damageMultiplier > 0.0 }
            .maxByOrNull { it.damageMultiplier }
        val healReserve = classSkills
            .filter { it.healingMultiplier > 0.0 }
            .minOfOrNull { it.manaCost } ?: 0
        val keepReserve = healReserve > 0 && hpPct < 0.60
        val bestAffordable = affordableDamage
            .filter { !keepReserve || state.playerCurrentMp - it.manaCost >= healReserve }
            .maxByOrNull { it.damageMultiplier }

        if (bestAffordable != null &&
            (bestOverall == null || bestAffordable.damageMultiplier >= bestOverall.damageMultiplier * 0.7)
        ) {
            executeSkill(bestAffordable)
            return
        }

        // ── 9. A mano: o no llega el maná, o lo que llega no merece gastarlo.
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

    // ═══════════════════════════════════════════════════════════════════════
    //  PUENTE «RENACER DE ELDORIA» — implementación de EldoriaHost
    // ═══════════════════════════════════════════════════════════════════════

    override val hostScope: kotlinx.coroutines.CoroutineScope
        get() = viewModelScope

    override fun currentProgress(): GameProgress? = _progressState.value

    override fun persistProgress(updated: GameProgress) {
        // El estado en memoria se adelanta a Room para que dos acciones seguidas
        // del controlador no lean una fila obsoleta y se pisen entre sí.
        _progressState.value = updated
        viewModelScope.launch { repository.saveProgress(updated) }
    }

    /**
     * Guardado normal del ViewModel. Misma disciplina que [persistProgress]: la fila
     * escrita se publica en memoria ANTES de que Room la emita, de modo que dos
     * acciones seguidas (vender dos objetos, equipar dos piezas, vender y aceptar un
     * contrato…) no partan las dos del mismo snapshot y se borren la una a la otra.
     */
    private suspend fun saveProgressSynced(updated: GameProgress): Long {
        _progressState.value = updated
        return repository.saveProgress(updated)
    }

    override fun hostNotify(message: String) {
        showNotification(message)
    }

    override fun hostNavigate(screen: GameScreen) {
        _screenState.value = screen
    }

    override fun hostSyncStats(progress: GameProgress): GameProgress = syncMaxHpAndMp(progress)

    override fun hostPlaySound(key: String) {
        when (key) {
            "click" -> SoundManager.playButtonClick()
            "slash" -> SoundManager.playSwordSlash()
            "crit" -> SoundManager.playCriticalHit()
            "magic" -> SoundManager.playMagicSpell()
            "heal" -> SoundManager.playHealPotion()
            "enemy" -> SoundManager.playEnemyAttack()
            "victory" -> SoundManager.playVictory()
            "defeat" -> SoundManager.playDefeat()
            else -> Unit
        }
    }

    override fun hostGrantItem(item: Item) {
        val progress = _progressState.value ?: return
        val inventory = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        inventory.add(item)
        val updated = progress.copy(inventoryJson = GameJsonParser.listToJson(inventory.toList()))
        _progressState.value = updated
        viewModelScope.launch { repository.saveProgress(updated) }
    }

    /** Reino canónico de cada destino de expedición (mismo escalón que el tier del calabozo). */
    private fun expeditionKingdomFor(dungeonId: Int): String = when {
        dungeonId >= 13 -> "aetheria"
        dungeonId >= 11 -> "solaria"
        dungeonId >= 8 -> "aethelgard"
        dungeonId >= 6 -> "frostgard"
        dungeonId >= 3 -> "drakenhold"
        else -> "eldoria"
    }

    /**
     * E9 — Combate dentro de una expedición.
     * Reutiliza la maquinaria de `startDungeonStageCombat` (mismas curvas de HP/ATK/DEF)
     * pero marca `inExpedition`/`expeditionDepth`/`expeditionRoomLabel` y NO toca `_dungeonRunState`.
     */
    override fun hostStartExpeditionCombat(
        dungeonId: Int,
        depth: Int,
        roomKind: String,
        roomLabel: String,
        hp: Int,
        mp: Int,
        bossName: String?
    ) {
        val progress = _progressState.value ?: return
        resetCombatAuxiliaries()
        val kind = roomKind.uppercase()
        val isBoss = kind == EldoriaExpeditions.KIND_BOSS
        val isElite = kind == EldoriaExpeditions.KIND_ELITE

        val blueprint = EldoriaExpeditions.blueprint(dungeonId)
        val levelReq = blueprint?.levelReq ?: 1
        val safeDepth = depth.coerceAtLeast(0)

        // Planta equivalente del descenso, para poder usar la MISMA vara que el
        // calabozo clásico. La profundidad y el tipo de sala deciden el escalón.
        val stage = when {
            isBoss -> 10
            isElite -> (4 + safeDepth * 2).coerceIn(4, 9)
            else -> (2 + safeDepth * 2).coerceIn(1, 8)
        }
        val monsterLevel = EldoriaDungeonBalance.enemyLevel(levelReq, stage)

        val rarity = when {
            isBoss -> "UNIVERSAL"
            isElite -> "CHAMPION"
            else -> "ELITE"
        }

        val deco = systems.decorateEnemy(
            kingdomId = expeditionKingdomFor(dungeonId),
            level = monsterLevel,
            rarity = rarity,
            isBoss = isBoss
        )

        // BALANCE ABSOLUTO. El descenso llevaba su propia fórmula por nivel
        // (`9 + nivel * 3.8` de ataque) que no miraba la vida REAL del héroe: con
        // el equipo bien montado el enemigo pegaba una milésima de tu barra y el
        // calabozo se volvía un paseo. `buildEnemy` es la vara que ya usa el
        // calabozo clásico y acota el golpe entre el 5 % y el 30 % de tu vida.
        val built = EldoriaDungeonBalance.buildEnemy(
            dungeonLevelReq = levelReq,
            stage = stage,
            actualHeroHp = progress.maxHp.coerceAtLeast(1),
            hpMult = deco.hpMult.toDouble(),
            atkMult = deco.atkMult.toDouble(),
            defMult = deco.defMult.toDouble()
        )
        val enemyHp = built.hp.coerceAtLeast(1)
        val enemyAtk = built.attack.coerceAtLeast(1)
        val enemyDef = built.defense.coerceAtLeast(0)

        // Identidad temática del destino. Cada calabozo anuncia una raza en su
        // ficha del vestíbulo ("Goblins", "Naga", "Máquinas") y tiene su propio
        // elenco de nueve subjefes con lámina propia; el descenso, en cambio,
        // estaba sacando fauna aleatoria del REINO, así que en las Cavernas del
        // Clan Goblin salían ciervos espectrales. Los 16 calabozos usan su
        // elenco; el Abismo (101-104), que no tiene, se queda con el bestiario.
        val roster = DUNGEONS_LIST.firstOrNull { it.id == dungeonId }
        val themedRawName: String? = when {
            roster == null -> null
            isBoss -> roster.finalBossName
            roster.subBosses.isEmpty() -> null
            else -> {
                val pool = roster.subBosses
                val index = if (isElite) {
                    // Las salas de élite tiran del tramo final del elenco.
                    (pool.size / 2) + Random.nextInt((pool.size + 1) / 2)
                } else {
                    Random.nextInt(pool.size)
                }
                pool[index.coerceIn(0, pool.lastIndex)]
            }
        }

        val rawName = when {
            isBoss && !bossName.isNullOrBlank() -> bossName
            themedRawName != null -> themedRawName
            else -> deco.displayName
        }
        val displayName = when {
            isBoss -> "👑 $rawName"
            isElite -> "⭐ $rawName"
            else -> rawName
        }

        // El artKey NUNCA se pasaba aquí: la UI tenía que adivinar el retrato a
        // partir del nombre ya decorado y acababa enseñando otra criatura.
        val enemyArtKey = if (themedRawName != null) {
            EldoriaArt.dungeonKey(dungeonId, rawName, isBoss).ifBlank { deco.artKey }
        } else {
            deco.artKey
        }

        val enemy = Combatant(
            name = displayName,
            maxHp = enemyHp,
            currentHp = enemyHp,
            maxMp = 100,
            currentMp = 100,
            attack = enemyAtk,
            defense = enemyDef,
            level = monsterLevel,
            isBoss = isBoss,
            rarity = rarity,
            pet = generateEnemyPetIfNeeded(monsterLevel, rarity, isBoss),
            artKey = enemyArtKey
        )

        val maxHp = progress.maxHp.coerceAtLeast(1)
        val maxMp = progress.maxMp.coerceAtLeast(1)
        val affixNames = deco.affixes.mapNotNull { EldoriaBestiary.affix(it)?.name }
        val logs = mutableListOf(
            "🕯️ ${blueprint?.name ?: "Abismo"} · Profundidad ${safeDepth + 1} · $roomLabel",
            "⚔️ ${enemy.name} (Nivel ${enemy.level}) te cierra el paso."
        )
        if (affixNames.isNotEmpty()) logs.add("☠️ Afijos: ${affixNames.joinToString(" · ")}")
        logs.add("⚠️ Tu salud y maná son los que traías de la sala anterior.")

        _combatState.value = CombatState(
            active = true,
            enemy = enemy,
            playerCurrentHp = hp.coerceIn(1, maxHp),
            playerCurrentMp = mp.coerceIn(0, maxMp),
            combatLogs = logs.toList(),
            playerTurn = true,
            victory = null,
            enemyArchetype = deco.archetype,
            enemyAffixes = deco.affixes,
            // Con elenco temático no se registra especie: apuntar en el bestiario
            // una criatura del reino que el jugador no ha visto es mentirle. El
            // botín no depende de esto (cae al tramo por nivel).
            enemySpeciesId = if (themedRawName != null) "" else deco.speciesId,
            petCooldown = 0,
            momentum = 0,
            inExpedition = true,
            expeditionDepth = safeDepth,
            expeditionRoomLabel = roomLabel
        )

        _screenState.value = GameScreen.COMBAT
    }

    /**
     * Controlador de expediciones, mascotas, bestiario, materiales, contratos,
     * minijuegos y ajustes. Se inicializa aquí, antes del bloque `init`, de modo
     * que la hidratación del colector ya lo encuentra construido.
     */
    val systems: EldoriaSystemsController = EldoriaSystemsController(this)

    init {
        viewModelScope.launch {
            repository.progressFlow.collect { progress ->
                if (progress != null && progress.hasActiveChar) {
                    val synced = syncMaxHpAndMp(progress)
                    _progressState.value = synced
                    systems.hydrate(synced)
                    if (isFirstLoad) {
                        generateMapAround(synced.currentX, synced.currentY, synced.mapPointsExploredJson, synced.mapPointsClearedJson)
                        isFirstLoad = false
                    }
                } else {
                    _progressState.value = progress
                    // Sin personaje activo no hay dónde volver: el menú principal
                    // es el único destino seguro (y el creador si ya está abierto).
                    if (_screenState.value != GameScreen.CREATING_CHARACTER &&
                        _screenState.value != GameScreen.MAIN_MENU
                    ) {
                        _screenState.value = GameScreen.MAIN_MENU
                    }
                    isFirstLoad = false
                }
            }
        }

        // Auto Combat collector
        viewModelScope.launch {
            _combatState.collect { state ->
                if (state.active && state.playerTurn && state.victory == null &&
                    _isAutoCombat.value && !isAutoCombatBlocked(state)
                ) {
                    kotlinx.coroutines.delay(1000)
                    val currentState = _combatState.value
                    if (currentState.active && currentState.playerTurn && currentState.victory == null &&
                        _isAutoCombat.value && !isAutoCombatBlocked(currentState)
                    ) {
                        performAutoCombatTurn(currentState)
                    }
                }
            }
        }

        // Ventana de reacción en automático: sin esto el piloto se comía entero
        // cada golpe telegrafiado y no podía con élites ni jefes.
        viewModelScope.launch {
            _combatState.collect { state ->
                if (state.active && state.reactionWindow && state.victory == null && _isAutoCombat.value) {
                    // Se toma su tiempo dentro de la ventana: reaccionar al
                    // instante delataría que no hay nadie pulsando.
                    kotlinx.coroutines.delay(reactionWindowMillis() / 2)
                    val now = _combatState.value
                    if (now.active && now.reactionWindow && now.victory == null && _isAutoCombat.value) {
                        autoResolveReaction()
                    }
                }
            }
        }

        // Marcha automática del descenso: entra sola de sala en sala.
        viewModelScope.launch {
            while (true) {
                if (!_isAutoExpedition.value) {
                    kotlinx.coroutines.delay(1200)
                    continue
                }
                kotlinx.coroutines.delay(1100)
                if (!_isAutoExpedition.value) continue

                val run = systems.expedition.value
                if (!run.active || run.finished) {
                    if (_isAutoExpedition.value) {
                        _isAutoExpedition.value = false
                        systems.showToast("🧭 Marcha automática detenida: el descenso ha terminado.", "IRON")
                    }
                    continue
                }
                // El combate y las ofertas mandan: la marcha espera, no decide por
                // ti una bendición permanente ni interrumpe una pelea.
                if (_combatState.value.active) continue
                if (systems.expeditionOffer.value != null) continue
                if (_screenState.value != GameScreen.EXPEDITION) continue

                val next = pickAutoExpeditionRoom(run) ?: continue
                systems.enterRoom(next)
            }
        }

        // Auto Navigation checker loop — dormido de verdad mientras está desactivada.
        viewModelScope.launch {
            while (true) {
                if (!_isAutoNavigation.value) {
                    kotlinx.coroutines.delay(1500)
                    continue
                }
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

    private val _showClassAdvancementCutscene = MutableStateFlow<String?>(null)
    val showClassAdvancementCutscene = _showClassAdvancementCutscene.asStateFlow()

    fun dismissClassAdvancementCutscene() {
        _showClassAdvancementCutscene.value = null
    }

    private val _backupStatus = MutableStateFlow<String>("")
    val backupStatus = _backupStatus.asStateFlow()

    fun refreshBackupStatus() {
        // Consulta al sistema de ficheros: fuera del hilo principal.
        viewModelScope.launch {
            _backupStatus.value = repository.getBackupStatusText()
        }
    }

    fun exportManualBackup() {
        viewModelScope.launch {
            val success = repository.exportManualBackup()
            refreshBackupStatus()
            if (success) {
                showNotification("💾 ¡Copia de seguridad guardada con éxito en almacenamiento seguro!")
            } else {
                showNotification("⚠️ No se pudo crear el backup. Asegúrate de tener una partida activa.")
            }
        }
    }

    fun restoreManualBackup() {
        viewModelScope.launch {
            val restored = repository.restoreManualBackup()
            refreshBackupStatus()
            if (restored != null && restored.hasActiveChar) {
                showNotification("✨ ¡Personaje '${restored.charName}' restaurado con éxito desde el backup!")
                _screenState.value = GameScreen.WORLD_MAP
            } else {
                showNotification("⚠️ No se encontró ninguna copia de seguridad válida para restaurar.")
            }
        }
    }

    // Shop System State
    private val _shopItems = MutableStateFlow<List<Item>>(emptyList())
    val shopItems = _shopItems.asStateFlow()

    fun generateShopItems(level: Int) {
        val list = mutableListOf<Item>()
        for (i in 1..8) {
            val roll = Random.nextInt(100)
            val rarity = when {
                roll < 20 -> "ÉPICO"
                roll < 60 -> "RARO"
                else -> "COMÚN"
            }
            list.add(generateProceduralItem(level, isBoss = false, rarityPreset = rarity))
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
            saveProgressSynced(updated)
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
        viewModelScope.launch {
            invList.add(item)
            val updatedProgress = progress.copy(
                charGold = progress.charGold - cost,
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            saveProgressSynced(updatedProgress)
            _shopItems.value = _shopItems.value.filter { it.id != item.id }
            systems.showToast("🛒 Compras ${item.name} por $cost de oro.", "GOLD")
        }
    }

    fun calculateSellPrice(item: Item): Int {
        val basePrice = when (item.rarity.uppercase()) {
            "UNIVERSAL" -> 3500
            "ARCANO" -> 2000
            "LEGENDARY", "LEGENDARIO" -> 1200
            "EPIC", "ÉPICO" -> 350
            "RARE", "RARO" -> 120
            else -> 30
        }
        return basePrice + (item.itemLevel * 10)
    }

    fun sellItem(item: Item) {
        val progress = _progressState.value ?: return
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        val index = invList.indexOfFirst { it.id == item.id }
        if (index == -1) return

        val sellPrice = calculateSellPrice(item)

        viewModelScope.launch {
            invList.removeAt(index)
            val updatedProgress = progress.copy(
                charGold = progress.charGold + sellPrice,
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            saveProgressSynced(updatedProgress)
            systems.showToast("🪙 Vendes ${item.name} por $sellPrice de oro.", "GOLD")
        }
    }

    fun massSellItems(itemsToSell: List<Item>) {
        val progress = _progressState.value ?: return
        if (itemsToSell.isEmpty()) return

        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        val idsToSell = itemsToSell.map { it.id }.toSet()

        var totalEarnedGold = 0
        val remainingInv = invList.filter { item ->
            if (idsToSell.contains(item.id)) {
                totalEarnedGold += calculateSellPrice(item)
                false
            } else {
                true
            }
        }

        if (totalEarnedGold <= 0) return

        viewModelScope.launch {
            SoundManager.playButtonClick()
            val updatedProgress = progress.copy(
                charGold = progress.charGold + totalEarnedGold,
                inventoryJson = GameJsonParser.listToJson(remainingInv)
            )
            saveProgressSynced(updatedProgress)
            systems.showToast("🪙 Venta masiva: ${itemsToSell.size} objetos por $totalEarnedGold de oro.", "GOLD")
        }
    }

    fun changeScreen(screen: GameScreen) {
        // E12 — Gancho perezoso: sin expedición viva no hay mapa de expedición que enseñar.
        if (screen == GameScreen.EXPEDITION && !systems.expedition.value.active) {
            _screenState.value = GameScreen.DUNGEON
            return
        }

        _screenState.value = screen

        if (screen == GameScreen.SHOP && _shopItems.value.isEmpty()) {
            _progressState.value?.let {
                generateShopItems(it.charLevel)
            }
        }
        if (screen == GameScreen.CONTRACTS) systems.refreshContracts(false)
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

            // Starter items based on class
            val starterWeapon = when (_creatorClass.value) {
                "Guerrero" -> Item("w_start", "Espada de Madera", "WEAPON", "COMÚN", strBonus = 2, dmgBonus = 4, description = "Una espada simple tallada en pino.", itemLevel = 1, imageResName = "img_item_sword_1784593548868")
                "Mago" -> Item("w_start", "Vara del Aprendiz", "WEAPON", "COMÚN", intBonus = 2, dmgBonus = 3, description = "Canaliza leves destellos de magia.", itemLevel = 1, imageResName = "img_item_staff_1784593558118")
                "Pícaro" -> Item("w_start", "Daga Oxidada", "WEAPON", "COMÚN", dexBonus = 2, dmgBonus = 4, description = "Vieja y mellada, pero afilada.", itemLevel = 1, imageResName = "img_item_dagger_1784593567531")
                else -> Item("w_start", "Maza de Fresno", "WEAPON", "COMÚN", conBonus = 1, dmgBonus = 3, description = "Robusta maza para impartir justicia.", itemLevel = 1, imageResName = "img_item_sword_1784593548868")
            }

            val starterArmor = Item("a_start", "Harapos de Viaje", "ARMOR", "COMÚN", conBonus = 1, defBonus = 2, description = "Prenda básica que cubre lo justo.", itemLevel = 1, imageResName = "img_item_plate_1784593577913")

            val initialCon = _creatorCon.value + starterArmor.conBonus + starterWeapon.conBonus
            val initialInt = _creatorInt.value + starterArmor.intBonus + starterWeapon.intBonus
            val maxHp = (initialCon * 30) + 25 + 120
            val maxMp = (initialInt * 10) + 5 + 50

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
                equippedPetJson = "",
                inventoryJson = "[]",
                talentsJson = GameJsonParser.listToJson(talentsList),
                skillsJson = GameJsonParser.listToJson(skillsList),
                completedQuestsJson = "[]",
                mapPointsExploredJson = "[\"0,0\"]",
                currentX = 0,
                currentY = 0
            )

            repository.saveProgress(progress)

            // El controlador necesita la fila ya escrita (con su id real) antes de
            // conceder nada: si no, `currentProgress()` devolvería la partida anterior.
            isFirstLoad = true
            val stored = repository.getProgress()
            if (stored != null) {
                _progressState.value = stored
                systems.hydrate(stored)
            }

            _screenState.value = GameScreen.WORLD_MAP
            // La bestia inicial la concede `systems.hydrate(...)` (llamado arriba):
            // así también la reciben las partidas migradas, que nunca pasaron por aquí.
            systems.refreshContracts(force = true)
            showNotification("¡Tu aventura comienza en Eldoria, $name!")
        }
    }

    private fun getStarterSkills(cls: String): List<Skill> {
        return when (cls) {
            "Guerrero" -> listOf(
                Skill("g_1", "Golpe Sangriento", "Un ataque de fuerza bruta que inflige 1.6x de daño e inflige Herida Anti-Curación por 3 turnos (anula sanación enemiga).", manaCost = 10, minLevel = 1, damageMultiplier = 1.6, isAntiHeal = true),
                Skill("g_2", "Grito de Provocación", "Intimida al enemigo, aumentando tu defensa en un 30%.", manaCost = 12, minLevel = 2, damageMultiplier = 0.0, healingMultiplier = 0.0)
            )
            "Mago" -> listOf(
                Skill("m_1", "Centella Arcana", "Arroja una chispa arcana rápida que inflige 1.5x de daño mágico.", manaCost = 8, minLevel = 1, damageMultiplier = 1.5),
                Skill("m_2", "Llama Necrótica", "Desata fuego maldito. Daño 2.2x e impida que el enemigo se cure durante 3 turnos (Anti-Curación).", manaCost = 18, minLevel = 3, damageMultiplier = 2.2, isAntiHeal = true)
            )
            "Pícaro" -> listOf(
                Skill("p_1", "Puñalada Venenosa", "Ataque veloz a puntos débiles. Daño 1.4x e infecta con Veneno Anti-Curación por 3 turnos.", manaCost = 10, minLevel = 1, damageMultiplier = 1.4, isAntiHeal = true),
                Skill("p_2", "Ataque Sombrío", "Desaparece en las sombras e inflige 2.0x de daño sorpresa.", manaCost = 15, minLevel = 3, damageMultiplier = 2.0)
            )
            else -> listOf( // Clérigo
                Skill("c_1", "Luz Sagrada", "Invoca el poder de los cielos para curar tus heridas.", manaCost = 12, minLevel = 1, damageMultiplier = 0.0, healingMultiplier = 1.8),
                Skill("c_2", "Martillo de Justicia", "Golpea con fe pura, infligiendo 1.3x de daño, sanándote 20% y sellando la curación enemiga por 3 turnos (Anti-Curación).", manaCost = 14, minLevel = 2, damageMultiplier = 1.3, healingMultiplier = 0.5, isAntiHeal = true)
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

    fun generateMapAround(cx: Int, cy: Int, exploredJson: String, clearedJson: String = "[]") {
        val exploredSet = GameJsonParser.listFromJson<String>(exploredJson).toSet()
        val clearedSet = GameJsonParser.listFromJson<String>(clearedJson).toSet()
        val tiles = mutableListOf<MapTile>()

        // Este es el embudo por el que pasa todo movimiento: si el paso ha
        // cruzado una frontera, el tablón de encargos locales se rehace solo.
        _progressState.value?.let { systems.refreshKingdomBoard(cx, cy, it.charLevel) }

        // Generate a 5x5 viewing grid around player (cx, cy) for infinite viewport rendering
        for (dx in -2..2) {
            for (dy in -2..2) {
                val x = cx + dx
                val y = cy + dy
                val coordKey = "$x,$y"
                val isExplored = exploredSet.contains(coordKey)
                val isCleared = clearedSet.contains(coordKey)

                val kingdom = KingdomGenerator.getKingdomForCoords(x, y)

                // Deterministic seed for infinite procedural generation at coordinate (x,y)
                val seed = (x * 73856093) xor (y * 19349663)
                val random = Random(seed)

                val biome = if (x == 0 && y == 0) "Santuario Inicial"
                else kingdom.biomes[abs(seed % kingdom.biomes.size)]

                val levelReq = maxOf(1, 1 + (abs(x) + abs(y)) / 3)

                var specialName = ""
                // El hito del reino manda sobre cualquier otro encuentro: es la
                // única casilla irrepetible de toda la banda.
                val landmark = KingdomAtlas.isLandmarkTile(x, y)
                val encounterType = if (landmark != null) {
                    specialName = landmark.landmarkName
                    "LANDMARK"
                } else if (x == 0 && y == 0) {
                    specialName = "Santuario Inicial"
                    "SHRINE"
                } else if ((abs(x) % 12 == 0 && abs(y) % 12 == 0 && (x != 0 || y != 0)) || (x == 7 && y == -7) || (x == -10 && y == 10)) {
                    specialName = kingdom.castleNames[abs(seed) % kingdom.castleNames.size]
                    "CASTLE"
                } else {
                    val r = random.nextInt(100)
                    when {
                        r < 8 -> {
                            specialName = kingdom.merchantNames[abs(seed) % kingdom.merchantNames.size]
                            "SPECIAL_MERCHANT"
                        }
                        r < 16 -> {
                            specialName = "Gran Tesoro de " + kingdom.name.replace("Reino de ", "")
                            "TREASURE"
                        }
                        r < 24 -> {
                            specialName = "Altar de " + kingdom.name.replace("Reino de ", "")
                            "SHRINE"
                        }
                        r < 28 -> {
                            specialName = kingdom.bossNames[abs(seed) % kingdom.bossNames.size]
                            "BOSS"
                        }
                        else -> "MONSTER"
                    }
                }

                // Un hito nunca es terreno intransitable: sería un destino al que
                // no se puede llegar.
                val isObs = landmark == null &&
                    (biome.contains("Montaña") || biome.contains("Picos") || biome.contains("Cráter")) &&
                    (abs(x) + abs(y) > 1) && (random.nextInt(100) < 30)
                val isEnemy = (encounterType == "MONSTER" || encounterType == "BOSS") && !isCleared && (x != 0 || y != 0)

                val tileHasEncounter = when (encounterType) {
                    "CASTLE", "SPECIAL_MERCHANT" -> true
                    else -> !isCleared && (x != 0 || y != 0)
                }

                tiles.add(
                    MapTile(
                        x = x,
                        y = y,
                        biome = biome,
                        explored = isExplored,
                        cleared = isCleared,
                        hasEncounter = tileHasEncounter,
                        encounterType = encounterType,
                        levelRequirement = levelReq,
                        isBossLair = encounterType == "BOSS" && !isCleared,
                        isObstacle = isObs,
                        isEnemySpawn = isEnemy,
                        kingdomName = kingdom.name,
                        kingdomColor = kingdom.colorHex,
                        specialName = specialName
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
        if (distance == 0) {
            // Player is ALREADY standing on this tile -> trigger interaction if applicable
            if (!tile.cleared || tile.encounterType == "CASTLE" || tile.encounterType == "SPECIAL_MERCHANT") {
                triggerEncounter(tile)
            } else {
                showNotification("Este lugar ya ha sido explorado y sus energías reclamadas.")
            }
            return
        }

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
                    val originX = currentProgress.currentX
                    val originY = currentProgress.currentY
                    val exploredList = GameJsonParser.listFromJson<String>(currentProgress.mapPointsExploredJson).toMutableList()
                    val clearedList = GameJsonParser.listFromJson<String>(currentProgress.mapPointsClearedJson).toSet()
                    val tileKey = "${stepTile.x},${stepTile.y}"
                    val wasAlreadyExplored = exploredList.contains(tileKey)
                    val wasAlreadyCleared = clearedList.contains(tileKey)

                    val isMonsterEncounter = (stepTile.encounterType == "MONSTER" || stepTile.encounterType == "BOSS") && !wasAlreadyCleared

                    if (isMonsterEncounter) {
                        triggerEncounter(stepTile)

                        // Pause map movement until combat outcome is decided (victory is true or false)
                        while (_combatState.value.active && _combatState.value.victory == null) {
                            kotlinx.coroutines.delay(100)
                        }

                        val combatVictory = _combatState.value.victory == true
                        if (combatVictory) {
                            if (!exploredList.contains(tileKey)) {
                                exploredList.add(tileKey)
                                systems.progressRealmExploration(
                                    KingdomAtlas.entryForCoords(stepTile.x, stepTile.y).id
                                )
                            }
                            val postProgress = _progressState.value ?: currentProgress
                            // E11 — La casilla queda LIMPIA tras la victoria: si no, el
                            // mismo monstruo reaparecía cada vez que pasabas por encima.
                            val clearedAfter = GameJsonParser
                                .listFromJson<String>(postProgress.mapPointsClearedJson)
                                .toMutableList()
                            if (!clearedAfter.contains(tileKey)) clearedAfter.add(tileKey)
                            val clearedAfterJson = GameJsonParser.listToJson(clearedAfter.toList())

                            val updatedProgress = postProgress.copy(
                                currentX = stepTile.x,
                                currentY = stepTile.y,
                                mapPointsExploredJson = GameJsonParser.listToJson(exploredList),
                                mapPointsClearedJson = clearedAfterJson
                            )
                            saveProgressSynced(updatedProgress)
                            _progressState.value = updatedProgress
                            generateMapAround(stepTile.x, stepTile.y, GameJsonParser.listToJson(exploredList), clearedAfterJson)

                            // Wait until user exits combat screen
                            while (_combatState.value.active) {
                                kotlinx.coroutines.delay(100)
                            }
                        } else {
                            // Player was defeated or fled: return to previous tile
                            val postProgress = _progressState.value ?: currentProgress
                            val returnedProgress = postProgress.copy(
                                currentX = originX,
                                currentY = originY,
                                mapPointsExploredJson = GameJsonParser.listToJson(exploredList)
                            )
                            saveProgressSynced(returnedProgress)
                            generateMapAround(originX, originY, GameJsonParser.listToJson(exploredList), postProgress.mapPointsClearedJson)
                            showNotification("Te retiras al cuadro anterior. El enemigo sigue custodiando la casilla.")

                            while (_combatState.value.active) {
                                kotlinx.coroutines.delay(100)
                            }
                            break
                        }
                    } else {
                        if (!wasAlreadyExplored) {
                            exploredList.add(tileKey)
                            // Casilla nueva: cuenta para los encargos de cartografía
                            // del reino al que pertenece.
                            systems.progressRealmExploration(
                                KingdomAtlas.entryForCoords(stepTile.x, stepTile.y).id
                            )
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

                        saveProgressSynced(updatedProgress)
                        generateMapAround(stepTile.x, stepTile.y, GameJsonParser.listToJson(exploredList), currentProgress.mapPointsClearedJson)

                        val isDestination = stepTile.x == tile.x && stepTile.y == tile.y
                        if (isDestination || (!wasAlreadyCleared && (stepTile.encounterType == "SHRINE" || stepTile.encounterType == "CHEST" || stepTile.encounterType == "TREASURE"))) {
                            if (!wasAlreadyCleared || stepTile.encounterType == "CASTLE" || stepTile.encounterType == "SPECIAL_MERCHANT") {
                                triggerEncounter(stepTile)
                            } else {
                                systems.showToast("🧭 Viajas a ${stepTile.biome} ($tileKey).", "SILVER")
                            }
                        } else {
                            systems.showToast("🧭 Viajas a ${stepTile.biome} ($tileKey).", "SILVER")
                        }

                        while (_combatState.value.active) {
                            kotlinx.coroutines.delay(100)
                        }

                        if (_combatState.value.victory == false) {
                            break
                        }
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
        val tileKey = "${tile.x},${tile.y}"
        val clearedList = GameJsonParser.listFromJson<String>(progress.mapPointsClearedJson).toMutableList()

        when (tile.encounterType) {
            // ─── Hito del reino: el evento irrepetible de cada tierra ───
            "LANDMARK" -> {
                val entry = KingdomAtlas.isLandmarkTile(tile.x, tile.y)
                if (entry == null) {
                    showNotification("El hito se ha desvanecido antes de que llegaras.")
                    return
                }
                viewModelScope.launch {
                    val alreadyTaken = clearedList.contains(tileKey)
                    _landmarkState.value = LandmarkState(
                        active = true,
                        kingdomId = entry.id,
                        name = entry.landmarkName,
                        lore = entry.landmarkLore,
                        boon = entry.landmarkBoon,
                        kingdomName = KingdomAtlas.dataOf(entry).name,
                        claimed = alreadyTaken,
                        x = tile.x,
                        y = tile.y
                    )
                }
            }
            "SHRINE" -> {
                viewModelScope.launch {
                    if (clearedList.contains(tileKey)) {
                        showNotification("Este Santuario ya ha sido activado y sus energías están agotadas.")
                        return@launch
                    }
                    val healHp = (progress.maxHp * 0.4).toInt()
                    val healMp = (progress.maxMp * 0.4).toInt()
                    val newHp = minOf(progress.maxHp, progress.currentHp + healHp)
                    val newMp = minOf(progress.maxMp, progress.currentMp + healMp)

                    clearedList.add(tileKey)

                    val updated = progress.copy(
                        currentHp = newHp,
                        currentMp = newMp,
                        charGold = progress.charGold + 25,
                        mapPointsClearedJson = GameJsonParser.listToJson(clearedList)
                    )
                    saveProgressSynced(updated)
                    generateMapAround(progress.currentX, progress.currentY, updated.mapPointsExploredJson, updated.mapPointsClearedJson)
                    showNotification("¡Activaste un Santuario Ancestral! Sanas +$healHp HP, +$healMp MP y obtienes 25 de oro. El altar se ha agotado.")
                }
            }
            "CHEST" -> {
                viewModelScope.launch {
                    if (clearedList.contains(tileKey)) {
                        showNotification("Este cofre ya fue abierto y saqueado.")
                        return@launch
                    }
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

                    clearedList.add(tileKey)

                    val updated = progress.copy(
                        charGold = progress.charGold + goldGained,
                        inventoryJson = GameJsonParser.listToJson(invList),
                        mapPointsClearedJson = GameJsonParser.listToJson(clearedList)
                    )
                    val (finalProgress, equippedNames) = autoEquipProgress(updated)
                    saveProgressSynced(finalProgress)
                    generateMapAround(progress.currentX, progress.currentY, finalProgress.mapPointsExploredJson, finalProgress.mapPointsClearedJson)
                    if (equippedNames.isNotEmpty()) {
                        notificationMsg += " (Auto-Equipado: ${equippedNames.joinToString(", ")})"
                    }
                    showNotification(notificationMsg)
                }
            }
            "TREASURE" -> {
                viewModelScope.launch {
                    if (clearedList.contains(tileKey)) {
                        showNotification("Este Gran Tesoro Real ya ha sido reclamado.")
                        return@launch
                    }
                    val kingdom = KingdomGenerator.getKingdomForCoords(tile.x, tile.y)
                    val goldGained = random.nextInt(120, 350) + (tile.levelRequirement * 20)
                    val lootItem = generateProceduralItem(tile.levelRequirement + 1, isBoss = random.nextInt(100) < 40)
                    val kingdomTag = kingdom.name.replace("Reino de ", "")
                    val royalItem = lootItem.copy(name = "${lootItem.name} Real de $kingdomTag")

                    val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
                    invList.add(royalItem)

                    clearedList.add(tileKey)

                    var notificationMsg = "¡Abriste un Gran Tesoro Real de $kingdomTag! Encontraste $goldGained de oro y la reliquia: ${royalItem.name} (${royalItem.rarity})."

                    val updated = progress.copy(
                        charGold = progress.charGold + goldGained,
                        inventoryJson = GameJsonParser.listToJson(invList),
                        mapPointsClearedJson = GameJsonParser.listToJson(clearedList)
                    )
                    val (finalProgress, equippedNames) = autoEquipProgress(updated)
                    saveProgressSynced(finalProgress)
                    generateMapAround(progress.currentX, progress.currentY, finalProgress.mapPointsExploredJson, finalProgress.mapPointsClearedJson)
                    if (equippedNames.isNotEmpty()) {
                        notificationMsg += " (Auto-Equipado: ${equippedNames.joinToString(", ")})"
                    }
                    showNotification(notificationMsg)
                }
            }
            "CASTLE" -> {
                viewModelScope.launch {
                    val kingdom = KingdomGenerator.getKingdomForCoords(tile.x, tile.y)
                    val castleName = if (tile.specialName.isNotEmpty()) tile.specialName else kingdom.castleNames.first()
                    val isBlessingClaimed = clearedList.contains(tileKey)
                    _castleState.value = CastleState(
                        active = true,
                        castleName = castleName,
                        kingdomName = kingdom.name,
                        kingdomColor = kingdom.colorHex,
                        description = "Bastión supremo de ${kingdom.name}. Los nobles y altos comandantes rigen el reino desde esta fortaleza. Puedes solicitar la Bendición Real o desafiar al Campeón de la Corona.",
                        blessingClaimed = isBlessingClaimed,
                        tile = tile
                    )
                    showNotification("¡Has llegado a $castleName en ${kingdom.name}!")
                }
            }
            "SPECIAL_MERCHANT" -> {
                viewModelScope.launch {
                    val kingdom = KingdomGenerator.getKingdomForCoords(tile.x, tile.y)
                    val merchantName = if (tile.specialName.isNotEmpty()) tile.specialName else kingdom.merchantNames.random()
                    val levelReq = maxOf(1, tile.levelRequirement)

                    val items = mutableListOf<SpecialMerchantItem>()
                    val kingdomTag = kingdom.name.replace("Reino de ", "")
                    for (i in 1..4) {
                        val item = generateProceduralItem(levelReq + 2, isBoss = false, rarityPreset = "LEGENDARIO")
                        val kingdomItem = item.copy(
                            name = "${item.name} de $kingdomTag",
                            rarity = "LEGENDARIO"
                        )
                        val baseVal = 2500 + (levelReq * 120) + (i * 200)
                        val discount = random.nextInt(10, 25)
                        val discountedPrice = maxOf(1800, (baseVal * (100 - discount) / 100))
                        items.add(SpecialMerchantItem(kingdomItem, baseVal, discountedPrice, discount))
                    }

                    _specialMerchantState.value = SpecialMerchantState(
                        active = true,
                        merchantName = merchantName,
                        kingdomName = kingdom.name,
                        dialogue = "¡Saludos, noble viajero! Solo vendo reliquias de grado LEGENDARIO rescatadas en las zonas más peligrosas de ${kingdom.name}. Sus precios son elevados, reflejando su inmenso poder.",
                        items = items
                    )
                    showNotification("¡Encontraste a $merchantName con reliquias LEGENDARIAS exclusivas!")
                }
            }
            "MONSTER", "BOSS" -> {
                startCombat(tile)
            }
        }
    }

    fun claimCastleBlessing() {
        val castle = _castleState.value
        val progress = _progressState.value ?: return
        val tile = castle.tile
        if (!castle.active || castle.blessingClaimed || tile == null) return

        viewModelScope.launch {
            val tileKey = "${tile.x},${tile.y}"
            val clearedList = GameJsonParser.listFromJson<String>(progress.mapPointsClearedJson).toMutableList()
            if (!clearedList.contains(tileKey)) {
                clearedList.add(tileKey)
            }

            val goldBonus = 150
            val updatedProgress = progress.copy(
                currentHp = progress.maxHp,
                currentMp = progress.maxMp,
                charGold = progress.charGold + goldBonus,
                mapPointsClearedJson = GameJsonParser.listToJson(clearedList)
            )
            saveProgressSynced(updatedProgress)
            _castleState.value = castle.copy(blessingClaimed = true)
            showNotification("¡Restauras todo tu HP/MP y recibes $goldBonus de oro de la Bendición Real de ${castle.kingdomName}!")
        }
    }

    fun challengeCastleBoss() {
        val castle = _castleState.value
        val tile = castle.tile ?: return
        closeCastleDialog()
        startCombat(tile.copy(encounterType = "BOSS", specialName = "Campeón Real de ${castle.kingdomName}"))
    }

    fun closeCastleDialog() {
        _castleState.value = CastleState()
    }

    // ─── Hitos de reino ───

    fun closeLandmarkDialog() {
        _landmarkState.value = LandmarkState()
    }

    /**
     * Reclama el don del hito. Cada reino paga en su propia moneda: Eldoria
     * cura, Drakenhold da brasas de forja, Aetheria suelta un diamante infinito.
     * Se cobra una sola vez y la casilla queda marcada como limpia para siempre.
     */
    fun claimLandmarkBoon() {
        val state = _landmarkState.value
        val progress = _progressState.value ?: return
        val entry = KingdomAtlas.byId(state.kingdomId) ?: return
        if (state.claimed) {
            systems.showToast("🏛️ Ya tomaste lo que este lugar tenía que dar.", "IRON")
            return
        }

        viewModelScope.launch {
            val tileKey = "${state.x},${state.y}"
            val clearedList = GameJsonParser
                .listFromJson<String>(progress.mapPointsClearedJson)
                .toMutableList()
            if (!clearedList.contains(tileKey)) clearedList.add(tileKey)

            // Oro del hito: escala con el tier del reino.
            val goldBoon = entry.tier * entry.tier * 350

            val updated = progress.copy(
                currentHp = progress.maxHp,
                currentMp = progress.maxMp,
                charGold = progress.charGold + goldBoon,
                mapPointsClearedJson = GameJsonParser.listToJson(clearedList)
            )
            saveProgressSynced(updated)
            _progressState.value = updated

            // Materiales temáticos del reino, entregados por el controlador para
            // que el bolsón y los contratos de recolección se enteren.
            val drops: Map<String, Int> = when (entry.id) {
                "eldoria" -> emptyMap()
                "drakenhold" -> mapOf("forge_ember" to 3)
                "frostgard" -> mapOf("pure_crystal" to 2)
                "aethelgard" -> mapOf("shadow_essence" to 3, "anima_shard" to 1)
                "solaria" -> mapOf("ancient_relic" to 2)
                else -> mapOf("infinite_diamond" to 1)
            }
            if (drops.isNotEmpty()) systems.grantMaterials(drops)

            generateMapAround(updated.currentX, updated.currentY, updated.mapPointsExploredJson, updated.mapPointsClearedJson)
            _landmarkState.value = state.copy(claimed = true)

            showNotification(
                "🏛️ ${entry.landmarkName}\n\n${entry.landmarkBoon}\n\nRecuperas toda tu vida y maná, y recibes $goldBoon de oro."
            )
        }
    }

    fun buySpecialMerchantItem(specialItem: SpecialMerchantItem) {
        val progress = _progressState.value ?: return
        if (progress.charGold < specialItem.discountPrice) {
            showNotification("No tienes suficiente oro para comprar ${specialItem.item.name}.")
            return
        }

        viewModelScope.launch {
            val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
            invList.add(specialItem.item)

            val updated = progress.copy(
                charGold = progress.charGold - specialItem.discountPrice,
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            val (finalProgress, equippedNames) = autoEquipProgress(updated)
            saveProgressSynced(finalProgress)

            val currentMerchant = _specialMerchantState.value
            val updatedItems = currentMerchant.items.filter { it.item.id != specialItem.item.id }
            _specialMerchantState.value = currentMerchant.copy(items = updatedItems)

            var msg = "Compraste ${specialItem.item.name} por ${specialItem.discountPrice} oro (${specialItem.discountPercent}% OFF)."
            if (equippedNames.isNotEmpty()) {
                msg += " (Auto-Equipado: ${equippedNames.joinToString(", ")})"
            }
            showNotification(msg)
        }
    }

    fun closeSpecialMerchantDialog() {
        _specialMerchantState.value = SpecialMerchantState()
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  VIAJE ENTRE REINOS
    //  El mundo son seis anillos alrededor del Santuario. Caminar de Eldoria a
    //  Aetheria son más de cien casillas; la caravana lo hace en un pago, pero
    //  sólo lleva a quien tiene nivel para sobrevivir al destino.
    // ═══════════════════════════════════════════════════════════════════════

    fun travelToKingdom(kingdomId: String) {
        val progress = _progressState.value ?: return
        val entry = KingdomAtlas.byId(kingdomId)
        if (entry == null) {
            systems.showToast("🧭 Ese reino no figura en ningún atlas.", "IRON")
            return
        }

        val current = KingdomAtlas.entryForCoords(progress.currentX, progress.currentY)
        if (current.id == entry.id) {
            systems.showToast("🧭 Ya estás en ${entry.capitalName}… o muy cerca.", "SILVER")
            return
        }
        if (progress.charLevel < entry.requiredLevel) {
            systems.showToast(
                "🔒 La caravana no cruza a ese reino con un héroe de nivel ${progress.charLevel}: exige ${entry.requiredLevel}.",
                "IRON"
            )
            return
        }
        if (progress.charGold < entry.travelCost) {
            systems.showToast(
                "💰 El pasaje cuesta ${entry.travelCost} de oro y llevas ${progress.charGold}.",
                "IRON"
            )
            return
        }
        if (_combatState.value.active) {
            systems.showToast("⚔️ Termina el combate antes de partir.", "IRON")
            return
        }

        viewModelScope.launch {
            SoundManager.playButtonClick()

            val exploredList = GameJsonParser
                .listFromJson<String>(progress.mapPointsExploredJson)
                .toMutableList()
            val destinationKey = "${entry.capitalX},${entry.capitalY}"
            if (!exploredList.contains(destinationKey)) {
                exploredList.add(destinationKey)
                systems.progressRealmExploration(entry.id)
            }
            val exploredJson = GameJsonParser.listToJson(exploredList)

            // El viaje agota: se llega con la vida y el maná al máximo, pero
            // pagando el pasaje. Es un descanso caro, no un atajo gratis.
            val travelled = progress.copy(
                currentX = entry.capitalX,
                currentY = entry.capitalY,
                charGold = (progress.charGold - entry.travelCost).coerceAtLeast(0),
                currentHp = progress.maxHp,
                currentMp = progress.maxMp,
                mapPointsExploredJson = exploredJson
            )
            saveProgressSynced(travelled)
            _progressState.value = travelled
            generateMapAround(entry.capitalX, entry.capitalY, exploredJson, travelled.mapPointsClearedJson)

            // Al pisar tierra nueva, el tablón local se rehace.
            systems.refreshKingdomBoard(entry.capitalX, entry.capitalY, travelled.charLevel, force = true)

            val kingdomName = KingdomAtlas.dataOf(entry).name
            systems.showToast("🧭 La caravana te deja en ${entry.capitalName}. Bienvenido a $kingdomName.", "GOLD")
            _isAutoNavigation.value = false
        }
    }

    /** Reinos que el héroe ha pisado alguna vez. Se deduce de lo explorado. */
    fun discoveredKingdomIds(progress: GameProgress): Set<String> {
        val explored = GameJsonParser.listFromJson<String>(progress.mapPointsExploredJson)
        return KingdomAtlas.discoveredIds(explored, progress.currentX, progress.currentY)
    }

    fun moveDirection(dx: Int, dy: Int) {
        val p = _progressState.value ?: return
        val targetX = p.currentX + dx
        val targetY = p.currentY + dy
        val currentTiles = _proceduralMap.value
        val targetTile = currentTiles.find { it.x == targetX && it.y == targetY }
            ?: MapTile(x = targetX, y = targetY, biome = "Exploración", levelRequirement = maxOf(1, 1 + (abs(targetX) + abs(targetY)) / 3))
        selectTileAndExplore(targetTile)
    }

    private fun generateEnemyPetIfNeeded(monsterLevel: Int, rarity: String, isBoss: Boolean): EnemyPet? {
        val petChance = when {
            isBoss || rarity == "UNIVERSAL" || rarity == "LEGENDARY" -> 100
            rarity == "CHAMPION" -> 80
            rarity == "ELITE" -> 55
            else -> 35
        }

        if (Random.nextInt(100) >= petChance) return null

        val enemyPetsPool = listOf(
            Pair("Huargo de las Sombras", "img_pet_lobo_celestial"),
            Pair("Draco de la Ciénaga", "img_pet_dragon_sombras"),
            Pair("Gárgola de Obsidiana", "img_pet_titan_cristal"),
            Pair("Búho Abisal", "img_pet_gato_estelar"),
            Pair("Víbora de Sangre", "img_pet_serpiente_astral"),
            Pair("Cachorro de Fénix Demoniaco", "img_pet_fenix_cosmico"),
            Pair("Grifo Infernal", "img_pet_grifo_dorado"),
            Pair("Behemoth Sombrío", "img_pet_behemoth_vacio")
        )

        val (petName, imgRes) = enemyPetsPool.random()
        val petLevel = maxOf(1, monsterLevel + Random.nextInt(-1, 3))
        val petAttack = maxOf(8, (petLevel * 3.2 + Random.nextInt(4, 12)).toInt())

        return EnemyPet(
            name = petName,
            level = petLevel,
            attack = petAttack,
            imageResName = imgRes
        )
    }

    // --- COMBAT ENGINE ---
    private fun startCombat(tile: MapTile) {
        val progress = _progressState.value ?: return
        val isBoss = tile.encounterType == "BOSS"
        resetCombatAuxiliaries()

        // Map monsters scale relative to hero level: hero level up to hero level + 10 (random)
        val extraLevels = Random.nextInt(0, 11)
        val monsterLevel = maxOf(tile.levelRequirement, progress.charLevel + extraLevels)

        val roll = Random.nextInt(100)
        val rarity = when {
            isBoss -> "LEGENDARY"
            roll >= 92 -> "CHAMPION"
            roll >= 75 -> "ELITE"
            else -> "NORMAL"
        }

        val (hpMult, atkMult, defMult) = when (rarity) {
            "UNIVERSAL" -> Triple(3.8, 1.8, 1.55)
            "LEGENDARY" -> Triple(2.8, 1.6, 1.45)
            "CHAMPION" -> Triple(1.9, 1.4, 1.35)
            "ELITE" -> Triple(1.4, 1.25, 1.2)
            else -> Triple(1.0, 1.0, 1.0)
        }

        val kingdom = KingdomGenerator.getKingdomForCoords(tile.x, tile.y)

        // E7 — El bestiario decide qué criatura concreta aparece, con qué arquetipo
        // y con qué afijos; sus multiplicadores se aplican ENCIMA de los de rareza.
        val deco = systems.decorateEnemy(
            kingdomId = EldoriaBestiary.kingdomIdForCoords(tile.x, tile.y),
            level = monsterLevel,
            rarity = rarity,
            isBoss = isBoss
        )

        val baseName = if (isBoss) {
            if (tile.specialName.isNotEmpty()) tile.specialName else kingdom.bossNames.random()
        } else {
            deco.displayName
        }

        val name = when (rarity) {
            "CHAMPION" -> "👑 $baseName Campeón"
            "ELITE" -> "⭐ $baseName Élite"
            "LEGENDARY" -> "🔥 $baseName Leyenda"
            else -> baseName
        }

        // El enemigo se calibra contra el PODER REAL del héroe (equipo incluido),
        // no contra su nivel: el nivel no mide nada cuando la mitad de la fuerza
        // del jugador viene del equipo. `EldoriaBalance` fija cuántos turnos debe
        // durar el combate y despeja vida y ataque desde ahí.
        val hero = EldoriaBalance.measureHero(progress) { id -> getTalentRank(id) }
        val stats = EldoriaBalance.buildEnemy(
            hero = hero,
            rarity = rarity,
            enemyLevel = monsterLevel,
            isBoss = isBoss,
            // Los multiplicadores de rareza y del bestiario se combinan y entran
            // como modificadores; el grueso ya lo pone la forma del combate.
            hpMult = hpMult * deco.hpMult,
            atkMult = atkMult * deco.atkMult,
            defMult = defMult * deco.defMult
        )

        val hp = stats.hp
        val attack = stats.attack
        val defense = stats.defense

        val enemyPet = generateEnemyPetIfNeeded(monsterLevel, rarity, isBoss)

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
            rarity = rarity,
            pet = enemyPet,
            artKey = deco.artKey
        )

        val tierLabel = when (rarity) {
            "LEGENDARY" -> "¡¡JEFE LEGENDARIO!!"
            "CHAMPION" -> "¡Un poderoso Campeón!"
            "ELITE" -> "¡Un peligroso Élite!"
            else -> "Un monstruo común"
        }

        val petMsg = if (enemyPet != null) " acompañado por su mascota ${enemyPet.name} (Niv.${enemyPet.level})" else ""

        val affixNames = deco.affixes.mapNotNull { EldoriaBestiary.affix(it)?.name }
        val logs = mutableListOf(
            "¡Un salvaje ${enemy.name} (Nivel ${enemy.level})$petMsg bloquea tu camino! $tierLabel"
        )
        if (affixNames.isNotEmpty()) logs.add("☠️ Afijos: ${affixNames.joinToString(" · ")}")

        // Pasivas del equipo legendario o superior: se resuelven una vez, al
        // entrar. Son lo que equilibra que el enemigo pegue por encima de ti.
        val loadout = EldoriaPassives.loadoutOf(progress, getAllEquippedItems(progress))
        val shield = (progress.maxHp * loadout.runeShield).toInt()
        if (loadout.hasAny) {
            logs.add("✨ Pasivas activas: ${loadout.names.joinToString(" · ")}")
        }
        if (shield > 0) {
            logs.add("🛡️ El Escudo Rúnico te envuelve: absorbe los próximos $shield de daño.")
        }

        _combatState.value = CombatState(
            active = true,
            enemy = enemy,
            playerCurrentHp = progress.currentHp,
            playerCurrentMp = progress.currentMp,
            combatLogs = logs.toList(),
            playerTurn = true,
            victory = null,
            enemyArchetype = deco.archetype,
            enemyAffixes = deco.affixes,
            enemySpeciesId = deco.speciesId,
            petCooldown = 0,
            momentum = 0,
            runeShieldLeft = shield,
            activePassives = loadout.names,
            bossPhase = 1
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

            // Árbol nuevo: el contexto se saca del estado con el que se abre el
            // golpe, y todo lo que sume se aplica ENCIMA del talento viejo.
            val talents = heroTalentLoadout()
            val tctx = talentContextOf(currentCombat, progress)

            val baseDmg = (modifierStat * 0.6) + weaponDmg + Random.nextInt(3, 8)
            // El daño del héroe crece en centenas mientras su vida crece en
            // decenas de miles; este factor pone las dos en la misma escala.
            // Es el mismo que usa `measureHero` para calibrar al enemigo, así
            // que golpe y vida enemiga siempre se miden con la misma regla.
            val outputScale = EldoriaBalance.measureHero(progress) { id -> getTalentRank(id) }.outputScale
            var finalDmg = EldoriaBalance.scaleHeroDamage(
                (baseDmg * talentDmgMultiplier).toInt(), outputScale
            )

            // Racial damage bonus (Orco)
            val raceDmgMult = when {
                progress.charRace == "Orco" && progress.charLevel >= 100 -> 1.80
                progress.charRace == "Orco" && progress.charLevel >= 50 -> 1.45
                progress.charRace == "Orco" && progress.charLevel >= 20 -> 1.25
                progress.charRace == "Orco" -> 1.10
                else -> 1.0
            }
            finalDmg = (finalDmg * raceDmgMult).toInt()

            // Ímpetu acumulado con las paradas: hasta un +50 % de daño.
            val momentumMult = 1.0 + (currentCombat.momentum / 200.0)
            finalDmg = (finalDmg * momentumMult).toInt()

            // Filtro de Furia: multiplica igual que el ímpetu, así que las dos
            // cosas se combinan y beberlo tras una parada compensa de verdad.
            if (currentCombat.damageBuffTurns > 0) {
                finalDmg = (finalDmg * (1.0 + currentCombat.damageBuffPotency)).toInt()
            }

            // ─── Talentos ofensivos ───
            // Los tres factores se multiplican por separado en vez de sumarse:
            // así el que sube daño global, el que sube daño físico y el que sube
            // sólo el golpe básico se premian por combinarse, que es justo la
            // decisión que el árbol quiere que el jugador tome.
            finalDmg = (finalDmg *
                (1.0 + talents.value(TalentKind.DANO_TOTAL, tctx)) *
                (1.0 + talents.value(TalentKind.DANO_FISICO, tctx)) *
                (1.0 + talents.value(TalentKind.DANO_BASICO, tctx))).toInt()

            // ─── Pasivas ofensivas del equipo legendario ───
            val loadout = EldoriaPassives.loadoutOf(progress, getAllEquippedItems(progress))
            // Furia Creciente: premia aguantar. Se corta a los ocho turnos para
            // que no convierta un combate largo en una bola de nieve infinita.
            if (loadout.risingFury > 0.0) {
                val stacks = currentCombat.turnsFought.coerceAtMost(8)
                finalDmg = (finalDmg * (1.0 + loadout.risingFury * stacks)).toInt()
            }
            // Verdugo: el daño extra va donde hace falta, contra los grandes.
            val target = currentCombat.enemy
            val isBigTarget = target?.isBoss == true ||
                target?.rarity == "LEGENDARY" || target?.rarity == "CHAMPION"
            if (loadout.executioner > 0.0 && isBigTarget) {
                finalDmg = (finalDmg * (1.0 + loadout.executioner)).toInt()
            }

            // Armadura del enemigo: misma curva de rendimientos decrecientes que
            // se aplica al héroe, para que las dos mitades del combate midan igual.
            val enemyDef = currentCombat.enemy?.defense ?: 0
            // La penetración de talento entra por el mismo hueco que la de los
            // movimientos enemigos: es el porcentaje del golpe que se salta la
            // curva de armadura, no un descuento plano sobre la defensa.
            finalDmg = EldoriaBalance.mitigate(
                finalDmg, enemyDef, progress.charLevel,
                talents.value(TalentKind.PENETRACION, tctx).coerceIn(0.0, 0.85)
            ).coerceAtLeast(3)

            // Critical strike chance
            val baseCrit = 5 + (progress.statDex * 0.4) + (getTalentRank("t_8") * 3) +
                // El árbol da la probabilidad en fracción y aquí se trabaja en
                // porcentaje: un talento de 0,03 vale tres puntos de crítico.
                (talents.value(TalentKind.CRIT_PROB, tctx) * 100.0)
            val raceCritBonus = when {
                progress.charRace == "Elfo" && progress.charLevel >= 100 -> 40
                progress.charRace == "Elfo" && progress.charLevel >= 50 -> 25
                progress.charRace == "Elfo" && progress.charLevel >= 20 -> 15
                progress.charRace == "Elfo" -> 5
                progress.charRace == "Humano" && progress.charLevel >= 100 -> 25
                progress.charRace == "Humano" && progress.charLevel >= 50 -> 15
                progress.charRace == "Humano" && progress.charLevel >= 20 -> 10
                progress.charRace == "Humano" -> 5
                else -> 0
            }
            val critChance = baseCrit + raceCritBonus
            // Primer Golpe Crítico: el turno de apertura no se sortea. Un talento
            // que "a veces" abre con crítico no se nota; garantizarlo es lo que
            // hace que valga la pena planear el primer golpe.
            val guaranteedFirst = !currentCombat.firstStrikeUsed &&
                talents.has(TalentKind.PRIMER_GOLPE_CRITICO, tctx)
            val isCrit = guaranteedFirst || Random.nextInt(100) < critChance
            if (isCrit) {
                finalDmg = (finalDmg * (1.8 + talents.value(TalentKind.CRIT_MULT, tctx))).toInt()
                SoundManager.playCriticalHit()
            } else {
                SoundManager.playSwordSlash()
            }

            // Apply to enemy
            val enemy = currentCombat.enemy ?: return@launch
            val newEnemyHp = maxOf(0, enemy.currentHp - finalDmg)
            enemy.currentHp = newEnemyHp

            val critLabel = if (isCrit) " ¡CRÍTICO!" else ""
            var log = "Atacas a ${enemy.name} e infliges $finalDmg puntos de daño físico.$critLabel"

            // Orc Devastador Berserker healing (Lvl 20+)
            var currentPlayerHp = currentCombat.playerCurrentHp
            var currentPlayerMp = currentCombat.playerCurrentMp

            // ─── Pasivas que se cobran al golpear ───
            if (loadout.lifesteal > 0.0) {
                val drained = (finalDmg * loadout.lifesteal).toInt()
                if (drained > 0) {
                    currentPlayerHp = minOf(progress.maxHp, currentPlayerHp + drained)
                    log += " 🩸 Sed de Sangre te devuelve $drained HP."
                }
            }
            if (loadout.manaLeech > 0.0) {
                val siphoned = (finalDmg * loadout.manaLeech).toInt()
                if (siphoned > 0) {
                    currentPlayerMp = minOf(progress.maxMp, currentPlayerMp + siphoned)
                    log += " 🔮 Sanguijuela Arcana te devuelve $siphoned MP."
                }
            }
            // ─── Robo de vida y maná del árbol ───
            // Se cobra sobre el daño YA mitigado: robar sobre el daño bruto
            // convertiría a los enemigos acorazados en la mejor fuente de vida.
            val talentDrain = talents.value(TalentKind.ROBO_VIDA, tctx)
            if (talentDrain > 0.0) {
                val drained = (finalDmg * talentDrain).toInt()
                if (drained > 0) {
                    currentPlayerHp = minOf(progress.maxHp, currentPlayerHp + drained)
                    log += " 🩸 Tus talentos de sangre te devuelven $drained HP."
                }
            }
            val talentSiphon = talents.value(TalentKind.ROBO_MANA, tctx)
            if (talentSiphon > 0.0) {
                val siphoned = (finalDmg * talentSiphon).toInt()
                if (siphoned > 0) {
                    currentPlayerMp = minOf(progress.maxMp, currentPlayerMp + siphoned)
                    log += " 🔮 Tus talentos arcanos te devuelven $siphoned MP."
                }
            }
            if (progress.charRace == "Orco" && progress.charLevel >= 20) {
                val lifestealPct = when {
                    progress.charLevel >= 100 -> 0.35
                    progress.charLevel >= 50 -> 0.20
                    else -> 0.12
                }
                val orcHeal = (finalDmg * lifestealPct).toInt()
                if (orcHeal > 0) {
                    currentPlayerHp = minOf(progress.maxHp, currentPlayerHp + orcHeal)
                    log += " ¡Tu sed de sangre orca te sana +$orcHeal HP!"
                }
            }

            // Player's Pet Attack on Hero's turn
            // `working` acumula los cambios del turno: si se volviera a copiar del
            // snapshot original, la última escritura borraría la saciedad gastada.
            var working = progress
            var updatedEnemyHp = newEnemyHp
            val playerPet = GameJsonParser.fromJson<Item>(progress.equippedPetJson)
            if (playerPet != null && updatedEnemyHp > 0) {
                val petWpn = GameJsonParser.fromJson<Item>(progress.petEquippedWeaponJson)
                val petArm = GameJsonParser.fromJson<Item>(progress.petEquippedArmorJson)
                val petAcc = GameJsonParser.fromJson<Item>(progress.petEquippedAccessoryJson)

                val extraDmg = (petWpn?.dmgBonus ?: 0) + (petWpn?.strBonus ?: 0) + (petAcc?.strBonus ?: 0) + (petAcc?.intBonus ?: 0)
                val extraHeal = (petArm?.defBonus ?: 0) + (petArm?.conBonus ?: 0) + (petAcc?.conBonus ?: 0) + (petAcc?.hpRegen ?: 0)

                // Pasivo NERFEADO (×0.40): la mascota ya tiene órdenes activas propias.
                val satietyMult = if (progress.petSatiety >= 50) 1.25f else if (progress.petSatiety > 0) 1.0f else 0.6f
                // La mascota entra en la medida del héroe, así que se reescala
                // con él: si no, dejaría de aportar nada al subir el factor.
                val petDmg = EldoriaBalance.scaleHeroDamage(
                    (((playerPet.dmgBonus * 0.9 + progress.charLevel * 4 + progress.petLevel * 14 + playerPet.strBonus * 0.5 + extraDmg + Random.nextInt(10, 25)) * satietyMult) * 0.40f).toInt().coerceAtLeast(8),
                    outputScale
                )
                val petHeal = (((playerPet.hpRegen * 2 + progress.petLevel * 6 + playerPet.conBonus * 0.5 + extraHeal + Random.nextInt(8, 15)) * satietyMult) * 0.40f).toInt().coerceAtLeast(4)

                // Rama Bestia: multiplica el mordisco de la mascota, no el del
                // héroe, para que invertir ahí cambie de verdad cómo se pelea.
                val petBite = (petDmg * (1.0 + talents.value(TalentKind.DANO_MASCOTA, tctx))).toInt()

                updatedEnemyHp = maxOf(0, updatedEnemyHp - petBite)
                enemy.currentHp = updatedEnemyHp

                val currentPetSatiety = maxOf(0, progress.petSatiety - 1)
                currentPlayerHp = minOf(progress.maxHp, currentPlayerHp + petHeal)

                log += "\n🐾 [Mascota ${playerPet.name} Niv.${progress.petLevel}] ¡Ataca asestando +$petBite de daño a ${enemy.name} y te cura +$petHeal HP!"

                working = working.copy(
                    currentHp = currentPlayerHp,
                    petSatiety = currentPetSatiety
                )
            }

            _combatState.value = currentCombat.copy(
                playerCurrentHp = currentPlayerHp,
                playerCurrentMp = currentPlayerMp,
                playerTurn = false,
                damageFeedbackEnemy = "-$finalDmg HP$critLabel",
                combatLogs = currentCombat.combatLogs + log,
                activeAnimation = "PLAYER_ATTACK",
                turnsFought = currentCombat.turnsFought + 1,
                // El primer golpe ya se gastó, aunque el crítico haya salido por
                // suerte: si no se marcara aquí, el talento seguiría regalando
                // críticos garantizados hasta el fin del combate.
                firstStrikeUsed = true
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

            // Sync player HP updates to database (una sola escritura por ataque)
            val latestHp = _combatState.value.playerCurrentHp
            val progressAfterAttack = working.copy(
                currentHp = latestHp
            )
            saveProgressSynced(progressAfterAttack)

            kotlinx.coroutines.delay(1000)
            _combatState.value = _combatState.value.copy(damageFeedbackEnemy = null, activeAnimation = null)

            // El golpe de la mascota también mata: hay que mirar la vida REAL del
            // enemigo, no la que quedaba tras el golpe del héroe.
            if (updatedEnemyHp <= 0) {
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
            if (skill.healingMultiplier > 0.0) {
                SoundManager.playHealPotion()
            } else {
                SoundManager.playMagicSpell()
            }
            // Mana cost discount talent and Elf passive
            val manaCostDiscount = if (getTalentRank("t_6") > 0) 0.8 else 1.0
            val raceManaDiscount = when {
                progress.charRace == "Elfo" && progress.charLevel >= 100 -> 0.50
                progress.charRace == "Elfo" && progress.charLevel >= 50 -> 0.65
                progress.charRace == "Elfo" && progress.charLevel >= 20 -> 0.80
                else -> 1.0
            }
            val finalManaCost = (skill.manaCost * manaCostDiscount * raceManaDiscount).toInt()
            val newPlayerMp = maxOf(0, currentCombat.playerCurrentMp - finalManaCost)

            // Spells power talent
            val spellMult = 1.0 + (getTalentRank("t_4") * 0.04)

            // Árbol nuevo, mismo criterio que en el golpe básico: se suma encima.
            val talents = heroTalentLoadout()
            val tctx = talentContextOf(currentCombat, progress)

            // Mismo reescalado que el golpe básico: si sólo se aplicara a uno de
            // los dos, las habilidades se volverían inútiles. Lo comparten el
            // daño de la habilidad y el acoso de la mascota que la acompaña.
            val outputScale = EldoriaBalance.measureHero(progress) { id -> getTalentRank(id) }.outputScale

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
                    progress.charRace == "Orco" && progress.charLevel >= 100 -> 1.80
                    progress.charRace == "Orco" && progress.charLevel >= 50 -> 1.45
                    progress.charRace == "Orco" && progress.charLevel >= 20 -> 1.25
                    progress.charRace == "Orco" -> 1.10
                    else -> 1.0
                }
                // Ímpetu acumulado con las paradas: hasta un +50 % de daño.
                val momentumMult = 1.0 + (currentCombat.momentum / 200.0)
                val furyMult = if (currentCombat.damageBuffTurns > 0)
                    1.0 + currentCombat.damageBuffPotency else 1.0
                // La rama mágica o la física según con qué pegue la clase: un
                // Mago no debería sacar nada de un talento de daño físico, y al
                // revés. Es lo que hace que las ramas signifiquen algo por clase.
                val schoolMult = if (isMagic)
                    1.0 + talents.value(TalentKind.DANO_MAGICO, tctx)
                else
                    1.0 + talents.value(TalentKind.DANO_FISICO, tctx)
                val talentSkillMult =
                    (1.0 + talents.value(TalentKind.DANO_TOTAL, tctx)) *
                    schoolMult *
                    (1.0 + talents.value(TalentKind.DANO_HABILIDAD, tctx))

                var finalSkillDmg = EldoriaBalance.scaleHeroDamage(
                    (baseSkillDmg * skill.damageMultiplier * spellMult * raceDmgMult *
                        momentumMult * furyMult * talentSkillMult).toInt(),
                    outputScale
                )

                // Defense mitigation — misma curva que el ataque básico, con la
                // misma penetración: si sólo perforara el golpe básico, invertir
                // en penetración castigaría a las clases que viven de habilidades.
                val enemyDef = currentCombat.enemy?.defense ?: 0
                finalSkillDmg = EldoriaBalance.mitigate(
                    finalSkillDmg, enemyDef, progress.charLevel,
                    talents.value(TalentKind.PENETRACION, tctx).coerceIn(0.0, 0.85)
                ).coerceAtLeast(4)

                // Primer Golpe Crítico: vale igual si abres con habilidad. Si sólo
                // contara el ataque básico, el talento sería una trampa para el
                // que juega lanzando su mejor conjuro de salida.
                var critLabel = ""
                if (!currentCombat.firstStrikeUsed &&
                    talents.has(TalentKind.PRIMER_GOLPE_CRITICO, tctx)
                ) {
                    finalSkillDmg =
                        (finalSkillDmg * (1.8 + talents.value(TalentKind.CRIT_MULT, tctx))).toInt()
                    critLabel = " ¡CRÍTICO!"
                    SoundManager.playCriticalHit()
                }

                currentEnemyHp = maxOf(0, currentEnemyHp - finalSkillDmg)
                currentCombat.enemy?.currentHp = currentEnemyHp
                damageFeedbackEnemy = "-$finalSkillDmg HP (${skill.name})$critLabel"
                log = "Usas ${skill.name} contra ${currentCombat.enemy?.name} e infliges $finalSkillDmg de daño.$critLabel"
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

            var newAntiHealTurns = currentCombat.enemyAntiHealTurns
            if (skill.isAntiHeal) {
                newAntiHealTurns = 3
                val antiHealMsg = "🚫 ¡Aplicas [Anti-Curación]! Se bloquea la sanación enemiga por 3 turnos."
                log = if (log.isEmpty()) antiHealMsg else "$log $antiHealMsg"
            }

            // Player's Pet Attack on Skill Turn
            val playerPet = GameJsonParser.fromJson<Item>(progress.equippedPetJson)
            val enemyObj = currentCombat.enemy
            if (playerPet != null && enemyObj != null && currentEnemyHp > 0) {
                val petWpn = GameJsonParser.fromJson<Item>(progress.petEquippedWeaponJson)
                val petArm = GameJsonParser.fromJson<Item>(progress.petEquippedArmorJson)
                val petAcc = GameJsonParser.fromJson<Item>(progress.petEquippedAccessoryJson)

                val extraDmg = (petWpn?.dmgBonus ?: 0) + (petWpn?.strBonus ?: 0) + (petAcc?.strBonus ?: 0) + (petAcc?.intBonus ?: 0)
                val extraHeal = (petArm?.defBonus ?: 0) + (petArm?.conBonus ?: 0) + (petAcc?.conBonus ?: 0) + (petAcc?.hpRegen ?: 0)

                // Pasivo NERFEADO (×0.40): la mascota ya tiene órdenes activas propias.
                val satietyMult = if (progress.petSatiety >= 50) 1.25f else if (progress.petSatiety > 0) 1.0f else 0.6f
                val petDmg = EldoriaBalance.scaleHeroDamage(
                    (((playerPet.dmgBonus * 0.9 + progress.charLevel * 4 + progress.petLevel * 14 + playerPet.strBonus * 0.5 + extraDmg + Random.nextInt(10, 25)) * satietyMult) * 0.40f).toInt().coerceAtLeast(8),
                    outputScale
                )
                val petHeal = (((playerPet.hpRegen * 2 + progress.petLevel * 6 + playerPet.conBonus * 0.5 + extraHeal + Random.nextInt(8, 15)) * satietyMult) * 0.40f).toInt().coerceAtLeast(4)

                // Mismo bono de bestia que en el turno de ataque básico: la
                // mascota no pega menos por acompañar a una habilidad.
                val petBite = (petDmg * (1.0 + talents.value(TalentKind.DANO_MASCOTA, tctx))).toInt()

                currentEnemyHp = maxOf(0, currentEnemyHp - petBite)
                enemyObj.currentHp = currentEnemyHp

                currentPlayerHp = minOf(progress.maxHp, currentPlayerHp + petHeal)

                log += "\n🐾 [Mascota ${playerPet.name} Niv.${progress.petLevel}] ¡Ataca coordinadamente con tu habilidad asestando +$petBite de daño y te cura +$petHeal HP!"
            }

            // Synchronize player health and mana to database
            val progressAfterSkill = progress.copy(
                currentHp = currentPlayerHp,
                currentMp = newPlayerMp
            )
            saveProgressSynced(progressAfterSkill)

            _combatState.value = currentCombat.copy(
                playerCurrentHp = currentPlayerHp,
                playerCurrentMp = newPlayerMp,
                enemyAntiHealTurns = newAntiHealTurns,
                playerTurn = false,
                damageFeedbackEnemy = damageFeedbackEnemy,
                damageFeedbackPlayer = damageFeedbackPlayer,
                combatLogs = currentCombat.combatLogs + log,
                activeAnimation = if (skill.healingMultiplier > 0.0) "PLAYER_HEAL" else "PLAYER_MAGIC",
                lastSkillId = skill.id,
                // Un turno gastado en habilidad es un turno peleado: sin esto,
                // las condiciones de "combate largo" nunca se cumplirían para
                // quien juega lanzando conjuros.
                turnsFought = currentCombat.turnsFought + 1,
                firstStrikeUsed = true
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

    /**
     * Bebe un frasco del inventario.
     *
     * @param potionId id del frasco concreto. Vacío = el primero que haya, que
     *        es lo que hacía el botón único de antes y lo que sigue haciendo el
     *        auto-combate.
     */
    fun usePotionCombat(potionId: String = "") {
        val currentCombat = _combatState.value
        val progress = _progressState.value ?: return
        if (!currentCombat.active || !currentCombat.playerTurn || currentCombat.victory != null) return

        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        val potionIndex = if (potionId.isBlank()) {
            invList.indexOfFirst { it.type == "POTION" }
        } else {
            invList.indexOfFirst { it.type == "POTION" && it.id.startsWith(potionId) }
        }

        if (potionIndex == -1) {
            showNotification("¡No tienes esa poción en tu inventario!")
            return
        }

        val item = invList[potionIndex]
        val spec = EldoriaPotions.fromItem(item.id, item.name)

        viewModelScope.launch {
            SoundManager.playHealPotion()

            // ─── Talentos de alquimia ───
            // POTENCIA escala lo que hace el frasco y DURACIÓN los turnos que
            // aguanta; se leen antes de resolver el efecto para que la misma
            // poción valga más en manos de quien invirtió en la rama.
            val talents = heroTalentLoadout()
            val tctx = talentContextOf(currentCombat, progress)
            val potency = 1.0 + talents.value(TalentKind.POCION_POTENCIA, tctx)
            val extraTurns = talents.value(TalentKind.POCION_DURACION, tctx).toInt()

            // AHORRO: a veces el frasco no se gasta. Se tira ANTES de aplicar el
            // efecto para no tener que deshacer nada, y el jugador ve el efecto
            // completo igual: lo único que cambia es si el frasco vuelve al zurrón.
            val saved = Random.nextDouble() < talents.value(TalentKind.POCION_AHORRO, tctx)
            if (!saved) invList.removeAt(potionIndex)

            var newHp = currentCombat.playerCurrentHp
            var newMp = currentCombat.playerCurrentMp
            var feedback = ""
            var log: String

            // Los buffs NO se acumulan consigo mismos: beber dos frascos del
            // mismo tipo renueva la duración y se queda con la mayor potencia.
            // Sin esta regla la jugada óptima seria encadenar el mismo frasco.
            var regenT = currentCombat.regenTurns
            var regenP = currentCombat.regenPotency
            var dmgT = currentCombat.damageBuffTurns
            var dmgP = currentCombat.damageBuffPotency
            var evaT = currentCombat.evasionTurns
            var evaP = currentCombat.evasionPotency
            var wardT = currentCombat.wardTurns
            var wardP = currentCombat.wardPotency

            // Valores ya escalados por los talentos. La evasión se corta al 75 %
            // porque una esquiva casi segura deja al enemigo sin turno y el
            // combate se vuelve una animación en vez de una pelea.
            val effTurns = spec.turns + extraTurns
            val effPotency = spec.potency * potency

            when (spec.effect) {
                PotionEffect.RESTORE -> {
                    val heal = (progress.maxHp * spec.healPct * potency).toInt()
                    val mana = (progress.maxMp * spec.manaPct * potency).toInt()
                    newHp = minOf(progress.maxHp, newHp + heal)
                    newMp = minOf(progress.maxMp, newMp + mana)
                    feedback = "+$heal HP / +$mana MP"
                    log = "Bebes ${spec.name}: recuperas $heal de salud y $mana de maná."
                }
                PotionEffect.REGEN -> {
                    regenT = maxOf(regenT, effTurns)
                    regenP = maxOf(regenP, effPotency)
                    feedback = "REGENERACIÓN"
                    log = "Bebes ${spec.name}: te curarás un ${(effPotency * 100).toInt()} % " +
                        "al principio de cada turno durante $effTurns turnos."
                }
                PotionEffect.DAMAGE -> {
                    dmgT = maxOf(dmgT, effTurns)
                    dmgP = maxOf(dmgP, effPotency)
                    feedback = "+${(effPotency * 100).toInt()} % DAÑO"
                    log = "Bebes ${spec.name}: tus golpes hacen un ${(effPotency * 100).toInt()} % " +
                        "más de daño durante $effTurns turnos."
                }
                PotionEffect.EVASION -> {
                    evaT = maxOf(evaT, effTurns)
                    evaP = maxOf(evaP, effPotency.coerceAtMost(0.75))
                    feedback = "+${(evaP * 100).toInt()} % EVASIÓN"
                    log = "Bebes ${spec.name}: esquivarás por completo un ${(evaP * 100).toInt()} % " +
                        "de los golpes durante $effTurns turnos."
                }
                PotionEffect.DEFENSE -> {
                    wardT = maxOf(wardT, effTurns)
                    wardP = maxOf(wardP, effPotency.coerceAtMost(0.75))
                    feedback = "-${(wardP * 100).toInt()} % DAÑO RECIBIDO"
                    log = "Bebes ${spec.name}: recibes un ${(wardP * 100).toInt()} % menos " +
                        "de daño durante $effTurns turnos."
                }
            }

            if (saved) {
                log += " ✨ Apuras el frasco sin gastarlo: aún te queda."
            }

            val updatedProgress = progress.copy(
                currentHp = newHp,
                currentMp = newMp,
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            saveProgressSynced(updatedProgress)

            _combatState.value = currentCombat.copy(
                playerCurrentHp = newHp,
                playerCurrentMp = newMp,
                playerTurn = false,
                damageFeedbackPlayer = feedback,
                combatLogs = currentCombat.combatLogs + log,
                activeAnimation = "PLAYER_POTION",
                regenTurns = regenT, regenPotency = regenP,
                damageBuffTurns = dmgT, damageBuffPotency = dmgP,
                evasionTurns = evaT, evasionPotency = evaP,
                wardTurns = wardT, wardPotency = wardP
            )

            kotlinx.coroutines.delay(1000)
            _combatState.value = _combatState.value.copy(damageFeedbackPlayer = null, activeAnimation = null)

            executeEnemyTurn()
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  E8 — INTENCIÓN ENEMIGA, VENTANA DE REACCIÓN Y ÓRDENES DE MASCOTA
    // ═══════════════════════════════════════════════════════════════════════

    /** Movimiento que el enemigo ya tiene elegido para su turno actual. */
    private var pendingEnemyMove: String = "BASIC"

    /** 1f = daño íntegro, 0.5f = reacción buena, 0f = parada perfecta. */
    private var pendingReactionMitigation: Float = 1f
    private var pendingReactionCounter: Boolean = false
    private var pendingMpDrainBlocked: Boolean = false

    /** Guardia de la bestia: absorbe daño en el próximo golpe enemigo. */
    private var petGuardAbsorb: Int = 0
    private var petGuardFull: Boolean = false
    private var petGuardianUsed: Boolean = false

    /** Veneno de Embestida (rasgo Colmillo Venenoso). */
    private var petPoisonTurns: Int = 0
    private var petPoisonDamage: Int = 0

    /** Deja el estado auxiliar de combate limpio al empezar una pelea nueva. */
    private fun resetCombatAuxiliaries() {
        pendingEnemyMove = "BASIC"
        pendingReactionMitigation = 1f
        pendingReactionCounter = false
        pendingMpDrainBlocked = false
        petGuardAbsorb = 0
        petGuardFull = false
        petGuardianUsed = false
        petPoisonTurns = 0
        petPoisonDamage = 0
    }

    private fun intentLabel(move: String): String = when (move) {
        "ARMOR_PIERCE" -> "Perforación de Armadura"
        "TRUE_STRIKE" -> "Golpe Certero"
        "POISON" -> "Veneno Corrosivo"
        "FREEZE" -> "Congelación Arcana"
        "BLEED" -> "Hemorragia Mortal"
        "BOSS_FURY" -> "Ira de Jefe"
        "REGEN_SHIELD" -> "Escudo de Sangre"
        else -> "Golpe Directo"
    }

    private fun intentIcon(move: String): String = when (move) {
        "ARMOR_PIERCE" -> "🗡️"
        "TRUE_STRIKE" -> "🎯"
        "POISON" -> "🧪"
        "FREEZE" -> "❄️"
        "BLEED" -> "🩸"
        "BOSS_FURY" -> "🔥"
        "REGEN_SHIELD" -> "🖤"
        else -> "💥"
    }

    /** Ímpetu que cuesta una parada perfecta. Sin él, el tiempo perfecto sólo bloquea. */
    private val PERFECT_PARRY_COST = 30

    /**
     * Lo que deja pasar un bloqueo normal. Antes era 0.5 —la mitad del golpe— y
     * con una banda de acierto ancha eso volvía el combate inofensivo.
     */
    private val GOOD_PARRY_MITIGATION = 0.65f

    /** Duración de la ventana de reacción; la asistencia la alarga a 1600 ms. */
    private fun reactionWindowMillis(): Long =
        if (systems.settings.value.reactionAssist) 1600L else 1100L

    /**
     * Elige el movimiento del enemigo AL TERMINAR el turno del jugador, lo publica
     * como intención y abre la ventana de reacción. El turno enemigo posterior usa
     * este movimiento tal cual: no se vuelve a sortear.
     */
    private fun rollEnemyIntent() {
        val state = _combatState.value
        val enemy = state.enemy ?: return
        if (!state.active || state.victory != null) return

        val skillTriggerChance = when {
            enemy.isBoss || enemy.rarity == "UNIVERSAL" || enemy.rarity == "LEGENDARY" -> 85
            enemy.rarity == "CHAMPION" || enemy.rarity == "ELITE" -> 65
            else -> 40
        }

        val move = if (Random.nextInt(100) >= skillTriggerChance) {
            "BASIC"
        } else {
            val pool = mutableListOf("ARMOR_PIERCE", "TRUE_STRIKE", "POISON", "FREEZE", "BLEED")
            if (enemy.isBoss || enemy.rarity == "CHAMPION" || enemy.rarity == "ELITE" ||
                enemy.rarity == "LEGENDARY" || enemy.rarity == "UNIVERSAL"
            ) {
                pool.add("BOSS_FURY")
                pool.add("REGEN_SHIELD")
            }
            pool.random()
        }

        pendingEnemyMove = move
        pendingReactionMitigation = 1f
        pendingReactionCounter = false
        pendingMpDrainBlocked = false

        _combatState.value = _combatState.value.copy(
            enemyIntent = intentLabel(move),
            enemyIntentIcon = intentIcon(move),
            reactionWindow = true,
            reactionDeadline = System.currentTimeMillis() + reactionWindowMillis()
        )
    }

    /** Daño del contraataque de una parada perfecta. */
    private fun counterAttackDamage(progress: GameProgress, enemy: Combatant, momentum: Int): Int {
        val weapon = GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)
        val isRogue = progress.charClass == "Pícaro"
        val modifierStat = if (isRogue) (progress.statDex + (weapon?.dexBonus ?: 0))
        else (progress.statStr + (weapon?.strBonus ?: 0))
        val base = (modifierStat * 0.6) + (weapon?.dmgBonus ?: 0) + Random.nextInt(3, 8)
        val raw = (base * 0.75 * (1.0 + momentum / 200.0)).toInt()
        return maxOf(5, raw - (enemy.defense / 2))
    }

    /**
     * Resultado del anillo de parada: "PERFECTO" anula el golpe y contraataca,
     * "BUENO" lo mitiga a la mitad, "FALLO" lo deja pasar entero.
     */
    fun executeReaction(quality: String) {
        val state = _combatState.value
        val progress = _progressState.value ?: return
        val enemy = state.enemy ?: return
        if (!state.active || state.victory != null || !state.reactionWindow) return

        val grade = quality.trim().uppercase()
        val log: String
        var momentum = state.momentum
        var counterDealt = 0

        // Ímpetu de talento: multiplica lo que GANA cada parada en vez de subir
        // el techo. Así el talento premia parar bien —que es la jugada que
        // queremos enseñar— y no simplemente aguantar turnos.
        val talents = heroTalentLoadout()
        val tctx = talentContextOf(state, progress)
        val momentumMult = 1.0 + talents.value(TalentKind.IMPETU_GANANCIA, tctx)

        // La parada perfecta SE PAGA. Antes era gratis e ilimitada: bastaba con
        // acertar el aro para anular el golpe y contraatacar todos los turnos, y
        // el combate dejaba de tener riesgo. Ahora el ímpetu es su munición, así
        // que compite con el daño que ese mismo ímpetu te da y no se puede
        // encadenar indefinidamente.
        val perfectAffordable = state.momentum >= PERFECT_PARRY_COST
        val effectiveGrade = if (grade == "PERFECTO" && !perfectAffordable) "BUENO_SIN_IMPETU" else grade

        when (effectiveGrade) {
            "PERFECTO" -> {
                pendingReactionMitigation = 0f
                pendingReactionCounter = true
                counterDealt = counterAttackDamage(progress, enemy, state.momentum)
                enemy.currentHp = maxOf(0, enemy.currentHp - counterDealt)
                momentum = (momentum - PERFECT_PARRY_COST).coerceAtLeast(0)
                log = "🛡️ ¡PARADA PERFECTA! Desvías ${intentLabel(pendingEnemyMove).lowercase()} y " +
                    "contraatacas por $counterDealt de daño. (−$PERFECT_PARRY_COST de ímpetu)"
                SoundManager.playCriticalHit()
            }
            "BUENO_SIN_IMPETU" -> {
                // El tiempo era perfecto pero no había con qué pagarlo: se queda
                // en bloqueo y el aviso explica por qué, que si no parece un fallo
                // del juego.
                pendingReactionMitigation = GOOD_PARRY_MITIGATION
                val gained = (14 * momentumMult).toInt()
                momentum = (momentum + gained).coerceAtMost(100)
                log = "🛡️ Tiempo perfecto, pero te faltó ímpetu para la parada " +
                    "(necesitas $PERFECT_PARRY_COST). Bloqueas y ganas +$gained de ímpetu."
                SoundManager.playSwordSlash()
            }
            "BUENO" -> {
                pendingReactionMitigation = GOOD_PARRY_MITIGATION
                val gained = (10 * momentumMult).toInt()
                momentum = (momentum + gained).coerceAtMost(100)
                log = "🛡️ Bloqueas a tiempo: el golpe pierde fuerza. (+$gained de ímpetu)"
                SoundManager.playSwordSlash()
            }
            else -> {
                pendingReactionMitigation = 1f
                log = "💢 Reaccionas tarde: el golpe entra limpio."
            }
        }

        _combatState.value = _combatState.value.copy(
            momentum = momentum,
            reactionWindow = false,
            combatLogs = _combatState.value.combatLogs + log,
            damageFeedbackEnemy =
                if (counterDealt > 0) "-$counterDealt HP ⚔️" else _combatState.value.damageFeedbackEnemy
        )
    }

    /**
     * Órdenes activas de la bestia: EMBESTIDA (daño), GUARDIA (absorción) y
     * ALIENTO (curación). Consumen el enfriamiento del perfil de la mascota.
     */
    fun executePetCommand(commandId: String) {
        val state = _combatState.value
        val progress = _progressState.value ?: return
        val enemy = state.enemy ?: return
        if (!state.active || state.victory != null) return

        if (state.petCooldown > 0) {
            systems.showToast("🐾 Tu bestia aún recupera el aliento (${state.petCooldown} turnos).", "IRON")
            return
        }

        val profile = systems.petCombatProfile()
        if (profile.petId.isBlank()) {
            systems.showToast("🐾 No llevas ninguna bestia al combate.", "IRON")
            return
        }

        val command = commandId.trim().uppercase()
        var log: String
        var playerHp = state.playerCurrentHp
        var enemyHp = enemy.currentHp
        var damageDealt = 0
        var healedAmount = 0
        val maxHp = progress.maxHp.coerceAtLeast(1)

        when (command) {
            "EMBESTIDA" -> {
                val raw = (profile.attack * 1.6f).toInt() + Random.nextInt(4, 14)
                val damage = maxOf(6, raw - (enemy.defense / 3))
                damageDealt = damage
                enemyHp = maxOf(0, enemyHp - damage)
                enemy.currentHp = enemyHp
                log = "🐾 ¡${profile.name} embiste a ${enemy.name} por $damage de daño!"
                if (profile.traits.contains(EldoriaPets.TRAIT_COLMILLO_VENENOSO)) {
                    petPoisonTurns = 3
                    petPoisonDamage = maxOf(3, damage / 6)
                    log += " Sus colmillos dejan veneno por 3 turnos ($petPoisonDamage por turno)."
                }
                SoundManager.playSwordSlash()
            }

            "GUARDIA" -> {
                petGuardAbsorb = (profile.guard * 2.2f).toInt().coerceAtLeast(10)
                petGuardFull = profile.traits.contains(EldoriaPets.TRAIT_GUARDIAN_LEAL) && !petGuardianUsed
                if (petGuardFull) {
                    petGuardianUsed = true
                    log = "🐾 ${profile.name} se planta delante de ti: como Guardián Leal, absorberá el próximo golpe ENTERO."
                } else {
                    log = "🐾 ${profile.name} monta guardia y absorberá hasta $petGuardAbsorb de daño del próximo golpe."
                }
                SoundManager.playButtonClick()
            }

            "ALIENTO" -> {
                val healed = (profile.heal * 1.8f).toInt().coerceAtLeast(8)
                val before = playerHp
                playerHp = minOf(maxHp, playerHp + healed)
                healedAmount = playerHp - before
                log = "🐾 El aliento de ${profile.name} te restaura $healedAmount de vida."
                if (profile.traits.contains(EldoriaPets.TRAIT_BENDICION_SERENA)) {
                    pendingMpDrainBlocked = true
                    log += " Su bendición serena disipa el drenaje de maná que venía en camino."
                }
                SoundManager.playHealPotion()
            }

            else -> {
                systems.showToast("🐾 Esa orden no existe: usa EMBESTIDA, GUARDIA o ALIENTO.", "IRON")
                return
            }
        }

        _combatState.value = _combatState.value.copy(
            playerCurrentHp = playerHp,
            petCooldown = profile.commandCooldown,
            combatLogs = _combatState.value.combatLogs + log,
            damageFeedbackEnemy =
                if (damageDealt > 0) "-$damageDealt HP 🐾" else _combatState.value.damageFeedbackEnemy,
            damageFeedbackPlayer =
                if (healedAmount > 0) "+$healedAmount HP 🐾" else _combatState.value.damageFeedbackPlayer
        )

        if (enemyHp <= 0) {
            viewModelScope.launch { handleCombatVictory() }
            return
        }

        viewModelScope.launch {
            val updated = progress.copy(currentHp = playerHp)
            saveProgressSynced(updated)
            kotlinx.coroutines.delay(700)
            _combatState.value = _combatState.value.copy(
                damageFeedbackEnemy = null,
                damageFeedbackPlayer = null
            )
        }
    }

    private fun executeEnemyTurn() {
        val currentCombat = _combatState.value
        val progress = _progressState.value ?: return
        if (currentCombat.victory != null || currentCombat.enemy == null) return

        // El movimiento se elige AQUÍ, al cerrar el turno del jugador, y se anuncia
        // antes de resolverse: esa espera es la ventana de reacción.
        rollEnemyIntent()

        viewModelScope.launch {
            kotlinx.coroutines.delay(reactionWindowMillis())
            SoundManager.playEnemyAttack()

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
                val dwarfDefBonus = when {
                    progress.charLevel >= 100 -> 80
                    progress.charLevel >= 50 -> 35
                    progress.charLevel >= 20 -> 15
                    else -> 5
                }
                playerDefense += dwarfDefBonus
            }

            // El contraataque de una parada perfecta puede haber bajado al enemigo
            // mientras corría la ventana: si ya está muerto, no hay turno enemigo.
            if (enemy.currentHp <= 0) {
                _combatState.value = _combatState.value.copy(reactionWindow = false)
                handleCombatVictory()
                return@launch
            }

            // Estado VIVO: durante la ventana el jugador pudo reaccionar o dar una
            // orden a su bestia, y ese HP/MP/ímpetu no puede perderse aquí.
            val live = _combatState.value

            // Los talentos del turno enemigo se resuelven contra el estado VIVO:
            // si el jugador quedó a un hilo de vida, los talentos de "vida baja"
            // tienen que estar ya activos para el golpe que viene.
            val talents = heroTalentLoadout()
            val tctx = talentContextOf(live, progress)

            // Dodge check
            val dodgeChance = 3 + (progress.statDex * 0.3) + (getTalentRank("t_7") * 4) +
                // La esquiva de talento se suma como la del Tónico: son puntos
                // porcentuales sobre la misma tirada, no una tirada aparte.
                (talents.value(TalentKind.ESQUIVA, tctx) * 100.0) +
                // Tónico de Sombras: se SUMA a tu esquiva en vez de sustituirla,
                // para que el Pícaro siga siendo el que mejor lo aprovecha.
                (if (live.evasionTurns > 0) live.evasionPotency * 100.0 else 0.0)
            val dodged = Random.nextInt(100) < dodgeChance

            // El movimiento ya venía elegido por `rollEnemyIntent()`: no se re-sortea.
            val skillChosen = pendingEnemyMove
            val isSkillUsed = skillChosen != "BASIC"
            var finalDmg = 0
            var feedbackText = ""
            var logMsg = ""
            var mpDrained = 0

            // La mitigación ya no resta plano (`daño − def/2`), que permitía llegar
            // a la inmunidad acumulando defensa: ahora la armadura tiene
            // rendimientos decrecientes y la PENETRACIÓN es el porcentaje del
            // golpe que se salta esa curva. Cada movimiento tiene el suyo.
            val loadout = EldoriaPassives.loadoutOf(progress, getAllEquippedItems(progress))
            // La Égida es la respuesta directa a los golpes perforantes: les
            // recorta la penetración a la mitad, además de rebajar todo el daño.
            val armorPen = EldoriaBalance.armorPenOf(skillChosen) *
                (if (loadout.aegis > 0.0) 0.5 else 1.0)
            val talentGuard = if (getTalentRank("t_6") > 0) 0.85 else 1.0
            // Reducción del árbol. Con suelo del 20 % del golpe: una reducción
            // que pueda llegar al 100 % convierte al héroe en inmune y deja el
            // combate sin salida, igual que pasaba con la mitigación plana vieja.
            val treeGuard = (1.0 - talents.value(TalentKind.REDUCCION_DANO, tctx))
                .coerceIn(0.20, 1.0)
            val aegisGuard = 1.0 - loadout.aegis
            // El jefe enfurecido pega más fuerte mientras le dura la fase.
            val enrageMult = if (live.enrageTurns > 0) 1.35 else 1.0

            fun resolve(multiplier: Double, floorHp: Double = 0.0, pen: Double = armorPen): Int {
                val raw = ((baseDmg * multiplier * enrageMult).toInt() + (progress.maxHp * floorHp).toInt())
                val hit = EldoriaBalance.mitigate(raw, playerDefense, enemy.level, pen)
                val guarded = (hit * talentGuard * treeGuard * aegisGuard).toInt()
                // Techo y suelo: ningún golpe se lleva más de lo que su rareza
                // permite, y ninguno se queda en cosquillas por mucha armadura
                // que lleves encima. Esquivar y la parada perfecta siguen
                // anulándolo del todo: eso se aplica después.
                val capped = EldoriaBalance.capHit(guarded, progress.maxHp, enemy.rarity)
                return EldoriaBalance.floorHit(capped, progress.maxHp, enemy.rarity)
                    .coerceAtLeast(1)
            }

            if (isSkillUsed) {
                when (skillChosen) {
                    "ARMOR_PIERCE" -> {
                        finalDmg = resolve(1.75)
                        logMsg = "${enemy.name} lanza [Perforación de Armadura] 🗡️💥 e inflige $finalDmg" +
                            EldoriaBalance.penLabel(armorPen) + "."
                        feedbackText = "-$finalDmg HP 🛡️❌"
                    }
                    "TRUE_STRIKE" -> {
                        finalDmg = resolve(1.65)
                        logMsg = "${enemy.name} ejecuta [Golpe Certero Implacable] 👁️🎯 ¡inesquivable! " +
                            "Inflige $finalDmg" + EldoriaBalance.penLabel(armorPen) + "."
                        feedbackText = "-$finalDmg HP 🎯"
                    }
                    "POISON" -> {
                        finalDmg = resolve(1.45, floorHp = 0.02)
                        mpDrained = 15
                        logMsg = "${enemy.name} escupe [Veneno Corrosivo] 🧪 e inflige $finalDmg de daño tóxico" +
                            EldoriaBalance.penLabel(armorPen) + " y drena 15 MP."
                        feedbackText = "-$finalDmg HP 🧪"
                    }
                    "FREEZE" -> {
                        finalDmg = resolve(1.5, floorHp = 0.02)
                        mpDrained = 25
                        logMsg = "${enemy.name} conjura [Congelación Arcana] ❄️ hiela tus venas por $finalDmg" +
                            EldoriaBalance.penLabel(armorPen) + " y drena 25 MP."
                        feedbackText = "-$finalDmg HP ❄️"
                    }
                    "BLEED" -> {
                        finalDmg = resolve(1.7, floorHp = 0.03)
                        logMsg = "${enemy.name} asesta un tajo de [Hemorragia Mortal] 🩸 por $finalDmg" +
                            EldoriaBalance.penLabel(armorPen) + "."
                        feedbackText = "-$finalDmg HP 🩸"
                    }
                    "BOSS_FURY" -> {
                        finalDmg = resolve(1.95, floorHp = 0.04)
                        logMsg = "🔥 ${enemy.name} desata su [IRA DE JEFE] 💥 por $finalDmg" +
                            EldoriaBalance.penLabel(armorPen) + "."
                        feedbackText = "-$finalDmg HP ⚡🔥"
                    }
                    "REGEN_SHIELD" -> {
                        if (live.enemyAntiHealTurns > 0) {
                            finalDmg = resolve(1.3)
                            logMsg = "🖤 ${enemy.name} intenta curarse con [Escudo de Sangre], pero 🚫 ¡la " +
                                "[Maldición Anti-Curación] anula su regeneración! Te golpea por $finalDmg."
                            feedbackText = "-$finalDmg HP 🚫🖤"
                        } else {
                            val healVal = (enemy.maxHp * 0.14).toInt()
                            enemy.currentHp = minOf(enemy.maxHp, enemy.currentHp + healVal)
                            finalDmg = resolve(1.3)
                            logMsg = "🖤 ${enemy.name} invoca [Escudo de Sangre], regenerando +$healVal HP " +
                                "y golpeando por $finalDmg."
                            feedbackText = "-$finalDmg HP 🛡️🖤"
                        }
                    }
                    else -> {
                        finalDmg = resolve(1.0, pen = 0.0)
                        feedbackText = "-$finalDmg HP"
                        logMsg = "${enemy.name} te ataca e inflige $finalDmg puntos de daño físico."
                    }
                }
            } else {
                if (dodged) {
                    finalDmg = 0
                    feedbackText = "¡ESQUIVADO!"
                    logMsg = "¡Esquivas con agilidad el ataque de ${enemy.name}!"
                } else {
                    finalDmg = resolve(1.0, pen = 0.0)
                    feedbackText = "-$finalDmg HP"
                    logMsg = "${enemy.name} te ataca e inflige $finalDmg puntos de daño físico."
                }
            }

            // ── Reacción del jugador: mitigación del golpe anunciado ──
            if (finalDmg > 0 && pendingReactionMitigation < 1f) {
                val mitigated = (finalDmg * pendingReactionMitigation).toInt()
                logMsg += when {
                    mitigated <= 0 -> " 🛡️ ¡Tu parada perfecta anula el golpe por completo!"
                    else -> " 🛡️ Tu bloqueo recorta el golpe a $mitigated de daño."
                }
                finalDmg = mitigated
                feedbackText = if (finalDmg <= 0) "¡PARADO!" else "-$finalDmg HP 🛡️"
            }

            // ── Guardia de la bestia: absorbe antes de que el daño te llegue ──
            if (finalDmg > 0 && (petGuardFull || petGuardAbsorb > 0)) {
                if (petGuardFull) {
                    logMsg += " 🐾 Tu Guardián Leal encaja el golpe entero por ti."
                    finalDmg = 0
                    feedbackText = "¡ABSORBIDO! 🐾"
                } else {
                    val absorbed = minOf(finalDmg, petGuardAbsorb)
                    finalDmg -= absorbed
                    logMsg += " 🐾 Tu bestia absorbe $absorbed de daño con su guardia."
                    feedbackText = if (finalDmg <= 0) "¡ABSORBIDO! 🐾" else "-$finalDmg HP 🐾"
                }
                petGuardFull = false
                petGuardAbsorb = 0
            }

            // ── Bendición Serena: corta el drenaje de maná del movimiento anunciado ──
            if (mpDrained > 0 && pendingMpDrainBlocked) {
                logMsg += " 🐾 El aliento de tu bestia impide el drenaje de $mpDrained de maná."
                mpDrained = 0
            }
            pendingMpDrainBlocked = false

            var newPlayerMp = maxOf(0, live.playerCurrentMp - mpDrained)

            // ─── Bálsamo de Piedra: recorta el golpe antes de repartirlo ───
            // Va ANTES del escudo rúnico a propósito: así el escudo dura más,
            // que es lo que uno espera al beberse las dos cosas.
            if (live.wardTurns > 0 && finalDmg > 0) {
                val blocked = (finalDmg * live.wardPotency).toInt()
                if (blocked > 0) {
                    finalDmg -= blocked
                    logMsg += " 🪨 El Bálsamo de Piedra absorbe $blocked."
                }
            }

            // ─── Escudo Rúnico: se come el golpe antes que la carne ───
            var shieldLeft = live.runeShieldLeft
            var damageToHp = finalDmg
            if (shieldLeft > 0 && damageToHp > 0) {
                val absorbed = minOf(shieldLeft, damageToHp)
                shieldLeft -= absorbed
                damageToHp -= absorbed
                logMsg += " 🛡️ El Escudo Rúnico absorbe $absorbed" +
                    (if (shieldLeft <= 0) " y se quiebra." else " (le quedan $shieldLeft).")
            }

            var newHp = maxOf(0, live.playerCurrentHp - damageToHp)

            // ─── Segundo Aliento: una vez por combate, no mueres ───
            var secondWindUsed = live.secondWindUsed
            if (newHp <= 0 && !secondWindUsed && loadout.secondWind > 0.0) {
                newHp = (progress.maxHp * loadout.secondWind).toInt().coerceAtLeast(1)
                secondWindUsed = true
                logMsg += " ✨ ¡SEGUNDO ALIENTO! Te levantas con $newHp HP."
            }

            // ─── Último Aliento (talento): la segunda red, una vez por combate ───
            // Se comprueba DESPUÉS del Segundo Aliento y con su propia bandera:
            // el que pagó puntos por el talento Y lleva el objeto legendario
            // espera dos salvadas, no una.
            var lastBreathUsed = live.lastBreathUsed
            val lastBreath = talents.value(TalentKind.ULTIMO_ALIENTO, tctx)
            if (newHp <= 0 && !lastBreathUsed && lastBreath > 0.0) {
                newHp = (progress.maxHp * lastBreath).toInt().coerceAtLeast(1)
                lastBreathUsed = true
                logMsg += " 🕯️ ¡ÚLTIMO ALIENTO! Te niegas a caer y aguantas con $newHp HP."
            }

            // Dwarf Level 20+ Reflect passive
            var enemyHpAfterReflect = enemy.currentHp
            var reflectLog = ""

            // ─── Espinas: el golpe recibido vuelve al que lo dio ───
            if (loadout.thorns > 0.0 && finalDmg > 0 && !dodged) {
                val thornsDmg = (finalDmg * loadout.thorns).toInt().coerceAtLeast(1)
                enemyHpAfterReflect = maxOf(0, enemyHpAfterReflect - thornsDmg)
                enemy.currentHp = enemyHpAfterReflect
                logMsg += " 🌵 Las Espinas de Hierro devuelven $thornsDmg de daño."
            }
            // Espinas del árbol: se cobran sobre el daño que TE llegó, así que
            // esquivar o parar también anula la devolución. Es coherente con que
            // sean un castigo por golpearte, no una fuente de daño pasiva.
            val treeThorns = talents.value(TalentKind.ESPINAS, tctx)
            if (treeThorns > 0.0 && finalDmg > 0 && !dodged) {
                val spikes = (finalDmg * treeThorns).toInt().coerceAtLeast(1)
                enemyHpAfterReflect = maxOf(0, enemyHpAfterReflect - spikes)
                enemy.currentHp = enemyHpAfterReflect
                logMsg += " 🌵 Tus espinas devuelven $spikes de daño."
            }
            if (progress.charRace == "Enano" && progress.charLevel >= 20 && finalDmg > 0 && !dodged) {
                val reflectPct = when {
                    progress.charLevel >= 100 -> 0.35
                    progress.charLevel >= 50 -> 0.20
                    else -> 0.10
                }
                val damageReflected = maxOf(1, (finalDmg * reflectPct).toInt())
                enemyHpAfterReflect = maxOf(0, enemy.currentHp - damageReflected)
                enemy.currentHp = enemyHpAfterReflect
                reflectLog = " ¡Tu Escudo Rúnico devuelve $damageReflected de daño!"
            }

            // Human Level 20+ Turn Heal passive
            var afterHealHp = newHp
            var humanHealLog = ""
            if (progress.charRace == "Humano" && progress.charLevel >= 20 && newHp > 0) {
                val healPct = when {
                    progress.charLevel >= 100 -> 0.25
                    progress.charLevel >= 50 -> 0.15
                    else -> 0.08
                }
                val humanHeal = (progress.maxHp * healPct).toInt()
                afterHealHp = minOf(progress.maxHp, newHp + humanHeal)
                humanHealLog = " ¡Tu don Imperial te sana +$humanHeal HP!"
            }

            if (reflectLog.isNotEmpty()) logMsg += reflectLog
            if (humanHealLog.isNotEmpty()) logMsg += humanHealLog

            // Regeneración por turno: la del equipo más la del árbol. Los
            // talentos dan FRACCIÓN de la barra, no puntos planos, porque un
            // "+10 HP por turno" deja de significar nada a nivel 80.
            val regenHpVal = getHpRegenerationValue(progress) +
                (progress.maxHp * talents.value(TalentKind.REGEN_VIDA_TURNO, tctx)).toInt()
            val regenMpVal = getMpRegenerationValue(progress) +
                (progress.maxMp * talents.value(TalentKind.REGEN_MANA_TURNO, tctx)).toInt()

            var finalPlayerHp = if (afterHealHp > 0) minOf(progress.maxHp, afterHealHp + regenHpVal) else 0
            val afterRegenMp = if (afterHealHp > 0) minOf(progress.maxMp, newPlayerMp + regenMpVal) else newPlayerMp
            
            var updatedLogMsg = logMsg
            if (afterHealHp > 0) {
                updatedLogMsg += " ¡Regeneras +$regenHpVal HP y +$regenMpVal MP al inicio de tu turno!"
            }

            // Enemy Pet Attack on Enemy's turn
            val enemyPet = enemy.pet
            if (enemyPet != null && finalPlayerHp > 0) {
                val enemyPetRawAtk = enemyPet.attack + Random.nextInt(-2, 4)
                val enemyPetDmg = maxOf(3, enemyPetRawAtk - (playerDefense / 5))
                finalPlayerHp = maxOf(0, finalPlayerHp - enemyPetDmg)
                updatedLogMsg += "\n🐾 [Mascota Enemiga: ${enemyPet.name} Niv.${enemyPet.level}] ¡Ataca embistiendo e inflige $enemyPetDmg de daño!"
            }

            // Veneno de la Embestida (rasgo Colmillo Venenoso): 3 turnos de sangrado.
            if (petPoisonTurns > 0 && enemyHpAfterReflect > 0) {
                enemyHpAfterReflect = maxOf(0, enemyHpAfterReflect - petPoisonDamage)
                enemy.currentHp = enemyHpAfterReflect
                petPoisonTurns--
                updatedLogMsg += "\n🧪 El veneno de tu bestia corroe a ${enemy.name} por $petPoisonDamage de daño ($petPoisonTurns turnos restantes)."
                if (petPoisonTurns <= 0) petPoisonDamage = 0
            }

            // Pet Combat Assistance Action — pasivo NERFEADO (×0.40) y sin curación
            // automática: la curación ahora es la orden ALIENTO, que el jugador decide.
            val pet = GameJsonParser.fromJson<Item>(progress.equippedPetJson)
            var currentPetSatiety = progress.petSatiety
            if (pet != null && enemyHpAfterReflect > 0 && finalPlayerHp > 0) {
                val petWpn = GameJsonParser.fromJson<Item>(progress.petEquippedWeaponJson)
                val petAcc = GameJsonParser.fromJson<Item>(progress.petEquippedAccessoryJson)

                val extraDmg = (petWpn?.dmgBonus ?: 0) + (petWpn?.strBonus ?: 0) + (petAcc?.strBonus ?: 0) + (petAcc?.intBonus ?: 0)

                val satietyMult = if (progress.petSatiety >= 50) 1.25f else if (progress.petSatiety > 0) 1.0f else 0.6f
                // Acoso de la mascota en el turno enemigo: mismo reescalado que
                // el resto de la ofensiva del héroe.
                val petDmg = EldoriaBalance.scaleHeroDamage(
                    (((pet.dmgBonus * 0.9 + progress.charLevel * 6 + progress.petLevel * 18 + pet.strBonus * 0.5 + extraDmg + Random.nextInt(15, 35)) * satietyMult) * 0.40f).toInt().coerceAtLeast(14),
                    EldoriaBalance.measureHero(progress) { id -> getTalentRank(id) }.outputScale
                )
                // El acoso también es daño de mascota: dejarlo fuera del talento
                // haría que la rama Bestia rindiera distinto según de qué turno
                // viniera el mordisco, que es exactamente lo que confunde.
                val petHarass = (petDmg * (1.0 + talents.value(TalentKind.DANO_MASCOTA, tctx))).toInt()

                enemyHpAfterReflect = maxOf(0, enemyHpAfterReflect - petHarass)
                enemy.currentHp = enemyHpAfterReflect

                currentPetSatiety = maxOf(0, progress.petSatiety - 1)
                updatedLogMsg += "\n🐾 [Mascota ${pet.name} Niv.${progress.petLevel}] Acosa a ${enemy.name} por $petHarass de daño. (Saciedad: $currentPetSatiety%)"
            }

            // Synchronize with database so stats screens and HUDs are updated
            val progressAfterEnemyTurn = progress.copy(
                currentHp = finalPlayerHp,
                currentMp = afterRegenMp,
                petSatiety = currentPetSatiety
            )
            saveProgressSynced(progressAfterEnemyTurn)

            val nextAntiHeal = maxOf(0, live.enemyAntiHealTurns - 1)
            val settled = _combatState.value

            // ═══ MECÁNICAS DE JEFE ═══
            // Un jefe no es un saco de vida con números grandes: cambia de fase
            // según cómo va el combate, y cada fase le da una herramienta nueva.
            var bossPhase = settled.bossPhase
            var enrageTurns = maxOf(0, settled.enrageTurns - 1)
            var phaseLog = ""
            if (enemy.isBoss && enemyHpAfterReflect > 0) {
                val hpRatio = enemyHpAfterReflect.toDouble() / enemy.maxHp.coerceAtLeast(1)
                val newPhase = when {
                    hpRatio <= 0.30 -> 3
                    hpRatio <= 0.65 -> 2
                    else -> 1
                }
                if (newPhase > bossPhase) {
                    bossPhase = newPhase
                    // Al cambiar de fase se enfurece: tres turnos pegando un 35 % más.
                    enrageTurns = 3
                    phaseLog = when (newPhase) {
                        2 -> "\n🔥 ${enemy.name} entra en FASE 2: se enfurece y su guardia se abre. ¡Tres turnos de golpes más duros!"
                        else -> "\n💀 ${enemy.name} entra en FASE 3: pelea como si no tuviera nada que perder. ¡Acaba con él ya!"
                    }
                    // La fase 3 le devuelve algo de aire: castiga alargar el combate.
                    if (newPhase == 3) {
                        val desperate = (enemy.maxHp * 0.10).toInt()
                        enemy.currentHp = minOf(enemy.maxHp, enemyHpAfterReflect + desperate)
                        enemyHpAfterReflect = enemy.currentHp
                        phaseLog += " Recupera $desperate HP en un último arranque."
                    }
                }
            }

            // ─── Elixir de Regeneración y caducidad de los frascos ───
            //
            // Todo esto ocurre al DEVOLVER el turno, que es el unico punto por el
            // que pasan todas las ramas del turno enemigo. Ponerlo en el turno
            // del jugador habria dejado fuera los combates que terminan aqui.
            var regenLog = ""
            var healedHp = finalPlayerHp
            if (settled.regenTurns > 0 && healedHp > 0) {
                val healed = (progress.maxHp * settled.regenPotency).toInt()
                if (healed > 0) {
                    healedHp = minOf(progress.maxHp, healedHp + healed)
                    regenLog = "\n🌿 El Elixir de Regeneración te devuelve $healed HP " +
                        "(${settled.regenTurns - 1} turnos restantes)."
                }
            }

            _combatState.value = settled.copy(
                playerCurrentHp = healedHp,
                playerCurrentMp = afterRegenMp,
                enemyAntiHealTurns = nextAntiHeal,
                regenTurns = (settled.regenTurns - 1).coerceAtLeast(0),
                damageBuffTurns = (settled.damageBuffTurns - 1).coerceAtLeast(0),
                evasionTurns = (settled.evasionTurns - 1).coerceAtLeast(0),
                wardTurns = (settled.wardTurns - 1).coerceAtLeast(0),
                playerTurn = true,
                damageFeedbackPlayer = feedbackText,
                runeShieldLeft = shieldLeft,
                secondWindUsed = secondWindUsed,
                lastBreathUsed = lastBreathUsed,
                bossPhase = bossPhase,
                enrageTurns = enrageTurns,
                combatLogs = settled.combatLogs + updatedLogMsg + phaseLog + regenLog,
                activeAnimation = when {
                    pendingReactionCounter -> "PLAYER_ATTACK"
                    isSkillUsed && !dodged -> "ENEMY_SKILL"
                    else -> "ENEMY_ATTACK"
                },
                reactionWindow = false,
                reactionDeadline = 0L,
                enemyIntent = null,
                enemyIntentIcon = "",
                petCooldown = maxOf(0, settled.petCooldown - 1),
                momentum = maxOf(0, settled.momentum - 5)
            )

            // La reacción sólo vale para el golpe que se acaba de resolver.
            pendingReactionMitigation = 1f
            pendingReactionCounter = false

            if (enemyHpAfterReflect <= 0 && finalPlayerHp > 0) {
                kotlinx.coroutines.delay(600)
                _combatState.value = _combatState.value.copy(damageFeedbackPlayer = null, activeAnimation = null)
                handleCombatVictory()
                return@launch
            }

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

        // Drop rates calibration. La ganancia NO se recorta: sólo se calcula en Long
        // para que un héroe de nivel alto no desborde el Int y acabe con oro negativo.
        val pLvl = maxOf(1, progress.charLevel)
        val baseGoldReward = 80L * pLvl * enemy.level + (if (enemy.isBoss) 400L * pLvl else 0L)
        val baseExpReward = 15 * enemy.level + (enemy.level * enemy.level * 2) + (if (enemy.isBoss) 120 * enemy.level else 0)

        // Tier multipliers for rewards
        val rewardMultiplier = when (enemy.rarity) {
            "UNIVERSAL" -> 12.0
            "LEGENDARY" -> 7.0
            "CHAMPION" -> 4.0
            "ELITE" -> 2.2
            else -> 1.0
        }

        // Talentos de Fortuna. El contexto se toma del combate que se acaba de
        // ganar, así que un talento de "oro contra grandes" mira al bicho que de
        // verdad cayó y no a un contexto vacío.
        val talents = heroTalentLoadout()
        val tctx = talentContextOf(currentCombat, progress)

        val goldReward = (baseGoldReward * rewardMultiplier) *
            (1.0 + talents.value(TalentKind.ORO, tctx))
        val expReward = (baseExpReward * rewardMultiplier *
            (1.0 + talents.value(TalentKind.EXP, tctx))).toInt()

        // Drop generation rate calibrator
        val isBoss = enemy.isBoss

        // Custom drop rate calculation and Human racial passive (+10% gold / +15% gold if level 5+)
        val goldTalentMultiplier = 1.0 + (getTalentRank("t_9") * 0.20)
        val raceGoldMultiplier = when {
            progress.charRace == "Humano" && progress.charLevel >= 100 -> 1.60
            progress.charRace == "Humano" && progress.charLevel >= 50 -> 1.35
            progress.charRace == "Humano" && progress.charLevel >= 20 -> 1.20
            progress.charRace == "Humano" -> 1.10
            else -> 1.0
        }
        val finalGoldReward = (goldReward * goldTalentMultiplier * raceGoldMultiplier)
            .toLong()
            .coerceIn(0L, Int.MAX_VALUE.toLong())
            .toInt()

        // Calibrated drop rates
        val legendaryThreshold = 100 - _dropRateLegendary.value
        val epicThreshold = legendaryThreshold - _dropRateEpic.value
        val rareThreshold = epicThreshold - _dropRateRare.value

        var droppedItem: Item? = null
        val isBossEnemy = isBoss || enemy.isBoss || enemy.rarity == "BOSS" || enemy.rarity == "UNIVERSAL"
        val isChampionOrElite = enemy.rarity == "CHAMPION" || enemy.rarity == "ELITE"

        if (isBossEnemy) {
            // Jefes: drop 100% asegurado de grado UNIVERSAL (Mascotas o Equipos Universales)
            if (Random.nextBoolean()) {
                droppedItem = generateUniversalPet(enemy.level)
            } else {
                droppedItem = generateProceduralItem(enemy.level, isBoss = true, rarityPreset = "UNIVERSAL")
            }
        } else if (isChampionOrElite) {
            // Campeones y Elites: drop 100% asegurado de grado LEGENDARIO
            droppedItem = generateProceduralItem(enemy.level, isBoss = false, rarityPreset = "LEGENDARIO")
        } else {
            // Enemigos normales: 40% probabilidad de drop (COMÚN, RARO o ÉPICO)
            val shouldDrop = Random.nextInt(100) < 40
            if (shouldDrop) {
                val roll = Random.nextInt(100)
                val rarity = when {
                    roll >= 85 -> "ÉPICO"
                    roll >= 50 -> "RARO"
                    else -> "COMÚN"
                }
                droppedItem = generateProceduralItem(enemy.level, isBoss = false, rarityPreset = rarity)
            }
        }

        viewModelScope.launch {
            SoundManager.playVictory()
            val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
            
            val dungeonRun = _dungeonRunState.value
            var finalDroppedItem = droppedItem

            if (dungeonRun.inDungeonRun) {
                val dungeon = DUNGEONS_LIST.find { it.id == dungeonRun.dungeonId }
                val isFinalBoss = dungeonRun.currentStage == 10

                if (isFinalBoss && dungeon != null) {
                    finalDroppedItem = dungeon.uniqueTreasure
                    invList.add(finalDroppedItem)
                    val dungeonPet = generateUniversalPet(enemy.level)
                    invList.add(dungeonPet)
                } else {
                    if (droppedItem != null) {
                        invList.add(droppedItem)
                    }
                }
            } else {
                if (droppedItem != null) {
                    invList.add(droppedItem)
                }
            }

            var currentExp = progress.charExp + expReward
            var currentLevel = progress.charLevel
            var nextLevelExp = getRequiredExpForLevel(currentLevel)
            var didLevelUp = false
            var addedStatPoints = 0
            var addedTalentPoints = 0

            var pStr = progress.statStr
            var pDex = progress.statDex
            var pInt = progress.statInt
            var pCon = progress.statCon

            while (currentExp >= nextLevelExp) {
                currentExp -= nextLevelExp
                currentLevel += 1
                nextLevelExp = getRequiredExpForLevel(currentLevel)
                didLevelUp = true
                addedStatPoints += 5
                addedTalentPoints += 1

                pStr += 1
                pDex += 1
                pInt += 1
                pCon += 1
            }

            // Remove checkpoint if stage 10 completed
            val newCheckpointsJson = if (dungeonRun.inDungeonRun && dungeonRun.currentStage == 10) {
                val map = GameJsonParser.mapFromJson<String, Int>(progress.dungeonCheckpointsJson).toMutableMap()
                map.remove(dungeonRun.dungeonId.toString())
                GameJsonParser.mapToJson(map)
            } else progress.dungeonCheckpointsJson

            val updatedProgress = progress.copy(
                charLevel = currentLevel,
                charExp = currentExp,
                charGold = (progress.charGold.toLong() + finalGoldReward)
                    .coerceIn(0L, Int.MAX_VALUE.toLong())
                    .toInt(),
                statStr = pStr,
                statDex = pDex,
                statInt = pInt,
                statCon = pCon,
                currentHp = currentCombat.playerCurrentHp,
                currentMp = currentCombat.playerCurrentMp,
                statPointsAvailable = progress.statPointsAvailable + addedStatPoints,
                talentPointsAvailable = progress.talentPointsAvailable + addedTalentPoints,
                inventoryJson = GameJsonParser.listToJson(invList),
                dungeonCheckpointsJson = newCheckpointsJson,
                highestUnlockedDungeon = if (dungeonRun.inDungeonRun && dungeonRun.currentStage == 10) {
                    maxOf(progress.highestUnlockedDungeon, dungeonRun.dungeonId + 1)
                } else progress.highestUnlockedDungeon,
                completedDungeonsJson = if (dungeonRun.inDungeonRun && dungeonRun.currentStage == 10) {
                    val completedList = GameJsonParser.listFromJson<Int>(progress.completedDungeonsJson).toMutableList()
                    if (!completedList.contains(dungeonRun.dungeonId)) {
                        completedList.add(dungeonRun.dungeonId)
                    }
                    GameJsonParser.listToJson(completedList)
                } else progress.completedDungeonsJson
            )

            val (equippedProgress, equippedNames) = autoEquipProgress(updatedProgress)

            // E11 — `syncMaxHpAndMp` es la ÚNICA fuente de verdad del HP/MP máximo.
            // Al subir de nivel se recupera el 35 %, no el total: subir no cura.
            var finalProgress = syncMaxHpAndMp(equippedProgress)
            if (didLevelUp) {
                val restoredHp = (finalProgress.currentHp + finalProgress.maxHp * 35 / 100)
                    .coerceIn(1, finalProgress.maxHp.coerceAtLeast(1))
                val restoredMp = (finalProgress.currentMp + finalProgress.maxMp * 35 / 100)
                    .coerceIn(0, finalProgress.maxMp.coerceAtLeast(1))
                finalProgress = finalProgress.copy(currentHp = restoredHp, currentMp = restoredMp)
            }
            saveProgressSynced(finalProgress)
            _progressState.value = finalProgress

            if (dungeonRun.inDungeonRun) {
                val isFinalBoss = dungeonRun.currentStage == 10
                _dungeonRunState.value = dungeonRun.copy(
                    persistentHp = finalProgress.currentHp,
                    persistentMp = finalProgress.currentMp,
                    stageVictoryPending = !isFinalBoss,
                    dungeonCompletedJustNow = isFinalBoss
                )
            }

            var victoryLogs = "¡Has derrotado a ${enemy.name}! Obtienes $expReward EXP y $finalGoldReward monedas de oro."
            if (didLevelUp) {
                victoryLogs += " ¡¡SUBISTE AL NIVEL $currentLevel!! Tu salud máxima ahora es de ${finalProgress.maxHp} HP y recuperas un 35 %. Ganas +$addedStatPoints atributos y +$addedTalentPoints talentos."
            }
            if (finalDroppedItem != null) {
                victoryLogs += " Encontraste: ${finalDroppedItem.name} [${finalDroppedItem.rarity}]"
            }
            if (equippedNames.isNotEmpty()) {
                victoryLogs += " ¡Auto-equipado: ${equippedNames.joinToString(", ")}!"
            }

            _combatState.value = currentCombat.copy(
                victory = true,
                playerCurrentHp = finalProgress.currentHp,
                playerCurrentMp = finalProgress.currentMp,
                lootDropped = finalDroppedItem,
                expGained = expReward,
                goldGained = finalGoldReward,
                combatLogs = currentCombat.combatLogs + victoryLogs,
                reactionWindow = false,
                enemyIntent = null,
                enemyIntentIcon = ""
            )

            // E10 — Ganchos del controlador: bestiario, materiales, contratos y expedición.
            systems.onCombatVictory(enemy.name, currentCombat.enemySpeciesId, enemy.isBoss, enemy.level)
            systems.progressContracts("CAZA", currentCombat.enemyArchetype, 1)
            // Los encargos del reino apuntan al NOMBRE de la bestia, no a su
            // arquetipo: sin esta segunda emisión nunca avanzarían.
            systems.progressContracts("CAZA", enemy.name, 1)
            checkAndUnlockAchievements()
            if (currentCombat.inExpedition) systems.onExpeditionCombatResolved(true)

            if (_isAutoCombat.value || _isAutoNavigation.value) {
                kotlinx.coroutines.delay(2500)
                if (_combatState.value.victory == true) {
                    when {
                        currentCombat.inExpedition -> {
                            _combatState.value = CombatState()
                            systems.returnToExpeditionMap()
                        }
                        _dungeonRunState.value.stageVictoryPending -> advanceDungeonStage()
                        else -> exitCombatScreen()
                    }
                }
            }
        }
    }

    private fun handleCombatDefeat() {
        val currentCombat = _combatState.value
        val progress = _progressState.value ?: return

        viewModelScope.launch {
            SoundManager.playDefeat()

            var newCheckpointsJson = progress.dungeonCheckpointsJson
            if (_dungeonRunState.value.inDungeonRun) {
                val dungeonId = _dungeonRunState.value.dungeonId
                val stage = _dungeonRunState.value.currentStage
                
                val checkpointsMap = GameJsonParser.mapFromJson<String, Int>(progress.dungeonCheckpointsJson).toMutableMap()
                checkpointsMap[dungeonId.toString()] = stage
                newCheckpointsJson = GameJsonParser.mapToJson(checkpointsMap)

                _dungeonRunState.value = DungeonRunState(inDungeonRun = false)
                showNotification("¡Caíste derrotado en la Etapa $stage/10 del Calabozo! Se guardó tu avance en la Etapa $stage para que reintentes cuando estés listo.")
            }

            // Revive at starting safe room with gold penalty of 15%
            val penaltyGold = (progress.charGold * 0.15).toInt()
            val newGold = maxOf(0, progress.charGold - penaltyGold)

            val updatedProgress = progress.copy(
                currentHp = progress.maxHp,
                currentMp = progress.maxMp,
                charGold = newGold,
                dungeonCheckpointsJson = newCheckpointsJson
            )

            saveProgressSynced(updatedProgress)
            _progressState.value = updatedProgress
            _combatState.value = currentCombat.copy(
                victory = false,
                combatLogs = currentCombat.combatLogs + "Has caído en combate... Te retiras al cuadro anterior. Perdiste $penaltyGold monedas de oro de penalización.",
                reactionWindow = false,
                enemyIntent = null,
                enemyIntentIcon = ""
            )

            // E10 — Ganchos de derrota.
            systems.onCombatDefeat()
            if (currentCombat.inExpedition) systems.onExpeditionCombatResolved(false)

            if (_isAutoCombat.value || _isAutoNavigation.value) {
                kotlinx.coroutines.delay(2500)
                if (_combatState.value.victory == false) {
                    if (currentCombat.inExpedition) {
                        _combatState.value = CombatState()
                        _screenState.value = GameScreen.DUNGEON
                    } else {
                        exitCombatScreen()
                    }
                }
            }
        }
    }

    fun exitCombatScreen() {
        _combatState.value = CombatState()
        resetCombatAuxiliaries()
        val progress = _progressState.value
        if (progress != null) {
            // E11 — El 4.º argumento faltaba: sin él el mapa perdía las casillas limpias.
            generateMapAround(
                progress.currentX,
                progress.currentY,
                progress.mapPointsExploredJson,
                progress.mapPointsClearedJson
            )
        }
        _screenState.value = GameScreen.WORLD_MAP
    }

    fun fleeCombat() {
        val currentCombat = _combatState.value
        if (!currentCombat.active || !currentCombat.playerTurn || currentCombat.victory != null) return

        viewModelScope.launch {
            val progress = _progressState.value
            if (progress != null) {
                val saved = progress.copy(
                    currentHp = maxOf(1, currentCombat.playerCurrentHp),
                    currentMp = currentCombat.playerCurrentMp
                )
                saveProgressSynced(saved)
                _progressState.value = saved
            }
            val isDungeon = _dungeonRunState.value.inDungeonRun
            val fled = true
            val wasExpedition = currentCombat.inExpedition

            // E11 — Huir NO es una derrota: `victory` se queda en null para que ninguna
            // pantalla dibuje la carta de "has caído" ni se apliquen penalizaciones.
            _combatState.value = CombatState()
            resetCombatAuxiliaries()

            if (fled) systems.showToast("🏃 Escapas del combate con lo puesto.", "SILVER")

            when {
                wasExpedition -> {
                    systems.abandonExpedition()
                    _screenState.value = GameScreen.DUNGEON
                }
                isDungeon -> {
                    _dungeonRunState.value = DungeonRunState(inDungeonRun = false)
                    _screenState.value = GameScreen.DUNGEON
                }
                else -> _screenState.value = GameScreen.WORLD_MAP
            }
        }
    }

    // --- PROCEDURAL ITEMS DROP ENGINE ---
    fun getAllEquippedItems(progress: GameProgress): List<Item> {
        val items = mutableListOf<Item>()
        GameJsonParser.fromJson<Item>(progress.equippedHelmetJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.equippedWingsJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.equippedShieldJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.equippedArmorJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.equippedGlovesJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.equippedBootsJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.equippedRingJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.equippedEarringJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.equippedRelicJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.equippedPetJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.petEquippedWeaponJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.petEquippedArmorJson)?.let { items.add(it.withScaledStats()) }
        GameJsonParser.fromJson<Item>(progress.petEquippedAccessoryJson)?.let { items.add(it.withScaledStats()) }
        return items
    }

    fun generateUniversalPet(itemLevel: Int): Item {
        val r = Random.Default
        val levelBase = maxOf(1, itemLevel)
        val multiplier = getRarityMultiplier("UNIVERSAL")
        val baseStat = levelBase * multiplier

        val petsPool = listOf(
            Triple("Fénix Cósmico de Flama Eterna", "Mascota Universal. Imbuida de llama celestial. En combate abrasa al enemigo con fuego sagrado y restaura tu vitalidad.", "img_pet_fenix_cosmico"),
            Triple("Dragón de Sombras Abisales", "Mascota Universal. Forjado en la noche insondable. Desata alientos espectrales devoradores de vida.", "img_pet_dragon_sombras"),
            Triple("Lobo Celestial de las Estrellas", "Mascota Universal. Cazador sideral. Realiza embestidas furiosas que aumentan el impacto de tus golpes.", "img_pet_lobo_celestial"),
            Triple("Gato Estelar Fortuna", "Mascota Universal. Felino sagrado que canaliza la suerte del universo, restaurando tu Salud y Maná.", "img_pet_gato_estelar"),
            Triple("Titán de Cristal Ancestral", "Mascota Universal. Coloso impenetrable. Absorbe el impacto de los golpes enemigos protegiéndote.", "img_pet_titan_cristal"),
            Triple("Grifo Dorado de Eldoria", "Mascota Universal. Majestuoso grifo real. Embiste al enemigo provocando aturdimiento y cortes rúnicos.", "img_pet_grifo_dorado"),
            Triple("Serpiente Astral de Luz", "Mascota Universal. Criatura de polvo de estrellas. Envenena al enemigo causando daño mágico sostenido.", "img_pet_serpiente_astral"),
            Triple("Behemoth del Vacío Infinito", "Mascota Universal. Bestia de fuerza devastadora. Aplasta a los enemigos con ondas de choque multidimensionales.", "img_pet_behemoth_vacio")
        )

        val selectedPet = petsPool.random(r)
        val s = (baseStat * 1.3).toInt() + r.nextInt(10, 30)
        val d = (baseStat * 1.3).toInt() + r.nextInt(10, 30)
        val i = (baseStat * 1.3).toInt() + r.nextInt(10, 30)
        val c = (baseStat * 1.4).toInt() + r.nextInt(15, 35)
        val dmg = (baseStat * 1.2).toInt() + r.nextInt(20, 50)
        val def = (baseStat * 1.2).toInt() + r.nextInt(20, 50)
        val hpReg = (baseStat * 0.5).toInt() + r.nextInt(5, 15)

        return Item(
            id = "pet_${System.currentTimeMillis()}_${r.nextInt(1000)}",
            name = selectedPet.first,
            type = "PET",
            rarity = "UNIVERSAL",
            strBonus = s,
            dexBonus = d,
            intBonus = i,
            conBonus = c,
            dmgBonus = dmg,
            defBonus = def,
            hpRegen = hpReg,
            description = selectedPet.second,
            itemLevel = levelBase,
            imageResName = selectedPet.third
        ).withScaledStats()
    }

    // --- PET MANAGEMENT & TRAINING SYSTEM ---
    private data class FoodSpec(val cost: Int, val satiety: Int, val exp: Int, val name: String, val img: String)

    fun feedPet(foodItem: Item) {
        val progress = _progressState.value ?: return
        val equippedPet = GameJsonParser.fromJson<Item>(progress.equippedPetJson)
        if (equippedPet == null) {
            showNotification("¡Debes equipar una mascota primero para alimentarla!")
            return
        }

        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        val foodIndex = invList.indexOfFirst { it.id == foodItem.id }
        if (foodIndex == -1) return
        invList.removeAt(foodIndex)

        val satietyGained = if (foodItem.conBonus > 0) foodItem.conBonus else 30
        val expGained = if (foodItem.strBonus > 0) foodItem.strBonus else 150

        val newSatiety = minOf(100, progress.petSatiety + satietyGained)
        var newExp = progress.petExp + expGained
        var newLevel = progress.petLevel

        var requiredExp = newLevel * 150 + (newLevel * newLevel * 25)
        var leveledUp = false

        while (newExp >= requiredExp) {
            newExp -= requiredExp
            newLevel++
            requiredExp = newLevel * 150 + (newLevel * newLevel * 25)
            leveledUp = true
        }

        viewModelScope.launch {
            SoundManager.playHealPotion()
            val updated = progress.copy(
                petSatiety = newSatiety,
                petExp = newExp,
                petLevel = newLevel,
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            val synced = syncMaxHpAndMp(updated)
            saveProgressSynced(synced)

            if (leveledUp) {
                systems.showToast("🍖 Alimentas a ${equippedPet.name}: destello divino y ¡NIVEL $newLevel!", "VITAE")
            } else {
                systems.showToast("🍖 Alimentas a ${equippedPet.name}: +$satietyGained de saciedad y +$expGained de EXP.", "VITAE")
            }
        }
    }

    fun trainPet(trainType: String) {
        val progress = _progressState.value ?: return
        val equippedPet = GameJsonParser.fromJson<Item>(progress.equippedPetJson)
        if (equippedPet == null) {
            showNotification("¡Debes equipar una mascota para entrenarla!")
            return
        }

        if (progress.petSatiety < 10) {
            showNotification("¡${equippedPet.name} tiene demasiada hambre (<10 Saciedad)! Aliméntala antes de entrenar.")
            return
        }

        val cost = progress.petLevel * 50 + 50
        if (progress.charGold < cost) {
            showNotification("¡Necesitas $cost 🪙 monedas de oro para este entrenamiento!")
            return
        }

        val (trainTitle, expGained) = when (trainType) {
            "ATTACK" -> Pair("Entrenamiento de Furia Celestial", 220 + progress.petLevel * 25)
            "DEFENSE" -> Pair("Entrenamiento Bastión Sagrado", 220 + progress.petLevel * 25)
            else -> Pair("Entrenamiento Vitalidad Inmortal", 220 + progress.petLevel * 25)
        }

        val newSatiety = maxOf(0, progress.petSatiety - 10)
        var newExp = progress.petExp + expGained
        var newLevel = progress.petLevel
        var requiredExp = newLevel * 150 + (newLevel * newLevel * 25)
        var leveledUp = false

        while (newExp >= requiredExp) {
            newExp -= requiredExp
            newLevel++
            requiredExp = newLevel * 150 + (newLevel * newLevel * 25)
            leveledUp = true
        }

        viewModelScope.launch {
            SoundManager.playButtonClick()
            val updated = progress.copy(
                charGold = progress.charGold - cost,
                petSatiety = newSatiety,
                petExp = newExp,
                petLevel = newLevel
            )
            val synced = syncMaxHpAndMp(updated)
            saveProgressSynced(synced)

            if (leveledUp) {
                SoundManager.playVictory()
                systems.showToast("⚔️ $trainTitle completado: ¡${equippedPet.name} sube al nivel $newLevel!", "EMBER")
            } else {
                systems.showToast("⚔️ $trainTitle completado: +$expGained de EXP y −10 de saciedad.", "EMBER")
            }
        }
    }

    fun autoTrainPet(trainType: String = "ATTACK") {
        val progress = _progressState.value ?: return
        val equippedPet = GameJsonParser.fromJson<Item>(progress.equippedPetJson)
        if (equippedPet == null) {
            showNotification("¡Necesitas equipar una mascota para entrenarla!")
            return
        }

        if (progress.petSatiety < 10) {
            showNotification("¡${equippedPet.name} tiene demasiada hambre (<10 Saciedad)! Aliméntala primero.")
            return
        }

        var currentGold = progress.charGold
        var currentSatiety = progress.petSatiety
        var currentExp = progress.petExp
        var currentLevel = progress.petLevel
        var totalTrainings = 0
        var totalGoldSpent = 0

        val trainTitle = when (trainType) {
            "ATTACK" -> "Furia Celestial"
            "DEFENSE" -> "Bastión Sagrado"
            else -> "Vitalidad Inmortal"
        }

        while (currentSatiety >= 10) {
            val cost = currentLevel * 50 + 50
            if (currentGold < cost) break

            currentGold -= cost
            totalGoldSpent += cost
            currentSatiety -= 10
            val expGained = 220 + currentLevel * 25
            currentExp += expGained
            totalTrainings++

            var reqExp = currentLevel * 150 + (currentLevel * currentLevel * 25)
            while (currentExp >= reqExp) {
                currentExp -= reqExp
                currentLevel++
                reqExp = currentLevel * 150 + (currentLevel * currentLevel * 25)
            }
        }

        if (totalTrainings == 0) {
            val cost = currentLevel * 50 + 50
            if (progress.charGold < cost) {
                showNotification("¡Necesitas al menos $cost 🪙 de oro para entrenar!")
            }
            return
        }

        viewModelScope.launch {
            SoundManager.playVictory()
            val updated = progress.copy(
                charGold = currentGold,
                petSatiety = currentSatiety,
                petExp = currentExp,
                petLevel = currentLevel
            )
            val synced = syncMaxHpAndMp(updated)
            saveProgressSynced(synced)
            systems.showToast("⚡ Auto-entrenamiento ($trainTitle): $totalTrainings sesiones, nivel $currentLevel, −$totalGoldSpent de oro.", "EMBER")
        }
    }

    fun equipPetGear(item: Item, petSlot: String) {
        val progress = _progressState.value ?: return
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()

        val itemIndex = invList.indexOfFirst { it.id == item.id }
        if (itemIndex != -1) {
            invList.removeAt(itemIndex)
        }

        var updated = progress
        when (petSlot) {
            "PET_WEAPON" -> {
                val oldItem = GameJsonParser.fromJson<Item>(progress.petEquippedWeaponJson)
                if (oldItem != null) invList.add(oldItem)
                updated = updated.copy(petEquippedWeaponJson = GameJsonParser.toJson(item))
            }
            "PET_ARMOR" -> {
                val oldItem = GameJsonParser.fromJson<Item>(progress.petEquippedArmorJson)
                if (oldItem != null) invList.add(oldItem)
                updated = updated.copy(petEquippedArmorJson = GameJsonParser.toJson(item))
            }
            "PET_ACCESSORY" -> {
                val oldItem = GameJsonParser.fromJson<Item>(progress.petEquippedAccessoryJson)
                if (oldItem != null) invList.add(oldItem)
                updated = updated.copy(petEquippedAccessoryJson = GameJsonParser.toJson(item))
            }
        }

        viewModelScope.launch {
            SoundManager.playButtonClick()
            val finalProgress = syncMaxHpAndMp(updated.copy(inventoryJson = GameJsonParser.listToJson(invList)))
            saveProgressSynced(finalProgress)
            showNotification("¡Equipaste ${item.name} a tu mascota!")
        }
    }

    fun unequipPetGear(petSlot: String) {
        val progress = _progressState.value ?: return
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        var itemToReturn: Item? = null
        var updated = progress

        when (petSlot) {
            "PET_WEAPON" -> {
                itemToReturn = GameJsonParser.fromJson<Item>(progress.petEquippedWeaponJson)
                updated = updated.copy(petEquippedWeaponJson = "")
            }
            "PET_ARMOR" -> {
                itemToReturn = GameJsonParser.fromJson<Item>(progress.petEquippedArmorJson)
                updated = updated.copy(petEquippedArmorJson = "")
            }
            "PET_ACCESSORY" -> {
                itemToReturn = GameJsonParser.fromJson<Item>(progress.petEquippedAccessoryJson)
                updated = updated.copy(petEquippedAccessoryJson = "")
            }
        }

        if (itemToReturn != null) {
            invList.add(itemToReturn)
            viewModelScope.launch {
                SoundManager.playButtonClick()
                val finalProgress = syncMaxHpAndMp(updated.copy(inventoryJson = GameJsonParser.listToJson(invList)))
                saveProgressSynced(finalProgress)
                showNotification("¡Desequipaste ${itemToReturn.name} de tu mascota!")
            }
        }
    }

    fun buyPetFood(foodName: String, quantity: Int = 1) {
        val progress = _progressState.value ?: return
        val qty = quantity.coerceAtLeast(1)
        val spec = when (foodName) {
            "BESTIAL" -> FoodSpec(150, 25, 100, "Ración de Carne Bestial", "img_food_bestial")
            "MISTICA" -> FoodSpec(500, 50, 400, "Galleta Mística de Mascota", "img_food_mistica")
            "DRAGON" -> FoodSpec(2000, 80, 1800, "Manjar Imperial de Dragón", "img_food_dragon")
            else -> FoodSpec(8000, 100, 7000, "Elixir Celestial Estelar", "img_food_celestial")
        }

        val totalCost = spec.cost.toLong() * qty
        if (progress.charGold < totalCost) {
            showNotification("¡No tienes suficiente oro ($totalCost 🪙) para comprar $qty de ${spec.name}!")
            return
        }

        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        val now = System.currentTimeMillis()

        for (i in 0 until qty) {
            invList.add(
                Item(
                    id = "petfood_${now}_${i}_${Random.nextInt(1000)}",
                    name = spec.name,
                    type = "PET_FOOD",
                    rarity = if (spec.cost >= 2000) "LEGENDARIO" else "ÉPICO",
                    strBonus = spec.exp,
                    conBonus = spec.satiety,
                    description = "Alimento de Mascota. Otorga +${spec.satiety} Saciedad y +${spec.exp} EXP a tu mascota.",
                    imageResName = spec.img
                )
            )
        }

        viewModelScope.launch {
            SoundManager.playButtonClick()
            val updatedProgress = progress.copy(
                charGold = (progress.charGold - totalCost).toInt(),
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            saveProgressSynced(updatedProgress)
            showNotification("¡Compraste $qty de ${spec.name} por $totalCost 🪙 de oro!")
        }
    }

    fun syncMaxHpAndMp(progress: GameProgress): GameProgress {
        val equipped = getAllEquippedItems(progress)
        val bonusCon = equipped.sumOf { it.conBonus }
        val bonusInt = equipped.sumOf { it.intBonus }

        val totalCon = progress.statCon + bonusCon
        val totalInt = progress.statInt + bonusInt

        val calculatedMaxHp = (totalCon * 30) + (progress.charLevel * 25) + 120
        val calculatedMaxMp = (totalInt * 10) + (progress.charLevel * 5) + 50

        val currentHpClamped = progress.currentHp.coerceAtMost(calculatedMaxHp)
        val currentMpClamped = progress.currentMp.coerceAtMost(calculatedMaxMp)

        if (calculatedMaxHp != progress.maxHp || calculatedMaxMp != progress.maxMp) {
            val updated = progress.copy(
                maxHp = calculatedMaxHp,
                maxMp = calculatedMaxMp,
                currentHp = if (progress.currentHp >= progress.maxHp || progress.currentHp <= 0) calculatedMaxHp else currentHpClamped,
                currentMp = if (progress.currentMp >= progress.maxMp) calculatedMaxMp else currentMpClamped
            )
            viewModelScope.launch {
                repository.saveProgress(updated)
            }
            return updated
        }
        return progress
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

    private fun generateProceduralItem(
        level: Int,
        isBoss: Boolean,
        rarityPreset: String? = null,
        typePreset: String? = null
    ): Item {
        val r = Random.Default
        val rarity = rarityPreset?.uppercase() ?: if (isBoss) {
            "UNIVERSAL"
        } else {
            val roll = r.nextInt(100)
            when {
                roll >= 85 -> "ÉPICO"
                roll >= 50 -> "RARO"
                else -> "COMÚN"
            }
        }

        val type = typePreset?.uppercase()
            ?: listOf("HELMET", "WINGS", "WEAPON", "SHIELD", "ARMOR", "GLOVES", "BOOTS", "RING", "EARRING", "RELIC").random()
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

        val levelBase = maxOf(1, level)
        val multiplier = getRarityMultiplier(rarity)
        val baseStat = levelBase * multiplier

        var s = 0
        var d = 0
        var i = 0
        var c = 0
        var dmg = 0
        var def = 0
        var hpReg = 0

        // Base combat stats scaling strictly according to item level * rarity multiplier
        if (type == "WEAPON") dmg = baseStat + r.nextInt(1, maxOf(3, baseStat / 4 + 1))
        if (type in listOf("ARMOR", "HELMET", "GLOVES", "BOOTS", "SHIELD")) def = baseStat + r.nextInt(1, maxOf(3, baseStat / 4 + 1))
        if (type in listOf("WINGS", "RELIC")) {
            dmg = baseStat + r.nextInt(1, 3)
            def = (baseStat * 0.8).toInt() + r.nextInt(1, 3)
        }
        if (type in listOf("RING", "EARRING") || rarity in listOf("ÉPICO", "EPIC", "LEGENDARIO", "LEGENDARY", "ARCANO", "UNIVERSAL")) {
            hpReg = (baseStat * 0.4).toInt() + r.nextInt(1, 4)
        }

        val primaryBonus = baseStat + r.nextInt(0, maxOf(2, baseStat / 5 + 1))
        val secondaryBonus = (baseStat * 0.5).toInt() + r.nextInt(0, 2)

        val primaryStatType = when {
            name.contains("Báculo") || name.contains("Túnica") || name.contains("Orbe") -> "INT"
            name.contains("Daga") || name.contains("Botas") || name.contains("Guantes") -> "DEX"
            name.contains("Casco") || name.contains("Pechera") || name.contains("Escudo") || name.contains("Baluarte") -> "CON"
            else -> "STR"
        }

        when (rarity) {
            "UNIVERSAL" -> {
                s = (baseStat * 1.25).toInt()
                d = (baseStat * 1.25).toInt()
                i = (baseStat * 1.25).toInt()
                c = (baseStat * 1.25).toInt()
            }
            "ARCANO" -> {
                s = (baseStat * 1.1).toInt()
                d = (baseStat * 1.1).toInt()
                i = (baseStat * 1.1).toInt()
                c = (baseStat * 1.1).toInt()
            }
            "LEGENDARIO", "LEGENDARY" -> {
                s = primaryBonus
                d = primaryBonus
                i = primaryBonus
                c = primaryBonus
            }
            "ÉPICO", "EPIC" -> {
                when (primaryStatType) {
                    "STR" -> { s = primaryBonus; c = primaryBonus; d = primaryBonus; i = secondaryBonus }
                    "DEX" -> { d = primaryBonus; s = primaryBonus; c = primaryBonus; i = secondaryBonus }
                    "INT" -> { i = primaryBonus; c = primaryBonus; d = primaryBonus; s = secondaryBonus }
                    else -> { c = primaryBonus; s = primaryBonus; i = primaryBonus; d = secondaryBonus }
                }
            }
            "RARO", "RARE" -> {
                when (primaryStatType) {
                    "STR" -> { s = primaryBonus; c = primaryBonus; d = secondaryBonus; i = 0 }
                    "DEX" -> { d = primaryBonus; s = primaryBonus; c = secondaryBonus; i = 0 }
                    "INT" -> { i = primaryBonus; c = primaryBonus; s = secondaryBonus; d = 0 }
                    else -> { c = primaryBonus; s = primaryBonus; i = secondaryBonus; d = 0 }
                }
            }
            else -> { // COMÚN
                when (primaryStatType) {
                    "STR" -> { s = primaryBonus; c = secondaryBonus }
                    "DEX" -> { d = primaryBonus; s = secondaryBonus }
                    "INT" -> { i = primaryBonus; c = secondaryBonus }
                    else -> { c = primaryBonus; s = secondaryBonus }
                }
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
        ).withScaledStats()
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
        val slots = listOf("HELMET", "WINGS", "WEAPON", "SHIELD", "ARMOR", "GLOVES", "BOOTS", "RING", "EARRING", "RELIC", "PET")
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
                "PET" -> updatedProgress.equippedPetJson
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
                        "PET" -> updatedProgress.copy(equippedPetJson = GameJsonParser.toJson(bestItemInInv))
                        else -> updatedProgress
                    }
                    equippedNames.add(bestItemInInv.name)
                }
            }
        }

        val finalProgress = syncMaxHpAndMp(updatedProgress.copy(
            inventoryJson = GameJsonParser.listToJson(invList)
        ))
        return Pair(finalProgress, equippedNames)
    }

    fun autoEquip() {
        val progress = _progressState.value ?: return
        val (finalProgress, equippedNames) = autoEquipProgress(progress)
        if (equippedNames.isNotEmpty()) {
            viewModelScope.launch {
                saveProgressSynced(finalProgress)
                systems.showToast("🛡️ Auto-equipo: ${equippedNames.joinToString(", ")}.", "SILVER")
            }
        } else {
            systems.showToast("🛡️ Ya llevas el mejor equipo disponible para tu nivel.", "IRON")
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
                "PET" -> {
                    val current = GameJsonParser.fromJson<Item>(progress.equippedPetJson)
                    if (current != null) invList.add(current)
                    updatedProgress = updatedProgress.copy(equippedPetJson = GameJsonParser.toJson(item))
                }
            }

            val finalProgress = syncMaxHpAndMp(updatedProgress.copy(
                inventoryJson = GameJsonParser.listToJson(invList)
            ))
            saveProgressSynced(finalProgress)
            showNotification("¡Equipaste exitosamente: ${item.name}!")
        }
    }

    fun unequipItem(slot: String) {
        val progress = _progressState.value ?: return
        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()

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
                "PET" -> {
                    itemToStore = GameJsonParser.fromJson<Item>(progress.equippedPetJson)
                    updated = updated.copy(equippedPetJson = "")
                }
            }

            if (itemToStore != null) {
                invList.add(itemToStore)
                val finalProgress = syncMaxHpAndMp(updated.copy(inventoryJson = GameJsonParser.listToJson(invList)))
                saveProgressSynced(finalProgress)
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
            saveProgressSynced(updated)
            showNotification("Descartaste el objeto: ${item.name}")
        }
    }

    /**
     * Compra frascos del catálogo.
     *
     * @param potionId id del frasco. Vacío = Poción Menor, que es lo que
     *        compraba el botón único de antes y lo que siguen llamando las
     *        pantallas que aún no ofrecen el catálogo.
     */
    fun buyPotion(quantity: Int = 1, potionId: String = "pot_menor") {
        val progress = _progressState.value ?: return
        val qty = quantity.coerceAtLeast(1)
        val spec = EldoriaPotions.spec(potionId) ?: EldoriaPotions.spec("pot_menor")!!

        if (progress.charLevel < spec.unlockLevel) {
            showNotification("${spec.name} se desbloquea a nivel ${spec.unlockLevel}.")
            return
        }

        val totalCost = spec.price.toLong() * qty
        if (progress.charGold < totalCost) {
            showNotification("¡No tienes suficiente oro! $qty × ${spec.name} cuestan $totalCost monedas.")
            return
        }

        val invList = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
        val now = System.currentTimeMillis()

        for (i in 0 until qty) {
            invList.add(
                Item(
                    // El id EMPIEZA por el del catálogo: así `usePotionCombat`
                    // reconoce el frasco sin guardar un campo nuevo en la ficha.
                    id = "${spec.id}_${now}_${i}_${Random.nextInt(1000)}",
                    name = spec.name,
                    type = "POTION",
                    rarity = spec.rarity,
                    description = spec.description,
                    itemLevel = spec.unlockLevel,
                    imageResName = spec.artKey
                )
            )
        }

        viewModelScope.launch {
            SoundManager.playButtonClick()
            val updated = progress.copy(
                charGold = (progress.charGold - totalCost).toInt(),
                inventoryJson = GameJsonParser.listToJson(invList)
            )
            saveProgressSynced(updated)
            showNotification("Compraste $qty × ${spec.name} por $totalCost monedas de oro.")
        }
    }

    // --- TALENTS ALLOCATION ---
    /**
     * Rangos invertidos del heroe, indexados por id de talento.
     *
     * `talentsJson` guarda una lista de [Talent], pero de las partidas viejas
     * solo trae los nueve talentos originales. El arbol de raza tiene cien, asi
     * que aqui se LEE lo guardado y se completa con lo que falte en vez de dar
     * por hecho que la lista esta al dia: al abrir el arbol nuevo con una
     * partida vieja, ninguno de los cien ids existia todavia y asignar puntos
     * fallaba en silencio.
     */
    private fun talentRanksOf(progress: GameProgress): MutableMap<String, Int> {
        val saved = GameJsonParser.listFromJson<Talent>(progress.talentsJson)
        return saved.associate { it.id to it.currentRank }.toMutableMap()
    }

    /** Persiste los rangos como lista de [Talent], que es el formato guardado. */
    private fun talentJsonFrom(race: String, ranks: Map<String, Int>): String {
        val defs = EldoriaTalents.forRace(race)
        val fromTree = defs.map { def ->
            Talent(
                id = def.id,
                name = def.name,
                description = def.description,
                maxRank = def.maxRank,
                currentRank = ranks[def.id] ?: 0,
                category = def.branch.name,
                prerequisiteId = def.prerequisiteId,
                row = def.tier,
                col = 1
            )
        }
        // Los talentos viejos (t_1..t_9) se conservan aunque ya no se muestren:
        // siguen leidos por el combate y borrarlos quitaria poder a quien ya los
        // tenia puestos.
        val treeIds = defs.map { it.id }.toSet()
        val legacy = GameJsonParser.listFromJson<Talent>(_progressState.value?.talentsJson ?: "")
            .filter { it.id !in treeIds }
        return GameJsonParser.listToJson(fromTree + legacy)
    }

    fun allocateTalentPoint(talentId: String) {
        val progress = _progressState.value ?: return
        if (progress.talentPointsAvailable <= 0) {
            showNotification("No tienes puntos de talento disponibles.")
            return
        }

        val def = EldoriaTalents.def(talentId)
        if (def == null) {
            showNotification("Ese talento no pertenece a la red de tu raza.")
            return
        }

        val ranks = talentRanksOf(progress)
        val current = ranks[talentId] ?: 0

        if (current >= def.maxRank) {
            showNotification("${def.name} ya esta al rango maximo (${def.maxRank}).")
            return
        }

        if (!EldoriaTalents.isUnlocked(def, progress.charLevel)) {
            val etapa = EldoriaTalentEngine.evolutionName(progress.charRace, def.evolutionTier)
            val nivel = when (def.evolutionTier) { 1 -> 20; 2 -> 50; else -> 100 }
            showNotification("${def.name} exige ser $etapa (nivel $nivel).")
            return
        }

        val prereqId = def.prerequisiteId
        if (prereqId != null && (ranks[prereqId] ?: 0) < 1) {
            val prereqName = EldoriaTalents.def(prereqId)?.name ?: prereqId
            showNotification("Requiere tener puntos en '$prereqName'.")
            return
        }

        viewModelScope.launch {
            ranks[talentId] = current + 1
            val updatedProgress = progress.copy(
                talentPointsAvailable = progress.talentPointsAvailable - 1,
                talentPointsSpent = progress.talentPointsSpent + 1,
                talentsJson = talentJsonFrom(progress.charRace, ranks)
            )
            saveProgressSynced(updatedProgress)
            showNotification("¡Asignaste un punto a ${def.name}! (rango ${current + 1}/${def.maxRank})")
        }
    }

    fun autoAllocateTalentPoints() {
        val progress = _progressState.value ?: return
        if (progress.talentPointsAvailable <= 0) {
            showNotification("No tienes puntos de talento disponibles para auto-asignar.")
            return
        }

        val defs = EldoriaTalents.forRace(progress.charRace)
        if (defs.isEmpty()) {
            showNotification("Tu raza aun no tiene red de talentos.")
            return
        }

        val ranks = talentRanksOf(progress)
        var available = progress.talentPointsAvailable
        var allocated = 0

        // Reparte de arriba abajo: primero los escalones bajos y, dentro de un
        // escalon, el talento menos invertido. Asi el auto-asignar ABRE camino
        // en vez de vaciar todos los puntos en la primera rama que encuentra.
        while (available > 0) {
            val target = defs
                .filter { def ->
                    (ranks[def.id] ?: 0) < def.maxRank &&
                        EldoriaTalents.isUnlocked(def, progress.charLevel) &&
                        (def.prerequisiteId == null || (ranks[def.prerequisiteId] ?: 0) >= 1)
                }
                .minByOrNull { def -> def.tier * 100 + (ranks[def.id] ?: 0) }
                ?: break

            ranks[target.id] = (ranks[target.id] ?: 0) + 1
            available -= 1
            allocated += 1
        }

        if (allocated == 0) {
            showNotification("No queda ningun talento disponible que subir.")
            return
        }

        viewModelScope.launch {
            val updatedProgress = progress.copy(
                talentPointsAvailable = available,
                talentPointsSpent = progress.talentPointsSpent + allocated,
                talentsJson = talentJsonFrom(progress.charRace, ranks)
            )
            saveProgressSynced(updatedProgress)
            showNotification("⚡ ¡Se auto-asignaron $allocated puntos de talento!")
        }
    }

    fun advanceClass() {
        val progress = _progressState.value ?: return
        if (progress.charLevel < 20) {
            showNotification("Requiere Nivel 20 de héroe para avanzar de clase.")
            return
        }
        if (progress.hasAdvancedClass) {
            showNotification("¡Ya has alcanzado la Clase Avanzada Épica!")
            return
        }

        val advName = when (progress.charClass) {
            "Guerrero" -> "Señor de la Guerra Alado"
            "Mago" -> "Archimago Cósmico"
            "Pícaro" -> "Sombra Celeste"
            else -> "Serafín Sagrado"
        }

        val ultimateSkill = when (progress.charClass) {
            "Guerrero" -> Skill("sk_adv_warrior", "Furia de Guerra Alada", "Lanza un embate devastador. Hace x5 de daño, causa Hemorragia y bloquea curación enemiga (Anti-Curación).", manaCost = 35, minLevel = 20, damageMultiplier = 5.0, isUltimate = true, isAntiHeal = true)
            "Mago" -> Skill("sk_adv_mage", "Cataclismo Cósmico", "Desata una tormenta de éter estelar. Hace x5 de daño, Congelación y bloquea curación enemiga (Anti-Curación).", manaCost = 40, minLevel = 20, damageMultiplier = 5.0, isUltimate = true, isAntiHeal = true)
            "Pícaro" -> Skill("sk_adv_rogue", "Danza de Sombras Celestes", "Se desplaza a velocidad luz. Hace x5 de daño, aplica Veneno Mortal y bloquea curación enemiga (Anti-Curación).", manaCost = 35, minLevel = 20, damageMultiplier = 5.0, isUltimate = true, isAntiHeal = true)
            else -> Skill("sk_adv_cleric", "Sentencia Serafín", "Invoca un rayo divino. Hace x5 de daño, sana al héroe y anula la curación enemiga (Anti-Curación).", manaCost = 35, minLevel = 20, damageMultiplier = 5.0, healingMultiplier = 1.0, isUltimate = true, isAntiHeal = true)
        }

        val currentSkills = GameJsonParser.listFromJson<Skill>(progress.skillsJson).toMutableList()
        if (!currentSkills.any { it.id == ultimateSkill.id }) {
            currentSkills.add(ultimateSkill)
        }

        val newStr = progress.statStr * 2
        val newDex = progress.statDex * 2
        val newInt = progress.statInt * 2
        val newCon = progress.statCon * 2
        val newMaxHp = progress.maxHp * 2
        val newMaxMp = progress.maxMp * 2

        viewModelScope.launch {
            val updated = progress.copy(
                hasAdvancedClass = true,
                advancedClassName = advName,
                statStr = newStr,
                statDex = newDex,
                statInt = newInt,
                statCon = newCon,
                maxHp = newMaxHp,
                currentHp = newMaxHp,
                maxMp = newMaxMp,
                currentMp = newMaxMp,
                skillsJson = GameJsonParser.listToJson(currentSkills)
            )
            saveProgressSynced(updated)
            SoundManager.playVictory()
            _showClassAdvancementCutscene.value = progress.charClass
            showNotification("✨ ¡AVANCE DE CLASE COMPLETADO! ¡Ahora eres $advName! Estadísticas duplicadas y habilidad X5 desbloqueada.")
        }
    }

    private fun getTalentRank(talentId: String): Int {
        val progress = _progressState.value ?: return 0
        val list = GameJsonParser.listFromJson<Talent>(progress.talentsJson)
        return list.find { it.id == talentId }?.currentRank ?: 0
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PUENTE CON EL MOTOR DE TALENTOS
    //
    //  Los nueve talentos viejos (`t_1`..`t_9`) siguen leyéndose a mano donde
    //  siempre: hay partidas guardadas con puntos puestos en ellos y quitarles
    //  el efecto sería robarles la inversión. Lo que viene del árbol nuevo se
    //  SUMA encima, y el combate nunca pregunta por un talento concreto: pide
    //  un [TalentKind] y recibe el total que aplica en ese instante.
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * El loadout se recalcula sólo cuando cambian los rangos o la raza. Se
     * consulta varias veces por golpe y reconstruirlo cada vez obligaría a
     * reparsear el JSON de talentos en mitad de la fórmula del daño.
     */
    private var talentLoadoutKey: String = ""
    private var talentLoadoutCache: TalentLoadout = TalentLoadout.EMPTY

    private fun heroTalentLoadout(): TalentLoadout {
        val progress = _progressState.value ?: return TalentLoadout.EMPTY
        val key = progress.charRace + "\u0000" + progress.talentsJson
        if (key == talentLoadoutKey) return talentLoadoutCache
        val ranks = GameJsonParser.listFromJson<Talent>(progress.talentsJson)
            .filter { it.currentRank > 0 }
            .associate { it.id to it.currentRank }
        val built = EldoriaTalents.loadoutFor(progress.charRace, ranks)
        talentLoadoutKey = key
        talentLoadoutCache = built
        return built
    }

    /**
     * Fotografía del instante para resolver las condiciones de los talentos.
     *
     * Se construye desde el estado VIVO de combate y no desde el snapshot con
     * el que empezó la acción: un talento "por debajo del 35 % de vida" tiene
     * que mirar la vida que hay cuando se aplica, no la que había al abrir el
     * turno, o llegaría siempre un golpe tarde.
     */
    private fun talentContextOf(state: CombatState, progress: GameProgress): TalentContext {
        val enemy = state.enemy
        // "Grande" es todo lo que no es un bicho de camino: jefes y las rarezas
        // que el botín ya trata como excepcionales. Es la misma lista que usa
        // el reparto de recompensas, para que el jugador no tenga que aprender
        // dos definiciones distintas de enemigo importante.
        val big = enemy != null && (
            enemy.isBoss ||
            enemy.rarity == "ELITE" || enemy.rarity == "CHAMPION" ||
            enemy.rarity == "LEGENDARY" || enemy.rarity == "UNIVERSAL"
        )
        return TalentContext(
            hpFraction = if (progress.maxHp > 0)
                state.playerCurrentHp.toDouble() / progress.maxHp else 1.0,
            // `turnsFought` cuenta turnos cerrados; el que se está jugando es el
            // siguiente, así que PRIMER_TURNO cuadra con el primer golpe real.
            turn = state.turnsFought + 1,
            againstBigTarget = big,
            hasPet = GameJsonParser.fromJson<Item>(progress.equippedPetJson) != null,
            // La expedición cuenta como calabozo: para el jugador es lo mismo
            // —bajar por salas sin poder volver— y separarlas sólo haría que la
            // mitad de los talentos de calabozo parecieran rotos.
            inDungeon = _dungeonRunState.value.inDungeonRun || state.inExpedition,
            potionActive = state.regenTurns > 0 || state.damageBuffTurns > 0 ||
                state.evasionTurns > 0 || state.wardTurns > 0
        )
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
            val baseUpdated = when (stat) {
                "STR" -> progress.copy(
                    statStr = progress.statStr + 1,
                    statPointsAvailable = progress.statPointsAvailable - 1
                )
                "DEX" -> progress.copy(
                    statDex = progress.statDex + 1,
                    statPointsAvailable = progress.statPointsAvailable - 1
                )
                "INT" -> progress.copy(
                    statInt = progress.statInt + 1,
                    statPointsAvailable = progress.statPointsAvailable - 1
                )
                else -> progress.copy( // CON
                    statCon = progress.statCon + 1,
                    statPointsAvailable = progress.statPointsAvailable - 1
                )
            }
            val updated = syncMaxHpAndMp(baseUpdated)
            saveProgressSynced(updated)
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
            val newMaxHp = (newCon * 25) + (currentProgress.charLevel * 20)
            val newMaxMp = newInt * 6

            val baseUpdated = currentProgress.copy(
                statStr = newStr,
                statDex = newDex,
                statInt = newInt,
                statCon = newCon,
                statPointsAvailable = 0
            )

            val updated = syncMaxHpAndMp(baseUpdated)

            saveProgressSynced(updated)
            
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
            // Sólo el héroe ACTIVO, como promete la pantalla de ajustes: el resto del
            // Salón de Héroes se queda donde estaba.
            val activeId = _progressState.value?.id ?: repository.getActiveProgressSync()?.id
            if (activeId == null) {
                showNotification("No hay ningún héroe activo que borrar.")
                return@launch
            }
            repository.deleteCharacter(activeId)
            _screenState.value = GameScreen.MAIN_MENU
            _creatorName.value = ""
            _creatorPointsAvailable.value = 15
            recalculateCreatorBaseStats()
            showNotification("Tu héroe activo ha sido borrado. El resto de tu Salón sigue intacto.")
        }
    }

    fun getEvolvedRaceName(race: String, level: Int): String {
        return when {
            level >= 100 -> when (race) {
                "Humano" -> "Avatar Supremo Imperial"
                "Elfo" -> "Ser Celestial Eterno"
                "Enano" -> "Titán de la Montaña Primordial"
                "Orco" -> "Dios de la Furia Sangrienta"
                else -> "$race Supremo"
            }
            level >= 50 -> when (race) {
                "Humano" -> "Soberano de la Luz"
                "Elfo" -> "Archimago de las Estrellas"
                "Enano" -> "Guardián de Titanio Abisal"
                "Orco" -> "Caudillo Infernal de la Horda"
                else -> "$race Ancestral"
            }
            level >= 20 -> when (race) {
                "Humano" -> "Campeón Imperial"
                "Elfo" -> "Guardián Astral"
                "Enano" -> "Señor de las Runas"
                "Orco" -> "Devastador Berserker"
                else -> race
            }
            else -> "$race Novato"
        }
    }

    fun getEvolvedClassName(cls: String, level: Int): String {
        return when {
            level >= 100 -> when (cls) {
                "Guerrero" -> "Dios de la Guerra Primigenio"
                "Mago" -> "Avatar Supremo de la Creación"
                "Pícaro" -> "Señor Fantasma del Abismo"
                "Clérigo" -> "Soberano Celestial Inmortal"
                else -> cls
            }
            level >= 50 -> when (cls) {
                "Guerrero" -> "Señor de la Guerra Titánico"
                "Mago" -> "Señor del Caos Arcano"
                "Pícaro" -> "Sombra Silenciosa de la Muerte"
                "Clérigo" -> "Santo Apóstol Divino"
                else -> cls
            }
            level >= 20 -> when (cls) {
                "Guerrero" -> "Caballero de Hierro"
                "Mago" -> "Archimago Elemental"
                "Pícaro" -> "Asesino de Sombras"
                "Clérigo" -> "Paladín Sagrado"
                else -> cls
            }
            else -> "$cls Novato"
        }
    }

    fun getFullEvolvedTitle(race: String, cls: String, level: Int): String {
        val raceTitle = getEvolvedRaceName(race, level)
        val classTitle = getEvolvedClassName(cls, level)
        return when {
            level >= 100 -> "✨ 3ª EVOLUCIÓN SUPREMA: $raceTitle ($classTitle)"
            level >= 50 -> "⚡ 2ª EVOLUCIÓN: $raceTitle ($classTitle)"
            level >= 20 -> "⚔️ 1ª EVOLUCIÓN: $raceTitle ($classTitle)"
            else -> "🛡️ FASE BÁSICA: $race $cls"
        }
    }

    fun getRacePassiveDescription(race: String, level: Int): String {
        return when {
            level >= 100 -> when (race) {
                "Humano" -> "Supremacía Imperial (3ª EVOLUCIÓN SUPREMA): +60% de oro obtenido, +25% de golpe crítico, y regeneras un 25% de tu Vida Máxima cada turno en combate."
                "Elfo" -> "Gracia Celestial Eterna (3ª EVOLUCIÓN SUPREMA): +40% de golpe crítico, +100% de Maná Máximo, y reduce un 50% todos los costos de Maná de tus hechizos."
                "Enano" -> "Fortaleza Primordial (3ª EVOLUCIÓN SUPREMA): +75% de Vida Máxima, +80 de Defensa fija, y devuelves un 35% del daño recibido directamente al atacante."
                "Orco" -> "Dios de la Furia Sangrienta (3ª EVOLUCIÓN SUPREMA): +80% de daño total infligido, +30% de golpe crítico, y tus ataques te curan un 35% del daño infligido."
                else -> ""
            }
            level >= 50 -> when (race) {
                "Humano" -> "Soberanía de Luz (2ª EVOLUCIÓN): +35% de oro obtenido, +15% de golpe crítico, y regeneras un 15% de tu Vida Máxima cada turno."
                "Elfo" -> "Estela Estelar (2ª EVOLUCIÓN): +25% de golpe crítico, +50% de Maná Máximo, y reduce un 35% los costos de Maná."
                "Enano" -> "Titanio Abisal (2ª EVOLUCIÓN): +40% de Vida Máxima, +35 de Defensa fija, y devuelves un 20% del daño recibido."
                "Orco" -> "Caudillo de la Horda (2ª EVOLUCIÓN): +45% de daño total infligido, y tus ataques te curan un 20% del daño causado."
                else -> ""
            }
            level >= 20 -> when (race) {
                "Humano" -> "Espíritu Imperial (1ª EVOLUCIÓN): +20% de oro obtenido, +10% de golpe crítico, y recuperas un 8% de tu Vida Máxima al final de cada turno."
                "Elfo" -> "Sabiduría Astral (1ª EVOLUCIÓN): +15% de golpe crítico, +25% de Maná Máximo, y reduce los costos de Maná en un 20%."
                "Enano" -> "Escudo Rúnico (1ª EVOLUCIÓN): +20% de Vida Máxima, +15 de Defensa fija, y devuelves un 10% del daño recibido."
                "Orco" -> "Devastación Sangrienta (1ª EVOLUCIÓN): +25% de daño total infligido, y tus ataques básicos te curan un 12% del daño causado."
                else -> ""
            }
            else -> when (race) {
                "Humano" -> "Determinación Humana (FASE BÁSICA): +10% de oro obtenido en combate y +5% de golpe crítico."
                "Elfo" -> "Sentidos Élficos (FASE BÁSICA): +5% de golpe crítico y +10% de Maná Máximo."
                "Enano" -> "Piel de Piedra (FASE BÁSICA): +10% de Vida Máxima y +5 de Defensa fija."
                "Orco" -> "Furia Berserker (FASE BÁSICA): +10% de daño total infligido."
                else -> ""
            }
        }
    }

    // --- DUNGEONS ENGINE ---
    fun startDungeonRun(dungeonId: Int, startFromStage: Int = 1) {
        val progress = _progressState.value ?: return
        val dungeon = DUNGEONS_LIST.find { it.id == dungeonId } ?: return

        val stageToStart = maxOf(1, minOf(10, startFromStage))

        _dungeonRunState.value = DungeonRunState(
            inDungeonRun = true,
            dungeonId = dungeonId,
            currentStage = stageToStart,
            persistentHp = progress.maxHp,
            persistentMp = progress.maxMp,
            stageVictoryPending = false,
            dungeonCompletedJustNow = false
        )

        startDungeonStageCombat(dungeon, stageToStart, progress.maxHp, progress.maxMp)
    }

    fun advanceDungeonStage() {
        val run = _dungeonRunState.value
        val progress = _progressState.value ?: return
        if (!run.inDungeonRun) return

        val dungeon = DUNGEONS_LIST.find { it.id == run.dungeonId } ?: return
        val nextStage = run.currentStage + 1
        if (nextStage > 10) return

        _dungeonRunState.value = run.copy(
            currentStage = nextStage,
            stageVictoryPending = false
        )

        startDungeonStageCombat(dungeon, nextStage, run.persistentHp, run.persistentMp)
    }

    fun exitDungeonRun() {
        _dungeonRunState.value = DungeonRunState(inDungeonRun = false)
        _screenState.value = GameScreen.DUNGEON
        showNotification("Has salido del Calabozo.")
    }

    private fun startDungeonStageCombat(
        dungeon: DungeonData,
        stage: Int,
        playerHp: Int,
        playerMp: Int
    ) {
        resetCombatAuxiliaries()
        val progress = _progressState.value
        // El nivel del enemigo sale del CALABOZO y la planta, no de tu ficha:
        // atarlo al héroe hacía que volver con más nivel subiese también al
        // enemigo, así que el calabozo no se superaba nunca.
        val monsterLevel = EldoriaDungeonBalance.enemyLevel(dungeon.levelReq, stage)

        val isFinalBoss = stage == 10
        val rarity = EldoriaDungeonBalance.rarityForStage(stage)

        // El nombre sin decorar es la clave del arte: con el emoji delante no
        // hay forma de encontrarlo en el índice.
        val rawEnemyName = if (isFinalBoss) {
            dungeon.finalBossName
        } else {
            dungeon.subBosses.getOrElse(stage - 1) { "Subjefe de ${dungeon.species}" }
        }
        val enemyName = if (isFinalBoss) "🔥 $rawEnemyName" else "⚔️ $rawEnemyName"

        // Hasta ahora los enemigos de calabozo no declaraban artKey, así que la
        // UI adivinaba por palabras del nombre y los NUEVE subjefes goblin
        // salían con el mismo goblin. Cada uno tiene ya su propia lámina.
        val dungeonArtKey = EldoriaArt.dungeonKey(dungeon.id, rawEnemyName, isFinalBoss)

        // Balance ABSOLUTO: la vara es el héroe de referencia del nivel del
        // calabozo, no el tuyo. Tu vida entra sólo como retoque acotado a ±15 %
        // y para que ningún golpe pase del 30 % de tu salud real.
        val dungeonStats = EldoriaDungeonBalance.buildEnemy(
            dungeonLevelReq = dungeon.levelReq,
            stage = stage,
            actualHeroHp = progress?.maxHp ?: 1000
        )
        val enemyHp = dungeonStats.hp
        val enemyAtk = dungeonStats.attack
        val enemyDef = dungeonStats.defense

        val enemyPet = generateEnemyPetIfNeeded(monsterLevel, rarity, isFinalBoss)

        val enemy = Combatant(
            name = enemyName,
            maxHp = enemyHp,
            currentHp = enemyHp,
            maxMp = 100,
            currentMp = 100,
            attack = enemyAtk,
            defense = enemyDef,
            level = monsterLevel,
            isBoss = isFinalBoss,
            rarity = rarity,
            pet = enemyPet,
            artKey = dungeonArtKey
        )

        val stageTitle = if (isFinalBoss) "¡¡JEFE FINAL: ${dungeon.finalBossName.uppercase()}!!" else "Subjefe $stage/9 de ${dungeon.species}"

        _combatState.value = CombatState(
            active = true,
            enemy = enemy,
            playerCurrentHp = playerHp,
            playerCurrentMp = playerMp,
            combatLogs = listOf(
                "🏛️ CALABOZO ${dungeon.id}: ${dungeon.name}",
                "⚔️ ETAPA $stage DE 10 - $stageTitle",
                "⚠️ Tu salud y maná persisten de combates anteriores."
            ),
            playerTurn = true,
            victory = null
        )

        _screenState.value = GameScreen.COMBAT
    }

    // === NUEVOS SISTEMAS: LOGROS, RECOMPENSAS DIARIAS, FORJA ===

    private val _achievementState = MutableStateFlow(AchievementDefinitions.ALL_ACHIEVEMENTS)
    val achievementState: StateFlow<List<Achievement>> = _achievementState.asStateFlow()

    private val _dailyRewardState = MutableStateFlow(DailyRewardState())
    val dailyRewardState: StateFlow<DailyRewardState> = _dailyRewardState.asStateFlow()

    fun checkAndUnlockAchievements() {
        val progress = _progressState.value ?: return
        val current = _achievementState.value.toMutableList()
        var changed = false

        current.forEachIndexed { index, achievement ->
            if (achievement.isUnlocked) return@forEachIndexed

            val newProgress = when (achievement.id) {
                "first_blood", "beast_hunter", "exterminator", "elite_warrior" ->
                    progress.mapPointsClearedJson.count { it == ',' || it == '"' } + 1
                "dragon_slayer" -> GameJsonParser.listFromJson<String>(progress.completedQuestsJson).size
                "novice_explorer", "cartographer", "world_traveler" ->
                    GameJsonParser.listFromJson<String>(progress.mapPointsExploredJson).size
                "adventurer", "hero_of_eldoria", "legend_alive" -> progress.charLevel
                "collector" -> GameJsonParser.listFromJson<Item>(progress.inventoryJson).size
                "millionaire" -> progress.charGold.toInt()
                "beast_tamer" -> progress.petLevel
                "dungeon_king", "dungeon_conqueror" ->
                    GameJsonParser.listFromJson<Int>(progress.completedDungeonsJson).size
                "ascended" -> if (progress.hasAdvancedClass) 1 else 0
                else -> achievement.currentProgress
            }

            val unlocked = newProgress >= achievement.requirement
            if (newProgress != achievement.currentProgress || unlocked != achievement.isUnlocked) {
                current[index] = achievement.copy(currentProgress = newProgress, isUnlocked = unlocked)
                changed = true
                if (unlocked && !achievement.isUnlocked) {
                    showNotification("🏆 ¡Logro Desbloqueado: ${achievement.title}! (+${achievement.rewardGold}🪙 +${achievement.rewardXp}XP)")
                }
            }
        }

        if (changed) {
            _achievementState.value = current
        }
    }

    fun canClaimDailyRewardNow(): Boolean {
        return canClaimDailyReward(_dailyRewardState.value)
    }

    fun claimDailyRewardNow() {
        val state = _dailyRewardState.value
        if (!canClaimDailyReward(state)) return

        val newState = claimDailyReward(state)
        _dailyRewardState.value = newState

        val currentReward = state.cycleRewards.getOrNull(state.currentDay - 1)
        if (currentReward != null) {
            val progress = _progressState.value ?: return
            var goldBonus = 0
            var xpBonus = 0
            currentReward.rewards.forEach { item ->
                when (item.type) {
                    "gold" -> goldBonus += item.amount
                    "xp" -> xpBonus += item.amount
                }
            }
            if (goldBonus > 0 || xpBonus > 0) {
                viewModelScope.launch {
                    val updated = progress.copy(
                        charGold = progress.charGold + goldBonus,
                        charExp = progress.charExp + xpBonus
                    )
                    saveProgressSynced(updated)
                    showNotification("🎁 ¡Recompensa del Día ${state.currentDay} reclamada! +${goldBonus}🪙 +${xpBonus}XP")
                }
            }
        }
    }

    fun resetDailyCycle() {
        _dailyRewardState.value = DailyRewardState()
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CONSOLA DEL ARCANISTA — herramientas de desarrollo
    //
    //  Todo lo de aquí abajo salta el balance a propósito: se desbloquea con
    //  el código de la pantalla de ajustes y escribe en la misma fila de Room
    //  que el juego normal, así que cada cambio se guarda y sobrevive al
    //  cierre de la app. Nada de esto se llama desde el juego real.
    // ═══════════════════════════════════════════════════════════════════════

    /** Los diez huecos de equipo que el héroe puede llevar, en orden de muñeco. */
    val devEquipSlots: List<String> = listOf(
        "WEAPON", "SHIELD", "HELMET", "ARMOR", "GLOVES",
        "BOOTS", "RING", "EARRING", "WINGS", "RELIC"
    )

    /** Rarezas del generador, de peor a mejor. */
    val devRarities: List<String> = listOf("COMÚN", "RARO", "ÉPICO", "LEGENDARIO", "ARCANO", "UNIVERSAL")

    /**
     * Fija el nivel del héroe aplicando (o revirtiendo) las mismas ganancias que
     * daría subir peleando: +1 a cada atributo, +5 puntos de stat y +1 de talento
     * por nivel. Así el personaje queda idéntico a uno que hubiese llegado ahí
     * jugando, y no con un nivel alto y los atributos de novato.
     */
    fun devSetLevel(target: Int) {
        val progress = _progressState.value ?: return
        val level = target.coerceIn(1, 500)
        val delta = level - progress.charLevel
        if (delta == 0) {
            showNotification("El héroe ya está en el nivel $level.")
            return
        }
        viewModelScope.launch {
            val updated = progress.copy(
                charLevel = level,
                charExp = 0,
                statStr = (progress.statStr + delta).coerceAtLeast(1),
                statDex = (progress.statDex + delta).coerceAtLeast(1),
                statInt = (progress.statInt + delta).coerceAtLeast(1),
                statCon = (progress.statCon + delta).coerceAtLeast(1),
                statPointsAvailable = (progress.statPointsAvailable + delta * 5).coerceAtLeast(0),
                talentPointsAvailable = (progress.talentPointsAvailable + delta).coerceAtLeast(0)
            )
            val synced = syncMaxHpAndMp(updated)
            saveProgressSynced(synced.copy(currentHp = synced.maxHp, currentMp = synced.maxMp))
            showNotification("⚗️ Nivel fijado en $level (${if (delta > 0) "+$delta" else "$delta"}).")
        }
    }

    fun devAddExp(amount: Int) {
        val progress = _progressState.value ?: return
        if (amount == 0) return
        viewModelScope.launch {
            val updated = progress.copy(charExp = (progress.charExp + amount).coerceAtLeast(0))
            saveProgressSynced(updated)
            showNotification("⚗️ EXP: ${updated.charExp} / ${getRequiredExpForLevel(updated.charLevel)}.")
        }
    }

    fun devSetGold(amount: Int) {
        val progress = _progressState.value ?: return
        viewModelScope.launch {
            val updated = progress.copy(charGold = amount.coerceIn(0, Int.MAX_VALUE))
            saveProgressSynced(updated)
            showNotification("⚗️ Oro fijado en ${formatGameNumber(updated.charGold)}.")
        }
    }

    fun devAddGold(amount: Int) {
        val progress = _progressState.value ?: return
        val total = (progress.charGold.toLong() + amount).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
        devSetGold(total)
    }

    /** Escribe un atributo base directamente. El HP/MP máximo se recalcula solo. */
    fun devSetAttribute(stat: String, value: Int) {
        val progress = _progressState.value ?: return
        val v = value.coerceIn(1, 9999)
        viewModelScope.launch {
            val updated = when (stat.uppercase()) {
                "STR" -> progress.copy(statStr = v)
                "DEX" -> progress.copy(statDex = v)
                "INT" -> progress.copy(statInt = v)
                "CON" -> progress.copy(statCon = v)
                else -> return@launch
            }
            saveProgressSynced(syncMaxHpAndMp(updated))
            showNotification("⚗️ ${stat.uppercase()} = $v.")
        }
    }

    fun devGrantPoints(statPoints: Int, talentPoints: Int) {
        val progress = _progressState.value ?: return
        viewModelScope.launch {
            val updated = progress.copy(
                statPointsAvailable = (progress.statPointsAvailable + statPoints).coerceAtLeast(0),
                talentPointsAvailable = (progress.talentPointsAvailable + talentPoints).coerceAtLeast(0)
            )
            saveProgressSynced(updated)
            showNotification("⚗️ Puntos: ${updated.statPointsAvailable} de atributo · ${updated.talentPointsAvailable} de talento.")
        }
    }

    fun devFullRestore() {
        val progress = _progressState.value ?: return
        viewModelScope.launch {
            val synced = syncMaxHpAndMp(progress)
            saveProgressSynced(synced.copy(currentHp = synced.maxHp, currentMp = synced.maxMp))
            showNotification("⚗️ Vida y maná al máximo (${synced.maxHp} / ${synced.maxMp}).")
        }
    }

    /**
     * Mete el objeto en el hueco que le toca sin pasar por [equipItem]: esa exige
     * `itemLevel <= charLevel` y aquí forjamos a propósito por encima del nivel.
     * Lo que hubiera puesto vuelve al inventario, nunca se destruye.
     */
    private fun devEquipInto(progress: GameProgress, item: Item, inventory: MutableList<Item>): GameProgress {
        val slotJson = GameJsonParser.toJson(item)
        return when (item.type.uppercase()) {
            "WEAPON" -> {
                GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)?.let { inventory.add(it) }
                progress.copy(equippedWeaponJson = slotJson)
            }
            "SHIELD" -> {
                GameJsonParser.fromJson<Item>(progress.equippedShieldJson)?.let { inventory.add(it) }
                progress.copy(equippedShieldJson = slotJson)
            }
            "HELMET" -> {
                GameJsonParser.fromJson<Item>(progress.equippedHelmetJson)?.let { inventory.add(it) }
                progress.copy(equippedHelmetJson = slotJson)
            }
            "ARMOR" -> {
                GameJsonParser.fromJson<Item>(progress.equippedArmorJson)?.let { inventory.add(it) }
                progress.copy(equippedArmorJson = slotJson)
            }
            "GLOVES" -> {
                GameJsonParser.fromJson<Item>(progress.equippedGlovesJson)?.let { inventory.add(it) }
                progress.copy(equippedGlovesJson = slotJson)
            }
            "BOOTS" -> {
                GameJsonParser.fromJson<Item>(progress.equippedBootsJson)?.let { inventory.add(it) }
                progress.copy(equippedBootsJson = slotJson)
            }
            "RING" -> {
                GameJsonParser.fromJson<Item>(progress.equippedRingJson)?.let { inventory.add(it) }
                progress.copy(equippedRingJson = slotJson)
            }
            "EARRING" -> {
                GameJsonParser.fromJson<Item>(progress.equippedEarringJson)?.let { inventory.add(it) }
                progress.copy(equippedEarringJson = slotJson)
            }
            "WINGS" -> {
                GameJsonParser.fromJson<Item>(progress.equippedWingsJson)?.let { inventory.add(it) }
                progress.copy(equippedWingsJson = slotJson)
            }
            "RELIC" -> {
                GameJsonParser.fromJson<Item>(progress.equippedRelicJson)?.let { inventory.add(it) }
                progress.copy(equippedRelicJson = slotJson)
            }
            else -> progress
        }
    }

    /**
     * Forja equipo a medida con el mismo generador que suelta el botín, pero
     * eligiendo tipo, rareza y nivel en vez de dejarlo al azar.
     */
    fun devForgeItem(type: String, rarity: String, itemLevel: Int, count: Int = 1, equip: Boolean = false) {
        val progress = _progressState.value ?: return
        val qty = count.coerceIn(1, 20)
        val lvl = itemLevel.coerceIn(1, 500)
        viewModelScope.launch {
            val inventory = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
            var updated = progress
            var lastName = ""
            repeat(qty) {
                val forged = generateProceduralItem(
                    level = lvl,
                    isBoss = false,
                    rarityPreset = rarity,
                    typePreset = type
                )
                lastName = forged.name
                // Sólo la última copia se equipa; el resto va al zurrón.
                if (equip && it == qty - 1) {
                    updated = devEquipInto(updated, forged, inventory)
                } else {
                    inventory.add(forged)
                }
            }
            val finalProgress = syncMaxHpAndMp(
                updated.copy(inventoryJson = GameJsonParser.listToJson(inventory.toList()))
            )
            saveProgressSynced(finalProgress)
            SoundManager.playButtonClick()
            showNotification(
                if (equip) "⚗️ Forjado y equipado: $lastName (niv. $lvl, $rarity)."
                else "⚗️ Forjadas $qty piezas: $lastName (niv. $lvl, $rarity)."
            )
        }
    }

    /** Un objeto por cada uno de los diez huecos, todos puestos de una vez. */
    fun devForgeFullSet(rarity: String, itemLevel: Int) {
        val progress = _progressState.value ?: return
        val lvl = itemLevel.coerceIn(1, 500)
        viewModelScope.launch {
            val inventory = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
            var updated = progress
            devEquipSlots.forEach { slot ->
                val forged = generateProceduralItem(
                    level = lvl,
                    isBoss = false,
                    rarityPreset = rarity,
                    typePreset = slot
                )
                updated = devEquipInto(updated, forged, inventory)
            }
            val finalProgress = syncMaxHpAndMp(
                updated.copy(inventoryJson = GameJsonParser.listToJson(inventory.toList()))
            )
            saveProgressSynced(finalProgress.copy(currentHp = finalProgress.maxHp, currentMp = finalProgress.maxMp))
            SoundManager.playVictory()
            showNotification("⚗️ Equipo completo $rarity de nivel $lvl puesto en los diez huecos.")
        }
    }

    /** Frascos del catálogo, gratis y sin mirar el nivel de desbloqueo. */
    fun devGrantPotions(potionId: String, quantity: Int) {
        val progress = _progressState.value ?: return
        val spec = EldoriaPotions.spec(potionId) ?: return
        val qty = quantity.coerceIn(1, 99)
        viewModelScope.launch {
            val inventory = GameJsonParser.listFromJson<Item>(progress.inventoryJson).toMutableList()
            val now = System.currentTimeMillis()
            repeat(qty) { i ->
                inventory.add(
                    Item(
                        // Mismo formato de id que `buyPotion`: el prefijo del catálogo
                        // es lo que `usePotionCombat` mira para saber qué frasco es.
                        id = "${spec.id}_${now}_${i}_${Random.nextInt(1000)}",
                        name = spec.name,
                        type = "POTION",
                        rarity = spec.rarity,
                        description = spec.description,
                        itemLevel = spec.unlockLevel,
                        imageResName = spec.artKey
                    )
                )
            }
            saveProgressSynced(progress.copy(inventoryJson = GameJsonParser.listToJson(inventory.toList())))
            showNotification("⚗️ $qty × ${spec.name} al zurrón.")
        }
    }

    /** Todos los materiales del catálogo a la vez, para no tocarlos de uno en uno. */
    fun devGrantAllMaterials(quantity: Int) {
        val qty = quantity.coerceIn(1, 999)
        systems.grantMaterials(
            com.example.data.content.EldoriaMaterials.ALL.associate { it.id to qty }
        )
    }

    fun devGrantTorches(quantity: Int) {
        systems.refillTorch(quantity.coerceIn(1, 99))
    }

    /** Abre los dieciséis calabozos del mapa sin tener que limpiarlos en orden. */
    fun devUnlockAllDungeons() {
        val progress = _progressState.value ?: return
        viewModelScope.launch {
            saveProgressSynced(progress.copy(highestUnlockedDungeon = 16))
            showNotification("⚗️ Todos los calabozos desbloqueados.")
        }
    }

    /** Enciende o apaga la consola. El estado vive en los ajustes de la partida. */
    fun devSetUnlocked(unlocked: Boolean) {
        systems.updateSettings(systems.settings.value.copy(devUnlocked = unlocked))
    }
}

data class DungeonData(
    val id: Int,
    val name: String,
    val species: String,
    val levelReq: Int,
    val finalBossName: String,
    val finalBossTitle: String,
    val subBosses: List<String>,
    val uniqueTreasure: Item,
    val bossImageResName: String,
    val description: String
)

data class DungeonRunState(
    val inDungeonRun: Boolean = false,
    val dungeonId: Int = 1,
    val currentStage: Int = 1,
    val persistentHp: Int = 100,
    val persistentMp: Int = 50,
    val stageVictoryPending: Boolean = false,
    val dungeonCompletedJustNow: Boolean = false
)

val DUNGEONS_LIST = listOf(
    DungeonData(
        id = 1,
        name = "Cavernas del Clan Goblin",
        species = "Goblins",
        levelReq = 20,
        finalBossName = "Hobgoblin",
        finalBossTitle = "Gran Warlord Hobgoblin",
        subBosses = listOf(
            "Goblin Explorador", "Goblin Chamán", "Goblin Asesino",
            "Goblin Táctico", "Goblin Cuadrillero", "Goblin Tamborilero",
            "Goblin Trampero", "Goblin Fanático", "Goblin Capataz"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_1",
            name = "Anillo de Avaricia del Hobgoblin",
            type = "RING",
            rarity = "UNIVERSAL",
            itemLevel = 10,
            strBonus = 35,
            dexBonus = 35,
            intBonus = 15,
            conBonus = 25,
            dmgBonus = 100,
            defBonus = 40,
            imageResName = "img_item_ring_1784593597914",
            description = "Anillo supremo forjado en el vientre de las cavernas goblin. Otorga poder masivo."
        ),
        bossImageResName = "img_boss_hobgoblin_1784674116743",
        description = "Calabozo inicial habitado por la vil plaga goblin. Derrota a los 9 subjefes para enfrentar al Hobgoblin."
    ),
    DungeonData(
        id = 2,
        name = "Fortaleza Orqueta de Hierro",
        species = "Orcos",
        levelReq = 40,
        finalBossName = "Rey Orco",
        finalBossTitle = "Soberano Sangriento de la Horda",
        subBosses = listOf(
            "Orco Berserker", "Chamán Orco", "Demoledor Orco",
            "Cazador Orco", "Tambor de Guerra Orco", "Asaltante Orco",
            "Warg Rider", "Capataz de Hierro", "Gladiador Orco"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_2",
            name = "Hacha de Guerra del Rey Orco",
            type = "WEAPON",
            rarity = "UNIVERSAL",
            itemLevel = 20,
            strBonus = 80,
            conBonus = 50,
            dmgBonus = 180,
            defBonus = 30,
            imageResName = "img_item_sword_1784593548868",
            description = "Devastadora hacha bidentada empuñada por el Rey Orco. Aplasta la armadura enemiga."
        ),
        bossImageResName = "img_enemy_ogre_1784386944311",
        description = "Bastión inexpugnable donde los orcos preparan sus invasiones. Su soberano no muestra piedad."
    ),
    DungeonData(
        id = 3,
        name = "Guarida de las Sombras",
        species = "Ladrones",
        levelReq = 60,
        finalBossName = "Ladrón Asesino",
        finalBossTitle = "Gran Maestro de la Guilda de las Sombras",
        subBosses = listOf(
            "Humano Mercenario", "Humano Ballestero", "Humano Sombra",
            "Envenenador Nocturno", "Matón de Callejón", "Capitán Filo",
            "Pirata del Puerto", "Infiltrador Humano", "Verdugo de las Sombras"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_3",
            name = "Daga Sombra del Ladrón Asesino",
            type = "WEAPON",
            rarity = "UNIVERSAL",
            itemLevel = 30,
            dexBonus = 100,
            strBonus = 40,
            dmgBonus = 220,
            imageResName = "img_item_dagger_1784593567531",
            description = "Hoja emponzoñada con veneno estigio. Ejecuta asaltos silenciosos y mortales."
        ),
        bossImageResName = "img_portrait_humano_picaro_1784507327963",
        description = "Catarro subterráneo repleto de mercenarios y asesinos humanos highly entrenados."
    ),
    DungeonData(
        id = 4,
        name = "Colinas Ferales de las Bestias",
        species = "Hombres bestia",
        levelReq = 80,
        finalBossName = "Rey Lobo Fenrir",
        finalBossTitle = "Titán Primigenio de las Colinas",
        subBosses = listOf(
            "Bestia Alfa", "Minotauro Feroz", "Hombre Jabalí",
            "Chacal Furioso", "Centauro de Guerra", "Licántropo Garra",
            "Pantera Umbría", "Oso de las Cavernas", "Chamán Bestial"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_4",
            name = "Manto Feroz de Fenrir",
            type = "ARMOR",
            rarity = "UNIVERSAL",
            itemLevel = 40,
            strBonus = 70,
            conBonus = 80,
            defBonus = 180,
            dmgBonus = 50,
            imageResName = "img_item_plate_1784593577913",
            description = "Pelaje indestructible impregnado con el aliento de hielo de Fenrir."
        ),
        bossImageResName = "img_enemy_boss_1784386985144",
        description = "Bosque salvaje habitado por bestias feroces. Fenrir destroza a quienes pisan su territorio."
    ),
    DungeonData(
        id = 5,
        name = "Fosa Abisal de las Mareas",
        species = "Naga",
        levelReq = 100,
        finalBossName = "Rey del Océano Neptuno",
        finalBossTitle = "Emperador de las Profundidades",
        subBosses = listOf(
            "Naga Cazador", "Sireno de Coral", "Mago de Mareas",
            "Tritón de las Profundidades", "Serpiente Abisal", "Guardián de Perlas",
            "Devorador de Fosas", "Bruja de Coral", "Leviatán Cazador"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_5",
            name = "Tridente Celestial de Neptuno",
            type = "WEAPON",
            rarity = "UNIVERSAL",
            itemLevel = 50,
            intBonus = 130,
            conBonus = 60,
            dmgBonus = 260,
            defBonus = 40,
            imageResName = "img_item_staff_1784593558118",
            description = "Arma legendaria de los mares que desata maremotos y rayos abisales."
        ),
        bossImageResName = "img_enemy_mud_golem_1784386930907",
        description = "Templo inundado habitado por guerreros naga y criaturas del abismo oceánico."
    ),
    DungeonData(
        id = 6,
        name = "Cripta Necrótica Sangrienta",
        species = "Muertos vivientes",
        levelReq = 120,
        finalBossName = "Vampiro de alto nivel",
        finalBossTitle = "Conde Sangriento Inmortal",
        subBosses = listOf(
            "Esqueleto Guerrero", "Lich Menor", "Caballero de la Muerte",
            "Momia Ancestral", "Alma en Pena", "Necrófago Voraz",
            "Necromante Oscuro", "Espectro de Hielo", "Dragón de Hueso"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_6",
            name = "Cáliz de Sangre del Vampiro Supremo",
            type = "RELIC",
            rarity = "UNIVERSAL",
            itemLevel = 60,
            conBonus = 90,
            intBonus = 90,
            defBonus = 150,
            dmgBonus = 100,
            imageResName = "img_item_relic_1784658251007",
            description = "Reliquia maldita que drena constantemente la esencia vital de los enemigos."
        ),
        bossImageResName = "img_boss_high_vampire_1784674139269",
        description = "Mausoleo ancestral plagado de no-muertos. El Conde Vampiro bebe la sangre de los intrusos."
    ),
    DungeonData(
        id = 7,
        name = "Santuario de las Almas Perdidas",
        species = "Espíritus",
        levelReq = 140,
        finalBossName = "Rey Necromancer",
        finalBossTitle = "Soberano Inmaterial del Infierno",
        subBosses = listOf(
            "Espectro del Vacío", "Alma Penante", "Sombra Tormentosa",
            "Orbe Etéreo", "Poltergeist Furioso", "Guardián Astral",
            "Lamento de las Sombras", "Furia del Viento", "Fuego Fatuo Ancestral"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_7",
            name = "Filacteria Etérea del Rey Necromancer",
            type = "EARRING",
            rarity = "UNIVERSAL",
            itemLevel = 70,
            intBonus = 150,
            dexBonus = 60,
            dmgBonus = 300,
            imageResName = "img_item_earring_1784658263366",
            description = "Artefacto inmortal que canaliza el poder de miles de almas atrapadas."
        ),
        bossImageResName = "img_enemy_spectre_1784386971041",
        description = "Reino etéreo envuelto en brumas místicas. Los espíritus atormentados defienden a su Rey."
    ),
    DungeonData(
        id = 8,
        name = "Templo Viperino Esmeralda",
        species = "Hombres serpiente",
        levelReq = 160,
        finalBossName = "Rey serpiente dragon",
        finalBossTitle = "Titán Viperino Primigenio",
        subBosses = listOf(
            "Guerrero Cobra", "Sacerdote Víbora", "Gorgona de Hierro",
            "Basilisco Menor", "Cascabel de Muerte", "Nagani Guardián",
            "Ilusionista Escamado", "Devorador de Veneno", "Asesino Anaconda"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_8",
            name = "Escudo Draco-Serpiente del Rey",
            type = "SHIELD",
            rarity = "UNIVERSAL",
            itemLevel = 80,
            conBonus = 140,
            defBonus = 320,
            dmgBonus = 60,
            imageResName = "img_item_shield_1784593608106",
            description = "Escudo impenetrable forjado con escamas de dragón viperino esmeralda."
        ),
        bossImageResName = "img_enemy_spider_1784386956688",
        description = "Pirámide antigua habitada por adoradores de la gran serpiente. Su Rey escupe veneno dracónico."
    ),
    DungeonData(
        id = 9,
        name = "Laberinto Cibernético Titanium",
        species = "Máquinas",
        levelReq = 180,
        finalBossName = "Igdrasil El cerebro de las máquinas",
        finalBossTitle = "Superinteligencia Sintética Titánica",
        subBosses = listOf(
            "Autómata de Bronce", "Golem de Engranajes", "Centinela de Energía",
            "Dron Láser", "Destructor de Titanio", "Célula Voltáica",
            "Coloso Mecánico", "Núcleo de Plasma", "Ejecutor Cibernético"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_9",
            name = "Matriz Cuántica de Igdrasil",
            type = "RELIC",
            rarity = "UNIVERSAL",
            itemLevel = 90,
            strBonus = 90,
            dexBonus = 90,
            intBonus = 90,
            conBonus = 90,
            defBonus = 250,
            dmgBonus = 150,
            imageResName = "img_item_relic_1784658251007",
            description = "El cerebro central sintético. Procesa y neutraliza todas las amenazas de batalla."
        ),
        bossImageResName = "img_boss_yggdrasil_machine_1784674150126",
        description = "Complejo futurista subterráneo gobernado por constructos autómatas y el procesador Yggdrasil."
    ),
    DungeonData(
        id = 10,
        name = "Abismo de la Calamidad",
        species = "Dragones",
        levelReq = 200,
        finalBossName = "Dragon Oscuro",
        finalBossTitle = "Emperador Supremo del Caos Inmemorial",
        subBosses = listOf(
            "Cría de Dragón", "Wyvern de Fuego", "Drakoniano de Magma",
            "Dragón de Viento", "Hidra Venenosa", "Dragón Dorado",
            "Drake Caótico", "Wyrm de Hielo", "Dragón Ancestral"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_10",
            name = "Alas de la Calamidad Oscura",
            type = "WINGS",
            rarity = "UNIVERSAL",
            itemLevel = 100,
            strBonus = 160,
            dexBonus = 160,
            intBonus = 160,
            conBonus = 160,
            dmgBonus = 400,
            defBonus = 400,
            imageResName = "img_item_wings_1784658202673",
            description = "Las alas del mítico Dragón Oscuro. Otorgan supremacía absoluta sobre la creación."
        ),
        bossImageResName = "img_boss_dark_dragon_1784674128719",
        description = "El desafío definitivo de Eldoria. Derrota a los 9 supremos dragones antes de enfrentar al Dragón Oscuro."
    ),
    DungeonData(
        id = 11,
        name = "Santuario Serafín del Firmamento",
        species = "Serafines y Ángeles",
        levelReq = 220,
        finalBossName = "Archicreador Seraph",
        finalBossTitle = "Soberano Sagrado de los Cielos Astrales",
        subBosses = listOf(
            "Ángel Guerrero", "Valquiria de Cristal", "Serafín de Luz",
            "Sentinela Sagrado", "Guardián Celestial", "Arcángel Menor",
            "Querubín de Guerra", "Sacerdote Estelar", "Juez del Firmamento"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_11",
            name = "Corona Divina del Archicreador",
            type = "HELMET",
            rarity = "UNIVERSAL",
            itemLevel = 110,
            strBonus = 180,
            dexBonus = 180,
            intBonus = 220,
            conBonus = 180,
            dmgBonus = 450,
            defBonus = 450,
            imageResName = "img_item_relic_1784658251007",
            description = "Corona forjada con rayos de luz pura celestial. Emite un aura de inmortalidad suprema."
        ),
        bossImageResName = "img_boss_yggdrasil_machine_1784674150126",
        description = "Templo suspendido en el éter habitado por seres de luz divina. El Archicreador juzga a los mortales."
    ),
    DungeonData(
        id = 12,
        name = "Cráter Abisal del Maelstrom",
        species = "Devoradores Abisales",
        levelReq = 250,
        finalBossName = "Leviatán Cthulhu",
        finalBossTitle = "Titán Abisal Devorador de Mundos",
        subBosses = listOf(
            "Calamar Estigio", "Devorador de Almas", "Guardián de la Fosa",
            "Engendro del Maelstrom", "Sombra Estigia", "Tritón Maldito",
            "Kraken de Sangre", "Basilisco Marino", "Horror de la Fosa"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_12",
            name = "Orbe Abisal de Cthulhu",
            type = "RELIC",
            rarity = "UNIVERSAL",
            itemLevel = 125,
            strBonus = 200,
            dexBonus = 200,
            intBonus = 250,
            conBonus = 220,
            dmgBonus = 520,
            defBonus = 500,
            imageResName = "img_item_relic_1784658251007",
            description = "Orbe de materia abisal primigenia que retuerce la realidad y absorbe el alma de los enemigos."
        ),
        bossImageResName = "img_enemy_spectre_1784386971041",
        description = "Abismo insondable bajo las corrientes del multiverso donde Cthulhu aguarda su despertar."
    ),
    DungeonData(
        id = 13,
        name = "Forja Cósmica de los Titanidos",
        species = "Titanes de Piedra",
        levelReq = 280,
        finalBossName = "Forjador Supremo Aethel",
        finalBossTitle = "Creador de las Estrellas Primigenias",
        subBosses = listOf(
            "Gólem de Obsidiana", "Coloso de Bronce", "Minotauro de Titanio",
            "Elemental de Plasma", "Destructor Estelar", "Guardián de la Forja",
            "Escultor del Caos", "Centinela Cósmico", "Bégimo Rúnico"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_13",
            name = "Martillo de la Creación Cósmica",
            type = "WEAPON",
            rarity = "UNIVERSAL",
            itemLevel = 140,
            strBonus = 300,
            conBonus = 250,
            dmgBonus = 650,
            defBonus = 400,
            imageResName = "img_item_sword_1784593548868",
            description = "Martillo con el que Aethel forjó las galaxias. Sus impactos desatan supernovas."
        ),
        bossImageResName = "img_enemy_ogre_1784386944311",
        description = "La forja primordial del universo donde se moldearon las primeras estrellas y planetas."
    ),
    DungeonData(
        id = 14,
        name = "Infierno de la Llama Caótica",
        species = "Demonios Supremos",
        levelReq = 320,
        finalBossName = "Lucifer Señor del Inframundo",
        finalBossTitle = "Emperador Infernal del Caos Absoluto",
        subBosses = listOf(
            "Súcubo Infernal", "Cerbero de Lava", "Belfegor de la Gula",
            "Mammon del Infortunio", "Gárgola de Inframundo", "Íncubo de la Sombra",
            "Archidemonio de Azufre", "Balrog de las Cenizas", "Señor de la Tormenta"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_14",
            name = "Manto Infernal de Lucifer",
            type = "ARMOR",
            rarity = "UNIVERSAL",
            itemLevel = 160,
            strBonus = 280,
            dexBonus = 280,
            intBonus = 280,
            conBonus = 320,
            dmgBonus = 600,
            defBonus = 700,
            imageResName = "img_item_plate_1784593577913",
            description = "Armadura de fuego eterno y azufre del infierno. Devora los ataques y sana al portador."
        ),
        bossImageResName = "img_boss_hobgoblin_1784674116743",
        description = "El noveno círculo infernal. Lucifer gobierna sobre las llamas eternas consumiendo a los caídos."
    ),
    DungeonData(
        id = 15,
        name = "Vértice del Vacío Absoluto",
        species = "Espectros del Vacío",
        levelReq = 360,
        finalBossName = "Sombra del Dios Olvidado",
        finalBossTitle = "Deidad Omnipotente Inmemorial",
        subBosses = listOf(
            "Espíritu del Abismo", "Terror Nocturno", "Aniquilador Estelar",
            "Ente Incorpóreo", "Vértice de Sombras", "Distorsión Cuántica",
            "Espectro Infinito", "Centinela del Vacío", "Herrero del Olvido"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_15",
            name = "Anillo del Creador Inmortal",
            type = "RING",
            rarity = "UNIVERSAL",
            itemLevel = 180,
            strBonus = 350,
            dexBonus = 350,
            intBonus = 350,
            conBonus = 350,
            dmgBonus = 800,
            defBonus = 800,
            imageResName = "img_item_ring_1784593597914",
            description = "Anillo de la existencia pura. Concede omnipresencia e invulnerabilidad casi perfecta."
        ),
        bossImageResName = "img_boss_high_vampire_1784674139269",
        description = "La nada absoluta más allá del espacio-tiempo. Solo los guerreros más legendarios sobreviven."
    ),
    DungeonData(
        id = 16,
        name = "Trono del Gran Multiverso",
        species = "Dioses Primigenios",
        levelReq = 400,
        finalBossName = "Ouroboros el Eterno",
        finalBossTitle = "Señor del Infinito y Creador del Multiverso",
        subBosses = listOf(
            "Guardián del Tiempo", "Sombra del Big Bang", "Dragón de la Creación",
            "Titán del Espacio", "Avatar de la Realidad", "Nébula Destructora",
            "Soberano Cósmico", "Vértice Infinito", "Heraldo del Destino"
        ),
        uniqueTreasure = Item(
            id = "item_treasure_16",
            name = "Cetro del Infinito Multiversal",
            type = "WEAPON",
            rarity = "UNIVERSAL",
            itemLevel = 200,
            strBonus = 500,
            dexBonus = 500,
            intBonus = 500,
            conBonus = 500,
            dmgBonus = 1200,
            defBonus = 1000,
            imageResName = "img_item_staff_1784593558118",
            description = "El arma suprema del multiverso. Canaliza el poder creador y destructor de todas las dimensiones."
        ),
        bossImageResName = "img_boss_dark_dragon_1784674128719",
        description = "La cúspide suprema de toda la existencia. Enfrenta a Ouroboros para dominar el multiverso."
    )
)

