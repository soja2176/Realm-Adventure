package com.example.ui.design

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// ══════════════════════════════════════════════════════════════════════════════
//  EFECTOS DE COMBATE
//
//  Cada habilidad golpea distinto y debe VERSE distinto: el acero corta, el
//  fuego envuelve, lo arcano dibuja un círculo de runas, la sombra se cierra
//  hacia dentro y lo sagrado cae desde arriba. Todo se dibuja con Canvas
//  (nada de `blur`, que es no-op por debajo de API 31) y todo arranca al
//  incrementar `trigger`, de modo que el mismo golpe repetido se vuelve a ver.
// ══════════════════════════════════════════════════════════════════════════════

/** Familia de efecto. La habilidad decide cuál se dispara. */
enum class CombatFx {
    PHYSICAL,   // acero: tajos limpios
    BLOOD,      // corte sangriento: tajo + salpicadura
    FIRE,       // llamaradas envolventes
    NECROTIC,   // fuego maldito verde + ceniza
    POISON,     // burbujas y niebla tóxica
    ARCANE,     // círculo de runas giratorio
    SHADOW,     // zarcillos que se cierran hacia dentro
    HOLY,       // columna de luz descendente
    HEAL,       // motas ascendentes
    WARCRY,     // ondas de grito concéntricas
    FROST,      // esquirlas que convergen
    NONE
}

/** Duración natural de cada familia (ms). Un tajo es seco; un ritual, largo. */
fun CombatFx.durationMs(): Int = when (this) {
    CombatFx.PHYSICAL -> 340
    CombatFx.BLOOD -> 430
    CombatFx.FIRE -> 620
    CombatFx.NECROTIC -> 640
    CombatFx.POISON -> 700
    CombatFx.ARCANE -> 640
    CombatFx.SHADOW -> 560
    CombatFx.HOLY -> 700
    CombatFx.HEAL -> 720
    CombatFx.WARCRY -> 520
    CombatFx.FROST -> 560
    CombatFx.NONE -> 1
}

/** Color rector del efecto: tiñe destellos, flashes y textos flotantes. */
fun CombatFx.accent(): Color = when (this) {
    CombatFx.PHYSICAL -> Color(0xFFEFF4FA)
    CombatFx.BLOOD -> Eldoria.BloodBright
    CombatFx.FIRE -> Eldoria.Ember
    CombatFx.NECROTIC -> Color(0xFF7CF29B)
    CombatFx.POISON -> Color(0xFF9CCC65)
    CombatFx.ARCANE -> Eldoria.ArcaneBright
    CombatFx.SHADOW -> Color(0xFF8E6BD0)
    CombatFx.HOLY -> Eldoria.GoldBright
    CombatFx.HEAL -> Eldoria.VitaeBright
    CombatFx.WARCRY -> Eldoria.EmberCore
    CombatFx.FROST -> Color(0xFFAEE6FF)
    CombatFx.NONE -> Color.Transparent
}

/**
 * Traduce el id de habilidad a su familia de efecto.
 * Los ids desconocidos (clases futuras, habilidades de evolución) caen en
 * ARCANE si gastan maná: es el genérico "esto es magia" del juego.
 */
fun combatFxForSkill(skillId: String, healing: Boolean = false, damaging: Boolean = true): CombatFx =
    when (skillId) {
        "g_1" -> CombatFx.BLOOD      // Golpe Sangriento
        "g_2" -> CombatFx.WARCRY     // Grito de Provocación
        "m_1" -> CombatFx.ARCANE     // Centella Arcana
        "m_2" -> CombatFx.NECROTIC   // Llama Necrótica
        "p_1" -> CombatFx.POISON     // Puñalada Venenosa
        "p_2" -> CombatFx.SHADOW     // Ataque Sombrío
        "c_1" -> CombatFx.HOLY       // Luz Sagrada
        "c_2" -> CombatFx.HOLY       // Martillo de Justicia
        else -> when {
            healing && !damaging -> CombatFx.HEAL
            !damaging -> CombatFx.WARCRY
            else -> CombatFx.ARCANE
        }
    }

/** Efecto del enemigo según su arquetipo: cada bestia pega a su manera. */
fun combatFxForEnemyArchetype(archetype: String): CombatFx = when (archetype.uppercase()) {
    "BRUTO", "BRUTE" -> CombatFx.PHYSICAL
    "ARDIENTE", "ÍGNEO", "IGNEO", "FIRE" -> CombatFx.FIRE
    "NO_MUERTO", "NOMUERTO", "UNDEAD" -> CombatFx.NECROTIC
    "PONZOÑOSO", "PONZONOSO", "POISON" -> CombatFx.POISON
    "ARCANO", "ARCANE", "HECHICERO" -> CombatFx.ARCANE
    "SOMBRÍO", "SOMBRIO", "SHADOW", "ACECHADOR" -> CombatFx.SHADOW
    "GÉLIDO", "GELIDO", "FROST" -> CombatFx.FROST
    "SANGRIENTO", "BLOOD" -> CombatFx.BLOOD
    else -> CombatFx.SHADOW
}

/**
 * Efecto sobre el objetivo. Se dibuja encima del retrato, ocupando su caja.
 * Se relanza cada vez que [trigger] crece; con `trigger == 0` no pinta nada.
 */
@Composable
fun EldoriaSkillFx(
    fx: CombatFx,
    trigger: Int,
    modifier: Modifier = Modifier,
    seed: Int = 0
) {
    if (fx == CombatFx.NONE) return

    val anim = remember { Animatable(1f) }
    LaunchedEffect(trigger, fx) {
        if (trigger > 0) {
            anim.snapTo(0f)
            anim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = fx.durationMs(), easing = LinearEasing)
            )
        }
    }

    val p = anim.value
    if (p >= 0.999f) return

    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        when (fx) {
            CombatFx.PHYSICAL -> drawSlashes(p, Color(0xFFEFF4FA), 3, seed)
            CombatFx.BLOOD -> drawBlood(p, seed)
            CombatFx.FIRE -> drawFlames(p, Eldoria.EmberCore, Eldoria.Ember, Eldoria.EmberDeep, seed)
            CombatFx.NECROTIC -> drawFlames(p, Color(0xFFD9FFE4), Color(0xFF5FD98A), Color(0xFF12452A), seed)
            CombatFx.POISON -> drawPoison(p, seed)
            CombatFx.ARCANE -> drawRuneCircle(p, Eldoria.ArcaneBright, Eldoria.Arcane, seed)
            CombatFx.SHADOW -> drawShadowGrasp(p, seed)
            CombatFx.HOLY -> drawHolyBeam(p, seed)
            CombatFx.HEAL -> drawHealMotes(p, seed)
            CombatFx.WARCRY -> drawShoutRings(p)
            CombatFx.FROST -> drawFrost(p, seed)
            CombatFx.NONE -> Unit
        }
    }
}

/**
 * Velo de color a pantalla completa del arena. Muy sutil: subraya el golpe
 * sin tapar los retratos ni cansar la vista en combates largos.
 */
@Composable
fun EldoriaCombatFlash(
    fx: CombatFx,
    trigger: Int,
    modifier: Modifier = Modifier,
    maxAlpha: Float = 0.20f
) {
    if (fx == CombatFx.NONE) return

    val anim = remember { Animatable(1f) }
    LaunchedEffect(trigger, fx) {
        if (trigger > 0) {
            anim.snapTo(0f)
            anim.animateTo(1f, tween(durationMillis = 320, easing = LinearEasing))
        }
    }
    val p = anim.value
    if (p >= 0.999f) return

    // Sube de golpe y se apaga despacio: así se lee como un impacto, no como un parpadeo.
    val intensity = if (p < 0.22f) p / 0.22f else (1f - (p - 0.22f) / 0.78f)
    val a = (maxAlpha * intensity).coerceIn(0f, 1f)
    val color = fx.accent()

    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        // Anillo de color pegado a los bordes: el centro queda limpio.
        drawRect(
            brush = Brush.radialGradient(
                0f to Color.Transparent,
                0.55f to color.copy(alpha = a * 0.35f),
                1f to color.copy(alpha = a),
                center = center,
                radius = size.maxDimension * 0.72f
            )
        )
    }
}

// ───────────────────────────── dibujantes por familia ────────────────────────

/** Tajos diagonales que barren la caja y se apagan. */
private fun DrawScope.drawSlashes(p: Float, color: Color, count: Int, seed: Int) {
    val rnd = Random(seed * 31 + 7)
    val fade = (1f - p).coerceIn(0f, 1f)
    val w = size.width
    val h = size.height

    for (i in 0 until count) {
        val delay = i * 0.14f
        val local = ((p - delay) / (1f - delay).coerceAtLeast(0.01f)).coerceIn(0f, 1f)
        if (local <= 0f) continue

        val y0 = h * (0.18f + 0.30f * rnd.nextFloat())
        val down = rnd.nextBoolean()
        val y1 = if (down) y0 + h * 0.55f else y0 - h * 0.35f
        val sweep = local
        val x0 = -w * 0.15f
        val x1 = x0 + (w * 1.3f) * sweep
        val yy1 = y0 + (y1 - y0) * sweep
        val alpha = fade * (1f - local * 0.35f)

        // estela ancha + filo blanco
        drawLine(
            color = color.copy(alpha = 0.22f * alpha),
            start = Offset(x0, y0),
            end = Offset(x1, yy1),
            strokeWidth = 9.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color.copy(alpha = 0.95f * alpha),
            start = Offset(x0 + (x1 - x0) * 0.55f, y0 + (yy1 - y0) * 0.55f),
            end = Offset(x1, yy1),
            strokeWidth = 2.4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // chispa del impacto
    if (p < 0.4f) {
        val sparkFade = 1f - p / 0.4f
        drawCircle(
            color = Color.White.copy(alpha = 0.8f * sparkFade),
            radius = size.minDimension * 0.10f * (0.4f + p),
            center = center
        )
    }
}

/** Corte + salpicadura: el tajo abre y la sangre sale disparada. */
private fun DrawScope.drawBlood(p: Float, seed: Int) {
    drawSlashes(p, Eldoria.BloodBright, 2, seed)

    val rnd = Random(seed * 17 + 3)
    val fade = (1f - p).coerceIn(0f, 1f)
    val n = 14
    val maxR = size.minDimension * 0.62f
    for (i in 0 until n) {
        val ang = rnd.nextFloat() * 2f * PI.toFloat()
        val speed = 0.45f + rnd.nextFloat() * 0.55f
        val r = maxR * p * speed
        // la gota cae mientras vuela: gravedad barata pero legible
        val gy = size.height * 0.32f * p * p
        val cx = center.x + cos(ang) * r
        val cy = center.y + sin(ang) * r + gy
        val rad = (size.minDimension * 0.035f) * (1f - p * 0.5f) * (0.5f + rnd.nextFloat())
        drawCircle(
            color = Eldoria.Blood.copy(alpha = 0.85f * fade),
            radius = rad.coerceAtLeast(0.5f),
            center = Offset(cx, cy)
        )
    }
}

/** Llamaradas: lenguas de fuego que suben desde la base y se consumen. */
private fun DrawScope.drawFlames(p: Float, core: Color, mid: Color, deep: Color, seed: Int) {
    val rnd = Random(seed * 13 + 11)
    val fade = (1f - p).coerceIn(0f, 1f)
    val w = size.width
    val h = size.height

    // resplandor caliente que envuelve al objetivo
    drawCircle(
        brush = Brush.radialGradient(
            0f to core.copy(alpha = 0.42f * fade),
            0.6f to mid.copy(alpha = 0.28f * fade),
            1f to Color.Transparent,
            center = center,
            radius = size.minDimension * (0.45f + 0.30f * p)
        ),
        radius = size.minDimension * (0.45f + 0.30f * p),
        center = center
    )

    val tongues = 9
    for (i in 0 until tongues) {
        val bx = w * ((i + 0.5f) / tongues) + (rnd.nextFloat() - 0.5f) * w * 0.06f
        val delay = rnd.nextFloat() * 0.25f
        val local = ((p - delay) / (1f - delay).coerceAtLeast(0.01f)).coerceIn(0f, 1f)
        if (local <= 0f) continue

        val rise = h * (0.55f + 0.35f * rnd.nextFloat()) * local
        val sway = sin(local * 5f + i) * w * 0.05f
        val tipY = h - rise
        val width = w * 0.085f * (1f - local * 0.65f)

        val path = Path().apply {
            moveTo(bx - width, h)
            quadraticBezierTo(bx - width * 0.4f + sway, h - rise * 0.55f, bx + sway, tipY)
            quadraticBezierTo(bx + width * 0.4f + sway, h - rise * 0.55f, bx + width, h)
            close()
        }
        drawPath(path, color = mid.copy(alpha = 0.55f * (1f - local) + 0.15f))
        // núcleo claro dentro de la lengua
        val corePath = Path().apply {
            moveTo(bx - width * 0.45f, h)
            quadraticBezierTo(bx + sway, h - rise * 0.5f, bx + sway * 0.8f, tipY + rise * 0.22f)
            quadraticBezierTo(bx + sway, h - rise * 0.5f, bx + width * 0.45f, h)
            close()
        }
        drawPath(corePath, color = core.copy(alpha = 0.6f * (1f - local)))
    }

    // brasas que suben tras la llamarada
    val embers = 10
    for (i in 0 until embers) {
        val ex = rnd.nextFloat() * w
        val phase = (p + rnd.nextFloat()) % 1f
        val ey = h * (1f - phase)
        drawCircle(
            color = core.copy(alpha = 0.75f * fade * (1f - phase)),
            radius = (w * 0.012f) * (1f - phase * 0.5f),
            center = Offset(ex + sin(phase * 6f + i) * w * 0.03f, ey)
        )
    }

    // poso oscuro al pie: ceniza
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Color.Transparent, deep.copy(alpha = 0.45f * fade)),
            startY = h * 0.6f,
            endY = h
        )
    )
}

/** Veneno: niebla baja y burbujas que suben y revientan. */
private fun DrawScope.drawPoison(p: Float, seed: Int) {
    val rnd = Random(seed * 23 + 5)
    val fade = (1f - p).coerceIn(0f, 1f)
    val toxic = Color(0xFF9CCC65)
    val deep = Color(0xFF33691E)

    drawCircle(
        brush = Brush.radialGradient(
            0f to toxic.copy(alpha = 0.30f * fade),
            1f to Color.Transparent,
            center = center,
            radius = size.minDimension * (0.40f + 0.28f * p)
        ),
        radius = size.minDimension * (0.40f + 0.28f * p),
        center = center
    )

    val bubbles = 14
    for (i in 0 until bubbles) {
        val bx = rnd.nextFloat() * size.width
        val delay = rnd.nextFloat() * 0.35f
        val local = ((p - delay) / (1f - delay).coerceAtLeast(0.01f)).coerceIn(0f, 1f)
        if (local <= 0f) continue
        val by = size.height * (1f - local * (0.55f + rnd.nextFloat() * 0.4f))
        val r = size.minDimension * (0.02f + rnd.nextFloat() * 0.035f) * (1f - local * 0.4f)
        val a = fade * (1f - local * 0.55f)
        drawCircle(deep.copy(alpha = 0.55f * a), r, Offset(bx, by))
        drawCircle(
            toxic.copy(alpha = 0.9f * a),
            r,
            Offset(bx, by),
            style = Stroke(width = 1.2.dp.toPx())
        )
    }
}

/** Círculo de runas: dos anillos que giran en sentidos opuestos y se abren. */
private fun DrawScope.drawRuneCircle(p: Float, bright: Color, mid: Color, seed: Int) {
    val fade = (1f - p).coerceIn(0f, 1f)
    val maxR = size.minDimension * 0.52f
    val r = maxR * (0.35f + 0.65f * p)

    drawCircle(
        brush = Brush.radialGradient(
            0f to mid.copy(alpha = 0.30f * fade),
            1f to Color.Transparent,
            center = center,
            radius = r * 1.15f
        ),
        radius = r * 1.15f,
        center = center
    )

    val dash = PathEffect.dashPathEffect(floatArrayOf(r * 0.28f, r * 0.18f), 0f)
    rotate(degrees = p * 220f, pivot = center) {
        drawCircle(
            color = bright.copy(alpha = 0.9f * fade),
            radius = r,
            center = center,
            style = Stroke(width = 2.2.dp.toPx(), pathEffect = dash)
        )
    }
    rotate(degrees = -p * 300f, pivot = center) {
        drawCircle(
            color = mid.copy(alpha = 0.75f * fade),
            radius = r * 0.66f,
            center = center,
            style = Stroke(width = 1.6.dp.toPx(), pathEffect = dash)
        )
    }

    // glifos en los vértices del anillo exterior
    val rnd = Random(seed * 7 + 19)
    val marks = 6
    for (i in 0 until marks) {
        val ang = (i / marks.toFloat()) * 2f * PI.toFloat() + p * 2.4f
        val gx = center.x + cos(ang) * r
        val gy = center.y + sin(ang) * r
        val gs = size.minDimension * (0.035f + rnd.nextFloat() * 0.02f)
        drawPath(eldoriaDiamondPath(gx, gy, gs), bright.copy(alpha = 0.85f * fade))
    }
}

/** Sombra: zarcillos que se cierran hacia el objetivo y estallan al llegar. */
private fun DrawScope.drawShadowGrasp(p: Float, seed: Int) {
    val rnd = Random(seed * 29 + 13)
    val fade = (1f - p).coerceIn(0f, 1f)
    val violet = Color(0xFF8E6BD0)
    val maxR = size.minDimension * 0.75f
    // convergen (p<0.6) y luego el punto de impacto se abre (p>=0.6)
    val closing = (p / 0.6f).coerceIn(0f, 1f)

    val n = 9
    for (i in 0 until n) {
        val ang = (i / n.toFloat()) * 2f * PI.toFloat() + rnd.nextFloat() * 0.4f
        val from = maxR * (1f - closing) + size.minDimension * 0.12f
        val to = from + maxR * 0.42f * (1f - closing)
        drawLine(
            color = Eldoria.Abyss.copy(alpha = 0.85f * fade),
            start = Offset(center.x + cos(ang) * to, center.y + sin(ang) * to),
            end = Offset(center.x + cos(ang) * from, center.y + sin(ang) * from),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = violet.copy(alpha = 0.7f * fade),
            start = Offset(center.x + cos(ang) * to, center.y + sin(ang) * to),
            end = Offset(center.x + cos(ang) * from, center.y + sin(ang) * from),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    if (p >= 0.55f) {
        val burst = ((p - 0.55f) / 0.45f).coerceIn(0f, 1f)
        drawCircle(
            color = violet.copy(alpha = 0.75f * (1f - burst)),
            radius = maxR * 0.55f * burst,
            center = center,
            style = Stroke(width = (5f * (1f - burst)).coerceAtLeast(0.6f).dp.toPx())
        )
    }
}

/** Sagrado: columna de luz que cae del cielo y halo que se abre en el suelo. */
private fun DrawScope.drawHolyBeam(p: Float, seed: Int) {
    val rnd = Random(seed * 11 + 2)
    val fade = (1f - p).coerceIn(0f, 1f)
    val w = size.width
    val h = size.height
    val drop = (p / 0.45f).coerceIn(0f, 1f)

    // haz descendente
    val beamW = w * 0.42f
    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Eldoria.GoldBright.copy(alpha = 0.55f * fade),
                Eldoria.Gold.copy(alpha = 0.30f * fade),
                Color.Transparent
            ),
            startY = 0f,
            endY = h * drop
        ),
        topLeft = Offset((w - beamW) / 2f, 0f),
        size = Size(beamW, h * drop)
    )
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.65f * fade), Color.Transparent),
            startY = 0f,
            endY = h * drop
        ),
        topLeft = Offset((w - beamW * 0.28f) / 2f, 0f),
        size = Size(beamW * 0.28f, h * drop)
    )

    // halo al tocar
    if (p >= 0.35f) {
        val ring = ((p - 0.35f) / 0.65f).coerceIn(0f, 1f)
        val r = size.minDimension * (0.18f + 0.42f * ring)
        drawCircle(
            color = Eldoria.GoldBright.copy(alpha = 0.85f * (1f - ring)),
            radius = r,
            center = Offset(w / 2f, h * 0.62f),
            style = Stroke(width = (4f * (1f - ring)).coerceAtLeast(0.6f).dp.toPx())
        )
    }

    // motas que descienden con el haz
    for (i in 0 until 10) {
        val mx = (w - beamW) / 2f + rnd.nextFloat() * beamW
        val phase = (p * 1.4f + rnd.nextFloat()) % 1f
        drawCircle(
            color = Color.White.copy(alpha = 0.8f * fade * (1f - phase)),
            radius = w * 0.012f,
            center = Offset(mx, h * phase)
        )
    }
}

/** Curación: motas verdes que ascienden y un anillo suave al pie. */
private fun DrawScope.drawHealMotes(p: Float, seed: Int) {
    val rnd = Random(seed * 19 + 4)
    val fade = (1f - p).coerceIn(0f, 1f)
    val w = size.width
    val h = size.height

    drawCircle(
        brush = Brush.radialGradient(
            0f to Eldoria.VitaeBright.copy(alpha = 0.26f * fade),
            1f to Color.Transparent,
            center = center,
            radius = size.minDimension * 0.6f
        ),
        radius = size.minDimension * 0.6f,
        center = center
    )

    for (i in 0 until 16) {
        val mx = rnd.nextFloat() * w
        val delay = rnd.nextFloat() * 0.4f
        val local = ((p - delay) / (1f - delay).coerceAtLeast(0.01f)).coerceIn(0f, 1f)
        if (local <= 0f) continue
        val my = h * (1f - local)
        val sway = sin(local * 6f + i) * w * 0.04f
        val r = w * (0.012f + rnd.nextFloat() * 0.012f)
        drawCircle(
            color = Eldoria.VitaeBright.copy(alpha = 0.9f * fade * (1f - local * 0.6f)),
            radius = r,
            center = Offset(mx + sway, my)
        )
    }

    // cruz de luz al centro: se lee como "cura" sin necesidad de texto
    val cross = (1f - p) * size.minDimension * 0.16f
    if (cross > 1f) {
        val a = 0.8f * fade
        drawLine(
            Eldoria.VitaeBright.copy(alpha = a),
            Offset(center.x - cross, center.y),
            Offset(center.x + cross, center.y),
            3.dp.toPx(),
            StrokeCap.Round
        )
        drawLine(
            Eldoria.VitaeBright.copy(alpha = a),
            Offset(center.x, center.y - cross),
            Offset(center.x, center.y + cross),
            3.dp.toPx(),
            StrokeCap.Round
        )
    }
}

/** Grito: tres ondas concéntricas que se expanden desde el centro. */
private fun DrawScope.drawShoutRings(p: Float) {
    val maxR = size.minDimension * 0.8f
    for (k in 0 until 3) {
        val delay = k * 0.16f
        val local = ((p - delay) / (1f - delay).coerceAtLeast(0.01f)).coerceIn(0f, 1f)
        if (local <= 0f) continue
        val r = maxR * local
        val a = (1f - local) * 0.85f
        drawCircle(
            color = Eldoria.EmberCore.copy(alpha = a),
            radius = r,
            center = center,
            style = Stroke(width = (3.5f * (1f - local)).coerceAtLeast(0.6f).dp.toPx())
        )
        drawCircle(
            color = Eldoria.Ember.copy(alpha = a * 0.35f),
            radius = r * 0.92f,
            center = center,
            style = Stroke(width = (7f * (1f - local)).coerceAtLeast(0.6f).dp.toPx())
        )
    }
}

/** Escarcha: esquirlas que convergen y anillo hexagonal al impactar. */
private fun DrawScope.drawFrost(p: Float, seed: Int) {
    val rnd = Random(seed * 37 + 8)
    val fade = (1f - p).coerceIn(0f, 1f)
    val ice = Color(0xFFAEE6FF)
    val deep = Color(0xFF2E6E93)
    val maxR = size.minDimension * 0.8f
    val closing = (p / 0.65f).coerceIn(0f, 1f)

    for (i in 0 until 10) {
        val ang = rnd.nextFloat() * 2f * PI.toFloat()
        val d = maxR * (1f - closing)
        val len = size.minDimension * 0.16f
        val sx = center.x + cos(ang) * (d + len)
        val sy = center.y + sin(ang) * (d + len)
        val ex = center.x + cos(ang) * d
        val ey = center.y + sin(ang) * d
        drawLine(deep.copy(alpha = 0.7f * fade), Offset(sx, sy), Offset(ex, ey), 4.dp.toPx(), StrokeCap.Round)
        drawLine(ice.copy(alpha = 0.95f * fade), Offset(sx, sy), Offset(ex, ey), 1.4.dp.toPx(), StrokeCap.Round)
    }

    if (p >= 0.55f) {
        val ring = ((p - 0.55f) / 0.45f).coerceIn(0f, 1f)
        val r = size.minDimension * (0.20f + 0.35f * ring)
        val hex = Path()
        for (i in 0 until 6) {
            val a = (i / 6f) * 2f * PI.toFloat() - PI.toFloat() / 2f
            val x = center.x + cos(a) * r
            val y = center.y + sin(a) * r
            if (i == 0) hex.moveTo(x, y) else hex.lineTo(x, y)
        }
        hex.close()
        drawPath(
            hex,
            color = ice.copy(alpha = 0.9f * (1f - ring)),
            style = Stroke(width = (3f * (1f - ring)).coerceAtLeast(0.6f).dp.toPx())
        )
    }
}
