package com.example.data.engine

import com.example.data.GameProgress
import com.example.data.Item
import kotlin.math.roundToInt

// ══════════════════════════════════════════════════════════════════════════════
//  PASIVAS DE OBJETO
//
//  A partir de LEGENDARIO, una pieza deja de ser un montón de números y pasa a
//  cambiar cómo se pelea. Son las pasivas las que permiten que los enemigos
//  peguen más fuerte que tú sin que el combate sea injusto: el jefe golpea por
//  encima de tus posibilidades y tú respondes con espinas, robo de vida, un
//  escudo que absorbe el primer golpe o un segundo aliento que te salva una vez.
//
//  NO SE GUARDAN EN LA PARTIDA.
//  La pasiva se DEDUCE del objeto (su id, su rareza y su tipo) de forma
//  determinista: el mismo objeto da siempre la misma pasiva. Así el equipo que
//  el jugador ya tiene guardado gana pasivas sin migrar la base de datos y sin
//  tocar el JSON de inventario.
// ══════════════════════════════════════════════════════════════════════════════

/** Qué hace una pasiva. El motor de combate consulta estos identificadores. */
enum class PassiveKind {
    /** Devuelve parte del daño recibido al atacante. */
    THORNS,
    /** Cura una parte del daño infligido. */
    LIFESTEAL,
    /** Reduce todo el daño recibido y, sobre todo, frena la penetración de armadura. */
    AEGIS,
    /** Una vez por combate, sobrevives al golpe letal con algo de vida. */
    SECOND_WIND,
    /** El daño crece con cada turno: premia aguantar peleas largas. */
    RISING_FURY,
    /** Daño extra contra jefes y enemigos con mucha vida. */
    EXECUTIONER,
    /** Devuelve maná al golpear: permite seguir lanzando habilidades. */
    MANA_LEECH,
    /** Absorbe una bolsa fija de daño al empezar el combate. */
    RUNE_SHIELD
}

/** Una pasiva concreta, ya con su magnitud resuelta. */
data class ItemPassive(
    val kind: PassiveKind,
    val name: String,
    /** Magnitud: fracción (0..1) o valor absoluto según la pasiva. */
    val power: Double,
    val description: String,
    /** Objeto del que sale, para poder decir de dónde viene en el registro. */
    val sourceItem: String = ""
)

object EldoriaPassives {

    /** Rarezas que llevan pasiva. Por debajo de legendario, un objeto es números. */
    private const val T_LEGENDARY = 3
    private const val T_ARCANE = 4
    private const val T_UNIVERSAL = 5

    private fun tierOf(rarity: String): Int = when (rarity.uppercase()) {
        "UNIVERSAL" -> T_UNIVERSAL
        "ARCANO", "ARCANE" -> T_ARCANE
        "LEGENDARIO", "LEGENDARY" -> T_LEGENDARY
        else -> 0
    }

    /**
     * Pasivas de un objeto. Vacío si no llega a legendario.
     * Un UNIVERSAL lleva dos: es la recompensa de los jefes de calabozo.
     */
    fun forItem(item: Item): List<ItemPassive> {
        val tier = tierOf(item.rarity)
        if (tier < T_LEGENDARY) return emptyList()

        // Semilla estable: el mismo objeto da siempre la misma pasiva.
        val seed = (item.id.hashCode().toLong() * 31L) xor (item.name.hashCode().toLong())
        val pool = poolFor(item.type)
        val first = pool[(Math.floorMod(seed, pool.size.toLong())).toInt()]

        val out = mutableListOf(build(first, tier, item))
        if (tier >= T_UNIVERSAL) {
            // La segunda nunca repite la primera.
            val alt = pool[(Math.floorMod(seed / 7 + 3, pool.size.toLong())).toInt()]
            val second = if (alt == first) pool[(pool.indexOf(first) + 1) % pool.size] else alt
            out.add(build(second, tier, item))
        }
        return out
    }

    /**
     * El tipo de pieza decide qué pasivas puede llevar: el escudo defiende, el
     * arma castiga. Que un báculo diera espinas rompería la lectura del equipo.
     */
    private fun poolFor(type: String): List<PassiveKind> = when (type.uppercase()) {
        "WEAPON" -> listOf(PassiveKind.LIFESTEAL, PassiveKind.EXECUTIONER, PassiveKind.RISING_FURY, PassiveKind.MANA_LEECH)
        "SHIELD" -> listOf(PassiveKind.THORNS, PassiveKind.AEGIS, PassiveKind.RUNE_SHIELD)
        "ARMOR", "HELMET", "BOOTS", "GLOVES" -> listOf(PassiveKind.AEGIS, PassiveKind.THORNS, PassiveKind.SECOND_WIND)
        "RING", "EARRING", "RELIC" -> listOf(PassiveKind.MANA_LEECH, PassiveKind.LIFESTEAL, PassiveKind.SECOND_WIND, PassiveKind.EXECUTIONER)
        "WINGS" -> listOf(PassiveKind.RUNE_SHIELD, PassiveKind.RISING_FURY, PassiveKind.AEGIS)
        else -> listOf(PassiveKind.LIFESTEAL, PassiveKind.AEGIS)
    }

    /** Magnitud por rareza. Escala con contención: dos piezas no deben trivializar. */
    private fun build(kind: PassiveKind, tier: Int, item: Item): ItemPassive {
        val step = when (tier) {
            T_UNIVERSAL -> 3
            T_ARCANE -> 2
            else -> 1
        }
        return when (kind) {
            PassiveKind.THORNS -> ItemPassive(
                kind, "Espinas de Hierro", 0.10 * step,
                "Devuelve el ${pct(0.10 * step)} del daño recibido al atacante.", item.name
            )
            PassiveKind.LIFESTEAL -> ItemPassive(
                kind, "Sed de Sangre", 0.07 * step,
                "Te cura el ${pct(0.07 * step)} del daño que infliges.", item.name
            )
            PassiveKind.AEGIS -> ItemPassive(
                kind, "Égida Rúnica", 0.07 * step,
                "Reduce el daño recibido un ${pct(0.07 * step)} y recorta a la mitad la penetración de armadura enemiga.",
                item.name
            )
            PassiveKind.SECOND_WIND -> ItemPassive(
                kind, "Segundo Aliento", 0.12 * step,
                "Una vez por combate sobrevives al golpe mortal con el ${pct(0.12 * step)} de tu vida.",
                item.name
            )
            PassiveKind.RISING_FURY -> ItemPassive(
                kind, "Furia Creciente", 0.04 * step,
                "Tu daño sube un ${pct(0.04 * step)} por turno de combate, hasta ocho turnos.",
                item.name
            )
            PassiveKind.EXECUTIONER -> ItemPassive(
                kind, "Verdugo", 0.12 * step,
                "Inflige un ${pct(0.12 * step)} más de daño a jefes y campeones.", item.name
            )
            PassiveKind.MANA_LEECH -> ItemPassive(
                kind, "Sanguijuela Arcana", 0.05 * step,
                "Recuperas maná igual al ${pct(0.05 * step)} del daño infligido.", item.name
            )
            PassiveKind.RUNE_SHIELD -> ItemPassive(
                kind, "Escudo Rúnico", 0.10 * step,
                "Empiezas cada combate con un escudo que absorbe el ${pct(0.10 * step)} de tu vida máxima.",
                item.name
            )
        }
    }

    private fun pct(v: Double): String = "${(v * 100).roundToInt()} %"

    // ═══════════════════════════════════════════════════════════════════════
    //  AGREGADO PARA EL COMBATE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Suma de todas las pasivas del equipo puesto. El combate consulta esto una
     * vez al empezar en vez de recorrer el inventario en cada golpe.
     */
    data class PassiveLoadout(
        val thorns: Double = 0.0,
        val lifesteal: Double = 0.0,
        val aegis: Double = 0.0,
        val secondWind: Double = 0.0,
        val risingFury: Double = 0.0,
        val executioner: Double = 0.0,
        val manaLeech: Double = 0.0,
        val runeShield: Double = 0.0,
        val names: List<String> = emptyList()
    ) {
        val hasAny: Boolean get() = names.isNotEmpty()
    }

    /**
     * Lee el equipo del héroe y agrega sus pasivas.
     *
     * Los porcentajes se suman pero con techo: sin él, un jugador con seis
     * piezas universales se volvía intocable y volveríamos al problema de
     * partida, sólo que por el otro lado.
     */
    fun loadoutOf(progress: GameProgress, equipped: List<Item>): PassiveLoadout {
        var thorns = 0.0; var lifesteal = 0.0; var aegis = 0.0; var secondWind = 0.0
        var fury = 0.0; var exec = 0.0; var mana = 0.0; var shield = 0.0
        val names = mutableListOf<String>()

        equipped.forEach { item ->
            forItem(item).forEach { p ->
                when (p.kind) {
                    PassiveKind.THORNS -> thorns += p.power
                    PassiveKind.LIFESTEAL -> lifesteal += p.power
                    PassiveKind.AEGIS -> aegis += p.power
                    PassiveKind.SECOND_WIND -> secondWind = maxOf(secondWind, p.power)
                    PassiveKind.RISING_FURY -> fury += p.power
                    PassiveKind.EXECUTIONER -> exec += p.power
                    PassiveKind.MANA_LEECH -> mana += p.power
                    PassiveKind.RUNE_SHIELD -> shield += p.power
                }
                names.add(p.name)
            }
        }

        return PassiveLoadout(
            thorns = thorns.coerceAtMost(0.60),
            lifesteal = lifesteal.coerceAtMost(0.35),
            aegis = aegis.coerceAtMost(0.40),
            secondWind = secondWind.coerceAtMost(0.40),
            risingFury = fury.coerceAtMost(0.15),
            executioner = exec.coerceAtMost(0.60),
            manaLeech = mana.coerceAtMost(0.30),
            runeShield = shield.coerceAtMost(0.35),
            names = names.distinct()
        )
    }
}
