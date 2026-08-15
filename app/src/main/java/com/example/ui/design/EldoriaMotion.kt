package com.example.ui.design

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.audio.SoundManager
import kotlin.math.PI
import kotlin.math.sin

/**
 * Curvas y duraciones canónicas de Eldoria.
 * Toda animación nueva usa estas constantes: el juego debe sentirse como una sola pieza.
 */
object EldoriaMotion {
    const val fast = 140
    const val normal = 260
    const val slow = 520
    const val cinematic = 900

    /** Entrada decidida, frenada larga. Para barras, apariciones y swaps. */
    val easeOut: Easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

    /** Simétrica. Para latidos y bucles infinitos. */
    val easeInOut: Easing = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)

    /** Rebasa el destino: impactos, recompensas, subidas de nivel. */
    val overshoot: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
}

/** Latido de alfa/escala entre [from] y [to]. Devuelve el valor vivo. */
@Composable
fun eldoriaPulse(
    periodMs: Int = 1800,
    from: Float = 0.55f,
    to: Float = 1f,
    label: String = "pulse"
): Float {
    val transition = rememberInfiniteTransition(label = label)
    val value by transition.animateFloat(
        initialValue = from,
        targetValue = to,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMs.coerceAtLeast(60), easing = EldoriaMotion.easeInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = label
    )
    return value
}

/** Flotación vertical suave (±amplitude/2). Ideal para retratos, gemas y sellos. */
@Composable
fun eldoriaFloat(
    periodMs: Int = 3200,
    amplitude: Dp = 4.dp,
    label: String = "float"
): Dp {
    val transition = rememberInfiniteTransition(label = label)
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMs.coerceAtLeast(120), easing = EldoriaMotion.easeInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = label
    )
    return amplitude * (t - 0.5f)
}

/**
 * Sacudida amortiguada disparada por [trigger] (incrementa el entero para relanzarla).
 * Devuelve el desplazamiento en Dp que el llamante aplica con `offset(x = ...)`.
 */
@Composable
fun eldoriaShake(
    trigger: Int,
    magnitude: Dp = 8.dp,
    cycles: Int = 3,
    label: String = "shake"
): Dp {
    val safeCycles = cycles.coerceIn(1, 12)
    val anim = remember(label) { Animatable(1f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            anim.snapTo(0f)
            anim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 70 * safeCycles * 2, easing = LinearEasing)
            )
        }
    }
    val p = anim.value
    if (p >= 1f) return 0.dp
    val decay = 1f - p
    val wave = sin(p * safeCycles * 2f * PI.toFloat())
    return magnitude * (wave * decay)
}

/** Cuenta ascendente/descendente animada de un valor largo (oro, daño total, puntuación). */
@Composable
fun rememberEldoriaCountUp(target: Long, durationMs: Int = 700): Long {
    val anim = remember { Animatable(target.toFloat()) }
    LaunchedEffect(target) {
        anim.animateTo(
            targetValue = target.toFloat(),
            animationSpec = tween(durationMillis = durationMs.coerceAtLeast(1), easing = EldoriaMotion.easeOut)
        )
    }
    return anim.value.toLong()
}

/** Cross-fade + leve escala entre pantallas/estados. Sustituye los cortes secos del router. */
@Composable
fun <T> EldoriaSwap(
    targetState: T,
    modifier: Modifier = Modifier,
    durationMs: Int = EldoriaMotion.normal,
    content: @Composable (T) -> Unit
) {
    val d = durationMs.coerceAtLeast(40)
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            (
                fadeIn(animationSpec = tween(durationMillis = d, easing = EldoriaMotion.easeOut)) +
                    scaleIn(
                        initialScale = 0.965f,
                        animationSpec = tween(durationMillis = d, easing = EldoriaMotion.easeOut)
                    )
                ).togetherWith(
                fadeOut(animationSpec = tween(durationMillis = d / 2, easing = LinearEasing)) +
                    scaleOut(
                        targetScale = 1.02f,
                        animationSpec = tween(durationMillis = d / 2, easing = LinearEasing)
                    )
            )
        },
        contentAlignment = Alignment.Center,
        label = "eldoriaSwap"
    ) { state ->
        content(state)
    }
}

/**
 * Halo por capas alrededor del nodo. Sustituto legal de `Modifier.blur`
 * (que es no-op por debajo de API 31 y está prohibido en código nuevo).
 */
fun Modifier.eldoriaGlowLayer(
    color: Color,
    alpha: Float = 0.28f,
    corner: Dp = 12.dp,
    spread: Dp = 6.dp
): Modifier = this.drawBehind {
    val s = spread.toPx().coerceAtLeast(0f)
    val r = corner.toPx()
    val layers = 4
    for (i in layers downTo 1) {
        val f = i.toFloat() / layers
        val grow = s * f
        val a = (alpha * (1f - f) * 0.85f + alpha * 0.14f).coerceIn(0f, 1f)
        drawRoundRect(
            color = color.copy(alpha = a),
            topLeft = Offset(-grow, -grow),
            size = Size(size.width + grow * 2f, size.height + grow * 2f),
            cornerRadius = CornerRadius(r + grow, r + grow)
        )
    }
}

/** Bisel interno: filo claro arriba/izquierda, sombra abajo/derecha. Da volumen metálico. */
fun Modifier.eldoriaBevel(
    corner: Dp = 8.dp,
    light: Color = Color.White.copy(alpha = 0.10f),
    dark: Color = Color.Black.copy(alpha = 0.35f)
): Modifier = this.drawWithContent {
    drawContent()
    val r = corner.toPx()
    val w = 1.dp.toPx()
    val i = w / 2f
    val pad = (r * 0.55f).coerceAtMost(size.minDimension / 2f)
    if (size.width <= pad * 2f || size.height <= pad * 2f) return@drawWithContent

    drawLine(light, Offset(pad, i), Offset(size.width - pad, i), w)
    drawLine(light.copy(alpha = light.alpha * 0.55f), Offset(i, pad), Offset(i, size.height - pad), w)
    drawLine(dark, Offset(pad, size.height - i), Offset(size.width - pad, size.height - i), w)
    drawLine(
        dark.copy(alpha = dark.alpha * 0.6f),
        Offset(size.width - i, pad),
        Offset(size.width - i, size.height - pad),
        w
    )
}

/** Clic con el "clac" del juego siempre delante del callback. */
fun Modifier.eldoriaPressable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    sound: Boolean = true
): Modifier = this.clickable(enabled = enabled) {
    if (sound) SoundManager.playButtonClick()
    onClick()
}
