package com.example.ui.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.audio.SoundManager
import com.example.data.GameViewModel
import com.example.data.model.MinigameRequest
import com.example.data.model.MinigameResult
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaConfirmDialog
import com.example.ui.design.EldoriaCrest
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmptyState
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaImpactBurst
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaProgressRing
import com.example.ui.design.EldoriaRuneGlyph
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaStatPill
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaFloat
import com.example.ui.design.eldoriaPulse

// ──────────────────────────────────────────────────────────────────────────────
//  LA TABERNA DEL GRIFO DORADO — anfitrión de los seis minijuegos.
//  Overlay a pantalla completa: monta el juego pedido, recoge su resultado,
//  lo entrega al controlador y muestra el panel de recompensas.
// ──────────────────────────────────────────────────────────────────────────────

/** Overlay montado por el contenedor principal cuando `systems.minigame != null`. */
@Composable
fun MinigameHostScreen(viewModel: GameViewModel) {
    val request by viewModel.systems.minigame.collectAsState()
    val req = request ?: return

    var result by remember(req) { mutableStateOf<MinigameResult?>(null) }
    val current = result

    LaunchedEffect(current) {
        val r = current ?: return@LaunchedEffect
        // Abandonar no paga: el diálogo de salida lo dice («no recibirás recompensa
        // alguna»), pero el resultado se enviaba igual y la Ganzúa, de entrada libre,
        // se convertía en un grifo infinito de oro y botín.
        if (r.rating == "ABANDONADA") {
            viewModel.systems.closeMinigame()
            return@LaunchedEffect
        }
        viewModel.systems.submitMinigameResult(r)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Eldoria.Abyss)
            // corta cualquier toque que intente alcanzar la pantalla de debajo
            .pointerInput(Unit) { detectTapGestures { } }
    ) {
        if (current == null) {
            val finish: (MinigameResult) -> Unit = { r -> result = r }
            when (req.id.uppercase()) {
                "YUNQUE" -> MinigameYunque(req, finish)
                "GANZUA" -> MinigameGanzua(req, finish)
                "GLIFOS" -> MinigameGlifos(req, finish)
                "EXCAVACION" -> MinigameExcavacion(req, finish)
                "ADIESTRAMIENTO" -> MinigameAdiestramiento(req, finish)
                "VIGILIA" -> MinigameVigilia(req, finish)
                else -> HostUnknownGame(req) { viewModel.systems.closeMinigame() }
            }
        } else {
            MinigameResultPanel(
                result = current,
                rewardLines = hostRewardLines(current),
                onClose = { viewModel.systems.closeMinigame() }
            )
        }
    }
}

/** Cromo compartido: marco, cabecera, reloj (en [subtitle]), marcador, salida y arena. */
@Composable
fun MinigameShell(
    title: String,
    subtitle: String,
    tone: EldoriaTone,
    scoreLabel: String,
    onQuit: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val accent = Eldoria.toneColor(tone)
    var confirmQuit by remember { mutableStateOf(false) }

    EldoriaScreen(
        modifier = modifier,
        depth = 2,
        embers = true,
        fog = true,
        vignetteStrength = 0.62f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EldoriaIconButton(
                    icon = Icons.Filled.Close,
                    contentDescription = "Abandonar el minijuego",
                    onClick = { confirmQuit = true },
                    tone = EldoriaTone.Iron,
                    size = 40.dp,
                    testTag = "minigame_quit_btn"
                )
                Spacer(Modifier.width(Eldoria.S12))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = EldoriaType.title,
                        color = accent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = EldoriaType.small,
                        color = Eldoria.TextMid,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(Eldoria.S8))
                EldoriaStatPill(
                    label = "Marcador",
                    value = scoreLabel,
                    icon = Icons.Filled.Star,
                    accent = accent
                )
            }

            Spacer(Modifier.height(Eldoria.S8))
            EldoriaDivider(color = accent)
            Spacer(Modifier.height(Eldoria.S8))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                content = content
            )
        }
    }

    if (confirmQuit) {
        EldoriaConfirmDialog(
            title = "¿Abandonar la partida?",
            message = "Perderás la entrada pagada y no recibirás recompensa alguna por lo jugado.",
            confirmLabel = "Abandonar",
            onConfirm = {
                confirmQuit = false
                onQuit()
            },
            onDismiss = { confirmQuit = false },
            dismissLabel = "Seguir jugando",
            tone = EldoriaTone.Blood,
            testTagPrefix = "minigameQuit"
        )
    }
}

/** Panel de cierre: veredicto, marcador circular y desglose de recompensas. */
@Composable
fun MinigameResultPanel(
    result: MinigameResult,
    rewardLines: List<String>,
    onClose: () -> Unit
) {
    var burst by remember(result) { mutableStateOf(0) }
    LaunchedEffect(result) {
        burst = 1
        if (result.success) SoundManager.playVictory() else SoundManager.playDefeat()
    }

    val tone = if (result.success) EldoriaTone.Gold else EldoriaTone.Iron
    val accent = if (result.success) Eldoria.TextGold else Eldoria.TextMid
    val edge = if (result.success) EldoriaEdge.Gold else EldoriaEdge.Iron
    val lift = eldoriaFloat(periodMs = 3600, amplitude = 5.dp, label = "resultLift")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Eldoria.Scrim)
            .pointerInput(Unit) { detectTapGestures { } },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 460.dp)
                    .padding(vertical = 24.dp),
                edge = edge,
                padding = PaddingValues(18.dp),
                glow = true,
                filigree = true,
                testTag = "minigame_result_panel"
            ) {
                Text(
                    text = if (result.success) "PARTIDA SUPERADA" else "PARTIDA PERDIDA",
                    style = EldoriaType.label,
                    color = if (result.success) Eldoria.Success else Eldoria.Danger,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Eldoria.S6))
                Text(
                    text = if (result.rating.isBlank()) "SIN VEREDICTO" else result.rating,
                    style = EldoriaType.display,
                    color = accent,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Eldoria.S12))

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(126.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EldoriaProgressRing(
                        progress = result.score / 100f,
                        modifier = Modifier.offset(y = lift),
                        size = 112.dp,
                        stroke = 9.dp,
                        accent = if (result.success) Eldoria.Gold else Eldoria.IronEdge,
                        centerLabel = "${result.score.coerceIn(0, 100)} %"
                    )
                    EldoriaImpactBurst(
                        trigger = burst,
                        modifier = Modifier.fillMaxSize(),
                        color = if (result.success) Eldoria.GoldBright else Eldoria.IronEdge,
                        rays = 14,
                        durationMs = 620
                    )
                }

                if (result.perfect > 0) {
                    Spacer(Modifier.height(Eldoria.S12))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        EldoriaChip(
                            text = "${result.perfect} aciertos perfectos",
                            color = Eldoria.ArcaneBright,
                            icon = Icons.Filled.Star,
                            filled = true
                        )
                    }
                }

                Spacer(Modifier.height(Eldoria.S16))
                EldoriaSectionTitle(text = "Botín", accent = Eldoria.toneColor(tone))
                Spacer(Modifier.height(Eldoria.S8))

                if (rewardLines.isEmpty()) {
                    Text(
                        text = "Sin recompensa. La casa se queda con la entrada.",
                        style = EldoriaType.lore,
                        color = Eldoria.TextLow,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    rewardLines.forEach { line -> HostRewardRow(line) }
                }

                Spacer(Modifier.height(Eldoria.S20))
                EldoriaButton(
                    text = "CONTINUAR",
                    onClick = onClose,
                    tone = tone,
                    size = EldoriaButtonSize.Large,
                    icon = Icons.Filled.PlayArrow,
                    fullWidth = true,
                    testTag = "minigame_result_close"
                )
            }
        }
    }
}

/** Pantalla de instrucciones compartida: dos líneas, COMENZAR y salida limpia. */
@Composable
internal fun MinigameIntro(
    title: String,
    subtitle: String,
    tone: EldoriaTone,
    crestSeed: Int,
    lineOne: String,
    lineTwo: String,
    onStart: () -> Unit,
    onQuit: () -> Unit
) {
    val accent = Eldoria.toneColor(tone)
    val glow = eldoriaPulse(periodMs = 2400, from = 0.55f, to = 1f, label = "introGlow")
    val lift = eldoriaFloat(periodMs = 3800, amplitude = 6.dp, label = "introLift")

    EldoriaScreen(
        depth = 2,
        embers = true,
        fog = true,
        vignetteStrength = 0.7f,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 116.dp)
                    .offset(y = lift),
                contentAlignment = Alignment.Center
            ) {
                EldoriaCrest(
                    seed = crestSeed,
                    modifier = Modifier.fillMaxSize(),
                    primary = accent,
                    secondary = Eldoria.IronDeep,
                    ornate = true
                )
            }
            Spacer(Modifier.height(Eldoria.S16))
            Text(
                text = title,
                style = EldoriaType.display,
                color = accent,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Eldoria.S4))
            Text(
                text = subtitle,
                style = EldoriaType.lore,
                color = Eldoria.TextMid,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Eldoria.S16))
            EldoriaDivider(color = accent)
            Spacer(Modifier.height(Eldoria.S16))

            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp),
                edge = EldoriaEdge.tone(tone),
                padding = PaddingValues(14.dp)
            ) {
                HostInstructionRow(index = 1, text = lineOne, accent = accent, glow = glow)
                Spacer(Modifier.height(Eldoria.S8))
                HostInstructionRow(index = 2, text = lineTwo, accent = accent, glow = glow)
            }

            Spacer(Modifier.height(Eldoria.S24))
            EldoriaButton(
                text = "COMENZAR",
                onClick = onStart,
                modifier = Modifier.widthIn(max = 440.dp),
                tone = tone,
                size = EldoriaButtonSize.Large,
                icon = Icons.Filled.PlayArrow,
                fullWidth = true,
                testTag = "minigame_start_btn"
            )
            Spacer(Modifier.height(Eldoria.S8))
            EldoriaButton(
                text = "SALIR",
                onClick = onQuit,
                modifier = Modifier.widthIn(max = 440.dp),
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Medium,
                icon = Icons.Filled.Close,
                fullWidth = true,
                testTag = "minigame_exit_btn"
            )
        }
    }
}

/**
 * Reloj de cuenta atrás compartido. Un único `withFrameNanos` por partida; sólo
 * escribe estado cuando cambia el segundo mostrado. Devuelve los segundos restantes.
 */
@Composable
internal fun MinigameCountdown(
    active: Boolean,
    totalMs: Int,
    onExpire: () -> Unit
): Int {
    val total = totalMs.coerceAtLeast(1000)
    val expire = rememberUpdatedState(onExpire)
    var secondsLeft by remember(total) { mutableStateOf((total + 999) / 1000) }

    LaunchedEffect(active, total) {
        if (!active) return@LaunchedEffect
        var startNs = 0L
        var done = false
        while (!done) {
            withFrameNanos { now ->
                if (startNs == 0L) startNs = now
                val elapsedMs = (now - startNs) / 1_000_000L
                val left = (((total - elapsedMs) + 999L) / 1000L).toInt().coerceAtLeast(0)
                if (left != secondsLeft) secondsLeft = left
                if (elapsedMs >= total) done = true
            }
        }
        expire.value.invoke()
    }
    return secondsLeft
}

/** Formato de reloj compartido: "0:24". */
internal fun MinigameClockText(secondsLeft: Int): String {
    val s = secondsLeft.coerceAtLeast(0)
    val mm = s / 60
    val ss = s % 60
    return "$mm:${if (ss < 10) "0$ss" else "$ss"}"
}

// ───────────────────────────── piezas privadas ────────────────────────────────

@Composable
private fun HostInstructionRow(index: Int, text: String, accent: Color, glow: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(30.dp),
            contentAlignment = Alignment.Center
        ) {
            EldoriaRuneGlyph(
                seed = 3100 + index,
                modifier = Modifier.fillMaxSize(),
                color = accent.copy(alpha = (0.45f + 0.55f * glow).coerceIn(0f, 1f)),
                strokeWidth = 1.6.dp
            )
        }
        Spacer(Modifier.width(Eldoria.S12))
        Text(
            text = text,
            style = EldoriaType.body,
            color = Eldoria.TextHi,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HostRewardRow(line: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(14.dp),
            contentAlignment = Alignment.Center
        ) {
            EldoriaRuneGlyph(
                seed = line.length * 7 + 11,
                modifier = Modifier.fillMaxSize(),
                color = Eldoria.Gold.copy(alpha = 0.8f),
                strokeWidth = 1.dp
            )
        }
        Spacer(Modifier.width(Eldoria.S8))
        Text(
            text = line,
            style = EldoriaType.body,
            color = Eldoria.TextHi,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HostUnknownGame(request: MinigameRequest, onClose: () -> Unit) {
    EldoriaScreen(depth = 1, embers = true, vignetteStrength = 0.6f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            EldoriaEmptyState(
                title = "Mesa vacía",
                message = "El juego «${request.id}» no se sirve en esta taberna. Vuelve al tablón y elige otro.",
                icon = Icons.Filled.Warning,
                accent = Eldoria.Warning,
                actionLabel = "Volver",
                onAction = onClose,
                testTag = "minigame_unknown"
            )
        }
    }
}

/** Traduce el resultado en el desglose que ejecuta el controlador de sistemas. */
private fun hostRewardLines(result: MinigameResult): List<String> {
    val s = result.score.coerceIn(0, 100)
    val ok = result.success
    return when (result.id.uppercase()) {
        "YUNQUE" -> if (result.contextJson.isBlank()) {
            listOfNotNull(
                "Materiales de forja: ${2 + s / 25} lotes recuperados",
                if (s >= 70) "Brasa de forja extra por el temple del acero" else null
            )
        } else {
            listOfNotNull(
                "Pieza forjada con calidad $s",
                if (s >= 70) "Brasa de forja extra por el temple del acero" else null,
                if (s < 30) "El golpe apenas cuaja: sólo rescatas chatarra" else null
            )
        }

        "GANZUA" -> listOfNotNull(
            if (ok) "Cofre abierto: botín con rareza mejorada" else "El cofre resiste: botín deslucido",
            "Oro del escondrijo multiplicado por el desempeño",
            if (s >= 75) "Los pasadores cedieron sin romper ganzúa" else null
        )

        "GLIFOS" -> listOfNotNull(
            "Materiales arcanos: ${2 + s / 20} lotes",
            if (s >= 60) "El altar añade 2 pociones a tu zurrón" else null,
            if (s < 34) "Los glifos se apagan antes de pagar del todo" else null
        )

        "EXCAVACION" -> listOfNotNull(
            "Materiales de cripta: ${3 + s / 12} lotes",
            if (s >= 80) "Has vaciado la veta entera" else null,
            if (s == 0) "Ninguna picada dio con nada aprovechable" else null
        )

        "ADIESTRAMIENTO" -> listOfNotNull(
            "Experiencia y disciplina para la mascota",
            "Vínculo reforzado con la bestia",
            if (s >= 80) "Sesión ejemplar: bonificación de disciplina" else null,
            if (s < 34) "La bestia se distrae: progreso mínimo" else null
        )

        "VIGILIA" -> listOfNotNull(
            "Recuperas un $s % de vida",
            "Recuperas un $s % de maná",
            if (s >= 90) "La hoguera arde intacta hasta el alba" else null,
            if (s < 34) "El fuego apenas sobrevive a la noche" else null
        )

        else -> listOfNotNull(if (ok) "Recompensa simbólica de la casa" else null)
    }
}
