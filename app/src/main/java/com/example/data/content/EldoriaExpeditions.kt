package com.example.data.content

import com.example.data.model.DungeonBlueprint
import com.example.data.model.ExpeditionBoon
import com.example.data.model.ExpeditionSeal
import kotlin.random.Random

/**
 * Catálogo de expediciones: 20 destinos (16 espejo exacto de `com.example.data.DUNGEONS_LIST`
 * + 4 Abismos nuevos con ids 101..104), 18 bendiciones, 12 sellos y los 11 tipos de sala.
 */
object EldoriaExpeditions {

    // ───────────────────────────────────────────────────────────────────────
    //  TIPOS DE SALA (11)
    // ───────────────────────────────────────────────────────────────────────

    const val KIND_COMBAT = "COMBATE"
    const val KIND_ELITE = "ELITE"
    const val KIND_TREASURE = "TESORO"
    const val KIND_CAMPFIRE = "HOGUERA"
    const val KIND_SHRINE = "SANTUARIO"
    const val KIND_TRAP = "TRAMPA"
    const val KIND_MERCHANT = "MERCADER"
    const val KIND_EVENT = "EVENTO"
    const val KIND_GATE = "PUERTA"
    const val KIND_BOSS = "JEFE"
    const val KIND_VOID = "VACIO"

    val ROOM_KINDS: List<String> = listOf(
        KIND_COMBAT, KIND_ELITE, KIND_TREASURE, KIND_CAMPFIRE, KIND_SHRINE,
        KIND_TRAP, KIND_MERCHANT, KIND_EVENT, KIND_GATE, KIND_BOSS, KIND_VOID
    )

    // ───────────────────────────────────────────────────────────────────────
    //  DESTINOS (20)
    // ───────────────────────────────────────────────────────────────────────

    /** Los 16 calabozos clásicos, reconstruidos como expedición. */
    private val CLASSIC_BLUEPRINTS: List<DungeonBlueprint> = listOf(
        DungeonBlueprint(
            dungeonId = 1, name = "Cavernas del Clan Goblin", species = "Goblins", levelReq = 20,
            paletteKey = "VITAE", artResName = "img_boss_hobgoblin_1784674116743", ambience = "GRUTA",
            floorLabels = listOf("Túneles de Entrada", "Cocinas de la Horda", "Salón del Warlord"),
            finalBossName = "Hobgoblin", finalBossTitle = "Gran Warlord Hobgoblin",
            loreShort = "Galerías húmedas talladas a mordiscos donde la plaga goblin cría, roba y engorda a su warlord."
        ),
        DungeonBlueprint(
            dungeonId = 2, name = "Fortaleza Orqueta de Hierro", species = "Orcos", levelReq = 40,
            paletteKey = "IRON", artResName = "img_enemy_ogre_1784386944311", ambience = "FORTALEZA",
            floorLabels = listOf("Empalizada Exterior", "Patio de Armas", "Trono de Hierro"),
            finalBossName = "Rey Orco", finalBossTitle = "Soberano Sangriento de la Horda",
            loreShort = "Bastión de estacas y hierro donde la horda afila las hachas para la próxima invasión."
        ),
        DungeonBlueprint(
            dungeonId = 3, name = "Guarida de las Sombras", species = "Ladrones", levelReq = 60,
            paletteKey = "SILVER", artResName = "img_portrait_humano_picaro_1784507327963", ambience = "CATACUMBA",
            floorLabels = listOf("Alcantarillas", "Casa de Apuestas", "Cámara del Gran Maestro"),
            finalBossName = "Ladrón Asesino", finalBossTitle = "Gran Maestro de la Guilda de las Sombras",
            loreShort = "Red de sótanos donde la Guilda cobra sus deudas con cuchillo y sin testigos."
        ),
        DungeonBlueprint(
            dungeonId = 4, name = "Colinas Ferales de las Bestias", species = "Hombres bestia", levelReq = 80,
            paletteKey = "VITAE", artResName = "img_enemy_boss_1784386985144", ambience = "BOSQUE",
            floorLabels = listOf("Linde de Zarzas", "Cañada de los Aullidos", "Cubil de Fenrir"),
            finalBossName = "Rey Lobo Fenrir", finalBossTitle = "Titán Primigenio de las Colinas",
            loreShort = "Colinas salvajes donde la manada decide quién entra y nadie decide quién sale."
        ),
        DungeonBlueprint(
            dungeonId = 5, name = "Fosa Abisal de las Mareas", species = "Naga", levelReq = 100,
            paletteKey = "MANA", artResName = "img_enemy_mud_golem_1784386930907", ambience = "INUNDADO",
            floorLabels = listOf("Templo Anegado", "Corriente de Coral", "Trono de Neptuno"),
            finalBossName = "Rey del Océano Neptuno", finalBossTitle = "Emperador de las Profundidades",
            loreShort = "Templo hundido donde los naga guardan el tridente que gobierna las mareas."
        ),
        DungeonBlueprint(
            dungeonId = 6, name = "Cripta Necrótica Sangrienta", species = "Muertos vivientes", levelReq = 120,
            paletteKey = "BLOOD", artResName = "img_boss_high_vampire_1784674139269", ambience = "CRIPTA",
            floorLabels = listOf("Osario Superior", "Galería de Sarcófagos", "Cámara del Conde"),
            finalBossName = "Vampiro de alto nivel", finalBossTitle = "Conde Sangriento Inmortal",
            loreShort = "Mausoleo ancestral donde la sangre de los intrusos paga el alquiler de la inmortalidad."
        ),
        DungeonBlueprint(
            dungeonId = 7, name = "Santuario de las Almas Perdidas", species = "Espíritus", levelReq = 140,
            paletteKey = "ARCANE", artResName = "img_enemy_spectre_1784386971041", ambience = "ETEREO",
            floorLabels = listOf("Umbral de Brumas", "Coro de Lamentos", "Sagrario del Necromante"),
            finalBossName = "Rey Necromancer", finalBossTitle = "Soberano Inmaterial del Infierno",
            loreShort = "Reino etéreo donde miles de almas atrapadas defienden al que las encadenó."
        ),
        DungeonBlueprint(
            dungeonId = 8, name = "Templo Viperino Esmeralda", species = "Hombres serpiente", levelReq = 160,
            paletteKey = "VITAE", artResName = "img_enemy_spider_1784386956688", ambience = "TEMPLO",
            floorLabels = listOf("Escalinata de Escamas", "Nido de Veneno", "Ábside del Rey Serpiente"),
            finalBossName = "Rey serpiente dragon", finalBossTitle = "Titán Viperino Primigenio",
            loreShort = "Pirámide esmeralda cuyos sacerdotes escamados adoran a una serpiente con alas de dragón."
        ),
        DungeonBlueprint(
            dungeonId = 9, name = "Laberinto Cibernético Titanium", species = "Máquinas", levelReq = 180,
            paletteKey = "SILVER", artResName = "img_boss_yggdrasil_machine_1784674150126", ambience = "MECANICO",
            floorLabels = listOf("Corredor de Servidores", "Sala de Reactores", "Núcleo de Igdrasil"),
            finalBossName = "Igdrasil El cerebro de las máquinas", finalBossTitle = "Superinteligencia Sintética Titánica",
            loreShort = "Complejo subterráneo que se reconfigura solo mientras calcula la forma óptima de detenerte."
        ),
        DungeonBlueprint(
            dungeonId = 10, name = "Abismo de la Calamidad", species = "Dragones", levelReq = 200,
            paletteKey = "EMBER", artResName = "img_boss_dark_dragon_1784674128719", ambience = "VOLCANICO",
            floorLabels = listOf("Cornisa de Escamas", "Nido de Wyrms", "Fauces de la Calamidad"),
            finalBossName = "Dragon Oscuro", finalBossTitle = "Emperador Supremo del Caos Inmemorial",
            loreShort = "Sima donde los nueve dragones supremos custodian el sueño del Dragón Oscuro."
        ),
        DungeonBlueprint(
            dungeonId = 11, name = "Santuario Serafín del Firmamento", species = "Serafines y Ángeles", levelReq = 220,
            paletteKey = "GOLD", artResName = "img_boss_yggdrasil_machine_1784674150126", ambience = "CELESTIAL",
            floorLabels = listOf("Escalera de Luz", "Coro de Querubines", "Trono del Archicreador"),
            finalBossName = "Archicreador Seraph", finalBossTitle = "Soberano Sagrado de los Cielos Astrales",
            loreShort = "Templo suspendido en el éter donde la luz juzga a los mortales antes de dejarles pasar."
        ),
        DungeonBlueprint(
            dungeonId = 12, name = "Cráter Abisal del Maelstrom", species = "Devoradores Abisales", levelReq = 250,
            paletteKey = "MANA", artResName = "img_enemy_spectre_1784386971041", ambience = "ABISAL",
            floorLabels = listOf("Borde del Remolino", "Garganta de Tentáculos", "Lecho de Cthulhu"),
            finalBossName = "Leviatán Cthulhu", finalBossTitle = "Titán Abisal Devorador de Mundos",
            loreShort = "Remolino insondable bajo las corrientes del multiverso, con algo enorme durmiendo al fondo."
        ),
        DungeonBlueprint(
            dungeonId = 13, name = "Forja Cósmica de los Titanidos", species = "Titanes de Piedra", levelReq = 280,
            paletteKey = "EMBER", artResName = "img_enemy_ogre_1784386944311", ambience = "FORJA",
            floorLabels = listOf("Andamios de Basalto", "Crisol de Estrellas", "Yunque de Aethel"),
            finalBossName = "Forjador Supremo Aethel", finalBossTitle = "Creador de las Estrellas Primigenias",
            loreShort = "La fragua primordial donde se moldearon los primeros soles y aún queda metal caliente."
        ),
        DungeonBlueprint(
            dungeonId = 14, name = "Infierno de la Llama Caótica", species = "Demonios Supremos", levelReq = 320,
            paletteKey = "BLOOD", artResName = "img_boss_hobgoblin_1784674116743", ambience = "INFERNAL",
            floorLabels = listOf("Puente de Azufre", "Corte de los Pecados", "Trono de Lucifer"),
            finalBossName = "Lucifer Señor del Inframundo", finalBossTitle = "Emperador Infernal del Caos Absoluto",
            loreShort = "El noveno círculo, donde las llamas eternas consumen a los caídos y los vuelven a servir."
        ),
        DungeonBlueprint(
            dungeonId = 15, name = "Vértice del Vacío Absoluto", species = "Espectros del Vacío", levelReq = 360,
            paletteKey = "ARCANE", artResName = "img_boss_high_vampire_1784674139269", ambience = "VACIO",
            floorLabels = listOf("Fisura Silente", "Corredor Sin Dimensión", "Ojo del Dios Olvidado"),
            finalBossName = "Sombra del Dios Olvidado", finalBossTitle = "Deidad Omnipotente Inmemorial",
            loreShort = "La nada más allá del espacio-tiempo, donde hasta el nombre del héroe se borra al entrar."
        ),
        DungeonBlueprint(
            dungeonId = 16, name = "Trono del Gran Multiverso", species = "Dioses Primigenios", levelReq = 400,
            paletteKey = "GOLD", artResName = "img_boss_dark_dragon_1784674128719", ambience = "COSMICO",
            floorLabels = listOf("Anillo de Mundos", "Balanza del Destino", "Trono de Ouroboros"),
            finalBossName = "Ouroboros el Eterno", finalBossTitle = "Señor del Infinito y Creador del Multiverso",
            loreShort = "La cúspide de toda la existencia, donde el círculo se cierra y decide si vuelve a abrirse."
        )
    )

    /** Los 4 Abismos nuevos (ids 101..104), cada uno con su regla propia. */
    private val ABYSS_BLUEPRINTS: List<DungeonBlueprint> = listOf(
        DungeonBlueprint(
            dungeonId = 101, name = "Abismo de las Mareas Negras", species = "Naga corruptos", levelReq = 150,
            paletteKey = "MANA", artResName = "img_enemy_mud_golem_1784386930907", ambience = "INUNDADO",
            floorLabels = listOf("Rompiente de Brea", "Arrecife Ahogado", "Fosa de Mareas Negras"),
            finalBossName = "Madre de las Mareas Negras", finalBossTitle = "Soberana del Agua Podrida",
            loreShort = "El agua aquí traga la luz: la antorcha se consume el doble, pero hay dos hogueras extra escondidas.",
            isAbyss = true
        ),
        DungeonBlueprint(
            dungeonId = 102, name = "Abismo del Reloj Roto", species = "Constructos temporales", levelReq = 220,
            paletteKey = "SILVER", artResName = "enemy_automaton_1784850938702", ambience = "MECANICO",
            floorLabels = listOf("Engranaje Detenido", "Péndulo Invertido", "Cámara del Reloj Roto"),
            finalBossName = "Guardián del Instante", finalBossTitle = "Custodio del Segundo que Nunca Pasa",
            loreShort = "Todo enemigo llega con el afijo Veloz garantizado y los fragmentos de ánima rinden un 50 % más.",
            isAbyss = true
        ),
        DungeonBlueprint(
            dungeonId = 103, name = "Abismo de la Corona Hueca", species = "Reyes no-muertos", levelReq = 300,
            paletteKey = "GOLD", artResName = "enemy_lich_1784850885522", ambience = "CRIPTA",
            floorLabels = listOf("Galería de Coronas", "Sala de los Juramentos", "Trono Hueco"),
            finalBossName = "Rey de la Corona Hueca", finalBossTitle = "Monarca de las Coronas Vacías",
            loreShort = "Aquí ningún rey acepta morir: todo enemigo revive una vez y, a cambio, el botín sube una rareza.",
            isAbyss = true
        ),
        DungeonBlueprint(
            dungeonId = 104, name = "Abismo del Eco Final", species = "Vacío cósmico", levelReq = 380,
            paletteKey = "ARCANE", artResName = "enemy_demon_1784903246195", ambience = "VACIO",
            floorLabels = listOf("Primer Eco", "Segundo Eco", "Tercer Eco", "Eco Final"),
            finalBossName = "El Último Eco", finalBossTitle = "Repetición Final de Todo lo que Fue",
            loreShort = "Cuatro profundidades en lugar de tres y el Sello del Vacío es obligatorio: el mapa se apaga.",
            isAbyss = true
        )
    )

    /** 20 destinos: 16 clásicos + 4 Abismos. */
    val BLUEPRINTS: List<DungeonBlueprint> = CLASSIC_BLUEPRINTS + ABYSS_BLUEPRINTS

    // ───────────────────────────────────────────────────────────────────────
    //  BENDICIONES (18)
    // ───────────────────────────────────────────────────────────────────────

    val BOONS: List<ExpeditionBoon> = listOf(
        ExpeditionBoon(
            id = "boon_furia_yunque", name = "Furia del Yunque",
            description = "El eco de mil martillazos guía tu brazo: +18 % de ataque.",
            tone = "EMBER", atkPct = 18
        ),
        ExpeditionBoon(
            id = "boon_piel_basalto", name = "Piel de Basalto",
            description = "Tu piel se cubre de roca volcánica: +20 % de defensa.",
            tone = "IRON", defPct = 20
        ),
        ExpeditionBoon(
            id = "boon_corazon_roble", name = "Corazón de Roble",
            description = "Late lento y hondo como un árbol viejo: +15 % de vida máxima.",
            tone = "VITAE", hpPct = 15
        ),
        ExpeditionBoon(
            id = "boon_ojo_halcon", name = "Ojo del Halcón",
            description = "Ves la juntura de cada armadura: +6 % de probabilidad de crítico.",
            tone = "SILVER", critPct = 6
        ),
        ExpeditionBoon(
            id = "boon_bolsa_sin_fondo", name = "Bolsa Sin Fondo",
            description = "Cabe todo y algo más: +25 % de oro durante la expedición.",
            tone = "GOLD", goldPct = 25
        ),
        ExpeditionBoon(
            id = "boon_llama_eterna", name = "Llama Eterna",
            description = "Tu antorcha se niega a apagarse: +30 puntos de antorcha al instante.",
            tone = "EMBER", torchPct = 30
        ),
        ExpeditionBoon(
            id = "boon_sangre_hirviente", name = "Sangre Hirviente",
            description = "El dolor se vuelve combustible: +30 % de ataque a costa de tu guardia.",
            tone = "BLOOD", atkPct = 30, defPct = -15,
            drawback = "Pierdes un 15 % de defensa mientras dure la expedición."
        ),
        ExpeditionBoon(
            id = "boon_bendicion_vado", name = "Bendición del Vado",
            description = "Al limpiar cada sala recuperas un 8 % de tu vida máxima.",
            tone = "VITAE"
        ),
        ExpeditionBoon(
            id = "boon_runa_eco", name = "Runa de Eco",
            description = "Cada habilidad resuena dos veces: +20 % de daño de habilidad.",
            tone = "ARCANE"
        ),
        ExpeditionBoon(
            id = "boon_ampolla_vida", name = "Ampolla de Vida",
            description = "Recibes al momento 3 pociones de vida para esta expedición.",
            tone = "VITAE"
        ),
        ExpeditionBoon(
            id = "boon_ofrenda_sangre", name = "Ofrenda de Sangre",
            description = "Ofreces vitalidad a cambio de furia: +35 % de daño y −20 % de vida máxima.",
            tone = "BLOOD", atkPct = 35, hpPct = -20,
            drawback = "Tu vida máxima cae un 20 % durante toda la expedición."
        ),
        ExpeditionBoon(
            id = "boon_manto_sombras", name = "Manto de Sombras",
            description = "La penumbra se te pega al cuerpo: +10 puntos de evasión.",
            tone = "ARCANE"
        ),
        ExpeditionBoon(
            id = "boon_reloj_arena", name = "Reloj de Arena",
            description = "El primer turno de cada combate lo juegas dos veces.",
            tone = "SILVER"
        ),
        ExpeditionBoon(
            id = "boon_faro_ancestral", name = "Faro Ancestral",
            description = "Revela de golpe todas las salas de la profundidad actual.",
            tone = "GOLD"
        ),
        ExpeditionBoon(
            id = "boon_cadena_rota", name = "Cadena Rota",
            description = "Rompe y elimina uno de los sellos activos de esta expedición.",
            tone = "IRON"
        ),
        ExpeditionBoon(
            id = "boon_semilla_voraz", name = "Semilla Voraz",
            description = "Todo el botín de la expedición sube un nivel de rareza.",
            tone = "VITAE", lootTiers = 1
        ),
        ExpeditionBoon(
            id = "boon_pacto_fenix", name = "Pacto del Fénix",
            description = "La primera vez que caigas, revives con el 40 % de tu vida.",
            tone = "EMBER"
        ),
        ExpeditionBoon(
            id = "boon_voz_abismo", name = "Voz del Abismo",
            description = "El abismo te paga por escucharlo: +50 % de fragmentos de ánima.",
            tone = "ARCANE"
        )
    )

    // ───────────────────────────────────────────────────────────────────────
    //  SELLOS (12) — máximo 3 por expedición
    // ───────────────────────────────────────────────────────────────────────

    val SEALS: List<ExpeditionSeal> = listOf(
        ExpeditionSeal("seal_avaricia", "Sello de Avaricia",
            "Los enemigos tienen un 30 % más de vida, pero el oro se duplica.", 1.30f, 1.60f, "GOLD"),
        ExpeditionSeal("seal_ayuno", "Sello del Ayuno",
            "No puedes usar pociones; a cambio, todo el botín sube 1 rareza.", 1.35f, 1.50f, "SILVER"),
        ExpeditionSeal("seal_prisa", "Sello de la Prisa",
            "Cada combate se resuelve como mucho en 20 turnos o lo pierdes.", 1.25f, 1.30f, "EMBER"),
        ExpeditionSeal("seal_niebla", "Sello de la Niebla",
            "Empiezas la expedición con sólo 60 puntos de antorcha.", 1.20f, 1.25f, "IRON"),
        ExpeditionSeal("seal_hierro", "Sello de Hierro",
            "Los enemigos ganan un 25 % de defensa; los materiales se duplican.", 1.25f, 1.45f, "IRON"),
        ExpeditionSeal("seal_silencio", "Sello del Silencio",
            "Llevas una habilidad equipada menos durante toda la expedición.", 1.30f, 1.35f, "ARCANE"),
        ExpeditionSeal("seal_sangre", "Sello de la Sangre",
            "No recuperas vida al pasar de una sala a otra.", 1.40f, 1.50f, "BLOOD"),
        ExpeditionSeal("seal_eco", "Sello del Eco",
            "Todo enemigo aparece como Élite, incluido el más humilde.", 1.60f, 1.80f, "ARCANE"),
        ExpeditionSeal("seal_grieta", "Sello de la Grieta",
            "El calabozo gana una profundidad adicional.", 1.45f, 1.70f, "EMBER"),
        ExpeditionSeal("seal_hambre", "Sello del Hambre",
            "Tu mascota pierde 3 puntos de saciedad por cada sala superada.", 1.15f, 1.20f, "VITAE"),
        ExpeditionSeal("seal_juicio", "Sello del Juicio",
            "El jefe final comienza el combate con un afijo adicional.", 1.35f, 1.40f, "BLOOD"),
        ExpeditionSeal("seal_vacio", "Sello del Vacío",
            "El mapa permanece oculto salvo las salas contiguas a la tuya.", 1.30f, 1.35f, "ARCANE")
    )

    // ───────────────────────────────────────────────────────────────────────
    //  ÍNDICES Y CONSULTAS
    // ───────────────────────────────────────────────────────────────────────

    private val blueprintIndex: Map<Int, DungeonBlueprint> = BLUEPRINTS.associateBy { it.dungeonId }
    private val boonIndex: Map<String, ExpeditionBoon> = BOONS.associateBy { it.id }
    private val sealIndex: Map<String, ExpeditionSeal> = SEALS.associateBy { it.id }

    fun blueprint(dungeonId: Int): DungeonBlueprint? = blueprintIndex[dungeonId]

    fun boon(id: String): ExpeditionBoon? = boonIndex[id]

    fun seal(id: String): ExpeditionSeal? = sealIndex[id]

    /** Sólo los Abismos (ids 101..104). */
    fun abysses(): List<DungeonBlueprint> = BLUEPRINTS.filter { it.isAbyss }

    /** Profundidades por defecto de un destino: 3, salvo el Abismo del Eco Final (4). */
    fun defaultMaxDepth(dungeonId: Int): Int = if (dungeonId == 104) 4 else 3

    /** Sellos obligatorios impuestos por el destino. */
    fun mandatorySeals(dungeonId: Int): List<String> =
        if (dungeonId == 104) listOf("seal_vacio") else emptyList()

    // ───────────────────────────────────────────────────────────────────────
    //  ETIQUETAS E ICONOS DE SALA
    // ───────────────────────────────────────────────────────────────────────

    private val LABELS_COMBAT = listOf(
        "Corredor Vigilado", "Galería de Emboscada", "Cámara de Guardia", "Puesto Avanzado",
        "Nave Derruida", "Antesala Manchada", "Pasaje de Huesos", "Rellano de Piedra"
    )
    private val LABELS_ELITE = listOf(
        "Guarida del Campeón", "Arena Menor", "Cámara del Verdugo", "Foso del Elegido",
        "Salón del Portaestandarte", "Cripta del Adalid"
    )
    private val LABELS_TREASURE = listOf(
        "Cámara del Tesoro", "Alacena Sellada", "Depósito Olvidado", "Bóveda Agrietada",
        "Arcón del Saqueador", "Nicho Dorado"
    )
    private val LABELS_CAMPFIRE = listOf(
        "Hoguera del Refugio", "Rescoldo del Peregrino", "Fogata Abandonada",
        "Vivac de Cazadores", "Brasero Encendido"
    )
    private val LABELS_SHRINE = listOf(
        "Santuario Olvidado", "Altar de Ceniza", "Capilla Sumergida",
        "Piedra de Ofrenda", "Nicho de la Bendición"
    )
    private val LABELS_TRAP = listOf(
        "Corredor de Cuchillas", "Losa Falsa", "Pasillo de Púas",
        "Techo Descendente", "Foso Camuflado", "Galería Envenenada"
    )
    private val LABELS_MERCHANT = listOf(
        "Puesto del Buhonero", "Carro Ambulante", "Tenderete de la Grieta",
        "Chamarilero del Abismo"
    )
    private val LABELS_EVENT = listOf(
        "Encuentro Extraño", "Voz en la Oscuridad", "Cuerpo Aún Caliente",
        "Inscripción Reciente", "Espejo Rajado", "Trato Susurrado"
    )
    private val LABELS_GATE = listOf(
        "Puerta Sellada", "Portón de Runas", "Reja de Hierro Negro", "Umbral Cerrado"
    )
    private val LABELS_VOID = listOf(
        "Vacío Silente", "Hueco Sin Nombre", "Corredor que No Está"
    )
    private val LABELS_BOSS = listOf(
        "Guarida del Señor", "Salón Final", "Trono del Abismo", "Cámara del Amo"
    )

    /** Etiqueta determinista para una sala concreta. */
    fun roomLabel(kind: String, depth: Int, seed: Long): String {
        val pool = when (kind.uppercase()) {
            KIND_COMBAT -> LABELS_COMBAT
            KIND_ELITE -> LABELS_ELITE
            KIND_TREASURE -> LABELS_TREASURE
            KIND_CAMPFIRE -> LABELS_CAMPFIRE
            KIND_SHRINE -> LABELS_SHRINE
            KIND_TRAP -> LABELS_TRAP
            KIND_MERCHANT -> LABELS_MERCHANT
            KIND_EVENT -> LABELS_EVENT
            KIND_GATE -> LABELS_GATE
            KIND_VOID -> LABELS_VOID
            KIND_BOSS -> LABELS_BOSS
            else -> LABELS_COMBAT
        }
        val rnd = Random(seed * 1_000_003L + depth.toLong() * 7L + kind.hashCode().toLong())
        return pool[rnd.nextInt(pool.size)]
    }

    /** Clave lógica de icono; el agente de UI la mapea a un ImageVector. */
    fun roomIcon(kind: String): String = when (kind.uppercase()) {
        KIND_COMBAT -> "SWORD"
        KIND_ELITE -> "SKULL"
        KIND_TREASURE -> "CHEST"
        KIND_CAMPFIRE -> "CAMPFIRE"
        KIND_SHRINE -> "SHRINE"
        KIND_TRAP -> "TRAP"
        KIND_MERCHANT -> "MERCHANT"
        KIND_EVENT -> "EVENT"
        KIND_GATE -> "GATE"
        KIND_BOSS -> "CROWN"
        KIND_VOID -> "VOID"
        else -> "SWORD"
    }

    /** Nombre legible en español de un tipo de sala. */
    fun roomKindName(kind: String): String = when (kind.uppercase()) {
        KIND_COMBAT -> "Combate"
        KIND_ELITE -> "Élite"
        KIND_TREASURE -> "Tesoro"
        KIND_CAMPFIRE -> "Hoguera"
        KIND_SHRINE -> "Santuario"
        KIND_TRAP -> "Trampa"
        KIND_MERCHANT -> "Mercader"
        KIND_EVENT -> "Evento"
        KIND_GATE -> "Puerta Sellada"
        KIND_BOSS -> "Jefe"
        KIND_VOID -> "Vacío"
        else -> "Combate"
    }

    /** Multiplicador de peligro acumulado de una lista de sellos. */
    fun dangerMultiplierOf(seals: List<String>): Float {
        var m = 1f
        seals.forEach { id -> sealIndex[id]?.let { m *= it.dangerMult } }
        return m
    }

    /** Multiplicador de recompensa acumulado de una lista de sellos. */
    fun rewardMultiplierOf(seals: List<String>): Float {
        var m = 1f
        seals.forEach { id -> sealIndex[id]?.let { m *= it.rewardMult } }
        return m
    }
}
