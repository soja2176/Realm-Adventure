package com.example.data

import kotlin.math.abs
import kotlin.math.max

data class KingdomData(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val colorHex: String,
    val biomes: List<String>,
    val monsters: List<String>,
    val bossNames: List<String>,
    val castleNames: List<String>,
    val merchantNames: List<String>
)

object KingdomGenerator {
    fun getKingdomForCoords(x: Int, y: Int): KingdomData {
        val dist = max(abs(x), abs(y))
        return when {
            dist <= 12 -> KingdomData(
                id = "eldoria",
                name = "Reino de Eldoria",
                subtitle = "Valle del Alba y Tierras Sagradas",
                description = "El corazón próspero del continente. Valles esmeralda y ruinas impregnadas de magia primordial de la creación.",
                colorHex = "#4CAF50",
                biomes = listOf("Pradera Esmeralda", "Bosque Susurrante", "Ruinas Ancestrales", "Lago Cristalino", "Valle de las Flores"),
                monsters = listOf(
                    "Duende Silvestre", "Lobo Hambriento", "Espectro de Niebla", "Oso Cavernario",
                    "Treant Ancestral", "Bandido del Valle", "Araña Gigante", "Minotauro de la Arboleda",
                    "Guardián Hobgoblin", "Orco Explorador", "Licántropo Nocturno", "Chamán Silvestre"
                ),
                bossNames = listOf("Dragón Dorado de Eldoria", "Gran Treant Corrupto", "Guardián de Piedra Ancestral", "Señor Minotauro de las Praderas"),
                castleNames = listOf("Castillo de Ciudad Alba", "Fortaleza del Sol Naciente", "Ciudadela de Cristal"),
                merchantNames = listOf("Mercader Ambulante Grommar", "Comerciante Elfo Elion", "Buhonero Raro Pip")
            )
            dist <= 28 -> KingdomData(
                id = "drakenhold",
                name = "Reino de Drakenhold",
                subtitle = "Cañones Volcánicos y Tierras de Fuego",
                description = "Dominio abrasador sobre cumbres volcánicas de obsidiana. Ríos de magma fluida y templos del dragón ancestral.",
                colorHex = "#FF5722",
                biomes = listOf("Cumbre Llameante", "Cañón de Obsidiana", "Páramo de Azufre", "Forja Infernal", "Cráter de Magma"),
                monsters = listOf(
                    "Salamandra de Fuego", "Cultista de la Llama", "Elemental de Magma", "Culebra Abrasadora",
                    "Troll de Ceniza", "Wyrm de Obsidiana", "Demonio de la Forja", "Gólem de Lava",
                    "Acolito del Fuego", "Minotauro Infernal", "Basilisco Volcánico", "Gárgola de Obsidiana"
                ),
                bossNames = listOf("Dragón Volcánico Ignis", "Señor del Magma Pyros", "Archidemonio de Cenizas", "Gran Wyrm de Flama"),
                castleNames = listOf("Fortaleza de Obsidiana", "Castillo de la Llama Eterna", "Ciudadela Abrasadora"),
                merchantNames = listOf("Herrero de Magma Ignazio", "Buhonero de las Cenizas Volkan", "Mercader Pirata Drake")
            )
            dist <= 48 -> KingdomData(
                id = "frostgard",
                name = "Reino de Frostgard",
                subtitle = "Picos Helados y Glaciares Eternos",
                description = "Reino gélido e indomable de ventiscas eternas y fortalezas esculpidas en escarcha viva.",
                colorHex = "#00BCD4",
                biomes = listOf("Glaciar Eterno", "Tundra de Cristal", "Caverna de Hielo", "Picos Nevados", "Bosque Congelado"),
                monsters = listOf(
                    "Lobo de Tundra", "Golem de Hielo", "Espectro del Ventisquero", "Yeti Devorador",
                    "Guardián de Escarcha", "Oso Glacial", "Bruja de Escarcha", "Kraken de Hielo",
                    "Warg de Cristal", "Vampiro Glacial", "Autómata Helado", "Serpiente Glacial"
                ),
                bossNames = listOf("Wyrm Blanco de Ventisca", "Reina de Escarcha Freya", "Titán del Hielo Glacius", "Señor Yeti de la Tundra"),
                castleNames = listOf("Castillo de Escarcha", "Ciudadela del Viento Helado", "Bastión Glacial"),
                merchantNames = listOf("Nómada del Hielo Bjorn", "Alquimista de Escarcha Ymir", "Comerciante de Pieles Sven")
            )
            dist <= 72 -> KingdomData(
                id = "aethelgard",
                name = "Reino de Aethelgard",
                subtitle = "Bosques Sombríos y Ciénagas Malditas",
                description = "Región lúgubre rodeada de niebla vil. Necrópolis olvidadas y cultos oscuros acechan bajo las raíces de la arboleda muerta.",
                colorHex = "#AB47BC",
                biomes = listOf("Pantano de Sombras", "Arboleda Arcana", "Necrópolis Olvidada", "Ciénaga Maldita", "Vértice Umbrío"),
                monsters = listOf(
                    "Zombi del Pantano", "Basilisco Umbrío", "Bruja Arcana", "Señor de la Peste",
                    "Lich Devorador", "Sombra Devoradora", "Vampiro de la Noche", "Demonio Umbrío",
                    "Esqueleto Guerrero", "Cultista del Vacío", "Ghouls Abisales", "Naga Abisal"
                ),
                bossNames = listOf("Lich Primordial Malakor", "Gran Vampiro Lord", "Bailarina de Sombras", "Archidemonio Umbrío"),
                castleNames = listOf("Castillo de las Sombras", "Bastión de la Ciénaga", "Torreón Umbrío"),
                merchantNames = listOf("Buhonero Oscuro Malakor", "Noche Bruja Morgana", "Mercader de Reliquias Prohibidas")
            )
            dist <= 100 -> KingdomData(
                id = "solaria",
                name = "Reino de Solaria",
                subtitle = "Desierto Infinito del Sol Dorado",
                description = "Mar de dunas doradas salpicadas de oasis mágicos, templos solares olvidados y tumbas de antiguos reyes del sol.",
                colorHex = "#FFC107",
                biomes = listOf("Dunas Doradas", "Oasis Místico", "Pirámide del Sol", "Garganta Seca", "Catacumbas Solas"),
                monsters = listOf(
                    "Escorpión Gigante", "Guardián Anubis", "Sacerdote Solar", "Manta del Desierto",
                    "Gólem de Arena", "Momia Real", "Víbora de Arena", "Naga Solar",
                    "Ladrón de Tumbas", "Esfinge Menor", "Cultista del Sol", "Demonio del Desierto"
                ),
                bossNames = listOf("Dragón de Arena Ra-Horakhty", "Faraón Inmortal Osiris", "Gran Esfinge Solar", "Señor Anubis de la Tumba"),
                castleNames = listOf("Pirámide del Sol Dorado", "Palacio de los Oasis", "Fortaleza del Viento Dorado"),
                merchantNames = listOf("Mercader de la Seda Tariq", "Tesorero del Faraón Samir", "Alquimista del Sol Zahra")
            )
            else -> KingdomData(
                id = "aetheria",
                name = "Reino Celestial de Aetheria",
                subtitle = "Islas Flotantes y Cielos Astrales",
                description = "Dominio cósmico suspendido en las alturas astrales. Templos de luz pura e islas flotantes habitadas por entes sagrados.",
                colorHex = "#E040FB",
                biomes = listOf("Cielos de Éter", "Plataforma Astral", "Templo de la Luz", "Nubes de Cristal", "Castillo de Estrellas"),
                monsters = listOf(
                    "Guardián Astral", "Quimera Celestial", "Sentinela de Luz", "Arcángel Caído",
                    "Bestia Estelar", "Sombra Estelar", "Autómata Celestial", "Vampiro Astral",
                    "Serpiente Astral", "Demonio de las Estrellas", "Lich Cósmico", "Kraken Celestial"
                ),
                bossNames = listOf("Dragón Cósmico Aetherion", "Arcángel Seraphiel", "Titán del Firmamento", "Gran Archidemonio Astral"),
                castleNames = listOf("Palacio Astral de Aetheria", "Fortaleza del Firmamento", "Templo Celestial de la Luz"),
                merchantNames = listOf("Comerciante Estelar Orion", "Sacerdote de las Estrellas Lyra", "Buhonero Cósmico Zephyr")
            )
        }
    }
}
