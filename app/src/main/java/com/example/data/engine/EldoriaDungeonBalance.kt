package com.example.data.engine

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

// ══════════════════════════════════════════════════════════════════════════════
//  BALANCE DE CALABOZO
//
//  POR QUÉ NO SIRVE EL DEL MUNDO ABIERTO
//  `EldoriaBalance` calibra al enemigo contra el PODER REAL del héroe, y eso
//  está bien fuera: un bicho del camino debe seguir doliendo lleves lo que
//  lleves. Pero aplicado al calabozo produce justo lo contrario de lo que un
//  calabozo es. Si el enemigo crece contigo, el calabozo de nivel 20 pega igual
//  de fuerte cuando vas a nivel 20 mal equipado que cuando vuelves a nivel 90
//  con equipo legendario. Nunca lo superas: sólo lo empatas para siempre. Y el
//  equipo que tanto costó conseguir deja de significar nada.
//
//  CÓMO SE MIDE AQUÍ
//  En ABSOLUTO, contra dos cosas que no dependen de ti:
//
//    · El NIVEL del calabozo (su `levelReq`, de 20 a 400). Fija la vara.
//    · El RANGO del enemigo dentro de él (los nueve subjefes escalan y el jefe
//      final está por encima de todos).
//
//  La vara es el HÉROE DE REFERENCIA: lo que rinde alguien que llega a ese
//  calabozo cuando le toca, con el equipo que le tocaría. Nada de esto mira tu
//  ficha. Consecuencia buscada: si vuelves con veinte niveles de más y equipo
//  mejor, el calabozo NO sube contigo — se vuelve más fácil, que es la razón de
//  volver. Y hay techo: por encima de cierto punto ya no puede pedirte más.
//
//  EL HÉROE COMO FACTOR SUTIL
//  Sólo entra en [heroNudge], acotado a ±15 %, y sólo sobre la vida enemiga.
//  Sirve para que dos partidas no sean idénticas y para que un jugador muy por
//  encima no pase el combate en un turno, pero es un retoque, no el volante.
//
//  DIFICULTAD: MÁS QUE EL MUNDO ABIERTO, PERO CON SUELO Y TECHO
//  Un jefe de calabozo pide casi el doble de turnos que un jefe del mundo y te
//  mata en la mitad. Aun así ningún golpe pasa de [MAX_HIT_FRACTION] de tu vida
//  máxima real: se puede perder, no se puede morir sin verlo venir.
// ══════════════════════════════════════════════════════════════════════════════

object EldoriaDungeonBalance {

    /**
     * Vida del HÉROE DE REFERENCIA en un nivel dado: lo que tendría alguien con
     * el equipo que le corresponde. Sale de la fórmula real del juego
     * (`CON × 30 + nivel × 25 + 120`) con la progresión de atributos y equipo
     * esperada, y coincide con el héroe realista de las pruebas de balance.
     *
     *   nivel  20 →   6 560      nivel 200 →  54 260
     *   nivel 100 →  27 760      nivel 400 → 107 260
     */
    fun referenceHp(level: Int): Double = 265.0 * level.coerceAtLeast(1) + 1_260.0

    /**
     * Daño por turno del héroe de referencia. Es `vida / 12` porque ése es el
     * espejo que fija `EldoriaBalance.MIRROR_TURNS`: un héroe tarda doce turnos
     * en matarse a sí mismo. Medirlo de otra forma descuadraría las dos mitades.
     */
    fun referenceDamagePerTurn(level: Int): Double = referenceHp(level) / 12.0

    /**
     * Defensa del héroe de referencia. Se usa para convertir el daño que el
     * enemigo DEBE hacer en el ataque bruto que lo produce.
     *
     * Es la pieza que hace que la armadura siga valiendo: si el ataque se
     * despejara contra tu defensa real, subir defensa no cambiaría nada — el
     * enemigo compensaría. Despejado contra la referencia, cada punto de
     * armadura por encima de lo esperado se nota de verdad.
     */
    fun referenceDefense(level: Int): Double = 6.8 * level.coerceAtLeast(1) + 26.0

    /** Ningún golpe se lleva más de esta fracción de tu vida máxima real. */
    private const val MAX_HIT_FRACTION = 0.30

    /** Ni menos de ésta: en un calabozo no hay turnos gratis. */
    private const val MIN_HIT_FRACTION = 0.05

    /** Forma del combate de un rango de calabozo. */
    private data class DungeonShape(
        /** Turnos que el héroe de referencia tarda en matarlo. */
        val turnsToKill: Double,
        /** Turnos que tardaría el enemigo en matarlo a él. */
        val turnsToDie: Double,
        /** Fracción del daño del héroe que absorbe su armadura. */
        val armorSoak: Double
    )

    // Comparado con el mundo abierto: allí un jefe legendario pide 12 turnos y
    // te mata en 5. Aquí el jefe final pide 18 y te mata en 3,2 — el calabozo
    // es el sitio donde de verdad se puede perder.
    private val SHAPES: Map<String, DungeonShape> = mapOf(
        "ELITE" to DungeonShape(turnsToKill = 6.0, turnsToDie = 8.0, armorSoak = 0.20),
        "CHAMPION" to DungeonShape(turnsToKill = 9.0, turnsToDie = 6.0, armorSoak = 0.26),
        "LEGENDARY" to DungeonShape(turnsToKill = 13.0, turnsToDie = 4.5, armorSoak = 0.32),
        "UNIVERSAL" to DungeonShape(turnsToKill = 18.0, turnsToDie = 3.2, armorSoak = 0.38)
    )

    /** Rango del enemigo según la planta. Diez plantas, cuatro escalones. */
    fun rarityForStage(stage: Int): String = when {
        stage >= 10 -> "UNIVERSAL"
        stage >= 7 -> "LEGENDARY"
        stage >= 4 -> "CHAMPION"
        else -> "ELITE"
    }

    /**
     * Nivel del enemigo. Depende del calabozo y de la planta, NUNCA del héroe.
     *
     * Antes era `max(levelReq + stage/2, heroLevel + stage/3)`: el segundo
     * término ataba el calabozo al jugador, así que volver con más nivel subía
     * también al enemigo y el calabozo no se superaba jamás.
     */
    fun enemyLevel(dungeonLevelReq: Int, stage: Int): Int =
        (dungeonLevelReq + stage.coerceIn(1, 10)).coerceAtLeast(1)

    /**
     * Rampa dentro del calabozo: la décima planta pesa un 22 % más que la
     * primera dentro de su propio rango, para que bajar se note incluso entre
     * dos subjefes del mismo escalón.
     */
    private fun stageRamp(stage: Int): Double =
        1.0 + (stage.coerceIn(1, 10) - 1) * 0.022

    /**
     * Retoque por el poder real del héroe, acotado a ±15 %.
     *
     * La raíz cuarta es lo que lo mantiene sutil: llegar con el DOBLE de vida de
     * la esperada sube al enemigo un 19 % antes del recorte — o sea, nada al
     * lado de tu ventaja. Es dinamismo, no dificultad adaptativa.
     */
    fun heroNudge(actualHeroHp: Int, level: Int): Double {
        val ratio = (actualHeroHp.coerceAtLeast(1) / referenceHp(level)).coerceIn(0.25, 4.0)
        return ratio.pow(0.25).coerceIn(0.85, 1.15)
    }

    data class DungeonEnemy(val hp: Int, val attack: Int, val defense: Int, val level: Int)

    /**
     * Estadísticas del enemigo de una planta.
     *
     * @param dungeonLevelReq nivel del calabozo (20…400). Es la vara.
     * @param stage planta 1..10; la 10 es el jefe final.
     * @param actualHeroHp vida real del héroe, sólo para [heroNudge] y para
     *        acotar el golpe. No decide la dificultad.
     */
    fun buildEnemy(
        dungeonLevelReq: Int,
        stage: Int,
        actualHeroHp: Int,
        hpMult: Double = 1.0,
        atkMult: Double = 1.0,
        defMult: Double = 1.0
    ): DungeonEnemy {
        val st = stage.coerceIn(1, 10)
        val rarity = rarityForStage(st)
        val shape = SHAPES.getValue(rarity)
        val lvl = enemyLevel(dungeonLevelReq, st)

        val refDpt = referenceDamagePerTurn(dungeonLevelReq)
        val refHp = referenceHp(dungeonLevelReq)
        val refDef = referenceDefense(dungeonLevelReq)

        // ── Defensa: absorbe una fracción fija del daño. Se despeja de la misma
        // curva de rendimientos decrecientes que usa el mundo abierto.
        val soak = (shape.armorSoak * defMult).coerceIn(0.0, 0.55)
        val kRef = 55.0 + 11.0 * dungeonLevelReq
        val defense = (kRef * soak / (1.0 - soak)).roundToInt().coerceAtLeast(0)

        // ── Vida: la que aguanta los turnos previstos frente al héroe de
        // referencia. Se descuenta lo que su propia armadura ya frena, para no
        // cobrar dos veces por la misma defensa.
        val through = EldoriaBalance.damageThrough(defense, dungeonLevelReq, 0.0)
        val hp = refDpt * through * shape.turnsToKill *
            stageRamp(st) * hpMult * heroNudge(actualHeroHp, dungeonLevelReq)

        // ── Ataque: el que vacía al héroe de referencia en los turnos previstos.
        // Se acota contra la vida REAL para que ni un jugador mal equipado muera
        // de un golpe ni uno sobrado pase el combate sin despeinarse.
        val wantedPerTurn = (refHp / shape.turnsToDie) * stageRamp(st) * atkMult
        val capped = wantedPerTurn.coerceIn(
            actualHeroHp * MIN_HIT_FRACTION,
            actualHeroHp * MAX_HIT_FRACTION
        )
        // Despejado contra la defensa de REFERENCIA: ver la nota de
        // [referenceDefense] sobre por qué no se usa la del héroe.
        val refThrough = EldoriaBalance.damageThrough(refDef.roundToInt(), lvl, 0.0)
            .coerceAtLeast(0.01)
        // El enemigo alterna golpe básico y habilidades; el objetivo es la MEDIA
        // del turno, así que se descuenta ese multiplicador medio.
        val attack = (capped / refThrough) / 1.32

        return DungeonEnemy(
            hp = hp.roundToInt().coerceAtLeast(24),
            attack = max(2, attack.roundToInt()),
            defense = defense,
            level = lvl
        )
    }

    /** Turnos que el héroe de referencia tardaría: sirve para las pruebas. */
    fun expectedTurnsToKill(dungeonLevelReq: Int, enemy: DungeonEnemy): Double {
        val through = EldoriaBalance.damageThrough(enemy.defense, dungeonLevelReq, 0.0)
        return enemy.hp / (referenceDamagePerTurn(dungeonLevelReq) * through)
    }
}
