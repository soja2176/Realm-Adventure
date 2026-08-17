package com.example.data.engine

import com.example.data.GameProgress
import com.example.data.GameJsonParser
import com.example.data.Item
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// ══════════════════════════════════════════════════════════════════════════════
//  BALANCE DE COMBATE
//
//  EL PROBLEMA QUE RESUELVE
//  El poder del héroe viene sobre todo del EQUIPO, no del nivel: cada punto de
//  CON del equipo vale 30 de vida, y el daño del arma se suma entero. El enemigo,
//  en cambio, se generaba sólo a partir de su nivel. Resultado medido con las
//  fórmulas reales: un héroe de nivel 7 con equipo épico tenía 4045 de vida y
//  pegaba 98 por turno; un enemigo de nivel 16 tenía 881 de vida y pegaba 19.
//  Lo mataba en 9 turnos y perdía el 4 % de la vida. No había combate.
//
//  LA SEGUNDA MITAD DEL PROBLEMA: DOS ESCALAS QUE NO SE HABLAN
//  El ancla al nivel arreglaba la forma de la curva pero no su escala. La vida
//  del héroe es `CON × 30`, y hay objetos con 350 de CON: a nivel 12 se llega a
//  12 000 de vida. Su DAÑO, en cambio, es lineal (`FUE × 0,6 + arma`): unos 250
//  por turno. Un héroe tardaría CINCUENTA turnos en matarse a sí mismo, así que
//  cualquier enemigo que dure 2-3 turnos tiene que tener ~500 de vida — de ahí
//  el "nivel 12 con 200 de vida" al lado de un héroe con 12 000. El número no
//  estaba mal calculado: estaba medido en otra unidad.
//
//  LA SOLUCIÓN: TODO SE MIDE CONTRA LA MISMA REGLA — LA VIDA DEL HÉROE
//
//  · El DAÑO del héroe se reescala (`outputScale`) para que un espejo de sí
//    mismo cayera en [MIRROR_TURNS] turnos. Es un factor, no una fórmula nueva:
//    el equipo, los atributos y los críticos siguen decidiendo quién pega más;
//    sólo se corrige que el daño creciera en centenas mientras la vida crecía
//    en decenas de miles. Nunca reduce el daño, sólo lo pone a escala.
//
//  · La VIDA del enemigo es una FRACCIÓN DE LA TUYA (`heroHpShare`), corregida
//    por la diferencia de nivel: un común ronda un quinto de tu vida, un jefe la
//    supera. Así los dos números de la pantalla de combate son comparables, que
//    es lo único que el jugador puede juzgar de un vistazo.
//
//  · El ATAQUE del enemigo sale del PODER REAL del héroe, equipo incluido. Es
//    lo que arregla el fallo de partida: el daño que recibes tiene que medirse
//    contra lo que aguantas, no contra un nivel que no sabe qué llevas puesto.
//
//  Los objetivos por rareza se expresan en turnos:
//    · TTK (turns-to-kill)  — cuántos turnos tarda el héroe en matarlo.
//    · TTD (turns-to-die)   — cuántos turnos tardaría el enemigo en matarlo a él.
//  Y su proporción TTK/TTD es el coste del combate en vida: un común sale casi
//  gratis, un jefe pide más vida de la que tienes — y ahí es donde entran las
//  pasivas del equipo legendario y las pociones.
//
//  Y AL REVÉS: NADIE ES INVENCIBLE
//  El daño por turno está acotado por arriba (nunca más del 30–36 % de la vida
//  máxima: no hay muertes de un golpe) y por abajo (nunca menos del 4 %: ningún
//  combate es gratis).
//
//  MITIGACIÓN
//  La fórmula vieja restaba plano (`daño − defensa/2`), lo que permitía llegar
//  a la inmunidad acumulando defensa. Aquí la defensa tiene rendimientos
//  decrecientes: `K/(K+def)`, que se acerca a cero pero no lo alcanza. Eso
//  además da sentido a la PENETRACIÓN DE ARMADURA, que ahora es un porcentaje
//  del golpe que ignora la curva en vez de un interruptor de todo o nada.
// ══════════════════════════════════════════════════════════════════════════════

/** Lo que el héroe rinde de verdad en combate, con el equipo puesto. */
data class HeroPower(
    /**
     * Daño esperado por turno YA REESCALADO: golpe básico + mascota + esperanza
     * de crítico, multiplicado por [outputScale]. Es el número con el que hay
     * que calibrar al enemigo, porque es el que el combate va a producir.
     */
    val damagePerTurn: Double,
    /** Vida máxima real. */
    val maxHp: Int,
    /** Defensa efectiva que se opone al golpe enemigo. */
    val defense: Int,
    /** Nivel del héroe: sólo se usa como suelo, no como medida de poder. */
    val level: Int,
    /**
     * Factor que el motor de combate debe aplicar a TODO el daño del héroe
     * (básico, habilidad y mascota) para que su ofensiva viva en la misma
     * escala que su vida. Nunca baja de 1,0: corrige hacia arriba o no corrige.
     */
    val outputScale: Double = 1.0,
    /** Daño por turno tal y como sale de las fórmulas, sin reescalar. */
    val rawDamagePerTurn: Double = damagePerTurn
)

/** Objetivos de duración de un combate, por rareza del enemigo. */
private data class CombatShape(
    /** Turnos que el héroe debe tardar en matarlo. */
    val turnsToKill: Double,
    /** Turnos que el enemigo debe tardar en matar al héroe. */
    val turnsToDie: Double,
    /** Tope de daño por turno como fracción de la vida máxima del héroe. */
    val maxHitFraction: Double,
    /**
     * Suelo de daño por turno. Los comunes lo tienen más bajo que el resto:
     * ahora cuestan dos o tres turnos, y con el suelo general del 4 % farmear
     * habría costado más de un 10 % de vida por bicho.
     */
    val minHitFraction: Double,
    /** Fracción del daño del héroe que absorbe la armadura del enemigo. */
    val armorSoak: Double,
    /**
     * Vida de esta rareza como FRACCIÓN DE LA VIDA DEL HÉROE, a igualdad de
     * nivel. Es lo que hace que los dos números de la pantalla de combate se
     * puedan comparar: un común ronda un quinto de tu vida, un jefe la supera.
     * La diferencia de nivel lo corrige después ([levelFactor]).
     */
    val heroHpShare: Double,
    /** Banda de turnos aceptable. La vida anclada al nivel se recorta a ella. */
    val minTurns: Double,
    val maxTurns: Double
)

object EldoriaBalance {

    // ─── Objetivos de diseño ───
    // Coste en vida del combate = turnsToKill / turnsToDie.
    private val SHAPES: Map<String, CombatShape> = mapOf(
        // Los comunes ya no caen de un golpe: cuestan dos o tres turnos, lo justo
        // para que su barra de vida signifique algo. Siguen siendo baratos en
        // vida (`minHitFraction` bajo): el farmeo no puede arruinarte.
        "NORMAL" to CombatShape(
            turnsToKill = 2.5, turnsToDie = 30.0,
            maxHitFraction = 0.05, minHitFraction = 0.015, armorSoak = 0.08,
            heroHpShare = 0.22, minTurns = 2.0, maxTurns = 3.2
        ),
        // Aquí empieza el juego de verdad: cuesta gran parte de la vida.
        "ELITE" to CombatShape(
            turnsToKill = 6.0, turnsToDie = 6.5,
            maxHitFraction = 0.24, minHitFraction = 0.04, armorSoak = 0.22,
            heroHpShare = 0.55, minTurns = 5.0, maxTurns = 7.0
        ),
        // Por encima de ti: sin pasivas ni pociones, no sales.
        "CHAMPION" to CombatShape(
            turnsToKill = 8.5, turnsToDie = 5.5,
            maxHitFraction = 0.30, minHitFraction = 0.04, armorSoak = 0.26,
            heroHpShare = 0.80, minTurns = 7.0, maxTurns = 10.0
        ),
        // Un jefe DEBE poder matarte. Las pasivas son las que te dan la vuelta.
        "LEGENDARY" to CombatShape(
            turnsToKill = 12.0, turnsToDie = 5.0,
            maxHitFraction = 0.34, minHitFraction = 0.04, armorSoak = 0.30,
            heroHpShare = 1.15, minTurns = 10.0, maxTurns = 14.0
        ),
        // El jefe de calabozo: lo más duro del juego.
        "UNIVERSAL" to CombatShape(
            turnsToKill = 14.5, turnsToDie = 4.5,
            maxHitFraction = 0.36, minHitFraction = 0.04, armorSoak = 0.32,
            heroHpShare = 1.45, minTurns = 12.0, maxTurns = 17.0
        )
    )

    private val DEFAULT_SHAPE = SHAPES.getValue("NORMAL")

    /**
     * Turnos que tardaría el héroe en matar a un espejo exacto de sí mismo.
     * Es la regla que pone en la misma escala su vida y su daño: `outputScale`
     * es justo el factor que hace cierta esta igualdad.
     */
    private const val MIRROR_TURNS = 12.0

    /**
     * Tope del reescalado. Sin él, un personaje con muchísimo CON y un arma de
     * madera se convertiría en un cañón por el mero hecho de tener vida.
     */
    private const val MAX_OUTPUT_SCALE = 15.0

    /**
     * El enemigo no pega siempre igual: alterna golpe básico y habilidades, que
     * multiplican el daño. `buildEnemy` calibra el ataque para que la MEDIA del
     * turno caiga en el objetivo, así que hay que descontar ese multiplicador
     * medio; si no, cada especial se saldría de la curva.
     * ≈ 0,55 × 1,0 (básico) + 0,45 × 1,7 (habilidad).
     */
    private const val SKILL_AVERAGE_MULT = 1.32

    /**
     * Lo mismo para el héroe: reparte sus turnos entre golpe básico y
     * habilidades de clase (×1,3 a ×2,2). Sin esto se le medía sólo el básico.
     */
    private const val HERO_SKILL_AVERAGE_MULT = 1.30

    // ─── Curva de mitigación ───
    // `K` marca cuánta defensa hace falta para mitigar la mitad del golpe.
    // Crece con el nivel del atacante: la armadura vieja envejece.
    private fun defenseConstant(attackerLevel: Int): Double =
        55.0 + 11.0 * attackerLevel.coerceAtLeast(1)

    /**
     * Corrector de la vida enemiga por diferencia de nivel. A igualdad de nivel
     * vale 1: el enemigo pesa exactamente la fracción que le toca por rareza.
     * Por encima pesa más y por debajo menos, pero con exponente < 1 y acotado,
     * para que un bicho muy superior sea duro sin volverse un muro infinito.
     *
     *   héroe nivel 12 →  enemigo  6 → 0,56    enemigo 24 → 1,80
     *                     enemigo 12 → 1,00    enemigo 60 → 2,20 (tope)
     */
    fun levelFactor(enemyLevel: Int, heroLevel: Int): Double {
        val e = enemyLevel.coerceAtLeast(1).toDouble()
        val h = heroLevel.coerceAtLeast(1).toDouble()
        return Math.pow(e / h, 0.85).coerceIn(0.45, 2.2)
    }

    /**
     * Factor de corrección de la ofensiva del héroe. Se despeja de [MIRROR_TURNS]:
     * el daño que produce de verdad debe vaciar su propia vida en esos turnos.
     * Sólo corrige hacia arriba — si alguien ya pega en su escala, se le deja.
     */
    fun outputScaleFor(rawDamagePerTurn: Double, maxHp: Int): Double {
        val target = maxHp.coerceAtLeast(1) / MIRROR_TURNS
        val raw = rawDamagePerTurn.coerceAtLeast(1.0)
        return (target / raw).coerceIn(1.0, MAX_OUTPUT_SCALE)
    }

    /** Aplica [HeroPower.outputScale] a un golpe del héroe. */
    fun scaleHeroDamage(damage: Int, scale: Double): Int =
        max(1, (damage * scale).roundToInt())

    /**
     * Fracción del golpe que ATRAVIESA la defensa, entre 0 y 1.
     * Nunca llega a 0: con defensa infinita sigue pasando algo.
     */
    fun damageThrough(defense: Int, attackerLevel: Int, armorPen: Double = 0.0): Double {
        val k = defenseConstant(attackerLevel)
        val d = defense.coerceAtLeast(0).toDouble()
        val pen = armorPen.coerceIn(0.0, 1.0)
        val soaked = k / (k + d)          // parte que la armadura sí frena
        return (1.0 - pen) * soaked + pen // la penetración se salta la curva
    }

    /** Aplica la mitigación a un golpe. Siempre deja pasar al menos 1. */
    fun mitigate(rawDamage: Int, defense: Int, attackerLevel: Int, armorPen: Double = 0.0): Int {
        if (rawDamage <= 0) return 0
        val through = damageThrough(defense, attackerLevel, armorPen)
        return max(1, (rawDamage * through).roundToInt())
    }

    /**
     * Ataque bruto necesario para que, tras la mitigación del héroe, el golpe
     * acabe haciendo [wantedDamage]. Es la inversa de [mitigate].
     */
    private fun rawAttackFor(
        wantedDamage: Double,
        defense: Int,
        attackerLevel: Int,
        armorPen: Double
    ): Int {
        val through = damageThrough(defense, attackerLevel, armorPen).coerceAtLeast(0.01)
        return max(1, (wantedDamage / through).roundToInt())
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  MEDIR AL HÉROE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Daño por turno y aguante reales del héroe. Se mide lo mismo que hace el
     * motor de combate — arma, atributos, crítico, mascota — porque si aquí se
     * midiera de otra forma, el enemigo se calibraría contra un héroe que no existe.
     */
    fun measureHero(progress: GameProgress, talentRank: (String) -> Int = { 0 }): HeroPower {
        val weapon = GameJsonParser.fromJson<Item>(progress.equippedWeaponJson)
        val armor = GameJsonParser.fromJson<Item>(progress.equippedArmorJson)
        val shield = GameJsonParser.fromJson<Item>(progress.equippedShieldJson)
        val ring = GameJsonParser.fromJson<Item>(progress.equippedRingJson)

        val weaponDmg = weapon?.dmgBonus ?: 0
        val weaponStr = weapon?.strBonus ?: 0
        val weaponDex = weapon?.dexBonus ?: 0

        val isRogue = progress.charClass == "Pícaro"
        val modifierStat = if (isRogue) (progress.statDex + weaponDex) else (progress.statStr + weaponStr)

        // Golpe básico medio (el motor suma un aleatorio 3..8; aquí, su media).
        val talentMult = 1.0 + (talentRank("t_1") * 0.04)
        val raceMult = when {
            progress.charRace == "Orco" && progress.charLevel >= 100 -> 1.80
            progress.charRace == "Orco" && progress.charLevel >= 50 -> 1.45
            progress.charRace == "Orco" && progress.charLevel >= 20 -> 1.25
            progress.charRace == "Orco" -> 1.10
            else -> 1.0
        }
        val baseHit = (modifierStat * 0.6) + weaponDmg + 5.5

        // Esperanza del crítico: probabilidad × (1.8 − 1).
        val baseCrit = 5.0 + (progress.statDex * 0.4) + (talentRank("t_8") * 3)
        val raceCrit = when {
            progress.charRace == "Elfo" && progress.charLevel >= 100 -> 40
            progress.charRace == "Elfo" && progress.charLevel >= 50 -> 25
            progress.charRace == "Elfo" && progress.charLevel >= 20 -> 15
            progress.charRace == "Elfo" -> 5
            progress.charRace == "Humano" && progress.charLevel >= 100 -> 25
            progress.charRace == "Humano" && progress.charLevel >= 50 -> 15
            progress.charRace == "Humano" && progress.charLevel >= 20 -> 10
            progress.charRace == "Humano" -> 5
            else -> 0
        }
        val critChance = ((baseCrit + raceCrit) / 100.0).coerceIn(0.0, 0.75)
        val critMult = 1.0 + critChance * 0.8

        // El héroe tampoco pega siempre con el golpe básico: alterna con
        // habilidades de clase que multiplican entre 1,3 y 2,2. Medir sólo el
        // básico infravaloraba su daño ~30 %, y como la vida del enemigo salía
        // de esta medida, TODOS los enemigos nacían más flojos de lo previsto.
        // Al enemigo ya se le aplicaba su equivalente; era una asimetría.
        var dpt = baseHit * talentMult * raceMult * critMult * HERO_SKILL_AVERAGE_MULT

        // La mascota es un segundo atacante: ignorarla dejaba al enemigo corto.
        val pet = GameJsonParser.fromJson<Item>(progress.equippedPetJson)
        if (pet != null) {
            val petWpn = GameJsonParser.fromJson<Item>(progress.petEquippedWeaponJson)
            val petAcc = GameJsonParser.fromJson<Item>(progress.petEquippedAccessoryJson)
            val extra = (petWpn?.dmgBonus ?: 0) + (petWpn?.strBonus ?: 0) +
                (petAcc?.strBonus ?: 0) + (petAcc?.intBonus ?: 0)
            val satiety = when {
                progress.petSatiety >= 50 -> 1.25
                progress.petSatiety > 0 -> 1.0
                else -> 0.6
            }
            val petDmg = ((pet.dmgBonus * 0.9 + progress.charLevel * 4 + progress.petLevel * 14 +
                pet.strBonus * 0.5 + extra + 17.0) * satiety) * 0.40
            dpt += petDmg.coerceAtLeast(8.0)
        }

        // Defensa: idéntica a la que aplica el turno enemigo.
        val totalDefBonus = (armor?.defBonus ?: 0) + (shield?.defBonus ?: 0) + (ring?.defBonus ?: 0)
        var defense = (progress.statCon * 0.4).toInt() + totalDefBonus
        if (progress.charRace == "Enano") {
            defense += when {
                progress.charLevel >= 100 -> 80
                progress.charLevel >= 50 -> 35
                progress.charLevel >= 20 -> 15
                else -> 5
            }
        }

        val rawDpt = dpt.coerceAtLeast(1.0)
        val maxHp = progress.maxHp.coerceAtLeast(1)
        val scale = outputScaleFor(rawDpt, maxHp)

        return HeroPower(
            // El enemigo se calibra contra el daño REESCALADO, que es el que el
            // combate va a producir de verdad. Medirlo sin el factor sería
            // repetir el fallo original con el signo cambiado.
            damagePerTurn = rawDpt * scale,
            maxHp = maxHp,
            defense = defense.coerceAtLeast(0),
            level = progress.charLevel.coerceAtLeast(1),
            outputScale = scale,
            rawDamagePerTurn = rawDpt
        )
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CONSTRUIR AL ENEMIGO
    // ═══════════════════════════════════════════════════════════════════════

    /** Estadísticas de un enemigo calibrado contra un héroe concreto. */
    data class EnemyStats(val hp: Int, val attack: Int, val defense: Int, val level: Int = 1)

    /**
     * Vida, ataque y defensa que hacen que el combate dure lo que debe durar.
     *
     * @param hero medida del héroe ([measureHero]).
     * @param rarity NORMAL / ELITE / CHAMPION / LEGENDARY / UNIVERSAL.
     * @param enemyLevel sólo afecta a la curva de mitigación, no al poder bruto.
     * @param hpMult multiplicadores del bestiario (arquetipo y afijos).
     */
    fun buildEnemy(
        hero: HeroPower,
        rarity: String,
        enemyLevel: Int,
        isBoss: Boolean = false,
        hpMult: Double = 1.0,
        atkMult: Double = 1.0,
        defMult: Double = 1.0
    ): EnemyStats {
        val shape = SHAPES[rarity.uppercase()] ?: DEFAULT_SHAPE

        // ── Defensa: absorbe una fracción fija del daño del héroe ──
        // Se despeja de la curva: def = K · soak/(1 − soak).
        val soak = (shape.armorSoak * defMult).coerceIn(0.0, 0.55)
        val kHero = defenseConstant(hero.level)
        val rawDefense = (kHero * soak / (1.0 - soak)).roundToInt().coerceAtLeast(0)

        // ── Vida: una FRACCIÓN DE LA VIDA DEL HÉROE, corregida por nivel ──
        //
        // Anclarla al nivel con una fórmula propia (`40 + 26·L + 1,1·L²`) daba
        // una curva correcta en una escala equivocada: 510 de vida para un nivel
        // 12 que se enfrenta a un héroe de 12 000. El jugador no compara al
        // enemigo con una tabla, lo compara con SU barra de vida — así que la
        // vida enemiga se expresa justamente en esa unidad.
        //
        // Sigue creciendo con el nivel, que era el arreglo anterior y no se
        // pierde: `levelFactor` es lo que hace que un bicho de nivel 24 pese el
        // triple que uno de nivel 6 delante del mismo héroe.
        val hpFactor = 1.0 + (hpMult - 1.0) * 0.5
        val soakedThrough = damageThrough(rawDefense, hero.level, 0.0)
        val effectiveDpt = (hero.damagePerTurn * soakedThrough).coerceAtLeast(1.0)

        val anchored = hero.maxHp * shape.heroHpShare *
            levelFactor(enemyLevel, hero.level) * hpFactor *
            (if (isBoss) 1.10 else 1.0)

        // El techo de turnos cede cuando el enemigo te saca niveles: si no, a
        // partir de cierto punto TODA la vida quedaba recortada al mismo valor
        // y la curva volvía a aplanarse — el fallo que se está corrigiendo.
        // Que un bicho muy por encima de tu nivel cueste más turnos no es un
        // defecto: es lo que significa estar por encima de tu nivel.
        val levelGap = (enemyLevel - hero.level).coerceAtLeast(0)
        val stretch = (1.0 + levelGap * 0.04).coerceAtMost(1.4)
        val rawHp = anchored.coerceIn(
            effectiveDpt * shape.minTurns,
            effectiveDpt * shape.maxTurns * stretch
        )

        // ── Ataque: el que vacía la vida del héroe en los turnos previstos ──
        val wantedPerTurn = hero.maxHp / shape.turnsToDie
        val cappedPerTurn = wantedPerTurn.coerceIn(
            hero.maxHp * shape.minHitFraction,
            hero.maxHp * shape.maxHitFraction
        )
        val atkFactor = 1.0 + (atkMult - 1.0) * 0.5
        val rawAttack = rawAttackFor(
            // Se divide por el multiplicador medio de habilidad: el objetivo es
            // la MEDIA del turno, no el golpe básico aislado.
            wantedDamage = cappedPerTurn * atkFactor / SKILL_AVERAGE_MULT,
            defense = hero.defense,
            attackerLevel = enemyLevel,
            armorPen = 0.0
        )

        return EnemyStats(
            hp = rawHp.roundToInt().coerceAtLeast(12),
            attack = rawAttack.coerceAtLeast(2),
            defense = rawDefense,
            level = enemyLevel.coerceAtLeast(1)
        )
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PENETRACIÓN DE ARMADURA
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Cuánta armadura ignora cada movimiento enemigo, de 0 a 1.
     *
     * Antes era binario: o el golpe respetaba la armadura entera, o la ignoraba
     * del todo. Con la curva de mitigación tiene sentido el término medio, y es
     * lo que convierte la defensa en una decisión y no en un número que se sube
     * hasta ser inmune.
     */
    fun armorPenOf(move: String): Double = when (move.uppercase()) {
        "ARMOR_PIERCE" -> 0.85  // su razón de ser: la armadura casi no cuenta
        "TRUE_STRIKE" -> 0.75   // certero e inesquivable
        "BOSS_FURY" -> 0.70     // el golpe que define a un jefe
        "BLEED" -> 0.45         // el tajo entra por las juntas
        "POISON" -> 0.35        // corroe, no golpea
        "FREEZE" -> 0.25        // hiela por dentro
        "REGEN_SHIELD" -> 0.0
        else -> 0.0             // el ataque básico respeta la armadura entera
    }

    /** Etiqueta legible del porcentaje de armadura atravesada, para el registro. */
    fun penLabel(pen: Double): String =
        if (pen <= 0.0) "" else " (atraviesa ${(pen * 100).roundToInt()} % de tu armadura)"

    /**
     * Techo de un golpe suelto. Es la red de seguridad contra las muertes de un
     * solo impacto: por muchos afijos, críticos y multiplicadores que se apilen,
     * ningún golpe puede llevarse más de esta fracción de la vida máxima.
     *
     * Se aplica DESPUÉS de la mitigación, sobre el daño ya definitivo.
     */
    fun capHit(damage: Int, heroMaxHp: Int, rarity: String): Int {
        if (damage <= 0) return 0
        val shape = SHAPES[rarity.uppercase()] ?: DEFAULT_SHAPE
        // Un especial pega la mitad más que el turno medio, y ahí se corta. Con
        // los jefes pegando por encima del héroe, sin este techo un crítico
        // encadenado a un enfurecimiento te mataba en dos golpes sin respuesta.
        val ceiling = (heroMaxHp * shape.maxHitFraction * 1.5).roundToInt().coerceAtLeast(1)
        return min(damage, ceiling)
    }

    /**
     * Suelo de un golpe que ACIERTA. La contrapartida de [capHit].
     *
     * `damageThrough` es k/(k+defensa): tiende a cero, así que una armadura lo
     * bastante grande dejaba todos los golpes básicos en 1 de daño y el combate
     * se volvía inofensivo por mucho que el enemigo estuviera bien calibrado.
     * Aquí se garantiza que un impacto SIEMPRE se nota, sin tocar la curva de
     * armadura (que sigue premiando equiparse bien: la diferencia entre el suelo
     * y el techo es de más de un orden de magnitud).
     *
     * No se aplica a lo que el jugador evita: esquivar y la parada perfecta
     * anulan el golpe DESPUÉS, y eso debe seguir dejándolo en cero.
     */
    fun floorHit(damage: Int, heroMaxHp: Int, rarity: String): Int {
        if (damage <= 0) return 0
        val fraction = when (rarity.uppercase()) {
            "UNIVERSAL" -> 0.025
            "LEGENDARY" -> 0.020
            "CHAMPION" -> 0.015
            else -> 0.010
        }
        val floor = (heroMaxHp * fraction).roundToInt().coerceAtLeast(1)
        return max(damage, floor)
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  DIAGNÓSTICO
    // ═══════════════════════════════════════════════════════════════════════

    /** Turnos reales que costará matar a un enemigo. Para depurar el balance. */
    fun expectedTurnsToKill(hero: HeroPower, enemy: EnemyStats): Double {
        val through = damageThrough(enemy.defense, hero.level, 0.0)
        val perTurn = (hero.damagePerTurn * through).coerceAtLeast(1.0)
        return enemy.hp / perTurn
    }

    /**
     * Turnos reales que el héroe aguantará. Para depurar el balance.
     *
     * Mide el turno MEDIO, no el golpe básico: el enemigo alterna básico y
     * habilidades, y el ataque se calibró contra esa media. Y usa el nivel del
     * ENEMIGO en la curva, que es quien ataca — medirlo con el nivel del héroe
     * inflaba el aguante y escondía lo dura que es de verdad la pelea.
     */
    fun expectedTurnsToDie(hero: HeroPower, enemy: EnemyStats): Double {
        val basic = mitigate(enemy.attack, hero.defense, enemy.level, 0.0)
        val perTurn = (basic * SKILL_AVERAGE_MULT).coerceAtLeast(1.0)
        return hero.maxHp.toDouble() / perTurn
    }
}
