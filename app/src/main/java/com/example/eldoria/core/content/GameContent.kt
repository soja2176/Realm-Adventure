package com.example.eldoria.core.content

import androidx.compose.ui.graphics.Color

/**
 * DEFINICIÓN DE CONTENIDO DEL JUEGO: LORE, REINOS, ENEMIGOS, MISIONES
 * Aquí reside toda la "alma" del juego: historia, dificultad y variedad.
 */

// --- 1. SISTEMA DE LORE E HISTORIA ---

object WorldLore {
    const val WORLD_NAME = "Aethelgard"
    const val MAIN_ARTIFACT = "El Cristal de Aethelgard"
    
    val introduction = """
        Hace mil años, el Cristal de Aethelgard mantenía el equilibrio entre la luz y la sombra.
        Pero la traición del Archimago Malakor lo fracturó en cuatro fragmentos, dispersándolos por los rincones del reino.
        Sin el cristal, las estaciones se detuvieron, los muertos caminan en el norte y las bestias de sombra acechan en los bosques.
        
        Tú eres el último descendiente de los Guardianes del Cristal. Tu misión no es solo sobrevivir, 
        es restaurar el orden o ver cómo el mundo se consume en la oscuridad eterna.
    """.trimIndent()

    val classLore = mapOf(
        "Guerrero" to "Antiguos defensores de las murallas de Hierro. Su sangre lleva el juramento de proteger a los débiles, incluso cuando ya no queda nadie a quien proteger.",
        "Mago" to "Estudiosos de la Torre Etérea. Buscan los fragmentos no por poder, sino porque solo la magia arcana puede soldar el cristal roto sin destruirlo.",
        "Pícaro" to "Hijos de las sombras. Conocen los secretos que los reyes ocultan bajo sus tronos. Para ellos, el cristal es la mayor joya jamás robada."
    )
}

// --- 2. SISTEMA DE REINOS Y BIOMAS ---

enum class Realm(
    val displayName: String,
    val description: String,
    val baseDifficulty: Float, // Multiplicador de daño/enemigos
    val xpMultiplier: Float,   // Multiplicador de experiencia
    val terrainColor: Color,
    val skyColor: Color,
    val ambientSound: String,
    val lore: String
) {
    STARTER(
        displayName = "Valle de los Susurros",
        description = "Las tierras fértiles donde comienza tu viaje. Pacífico, pero plagado de bandidos y bestias menores.",
        baseDifficulty = 1.0f,
        xpMultiplier = 1.0f,
        terrainColor = Color(0xFF4CAF50),
        skyColor = Color(0xFF87CEEB),
        ambientSound = "birds_chirping",
        lore = "El valle donde creciste. Los ancianos dicen que el primer fragmento del cristal cayó aquí, fertilizando la tierra con magia pura."
    ),
    FOREST(
        displayName = "Bosque de las Sombras Eternas",
        description = "Un bosque denso donde la luz del sol apenas toca el suelo. Lleno de bestias corrompidas.",
        baseDifficulty = 1.5f,
        xpMultiplier = 1.5f,
        terrainColor = Color(0xFF1B5E20),
        skyColor = Color(0xFF455A64),
        ambientSound = "wind_howl",
        lore = "Aquí se esconden los Druidas Oscuros, quienes creen que la fragmentación del cristal es la voluntad de la naturaleza."
    ),
    MOUNTAINS(
        displayName = "Picos de Hielo y Sangre",
        description = "Montañas escarpadas habitadas por gigantes y constructos de piedra antigua.",
        baseDifficulty = 2.2f,
        xpMultiplier = 2.5f,
        terrainColor = Color(0xFFECEFF1),
        skyColor = Color(0xFFB0BEC5),
        ambientSound = "blizzard",
        lore = "Las minas enanas fueron selladas hace siglos. Dicen que el segundo fragmento está en la forja del Rey de Piedra."
    ),
    WASTELAND(
        displayName = "Las Tierras Baldías de Malakor",
        description = "Tierra quemada por la magia oscura. El aire es tóxico y los muertos no descansan.",
        baseDifficulty = 3.5f,
        xpMultiplier = 4.0f,
        terrainColor = Color(0xFF3E2723),
        skyColor = Color(0xFF4A148C),
        ambientSound = "demon_screams",
        lore = "El corazón de la corrupción. Aquí se alza la Torre Oscura, donde Malakor espera el fin de los tiempos."
    ),
    DUNGEON(
        displayName = "La Mazmorra Olvidada",
        description = "Una dimensión de bolsillo creada por magia antigua. Solo los más valientes entran.",
        baseDifficulty = 5.0f,
        xpMultiplier = 8.0f,
        terrainColor = Color(0xFF212121),
        skyColor = Color(0xFF000000),
        ambientSound = "chains_rattling",
        lore = "No es un lugar físico, sino un laberinto mágico que cambia cada vez que entras. El premio: un fragmento puro de poder."
    )
}

// --- 3. BESTIARIO Y ENEMIGOS (CON ASSETS 3D) ---

enum class EnemyType(
    val displayName: String,
    val description: String,
    val baseHp: Int,
    val baseDmg: Int,
    val xpValue: Int,
    val goldValue: Int,
    val modelAsset: String, // Clave para el AssetManager3D
    val colorHex: Long,    // Color para variantes o si no hay modelo
    val isElite: Boolean = false,
    val requiredRealm: Realm = Realm.STARTER
) {
    // Valle (Fáciles)
    RAT("Rata Gigante", "Una rata mutada por la magia residual.", 30, 4, 10, 5, "rat.glb", 0xFF8D6E63),
    GOBLIN_SCOUT("Explorador Goblin", "Débil pero astuto. Usa dagas oxidadas.", 45, 6, 15, 8, "goblin.glb", 0xFF4CAF50),
    BANDIT("Bandido Desesperado", "Un humano corrupto por la codicia.", 60, 8, 20, 15, "bandit.glb", 0xFF795548),

    // Bosque (Medios)
    WARG("Warg Salvaje", "Lobo gigante con ojos brillantes.", 120, 15, 45, 25, "wolf.glb", 0xFF37474F),
    DARK_DRUID("Druida Oscuro", "Usa espinas venenosas y magia natural.", 100, 20, 60, 40, "mage.glb", 0xFF1B5E20),
    TREANT_GUARD("Treant Guardían", "Árbol viviente enfurecido.", 200, 12, 55, 30, "treant.glb", 0xFF5D4037),

    // Montañas (Difíciles)
    STONE_GOLEM("Golem de Piedra", "Constructo antiguo sin piedad.", 350, 25, 120, 80, "golem.glb", 0xFF9E9E9E),
    ICE_GIANT("Gigante de Hielo", "Golpea con mazos de hielo eterno.", 450, 35, 180, 120, "giant.glb", 0xFFB3E5FC),
    HARPY("Harpy de los Picos", "Ataca desde el aire con garras afiladas.", 180, 30, 90, 60, "harpy.glb", 0xFFE1F5FE),

    // Tierras Baldías (Muy Difíciles)
    SKELETAL_KNIGHT("Caballero Esquelético", "Guerrero inmune al dolor.", 500, 40, 250, 150, "skeleton.glb", 0xFFCFD8DC),
    NECROMANCER("Nigromante Menor", "Invoca huesos y debilita el alma.", 300, 55, 300, 200, "necro.glb", 0xFF4A148C),
    DEMON_IMP("Diablillo del Abismo", "Pequeño pero lleno de fuego infernal.", 250, 45, 200, 100, "imp.glb", 0xFFD32F2F),

    // Jefes y Élite (Bosses)
    GOBLIN_KING("Rey Goblin", "Líder de las hordas verdes. Muy peligroso.", 800, 25, 500, 400, "goblin_king.glb", 0xFF2E7D32, true, Realm.FOREST),
    ANCIENT_TREANT("Anciano del Bosque", "El árbol más viejo, corrompido totalmente.", 1200, 30, 800, 600, "ancient_tree.glb", 0xFF3E2723, true, Realm.FOREST),
    FROST_LORD("Señor de la Escarcha", "Comanda a los gigantes de hielo.", 1500, 45, 1200, 900, "frost_lord.glb", 0xFF0288D1, true, Realm.MOUNTAINS),
    MALAKOR_AVATAR("Avatar de Malakor", "Un fragmento de la consciencia del villano.", 2500, 60, 2500, 2000, "dark_mage.glb", 0xFF311B92, true, Realm.WASTELAND),
    CRYSTAL_GUARDIAN("Guardián del Cristal", "Protector mecánico de la mazmorra.", 2000, 50, 1500, 1000, "construct.glb", 0xFFFFD700, true, Realm.DUNGEON);

    fun getEliteVersion(): EnemyType {
        return if (this.isElite) this else this
    }
}

// --- 4. SISTEMA DE MISIONES Y EVENTOS ---

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val type: QuestType,
    val targetEntity: String?,
    val targetCount: Int,
    val rewardXp: Int,
    val rewardGold: Int,
    val difficultyTier: Int,
    val isCompleted: Boolean = false
)

enum class QuestType {
    HUNT,
    COLLECT,
    EXPLORE,
    BOSS_HUNT,
    SURVIVAL
}

object QuestGenerator {
    private val questTemplates = listOf(
        "Las aldeas necesitan protección contra las {}.",
        "Se ha reportado una plaga de {} en la zona.",
        "Un ermitaño busca pieles de {} para un ritual.",
        "Los espíritus del bosque exigen la cabeza de un {}.",
        "Entrena tu cuerpo luchando contra {} para volverte más fuerte."
    )

    fun generateQuest(realm: Realm, playerLevel: Int): Quest {
        val possibleEnemies = EnemyType.values().filter { it.requiredRealm == realm || realm == Realm.STARTER }
            .filter { !it.isElite }
        
        val enemy = possibleEnemies.randomOrNull() ?: EnemyType.RAT
        val count = (5 * playerLevel * realm.baseDifficulty).toInt().coerceIn(3, 50)
        val type = QuestType.values().random()

        return Quest(
            id = "q_${realm.name}_${System.currentTimeMillis()}",
            title = "Misión en ${realm.displayName}",
            description = questTemplates.random().format(enemy.displayName),
            type = type,
            targetEntity = enemy.displayName,
            targetCount = count,
            rewardXp = (count * enemy.xpValue * 1.5).toInt(),
            rewardGold = (count * enemy.goldValue * 1.2).toInt(),
            difficultyTier = (playerLevel * realm.baseDifficulty).toInt()
        )
    }
}

data class RandomEvent(
    val title: String,
    val description: String,
    val effect: EventEffect
)

enum class EventEffect {
    HEAL_FULL,
    HEAL_HALF,
    DAMAGE_PLAYER,
    BUFF_ATTACK,
    BUFF_DEFENSE,
    FIND_GOLD,
    LOSE_GOLD,
    SPAWN_AMBUSH,
    NOTHING
}

object EventGenerator {
    val positiveEvents = listOf(
        RandomEvent("Fuente Sagrada", "Encuentras una fuente de agua pura. Tus heridas sanan completamente.", EventEffect.HEAL_FULL),
        RandomEvent("Mercader Ambulante", "Un mercader te da oro por protegerlo de bestias.", EventEffect.FIND_GOLD),
        RandomEvent("Bendición Ancestral", "Un espíritu te bendice. Tu ataque aumenta temporalmente.", EventEffect.BUFF_ATTACK),
        RandomEvent("Descanso Seguro", "Logras descansar sin ser molestado. Recuperas salud.", EventEffect.HEAL_HALF),
        RandomEvent("Santuario Antiguo", "Encontras un altar olvidado. Te sientes revitalizado.", EventEffect.HEAL_FULL),
        RandomEvent("Cazador Generoso", "Un cazador te comparte provisiones y oro.", EventEffect.FIND_GOLD)
    )

    val negativeEvents = listOf(
        RandomEvent("Emboscada", "¡Te han rodeado! Enemigos adicionales aparecen.", EventEffect.SPAWN_AMBUSH),
        RandomEvent("Terreno Traicionero", "Caes en un hoyo oculto. Te lastimas.", EventEffect.DAMAGE_PLAYER),
        RandomEvent("Ladrón de Sombras", "Un espectro roba parte de tu oro.", EventEffect.LOSE_GOLD),
        RandomEvent("Niebla Tóxica", "Respiras aire viciado. Pierdes salud.", EventEffect.DAMAGE_PLAYER),
        RandomEvent("Tormenta Repentina", "Un rayo cae cerca. Resultas herido.", EventEffect.DAMAGE_PLAYER),
        RandomEvent("Ilusión Demoníaca", "Confundido, pierdes oro en la confusión.", EventEffect.LOSE_GOLD)
    )

    fun generateEvent(dangerLevel: Float): RandomEvent {
        val chance = kotlin.random.Random.nextFloat()
        val threshold = 0.45f + (dangerLevel * 0.05f).coerceAtMost(0.15f)
        
        return if (chance > threshold) negativeEvents.random() else positiveEvents.random()
    }
}

// --- 5. CONFIGURACIÓN DE DIFICULTAD Y PROGRESIÓN ---

object GameBalance {
    // Curva de XP Exponencial ajustada para ser más desafiante
    fun getXpRequiredForLevel(level: Int): Int {
        // Nivel 1: 100 XP
        // Nivel 5: ~550 XP
        // Nivel 10: ~1500 XP
        // Nivel 20: ~5000 XP
        // Nivel 50: ~25000 XP
        return (100 * Math.pow(level.toDouble(), 1.7)).toInt()
    }

    // Escalado de enemigos
    fun scaleEnemyStats(enemy: EnemyType, playerLevel: Int, realm: Realm): Pair<Int, Int> {
        val realmMult = realm.baseDifficulty
        val levelMult = 1.0f + (playerLevel * 0.08f)
        
        val hp = (enemy.baseHp * realmMult * levelMult).toInt()
        val dmg = (enemy.baseDmg * realmMult * levelMult).toInt()
        
        return Pair(hp.coerceAtLeast(10), dmg.coerceAtLeast(2))
    }

    // Probabilidad de encontrar un enemigo Élite
    fun getEliteSpawnChance(realm: Realm): Float {
        return when(realm) {
            Realm.STARTER -> 0.05f
            Realm.FOREST -> 0.10f
            Realm.MOUNTAINS -> 0.15f
            Realm.WASTELAND -> 0.25f
            Realm.DUNGEON -> 0.40f
        }
    }
    
    // Bonus de XP para enemigos Elite
    fun getEliteXpMultiplier(): Float = 3.0f
    
    // Bonus de Oro para enemigos Elite
    fun getEliteGoldMultiplier(): Float = 2.5f
}
