package com.example.ui.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.audio.SoundManager
import com.example.data.model.MinigameRequest
import com.example.data.model.MinigameResult
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBarTone
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaImpactBurst
import com.example.ui.design.EldoriaResourceBar
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaTorchLight
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaShake
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

// ──────────────────────────────────────────────────────────────────────────────
//  VIGILIA DEL CAMPAMENTO
//  Treinta segundos defendiendo la hoguera: devuelve las brasas que escapan y
//  espanta a los murciélagos antes de que apaguen el fuego.
// ──────────────────────────────────────────────────────────────────────────────

private const val VIGIL_TOTAL_MS = 30_000
private const val VIGIL_CAPACITY = 18
private const val VIGIL_KIND_EMBER = 0
private const val VIGIL_KIND_BAT = 1
private const val VIGIL_FIRE_X = 0.5f
private const val VIGIL_FIRE_Y = 0.64f

/** Simulación del campamento: arrays fijos, sin asignaciones por frame. */
private class VigilRun(seedValue: Int) {
    val rnd = Random(seedValue)
    val x = FloatArray(VIGIL_CAPACITY)
    val y = FloatArray(VIGIL_CAPACITY)
    val vx = FloatArray(VIGIL_CAPACITY)
    val vy = FloatArray(VIGIL_CAPACITY)
    val kind = IntArray(VIGIL_CAPACITY)
    val phase = FloatArray(VIGIL_CAPACITY)
    val live = BooleanArray(VIGIL_CAPACITY)

    var spawnT = 0f
    var clock = 0f
    val hitX = FloatArray(1)
    val hitY = FloatArray(1)

    fun spawn(batChance: Float, batSpeed: Float, emberSpeed: Float) {
        var slot = -1
        for (i in 0 until VIGIL_CAPACITY) {
            if (!live[i]) { slot = i; break }
        }
        if (slot < 0) return
        val isBat = rnd.nextFloat() < batChance
        live[slot] = true
        phase[slot] = rnd.nextFloat() * 6.2831855f
        if (isBat) {
            kind[slot] = VIGIL_KIND_BAT
            val edge = rnd.nextInt(4)
            val t = rnd.nextFloat()
            when (edge) {
                0 -> { x[slot] = t; y[slot] = -0.06f }
                1 -> { x[slot] = 1.06f; y[slot] = t * 0.9f }
                2 -> { x[slot] = t; y[slot] = 1.06f }
                else -> { x[slot] = -0.06f; y[slot] = t * 0.9f }
            }
            val dx = VIGIL_FIRE_X - x[slot]
            val dy = VIGIL_FIRE_Y - y[slot]
            val len = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
            vx[slot] = dx / len * batSpeed
            vy[slot] = dy / len * batSpeed
        } else {
            kind[slot] = VIGIL_KIND_EMBER
            val ang = rnd.nextFloat() * 6.2831855f
            x[slot] = VIGIL_FIRE_X + cos(ang) * 0.05f
            y[slot] = VIGIL_FIRE_Y + sin(ang) * 0.05f
            vx[slot] = cos(ang) * emberSpeed
            vy[slot] = (sin(ang) - 0.45f) * emberSpeed
        }
    }
}

@Composable
fun MinigameVigilia(request: MinigameRequest, onFinish: (MinigameResult) -> Unit) {
    val difficulty = request.difficulty.coerceIn(1, 5)
    val combo = rememberComboState()
    val gameFeel = rememberMinigameFeedback()

    var started by remember { mutableStateOf(false) }

    if (!started) {
        MinigameIntro(
            title = "VIGILIA DEL CAMPAMENTO",
            subtitle = if (request.title.isBlank()) "La noche quiere tu hoguera" else request.title,
            tone = EldoriaTone.Ember,
            crestSeed = 4606,
            lineOne = "Toca las brasas que escapan antes de que se pierdan y espanta a los murciélagos antes de que lleguen al fuego.",
            lineTwo = "La salud de la hoguera al amanecer decide cuánta vida y maná recuperas.",
            onStart = { started = true },
            onQuit = {
                onFinish(
                    MinigameResult(
                        id = "VIGILIA", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        )
        return
    }

    val sim = remember(request.contextJson, difficulty) {
        VigilRun(request.contextJson.hashCode() * 53 + difficulty * 307 + 8821)
    }

    var frame by remember { mutableStateOf(0) }
    var secondsLeft by remember { mutableStateOf(VIGIL_TOTAL_MS / 1000) }
    var health by remember { mutableStateOf(100) }
    var saved by remember { mutableStateOf(0) }
    var lost by remember { mutableStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var burst by remember { mutableStateOf(0) }
    var shakeTrigger by remember { mutableStateOf(0) }

    val arena = remember { FloatArray(2) }
    val shakeOffset = eldoriaShake(trigger = shakeTrigger, magnitude = 10.dp, cycles = 3, label = "vigilShake")

    val closeRun: () -> Unit = closeRun@{
        if (finished) return@closeRun
        finished = true
        val score = health.coerceIn(0, 100)
        val rating = when {
            score >= 90 -> "GUARDIA IMPECABLE"
            score >= 65 -> "VIGILIA FIRME"
            score >= 35 -> "NOCHE LARGA"
            score > 0 -> "FUEGO MORIBUNDO"
            else -> "HOGUERA APAGADA"
        }
        onFinish(
            MinigameResult(
                id = "VIGILIA",
                success = score > 0,
                score = score,
                perfect = saved,
                rating = rating,
                contextJson = request.contextJson
            )
        )
    }

    LaunchedEffect(Unit) {
        var lastNs = 0L
        var elapsedMs = 0L
        val batChance = 0.30f + difficulty * 0.07f
        val batSpeed = 0.15f + difficulty * 0.022f
        val emberSpeed = 0.16f + difficulty * 0.018f
        val spawnEvery = (0.95f - difficulty * 0.10f).coerceAtLeast(0.34f)

        while (elapsedMs < VIGIL_TOTAL_MS) {
            withFrameNanos { now ->
                if (lastNs == 0L) lastNs = now
                var dt = (now - lastNs) / 1_000_000_000f
                lastNs = now
                if (dt > 0.05f) dt = 0.05f
                elapsedMs += (dt * 1000f).toLong()
                sim.clock += dt

                val left = (((VIGIL_TOTAL_MS - elapsedMs) + 999L) / 1000L).toInt().coerceAtLeast(0)
                if (left != secondsLeft) secondsLeft = left

                sim.spawnT += dt
                if (sim.spawnT >= spawnEvery) {
                    sim.spawnT = 0f
                    sim.spawn(batChance, batSpeed, emberSpeed)
                }

                var damage = 0
                for (i in 0 until VIGIL_CAPACITY) {
                    if (!sim.live[i]) continue
                    sim.x[i] += sim.vx[i] * dt
                    sim.y[i] += sim.vy[i] * dt
                    if (sim.kind[i] == VIGIL_KIND_BAT) {
                        // aleteo lateral
                        sim.x[i] += sin(sim.clock * 6f + sim.phase[i]) * 0.045f * dt
                        val dx = sim.x[i] - VIGIL_FIRE_X
                        val dy = sim.y[i] - VIGIL_FIRE_Y
                        if (dx * dx + dy * dy < 0.0045f) {
                            sim.live[i] = false
                            damage += 9
                        }
                    } else {
                        val dx = sim.x[i] - VIGIL_FIRE_X
                        val dy = sim.y[i] - VIGIL_FIRE_Y
                        if (dx * dx + dy * dy > 0.22f ||
                            sim.x[i] < -0.1f || sim.x[i] > 1.1f || sim.y[i] < -0.1f || sim.y[i] > 1.1f
                        ) {
                            sim.live[i] = false
                            damage += 5
                        }
                    }
                }
                if (damage > 0) {
                    health = (health - damage).coerceAtLeast(0)
                    lost += 1
                    shakeTrigger += 1
                }

                frame += 1
            }
        }
        closeRun()
    }

    LaunchedEffect(health) {
        if (health <= 0 && !finished) closeRun()
    }
    // La racha se lleva por marcador, no por toque: en Vigilia una chispa
    // apagada cuenta como fallo aunque el jugador no llegase a tocarla.
    LaunchedEffect(lost) {
        if (lost > 0) {
            combo.miss()
            gameFeel.miss()
            SoundManager.playEnemyAttack()
        }
    }
    LaunchedEffect(saved) {
        if (saved > 0) {
            val before = combo.multiplier
            combo.hit()
            gameFeel.hit(combo.streak)
            if (combo.multiplier > before) gameFeel.step()
            SoundManager.playMagicSpell()
        }
    }

    MinigameShell(
        title = "VIGILIA DEL CAMPAMENTO",
        subtitle = "Amenazas repelidas $saved  ·  ${MinigameClockText(secondsLeft)}",
        tone = EldoriaTone.Ember,
        scoreLabel = "$health %",
        combo = combo,
        onQuit = {
            if (!finished) {
                finished = true
                onFinish(
                    MinigameResult(
                        id = "VIGILIA", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            EldoriaResourceBar(
                current = health,
                max = 100,
                tone = EldoriaBarTone.Torch,
                modifier = Modifier.fillMaxWidth(),
                label = "Salud de la hoguera",
                height = 18.dp,
                dangerPulse = true
            )

            Spacer(Modifier.height(Eldoria.S8))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EldoriaChip(text = "Repelidas $saved", color = Eldoria.Success, filled = saved > 0)
                EldoriaChip(text = "Fugas $lost", color = Eldoria.Danger)
            }

            Spacer(Modifier.height(Eldoria.S8))

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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged {
                            arena[0] = it.width.toFloat()
                            arena[1] = it.height.toFloat()
                        }
                        .pointerInput(Unit) {
                            detectTapGestures { pos ->
                                if (finished) return@detectTapGestures
                                val w = size.width.toFloat()
                                val h = size.height.toFloat()
                                if (w <= 1f || h <= 1f) return@detectTapGestures
                                val nx = pos.x / w
                                val ny = pos.y / h
                                val reach = 44.dp.toPx()
                                var best = -1
                                var bestD = Float.MAX_VALUE
                                for (i in 0 until VIGIL_CAPACITY) {
                                    if (!sim.live[i]) continue
                                    val dx = (sim.x[i] - nx) * w
                                    val dy = (sim.y[i] - ny) * h
                                    val d = dx * dx + dy * dy
                                    if (d < bestD) {
                                        bestD = d
                                        best = i
                                    }
                                }
                                if (best >= 0 && bestD <= reach * reach) {
                                    sim.hitX[0] = sim.x[best]
                                    sim.hitY[0] = sim.y[best]
                                    sim.live[best] = false
                                    saved += 1
                                    burst += 1
                                }
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (frame >= 0) vigilDrawCamp(sim, health)
                    }

                    EldoriaTorchLight(
                        modifier = Modifier.fillMaxSize(),
                        intensity = (0.55f + 0.45f * (health / 100f)).coerceIn(0.35f, 1f),
                        centerX = VIGIL_FIRE_X,
                        centerY = VIGIL_FIRE_Y
                    )

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .offset {
                                val w = arena[0]
                                val h = arena[1]
                                if (frame < 0 || w <= 1f || h <= 1f) return@offset IntOffset(0, 0)
                                val px = 120.dp.toPx()
                                IntOffset(
                                    (sim.hitX[0] * w - px * 0.5f).toInt(),
                                    (sim.hitY[0] * h - px * 0.5f).toInt()
                                )
                            }
                    ) {
                        EldoriaImpactBurst(
                            trigger = burst,
                            modifier = Modifier.fillMaxSize(),
                            color = Eldoria.EmberCore,
                            rays = 12,
                            durationMs = 360
                        )
                    }
                }
            }

            Spacer(Modifier.height(Eldoria.S8))
            Text(
                text = "Toca las brasas fugadas y los murciélagos: cada fuga apaga la hoguera",
                style = EldoriaType.caption,
                color = Eldoria.TextLow,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )
        }
    }
}

// ───────────────────────────── ayudas privadas ────────────────────────────────

/** Campamento completo: noche, hoguera viva, brasas fugadas y murciélagos. */
private fun DrawScope.vigilDrawCamp(sim: VigilRun, health: Int) {
    val w = size.width
    val h = size.height
    if (w <= 8f || h <= 8f) return
    val fx = w * VIGIL_FIRE_X
    val fy = h * VIGIL_FIRE_Y
    val vigor = (health / 100f).coerceIn(0f, 1f)
    val t = sim.clock

    // noche
    drawRect(
        brush = Brush.verticalGradient(listOf(Eldoria.Abyss, Eldoria.Ink, Eldoria.EmberShadow)),
        size = Size(w, h)
    )

    // suelo de tierra
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Color.Transparent, Eldoria.Ash.copy(alpha = 0.35f)),
            startY = h * 0.62f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.62f),
        size = Size(w, h * 0.38f)
    )

    // círculo de piedras
    val ringR = w * 0.135f
    var s = 0
    while (s < 9) {
        val a = s * 0.698f
        val sx = fx + cos(a) * ringR
        val sy = fy + sin(a) * ringR * 0.42f + h * 0.045f
        drawCircle(Eldoria.IronDeep, radius = w * 0.024f, center = Offset(sx, sy))
        drawCircle(
            Eldoria.Iron.copy(alpha = 0.85f),
            radius = w * 0.018f,
            center = Offset(sx - w * 0.004f, sy - w * 0.005f)
        )
        s++
    }

    // leños
    val logW = w * 0.20f
    drawLine(
        color = Eldoria.Ash,
        start = Offset(fx - logW, fy + h * 0.035f),
        end = Offset(fx + logW * 0.7f, fy + h * 0.010f),
        strokeWidth = w * 0.030f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Eldoria.EmberShadow,
        start = Offset(fx - logW * 0.7f, fy + h * 0.012f),
        end = Offset(fx + logW, fy + h * 0.036f),
        strokeWidth = w * 0.030f,
        cap = StrokeCap.Round
    )

    // llama: tres lenguas que ondulan con el reloj de la simulación
    val flameH = h * (0.10f + 0.14f * vigor)
    var k = 0
    while (k < 3) {
        val off = (k - 1) * w * 0.035f
        val wob = sin(t * (3.1f + k * 0.7f) + k) * w * 0.018f
        val fw = w * (0.055f - k * 0.010f) * (0.65f + 0.55f * vigor)
        val flame = Path()
        flame.moveTo(fx + off - fw, fy)
        flame.cubicTo(
            fx + off - fw * 0.9f, fy - flameH * 0.45f,
            fx + off + wob - fw * 0.5f, fy - flameH * 0.75f,
            fx + off + wob, fy - flameH * (0.85f + 0.15f * k)
        )
        flame.cubicTo(
            fx + off + wob + fw * 0.5f, fy - flameH * 0.75f,
            fx + off + fw * 0.9f, fy - flameH * 0.45f,
            fx + off + fw, fy
        )
        flame.close()
        val c0 = if (k == 0) Eldoria.EmberCore else if (k == 1) Eldoria.Ember else Eldoria.EmberDeep
        drawPath(
            flame,
            brush = Brush.verticalGradient(
                listOf(c0.copy(alpha = 0.95f), Eldoria.EmberDeep.copy(alpha = 0.35f)),
                startY = fy - flameH,
                endY = fy
            )
        )
        k++
    }
    drawCircle(
        brush = Brush.radialGradient(
            0f to Eldoria.EmberCore.copy(alpha = 0.55f * vigor),
            1f to Color.Transparent,
            center = Offset(fx, fy - flameH * 0.35f),
            radius = w * 0.28f * (0.5f + 0.5f * vigor)
        ),
        radius = w * 0.28f * (0.5f + 0.5f * vigor),
        center = Offset(fx, fy - flameH * 0.35f)
    )

    // amenazas
    for (i in 0 until VIGIL_CAPACITY) {
        if (!sim.live[i]) continue
        val ex = sim.x[i] * w
        val ey = sim.y[i] * h
        if (sim.kind[i] == VIGIL_KIND_EMBER) {
            val r = w * 0.016f
            drawCircle(
                brush = Brush.radialGradient(
                    0f to Eldoria.EmberCore.copy(alpha = 0.75f),
                    1f to Color.Transparent,
                    center = Offset(ex, ey),
                    radius = r * 3.4f
                ),
                radius = r * 3.4f,
                center = Offset(ex, ey)
            )
            drawCircle(Eldoria.Ember, radius = r, center = Offset(ex, ey))
            drawCircle(Color.White.copy(alpha = 0.85f), radius = r * 0.42f, center = Offset(ex, ey))
            // estela hacia la hoguera
            drawLine(
                color = Eldoria.EmberDeep.copy(alpha = 0.35f),
                start = Offset(ex, ey),
                end = Offset(ex - sim.vx[i] * w * 0.35f, ey - sim.vy[i] * h * 0.35f),
                strokeWidth = r * 0.8f,
                cap = StrokeCap.Round
            )
        } else {
            val bw = w * 0.052f
            val flap = abs(sin(t * 9f + sim.phase[i]))
            val wing = Path()
            wing.moveTo(ex, ey)
            wing.cubicTo(
                ex - bw * 0.5f, ey - bw * (0.30f + 0.45f * flap),
                ex - bw * 1.05f, ey - bw * 0.05f,
                ex - bw * 1.25f, ey + bw * 0.28f
            )
            wing.cubicTo(
                ex - bw * 0.75f, ey + bw * 0.12f,
                ex - bw * 0.35f, ey + bw * 0.22f,
                ex, ey + bw * 0.16f
            )
            wing.cubicTo(
                ex + bw * 0.35f, ey + bw * 0.22f,
                ex + bw * 0.75f, ey + bw * 0.12f,
                ex + bw * 1.25f, ey + bw * 0.28f
            )
            wing.cubicTo(
                ex + bw * 1.05f, ey - bw * 0.05f,
                ex + bw * 0.5f, ey - bw * (0.30f + 0.45f * flap),
                ex, ey
            )
            wing.close()
            drawPath(wing, Eldoria.Ink)
            drawPath(wing, Eldoria.ArcaneDeep.copy(alpha = 0.9f), style = Stroke(width = bw * 0.10f))
            drawCircle(Eldoria.BloodBright, radius = bw * 0.09f, center = Offset(ex - bw * 0.12f, ey + bw * 0.02f))
            drawCircle(Eldoria.BloodBright, radius = bw * 0.09f, center = Offset(ex + bw * 0.12f, ey + bw * 0.02f))
        }
    }
}
