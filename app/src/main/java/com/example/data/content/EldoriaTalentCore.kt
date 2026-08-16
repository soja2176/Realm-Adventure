package com.example.data.content

// ══════════════════════════════════════════════════════════════════════════════
//  MOTOR DE TALENTOS
//
//  EL PROBLEMA
//  La red tenía nueve talentos y sólo siete hacían algo, porque cada uno estaba
//  leído A MANO en el sitio del combate donde tocaba: `getTalentRank("t_1")`
//  incrustado en la fórmula del daño, `getTalentRank("t_6")` en la mitigación…
//  Con nueve se sostiene. Con cuatrocientos es imposible: cada talento nuevo
//  exigiría encontrar y editar su punto exacto del motor, y los dos que ya no
//  hacían nada demuestran lo fácil que es que uno se quede por el camino.
//
//  LA SOLUCIÓN
//  El talento declara SU efecto; el combate pregunta por el total.
//
//  Cada talento se escribe con un [TalentKind] (qué toca) y una
//  [TalentCondition] (cuándo). Esa pareja es lo que los hace distintos entre sí:
//  "+8 % de daño" y "+8 % de daño contra jefes" son el mismo número y dos
//  talentos completamente diferentes de jugar. El combate no conoce ningún
//  talento por su nombre: pide `loadout.damage(contexto)` y recibe la suma de
//  todo lo que aplica en ese instante.
//
//  Añadir un talento pasa a ser escribir una línea. Y ninguno puede quedarse
//  sin efecto por olvido, porque no hay punto de integración que olvidar.
// ══════════════════════════════════════════════════════════════════════════════

/** Qué toca un talento. */
enum class TalentKind {
    // ─── Ofensiva ───
    DANO_FISICO, DANO_MAGICO, DANO_TOTAL, DANO_HABILIDAD, DANO_BASICO,
    CRIT_PROB, CRIT_MULT, PENETRACION,
    // ─── Defensa ───
    VIDA_MAX, ARMADURA, REDUCCION_DANO, ESQUIVA, ESPINAS, ESCUDO_INICIAL,
    // ─── Sustento ───
    ROBO_VIDA, ROBO_MANA, REGEN_VIDA_TURNO, REGEN_MANA_TURNO,
    MANA_MAX, COSTE_MANA,
    // ─── Pociones ───
    POCION_POTENCIA, POCION_DURACION, POCION_AHORRO,
    // ─── Mascota ───
    DANO_MASCOTA, VIDA_MASCOTA,
    // ─── Ímpetu y rachas ───
    IMPETU_GANANCIA, FURIA_CRECIENTE,
    // ─── Botín ───
    ORO, EXP, RAREZA_BOTIN,
    // ─── Únicos (se comprueban por su cuenta) ───
    PRIMER_GOLPE_CRITICO, ULTIMO_ALIENTO, SEGUNDA_ACCION, ANTI_CURACION_EXTRA
}

/**
 * Cuándo aplica. Es la mitad que da variedad real: el mismo [TalentKind] con
 * dos condiciones distintas son dos decisiones distintas para el jugador.
 */
enum class TalentCondition {
    SIEMPRE,
    /** Contra ÉLITE, CAMPEÓN, LEGENDARIO o jefe. */
    CONTRA_GRANDES,
    /** Por debajo del 35 % de vida. */
    VIDA_BAJA,
    /** Por encima del 80 % de vida. */
    VIDA_ALTA,
    /** Sólo el primer turno del combate. */
    PRIMER_TURNO,
    /** A partir del quinto turno. */
    COMBATE_LARGO,
    /** Con mascota equipada. */
    CON_MASCOTA,
    /** Dentro de un calabozo. */
    EN_CALABOZO,
    /** Con algún efecto de poción activo. */
    CON_POCION_ACTIVA
}

/** Rama del árbol. Cada raza reparte sus talentos entre estas sendas. */
enum class TalentBranch(val display: String) {
    ARMAS("Armas"),
    DEFENSA("Defensa"),
    ARCANO("Arcano"),
    SOMBRA("Sombra"),
    SANGRE("Sangre"),
    FORTUNA("Fortuna"),
    BESTIA("Bestia"),
    LEGADO("Legado")
}

/**
 * El efecto de un talento, por rango.
 *
 * [magnitudePerRank] se multiplica por el rango invertido, así que un talento
 * de rango 3 con 0,02 da 0,06 al máximo. Se guarda por rango y no en total
 * para que la descripción que lee el jugador ("+2 % por rango") y el número que
 * aplica el motor salgan del MISMO sitio y no puedan desmentirse.
 */
data class TalentEffect(
    val kind: TalentKind,
    val magnitudePerRank: Double,
    val condition: TalentCondition = TalentCondition.SIEMPRE
)

/**
 * Definición de un talento.
 *
 * @param evolutionTier 0 = disponible desde el principio. 1, 2 y 3 son los
 *        talentos exclusivos de las evoluciones de raza, que se desbloquean en
 *        los niveles 20, 50 y 100 — los mismos umbrales en los que las razas ya
 *        ganaban sus bonos, para no inventar una segunda escalera paralela.
 */
data class TalentDef(
    val id: String,
    val name: String,
    val description: String,
    val race: String,
    val branch: TalentBranch,
    val tier: Int,
    val maxRank: Int,
    val effect: TalentEffect,
    val prerequisiteId: String? = null,
    val evolutionTier: Int = 0
) {
    /** Clave de su lámina: un icono por talento. */
    val artKey: String get() = "talent_$id"
}

/** Contexto del instante de combate, para resolver las condiciones. */
data class TalentContext(
    val hpFraction: Double = 1.0,
    val turn: Int = 1,
    val againstBigTarget: Boolean = false,
    val hasPet: Boolean = false,
    val inDungeon: Boolean = false,
    val potionActive: Boolean = false
) {
    fun matches(condition: TalentCondition): Boolean = when (condition) {
        TalentCondition.SIEMPRE -> true
        TalentCondition.CONTRA_GRANDES -> againstBigTarget
        TalentCondition.VIDA_BAJA -> hpFraction < 0.35
        TalentCondition.VIDA_ALTA -> hpFraction > 0.80
        TalentCondition.PRIMER_TURNO -> turn <= 1
        TalentCondition.COMBATE_LARGO -> turn >= 5
        TalentCondition.CON_MASCOTA -> hasPet
        TalentCondition.EN_CALABOZO -> inDungeon
        TalentCondition.CON_POCION_ACTIVA -> potionActive
    }
}

/**
 * Lo que el jugador tiene invertido, ya resuelto.
 *
 * El combate NO recorre talentos: pregunta por un [TalentKind] y recibe el
 * total que aplica en ese contexto. Así el motor no sabe cuántos talentos hay
 * ni cómo se llaman, y pasar de nueve a cuatrocientos no le afecta.
 */
class TalentLoadout(private val entries: List<Pair<TalentEffect, Int>>) {

    /** Total de un tipo de efecto para un contexto dado. */
    fun value(kind: TalentKind, ctx: TalentContext = TalentContext()): Double =
        entries.asSequence()
            .filter { (effect, rank) -> effect.kind == kind && rank > 0 && ctx.matches(effect.condition) }
            .sumOf { (effect, rank) -> effect.magnitudePerRank * rank }

    /** ¿Hay algún rango invertido en un efecto de tipo único? */
    fun has(kind: TalentKind, ctx: TalentContext = TalentContext()): Boolean =
        value(kind, ctx) > 0.0

    val isEmpty: Boolean get() = entries.none { it.second > 0 }

    companion object {
        val EMPTY = TalentLoadout(emptyList())
    }
}

object EldoriaTalentEngine {

    /**
     * Umbrales de evolución de raza. Coinciden con los niveles en los que las
     * razas ya ganaban bonos (20, 50 y 100), de modo que "evolucionar" nombra
     * algo que el juego ya hacía en vez de añadir una escalera nueva.
     */
    fun evolutionTierFor(level: Int): Int = when {
        level >= 100 -> 3
        level >= 50 -> 2
        level >= 20 -> 1
        else -> 0
    }

    fun evolutionName(race: String, tier: Int): String = when (race) {
        "Humano" -> listOf("Humano", "Cruzado", "Abanderado", "Heraldo Eterno")[tier]
        "Elfo" -> listOf("Elfo", "Guardián del Alba", "Cantor Estelar", "Arconte Silvano")[tier]
        "Enano" -> listOf("Enano", "Rompeyunques", "Señor de la Forja", "Titán de Piedra")[tier]
        "Orco" -> listOf("Orco", "Destripador", "Caudillo", "Devorador de Hordas")[tier]
        else -> "Aventurero"
    }

    /** Construye el loadout a partir de los rangos invertidos. */
    fun loadout(defs: List<TalentDef>, ranks: Map<String, Int>): TalentLoadout {
        val entries = defs.mapNotNull { def ->
            val rank = ranks[def.id] ?: 0
            if (rank <= 0) null else def.effect to rank
        }
        return TalentLoadout(entries)
    }
}
