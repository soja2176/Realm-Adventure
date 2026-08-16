package com.example.data.content

import com.example.data.content.TalentBranch.*
import com.example.data.content.TalentCondition.*
import com.example.data.content.TalentKind.*

// ══════════════════════════════════════════════════════════════════════════════
//  RED DE TALENTOS — HUMANO
//
//  Identidad: versatilidad y aguante. El humano no gana ninguna rama por
//  goleada; gana porque puede recorrer varias a la vez. Sus talentos tienden a
//  condiciones amplias (SIEMPRE) con magnitudes medias, al reves que el orco
//  —que pega enorme pero solo cuando se cumple algo— o el enano, que es un muro
//  y poco mas.
//
//  Evoluciones: Cruzado (20), Abanderado (50), Heraldo Eterno (100).
// ══════════════════════════════════════════════════════════════════════════════

private fun t(
    id: String, name: String, desc: String, branch: TalentBranch, tier: Int,
    maxRank: Int, kind: TalentKind, mag: Double,
    cond: TalentCondition = SIEMPRE, prereq: String? = null, evo: Int = 0
) = TalentDef("hum_$id", name, desc, "Humano", branch, tier, maxRank,
    TalentEffect(kind, mag, cond), prereq?.let { "hum_$it" }, evo)

internal val TALENTS_HUMANO: List<TalentDef> = listOf(

    // ─── ARMAS ───
    t("arm1", "Filo Firme", "El adiestramiento basico que todo humano recibe. +2 % de daño fisico por rango.", ARMAS, 1, 5, DANO_FISICO, 0.02),
    t("arm2", "Guardia Alta", "Golpear desde arriba cansa menos. +3 % de daño con el golpe basico por rango.", ARMAS, 1, 4, DANO_BASICO, 0.03, prereq = "arm1"),
    t("arm3", "Estocada Precisa", "Buscas la juntura, no la coraza. +1,5 % de probabilidad de critico por rango.", ARMAS, 2, 5, CRIT_PROB, 1.5, prereq = "arm1"),
    t("arm4", "Brazo Descansado", "El primer golpe de un combate siempre es el mejor. +12 % de daño en el primer turno por rango.", ARMAS, 2, 3, DANO_TOTAL, 0.12, PRIMER_TURNO, "arm2"),
    t("arm5", "Peso Muerto", "Aprendes a usar la inercia del arma. +4 % de daño de habilidad por rango.", ARMAS, 2, 4, DANO_HABILIDAD, 0.04, prereq = "arm2"),
    t("arm6", "Corte Profundo", "Los criticos abren mas de lo que deberian. +8 % de multiplicador critico por rango.", ARMAS, 3, 4, CRIT_MULT, 0.08, prereq = "arm3"),
    t("arm7", "Rompeplacas", "Sabes donde la armadura no llega. +3 % de penetracion por rango.", ARMAS, 3, 4, PENETRACION, 0.03, prereq = "arm3"),
    t("arm8", "Matagigantes", "Cuanto mas grande, mas sitios donde clavarlo. +6 % de daño contra elites y jefes por rango.", ARMAS, 3, 5, DANO_TOTAL, 0.06, CONTRA_GRANDES, "arm6"),
    t("arm9", "Segundo Aire", "Cuando el combate se alarga, tu mano se acostumbra. +5 % de daño desde el quinto turno por rango.", ARMAS, 4, 4, DANO_TOTAL, 0.05, COMBATE_LARGO, "arm5"),
    t("arm10", "Golpe de Gracia", "Rematar es un oficio aparte. +10 % de daño por debajo del 35 % de vida por rango.", ARMAS, 4, 3, DANO_TOTAL, 0.10, VIDA_BAJA, "arm8"),
    t("arm11", "Maestria del Acero", "El arma deja de pesar. +3 % de daño total por rango.", ARMAS, 5, 5, DANO_TOTAL, 0.03, prereq = "arm9"),

    // ─── DEFENSA ───
    t("def1", "Cuero Curtido", "Lo primero que aprende un recluta es a encajar. +3 % de armadura por rango.", DEFENSA, 1, 5, ARMADURA, 0.03),
    t("def2", "Vigor de Campaña", "Dormir en el suelo endurece. +2 % de vida maxima por rango.", DEFENSA, 1, 5, VIDA_MAX, 0.02, prereq = "def1"),
    t("def3", "Postura Cerrada", "Con el escudo pegado al cuerpo entra menos. +2 % de reduccion de daño por rango.", DEFENSA, 2, 4, REDUCCION_DANO, 0.02, prereq = "def1"),
    t("def4", "Pie Ligero", "Ni toda la defensa es coraza. +1,2 % de esquiva por rango.", DEFENSA, 2, 4, ESQUIVA, 1.2, prereq = "def2"),
    t("def5", "Aguante Terco", "Cuanto peor va, mas te cierras. +3 % de reduccion por debajo del 35 % de vida por rango.", DEFENSA, 3, 4, REDUCCION_DANO, 0.03, VIDA_BAJA, "def3"),
    t("def6", "Coraza Clavada", "Quien te golpea se lleva algo. +4 % del daño recibido devuelto por rango.", DEFENSA, 3, 3, ESPINAS, 0.04, prereq = "def3"),
    t("def7", "Sangre Fria", "Los grandes asustan a otros, no a ti. +4 % de reduccion contra elites y jefes por rango.", DEFENSA, 3, 4, REDUCCION_DANO, 0.04, CONTRA_GRANDES, "def5"),
    t("def8", "Escudo de Marcha", "Entras al combate ya cubierto. Escudo inicial del 3 % de tu vida por rango.", DEFENSA, 4, 4, ESCUDO_INICIAL, 0.03, prereq = "def6"),
    t("def9", "Fondo de Reserva", "El cuerpo guarda mas de lo que crees. +3 % de vida maxima por rango.", DEFENSA, 4, 5, VIDA_MAX, 0.03, prereq = "def2"),
    t("def10", "Muro de Escudos", "En calabozo cierras filas. +4 % de reduccion en calabozo por rango.", DEFENSA, 5, 4, REDUCCION_DANO, 0.04, EN_CALABOZO, "def7"),
    t("def11", "Inquebrantable", "No caes a la primera. Sobrevives a un golpe mortal con el 8 % de vida por rango.", DEFENSA, 5, 2, ULTIMO_ALIENTO, 0.08, prereq = "def10"),

    // ─── ARCANO ───
    t("arc1", "Letras Basicas", "Todo humano culto sabe leer una runa. +2 % de daño magico por rango.", ARCANO, 1, 5, DANO_MAGICO, 0.02),
    t("arc2", "Pulso Sereno", "Respirar hondo abarata el conjuro. -2 % de coste de mana por rango.", ARCANO, 1, 4, COSTE_MANA, 0.02, prereq = "arc1"),
    t("arc3", "Deposito Amplio", "Cabe mas de lo que parece. +4 % de mana maximo por rango.", ARCANO, 2, 5, MANA_MAX, 0.04, prereq = "arc1"),
    t("arc4", "Goteo Arcano", "El mana vuelve solo si no lo fuerzas. Recuperas un 1 % de mana por turno por rango.", ARCANO, 2, 4, REGEN_MANA_TURNO, 0.01, prereq = "arc3"),
    t("arc5", "Foco Prolongado", "Aguantar el conjuro lo hace mas hondo. +4 % de daño de habilidad desde el quinto turno por rango.", ARCANO, 3, 4, DANO_HABILIDAD, 0.04, COMBATE_LARGO, "arc2"),
    t("arc6", "Sifon Menor", "Cada impacto te devuelve algo. Robas un 1 % del daño como mana por rango.", ARCANO, 3, 4, ROBO_MANA, 0.01, prereq = "arc4"),
    t("arc7", "Runa de Apertura", "El primer conjuro sale limpio. +15 % de daño de habilidad en el primer turno por rango.", ARCANO, 3, 3, DANO_HABILIDAD, 0.15, PRIMER_TURNO, "arc5"),
    t("arc8", "Sello Persistente", "Tu anti-curacion dura un turno mas por rango.", ARCANO, 4, 2, ANTI_CURACION_EXTRA, 1.0, prereq = "arc7"),
    t("arc9", "Canalizacion Firme", "+5 % de daño magico por rango.", ARCANO, 4, 5, DANO_MAGICO, 0.05, prereq = "arc5"),
    t("arc10", "Reserva Profunda", "+5 % de mana maximo por rango.", ARCANO, 5, 4, MANA_MAX, 0.05, prereq = "arc9"),
    t("arc11", "Eco del Conjuro", "+6 % de daño de habilidad por rango.", ARCANO, 5, 4, DANO_HABILIDAD, 0.06, prereq = "arc10"),

    // ─── SOMBRA ───
    t("som1", "Paso Callado", "Aprendes a no pisar la rama. +1,5 % de esquiva por rango.", SOMBRA, 1, 5, ESQUIVA, 1.5),
    t("som2", "Filo Escondido", "Lo que no se ve venir corta mas. +2,5 % de probabilidad de critico por rango.", SOMBRA, 1, 4, CRIT_PROB, 2.5, prereq = "som1"),
    t("som3", "Emboscada", "El primer golpe llega antes de que te miren. +20 % de daño en el primer turno por rango.", SOMBRA, 2, 3, DANO_TOTAL, 0.20, PRIMER_TURNO, "som2"),
    t("som4", "Juntura Abierta", "+4 % de penetracion por rango.", SOMBRA, 2, 4, PENETRACION, 0.04, prereq = "som2"),
    t("som5", "Sombra Larga", "Cuanto mas dura el combate, menos te ven. +1,5 % de esquiva desde el quinto turno por rango.", SOMBRA, 3, 4, ESQUIVA, 1.5, COMBATE_LARGO, "som1"),
    t("som6", "Corte Limpio", "+10 % de multiplicador critico por rango.", SOMBRA, 3, 4, CRIT_MULT, 0.10, prereq = "som3"),
    t("som7", "Cazador de Colosos", "Los grandes tienen mas puntos ciegos. +5 % de critico contra elites y jefes por rango.", SOMBRA, 4, 4, CRIT_PROB, 5.0, CONTRA_GRANDES, "som6"),
    t("som8", "Golpe Certero", "El primer golpe del combate es critico garantizado.", SOMBRA, 4, 1, PRIMER_GOLPE_CRITICO, 1.0, prereq = "som3"),
    t("som9", "Silencio Util", "+3 % de daño total por rango.", SOMBRA, 5, 5, DANO_TOTAL, 0.03, prereq = "som7"),
    t("som10", "Escape Previsto", "+2 % de reduccion de daño por rango.", SOMBRA, 5, 4, REDUCCION_DANO, 0.02, prereq = "som5"),
    t("som11", "Danza de Cuchillos", "+8 % de multiplicador critico por rango.", SOMBRA, 5, 3, CRIT_MULT, 0.08, prereq = "som8"),

    // ─── SANGRE ───
    t("san1", "Herida Util", "Aprendes a beber del golpe. Robas un 1 % del daño como vida por rango.", SANGRE, 1, 5, ROBO_VIDA, 0.01),
    t("san2", "Cicatriz Rapida", "Recuperas un 0,8 % de vida por turno por rango.", SANGRE, 1, 4, REGEN_VIDA_TURNO, 0.008, prereq = "san1"),
    t("san3", "Furia Contenida", "El dolor te enfoca. +8 % de daño por debajo del 35 % de vida por rango.", SANGRE, 2, 4, DANO_TOTAL, 0.08, VIDA_BAJA, "san1"),
    t("san4", "Sed Creciente", "+2 % de robo de vida contra elites y jefes por rango.", SANGRE, 2, 3, ROBO_VIDA, 0.02, CONTRA_GRANDES, "san1"),
    t("san5", "Segundo Corazon", "+3 % de vida maxima por rango.", SANGRE, 3, 5, VIDA_MAX, 0.03, prereq = "san2"),
    t("san6", "Rabia Sostenida", "El combate largo te alimenta. +1,5 % de daño acumulativo por turno por rango.", SANGRE, 3, 4, FURIA_CRECIENTE, 0.015, prereq = "san3"),
    t("san7", "Vendaje de Guerra", "+1,2 % de vida por turno por rango.", SANGRE, 4, 4, REGEN_VIDA_TURNO, 0.012, prereq = "san5"),
    t("san8", "Ultimo Aliento", "Sobrevives a un golpe mortal con el 10 % de vida por rango.", SANGRE, 4, 2, ULTIMO_ALIENTO, 0.10, prereq = "san7"),
    t("san9", "Vampirismo Marcial", "+2 % de robo de vida por rango.", SANGRE, 5, 4, ROBO_VIDA, 0.02, prereq = "san4"),
    t("san10", "Corazon Terco", "+4 % de vida maxima por rango.", SANGRE, 5, 4, VIDA_MAX, 0.04, prereq = "san8"),
    t("san11", "Frenesi Final", "+12 % de daño por debajo del 35 % de vida por rango.", SANGRE, 5, 3, DANO_TOTAL, 0.12, VIDA_BAJA, "san10"),

    // ─── FORTUNA ───
    t("for1", "Ojo de Mercader", "Sabes lo que vale lo que recoges. +4 % de oro por rango.", FORTUNA, 1, 5, ORO, 0.04),
    t("for2", "Buena Estrella", "Aprendes rapido de lo que sale mal. +3 % de experiencia por rango.", FORTUNA, 1, 5, EXP, 0.03, prereq = "for1"),
    t("for3", "Manos Limpias", "Encuentras lo bueno antes que nadie. +2 % de rareza del botin por rango.", FORTUNA, 2, 4, RAREZA_BOTIN, 0.02, prereq = "for1"),
    t("for4", "Trago Aprovechado", "Tus pociones rinden un 4 % mas por rango.", FORTUNA, 2, 4, POCION_POTENCIA, 0.04, prereq = "for2"),
    t("for5", "Frasco Bien Tapado", "Tus efectos de pocion duran un turno mas por rango.", FORTUNA, 3, 2, POCION_DURACION, 1.0, prereq = "for4"),
    t("for6", "Ultimo Sorbo", "Un 5 % de probabilidad por rango de no gastar la pocion que bebes.", FORTUNA, 3, 4, POCION_AHORRO, 0.05, prereq = "for4"),
    t("for7", "Golpe de Suerte", "+2 % de probabilidad de critico por rango.", FORTUNA, 3, 4, CRIT_PROB, 2.0, prereq = "for3"),
    t("for8", "Botin de Cripta", "+5 % de oro en calabozo por rango.", FORTUNA, 4, 4, ORO, 0.05, EN_CALABOZO, "for1"),
    t("for9", "Leccion Dura", "+5 % de experiencia contra elites y jefes por rango.", FORTUNA, 4, 4, EXP, 0.05, CONTRA_GRANDES, "for2"),
    t("for10", "Alquimia Casera", "+6 % de potencia de pocion por rango.", FORTUNA, 5, 4, POCION_POTENCIA, 0.06, prereq = "for6"),
    t("for11", "Fortuna del Osado", "+3 % de rareza del botin por rango.", FORTUNA, 5, 3, RAREZA_BOTIN, 0.03, prereq = "for8"),

    // ─── BESTIA ───
    t("bes1", "Mano Firme", "La bestia obedece a quien no duda. +4 % de daño de mascota por rango.", BESTIA, 1, 5, DANO_MASCOTA, 0.04),
    t("bes2", "Rancho Propio", "+4 % de vida de mascota por rango.", BESTIA, 1, 4, VIDA_MASCOTA, 0.04, prereq = "bes1"),
    t("bes3", "Cazan en Pareja", "Peleais mejor juntos. +4 % de daño propio con mascota equipada por rango.", BESTIA, 2, 4, DANO_TOTAL, 0.04, CON_MASCOTA, "bes1"),
    t("bes4", "Cebo Vivo", "La bestia se lleva parte de la atencion. +3 % de reduccion con mascota por rango.", BESTIA, 2, 4, REDUCCION_DANO, 0.03, CON_MASCOTA, "bes2"),
    t("bes5", "Colmillo Afilado", "+6 % de daño de mascota por rango.", BESTIA, 3, 4, DANO_MASCOTA, 0.06, prereq = "bes1"),
    t("bes6", "Vinculo Sano", "+5 % de vida de mascota por rango.", BESTIA, 3, 4, VIDA_MASCOTA, 0.05, prereq = "bes2"),
    t("bes7", "Acoso Coordinado", "+6 % de daño de mascota contra elites y jefes por rango.", BESTIA, 4, 4, DANO_MASCOTA, 0.06, CONTRA_GRANDES, "bes5"),
    t("bes8", "Calor de Manada", "Con mascota recuperas un 1 % de vida por turno por rango.", BESTIA, 4, 3, REGEN_VIDA_TURNO, 0.01, CON_MASCOTA, "bes6"),
    t("bes9", "Adiestramiento Duro", "+8 % de daño de mascota por rango.", BESTIA, 5, 4, DANO_MASCOTA, 0.08, prereq = "bes7"),
    t("bes10", "Bestia de Cripta", "+6 % de daño de mascota en calabozo por rango.", BESTIA, 5, 3, DANO_MASCOTA, 0.06, EN_CALABOZO, "bes9"),
    t("bes11", "Un Solo Animal", "+5 % de daño propio con mascota por rango.", BESTIA, 5, 3, DANO_TOTAL, 0.05, CON_MASCOTA, "bes3"),

    // ─── LEGADO ───
    t("leg1", "Disciplina", "Lo que separa a un soldado de un matón. +2 % de daño total por rango.", LEGADO, 1, 5, DANO_TOTAL, 0.02),
    t("leg2", "Instruccion", "+2 % de armadura por rango.", LEGADO, 1, 5, ARMADURA, 0.02, prereq = "leg1"),
    t("leg3", "Impetu de Mando", "Ganas un 8 % mas de impetu por rango.", LEGADO, 2, 4, IMPETU_GANANCIA, 0.08, prereq = "leg1"),
    t("leg4", "Ejemplo", "+3 % de vida maxima por rango.", LEGADO, 2, 4, VIDA_MAX, 0.03, prereq = "leg2"),
    t("leg5", "Aguantar la Linea", "+3 % de reduccion en calabozo por rango.", LEGADO, 3, 4, REDUCCION_DANO, 0.03, EN_CALABOZO, "leg4"),
    t("leg6", "Veterania", "+4 % de daño desde el quinto turno por rango.", LEGADO, 3, 4, DANO_TOTAL, 0.04, COMBATE_LARGO, "leg3"),
    t("leg7", "Reputacion", "+4 % de oro por rango.", LEGADO, 4, 3, ORO, 0.04, prereq = "leg5"),
    t("leg8", "Mando en Campaña", "+3 % de daño con la vida por encima del 80 % por rango.", LEGADO, 4, 4, DANO_TOTAL, 0.03, VIDA_ALTA, "leg6"),
    t("leg9", "Botiquin de Compañia", "Con alguna pocion activa recuperas un 1 % de vida por turno por rango.", LEGADO, 5, 3, REGEN_VIDA_TURNO, 0.01, CON_POCION_ACTIVA, "leg7"),
    t("leg10", "Orden Cerrado", "+3 % de armadura por rango.", LEGADO, 5, 4, ARMADURA, 0.03, prereq = "leg8"),
    t("leg11", "Legado Humano", "Lo que dejas escrito pesa mas que lo que cortas. +4 % de experiencia por rango.", LEGADO, 5, 4, EXP, 0.04, prereq = "leg9"),

    // ─── EVOLUCIONES ───
    // Nivel 20 · CRUZADO
    t("ev1a", "Juramento del Cruzado", "Al jurar la cruzada, tu acero recuerda por que peleas. +5 % de daño total por rango.", LEGADO, 6, 4, DANO_TOTAL, 0.05, prereq = "leg6", evo = 1),
    t("ev1b", "Fe de Marcha", "+5 % de vida maxima por rango.", DEFENSA, 6, 4, VIDA_MAX, 0.05, prereq = "def10", evo = 1),
    t("ev1c", "Botin de Cruzada", "+8 % de oro por rango.", FORTUNA, 6, 3, ORO, 0.08, prereq = "for11", evo = 1),
    t("ev1d", "Brio del Converso", "+6 % de daño en el primer turno por rango.", ARMAS, 6, 3, DANO_TOTAL, 0.06, PRIMER_TURNO, "arm11", evo = 1),
    // Nivel 50 · ABANDERADO
    t("ev2a", "Estandarte en Alto", "Quien lleva la bandera no puede caer primero. +6 % de reduccion de daño por rango.", DEFENSA, 7, 4, REDUCCION_DANO, 0.06, prereq = "ev1b", evo = 2),
    t("ev2b", "Grito de Guerra", "+8 % de daño contra elites y jefes por rango.", ARMAS, 7, 4, DANO_TOTAL, 0.08, CONTRA_GRANDES, "ev1d", evo = 2),
    t("ev2c", "Carga del Abanderado", "+12 % de impetu ganado por rango.", LEGADO, 7, 3, IMPETU_GANANCIA, 0.12, prereq = "ev1a", evo = 2),
    t("ev2d", "Cantimplora del Capitan", "+10 % de potencia de pocion por rango.", FORTUNA, 7, 3, POCION_POTENCIA, 0.10, prereq = "ev1c", evo = 2),
    // Nivel 100 · HERALDO ETERNO
    t("ev3a", "Heraldo Eterno", "Ya no eres un hombre: eres el aviso de lo que viene. +8 % de daño total por rango.", LEGADO, 8, 5, DANO_TOTAL, 0.08, prereq = "ev2c", evo = 3),
    t("ev3b", "Cuerpo Imperecedero", "+8 % de vida maxima por rango.", DEFENSA, 8, 5, VIDA_MAX, 0.08, prereq = "ev2a", evo = 3),
    t("ev3c", "Sentencia del Heraldo", "+15 % de multiplicador critico por rango.", ARMAS, 8, 4, CRIT_MULT, 0.15, prereq = "ev2b", evo = 3),
    t("ev3d", "Segunda Voluntad", "Sobrevives a un golpe mortal con el 20 % de vida por rango.", SANGRE, 8, 2, ULTIMO_ALIENTO, 0.20, prereq = "san11", evo = 3),
)
