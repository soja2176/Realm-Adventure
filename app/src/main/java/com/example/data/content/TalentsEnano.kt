package com.example.data.content

import com.example.data.content.TalentBranch.*
import com.example.data.content.TalentCondition.*
import com.example.data.content.TalentKind.*

// ══════════════════════════════════════════════════════════════════════════════
//  RED DE TALENTOS — ENANO
//
//  Identidad: no caerse. Donde el humano reparte, el enano acumula en DEFENSA y
//  SANGRE, y su ofensiva llega tarde pero no se apaga: casi todo lo suyo mejora
//  con COMBATE_LARGO. El enano no gana el intercambio, gana el desgaste.
//
//  Evoluciones: Rompeyunques (20), Señor de la Forja (50), Titan de Piedra (100).
// ══════════════════════════════════════════════════════════════════════════════

private fun t(
    id: String, name: String, desc: String, branch: TalentBranch, tier: Int,
    maxRank: Int, kind: TalentKind, mag: Double,
    cond: TalentCondition = SIEMPRE, prereq: String? = null, evo: Int = 0
) = TalentDef("ena_$id", name, desc, "Enano", branch, tier, maxRank,
    TalentEffect(kind, mag, cond), prereq?.let { "ena_$it" }, evo)

internal val TALENTS_ENANO: List<TalentDef> = listOf(

    // ─── ARMAS ───
    t("arm1", "Mango Corto", "Un arma corta pega mas veces. +2 % de daño fisico por rango.", ARMAS, 1, 5, DANO_FISICO, 0.02),
    t("arm2", "Peso del Martillo", "+3 % de daño con el golpe basico por rango.", ARMAS, 1, 4, DANO_BASICO, 0.03, prereq = "arm1"),
    t("arm3", "Golpe de Yunque", "Aplastar no requiere puntería. +5 % de penetracion por rango.", ARMAS, 2, 4, PENETRACION, 0.05, prereq = "arm1"),
    t("arm4", "Brazo de Herrero", "+4 % de daño de habilidad por rango.", ARMAS, 2, 4, DANO_HABILIDAD, 0.04, prereq = "arm2"),
    t("arm5", "Sin Prisa", "El enano calienta despacio. +6 % de daño desde el quinto turno por rango.", ARMAS, 2, 4, DANO_TOTAL, 0.06, COMBATE_LARGO, "arm2"),
    t("arm6", "Rompecorazas", "+5 % de penetracion contra elites y jefes por rango.", ARMAS, 3, 4, PENETRACION, 0.05, CONTRA_GRANDES, "arm3"),
    t("arm7", "Cuña de Acero", "+1,5 % de probabilidad de critico por rango.", ARMAS, 3, 4, CRIT_PROB, 1.5, prereq = "arm4"),
    t("arm8", "Repique Constante", "+2 % de daño acumulativo por turno por rango.", ARMAS, 3, 4, FURIA_CRECIENTE, 0.02, prereq = "arm5"),
    t("arm9", "Machaca", "+8 % de multiplicador critico por rango.", ARMAS, 4, 4, CRIT_MULT, 0.08, prereq = "arm7"),
    t("arm10", "Demoledor", "+7 % de daño contra elites y jefes por rango.", ARMAS, 4, 4, DANO_TOTAL, 0.07, CONTRA_GRANDES, "arm6"),
    t("arm11", "Forjado en Guerra", "+3 % de daño total por rango.", ARMAS, 5, 5, DANO_TOTAL, 0.03, prereq = "arm8"),
    t("arm12", "Ultimo Martillazo", "+10 % de daño por debajo del 35 % de vida por rango.", ARMAS, 5, 3, DANO_TOTAL, 0.10, VIDA_BAJA, "arm10"),

    // ─── DEFENSA ───
    t("def1", "Piel de Roca", "La montaña deja marca en quien nace dentro. +4 % de armadura por rango.", DEFENSA, 1, 5, ARMADURA, 0.04),
    t("def2", "Cuerpo Bajo", "Cuesta tirarte. +3 % de vida maxima por rango.", DEFENSA, 1, 5, VIDA_MAX, 0.03, prereq = "def1"),
    t("def3", "Barba Blindada", "+3 % de reduccion de daño por rango.", DEFENSA, 2, 5, REDUCCION_DANO, 0.03, prereq = "def1"),
    t("def4", "Remaches", "Devuelves un 5 % del daño recibido por rango.", DEFENSA, 2, 4, ESPINAS, 0.05, prereq = "def3"),
    t("def5", "Terco como Piedra", "+4 % de reduccion por debajo del 35 % de vida por rango.", DEFENSA, 3, 4, REDUCCION_DANO, 0.04, VIDA_BAJA, "def3"),
    t("def6", "Yunque Portatil", "Escudo inicial del 4 % de tu vida por rango.", DEFENSA, 3, 4, ESCUDO_INICIAL, 0.04, prereq = "def4"),
    t("def7", "Aguanta el Peso", "+5 % de reduccion desde el quinto turno por rango.", DEFENSA, 3, 4, REDUCCION_DANO, 0.05, COMBATE_LARGO, "def5"),
    t("def8", "Nacido Bajo Tierra", "+5 % de reduccion en calabozo por rango.", DEFENSA, 4, 4, REDUCCION_DANO, 0.05, EN_CALABOZO, "def7"),
    t("def9", "Huesos Densos", "+4 % de vida maxima por rango.", DEFENSA, 4, 5, VIDA_MAX, 0.04, prereq = "def2"),
    t("def10", "Contra los Grandes", "+5 % de reduccion contra elites y jefes por rango.", DEFENSA, 4, 4, REDUCCION_DANO, 0.05, CONTRA_GRANDES, "def8"),
    t("def11", "No Me Muevo", "+4 % de armadura por rango.", DEFENSA, 5, 5, ARMADURA, 0.04, prereq = "def9"),
    t("def12", "Raiz de Montaña", "Sobrevives a un golpe mortal con el 12 % de vida por rango.", DEFENSA, 5, 2, ULTIMO_ALIENTO, 0.12, prereq = "def11"),

    // ─── ARCANO (runas de forja) ───
    t("arc1", "Runa Grabada", "El enano no conjura: graba. +2 % de daño magico por rango.", ARCANO, 1, 4, DANO_MAGICO, 0.02),
    t("arc2", "Metal Conductor", "+3 % de mana maximo por rango.", ARCANO, 1, 4, MANA_MAX, 0.03, prereq = "arc1"),
    t("arc3", "Ahorro de Cincel", "-2 % de coste de mana por rango.", ARCANO, 2, 4, COSTE_MANA, 0.02, prereq = "arc2"),
    t("arc4", "Runa de Escudo", "Escudo inicial del 3 % de tu vida por rango.", ARCANO, 2, 3, ESCUDO_INICIAL, 0.03, prereq = "arc1"),
    t("arc5", "Brasa Interna", "Recuperas un 1 % de mana por turno por rango.", ARCANO, 3, 4, REGEN_MANA_TURNO, 0.01, prereq = "arc3"),
    t("arc6", "Runa Sangrante", "Robas un 1 % del daño como mana por rango.", ARCANO, 3, 3, ROBO_MANA, 0.01, prereq = "arc5"),
    t("arc7", "Sello de Yunque", "+5 % de daño de habilidad por rango.", ARCANO, 4, 4, DANO_HABILIDAD, 0.05, prereq = "arc6"),
    t("arc8", "Runa Persistente", "Tu anti-curacion dura un turno mas por rango.", ARCANO, 4, 2, ANTI_CURACION_EXTRA, 1.0, prereq = "arc7"),
    t("arc9", "Fragua Mental", "+4 % de mana maximo por rango.", ARCANO, 5, 4, MANA_MAX, 0.04, prereq = "arc8"),
    t("arc10", "Runa Mayor", "+5 % de daño magico por rango.", ARCANO, 5, 4, DANO_MAGICO, 0.05, prereq = "arc9"),

    // ─── SOMBRA (minería y emboscada en galerías) ───
    t("som1", "Ojo de Mina", "Ves en la oscuridad porque naciste en ella. +1 % de esquiva por rango.", SOMBRA, 1, 4, ESQUIVA, 1.0),
    t("som2", "Galeria Estrecha", "+2 % de esquiva en calabozo por rango.", SOMBRA, 1, 4, ESQUIVA, 2.0, EN_CALABOZO, "som1"),
    t("som3", "Golpe de Pico", "+2 % de probabilidad de critico por rango.", SOMBRA, 2, 4, CRIT_PROB, 2.0, prereq = "som1"),
    t("som4", "Veta Blanda", "+4 % de penetracion por rango.", SOMBRA, 2, 4, PENETRACION, 0.04, prereq = "som3"),
    t("som5", "Derrumbe", "+15 % de daño en el primer turno por rango.", SOMBRA, 3, 3, DANO_TOTAL, 0.15, PRIMER_TURNO, "som4"),
    t("som6", "Paso de Piedra", "+1,5 % de esquiva desde el quinto turno por rango.", SOMBRA, 3, 4, ESQUIVA, 1.5, COMBATE_LARGO, "som2"),
    t("som7", "Filo de Cantera", "+8 % de multiplicador critico por rango.", SOMBRA, 4, 4, CRIT_MULT, 0.08, prereq = "som5"),
    t("som8", "Emboscada de Tunel", "+6 % de daño en calabozo por rango.", SOMBRA, 4, 4, DANO_TOTAL, 0.06, EN_CALABOZO, "som6"),
    t("som9", "Ojo de Tasador", "+3 % de rareza del botin por rango.", SOMBRA, 5, 3, RAREZA_BOTIN, 0.03, prereq = "som8"),
    t("som10", "Golpe Seco", "+3 % de probabilidad de critico por rango.", SOMBRA, 5, 4, CRIT_PROB, 3.0, prereq = "som7"),

    // ─── SANGRE ───
    t("san1", "Aguante Enano", "Recuperas un 1 % de vida por turno por rango.", SANGRE, 1, 5, REGEN_VIDA_TURNO, 0.01),
    t("san2", "Cerveza Fuerte", "Robas un 1 % del daño como vida por rango.", SANGRE, 1, 4, ROBO_VIDA, 0.01, prereq = "san1"),
    t("san3", "Sangre Espesa", "+3 % de vida maxima por rango.", SANGRE, 2, 5, VIDA_MAX, 0.03, prereq = "san1"),
    t("san4", "Rencor", "+9 % de daño por debajo del 35 % de vida por rango.", SANGRE, 2, 4, DANO_TOTAL, 0.09, VIDA_BAJA, "san2"),
    t("san5", "Curtido a Golpes", "+1,5 % de vida por turno desde el quinto turno por rango.", SANGRE, 3, 4, REGEN_VIDA_TURNO, 0.015, COMBATE_LARGO, "san3"),
    t("san6", "Sed de Enano", "+2 % de robo de vida contra elites y jefes por rango.", SANGRE, 3, 3, ROBO_VIDA, 0.02, CONTRA_GRANDES, "san2"),
    t("san7", "Corazon de Fragua", "+4 % de vida maxima por rango.", SANGRE, 4, 5, VIDA_MAX, 0.04, prereq = "san5"),
    t("san8", "No Se Cae", "Sobrevives a un golpe mortal con el 12 % de vida por rango.", SANGRE, 4, 2, ULTIMO_ALIENTO, 0.12, prereq = "san7"),
    t("san9", "Furia Contenida", "+2,5 % de daño acumulativo por turno por rango.", SANGRE, 5, 4, FURIA_CRECIENTE, 0.025, prereq = "san4"),
    t("san10", "Vida de Piedra", "+5 % de vida maxima por rango.", SANGRE, 5, 4, VIDA_MAX, 0.05, prereq = "san8"),

    // ─── FORTUNA ───
    t("for1", "Tasador Nato", "+5 % de oro por rango.", FORTUNA, 1, 5, ORO, 0.05),
    t("for2", "Bolsa Honda", "+3 % de rareza del botin por rango.", FORTUNA, 1, 4, RAREZA_BOTIN, 0.03, prereq = "for1"),
    t("for3", "Destilado Enano", "+5 % de potencia de pocion por rango.", FORTUNA, 2, 4, POCION_POTENCIA, 0.05, prereq = "for1"),
    t("for4", "Barril Grande", "Tus efectos de pocion duran un turno mas por rango.", FORTUNA, 2, 2, POCION_DURACION, 1.0, prereq = "for3"),
    t("for5", "Sorbo Contado", "Un 6 % de probabilidad por rango de no gastar la pocion.", FORTUNA, 3, 4, POCION_AHORRO, 0.06, prereq = "for3"),
    t("for6", "Oro de Veta", "+6 % de oro en calabozo por rango.", FORTUNA, 3, 4, ORO, 0.06, EN_CALABOZO, "for1"),
    t("for7", "Aprender Peleando", "+4 % de experiencia por rango.", FORTUNA, 4, 4, EXP, 0.04, prereq = "for2"),
    t("for8", "Reserva de Clan", "+7 % de potencia de pocion por rango.", FORTUNA, 4, 4, POCION_POTENCIA, 0.07, prereq = "for5"),
    t("for9", "Botin de Profundidad", "+4 % de rareza del botin en calabozo por rango.", FORTUNA, 5, 3, RAREZA_BOTIN, 0.04, EN_CALABOZO, "for6"),
    t("for10", "Con la Pocion Puesta", "+4 % de daño con algun efecto de pocion activo por rango.", FORTUNA, 5, 4, DANO_TOTAL, 0.04, CON_POCION_ACTIVA, "for8"),

    // ─── BESTIA ───
    t("bes1", "Bestia de Carga", "+4 % de vida de mascota por rango.", BESTIA, 1, 4, VIDA_MASCOTA, 0.04),
    t("bes2", "Arreo Firme", "+4 % de daño de mascota por rango.", BESTIA, 1, 4, DANO_MASCOTA, 0.04, prereq = "bes1"),
    t("bes3", "Escudo Viviente", "+4 % de reduccion con mascota por rango.", BESTIA, 2, 4, REDUCCION_DANO, 0.04, CON_MASCOTA, "bes1"),
    t("bes4", "Bestia de Mina", "+6 % de daño de mascota en calabozo por rango.", BESTIA, 2, 4, DANO_MASCOTA, 0.06, EN_CALABOZO, "bes2"),
    t("bes5", "Alimento de Sobra", "+6 % de vida de mascota por rango.", BESTIA, 3, 4, VIDA_MASCOTA, 0.06, prereq = "bes3"),
    t("bes6", "Golpean Juntos", "+4 % de daño propio con mascota por rango.", BESTIA, 3, 4, DANO_TOTAL, 0.04, CON_MASCOTA, "bes4"),
    t("bes7", "Colmillo de Hierro", "+7 % de daño de mascota por rango.", BESTIA, 4, 4, DANO_MASCOTA, 0.07, prereq = "bes5"),
    t("bes8", "Contra el Coloso", "+7 % de daño de mascota contra elites y jefes por rango.", BESTIA, 4, 3, DANO_MASCOTA, 0.07, CONTRA_GRANDES, "bes7"),
    t("bes9", "Manada de Clan", "+5 % de daño de mascota por rango.", BESTIA, 5, 4, DANO_MASCOTA, 0.05, prereq = "bes8"),
    t("bes10", "Yugo de Guerra", "+5 % de vida de mascota por rango.", BESTIA, 5, 4, VIDA_MASCOTA, 0.05, prereq = "bes9"),
    t("bes11", "Bestia Tozuda", "+3 % de reduccion de daño con mascota por rango.", BESTIA, 5, 3, REDUCCION_DANO, 0.03, CON_MASCOTA, "bes6"),
    t("bes12", "Aguante Compartido", "Con mascota recuperas un 1 % de vida por turno por rango.", BESTIA, 5, 3, REGEN_VIDA_TURNO, 0.01, CON_MASCOTA, "bes11"),

    // ─── LEGADO ───
    t("leg1", "Tradicion de Clan", "+2 % de armadura por rango.", LEGADO, 1, 5, ARMADURA, 0.02),
    t("leg2", "Juramento de Piedra", "+2 % de vida maxima por rango.", LEGADO, 1, 5, VIDA_MAX, 0.02, prereq = "leg1"),
    t("leg3", "Rencor Ancestral", "+4 % de daño contra elites y jefes por rango.", LEGADO, 2, 4, DANO_TOTAL, 0.04, CONTRA_GRANDES, "leg1"),
    t("leg4", "Paso de Marcha", "+6 % de impetu ganado por rango.", LEGADO, 2, 4, IMPETU_GANANCIA, 0.06, prereq = "leg2"),
    t("leg5", "Memoria de Forja", "+3 % de daño de habilidad por rango.", LEGADO, 3, 4, DANO_HABILIDAD, 0.03, prereq = "leg3"),
    t("leg6", "Cimiento", "+3 % de reduccion de daño por rango.", LEGADO, 3, 4, REDUCCION_DANO, 0.03, prereq = "leg4"),
    t("leg7", "Piedra Angular", "+4 % de armadura por rango.", LEGADO, 4, 4, ARMADURA, 0.04, prereq = "leg6"),
    t("leg8", "Cronica de Guerra", "+5 % de daño desde el quinto turno por rango.", LEGADO, 4, 4, DANO_TOTAL, 0.05, COMBATE_LARGO, "leg5"),
    t("leg9", "Herencia de Hueso", "+3 % de vida maxima por rango.", LEGADO, 4, 4, VIDA_MAX, 0.03, prereq = "leg7"),
    t("leg10", "Guardia del Salon", "+4 % de reduccion de daño desde el quinto turno por rango.", LEGADO, 5, 4, REDUCCION_DANO, 0.04, COMBATE_LARGO, "leg8"),
    t("leg11", "Deuda de Clan", "+4 % de oro por rango.", LEGADO, 5, 3, ORO, 0.04, prereq = "leg9"),
    t("leg12", "Nombre en la Piedra", "+4 % de experiencia por rango.", LEGADO, 5, 4, EXP, 0.04, prereq = "leg11"),

    // ─── EVOLUCIONES ───
    // Nivel 20 · ROMPEYUNQUES
    t("ev1a", "Rompeyunques", "Partiste un yunque de un golpe y el clan te dio el nombre. +6 % de penetracion por rango.", ARMAS, 6, 4, PENETRACION, 0.06, prereq = "arm11", evo = 1),
    t("ev1b", "Costillar de Granito", "+6 % de vida maxima por rango.", DEFENSA, 6, 4, VIDA_MAX, 0.06, prereq = "def11", evo = 1),
    t("ev1c", "Martillo Rúnico", "+6 % de daño de habilidad por rango.", ARCANO, 6, 3, DANO_HABILIDAD, 0.06, prereq = "arc10", evo = 1),
    t("ev1d", "Sed de Fragua", "+3 % de robo de vida por rango.", SANGRE, 6, 3, ROBO_VIDA, 0.03, prereq = "san10", evo = 1),
    // Nivel 50 · SEÑOR DE LA FORJA
    t("ev2a", "Señor de la Forja", "El fuego te obedece porque le has dado de comer toda la vida. +8 % de daño total por rango.", ARMAS, 7, 4, DANO_TOTAL, 0.08, prereq = "ev1a", evo = 2),
    t("ev2b", "Coraza Maestra", "+7 % de reduccion de daño por rango.", DEFENSA, 7, 4, REDUCCION_DANO, 0.07, prereq = "ev1b", evo = 2),
    t("ev2c", "Yunque Vengador", "Devuelves un 8 % del daño recibido por rango.", DEFENSA, 7, 3, ESPINAS, 0.08, prereq = "ev2b", evo = 2),
    t("ev2d", "Destilado Maestro", "+12 % de potencia de pocion por rango.", FORTUNA, 7, 3, POCION_POTENCIA, 0.12, prereq = "for10", evo = 2),
    // Nivel 100 · TITAN DE PIEDRA
    t("ev3a", "Titan de Piedra", "Ya no eres del clan: eres la montaña. +10 % de vida maxima por rango.", DEFENSA, 8, 5, VIDA_MAX, 0.10, prereq = "ev2b", evo = 3),
    t("ev3b", "Puño Tectonico", "+10 % de daño contra elites y jefes por rango.", ARMAS, 8, 4, DANO_TOTAL, 0.10, CONTRA_GRANDES, "ev2a", evo = 3),
    t("ev3c", "Inamovible", "+8 % de reduccion de daño por rango.", DEFENSA, 8, 4, REDUCCION_DANO, 0.08, prereq = "ev3a", evo = 3),
    t("ev3d", "Corazon de Magma", "Sobrevives a un golpe mortal con el 25 % de vida por rango.", SANGRE, 8, 2, ULTIMO_ALIENTO, 0.25, prereq = "ev1d", evo = 3),
)
