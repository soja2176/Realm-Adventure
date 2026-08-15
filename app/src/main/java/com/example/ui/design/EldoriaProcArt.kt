package com.example.ui.design

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// ──────────────────────────────────────────────────────────────────────────────
//  Arte procedural de Eldoria.
//  100 % Canvas/Path/Brush. Determinista por semilla: toda posición aleatoria se
//  calcula con kotlin.random.Random(seed) dentro de remember(seed) o dentro de un
//  helper puro. Nunca Math.random ni System.currentTimeMillis en el dibujo.
// ──────────────────────────────────────────────────────────────────────────────

private const val TAU = 6.2831855f

private fun Float.safe(): Float = if (this.isFinite()) this else 0f

// ═════════════════════════════ AMBIENTE ═══════════════════════════════════════

private class EmberSpec(
    val x: Float,
    val phase: Float,
    val speed: Float,
    val radiusDp: Float,
    val drift: Float,
    val alphaScale: Float
)

/** Brasas ascendentes. Máximo 32 partículas; posiciones calculadas UNA vez en remember(seed). */
@Composable
fun EldoriaEmberField(
    modifier: Modifier = Modifier,
    count: Int = 22,
    tint: Color = Eldoria.Ember,
    periodMs: Int = 9000,
    seed: Int = 7,
    maxAlpha: Float = 0.55f
) {
    val n = count.coerceIn(0, 32)
    val specs = remember(seed, n) {
        val rnd = Random(seed)
        List(n) {
            EmberSpec(
                x = rnd.nextFloat(),
                phase = rnd.nextFloat(),
                speed = 0.55f + rnd.nextFloat() * 0.85f,
                radiusDp = 0.7f + rnd.nextFloat() * 1.9f,
                drift = (rnd.nextFloat() - 0.5f) * 0.10f,
                alphaScale = 0.4f + rnd.nextFloat() * 0.6f
            )
        }
    }
    if (n == 0) return

    val transition = rememberInfiniteTransition(label = "eldoriaEmbers")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMs.coerceAtLeast(800), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "eldoriaEmbersT"
    )

    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val core = Eldoria.EmberCore
        specs.forEach { s ->
            var local = (t * s.speed + s.phase) % 1f
            if (local < 0f) local += 1f
            val y = size.height * (1f - local)
            val x = (size.width * s.x + size.width * s.drift * sin(local * TAU + s.phase * TAU)).safe()
            val fade = sin(local * PI.toFloat()).coerceIn(0f, 1f)
            val a = (maxAlpha * s.alphaScale * fade).coerceIn(0f, 1f)
            if (a <= 0.012f) return@forEach
            val r = (s.radiusDp * density).coerceAtLeast(0.8f)
            drawCircle(color = tint.copy(alpha = a * 0.24f), radius = r * 3.0f, center = Offset(x, y))
            drawCircle(color = tint.copy(alpha = a * 0.75f), radius = r * 1.4f, center = Offset(x, y))
            drawCircle(color = core.copy(alpha = a), radius = r * 0.55f, center = Offset(x, y))
        }
    }
}

/** Bruma en bandas horizontales que se cruzan a distinta velocidad. */
@Composable
fun EldoriaFogLayer(
    modifier: Modifier = Modifier,
    tint: Color = Eldoria.Iron,
    alpha: Float = 0.16f,
    periodMs: Int = 15000,
    bands: Int = 3
) {
    val n = bands.coerceIn(1, 5)
    val transition = rememberInfiniteTransition(label = "eldoriaFog")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMs.coerceAtLeast(1200), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "eldoriaFogT"
    )

    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        for (i in 0 until n) {
            val f = (i + 1f) / (n + 1f)
            val bandH = size.height * (0.16f + 0.09f * i)
            val cy = size.height * f
            val dir = if (i % 2 == 0) 1f else -1f
            val shift = (t * dir - i * 0.21f) * size.width
            val a = (alpha * (1f - i * 0.20f)).coerceIn(0f, 1f)
            val brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    tint.copy(alpha = a),
                    tint.copy(alpha = a * 0.45f),
                    Color.Transparent
                ),
                startX = shift - size.width * 0.75f,
                endX = shift + size.width * 1.05f
            )
            drawRect(
                brush = brush,
                topLeft = Offset(0f, cy - bandH / 2f),
                size = Size(size.width, bandH)
            )
        }
    }
}

/** Viñeta radial: cierra la escena y empuja la mirada al centro. */
@Composable
fun EldoriaVignette(
    modifier: Modifier = Modifier,
    strength: Float = 0.6f,
    tint: Color = Color.Black,
    centerBiasY: Float = 0.5f
) {
    val s = strength.coerceIn(0f, 1f)
    if (s <= 0.001f) return
    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val r = max(size.width, size.height) * 0.82f
        val center = Offset(size.width * 0.5f, size.height * centerBiasY.coerceIn(0f, 1f))
        drawRect(
            brush = Brush.radialGradient(
                0.00f to Color.Transparent,
                0.50f to tint.copy(alpha = s * 0.12f),
                0.78f to tint.copy(alpha = s * 0.50f),
                1.00f to tint.copy(alpha = s),
                center = center,
                radius = r
            )
        )
    }
}

/**
 * Luz de antorcha: círculo cálido cuyo radio depende de intensity (0f..1f) + parpadeo.
 * Núcleo de la ambientación de calabozo — a menos antorcha, más oscuridad devora la pantalla.
 */
@Composable
fun EldoriaTorchLight(
    modifier: Modifier = Modifier,
    intensity: Float,
    warm: Color = Eldoria.EmberCore,
    flicker: Boolean = true,
    centerX: Float = 0.5f,
    centerY: Float = 0.5f
) {
    val i = intensity.coerceIn(0f, 1f)
    val flickFast = if (flicker) eldoriaPulse(periodMs = 730, from = 0.90f, to = 1.10f, label = "torchFast") else 1f
    val flickSlow = if (flicker) eldoriaPulse(periodMs = 1900, from = 0.93f, to = 1.07f, label = "torchSlow") else 1f

    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val maxDim = max(size.width, size.height)
        val center = Offset(size.width * centerX.coerceIn(0f, 1f), size.height * centerY.coerceIn(0f, 1f))
        val radius = (maxDim * (0.15f + 0.60f * i) * flickFast).coerceAtLeast(2f)

        // 1. Oscuridad envolvente proporcional a lo que falta de antorcha.
        val darkA = (0.78f * (1f - i)).coerceIn(0f, 0.9f)
        if (darkA > 0.01f) {
            drawRect(
                brush = Brush.radialGradient(
                    0.00f to Color.Transparent,
                    0.55f to Color.Black.copy(alpha = darkA * 0.30f),
                    1.00f to Color.Black.copy(alpha = darkA),
                    center = center,
                    radius = radius * 1.85f
                )
            )
        }

        // 2. Halo cálido en tres paradas.
        drawCircle(
            brush = Brush.radialGradient(
                0.00f to warm.copy(alpha = (0.42f * i * flickSlow).coerceIn(0f, 1f)),
                0.34f to warm.copy(alpha = (0.21f * i).coerceIn(0f, 1f)),
                0.68f to Eldoria.Ember.copy(alpha = (0.11f * i).coerceIn(0f, 1f)),
                1.00f to Color.Transparent,
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )

        // 3. Núcleo incandescente.
        drawCircle(
            brush = Brush.radialGradient(
                0.00f to warm.copy(alpha = (0.55f * i * flickFast).coerceIn(0f, 1f)),
                1.00f to Color.Transparent,
                center = center,
                radius = radius * 0.22f
            ),
            radius = radius * 0.22f,
            center = center
        )
    }
}

// ═════════════════════════════ RUNAS Y EMBLEMAS ═══════════════════════════════

/** Glifo rúnico angular derivado de la semilla. */
@Composable
fun EldoriaRuneGlyph(
    seed: Int,
    modifier: Modifier = Modifier,
    color: Color = Eldoria.Gold,
    strokeWidth: Dp = 2.dp,
    animated: Boolean = false
) {
    val glow = if (animated) eldoriaPulse(periodMs = 2400, from = 0.45f, to = 1f, label = "rune") else 1f
    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val pad = size.minDimension * 0.13f
        val w = size.width - pad * 2f
        val h = size.height - pad * 2f
        if (w <= 1f || h <= 1f) return@Canvas
        val path = eldoriaRunePath(seed, w, h)
        val sw = strokeWidth.toPx().coerceAtLeast(0.6f)
        translate(pad, pad) {
            drawPath(
                path, color.copy(alpha = (0.16f * glow).coerceIn(0f, 1f)),
                style = Stroke(width = sw * 3.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path, color.copy(alpha = (0.42f * glow).coerceIn(0f, 1f)),
                style = Stroke(width = sw * 1.9f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path, color.copy(alpha = (0.98f * glow).coerceIn(0f, 1f)),
                style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

private class CrestSpec(
    val division: Int,
    val charge: Int,
    val chargeCount: Int,
    val bordure: Boolean,
    val chief: Boolean,
    val chevron: Boolean
)

/** Escudo heráldico procedural: partición + carga central + bordura. Reconocible por semilla. */
@Composable
fun EldoriaCrest(
    seed: Int,
    modifier: Modifier = Modifier,
    primary: Color = Eldoria.Gold,
    secondary: Color = Eldoria.Iron,
    ornate: Boolean = true
) {
    val spec = remember(seed) {
        val rnd = Random(seed)
        CrestSpec(
            division = rnd.nextInt(5),
            charge = rnd.nextInt(6),
            chargeCount = 1 + rnd.nextInt(3),
            bordure = rnd.nextBoolean(),
            chief = rnd.nextBoolean(),
            chevron = rnd.nextBoolean()
        )
    }

    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val w = size.width
        val h = size.height
        val shield = eldoriaShieldPath(w, h)
        val dark = Color(
            red = secondary.red * 0.35f,
            green = secondary.green * 0.35f,
            blue = secondary.blue * 0.35f,
            alpha = 1f
        )

        clipPath(shield) {
            drawRect(brush = Brush.verticalGradient(listOf(secondary, dark)), size = Size(w, h))

            val tint = primary.copy(alpha = 0.22f)
            when (spec.division) {
                0 -> drawRect(color = tint, topLeft = Offset(0f, 0f), size = Size(w * 0.5f, h))
                1 -> drawRect(color = tint, topLeft = Offset(0f, 0f), size = Size(w, h * 0.5f))
                2 -> {
                    drawRect(color = tint, topLeft = Offset(0f, 0f), size = Size(w * 0.5f, h * 0.5f))
                    drawRect(color = tint, topLeft = Offset(w * 0.5f, h * 0.5f), size = Size(w * 0.5f, h * 0.5f))
                }
                3 -> {
                    val bend = Path()
                    bend.moveTo(0f, 0f); bend.lineTo(w, h); bend.lineTo(0f, h); bend.close()
                    drawPath(bend, tint)
                }
                else -> {
                    // campo liso con degradado radial de brillo
                    drawCircle(
                        brush = Brush.radialGradient(
                            0f to primary.copy(alpha = 0.16f),
                            1f to Color.Transparent,
                            center = Offset(w * 0.5f, h * 0.38f),
                            radius = w * 0.6f
                        ),
                        radius = w * 0.6f,
                        center = Offset(w * 0.5f, h * 0.38f)
                    )
                }
            }

            if (spec.chief) {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(primary.copy(alpha = 0.85f), primary.copy(alpha = 0.35f))
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(w, h * 0.16f)
                )
            }

            if (spec.chevron) {
                val chev = Path()
                chev.moveTo(w * 0.08f, h * 0.72f)
                chev.lineTo(w * 0.5f, h * 0.44f)
                chev.lineTo(w * 0.92f, h * 0.72f)
                drawPath(chev, primary.copy(alpha = 0.55f), style = Stroke(width = h * 0.055f, join = StrokeJoin.Miter))
            }

            // carga central (una, dos o tres)
            val cw = w * (if (spec.chargeCount == 1) 0.42f else 0.24f)
            val cy = h * 0.46f
            for (k in 0 until spec.chargeCount) {
                val cx = if (spec.chargeCount == 1) w * 0.5f
                else w * (0.5f + (k - (spec.chargeCount - 1) / 2f) * 0.26f)
                drawCrestCharge(spec.charge, cx, cy, cw * 0.5f, primary)
            }

            // sombra interior inferior: volumen
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)),
                    startY = h * 0.55f,
                    endY = h
                ),
                size = Size(w, h)
            )
        }

        // bordura y filo metálico
        val edgeW = (min(w, h) * 0.055f).coerceIn(1f, 8f)
        if (spec.bordure) {
            drawPath(
                shield,
                brush = Brush.verticalGradient(listOf(primary.copy(alpha = 0.45f), Color.Transparent)),
                style = Stroke(width = edgeW * 2.6f)
            )
        }
        drawPath(shield, color = Color.Black.copy(alpha = 0.55f), style = Stroke(width = edgeW * 1.5f))
        drawPath(
            shield,
            brush = Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.75f).compositeOver(primary),
                    primary,
                    Color.Black.copy(alpha = 0.45f).compositeOver(primary)
                )
            ),
            style = Stroke(width = edgeW, join = StrokeJoin.Round)
        )

        if (ornate) {
            val rr = edgeW * 0.85f
            val rivets = listOf(
                Offset(w * 0.5f, h * 0.055f),
                Offset(w * 0.13f, h * 0.20f),
                Offset(w * 0.87f, h * 0.20f),
                Offset(w * 0.5f, h * 0.93f)
            )
            rivets.forEach { o ->
                drawCircle(color = Color.Black.copy(alpha = 0.5f), radius = rr * 1.25f, center = o)
                drawCircle(
                    brush = Brush.radialGradient(
                        0f to Color.White.copy(alpha = 0.85f).compositeOver(primary),
                        1f to primary.copy(alpha = 0.6f),
                        center = Offset(o.x - rr * 0.3f, o.y - rr * 0.3f),
                        radius = rr * 1.4f
                    ),
                    radius = rr,
                    center = o
                )
            }
        }
    }
}

private fun DrawScope.drawCrestCharge(kind: Int, cx: Float, cy: Float, r: Float, color: Color) {
    val glow = color.copy(alpha = 0.30f)
    when (kind) {
        // Losange
        0 -> {
            drawPath(eldoriaDiamondPath(cx, cy, r * 1.15f), glow)
            drawPath(eldoriaDiamondPath(cx, cy, r), color)
            drawPath(eldoriaDiamondPath(cx, cy, r * 0.5f), Color.Black.copy(alpha = 0.35f))
        }
        // Estrella de 5 puntas
        1 -> {
            val p = Path()
            for (k in 0 until 10) {
                val rad = if (k % 2 == 0) r else r * 0.42f
                val ang = -PI.toFloat() / 2f + k * PI.toFloat() / 5f
                val x = cx + cos(ang) * rad
                val y = cy + sin(ang) * rad
                if (k == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            p.close()
            drawPath(p, color)
        }
        // Cruz patada
        2 -> {
            val t = r * 0.34f
            drawRect(color, topLeft = Offset(cx - t, cy - r), size = Size(t * 2f, r * 2f))
            drawRect(color, topLeft = Offset(cx - r, cy - t), size = Size(r * 2f, t * 2f))
            drawRect(Color.Black.copy(alpha = 0.30f), topLeft = Offset(cx - t * 0.4f, cy - t * 0.4f), size = Size(t * 0.8f, t * 0.8f))
        }
        // Torre almenada
        3 -> {
            val bw = r * 1.35f
            drawRect(color, topLeft = Offset(cx - bw * 0.5f, cy - r * 0.45f), size = Size(bw, r * 1.5f))
            val merlon = bw / 5f
            for (k in 0 until 3) {
                drawRect(
                    color,
                    topLeft = Offset(cx - bw * 0.5f + k * merlon * 2f, cy - r * 0.95f),
                    size = Size(merlon, r * 0.55f)
                )
            }
            drawRect(
                Color.Black.copy(alpha = 0.45f),
                topLeft = Offset(cx - merlon * 0.5f, cy + r * 0.35f),
                size = Size(merlon, r * 0.7f)
            )
        }
        // Espada
        4 -> {
            val blade = Path()
            blade.moveTo(cx, cy - r * 1.05f)
            blade.lineTo(cx + r * 0.22f, cy - r * 0.6f)
            blade.lineTo(cx + r * 0.14f, cy + r * 0.55f)
            blade.lineTo(cx - r * 0.14f, cy + r * 0.55f)
            blade.lineTo(cx - r * 0.22f, cy - r * 0.6f)
            blade.close()
            drawPath(blade, color)
            drawRect(color, topLeft = Offset(cx - r * 0.62f, cy + r * 0.5f), size = Size(r * 1.24f, r * 0.20f))
            drawRect(color, topLeft = Offset(cx - r * 0.12f, cy + r * 0.7f), size = Size(r * 0.24f, r * 0.42f))
            drawCircle(color, radius = r * 0.17f, center = Offset(cx, cy + r * 1.16f))
        }
        // Ojo arcano
        else -> {
            val eye = Path()
            eye.moveTo(cx - r, cy)
            eye.cubicTo(cx - r * 0.45f, cy - r * 0.85f, cx + r * 0.45f, cy - r * 0.85f, cx + r, cy)
            eye.cubicTo(cx + r * 0.45f, cy + r * 0.85f, cx - r * 0.45f, cy + r * 0.85f, cx - r, cy)
            eye.close()
            drawPath(eye, color)
            drawCircle(Color.Black.copy(alpha = 0.72f), radius = r * 0.38f, center = Offset(cx, cy))
            drawCircle(color.copy(alpha = 0.9f), radius = r * 0.16f, center = Offset(cx, cy))
        }
    }
}

private class BeastSpec(
    val bodyW: Float,
    val bodyH: Float,
    val headR: Float,
    val hornCurl: Float,
    val hornCount: Int,
    val spikes: Int,
    val jaw: Boolean,
    val tail: Boolean
)

/** Emblema procedural de criatura (mascotas y enemigos sin JPG). stage 1..3 añade cuernos/alas/halo. */
@Composable
fun EldoriaBeastSigil(
    seed: Int,
    modifier: Modifier = Modifier,
    primary: Color,
    secondary: Color,
    stage: Int = 1,
    animated: Boolean = true
) {
    val st = stage.coerceIn(1, 3)
    val spec = remember(seed) {
        val rnd = Random(seed)
        BeastSpec(
            bodyW = 0.46f + rnd.nextFloat() * 0.16f,
            bodyH = 0.34f + rnd.nextFloat() * 0.14f,
            headR = 0.15f + rnd.nextFloat() * 0.07f,
            hornCurl = -0.5f + rnd.nextFloat(),
            hornCount = 1 + rnd.nextInt(2),
            spikes = 3 + rnd.nextInt(4),
            jaw = rnd.nextBoolean(),
            tail = rnd.nextBoolean()
        )
    }
    val pulse = if (animated) eldoriaPulse(periodMs = 2100, from = 0.55f, to = 1f, label = "beast") else 1f

    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val bodyCy = h * 0.66f
        val bw = w * spec.bodyW
        val bh = h * spec.bodyH
        val headR = min(w, h) * spec.headR
        val headCy = bodyCy - bh * 0.62f - headR * 0.35f

        // aura
        drawCircle(
            brush = Brush.radialGradient(
                0f to primary.copy(alpha = 0.20f * pulse),
                1f to Color.Transparent,
                center = Offset(cx, h * 0.55f),
                radius = min(w, h) * 0.62f
            ),
            radius = min(w, h) * 0.62f,
            center = Offset(cx, h * 0.55f)
        )

        // ── Etapa 3: halo ──
        if (st >= 3) {
            val hr = headR * 1.75f
            drawCircle(
                color = Eldoria.GoldBright.copy(alpha = 0.28f + 0.35f * pulse),
                radius = hr,
                center = Offset(cx, headCy - headR * 1.55f),
                style = Stroke(width = min(w, h) * 0.022f)
            )
            drawCircle(
                color = Eldoria.GoldBright.copy(alpha = 0.14f * pulse),
                radius = hr * 1.28f,
                center = Offset(cx, headCy - headR * 1.55f),
                style = Stroke(width = min(w, h) * 0.012f)
            )
        }

        // ── Etapa 2: alas detrás ──
        if (st >= 2) {
            for (side in intArrayOf(-1, 1)) {
                val wing = Path()
                val sx = cx + side * bw * 0.42f
                wing.moveTo(sx, bodyCy - bh * 0.35f)
                wing.cubicTo(
                    sx + side * w * 0.30f, bodyCy - bh * 1.35f,
                    sx + side * w * 0.44f, bodyCy - bh * 0.25f,
                    sx + side * w * 0.30f, bodyCy + bh * 0.42f
                )
                wing.cubicTo(
                    sx + side * w * 0.20f, bodyCy + bh * 0.10f,
                    sx + side * w * 0.12f, bodyCy - bh * 0.10f,
                    sx, bodyCy - bh * 0.35f
                )
                wing.close()
                drawPath(wing, secondary.copy(alpha = 0.85f))
                drawPath(wing, primary.copy(alpha = 0.55f), style = Stroke(width = min(w, h) * 0.014f))
            }
        }

        // ── Cuerpo ──
        val body = Path()
        body.moveTo(cx, bodyCy - bh)
        body.cubicTo(cx + bw * 1.05f, bodyCy - bh * 0.85f, cx + bw * 0.95f, bodyCy + bh * 0.95f, cx, bodyCy + bh)
        body.cubicTo(cx - bw * 0.95f, bodyCy + bh * 0.95f, cx - bw * 1.05f, bodyCy - bh * 0.85f, cx, bodyCy - bh)
        body.close()
        drawPath(
            body,
            brush = Brush.verticalGradient(
                listOf(primary.copy(alpha = 0.92f), secondary),
                startY = bodyCy - bh,
                endY = bodyCy + bh
            )
        )
        drawPath(body, Color.Black.copy(alpha = 0.55f), style = Stroke(width = min(w, h) * 0.016f))

        // púas dorsales
        for (k in 0 until spec.spikes) {
            val f = (k + 0.5f) / spec.spikes
            val x = cx - bw * 0.7f + bw * 1.4f * f
            val y = bodyCy - bh * (0.55f + 0.30f * sin(f * PI.toFloat()))
            val sp = Path()
            sp.moveTo(x - bw * 0.09f, y)
            sp.lineTo(x, y - bh * 0.34f)
            sp.lineTo(x + bw * 0.09f, y)
            sp.close()
            drawPath(sp, primary.copy(alpha = 0.85f))
        }

        // cola
        if (spec.tail) {
            val tail = Path()
            tail.moveTo(cx - bw * 0.85f, bodyCy + bh * 0.30f)
            tail.cubicTo(
                cx - bw * 1.6f, bodyCy + bh * 0.55f,
                cx - bw * 1.5f, bodyCy - bh * 0.25f,
                cx - bw * 1.15f, bodyCy - bh * 0.55f
            )
            drawPath(
                tail, secondary,
                style = Stroke(width = min(w, h) * 0.030f, cap = StrokeCap.Round)
            )
        }

        // ── Cabeza ──
        drawCircle(
            brush = Brush.verticalGradient(
                listOf(primary, secondary),
                startY = headCy - headR,
                endY = headCy + headR
            ),
            radius = headR,
            center = Offset(cx, headCy)
        )
        drawCircle(
            Color.Black.copy(alpha = 0.55f),
            radius = headR,
            center = Offset(cx, headCy),
            style = Stroke(width = min(w, h) * 0.014f)
        )

        // ── Etapa 1+: cuernos ──
        for (side in intArrayOf(-1, 1)) {
            for (hIdx in 0 until spec.hornCount) {
                val base = Offset(
                    cx + side * headR * (0.55f + hIdx * 0.32f),
                    headCy - headR * (0.62f - hIdx * 0.18f)
                )
                val horn = Path()
                horn.moveTo(base.x, base.y)
                horn.cubicTo(
                    base.x + side * headR * (0.75f + spec.hornCurl * 0.5f), base.y - headR * 1.05f,
                    base.x + side * headR * (0.35f - spec.hornCurl * 0.6f), base.y - headR * 1.75f,
                    base.x + side * headR * (0.15f + spec.hornCurl * 0.4f), base.y - headR * 2.05f
                )
                drawPath(
                    horn, Eldoria.ParchmentDim,
                    style = Stroke(width = headR * (0.30f - hIdx * 0.07f), cap = StrokeCap.Round)
                )
            }
        }

        // ojos
        val eyeR = headR * 0.19f
        for (side in intArrayOf(-1, 1)) {
            val eo = Offset(cx + side * headR * 0.38f, headCy - headR * 0.10f)
            drawCircle(Color.Black.copy(alpha = 0.85f), radius = eyeR * 1.7f, center = eo)
            drawCircle(Eldoria.EmberCore.copy(alpha = 0.45f + 0.55f * pulse), radius = eyeR, center = eo)
        }

        // mandíbula
        if (spec.jaw) {
            val teeth = 4
            for (k in 0 until teeth) {
                val f = (k + 0.5f) / teeth
                val x = cx - headR * 0.5f + headR * f
                val t = Path()
                t.moveTo(x - headR * 0.07f, headCy + headR * 0.42f)
                t.lineTo(x, headCy + headR * 0.72f)
                t.lineTo(x + headR * 0.07f, headCy + headR * 0.42f)
                t.close()
                drawPath(t, Eldoria.Parchment.copy(alpha = 0.9f))
            }
        }
    }
}

/** Cuatro esquinas de filigrana metálica: brazo en L + voluta + remache. */
@Composable
fun EldoriaFiligreeCorners(
    modifier: Modifier = Modifier,
    color: Color = Eldoria.Gold,
    inset: Dp = 3.dp,
    armLength: Dp = 20.dp,
    strokeWidth: Dp = 1.5.dp
) {
    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val i = inset.toPx()
        val arm = armLength.toPx().coerceAtMost(min(size.width, size.height) * 0.42f)
        val sw = strokeWidth.toPx().coerceAtLeast(0.5f)
        if (arm <= sw * 2f) return@Canvas

        val corners = listOf(
            Triple(Offset(i, i), 1f, 1f),
            Triple(Offset(size.width - i, i), -1f, 1f),
            Triple(Offset(i, size.height - i), 1f, -1f),
            Triple(Offset(size.width - i, size.height - i), -1f, -1f)
        )
        corners.forEach { (o, sx, sy) ->
            val p = Path()
            p.moveTo(o.x + sx * arm, o.y)
            p.lineTo(o.x + sx * arm * 0.30f, o.y)
            p.cubicTo(
                o.x + sx * arm * 0.05f, o.y,
                o.x, o.y + sy * arm * 0.05f,
                o.x, o.y + sy * arm * 0.30f
            )
            p.lineTo(o.x, o.y + sy * arm)

            // voluta interior
            p.moveTo(o.x + sx * arm * 0.30f, o.y + sy * arm * 0.08f)
            p.cubicTo(
                o.x + sx * arm * 0.55f, o.y + sy * arm * 0.16f,
                o.x + sx * arm * 0.46f, o.y + sy * arm * 0.46f,
                o.x + sx * arm * 0.20f, o.y + sy * arm * 0.40f
            )

            drawPath(p, color.copy(alpha = 0.20f), style = Stroke(width = sw * 3f, cap = StrokeCap.Round))
            drawPath(p, color.copy(alpha = 0.92f), style = Stroke(width = sw, cap = StrokeCap.Round))
            drawCircle(color.copy(alpha = 0.95f), radius = sw * 1.5f, center = Offset(o.x + sx * arm, o.y))
            drawCircle(color.copy(alpha = 0.95f), radius = sw * 1.5f, center = Offset(o.x, o.y + sy * arm))
        }
    }
}

/** Vitral: facetas radiales con plomos oscuros y un resplandor central. */
@Composable
fun EldoriaStainedGlass(
    modifier: Modifier = Modifier,
    glow: Color,
    base: Color,
    facets: Int = 6,
    seed: Int = 3
) {
    val n = facets.coerceIn(3, 16)
    val alphas = remember(seed, n) {
        val rnd = Random(seed)
        FloatArray(n) { 0.25f + rnd.nextFloat() * 0.55f }
    }
    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val r = max(size.width, size.height) * 0.78f
        val step = TAU / n

        drawRect(base)
        for (k in 0 until n) {
            val a0 = -PI.toFloat() / 2f + k * step
            val a1 = a0 + step
            val wedge = Path()
            wedge.moveTo(cx, cy)
            wedge.lineTo(cx + cos(a0) * r, cy + sin(a0) * r)
            wedge.lineTo(cx + cos((a0 + a1) / 2f) * r * 1.05f, cy + sin((a0 + a1) / 2f) * r * 1.05f)
            wedge.lineTo(cx + cos(a1) * r, cy + sin(a1) * r)
            wedge.close()
            drawPath(wedge, glow.copy(alpha = alphas[k] * 0.55f))
            drawPath(wedge, Color.Black.copy(alpha = 0.55f), style = Stroke(width = min(size.width, size.height) * 0.020f))
        }
        drawCircle(
            brush = Brush.radialGradient(
                0f to glow.copy(alpha = 0.85f),
                0.5f to glow.copy(alpha = 0.30f),
                1f to Color.Transparent,
                center = Offset(cx, cy),
                radius = min(size.width, size.height) * 0.42f
            ),
            radius = min(size.width, size.height) * 0.42f,
            center = Offset(cx, cy)
        )
        drawCircle(
            Color.Black.copy(alpha = 0.6f),
            radius = min(size.width, size.height) * 0.16f,
            center = Offset(cx, cy),
            style = Stroke(width = min(size.width, size.height) * 0.022f)
        )
    }
}

/** Grietas de piedra: textura para paneles de mazmorra. */
@Composable
fun EldoriaCrackedStone(
    modifier: Modifier = Modifier,
    seed: Int = 11,
    color: Color = Eldoria.IronDeep,
    density: Int = 14,
    alpha: Float = 0.5f
) {
    val n = density.coerceIn(1, 40)
    val cracks = remember(seed, n) {
        val rnd = Random(seed)
        List(n) {
            val startX = rnd.nextFloat()
            val startY = rnd.nextFloat()
            val segments = 3 + rnd.nextInt(4)
            val pts = ArrayList<Offset>(segments + 1)
            var x = startX
            var y = startY
            pts.add(Offset(x, y))
            var ang = rnd.nextFloat() * TAU
            for (s in 0 until segments) {
                ang += (rnd.nextFloat() - 0.5f) * 1.5f
                val len = 0.05f + rnd.nextFloat() * 0.14f
                x = (x + cos(ang) * len).coerceIn(0f, 1f)
                y = (y + sin(ang) * len).coerceIn(0f, 1f)
                pts.add(Offset(x, y))
            }
            pts
        }
    }
    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val sw = min(size.width, size.height) * 0.006f
        cracks.forEach { pts ->
            val p = Path()
            pts.forEachIndexed { idx, o ->
                val x = o.x * size.width
                val y = o.y * size.height
                if (idx == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            drawPath(
                p, Color.Black.copy(alpha = alpha * 0.55f),
                style = Stroke(width = sw * 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                p, color.copy(alpha = alpha),
                style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

/** Grafo de nodos del mapa de expedición: líneas con glow + tramo discontinuo si no está disponible. */
@Composable
fun EldoriaLeyLines(
    modifier: Modifier = Modifier,
    nodes: List<Offset>,
    edges: List<Triple<Int, Int, Boolean>>,
    color: Color = Eldoria.Ember,
    strokeWidth: Dp = 2.dp,
    animated: Boolean = true
) {
    if (nodes.isEmpty()) return
    val normalized = remember(nodes) {
        nodes.all { it.x.isFinite() && it.y.isFinite() && abs(it.x) <= 1.5f && abs(it.y) <= 1.5f }
    }
    val travel = if (animated) {
        val transition = rememberInfiniteTransition(label = "leyLines")
        val v by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "leyTravel"
        )
        v
    } else 1f

    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val sw = strokeWidth.toPx().coerceAtLeast(0.75f)
        fun place(o: Offset): Offset =
            if (normalized) Offset(o.x * size.width, o.y * size.height) else o

        val dash = PathEffect.dashPathEffect(floatArrayOf(sw * 3.5f, sw * 3.5f), 0f)

        edges.forEach { (fromIdx, toIdx, available) ->
            if (fromIdx !in nodes.indices || toIdx !in nodes.indices) return@forEach
            val a = place(nodes[fromIdx])
            val b = place(nodes[toIdx])
            if (available) {
                drawLine(color.copy(alpha = 0.12f), a, b, sw * 5f, StrokeCap.Round)
                drawLine(color.copy(alpha = 0.28f), a, b, sw * 2.6f, StrokeCap.Round)
                drawLine(color.copy(alpha = 0.95f), a, b, sw, StrokeCap.Round)
                if (animated) {
                    val t = travel
                    val px = a.x + (b.x - a.x) * t
                    val py = a.y + (b.y - a.y) * t
                    val fade = sin(t * PI.toFloat()).coerceIn(0f, 1f)
                    drawCircle(Eldoria.EmberCore.copy(alpha = 0.85f * fade), radius = sw * 1.9f, center = Offset(px, py))
                    drawCircle(color.copy(alpha = 0.25f * fade), radius = sw * 4.2f, center = Offset(px, py))
                }
            } else {
                drawLine(
                    color = Eldoria.IronEdge.copy(alpha = 0.55f),
                    start = a,
                    end = b,
                    strokeWidth = sw * 0.9f,
                    cap = StrokeCap.Round,
                    pathEffect = dash
                )
            }
        }

        nodes.forEach { raw ->
            val o = place(raw)
            drawPath(eldoriaDiamondPath(o.x, o.y, sw * 3.4f), Color.Black.copy(alpha = 0.7f))
            drawPath(eldoriaDiamondPath(o.x, o.y, sw * 2.6f), color.copy(alpha = 0.9f))
            drawPath(eldoriaDiamondPath(o.x, o.y, sw * 1.1f), Eldoria.EmberCore.copy(alpha = 0.95f))
        }
    }
}

/** Estallido radial disparado al incrementar [trigger]: anillo + rayos. */
@Composable
fun EldoriaImpactBurst(
    trigger: Int,
    modifier: Modifier = Modifier,
    color: Color = Eldoria.EmberCore,
    rays: Int = 12,
    durationMs: Int = 380
) {
    val n = rays.coerceIn(3, 32)
    val anim = remember { Animatable(1f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            anim.snapTo(0f)
            anim.animateTo(1f, tween(durationMillis = durationMs.coerceAtLeast(80), easing = EldoriaMotion.easeOut))
        }
    }
    val p = anim.value
    if (p >= 0.999f) return

    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val maxR = min(size.width, size.height) * 0.5f
        val fade = (1f - p).coerceIn(0f, 1f)
        val ringR = maxR * (0.15f + 0.85f * p)

        drawCircle(
            color = color.copy(alpha = 0.55f * fade),
            radius = ringR,
            center = Offset(cx, cy),
            style = Stroke(width = maxR * 0.10f * fade + 0.5f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                0f to color.copy(alpha = 0.45f * fade * fade),
                1f to Color.Transparent,
                center = Offset(cx, cy),
                radius = ringR * 0.9f
            ),
            radius = ringR * 0.9f,
            center = Offset(cx, cy)
        )
        for (k in 0 until n) {
            val ang = k * TAU / n
            val inner = ringR * 0.55f
            val outer = ringR * (1.05f + 0.25f * ((k % 3) / 3f))
            drawLine(
                color = color.copy(alpha = 0.85f * fade),
                start = Offset(cx + cos(ang) * inner, cy + sin(ang) * inner),
                end = Offset(cx + cos(ang) * outer, cy + sin(ang) * outer),
                strokeWidth = (maxR * 0.045f * fade).coerceAtLeast(0.75f),
                cap = StrokeCap.Round
            )
        }
    }
}

/** Cifra de daño/curación que sube y se desvanece. Se relanza al incrementar [trigger]. */
@Composable
fun EldoriaDamageFloater(
    text: String,
    trigger: Int,
    modifier: Modifier = Modifier,
    color: Color = Eldoria.BloodBright,
    big: Boolean = false
) {
    val anim = remember { Animatable(1f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            anim.snapTo(0f)
            anim.animateTo(1f, tween(durationMillis = 950, easing = EldoriaMotion.easeOut))
        }
    }
    val p = anim.value
    if (p >= 0.999f) return

    val riseTarget = if (big) 62.dp else 44.dp
    val risePx = with(LocalDensity.current) { riseTarget.toPx() }
    val style = if (big) EldoriaType.numericBig else EldoriaType.numeric
    val popScale = 1f + (if (big) 0.45f else 0.22f) * (1f - p) * (1f - p)
    val fadeA = if (p < 0.15f) (p / 0.15f).coerceIn(0f, 1f) else (1f - (p - 0.15f) / 0.85f).coerceIn(0f, 1f)

    Box(modifier) {
        Text(
            text = text,
            style = style,
            color = Color.Black.copy(alpha = 0.85f),
            modifier = Modifier.graphicsLayer {
                translationY = -risePx * p + 2f
                translationX = 2f
                alpha = fadeA
                scaleX = popScale
                scaleY = popScale
            }
        )
        Text(
            text = text,
            style = style,
            color = color,
            modifier = Modifier.graphicsLayer {
                translationY = -risePx * p
                alpha = fadeA
                scaleX = popScale
                scaleY = popScale
            }
        )
    }
}

/** Rejilla de líneas finísimas: da textura de vidrio/CRT a paneles arcanos. */
@Composable
fun EldoriaScanlines(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    alpha: Float = 0.04f,
    gapDp: Dp = 3.dp
) {
    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val gap = gapDp.toPx().coerceAtLeast(1.5f)
        val c = color.copy(alpha = alpha.coerceIn(0f, 1f))
        var y = 0f
        while (y < size.height) {
            drawLine(c, Offset(0f, y), Offset(size.width, y), 1f)
            y += gap
        }
    }
}

// ═════════════════════════════ HELPERS PUROS ══════════════════════════════════

/** Trazo rúnico angular (tipo fuþark) determinista por semilla, en una caja w×h. */
fun eldoriaRunePath(seed: Int, w: Float, h: Float): Path {
    val p = Path()
    if (w <= 0f || h <= 0f) return p
    val rnd = Random(seed)

    val stemX = w * (0.34f + rnd.nextFloat() * 0.32f)
    p.moveTo(stemX, 0f)
    p.lineTo(stemX, h)

    val branches = 2 + rnd.nextInt(3) // 2..4 brazos
    for (i in 0 until branches) {
        val y = h * (0.10f + 0.75f * ((i + 0.5f) / branches))
        val dir = if (rnd.nextBoolean()) 1f else -1f
        val len = w * (0.22f + 0.20f * rnd.nextFloat())
        val drop = h * (0.10f + 0.16f * rnd.nextFloat()) * (if (rnd.nextBoolean()) 1f else -1f)
        val ex = (stemX + dir * len).coerceIn(0f, w)
        val ey = (y + drop).coerceIn(0f, h)
        p.moveTo(stemX, y)
        p.lineTo(ex, ey)
        if (rnd.nextInt(3) == 0) {
            val tx = (ex - dir * len * 0.45f).coerceIn(0f, w)
            val ty = (ey + h * 0.16f).coerceIn(0f, h)
            p.lineTo(tx, ty)
        }
    }

    // remate: travesaño o pie
    when (rnd.nextInt(3)) {
        0 -> {
            val y = h * 0.5f
            p.moveTo((stemX - w * 0.28f).coerceIn(0f, w), y)
            p.lineTo((stemX + w * 0.28f).coerceIn(0f, w), y)
        }
        1 -> {
            p.moveTo((stemX - w * 0.22f).coerceIn(0f, w), h)
            p.lineTo(stemX, h * 0.80f)
            p.lineTo((stemX + w * 0.22f).coerceIn(0f, w), h)
        }
        else -> {
            val r = min(w, h) * 0.10f
            p.moveTo(stemX - r, 0f)
            p.lineTo(stemX, r)
            p.lineTo(stemX + r, 0f)
        }
    }
    return p
}

/** Rombo (losange) centrado en (cx, cy) con semidiagonal r. */
fun eldoriaDiamondPath(cx: Float, cy: Float, r: Float): Path {
    val p = Path()
    p.moveTo(cx, cy - r)
    p.lineTo(cx + r, cy)
    p.lineTo(cx, cy + r)
    p.lineTo(cx - r, cy)
    p.close()
    return p
}

/** Escudo heráldico clásico: hombros rectos y punta ojival. */
fun eldoriaShieldPath(w: Float, h: Float): Path {
    val p = Path()
    if (w <= 0f || h <= 0f) return p
    val shoulder = h * 0.14f
    p.moveTo(w * 0.05f, shoulder)
    p.cubicTo(w * 0.05f, h * 0.035f, w * 0.09f, 0f, w * 0.22f, 0f)
    p.lineTo(w * 0.78f, 0f)
    p.cubicTo(w * 0.91f, 0f, w * 0.95f, h * 0.035f, w * 0.95f, shoulder)
    p.lineTo(w * 0.95f, h * 0.50f)
    p.cubicTo(w * 0.95f, h * 0.80f, w * 0.72f, h * 0.95f, w * 0.50f, h)
    p.cubicTo(w * 0.28f, h * 0.95f, w * 0.05f, h * 0.80f, w * 0.05f, h * 0.50f)
    p.close()
    return p
}

/** Par (claro, oscuro) por clave de paleta. Claves: EMBER, MANA, VITAE, ARCANE, IRON, BLOOD, SILVER, GOLD. */
fun eldoriaPaletteOf(key: String): Pair<Color, Color> = when (key.uppercase()) {
    "EMBER" -> Eldoria.EmberCore to Eldoria.EmberDeep
    "MANA" -> Eldoria.ManaBright to Eldoria.ManaDeep
    "VITAE" -> Eldoria.VitaeBright to Eldoria.VitaeDeep
    "ARCANE" -> Eldoria.ArcaneBright to Eldoria.ArcaneDeep
    "IRON" -> Eldoria.IronEdge to Eldoria.IronDeep
    "BLOOD" -> Eldoria.BloodBright to Eldoria.BloodDeep
    "SILVER" -> Eldoria.Silver to Eldoria.SilverDeep
    "GOLD" -> Eldoria.GoldBright to Eldoria.GoldDeep
    else -> Eldoria.EmberCore to Eldoria.EmberDeep
}
