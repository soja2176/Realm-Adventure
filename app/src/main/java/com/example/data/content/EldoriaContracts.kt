package com.example.data.content

import com.example.data.model.ContractDef
import kotlin.random.Random

/**
 * Tablón de contratos: 24 plantillas repartidas en 4 arquetipos × 6 niveles.
 *
 * - CAZA         → `target` es el id de arquetipo de [EldoriaBestiary].
 * - EXPEDICION   → `target` es una condición de expedición ("DEPTH:2", "DUNGEON:104", ...).
 * - RECOLECCION  → `target` es un id de material de [EldoriaMaterials].
 * - DOMA         → `target` es una acción de mascota ("ADOPTAR", "ENTRENAR:FURIA", ...).
 */
object EldoriaContracts {

    const val KIND_HUNT = "CAZA"
    const val KIND_EXPEDITION = "EXPEDICION"
    const val KIND_GATHER = "RECOLECCION"
    const val KIND_TAMING = "DOMA"

    /** Oro de recompensa base por nivel de contrato. */
    private fun goldFor(tier: Int): Int = when (tier) {
        1 -> 750
        2 -> 2_000
        3 -> 3_750
        4 -> 6_000
        5 -> 8_750
        else -> 12_000
    }

    /** Experiencia de recompensa base por nivel de contrato. */
    private fun expFor(tier: Int): Int = when (tier) {
        1 -> 450
        2 -> 1_200
        3 -> 2_250
        4 -> 3_600
        5 -> 5_250
        else -> 7_200
    }

    val DEFS: List<ContractDef> = listOf(
        // ─────────── CAZA (6) ───────────
        ContractDef(
            id = "ct_caza_1", title = "Purga de Brutos",
            description = "La guardia de Ciudad Alba paga por cada bruto que deje de golpear puertas.",
            kind = KIND_HUNT, target = EldoriaBestiary.BRUTO, amount = 8,
            goldReward = goldFor(1), expReward = expFor(1),
            materialReward = "iron", materialQty = 6, tier = 1
        ),
        ContractDef(
            id = "ct_caza_2", title = "Silenciar a los Lanzadores",
            description = "Los conjuradores enemigos han empezado a coordinarse. Córtales la voz.",
            kind = KIND_HUNT, target = EldoriaBestiary.LANZADOR, amount = 10,
            goldReward = goldFor(2), expReward = expFor(2),
            materialReward = "crystal", materialQty = 5, tier = 2
        ),
        ContractDef(
            id = "ct_caza_3", title = "Quebrar Bastiones",
            description = "Ningún avance es posible mientras esos muros vivientes sigan en pie.",
            kind = KIND_HUNT, target = EldoriaBestiary.BASTION, amount = 12,
            goldReward = goldFor(3), expReward = expFor(3),
            materialReward = "steel", materialQty = 8, tier = 3
        ),
        ContractDef(
            id = "ct_caza_4", title = "Cazar Acechadores",
            description = "Tres exploradores han desaparecido sin ruido. Devuelve el favor.",
            kind = KIND_HUNT, target = EldoriaBestiary.ACECHADOR, amount = 14,
            goldReward = goldFor(4), expReward = expFor(4),
            materialReward = "shadow_essence", materialQty = 6, tier = 4
        ),
        ContractDef(
            id = "ct_caza_5", title = "Exterminio de Enjambres",
            description = "Se reproducen más rápido de lo que la aldea puede enterrar a los suyos.",
            kind = KIND_HUNT, target = EldoriaBestiary.ENJAMBRE, amount = 20,
            goldReward = goldFor(5), expReward = expFor(5),
            materialReward = "blood_gem", materialQty = 5, tier = 5
        ),
        ContractDef(
            id = "ct_caza_6", title = "Decapitar Invocadores",
            description = "Mientras vivan seguirán abriendo puertas que nadie pidió abrir.",
            kind = KIND_HUNT, target = EldoriaBestiary.INVOCADOR, amount = 16,
            goldReward = goldFor(6), expReward = expFor(6),
            materialReward = "ancient_relic", materialQty = 3, tier = 6
        ),

        // ─────────── EXPEDICION (6) ───────────
        ContractDef(
            id = "ct_exped_1", title = "Descenso Superficial",
            description = "Limpia diez salas de expedición sin abandonar la primera profundidad.",
            kind = KIND_EXPEDITION, target = "ROOMS:10", amount = 10,
            goldReward = goldFor(1), expReward = expFor(1),
            materialReward = "anima_shard", materialQty = 10, tier = 1
        ),
        ContractDef(
            id = "ct_exped_2", title = "Corazón del Calabozo",
            description = "Alcanza la segunda profundidad en tres expediciones distintas.",
            kind = KIND_EXPEDITION, target = "DEPTH:2", amount = 3,
            goldReward = goldFor(2), expReward = expFor(2),
            materialReward = "anima_shard", materialQty = 25, tier = 2
        ),
        ContractDef(
            id = "ct_exped_3", title = "Tocar el Fondo",
            description = "Derrota al jefe final de dos expediciones completas.",
            kind = KIND_EXPEDITION, target = "BOSS", amount = 2,
            goldReward = goldFor(3), expReward = expFor(3),
            materialReward = "forge_ember", materialQty = 8, tier = 3
        ),
        ContractDef(
            id = "ct_exped_4", title = "Sin Antorcha",
            description = "Completa una expedición terminando con menos de 20 puntos de antorcha.",
            kind = KIND_EXPEDITION, target = "TORCH_LOW", amount = 1,
            goldReward = goldFor(4), expReward = expFor(4),
            materialReward = "sealed_key", materialQty = 3, tier = 4
        ),
        ContractDef(
            id = "ct_exped_5", title = "Sellado Triple",
            description = "Termina una expedición con tres sellos activos desde el primer paso.",
            kind = KIND_EXPEDITION, target = "SEALS:3", amount = 1,
            goldReward = goldFor(5), expReward = expFor(5),
            materialReward = "pure_crystal", materialQty = 4, tier = 5
        ),
        ContractDef(
            id = "ct_exped_6", title = "Conquista de Abismo",
            description = "Conquista cualquiera de los cuatro Abismos hasta su sala final.",
            kind = KIND_EXPEDITION, target = "ABYSS", amount = 1,
            goldReward = goldFor(6), expReward = expFor(6),
            materialReward = "infinite_diamond", materialQty = 2, tier = 6
        ),

        // ─────────── RECOLECCION (6) ───────────
        ContractDef(
            id = "ct_recol_1", title = "Encargo de Hierro",
            description = "La fragua del pueblo se ha quedado seca. Reúne hierro suficiente.",
            kind = KIND_GATHER, target = "iron", amount = 20,
            goldReward = goldFor(1), expReward = expFor(1),
            materialReward = "leather", materialQty = 10, tier = 1
        ),
        ContractDef(
            id = "ct_recol_2", title = "Cosecha de Hierbas",
            description = "La botica necesita hoja amarga antes de que llegue el invierno.",
            kind = KIND_GATHER, target = "herbs", amount = 30,
            goldReward = goldFor(2), expReward = expFor(2),
            materialReward = "crystal", materialQty = 6, tier = 2
        ),
        ContractDef(
            id = "ct_recol_3", title = "Acero para el Yunque",
            description = "Sin acero templado no hay armas nuevas para la milicia.",
            kind = KIND_GATHER, target = "steel", amount = 24,
            goldReward = goldFor(3), expReward = expFor(3),
            materialReward = "forge_ember", materialQty = 6, tier = 3
        ),
        ContractDef(
            id = "ct_recol_4", title = "Escamas de Dragón",
            description = "El armero jura que puede hacer una coraza si le traes escamas enteras.",
            kind = KIND_GATHER, target = "dragon_scale", amount = 12,
            goldReward = goldFor(4), expReward = expFor(4),
            materialReward = "gold_ore", materialQty = 10, tier = 4
        ),
        ContractDef(
            id = "ct_recol_5", title = "Esencia de Sombra",
            description = "Un cliente sin rostro paga muy bien por sombra embotellada.",
            kind = KIND_GATHER, target = "shadow_essence", amount = 15,
            goldReward = goldFor(5), expReward = expFor(5),
            materialReward = "mystic_silk", materialQty = 8, tier = 5
        ),
        ContractDef(
            id = "ct_recol_6", title = "Plumas de Fénix",
            description = "Sólo caen cuando algo eterno decide arder. Consigue seis.",
            kind = KIND_GATHER, target = "phoenix_feather", amount = 6,
            goldReward = goldFor(6), expReward = expFor(6),
            materialReward = "pure_crystal", materialQty = 5, tier = 6
        ),

        // ─────────── DOMA (6) ───────────
        ContractDef(
            id = "ct_doma_1", title = "Primer Vínculo",
            description = "Adopta una mascota y llévala contigo hasta que confíe en ti.",
            kind = KIND_TAMING, target = "ADOPTAR", amount = 1,
            goldReward = goldFor(1), expReward = expFor(1),
            materialReward = "herbs", materialQty = 12, tier = 1
        ),
        ContractDef(
            id = "ct_doma_2", title = "Disciplina de Furia",
            description = "Entrena la disciplina de Furia de tu bestia en cinco sesiones.",
            kind = KIND_TAMING, target = "ENTRENAR:FURIA", amount = 5,
            goldReward = goldFor(2), expReward = expFor(2),
            materialReward = "leather", materialQty = 14, tier = 2
        ),
        ContractDef(
            id = "ct_doma_3", title = "Bastión Viviente",
            description = "Entrena la disciplina de Bastión hasta que tu bestia aguante por los dos.",
            kind = KIND_TAMING, target = "ENTRENAR:BASTION", amount = 6,
            goldReward = goldFor(3), expReward = expFor(3),
            materialReward = "steel", materialQty = 10, tier = 3
        ),
        ContractDef(
            id = "ct_doma_4", title = "Banquete Favorito",
            description = "Alimenta a tus bestias con su comida favorita ocho veces.",
            kind = KIND_TAMING, target = "ALIMENTAR_FAVORITA", amount = 8,
            goldReward = goldFor(4), expReward = expFor(4),
            materialReward = "blood_gem", materialQty = 4, tier = 4
        ),
        ContractDef(
            id = "ct_doma_5", title = "Ascensión Bestial",
            description = "Haz evolucionar una mascota hasta su segunda etapa o más allá.",
            kind = KIND_TAMING, target = "EVOLUCIONAR", amount = 1,
            goldReward = goldFor(5), expReward = expFor(5),
            materialReward = "dragon_scale", materialQty = 8, tier = 5
        ),
        ContractDef(
            id = "ct_doma_6", title = "Maestro del Establo",
            description = "Reúne seis bestias distintas en tu santuario al mismo tiempo.",
            kind = KIND_TAMING, target = "ROSTER:6", amount = 6,
            goldReward = goldFor(6), expReward = expFor(6),
            materialReward = "phoenix_feather", materialQty = 3, tier = 6
        )
    )

    private val index: Map<String, ContractDef> = DEFS.associateBy { it.id }

    fun def(id: String): ContractDef? = index[id]

    /** Nivel de contrato recomendado para el nivel de personaje dado. */
    fun tierForLevel(playerLevel: Int): Int = when {
        playerLevel < 40 -> 1
        playerLevel < 90 -> 2
        playerLevel < 150 -> 3
        playerLevel < 220 -> 4
        playerLevel < 320 -> 5
        else -> 6
    }

    /**
     * Tablón determinista: `count` contratos variados centrados en el nivel del jugador,
     * garantizando presencia de los 4 arquetipos siempre que `count >= 4`.
     */
    fun rollBoard(playerLevel: Int, seed: Long, count: Int = 6): List<ContractDef> {
        if (count <= 0) return emptyList()
        val tier = tierForLevel(playerLevel)
        val low = (tier - 1).coerceAtLeast(1)
        val high = (tier + 1).coerceAtMost(6)
        val window = DEFS.filter { it.tier in low..high }.ifEmpty { DEFS }
        val rnd = Random(seed xor 0x00C0_FFEEL)

        val picked = LinkedHashSet<String>()
        val out = ArrayList<ContractDef>(count)

        // 1) Un contrato de cada arquetipo (orden determinista).
        listOf(KIND_HUNT, KIND_EXPEDITION, KIND_GATHER, KIND_TAMING).forEach { kind ->
            if (out.size >= count) return@forEach
            val pool = window.filter { it.kind == kind && it.id !in picked }
            if (pool.isNotEmpty()) {
                val def = pool[rnd.nextInt(pool.size)]
                picked.add(def.id)
                out.add(def)
            }
        }

        // 2) Rellena hasta `count` con el resto de la ventana, y luego con el catálogo entero.
        val fillers = (window.filter { it.id !in picked } + DEFS.filter { it.id !in picked }).distinctBy { it.id }
        val remaining = fillers.toMutableList()
        while (out.size < count && remaining.isNotEmpty()) {
            val def = remaining.removeAt(rnd.nextInt(remaining.size))
            if (picked.add(def.id)) out.add(def)
        }
        return out
    }

    /** Contratos del arquetipo indicado. */
    fun byKind(kind: String): List<ContractDef> = DEFS.filter { it.kind == kind.uppercase() }
}
