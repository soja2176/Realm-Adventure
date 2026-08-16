package com.example.ui.minigames

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.audio.SoundManager
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaType

// ══════════════════════════════════════════════════════════════════════════════
//  RACHAS DE MINIJUEGO
//
//  EL PROBLEMA
//  Los seis minijuegos puntuaban cada acierto por separado: fallar el tercer
//  golpe costaba exactamente lo mismo que fallar el primero, y encadenar diez
//  aciertos valía lo mismo que acertar diez sueltos. Sin memoria entre jugadas
//  no hay tensión — daba igual cómo lo hicieras mientras el total cuadrase.
//
//  LO QUE HACE
//  Una racha compartida por los seis: cada acierto encadenado sube un
//  multiplicador, y un fallo lo tira entero. Eso convierte cada jugada en una
//  decisión con algo que perder, que es lo que no había.
//
//  El multiplicador se corta en [MAX_MULTIPLIER] a propósito: sin techo, una
//  racha larga en un juego de veinte jugadas eclipsaba a cualquier partida
//  buena pero imperfecta, y el juego dejaba de premiar jugar bien para premiar
//  jugar sin un solo fallo.
// ══════════════════════════════════════════════════════════════════════════════

/** Aciertos encadenados necesarios para cada escalón del multiplicador. */
private const val STEP_EVERY = 3

/** Techo del multiplicador. Ver la nota de arriba sobre por qué existe. */
private const val MAX_MULTIPLIER = 2.5f

class ComboState {
    /** Aciertos encadenados ahora mismo. */
    var streak by mutableIntStateOf(0)
        private set

    /** La racha más larga de la partida: es lo que se enseña al terminar. */
    var best by mutableIntStateOf(0)
        private set

    /** Total de aciertos y fallos, para el resumen final. */
    var hits by mutableIntStateOf(0)
        private set
    var misses by mutableIntStateOf(0)
        private set

    /** Sube 0,25 cada [STEP_EVERY] aciertos, hasta [MAX_MULTIPLIER]. */
    val multiplier: Float
        get() = (1f + (streak / STEP_EVERY) * 0.25f).coerceAtMost(MAX_MULTIPLIER)

    /** Está a un acierto de subir de escalón: sirve para avisar en pantalla. */
    val nextStepIn: Int
        get() = STEP_EVERY - (streak % STEP_EVERY)

    fun hit(): Float {
        streak += 1
        hits += 1
        if (streak > best) best = streak
        return multiplier
    }

    fun miss() {
        streak = 0
        misses += 1
    }

    fun reset() {
        streak = 0
        best = 0
        hits = 0
        misses = 0
    }
}

@Composable
fun rememberComboState(): ComboState = remember { ComboState() }

/**
 * Respuesta física y sonora de una jugada.
 *
 * El juego no tenía háptica en ninguna pantalla — ni un solo `performHapticFeedback`
 * en todo el proyecto. En un juego que se juega a toques, el dedo es la mitad de
 * la información, y esa mitad faltaba entera.
 */
class MinigameFeedback(private val view: View) {

    /** Acierto. El golpe se endurece con la racha. */
    fun hit(streak: Int) {
        SoundManager.playComboTick(streak)
        view.performHapticFeedback(
            if (streak >= 6) HapticFeedbackConstants.LONG_PRESS
            else HapticFeedbackConstants.KEYBOARD_TAP
        )
    }

    /** Fallo: se rompe la racha. */
    fun miss() {
        SoundManager.playComboBreak()
        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
    }

    /** Subida de escalón del multiplicador. */
    fun step() {
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }
}

@Composable
fun rememberMinigameFeedback(): MinigameFeedback {
    val view = LocalView.current
    return remember(view) { MinigameFeedback(view) }
}

/**
 * Medidor de racha. Da un salto cada vez que sube el multiplicador — sin ese
 * pulso el número cambia sin que nadie lo note.
 */
@Composable
fun MinigameComboMeter(
    combo: ComboState,
    modifier: Modifier = Modifier,
    accent: Color = Eldoria.GoldBright
) {
    val pulse by animateFloatAsState(
        targetValue = if (combo.streak > 0) 1f + (combo.multiplier - 1f) * 0.18f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "comboPulse"
    )
    val alpha by animateFloatAsState(
        targetValue = if (combo.streak > 0) 1f else 0.35f,
        label = "comboAlpha"
    )

    Row(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .scale(pulse)
            .background(Eldoria.Ink.copy(alpha = 0.55f), RoundedCornerShape(Eldoria.R8))
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(Eldoria.R8))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (combo.streak > 0) "RACHA ${combo.streak}" else "SIN RACHA",
            style = EldoriaType.small,
            color = if (combo.streak > 0) accent else Eldoria.TextMid
        )
        Box(
            modifier = Modifier
                .background(accent.copy(alpha = 0.18f), RoundedCornerShape(Eldoria.R8))
                .padding(horizontal = 6.dp, vertical = 1.dp)
        ) {
            Text(
                text = "×%.2f".format(combo.multiplier),
                style = EldoriaType.small,
                color = accent
            )
        }
    }
}
