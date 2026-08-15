package com.example.ui.design

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.SoundManager
import com.example.data.formatGameNumber
import kotlin.math.min

// ──────────────────────────────────────────────────────────────────────────────
//  Controles de Eldoria. Ningún control usa componentes Material por defecto:
//  todo relleno metálico es un gradiente de tres paradas con bisel interno.
// ──────────────────────────────────────────────────────────────────────────────

/** Multiplica el canal RGB conservando el alfa. Base de los rellenos metálicos. */
private fun Color.eldoriaShade(factor: Float): Color = Color(
    red = (red * factor).coerceIn(0f, 1f),
    green = (green * factor).coerceIn(0f, 1f),
    blue = (blue * factor).coerceIn(0f, 1f),
    alpha = alpha
)

@Composable
fun EldoriaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tone: EldoriaTone = EldoriaTone.Gold,
    size: EldoriaButtonSize = EldoriaButtonSize.Medium,
    icon: ImageVector? = null,
    fullWidth: Boolean = false,
    loading: Boolean = false,
    testTag: String? = null
) {
    val active = enabled && !loading
    val barHeight = when (size) {
        EldoriaButtonSize.Small -> 36.dp
        EldoriaButtonSize.Medium -> 46.dp
        EldoriaButtonSize.Large -> 56.dp
    }
    val hPad = when (size) {
        EldoriaButtonSize.Small -> 14.dp
        EldoriaButtonSize.Medium -> 20.dp
        EldoriaButtonSize.Large -> 26.dp
    }
    val shape = RoundedCornerShape(8.dp)
    val base = Eldoria.toneColor(tone)

    val fill = if (active) {
        Brush.verticalGradient(
            listOf(base.eldoriaShade(1.15f), base, base.eldoriaShade(0.65f))
        )
    } else {
        Brush.verticalGradient(
            listOf(Eldoria.IronDeep, Eldoria.IronDeep.eldoriaShade(0.85f), Eldoria.IronDeep.eldoriaShade(0.6f))
        )
    }
    val borderBrush = if (active) EldoriaEdge.tone(tone).brush() else SolidColor(Eldoria.Iron)
    val labelColor = when {
        !active -> Eldoria.TextLow
        tone == EldoriaTone.Gold || tone == EldoriaTone.Silver -> Eldoria.TextOnGold
        else -> Eldoria.TextHi
    }
    val textStyle = if (size == EldoriaButtonSize.Small) EldoriaType.buttonSmall else EldoriaType.button

    Box(
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(barHeight)
            .then(
                if (active) Modifier.eldoriaGlowLayer(base.copy(alpha = 0.5f), alpha = 0.18f, corner = 8.dp, spread = 5.dp)
                else Modifier
            )
            .clip(shape)
            .background(fill)
            .then(if (active) Modifier.eldoriaPressable(onClick = onClick) else Modifier)
            .border(Eldoria.StrokeMed, borderBrush, shape)
            .eldoriaBevel(
                corner = 8.dp,
                light = Color.White.copy(alpha = if (active) 0.12f else 0.04f),
                dark = Color.Black.copy(alpha = 0.40f)
            )
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // brillo especular del tercio superior
        if (active) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = this.size.width
                val h = this.size.height
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
                        startY = 0f,
                        endY = h * 0.42f
                    ),
                    size = Size(w, h * 0.42f)
                )
            }
        }

        if (loading) {
            EldoriaSpinner(
                modifier = Modifier.size(if (size == EldoriaButtonSize.Small) 16.dp else 20.dp),
                color = labelColor
            )
        } else {
            Row(
                modifier = Modifier.padding(horizontal = hPad),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = labelColor,
                        modifier = Modifier.size(if (size == EldoriaButtonSize.Small) 15.dp else 18.dp)
                    )
                    Spacer(Modifier.width(Eldoria.S6))
                }
                Text(
                    text = text,
                    style = textStyle,
                    color = labelColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Ruleta de carga dibujada a mano (prohibido LinearProgressIndicator con Float). */
@Composable
private fun EldoriaSpinner(modifier: Modifier = Modifier, color: Color = Eldoria.Gold) {
    val transition = rememberInfiniteTransition(label = "spinner")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinnerAngle"
    )
    Canvas(modifier) {
        if (size.minDimension <= 0f) return@Canvas
        val sw = size.minDimension * 0.16f
        drawArc(
            color = color.copy(alpha = 0.22f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(sw / 2f, sw / 2f),
            size = Size(size.width - sw, size.height - sw),
            style = Stroke(width = sw, cap = StrokeCap.Round)
        )
        drawArc(
            color = color,
            startAngle = angle,
            sweepAngle = 96f,
            useCenter = false,
            topLeft = Offset(sw / 2f, sw / 2f),
            size = Size(size.width - sw, size.height - sw),
            style = Stroke(width = sw, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun EldoriaIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: EldoriaTone = EldoriaTone.Iron,
    size: Dp = 42.dp,
    enabled: Boolean = true,
    badge: String? = null,
    testTag: String? = null
) {
    val shape = CutCornerShape(7.dp)
    val base = Eldoria.toneColor(tone)
    val fill = if (enabled) {
        Brush.verticalGradient(listOf(base.eldoriaShade(0.85f), base.eldoriaShade(0.55f), base.eldoriaShade(0.35f)))
    } else {
        Brush.verticalGradient(listOf(Eldoria.IronDeep, Eldoria.IronDeep.eldoriaShade(0.7f)))
    }
    val tint = if (enabled) {
        if (tone == EldoriaTone.Gold) Eldoria.GoldBright else Eldoria.TextHi
    } else Eldoria.TextLow

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(fill)
                .then(if (enabled) Modifier.eldoriaPressable(onClick = onClick) else Modifier)
                .border(Eldoria.StrokeThin, if (enabled) EldoriaEdge.tone(tone).brush() else SolidColor(Eldoria.Iron), shape)
                .eldoriaBevel(corner = 7.dp)
                .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(size * 0.48f)
            )
        }
        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Eldoria.Blood)
                    .border(1.dp, Eldoria.BloodBright.copy(alpha = 0.8f), RoundedCornerShape(9.dp))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge,
                    style = EldoriaType.caption,
                    color = Eldoria.TextHi,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun EldoriaChip(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Eldoria.Gold,
    icon: ImageVector? = null,
    filled: Boolean = false
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .clip(shape)
            .background(
                if (filled) Brush.verticalGradient(
                    listOf(color.copy(alpha = 0.34f), color.copy(alpha = 0.14f))
                ) else Brush.verticalGradient(
                    listOf(Eldoria.PanelHi.copy(alpha = 0.9f), Eldoria.PanelSunken.copy(alpha = 0.9f))
                )
            )
            .border(Eldoria.StrokeThin, color.copy(alpha = if (filled) 0.9f else 0.55f), shape)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Spacer(Modifier.width(Eldoria.S4))
        }
        Text(
            text = text,
            style = EldoriaType.caption,
            color = if (filled) Eldoria.TextHi else color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EldoriaToggleChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Eldoria.Gold,
    icon: ImageVector? = null,
    testTag: String? = null
) {
    val shape = RoundedCornerShape(50)
    val fill = if (selected) {
        Brush.verticalGradient(listOf(accent.eldoriaShade(1.05f), accent.eldoriaShade(0.62f)))
    } else {
        Brush.verticalGradient(listOf(Eldoria.PanelHi, Eldoria.PanelSunken))
    }
    Row(
        modifier = modifier
            .then(if (selected) Modifier.eldoriaGlowLayer(accent.copy(alpha = 0.5f), alpha = 0.22f, corner = 20.dp, spread = 5.dp) else Modifier)
            .clip(shape)
            .background(fill)
            .eldoriaPressable(onClick = onClick)
            .border(
                if (selected) Eldoria.StrokeMed else Eldoria.StrokeThin,
                if (selected) SolidColor(accent.eldoriaShade(1.3f)) else SolidColor(Eldoria.Iron),
                shape
            )
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Eldoria.TextOnGold else Eldoria.TextMid,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(Eldoria.S4))
        }
        Text(
            text = text,
            style = EldoriaType.label,
            color = if (selected) Eldoria.TextOnGold else Eldoria.TextMid,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Barra animada (animateFloatAsState 420ms EaseOutCubic), con brillo superior y numerales Monospace. */
@Composable
fun EldoriaResourceBar(
    current: Int,
    max: Int,
    tone: EldoriaBarTone,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: ImageVector? = null,
    height: Dp = 14.dp,
    showNumbers: Boolean = true,
    animated: Boolean = true,
    dangerPulse: Boolean = false
) {
    val safeMax = if (max <= 0) 1 else max
    val ratio = (current.toFloat() / safeMax.toFloat()).coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(durationMillis = 420, easing = EldoriaMotion.easeOut),
        label = "eldoriaBar"
    )
    val shown = if (animated) animatedRatio else ratio
    val low = ratio <= 0.28f
    val pulse = if (dangerPulse && low) eldoriaPulse(periodMs = 720, from = 0.45f, to = 1f, label = "barDanger") else 1f

    val (bright, mid, deep) = Eldoria.barColors(tone)
    val hasHeader = label != null || icon != null
    val barShape = RoundedCornerShape(3.dp)

    Column(modifier = modifier.fillMaxWidth()) {
        if (hasHeader) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = mid,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(Eldoria.S4))
                }
                if (label != null) {
                    Text(
                        text = label,
                        style = EldoriaType.caption,
                        color = Eldoria.TextMid,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.width(Eldoria.S8))
                if (showNumbers) {
                    Text(
                        text = "${formatGameNumber(current)} / ${formatGameNumber(max)}",
                        style = EldoriaType.caption,
                        color = if (low) bright else Eldoria.TextMid,
                        maxLines = 1
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (hasHeader || !showNumbers) height else height.coerceAtLeast(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(barShape)
            ) {
                val w = size.width
                val h = size.height
                if (w <= 0f || h <= 0f) return@Canvas

                // canal hundido
                drawRect(
                    brush = Brush.verticalGradient(listOf(Eldoria.Abyss, Eldoria.PanelSunken)),
                    size = Size(w, h)
                )
                // relleno
                val fw = w * shown
                if (fw > 0.5f) {
                    val a = if (dangerPulse && low) (0.55f + 0.45f * pulse).coerceIn(0f, 1f) else 1f
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(
                                bright.copy(alpha = a),
                                mid.copy(alpha = a),
                                deep.copy(alpha = a)
                            ),
                            startY = 0f,
                            endY = h
                        ),
                        size = Size(fw, h)
                    )
                    // brillo especular en el tercio superior
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.34f), Color.White.copy(alpha = 0.02f)),
                            startY = 0f,
                            endY = h * 0.34f
                        ),
                        size = Size(fw, h * 0.34f)
                    )
                    // filo brillante en la punta del relleno
                    drawLine(
                        color = bright.copy(alpha = 0.9f * a),
                        start = Offset(fw - 0.75f, 0f),
                        end = Offset(fw - 0.75f, h),
                        strokeWidth = 1.5f
                    )
                }
                // marco de hierro
                drawRect(
                    color = Eldoria.IronEdge.copy(alpha = 0.85f),
                    size = Size(w, h),
                    style = Stroke(width = 1f)
                )
                drawLine(
                    color = Color.Black.copy(alpha = 0.55f),
                    start = Offset(0f, h - 0.5f),
                    end = Offset(w, h - 0.5f),
                    strokeWidth = 1f
                )
            }

            if (showNumbers && !hasHeader) {
                Text(
                    text = "${formatGameNumber(current)} / ${formatGameNumber(max)}",
                    style = EldoriaType.caption,
                    color = Eldoria.TextHi,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun EldoriaSegmentedTabs(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Eldoria.Gold,
    testTagPrefix: String? = null
) {
    if (options.isEmpty()) return
    val shape = CutCornerShape(8.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Eldoria.sunkenBrush())
            .border(Eldoria.StrokeThin, Eldoria.ironEdge(), shape)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(CutCornerShape(6.dp))
                    .then(
                        if (selected) Modifier.background(
                            Brush.verticalGradient(
                                listOf(
                                    accent.copy(alpha = 0.32f),
                                    accent.copy(alpha = 0.10f)
                                )
                            )
                        ) else Modifier
                    )
                    .eldoriaPressable(onClick = { onSelect(index) })
                    .then(
                        if (testTagPrefix != null) Modifier.testTag("$testTagPrefix$index") else Modifier
                    )
                    .drawBehind {
                        if (selected) {
                            val y = size.height - 1.5f
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    listOf(Color.Transparent, accent, Color.Transparent)
                                ),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 2.5f
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = EldoriaType.label,
                    color = if (selected) accent else Eldoria.TextLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun EldoriaStatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accent: Color = Eldoria.Gold
) {
    val shape = CutCornerShape(6.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Eldoria.PanelHi, Eldoria.PanelSunken)))
            .border(Eldoria.StrokeThin, accent.copy(alpha = 0.45f), shape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(Eldoria.S6))
        }
        Column {
            Text(
                text = label.uppercase(),
                style = EldoriaType.caption,
                color = Eldoria.TextLow,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = EldoriaType.numeric,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EldoriaRarityGem(rarity: String, modifier: Modifier = Modifier, size: Dp = 14.dp) {
    val c = Eldoria.rarityColor(rarity)
    Canvas(modifier = modifier.size(size)) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val r = min(this.size.width, this.size.height) / 2f
        drawCircle(
            brush = Brush.radialGradient(
                0f to c.copy(alpha = 0.55f),
                1f to Color.Transparent,
                center = Offset(cx, cy),
                radius = r * 1.15f
            ),
            radius = r * 1.15f,
            center = Offset(cx, cy)
        )
        drawPath(eldoriaDiamondPath(cx, cy, r * 0.92f), Color.Black.copy(alpha = 0.75f))
        drawPath(
            eldoriaDiamondPath(cx, cy, r * 0.78f),
            brush = Brush.verticalGradient(
                listOf(Color.White.copy(alpha = 0.75f).compositeOver(c), c, c.eldoriaShade(0.5f))
            )
        )
        // chispa especular
        drawPath(
            eldoriaDiamondPath(cx - r * 0.16f, cy - r * 0.20f, r * 0.22f),
            Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun EldoriaProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    stroke: Dp = 6.dp,
    accent: Color = Eldoria.Gold,
    trackColor: Color = Eldoria.IronDeep,
    centerLabel: String? = null
) {
    val target = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 520, easing = EldoriaMotion.easeOut),
        label = "eldoriaRing"
    )
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sw = stroke.toPx()
            val inset = sw / 2f
            val arcSize = Size(this.size.width - sw, this.size.height - sw)
            if (arcSize.width <= 0f || arcSize.height <= 0f) return@Canvas
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = sw)
            )
            if (animated > 0.001f) {
                drawArc(
                    color = accent.copy(alpha = 0.22f),
                    startAngle = -90f,
                    sweepAngle = 360f * animated,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = sw * 2.1f, cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(accent.eldoriaShade(0.6f), accent, Color.White.copy(alpha = 0.6f).compositeOver(accent), accent.eldoriaShade(0.6f)),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * animated,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = sw, cap = StrokeCap.Round)
                )
            }
        }
        if (centerLabel != null) {
            Text(
                text = centerLabel,
                style = EldoriaType.numeric,
                color = accent,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Contador con count-up animado y formato K/M/G. */
@Composable
fun EldoriaCounter(
    value: Long,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accent: Color = Eldoria.TextGold,
    prefix: String = "",
    animate: Boolean = true
) {
    val shown = if (animate) rememberEldoriaCountUp(value) else value
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(Eldoria.S4))
        }
        Text(
            text = prefix + formatGameNumber(shown),
            style = EldoriaType.numeric,
            color = accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Slot de equipo/mascota: borde por rareza, chip "Niv.N", gema de rareza, silueta vacía. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EldoriaSlotFrame(
    modifier: Modifier = Modifier,
    rarity: String? = null,
    level: Int? = null,
    size: Dp = 66.dp,
    emptyIcon: ImageVector? = null,
    emptyLabel: String? = null,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val edge = if (rarity != null) EldoriaEdge.rarity(rarity) else EldoriaEdge.Iron
    val shape = CutCornerShape(7.dp)
    val clickable = onClick != null || onLongClick != null
    val longClick = onLongClick

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (selected) Modifier.eldoriaGlowLayer(edge.glow, alpha = 0.45f, corner = 7.dp, spread = 6.dp)
                else Modifier
            )
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Eldoria.PanelSunken, Eldoria.Abyss)))
            .then(
                if (clickable) Modifier.combinedClickable(
                    onLongClick = if (longClick != null) {
                        { longClick() }
                    } else null,
                    onClick = {
                        SoundManager.playButtonClick()
                        onClick?.invoke()
                    }
                ) else Modifier
            )
            .border(
                if (selected) Eldoria.StrokeBold else Eldoria.StrokeMed,
                edge.brush(),
                shape
            )
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // silueta vacía por debajo del contenido
        if (emptyIcon != null || emptyLabel != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (emptyIcon != null) {
                    Icon(
                        imageVector = emptyIcon,
                        contentDescription = null,
                        tint = Eldoria.Iron,
                        modifier = Modifier.size(size * 0.36f)
                    )
                }
                if (emptyLabel != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = emptyLabel,
                        style = EldoriaType.caption,
                        color = Eldoria.TextLow,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }

        content()

        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.06f), Color.Transparent),
                    startY = 0f,
                    endY = this.size.height * 0.35f
                ),
                size = Size(this.size.width, this.size.height * 0.35f)
            )
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)),
                    startY = this.size.height * 0.55f,
                    endY = this.size.height
                )
            )
        }

        if (rarity != null) {
            EldoriaRarityGem(
                rarity = rarity,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp),
                size = (size * 0.20f).coerceAtLeast(10.dp)
            )
        }

        if (level != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Eldoria.Abyss.copy(alpha = 0.85f))
                    .border(0.75.dp, edge.mid.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "Niv.$level",
                    style = EldoriaType.caption,
                    color = edge.top,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun EldoriaConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissLabel: String = "Cancelar",
    tone: EldoriaTone = EldoriaTone.Gold,
    testTagPrefix: String? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Eldoria.Scrim)
                .pointerInput(Unit) { detectTapGestures { onDismiss() } },
            contentAlignment = Alignment.Center
        ) {
            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .widthIn(max = 420.dp)
                    .pointerInput(Unit) { detectTapGestures { } },
                edge = EldoriaEdge.tone(tone),
                corner = Eldoria.R12,
                padding = PaddingValues(18.dp),
                glow = true,
                filigree = true,
                testTag = if (testTagPrefix != null) "${testTagPrefix}Panel" else null
            ) {
                Text(
                    text = title,
                    style = EldoriaType.title,
                    color = Eldoria.TextGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Eldoria.S8))
                EldoriaDivider(color = Eldoria.toneColor(tone))
                Spacer(Modifier.height(Eldoria.S8))
                Text(
                    text = message,
                    style = EldoriaType.body,
                    color = Eldoria.TextMid,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Eldoria.S20))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                ) {
                    EldoriaButton(
                        text = dismissLabel,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        tone = EldoriaTone.Iron,
                        size = EldoriaButtonSize.Medium,
                        testTag = if (testTagPrefix != null) "${testTagPrefix}Dismiss" else null
                    )
                    EldoriaButton(
                        text = confirmLabel,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        tone = tone,
                        size = EldoriaButtonSize.Medium,
                        testTag = if (testTagPrefix != null) "${testTagPrefix}Confirm" else null
                    )
                }
            }
        }
    }
}

/** Hoja modal inferior (Dialog usePlatformDefaultWidth=false, alineada abajo). */
@Composable
fun EldoriaSheet(
    visible: Boolean,
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    edge: EldoriaEdge = EldoriaEdge.Gold,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!visible) return
    val shape = RoundedCornerShape(topStart = Eldoria.R28, topEnd = Eldoria.R28)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Eldoria.ScrimSoft)
                .pointerInput(Unit) { detectTapGestures { onDismiss() } },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) { detectTapGestures { } }
                    .clip(shape)
                    .background(Eldoria.panelBrush())
                    .border(Eldoria.StrokeMed, edge.brush(), shape)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(46.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Eldoria.IronEdge)
                )
                Spacer(Modifier.height(Eldoria.S12))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = EldoriaType.title,
                        color = Eldoria.TextGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    EldoriaIconButton(
                        icon = Icons.Filled.Close,
                        contentDescription = "Cerrar",
                        onClick = onDismiss,
                        tone = EldoriaTone.Iron,
                        size = 34.dp
                    )
                }
                Spacer(Modifier.height(Eldoria.S6))
                EldoriaDivider(color = edge.mid)
                Spacer(Modifier.height(Eldoria.S12))
                content()
                Spacer(Modifier.height(Eldoria.S8))
            }
        }
    }
}

/** Aviso no bloqueante (banda superior). El host lo posiciona. */
@Composable
fun EldoriaToastCard(
    message: String,
    tone: EldoriaTone,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val accent = Eldoria.toneColor(tone)
    val shape = CutCornerShape(8.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .eldoriaGlowLayer(accent.copy(alpha = 0.4f), alpha = 0.20f, corner = 8.dp, spread = 6.dp)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Eldoria.PanelHi, Eldoria.Ink)))
            .border(Eldoria.StrokeMed, EldoriaEdge.tone(tone).brush(), shape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(26.dp)
                .background(Brush.verticalGradient(listOf(accent, accent.eldoriaShade(0.4f))))
        )
        Spacer(Modifier.width(Eldoria.S8))
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(Eldoria.S8))
        }
        Text(
            text = message,
            style = EldoriaType.bodyStrong,
            color = Eldoria.TextHi,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Tarjeta de objeto reutilizable en tienda/inventario/botín/forja. */
@Composable
fun EldoriaItemCard(
    name: String,
    rarity: String,
    level: Int,
    stats: String,
    imageRes: Int,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    testTag: String? = null
) {
    val edge = EldoriaEdge.rarity(rarity)
    val rarityColor = Eldoria.rarityColor(rarity)
    val shape = CutCornerShape(9.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(rarityColor.copy(alpha = 0.10f), Eldoria.Panel, Eldoria.PanelSunken)))
            .then(if (onClick != null) Modifier.eldoriaPressable(onClick = onClick) else Modifier)
            .border(Eldoria.StrokeThin, edge.brush(), shape)
            .padding(9.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EldoriaSlotFrame(
            rarity = rarity,
            level = level,
            size = 58.dp
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.width(Eldoria.S12))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = EldoriaType.subheading,
                color = rarityColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(3.dp))
            Text(
                text = stats,
                style = EldoriaType.small,
                color = Eldoria.TextMid,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (trailing != null) {
            Spacer(Modifier.width(Eldoria.S8))
            trailing()
        }
    }
}
