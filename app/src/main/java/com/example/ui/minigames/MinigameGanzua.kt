package com.example.ui.minigames

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaDiamondPath
import com.example.ui.design.eldoriaShake
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// ──────────────────────────────────────────────────────────────────────────────
//  GANZÚA DEL LADRÓN
//  Un pin gira sin descanso dentro del bombín. Fija cada pasador tocando cuando
//  el pin entra en su muesca. Tres ganzúas: cada fallo parte una.
// ──────────────────────────────────────────────────────────────────────────────

private const val LOCK_PICKS = 3
private const val LOCK_TOTAL_MS = 25_000

@Composable
fun MinigameGanzua(request: MinigameRequest, onFinish: (MinigameResult) -> Unit) {
    val difficulty = request.difficulty.coerceIn(1, 5)
    val combo = rememberComboState()
    val gameFeel = rememberMinigameFeedback()

    var started by remember { mutableStateOf(false) }

    if (!started) {
        MinigameIntro(
            title = "GANZÚA DEL LADRÓN",
            subtitle = if (request.title.isBlank()) "Un bombín antiguo, tres ganzúas frágiles" else request.title,
            tone = EldoriaTone.Silver,
            crestSeed = 4202,
            lineOne = "Toca cuando el pin giratorio entre en la muesca dorada para fijar ese pasador.",
            lineTwo = "Cada fallo parte una ganzúa. Sin ganzúas, el cofre se queda cerrado.",
            onStart = { started = true },
            onQuit = {
                onFinish(
                    MinigameResult(
                        id = "GANZUA", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        )
        return
    }

    val pinCount = (3 + difficulty / 2).coerceIn(3, 5)
    val targets = remember(request.contextJson, difficulty) {
        val rnd = Random(request.contextJson.hashCode() * 17 + difficulty * 13 + 907)
        FloatArray(pinCount) { i -> (i * 360f / pinCount) + rnd.nextFloat() * (300f / pinCount) }
    }

    var solved by remember { mutableStateOf(0) }
    var picks by remember { mutableStateOf(LOCK_PICKS) }
    var clean by remember { mutableStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var burst by remember { mutableStateOf(0) }
    var shakeTrigger by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf(0) } // 1 fija, -1 rota, 0 neutro

    val spinMs = (2400 - difficulty * 230).coerceAtLeast(820)
    val transition = rememberInfiniteTransition(label = "lockSpin")
    val angleState = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = spinMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lockAngle"
    )
    val glowState = transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lockGlow"
    )

    val shakeOffset = eldoriaShake(trigger = shakeTrigger, magnitude = 10.dp, cycles = 4, label = "lockShake")

    val closeRun: (Boolean) -> Unit = closeRun@{ won ->
        if (finished) return@closeRun
        finished = true
        val base = solved * 80 / pinCount
        val bonus = if (won) picks * 20 / LOCK_PICKS else 0
        val finalScore = (base + bonus).coerceIn(0, 100)
        val rating = when {
            won && picks == LOCK_PICKS -> "APERTURA IMPECABLE"
            won && picks == 2 -> "APERTURA LIMPIA"
            won -> "APERTURA A LA FUERZA"
            solved > 0 -> "CERRADURA RESISTENTE"
            else -> "MANOS TORPES"
        }
        onFinish(
            MinigameResult(
                id = "GANZUA",
                success = won,
                score = finalScore,
                perfect = clean,
                rating = rating,
                contextJson = request.contextJson
            )
        )
    }

    val secondsLeft = MinigameCountdown(
        active = !finished,
        totalMs = LOCK_TOTAL_MS,
        onExpire = { closeRun(false) }
    )

    LaunchedEffect(solved, picks, finished) {
        if (finished) return@LaunchedEffect
        if (solved >= pinCount) closeRun(true)
        else if (picks <= 0) closeRun(false)
    }

    val tryPin: () -> Unit = tryPin@{
        if (finished) return@tryPin
        val idx = solved
        if (idx >= pinCount) return@tryPin
        val tol = lockTolerance(idx, difficulty)
        val diff = lockAngleDiff(angleState.value, targets[idx])
        if (diff <= tol) {
            solved = idx + 1
            if (diff <= tol * 0.38f) clean += 1
            feedback = 1
            burst += 1
            val before = combo.multiplier
            combo.hit()
            gameFeel.hit(combo.streak)
            if (combo.multiplier > before) gameFeel.step()
            SoundManager.playHealPotion()
        } else {
            picks -= 1
            feedback = -1
            shakeTrigger += 1
            combo.miss()
            gameFeel.miss()
            SoundManager.playEnemyAttack()
        }
    }

    MinigameShell(
        title = "GANZÚA DEL LADRÓN",
        subtitle = "Pasador ${(solved + 1).coerceAtMost(pinCount)}/$pinCount  ·  ${MinigameClockText(secondsLeft)}",
        tone = EldoriaTone.Silver,
        scoreLabel = "$solved/$pinCount",
        combo = combo,
        onQuit = {
            if (!finished) {
                finished = true
                onFinish(
                    MinigameResult(
                        id = "GANZUA", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures { tryPin() } },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EldoriaFrame(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .offset(x = shakeOffset),
                edge = EldoriaEdge.Silver,
                strokeWidth = Eldoria.StrokeBold,
                filigree = true,
                rivets = true
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.92f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        lockDrawDial(
                            angle = angleState.value,
                            glow = glowState.value,
                            targets = targets,
                            solved = solved,
                            tolerance = lockTolerance(solved.coerceAtMost(pinCount - 1), difficulty)
                        )
                    }
                    EldoriaImpactBurst(
                        trigger = burst,
                        modifier = Modifier.fillMaxSize(),
                        color = Eldoria.Silver,
                        rays = 10,
                        durationMs = 380
                    )
                }
                Text(
                    text = when {
                        feedback > 0 -> "PASADOR FIJADO"
                        feedback < 0 -> "¡GANZÚA PARTIDA!"
                        else -> "ESCUCHA EL MECANISMO"
                    },
                    style = EldoriaType.heading,
                    color = when {
                        feedback > 0 -> Eldoria.Success
                        feedback < 0 -> Eldoria.Danger
                        else -> Eldoria.TextLow
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                )
            }

            Spacer(Modifier.height(Eldoria.S12))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S12, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until LOCK_PICKS) {
                    val alive = i < picks
                    Canvas(
                        modifier = Modifier
                            .width(20.dp)
                            .height(46.dp)
                    ) {
                        lockDrawPick(alive)
                    }
                }
                Spacer(Modifier.width(Eldoria.S4))
                Text(
                    text = if (picks > 0) "$picks ganzúas" else "sin ganzúas",
                    style = EldoriaType.label,
                    color = if (picks > 1) Eldoria.TextMid else Eldoria.Danger
                )
            }

            Spacer(Modifier.height(Eldoria.S8))
            Text(
                text = "Toca en cualquier parte cuando el pin entre en la muesca",
                style = EldoriaType.caption,
                color = Eldoria.TextLow,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Eldoria.S8))
        }
    }
}

// ───────────────────────────── ayudas privadas ────────────────────────────────

private fun lockTolerance(index: Int, difficulty: Int): Float =
    (27f - difficulty * 2.3f - index * 2.4f).coerceAtLeast(7.5f)

private fun lockAngleDiff(a: Float, b: Float): Float {
    var d = abs(a - b) % 360f
    if (d > 180f) d = 360f - d
    return d
}

/** Bombín completo: anillo metálico, muescas de pasador, pin giratorio y ojo de cerradura. */
private fun DrawScope.lockDrawDial(
    angle: Float,
    glow: Float,
    targets: FloatArray,
    solved: Int,
    tolerance: Float
) {
    val w = size.width
    val h = size.height
    if (w <= 8f || h <= 8f) return
    val cx = w * 0.5f
    val cy = h * 0.5f
    val r = min(w, h) * 0.44f
    val ringW = r * 0.16f

    // fondo hundido
    drawCircle(
        brush = Brush.radialGradient(
            0f to Eldoria.PanelSunken,
            1f to Eldoria.Abyss,
            center = Offset(cx, cy),
            radius = r
        ),
        radius = r,
        center = Offset(cx, cy)
    )

    // anillo exterior
    drawCircle(
        brush = Brush.verticalGradient(
            listOf(Eldoria.Silver, Eldoria.SilverDeep, Eldoria.IronDeep),
            startY = cy - r,
            endY = cy + r
        ),
        radius = r,
        center = Offset(cx, cy),
        style = Stroke(width = ringW)
    )
    drawCircle(
        color = Color.Black.copy(alpha = 0.55f),
        radius = r - ringW * 0.5f,
        center = Offset(cx, cy),
        style = Stroke(width = 2f)
    )

    // muescas del dial
    var k = 0
    while (k < 48) {
        val a = k * (360f / 48f) * (PI.toFloat() / 180f)
        val long = k % 4 == 0
        val r0 = r - ringW * (if (long) 1.15f else 0.85f)
        val r1 = r - ringW * 0.55f
        drawLine(
            color = Eldoria.IronEdge.copy(alpha = if (long) 0.85f else 0.45f),
            start = Offset(cx + cos(a) * r0, cy + sin(a) * r0),
            end = Offset(cx + cos(a) * r1, cy + sin(a) * r1),
            strokeWidth = if (long) 2f else 1f
        )
        k++
    }

    // pasadores pendientes y fijados
    for (i in targets.indices) {
        val t = targets[i]
        val rad = t * (PI.toFloat() / 180f)
        val px = cx + cos(rad) * (r - ringW * 1.9f)
        val py = cy + sin(rad) * (r - ringW * 1.9f)
        when {
            i < solved -> {
                drawCircle(Eldoria.Success.copy(alpha = 0.30f), radius = ringW * 0.95f, center = Offset(px, py))
                drawPath(eldoriaDiamondPath(px, py, ringW * 0.55f), Eldoria.Success)
            }
            i == solved -> {
                // muesca activa: arco dorado con la tolerancia real
                val sweep = tolerance * 2f
                drawArc(
                    color = Eldoria.Gold.copy(alpha = 0.22f + 0.35f * glow),
                    startAngle = t - tolerance,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(cx - r + ringW * 1.4f, cy - r + ringW * 1.4f),
                    size = Size((r - ringW * 1.4f) * 2f, (r - ringW * 1.4f) * 2f),
                    style = Stroke(width = ringW * 1.5f, cap = StrokeCap.Butt)
                )
                drawArc(
                    color = Eldoria.GoldBright.copy(alpha = 0.55f + 0.45f * glow),
                    startAngle = t - tolerance * 0.38f,
                    sweepAngle = tolerance * 0.76f,
                    useCenter = false,
                    topLeft = Offset(cx - r + ringW * 1.4f, cy - r + ringW * 1.4f),
                    size = Size((r - ringW * 1.4f) * 2f, (r - ringW * 1.4f) * 2f),
                    style = Stroke(width = ringW * 1.5f, cap = StrokeCap.Butt)
                )
                drawPath(eldoriaDiamondPath(px, py, ringW * 0.45f), Eldoria.GoldBright)
            }
            else -> {
                drawCircle(
                    color = Eldoria.IronEdge.copy(alpha = 0.55f),
                    radius = ringW * 0.40f,
                    center = Offset(px, py)
                )
            }
        }
    }

    // pin giratorio
    val rad = angle * (PI.toFloat() / 180f)
    val tipX = cx + cos(rad) * (r - ringW * 1.1f)
    val tipY = cy + sin(rad) * (r - ringW * 1.1f)
    drawLine(
        color = Eldoria.EmberCore.copy(alpha = 0.22f),
        start = Offset(cx, cy),
        end = Offset(tipX, tipY),
        strokeWidth = ringW * 0.85f,
        cap = StrokeCap.Round
    )
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Eldoria.Silver, Eldoria.GoldBright),
            start = Offset(cx, cy),
            end = Offset(tipX, tipY)
        ),
        start = Offset(cx, cy),
        end = Offset(tipX, tipY),
        strokeWidth = ringW * 0.32f,
        cap = StrokeCap.Round
    )
    drawCircle(Eldoria.GoldBright, radius = ringW * 0.34f, center = Offset(tipX, tipY))
    drawCircle(Color.White.copy(alpha = 0.7f), radius = ringW * 0.14f, center = Offset(tipX, tipY))

    // ojo de cerradura central
    drawCircle(Eldoria.Abyss, radius = r * 0.20f, center = Offset(cx, cy))
    drawCircle(
        color = Eldoria.SilverDeep,
        radius = r * 0.20f,
        center = Offset(cx, cy),
        style = Stroke(width = r * 0.035f)
    )
    val keyhole = Path()
    keyhole.moveTo(cx - r * 0.055f, cy + r * 0.02f)
    keyhole.lineTo(cx + r * 0.055f, cy + r * 0.02f)
    keyhole.lineTo(cx + r * 0.028f, cy + r * 0.16f)
    keyhole.lineTo(cx - r * 0.028f, cy + r * 0.16f)
    keyhole.close()
    drawPath(keyhole, Eldoria.Ink)
    drawCircle(Eldoria.Ink, radius = r * 0.062f, center = Offset(cx, cy - r * 0.02f))
}

/** Ganzúa individual: entera o partida por la mitad. */
private fun DrawScope.lockDrawPick(alive: Boolean) {
    val w = size.width
    val h = size.height
    if (w <= 2f || h <= 2f) return
    val cx = w * 0.5f
    val color = if (alive) Eldoria.Silver else Eldoria.IronDeep

    val body = Path()
    body.moveTo(cx - w * 0.14f, h * 0.98f)
    body.lineTo(cx + w * 0.14f, h * 0.98f)
    body.lineTo(cx + w * 0.10f, h * 0.30f)
    body.lineTo(cx + w * 0.34f, h * 0.06f)
    body.lineTo(cx + w * 0.06f, h * 0.16f)
    body.lineTo(cx - w * 0.10f, h * 0.30f)
    body.close()
    drawPath(body, color)
    drawPath(body, Color.Black.copy(alpha = 0.5f), style = Stroke(width = 1f))

    if (!alive) {
        drawLine(
            color = Eldoria.Danger,
            start = Offset(cx - w * 0.45f, h * 0.62f),
            end = Offset(cx + w * 0.45f, h * 0.44f),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )
    } else {
        drawCircle(Eldoria.GoldBright.copy(alpha = 0.8f), radius = w * 0.10f, center = Offset(cx, h * 0.88f))
    }
}
