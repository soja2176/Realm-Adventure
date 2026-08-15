package com.example.data.engine

import com.example.data.Item
import com.example.data.content.EldoriaExpeditions
import com.example.data.content.EldoriaMaterials
import com.example.data.model.ExpeditionOffer
import com.example.data.model.ExpeditionRoom
import com.example.data.model.ExpeditionState
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

/**
 * Generadores puros y deterministas de la capa de expediciones.
 *
 * Todo lo que hay aquí depende exclusivamente de la semilla recibida: dos llamadas
 * con la misma semilla producen exactamente el mismo resultado. Sin Compose,
 * sin Android, sin estado global mutable.
 */
object EldoriaGenerators {

    private const val SEAL_RIFT = "seal_grieta"      // +1 profundidad
    private const val SEAL_ECHO = "seal_eco"         // todo COMBATE pasa a ELITE
    private const val SEAL_FOG = "seal_niebla"       // antorcha inicial 60
    private const val SEAL_VOID = "seal_vacio"       // mapa oculto salvo lo adyacente
    private const val SEAL_HASTE = "seal_prisa"

    private const val ABYSS_BLACK_TIDES = 101        // hogueras extra, antorcha doble

    // ═══════════════════════════════════════════════════════════════════════
    //  GRAFO DE EXPEDICIÓN
    // ═══════════════════════════════════════════════════════════════════════

    /** Peso relativo de cada tipo de sala en el sorteo general. */
    private val KIND_WEIGHTS: List<Pair<String, Int>> = listOf(
        EldoriaExpeditions.KIND_COMBAT to 100,
        EldoriaExpeditions.KIND_ELITE to 18,
        EldoriaExpeditions.KIND_TREASURE to 14,
        EldoriaExpeditions.KIND_CAMPFIRE to 12,
        EldoriaExpeditions.KIND_EVENT to 12,
        EldoriaExpeditions.KIND_TRAP to 10,
        EldoriaExpeditions.KIND_SHRINE to 8,
        EldoriaExpeditions.KIND_MERCHANT to 7,
        EldoriaExpeditions.KIND_GATE to 5,
        EldoriaExpeditions.KIND_VOID to 2
    )

    /** Tope de apariciones por tipo dentro de una misma expedición. */
    private val KIND_CAPS: Map<String, Int> = mapOf(
        EldoriaExpeditions.KIND_ELITE to 4,
        EldoriaExpeditions.KIND_TREASURE to 3,
        EldoriaExpeditions.KIND_CAMPFIRE to 3,
        EldoriaExpeditions.KIND_EVENT to 3,
        EldoriaExpeditions.KIND_TRAP to 3,
        EldoriaExpeditions.KIND_SHRINE to 2,
        EldoriaExpeditions.KIND_MERCHANT to 2,
        EldoriaExpeditions.KIND_GATE to 2,
        EldoriaExpeditions.KIND_VOID to 1
    )

    private fun payloadFor(kind: String): String = when (kind) {
        EldoriaExpeditions.KIND_COMBAT -> "NORMAL"
        EldoriaExpeditions.KIND_ELITE -> "ELITE"
        EldoriaExpeditions.KIND_BOSS -> "BOSS"
        EldoriaExpeditions.KIND_TREASURE -> "LOOT"
        EldoriaExpeditions.KIND_CAMPFIRE -> "REST"
        EldoriaExpeditions.KIND_SHRINE -> "BOON"
        EldoriaExpeditions.KIND_TRAP -> "TRAP"
        EldoriaExpeditions.KIND_MERCHANT -> "MERCHANT"
        EldoriaExpeditions.KIND_EVENT -> "EVENT"
        EldoriaExpeditions.KIND_GATE -> "GATE"
        EldoriaExpeditions.KIND_VOID -> "VOID"
        else -> "NORMAL"
    }

    private fun weightedKind(rnd: Random, counts: MutableMap<String, Int>): String {
        val pool = KIND_WEIGHTS.filter { (kind, _) ->
            val cap = KIND_CAPS[kind] ?: Int.MAX_VALUE
            (counts[kind] ?: 0) < cap
        }
        if (pool.isEmpty()) return EldoriaExpeditions.KIND_COMBAT
        val total = pool.sumOf { it.second }
        var roll = rnd.nextInt(total)
        for ((kind, weight) in pool) {
            roll -= weight
            if (roll < 0) {
                counts[kind] = (counts[kind] ?: 0) + 1
                return kind
            }
        }
        val fallback = pool.last().first
        counts[fallback] = (counts[fallback] ?: 0) + 1
        return fallback
    }

    /**
     * Grafo dirigido acíclico de 14–17 salas repartidas en [maxDepth] profundidades
     * (más si hay Sello de la Grieta), con 2–3 nodos por fila, aristas exclusivamente
     * entre filas consecutivas, todos los nodos alcanzables desde la entrada y
     * exactamente una sala JEFE al final.
     */
    fun buildExpeditionGraph(dungeonId: Int, seed: Long, maxDepth: Int, seals: List<String>): List<ExpeditionRoom> {
        val effectiveDepth = (maxDepth + if (seals.contains(SEAL_RIFT)) 1 else 0).coerceIn(1, 6)
        val rnd = Random(seed * 1_000_003L + dungeonId.toLong() * 7_919L + effectiveDepth.toLong() * 131L)

        // ── 1. Tamaño de la expedición ──
        val rowCount = effectiveDepth * 2
        val minTotal = effectiveDepth * 4 + 2          // profundidad 3 → 14
        val maxTotal = effectiveDepth * 5 + 2          // profundidad 3 → 17
        val totalRooms = minTotal + rnd.nextInt(maxTotal - minTotal + 1)
        val bodyCount = totalRooms - 1                 // la última sala siempre es el JEFE

        val sizes = IntArray(rowCount) { 2 }
        var extra = (bodyCount - rowCount * 2).coerceIn(0, rowCount)
        for (row in (0 until rowCount).shuffled(rnd)) {
            if (extra <= 0) break
            if (sizes[row] < 3) {
                sizes[row] = 3
                extra--
            }
        }

        // ── 2. Reparto de ids por fila ──
        val rows = ArrayList<List<Int>>(rowCount)
        var nextId = 0
        for (r in 0 until rowCount) {
            val ids = ArrayList<Int>(sizes[r])
            repeat(sizes[r]) { ids.add(nextId++) }
            rows.add(ids)
        }
        val bossId = nextId

        // ── 3. Tipos de sala ──
        val kinds = HashMap<Int, String>()
        val counts = HashMap<String, Int>()
        for (r in 0 until rowCount) {
            for (id in rows[r]) {
                // La primera fila siempre es combate honesto: nada de trampas ni vacíos de salida.
                kinds[id] = if (r == 0) EldoriaExpeditions.KIND_COMBAT else weightedKind(rnd, counts)
            }
        }
        kinds[bossId] = EldoriaExpeditions.KIND_BOSS

        val bodyIds = rows.flatten()

        fun convert(candidates: List<Int>, target: String): Boolean {
            val pick = candidates.firstOrNull { kinds[it] == EldoriaExpeditions.KIND_COMBAT }
                ?: candidates.firstOrNull { kinds[it] != target }
                ?: return false
            kinds[pick] = target
            return true
        }

        // Garantías de ritmo: hoguera justo antes del jefe, y al menos un tesoro y un santuario.
        val lastBodyRow = rows[rowCount - 1]
        if (lastBodyRow.none { kinds[it] == EldoriaExpeditions.KIND_CAMPFIRE }) {
            convert(lastBodyRow.shuffled(rnd), EldoriaExpeditions.KIND_CAMPFIRE)
        }
        val midIds = bodyIds.filter { it !in rows[0] }.shuffled(rnd)
        if (bodyIds.none { kinds[it] == EldoriaExpeditions.KIND_TREASURE }) {
            convert(midIds, EldoriaExpeditions.KIND_TREASURE)
        }
        if (bodyIds.none { kinds[it] == EldoriaExpeditions.KIND_SHRINE }) {
            convert(midIds, EldoriaExpeditions.KIND_SHRINE)
        }

        // Regla propia del Abismo de las Mareas Negras: dos hogueras extra.
        if (dungeonId == ABYSS_BLACK_TIDES) {
            var added = 0
            for (id in midIds) {
                if (added >= 2) break
                if (kinds[id] == EldoriaExpeditions.KIND_COMBAT) {
                    kinds[id] = EldoriaExpeditions.KIND_CAMPFIRE
                    added++
                }
            }
        }

        // Sello del Eco: todo combate normal asciende a élite.
        if (seals.contains(SEAL_ECHO)) {
            bodyIds.forEach { id ->
                if (kinds[id] == EldoriaExpeditions.KIND_COMBAT) kinds[id] = EldoriaExpeditions.KIND_ELITE
            }
        }

        // ── 4. Aristas: sólo de una fila a la siguiente ──
        val edges = HashMap<Int, LinkedHashSet<Int>>()
        fun link(from: Int, to: Int) {
            edges.getOrPut(from) { LinkedHashSet() }.add(to)
        }

        for (r in 0 until rowCount - 1) {
            val a = rows[r]
            val b = rows[r + 1]
            // 4a. Cada nodo de la fila tiene salida.
            a.forEachIndexed { i, src -> link(src, b[(i * b.size) / a.size]) }
            // 4b. Cada nodo de la fila siguiente tiene entrada.
            b.forEachIndexed { j, dst ->
                val hasIncoming = a.any { edges[it]?.contains(dst) == true }
                if (!hasIncoming) link(a[(j * a.size) / b.size], dst)
            }
            // 4c. Ramificación extra sin cruces largos.
            a.forEachIndexed { i, src ->
                if (rnd.nextInt(100) < 45) {
                    val base = (i * b.size) / a.size
                    val alt = (base + if (rnd.nextBoolean()) 1 else b.size - 1) % b.size
                    link(src, b[alt])
                }
            }
        }
        // Última fila del cuerpo → JEFE.
        rows[rowCount - 1].forEach { link(it, bossId) }

        // ── 5. Materialización ──
        val revealDepthZero = !seals.contains(SEAL_VOID)
        val out = ArrayList<ExpeditionRoom>(totalRooms)
        for (r in 0 until rowCount) {
            val depth = r / 2
            rows[r].forEachIndexed { column, id ->
                val kind = kinds[id] ?: EldoriaExpeditions.KIND_COMBAT
                out.add(
                    ExpeditionRoom(
                        id = id,
                        kind = kind,
                        depth = depth,
                        column = column,
                        label = EldoriaExpeditions.roomLabel(kind, depth, seed + id * 97L),
                        next = (edges[id] ?: emptySet<Int>()).toList().sorted(),
                        cleared = false,
                        revealed = r == 0 || (revealDepthZero && r == 1),
                        locked = kind == EldoriaExpeditions.KIND_GATE,
                        payload = payloadFor(kind)
                    )
                )
            }
        }
        out.add(
            ExpeditionRoom(
                id = bossId,
                kind = EldoriaExpeditions.KIND_BOSS,
                depth = effectiveDepth - 1,
                column = 0,
                label = EldoriaExpeditions.roomLabel(EldoriaExpeditions.KIND_BOSS, effectiveDepth - 1, seed + bossId * 97L),
                next = emptyList(),
                cleared = false,
                revealed = false,
                locked = false,
                payload = "BOSS"
            )
        )
        return out
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ANTORCHA
    // ═══════════════════════════════════════════════════════════════════════

    /** Antorcha inicial: 100, o 60 con el Sello de la Niebla. */
    fun defaultTorch(seals: List<String>): Int =
        if (seals.contains(SEAL_FOG)) 60 else 100

    /**
     * Coste de antorcha al entrar en una sala. Las hogueras no consumen (restauran),
     * y algunos sellos encarecen cada paso.
     */
    fun torchCostFor(kind: String, seals: List<String>): Int {
        val base = when (kind.uppercase()) {
            EldoriaExpeditions.KIND_COMBAT -> 6
            EldoriaExpeditions.KIND_ELITE -> 9
            EldoriaExpeditions.KIND_TREASURE -> 4
            EldoriaExpeditions.KIND_CAMPFIRE -> 0
            EldoriaExpeditions.KIND_SHRINE -> 3
            EldoriaExpeditions.KIND_TRAP -> 8
            EldoriaExpeditions.KIND_MERCHANT -> 3
            EldoriaExpeditions.KIND_EVENT -> 5
            EldoriaExpeditions.KIND_GATE -> 6
            EldoriaExpeditions.KIND_BOSS -> 10
            EldoriaExpeditions.KIND_VOID -> 12
            else -> 5
        }
        if (base == 0) return 0
        var cost = base
        if (seals.contains(SEAL_FOG)) cost += 1
        if (seals.contains(SEAL_VOID)) cost += 2
        if (seals.contains(SEAL_RIFT)) cost += 1
        if (seals.contains(SEAL_HASTE)) cost -= 1
        return cost.coerceAtLeast(1)
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  OFERTAS DE SALA
    // ═══════════════════════════════════════════════════════════════════════

    private val EVENT_SCRIPTS: List<Triple<String, String, List<Triple<String, String, String>>>> = listOf(
        Triple(
            "Un cadáver aún caliente",
            "Un aventurero yace boca abajo con la mochila intacta. Alguien lo mató sin robarle nada, y eso da que pensar.",
            listOf(
                Triple("event_loot_body", "Registrar la mochila", "Botín seguro, algo de ruido"),
                Triple("event_bury_body", "Enterrarlo como es debido", "Pierdes tiempo, ganas fortuna"),
                Triple("event_leave_body", "Dejarlo estar", "Sigues sin gastar antorcha")
            )
        ),
        Triple(
            "Una voz en la oscuridad",
            "Algo te llama por tu nombre desde una grieta demasiado estrecha para nada que hable.",
            listOf(
                Triple("event_answer_voice", "Responder a la voz", "Bendición a cambio de vida"),
                Triple("event_seal_crack", "Tapiar la grieta", "Fragmentos de ánima"),
                Triple("event_ignore_voice", "Fingir que no la oyes", "Sin efecto")
            )
        ),
        Triple(
            "Un espejo rajado",
            "Tu reflejo tarda un instante de más en imitarte. Cuando parpadeas, él no.",
            listOf(
                Triple("event_touch_mirror", "Tocar el cristal", "Cambias botín por poder"),
                Triple("event_break_mirror", "Romperlo de una patada", "Materiales y mala suerte"),
                Triple("event_walk_away", "Alejarte despacio", "Sin efecto")
            )
        ),
        Triple(
            "Un pacto susurrado",
            "Una figura encapuchada te ofrece fuerza inmediata a cambio de algo que aún no has perdido.",
            listOf(
                Triple("event_accept_pact", "Aceptar el pacto", "Mucho ataque, poca defensa"),
                Triple("event_haggle_pact", "Regatear las condiciones", "Oro a cambio de nada"),
                Triple("event_refuse_pact", "Rechazarlo", "Sin efecto")
            )
        ),
        Triple(
            "Inscripción reciente",
            "Alguien grabó ayer mismo un aviso en la pared: «no bajéis por la derecha». La tinta aún gotea.",
            listOf(
                Triple("event_follow_warning", "Hacer caso al aviso", "Revela la siguiente fila"),
                Triple("event_defy_warning", "Ir justo por la derecha", "Riesgo alto, botín alto"),
                Triple("event_erase_warning", "Borrar la inscripción", "Fragmentos de ánima")
            )
        )
    )

    /** Oferta modal asociada a la sala, o `null` si la sala se resuelve sin decisión. */
    fun offerForRoom(room: ExpeditionRoom, state: ExpeditionState, playerLevel: Int, seed: Long): ExpeditionOffer? {
        val rnd = Random(seed * 31L + room.id.toLong() * 7_907L + state.dungeonId.toLong())
        return when (room.kind.uppercase()) {
            EldoriaExpeditions.KIND_TREASURE -> {
                val goldOffer = 120 * max(1, playerLevel) + rnd.nextInt(200)
                ExpeditionOffer(
                    kind = "LOOT",
                    title = "Cámara del Tesoro",
                    description = "Un arcón reforzado espera bajo el polvo. La cerradura tiene marcas de dientes.",
                    optionIds = listOf("loot_open", "loot_force", "loot_skip"),
                    optionTitles = listOf("Abrirlo con cuidado", "Forzar la cerradura", "No tocarlo"),
                    optionSubtitles = listOf(
                        "Botín garantizado del nivel del calabozo",
                        "Botín mejorado, pero pierdes 10 de antorcha",
                        "Conservas $goldOffer de oro imaginario y la dignidad"
                    ),
                    optionTones = listOf("GOLD", "EMBER", "IRON")
                )
            }

            EldoriaExpeditions.KIND_CAMPFIRE -> ExpeditionOffer(
                kind = "REST",
                title = "Hoguera del Refugio",
                description = "Las brasas aún sirven. Sólo hay tiempo para una cosa antes de que se apaguen.",
                optionIds = listOf("rest_heal", "rest_torch", "rest_pet"),
                optionTitles = listOf("Vendar las heridas", "Reavivar la antorcha", "Atender a tu bestia"),
                optionSubtitles = listOf(
                    "Recuperas un 35 % de vida y maná",
                    "Recuperas 30 puntos de antorcha",
                    "+20 de saciedad y +5 de vínculo"
                ),
                optionTones = listOf("VITAE", "EMBER", "GOLD")
            )

            EldoriaExpeditions.KIND_SHRINE -> {
                val choices = rollBoonChoices(state, seed + room.id, 3)
                if (choices.isEmpty()) null
                else ExpeditionOffer(
                    kind = "BOON",
                    title = "Santuario Olvidado",
                    description = "Tres símbolos arden a la vez sobre el altar. Sólo uno acepta tu mano.",
                    optionIds = choices,
                    optionTitles = choices.map { EldoriaExpeditions.boon(it)?.name ?: it },
                    optionSubtitles = choices.map { EldoriaExpeditions.boon(it)?.description ?: "" },
                    optionTones = choices.map { EldoriaExpeditions.boon(it)?.tone ?: "GOLD" }
                )
            }

            EldoriaExpeditions.KIND_MERCHANT -> {
                val torchPrice = 400 + playerLevel * 25
                val keyPrice = 1_200 + playerLevel * 60
                val potionPrice = 300 + playerLevel * 20
                ExpeditionOffer(
                    kind = "MERCHANT",
                    title = "Puesto del Buhonero",
                    description = "Un mercader que no debería estar aquí sonríe como si te esperase desde hace días.",
                    optionIds = listOf("shop_torch", "shop_key", "shop_potion", "shop_leave"),
                    optionTitles = listOf("3 antorchas", "1 llave sellada", "2 pociones de vida", "Marcharte"),
                    optionSubtitles = listOf(
                        "$torchPrice de oro",
                        "$keyPrice de oro",
                        "$potionPrice de oro",
                        "No hoy, gracias"
                    ),
                    optionTones = listOf("EMBER", "IRON", "VITAE", "SILVER")
                )
            }

            EldoriaExpeditions.KIND_EVENT -> {
                val script = EVENT_SCRIPTS[rnd.nextInt(EVENT_SCRIPTS.size)]
                ExpeditionOffer(
                    kind = "EVENT",
                    title = script.first,
                    description = script.second,
                    optionIds = script.third.map { it.first },
                    optionTitles = script.third.map { it.second },
                    optionSubtitles = script.third.map { it.third },
                    optionTones = listOf("GOLD", "ARCANE", "IRON").take(script.third.size)
                )
            }

            EldoriaExpeditions.KIND_GATE -> ExpeditionOffer(
                kind = "GATE",
                title = "Puerta Sellada",
                description = "Hierro negro sin bisagras visibles. Detrás se oye algo que respira despacio.",
                optionIds = listOf("gate_key", "gate_force", "gate_back"),
                optionTitles = listOf("Usar una llave sellada", "Forzarla a hombros", "Retroceder"),
                optionSubtitles = listOf(
                    "Consume 1 llave · botín garantizado",
                    "Pierdes un 15 % de vida y 12 de antorcha",
                    "Vuelves al mapa sin abrirla"
                ),
                optionTones = listOf("GOLD", "BLOOD", "IRON")
            )

            else -> null
        }
    }

    /** Tres bendiciones distintas que el jugador aún no posee. */
    fun rollBoonChoices(state: ExpeditionState, seed: Long, count: Int = 3): List<String> {
        val owned = state.boons.toSet()
        val pool = EldoriaExpeditions.BOONS.map { it.id }.filter { it !in owned }.toMutableList()
        if (pool.isEmpty()) return emptyList()
        val rnd = Random(seed xor 0x0B00_0000L)
        val out = ArrayList<String>(count)
        repeat(count.coerceAtMost(pool.size)) {
            out.add(pool.removeAt(rnd.nextInt(pool.size)))
        }
        return out
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  BOTÍN
    // ═══════════════════════════════════════════════════════════════════════

    private val LOOT_TYPES = listOf(
        "WEAPON", "ARMOR", "SHIELD", "HELMET", "BOOTS", "GLOVES", "RING", "EARRING", "RELIC", "WINGS"
    )

    private val LOOT_ART: Map<String, String> = mapOf(
        "WEAPON" to "img_item_sword_1784593548868",
        "ARMOR" to "img_item_plate_1784593577913",
        "SHIELD" to "img_item_shield_1784593608106",
        "HELMET" to "img_item_helmet_1784658214656",
        "BOOTS" to "img_item_boots_1784658239207",
        "GLOVES" to "img_item_gloves_1784658226142",
        "RING" to "img_item_ring_1784593597914",
        "EARRING" to "img_item_earring_1784658263366",
        "RELIC" to "img_item_relic_1784658251007",
        "WINGS" to "img_item_wings_1784658202673"
    )

    private val LOOT_NOUNS: Map<String, String> = mapOf(
        "WEAPON" to "Filo", "ARMOR" to "Coraza", "SHIELD" to "Broquel", "HELMET" to "Yelmo",
        "BOOTS" to "Grebas", "GLOVES" to "Guanteletes", "RING" to "Anillo", "EARRING" to "Pendiente",
        "RELIC" to "Reliquia", "WINGS" to "Alas"
    )

    private val LOOT_PREFIX = listOf(
        "del Abismo", "de Ceniza", "del Vado", "de Escarcha", "del Cónclave", "de Basalto",
        "del Eco", "de Ámbar", "del Umbral", "de Sangre Seca", "del Yunque", "de Aurora"
    )

    private val LOOT_SUFFIX = listOf(
        "Olvidado", "Sellado", "Rúnico", "Profanado", "Ancestral", "Hambriento",
        "Silente", "Coronado", "Insomne", "Fracturado"
    )

    private fun rollRarity(rnd: Random, depth: Int, kind: String, isAbyss: Boolean): String {
        var score = depth * 12 + rnd.nextInt(100)
        score += when (kind.uppercase()) {
            EldoriaExpeditions.KIND_BOSS -> 55
            EldoriaExpeditions.KIND_TREASURE -> 30
            EldoriaExpeditions.KIND_ELITE -> 18
            EldoriaExpeditions.KIND_GATE -> 22
            else -> 0
        }
        if (isAbyss) score += 25
        return when {
            score >= 175 -> "UNIVERSAL"
            score >= 155 -> "ARCANO"
            score >= 128 -> "LEGENDARIO"
            score >= 96 -> "ÉPICO"
            score >= 58 -> "RARO"
            else -> "COMÚN"
        }
    }

    private fun rarityFactor(rarity: String): Int = when (rarity) {
        "UNIVERSAL" -> 10
        "ARCANO" -> 7
        "LEGENDARIO" -> 5
        "ÉPICO" -> 3
        "RARO" -> 2
        else -> 1
    }

    /** Botín determinista de una sala de expedición. */
    fun expeditionLoot(dungeonId: Int, depth: Int, kind: String, playerLevel: Int, seed: Long): List<Item> {
        val rnd = Random(seed * 1_000_033L + dungeonId.toLong() * 31L + depth.toLong() * 17L + kind.hashCode().toLong())
        val isAbyss = dungeonId >= 101
        val count = when (kind.uppercase()) {
            EldoriaExpeditions.KIND_BOSS -> 3
            EldoriaExpeditions.KIND_TREASURE -> 2 + rnd.nextInt(2)
            EldoriaExpeditions.KIND_ELITE -> 1 + rnd.nextInt(2)
            EldoriaExpeditions.KIND_GATE -> 2
            EldoriaExpeditions.KIND_COMBAT -> if (rnd.nextInt(100) < 45) 1 else 0
            EldoriaExpeditions.KIND_EVENT -> if (rnd.nextInt(100) < 60) 1 else 0
            else -> 0
        }
        if (count <= 0) return emptyList()

        val lvl = playerLevel.coerceAtLeast(1)
        val out = ArrayList<Item>(count)
        repeat(count) { i ->
            val type = LOOT_TYPES[rnd.nextInt(LOOT_TYPES.size)]
            val rarity = rollRarity(rnd, depth, kind, isAbyss)
            val factor = rarityFactor(rarity)
            val itemLevel = (lvl + depth * 4 + rnd.nextInt(6)).coerceAtLeast(1)
            val base = 3 + itemLevel / 4
            val noun = LOOT_NOUNS[type] ?: "Reliquia"
            val name = if (rnd.nextBoolean()) {
                "$noun ${LOOT_PREFIX[rnd.nextInt(LOOT_PREFIX.size)]}"
            } else {
                "$noun ${LOOT_SUFFIX[rnd.nextInt(LOOT_SUFFIX.size)]}"
            }
            val isOffensive = type == "WEAPON" || type == "RING" || type == "EARRING" || type == "RELIC"
            out.add(
                Item(
                    id = "exp_${dungeonId}_${depth}_${rnd.nextInt(1_000_000)}_$i",
                    name = name,
                    type = type,
                    rarity = rarity,
                    strBonus = if (isOffensive) base * factor / 2 else base * factor / 4,
                    dexBonus = base * factor / 4,
                    intBonus = if (type == "RELIC" || type == "EARRING") base * factor / 2 else base * factor / 5,
                    conBonus = if (isOffensive) base * factor / 5 else base * factor / 3,
                    dmgBonus = if (isOffensive) base * factor * 2 else base * factor / 2,
                    defBonus = if (isOffensive) base * factor / 2 else base * factor * 2,
                    hpRegen = if (rarity == "UNIVERSAL" || rarity == "ARCANO") factor else 0,
                    description = "Recuperado en la profundidad ${depth + 1} de la expedición.",
                    itemLevel = itemLevel,
                    imageResName = LOOT_ART[type] ?: "img_item_relic_1784658251007"
                )
            )
        }
        return out
    }

    /** Materiales sueltos de una sala, con el tier deducido del destino y la profundidad. */
    fun expeditionMaterials(dungeonId: Int, depth: Int, kind: String, seed: Long): Map<String, Int> {
        val tier = when {
            dungeonId >= 101 -> 6
            dungeonId >= 13 -> 6
            dungeonId >= 11 -> 5
            dungeonId >= 8 -> 4
            dungeonId >= 6 -> 3
            dungeonId >= 3 -> 2
            else -> 1
        }
        val count = when (kind.uppercase()) {
            EldoriaExpeditions.KIND_BOSS -> 8
            EldoriaExpeditions.KIND_TREASURE -> 5
            EldoriaExpeditions.KIND_ELITE -> 3
            EldoriaExpeditions.KIND_COMBAT -> 2
            EldoriaExpeditions.KIND_GATE -> 4
            else -> 1
        } + depth
        return EldoriaMaterials.rollDrops(tier, count, seed + dungeonId * 13L + depth * 7L)
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  IDENTIDADES
    // ═══════════════════════════════════════════════════════════════════════

    /** Identificador único y estable para una nueva instancia de mascota. */
    fun petIdFor(speciesId: String, stamp: Long): String {
        val slug = speciesId.removePrefix("pet_").ifEmpty { "bestia" }
        val tail = abs(stamp).toString(36)
        return "pet_${slug}_$tail"
    }

    /** Semilla derivada estable, útil para sub-sorteos dentro de una misma run. */
    fun deriveSeed(base: Long, salt: Int): Long = base * 6_364_136_223_846_793L + salt.toLong() * 1_442_695_040L
}
