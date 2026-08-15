package com.example.ui.minigames

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.audio.SoundManager
import com.example.data.model.MinigameRequest
import com.example.data.model.MinigameResult
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaCrackedStone
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaImpactBurst
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaResourceBar
import com.example.ui.design.EldoriaBarTone
import com.example.ui.design.EldoriaRuneGlyph
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaDiamondPath
import com.example.ui.design.eldoriaGlowLayer
import com.example.ui.design.eldoriaShake
import kotlin.random.Random

// ──────────────────────────────────────────────────────────────────────────────
//  EXCAVACIÓN DE LA CRIPTA
//  Rejilla 5×5, ocho picadas. Bajo la piedra hay vetas, trampas de aguja y roca
//  muerta: la roca muerta revela cuántas vetas la rodean (buscaminas invertido).
// ──────────────────────────────────────────────────────────────────────────────

private const val DIG_SIDE = 5
private const val DIG_CELLS = DIG_SIDE * DIG_SIDE
private const val DIG_PICKS = 8
private const val DIG_TOTAL_MS = 45_000
private const val DIG_EMPTY = 0
private const val DIG_VEIN = 1
private const val DIG_TRAP = 2

@Composable
fun MinigameExcavacion(request: MinigameRequest, onFinish: (MinigameResult) -> Unit) {
    val difficulty = request.difficulty.coerceIn(1, 5)
    var started by remember { mutableStateOf(false) }

    if (!started) {
        MinigameIntro(
            title = "EXCAVACIÓN DE LA CRIPTA",
            subtitle = if (request.title.isBlank()) "Ocho picadas contra la roca sellada" else request.title,
            tone = EldoriaTone.Iron,
            crestSeed = 4404,
            lineOne = "Pica en las losas para sacar vetas de material; las trampas de aguja te cuestan 2 picadas.",
            lineTwo = "La roca muerta marca cuántas vetas la rodean: usa ese número para decidir la siguiente picada.",
            onStart = { started = true },
            onQuit = {
                onFinish(
                    MinigameResult(
                        id = "EXCAVACION", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        )
        return
    }

    val board = remember(request.contextJson, difficulty) {
        digBuildBoard(
            seed = request.contextJson.hashCode() * 29 + difficulty * 101 + 3313,
            veins = (7 - difficulty / 2).coerceIn(5, 7),
            traps = (2 + difficulty / 2).coerceIn(2, 5)
        )
    }
    val neighbours = remember(board) { digNeighbourCounts(board) }
    val totalVeins = remember(board) { board.count { it == DIG_VEIN }.coerceAtLeast(1) }

    var revealed by remember { mutableStateOf(0) }
    var picks by remember { mutableStateOf(DIG_PICKS) }
    var found by remember { mutableStateOf(0) }
    var trapped by remember { mutableStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var burst by remember { mutableStateOf(0) }
    var shakeTrigger by remember { mutableStateOf(0) }
    var lastEvent by remember { mutableStateOf(0) } // 1 veta, -1 trampa, 0 roca

    val shakeOffset = eldoriaShake(trigger = shakeTrigger, magnitude = 11.dp, cycles = 4, label = "digShake")

    val closeRun: () -> Unit = closeRun@{
        if (finished) return@closeRun
        finished = true
        val score = (found * 100 / totalVeins).coerceIn(0, 100)
        val rating = when {
            found >= totalVeins -> "VETA MADRE"
            found * 3 >= totalVeins * 2 -> "EXCAVACIÓN PRÓSPERA"
            found >= 1 -> "HALLAZGO MODESTO"
            else -> "TIERRA ESTÉRIL"
        }
        onFinish(
            MinigameResult(
                id = "EXCAVACION",
                success = found >= 1,
                score = score,
                perfect = if (trapped == 0) found else 0,
                rating = rating,
                contextJson = request.contextJson
            )
        )
    }

    val secondsLeft = MinigameCountdown(active = !finished, totalMs = DIG_TOTAL_MS, onExpire = closeRun)

    LaunchedEffect(picks, found, finished) {
        if (finished) return@LaunchedEffect
        if (found >= totalVeins || picks <= 0) closeRun()
    }

    val onDig: (Int) -> Unit = onDig@{ index ->
        if (finished || picks <= 0) return@onDig
        if (revealed and (1 shl index) != 0) return@onDig
        revealed = revealed or (1 shl index)
        when (board[index]) {
            DIG_VEIN -> {
                found += 1
                picks -= 1
                lastEvent = 1
                burst += 1
                SoundManager.playCriticalHit()
            }
            DIG_TRAP -> {
                trapped += 1
                picks = (picks - 3).coerceAtLeast(0)
                lastEvent = -1
                shakeTrigger += 1
                SoundManager.playEnemyAttack()
            }
            else -> {
                picks -= 1
                lastEvent = 0
                SoundManager.playButtonClick()
            }
        }
    }

    MinigameShell(
        title = "EXCAVACIÓN DE LA CRIPTA",
        subtitle = "Vetas $found/$totalVeins  ·  ${MinigameClockText(secondsLeft)}",
        tone = EldoriaTone.Iron,
        scoreLabel = "$picks ⛏",
        onQuit = {
            if (!finished) {
                finished = true
                onFinish(
                    MinigameResult(
                        id = "EXCAVACION", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EldoriaResourceBar(
                current = picks,
                max = DIG_PICKS,
                tone = EldoriaBarTone.Torch,
                modifier = Modifier.fillMaxWidth(),
                label = "Picadas restantes",
                height = 16.dp,
                dangerPulse = true
            )

            Spacer(Modifier.height(Eldoria.S8))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EldoriaChip(
                    text = when (lastEvent) {
                        1 -> "¡VETA A LA VISTA!"
                        -1 -> "¡TRAMPA DE AGUJA!"
                        else -> "ROCA MUERTA"
                    },
                    color = when (lastEvent) {
                        1 -> Eldoria.Success
                        -1 -> Eldoria.Danger
                        else -> Eldoria.TextLow
                    },
                    filled = lastEvent != 0
                )
                EldoriaChip(text = "Trampas $trapped", color = Eldoria.Danger)
            }

            Spacer(Modifier.height(Eldoria.S12))

            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(x = shakeOffset),
                edge = EldoriaEdge.Iron,
                padding = PaddingValues(8.dp),
                filigree = true
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EldoriaCrackedStone(
                        modifier = Modifier.fillMaxSize(),
                        seed = 4404,
                        color = Eldoria.IronEdge,
                        density = 16,
                        alpha = 0.35f
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (r in 0 until DIG_SIDE) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (c in 0 until DIG_SIDE) {
                                    val index = r * DIG_SIDE + c
                                    DigCell(
                                        index = index,
                                        content = board[index],
                                        adjacent = neighbours[index],
                                        open = revealed and (1 shl index) != 0,
                                        onTap = onDig,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                    EldoriaImpactBurst(
                        trigger = burst,
                        modifier = Modifier.fillMaxSize(),
                        color = Eldoria.GoldBright,
                        rays = 12,
                        durationMs = 460
                    )
                }
            }

            Spacer(Modifier.height(Eldoria.S8))
            Text(
                text = "El número de una losa vacía indica cuántas vetas tocan sus ocho lados",
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

@Composable
private fun DigCell(
    index: Int,
    content: Int,
    adjacent: Int,
    open: Boolean,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = CutCornerShape(6.dp)
    val accent = when {
        !open -> Eldoria.IronEdge
        content == DIG_VEIN -> Eldoria.GoldBright
        content == DIG_TRAP -> Eldoria.Danger
        adjacent > 0 -> Eldoria.Info
        else -> Eldoria.Iron
    }
    val fill = when {
        !open -> Brush.verticalGradient(listOf(Eldoria.Iron, Eldoria.IronDeep, Eldoria.Abyss))
        content == DIG_VEIN -> Brush.verticalGradient(listOf(Eldoria.GoldDeep, Eldoria.EmberShadow))
        content == DIG_TRAP -> Brush.verticalGradient(listOf(Eldoria.BloodDeep, Eldoria.Abyss))
        else -> Brush.verticalGradient(listOf(Eldoria.PanelSunken, Eldoria.Abyss))
    }

    Box(
        modifier = modifier
            .then(
                if (open && content == DIG_VEIN)
                    Modifier.eldoriaGlowLayer(Eldoria.Gold.copy(alpha = 0.55f), alpha = 0.30f, corner = 6.dp, spread = 6.dp)
                else Modifier
            )
            .clip(shape)
            .background(fill)
            .border(Eldoria.StrokeThin, SolidColor(accent.copy(alpha = if (open) 0.85f else 0.55f)), shape)
            .pointerInput(Unit) { detectTapGestures { onTap(index) } }
            .testTag("dig_cell_$index"),
        contentAlignment = Alignment.Center
    ) {
        if (!open) {
            Canvas(modifier = Modifier.fillMaxSize()) { digDrawRubble(index) }
        } else {
            when (content) {
                DIG_VEIN -> Canvas(modifier = Modifier.fillMaxSize()) { digDrawVein() }
                DIG_TRAP -> Canvas(modifier = Modifier.fillMaxSize()) { digDrawTrap() }
                else -> {
                    if (adjacent > 0) {
                        Box(contentAlignment = Alignment.Center) {
                            EldoriaRuneGlyph(
                                seed = 900 + adjacent,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(9.dp),
                                color = Eldoria.Info.copy(alpha = 0.22f),
                                strokeWidth = 1.2.dp
                            )
                            Text(
                                text = "$adjacent",
                                style = EldoriaType.numeric,
                                color = digNumberColor(adjacent)
                            )
                        }
                    } else {
                        Canvas(modifier = Modifier.fillMaxSize()) { digDrawHollow() }
                    }
                }
            }
        }
    }
}

// ───────────────────────────── ayudas privadas ────────────────────────────────

private fun digBuildBoard(seed: Int, veins: Int, traps: Int): IntArray {
    val board = IntArray(DIG_CELLS) { DIG_EMPTY }
    val rnd = Random(seed)
    val slots = ArrayList<Int>(DIG_CELLS)
    for (i in 0 until DIG_CELLS) slots.add(i)
    // barajado determinista
    for (i in slots.indices.reversed()) {
        val j = rnd.nextInt(i + 1)
        val tmp = slots[i]
        slots[i] = slots[j]
        slots[j] = tmp
    }
    var cursor = 0
    repeat(veins.coerceIn(1, 10)) {
        if (cursor < slots.size) board[slots[cursor]] = DIG_VEIN
        cursor++
    }
    repeat(traps.coerceIn(0, 8)) {
        if (cursor < slots.size) board[slots[cursor]] = DIG_TRAP
        cursor++
    }
    return board
}

private fun digNeighbourCounts(board: IntArray): IntArray {
    val out = IntArray(DIG_CELLS)
    for (r in 0 until DIG_SIDE) {
        for (c in 0 until DIG_SIDE) {
            var n = 0
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if (dr == 0 && dc == 0) continue
                    val rr = r + dr
                    val cc = c + dc
                    if (rr in 0 until DIG_SIDE && cc in 0 until DIG_SIDE &&
                        board[rr * DIG_SIDE + cc] == DIG_VEIN
                    ) n++
                }
            }
            out[r * DIG_SIDE + c] = n
        }
    }
    return out
}

private fun digNumberColor(n: Int): Color = when {
    n >= 4 -> Eldoria.GoldBright
    n == 3 -> Eldoria.Warning
    n == 2 -> Eldoria.Info
    else -> Eldoria.TextMid
}

/** Losa sin picar: piedra con vetas grises y una grieta determinista. */
private fun DrawScope.digDrawRubble(index: Int) {
    val w = size.width
    val h = size.height
    if (w <= 3f || h <= 3f) return
    val a = 0.10f + (index % 5) * 0.02f
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color.White.copy(alpha = a), Color.Transparent),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        ),
        size = Size(w, h)
    )
    val p = Path()
    val sx = w * (0.18f + (index % 3) * 0.22f)
    p.moveTo(sx, h * 0.12f)
    p.lineTo(sx + w * 0.16f, h * 0.42f)
    p.lineTo(sx - w * 0.08f, h * 0.66f)
    p.lineTo(sx + w * 0.14f, h * 0.9f)
    drawPath(
        p,
        color = Color.Black.copy(alpha = 0.35f),
        style = Stroke(width = w * 0.035f, cap = StrokeCap.Round)
    )
}

/** Veta descubierta: cristal facetado con halo. */
private fun DrawScope.digDrawVein() {
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    val r = size.minDimension * 0.30f
    drawCircle(
        brush = Brush.radialGradient(
            0f to Eldoria.GoldBright.copy(alpha = 0.55f),
            1f to Color.Transparent,
            center = Offset(cx, cy),
            radius = r * 2.1f
        ),
        radius = r * 2.1f,
        center = Offset(cx, cy)
    )
    drawPath(eldoriaDiamondPath(cx, cy, r * 1.15f), Color.Black.copy(alpha = 0.6f))
    drawPath(
        eldoriaDiamondPath(cx, cy, r),
        brush = Brush.verticalGradient(
            listOf(Color.White, Eldoria.GoldBright, Eldoria.GoldDeep),
            startY = cy - r,
            endY = cy + r
        )
    )
    drawPath(eldoriaDiamondPath(cx - r * 0.18f, cy - r * 0.22f, r * 0.26f), Color.White.copy(alpha = 0.85f))
}

/** Trampa de aguja: cepo de púas rojas. */
private fun DrawScope.digDrawTrap() {
    val w = size.width
    val h = size.height
    val baseY = h * 0.72f
    drawRect(
        color = Eldoria.BloodDeep,
        topLeft = Offset(w * 0.12f, baseY),
        size = Size(w * 0.76f, h * 0.14f)
    )
    var k = 0
    while (k < 4) {
        val x = w * (0.20f + k * 0.20f)
        val spike = Path()
        spike.moveTo(x - w * 0.07f, baseY)
        spike.lineTo(x, h * 0.24f)
        spike.lineTo(x + w * 0.07f, baseY)
        spike.close()
        drawPath(
            spike,
            brush = Brush.verticalGradient(
                listOf(Eldoria.BloodBright, Eldoria.Blood),
                startY = h * 0.24f,
                endY = baseY
            )
        )
        k++
    }
}

/** Losa vacía sin vetas alrededor. */
private fun DrawScope.digDrawHollow() {
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    drawCircle(
        color = Eldoria.IronEdge.copy(alpha = 0.45f),
        radius = size.minDimension * 0.13f,
        center = Offset(cx, cy),
        style = Stroke(width = size.minDimension * 0.05f)
    )
}
