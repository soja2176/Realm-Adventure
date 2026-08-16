package com.example.data.content

// ══════════════════════════════════════════════════════════════════════════════
//  POCIONES
//
//  LO QUE HABÍA
//  Una sola poción — "Rejuvenecedora", 50 % de vida y maná — y un botón que la
//  bebía. Con un único consumible no hay decisión: o la bebes o no. Y en un
//  calabozo donde el jefe pide cinco barras de vida, curar la mitad no era una
//  jugada, era un trámite que se repetía hasta quedarse sin frascos.
//
//  LO QUE HAY AHORA
//  Seis frascos con papeles distintos, y sobre todo dos familias:
//
//    · CURACIÓN — la Menor sigue siendo el frasco barato de siempre; la Mayor
//      llena la barra entera; el Elixir cura POR TURNOS, que es lo que sirve
//      cuando el problema no es un pico de daño sino aguantar diez asaltos.
//
//    · EFECTO — furia (pegas más), sombras (esquivas), piedra (encajas menos).
//      No curan nada. Existen para que beber sea una decisión sobre CÓMO vas a
//      ganar el combate y no sólo sobre cuánta vida te queda.
//
//  Las de efecto no se acumulan consigo mismas: beber dos de furia renueva la
//  duración, no dobla el daño. Sin esa regla, la jugada óptima sería siempre
//  encadenar frascos del mismo tipo y las demás pociones sobrarían.
// ══════════════════════════════════════════════════════════════════════════════

/** Qué hace un frasco al beberlo. */
enum class PotionEffect {
    /** Restaura vida y maná al instante. */
    RESTORE,
    /** Cura un porcentaje de la vida máxima al principio de cada turno. */
    REGEN,
    /** Sube el daño que haces. */
    DAMAGE,
    /** Sube la probabilidad de esquivar. */
    EVASION,
    /** Baja el daño que recibes. */
    DEFENSE
}

data class PotionSpec(
    val id: String,
    val name: String,
    val rarity: String,
    val effect: PotionEffect,
    /** Precio en oro. */
    val price: Int,
    /** Fracción de vida máxima que restaura al instante (RESTORE). */
    val healPct: Double = 0.0,
    /** Fracción de maná máximo que restaura al instante (RESTORE). */
    val manaPct: Double = 0.0,
    /** Magnitud del efecto por turno o del porcentaje de buff. */
    val potency: Double = 0.0,
    /** Turnos que dura. Cero para las de efecto instantáneo. */
    val turns: Int = 0,
    val artKey: String,
    val description: String,
    /** Nivel de héroe a partir del cual el mercader la vende. */
    val unlockLevel: Int = 1
)

object EldoriaPotions {

    val ALL: List<PotionSpec> = listOf(
        PotionSpec(
            id = "pot_menor", name = "Poción Menor", rarity = "COMÚN",
            effect = PotionEffect.RESTORE, price = 40,
            healPct = 0.50, manaPct = 0.50,
            artKey = "potion_menor",
            description = "Restaura la mitad de tu salud y maná. El frasco de siempre, el que se acaba primero.",
            unlockLevel = 1
        ),
        PotionSpec(
            id = "pot_mayor", name = "Gran Poción de Vida", rarity = "RARO",
            effect = PotionEffect.RESTORE, price = 320,
            healPct = 1.0, manaPct = 1.0,
            artKey = "potion_mayor",
            description = "Llena la barra entera, vida y maná. Se guarda para el turno en que todo se tuerce.",
            unlockLevel = 8
        ),
        PotionSpec(
            id = "pot_regen", name = "Elixir de Regeneración", rarity = "ÉPICO",
            effect = PotionEffect.REGEN, price = 520,
            potency = 0.12, turns = 5,
            artKey = "potion_regen",
            description = "Cura un 12 % de tu salud al principio de cada turno durante 5 turnos. Para aguantar, no para salvarse.",
            unlockLevel = 14
        ),
        PotionSpec(
            id = "pot_furia", name = "Filtro de Furia", rarity = "ÉPICO",
            effect = PotionEffect.DAMAGE, price = 480,
            potency = 0.35, turns = 4,
            artKey = "potion_furia",
            description = "Tus golpes hacen un 35 % más de daño durante 4 turnos. No cura nada: es la apuesta por acabar antes.",
            unlockLevel = 12
        ),
        PotionSpec(
            id = "pot_sombra", name = "Tónico de Sombras", rarity = "ÉPICO",
            effect = PotionEffect.EVASION, price = 460,
            potency = 0.30, turns = 3,
            artKey = "potion_sombra",
            description = "Un 30 % de probabilidad de esquivar por completo durante 3 turnos. Contra un jefe que pega fuerte, esquivar vale más que curar.",
            unlockLevel = 12
        ),
        PotionSpec(
            id = "pot_piedra", name = "Bálsamo de Piedra", rarity = "ÉPICO",
            effect = PotionEffect.DEFENSE, price = 500,
            potency = 0.35, turns = 4,
            artKey = "potion_piedra",
            description = "Reduce en un 35 % el daño que recibes durante 4 turnos. La respuesta a las fases de enfurecimiento.",
            unlockLevel = 15
        )
    )

    private val byId = ALL.associateBy { it.id }

    fun spec(id: String): PotionSpec? = byId[id]

    /** Lo que el mercader ofrece a un héroe de este nivel. */
    fun purchasable(heroLevel: Int): List<PotionSpec> =
        ALL.filter { heroLevel >= it.unlockLevel }

    /**
     * Identifica el frasco de un objeto del inventario.
     *
     * Las pociones se guardan como `Item` normales, y las que ya existían en las
     * partidas guardadas no tienen id del catálogo. Por eso el respaldo por
     * nombre: sin él, cada poción comprada antes de este cambio se volvería
     * inutilizable de golpe.
     */
    fun fromItem(itemId: String, itemName: String): PotionSpec {
        byId.values.firstOrNull { itemId.startsWith(it.id) }?.let { return it }
        byId.values.firstOrNull { itemName.equals(it.name, ignoreCase = true) }?.let { return it }
        return byId.getValue("pot_menor")
    }
}
