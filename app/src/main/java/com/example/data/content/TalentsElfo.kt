package com.example.data.content

import com.example.data.content.TalentBranch.*
import com.example.data.content.TalentCondition.*
import com.example.data.content.TalentKind.*

// ══════════════════════════════════════════════════════════════════════════════
//  RED DE TALENTOS — ELFO
//
//  Identidad: no encajar el golpe. El elfo apuesta por ESQUIVA y CRIT_PROB, y
//  su ofensiva es de picos —criticos, primer turno, penetracion— en vez de la
//  acumulacion lenta del enano. Es la raza que mas castiga jugar mal: si le
//  entran los golpes, no tiene vida para aguantarlos.
//
//  Evoluciones: Guardian del Alba (20), Cantor Estelar (50), Arconte Silvano (100).
// ══════════════════════════════════════════════════════════════════════════════

private fun t(
    id: String, name: String, desc: String, branch: TalentBranch, tier: Int,
    maxRank: Int, kind: TalentKind, mag: Double,
    cond: TalentCondition = SIEMPRE, prereq: String? = null, evo: Int = 0
) = TalentDef("elf_$id", name, desc, "Elfo", branch, tier, maxRank,
    TalentEffect(kind, mag, cond), prereq?.let { "elf_$it" }, evo)

internal val TALENTS_ELFO: List<TalentDef> = listOf(

    // ─── ARMAS ───
    t("arm1", "Pulso de Arquero", "Siglos de practica sostienen la mano. +2,5 % de probabilidad de critico por rango.", ARMAS, 1, 5, CRIT_PROB, 2.5),
    t("arm2", "Filo Fino", "+2 % de daño fisico por rango.", ARMAS, 1, 5, DANO_FISICO, 0.02, prereq = "arm1"),
    t("arm3", "Punto Debil", "+4 % de penetracion por rango.", ARMAS, 2, 4, PENETRACION, 0.04, prereq = "arm1"),
    t("arm4", "Disparo de Apertura", "+18 % de daño en el primer turno por rango.", ARMAS, 2, 3, DANO_TOTAL, 0.18, PRIMER_TURNO, "arm2"),
    t("arm5", "Corte de Seda", "+9 % de multiplicador critico por rango.", ARMAS, 2, 4, CRIT_MULT, 0.09, prereq = "arm1"),
    t("arm6", "Blanco Grande", "+5 % de probabilidad de critico contra elites y jefes por rango.", ARMAS, 3, 4, CRIT_PROB, 5.0, CONTRA_GRANDES, "arm5"),
    t("arm7", "Mano que No Tiembla", "+4 % de daño de habilidad por rango.", ARMAS, 3, 4, DANO_HABILIDAD, 0.04, prereq = "arm3"),
    t("arm8", "Tiro Certero", "El primer golpe del combate es critico garantizado.", ARMAS, 4, 1, PRIMER_GOLPE_CRITICO, 1.0, prereq = "arm4"),
    t("arm9", "Precision Sostenida", "+3 % de probabilidad de critico por rango.", ARMAS, 4, 5, CRIT_PROB, 3.0, prereq = "arm6"),
    t("arm10", "Herida Abierta", "+10 % de multiplicador critico por rango.", ARMAS, 5, 4, CRIT_MULT, 0.10, prereq = "arm9"),
    t("arm11", "Arte de Milenios", "+3 % de daño total por rango.", ARMAS, 5, 5, DANO_TOTAL, 0.03, prereq = "arm10"),

    // ─── DEFENSA ───
    t("def1", "Paso de Hoja", "No estar donde cae el golpe. +2 % de esquiva por rango.", DEFENSA, 1, 5, ESQUIVA, 2.0),
    t("def2", "Cuerpo Ligero", "+2 % de armadura por rango.", DEFENSA, 1, 4, ARMADURA, 0.02, prereq = "def1"),
    t("def3", "Danza Defensiva", "+2 % de esquiva desde el quinto turno por rango.", DEFENSA, 2, 4, ESQUIVA, 2.0, COMBATE_LARGO, "def1"),
    t("def4", "Velo del Bosque", "+2 % de reduccion de daño por rango.", DEFENSA, 2, 4, REDUCCION_DANO, 0.02, prereq = "def2"),
    t("def5", "Reflejo Antiguo", "+3 % de esquiva contra elites y jefes por rango.", DEFENSA, 3, 4, ESQUIVA, 3.0, CONTRA_GRANDES, "def3"),
    t("def6", "Coraza de Hojas", "Escudo inicial del 3 % de tu vida por rango.", DEFENSA, 3, 3, ESCUDO_INICIAL, 0.03, prereq = "def4"),
    t("def7", "Cuerpo Sano", "+2,5 % de vida maxima por rango.", DEFENSA, 3, 5, VIDA_MAX, 0.025, prereq = "def2"),
    t("def8", "Sombra del Claro", "+3 % de esquiva por rango.", DEFENSA, 4, 4, ESQUIVA, 3.0, prereq = "def5"),
    t("def9", "Piel de Corteza", "+3 % de reduccion de daño por rango.", DEFENSA, 4, 4, REDUCCION_DANO, 0.03, prereq = "def6"),
    t("def10", "Aliento del Alba", "Sobrevives a un golpe mortal con el 10 % de vida por rango.", DEFENSA, 5, 2, ULTIMO_ALIENTO, 0.10, prereq = "def9"),
    t("def11", "Gracia Silvana", "+2 % de esquiva por rango.", DEFENSA, 5, 5, ESQUIVA, 2.0, prereq = "def8"),

    // ─── ARCANO ───
    t("arc1", "Lengua Antigua", "El elfo aprendio a conjurar antes que a escribir. +3 % de daño magico por rango.", ARCANO, 1, 5, DANO_MAGICO, 0.03),
    t("arc2", "Mana Abundante", "+5 % de mana maximo por rango.", ARCANO, 1, 5, MANA_MAX, 0.05, prereq = "arc1"),
    t("arc3", "Verbo Corto", "-3 % de coste de mana por rango.", ARCANO, 2, 4, COSTE_MANA, 0.03, prereq = "arc2"),
    t("arc4", "Manantial", "Recuperas un 1,5 % de mana por turno por rango.", ARCANO, 2, 4, REGEN_MANA_TURNO, 0.015, prereq = "arc2"),
    t("arc5", "Conjuro Limpio", "+5 % de daño de habilidad por rango.", ARCANO, 2, 5, DANO_HABILIDAD, 0.05, prereq = "arc1"),
    t("arc6", "Sifon Elfico", "Robas un 1,5 % del daño como mana por rango.", ARCANO, 3, 4, ROBO_MANA, 0.015, prereq = "arc4"),
    t("arc7", "Palabra Primera", "+18 % de daño de habilidad en el primer turno por rango.", ARCANO, 3, 3, DANO_HABILIDAD, 0.18, PRIMER_TURNO, "arc5"),
    t("arc8", "Sello Elfico", "Tu anti-curacion dura un turno mas por rango.", ARCANO, 3, 2, ANTI_CURACION_EXTRA, 1.0, prereq = "arc7"),
    t("arc9", "Trance Largo", "+6 % de daño magico desde el quinto turno por rango.", ARCANO, 4, 4, DANO_MAGICO, 0.06, COMBATE_LARGO, "arc6"),
    t("arc10", "Pozo Sin Fondo", "+6 % de mana maximo por rango.", ARCANO, 4, 5, MANA_MAX, 0.06, prereq = "arc3"),
    t("arc11", "Voz del Bosque", "+6 % de daño magico por rango.", ARCANO, 5, 5, DANO_MAGICO, 0.06, prereq = "arc9"),
    t("arc12", "Eco Estelar", "+7 % de daño de habilidad por rango.", ARCANO, 5, 4, DANO_HABILIDAD, 0.07, prereq = "arc11"),

    // ─── SOMBRA ───
    t("som1", "Andar sin Huella", "+2 % de esquiva por rango.", SOMBRA, 1, 5, ESQUIVA, 2.0),
    t("som2", "Aguja Envenenada", "+3 % de probabilidad de critico por rango.", SOMBRA, 1, 4, CRIT_PROB, 3.0, prereq = "som1"),
    t("som3", "Emboscada Silvana", "+20 % de daño en el primer turno por rango.", SOMBRA, 2, 3, DANO_TOTAL, 0.20, PRIMER_TURNO, "som2"),
    t("som4", "Costura Abierta", "+5 % de penetracion por rango.", SOMBRA, 2, 4, PENETRACION, 0.05, prereq = "som2"),
    t("som5", "Sombra Alargada", "+2,5 % de esquiva desde el quinto turno por rango.", SOMBRA, 3, 4, ESQUIVA, 2.5, COMBATE_LARGO, "som1"),
    t("som6", "Filo Lunar", "+10 % de multiplicador critico por rango.", SOMBRA, 3, 4, CRIT_MULT, 0.10, prereq = "som3"),
    t("som7", "Caza del Titan", "+6 % de daño contra elites y jefes por rango.", SOMBRA, 4, 4, DANO_TOTAL, 0.06, CONTRA_GRANDES, "som6"),
    t("som8", "Golpe entre Ramas", "+6 % de penetracion contra elites y jefes por rango.", SOMBRA, 4, 3, PENETRACION, 0.06, CONTRA_GRANDES, "som4"),
    t("som9", "Nunca Visto", "+3 % de esquiva por rango.", SOMBRA, 5, 4, ESQUIVA, 3.0, prereq = "som5"),
    t("som10", "Sentencia Silenciosa", "+12 % de multiplicador critico por rango.", SOMBRA, 5, 3, CRIT_MULT, 0.12, prereq = "som7"),

    // ─── SANGRE (savia) ───
    t("san1", "Savia Curativa", "Recuperas un 1 % de vida por turno por rango.", SANGRE, 1, 5, REGEN_VIDA_TURNO, 0.01),
    t("san2", "Vida Larga", "+2,5 % de vida maxima por rango.", SANGRE, 1, 5, VIDA_MAX, 0.025, prereq = "san1"),
    t("san3", "Beso del Filo", "Robas un 1 % del daño como vida por rango.", SANGRE, 2, 4, ROBO_VIDA, 0.01, prereq = "san1"),
    t("san4", "Serenidad", "+4 % de daño con la vida por encima del 80 % por rango.", SANGRE, 2, 4, DANO_TOTAL, 0.04, VIDA_ALTA, "san2"),
    t("san5", "Raiz Profunda", "+1,5 % de vida por turno desde el quinto turno por rango.", SANGRE, 3, 4, REGEN_VIDA_TURNO, 0.015, COMBATE_LARGO, "san1"),
    t("san6", "Sangre de Estrella", "+3 % de vida maxima por rango.", SANGRE, 3, 4, VIDA_MAX, 0.03, prereq = "san5"),
    t("san7", "Ciclo Natural", "+2 % de robo de vida por rango.", SANGRE, 4, 4, ROBO_VIDA, 0.02, prereq = "san3"),
    t("san8", "Aliento Silvano", "Sobrevives a un golpe mortal con el 10 % de vida por rango.", SANGRE, 4, 2, ULTIMO_ALIENTO, 0.10, prereq = "san6"),
    t("san9", "Plenitud", "+6 % de daño con la vida por encima del 80 % por rango.", SANGRE, 5, 3, DANO_TOTAL, 0.06, VIDA_ALTA, "san4"),
    t("san10", "Corteza Viva", "+2 % de reduccion de daño con la vida por encima del 80 % por rango.", SANGRE, 5, 4, REDUCCION_DANO, 0.02, VIDA_ALTA, "san8"),
    t("san11", "Savia Inagotable", "+4 % de vida maxima por rango.", SANGRE, 5, 4, VIDA_MAX, 0.04, prereq = "san8"),

    // ─── FORTUNA ───
    t("for1", "Ojo del Coleccionista", "+3 % de rareza del botin por rango.", FORTUNA, 1, 5, RAREZA_BOTIN, 0.03),
    t("for2", "Memoria Larga", "+4 % de experiencia por rango.", FORTUNA, 1, 5, EXP, 0.04, prereq = "for1"),
    t("for3", "Herbolario Elfico", "+6 % de potencia de pocion por rango.", FORTUNA, 2, 4, POCION_POTENCIA, 0.06, prereq = "for1"),
    t("for4", "Esencia Estable", "Tus efectos de pocion duran un turno mas por rango.", FORTUNA, 2, 2, POCION_DURACION, 1.0, prereq = "for3"),
    t("for5", "Gota Preservada", "Un 6 % de probabilidad por rango de no gastar la pocion.", FORTUNA, 3, 4, POCION_AHORRO, 0.06, prereq = "for3"),
    t("for6", "Comercio Silvano", "+4 % de oro por rango.", FORTUNA, 3, 4, ORO, 0.04, prereq = "for2"),
    t("for7", "Con la Savia Puesta", "+5 % de daño con algun efecto de pocion activo por rango.", FORTUNA, 4, 4, DANO_TOTAL, 0.05, CON_POCION_ACTIVA, "for5"),
    t("for8", "Sabiduria Acumulada", "+6 % de experiencia contra elites y jefes por rango.", FORTUNA, 4, 4, EXP, 0.06, CONTRA_GRANDES, "for2"),
    t("for9", "Tesoro del Claro", "+4 % de rareza del botin por rango.", FORTUNA, 5, 3, RAREZA_BOTIN, 0.04, prereq = "for8"),
    t("for10", "Elixir Silvano", "+7 % de potencia de pocion por rango.", FORTUNA, 5, 4, POCION_POTENCIA, 0.07, prereq = "for7"),
    t("for11", "Suerte del Claro", "+2 % de probabilidad de critico por rango.", FORTUNA, 5, 4, CRIT_PROB, 2.0, prereq = "for9"),

    // ─── BESTIA ───
    t("bes1", "Lengua de Animales", "+5 % de daño de mascota por rango.", BESTIA, 1, 5, DANO_MASCOTA, 0.05),
    t("bes2", "Nido Seguro", "+4 % de vida de mascota por rango.", BESTIA, 1, 4, VIDA_MASCOTA, 0.04, prereq = "bes1"),
    t("bes3", "Cazan como Uno", "+5 % de daño propio con mascota por rango.", BESTIA, 2, 4, DANO_TOTAL, 0.05, CON_MASCOTA, "bes1"),
    t("bes4", "Guardia Alada", "+3 % de esquiva con mascota por rango.", BESTIA, 2, 4, ESQUIVA, 3.0, CON_MASCOTA, "bes2"),
    t("bes5", "Garra Afilada", "+7 % de daño de mascota por rango.", BESTIA, 3, 4, DANO_MASCOTA, 0.07, prereq = "bes1"),
    t("bes6", "Vinculo Antiguo", "+6 % de vida de mascota por rango.", BESTIA, 3, 4, VIDA_MASCOTA, 0.06, prereq = "bes2"),
    t("bes7", "Cacería Mayor", "+8 % de daño de mascota contra elites y jefes por rango.", BESTIA, 4, 4, DANO_MASCOTA, 0.08, CONTRA_GRANDES, "bes5"),
    t("bes8", "Aliento Compartido", "Con mascota recuperas un 1,2 % de vida por turno por rango.", BESTIA, 4, 3, REGEN_VIDA_TURNO, 0.012, CON_MASCOTA, "bes6"),
    t("bes9", "Señor de Bestias", "+8 % de daño de mascota por rango.", BESTIA, 5, 4, DANO_MASCOTA, 0.08, prereq = "bes7"),
    t("bes10", "Vuelo Rasante", "+8 % de daño de mascota en el primer turno por rango.", BESTIA, 5, 3, DANO_MASCOTA, 0.08, PRIMER_TURNO, "bes9"),
    t("bes11", "Vista de Halcon", "+2,5 % de probabilidad de critico con mascota por rango.", BESTIA, 5, 4, CRIT_PROB, 2.5, CON_MASCOTA, "bes9"),

    // ─── LEGADO ───
    t("leg1", "Herencia Silvana", "+2 % de daño total por rango.", LEGADO, 1, 5, DANO_TOTAL, 0.02),
    t("leg2", "Paciencia", "+5 % de impetu ganado por rango.", LEGADO, 1, 4, IMPETU_GANANCIA, 0.05, prereq = "leg1"),
    t("leg3", "Canto de Guerra", "+3 % de daño de habilidad por rango.", LEGADO, 2, 4, DANO_HABILIDAD, 0.03, prereq = "leg1"),
    t("leg4", "Guardia del Bosque", "+2 % de armadura por rango.", LEGADO, 2, 4, ARMADURA, 0.02, prereq = "leg2"),
    t("leg5", "Siglos de Estudio", "+3 % de daño magico por rango.", LEGADO, 3, 4, DANO_MAGICO, 0.03, prereq = "leg3"),
    t("leg6", "Ojo Milenario", "+2,5 % de probabilidad de critico por rango.", LEGADO, 3, 4, CRIT_PROB, 2.5, prereq = "leg3"),
    t("leg7", "Vigilia Eterna", "+2 % de esquiva por rango.", LEGADO, 4, 4, ESQUIVA, 2.0, prereq = "leg4"),
    t("leg8", "Primer Aviso", "+8 % de daño en el primer turno por rango.", LEGADO, 4, 3, DANO_TOTAL, 0.08, PRIMER_TURNO, "leg6"),
    t("leg9", "Cantar de Gesta", "+4 % de experiencia por rango.", LEGADO, 4, 4, EXP, 0.04, prereq = "leg5"),
    t("leg10", "Herencia Arcana", "+5 % de daño magico por rango.", LEGADO, 5, 4, DANO_MAGICO, 0.05, prereq = "leg9"),
    t("leg11", "Legado Elfico", "+3 % de daño total por rango.", LEGADO, 5, 5, DANO_TOTAL, 0.03, prereq = "leg8"),

    // ─── EVOLUCIONES ───
    // Nivel 20 · GUARDIAN DEL ALBA
    t("ev1a", "Guardian del Alba", "Te confian la primera luz, y con ella el primer golpe. +8 % de daño en el primer turno por rango.", ARMAS, 6, 4, DANO_TOTAL, 0.08, PRIMER_TURNO, "arm11", evo = 1),
    t("ev1b", "Reflejo del Alba", "+4 % de esquiva por rango.", DEFENSA, 6, 4, ESQUIVA, 4.0, prereq = "def11", evo = 1),
    t("ev1c", "Verbo del Alba", "+7 % de daño magico por rango.", ARCANO, 6, 4, DANO_MAGICO, 0.07, prereq = "arc12", evo = 1),
    t("ev1d", "Filo del Alba", "+6 % de probabilidad de critico por rango.", SOMBRA, 6, 3, CRIT_PROB, 6.0, prereq = "som10", evo = 1),
    // Nivel 50 · CANTOR ESTELAR
    t("ev2a", "Cantor Estelar", "Tu voz llega mas lejos que tu flecha. +9 % de daño de habilidad por rango.", ARCANO, 7, 4, DANO_HABILIDAD, 0.09, prereq = "ev1c", evo = 2),
    t("ev2b", "Constelacion Propia", "+8 % de mana maximo por rango.", ARCANO, 7, 4, MANA_MAX, 0.08, prereq = "ev2a", evo = 2),
    t("ev2c", "Danza de Estrellas", "+5 % de esquiva por rango.", DEFENSA, 7, 3, ESQUIVA, 5.0, prereq = "ev1b", evo = 2),
    t("ev2d", "Puntería Estelar", "+18 % de multiplicador critico por rango.", ARMAS, 7, 3, CRIT_MULT, 0.18, prereq = "ev1d", evo = 2),
    // Nivel 100 · ARCONTE SILVANO
    t("ev3a", "Arconte Silvano", "El bosque deja de obedecerte porque ya sois lo mismo. +10 % de daño total por rango.", LEGADO, 8, 5, DANO_TOTAL, 0.10, prereq = "ev2a", evo = 3),
    t("ev3b", "Cuerpo de Luz", "+6 % de esquiva por rango.", DEFENSA, 8, 4, ESQUIVA, 6.0, prereq = "ev2c", evo = 3),
    t("ev3c", "Juicio Arcano", "+12 % de daño magico por rango.", ARCANO, 8, 4, DANO_MAGICO, 0.12, prereq = "ev2b", evo = 3),
    t("ev3d", "Flecha Inevitable", "+10 % de penetracion por rango.", ARMAS, 8, 4, PENETRACION, 0.10, prereq = "ev2d", evo = 3),
)
