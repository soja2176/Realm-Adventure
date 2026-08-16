package com.example.data.content

import com.example.data.model.MaterialDef
import kotlin.random.Random

/**
 * Catálogo de materiales (18).
 *
 * Los 15 primeros ids coinciden EXACTAMENTE con los usados por
 * `com.example.eldoria.systems.CraftingRecipes.ALL_RECIPES`:
 * iron, leather, wood, herbs, steel, crystal, mystic_silk, gold_ore, blood_gem,
 * dragon_scale, shadow_essence, phoenix_feather, pure_crystal, ancient_relic, infinite_diamond.
 *
 * Los 3 nuevos son forge_ember, anima_shard y sealed_key.
 */
object EldoriaMaterials {

    val ALL: List<MaterialDef> = listOf(
        // ── Compatibles con CraftingRecipes (15) ──
        MaterialDef(
            id = "iron", name = "Hierro", rarity = "COMÚN",
            description = "Lingote basto de las minas de Eldoria. La base de todo lo que corta.",
            imageResName = "img_mat_hierro", paletteKey = "IRON"
        ),
        MaterialDef(
            id = "leather", name = "Cuero Curtido", rarity = "COMÚN",
            description = "Piel de bestia curtida en corteza de roble. Ligera, silenciosa y barata.",
            imageResName = "img_mat_cuero", paletteKey = "EMBER"
        ),
        MaterialDef(
            id = "wood", name = "Madera de Roble", rarity = "COMÚN",
            description = "Tablón del Bosque Susurrante. Aguanta el golpe y perdona el error.",
            imageResName = "mat_wood", paletteKey = "VITAE"
        ),
        MaterialDef(
            id = "herbs", name = "Hierbas Silvestres", rarity = "COMÚN",
            description = "Manojo de hoja amarga del Valle del Alba. Base de toda poción decente.",
            imageResName = "mat_herbs", paletteKey = "VITAE"
        ),
        MaterialDef(
            id = "steel", name = "Acero Templado", rarity = "RARO",
            description = "Hierro plegado siete veces en la forja de Drakenhold. Canta al golpearlo.",
            imageResName = "img_mat_platino", paletteKey = "SILVER"
        ),
        MaterialDef(
            id = "crystal", name = "Cristal Bruto", rarity = "RARO",
            description = "Fragmento sin pulir de las Nubes de Cristal. Guarda maná como una esponja.",
            imageResName = "mat_crystal", paletteKey = "MANA"
        ),
        MaterialDef(
            id = "mystic_silk", name = "Seda Mística", rarity = "RARO",
            description = "Hilada por arañas de la Arboleda Arcana. Repele los conjuros de bajo nivel.",
            imageResName = "mat_mystic_silk", paletteKey = "ARCANE"
        ),
        MaterialDef(
            id = "gold_ore", name = "Mena de Oro", rarity = "RARO",
            description = "Veta arrancada de las Dunas Doradas. Blanda para el arma, perfecta para el anillo.",
            imageResName = "img_mat_oro", paletteKey = "GOLD"
        ),
        MaterialDef(
            id = "blood_gem", name = "Gema de Sangre", rarity = "ÉPICO",
            description = "Rubí que late despacio. Se dice que recuerda a quién perteneció la sangre.",
            imageResName = "mat_blood_gem", paletteKey = "BLOOD"
        ),
        MaterialDef(
            id = "dragon_scale", name = "Escama de Dragón", rarity = "ÉPICO",
            description = "Placa desprendida de un wyrm adulto. Ni el fuego ni el hielo la marcan.",
            imageResName = "img_mat_dragondskin", paletteKey = "EMBER"
        ),
        MaterialDef(
            id = "shadow_essence", name = "Esencia de Sombra", rarity = "ÉPICO",
            description = "Sombra condensada de la Ciénaga Maldita. Pesa mucho más de lo que debería.",
            imageResName = "mat_shadow_essence", paletteKey = "ARCANE"
        ),
        MaterialDef(
            id = "phoenix_feather", name = "Pluma de Fénix", rarity = "LEGENDARIO",
            description = "Sigue caliente siglos después de caer. Se usa para devolver lo que ya se perdió.",
            imageResName = "mat_phoenix_feather", paletteKey = "EMBER"
        ),
        MaterialDef(
            id = "pure_crystal", name = "Cristal Puro", rarity = "LEGENDARIO",
            description = "Cristal sin una sola impureza, tallado por la presión del Vértice Umbrío.",
            imageResName = "mat_pure_crystal", paletteKey = "MANA"
        ),
        MaterialDef(
            id = "ancient_relic", name = "Reliquia Antigua", rarity = "LEGENDARIO",
            description = "Pieza de un artefacto anterior a los reinos. Nadie sabe qué hacía la máquina entera.",
            imageResName = "mat_ancient_relic", paletteKey = "GOLD"
        ),
        MaterialDef(
            id = "infinite_diamond", name = "Diamante Infinito", rarity = "ARCANO",
            description = "Su interior no termina nunca. Mirarlo demasiado tiempo cuesta una noche de sueño.",
            imageResName = "img_mat_diamond_inf", paletteKey = "ARCANE"
        ),

        // ── Nuevos (3) ──
        MaterialDef(
            id = "forge_ember", name = "Brasa de Forja", rarity = "ÉPICO",
            description = "Rescoldo robado de la fragua primordial. Mejora la calidad de todo lo que templa.",
            imageResName = "mat_forge_ember", paletteKey = "EMBER"
        ),
        MaterialDef(
            id = "anima_shard", name = "Fragmento de Ánima", rarity = "LEGENDARIO",
            description = "Moneda de las expediciones: astilla de alma cristalizada al morir en el abismo.",
            imageResName = "mat_anima_shard", paletteKey = "ARCANE"
        ),
        MaterialDef(
            id = "sealed_key", name = "Llave Sellada", rarity = "ÉPICO",
            description = "Llave de hierro negro sin dientes visibles. Abre las salas de Puerta Sellada.",
            imageResName = "mat_sealed_key", paletteKey = "IRON"
        )
    )

    private val index: Map<String, MaterialDef> = ALL.associateBy { it.id }

    fun def(id: String): MaterialDef? = index[id]

    fun name(id: String): String = index[id]?.name ?: id

    /**
     * Tabla de botín por tier (1..6): pares (materialId, peso relativo).
     * Los tiers altos conservan algo de material básico para no romper las recetas iniciales.
     */
    fun dropTableFor(tier: Int): List<Pair<String, Int>> = when (tier.coerceIn(1, 6)) {
        1 -> listOf(
            "iron" to 30, "leather" to 26, "wood" to 22, "herbs" to 20,
            "steel" to 6, "crystal" to 4, "forge_ember" to 2, "anima_shard" to 2
        )
        2 -> listOf(
            "iron" to 22, "leather" to 20, "wood" to 14, "herbs" to 16,
            "steel" to 16, "crystal" to 10, "forge_ember" to 6, "gold_ore" to 4,
            "anima_shard" to 3
        )
        3 -> listOf(
            "iron" to 14, "leather" to 12, "herbs" to 12, "steel" to 20,
            "crystal" to 16, "mystic_silk" to 12, "gold_ore" to 8, "forge_ember" to 7,
            "blood_gem" to 4, "anima_shard" to 5, "sealed_key" to 2
        )
        4 -> listOf(
            "steel" to 16, "crystal" to 14, "mystic_silk" to 16, "gold_ore" to 12,
            "blood_gem" to 10, "shadow_essence" to 10, "dragon_scale" to 6,
            "forge_ember" to 8, "anima_shard" to 7, "sealed_key" to 3, "herbs" to 6
        )
        5 -> listOf(
            "gold_ore" to 12, "blood_gem" to 14, "dragon_scale" to 14, "shadow_essence" to 14,
            "mystic_silk" to 10, "phoenix_feather" to 7, "pure_crystal" to 7,
            "forge_ember" to 9, "anima_shard" to 9, "sealed_key" to 4, "ancient_relic" to 3
        )
        else -> listOf(
            "dragon_scale" to 12, "shadow_essence" to 12, "phoenix_feather" to 12,
            "pure_crystal" to 12, "ancient_relic" to 10, "infinite_diamond" to 6,
            "blood_gem" to 8, "forge_ember" to 10, "anima_shard" to 12, "sealed_key" to 6
        )
    }

    /**
     * Sorteo determinista de `count` unidades de material para el tier dado.
     * Devuelve un mapa materialId → cantidad acumulada.
     */
    fun rollDrops(tier: Int, count: Int, seed: Long): Map<String, Int> {
        if (count <= 0) return emptyMap()
        val table = dropTableFor(tier)
        val total = table.sumOf { it.second }
        if (total <= 0) return emptyMap()
        val rnd = Random(seed xor (tier.toLong() * 0x1F3B_47C5L))
        val out = LinkedHashMap<String, Int>()
        repeat(count) {
            var roll = rnd.nextInt(total)
            for ((id, weight) in table) {
                roll -= weight
                if (roll < 0) {
                    out[id] = (out[id] ?: 0) + 1
                    break
                }
            }
        }
        return out
    }

    /** Rareza numérica del material, útil para ordenar el inventario. */
    fun rarityRank(id: String): Int = when (index[id]?.rarity?.uppercase()) {
        "ARCANO" -> 4
        "LEGENDARIO" -> 3
        "ÉPICO" -> 2
        "RARO" -> 1
        else -> 0
    }

    /** Valor de venta orientativo en oro por unidad. */
    fun goldValue(id: String): Int = when (rarityRank(id)) {
        4 -> 12_000
        3 -> 3_500
        2 -> 900
        1 -> 220
        else -> 60
    }
}
