package com.example.eldoria.core.content

import com.example.data.KingdomData
import com.example.data.content.KingdomAtlas
import com.example.data.content.KingdomEntry
import com.example.data.content.EldoriaContracts
import com.example.data.model.ContractDef
import kotlin.random.Random

/**
 * ENCARGOS DEL REINO
 *
 * Los contratos del tablón de la taberna son genéricos: valen en cualquier
 * parte del mundo porque apuntan a ARQUETIPOS de bestia (bruto, lanzador…).
 * Estos son lo contrario: sólo tienen sentido donde estás. Apuntan a los
 * monstruos, jefes y biomas concretos del reino que pisas, y por eso se
 * renuevan al cruzar una frontera.
 *
 * Se emiten como [ContractDef] a propósito: así entran por el mismo tablón,
 * el mismo progreso y el mismo cobro que ya existen, sin un sistema paralelo.
 *
 * El progreso llega desde:
 *   · `progressContracts(KIND_HUNT, <nombre del monstruo>)` al matar,
 *   · `progressContracts(KIND_REALM, <id del reino>)` al explorar una casilla.
 */
object KingdomQuestGenerator {

    /** Explorar casillas de un reino concreto. El tablón genérico no lo usa. */
    const val KIND_REALM = "REINO"

    private val huntTemplates = listOf(
        "Las aldeas de %K viven aterradas por %M. Acaba con la plaga.",
        "Un convoy fue asaltado por %M en los caminos de %K. Limpia la ruta.",
        "El alguacil de %K paga por cabeza: trae %N de %M.",
        "Los cazadores de %K exigen trofeos. %N ejemplares de %M deben caer.",
        "Un espíritu de %K susurra: «%M mancilla esta tierra. Purifícala»."
    )

    private val bossTemplates = listOf(
        "Los bardos de %K cantarán tu gesta: desafía a %B en su guarida.",
        "La corona de %K ofrece una recompensa por la cabeza de %B.",
        "El templo de %K profetizó tu llegada: %B debe caer para romper la maldición.",
        "Los caballeros de %K fracasaron contra %B. Sé tú quien lo consiga."
    )

    private val exploreTemplates = listOf(
        "Cartografía %K: recorre %N regiones y reclama su conocimiento.",
        "Un sabio paga por mapas de %K. Visita %N lugares distintos.",
        "La corona ordena reconocer el territorio: %N exploraciones en %K.",
        "Dicen que %K esconde secretos milenarios. Recorre %N rincones."
    )

    /** Nivel de peligro del reino. Delegado en el atlas para no duplicarlo. */
    fun kingdomTier(kingdomId: String): Int = KingdomAtlas.tierOf(kingdomId)

    private fun goldFor(tier: Int, playerLevel: Int): Int =
        ((420 + playerLevel * 46) * tier * 0.85).toInt().coerceAtLeast(120)

    private fun expFor(tier: Int, playerLevel: Int): Int =
        ((260 + playerLevel * 38) * tier * 0.9).toInt().coerceAtLeast(90)

    /** Material temático que paga cada reino, además del oro. */
    private fun materialFor(kingdomId: String): Pair<String, Int> = when (kingdomId) {
        "eldoria" -> "herbs" to 8
        "drakenhold" -> "forge_ember" to 2
        "frostgard" -> "crystal" to 6
        "aethelgard" -> "shadow_essence" to 3
        "solaria" -> "gold_ore" to 5
        else -> "anima_shard" to 2
    }

    private fun fill(
        template: String,
        kingdomName: String,
        monster: String = "",
        boss: String = "",
        count: Int = 0
    ): String = template
        .replace("%K", kingdomName)
        .replace("%M", monster)
        .replace("%B", boss)
        .replace("%N", count.toString())

    /**
     * Tres encargos del reino, deterministas para una semilla dada: el mismo
     * jugador en el mismo reino ve el mismo tablón hasta que lo renueve.
     */
    fun generateContracts(
        entry: KingdomEntry,
        kingdom: KingdomData,
        playerLevel: Int,
        seed: Long,
        count: Int = 3
    ): List<ContractDef> {
        val random = Random(seed)
        val tier = entry.tier
        val shortName = kingdom.name.replace("Reino de ", "").replace("Reino Celestial de ", "")
        val (material, materialQty) = materialFor(entry.id)
        val used = mutableSetOf<String>()
        val out = mutableListOf<ContractDef>()

        repeat(count.coerceIn(1, 6)) { index ->
            val roll = random.nextInt(100)
            val def = when {
                roll < 22 -> bossContract(entry, kingdom, shortName, playerLevel, tier, material, materialQty, random, index)
                roll < 48 -> exploreContract(entry, shortName, playerLevel, tier, material, materialQty, random, index)
                else -> huntContract(entry, kingdom, shortName, playerLevel, tier, material, materialQty, random, index, used)
            }
            out.add(def)
        }
        return out
    }

    private fun huntContract(
        entry: KingdomEntry,
        kingdom: KingdomData,
        shortName: String,
        playerLevel: Int,
        tier: Int,
        material: String,
        materialQty: Int,
        random: Random,
        index: Int,
        used: MutableSet<String>
    ): ContractDef {
        val pool = kingdom.monsters.filter { it !in used }.ifEmpty { kingdom.monsters }
        val monster = pool[random.nextInt(pool.size)]
        used.add(monster)

        val amount = (3 + tier + random.nextInt(0, 4)).coerceIn(3, 14)
        return ContractDef(
            id = "kq_${entry.id}_hunt_${index}_${monster.hashCode().toString(36)}",
            title = "Caza: $monster",
            description = fill(
                huntTemplates[random.nextInt(huntTemplates.size)],
                shortName, monster = monster, count = amount
            ),
            kind = EldoriaContracts.KIND_HUNT,
            // El objetivo es el NOMBRE del monstruo: el combate lo emite al vencer.
            target = monster,
            amount = amount,
            goldReward = goldFor(tier, playerLevel),
            expReward = expFor(tier, playerLevel),
            materialReward = material,
            materialQty = materialQty,
            tier = tier
        )
    }

    private fun bossContract(
        entry: KingdomEntry,
        kingdom: KingdomData,
        shortName: String,
        playerLevel: Int,
        tier: Int,
        material: String,
        materialQty: Int,
        random: Random,
        index: Int
    ): ContractDef {
        val boss = kingdom.bossNames[random.nextInt(kingdom.bossNames.size)]
        return ContractDef(
            id = "kq_${entry.id}_boss_${index}_${boss.hashCode().toString(36)}",
            title = "Gesta: $boss",
            description = fill(
                bossTemplates[random.nextInt(bossTemplates.size)],
                shortName, boss = boss
            ),
            kind = EldoriaContracts.KIND_HUNT,
            target = boss,
            amount = 1,
            goldReward = goldFor(tier, playerLevel) * 2,
            expReward = expFor(tier, playerLevel) * 2,
            materialReward = material,
            materialQty = materialQty * 2,
            tier = (tier + 1).coerceAtMost(6)
        )
    }

    private fun exploreContract(
        entry: KingdomEntry,
        shortName: String,
        playerLevel: Int,
        tier: Int,
        material: String,
        materialQty: Int,
        random: Random,
        index: Int
    ): ContractDef {
        val amount = (4 + random.nextInt(0, 5)).coerceIn(4, 10)
        return ContractDef(
            id = "kq_${entry.id}_scout_${index}_$amount",
            title = "Cartografía de $shortName",
            description = fill(
                exploreTemplates[random.nextInt(exploreTemplates.size)],
                shortName, count = amount
            ),
            kind = KIND_REALM,
            // El objetivo es el id del reino: explorar allí hace avanzar el encargo.
            target = entry.id,
            amount = amount,
            goldReward = (goldFor(tier, playerLevel) * 0.8).toInt(),
            expReward = (expFor(tier, playerLevel) * 0.8).toInt(),
            materialReward = material,
            materialQty = materialQty,
            tier = tier
        )
    }
}
