package com.example.data.content

import com.example.data.content.TalentBranch.*
import com.example.data.content.TalentCondition.*
import com.example.data.content.TalentKind.*

// ══════════════════════════════════════════════════════════════════════════════
//  RED DE TALENTOS — ORCO
//
//  Identidad: daño bruto y riesgo. El orco carga en ARMAS y SANGRE, apenas roza
//  el ARCANO y no compra seguridad: su esquiva y su reduccion son las peores de
//  las cuatro razas. A cambio, casi todo lo suyo paga mas cuanto peor va la cosa
//  —VIDA_BAJA, CONTRA_GRANDES, COMBATE_LARGO— y su FURIA_CRECIENTE convierte
//  cada turno que sobrevive en daño acumulado. El enano gana el desgaste; el
//  orco intenta que el combate termine antes de que el desgaste importe.
//
//  Evoluciones: Destripador (20), Caudillo (50), Devorador de Hordas (100).
// ══════════════════════════════════════════════════════════════════════════════

private fun t(
    id: String, name: String, desc: String, branch: TalentBranch, tier: Int,
    maxRank: Int, kind: TalentKind, mag: Double,
    cond: TalentCondition = SIEMPRE, prereq: String? = null, evo: Int = 0
) = TalentDef("orc_$id", name, desc, "Orco", branch, tier, maxRank,
    TalentEffect(kind, mag, cond), prereq?.let { "orc_$it" }, evo)

internal val TALENTS_ORCO: List<TalentDef> = listOf(

    // ─── ARMAS ───
    t("arm1", "Fuerza Bruta", "No hay técnica, hay brazo. +3 % de daño físico por rango.", ARMAS, 1, 5, DANO_FISICO, 0.03),
    t("arm2", "Hachazo", "Levantar y dejar caer, nada más. +4 % de daño con el golpe básico por rango.", ARMAS, 1, 4, DANO_BASICO, 0.04, prereq = "arm1"),
    t("arm3", "Golpe Salvaje", "Cuando entra, entra entero. +10 % de multiplicador crítico por rango.", ARMAS, 2, 4, CRIT_MULT, 0.10, prereq = "arm1"),
    t("arm4", "Sin Guardia", "Bajas el arma para pegar más fuerte. +5 % de daño total por rango.", ARMAS, 2, 4, DANO_TOTAL, 0.05, prereq = "arm2"),
    t("arm5", "Machete Mellado", "Un filo roto desgarra más que uno limpio. +5 % de penetración por rango.", ARMAS, 2, 4, PENETRACION, 0.05, prereq = "arm2"),
    t("arm6", "Carnicero", "Cuanto más carne, mejor. +9 % de daño contra élites y jefes por rango.", ARMAS, 3, 5, DANO_TOTAL, 0.09, CONTRA_GRANDES, "arm4"),
    t("arm7", "Segar Cabezas", "Aprendes a rematar de un solo movimiento. +5 % de daño de habilidad por rango.", ARMAS, 3, 4, DANO_HABILIDAD, 0.05, prereq = "arm3"),
    t("arm8", "Ira del Hacha", "El arma se calienta contigo. +2,5 % de daño acumulativo por turno por rango.", ARMAS, 3, 4, FURIA_CRECIENTE, 0.025, prereq = "arm4"),
    t("arm9", "Diente Astillado", "Muerdes por donde no hay hierro. +1,5 % de probabilidad de crítico por rango.", ARMAS, 3, 4, CRIT_PROB, 1.5, prereq = "arm3"),
    t("arm10", "Rugido de Acero", "Herido pegas peor apuntado y mucho más fuerte. +12 % de daño por debajo del 35 % de vida por rango.", ARMAS, 4, 3, DANO_TOTAL, 0.12, VIDA_BAJA, "arm6"),
    t("arm11", "Partir el Yelmo", "Los grandes llevan más chatarra encima. +6 % de penetración contra élites y jefes por rango.", ARMAS, 4, 4, PENETRACION, 0.06, CONTRA_GRANDES, "arm5"),
    t("arm12", "Molino de Sangre", "Giras y no paras. +6 % de daño desde el quinto turno por rango.", ARMAS, 4, 4, DANO_TOTAL, 0.06, COMBATE_LARGO, "arm8"),
    t("arm13", "Doble Filo", "Lo afilas por los dos lados y te da igual cortarte. +12 % de multiplicador crítico por rango.", ARMAS, 4, 4, CRIT_MULT, 0.12, prereq = "arm9"),
    t("arm14", "Fuerza de Toro", "El músculo es el único entrenamiento. +5 % de daño físico por rango.", ARMAS, 5, 5, DANO_FISICO, 0.05, prereq = "arm12"),
    t("arm15", "Destrozar", "No queda nada que se pueda enterrar. +4 % de daño total por rango.", ARMAS, 5, 5, DANO_TOTAL, 0.04, prereq = "arm14"),
    t("arm16", "Último Hachazo", "El que das cuando ya no queda otra. +14 % de daño por debajo del 35 % de vida por rango.", ARMAS, 5, 3, DANO_TOTAL, 0.14, VIDA_BAJA, "arm10"),

    // ─── DEFENSA ───
    t("def1", "Cuero de Jabalí", "Te cubres con lo que cazaste. +3 % de armadura por rango.", DEFENSA, 1, 5, ARMADURA, 0.03),
    t("def2", "Corpachón", "Hay mucho orco que atravesar. +3 % de vida máxima por rango.", DEFENSA, 1, 5, VIDA_MAX, 0.03, prereq = "def1"),
    t("def3", "Piel Curtida a Golpes", "El orco no para el golpe, lo encaja. +1,5 % de reducción de daño por rango.", DEFENSA, 2, 4, REDUCCION_DANO, 0.015, prereq = "def1"),
    t("def4", "Costillas de Hierro", "Cuesta romperte por dentro. +4 % de vida máxima por rango.", DEFENSA, 2, 5, VIDA_MAX, 0.04, prereq = "def2"),
    t("def5", "Púas en la Coraza", "Te cuelgas encima todo lo que pincha. Devuelves un 6 % del daño recibido por rango.", DEFENSA, 2, 4, ESPINAS, 0.06, prereq = "def3"),
    t("def6", "No Siente el Dolor", "Cuando sangras dejas de notarlo. +2,5 % de reducción por debajo del 35 % de vida por rango.", DEFENSA, 3, 4, REDUCCION_DANO, 0.025, VIDA_BAJA, "def3"),
    t("def7", "Chatarra Atada", "Placas robadas, mal cosidas, pero placas. Escudo inicial del 3 % de tu vida por rango.", DEFENSA, 3, 4, ESCUDO_INICIAL, 0.03, prereq = "def5"),
    t("def8", "Reflejo Torpe", "Apartarse no es lo tuyo, pero algo sale. +0,8 % de esquiva por rango.", DEFENSA, 3, 4, ESQUIVA, 0.8, prereq = "def4"),
    t("def9", "Espinas de Guerra", "Quien te abraza se desangra. Devuelves un 8 % del daño recibido por rango.", DEFENSA, 4, 3, ESPINAS, 0.08, prereq = "def7"),
    t("def10", "Cuerpo de Guerra", "Años de pelea engordan lo que importa. +5 % de vida máxima por rango.", DEFENSA, 4, 4, VIDA_MAX, 0.05, prereq = "def4"),
    t("def11", "No Cae de Pie", "Te niegas a tocar el suelo. Sobrevives a un golpe mortal con el 10 % de vida por rango.", DEFENSA, 5, 2, ULTIMO_ALIENTO, 0.10, prereq = "def10"),

    // ─── ARCANO (chamanismo tosco) ───
    t("arc1", "Grito Chamánico", "El orco no conjura: grita hasta que algo cede. +1,5 % de daño mágico por rango.", ARCANO, 1, 4, DANO_MAGICO, 0.015),
    t("arc2", "Tótem Roto", "Un palo con huesos también guarda algo. +3 % de maná máximo por rango.", ARCANO, 1, 4, MANA_MAX, 0.03, prereq = "arc1"),
    t("arc3", "Sangre por Maná", "Pagas el conjuro con lo que sangra el otro. Robas un 1 % del daño como maná por rango.", ARCANO, 2, 3, ROBO_MANA, 0.01, prereq = "arc2"),
    t("arc4", "Maldición Cruda", "Dos palabras mal dichas bastan. +3 % de daño de habilidad por rango.", ARCANO, 2, 4, DANO_HABILIDAD, 0.03, prereq = "arc1"),
    t("arc5", "Fetiche de Hueso", "Lo llevas colgado y algo ahorra. -2 % de coste de maná por rango.", ARCANO, 3, 3, COSTE_MANA, 0.02, prereq = "arc3"),
    t("arc6", "Aullido de Espíritus", "Los muertos del clan aún gritan contigo. +3 % de daño mágico por rango.", ARCANO, 4, 4, DANO_MAGICO, 0.03, prereq = "arc4"),

    // ─── SOMBRA ───
    t("som1", "Acecho Torpe", "Te escondes mal, pero te escondes. +1 % de esquiva por rango.", SOMBRA, 1, 4, ESQUIVA, 1.0),
    t("som2", "Emboscada Orca", "Salir de un matorral gritando también es una emboscada. +22 % de daño en el primer turno por rango.", SOMBRA, 2, 3, DANO_TOTAL, 0.22, PRIMER_TURNO, "som1"),
    t("som3", "Tajo a Traición", "Nadie mira el hacha de abajo. +2 % de probabilidad de crítico por rango.", SOMBRA, 2, 4, CRIT_PROB, 2.0, prereq = "som1"),
    t("som4", "Hueco entre Placas", "Metes el filo donde no llega el hierro. +4 % de penetración por rango.", SOMBRA, 2, 4, PENETRACION, 0.04, prereq = "som3"),
    t("som5", "Cazador de Fosos", "Bajo tierra el orco caza mejor. +6 % de daño en calabozo por rango.", SOMBRA, 3, 4, DANO_TOTAL, 0.06, EN_CALABOZO, "som2"),
    t("som6", "Filo Sucio", "El óxido hace la mitad del trabajo. +10 % de multiplicador crítico por rango.", SOMBRA, 3, 4, CRIT_MULT, 0.10, prereq = "som3"),
    t("som7", "Primer Mordisco", "El primer golpe del combate es crítico garantizado.", SOMBRA, 4, 1, PRIMER_GOLPE_CRITICO, 1.0, prereq = "som2"),
    t("som8", "Presa Grande", "Lo enorme tiene más sitios blandos. +4 % de probabilidad de crítico contra élites y jefes por rango.", SOMBRA, 4, 4, CRIT_PROB, 4.0, CONTRA_GRANDES, "som6"),
    t("som9", "Sombra Pesada", "Al quinto turno ya nadie sabe por dónde vienes. +1 % de esquiva desde el quinto turno por rango.", SOMBRA, 5, 3, ESQUIVA, 1.0, COMBATE_LARGO, "som5"),
    t("som10", "Degüello", "Buscas el cuello y sólo el cuello. +12 % de multiplicador crítico por rango.", SOMBRA, 5, 3, CRIT_MULT, 0.12, prereq = "som8"),

    // ─── SANGRE ───
    t("san1", "Sed de Sangre", "Beber del tajo es medio descanso. Robas un 1,5 % del daño como vida por rango.", SANGRE, 1, 5, ROBO_VIDA, 0.015),
    t("san2", "Carne Viva", "Cierras solo, mal y rápido. Recuperas un 1 % de vida por turno por rango.", SANGRE, 1, 4, REGEN_VIDA_TURNO, 0.01, prereq = "san1"),
    t("san3", "Rabia", "Sangrar te enfada, y enfadado pegas. +10 % de daño por debajo del 35 % de vida por rango.", SANGRE, 2, 4, DANO_TOTAL, 0.10, VIDA_BAJA, "san1"),
    t("san4", "Furia Creciente", "Cada turno que aguantas suma. +3 % de daño acumulativo por turno por rango.", SANGRE, 2, 4, FURIA_CRECIENTE, 0.03, prereq = "san3"),
    t("san5", "Corazón Doble", "Dicen que los orcos tienen dos. +4 % de vida máxima por rango.", SANGRE, 2, 5, VIDA_MAX, 0.04, prereq = "san2"),
    t("san6", "Devorar", "Lo grande da de comer para dos días. +2,5 % de robo de vida contra élites y jefes por rango.", SANGRE, 3, 3, ROBO_VIDA, 0.025, CONTRA_GRANDES, "san1"),
    t("san7", "Herida que Alimenta", "Cuanto peor estás, más aprovechas cada tajo. +2 % de robo de vida por debajo del 35 % de vida por rango.", SANGRE, 3, 4, ROBO_VIDA, 0.02, VIDA_BAJA, "san3"),
    t("san8", "Gritar el Dolor", "Lo que duele se saca a gritos y se devuelve. +6 % de daño desde el quinto turno por rango.", SANGRE, 3, 4, DANO_TOTAL, 0.06, COMBATE_LARGO, "san4"),
    t("san9", "Costra Rápida", "La herida se cierra sola antes de que la mires. Recuperas un 1,5 % de vida por turno por rango.", SANGRE, 3, 4, REGEN_VIDA_TURNO, 0.015, prereq = "san5"),
    t("san10", "Frenesí Orco", "Ya no distingues aliado de enemigo. +3,5 % de daño acumulativo por turno por rango.", SANGRE, 4, 4, FURIA_CRECIENTE, 0.035, prereq = "san8"),
    t("san11", "Se Niega a Morir", "Todavía no. Sobrevives a un golpe mortal con el 12 % de vida por rango.", SANGRE, 4, 2, ULTIMO_ALIENTO, 0.12, prereq = "san9"),
    t("san12", "Sangre Hirviendo", "Al final la rabia es lo único que queda. +14 % de daño por debajo del 35 % de vida por rango.", SANGRE, 4, 3, DANO_TOTAL, 0.14, VIDA_BAJA, "san7"),
    t("san13", "Bebe del Tajo", "Cada golpe es un trago. +3 % de robo de vida por rango.", SANGRE, 5, 4, ROBO_VIDA, 0.03, prereq = "san6"),
    t("san14", "Carne de Guerra", "El cuerpo se acostumbra a que le falte un trozo. +5 % de vida máxima por rango.", SANGRE, 5, 4, VIDA_MAX, 0.05, prereq = "san11"),
    t("san15", "Nunca Basta", "Ninguna pelea te deja satisfecho. +5 % de daño total por rango.", SANGRE, 5, 5, DANO_TOTAL, 0.05, prereq = "san10"),

    // ─── FORTUNA ───
    t("for1", "Saqueo", "Lo que cae al suelo es tuyo. +5 % de oro por rango.", FORTUNA, 1, 5, ORO, 0.05),
    t("for2", "Trofeos", "Te quedas con lo que impresiona al clan. +3 % de rareza del botín por rango.", FORTUNA, 1, 4, RAREZA_BOTIN, 0.03, prereq = "for1"),
    t("for3", "Brebaje Espeso", "El chamán no mide, echa. +5 % de potencia de poción por rango.", FORTUNA, 2, 4, POCION_POTENCIA, 0.05, prereq = "for1"),
    t("for4", "Aprende Sangrando", "Lo que te parte también te enseña. +4 % de experiencia por rango.", FORTUNA, 2, 4, EXP, 0.04, prereq = "for2"),
    t("for5", "Odre Grande", "Cabe de sobra. Tus efectos de poción duran un turno más por rango.", FORTUNA, 3, 2, POCION_DURACION, 1.0, prereq = "for3"),
    t("for6", "Trago Corto", "Un sorbo y a guardar. Un 5 % de probabilidad por rango de no gastar la poción.", FORTUNA, 3, 4, POCION_AHORRO, 0.05, prereq = "for3"),
    t("for7", "Pillaje de Cripta", "Bajo tierra nadie reclama lo robado. +6 % de oro en calabozo por rango.", FORTUNA, 3, 4, ORO, 0.06, EN_CALABOZO, "for1"),
    t("for8", "Cabezas Colgadas", "Cada jefe caído vale por diez peleas. +6 % de experiencia contra élites y jefes por rango.", FORTUNA, 4, 4, EXP, 0.06, CONTRA_GRANDES, "for4"),
    t("for9", "Con el Brebaje Dentro", "Bebido pegas más. +6 % de daño con algún efecto de poción activo por rango.", FORTUNA, 4, 4, DANO_TOTAL, 0.06, CON_POCION_ACTIVA, "for6"),
    t("for10", "Cráneos de Valor", "Aprendes qué cráneo vale y cuál no. +4 % de rareza del botín por rango.", FORTUNA, 5, 3, RAREZA_BOTIN, 0.04, prereq = "for7"),

    // ─── BESTIA ───
    t("bes1", "Domar a Palos", "La bestia obedece porque conoce el palo. +5 % de daño de mascota por rango.", BESTIA, 1, 5, DANO_MASCOTA, 0.05),
    t("bes2", "Carne Cruda", "Comen lo mismo que tú, sin cocinar. +5 % de vida de mascota por rango.", BESTIA, 1, 4, VIDA_MASCOTA, 0.05, prereq = "bes1"),
    t("bes3", "Cazan en Manada", "Con la bestia al lado no te contienes. +6 % de daño propio con mascota equipada por rango.", BESTIA, 2, 4, DANO_TOTAL, 0.06, CON_MASCOTA, "bes1"),
    t("bes4", "Huargo de Guerra", "La bestia entra en la pelea antes que tú. +7 % de daño de mascota por rango.", BESTIA, 2, 4, DANO_MASCOTA, 0.07, prereq = "bes1"),
    t("bes5", "Carnada", "Que muerdan a la bestia primero. +3 % de reducción con mascota por rango.", BESTIA, 2, 4, REDUCCION_DANO, 0.03, CON_MASCOTA, "bes2"),
    t("bes6", "Bestia Cebada", "La engordas con lo que sobra del saqueo. +6 % de vida de mascota por rango.", BESTIA, 3, 4, VIDA_MASCOTA, 0.06, prereq = "bes2"),
    t("bes7", "Colmillos Rotos", "Muerde hasta perder los dientes. +8 % de daño de mascota contra élites y jefes por rango.", BESTIA, 3, 4, DANO_MASCOTA, 0.08, CONTRA_GRANDES, "bes4"),
    t("bes8", "Sangre Compartida", "Lo que ella desgarra te alimenta. Con mascota robas un 1,5 % del daño como vida por rango.", BESTIA, 4, 3, ROBO_VIDA, 0.015, CON_MASCOTA, "bes5"),
    t("bes9", "Jauría de Guerra", "Ya no es una bestia, es una costumbre del clan. +9 % de daño de mascota por rango.", BESTIA, 4, 4, DANO_MASCOTA, 0.09, prereq = "bes7"),
    t("bes10", "Montura de Foso", "Criada abajo, pelea mejor abajo. +6 % de daño de mascota en calabozo por rango.", BESTIA, 5, 3, DANO_MASCOTA, 0.06, EN_CALABOZO, "bes9"),
    t("bes11", "Rugen Juntos", "El grito es de dos gargantas. +5 % de daño propio con mascota por rango.", BESTIA, 5, 3, DANO_TOTAL, 0.05, CON_MASCOTA, "bes3"),

    // ─── LEGADO ───
    t("leg1", "Sangre de Horda", "Naciste debiendo una pelea. +2,5 % de daño total por rango.", LEGADO, 1, 5, DANO_TOTAL, 0.025),
    t("leg2", "Marca de Clan", "El hierro caliente deja algo más que cicatriz. +2,5 % de vida máxima por rango.", LEGADO, 1, 5, VIDA_MAX, 0.025, prereq = "leg1"),
    t("leg3", "Tambores de Guerra", "El ritmo empuja aunque no quieras. +10 % de ímpetu ganado por rango.", LEGADO, 2, 4, IMPETU_GANANCIA, 0.10, prereq = "leg1"),
    t("leg4", "Ley del Más Fuerte", "Sólo cuenta lo que puedas tumbar. +5 % de daño contra élites y jefes por rango.", LEGADO, 2, 4, DANO_TOTAL, 0.05, CONTRA_GRANDES, "leg1"),
    t("leg5", "Grito de la Horda", "Mil gargantas detrás de la tuya. +4 % de daño de habilidad por rango.", LEGADO, 3, 4, DANO_HABILIDAD, 0.04, prereq = "leg3"),
    t("leg6", "Cicatrices de Rango", "Cada marca es un puesto ganado. +2,5 % de armadura por rango.", LEGADO, 3, 4, ARMADURA, 0.025, prereq = "leg2"),
    t("leg7", "Botín del Jefe", "El primero en entrar cobra el doble. +5 % de oro por rango.", LEGADO, 4, 3, ORO, 0.05, prereq = "leg4"),
    t("leg8", "Nacido para Pelear", "Cuanto más dura, más cómodo estás. +5 % de daño desde el quinto turno por rango.", LEGADO, 4, 4, DANO_TOTAL, 0.05, COMBATE_LARGO, "leg5"),
    t("leg9", "Legado Orco", "Lo que dejas es una horda que sigue pegando. +3 % de daño acumulativo por turno por rango.", LEGADO, 5, 4, FURIA_CRECIENTE, 0.03, prereq = "leg8"),

    // ─── EVOLUCIONES ───
    // Nivel 20 · DESTRIPADOR
    t("ev1a", "Destripador", "Te ganaste el nombre abriendo a uno de arriba abajo. +7 % de daño total por rango.", ARMAS, 6, 4, DANO_TOTAL, 0.07, prereq = "arm16", evo = 1),
    t("ev1b", "Tripas al Aire", "Lo que abres, lo bebes. +3 % de robo de vida por rango.", SANGRE, 6, 4, ROBO_VIDA, 0.03, prereq = "san13", evo = 1),
    t("ev1c", "Carne Rota", "Te sobra cuerpo para perder trozos. +6 % de vida máxima por rango.", DEFENSA, 6, 4, VIDA_MAX, 0.06, prereq = "def11", evo = 1),
    t("ev1d", "Olor a Sangre", "Encuentras la herida abierta sin mirar. +5 % de probabilidad de crítico por rango.", SOMBRA, 6, 3, CRIT_PROB, 5.0, prereq = "som10", evo = 1),
    // Nivel 50 · CAUDILLO
    t("ev2a", "Caudillo", "Nadie te nombró: los demás dejaron de discutir. +9 % de daño total por rango.", LEGADO, 7, 4, DANO_TOTAL, 0.09, prereq = "leg9", evo = 2),
    t("ev2b", "Grito del Caudillo", "Cuando gritas, la horda entera se mueve. +14 % de ímpetu ganado por rango.", LEGADO, 7, 3, IMPETU_GANANCIA, 0.14, prereq = "ev2a", evo = 2),
    t("ev2c", "Furia de Caudillo", "Tu rabia marca el paso del combate. +4 % de daño acumulativo por turno por rango.", SANGRE, 7, 4, FURIA_CRECIENTE, 0.04, prereq = "ev1b", evo = 2),
    t("ev2d", "Cráneo Partido", "Los grandes caen igual, sólo tardan dos golpes. +10 % de daño contra élites y jefes por rango.", ARMAS, 7, 4, DANO_TOTAL, 0.10, CONTRA_GRANDES, "ev1a", evo = 2),
    // Nivel 100 · DEVORADOR DE HORDAS
    t("ev3a", "Devorador de Hordas", "Ya no lideras hordas: te las comes. +12 % de daño por debajo del 35 % de vida por rango.", SANGRE, 8, 5, DANO_TOTAL, 0.12, VIDA_BAJA, "ev2c", evo = 3),
    t("ev3b", "Fauces sin Fondo", "Nada de lo que matas se pierde. +4 % de robo de vida por rango.", SANGRE, 8, 4, ROBO_VIDA, 0.04, prereq = "ev3a", evo = 3),
    t("ev3c", "Carne de Titán", "Creciste hasta dejar de caber en la tienda. +9 % de vida máxima por rango.", DEFENSA, 8, 5, VIDA_MAX, 0.09, prereq = "ev1c", evo = 3),
    t("ev3d", "Golpe que Parte el Mundo", "No hay coraza que signifique nada. +10 % de penetración por rango.", ARMAS, 8, 4, PENETRACION, 0.10, prereq = "ev2d", evo = 3),
)
