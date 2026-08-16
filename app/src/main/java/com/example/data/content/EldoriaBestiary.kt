package com.example.data.content

import com.example.data.KingdomGenerator
import com.example.data.model.EnemyAffix
import com.example.data.model.EnemyArchetype
import com.example.data.model.EnemySpecies
import kotlin.random.Random

/**
 * Bestiario canónico de Eldoria Chronicles.
 *
 * 14 arquetipos · 16 afijos de élite · 72 especies (12 por cada uno de los 6 reinos).
 * Todo el contenido es determinista y no depende de Compose ni de recursos Android:
 * `artKey` es el nombre del drawable existente que la capa de UI resuelve.
 */
object EldoriaBestiary {

    // ───────────────────────────────────────────────────────────────────────
    //  ARQUETIPOS (14)
    // ───────────────────────────────────────────────────────────────────────

    const val BRUTO = "BRUTO"
    const val LANZADOR = "LANZADOR"
    const val BASTION = "BASTION"
    const val ACECHADOR = "ACECHADOR"
    const val ENJAMBRE = "ENJAMBRE"
    const val HOSTIGADOR = "HOSTIGADOR"
    const val INVOCADOR = "INVOCADOR"
    const val BERSERKER = "BERSERKER"
    const val NO_MUERTO = "NO_MUERTO"
    const val ELEMENTAL = "ELEMENTAL"
    const val CONSTRUCTO = "CONSTRUCTO"
    const val COLOSO = "COLOSO"
    const val VOLADOR = "VOLADOR"
    const val SANADOR_CORRUPTO = "SANADOR_CORRUPTO"

    val ARCHETYPES: List<EnemyArchetype> = listOf(
        EnemyArchetype(
            id = BRUTO, name = "Bruto",
            description = "Músculo sin arte. Golpea fuerte, aguanta lo justo y no conoce la retirada.",
            hpMult = 1.00f, atkMult = 1.15f, defMult = 0.95f,
            behaviour = "Ataca cada turno; cada tercer turno carga un Golpe Brutal que anuncia con un rugido.",
            tone = "BLOOD"
        ),
        EnemyArchetype(
            id = LANZADOR, name = "Lanzador Arcano",
            description = "Canaliza energía cruda a distancia. Frágil de cuerpo, letal de mente.",
            hpMult = 0.80f, atkMult = 1.35f, defMult = 0.75f,
            behaviour = "Alterna descargas mágicas y barreras; si baja del 30 % de vida canaliza su conjuro mayor.",
            tone = "ARCANE"
        ),
        EnemyArchetype(
            id = BASTION, name = "Bastión",
            description = "Muralla viviente. Existe para que nada pase por detrás de él.",
            hpMult = 1.25f, atkMult = 0.85f, defMult = 1.55f,
            behaviour = "Se atrinchera cada dos turnos duplicando su defensa y devuelve parte del daño recibido.",
            tone = "IRON"
        ),
        EnemyArchetype(
            id = ACECHADOR, name = "Acechador",
            description = "Nunca lo ves llegar. Cuando lo ves, ya has perdido sangre.",
            hpMult = 0.75f, atkMult = 1.45f, defMult = 0.80f,
            behaviour = "Se oculta un turno y reaparece con una Puñalada Trapera de crítico garantizado.",
            tone = "SILVER"
        ),
        EnemyArchetype(
            id = ENJAMBRE, name = "Enjambre",
            description = "Un individuo es nada. Mil individuos son una marea que devora.",
            hpMult = 0.60f, atkMult = 1.20f, defMult = 0.70f,
            behaviour = "Golpea tres veces por turno con daño bajo y gana ataque a medida que pierde vida.",
            tone = "VITAE"
        ),
        EnemyArchetype(
            id = HOSTIGADOR, name = "Hostigador",
            description = "Combate a distancia, hostiga, envenena y se retira antes del contragolpe.",
            hpMult = 0.85f, atkMult = 1.25f, defMult = 0.85f,
            behaviour = "Dispara andanadas que reducen tu precisión y mantiene la distancia salvo si lo acorralas.",
            tone = "SILVER"
        ),
        EnemyArchetype(
            id = INVOCADOR, name = "Invocador",
            description = "No lucha: abre puertas para que otros luchen por él.",
            hpMult = 0.95f, atkMult = 1.05f, defMult = 1.00f,
            behaviour = "Cada tres turnos llama a un secuaz menor que absorbe tu siguiente ataque.",
            tone = "ARCANE"
        ),
        EnemyArchetype(
            id = BERSERKER, name = "Berserker",
            description = "Cuanto más sangra, más rápido mata. La cordura fue lo primero que perdió.",
            hpMult = 1.10f, atkMult = 1.50f, defMult = 0.65f,
            behaviour = "Bajo el 50 % de vida entra en frenesí: +50 % de daño y pierde toda su defensa.",
            tone = "BLOOD"
        ),
        EnemyArchetype(
            id = NO_MUERTO, name = "No-Muerto",
            description = "Ya murió una vez. Convencerlo de morir otra vez cuesta el doble.",
            hpMult = 1.30f, atkMult = 0.95f, defMult = 1.05f,
            behaviour = "Al caer tiene una probabilidad de levantarse con un tercio de su vida.",
            tone = "ARCANE"
        ),
        EnemyArchetype(
            id = ELEMENTAL, name = "Elemental",
            description = "Materia primordial sin voluntad propia, sólo con hambre elemental.",
            hpMult = 0.90f, atkMult = 1.30f, defMult = 0.90f,
            behaviour = "Acumula carga elemental tres turnos y estalla en un área que ignora la armadura.",
            tone = "EMBER"
        ),
        EnemyArchetype(
            id = CONSTRUCTO, name = "Constructo",
            description = "Fabricado para una única orden y sin capacidad de dudarla.",
            hpMult = 1.20f, atkMult = 1.00f, defMult = 1.35f,
            behaviour = "Repite una secuencia fija de tres acciones; al final del ciclo se sobrecarga y golpea doble.",
            tone = "IRON"
        ),
        EnemyArchetype(
            id = COLOSO, name = "Coloso",
            description = "Montaña con intención. El suelo se agrieta antes de que te alcance.",
            hpMult = 1.60f, atkMult = 1.10f, defMult = 1.40f,
            behaviour = "Lento: actúa un turno de cada dos, pero su Impacto Sísmico golpea a todo el frente.",
            tone = "IRON"
        ),
        EnemyArchetype(
            id = VOLADOR, name = "Volador",
            description = "Domina el aire, y por tanto domina cuándo empieza y termina el combate.",
            hpMult = 0.80f, atkMult = 1.20f, defMult = 0.85f,
            behaviour = "Se eleva y esquiva un turno de cada tres; al descender ejecuta un Picado con crítico aumentado.",
            tone = "SILVER"
        ),
        EnemyArchetype(
            id = SANADOR_CORRUPTO, name = "Sanador Corrupto",
            description = "Reza a algo que nunca debió escucharle, y ese algo responde con vida robada.",
            hpMult = 1.05f, atkMult = 0.90f, defMult = 1.10f,
            behaviour = "Se cura un 15 % por turno y maldice tus curaciones reduciéndolas a la mitad.",
            tone = "VITAE"
        )
    )

    /** Movimiento firma mostrado como intención del enemigo. */
    private val SIGNATURE_BY_ARCHETYPE: Map<String, String> = mapOf(
        BRUTO to "GOLPE_BRUTAL",
        LANZADOR to "DESCARGA_ARCANA",
        BASTION to "MURO_DE_ESCUDOS",
        ACECHADOR to "PUÑALADA_TRAPERA",
        ENJAMBRE to "MAREA_DE_GARRAS",
        HOSTIGADOR to "ANDANADA",
        INVOCADOR to "LLAMADA_MENOR",
        BERSERKER to "FRENESÍ_SANGRIENTO",
        NO_MUERTO to "RESURRECCIÓN_IMPÍA",
        ELEMENTAL to "ESTALLIDO_ELEMENTAL",
        CONSTRUCTO to "SOBRECARGA",
        COLOSO to "IMPACTO_SÍSMICO",
        VOLADOR to "PICADO",
        SANADOR_CORRUPTO to "PLEGARIA_PUTREFACTA"
    )

    // ───────────────────────────────────────────────────────────────────────
    //  AFIJOS DE ÉLITE (16)
    // ───────────────────────────────────────────────────────────────────────

    val AFFIXES: List<EnemyAffix> = listOf(
        EnemyAffix("affix_acorazado", "Acorazado", "Reduce en un 20 % todo el daño físico que recibe.", "IRON", 3),
        EnemyAffix("affix_vampirico", "Vampírico", "Se cura con el 25 % del daño que inflige.", "BLOOD", 4),
        EnemyAffix("affix_ardiente", "Ardiente", "Sus golpes prenden al héroe durante 3 turnos.", "EMBER", 3),
        EnemyAffix("affix_gelido", "Gélido", "Cada impacto ralentiza y retrasa tu siguiente acción.", "SILVER", 2),
        EnemyAffix("affix_espinoso", "Espinoso", "Devuelve el 15 % del daño cuerpo a cuerpo recibido.", "VITAE", 2),
        EnemyAffix("affix_veloz", "Veloz", "Actúa dos veces cada tres turnos.", "ARCANE", 4),
        EnemyAffix("affix_regenerativo", "Regenerativo", "Recupera un 5 % de su vida máxima al final de cada turno.", "VITAE", 3),
        EnemyAffix("affix_explosivo", "Explosivo", "Al morir estalla y te causa daño igual al 12 % de tu vida.", "EMBER", 3),
        EnemyAffix("affix_blindado", "Blindado", "Su defensa aumenta un 35 % y anula el primer crítico recibido.", "IRON", 3),
        EnemyAffix("affix_maldito", "Maldito", "Todas tus curaciones se reducen a la mitad mientras viva.", "ARCANE", 4),
        EnemyAffix("affix_fantasmal", "Fantasmal", "Evade el 20 % de los ataques físicos.", "SILVER", 3),
        EnemyAffix("affix_divisor", "Divisor", "Al llegar al 50 % de vida se divide en dos copias menores.", "VITAE", 5),
        EnemyAffix("affix_aureo", "Áureo", "Suelta el triple de oro, pero pega un 15 % más fuerte.", "GOLD", 1),
        EnemyAffix("affix_ancestral", "Ancestral", "Todas sus estadísticas aumentan un 30 %.", "GOLD", 5),
        EnemyAffix("affix_toxico", "Tóxico", "Aplica veneno acumulable que ignora la armadura.", "VITAE", 2),
        EnemyAffix("affix_aturdidor", "Aturdidor", "Un 20 % de sus golpes te dejan aturdido un turno.", "BLOOD", 4)
    )

    // ───────────────────────────────────────────────────────────────────────
    //  ESPECIES (72 = 12 × 6 reinos)
    // ───────────────────────────────────────────────────────────────────────

    private fun sp(
        id: String, name: String, kingdomId: String, archetype: String,
        artKey: String, tier: Int, lore: String, weakness: String, resistance: String
    ) = EnemySpecies(
        id = id, name = name, kingdomId = kingdomId, archetype = archetype,
        signatureMove = SIGNATURE_BY_ARCHETYPE[archetype] ?: "GOLPE_BRUTAL",
        artKey = artKey, tier = tier, lore = lore, weakness = weakness, resistance = resistance
    )

    /** ELDORIA — tier 1 · valles esmeralda, ruinas y bosque susurrante. */
    private val ELDORIA_SPECIES: List<EnemySpecies> = listOf(
        sp("esp_eldoria_lobo_cenizo", "Lobo Cenizo", "eldoria", BRUTO,
            "bestiary_eldoria_lobo_cenizo", 1,
            "Los lobos que sobrevivieron al incendio del Valle del Alba nacieron con el pelaje gris de la ceniza. Cazan siempre contra el viento.",
            "FUEGO", "FÍSICO"),
        sp("esp_eldoria_duende_zarcero", "Duende Zarcero", "eldoria", ACECHADOR,
            "bestiary_eldoria_duende_zarcero", 1,
            "Vive entre las zarzas del camino real y roba tobillos antes que bolsas. Su risa se oye después de la primera herida.",
            "SAGRADO", "VENENO"),
        sp("esp_eldoria_espantapajaros_runico", "Espantapájaros Rúnico", "eldoria", CONSTRUCTO,
            "bestiary_eldoria_espantapajaros_runico", 1,
            "Un druida grabó runas de vigilia en la paja para proteger los trigales. Nadie recuerda haber escrito la orden de matar.",
            "FUEGO", "FÍSICO"),
        sp("esp_eldoria_musgoso_devorador", "Musgoso Devorador", "eldoria", BASTION,
            "bestiary_eldoria_musgoso_devorador", 1,
            "Bloque de musgo y piedra de río que tapona los senderos del bosque. Digiere lo que se apoya en él durante demasiado tiempo.",
            "FUEGO", "FÍSICO"),
        sp("esp_eldoria_avispa_reina_esmeralda", "Avispa Reina Esmeralda", "eldoria", ENJAMBRE,
            "bestiary_eldoria_avispa_reina_esmeralda", 1,
            "Su corte de obreras cubre un claro entero en tres latidos. La reina jamás pica: sólo señala.",
            "FUEGO", "VENENO"),
        sp("esp_eldoria_sabueso_del_alba", "Sabueso del Alba", "eldoria", HOSTIGADOR,
            "bestiary_eldoria_sabueso_del_alba", 1,
            "Criado en los establos de Ciudad Alba para rastrear desertores. Ladra tres veces antes de morder, siempre.",
            "ARCANO", "FÍSICO"),
        sp("esp_eldoria_druida_renegado", "Druida Renegado", "eldoria", LANZADOR,
            "bestiary_eldoria_druida_renegado", 1,
            "Rompió el Círculo cuando el bosque dejó de responderle. Ahora arranca respuestas a golpe de savia hervida.",
            "FÍSICO", "ARCANO"),
        sp("esp_eldoria_jabali_coronado", "Jabalí Coronado", "eldoria", BERSERKER,
            "bestiary_eldoria_jabali_coronado", 1,
            "Los aldeanos le clavaron una corona de hierro como burla; se le soldó al cráneo y le enseñó la ira de los reyes.",
            "ARCANO", "FÍSICO"),
        sp("esp_eldoria_ciervo_espectral", "Ciervo Espectral", "eldoria", VOLADOR,
            "bestiary_eldoria_ciervo_espectral", 1,
            "Aparece en el Lago Cristalino la noche en que alguien va a perderse. Sus cuernos no proyectan sombra.",
            "SAGRADO", "FÍSICO"),
        sp("esp_eldoria_raiz_estranguladora", "Raíz Estranguladora", "eldoria", COLOSO,
            "bestiary_eldoria_raiz_estranguladora", 1,
            "Una sola raíz del Gran Treant Corrupto, tan larga que su otro extremo sigue vivo a diez leguas de aquí.",
            "FUEGO", "FÍSICO"),
        sp("esp_eldoria_cuervo_presagio", "Cuervo de Presagio", "eldoria", VOLADOR,
            "bestiary_eldoria_cuervo_presagio", 1,
            "Grazna el nombre del siguiente en morir. Los veteranos de Eldoria aprenden pronto a no responder.",
            "RAYO", "SOMBRA"),
        sp("esp_eldoria_ermitano_podrido", "Ermitaño Podrido", "eldoria", NO_MUERTO,
            "bestiary_eldoria_ermitano_podrido", 1,
            "Rezó cuarenta años en las Ruinas Ancestrales pidiendo no morir jamás. La plegaria fue concedida sin piedad.",
            "SAGRADO", "SOMBRA")
    )

    /** DRAKENHOLD — tier 2 · obsidiana, azufre y forjas infernales. */
    private val DRAKENHOLD_SPECIES: List<EnemySpecies> = listOf(
        sp("esp_drakenhold_salamandra_escoria", "Salamandra de Escoria", "drakenhold", ELEMENTAL,
            "bestiary_drakenhold_salamandra_escoria", 2,
            "Nace en los desechos de las fundiciones de Drakenhold. Su piel es una costra de metal que aún gotea.",
            "HIELO", "FUEGO"),
        sp("esp_drakenhold_herrero_maldito", "Herrero Maldito", "drakenhold", BASTION,
            "bestiary_drakenhold_herrero_maldito", 2,
            "Juró no apagar su fragua hasta terminar el arma perfecta. Lleva doscientos años golpeando el mismo lingote.",
            "ARCANO", "FUEGO"),
        sp("esp_drakenhold_golem_magma", "Golem de Magma", "drakenhold", COLOSO,
            "bestiary_drakenhold_golem_magma", 2,
            "Costra de basalto sobre un corazón líquido. Cuando se agrieta, lo que sale ya no es piedra.",
            "HIELO", "FUEGO"),
        sp("esp_drakenhold_wyvern_ceniza", "Wyvern de Ceniza", "drakenhold", VOLADOR,
            "bestiary_drakenhold_wyvern_ceniza", 2,
            "Anida en las chimeneas del Cráter de Magma. Su aliento no quema: asfixia con ceniza caliente.",
            "RAYO", "FUEGO"),
        sp("esp_drakenhold_cultista_llama", "Cultista de la Llama", "drakenhold", LANZADOR,
            "bestiary_drakenhold_cultista_llama", 2,
            "Se arrancó los párpados para mirar el fuego eterno sin descanso. Asegura que el fuego le devolvió la mirada.",
            "FÍSICO", "FUEGO"),
        sp("esp_drakenhold_perro_obsidiana", "Perro de Obsidiana", "drakenhold", ACECHADOR,
            "bestiary_drakenhold_perro_obsidiana", 2,
            "Vidrio volcánico con forma de sabueso. Cada zancada deja astillas negras que siguen cortando después.",
            "FÍSICO", "SOMBRA"),
        sp("esp_drakenhold_vulcanoide_menor", "Vulcanoide Menor", "drakenhold", ENJAMBRE,
            "bestiary_drakenhold_vulcanoide_menor", 2,
            "Brotan a docenas de cada respiradero del Páramo de Azufre. Se apagan solos si logras alejarlos del calor.",
            "HIELO", "FUEGO"),
        sp("esp_drakenhold_portador_brasas", "Portador de Brasas", "drakenhold", INVOCADOR,
            "bestiary_drakenhold_portador_brasas", 2,
            "Carga un incensario con una brasa del primer incendio del mundo. De cada chispa nace un sirviente.",
            "HIELO", "FUEGO"),
        sp("esp_drakenhold_escupefuego_goblin", "Escupefuego Goblin", "drakenhold", HOSTIGADOR,
            "bestiary_drakenhold_escupefuego_goblin", 2,
            "Bebe aceite de forja y lo escupe sobre una antorcha. Su esperanza de vida rara vez pasa de dos estaciones.",
            "HIELO", "FUEGO"),
        sp("esp_drakenhold_titan_fundicion", "Titán de Fundición", "drakenhold", CONSTRUCTO,
            "bestiary_drakenhold_titan_fundicion", 2,
            "Autómata de las viejas acerías, programado para verter metal. Ya no distingue el molde del intruso.",
            "RAYO", "FUEGO"),
        sp("esp_drakenhold_murcielago_igneo", "Murciélago Ígneo", "drakenhold", ENJAMBRE,
            "bestiary_drakenhold_murcielago_igneo", 2,
            "Cuelga del techo de las galerías de magma como fruta ardiendo. Cae en oleadas al menor ruido.",
            "HIELO", "FUEGO"),
        sp("esp_drakenhold_chaman_azufre", "Chamán de Azufre", "drakenhold", SANADOR_CORRUPTO,
            "bestiary_drakenhold_chaman_azufre", 2,
            "Cura a los suyos vertiéndoles azufre líquido en las heridas. Ninguno se ha atrevido a rechazar el tratamiento.",
            "SAGRADO", "VENENO")
    )

    /** FROSTGARD — tier 3 · glaciares eternos, tundra y ventisca. */
    private val FROSTGARD_SPECIES: List<EnemySpecies> = listOf(
        sp("esp_frostgard_aullador_escarcha", "Aullador de Escarcha", "frostgard", BRUTO,
            "bestiary_frostgard_aullador_escarcha", 3,
            "Su aullido congela el aliento a media legua. Los cazadores de Frostgard lo usan para medir la distancia de la muerte.",
            "FUEGO", "HIELO"),
        sp("esp_frostgard_yeti_cristalino", "Yeti Cristalino", "frostgard", COLOSO,
            "bestiary_frostgard_yeti_cristalino", 3,
            "Lleva tanto tiempo en el Glaciar Eterno que el hielo creció dentro de su carne. Se mueve como una avalancha lenta.",
            "FUEGO", "HIELO"),
        sp("esp_frostgard_doncella_ventisquero", "Doncella del Ventisquero", "frostgard", LANZADOR,
            "bestiary_frostgard_doncella_ventisquero", 3,
            "Fue prometida al viento del norte en un pacto de aldea. El viento cumplió su parte y devolvió otra cosa.",
            "FUEGO", "HIELO"),
        sp("esp_frostgard_mamut_sepulcral", "Mamut Sepulcral", "frostgard", BASTION,
            "bestiary_frostgard_mamut_sepulcral", 3,
            "Murió bajo el hielo hace mil inviernos y el hielo se negó a soltarlo. Camina con su propia tumba a cuestas.",
            "FUEGO", "FÍSICO"),
        sp("esp_frostgard_vidente_congelada", "Vidente Congelada", "frostgard", SANADOR_CORRUPTO,
            "bestiary_frostgard_vidente_congelada", 3,
            "Ve el futuro en la escarcha de sus propios ojos. Cura a sus aliados con el tiempo que roba a los tuyos.",
            "SAGRADO", "HIELO"),
        sp("esp_frostgard_alma_nevisca", "Alma de Nevisca", "frostgard", NO_MUERTO,
            "bestiary_frostgard_alma_nevisca", 3,
            "Cada viajero perdido en la tundra deja un jirón de sí en la nieve. Cuando hay bastantes, se levantan juntos.",
            "SAGRADO", "HIELO"),
        sp("esp_frostgard_lanzahielo_enano", "Lanzahielo Enano", "frostgard", HOSTIGADOR,
            "bestiary_frostgard_lanzahielo_enano", 3,
            "Talla jabalinas de hielo azul en la Caverna de Hielo y las lanza sin errar a cien pasos.",
            "FUEGO", "HIELO"),
        sp("esp_frostgard_oso_glaciar_antiguo", "Oso Glaciar Antiguo", "frostgard", BERSERKER,
            "bestiary_frostgard_oso_glaciar_antiguo", 3,
            "Sobrevivió a tres generaciones de cazadores y guarda sus lanzas clavadas en el lomo como trofeos.",
            "FUEGO", "HIELO"),
        sp("esp_frostgard_serpiente_aguanieve", "Serpiente de Aguanieve", "frostgard", ACECHADOR,
            "bestiary_frostgard_serpiente_aguanieve", 3,
            "Nada bajo la costra de nieve como si fuera agua. Sólo la delata el crujido, y siempre demasiado tarde.",
            "FUEGO", "HIELO"),
        sp("esp_frostgard_estatua_sal", "Estatua de Sal", "frostgard", CONSTRUCTO,
            "bestiary_frostgard_estatua_sal", 3,
            "Los Picos Nevados están sembrados de figuras de sal con rostro humano. A veces una de ellas gira la cabeza.",
            "ARCANO", "HIELO"),
        sp("esp_frostgard_corneja_hielo", "Corneja de Hielo", "frostgard", VOLADOR,
            "bestiary_frostgard_corneja_hielo", 3,
            "Sus plumas son láminas de escarcha que se desprenden en pleno picado y siguen cortando en el aire.",
            "FUEGO", "HIELO"),
        sp("esp_frostgard_bruja_solsticio", "Bruja del Solsticio", "frostgard", INVOCADOR,
            "bestiary_frostgard_bruja_solsticio", 3,
            "Sólo tiene poder la noche más larga del año, y ha aprendido a arrastrar esa noche allí donde va.",
            "SAGRADO", "HIELO")
    )

    /** AETHELGARD — tier 4 · cónclaves arcanos, ciénaga maldita y necrópolis. */
    private val AETHELGARD_SPECIES: List<EnemySpecies> = listOf(
        sp("esp_aethelgard_custodio_runas", "Custodio de Runas", "aethelgard", BASTION,
            "bestiary_aethelgard_custodio_runas", 4,
            "Guarda la biblioteca sumergida del Cónclave. No lee las runas que protege: las es.",
            "FÍSICO", "ARCANO"),
        sp("esp_aethelgard_espectro_conclave", "Espectro del Cónclave", "aethelgard", NO_MUERTO,
            "bestiary_aethelgard_espectro_conclave", 4,
            "Los magos que votaron abrir el Vértice Umbrío siguen debatiendo, siglos después de perder los cuerpos.",
            "SAGRADO", "SOMBRA"),
        sp("esp_aethelgard_ojo_flotante", "Ojo Flotante Arcano", "aethelgard", VOLADOR,
            "bestiary_aethelgard_ojo_flotante", 4,
            "Fue el instrumento de vigilancia del Cónclave. Ahora vigila por vicio y dispara por costumbre.",
            "FÍSICO", "ARCANO"),
        sp("esp_aethelgard_automata_marfileno", "Autómata Marfileño", "aethelgard", CONSTRUCTO,
            "bestiary_aethelgard_automata_marfileno", 4,
            "Tallado en hueso de gigante y movido por un único pensamiento prestado. Repite ese pensamiento sin fin.",
            "RAYO", "SOMBRA"),
        sp("esp_aethelgard_sombra_bibliotecario", "Sombra de Bibliotecario", "aethelgard", ACECHADOR,
            "bestiary_aethelgard_sombra_bibliotecario", 4,
            "Sigue haciendo callar a los intrusos. El silencio que impone dura exactamente lo que dura una vida.",
            "SAGRADO", "SOMBRA"),
        sp("esp_aethelgard_quimera_sellada", "Quimera Sellada", "aethelgard", BERSERKER,
            "bestiary_aethelgard_quimera_sellada", 4,
            "Tres criaturas cosidas por un experimento prohibido y encerradas en un sello que ya sólo aguanta por costumbre.",
            "ARCANO", "VENENO"),
        sp("esp_aethelgard_coro_disonante", "Coro Disonante", "aethelgard", ENJAMBRE,
            "bestiary_aethelgard_coro_disonante", 4,
            "Docenas de voces sin garganta cantan la misma nota equivocada. Oírla demasiado tiempo agrieta la cordura.",
            "SAGRADO", "ARCANO"),
        sp("esp_aethelgard_tejedor_vacio", "Tejedor de Vacío", "aethelgard", LANZADOR,
            "bestiary_aethelgard_tejedor_vacio", 4,
            "Cose agujeros en la realidad con hilo de nada. Cada puntada le cuesta un recuerdo propio.",
            "FÍSICO", "ARCANO"),
        sp("esp_aethelgard_guardian_obelisco", "Guardián de Obelisco", "aethelgard", COLOSO,
            "bestiary_aethelgard_guardian_obelisco", 4,
            "Un obelisco de la Arboleda Arcana que decidió caminar. Bajo su base aún hay ofrendas sin recoger.",
            "ARCANO", "FÍSICO"),
        sp("esp_aethelgard_alquimista_fracturado", "Alquimista Fracturado", "aethelgard", INVOCADOR,
            "bestiary_aethelgard_alquimista_fracturado", 4,
            "Se dividió a sí mismo en frascos para vivir más. Cada frasco que rompes llama a los que faltan.",
            "FUEGO", "VENENO"),
        sp("esp_aethelgard_danzarina_eter", "Danzarina de Éter", "aethelgard", HOSTIGADOR,
            "bestiary_aethelgard_danzarina_eter", 4,
            "Baila entre dos planos a la vez, de modo que sólo la mitad de ella está donde apuntas.",
            "SAGRADO", "ARCANO"),
        sp("esp_aethelgard_sanguijuela_mana", "Sanguijuela de Maná", "aethelgard", SANADOR_CORRUPTO,
            "bestiary_aethelgard_sanguijuela_mana", 4,
            "No busca sangre: busca el maná que corre por debajo. Sana a sus crías con lo que te arranca.",
            "FUEGO", "ARCANO")
    )

    /** SOLARIA — tier 5 · dunas doradas, pirámides y tumbas solares. */
    private val SOLARIA_SPECIES: List<EnemySpecies> = listOf(
        sp("esp_solaria_escarabajo_solar", "Escarabajo Solar", "solaria", ENJAMBRE,
            "bestiary_solaria_escarabajo_solar", 5,
            "Su caparazón concentra la luz del mediodía en un punto ardiente. Vienen por millares al oler agua.",
            "HIELO", "FUEGO"),
        sp("esp_solaria_momia_dorada", "Momia Dorada", "solaria", NO_MUERTO,
            "bestiary_solaria_momia_dorada", 5,
            "Fue embalsamada con oro fundido para que su alma no encontrara la salida. Funcionó demasiado bien.",
            "FUEGO", "SOMBRA"),
        sp("esp_solaria_leon_alado_bronce", "León Alado de Bronce", "solaria", CONSTRUCTO,
            "bestiary_solaria_leon_alado_bronce", 5,
            "Custodia la puerta de la Pirámide del Sol desde antes de que existiera la puerta.",
            "RAYO", "FÍSICO"),
        sp("esp_solaria_sacerdotisa_rashen", "Sacerdotisa de Ra'shen", "solaria", SANADOR_CORRUPTO,
            "bestiary_solaria_sacerdotisa_rashen", 5,
            "Ofrece a su dios la vida de los intrusos y devuelve a los suyos justo la mitad de lo tomado.",
            "SOMBRA", "SAGRADO"),
        sp("esp_solaria_escorpion_ambar", "Escorpión de Ámbar", "solaria", ACECHADOR,
            "bestiary_solaria_escorpion_ambar", 5,
            "Su coraza translúcida guarda insectos atrapados hace mil años, y aún se mueven ahí dentro.",
            "HIELO", "VENENO"),
        sp("esp_solaria_espejismo_viviente", "Espejismo Viviente", "solaria", ELEMENTAL,
            "bestiary_solaria_espejismo_viviente", 5,
            "Toma la forma del oasis que más necesitas. Al beber descubres que el agua tenía dientes.",
            "ARCANO", "FUEGO"),
        sp("esp_solaria_coloso_arenisca", "Coloso de Arenisca", "solaria", COLOSO,
            "bestiary_solaria_coloso_arenisca", 5,
            "Cada tormenta de arena lo reconstruye un poco más grande. Nadie ha visto el original.",
            "RAYO", "FÍSICO"),
        sp("esp_solaria_halcon_fuego_blanco", "Halcón de Fuego Blanco", "solaria", VOLADOR,
            "bestiary_solaria_halcon_fuego_blanco", 5,
            "Anida en el disco solar según los sacerdotes. Su picado deja una línea recta de vidrio en la duna.",
            "SOMBRA", "FUEGO"),
        sp("esp_solaria_sepulturero_dunas", "Sepulturero de Dunas", "solaria", BRUTO,
            "bestiary_solaria_sepulturero_dunas", 5,
            "Enterró a los reyes del sol y luego se enterró a sí mismo. Sigue cavando por si vuelve alguno.",
            "SAGRADO", "FÍSICO"),
        sp("esp_solaria_serafin_caido", "Serafín Caído", "solaria", BERSERKER,
            "bestiary_solaria_serafin_caido", 5,
            "Sus alas se quemaron al atravesar el cielo de Solaria. Culpa de ello a todo lo que camina.",
            "SOMBRA", "SAGRADO"),
        sp("esp_solaria_djinn_encadenado", "Djinn Encadenado", "solaria", LANZADOR,
            "bestiary_solaria_djinn_encadenado", 5,
            "Concede un deseo por cada eslabón roto de su cadena, y él mismo elige qué eslabón romper.",
            "FÍSICO", "ARCANO"),
        sp("esp_solaria_devorador_soles", "Devorador de Soles", "solaria", BASTION,
            "bestiary_solaria_devorador_soles", 5,
            "La profecía dice que se tragará el disco solar. De momento se conforma con tragarse la luz de tu antorcha.",
            "SAGRADO", "FUEGO")
    )

    /** AETHERIA — tier 6 · vacío cósmico, islas astrales y entropía. */
    private val AETHERIA_SPECIES: List<EnemySpecies> = listOf(
        sp("esp_aetheria_heraldo_vacio", "Heraldo del Vacío", "aetheria", LANZADOR,
            "bestiary_aetheria_heraldo_vacio", 6,
            "Anuncia el final de las cosas con una voz que no usa aire. Los que la entienden dejan de existir primero.",
            "SAGRADO", "SOMBRA"),
        sp("esp_aetheria_fractal_viviente", "Fractal Viviente", "aetheria", ELEMENTAL,
            "bestiary_aetheria_fractal_viviente", 6,
            "Se repite a sí mismo hacia dentro sin fin. Cortarlo sólo revela otro igual, más pequeño y más furioso.",
            "FÍSICO", "ARCANO"),
        sp("esp_aetheria_pastor_estrellas", "Pastor de Estrellas", "aetheria", INVOCADOR,
            "bestiary_aetheria_pastor_estrellas", 6,
            "Guía rebaños de soles moribundos por el Cielo de Éter. Sabe el nombre exacto de cada uno.",
            "SOMBRA", "SAGRADO"),
        sp("esp_aetheria_larva_nebular", "Larva Nebular", "aetheria", ENJAMBRE,
            "bestiary_aetheria_larva_nebular", 6,
            "Cría de algo que aún no ha nacido. Se alimenta del polvo de las Nubes de Cristal y de todo lo demás.",
            "SAGRADO", "ARCANO"),
        sp("esp_aetheria_testigo_sin_rostro", "Testigo Sin Rostro", "aetheria", ACECHADOR,
            "bestiary_aetheria_testigo_sin_rostro", 6,
            "Estaba presente cuando el primer mundo terminó y no hizo nada. Sigue sin hacer nada, justo detrás de ti.",
            "SAGRADO", "SOMBRA"),
        sp("esp_aetheria_ancla_gravitatoria", "Ancla Gravitatoria", "aetheria", COLOSO,
            "bestiary_aetheria_ancla_gravitatoria", 6,
            "Sostiene una isla flotante entera. Si te acercas demasiado, tu peso deja de pertenecerte.",
            "ARCANO", "FÍSICO"),
        sp("esp_aetheria_eco_universo_muerto", "Eco de Universo Muerto", "aetheria", NO_MUERTO,
            "bestiary_aetheria_eco_universo_muerto", 6,
            "Todo lo que fue una creación entera, comprimido en una silueta que insiste en repetir su último día.",
            "SAGRADO", "SOMBRA"),
        sp("esp_aetheria_tejedora_constelaciones", "Tejedora de Constelaciones", "aetheria", SANADOR_CORRUPTO,
            "bestiary_aetheria_tejedora_constelaciones", 6,
            "Cose estrellas nuevas con el hilo de las viejas. Cura a los suyos apagando algo lejano y hermoso.",
            "SOMBRA", "ARCANO"),
        sp("esp_aetheria_simetria_rota", "Simetría Rota", "aetheria", CONSTRUCTO,
            "bestiary_aetheria_simetria_rota", 6,
            "Una ley física con cuerpo. Falló una sola vez y desde entonces intenta corregir el universo a golpes.",
            "RAYO", "ARCANO"),
        sp("esp_aetheria_devorador_entropia", "Devorador de Entropía", "aetheria", BERSERKER,
            "bestiary_aetheria_devorador_entropia", 6,
            "Se alimenta del desorden, y por eso el combate lo engorda. Cuanto peor va todo, más fuerte se vuelve.",
            "SAGRADO", "SOMBRA"),
        sp("esp_aetheria_peregrino_umbral", "Peregrino del Umbral", "aetheria", HOSTIGADOR,
            "bestiary_aetheria_peregrino_umbral", 6,
            "Camina de mundo en mundo buscando una puerta que no exista todavía. Cobra peaje a quien se cruza.",
            "ARCANO", "SOMBRA"),
        sp("esp_aetheria_cero_absoluto", "Cero Absoluto", "aetheria", BASTION,
            "bestiary_aetheria_cero_absoluto", 6,
            "El punto exacto donde el movimiento se rinde. A su alrededor incluso el sonido se queda quieto.",
            "FUEGO", "HIELO")
    )

    /** 72 especies: 12 por reino × 6 reinos. */
    val SPECIES: List<EnemySpecies> =
        ELDORIA_SPECIES + DRAKENHOLD_SPECIES + FROSTGARD_SPECIES +
            AETHELGARD_SPECIES + SOLARIA_SPECIES + AETHERIA_SPECIES

    // ───────────────────────────────────────────────────────────────────────
    //  ÍNDICES Y CONSULTAS
    // ───────────────────────────────────────────────────────────────────────

    private val archetypeIndex: Map<String, EnemyArchetype> = ARCHETYPES.associateBy { it.id }
    private val affixIndex: Map<String, EnemyAffix> = AFFIXES.associateBy { it.id }
    private val speciesIndex: Map<String, EnemySpecies> = SPECIES.associateBy { it.id }
    private val speciesByKingdom: Map<String, List<EnemySpecies>> = SPECIES.groupBy { it.kingdomId }

    /** Arquetipo por id; nunca nulo (cae en Bruto si el id es desconocido). */
    fun archetype(id: String): EnemyArchetype =
        archetypeIndex[id] ?: archetypeIndex[BRUTO] ?: ARCHETYPES.first()

    fun affix(id: String): EnemyAffix? = affixIndex[id]

    fun species(id: String): EnemySpecies? = speciesIndex[id]

    fun byKingdom(kingdomId: String): List<EnemySpecies> =
        speciesByKingdom[kingdomId.lowercase()] ?: emptyList()

    /** Los arquetipos que resultan buenos jefes (mucha vida o mucha amenaza). */
    private val BOSS_ARCHETYPES = setOf(COLOSO, BASTION, BERSERKER, NO_MUERTO, INVOCADOR, CONSTRUCTO)

    /**
     * Selección determinista de especie por (reino, nivel, rareza).
     * El nivel y la rareza sesgan hacia arquetipos más amenazantes sin romper el determinismo.
     */
    fun pick(kingdomId: String, level: Int, rarity: String, isBoss: Boolean, seed: Long): EnemySpecies {
        val pool = byKingdom(kingdomId).ifEmpty { SPECIES }
        val filtered = if (isBoss) {
            pool.filter { it.archetype in BOSS_ARCHETYPES }.ifEmpty { pool }
        } else {
            val r = rarity.uppercase()
            if (r == "ELITE" || r == "CHAMPION" || r == "LEGENDARY" || r == "UNIVERSAL") {
                pool.filter { it.archetype != ENJAMBRE }.ifEmpty { pool }
            } else pool
        }
        val mix = seed * 31L + level.toLong() * 7919L + rarity.uppercase().hashCode().toLong() +
            (if (isBoss) 104_729L else 0L)
        return filtered[Random(mix).nextInt(filtered.size)]
    }

    /** 0..3 afijos según rareza: NORMAL 0, ELITE 1, CHAMPION 2, LEGENDARY/UNIVERSAL 3. */
    fun rollAffixes(rarity: String, seed: Long): List<String> {
        val count = when (rarity.uppercase()) {
            "ELITE", "ÉLITE" -> 1
            "CHAMPION", "CAMPEON", "CAMPEÓN" -> 2
            "LEGENDARY", "LEGENDARIO", "UNIVERSAL", "BOSS", "JEFE" -> 3
            else -> 0
        }
        if (count <= 0) return emptyList()
        val rnd = Random(seed xor 0x5EA1_0FF5L)
        val pool = AFFIXES.map { it.id }.toMutableList()
        val out = ArrayList<String>(count)
        repeat(count) {
            if (pool.isEmpty()) return@repeat
            out.add(pool.removeAt(rnd.nextInt(pool.size)))
        }
        return out
    }

    /** Peligro acumulado de una lista de afijos (suma de dangerWeight). */
    fun affixDanger(affixIds: List<String>): Int =
        affixIds.sumOf { affixIndex[it]?.dangerWeight ?: 0 }

    /** Envuelve KingdomGenerator sin editarlo: coordenadas → id de reino en minúsculas. */
    fun kingdomIdForCoords(x: Int, y: Int): String =
        KingdomGenerator.getKingdomForCoords(x, y).id.lowercase()

    /** Tier canónico (1..6) de cada reino, usado para tablas de botín y contratos. */
    fun tierForKingdom(kingdomId: String): Int = when (kingdomId.lowercase()) {
        "eldoria" -> 1
        "drakenhold" -> 2
        "frostgard" -> 3
        "aethelgard" -> 4
        "solaria" -> 5
        "aetheria" -> 6
        else -> 1
    }
}
