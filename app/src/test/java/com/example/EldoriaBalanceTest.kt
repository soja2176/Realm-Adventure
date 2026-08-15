package com.example

import com.example.data.GameProgress
import com.example.data.Item
import com.example.data.GameJsonParser
import com.example.data.engine.EldoriaBalance
import com.example.data.engine.EldoriaPassives
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas del balance de combate.
 *
 * No comprueban "que compile": comprueban que un combate DURA lo que debe durar
 * y CUESTA lo que debe costar, con el equipo puesto. El fallo que motivó este
 * módulo — un héroe de nivel 7 con buen equipo barriendo a un enemigo de nivel
 * 16 perdiendo el 4 % de la vida — se reproduce aquí como caso de regresión.
 */
class EldoriaBalanceTest {

    private fun gear(dmg: Int = 0, def: Int = 0, str: Int = 0, con: Int = 0): String =
        GameJsonParser.toJson(
            Item(
                id = "t", name = "test", description = "", type = "WEAPON",
                rarity = "ÉPICO", itemLevel = 14,
                strBonus = str, dexBonus = 0, intBonus = 0, conBonus = con,
                dmgBonus = dmg, defBonus = def, hpRegen = 0, imageResName = ""
            )
        )

    /** El héroe del reporte: nivel 7, equipo épico, ~4000 de vida. */
    private fun reportedHero(): GameProgress = GameProgress(
        charName = "Test", charRace = "Humano", charClass = "Guerrero",
        charLevel = 7, statStr = 20, statDex = 15, statInt = 10, statCon = 25,
        maxHp = 4045, currentHp = 4045, maxMp = 400, currentMp = 400,
        equippedWeaponJson = gear(dmg = 42, str = 42),
        equippedArmorJson = gear(def = 42, con = 50),
        equippedShieldJson = gear(def = 42, con = 50)
    )

    /** Mismo nivel, sin equipo: el enemigo debe encogerse con él. */
    private fun nakedHero(): GameProgress = GameProgress(
        charName = "Test", charRace = "Humano", charClass = "Guerrero",
        charLevel = 7, statStr = 20, statDex = 15, statInt = 10, statCon = 25,
        maxHp = 1045, currentHp = 1045, maxMp = 400, currentMp = 400
    )

    private fun report(tag: String, progress: GameProgress) {
        val hero = EldoriaBalance.measureHero(progress)
        println("\n$tag")
        println("  héroe: vida ${hero.maxHp}  def ${hero.defense}  daño/turno ${"%.0f".format(hero.damagePerTurn)}")
        listOf("NORMAL", "ELITE", "CHAMPION", "LEGENDARY").forEach { rarity ->
            val e = EldoriaBalance.buildEnemy(hero, rarity, enemyLevel = 16, isBoss = rarity == "LEGENDARY")
            val ttk = EldoriaBalance.expectedTurnsToKill(hero, e)
            val ttd = EldoriaBalance.expectedTurnsToDie(hero, e)
            println(
                "  %-10s vida %6d  atk %6d  def %4d   TTK %4.1f  TTD %4.1f  coste %3.0f%% de vida"
                    .format(rarity, e.hp, e.attack, e.defense, ttk, ttd, 100 * ttk / ttd)
            )
        }
    }

    @Test
    fun `los comunes se farmean en uno o dos golpes`() {
        val hero = EldoriaBalance.measureHero(reportedHero())
        val enemy = EldoriaBalance.buildEnemy(hero, "NORMAL", enemyLevel = 16)
        val ttk = EldoriaBalance.expectedTurnsToKill(hero, enemy)
        val cost = ttk / EldoriaBalance.expectedTurnsToDie(hero, enemy)

        report("CASO DEL REPORTE (nivel 7, equipo épico)", reportedHero())

        // La basura del camino no cobra peaje. Con el enemigo nueve niveles por
        // encima del héroe puede pedir un segundo golpe; contra uno de su nivel
        // cae del primero.
        assertTrue("un común debe caer rápido, tardó $ttk turnos", ttk <= 2.3)
        assertTrue("un común no debe costar casi vida, costó ${(cost * 100).toInt()} %", cost < 0.15)
    }

    /**
     * El fallo que motivó este cambio: la vida no dependía del nivel, así que un
     * enemigo de nivel 18 podía tener 220 de vida — lo mismo que uno de nivel 5.
     */
    /**
     * Héroe realista de nivel [lvl]: los atributos y el equipo suben con él,
     * que es como progresa una partida de verdad.
     */
    private fun heroOfLevel(lvl: Int): GameProgress {
        val gearLvl = lvl + 3
        val base = gearLvl * 3 // épico
        val con = 20 + lvl * 2
        return GameProgress(
            charName = "N$lvl", charRace = "Humano", charClass = "Guerrero",
            charLevel = lvl, statStr = 15 + lvl * 2, statDex = 12 + lvl, statInt = 10, statCon = con,
            maxHp = (con + base * 2) * 30 + lvl * 25 + 120,
            currentHp = 1, maxMp = 400, currentMp = 400,
            equippedWeaponJson = gear(dmg = base, str = base),
            equippedArmorJson = gear(def = base, con = base),
            equippedShieldJson = gear(def = base, con = base)
        )
    }

    @Test
    fun `la vida del enemigo crece con el nivel del mundo`() {
        // Se avanza por el mundo: el héroe sube y los enemigos de su zona
        // también. Ésta es la curva que el jugador ve de verdad.
        val niveles = listOf(5, 10, 18, 30, 50)
        println("\nVIDA DEL ENEMIGO SEGÚN AVANZA LA PARTIDA")
        println(
            "  %-6s %-7s %-7s %7s %7s %7s   %s".format(
                "nivel", "vidaHé", "dañoHé", "común", "élite", "jefe", "turnos común/élite/jefe"
            )
        )
        val comunes = mutableListOf<Int>()
        niveles.forEach { lvl ->
            val hero = EldoriaBalance.measureHero(heroOfLevel(lvl))
            val c = EldoriaBalance.buildEnemy(hero, "NORMAL", enemyLevel = lvl)
            val e = EldoriaBalance.buildEnemy(hero, "ELITE", enemyLevel = lvl)
            val b = EldoriaBalance.buildEnemy(hero, "LEGENDARY", enemyLevel = lvl, isBoss = true)
            println(
                "  %-6d %-7d %-7.0f %7d %7d %7d   %.1f / %.1f / %.1f".format(
                    lvl, hero.maxHp, hero.damagePerTurn, c.hp, e.hp, b.hp,
                    EldoriaBalance.expectedTurnsToKill(hero, c),
                    EldoriaBalance.expectedTurnsToKill(hero, e),
                    EldoriaBalance.expectedTurnsToKill(hero, b)
                )
            )
            comunes.add(c.hp)
        }
        comunes.zipWithNext().forEach { (a, b) ->
            assertTrue("la vida del común no creció con el nivel: $a → $b", b > a)
        }
        assertTrue(
            "de nivel 5 a 50 la vida sólo pasó de ${comunes.first()} a ${comunes.last()}",
            comunes.last() > comunes.first() * 5
        )
    }

    @Test
    fun `un enemigo muy por encima de tu nivel no es imposible`() {
        // La otra cara: el recorte impide que un bicho 40 niveles por encima
        // se vuelva un muro infinito. Deja de crecer, a propósito.
        val hero = EldoriaBalance.measureHero(heroOfLevel(10))
        val cerca = EldoriaBalance.buildEnemy(hero, "NORMAL", enemyLevel = 14)
        val lejos = EldoriaBalance.buildEnemy(hero, "NORMAL", enemyLevel = 60)
        val turnos = EldoriaBalance.expectedTurnsToKill(hero, lejos)
        println("\nHÉROE NIVEL 10 — común de nivel 14: ${cerca.hp} de vida · de nivel 60: ${lejos.hp}")
        println("  matar al de nivel 60 cuesta %.1f turnos".format(turnos))
        assertTrue("uno muy superior debe pesar más", lejos.hp > cerca.hp)
        assertTrue("pero no debe ser un muro: $turnos turnos", turnos < 6.0)
    }

    @Test
    fun `un jefe pesa mucho mas que un comun del mismo nivel`() {
        val hero = EldoriaBalance.measureHero(reportedHero())
        val common = EldoriaBalance.buildEnemy(hero, "NORMAL", enemyLevel = 18)
        val boss = EldoriaBalance.buildEnemy(hero, "LEGENDARY", enemyLevel = 18, isBoss = true)
        println("\nNIVEL 18 — común ${common.hp} de vida · jefe ${boss.hp} de vida")
        assertTrue("un jefe debe tener mucha más vida que un común de su nivel", boss.hp > common.hp * 3)
    }

    @Test
    fun `elites y campeones son una amenaza real`() {
        val hero = EldoriaBalance.measureHero(reportedHero())
        listOf("ELITE" to 0.55, "CHAMPION" to 0.95).forEach { (rarity, floor) ->
            val e = EldoriaBalance.buildEnemy(hero, rarity, enemyLevel = 16)
            val cost = EldoriaBalance.expectedTurnsToKill(hero, e) /
                EldoriaBalance.expectedTurnsToDie(hero, e)
            assertTrue(
                "$rarity debe costar más del ${(floor * 100).toInt()} %, costó ${(cost * 100).toInt()} %",
                cost > floor
            )
        }
    }

    @Test
    fun `un jefe puede matarte sin pasivas ni pociones`() {
        val hero = EldoriaBalance.measureHero(reportedHero())
        val boss = EldoriaBalance.buildEnemy(hero, "LEGENDARY", enemyLevel = 16, isBoss = true)
        val cost = EldoriaBalance.expectedTurnsToKill(hero, boss) /
            EldoriaBalance.expectedTurnsToDie(hero, boss)
        // Muy por encima del 100 %: el jefe gana la carrera de daño en crudo,
        // y es la respuesta del jugador (pasivas, pociones, habilidades) la que
        // tiene que darle la vuelta.
        assertTrue("un jefe debe ganarte a pelo, costó ${(cost * 100).toInt()} %", cost > 1.5)
    }

    @Test
    fun `ningun golpe suelto puede matarte de dos`() {
        val hero = EldoriaBalance.measureHero(reportedHero())
        val boss = EldoriaBalance.buildEnemy(hero, "LEGENDARY", enemyLevel = 16, isBoss = true)
        // El especial más duro, con su penetración, enfurecido, y encima el tope.
        val raw = ((boss.attack * 1.95 * 1.35).toInt()) + (hero.maxHp * 0.04).toInt()
        val hit = EldoriaBalance.capHit(
            EldoriaBalance.mitigate(raw, hero.defense, 16, EldoriaBalance.armorPenOf("BOSS_FURY")),
            hero.maxHp,
            "LEGENDARY"
        )
        val frac = hit.toDouble() / hero.maxHp
        println("\nGOLPE MÁS DURO DE UN JEFE: $hit (${(frac * 100).toInt()} % de la vida)")
        assertTrue("un golpe se llevó el ${(frac * 100).toInt()} %: mataría de dos", frac <= 0.52)
    }

    @Test
    fun `el equipo no rompe la dificultad`() {
        val rich = EldoriaBalance.measureHero(reportedHero())
        val poor = EldoriaBalance.measureHero(nakedHero())

        report("MISMO NIVEL, SIN EQUIPO", nakedHero())

        val richCost = listOf("NORMAL", "ELITE", "CHAMPION").map { r ->
            val e = EldoriaBalance.buildEnemy(rich, r, 16)
            EldoriaBalance.expectedTurnsToKill(rich, e) / EldoriaBalance.expectedTurnsToDie(rich, e)
        }
        val poorCost = listOf("NORMAL", "ELITE", "CHAMPION").map { r ->
            val e = EldoriaBalance.buildEnemy(poor, r, 16)
            EldoriaBalance.expectedTurnsToKill(poor, e) / EldoriaBalance.expectedTurnsToDie(poor, e)
        }

        // Con la vida anclada al nivel, el equipo SÍ tiene que notarse: para eso
        // se farmea. Lo que no puede pasar es lo del fallo original —que el
        // equipo volviera el combate gratis— ni lo contrario, que sin equipo
        // fuera imposible. Así que se acota la divergencia en vez de exigir que
        // sea nula: el bien equipado siempre lo tiene más fácil, pero no gratis.
        richCost.zip(poorCost).forEach { (r, p) ->
            assertTrue(
                "con equipo (${(r * 100).toInt()} %) debería costar menos que sin él (${(p * 100).toInt()} %)",
                r <= p + 0.02
            )
            assertTrue(
                "el equipo abarata demasiado: ${(r * 100).toInt()} % frente a ${(p * 100).toInt()} %",
                r > p * 0.45
            )
        }
    }

    /**
     * El corazón del diseño nuevo: el jefe pega por encima del héroe y son las
     * PASIVAS las que le dan la vuelta al combate. Se simula turno a turno.
     */
    @Test
    fun `las pasivas legendarias dan la vuelta a un jefe superior`() {
        val progress = reportedHero()
        val hero = EldoriaBalance.measureHero(progress)
        val boss = EldoriaBalance.buildEnemy(hero, "LEGENDARY", enemyLevel = 16, isBoss = true)

        fun simulate(loadout: EldoriaPassives.PassiveLoadout, potions: Int): Boolean {
            var php = hero.maxHp
            var ehp = boss.hp
            var shield = (hero.maxHp * loadout.runeShield).toInt()
            var secondWind = false
            var pots = potions
            var turn = 0
            while (turn < 40) {
                turn++
                // ── turno del héroe ──
                var dmg = hero.damagePerTurn
                dmg *= (1.0 + loadout.risingFury * minOf(turn, 8))
                dmg *= (1.0 + loadout.executioner)
                val dealt = EldoriaBalance.mitigate(dmg.toInt(), boss.defense, hero.level)
                ehp -= dealt
                php = minOf(hero.maxHp, php + (dealt * loadout.lifesteal).toInt())
                if (ehp <= 0) return true

                // ── turno del jefe: el especial más duro que tiene ──
                val pen = EldoriaBalance.armorPenOf("BOSS_FURY") * (if (loadout.aegis > 0) 0.5 else 1.0)
                val raw = (boss.attack * 1.95).toInt() + (hero.maxHp * 0.04).toInt()
                var hit = EldoriaBalance.capHit(
                    (EldoriaBalance.mitigate(raw, hero.defense, boss.level, pen) * (1.0 - loadout.aegis)).toInt(),
                    hero.maxHp, "LEGENDARY"
                )
                if (shield > 0) { val a = minOf(shield, hit); shield -= a; hit -= a }
                php -= hit
                ehp -= (hit * loadout.thorns).toInt()
                if (ehp <= 0) return true
                if (php <= 0 && !secondWind && loadout.secondWind > 0) {
                    php = (hero.maxHp * loadout.secondWind).toInt(); secondWind = true
                }
                if (php <= 0) return false
                if (php < hero.maxHp * 0.4 && pots > 0) {
                    php = minOf(hero.maxHp, php + (hero.maxHp * 0.5).toInt()); pots--
                }
            }
            return false
        }

        val none = EldoriaPassives.PassiveLoadout()
        // Equipo universal completo: dos pasivas por pieza.
        val universal = EldoriaPassives.loadoutOf(
            progress,
            listOf("WEAPON", "SHIELD", "ARMOR", "RING", "WINGS").mapIndexed { i, t ->
                Item(
                    id = "u$i", name = "Universal $i", type = t, rarity = "UNIVERSAL",
                    itemLevel = 20, description = ""
                )
            }
        )

        val bare = simulate(none, potions = 3)
        val armed = simulate(universal, potions = 3)

        println("\nJEFE vs HÉROE — sin pasivas: ${if (bare) "victoria" else "DERROTA"}")
        println("JEFE vs HÉROE — con pasivas universales (${universal.names.size}): ${if (armed) "victoria" else "DERROTA"}")
        println("  espinas ${universal.thorns} · robo ${universal.lifesteal} · égida ${universal.aegis} · escudo ${universal.runeShield}")

        assertTrue("sin pasivas el jefe debería ganarte", !bare)
        assertTrue("con equipo universal deberías poder ganarle", armed)
    }

    @Test
    fun `las pasivas solo aparecen desde legendario`() {
        fun p(rarity: String) = EldoriaPassives.forItem(
            Item(id = "x", name = "Prueba", type = "WEAPON", rarity = rarity, itemLevel = 20, description = "")
        )
        assertTrue("un común no lleva pasiva", p("COMÚN").isEmpty())
        assertTrue("un raro no lleva pasiva", p("RARO").isEmpty())
        assertTrue("un épico no lleva pasiva", p("ÉPICO").isEmpty())
        assertTrue("un legendario lleva una", p("LEGENDARIO").size == 1)
        assertTrue("un universal lleva dos", p("UNIVERSAL").size == 2)

        // Determinismo: el mismo objeto da siempre la misma pasiva, que es lo
        // que permite deducirlas sin guardarlas en la partida.
        val a = p("LEGENDARIO").first().kind
        val b = p("LEGENDARIO").first().kind
        assertTrue("la pasiva debe ser estable para el mismo objeto", a == b)
    }

    @Test
    fun `la armadura nunca da inmunidad y la penetracion importa`() {
        // Defensa absurda: la curva debe seguir dejando pasar daño.
        val through = EldoriaBalance.damageThrough(defense = 100_000, attackerLevel = 16)
        assertTrue("con defensa 100000 pasó $through: no debe ser 0", through > 0.0)

        val normal = EldoriaBalance.mitigate(1000, defense = 400, attackerLevel = 16, armorPen = 0.0)
        val pierce = EldoriaBalance.mitigate(1000, defense = 400, attackerLevel = 16, armorPen = 0.85)
        println("\nMITIGACIÓN con 400 de defensa: golpe normal $normal · perforante $pierce")
        assertTrue("la perforación debe doler más ($pierce vs $normal)", pierce > normal * 1.5)
    }
}
