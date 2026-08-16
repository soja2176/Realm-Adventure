package com.example.data.content

import com.example.data.model.PetSpecies
import com.example.data.model.PetTrait
import kotlin.random.Random

/**
 * Catálogo de mascotas de Eldoria Chronicles.
 *
 * 24 especies: las 8 legendarias existentes (con su `imageResName` real, dibujadas con JPG)
 * y 16 nuevas procedurales (`imageResName` vacío → la UI dibuja `EldoriaBeastSigil`
 * con `sigilSeed` + `paletteKey`).
 *
 * 12 rasgos, 3 etapas de evolución y 3 disciplinas (FURIA / BASTION / VITALIDAD).
 */
object EldoriaPets {

    // ───────────────────────────────────────────────────────────────────────
    //  RASGOS (12)
    // ───────────────────────────────────────────────────────────────────────

    const val TRAIT_AURA_FEROZ = "trait_aura_feroz"
    const val TRAIT_PIEL_HIERRO = "trait_piel_hierro"
    const val TRAIT_CORAZON_VITAL = "trait_corazon_vital"
    const val TRAIT_OJO_CERTERO = "trait_ojo_certero"
    const val TRAIT_SED_ORO = "trait_sed_oro"
    const val TRAIT_ALMA_FENIX = "trait_alma_fenix"
    const val TRAIT_ZANCADA_VELOZ = "trait_zancada_veloz"
    const val TRAIT_BENDICION_SERENA = "trait_bendicion_serena"
    const val TRAIT_COLMILLO_VENENOSO = "trait_colmillo_venenoso"
    const val TRAIT_ESCAMA_IGNEA = "trait_escama_ignea"
    const val TRAIT_VINCULO_ARCANO = "trait_vinculo_arcano"
    const val TRAIT_GUARDIAN_LEAL = "trait_guardian_leal"

    val TRAITS: List<PetTrait> = listOf(
        PetTrait(TRAIT_AURA_FEROZ, "Aura Feroz",
            "La mascota inflige un 8 % más de daño con todas sus órdenes.", 8, "BLOOD"),
        PetTrait(TRAIT_PIEL_HIERRO, "Piel de Hierro",
            "La orden Guardia absorbe un 12 % adicional de daño.", 12, "IRON"),
        PetTrait(TRAIT_CORAZON_VITAL, "Corazón Vital",
            "La orden Aliento cura un 15 % más de vida.", 15, "VITAE"),
        PetTrait(TRAIT_OJO_CERTERO, "Ojo Certero",
            "El héroe gana un 4 % de probabilidad de crítico mientras la mascota esté activa.", 4, "SILVER"),
        PetTrait(TRAIT_SED_ORO, "Sed de Oro",
            "Ganas un 10 % más de oro en cada combate ganado.", 10, "GOLD"),
        PetTrait(TRAIT_ALMA_FENIX, "Alma Fénix",
            "Una vez por expedición, la mascota te revive con un tercio de tu vida.", 1, "EMBER"),
        PetTrait(TRAIT_ZANCADA_VELOZ, "Zancada Veloz",
            "Reduce en 1 turno el enfriamiento de las órdenes de mascota.", 1, "SILVER"),
        PetTrait(TRAIT_BENDICION_SERENA, "Bendición Serena",
            "La orden Aliento limpia además un estado alterado.", 1, "ARCANE"),
        PetTrait(TRAIT_COLMILLO_VENENOSO, "Colmillo Venenoso",
            "La orden Embestida aplica veneno durante 3 turnos.", 3, "VITAE"),
        PetTrait(TRAIT_ESCAMA_IGNEA, "Escama Ígnea",
            "Refleja un 10 % del daño recibido por el héroe al atacante.", 10, "EMBER"),
        PetTrait(TRAIT_VINCULO_ARCANO, "Vínculo Arcano",
            "Reduce un 15 % el coste de maná de tus habilidades.", 15, "ARCANE"),
        PetTrait(TRAIT_GUARDIAN_LEAL, "Guardián Leal",
            "Una vez por combate, la orden Guardia absorbe el 100 % de un golpe.", 100, "IRON")
    )

    // ───────────────────────────────────────────────────────────────────────
    //  ESPECIES (24 = 8 legacy con arte real + 16 nuevas procedurales)
    // ───────────────────────────────────────────────────────────────────────

    /** Las 8 mascotas universales heredadas, envueltas como PetSpecies con su arte JPG real. */
    private val LEGACY_SPECIES: List<PetSpecies> = listOf(
        PetSpecies(
            id = "pet_fenix_cosmico", name = "Fénix Cósmico",
            title = "Fénix Cósmico de Flama Eterna",
            lore = "Renace de su propia ceniza en cada amanecer del multiverso. Su llama sagrada abrasa al enemigo y devuelve el aliento a su vinculado.",
            rarity = "UNIVERSAL", paletteKey = "EMBER", sigilSeed = 90_101,
            imageResName = "img_pet_fenix_cosmico", signatureTrait = TRAIT_ALMA_FENIX,
            baseAtk = 46, baseDef = 28, baseVit = 40, favoriteFood = "CELESTIAL",
            evolutionNames = listOf("Fénix Cósmico", "Fénix de Nova", "Fénix del Amanecer Eterno")
        ),
        PetSpecies(
            id = "pet_dragon_sombras", name = "Dragón de Sombras",
            title = "Dragón de Sombras Abisales",
            lore = "Forjado en la noche insondable que hay bajo Aethelgard. Su aliento espectral devora la vida antes de tocar la carne.",
            rarity = "UNIVERSAL", paletteKey = "ARCANE", sigilSeed = 90_102,
            imageResName = "img_pet_dragon_sombras", signatureTrait = TRAIT_AURA_FEROZ,
            baseAtk = 52, baseDef = 30, baseVit = 34, favoriteFood = "DRAGON",
            evolutionNames = listOf("Dragón de Sombras", "Dragón de Eclipse", "Tirano de Sombras Abisales")
        ),
        PetSpecies(
            id = "pet_lobo_celestial", name = "Lobo Celestial",
            title = "Lobo Celestial de las Estrellas",
            lore = "Cazador sideral que persigue cometas por deporte. Sus embestidas abren el hueco justo para que tu golpe entre.",
            rarity = "UNIVERSAL", paletteKey = "SILVER", sigilSeed = 90_103,
            imageResName = "img_pet_lobo_celestial", signatureTrait = TRAIT_ZANCADA_VELOZ,
            baseAtk = 44, baseDef = 26, baseVit = 36, favoriteFood = "BESTIAL",
            evolutionNames = listOf("Lobo Celestial", "Huargo de Constelación", "Fenrir del Firmamento")
        ),
        PetSpecies(
            id = "pet_gato_estelar", name = "Gato Estelar",
            title = "Gato Estelar Fortuna",
            lore = "Felino sagrado que decide a quién sonríe el universo. Restaura salud y maná cuando le apetece, que es a menudo.",
            rarity = "UNIVERSAL", paletteKey = "MANA", sigilSeed = 90_104,
            imageResName = "img_pet_gato_estelar", signatureTrait = TRAIT_SED_ORO,
            baseAtk = 34, baseDef = 24, baseVit = 46, favoriteFood = "MISTICA",
            evolutionNames = listOf("Gato Estelar", "Felino de Buena Estrella", "Avatar de la Fortuna")
        ),
        PetSpecies(
            id = "pet_titan_cristal", name = "Titán de Cristal",
            title = "Titán de Cristal Ancestral",
            lore = "Coloso tallado en un único cristal de las Nubes de Cristal. Se interpone entre tú y el golpe sin que se lo pidas.",
            rarity = "UNIVERSAL", paletteKey = "IRON", sigilSeed = 90_105,
            imageResName = "img_pet_titan_cristal", signatureTrait = TRAIT_GUARDIAN_LEAL,
            baseAtk = 30, baseDef = 54, baseVit = 44, favoriteFood = "MISTICA",
            evolutionNames = listOf("Titán de Cristal", "Coloso Prismático", "Baluarte Ancestral de Cristal")
        ),
        PetSpecies(
            id = "pet_grifo_dorado", name = "Grifo Dorado",
            title = "Grifo Dorado de Eldoria",
            lore = "Emblema vivo de la casa real de Ciudad Alba. Embiste con cortes rúnicos que aturden a quien osa mirarlo de frente.",
            rarity = "UNIVERSAL", paletteKey = "GOLD", sigilSeed = 90_106,
            imageResName = "img_pet_grifo_dorado", signatureTrait = TRAIT_OJO_CERTERO,
            baseAtk = 48, baseDef = 32, baseVit = 34, favoriteFood = "BESTIAL",
            evolutionNames = listOf("Grifo Dorado", "Grifo Real Coronado", "Grifo Solar de Eldoria")
        ),
        PetSpecies(
            id = "pet_serpiente_astral", name = "Serpiente Astral",
            title = "Serpiente Astral de Luz",
            lore = "Criatura de polvo de estrellas que se enrosca en el brazo de su vinculado. Su veneno es luz que no deja de arder.",
            rarity = "UNIVERSAL", paletteKey = "ARCANE", sigilSeed = 90_107,
            imageResName = "img_pet_serpiente_astral", signatureTrait = TRAIT_COLMILLO_VENENOSO,
            baseAtk = 42, baseDef = 22, baseVit = 38, favoriteFood = "CELESTIAL",
            evolutionNames = listOf("Serpiente Astral", "Sierpe de Nebulosa", "Ouróboros de Luz")
        ),
        PetSpecies(
            id = "pet_behemoth_vacio", name = "Behemoth del Vacío",
            title = "Behemoth del Vacío Infinito",
            lore = "Bestia de fuerza devastadora nacida entre dos universos. Sus ondas de choque llegan antes que sus patas.",
            rarity = "UNIVERSAL", paletteKey = "ARCANE", sigilSeed = 90_108,
            imageResName = "img_pet_behemoth_vacio", signatureTrait = TRAIT_ESCAMA_IGNEA,
            baseAtk = 56, baseDef = 40, baseVit = 30, favoriteFood = "DRAGON",
            evolutionNames = listOf("Behemoth del Vacío", "Behemoth de Singularidad", "Devorador del Vacío Infinito")
        )
    )

    /** Las 16 mascotas nuevas: sin JPG, dibujadas por sigilo procedural. */
    private val NEW_SPECIES: List<PetSpecies> = listOf(
        PetSpecies(
            id = "pet_salamandra_forja", name = "Salamandra de Forja",
            title = "Guardiana de la Fragua",
            lore = "Duerme dentro del yunque de los herreros de Drakenhold y despierta cuando el metal canta. Su lomo mantiene el rescoldo toda la noche.",
            rarity = "RARO", paletteKey = "EMBER", sigilSeed = 11_027,
            imageResName = "pet_fenix_cosmico_s1", signatureTrait = TRAIT_ESCAMA_IGNEA,
            baseAtk = 18, baseDef = 14, baseVit = 12, favoriteFood = "BESTIAL",
            evolutionNames = listOf("Salamandra de Forja", "Salamandra de Yunque", "Draco de Fundición")
        ),
        PetSpecies(
            id = "pet_buho_runico", name = "Búho Rúnico",
            title = "Lector de Presagios",
            lore = "Sus plumas están cubiertas de runas que cambian cada noche. Lee tus conjuros antes de que los pronuncies y te ahorra el esfuerzo.",
            rarity = "RARO", paletteKey = "ARCANE", sigilSeed = 11_063,
            imageResName = "pet_dragon_sombras_s1", signatureTrait = TRAIT_VINCULO_ARCANO,
            baseAtk = 13, baseDef = 12, baseVit = 19, favoriteFood = "MISTICA",
            evolutionNames = listOf("Búho Rúnico", "Búho del Cónclave", "Oráculo Alado de Aethelgard")
        ),
        PetSpecies(
            id = "pet_zorro_ceniza", name = "Zorro de Ceniza",
            title = "Chispa de los Páramos",
            lore = "Nació en el rescoldo de un bosque quemado y aprendió a correr más rápido que el fuego. Nunca pisa dos veces la misma brasa.",
            rarity = "RARO", paletteKey = "EMBER", sigilSeed = 11_091,
            imageResName = "pet_lobo_celestial_s1", signatureTrait = TRAIT_ZANCADA_VELOZ,
            baseAtk = 17, baseDef = 10, baseVit = 15, favoriteFood = "BESTIAL",
            evolutionNames = listOf("Zorro de Ceniza", "Zorro de Tres Colas", "Kitsune de Brasa")
        ),
        PetSpecies(
            id = "pet_escarabajo_coraza", name = "Escarabajo Coraza",
            title = "Broquel Viviente",
            lore = "Su caparazón se endurece cada vez que lo golpean, de modo que agradece sinceramente cada combate difícil.",
            rarity = "RARO", paletteKey = "IRON", sigilSeed = 11_117,
            imageResName = "pet_gato_estelar_s1", signatureTrait = TRAIT_PIEL_HIERRO,
            baseAtk = 10, baseDef = 22, baseVit = 16, favoriteFood = "BESTIAL",
            evolutionNames = listOf("Escarabajo Coraza", "Escarabajo de Placas", "Coloso Escarabajo de Acero")
        ),
        PetSpecies(
            id = "pet_liebre_lunar", name = "Liebre Lunar",
            title = "Ojo de la Luna Llena",
            lore = "Sólo se deja ver cuando la luna está entera. Señala con la oreja el punto exacto donde la armadura enemiga falla.",
            rarity = "ÉPICO", paletteKey = "SILVER", sigilSeed = 12_203,
            imageResName = "pet_titan_cristal_s1", signatureTrait = TRAIT_OJO_CERTERO,
            baseAtk = 21, baseDef = 14, baseVit = 20, favoriteFood = "MISTICA",
            evolutionNames = listOf("Liebre Lunar", "Liebre de Marea Alta", "Heraldo de la Luna Llena")
        ),
        PetSpecies(
            id = "pet_golem_musgo", name = "Gólem de Musgo",
            title = "Corazón del Claro",
            lore = "Un montón de piedras que el bosque decidió vestir de verde. Reparte savia curativa a manos llenas, sin entender por qué duele nada.",
            rarity = "ÉPICO", paletteKey = "VITAE", sigilSeed = 12_241,
            imageResName = "pet_grifo_dorado_s1", signatureTrait = TRAIT_CORAZON_VITAL,
            baseAtk = 14, baseDef = 24, baseVit = 26, favoriteFood = "MISTICA",
            evolutionNames = listOf("Gólem de Musgo", "Gólem de Arboleda", "Guardián Primaveral de Eldoria")
        ),
        PetSpecies(
            id = "pet_kirin_tormenta", name = "Kirin de Tormenta",
            title = "Trueno de Cascos Blancos",
            lore = "Galopa sobre nubes cargadas y sólo toca el suelo para castigar a los injustos. Su relincho suena a descarga.",
            rarity = "ÉPICO", paletteKey = "MANA", sigilSeed = 12_277,
            imageResName = "pet_serpiente_astral_s1", signatureTrait = TRAIT_AURA_FEROZ,
            baseAtk = 27, baseDef = 17, baseVit = 20, favoriteFood = "MISTICA",
            evolutionNames = listOf("Kirin de Tormenta", "Kirin de Rayo Azul", "Emperador Kirin del Cielo Roto")
        ),
        PetSpecies(
            id = "pet_manticora_menor", name = "Mantícora Menor",
            title = "Aguijón de las Ruinas",
            lore = "Cría de mantícora criada entre columnas caídas. Todavía no ruge bien, pero su aguijón ya cumple de sobra.",
            rarity = "ÉPICO", paletteKey = "BLOOD", sigilSeed = 12_311,
            imageResName = "pet_behemoth_vacio_s1", signatureTrait = TRAIT_COLMILLO_VENENOSO,
            baseAtk = 29, baseDef = 15, baseVit = 17, favoriteFood = "BESTIAL",
            evolutionNames = listOf("Mantícora Menor", "Mantícora de Ruinas", "Mantícora Coronada")
        ),
        PetSpecies(
            id = "pet_quimera_cristal", name = "Quimera de Cristal",
            title = "Tres Voces, Una Luz",
            lore = "Tres bestias fundidas en cuarzo vivo por un alquimista arrepentido. Cantan a la vez y el aire se limpia de maldiciones.",
            rarity = "LEGENDARIO", paletteKey = "ARCANE", sigilSeed = 13_407,
            imageResName = "pet_salamandra_forja_s1", signatureTrait = TRAIT_BENDICION_SERENA,
            baseAtk = 31, baseDef = 27, baseVit = 29, favoriteFood = "DRAGON",
            evolutionNames = listOf("Quimera de Cristal", "Quimera Prismática", "Quimera del Cónclave Eterno")
        ),
        PetSpecies(
            id = "pet_wyvern_crepuscular", name = "Wyvern Crepuscular",
            title = "Sombra de la Última Luz",
            lore = "Caza en el instante exacto en que el día se rinde. Su aliento tiene el color naranja de las despedidas.",
            rarity = "LEGENDARIO", paletteKey = "EMBER", sigilSeed = 13_451,
            imageResName = "pet_buho_runico_s1", signatureTrait = TRAIT_AURA_FEROZ,
            baseAtk = 38, baseDef = 22, baseVit = 25, favoriteFood = "DRAGON",
            evolutionNames = listOf("Wyvern Crepuscular", "Wyvern del Ocaso", "Dragón del Último Sol")
        ),
        PetSpecies(
            id = "pet_basilisco_jade", name = "Basilisco de Jade",
            title = "Mirada de Piedra Verde",
            lore = "Sus escamas de jade valen un reino y su mirada cuesta bastante más. Envenena por lealtad, nunca por hambre.",
            rarity = "LEGENDARIO", paletteKey = "VITAE", sigilSeed = 13_487,
            imageResName = "pet_zorro_ceniza_s1", signatureTrait = TRAIT_COLMILLO_VENENOSO,
            baseAtk = 35, baseDef = 26, baseVit = 27, favoriteFood = "BESTIAL",
            evolutionNames = listOf("Basilisco de Jade", "Basilisco Imperial", "Rey Basilisco de Jade")
        ),
        PetSpecies(
            id = "pet_ave_trueno", name = "Ave del Trueno",
            title = "Batir de Tormenta",
            lore = "Cada aletazo suyo adelanta la tormenta un poco más. Los pastores de Frostgard rezan para que pase de largo.",
            rarity = "LEGENDARIO", paletteKey = "MANA", sigilSeed = 13_523,
            imageResName = "pet_escarabajo_coraza_s1", signatureTrait = TRAIT_ZANCADA_VELOZ,
            baseAtk = 36, baseDef = 21, baseVit = 28, favoriteFood = "CELESTIAL",
            evolutionNames = listOf("Ave del Trueno", "Ave de Tempestad", "Soberana del Cielo Tronante")
        ),
        PetSpecies(
            id = "pet_leviatan_bolsillo", name = "Leviatán de Bolsillo",
            title = "Océano en Miniatura",
            lore = "Cabe en la palma de la mano y contiene una fosa abisal entera. Nadie sabe dónde guarda toda esa agua.",
            rarity = "ARCANO", paletteKey = "MANA", sigilSeed = 14_609,
            imageResName = "pet_liebre_lunar_s1", signatureTrait = TRAIT_GUARDIAN_LEAL,
            baseAtk = 34, baseDef = 42, baseVit = 36, favoriteFood = "DRAGON",
            evolutionNames = listOf("Leviatán de Bolsillo", "Leviatán de Marea", "Leviatán de las Fosas Negras")
        ),
        PetSpecies(
            id = "pet_ouroboros_menor", name = "Ouróboros Menor",
            title = "Círculo Sin Final",
            lore = "Se muerde la cola para no olvidar dónde empieza. Mientras el círculo aguante, tú tampoco terminas.",
            rarity = "ARCANO", paletteKey = "VITAE", sigilSeed = 14_653,
            imageResName = "pet_golem_musgo_s1", signatureTrait = TRAIT_CORAZON_VITAL,
            baseAtk = 33, baseDef = 33, baseVit = 46, favoriteFood = "CELESTIAL",
            evolutionNames = listOf("Ouróboros Menor", "Ouróboros de Eras", "Ouróboros del Ciclo Eterno")
        ),
        PetSpecies(
            id = "pet_custodio_umbral", name = "Custodio del Umbral",
            title = "Portero de lo que No Vuelve",
            lore = "Vigila la puerta que separa lo vivo de lo demás. Cuando cruzas sin permiso, te acompaña de vuelta a rastras.",
            rarity = "UNIVERSAL", paletteKey = "ARCANE", sigilSeed = 15_733,
            imageResName = "pet_kirin_tormenta_s1", signatureTrait = TRAIT_ALMA_FENIX,
            baseAtk = 47, baseDef = 44, baseVit = 45, favoriteFood = "CELESTIAL",
            evolutionNames = listOf("Custodio del Umbral", "Custodio de la Puerta Negra", "Soberano del Umbral Final")
        ),
        PetSpecies(
            id = "pet_espectro_aurora", name = "Espectro de Aurora",
            title = "Luz que No Calienta",
            lore = "Hecho con el último resplandor de una aurora sobre Frostgard. Señala el oro enterrado porque el oro también brilla.",
            rarity = "UNIVERSAL", paletteKey = "SILVER", sigilSeed = 15_787,
            imageResName = "pet_manticora_menor_s1", signatureTrait = TRAIT_SED_ORO,
            baseAtk = 45, baseDef = 38, baseVit = 48, favoriteFood = "CELESTIAL",
            evolutionNames = listOf("Espectro de Aurora", "Espectro Boreal", "Corona de Aurora Eterna")
        )
    )

    /** 24 especies: 8 legacy + 16 nuevas. */
    val SPECIES: List<PetSpecies> = LEGACY_SPECIES + NEW_SPECIES

    private val speciesIndex: Map<String, PetSpecies> = SPECIES.associateBy { it.id }
    private val traitIndex: Map<String, PetTrait> = TRAITS.associateBy { it.id }

    fun species(id: String): PetSpecies? = speciesIndex[id]

    fun trait(id: String): PetTrait? = traitIndex[id]

    /** Orden de rareza usado por los suelos de rareza y los sorteos. */
    fun rarityRank(rarity: String): Int = when (rarity.uppercase()) {
        "UNIVERSAL" -> 5
        "ARCANO", "ARCANE" -> 4
        "LEGENDARIO", "LEGENDARY" -> 3
        "ÉPICO", "EPICO", "EPIC" -> 2
        "RARO", "RARE" -> 1
        else -> 0
    }

    /** Especie aleatoria determinista con suelo de rareza. */
    fun randomSpecies(rarityFloor: String, seed: Long): PetSpecies {
        val floor = rarityRank(rarityFloor)
        val pool = SPECIES.filter { rarityRank(it.rarity) >= floor }.ifEmpty { SPECIES }
        return pool[Random(seed xor 0x9E37_79B9L).nextInt(pool.size)]
    }

    /** Curva de experiencia idéntica a la vigente. */
    fun expForLevel(level: Int): Int {
        val l = level.coerceAtLeast(1)
        return l * 150 + l * l * 25
    }

    /** Vínculo mínimo requerido por etapa: 1→0, 2→40, 3→85. */
    fun bondForStage(stage: Int): Int = when (stage) {
        1 -> 0
        2 -> 40
        else -> 85
    }

    /**
     * Coste de evolución desde la etapa indicada.
     * Claves: "gold" (oro) + ids de material de [EldoriaMaterials].
     */
    fun evolutionCost(stage: Int): Map<String, Int> = when (stage) {
        1 -> mapOf("gold" to 5_000, "crystal" to 8, "mystic_silk" to 4, "anima_shard" to 20)
        2 -> mapOf("gold" to 28_000, "pure_crystal" to 6, "dragon_scale" to 5, "ancient_relic" to 2, "anima_shard" to 75)
        else -> emptyMap()
    }

    /** Tope de cada disciplina: 40 + nivel*2, máximo 200. */
    fun disciplineCap(level: Int): Int = (40 + level.coerceAtLeast(1) * 2).coerceAtMost(200)

    /** Nivel máximo alcanzable por etapa de evolución. */
    fun levelCapForStage(stage: Int): Int = when (stage) {
        1 -> 30
        2 -> 60
        else -> 100
    }

    /** Nombre mostrado de la especie según la etapa (1..3). */
    fun stageName(speciesId: String, stage: Int): String {
        val sp = speciesIndex[speciesId] ?: return ""
        val idx = (stage - 1).coerceIn(0, sp.evolutionNames.size - 1)
        return sp.evolutionNames[idx]
    }
}
