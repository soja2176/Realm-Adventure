package com.example.ui.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.example.ui.design.EldoriaBeastSigil
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaImpactBurst
import com.example.ui.design.EldoriaResourceBar
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaPaletteOf
import com.example.ui.design.eldoriaShake
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

// ──────────────────────────────────────────────────────────────────────────────
//  ADIESTRAMIENTO DE BESTIAS
//  Corredor de tres carriles. Arrastra a los lados para cambiar de carril, hacia
//  arriba para saltar la valla y hacia abajo para agacharte bajo la rama.
// ──────────────────────────────────────────────────────────────────────────────

private const val TRAIN_TOTAL_MS = 40_000
private const val TRAIN_CAPACITY = 12
private const val TRAIN_LANES = 3
private const val TRAIN_LINE = 0.84f
private const val TRAIN_KIND_HURDLE = 0   // valla → saltar
private const val TRAIN_KIND_BRANCH = 1   // rama  → agacharse
private const val TRAIN_KIND_BOULDER = 2  // roca  → cambiar de carril
private const val TRAIN_ACT_NONE = 0
private const val TRAIN_ACT_JUMP = 1
private const val TRAIN_ACT_DUCK = 2

/** Simulación pura: arrays fijos, cero asignaciones por frame. */
private class TrainRun(seedValue: Int) {
    val rnd = Random(seedValue)
    val y = FloatArray(TRAIN_CAPACITY)
    val lane = IntArray(TRAIN_CAPACITY)
    val kind = IntArray(TRAIN_CAPACITY)
    val live = BooleanArray(TRAIN_CAPACITY)
    val done = BooleanArray(TRAIN_CAPACITY)

    var petLane = 1
    var petX = 1f
    var action = TRAIN_ACT_NONE
    var actionT = 0f
    var scroll = 0f
    var spawnT = 0f
    var lastLane = 1

    fun spawn(): Boolean {
        var slot = -1
        for (i in 0 until TRAIN_CAPACITY) {
            if (!live[i]) { slot = i; break }
        }
        if (slot < 0) return false
        var l = rnd.nextInt(TRAIN_LANES)
        if (l == lastLane && rnd.nextInt(3) == 0) l = (l + 1) % TRAIN_LANES
        lastLane = l
        live[slot] = true
        done[slot] = false
        y[slot] = -0.12f
        lane[slot] = l
        kind[slot] = rnd.nextInt(3)
        return true
    }
}

@Composable
fun MinigameAdiestramiento(request: MinigameRequest, onFinish: (MinigameResult) -> Unit) {
    val difficulty = request.difficulty.coerceIn(1, 5)
    val combo = rememberComboState()
    val gameFeel = rememberMinigameFeedback()

    var started by remember { mutableStateOf(false) }

    if (!started) {
        MinigameIntro(
            title = "ADIESTRAMIENTO DE BESTIAS",
            subtitle = if (request.title.isBlank()) "Cuarenta segundos de obediencia ciega" else request.title,
            tone = EldoriaTone.Vitae,
            crestSeed = 4505,
            lineOne = "Arrastra a izquierda o derecha para cambiar de carril, arriba para saltar y abajo para agacharte.",
            lineTwo = "Valla = salto · Rama = agacharse · Roca = esquivar cambiando de carril.",
            onStart = { started = true },
            onQuit = {
                onFinish(
                    MinigameResult(
                        id = "ADIESTRAMIENTO", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        )
        return
    }

    val sim = remember(request.contextJson, difficulty) {
        TrainRun(request.contextJson.hashCode() * 37 + difficulty * 211 + 6607)
    }
    val palette = remember(request.contextJson) {
        eldoriaPaletteOf(
            when (abs(request.contextJson.hashCode()) % 5) {
                0 -> "EMBER"
                1 -> "ARCANE"
                2 -> "VITAE"
                3 -> "MANA"
                else -> "GOLD"
            }
        )
    }
    val beastSeed = remember(request.contextJson) { 700 + abs(request.contextJson.hashCode() % 5000) }

    var frame by remember { mutableStateOf(0) }
    var secondsLeft by remember { mutableStateOf(TRAIN_TOTAL_MS / 1000) }
    var cleared by remember { mutableStateOf(0) }
    var faults by remember { mutableStateOf(0) }
    var streak by remember { mutableStateOf(0) }
    var bestStreak by remember { mutableStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var burst by remember { mutableStateOf(0) }
    var shakeTrigger by remember { mutableStateOf(0) }
    var lastCall by remember { mutableStateOf(0) } // 1 acierto, -1 fallo

    val arena = remember { FloatArray(2) }
    val shakeOffset = eldoriaShake(trigger = shakeTrigger, magnitude = 12.dp, cycles = 4, label = "trainShake")

    val closeRun: () -> Unit = closeRun@{
        if (finished) return@closeRun
        finished = true
        val total = cleared + faults
        val score = if (total <= 0) 0 else (cleared * 100 / total).coerceIn(0, 100)
        val rating = when {
            score >= 90 -> "BESTIA ALFA"
            score >= 70 -> "BESTIA DISCIPLINADA"
            score >= 40 -> "BESTIA INQUIETA"
            else -> "BESTIA INDÓMITA"
        }
        onFinish(
            MinigameResult(
                id = "ADIESTRAMIENTO",
                success = score >= 40,
                score = score,
                perfect = bestStreak,
                rating = rating,
                contextJson = request.contextJson
            )
        )
    }

    // Reloj y simulación en un único bucle de frames.
    LaunchedEffect(Unit) {
        var lastNs = 0L
        var elapsedMs = 0L
        val speed = 0.42f + difficulty * 0.055f
        val spawnEvery = (1.45f - difficulty * 0.13f).coerceAtLeast(0.62f)
        while (elapsedMs < TRAIN_TOTAL_MS) {
            withFrameNanos { now ->
                if (lastNs == 0L) lastNs = now
                var dt = (now - lastNs) / 1_000_000_000f
                lastNs = now
                if (dt > 0.05f) dt = 0.05f
                elapsedMs += (dt * 1000f).toLong()

                val left = (((TRAIN_TOTAL_MS - elapsedMs) + 999L) / 1000L).toInt().coerceAtLeast(0)
                if (left != secondsLeft) secondsLeft = left

                sim.scroll = (sim.scroll + dt * speed) % 1f

                // desplazamiento lateral suavizado
                val targetX = sim.petLane.toFloat()
                val dx = targetX - sim.petX
                val step = dt * 7.5f
                sim.petX = if (abs(dx) <= step) targetX else sim.petX + if (dx > 0f) step else -step

                if (sim.actionT > 0f) {
                    sim.actionT -= dt
                    if (sim.actionT <= 0f) {
                        sim.actionT = 0f
                        sim.action = TRAIN_ACT_NONE
                    }
                }

                sim.spawnT += dt
                if (sim.spawnT >= spawnEvery) {
                    sim.spawnT = 0f
                    sim.spawn()
                }

                for (i in 0 until TRAIN_CAPACITY) {
                    if (!sim.live[i]) continue
                    sim.y[i] += dt * speed
                    if (!sim.done[i] && sim.y[i] >= TRAIN_LINE) {
                        sim.done[i] = true
                        val sameLane = sim.lane[i] == sim.petLane && abs(sim.petX - sim.petLane) < 0.34f
                        val ok = when {
                            !sameLane -> true
                            sim.kind[i] == TRAIN_KIND_HURDLE -> sim.action == TRAIN_ACT_JUMP
                            sim.kind[i] == TRAIN_KIND_BRANCH -> sim.action == TRAIN_ACT_DUCK
                            else -> false
                        }
                        if (ok) {
                            cleared += 1
                            streak += 1
                            if (streak > bestStreak) bestStreak = streak
                            lastCall = 1
                            burst += 1
                        } else {
                            faults += 1
                            streak = 0
                            lastCall = -1
                            shakeTrigger += 1
                        }
                    }
                    if (sim.y[i] > 1.18f) sim.live[i] = false
                }

                frame += 1
            }
        }
        closeRun()
    }

    // Sonidos fuera del bucle de dibujo, disparados por el marcador.
    LaunchedEffect(cleared) {
        if (cleared > 0) {
            val before = combo.multiplier
            combo.hit()
            gameFeel.hit(combo.streak)
            if (combo.multiplier > before) gameFeel.step()
            SoundManager.playButtonClick()
        }
    }
    LaunchedEffect(faults) {
        if (faults > 0) {
            combo.miss()
            gameFeel.miss()
            SoundManager.playEnemyAttack()
        }
    }

    val act: (Int) -> Unit = act@{ code ->
        if (finished) return@act
        when (code) {
            0 -> if (sim.petLane > 0) sim.petLane -= 1
            1 -> if (sim.petLane < TRAIN_LANES - 1) sim.petLane += 1
            2 -> {
                sim.action = TRAIN_ACT_JUMP
                sim.actionT = 0.62f
                SoundManager.playSwordSlash()
            }
            else -> {
                sim.action = TRAIN_ACT_DUCK
                sim.actionT = 0.55f
            }
        }
    }

    MinigameShell(
        title = "ADIESTRAMIENTO DE BESTIAS",
        subtitle = "Racha $streak  ·  ${MinigameClockText(secondsLeft)}",
        tone = EldoriaTone.Vitae,
        scoreLabel = "$cleared/${cleared + faults}",
        combo = combo,
        onQuit = {
            if (!finished) {
                finished = true
                onFinish(
                    MinigameResult(
                        id = "ADIESTRAMIENTO", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EldoriaChip(
                    text = when (lastCall) {
                        1 -> "¡OBEDECE!"
                        -1 -> "TROPIEZO"
                        else -> "A LA CARRERA"
                    },
                    color = when (lastCall) {
                        1 -> Eldoria.Success
                        -1 -> Eldoria.Danger
                        else -> Eldoria.TextLow
                    },
                    filled = lastCall != 0
                )
                EldoriaChip(text = "Mejor racha $bestStreak", color = Eldoria.TextGold)
            }

            Spacer(Modifier.height(Eldoria.S8))

            EldoriaFrame(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(x = shakeOffset),
                edge = EldoriaEdge.Vitae,
                strokeWidth = Eldoria.StrokeBold,
                filigree = false,
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
                            var accX = 0f
                            var accY = 0f
                            var handled = false
                            detectDragGestures(
                                onDragStart = {
                                    accX = 0f
                                    accY = 0f
                                    handled = false
                                },
                                onDragEnd = { handled = false },
                                onDragCancel = { handled = false },
                                onDrag = { change, delta ->
                                    change.consume()
                                    if (!handled) {
                                        accX += delta.x
                                        accY += delta.y
                                        val threshold = 36.dp.toPx()
                                        if (abs(accX) > abs(accY) && abs(accX) > threshold) {
                                            handled = true
                                            act(if (accX > 0f) 1 else 0)
                                        } else if (abs(accY) > threshold) {
                                            handled = true
                                            act(if (accY > 0f) 3 else 2)
                                        }
                                    }
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // la lectura de `frame` ata el repintado al reloj del juego
                        if (frame >= 0) trainDrawCorridor(sim)
                    }

                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .offset {
                                val w = arena[0]
                                val h = arena[1]
                                if (frame < 0 || w <= 1f || h <= 1f) return@offset IntOffset(0, 0)
                                val petPx = 86.dp.toPx()
                                val laneW = w / TRAIN_LANES
                                val cx = laneW * (sim.petX + 0.5f)
                                val jump = if (sim.action == TRAIN_ACT_JUMP && sim.actionT > 0f) {
                                    sin(((0.62f - sim.actionT) / 0.62f).coerceIn(0f, 1f) * 3.14159f)
                                } else 0f
                                val duck = if (sim.action == TRAIN_ACT_DUCK && sim.actionT > 0f) 1f else 0f
                                val cy = h * (TRAIN_LINE + 0.02f) - petPx * 0.5f -
                                    jump * h * 0.20f + duck * h * 0.045f
                                IntOffset((cx - petPx * 0.5f).toInt(), cy.toInt())
                            }
                    ) {
                        EldoriaBeastSigil(
                            seed = beastSeed,
                            modifier = Modifier.fillMaxSize(),
                            primary = palette.first,
                            secondary = palette.second,
                            stage = 2,
                            animated = false
                        )
                    }

                    EldoriaImpactBurst(
                        trigger = burst,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .size(150.dp),
                        color = Eldoria.VitaeBright,
                        rays = 10,
                        durationMs = 320
                    )
                }
            }

            Spacer(Modifier.height(Eldoria.S8))

            EldoriaResourceBar(
                current = cleared,
                max = (cleared + faults).coerceAtLeast(1),
                tone = EldoriaBarTone.Bond,
                modifier = Modifier.fillMaxWidth(),
                label = "Obediencia de la sesión",
                height = 14.dp
            )

            Spacer(Modifier.height(Eldoria.S8))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S12, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EldoriaIconButton(
                    icon = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Carril izquierdo",
                    onClick = { act(0) },
                    tone = EldoriaTone.Iron,
                    size = 46.dp,
                    testTag = "train_left"
                )
                EldoriaIconButton(
                    icon = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Saltar",
                    onClick = { act(2) },
                    tone = EldoriaTone.Vitae,
                    size = 52.dp,
                    testTag = "train_jump"
                )
                EldoriaIconButton(
                    icon = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Agacharse",
                    onClick = { act(3) },
                    tone = EldoriaTone.Vitae,
                    size = 52.dp,
                    testTag = "train_duck"
                )
                EldoriaIconButton(
                    icon = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Carril derecho",
                    onClick = { act(1) },
                    tone = EldoriaTone.Iron,
                    size = 46.dp,
                    testTag = "train_right"
                )
            }

            Spacer(Modifier.height(Eldoria.S6))
            Text(
                text = "Arrastra sobre el corredor o usa los mandos",
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

/** Corredor completo: suelo en fuga, carriles, línea de acción y obstáculos. */
private fun DrawScope.trainDrawCorridor(sim: TrainRun) {
    val w = size.width
    val h = size.height
    if (w <= 8f || h <= 8f) return
    val laneW = w / TRAIN_LANES

    // suelo
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Eldoria.Ink, Eldoria.Slate, Eldoria.PanelSunken),
            startY = 0f,
            endY = h
        ),
        size = Size(w, h)
    )

    // travesaños en movimiento
    var k = 0
    while (k < 9) {
        val fy = ((k / 9f) + sim.scroll) % 1f
        val y = h * fy
        drawLine(
            color = Eldoria.IronEdge.copy(alpha = 0.10f + 0.22f * fy),
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 1f + 2.5f * fy
        )
        k++
    }

    // separadores de carril
    var l = 1
    while (l < TRAIN_LANES) {
        val x = laneW * l
        drawLine(
            color = Eldoria.Iron.copy(alpha = 0.55f),
            start = Offset(x, 0f),
            end = Offset(x, h),
            strokeWidth = 1.5f
        )
        l++
    }

    // línea de acción
    val lineY = h * TRAIN_LINE
    drawLine(
        brush = Brush.horizontalGradient(
            listOf(Color.Transparent, Eldoria.VitaeBright.copy(alpha = 0.75f), Color.Transparent)
        ),
        start = Offset(0f, lineY),
        end = Offset(w, lineY),
        strokeWidth = 3f
    )

    // obstáculos
    for (i in 0 until TRAIN_CAPACITY) {
        if (!sim.live[i]) continue
        val fy = sim.y[i]
        if (fy < -0.15f || fy > 1.2f) continue
        val cx = laneW * (sim.lane[i] + 0.5f)
        val cy = h * fy
        val scale = 0.55f + 0.60f * fy.coerceIn(0f, 1f)
        val ow = laneW * 0.62f * scale
        val oh = h * 0.075f * scale
        when (sim.kind[i]) {
            TRAIN_KIND_HURDLE -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Eldoria.EmberCore, Eldoria.Ember, Eldoria.EmberDeep),
                        startY = cy - oh * 0.5f,
                        endY = cy + oh * 0.5f
                    ),
                    topLeft = Offset(cx - ow * 0.5f, cy - oh * 0.5f),
                    size = Size(ow, oh)
                )
                drawRect(
                    color = Eldoria.EmberShadow,
                    topLeft = Offset(cx - ow * 0.42f, cy),
                    size = Size(ow * 0.09f, oh * 1.5f)
                )
                drawRect(
                    color = Eldoria.EmberShadow,
                    topLeft = Offset(cx + ow * 0.33f, cy),
                    size = Size(ow * 0.09f, oh * 1.5f)
                )
            }
            TRAIN_KIND_BRANCH -> {
                val p = Path()
                p.moveTo(cx - ow * 0.55f, cy - oh * 0.35f)
                p.cubicTo(
                    cx - ow * 0.2f, cy + oh * 0.55f,
                    cx + ow * 0.2f, cy - oh * 0.75f,
                    cx + ow * 0.55f, cy + oh * 0.15f
                )
                drawPath(
                    p,
                    color = Eldoria.Vitae,
                    style = Stroke(width = oh * 0.55f, cap = StrokeCap.Round)
                )
                drawPath(
                    p,
                    color = Eldoria.VitaeDeep,
                    style = Stroke(width = oh * 0.22f, cap = StrokeCap.Round)
                )
            }
            else -> {
                val r = ow * 0.34f
                drawCircle(
                    brush = Brush.radialGradient(
                        0f to Eldoria.IronEdge,
                        1f to Eldoria.Abyss,
                        center = Offset(cx - r * 0.25f, cy - r * 0.25f),
                        radius = r * 1.5f
                    ),
                    radius = r,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color.Black.copy(alpha = 0.55f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = r * 0.16f)
                )
            }
        }
    }

    // sombra de la mascota
    val shadowX = laneW * (sim.petX + 0.5f)
    drawCircle(
        color = Color.Black.copy(alpha = 0.45f),
        radius = laneW * 0.22f,
        center = Offset(shadowX, h * (TRAIN_LINE + 0.055f))
    )
}
