package com.example

import com.example.data.engine.EldoriaBalance
import com.example.data.engine.EldoriaDungeonBalance
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Lo que el calabozo tiene que cumplir, escrito como pruebas.
 *
 * El fallo que motivó este balance: los enemigos de calabozo salían de una
 * fórmula que miraba el nivel del héroe, así que volver más fuerte no servía de
 * nada — el calabozo subía contigo y nunca se superaba.
 */
class EldoriaDungeonBalanceTest {

    private val DUNGEONS = listOf(20, 40, 60, 100, 200, 400)

    /** Vida del héroe con equipo de una rareza dada, sobre la referencia. */
    private fun heroHp(level: Int, gearFactor: Double): Int =
        (EldoriaDungeonBalance.referenceHp(level) * gearFactor).toInt()

    @Test
    fun `el calabozo NO sube con el nivel del heroe`() {
        // Mismo calabozo, dos héroes: el que llega justo y el que vuelve
        // sobrado. El enemigo debe ser practicamente el mismo.
        val justo = EldoriaDungeonBalance.buildEnemy(100, stage = 10, actualHeroHp = heroHp(100, 1.0))
        val sobrado = EldoriaDungeonBalance.buildEnemy(100, stage = 10, actualHeroHp = heroHp(100, 3.0))

        val crecimiento = sobrado.hp.toDouble() / justo.hp
        println("\nCALABOZO 100, JEFE FINAL")
        println("  héroe justo:   ${justo.hp} vida · ${justo.attack} ataque")
        println("  héroe sobrado: ${sobrado.hp} vida · ${sobrado.attack} ataque  (×%.2f)".format(crecimiento))

        assertTrue(
            "con el triple de vida el enemigo creció ×%.2f: el calabozo sigue atado al héroe".format(crecimiento),
            crecimiento <= 1.20
        )
        assertTrue("el nivel del enemigo no debe depender del héroe", justo.level == sobrado.level)
    }

    @Test
    fun `volver con mas nivel lo hace mas facil, y hay techo`() {
        // El coste del jefe en vida, para el mismo calabozo, según con qué
        // llegues. Tiene que bajar de forma monótona.
        val costes = listOf(1.0, 1.5, 2.0, 3.0).map { gear ->
            val hp = heroHp(100, gear)
            val e = EldoriaDungeonBalance.buildEnemy(100, stage = 10, actualHeroHp = hp)
            // Turnos que sobrevives frente a los que necesitas para matarlo.
            val ttd = hp / (e.attack * 1.32 * 0.5)
            val ttk = EldoriaDungeonBalance.expectedTurnsToKill(100, e) / gear
            gear to ttk / ttd
        }
        println("\nCOSTE DEL JEFE DEL CALABOZO 100 SEGÚN CON QUÉ LLEGUES")
        costes.forEach { (gear, cost) ->
            println("  equipo ×%.1f de la referencia -> cuesta %.0f %% de tu vida".format(gear, cost * 100))
        }
        costes.zipWithNext().forEach { (a, b) ->
            assertTrue(
                "llegar mejor equipado (×${b.first}) no lo hizo más fácil que ×${a.first}",
                b.second < a.second
            )
        }
    }

    @Test
    fun `el calabozo es mas duro que el mundo abierto`() {
        // Un jefe de calabozo tiene que pedir más turnos que un jefe del mundo
        // abierto del mismo nivel. Si no, no hay razón para entrar.
        DUNGEONS.forEach { lvl ->
            val jefe = EldoriaDungeonBalance.buildEnemy(lvl, stage = 10, actualHeroHp = heroHp(lvl, 1.0))
            val turnos = EldoriaDungeonBalance.expectedTurnsToKill(lvl, jefe)
            assertTrue(
                "el jefe del calabozo $lvl sólo pide %.1f turnos".format(turnos),
                turnos >= 14.0
            )
        }
    }

    @Test
    fun `duro pero no imposible, ningun golpe te mata de una`() {
        // El suelo de la promesa: se puede perder, no se puede morir sin verlo.
        listOf(0.6, 1.0, 2.0).forEach { gear ->
            DUNGEONS.forEach { lvl ->
                val hp = heroHp(lvl, gear)
                val jefe = EldoriaDungeonBalance.buildEnemy(lvl, stage = 10, actualHeroHp = hp)
                // Golpe real ya mitigado, con la defensa del héroe de referencia.
                val golpe = EldoriaBalance.mitigate(
                    (jefe.attack * 1.7).toInt(),
                    EldoriaDungeonBalance.referenceDefense(lvl).toInt(),
                    jefe.level
                )
                val frac = golpe.toDouble() / hp
                assertTrue(
                    "calabozo $lvl con equipo ×$gear: un golpe se lleva el ${(frac * 100).toInt()} %",
                    frac < 0.55
                )
            }
        }
    }

    @Test
    fun `la dificultad sube planta a planta`() {
        val vidas = (1..10).map {
            EldoriaDungeonBalance.buildEnemy(100, stage = it, actualHeroHp = heroHp(100, 1.0)).hp
        }
        println("\nVIDA POR PLANTA (CALABOZO 100)")
        vidas.forEachIndexed { i, v -> println("  planta ${i + 1}: $v") }
        vidas.zipWithNext().forEach { (a, b) ->
            assertTrue("la vida bajó entre plantas: $a -> $b", b >= a)
        }
        assertTrue("el jefe final debe pesar mucho más que la primera planta",
            vidas.last() > vidas.first() * 2.5)
    }

    @Test
    fun `la escalera de calabozos crece con su nivel`() {
        val jefes = DUNGEONS.map { lvl ->
            lvl to EldoriaDungeonBalance.buildEnemy(lvl, 10, heroHp(lvl, 1.0))
        }
        println("\nESCALERA DE JEFES FINALES")
        jefes.forEach { (lvl, e) ->
            println("  calabozo %3d -> nivel %3d · %8d vida · %6d ataque · %5d defensa"
                .format(lvl, e.level, e.hp, e.attack, e.defense))
        }
        jefes.zipWithNext().forEach { (a, b) ->
            assertTrue("el jefe del calabozo ${b.first} no supera al del ${a.first}",
                b.second.hp > a.second.hp)
        }
    }
}
