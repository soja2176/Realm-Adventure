package com.example.ui.minigames

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.audio.SoundManager
import com.example.data.model.MinigameRequest
import com.example.data.model.MinigameResult
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaImpactBurst
import com.example.ui.design.EldoriaMotion
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaDiamondPath
import com.example.ui.design.eldoriaShake
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ──────────────────────────────────────────────────────────────────────────────
//  EL YUNQUE DE GROMMASH
//  Cinco golpes. El martillo barre la barra; la zona dorada se estrecha en cada
//  golpe. Perfecto = núcleo incandescente; bueno = zona dorada; fuera = chatarra.
// ──────────────────────────────────────────────────────────────────────────────

private const val ANVIL_STRIKES = 5
private const val ANVIL_TOTAL_MS = 30_000

@Composable
fun MinigameYunque(request: MinigameRequest, onFinish: (MinigameResult) -> Unit) {
    val difficulty = request.difficulty.coerceIn(1, 5)
    var started by remember { mutableStateOf(false) }

    if (!started) {
        MinigameIntro(
            title = "EL YUNQUE DE GROMMASH",
            subtitle = if (request.title.isBlank()) "Templa el acero al ritmo del martillo" else request.title,
            tone = EldoriaTone.Ember,
            crestSeed = 4101,
            lineOne = "Toca la pantalla cuando el martillo cruce la franja dorada: el núcleo blanco es un golpe perfecto.",
            lineTwo = "Tienes 5 golpes y 30 segundos. La franja se estrecha después de cada impacto.",
            onStart = { started = true },
            onQuit = {
                onFinish(
                    MinigameResult(
                        id = "YUNQUE", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        )
        return
    }

    val qualities = remember { mutableStateListOf<Int>() }
    var finished by remember { mutableStateOf(false) }
    var burst by remember { mutableStateOf(0) }
    var shakeTrigger by remember { mutableStateOf(0) }
    var lastQuality by remember { mutableStateOf(-1) }

    val centers = remember(request.id, request.difficulty, request.contextJson) {
        val rnd = Random(request.contextJson.hashCode() * 31 + difficulty * 7 + 41)
        FloatArray(ANVIL_STRIKES) { 0.26f + rnd.nextFloat() * 0.48f }
    }

    val periodMs = (900 - difficulty * 90).coerceAtLeast(320)
    val transition = rememberInfiniteTransition(label = "anvilSweep")
    // Se leen SÓLO dentro de lambdas de dibujo: no recomponen, sólo repintan.
    val markerState = transition.animateFloat(
        initialValue = 0.02f,
        targetValue = 0.98f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anvilMarker"
    )
    val heatState = transition.animateFloat(
        initialValue = 0.62f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EldoriaMotion.easeInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anvilHeat"
    )

    val sparkAnim = remember { Animatable(1f) }
    val shakeOffset = eldoriaShake(trigger = shakeTrigger, magnitude = 9.dp, cycles = 3, label = "anvilShake")

    val strikes = qualities.size
    val perfects = qualities.count { it == 2 }
    val goods = qualities.count { it == 1 }
    val score = (perfects * 20 + goods * 10).coerceIn(0, 100)

    val closeRun: () -> Unit = closeRun@{
        if (finished) return@closeRun
        finished = true
        val p = qualities.count { it == 2 }
        val g = qualities.count { it == 1 }
        val finalScore = (p * 20 + g * 10).coerceIn(0, 100)
        val rating = when {
            p >= ANVIL_STRIKES -> "FORJA MAESTRA"
            p >= 3 -> "FORJA EXCELENTE"
            p >= 1 -> "FORJA BUENA"
            else -> "FORJA TOSCA"
        }
        onFinish(
            MinigameResult(
                id = "YUNQUE",
                success = p >= 1 || g >= 3,
                score = finalScore,
                perfect = p,
                rating = rating,
                contextJson = request.contextJson
            )
        )
    }

    val secondsLeft = MinigameCountdown(active = !finished, totalMs = ANVIL_TOTAL_MS, onExpire = closeRun)

    LaunchedEffect(strikes) {
        if (strikes > 0) {
            sparkAnim.snapTo(0f)
            sparkAnim.animateTo(1f, tween(durationMillis = 560, easing = EldoriaMotion.easeOut))
        }
    }

    LaunchedEffect(strikes, finished) {
        if (!finished && strikes >= ANVIL_STRIKES) closeRun()
    }

    val strike: () -> Unit = strike@{
        if (finished) return@strike
        val idx = qualities.size
        if (idx >= ANVIL_STRIKES) return@strike
        val half = anvilZoneHalf(idx, difficulty)
        val d = abs(markerState.value - centers[idx])
        val q = when {
            d <= half * 0.34f -> 2
            d <= half -> 1
            else -> 0
        }
        qualities.add(q)
        lastQuality = q
        burst += 1
        shakeTrigger += 1
        when (q) {
            2 -> SoundManager.playCriticalHit()
            1 -> SoundManager.playSwordSlash()
            else -> SoundManager.playEnemyAttack()
        }
    }

    val zoneIndex = strikes.coerceAtMost(ANVIL_STRIKES - 1)
    val zoneCenter = centers[zoneIndex]
    val zoneHalf = anvilZoneHalf(zoneIndex, difficulty)

    MinigameShell(
        title = "EL YUNQUE DE GROMMASH",
        subtitle = "Golpe ${(strikes + 1).coerceAtMost(ANVIL_STRIKES)}/$ANVIL_STRIKES  ·  ${MinigameClockText(secondsLeft)}",
        tone = EldoriaTone.Ember,
        scoreLabel = "$score",
        onQuit = {
            if (!finished) {
                finished = true
                onFinish(
                    MinigameResult(
                        id = "YUNQUE", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures { strike() } }
        ) {
            // ── Fragua ──
            EldoriaFrame(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(x = shakeOffset),
                edge = EldoriaEdge.Ember,
                strokeWidth = Eldoria.StrokeBold,
                filigree = true,
                rivets = true
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    anvilDrawForge(
                        heat = heatState.value,
                        temper = (perfects * 2 + goods).toFloat() / (ANVIL_STRIKES * 2f),
                        spark = sparkAnim.value
                    )
                }
                EldoriaImpactBurst(
                    trigger = burst,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(190.dp),
                    color = if (lastQuality == 2) Eldoria.GoldBright else Eldoria.Ember,
                    rays = 16,
                    durationMs = 420
                )
                Text(
                    text = anvilFeedbackText(lastQuality),
                    style = EldoriaType.title,
                    color = anvilFeedbackColor(lastQuality),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                )
            }

            Spacer(Modifier.height(Eldoria.S12))

            // ── Registro de golpes ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until ANVIL_STRIKES) {
                    val q = qualities.getOrNull(i) ?: -1
                    Canvas(modifier = Modifier.size(26.dp)) {
                        val c = when (q) {
                            2 -> Eldoria.GoldBright
                            1 -> Eldoria.Ember
                            0 -> Eldoria.IronEdge
                            else -> Eldoria.IronDeep
                        }
                        val r = size.minDimension * 0.42f
                        drawCircle(
                            brush = Brush.radialGradient(
                                0f to c.copy(alpha = if (q >= 1) 0.42f else 0.12f),
                                1f to Color.Transparent,
                                center = center,
                                radius = r * 1.6f
                            ),
                            radius = r * 1.6f,
                            center = center
                        )
                        drawPath(eldoriaDiamondPath(center.x, center.y, r), Color.Black.copy(alpha = 0.7f))
                        drawPath(eldoriaDiamondPath(center.x, center.y, r * 0.86f), c)
                        if (q == 2) {
                            drawPath(
                                eldoriaDiamondPath(center.x, center.y, r * 0.34f),
                                Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(Eldoria.S12))

            // ── Barra de temple ──
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
            ) {
                anvilDrawBar(
                    marker = markerState.value,
                    center = zoneCenter,
                    half = zoneHalf,
                    heat = heatState.value
                )
            }

            Spacer(Modifier.height(Eldoria.S8))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Canvas(modifier = Modifier.size(18.dp)) {
                    drawPath(eldoriaDiamondPath(center.x, center.y, size.minDimension * 0.4f), Eldoria.Ember)
                }
                Spacer(Modifier.width(Eldoria.S8))
                Text(
                    text = "Toca en cualquier parte para golpear",
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow
                )
            }
            Spacer(Modifier.height(Eldoria.S8))
        }
    }
}

// ───────────────────────────── ayudas privadas ────────────────────────────────

private fun anvilZoneHalf(index: Int, difficulty: Int): Float =
    (0.170f - index * 0.021f - difficulty * 0.006f).coerceAtLeast(0.048f)

private fun anvilFeedbackText(quality: Int): String = when (quality) {
    2 -> "¡TEMPLE PERFECTO!"
    1 -> "GOLPE FIRME"
    0 -> "GOLPE FALLIDO"
    else -> "PREPARA EL MARTILLO"
}

private fun anvilFeedbackColor(quality: Int): Color = when (quality) {
    2 -> Eldoria.GoldBright
    1 -> Eldoria.EmberCore
    0 -> Eldoria.Danger
    else -> Eldoria.TextLow
}

/** Yunque, lingote al rojo y chispas. Todo Path/Brush, sin asignaciones por frame salvo los Path. */
private fun DrawScope.anvilDrawForge(heat: Float, temper: Float, spark: Float) {
    val w = size.width
    val h = size.height
    if (w <= 4f || h <= 4f) return
    val cx = w * 0.5f
    val baseY = h * 0.88f

    // resplandor de la fragua
    drawCircle(
        brush = Brush.radialGradient(
            0f to Eldoria.Ember.copy(alpha = 0.28f * heat),
            0.55f to Eldoria.EmberDeep.copy(alpha = 0.14f * heat),
            1f to Color.Transparent,
            center = Offset(cx, h * 0.55f),
            radius = w * 0.55f
        ),
        radius = w * 0.55f,
        center = Offset(cx, h * 0.55f)
    )

    // pedestal
    val pedestal = Path()
    pedestal.moveTo(cx - w * 0.20f, baseY)
    pedestal.lineTo(cx + w * 0.20f, baseY)
    pedestal.lineTo(cx + w * 0.13f, baseY - h * 0.16f)
    pedestal.lineTo(cx - w * 0.13f, baseY - h * 0.16f)
    pedestal.close()
    drawPath(
        pedestal,
        brush = Brush.verticalGradient(
            listOf(Eldoria.Iron, Eldoria.IronDeep),
            startY = baseY - h * 0.16f,
            endY = baseY
        )
    )

    // cuerpo del yunque
    val body = Path()
    val topY = baseY - h * 0.34f
    body.moveTo(cx - w * 0.26f, topY)
    body.lineTo(cx + w * 0.20f, topY)
    body.lineTo(cx + w * 0.34f, topY + h * 0.045f)
    body.lineTo(cx + w * 0.19f, topY + h * 0.075f)
    body.lineTo(cx + w * 0.11f, baseY - h * 0.16f)
    body.lineTo(cx - w * 0.11f, baseY - h * 0.16f)
    body.lineTo(cx - w * 0.19f, topY + h * 0.075f)
    body.lineTo(cx - w * 0.26f, topY)
    body.close()
    drawPath(
        body,
        brush = Brush.verticalGradient(
            listOf(Eldoria.IronEdge, Eldoria.Iron, Eldoria.IronDeep),
            startY = topY,
            endY = baseY
        )
    )
    drawPath(body, Color.Black.copy(alpha = 0.6f), style = Stroke(width = w * 0.006f))

    // lingote al rojo sobre el yunque
    val ingotW = w * 0.30f
    val ingotH = h * 0.045f
    val ingotY = topY - ingotH
    val glowColor = Eldoria.Ember.copy(alpha = (0.35f + 0.5f * temper) * heat)
    drawCircle(
        brush = Brush.radialGradient(
            0f to Eldoria.GoldBright.copy(alpha = (0.30f + 0.55f * temper) * heat),
            1f to Color.Transparent,
            center = Offset(cx, ingotY + ingotH * 0.5f),
            radius = ingotW * 0.95f
        ),
        radius = ingotW * 0.95f,
        center = Offset(cx, ingotY + ingotH * 0.5f)
    )
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Eldoria.GoldBright, Eldoria.Ember, Eldoria.EmberDeep),
            startY = ingotY,
            endY = ingotY + ingotH
        ),
        topLeft = Offset(cx - ingotW * 0.5f, ingotY),
        size = Size(ingotW, ingotH)
    )
    drawRect(
        color = glowColor,
        topLeft = Offset(cx - ingotW * 0.5f, ingotY),
        size = Size(ingotW, ingotH),
        style = Stroke(width = w * 0.005f)
    )

    // martillo suspendido
    val hammerY = ingotY - h * 0.20f - h * 0.10f * (1f - spark)
    val headW = w * 0.16f
    val headH = h * 0.055f
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Eldoria.Silver, Eldoria.SilverDeep, Eldoria.IronDeep),
            startY = hammerY,
            endY = hammerY + headH
        ),
        topLeft = Offset(cx - headW * 0.5f, hammerY),
        size = Size(headW, headH)
    )
    drawRect(
        color = Eldoria.GoldDeep,
        topLeft = Offset(cx - headW * 0.10f, hammerY - h * 0.13f),
        size = Size(headW * 0.20f, h * 0.13f)
    )

    // chispas del impacto
    if (spark < 0.999f) {
        val fade = 1f - spark
        val originY = ingotY
        for (k in 0 until 14) {
            val ang = -2.6f + k * 0.30f
            val dist = w * (0.05f + 0.32f * spark) * (0.6f + (k % 4) * 0.18f)
            val px = cx + cos(ang) * dist
            val py = originY + sin(ang) * dist * 0.75f
            drawCircle(
                color = Eldoria.EmberCore.copy(alpha = 0.85f * fade),
                radius = (w * 0.006f * fade).coerceAtLeast(0.6f),
                center = Offset(px, py)
            )
            drawLine(
                color = Eldoria.Ember.copy(alpha = 0.45f * fade),
                start = Offset(cx, originY),
                end = Offset(px, py),
                strokeWidth = (w * 0.003f * fade).coerceAtLeast(0.4f),
                cap = StrokeCap.Round
            )
        }
    }
}

/** Barra de temple: canal hundido, franja dorada, núcleo perfecto y martillo deslizante. */
private fun DrawScope.anvilDrawBar(marker: Float, center: Float, half: Float, heat: Float) {
    val w = size.width
    val h = size.height
    if (w <= 4f || h <= 4f) return
    val barTop = h * 0.34f
    val barH = h * 0.44f

    drawRect(
        brush = Brush.verticalGradient(listOf(Eldoria.Abyss, Eldoria.PanelSunken)),
        topLeft = Offset(0f, barTop),
        size = Size(w, barH)
    )

    // zona buena
    val x0 = ((center - half) * w).coerceIn(0f, w)
    val x1 = ((center + half) * w).coerceIn(0f, w)
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Eldoria.Gold.copy(alpha = 0.85f), Eldoria.GoldDeep.copy(alpha = 0.55f)),
            startY = barTop,
            endY = barTop + barH
        ),
        topLeft = Offset(x0, barTop),
        size = Size((x1 - x0).coerceAtLeast(1f), barH)
    )

    // núcleo perfecto
    val p0 = ((center - half * 0.34f) * w).coerceIn(0f, w)
    val p1 = ((center + half * 0.34f) * w).coerceIn(0f, w)
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.95f * heat), Eldoria.GoldBright.copy(alpha = 0.9f)),
            startY = barTop,
            endY = barTop + barH
        ),
        topLeft = Offset(p0, barTop),
        size = Size((p1 - p0).coerceAtLeast(1f), barH)
    )

    // muescas
    var i = 0
    while (i <= 10) {
        val x = w * i / 10f
        drawLine(
            color = Eldoria.IronEdge.copy(alpha = 0.55f),
            start = Offset(x, barTop + barH * 0.72f),
            end = Offset(x, barTop + barH),
            strokeWidth = 1f
        )
        i++
    }

    // marco
    drawRect(
        color = Eldoria.IronEdge.copy(alpha = 0.9f),
        topLeft = Offset(0f, barTop),
        size = Size(w, barH),
        style = Stroke(width = 1.5f)
    )

    // martillo deslizante
    val mx = (marker * w).coerceIn(0f, w)
    drawLine(
        color = Eldoria.EmberCore.copy(alpha = 0.35f),
        start = Offset(mx, barTop - h * 0.10f),
        end = Offset(mx, barTop + barH + h * 0.10f),
        strokeWidth = h * 0.10f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color.White,
        start = Offset(mx, barTop - h * 0.06f),
        end = Offset(mx, barTop + barH + h * 0.06f),
        strokeWidth = h * 0.035f,
        cap = StrokeCap.Round
    )
    val head = Path()
    head.moveTo(mx, barTop - h * 0.06f)
    head.lineTo(mx + h * 0.12f, barTop - h * 0.26f)
    head.lineTo(mx - h * 0.12f, barTop - h * 0.26f)
    head.close()
    drawPath(head, Eldoria.EmberCore)
    drawPath(head, Color.Black.copy(alpha = 0.5f), style = Stroke(width = 1.2f))
}
