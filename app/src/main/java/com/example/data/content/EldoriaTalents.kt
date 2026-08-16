package com.example.data.content

// ══════════════════════════════════════════════════════════════════════════════
//  REGISTRO DE TALENTOS
//
//  Punto unico de acceso al arbol. El resto del juego —combate, pantalla,
//  auto-combate— entra por aqui y nunca por los ficheros de cada raza, de modo
//  que añadir una raza es añadir un fichero y una linea en [BY_RACE].
// ══════════════════════════════════════════════════════════════════════════════

object EldoriaTalents {

    val BY_RACE: Map<String, List<TalentDef>> = mapOf(
        "Humano" to TALENTS_HUMANO,
        "Enano" to TALENTS_ENANO,
        "Elfo" to TALENTS_ELFO,
        "Orco" to TALENTS_ORCO
    )

    val ALL: List<TalentDef> = BY_RACE.values.flatten()

    private val byId: Map<String, TalentDef> = ALL.associateBy { it.id }

    fun def(id: String): TalentDef? = byId[id]

    /** Arbol de una raza. Vacio si la raza no tiene red propia. */
    fun forRace(race: String): List<TalentDef> = BY_RACE[race].orEmpty()

    /** Ramas con contenido en esa raza, en el orden del enum. */
    fun branchesOf(race: String): List<TalentBranch> =
        forRace(race).map { it.branch }.distinct().sortedBy { it.ordinal }

    /**
     * Talentos de una rama, ordenados por escalon.
     *
     * El escalon ([TalentDef.tier]) es lo que la pantalla usa como fila: con
     * cien nodos por raza no cabe un mapa libre, y una rama por columna con
     * escalones por fila se recorre con el pulgar.
     */
    fun branch(race: String, branch: TalentBranch): List<TalentDef> =
        forRace(race).filter { it.branch == branch }.sortedWith(compareBy({ it.tier }, { it.id }))

    /**
     * Si un talento esta disponible para un heroe de este nivel.
     *
     * Los de evolucion piden haber alcanzado su etapa: no basta con tener el
     * punto, hay que ser ya Cruzado, Abanderado o Heraldo. Es lo que hace que
     * evolucionar signifique algo mas que un numero mayor al lado del nombre.
     */
    fun isUnlocked(def: TalentDef, heroLevel: Int): Boolean =
        def.evolutionTier <= EldoriaTalentEngine.evolutionTierFor(heroLevel)

    /** Construye el loadout de un heroe a partir de los rangos que tiene puestos. */
    fun loadoutFor(race: String, ranks: Map<String, Int>): TalentLoadout =
        EldoriaTalentEngine.loadout(forRace(race), ranks)

    /**
     * Claves de lamina que el arbol espera, para pasarselas al equipo de arte.
     * Mientras no existan, la pantalla dibuja el sigilo procedural.
     */
    fun artKeyManifest(): List<String> = ALL.map { it.artKey }.sorted()
}
