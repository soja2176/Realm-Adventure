package com.example.data.engine

import com.example.data.GameJsonParser
import com.example.data.GameProgress
import com.example.data.GameScreen
import com.example.data.Item
import com.example.data.content.EldoriaBestiary
import com.example.data.content.EldoriaContracts
import com.example.data.content.EldoriaExpeditions
import com.example.data.content.EldoriaMaterials
import com.example.data.content.EldoriaPets
import com.example.data.content.KingdomAtlas
import com.example.eldoria.core.content.KingdomQuestGenerator
import com.example.data.model.BestiaryEntry
import com.example.data.model.ContractDef
import com.example.data.model.ContractProgress
import com.example.data.model.DungeonBlueprint
import com.example.data.model.EldoriaToastMessage
import com.example.data.model.EnemyDecoration
import com.example.data.model.ExpeditionBoon
import com.example.data.model.ExpeditionOffer
import com.example.data.model.ExpeditionRoom
import com.example.data.model.ExpeditionSeal
import com.example.data.model.ExpeditionState
import com.example.data.model.GameSettings
import com.example.data.model.MaterialDef
import com.example.data.model.MinigameRequest
import com.example.data.model.MinigameResult
import com.example.data.model.PetCombatProfile
import com.example.data.model.PetRecord
import com.example.data.model.PetSpecies
import com.example.data.model.PetTrait
import com.example.data.model.RunStats
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Controlador de estado del overhaul "Renacer de Eldoria".
 *
 * Posee TODOS los flujos nuevos (expediciones, mascotas, bestiario, materiales,
 * contratos, minijuegos, ajustes y estadísticas) y es el único punto por el que
 * la UI puede mutarlos.
 *
 * Reglas internas, sin excepciones:
 *  - Nunca toca Room: sólo `host.currentProgress()` + `host.persistProgress(...)`.
 *  - Cada acción pública produce **una sola** escritura: se abre un [Draft], se
 *    modifica en memoria y se confirma con un único `copy(...)`.
 *  - Nunca navega por su cuenta: siempre `host.hostNavigate(...)`.
 *  - Todos los mensajes al jugador van en español con emoji líder.
 *  - Sin nada de Compose ni de Android.
 */
class EldoriaSystemsController(private val host: EldoriaHost) {

    // ═══════════════════════════════════════════════════════════════════════
    //  FLOWS (sólo lectura para la UI)
    // ═══════════════════════════════════════════════════════════════════════

    private val _toast = MutableStateFlow<EldoriaToastMessage?>(null)
    val toast: StateFlow<EldoriaToastMessage?> = _toast.asStateFlow()

    private val _expedition = MutableStateFlow(ExpeditionState())
    val expedition: StateFlow<ExpeditionState> = _expedition.asStateFlow()

    private val _expeditionOffer = MutableStateFlow<ExpeditionOffer?>(null)
    val expeditionOffer: StateFlow<ExpeditionOffer?> = _expeditionOffer.asStateFlow()

    private val _petRoster = MutableStateFlow<List<PetRecord>>(emptyList())
    val petRoster: StateFlow<List<PetRecord>> = _petRoster.asStateFlow()

    private val _activePet = MutableStateFlow<PetRecord?>(null)
    val activePet: StateFlow<PetRecord?> = _activePet.asStateFlow()

    private val _bestiary = MutableStateFlow<Map<String, Int>>(emptyMap())
    val bestiary: StateFlow<Map<String, Int>> = _bestiary.asStateFlow()

    private val _materials = MutableStateFlow<Map<String, Int>>(emptyMap())
    val materials: StateFlow<Map<String, Int>> = _materials.asStateFlow()

    private val _settings = MutableStateFlow(GameSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    private val _contracts = MutableStateFlow<List<ContractProgress>>(emptyList())
    val contracts: StateFlow<List<ContractProgress>> = _contracts.asStateFlow()

    private val _contractBoard = MutableStateFlow<List<ContractDef>>(emptyList())
    val contractBoard: StateFlow<List<ContractDef>> = _contractBoard.asStateFlow()

    /**
     * Encargos del reino que se pisa ahora mismo. Se regeneran al cruzar una
     * frontera: son ofertas locales, no un tablón que te sigue por el mundo.
     */
    private val _kingdomBoard = MutableStateFlow<List<ContractDef>>(emptyList())
    val kingdomBoard: StateFlow<List<ContractDef>> = _kingdomBoard.asStateFlow()

    /** Id del reino cuyo tablón local está cargado. */
    private val _kingdomBoardId = MutableStateFlow("")
    val kingdomBoardId: StateFlow<String> = _kingdomBoardId.asStateFlow()

    private val _minigame = MutableStateFlow<MinigameRequest?>(null)
    val minigame: StateFlow<MinigameRequest?> = _minigame.asStateFlow()

    private val _lastMinigameResult = MutableStateFlow<MinigameResult?>(null)
    val lastMinigameResult: StateFlow<MinigameResult?> = _lastMinigameResult.asStateFlow()

    private val _torchStock = MutableStateFlow(3)
    val torchStock: StateFlow<Int> = _torchStock.asStateFlow()

    private val _runStats = MutableStateFlow(RunStats())
    val runStats: StateFlow<RunStats> = _runStats.asStateFlow()

    // ── Estado interno no expuesto ──
    private var minigameScores: Map<String, Int> = emptyMap()
    private var boardSeed: Long = 0L
    private var hydratedCharId: Int = -1

    /** Id del personaje al que ya se le concedió la bestia de rescate en esta sesión. */
    private var starterPetCharId: Int = -1

    // ═══════════════════════════════════════════════════════════════════════
    //  CONSTANTES
    // ═══════════════════════════════════════════════════════════════════════

    private companion object {
        const val DISC_FURY = "FURIA"
        const val DISC_BASTION = "BASTION"
        const val DISC_VITALITY = "VITALIDAD"

        const val MAT_ANIMA = "anima_shard"
        const val MAT_KEY = "sealed_key"
        const val MAT_EMBER = "forge_ember"

        const val KIND_HUNT = "CAZA"
        const val KIND_EXPEDITION = "EXPEDICION"
        const val KIND_GATHER = "RECOLECCION"
        const val KIND_TAMING = "DOMA"

        const val MAX_ACTIVE_CONTRACTS = 3
        const val MAX_ROSTER = 12
        const val MAX_TORCH = 100
        const val TORCH_PRICE = 400
        const val ADOPTION_BASE_PRICE = 1_200
        const val ADOPTION_STEP_PRICE = 600

        const val SEAL_BLOOD = "seal_sangre"
        const val SEAL_HUNGER = "seal_hambre"
        const val SEAL_IRON = "seal_hierro"
        const val SEAL_GREED = "seal_avaricia"
        const val SEAL_JUDGEMENT = "seal_juicio"

        const val ABYSS_TIDES = 101
        const val ABYSS_CLOCK = 102
        const val ABYSS_CROWN = 103

        const val POTION_ART = "img_item_potion_1784593618142"
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  SERIALIZACIÓN DE MAPAS String→Int
    //  (Moshi rechaza tipos primitivos como argumento genérico, así que el
    //   adaptador se construye con el tipo envuelto `java.lang.Integer`.)
    // ═══════════════════════════════════════════════════════════════════════

    private val countsAdapter by lazy {
        GameJsonParser.moshi.adapter<Map<String, Int>>(
            Types.newParameterizedType(Map::class.java, String::class.java, Int::class.javaObjectType)
        )
    }

    private fun encodeCounts(map: Map<String, Int>): String = try {
        countsAdapter.toJson(map.filterValues { it > 0 })
    } catch (e: Exception) {
        "{}"
    }

    private fun decodeCounts(json: String): Map<String, Int> {
        if (json.isBlank() || json == "{}") return emptyMap()
        return try {
            countsAdapter.fromJson(json)?.filterValues { it > 0 } ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  BORRADOR DE MUTACIÓN — una acción, un copy(), una escritura
    // ═══════════════════════════════════════════════════════════════════════

    private inner class Draft(val base: GameProgress) {
        var gold: Int = base.charGold
        var exp: Int = base.charExp
        var hp: Int = base.currentHp
        var mp: Int = base.currentMp
        val inventory: MutableList<Item> = try {
            GameJsonParser.listFromJson<Item>(base.inventoryJson).toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }
        val roster: MutableList<PetRecord> = _petRoster.value.toMutableList()
        var activePetId: String = _activePet.value?.id ?: base.activePetId
        val materialBag: LinkedHashMap<String, Int> = LinkedHashMap(_materials.value)
        val killBook: LinkedHashMap<String, Int> = LinkedHashMap(_bestiary.value)
        val scores: LinkedHashMap<String, Int> = LinkedHashMap(minigameScores)
        var contractList: MutableList<ContractProgress> = _contracts.value.toMutableList()
        var expeditionState: ExpeditionState = _expedition.value
        var offer: ExpeditionOffer? = _expeditionOffer.value
        var settingsValue: GameSettings = _settings.value
        var torch: Int = _torchStock.value
        var stats: RunStats = _runStats.value
        var navigate: GameScreen? = null
        val after: MutableList<() -> Unit> = mutableListOf()

        fun commit() {
            val normalizedActive = when {
                roster.any { it.id == activePetId } -> activePetId
                roster.isNotEmpty() -> roster.first().id
                else -> ""
            }
            val liveExpedition = expeditionState.active
            val maxHp = base.maxHp.coerceAtLeast(1)
            val maxMp = base.maxMp.coerceAtLeast(1)
            val finalStats = stats.copy(
                petsOwned = roster.size,
                speciesDiscovered = killBook.count { it.value > 0 }
            )

            val updated = base.copy(
                charGold = gold.coerceAtLeast(0),
                charExp = exp.coerceAtLeast(0),
                currentHp = hp.coerceIn(0, maxHp),
                currentMp = mp.coerceIn(0, maxMp),
                inventoryJson = GameJsonParser.listToJson(inventory.toList()),
                petRosterJson = GameJsonParser.listToJson(roster.toList()),
                activePetId = normalizedActive,
                bestiaryJson = encodeCounts(killBook),
                materialsJson = encodeCounts(materialBag),
                expeditionJson = if (liveExpedition) GameJsonParser.toJson(expeditionState) else "",
                minigameStatsJson = encodeCounts(scores),
                settingsJson = GameJsonParser.toJson(settingsValue),
                contractsJson = GameJsonParser.listToJson(contractList.toList()),
                torchStock = torch.coerceIn(0, 99),
                totalKills = finalStats.totalKills,
                bossKills = finalStats.bossKills,
                dungeonsCleared = finalStats.dungeonsCleared,
                deepestDepth = finalStats.deepestDepth
            )
            host.persistProgress(updated)

            _petRoster.value = roster.toList()
            _activePet.value = roster.firstOrNull { it.id == normalizedActive }
            _materials.value = materialBag.filterValues { it > 0 }
            _bestiary.value = killBook.filterValues { it > 0 }
            minigameScores = scores.toMap()
            _contracts.value = contractList.toList()
            _expedition.value = expeditionState
            _expeditionOffer.value = if (liveExpedition) offer else null
            _settings.value = settingsValue
            _torchStock.value = torch.coerceIn(0, 99)
            _runStats.value = finalStats

            navigate?.let { host.hostNavigate(it) }
            after.forEach { it.invoke() }
        }
    }

    /** Abre un borrador, ejecuta el bloque y confirma sólo si devuelve `true`. */
    private fun edit(block: (Draft) -> Boolean) {
        val progress = host.currentProgress() ?: return
        val draft = Draft(progress)
        val commit = try {
            block(draft)
        } catch (e: Exception) {
            false
        }
        if (commit) draft.commit()
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CICLO DE VIDA
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Rehidrata los 14 flujos desde las 13 columnas nuevas de [progress].
     * Idempotente y tolerante a JSON vacío o corrupto: nunca lanza.
     */
    fun hydrate(progress: GameProgress) {
        val sameCharacter = progress.id == hydratedCharId
        hydratedCharId = progress.id

        val roster = try {
            GameJsonParser.listFromJson<PetRecord>(progress.petRosterJson)
                .filter { it.id.isNotBlank() && it.speciesId.isNotBlank() }
                .map { sanitizePet(it) }
        } catch (e: Exception) {
            emptyList()
        }
        _petRoster.value = roster

        val activeId = progress.activePetId
        _activePet.value = roster.firstOrNull { it.id == activeId } ?: roster.firstOrNull()

        _bestiary.value = decodeCounts(progress.bestiaryJson)
        _materials.value = decodeCounts(progress.materialsJson)
        minigameScores = decodeCounts(progress.minigameStatsJson)

        _settings.value = try {
            GameJsonParser.fromJson<GameSettings>(progress.settingsJson) ?: GameSettings()
        } catch (e: Exception) {
            GameSettings()
        }

        _contracts.value = try {
            GameJsonParser.listFromJson<ContractProgress>(progress.contractsJson)
                .filter { it.id.isNotBlank() }
                .map { it.copy(amount = it.amount.coerceAtLeast(1)) }
        } catch (e: Exception) {
            emptyList()
        }

        val stored = try {
            GameJsonParser.fromJson<ExpeditionState>(progress.expeditionJson) ?: ExpeditionState()
        } catch (e: Exception) {
            ExpeditionState()
        }
        val fromDisk = if (stored.active && stored.rooms.isNotEmpty()) stored else ExpeditionState()
        val live = _expedition.value
        // Una fila antigua de Room no puede deshacer el avance de la expedición en curso.
        _expedition.value = when {
            !sameCharacter -> fromDisk
            !live.active -> fromDisk
            fromDisk.active && fromDisk.seed == live.seed ->
                if (fromDisk.roomsCleared >= live.roomsCleared) fromDisk else live
            fromDisk.active -> fromDisk
            else -> live
        }
        if (!_expedition.value.active) _expeditionOffer.value = null

        _torchStock.value = progress.torchStock.coerceIn(0, 99)

        _runStats.value = RunStats(
            totalKills = progress.totalKills.coerceAtLeast(0),
            bossKills = progress.bossKills.coerceAtLeast(0),
            dungeonsCleared = progress.dungeonsCleared.coerceAtLeast(0),
            deepestDepth = progress.deepestDepth.coerceAtLeast(0),
            petsOwned = roster.size,
            speciesDiscovered = _bestiary.value.count { it.value > 0 }
        )

        if (boardSeed == 0L) boardSeed = progress.id.toLong() * 7_919L + progress.charLevel.toLong() * 131L + 17L
        if (_contractBoard.value.isEmpty()) {
            _contractBoard.value = EldoriaContracts.rollBoard(progress.charLevel, boardSeed, 6)
        }

        // Bestia inicial. Cubre tanto al héroe recién creado como a la partida
        // migrada desde la versión 8, que llega con `petRosterJson = '[]'` y se
        // quedaría sin santuario, sin órdenes de bestia y sin contratos de DOMA.
        if (roster.isEmpty() && progress.hasActiveChar && starterPetCharId != progress.id) {
            starterPetCharId = progress.id
            val granted = grantRandomPet(
                level = progress.charLevel.coerceAtLeast(1),
                rarityFloor = "COMÚN"
            )
            if (granted == null) starterPetCharId = -1
        }
    }

    private fun sanitizePet(record: PetRecord): PetRecord {
        val stage = record.stage.coerceIn(1, 3)
        val cap = EldoriaPets.disciplineCap(record.level.coerceAtLeast(1))
        return record.copy(
            level = record.level.coerceIn(1, EldoriaPets.levelCapForStage(stage)),
            stage = stage,
            satiety = record.satiety.coerceIn(0, 100),
            bond = record.bond.coerceIn(0, 100),
            disciplineAtk = record.disciplineAtk.coerceIn(0, cap),
            disciplineDef = record.disciplineDef.coerceIn(0, cap),
            disciplineVit = record.disciplineVit.coerceIn(0, cap),
            name = record.name.ifBlank { EldoriaPets.stageName(record.speciesId, stage) }
        )
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  AVISOS NO BLOQUEANTES
    // ═══════════════════════════════════════════════════════════════════════

    fun showToast(message: String, tone: String = "GOLD") {
        if (message.isBlank()) return
        _toast.value = EldoriaToastMessage(
            message = message,
            tone = tone,
            stamp = System.currentTimeMillis()
        )
    }

    fun dismissToast() {
        _toast.value = null
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  EXPEDICIONES
    // ═══════════════════════════════════════════════════════════════════════

    fun availableSeals(): List<ExpeditionSeal> = EldoriaExpeditions.SEALS

    fun availableBlueprints(): List<DungeonBlueprint> = EldoriaExpeditions.BLUEPRINTS

    fun blueprintFor(dungeonId: Int): DungeonBlueprint? = EldoriaExpeditions.blueprint(dungeonId)

    fun dangerMultiplier(seals: List<String>): Float = EldoriaExpeditions.dangerMultiplierOf(seals)

    fun rewardMultiplier(seals: List<String>): Float = EldoriaExpeditions.rewardMultiplierOf(seals)

    fun canStartExpedition(dungeonId: Int): Boolean {
        val progress = host.currentProgress() ?: return false
        val blueprint = EldoriaExpeditions.blueprint(dungeonId) ?: return false
        if (_expedition.value.active) return false
        if (progress.charLevel < blueprint.levelReq) return false
        return _torchStock.value >= 1
    }

    fun startExpedition(dungeonId: Int, seals: List<String> = emptyList()) {
        val progress = host.currentProgress() ?: return
        val blueprint = EldoriaExpeditions.blueprint(dungeonId)
        if (blueprint == null) {
            showToast("🗺️ Ese destino no existe en los mapas del gremio.", "IRON")
            return
        }
        if (_expedition.value.active) {
            showToast("🕯️ Ya tienes una expedición en curso: termínala o abandónala.", "EMBER")
            return
        }
        if (progress.charLevel < blueprint.levelReq) {
            showToast("🔒 ${blueprint.name} exige nivel ${blueprint.levelReq}.", "IRON")
            return
        }
        if (_torchStock.value < 1) {
            showToast("🕯️ No te queda ninguna antorcha. Compra alguna antes de bajar.", "EMBER")
            return
        }

        val chosen = (EldoriaExpeditions.mandatorySeals(dungeonId) + seals)
            .filter { EldoriaExpeditions.seal(it) != null }
            .distinct()
            .take(3)
        val seed = System.currentTimeMillis()
        val baseDepth = EldoriaExpeditions.defaultMaxDepth(dungeonId)
        val rooms = EldoriaGenerators.buildExpeditionGraph(dungeonId, seed, baseDepth, chosen)
        if (rooms.isEmpty()) {
            showToast("🌫️ El abismo se niega a abrirse hoy. Inténtalo de nuevo.", "ARCANE")
            return
        }
        val entries = entryRoomIds(rooms)
        val depthCount = (rooms.maxOfOrNull { it.depth } ?: 0) + 1

        val state = ExpeditionState(
            active = true,
            dungeonId = dungeonId,
            dungeonName = blueprint.name,
            speciesLabel = blueprint.species,
            paletteKey = blueprint.paletteKey,
            artResName = blueprint.artResName,
            seed = seed,
            rooms = rooms,
            currentRoomId = -1,
            availableRoomIds = entries,
            depth = 0,
            maxDepth = depthCount,
            torch = EldoriaGenerators.defaultTorch(chosen),
            seals = chosen,
            boons = emptyList(),
            runLoot = emptyList(),
            shards = 0,
            keys = 0,
            persistentHp = progress.currentHp,
            persistentMp = progress.currentMp,
            roomsCleared = 0,
            elitesCleared = 0,
            awaitingChoice = false,
            finished = false,
            victory = false,
            log = listOf("🔥 Enciendes la antorcha a las puertas de ${blueprint.name}.")
        )

        edit { draft ->
            draft.expeditionState = state
            draft.offer = null
            draft.torch = (draft.torch - 1).coerceAtLeast(0)
            if (chosen.size >= 3) {
                draft.contractList = contractsAdvanced(draft.contractList, KIND_EXPEDITION, "SEALS:3", 1).toMutableList()
            }
            draft.navigate = GameScreen.EXPEDITION
            draft.after += { host.hostPlaySound("click") }
            true
        }
        showToast("🕯️ Desciendes a ${blueprint.name}. ${blueprint.loreShort}", "EMBER")
    }

    fun resumeExpedition(): Boolean {
        val state = _expedition.value
        if (!state.active) return false
        if (!state.finished && state.availableRoomIds.isEmpty() && _expeditionOffer.value == null) {
            val current = state.rooms.firstOrNull { it.id == state.currentRoomId }
            val recovered = when {
                current == null -> entryRoomIds(state.rooms)
                current.cleared -> current.next.filter { id -> state.rooms.firstOrNull { it.id == id }?.cleared == false }
                else -> listOf(current.id)
            }
            val safe = recovered.ifEmpty { entryRoomIds(state.rooms) }
            edit { draft ->
                draft.expeditionState = state.copy(
                    availableRoomIds = safe,
                    awaitingChoice = false,
                    log = appendLog(state, "🧭 Recuperas el rumbo dentro del abismo.")
                )
                true
            }
        }
        host.hostNavigate(GameScreen.EXPEDITION)
        return true
    }

    fun abandonExpedition() {
        val state = _expedition.value
        if (!state.active) {
            host.hostNavigate(GameScreen.DUNGEON)
            return
        }
        edit { draft ->
            salvageRun(draft, "🚪 Abandonas la expedición y sales con la mitad de lo recogido.", navigateHome = true)
            true
        }
    }

    fun enterRoom(roomId: Int) {
        val state = _expedition.value
        if (!state.active || state.finished) {
            showToast("🕯️ No hay ninguna expedición en curso.", "IRON")
            return
        }
        if (_expeditionOffer.value != null) {
            showToast("📜 Primero decide qué hacer en la sala actual.", "ARCANE")
            return
        }
        if (roomId !in state.availableRoomIds) {
            showToast("🚫 Esa sala no conecta con tu posición.", "IRON")
            return
        }
        val room = state.rooms.firstOrNull { it.id == roomId } ?: return

        edit { draft ->
            val cost = torchCostOf(room.kind, state.seals, state.dungeonId)
            val torchLeft = state.torch - cost
            val revealed = state.rooms.map { candidate ->
                when {
                    candidate.id == roomId -> candidate.copy(revealed = true)
                    candidate.id in room.next -> candidate.copy(revealed = true)
                    else -> candidate
                }
            }
            var moved = state.copy(
                rooms = revealed,
                currentRoomId = roomId,
                depth = room.depth,
                torch = torchLeft.coerceAtLeast(0),
                availableRoomIds = emptyList(),
                awaitingChoice = false,
                persistentHp = draft.hp,
                persistentMp = draft.mp,
                log = appendLog(state, "🚪 Entras en ${room.label} · ${EldoriaExpeditions.roomKindName(room.kind)}.")
            )
            draft.expeditionState = moved

            if (torchLeft <= 0) {
                salvageRun(draft, "🕯️ La antorcha se apaga y la oscuridad te devuelve a la superficie.", navigateHome = true)
                return@edit true
            }

            when (room.kind.uppercase()) {
                EldoriaExpeditions.KIND_COMBAT,
                EldoriaExpeditions.KIND_ELITE,
                EldoriaExpeditions.KIND_BOSS -> {
                    val blueprint = EldoriaExpeditions.blueprint(state.dungeonId)
                    val isBoss = room.kind.uppercase() == EldoriaExpeditions.KIND_BOSS
                    val bossName = if (isBoss) blueprint?.finalBossName else null
                    val hp = draft.hp.coerceAtLeast(1)
                    val mp = draft.mp.coerceAtLeast(0)
                    draft.after += {
                        host.hostPlaySound(if (isBoss) "enemy" else "slash")
                        host.hostStartExpeditionCombat(
                            state.dungeonId,
                            room.depth,
                            room.kind,
                            room.label,
                            hp,
                            mp,
                            bossName
                        )
                    }
                }

                else -> {
                    val built = offerForRoom(room, moved, draft.base.charLevel)
                    if (built == null) {
                        // Sala sin decisión posible: se resuelve sola.
                        advanceAfterRoom(draft, room)
                    } else {
                        moved = moved.copy(awaitingChoice = true)
                        draft.expeditionState = moved
                        draft.offer = built
                    }
                }
            }
            true
        }
    }

    fun resolveRoomChoice(optionId: String) {
        val state = _expedition.value
        if (!state.active || state.finished) return
        val room = state.rooms.firstOrNull { it.id == state.currentRoomId } ?: return

        edit { draft ->
            val level = draft.base.charLevel.coerceAtLeast(1)
            val maxHp = draft.base.maxHp.coerceAtLeast(1)
            val maxMp = draft.base.maxMp.coerceAtLeast(1)
            val rnd = Random(state.seed + room.id * 7_907L + optionId.hashCode().toLong())
            var run = draft.expeditionState
            var note = ""

            when (optionId) {
                // ── TESORO ──
                "loot_open" -> {
                    val loot = EldoriaGenerators.expeditionLoot(
                        run.dungeonId, room.depth, EldoriaExpeditions.KIND_TREASURE, level, run.seed + room.id
                    )
                    run = run.copy(runLoot = run.runLoot + applyLootBoons(loot, run))
                    grantMaterialsInto(draft, EldoriaGenerators.expeditionMaterials(
                        run.dungeonId, room.depth, EldoriaExpeditions.KIND_TREASURE, run.seed + room.id
                    ), run)
                    note = "🧰 Abres el arcón con cuidado: ${loot.size} piezas al zurrón."
                }

                "loot_force" -> {
                    val loot = EldoriaGenerators.expeditionLoot(
                        run.dungeonId, room.depth + 1, EldoriaExpeditions.KIND_TREASURE, level, run.seed + room.id * 3L
                    )
                    run = run.copy(
                        runLoot = run.runLoot + applyLootBoons(loot, run).map { it.copy(rarity = upgradeRarity(it.rarity, 1)) },
                        torch = (run.torch - 10).coerceAtLeast(1)
                    )
                    note = "🔨 Revientas la cerradura: mejor botín, 10 de antorcha menos."
                }

                "loot_skip" -> note = "🚶 Dejas el arcón intacto. Alguien lo agradecerá."

                // ── HOGUERA ──
                "rest_heal" -> {
                    if (run.seals.contains(SEAL_BLOOD)) {
                        note = "🩸 El Sello de la Sangre impide toda cura: sólo consigues calentarte."
                    } else {
                        draft.hp = (draft.hp + maxHp * 35 / 100).coerceAtMost(maxHp)
                        draft.mp = (draft.mp + maxMp * 35 / 100).coerceAtMost(maxMp)
                        run = run.copy(persistentHp = draft.hp, persistentMp = draft.mp)
                        note = "🔥 Vendas tus heridas junto a las brasas: +35 % de vida y maná."
                    }
                }

                "rest_torch" -> {
                    run = run.copy(torch = (run.torch + 30).coerceAtMost(MAX_TORCH))
                    note = "🕯️ Reavivas la antorcha: ${run.torch} puntos de luz."
                }

                "rest_pet" -> {
                    val idx = draft.roster.indexOfFirst { it.id == draft.activePetId }
                    if (idx >= 0) {
                        val pet = draft.roster[idx]
                        draft.roster[idx] = pet.copy(
                            satiety = (pet.satiety + 20).coerceAtMost(100),
                            bond = (pet.bond + 5).coerceAtMost(100)
                        )
                        note = "🐾 Atiendes a ${pet.name}: +20 de saciedad y +5 de vínculo."
                    } else {
                        note = "🐾 No llevas ninguna bestia a la que atender."
                    }
                }

                // ── MERCADER ──
                "shop_torch" -> {
                    val price = 400 + level * 25
                    if (draft.gold < price) {
                        note = "💰 El buhonero no fía: te faltan ${price - draft.gold} de oro."
                    } else {
                        draft.gold -= price
                        draft.torch = (draft.torch + 3).coerceAtMost(99)
                        note = "🕯️ Compras 3 antorchas por $price de oro."
                    }
                }

                "shop_key" -> {
                    val price = 1_200 + level * 60
                    if (draft.gold < price) {
                        note = "💰 La llave sellada cuesta $price de oro y no los llevas encima."
                    } else {
                        draft.gold -= price
                        run = run.copy(keys = run.keys + 1)
                        note = "🗝️ Guardas una llave sellada en el cinturón."
                    }
                }

                "shop_potion" -> {
                    val price = 300 + level * 20
                    if (draft.gold < price) {
                        note = "💰 Ni con dos pociones fiadas: te faltan ${price - draft.gold} de oro."
                    } else {
                        draft.gold -= price
                        draft.inventory.addAll(newPotions(2))
                        note = "🧪 Dos pociones de vida más en la mochila."
                    }
                }

                "shop_leave" -> note = "👋 Dejas al buhonero con la palabra en la boca."

                // ── PUERTA SELLADA ──
                "gate_key" -> {
                    val hasRunKey = run.keys > 0
                    val hasMatKey = (draft.materialBag[MAT_KEY] ?: 0) > 0
                    if (!hasRunKey && !hasMatKey) {
                        note = "🗝️ No llevas ninguna llave sellada: la puerta sigue cerrada."
                    } else {
                        if (hasRunKey) run = run.copy(keys = run.keys - 1)
                        else draft.materialBag[MAT_KEY] = (draft.materialBag[MAT_KEY] ?: 0) - 1
                        val loot = EldoriaGenerators.expeditionLoot(
                            run.dungeonId, room.depth, EldoriaExpeditions.KIND_GATE, level, run.seed + room.id * 5L
                        )
                        run = run.copy(runLoot = run.runLoot + applyLootBoons(loot, run))
                        grantMaterialsInto(draft, EldoriaGenerators.expeditionMaterials(
                            run.dungeonId, room.depth, EldoriaExpeditions.KIND_GATE, run.seed + room.id * 5L
                        ), run)
                        note = "🗝️ La llave gira sin ruido y la puerta negra cede."
                    }
                }

                "gate_force" -> {
                    draft.hp = (draft.hp - maxHp * 15 / 100).coerceAtLeast(1)
                    val loot = EldoriaGenerators.expeditionLoot(
                        run.dungeonId, room.depth, EldoriaExpeditions.KIND_GATE, level, run.seed + room.id * 11L
                    )
                    run = run.copy(
                        runLoot = run.runLoot + applyLootBoons(loot, run),
                        torch = (run.torch - 12).coerceAtLeast(1),
                        persistentHp = draft.hp
                    )
                    note = "💪 Revientas la puerta a hombros: duele, pero abre."
                }

                "gate_back" -> note = "🚶 Retrocedes y dejas la puerta sellada donde estaba."

                // ── EVENTOS ──
                "event_loot_body" -> {
                    val loot = EldoriaGenerators.expeditionLoot(
                        run.dungeonId, room.depth, EldoriaExpeditions.KIND_EVENT, level, run.seed + room.id * 13L
                    )
                    draft.gold += 120 * level + rnd.nextInt(200)
                    run = run.copy(runLoot = run.runLoot + applyLootBoons(loot, run))
                    note = "🎒 Registras la mochila del muerto y te llevas lo que ya no necesita."
                }

                "event_bury_body" -> {
                    draft.gold += 320 * level + rnd.nextInt(400)
                    run = run.copy(shards = run.shards + 12)
                    note = "⚰️ Lo entierras como es debido. Alguien, en alguna parte, te lo paga."
                }

                "event_leave_body" -> {
                    run = run.copy(torch = (run.torch + 5).coerceAtMost(MAX_TORCH))
                    note = "🚶 Sigues tu camino sin tocar nada. La antorcha te lo agradece."
                }

                "event_answer_voice" -> {
                    val pool = EldoriaGenerators.rollBoonChoices(run, run.seed + room.id, 1)
                    val boon = pool.firstOrNull()
                    draft.hp = (draft.hp - maxHp * 15 / 100).coerceAtLeast(1)
                    if (boon != null) {
                        run = run.copy(boons = run.boons + boon, persistentHp = draft.hp)
                        note = "🗣️ Respondes, y algo te bendice: ${EldoriaExpeditions.boon(boon)?.name ?: "una bendición"}."
                    } else {
                        run = run.copy(persistentHp = draft.hp)
                        note = "🗣️ Respondes y la grieta se limita a reírse de ti."
                    }
                }

                "event_seal_crack" -> {
                    run = run.copy(shards = run.shards + 25 + room.depth * 8)
                    note = "🧱 Tapias la grieta y el silencio te paga en fragmentos de ánima."
                }

                "event_ignore_voice" -> note = "🙉 Finges no oírla. Ella finge no notarlo."

                "event_touch_mirror" -> {
                    val pool = EldoriaGenerators.rollBoonChoices(run, run.seed + room.id * 2L, 1)
                    val boon = pool.firstOrNull()
                    val trimmed = if (run.runLoot.isNotEmpty()) run.runLoot.dropLast(1) else run.runLoot
                    run = run.copy(
                        runLoot = trimmed,
                        boons = if (boon != null) run.boons + boon else run.boons
                    )
                    note = if (boon != null) {
                        "🪞 El reflejo se queda una pieza de tu botín y te deja ${EldoriaExpeditions.boon(boon)?.name}."
                    } else {
                        "🪞 El reflejo se queda con algo tuyo y no devuelve nada."
                    }
                }

                "event_break_mirror" -> {
                    grantMaterialsInto(draft, EldoriaMaterials.rollDrops(
                        tierForDungeon(run.dungeonId), 4 + room.depth, run.seed + room.id * 17L
                    ), run)
                    run = run.copy(torch = (run.torch - 8).coerceAtLeast(1))
                    note = "🔨 Rompes el cristal: esquirlas útiles y siete años de mala suerte."
                }

                "event_walk_away" -> note = "🚶 Te alejas del espejo sin darle la espalda."

                "event_accept_pact" -> {
                    val boon = "boon_sangre_hirviente"
                    run = if (run.boons.contains(boon)) {
                        run.copy(shards = run.shards + 20)
                    } else {
                        run.copy(boons = run.boons + boon)
                    }
                    note = "🩸 Aceptas el pacto. La sangre te hierve y la guardia se te afloja."
                }

                "event_haggle_pact" -> {
                    draft.gold += 500 * level + rnd.nextInt(900)
                    note = "🪙 Regateas tan bien que acabas cobrando tú."
                }

                "event_refuse_pact" -> note = "✋ Rechazas el trato. La figura asiente como si ya lo supiera."

                "event_follow_warning" -> {
                    run = run.copy(rooms = revealDepth(run.rooms, room.depth + 1))
                    note = "🕯️ Haces caso al aviso y la siguiente profundidad se ilumina."
                }

                "event_defy_warning" -> {
                    val loot = EldoriaGenerators.expeditionLoot(
                        run.dungeonId, room.depth + 1, EldoriaExpeditions.KIND_TREASURE, level, run.seed + room.id * 19L
                    )
                    draft.hp = (draft.hp - maxHp * 10 / 100).coerceAtLeast(1)
                    run = run.copy(runLoot = run.runLoot + applyLootBoons(loot, run), persistentHp = draft.hp)
                    note = "🩹 Vas justo por la derecha. Duele, pero el botín compensa."
                }

                "event_erase_warning" -> {
                    run = run.copy(shards = run.shards + 18 + room.depth * 6)
                    note = "🖊️ Borras la inscripción. El abismo aprecia la discreción."
                }

                // ── TRAMPA ──
                "trap_disarm" -> {
                    if (rnd.nextInt(100) < 62) {
                        grantMaterialsInto(draft, EldoriaMaterials.rollDrops(
                            tierForDungeon(run.dungeonId), 3 + room.depth, run.seed + room.id * 23L
                        ), run)
                        note = "🔧 Desarmas el mecanismo y te llevas las piezas."
                    } else {
                        draft.hp = (draft.hp - maxHp * 12 / 100).coerceAtLeast(1)
                        run = run.copy(persistentHp = draft.hp)
                        note = "⚙️ El resorte salta antes de tiempo: pierdes un 12 % de vida."
                    }
                }

                "trap_rush" -> {
                    draft.hp = (draft.hp - maxHp * 10 / 100).coerceAtLeast(1)
                    run = run.copy(torch = (run.torch + 2).coerceAtMost(MAX_TORCH), persistentHp = draft.hp)
                    note = "🏃 Cruzas a la carrera con un par de cortes de recuerdo."
                }

                "trap_scout" -> {
                    run = run.copy(torch = (run.torch - 8).coerceAtLeast(1))
                    note = "🧭 Buscas un rodeo largo: sales ileso y con menos luz."
                }

                // ── VACÍO ──
                "void_listen" -> {
                    draft.hp = (draft.hp - maxHp * 15 / 100).coerceAtLeast(1)
                    run = run.copy(shards = run.shards + 40 + room.depth * 10, persistentHp = draft.hp)
                    note = "🌌 Escuchas al vacío. Te contesta con fragmentos y con frío."
                }

                "void_leave" -> {
                    run = run.copy(torch = (run.torch + 6).coerceAtMost(MAX_TORCH))
                    note = "🚶 Retrocedes despacio, sin darle la espalda a la nada."
                }

                else -> note = "🚶 Sales de la sala sin tocar nada."
            }

            draft.expeditionState = run.copy(log = appendLog(run, note))
            draft.offer = null
            advanceAfterRoom(draft, room)
            if (note.isNotBlank()) showToast(note, "GOLD")
            true
        }
    }

    fun chooseBoon(boonId: String) {
        val state = _expedition.value
        if (!state.active || state.finished) return
        val boon = EldoriaExpeditions.boon(boonId)
        if (boon == null) {
            showToast("🔮 Esa bendición no figura en ningún altar conocido.", "ARCANE")
            return
        }
        val room = state.rooms.firstOrNull { it.id == state.currentRoomId }

        edit { draft ->
            var run = draft.expeditionState
            if (run.boons.contains(boonId)) {
                showToast("🔮 Ya portas ${boon.name}.", "ARCANE")
                run = run.copy(shards = run.shards + 15)
            } else {
                run = run.copy(boons = run.boons + boonId)
            }

            when (boonId) {
                "boon_llama_eterna" -> run = run.copy(torch = (run.torch + 30).coerceAtMost(MAX_TORCH))
                "boon_faro_ancestral" -> run = run.copy(rooms = revealDepth(run.rooms, run.depth + 1))
                "boon_cadena_rota" -> if (run.seals.isNotEmpty()) run = run.copy(seals = run.seals.dropLast(1))
                "boon_ampolla_vida" -> draft.inventory.addAll(newPotions(3))
                "boon_corazon_roble" -> {
                    val maxHp = draft.base.maxHp.coerceAtLeast(1)
                    draft.hp = (draft.hp + maxHp * 15 / 100).coerceAtMost(maxHp)
                    run = run.copy(persistentHp = draft.hp)
                }
                else -> Unit
            }

            draft.expeditionState = run.copy(log = appendLog(run, "🔮 Aceptas ${boon.name}."))
            draft.offer = null
            if (room != null) advanceAfterRoom(draft, room)
            draft.after += { host.hostPlaySound("magic") }
            showToast("🔮 ${boon.name}: ${boon.description}", boon.tone)
            true
        }
    }

    fun dismissOffer() {
        val state = _expedition.value
        if (!state.active) {
            _expeditionOffer.value = null
            return
        }
        if (_expeditionOffer.value == null) return
        val room = state.rooms.firstOrNull { it.id == state.currentRoomId }
        if (room == null) {
            _expeditionOffer.value = null
            return
        }
        edit { draft ->
            draft.offer = null
            draft.expeditionState = draft.expeditionState.copy(
                awaitingChoice = false,
                log = appendLog(draft.expeditionState, "🚶 Sales de ${room.label} sin tocar nada.")
            )
            advanceAfterRoom(draft, room)
            true
        }
    }

    fun returnToExpeditionMap() {
        if (!_expedition.value.active) {
            host.hostNavigate(GameScreen.DUNGEON)
            return
        }
        host.hostNavigate(GameScreen.EXPEDITION)
    }

    fun onExpeditionCombatResolved(victory: Boolean) {
        val state = _expedition.value
        if (!state.active || state.finished) return
        val room = state.rooms.firstOrNull { it.id == state.currentRoomId } ?: return

        edit { draft ->
            if (!victory) {
                salvageRun(
                    draft,
                    "💀 Caes en ${room.label}. El abismo se queda con la mitad de tu botín.",
                    navigateHome = false
                )
                return@edit true
            }

            var run = draft.expeditionState
            val level = draft.base.charLevel.coerceAtLeast(1)
            val loot = applyLootBoons(
                EldoriaGenerators.expeditionLoot(run.dungeonId, room.depth, room.kind, level, run.seed + room.id * 29L),
                run
            )
            val shards = shardsFor(room.kind, room.depth, run)
            run = run.copy(
                runLoot = run.runLoot + loot,
                shards = run.shards + shards,
                persistentHp = draft.hp,
                persistentMp = draft.mp
            )
            draft.expeditionState = run
            grantMaterialsInto(draft, EldoriaGenerators.expeditionMaterials(
                run.dungeonId, room.depth, room.kind, run.seed + room.id * 29L
            ), run)

            advanceAfterRoom(draft, room)
            showToast("💎 Sala despejada: +$shards fragmentos de ánima.", "ARCANE")
            true
        }
    }

    fun claimExpeditionRewards() {
        val state = _expedition.value
        if (!state.active) {
            showToast("📦 No hay ningún botín pendiente de reclamar.", "IRON")
            host.hostNavigate(GameScreen.DUNGEON)
            return
        }

        edit { draft ->
            val run = draft.expeditionState
            val reward = EldoriaExpeditions.rewardMultiplierOf(run.seals)
            val goldGain = ((180 + run.roomsCleared * 90 + run.elitesCleared * 240) *
                draft.base.charLevel.coerceAtLeast(1) / 4 * reward).toInt().coerceAtLeast(0)

            draft.inventory.addAll(run.runLoot)
            draft.gold += goldGain

            val bundle = LinkedHashMap<String, Int>()
            if (run.shards > 0) bundle[MAT_ANIMA] = run.shards
            if (run.keys > 0) bundle[MAT_KEY] = run.keys
            if (run.victory) {
                EldoriaMaterials.rollDrops(
                    tierForDungeon(run.dungeonId), 6 + run.depth * 2, run.seed + 977L
                ).forEach { (id, qty) -> bundle[id] = (bundle[id] ?: 0) + qty }
            }
            grantMaterialsInto(draft, bundle, run)

            if (run.torch in 1..19 && run.victory) {
                draft.contractList = contractsAdvanced(draft.contractList, KIND_EXPEDITION, "TORCH_LOW", 1).toMutableList()
            }

            draft.expeditionState = ExpeditionState()
            draft.offer = null
            draft.navigate = GameScreen.DUNGEON
            draft.after += { host.hostPlaySound("victory") }
            showToast(
                "📦 Reclamas ${run.runLoot.size} objetos, ${run.shards} fragmentos y $goldGain de oro.",
                "GOLD"
            )
            true
        }
    }

    fun consumeTorch(amount: Int) {
        val qty = amount.coerceAtLeast(0)
        if (qty == 0) return
        edit { draft ->
            val run = draft.expeditionState
            if (run.active) {
                val left = run.torch - qty
                draft.expeditionState = run.copy(
                    torch = left.coerceAtLeast(0),
                    log = appendLog(run, "🕯️ La antorcha pierde $qty puntos de luz.")
                )
                if (left <= 0) {
                    salvageRun(draft, "🕯️ La antorcha se apaga y la oscuridad te devuelve a la superficie.", navigateHome = true)
                }
            } else {
                if (draft.torch <= 0) {
                    showToast("🕯️ No te queda ninguna antorcha.", "EMBER")
                    return@edit false
                }
                draft.torch = (draft.torch - qty).coerceAtLeast(0)
            }
            true
        }
    }

    fun refillTorch(amount: Int) {
        val qty = amount.coerceAtLeast(0)
        if (qty == 0) return
        edit { draft ->
            val run = draft.expeditionState
            if (run.active) {
                val value = (run.torch + qty).coerceAtMost(MAX_TORCH)
                draft.expeditionState = run.copy(
                    torch = value,
                    log = appendLog(run, "🔥 Reavivas la antorcha hasta $value.")
                )
                showToast("🔥 Antorcha reavivada: $value puntos de luz.", "EMBER")
            } else {
                draft.torch = (draft.torch + qty).coerceAtMost(99)
                showToast("🕯️ Guardas $qty antorchas de repuesto.", "EMBER")
            }
            true
        }
    }

    /** Precio de una antorcha de repuesto (la UI lo necesita para etiquetar el botón). */
    fun torchPrice(): Int = TORCH_PRICE

    fun buyTorches(quantity: Int) {
        val qty = quantity.coerceIn(1, 20)
        edit { draft ->
            val total = TORCH_PRICE * qty
            if (draft.gold < total) {
                showToast("💰 Te faltan ${total - draft.gold} de oro para $qty antorchas.", "IRON")
                return@edit false
            }
            if (draft.torch >= 99) {
                showToast("🕯️ No te caben más antorchas en el zurrón.", "IRON")
                return@edit false
            }
            draft.gold -= total
            draft.torch = (draft.torch + qty).coerceAtMost(99)
            draft.after += { host.hostPlaySound("click") }
            showToast("🕯️ Compras $qty antorchas por $total de oro.", "EMBER")
            true
        }
    }

    fun activeBoons(): List<ExpeditionBoon> =
        _expedition.value.boons.mapNotNull { EldoriaExpeditions.boon(it) }

    fun activeSeals(): List<ExpeditionSeal> =
        _expedition.value.seals.mapNotNull { EldoriaExpeditions.seal(it) }

    // ── Ayudantes privados de expedición ───────────────────────────────────

    private fun entryRoomIds(rooms: List<ExpeditionRoom>): List<Int> {
        val targets = rooms.flatMap { it.next }.toSet()
        val entries = rooms.filter { it.id !in targets }.map { it.id }
        return entries.ifEmpty { rooms.take(2).map { it.id } }
    }

    private fun torchCostOf(kind: String, seals: List<String>, dungeonId: Int): Int {
        val base = EldoriaGenerators.torchCostFor(kind, seals)
        if (base <= 0) return 0
        return if (dungeonId == ABYSS_TIDES) base * 2 else base
    }

    private fun appendLog(state: ExpeditionState, line: String): List<String> {
        if (line.isBlank()) return state.log
        return (state.log + line).takeLast(14)
    }

    private fun revealDepth(rooms: List<ExpeditionRoom>, depth: Int): List<ExpeditionRoom> =
        rooms.map { if (it.depth == depth) it.copy(revealed = true) else it }

    private fun tierForDungeon(dungeonId: Int): Int = when {
        dungeonId >= 101 -> 6
        dungeonId >= 13 -> 6
        dungeonId >= 11 -> 5
        dungeonId >= 8 -> 4
        dungeonId >= 6 -> 3
        dungeonId >= 3 -> 2
        else -> 1
    }

    private fun shardsFor(kind: String, depth: Int, state: ExpeditionState): Int {
        val base = when (kind.uppercase()) {
            EldoriaExpeditions.KIND_BOSS -> 45
            EldoriaExpeditions.KIND_ELITE -> 16
            EldoriaExpeditions.KIND_TREASURE -> 12
            EldoriaExpeditions.KIND_COMBAT -> 7
            else -> 4
        }
        var value = base + depth * 5
        if (state.boons.contains("boon_voz_abismo")) value = value * 3 / 2
        if (state.dungeonId == ABYSS_CLOCK) value = value * 3 / 2
        value = (value * EldoriaExpeditions.rewardMultiplierOf(state.seals)).toInt()
        return value.coerceAtLeast(1)
    }

    private fun applyLootBoons(loot: List<Item>, state: ExpeditionState): List<Item> {
        var steps = 0
        if (state.boons.contains("boon_semilla_voraz")) steps++
        if (state.seals.contains("seal_ayuno")) steps++
        if (state.dungeonId == ABYSS_CROWN) steps++
        if (steps <= 0) return loot
        return loot.map { it.copy(rarity = upgradeRarity(it.rarity, steps)) }
    }

    private fun upgradeRarity(rarity: String, steps: Int): String {
        val ladder = listOf("COMÚN", "RARO", "ÉPICO", "LEGENDARIO", "ARCANO", "UNIVERSAL")
        val normalized = when (rarity.uppercase()) {
            "COMUN", "COMÚN", "COMMON" -> "COMÚN"
            "RARO", "RARE" -> "RARO"
            "EPICO", "ÉPICO", "EPIC" -> "ÉPICO"
            "LEGENDARIO", "LEGENDARY" -> "LEGENDARIO"
            "ARCANO", "ARCANE" -> "ARCANO"
            "UNIVERSAL" -> "UNIVERSAL"
            else -> "COMÚN"
        }
        val index = ladder.indexOf(normalized).coerceAtLeast(0)
        return ladder[(index + steps).coerceIn(0, ladder.size - 1)]
    }

    private fun newPotions(quantity: Int): List<Item> {
        val stamp = System.currentTimeMillis()
        return (0 until quantity.coerceAtLeast(1)).map { i ->
            Item(
                id = "potion_exp_${stamp}_$i",
                name = "Poción Rejuvenecedora",
                type = "POTION",
                rarity = "COMÚN",
                description = "Restaura instantáneamente el 50% de HP y Maná en combate.",
                itemLevel = 1,
                imageResName = POTION_ART
            )
        }
    }

    private fun offerForRoom(room: ExpeditionRoom, state: ExpeditionState, playerLevel: Int): ExpeditionOffer? =
        when (room.kind.uppercase()) {
            EldoriaExpeditions.KIND_TRAP -> ExpeditionOffer(
                kind = "EVENT",
                title = "Mecanismo Oculto",
                description = "Las losas están demasiado limpias y el aire huele a aceite viejo. Alguien preparó esto con tiempo.",
                optionIds = listOf("trap_disarm", "trap_rush", "trap_scout"),
                optionTitles = listOf("Desarmar el mecanismo", "Cruzar a la carrera", "Buscar un rodeo"),
                optionSubtitles = listOf(
                    "Materiales del mecanismo si aciertas",
                    "Pierdes un 10 % de vida, ganas 2 de antorcha",
                    "Sales ileso, gastas 8 de antorcha"
                ),
                optionTones = listOf("IRON", "BLOOD", "SILVER")
            )

            EldoriaExpeditions.KIND_VOID -> ExpeditionOffer(
                kind = "EVENT",
                title = "Vacío Silente",
                description = "Donde debería haber piedra sólo hay una ausencia que te devuelve la mirada.",
                optionIds = listOf("void_listen", "void_leave"),
                optionTitles = listOf("Escuchar al vacío", "Retroceder despacio"),
                optionSubtitles = listOf(
                    "Muchos fragmentos a cambio de un 15 % de vida",
                    "Recuperas 6 puntos de antorcha"
                ),
                optionTones = listOf("ARCANE", "IRON")
            )

            else -> EldoriaGenerators.offerForRoom(room, state, playerLevel, state.seed)
        }

    /** Marca la sala como limpia, abre las siguientes y aplica los efectos de paso. */
    private fun advanceAfterRoom(draft: Draft, room: ExpeditionRoom) {
        var run = draft.expeditionState
        val cleared = run.rooms.map { candidate ->
            when {
                candidate.id == room.id -> candidate.copy(cleared = true, revealed = true, locked = false)
                candidate.id in room.next -> candidate.copy(revealed = true)
                else -> candidate
            }
        }
        val nextIds = room.next.filter { id -> cleared.firstOrNull { it.id == id }?.cleared == false }
        val isBoss = room.kind.uppercase() == EldoriaExpeditions.KIND_BOSS
        val isElite = room.kind.uppercase() == EldoriaExpeditions.KIND_ELITE

        run = run.copy(
            rooms = cleared,
            availableRoomIds = if (isBoss) emptyList() else nextIds,
            roomsCleared = run.roomsCleared + 1,
            elitesCleared = run.elitesCleared + if (isElite) 1 else 0,
            awaitingChoice = false,
            depth = room.depth
        )

        // Bendición del Vado: cada sala limpia devuelve un 8 % de vida (salvo Sello de la Sangre).
        if (run.boons.contains("boon_bendicion_vado") && !run.seals.contains(SEAL_BLOOD)) {
            val maxHp = draft.base.maxHp.coerceAtLeast(1)
            draft.hp = (draft.hp + maxHp * 8 / 100).coerceAtMost(maxHp)
            run = run.copy(persistentHp = draft.hp)
        }

        // Sello del Hambre: la bestia activa pierde 3 de saciedad por sala superada.
        if (run.seals.contains(SEAL_HUNGER)) {
            val idx = draft.roster.indexOfFirst { it.id == draft.activePetId }
            if (idx >= 0) {
                val pet = draft.roster[idx]
                draft.roster[idx] = pet.copy(satiety = (pet.satiety - 3).coerceAtLeast(0))
            }
        }

        val reachedDepth = room.depth + 1
        draft.stats = draft.stats.copy(deepestDepth = maxOf(draft.stats.deepestDepth, reachedDepth))

        draft.contractList = contractsAdvanced(draft.contractList, KIND_EXPEDITION, "ROOMS", 1).toMutableList()
        if (reachedDepth >= 2) {
            draft.contractList = contractsAdvanced(
                draft.contractList, KIND_EXPEDITION, "DEPTH:$reachedDepth", 1
            ).toMutableList()
        }

        if (isBoss) {
            run = run.copy(
                finished = true,
                victory = true,
                log = appendLog(run, "👑 Derrotas al señor del abismo. La expedición es tuya.")
            )
            draft.stats = draft.stats.copy(dungeonsCleared = draft.stats.dungeonsCleared + 1)
            draft.contractList = contractsAdvanced(draft.contractList, KIND_EXPEDITION, "BOSS", 1).toMutableList()
            if (run.dungeonId >= 101) {
                draft.contractList = contractsAdvanced(draft.contractList, KIND_EXPEDITION, "ABYSS", 1).toMutableList()
            }
            showToast("👑 ¡${run.dungeonName} conquistado! Reclama tu botín.", "GOLD")
        } else if (run.availableRoomIds.isEmpty()) {
            run = run.copy(
                finished = true,
                victory = true,
                log = appendLog(run, "🚪 No queda camino por delante: la expedición termina aquí.")
            )
        }

        draft.expeditionState = run
    }

    /** Fin prematuro de la run: se conserva la mitad del botín y de los fragmentos. */
    private fun salvageRun(draft: Draft, message: String, navigateHome: Boolean) {
        val run = draft.expeditionState
        val keptLoot = run.runLoot.take((run.runLoot.size + 1) / 2)
        val keptShards = run.shards / 2
        draft.inventory.addAll(keptLoot)
        val bag = LinkedHashMap<String, Int>()
        if (keptShards > 0) bag[MAT_ANIMA] = keptShards
        if (run.keys > 0) bag[MAT_KEY] = run.keys
        if (bag.isNotEmpty()) grantMaterialsInto(draft, bag, run)

        draft.expeditionState = ExpeditionState()
        draft.offer = null
        if (navigateHome) draft.navigate = GameScreen.DUNGEON
        draft.after += { host.hostPlaySound("defeat") }
        showToast(message, "BLOOD")
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  MASCOTAS
    // ═══════════════════════════════════════════════════════════════════════

    fun allSpecies(): List<PetSpecies> = EldoriaPets.SPECIES

    fun speciesOf(petId: String): PetSpecies? {
        val record = _petRoster.value.firstOrNull { it.id == petId }
        if (record != null) return EldoriaPets.species(record.speciesId)
        return EldoriaPets.species(petId)
    }

    fun adoptPet(speciesId: String, level: Int = 1): PetRecord? {
        val species = EldoriaPets.species(speciesId) ?: return null
        if (host.currentProgress() == null) return null

        val stamp = System.currentTimeMillis()
        val record = PetRecord(
            id = EldoriaGenerators.petIdFor(species.id, stamp + _petRoster.value.size),
            speciesId = species.id,
            name = EldoriaPets.stageName(species.id, 1).ifBlank { species.name },
            rarity = species.rarity,
            level = level.coerceIn(1, EldoriaPets.levelCapForStage(1)),
            exp = 0,
            satiety = 100,
            bond = 0,
            disciplineAtk = 0,
            disciplineDef = 0,
            disciplineVit = 0,
            stage = 1,
            traits = listOf(species.signatureTrait),
            imageResName = species.imageResName,
            sigilSeed = species.sigilSeed,
            paletteKey = species.paletteKey,
            injuries = 0,
            bornAt = stamp
        )

        var adopted = false
        edit { draft ->
            if (draft.roster.size >= MAX_ROSTER) {
                showToast("🏚️ Tu establo está lleno ($MAX_ROSTER bestias). Libera alguna antes.", "IRON")
                return@edit false
            }
            if (draft.roster.any { it.id == record.id }) return@edit false
            draft.roster.add(record)
            if (draft.activePetId.isBlank() || draft.roster.none { it.id == draft.activePetId }) {
                draft.activePetId = record.id
            }
            draft.contractList = contractsAdvanced(draft.contractList, KIND_TAMING, "ADOPTAR", 1).toMutableList()
            draft.contractList = contractsSet(draft.contractList, KIND_TAMING, "ROSTER", draft.roster.size).toMutableList()
            draft.after += { host.hostPlaySound("magic") }
            adopted = true
            true
        }
        if (!adopted) return null
        showToast("🐣 ${record.name} (${record.rarity}) se une a tu santuario.", "VITAE")
        return record
    }

    fun grantRandomPet(level: Int, rarityFloor: String = "RARO"): PetRecord? {
        val seed = System.currentTimeMillis() + level.toLong() * 131L + _petRoster.value.size * 7L
        val species = EldoriaPets.randomSpecies(rarityFloor, seed)
        return adoptPet(species.id, level)
    }

    /** Precio de la siguiente adopción del santuario: sube con el tamaño del establo. */
    fun adoptionCost(): Int = ADOPTION_BASE_PRICE + ADOPTION_STEP_PRICE * _petRoster.value.size

    /** Tope de bestias del establo, para que la UI pueda avisar antes de cobrar. */
    fun rosterCapacity(): Int = MAX_ROSTER

    /**
     * Adopción pagada en el Santuario. Era la pieza que faltaba: sin ella el establo,
     * la fusión del altar y los contratos de DOMA «ADOPTAR»/«ROSTER» eran inalcanzables.
     */
    fun buyRandomPet(): PetRecord? {
        val progress = host.currentProgress() ?: return null
        if (_petRoster.value.size >= MAX_ROSTER) {
            showToast("🏚️ Tu establo está lleno ($MAX_ROSTER bestias). Libera alguna antes.", "IRON")
            return null
        }
        val cost = adoptionCost()
        if (progress.charGold < cost) {
            showToast("💰 La adopción cuesta $cost de oro: te faltan ${cost - progress.charGold}.", "IRON")
            return null
        }
        // El oro se cobra sólo si la bestia llega a entrar en el establo.
        val record = grantRandomPet(
            level = progress.charLevel.coerceAtLeast(1),
            rarityFloor = "RARO"
        ) ?: return null
        edit { draft ->
            draft.gold = (draft.gold - cost).coerceAtLeast(0)
            true
        }
        return record
    }

    fun setActivePet(petId: String) {
        if (_petRoster.value.none { it.id == petId }) {
            showToast("🐾 Esa bestia no está en tu establo.", "IRON")
            return
        }
        edit { draft ->
            if (draft.activePetId == petId) return@edit false
            draft.activePetId = petId
            val name = draft.roster.firstOrNull { it.id == petId }?.name ?: "Tu bestia"
            draft.after += { host.hostPlaySound("click") }
            showToast("🐾 $name te acompañará a partir de ahora.", "VITAE")
            true
        }
    }

    fun releasePet(petId: String) {
        edit { draft ->
            val record = draft.roster.firstOrNull { it.id == petId }
            if (record == null) {
                showToast("🐾 Esa bestia ya no está en tu establo.", "IRON")
                return@edit false
            }
            // Sin bestias no hay santuario, ni órdenes en combate, ni contratos de
            // DOMA: la última nunca se libera.
            if (draft.roster.size <= 1) {
                showToast("🐾 No puedes liberar a tu única bestia.", "IRON")
                return@edit false
            }
            draft.roster.removeAll { it.id == petId }
            if (draft.activePetId == petId) draft.activePetId = draft.roster.firstOrNull()?.id ?: ""
            val refund = 10 + record.level * 2 + record.stage * 15
            grantMaterialsInto(draft, mapOf(MAT_ANIMA to refund), draft.expeditionState)
            draft.contractList = contractsSet(draft.contractList, KIND_TAMING, "ROSTER", draft.roster.size).toMutableList()
            showToast("🕊️ Liberas a ${record.name}. El vínculo roto deja $refund fragmentos de ánima.", "SILVER")
            true
        }
    }

    fun renamePet(petId: String, newName: String) {
        val clean = newName.trim().take(24)
        if (clean.isBlank()) {
            showToast("✍️ El nombre no puede quedar vacío.", "IRON")
            return
        }
        edit { draft ->
            val idx = draft.roster.indexOfFirst { it.id == petId }
            if (idx < 0) return@edit false
            val old = draft.roster[idx].name
            draft.roster[idx] = draft.roster[idx].copy(name = clean)
            showToast("✍️ $old pasa a llamarse $clean.", "GOLD")
            true
        }
    }

    fun feedPetRecord(petId: String, foodItemId: String) {
        edit { draft ->
            val idx = draft.roster.indexOfFirst { it.id == petId }
            if (idx < 0) {
                showToast("🐾 No encuentro esa bestia en el establo.", "IRON")
                return@edit false
            }
            val foodIdx = draft.inventory.indexOfFirst { it.id == foodItemId && it.type.uppercase() == "PET_FOOD" }
            if (foodIdx < 0) {
                showToast("🍖 No te queda ese alimento en la mochila.", "IRON")
                return@edit false
            }
            val food = draft.inventory[foodIdx]
            val record = draft.roster[idx]
            if (record.satiety >= 100) {
                showToast("🍖 ${record.name} está saciada: no probará bocado.", "VITAE")
                return@edit false
            }

            val species = EldoriaPets.species(record.speciesId)
            val foodKey = foodKeyOf(food)
            val favorite = species != null && species.favoriteFood.equals(foodKey, ignoreCase = true)
            val satietyGain = food.conBonus.coerceAtLeast(15)
            val expGain = food.strBonus.coerceAtLeast(50)
            val bondGain = if (favorite) 8 else 4

            var fed = record.copy(
                satiety = (record.satiety + satietyGain).coerceAtMost(100),
                bond = (record.bond + bondGain).coerceAtMost(100),
                injuries = (record.injuries - 1).coerceAtLeast(0)
            )
            fed = grantPetExp(fed, expGain)
            draft.roster[idx] = fed
            draft.inventory.removeAt(foodIdx)

            if (favorite) {
                draft.contractList = contractsAdvanced(
                    draft.contractList, KIND_TAMING, "ALIMENTAR_FAVORITA", 1
                ).toMutableList()
            }
            draft.after += { host.hostPlaySound("heal") }
            showToast(
                if (favorite) "🍖 ¡Su favorita! ${fed.name} gana +$satietyGain de saciedad y +$bondGain de vínculo."
                else "🍖 ${fed.name} gana +$satietyGain de saciedad y +$bondGain de vínculo.",
                "VITAE"
            )
            true
        }
    }

    fun trainPetDiscipline(petId: String, discipline: String, quality: Int) {
        edit { draft -> applyTraining(draft, petId, discipline, quality, chargeGold = true) }
    }

    fun trainingCost(petId: String, discipline: String): Int {
        val record = _petRoster.value.firstOrNull { it.id == petId } ?: _activePet.value ?: return 200
        return trainingCostFor(record, normalizeDiscipline(discipline))
    }

    fun canTrain(petId: String, discipline: String): Boolean {
        val progress = host.currentProgress() ?: return false
        val record = _petRoster.value.firstOrNull { it.id == petId } ?: return false
        val disc = normalizeDiscipline(discipline)
        val cap = EldoriaPets.disciplineCap(record.level)
        if (disciplineValue(record, disc) >= cap) return false
        if (record.satiety < 10) return false
        return progress.charGold >= trainingCostFor(record, disc)
    }

    fun startPetTrainingMinigame(petId: String, discipline: String) {
        val record = _petRoster.value.firstOrNull { it.id == petId } ?: _activePet.value
        if (record == null) {
            showToast("🐾 Necesitas una bestia antes de adiestrar a nadie.", "IRON")
            return
        }
        val disc = normalizeDiscipline(discipline)
        if (!canTrain(record.id, disc)) {
            showToast("🚫 ${record.name} no puede entrenar ahora: revisa oro, saciedad y tope de disciplina.", "IRON")
            return
        }
        openMinigame(
            MinigameRequest(
                id = "ADIESTRAMIENTO",
                difficulty = (record.stage + record.level / 25).coerceIn(1, 5),
                title = "Adiestramiento · ${record.name}",
                contextJson = "${record.id}|$disc",
                rewardScale = 1f,
                originScreen = "PET_SANCTUARY"
            )
        )
    }

    fun canEvolve(petId: String): Boolean {
        val progress = host.currentProgress() ?: return false
        val record = _petRoster.value.firstOrNull { it.id == petId } ?: return false
        if (record.stage >= 3) return false
        if (record.bond < EldoriaPets.bondForStage(record.stage + 1)) return false
        val cost = EldoriaPets.evolutionCost(record.stage)
        if (progress.charGold < (cost["gold"] ?: 0)) return false
        return cost.filterKeys { it != "gold" }.all { (id, qty) -> (_materials.value[id] ?: 0) >= qty }
    }

    fun evolutionRequirements(petId: String): Map<String, Int> {
        val record = _petRoster.value.firstOrNull { it.id == petId } ?: return emptyMap()
        if (record.stage >= 3) return emptyMap()
        return EldoriaPets.evolutionCost(record.stage)
    }

    fun evolvePet(petId: String) {
        edit { draft ->
            val idx = draft.roster.indexOfFirst { it.id == petId }
            if (idx < 0) return@edit false
            val record = draft.roster[idx]
            if (record.stage >= 3) {
                showToast("🌟 ${record.name} ya alcanzó su forma final.", "GOLD")
                return@edit false
            }
            val needBond = EldoriaPets.bondForStage(record.stage + 1)
            if (record.bond < needBond) {
                showToast("💗 ${record.name} necesita $needBond de vínculo (tiene ${record.bond}).", "VITAE")
                return@edit false
            }
            val cost = EldoriaPets.evolutionCost(record.stage)
            val goldCost = cost["gold"] ?: 0
            if (draft.gold < goldCost) {
                showToast("💰 La evolución cuesta $goldCost de oro.", "IRON")
                return@edit false
            }
            val matCost = cost.filterKeys { it != "gold" }
            val missing = matCost.filterNot { (id, qty) -> (draft.materialBag[id] ?: 0) >= qty }
            if (missing.isNotEmpty()) {
                val label = missing.entries.joinToString(", ") {
                    "${EldoriaMaterials.name(it.key)} ×${it.value - (draft.materialBag[it.key] ?: 0)}"
                }
                showToast("🧰 Te faltan materiales: $label.", "IRON")
                return@edit false
            }

            draft.gold -= goldCost
            matCost.forEach { (id, qty) -> draft.materialBag[id] = (draft.materialBag[id] ?: 0) - qty }
            val newStage = record.stage + 1
            val newName = EldoriaPets.stageName(record.speciesId, newStage).ifBlank { record.name }
            draft.roster[idx] = record.copy(
                stage = newStage,
                name = newName,
                satiety = (record.satiety + 20).coerceAtMost(100)
            )
            draft.contractList = contractsAdvanced(draft.contractList, KIND_TAMING, "EVOLUCIONAR", 1).toMutableList()
            draft.after += { host.hostPlaySound("victory") }
            showToast("🌟 ${record.name} evoluciona: ahora es $newName (etapa $newStage).", "GOLD")
            true
        }
    }

    fun fusePets(hostPetId: String, sacrificePetId: String) {
        if (hostPetId == sacrificePetId) {
            showToast("🔥 Una bestia no puede fusionarse consigo misma.", "IRON")
            return
        }
        edit { draft ->
            val hostIdx = draft.roster.indexOfFirst { it.id == hostPetId }
            val sacrifice = draft.roster.firstOrNull { it.id == sacrificePetId }
            if (hostIdx < 0 || sacrifice == null) {
                showToast("🐾 No encuentro a una de las dos bestias.", "IRON")
                return@edit false
            }
            val anchor = draft.roster[hostIdx]
            val cap = EldoriaPets.disciplineCap(anchor.level)
            val donatedTrait = sacrifice.traits.firstOrNull { it !in anchor.traits }
            val fused = anchor.copy(
                traits = if (donatedTrait != null) anchor.traits + donatedTrait else anchor.traits,
                disciplineAtk = (anchor.disciplineAtk + sacrifice.disciplineAtk / 2).coerceAtMost(cap),
                disciplineDef = (anchor.disciplineDef + sacrifice.disciplineDef / 2).coerceAtMost(cap),
                disciplineVit = (anchor.disciplineVit + sacrifice.disciplineVit / 2).coerceAtMost(cap),
                bond = (anchor.bond + 6).coerceAtMost(100)
            )
            draft.roster[hostIdx] = fused
            draft.roster.removeAll { it.id == sacrificePetId }
            if (draft.activePetId == sacrificePetId) draft.activePetId = fused.id
            draft.contractList = contractsSet(draft.contractList, KIND_TAMING, "ROSTER", draft.roster.size).toMutableList()
            draft.after += { host.hostPlaySound("magic") }
            val traitName = donatedTrait?.let { EldoriaPets.trait(it)?.name }
            showToast(
                if (traitName != null) "🔥 ${sacrifice.name} se funde en ${fused.name} y le lega «$traitName»."
                else "🔥 ${sacrifice.name} se funde en ${fused.name} y le lega su disciplina.",
                "EMBER"
            )
            true
        }
    }

    fun petCombatProfile(petId: String? = null): PetCombatProfile {
        val record = (if (petId == null) _activePet.value else _petRoster.value.firstOrNull { it.id == petId })
            ?: return PetCombatProfile()
        val species = EldoriaPets.species(record.speciesId)
        val baseAtk = species?.baseAtk ?: 18
        val baseDef = species?.baseDef ?: 14
        val baseVit = species?.baseVit ?: 16

        val stageMult = when (record.stage) {
            1 -> 1.00f
            2 -> 1.28f
            else -> 1.62f
        }
        val satietyMult = when {
            record.satiety <= 0 -> 0.55f
            record.satiety < 20 -> 0.85f
            record.satiety >= 50 -> 1.12f
            else -> 1.00f
        }
        val injuryMult = (1f - record.injuries * 0.05f).coerceIn(0.55f, 1f)
        val level = record.level.coerceAtLeast(1)

        var attack = (baseAtk + level * 1.35f + record.disciplineAtk * 1.6f) * stageMult * satietyMult * injuryMult
        var guard = (baseDef + level * 1.10f + record.disciplineDef * 1.7f) * stageMult * satietyMult * injuryMult
        var heal = (baseVit + level * 1.20f + record.disciplineVit * 1.5f) * stageMult * satietyMult

        var cooldown = 3
        record.traits.forEach { traitId ->
            val trait = EldoriaPets.trait(traitId) ?: return@forEach
            when (traitId) {
                EldoriaPets.TRAIT_AURA_FEROZ -> attack *= 1f + trait.magnitude / 100f
                EldoriaPets.TRAIT_PIEL_HIERRO -> guard *= 1f + trait.magnitude / 100f
                EldoriaPets.TRAIT_CORAZON_VITAL -> heal *= 1f + trait.magnitude / 100f
                EldoriaPets.TRAIT_COLMILLO_VENENOSO -> attack *= 1.06f
                EldoriaPets.TRAIT_ESCAMA_IGNEA -> guard *= 1.08f
                EldoriaPets.TRAIT_GUARDIAN_LEAL -> guard *= 1.15f
                EldoriaPets.TRAIT_BENDICION_SERENA -> heal *= 1.08f
                EldoriaPets.TRAIT_ZANCADA_VELOZ -> cooldown -= 1
                else -> Unit
            }
        }

        return PetCombatProfile(
            petId = record.id,
            name = record.name,
            stage = record.stage,
            attack = attack.roundToInt().coerceAtLeast(1),
            guard = guard.roundToInt().coerceAtLeast(1),
            heal = heal.roundToInt().coerceAtLeast(1),
            commandCooldown = cooldown.coerceIn(1, 4),
            traits = record.traits,
            paletteKey = record.paletteKey,
            sigilSeed = record.sigilSeed,
            imageResName = record.imageResName
        )
    }

    fun petTraitsOf(petId: String): List<PetTrait> {
        val record = _petRoster.value.firstOrNull { it.id == petId } ?: return emptyList()
        return record.traits.mapNotNull { EldoriaPets.trait(it) }
    }

    // ── Ayudantes privados de mascotas ─────────────────────────────────────

    private fun normalizeDiscipline(raw: String): String = when (raw.trim().uppercase()) {
        "FURIA", "FURY", "ATAQUE", "ATTACK", "ATK" -> DISC_FURY
        "BASTION", "BASTIÓN", "DEFENSA", "DEFENSE", "DEF" -> DISC_BASTION
        "VITALIDAD", "VITALITY", "VIDA", "VIT" -> DISC_VITALITY
        else -> DISC_FURY
    }

    private fun disciplineLabel(discipline: String): String = when (discipline) {
        DISC_BASTION -> "Bastión"
        DISC_VITALITY -> "Vitalidad"
        else -> "Furia"
    }

    private fun disciplineValue(record: PetRecord, discipline: String): Int = when (discipline) {
        DISC_BASTION -> record.disciplineDef
        DISC_VITALITY -> record.disciplineVit
        else -> record.disciplineAtk
    }

    private fun trainingCostFor(record: PetRecord, discipline: String): Int {
        val current = disciplineValue(record, discipline)
        return (120 + current * 40) * record.stage.coerceIn(1, 3)
    }

    private fun grantPetExp(record: PetRecord, amount: Int): PetRecord {
        if (amount <= 0) return record
        val cap = EldoriaPets.levelCapForStage(record.stage)
        var level = record.level.coerceIn(1, cap)
        var exp = record.exp + amount
        var guard = 0
        while (level < cap && guard < 250) {
            val need = EldoriaPets.expForLevel(level + 1)
            if (need <= 0 || exp < need) break
            exp -= need
            level++
            guard++
        }
        if (level >= cap) exp = exp.coerceAtMost(EldoriaPets.expForLevel(cap))
        return record.copy(level = level, exp = exp.coerceAtLeast(0))
    }

    private fun foodKeyOf(food: Item): String {
        val art = food.imageResName.lowercase()
        return when {
            art.contains("bestial") -> "BESTIAL"
            art.contains("mistica") -> "MISTICA"
            art.contains("dragon") -> "DRAGON"
            art.contains("celestial") -> "CELESTIAL"
            food.name.contains("Bestial", true) -> "BESTIAL"
            food.name.contains("Mística", true) -> "MISTICA"
            food.name.contains("Imperial", true) -> "DRAGON"
            else -> "CELESTIAL"
        }
    }

    private fun applyTraining(
        draft: Draft,
        petId: String,
        discipline: String,
        quality: Int,
        chargeGold: Boolean
    ): Boolean {
        val idx = draft.roster.indexOfFirst { it.id == petId }
        if (idx < 0) {
            showToast("🐾 No encuentro esa bestia en el establo.", "IRON")
            return false
        }
        val record = draft.roster[idx]
        val disc = normalizeDiscipline(discipline)
        val cap = EldoriaPets.disciplineCap(record.level)
        val current = disciplineValue(record, disc)
        if (current >= cap) {
            showToast("🏅 ${record.name} ya tiene ${disciplineLabel(disc)} al tope ($cap).", "GOLD")
            return false
        }
        if (record.satiety < 10) {
            showToast("🍖 ${record.name} está famélica: dale de comer antes de entrenar.", "VITAE")
            return false
        }
        val cost = trainingCostFor(record, disc)
        if (chargeGold && draft.gold < cost) {
            showToast("💰 La sesión cuesta $cost de oro.", "IRON")
            return false
        }

        val q = quality.coerceIn(0, 100)
        val gain = (1 + (q * 8) / 100).coerceAtMost(cap - current)
        val bondGain = 1 + q / 40
        var trained = when (disc) {
            DISC_BASTION -> record.copy(disciplineDef = (current + gain).coerceAtMost(cap))
            DISC_VITALITY -> record.copy(disciplineVit = (current + gain).coerceAtMost(cap))
            else -> record.copy(disciplineAtk = (current + gain).coerceAtMost(cap))
        }
        trained = trained.copy(
            satiety = (trained.satiety - 10).coerceAtLeast(0),
            bond = (trained.bond + bondGain).coerceAtMost(100)
        )
        trained = grantPetExp(trained, 40 + q * 3)
        draft.roster[idx] = trained
        if (chargeGold) draft.gold -= cost

        draft.contractList = contractsAdvanced(
            draft.contractList, KIND_TAMING, "ENTRENAR:$disc", 1
        ).toMutableList()
        draft.after += { host.hostPlaySound("click") }
        showToast(
            "🐾 ${trained.name} gana +$gain de ${disciplineLabel(disc)} y +$bondGain de vínculo.",
            "VITAE"
        )
        return true
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  BESTIARIO
    // ═══════════════════════════════════════════════════════════════════════

    fun recordKill(speciesId: String, isBoss: Boolean) {
        if (speciesId.isBlank()) return
        edit { draft ->
            registerKill(draft, speciesId, isBoss)
            true
        }
    }

    fun bestiaryEntries(): List<BestiaryEntry> {
        val book = _bestiary.value
        return EldoriaBestiary.SPECIES.map { species ->
            val kills = book[species.id] ?: 0
            BestiaryEntry(
                species = species,
                kills = kills,
                discovered = kills > 0,
                archetypeName = EldoriaBestiary.archetype(species.archetype).name
            )
        }
    }

    fun discoveredCount(): Int = _bestiary.value.count { it.value > 0 }

    fun totalSpeciesCount(): Int = EldoriaBestiary.SPECIES.size

    fun decorateEnemy(kingdomId: String, level: Int, rarity: String, isBoss: Boolean): EnemyDecoration {
        val seed = System.currentTimeMillis() + level.toLong() * 7_919L + rarity.uppercase().hashCode().toLong()
        val species = EldoriaBestiary.pick(kingdomId, level, rarity, isBoss, seed)
        val archetype = EldoriaBestiary.archetype(species.archetype)
        val run = _expedition.value

        val affixRarity = if (isBoss) "JEFE" else rarity
        val affixes = EldoriaBestiary.rollAffixes(affixRarity, seed).toMutableList()

        if (run.active && run.dungeonId == ABYSS_CLOCK && !affixes.contains("affix_veloz")) {
            affixes.add("affix_veloz")
        }
        if (isBoss && run.active && run.seals.contains(SEAL_JUDGEMENT)) {
            EldoriaBestiary.AFFIXES
                .map { it.id }
                .firstOrNull { it !in affixes && Random(seed + it.hashCode()).nextInt(100) < 55 }
                ?.let { affixes.add(it) }
        }

        var hpMult = archetype.hpMult
        var atkMult = archetype.atkMult
        var defMult = archetype.defMult

        affixes.forEach { affixId ->
            when (affixId) {
                "affix_acorazado" -> defMult *= 1.20f
                "affix_blindado" -> defMult *= 1.35f
                "affix_espinoso" -> defMult *= 1.10f
                "affix_fantasmal" -> defMult *= 1.15f
                "affix_regenerativo" -> hpMult *= 1.15f
                "affix_divisor" -> hpMult *= 1.20f
                "affix_maldito" -> hpMult *= 1.10f
                "affix_vampirico" -> atkMult *= 1.10f
                "affix_ardiente" -> atkMult *= 1.08f
                "affix_gelido" -> atkMult *= 1.05f
                "affix_veloz" -> atkMult *= 1.12f
                "affix_explosivo" -> atkMult *= 1.05f
                "affix_toxico" -> atkMult *= 1.06f
                "affix_aturdidor" -> atkMult *= 1.08f
                "affix_aureo" -> atkMult *= 1.15f
                "affix_ancestral" -> {
                    hpMult *= 1.30f
                    atkMult *= 1.30f
                    defMult *= 1.30f
                }
                else -> Unit
            }
        }

        if (run.active) {
            val danger = EldoriaExpeditions.dangerMultiplierOf(run.seals)
            hpMult *= danger
            atkMult *= 1f + (danger - 1f) / 2f
            if (run.seals.contains(SEAL_GREED)) hpMult *= 1.30f
            if (run.seals.contains(SEAL_IRON)) defMult *= 1.25f
        }
        if (isBoss) hpMult *= 1.15f

        val firstAffix = affixes.firstOrNull()?.let { EldoriaBestiary.affix(it)?.name }
        val displayName = if (firstAffix != null) "${species.name} $firstAffix" else species.name

        return EnemyDecoration(
            speciesId = species.id,
            displayName = displayName,
            archetype = species.archetype,
            affixes = affixes.toList(),
            hpMult = hpMult,
            atkMult = atkMult,
            defMult = defMult,
            signatureMove = species.signatureMove,
            artKey = species.artKey
        )
    }

    private fun registerKill(draft: Draft, speciesId: String, isBoss: Boolean) {
        if (speciesId.isNotBlank()) {
            val previous = draft.killBook[speciesId] ?: 0
            draft.killBook[speciesId] = previous + 1
            if (previous == 0) {
                val name = EldoriaBestiary.species(speciesId)?.name ?: speciesId
                showToast("📖 Nueva entrada del bestiario: $name.", "ARCANE")
            }
        }
        draft.stats = draft.stats.copy(
            totalKills = draft.stats.totalKills + 1,
            bossKills = draft.stats.bossKills + if (isBoss) 1 else 0
        )
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  MATERIALES
    // ═══════════════════════════════════════════════════════════════════════

    fun grantMaterial(materialId: String, qty: Int) {
        if (materialId.isBlank() || qty <= 0) return
        grantMaterials(mapOf(materialId to qty))
    }

    fun grantMaterials(drops: Map<String, Int>) {
        val clean = drops.filterValues { it > 0 }
        if (clean.isEmpty()) return
        edit { draft ->
            grantMaterialsInto(draft, clean, draft.expeditionState)
            val label = clean.entries.take(3).joinToString(" · ") { "${EldoriaMaterials.name(it.key)} ×${it.value}" }
            showToast("🧰 Materiales obtenidos: $label.", "IRON")
            true
        }
    }

    fun spendMaterials(cost: Map<String, Int>): Boolean {
        val clean = cost.filterValues { it > 0 }
        if (clean.isEmpty()) return true
        if (host.currentProgress() == null) return false
        val bag = _materials.value
        if (clean.any { (id, qty) -> (bag[id] ?: 0) < qty }) {
            val missing = clean.filter { (id, qty) -> (bag[id] ?: 0) < qty }
                .entries.joinToString(", ") { "${EldoriaMaterials.name(it.key)} ×${it.value - (bag[it.key] ?: 0)}" }
            showToast("🧰 Te faltan materiales: $missing.", "IRON")
            return false
        }
        edit { draft ->
            clean.forEach { (id, qty) -> draft.materialBag[id] = (draft.materialBag[id] ?: 0) - qty }
            true
        }
        return true
    }

    fun materialCount(materialId: String): Int = _materials.value[materialId] ?: 0

    fun materialDefs(): List<MaterialDef> = EldoriaMaterials.ALL

    fun craftRecipe(recipeId: String, quality: Int) {
        edit { draft -> applyCraft(draft, recipeId, quality) }
    }

    private fun grantMaterialsInto(draft: Draft, drops: Map<String, Int>, state: ExpeditionState) {
        if (drops.isEmpty()) return
        val doubled = state.active && state.seals.contains(SEAL_IRON)
        drops.forEach { (id, qty) ->
            if (id.isBlank() || qty <= 0) return@forEach
            val amount = if (doubled) qty * 2 else qty
            draft.materialBag[id] = (draft.materialBag[id] ?: 0) + amount
            draft.contractList = contractsAdvanced(draft.contractList, KIND_GATHER, id, amount).toMutableList()
        }
    }

    private fun applyCraft(draft: Draft, recipeId: String, quality: Int): Boolean {
        val recipe = com.example.eldoria.systems.CraftingRecipes.ALL_RECIPES.firstOrNull { it.id == recipeId }
        if (recipe == null) {
            showToast("⚒️ Esa receta no figura en el libro del herrero.", "IRON")
            return false
        }
        if (draft.base.charLevel < recipe.requiredLevel) {
            showToast("🔒 «${recipe.name}» exige nivel ${recipe.requiredLevel}.", "IRON")
            return false
        }
        if (draft.gold < recipe.goldCost) {
            showToast("💰 Forjar «${recipe.name}» cuesta ${recipe.goldCost} de oro.", "IRON")
            return false
        }
        val missing = recipe.materials.filter { (draft.materialBag[it.id] ?: 0) < it.quantity }
        if (missing.isNotEmpty()) {
            val label = missing.joinToString(", ") {
                "${EldoriaMaterials.name(it.id)} ×${it.quantity - (draft.materialBag[it.id] ?: 0)}"
            }
            showToast("🧰 Faltan materiales para «${recipe.name}»: $label.", "IRON")
            return false
        }

        recipe.materials.forEach { mat ->
            draft.materialBag[mat.id] = (draft.materialBag[mat.id] ?: 0) - mat.quantity
        }
        draft.gold -= recipe.goldCost

        val q = quality.coerceIn(0, 100)
        val steps = when {
            q >= 95 -> 2
            q >= 70 -> 1
            else -> 0
        }
        val rarity = upgradeRarity(recipe.resultRarity, steps)
        val level = (recipe.resultLevel + q / 20).coerceAtLeast(1)
        val factor = when (rarity) {
            "UNIVERSAL" -> 6
            "ARCANO" -> 5
            "LEGENDARIO" -> 4
            "ÉPICO" -> 3
            "RARO" -> 2
            else -> 1
        }
        val offensive = recipe.resultItemType.uppercase() == "WEAPON"
        val crafted = Item(
            id = "craft_${recipeId}_${System.currentTimeMillis()}",
            name = if (steps > 0) "${recipe.resultItemName} de Maestro" else recipe.resultItemName,
            type = recipe.resultItemType,
            rarity = rarity,
            strBonus = if (offensive) level * factor / 2 else level * factor / 4,
            dexBonus = level * factor / 4,
            intBonus = level * factor / 4,
            conBonus = if (offensive) level * factor / 4 else level * factor / 3,
            dmgBonus = if (offensive) level * factor * 2 else level * factor / 2,
            defBonus = if (offensive) level * factor / 2 else level * factor * 2,
            hpRegen = if (factor >= 4) factor else 0,
            description = "${recipe.description} · Forjada con calidad $q.",
            itemLevel = level,
            imageResName = ""
        )
        draft.inventory.add(crafted)
        if (q >= 70) draft.materialBag[MAT_EMBER] = (draft.materialBag[MAT_EMBER] ?: 0) + 1
        draft.after += { host.hostPlaySound("crit") }
        showToast("⚒️ Forjas «${crafted.name}» ($rarity, calidad $q).", "EMBER")
        return true
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  MINIJUEGOS
    // ═══════════════════════════════════════════════════════════════════════

    fun openMinigame(request: MinigameRequest) {
        if (request.id.isBlank()) return
        val cost = minigameEntryCost(request.id)
        if (cost > 0) {
            val progress = host.currentProgress()
            if (progress == null || progress.charGold < cost) {
                showToast("💰 La entrada cuesta $cost de oro y no los llevas encima.", "IRON")
                return
            }
            edit { draft ->
                draft.gold -= cost
                true
            }
        }
        _lastMinigameResult.value = null
        _minigame.value = request
        host.hostPlaySound("click")
    }

    fun submitMinigameResult(result: MinigameResult) {
        _lastMinigameResult.value = result
        if (result.id.isBlank()) return

        edit { draft ->
            val id = result.id.uppercase()
            val score = result.score.coerceIn(0, 100)
            val previousBest = draft.scores[id] ?: 0
            if (result.score > previousBest) draft.scores[id] = result.score
            val level = draft.base.charLevel.coerceAtLeast(1)
            val run = draft.expeditionState
            val seed = System.currentTimeMillis() + result.score * 31L
            val tier = if (run.active) tierForDungeon(run.dungeonId) else EldoriaContracts.tierForLevel(level)

            when (id) {
                "YUNQUE" -> {
                    val recipeId = result.contextJson.trim()
                    if (recipeId.isNotBlank()) {
                        if (!applyCraft(draft, recipeId, score)) {
                            grantMaterialsInto(draft, mapOf(MAT_EMBER to 1), run)
                            showToast("⚒️ El golpe no cuaja, pero rescatas una brasa de forja.", "EMBER")
                        }
                    } else {
                        val drops = EldoriaMaterials.rollDrops(tier, 2 + score / 25, seed)
                        grantMaterialsInto(draft, drops, run)
                        showToast("⚒️ Practicas en el yunque y sacas material aprovechable.", "EMBER")
                    }
                }

                "GANZUA" -> {
                    val loot = EldoriaGenerators.expeditionLoot(
                        if (run.active) run.dungeonId else 1,
                        (score / 25).coerceIn(0, 3),
                        EldoriaExpeditions.KIND_TREASURE,
                        level,
                        seed
                    ).map { it.copy(rarity = upgradeRarity(it.rarity, if (result.success) 1 else 0)) }
                    draft.inventory.addAll(loot)
                    val gold = (140 * level * (1 + score / 50)).coerceAtLeast(100)
                    draft.gold += gold
                    showToast("🗝️ Fuerzas el cofre: ${loot.size} objetos y $gold de oro.", "GOLD")
                }

                "GLIFOS" -> {
                    val drops = EldoriaMaterials.rollDrops(tier, 2 + score / 20, seed)
                    grantMaterialsInto(draft, drops, run)
                    if (score >= 60) draft.inventory.addAll(newPotions(2))
                    showToast("🔯 Los glifos se ordenan y el altar te paga en materiales.", "ARCANE")
                }

                "EXCAVACION" -> {
                    val drops = EldoriaMaterials.rollDrops(tier, 3 + score / 12, seed)
                    grantMaterialsInto(draft, drops, run)
                    showToast("⛏️ La cripta suelta ${drops.values.sum()} unidades de material.", "IRON")
                }

                "ADIESTRAMIENTO" -> {
                    val parts = result.contextJson.split("|")
                    val petId = parts.getOrNull(0)?.trim().orEmpty().ifBlank { draft.activePetId }
                    val discipline = parts.getOrNull(1)?.trim().orEmpty().ifBlank { DISC_FURY }
                    if (!applyTraining(draft, petId, discipline, score, chargeGold = true)) {
                        val idx = draft.roster.indexOfFirst { it.id == petId }
                        if (idx >= 0) {
                            draft.roster[idx] = draft.roster[idx].copy(
                                bond = (draft.roster[idx].bond + 2).coerceAtMost(100)
                            )
                        }
                    }
                }

                "VIGILIA" -> {
                    val maxHp = draft.base.maxHp.coerceAtLeast(1)
                    val maxMp = draft.base.maxMp.coerceAtLeast(1)
                    val healed = maxHp * score / 100
                    draft.hp = (draft.hp + healed).coerceAtMost(maxHp)
                    draft.mp = (draft.mp + maxMp * score / 100).coerceAtMost(maxMp)
                    if (run.active) {
                        draft.expeditionState = run.copy(
                            persistentHp = draft.hp,
                            persistentMp = draft.mp,
                            log = appendLog(run, "🔥 La hoguera aguanta la noche: +$healed de vida.")
                        )
                    }
                    showToast("🔥 Defiendes la hoguera y recuperas un $score % de vida y maná.", "VITAE")
                }

                else -> {
                    draft.gold += 50 * level
                    showToast("🎲 Recibes una recompensa simbólica por la partida.", "GOLD")
                }
            }

            if (result.score > previousBest && result.score > 0) {
                showToast("🏆 Nueva mejor marca en ${minigameName(id)}: ${result.score}.", "GOLD")
            }
            draft.after += { host.hostPlaySound(if (result.success) "victory" else "click") }
            true
        }
    }

    fun closeMinigame() {
        _minigame.value = null
    }

    fun bestScore(minigameId: String): Int = minigameScores[minigameId.uppercase()] ?: 0

    fun minigameEntryCost(minigameId: String): Int = when (minigameId.uppercase()) {
        "GLIFOS" -> 250
        "EXCAVACION" -> 500
        // ADIESTRAMIENTO no cobra entrada: la sesión ya la cobra `applyTraining` con
        // el precio que anuncia la ficha de disciplina. Cobrar las dos dejaba al
        // jugador sin oro Y sin entrenamiento.
        else -> 0
    }

    private fun minigameName(id: String): String = when (id.uppercase()) {
        "YUNQUE" -> "El Yunque de Grommash"
        "GANZUA" -> "Ganzúa del Ladrón"
        "GLIFOS" -> "Glifos Rúnicos"
        "EXCAVACION" -> "Excavación de la Cripta"
        "ADIESTRAMIENTO" -> "Adiestramiento de Bestias"
        "VIGILIA" -> "Vigilia del Campamento"
        else -> "la taberna"
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CONTRATOS
    // ═══════════════════════════════════════════════════════════════════════

    fun refreshContracts(force: Boolean = false) {
        val progress = host.currentProgress()
        val level = progress?.charLevel ?: 1
        if (!force && _contractBoard.value.isNotEmpty()) return
        if (force || boardSeed == 0L) {
            boardSeed = System.currentTimeMillis() xor (level.toLong() * 7_919L)
        }
        val taken = _contracts.value.filterNot { it.claimed }.map { it.defId }.toSet()
        val rolled = EldoriaContracts.rollBoard(level, boardSeed, 6)
        val board = rolled.filterNot { it.id in taken }
        _contractBoard.value = board.ifEmpty { rolled }
        if (force) showToast("📜 El tablón de contratos se renueva.", "GOLD")
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ENCARGOS DEL REINO
    //  Comparten tablón, progreso y cobro con los contratos de taberna; sólo
    //  cambia de dónde salen: los genera el reino que estás pisando.
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Recarga el tablón local si el jugador ha cambiado de reino (o si se
     * fuerza). La semilla mezcla reino y nivel: dos héroes distintos ven
     * encargos distintos, y el mismo héroe ve los mismos hasta renovarlos.
     */
    fun refreshKingdomBoard(x: Int, y: Int, playerLevel: Int, force: Boolean = false) {
        val entry = KingdomAtlas.entryForCoords(x, y)
        if (!force && _kingdomBoardId.value == entry.id && _kingdomBoard.value.isNotEmpty()) return

        val salt = if (force) System.currentTimeMillis() else 0L
        val seed = entry.id.hashCode().toLong() * 31L + playerLevel * 7_919L + salt
        val kingdom = KingdomAtlas.dataOf(entry)
        val taken = _contracts.value.filterNot { it.claimed }.map { it.defId }.toSet()

        val rolled = KingdomQuestGenerator.generateContracts(entry, kingdom, playerLevel, seed, 3)
        _kingdomBoard.value = rolled.filterNot { it.id in taken }
        _kingdomBoardId.value = entry.id
        if (force) showToast("📜 Los encargos de ${kingdom.name} se renuevan.", "GOLD")
    }

    /** Acepta un encargo del reino. Igual que [acceptContract] pero con la def a mano. */
    fun acceptKingdomContract(defId: String) {
        val def = _kingdomBoard.value.firstOrNull { it.id == defId }
        if (def == null) {
            showToast("📜 Ese encargo ya no está disponible.", "IRON")
            return
        }
        val accepted = acceptContractDef(def)
        if (accepted) _kingdomBoard.value = _kingdomBoard.value.filterNot { it.id == defId }
    }

    /** Avance de exploración por reino: lo emite el mapa al pisar casilla nueva. */
    fun progressRealmExploration(kingdomId: String, amount: Int = 1) {
        if (kingdomId.isBlank()) return
        progressContracts(KingdomQuestGenerator.KIND_REALM, kingdomId, amount)
    }

    fun acceptContract(defId: String) {
        val def = EldoriaContracts.def(defId)
        if (def == null) {
            showToast("📜 Ese contrato ya no está en el tablón.", "IRON")
            return
        }
        if (acceptContractDef(def)) {
            _contractBoard.value = _contractBoard.value.filterNot { it.id == defId }
        }
    }

    /**
     * Núcleo común de aceptación: valida el cupo, siembra el progreso inicial y
     * mete el contrato en el zurrón. Devuelve si se aceptó de verdad.
     */
    private fun acceptContractDef(def: ContractDef): Boolean {
        var ok = false
        edit { draft ->
            val active = draft.contractList.count { !it.claimed }
            if (active >= MAX_ACTIVE_CONTRACTS) {
                showToast("📜 Ya llevas $MAX_ACTIVE_CONTRACTS contratos activos. Cierra uno antes.", "IRON")
                return@edit false
            }
            if (draft.contractList.any { it.defId == def.id && !it.claimed }) {
                showToast("📜 Ese contrato ya está en tu zurrón.", "IRON")
                return@edit false
            }
            val seeded = when {
                def.target.uppercase().startsWith("ROSTER") -> draft.roster.size.coerceAtMost(def.amount)
                else -> 0
            }
            draft.contractList.add(
                ContractProgress(
                    id = "ct_${def.id}_${System.currentTimeMillis().toString(36)}",
                    defId = def.id,
                    title = def.title,
                    description = def.description,
                    kind = def.kind,
                    target = def.target,
                    progress = seeded,
                    amount = def.amount.coerceAtLeast(1),
                    goldReward = def.goldReward,
                    expReward = def.expReward,
                    materialReward = def.materialReward,
                    materialQty = def.materialQty,
                    completed = seeded >= def.amount,
                    claimed = false,
                    tier = def.tier
                )
            )
            draft.after += { host.hostPlaySound("click") }
            showToast("📜 Aceptas «${def.title}».", "GOLD")
            ok = true
            true
        }
        return ok
    }

    fun abandonContract(contractId: String) {
        edit { draft ->
            val contract = draft.contractList.firstOrNull { it.id == contractId } ?: return@edit false
            draft.contractList.removeAll { it.id == contractId }
            showToast("🗑️ Abandonas «${contract.title}».", "IRON")
            true
        }
    }

    fun claimContract(contractId: String) {
        edit { draft ->
            val idx = draft.contractList.indexOfFirst { it.id == contractId }
            if (idx < 0) return@edit false
            val contract = draft.contractList[idx]
            if (contract.claimed) {
                showToast("📜 Ese contrato ya fue cobrado.", "IRON")
                return@edit false
            }
            if (!contract.completed && contract.progress < contract.amount) {
                showToast("📜 «${contract.title}» aún no está completo (${contract.progress}/${contract.amount}).", "IRON")
                return@edit false
            }

            draft.gold += contract.goldReward
            draft.exp += contract.expReward
            if (contract.materialReward.isNotBlank() && contract.materialQty > 0) {
                draft.materialBag[contract.materialReward] =
                    (draft.materialBag[contract.materialReward] ?: 0) + contract.materialQty
            }
            draft.contractList[idx] = contract.copy(completed = true, claimed = true)

            // Conserva sólo los seis últimos contratos cobrados para no inflar el JSON.
            val claimed = draft.contractList.filter { it.claimed }
            if (claimed.size > 6) {
                val drop = claimed.take(claimed.size - 6).map { it.id }.toSet()
                draft.contractList = draft.contractList.filterNot { it.id in drop }.toMutableList()
            }

            val matLabel = if (contract.materialQty > 0) {
                " y ${EldoriaMaterials.name(contract.materialReward)} ×${contract.materialQty}"
            } else ""
            draft.after += { host.hostPlaySound("victory") }
            showToast(
                "🏅 Cobras «${contract.title}»: ${contract.goldReward} de oro, ${contract.expReward} de EXP$matLabel.",
                "GOLD"
            )
            true
        }
    }

    fun progressContracts(kind: String, target: String, amount: Int = 1) {
        if (kind.isBlank() || target.isBlank() || amount <= 0) return
        val current = _contracts.value
        val updated = contractsAdvanced(current, kind, target, amount)
        if (updated == current) return
        val newlyDone = updated.filterIndexed { index, contract ->
            contract.completed && !current[index].completed
        }
        edit { draft ->
            draft.contractList = contractsAdvanced(draft.contractList, kind, target, amount).toMutableList()
            true
        }
        newlyDone.forEach { showToast("✅ Contrato listo para cobrar: «${it.title}».", "GOLD") }
    }

    private fun contractsAdvanced(
        list: List<ContractProgress>,
        kind: String,
        target: String,
        amount: Int
    ): List<ContractProgress> = list.map { contract ->
        if (contract.claimed || contract.completed) return@map contract
        if (!contract.kind.equals(kind, ignoreCase = true)) return@map contract
        if (!targetMatches(contract.target, target)) return@map contract
        val value = (contract.progress + amount).coerceIn(0, contract.amount.coerceAtLeast(1))
        contract.copy(progress = value, completed = value >= contract.amount)
    }

    private fun contractsSet(
        list: List<ContractProgress>,
        kind: String,
        target: String,
        absolute: Int
    ): List<ContractProgress> = list.map { contract ->
        if (contract.claimed || contract.completed) return@map contract
        if (!contract.kind.equals(kind, ignoreCase = true)) return@map contract
        if (!targetMatches(contract.target, target)) return@map contract
        val value = absolute.coerceIn(0, contract.amount.coerceAtLeast(1))
        contract.copy(progress = value, completed = value >= contract.amount)
    }

    private fun targetMatches(contractTarget: String, incoming: String): Boolean {
        val a = contractTarget.trim().uppercase()
        val b = incoming.trim().uppercase()
        if (a.isEmpty() || b.isEmpty()) return false
        if (a == b) return true
        if (a.substringBefore(':') == b) return true
        if (b.substringBefore(':') == a) return true
        val ap = a.split(':')
        val bp = b.split(':')
        if (ap.size == 2 && bp.size == 2 && ap[0] == bp[0]) {
            val need = ap[1].toIntOrNull()
            val got = bp[1].toIntOrNull()
            if (need != null && got != null) return got >= need
        }
        return false
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  AJUSTES
    // ═══════════════════════════════════════════════════════════════════════

    fun updateSettings(newSettings: GameSettings) {
        val clean = newSettings.copy(textScale = newSettings.textScale.coerceIn(85, 130))
        // Los ajustes viven en la fila de la partida: sin héroe activo no hay dónde
        // escribirlos, así que se avisa en vez de fingir que se han guardado.
        if (host.currentProgress() == null) {
            showToast("⚙️ Crea un héroe para guardar tus ajustes.", "IRON")
            return
        }
        edit { draft ->
            draft.settingsValue = clean
            true
        }
        _settings.value = clean
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  GANCHOS DE COMBATE
    // ═══════════════════════════════════════════════════════════════════════

    fun onCombatVictory(enemyName: String, enemySpeciesId: String, isBoss: Boolean, level: Int) {
        edit { draft ->
            registerKill(draft, enemySpeciesId, isBoss)

            val tier = EldoriaBestiary.species(enemySpeciesId)?.tier
                ?: EldoriaContracts.tierForLevel(level.coerceAtLeast(1))
            val seed = System.currentTimeMillis() + level.toLong() * 17L
            val units = if (isBoss) 5 else 1
            grantMaterialsInto(draft, EldoriaMaterials.rollDrops(tier, units, seed), draft.expeditionState)

            val idx = draft.roster.indexOfFirst { it.id == draft.activePetId }
            if (idx >= 0) {
                val pet = draft.roster[idx]
                var grown = grantPetExp(pet, 25 + level.coerceAtLeast(1) * 6 + if (isBoss) 400 else 0)
                grown = grown.copy(
                    satiety = (grown.satiety - 1).coerceAtLeast(0),
                    bond = (grown.bond + if (isBoss) 2 else 0).coerceAtMost(100)
                )
                draft.roster[idx] = grown
            }

            if (isBoss) {
                grantMaterialsInto(draft, mapOf(MAT_ANIMA to 15 + level / 4), draft.expeditionState)
                showToast("👑 $enemyName cae. El bestiario registra la hazaña.", "GOLD")
            }
            true
        }
    }

    fun onCombatDefeat() {
        edit { draft ->
            val idx = draft.roster.indexOfFirst { it.id == draft.activePetId }
            if (idx >= 0) {
                val pet = draft.roster[idx]
                draft.roster[idx] = pet.copy(
                    satiety = (pet.satiety - 15).coerceAtLeast(0),
                    injuries = (pet.injuries + 1).coerceAtMost(5),
                    bond = (pet.bond - 2).coerceAtLeast(0)
                )
            }
            draft.after += { host.hostPlaySound("defeat") }
            showToast("💀 Has caído. Tu bestia carga contigo de vuelta a la superficie.", "BLOOD")
            true
        }
    }
}
