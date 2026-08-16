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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.audio.SoundManager
import com.example.data.model.MinigameRequest
import com.example.data.model.MinigameResult
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaImpactBurst
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaRuneGlyph
import com.example.ui.design.EldoriaScanlines
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaDiamondPath
import com.example.ui.design.eldoriaGlowLayer
import com.example.ui.design.eldoriaShake
import kotlinx.coroutines.delay
import kotlin.random.Random

// ──────────────────────────────────────────────────────────────────────────────
//  GLIFOS RÚNICOS
//  Tres rondas. El altar canta una secuencia de runas cada vez más larga y el
//  jugador la repite sobre la rejilla 3×3. Un error cierra la ronda.
// ──────────────────────────────────────────────────────────────────────────────

private const val GLYPH_ROUNDS = 3
private const val GLYPH_TOTAL_MS = 40_000
private const val GLYPH_PHASE_SHOW = 0
private const val GLYPH_PHASE_INPUT = 1
private const val GLYPH_PHASE_BREAK = 2

@Composable
fun MinigameGlifos(request: MinigameRequest, onFinish: (MinigameResult) -> Unit) {
    val difficulty = request.difficulty.coerceIn(1, 5)
    val combo = rememberComboState()
    val gameFeel = rememberMinigameFeedback()

    var started by remember { mutableStateOf(false) }

    if (!started) {
        MinigameIntro(
            title = "GLIFOS RÚNICOS",
            subtitle = if (request.title.isBlank()) "El altar recita; tú respondes" else request.title,
            tone = EldoriaTone.Arcane,
            crestSeed = 4303,
            lineOne = "Observa qué runas se encienden y repite la secuencia exacta tocándolas en orden.",
            lineTwo = "Tres rondas cada vez más largas. Un solo error cierra la ronda en curso.",
            onStart = { started = true },
            onQuit = {
                onFinish(
                    MinigameResult(
                        id = "GLIFOS", success = false, score = 0, perfect = 0,
                        rating = "ABANDONADA", contextJson = request.contextJson
                    )
                )
            }
        )
        return
    }

    val runeSeeds = remember(request.contextJson) {
        val rnd = Random(request.contextJson.hashCode() * 23 + 5501)
        IntArray(9) { 5100 + rnd.nextInt(9000) }
    }
    val lengths = remember(difficulty) {
        val base = (4 + (difficulty - 1) / 2).coerceIn(4, 5)
        List(GLYPH_ROUNDS) { (base + it).coerceAtMost(7) }
    }
    val totalSymbols = remember(lengths) { lengths.sum().coerceAtLeast(1) }
    val rnd = remember(request.contextJson) { Random(request.contextJson.hashCode() * 41 + 7717) }

    var round by remember { mutableStateOf(0) }
    var sequence by remember { mutableStateOf(emptyList<Int>()) }
    var phase by remember { mutableStateOf(GLYPH_PHASE_SHOW) }
    var highlight by remember { mutableStateOf(-1) }
    var inputIndex by remember { mutableStateOf(0) }
    var flashCell by remember { mutableStateOf(-1) }
    var flashGood by remember { mutableStateOf(true) }
    var flashToken by remember { mutableStateOf(0) }
    var correctTotal by remember { mutableStateOf(0) }
    var cleanRounds by remember { mutableStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var burst by remember { mutableStateOf(0) }
    var shakeTrigger by remember { mutableStateOf(0) }
    var breakToken by remember { mutableStateOf(0) }

    val shakeOffset = eldoriaShake(trigger = shakeTrigger, magnitude = 8.dp, cycles = 3, label = "glyphShake")

    val closeRun: () -> Unit = closeRun@{
        if (finished) return@closeRun
        finished = true
        val score = (correctTotal * 100 / totalSymbols).coerceIn(0, 100)
        val rating = when (cleanRounds) {
            3 -> "ORÁCULO RÚNICO"
            2 -> "ADEPTO DEL ALTAR"
            1 -> "APRENDIZ DE RUNAS"
            else -> "PROFANO"
        }
        onFinish(
            MinigameResult(
                id = "GLIFOS",
                success = cleanRounds >= 1,
                score = score,
                perfect = cleanRounds,
                rating = rating,
                contextJson = request.contextJson
            )
        )
    }

    val secondsLeft = MinigameCountdown(active = !finished, totalMs = GLYPH_TOTAL_MS, onExpire = closeRun)

    // Recitado del altar
    LaunchedEffect(round) {
        if (round >= GLYPH_ROUNDS) return@LaunchedEffect
        val len = lengths[round]
        val built = ArrayList<Int>(len)
        var last = -1
        repeat(len) {
            var next = rnd.nextInt(9)
            if (next == last) next = (next + 1 + rnd.nextInt(8)) % 9
            last = next
            built.add(next)
        }
        sequence = built
        inputIndex = 0
        highlight = -1
        phase = GLYPH_PHASE_SHOW
        delay(620)
        val showMs = (640 - difficulty * 45).coerceAtLeast(240).toLong()
        val gapMs = (210 - difficulty * 12).coerceAtLeast(110).toLong()
        for (cell in built) {
            highlight = cell
            SoundManager.playMagicSpell()
            delay(showMs)
            highlight = -1
            delay(gapMs)
        }
        phase = GLYPH_PHASE_INPUT
    }

    LaunchedEffect(flashToken) {
        if (flashToken > 0) {
            delay(260)
            flashCell = -1
        }
    }

    val onCell: (Int) -> Unit = onCell@{ cell ->
        if (finished || phase != GLYPH_PHASE_INPUT) return@onCell
        val seq = sequence
        if (inputIndex >= seq.size) return@onCell
        flashCell = cell
        flashToken += 1
        if (seq[inputIndex] == cell) {
            flashGood = true
            correctTotal += 1
            inputIndex += 1
            val before = combo.multiplier
            combo.hit()
            gameFeel.hit(combo.streak)
            if (combo.multiplier > before) gameFeel.step()
            SoundManager.playButtonClick()
            if (inputIndex >= seq.size) {
                cleanRounds += 1
                burst += 1
                phase = GLYPH_PHASE_BREAK
                breakToken += 1
                SoundManager.playHealPotion()
            }
        } else {
            flashGood = false
            shakeTrigger += 1
            phase = GLYPH_PHASE_BREAK
            breakToken += 1
            combo.miss()
            gameFeel.miss()
            SoundManager.playEnemyAttack()
        }
    }

    // Pausa entre rondas: un único token evita carreras entre efectos.
    LaunchedEffect(breakToken) {
        if (breakToken <= 0) return@LaunchedEffect
        delay(950)
        if (round + 1 >= GLYPH_ROUNDS) closeRun() else round += 1
    }

    val phaseLabel = when {
        phase == GLYPH_PHASE_SHOW -> "MEMORIZA"
        phase == GLYPH_PHASE_INPUT -> "REPITE  ${inputIndex}/${sequence.size}"
        else -> "EL ALTAR MEDITA"
    }

    MinigameShell(
        title = "GLIFOS RÚNICOS",
        subtitle = "Ronda ${(round + 1).coerceAtMost(GLYPH_ROUNDS)}/$GLYPH_ROUNDS  ·  ${MinigameClockText(secondsLeft)}",
        tone = EldoriaTone.Arcane,
        scoreLabel = "$correctTotal/$totalSymbols",
        combo = combo,
        onQuit = {
            if (!finished) {
                finished = true
                onFinish(
                    MinigameResult(
                        id = "GLIFOS", success = false, score = 0, perfect = 0,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EldoriaChip(
                    text = phaseLabel,
                    color = if (phase == GLYPH_PHASE_INPUT) Eldoria.ArcaneBright else Eldoria.TextGold,
                    filled = phase == GLYPH_PHASE_INPUT
                )
                EldoriaChip(
                    text = "Rondas limpias $cleanRounds/$GLYPH_ROUNDS",
                    color = Eldoria.Success
                )
            }

            Spacer(Modifier.height(Eldoria.S8))

            // Cuentas de la secuencia en curso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S6, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val len = sequence.size
                for (i in 0 until len) {
                    Canvas(modifier = Modifier.size(14.dp)) {
                        val done = i < inputIndex
                        val c = if (done) Eldoria.ArcaneBright else Eldoria.IronEdge
                        drawPath(
                            eldoriaDiamondPath(center.x, center.y, size.minDimension * 0.42f),
                            Color.Black.copy(alpha = 0.6f)
                        )
                        drawPath(
                            eldoriaDiamondPath(center.x, center.y, size.minDimension * 0.34f),
                            c
                        )
                    }
                }
            }

            Spacer(Modifier.height(Eldoria.S12))

            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .offset(x = shakeOffset),
                edge = EldoriaEdge.Arcane,
                padding = PaddingValues(10.dp),
                glow = phase == GLYPH_PHASE_INPUT,
                filigree = true
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        verticalArrangement = Arrangement.spacedBy(Eldoria.S8)
                    ) {
                        for (r in 0 until 3) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                            ) {
                                for (c in 0 until 3) {
                                    val index = r * 3 + c
                                    GlyphCell(
                                        index = index,
                                        seed = runeSeeds[index],
                                        lit = highlight == index,
                                        flash = flashCell == index,
                                        flashGood = flashGood,
                                        enabled = phase == GLYPH_PHASE_INPUT,
                                        onTap = onCell,
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
                        color = Eldoria.ArcaneBright,
                        rays = 18,
                        durationMs = 600
                    )
                }
            }

            Spacer(Modifier.height(Eldoria.S8))
            EldoriaDivider(color = Eldoria.Arcane)
            Spacer(Modifier.height(Eldoria.S4))
            Text(
                text = if (phase == GLYPH_PHASE_SHOW) "El altar recita la secuencia…" else "Repite el orden exacto",
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
private fun GlyphCell(
    index: Int,
    seed: Int,
    lit: Boolean,
    flash: Boolean,
    flashGood: Boolean,
    enabled: Boolean,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = CutCornerShape(10.dp)
    val accent = when {
        flash && flashGood -> Eldoria.Success
        flash && !flashGood -> Eldoria.Danger
        lit -> Eldoria.GoldBright
        enabled -> Eldoria.Arcane
        else -> Eldoria.IronEdge
    }
    val fill = when {
        lit -> Brush.verticalGradient(listOf(Eldoria.ArcaneDeep, Eldoria.Arcane.copy(alpha = 0.55f), Eldoria.ArcaneDeep))
        flash -> Brush.verticalGradient(listOf(accent.copy(alpha = 0.42f), accent.copy(alpha = 0.12f)))
        else -> Brush.verticalGradient(listOf(Eldoria.PanelSunken, Eldoria.Abyss))
    }

    Box(
        modifier = modifier
            .then(
                if (lit || flash) Modifier.eldoriaGlowLayer(accent.copy(alpha = 0.6f), alpha = 0.32f, corner = 10.dp, spread = 8.dp)
                else Modifier
            )
            .clip(shape)
            .background(fill)
            .border(if (lit) Eldoria.StrokeBold else Eldoria.StrokeThin, SolidColor(accent.copy(alpha = if (enabled || lit) 0.95f else 0.4f)), shape)
            .pointerInput(Unit) { detectTapGestures { onTap(index) } }
            .testTag("glyph_cell_$index"),
        contentAlignment = Alignment.Center
    ) {
        EldoriaScanlines(
            modifier = Modifier.fillMaxSize(),
            color = Eldoria.ArcaneBright,
            alpha = 0.05f,
            gapDp = 4.dp
        )
        EldoriaRuneGlyph(
            seed = seed,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            color = if (lit) Eldoria.GoldBright else accent.copy(alpha = if (enabled) 0.85f else 0.45f),
            strokeWidth = if (lit) 3.dp else 2.dp,
            animated = lit
        )
    }
}
