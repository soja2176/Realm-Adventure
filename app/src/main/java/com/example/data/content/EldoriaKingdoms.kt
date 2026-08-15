package com.example.data.content

import com.example.data.KingdomData
import com.example.data.KingdomGenerator
import kotlin.math.abs
import kotlin.math.max

// ══════════════════════════════════════════════════════════════════════════════
//  ATLAS DE LOS SEIS REINOS
//
//  El mundo no es una cuadrícula plana: son seis anillos concéntricos alrededor
//  del Santuario Inicial (0,0). Cuanto más lejos del centro, más viejo y más
//  hostil es el territorio. `KingdomGenerator` ya reparte los biomas y bestias
//  por esa distancia; este atlas le pone encima lo que faltaba para poder
//  ENSEÑAR el mundo y VIAJAR por él:
//
//   · dónde empieza y acaba cada reino (banda de distancia),
//   · a qué coordenada te deja la caravana (la capital),
//   · qué nivel exige cruzar la frontera y cuánto cobra el viaje,
//   · el lore largo de cada tierra y su hito irrepetible.
//
//  Nada de esto se persiste: la banda se calcula de la posición, y el permiso
//  de viaje sale del nivel del héroe. Así el atlas no toca el esquema de Room.
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Ficha de viaje de un reino: todo lo que el Mapa del Mundo necesita para
 * pintarlo, contarlo y dejarte ir allí.
 */
data class KingdomEntry(
    val id: String,
    /** Distancia de Chebyshev a la que empieza la banda (inclusive). */
    val minDist: Int,
    /**
     * Distancia a la que termina la banda (inclusive). El último reino no tiene
     * borde exterior real; se usa un valor grande sólo para dibujarlo.
     */
    val maxDist: Int,
    /** Capital: coordenada exacta a la que te deja la caravana. */
    val capitalX: Int,
    val capitalY: Int,
    val capitalName: String,
    /** Nivel mínimo para que la caravana acepte llevarte. */
    val requiredLevel: Int,
    /** Precio del pasaje. Cruzar medio mundo cuesta dinero. */
    val travelCost: Int,
    /** Tier de peligro 1..6, usado por los encargos y el botín. */
    val tier: Int,
    /** Lore extendido: lo que un bardo te contaría antes de que pongas un pie. */
    val lore: String,
    /** El peligro concreto de la tierra, en una frase. */
    val threat: String,
    /** Costumbre local: da color y explica por qué el reino es distinto. */
    val custom: String,
    /** Hito único del reino: aparece una sola vez, en su capital. */
    val landmarkName: String,
    val landmarkLore: String,
    /** Recompensa temática del hito, descrita para el jugador. */
    val landmarkBoon: String
) {
    /** Ancho de la banda en casillas. */
    val bandWidth: Int get() = maxDist - minDist + 1
}

object KingdomAtlas {

    /**
     * Los seis reinos, del centro hacia fuera. Las bandas coinciden EXACTAMENTE
     * con los cortes de `KingdomGenerator.getKingdomForCoords`: si allí cambian
     * los umbrales, hay que cambiarlos aquí o el mapa mentiría.
     */
    val ALL: List<KingdomEntry> = listOf(
        KingdomEntry(
            id = "eldoria",
            minDist = 0, maxDist = 12,
            capitalX = 0, capitalY = 0,
            capitalName = "Santuario Inicial",
            requiredLevel = 1,
            travelCost = 0,
            tier = 1,
            lore = "Aquí empezó todo. El Valle del Alba guarda la primera piedra que los " +
                "dioses pusieron sobre el vacío, y de esa piedra brotó el resto del mundo. " +
                "Los caminos están empedrados, las posadas tienen nombre y las bestias del " +
                "bosque todavía respetan a un hombre armado. Ningún héroe se hizo leyenda " +
                "quedándose en Eldoria, pero todos aprendieron aquí a sostener una espada.",
            threat = "Fauna salvaje y bandidos de camino. Nada que un aprendiz no pueda matar.",
            custom = "Los aldeanos pagan en oro contante por cada alimaña que baje del bosque.",
            landmarkName = "Piedra del Alba",
            landmarkLore = "La primera piedra. Sigue tibia después de mil generaciones y quien " +
                "apoya la palma sobre ella jura oír, muy lejos, el latido de algo enorme.",
            landmarkBoon = "Restaura por completo tu vida y tu maná."
        ),
        KingdomEntry(
            id = "drakenhold",
            minDist = 13, maxDist = 28,
            capitalX = 20, capitalY = 0,
            capitalName = "Fortaleza de Obsidiana",
            requiredLevel = 10,
            travelCost = 400,
            tier = 2,
            lore = "Drakenhold no se conquistó: se negoció. Los primeros herreros humanos " +
                "subieron a las cumbres con hierro y bajaron con acero, y a cambio juraron " +
                "alimentar la fragua para siempre. Cada cien años el volcán cobra ese pacto y " +
                "el reino entero se retira a los túneles mientras la montaña vomita. Luego " +
                "vuelven, reconstruyen y siguen forjando. Nadie discute con la montaña.",
            threat = "Ríos de magma, criaturas de ceniza y wyrms que anidan en la obsidiana.",
            custom = "Ningún trato se cierra en Drakenhold sin templar antes el acero del acuerdo.",
            landmarkName = "Fragua Primordial",
            landmarkLore = "El primer fuego, el que encendió todas las forjas que vinieron " +
                "después. Arde sin combustible desde antes de que existiera la palabra «arder».",
            landmarkBoon = "Concede Brasa de Forja ×3 para mejorar la calidad de tus templados."
        ),
        KingdomEntry(
            id = "frostgard",
            minDist = 29, maxDist = 48,
            capitalX = -38, capitalY = 0,
            capitalName = "Bastión Glacial",
            requiredLevel = 22,
            travelCost = 1400,
            tier = 3,
            lore = "En Frostgard el invierno no es una estación: es el estado natural de las " +
                "cosas y el verano, una leyenda que se cuenta a los niños. Sus fortalezas no " +
                "están construidas sobre el hielo sino talladas DENTRO de él, y crecen solas " +
                "un palmo cada año. Los clanes miden la riqueza en leña y la nobleza en " +
                "cicatrices; un jarl sin marcas es un jarl al que nadie obedece.",
            threat = "Ventiscas que ciegan, yetis devoradores y algo que aúlla bajo el glaciar.",
            custom = "Se recibe al forastero con fuego y comida antes de preguntarle el nombre.",
            landmarkName = "Corazón del Glaciar",
            landmarkLore = "Un bloque de hielo azul del tamaño de una torre con una silueta " +
                "atrapada dentro. Lleva ahí desde antes de los clanes. A veces cambia de postura.",
            landmarkBoon = "Otorga Cristal Puro ×2 y una bolsa de oro del tributo de los clanes."
        ),
        KingdomEntry(
            id = "aethelgard",
            minDist = 49, maxDist = 72,
            // (0,-58) y no (0,-60): el generador planta un castillo en toda
            // casilla múltiplo de 12, y el hito del reino no debe pelearse con él.
            capitalX = 0, capitalY = -58,
            capitalName = "Torreón Umbrío",
            requiredLevel = 35,
            travelCost = 4000,
            tier = 4,
            lore = "Aethelgard fue un reino próspero hasta que su última reina intentó traer " +
                "de vuelta a su hijo muerto. Funcionó. Eso fue lo peor que pudo pasar. Desde " +
                "entonces la ciénaga se traga las lápidas, los muertos no terminan de irse y " +
                "los cultos discuten en voz baja quién debería gobernar ahora. Los mapas viejos " +
                "todavía marcan ciudades donde hoy sólo hay niebla y raíces.",
            threat = "Necrópolis activas, cultos del vacío y no-muertos que recuerdan tu cara.",
            custom = "Nadie pronuncia el nombre de un difunto en voz alta: podría contestar.",
            landmarkName = "Trono Vacío",
            landmarkLore = "La sala del trono de la reina, intacta bajo el agua negra. El asiento " +
                "está vacío y limpio de polvo, como si alguien lo cuidara todas las noches.",
            landmarkBoon = "Otorga Esencia de Sombra ×3 y Fragmento de Ánima ×1."
        ),
        KingdomEntry(
            id = "solaria",
            minDist = 73, maxDist = 100,
            capitalX = 0, capitalY = 86,
            capitalName = "Pirámide del Sol Dorado",
            requiredLevel = 50,
            travelCost = 12000,
            tier = 5,
            lore = "Los reyes solares no construyeron tumbas para morir en ellas, sino para " +
                "esperar. Cada pirámide es un reloj: cuando el sol entre por el ángulo correcto, " +
                "el faraón despertará y reclamará lo suyo. Han pasado tres mil años y algunos " +
                "ángulos ya se han cumplido. Por eso la arena de Solaria está llena de saqueadores " +
                "ricos y de saqueadores muertos, y muy pocos son las dos cosas a la vez.",
            threat = "Faraones inmortales, espejismos que matan y tumbas que se cierran solas.",
            custom = "Se comercia sólo al alba y al ocaso; el mediodía pertenece al sol.",
            landmarkName = "Reloj de los Reyes",
            landmarkLore = "Un obelisco cuya sombra no sigue al sol. Señala siempre al mismo punto " +
                "del desierto, donde nadie ha encontrado nunca nada… todavía.",
            landmarkBoon = "Otorga Reliquia Antigua ×2 y un botín de oro faraónico."
        ),
        KingdomEntry(
            id = "aetheria",
            minDist = 101, maxDist = 160,
            capitalX = 0, capitalY = -110,
            capitalName = "Palacio Astral de Aetheria",
            requiredLevel = 70,
            travelCost = 40000,
            tier = 6,
            lore = "Aetheria no está sobre el mundo: está DESPUÉS de él. Islas de piedra blanca " +
                "flotando en un cielo sin suelo, unidas por puentes de luz que sólo aguantan a " +
                "quien tiene derecho a cruzarlos. Sus habitantes no nacieron, fueron escritos. " +
                "Miran a los mortales que llegan hasta aquí con la curiosidad educada de quien " +
                "encuentra un insecto raro sobre la mesa, y algunos deciden aplastarlo.",
            threat = "Arcángeles caídos, titanes del éter y dragones que existen en varios sitios.",
            custom = "Aquí no se pregunta el nombre a nadie: se lee directamente.",
            landmarkName = "Puerta sin Otro Lado",
            landmarkLore = "Un arco de mármol en el borde de la última isla. No lleva a ninguna " +
                "parte. Los seraphim lo vigilan igualmente, en turnos de mil años.",
            landmarkBoon = "Otorga Diamante Infinito ×1: el material más raro que existe."
        )
    )

    private val index: Map<String, KingdomEntry> = ALL.associateBy { it.id }

    fun byId(id: String): KingdomEntry? = index[id]

    /** Distancia de Chebyshev al Santuario: la misma métrica que usa el generador. */
    fun distanceOf(x: Int, y: Int): Int = max(abs(x), abs(y))

    /** Reino al que pertenece una coordenada. Nunca devuelve nulo. */
    fun entryForCoords(x: Int, y: Int): KingdomEntry {
        val d = distanceOf(x, y)
        return ALL.firstOrNull { d in it.minDist..it.maxDist } ?: ALL.last()
    }

    /** Datos completos (biomas, bestias, jefes) del reino de una coordenada. */
    fun dataForCoords(x: Int, y: Int): KingdomData = KingdomGenerator.getKingdomForCoords(x, y)

    /** Datos completos por id, resueltos a través de la capital del reino. */
    fun dataOf(entry: KingdomEntry): KingdomData =
        KingdomGenerator.getKingdomForCoords(entry.capitalX, entry.capitalY)

    fun tierOf(kingdomId: String): Int = index[kingdomId]?.tier ?: 1

    /** ¿El héroe tiene nivel para que la caravana lo lleve? */
    fun canTravel(entry: KingdomEntry, playerLevel: Int): Boolean =
        playerLevel >= entry.requiredLevel

    /**
     * Un reino está descubierto si el jugador ha pisado alguna casilla suya.
     * Se deduce de los puntos explorados, sin guardar nada nuevo.
     */
    fun discoveredIds(exploredPoints: List<String>, currentX: Int, currentY: Int): Set<String> {
        val found = mutableSetOf(entryForCoords(currentX, currentY).id)
        exploredPoints.forEach { raw ->
            val parts = raw.split(',')
            if (parts.size == 2) {
                val px = parts[0].trim().toIntOrNull()
                val py = parts[1].trim().toIntOrNull()
                if (px != null && py != null) found.add(entryForCoords(px, py).id)
            }
        }
        return found
    }

    /**
     * Coordenada del hito único de un reino. Es la capital misma: el hito y la
     * capital comparten casilla para que llegar sea un destino, no una lotería.
     */
    fun isLandmarkTile(x: Int, y: Int): KingdomEntry? =
        ALL.firstOrNull { it.capitalX == x && it.capitalY == y && it.minDist > 0 }
}
