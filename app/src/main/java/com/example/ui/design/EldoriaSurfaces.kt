package com.example.ui.design

import androidx.compose.foundation.shape.CircleShape
import com.example.ui.art.EldoriaArt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min
import kotlin.random.Random

// ──────────────────────────────────────────────────────────────────────────────
//  Superficies de Eldoria: fondos, paneles, marcos, banners, divisores, títulos.
//  Todo metal es un gradiente vertical real (brillo arriba, sombra abajo) con
//  bisel interno; nada de bordes de color plano.
// ──────────────────────────────────────────────────────────────────────────────

/** Esquina biselada (octogonal) — la firma visual de los paneles del juego. */
private fun eldoriaBevelShape(corner: Dp): CutCornerShape = CutCornerShape(corner)

/** Fondo de pantalla completo: gradiente + viñeta + (opcional) brasas + (opcional) niebla. NO aplica insets. */
@Composable
fun EldoriaScreen(
    modifier: Modifier = Modifier,
    depth: Int = 0,
    embers: Boolean = false,
    fog: Boolean = false,
    vignetteStrength: Float = 0.55f,
    backgroundArtRes: Int? = null,
    backgroundArtAlpha: Float = 0.22f,
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Eldoria.depthBrush(depth))
    ) {
        if (backgroundArtRes != null) {
            Image(
                painter = painterResource(id = backgroundArtRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = backgroundArtAlpha.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Eldoria.Abyss.copy(alpha = 0.88f),
                                Eldoria.Ink.copy(alpha = 0.52f),
                                Eldoria.Abyss.copy(alpha = 0.92f)
                            )
                        )
                    )
            )
        }

        if (fog) {
            EldoriaFogLayer(
                modifier = Modifier.fillMaxSize(),
                tint = if (depth >= 2) Eldoria.EmberDeep else Eldoria.Iron,
                alpha = if (depth >= 2) 0.13f else 0.16f
            )
        }
        if (embers) {
            EldoriaEmberField(
                modifier = Modifier.fillMaxSize(),
                count = if (depth >= 2) 26 else 18,
                tint = Eldoria.Ember,
                seed = 7 + depth
            )
        }
        if (vignetteStrength > 0.001f) {
            EldoriaVignette(
                modifier = Modifier.fillMaxSize(),
                strength = vignetteStrength.coerceIn(0f, 1f),
                centerBiasY = 0.44f
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            content = content
        )
    }
}

/** Panel de contenido con borde metálico de 1.5dp, esquinas biseladas y sombra interna. */
@Composable
fun EldoriaPanel(
    modifier: Modifier = Modifier,
    edge: EldoriaEdge = EldoriaEdge.Gold,
    corner: Dp = Eldoria.R12,
    padding: PaddingValues = PaddingValues(14.dp),
    background: Brush = Eldoria.panelBrush(),
    glow: Boolean = false,
    filigree: Boolean = false,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = eldoriaBevelShape(corner)
    Box(
        modifier = modifier
            .then(if (glow) Modifier.eldoriaGlowLayer(edge.glow, alpha = 0.34f, corner = corner, spread = 7.dp) else Modifier)
            .clip(shape)
            .background(background)
            .drawBehind {
                // sombra interna: el panel se hunde hacia abajo
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.38f)),
                        startY = size.height * 0.45f,
                        endY = size.height
                    )
                )
                // veta de luz superior
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent),
                        startY = 0f,
                        endY = size.height * 0.28f
                    )
                )
            }
            .then(if (onClick != null) Modifier.eldoriaPressable(onClick) else Modifier)
            .border(Eldoria.StrokeMed, edge.brush(), shape)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            content = content
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            drawEldoriaInnerBevel(corner.toPx())
        }
        if (filigree) {
            EldoriaFiligreeCorners(
                modifier = Modifier.matchParentSize(),
                color = edge.mid,
                inset = 4.dp,
                armLength = 18.dp,
                strokeWidth = 1.2.dp
            )
        }
    }
}

/** Marco decorativo sin layout de columna: para retratos, arte, mapas. */
@Composable
fun EldoriaFrame(
    modifier: Modifier = Modifier,
    edge: EldoriaEdge = EldoriaEdge.Gold,
    corner: Dp = Eldoria.R16,
    strokeWidth: Dp = Eldoria.StrokeBold,
    filigree: Boolean = true,
    rivets: Boolean = false,
    glowPulse: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = eldoriaBevelShape(corner)
    val pulse = if (glowPulse) eldoriaPulse(periodMs = 2000, from = 0.30f, to = 1f, label = "frameGlow") else 0f

    Box(
        modifier = modifier
            .drawBehind {
                if (pulse > 0.01f) {
                    val grow = 6.dp.toPx()
                    for (k in 3 downTo 1) {
                        val g = grow * k / 3f
                        drawRoundRect(
                            color = edge.glow.copy(alpha = (0.30f * pulse * (1f - k / 4f)).coerceIn(0f, 1f)),
                            topLeft = Offset(-g, -g),
                            size = Size(size.width + g * 2f, size.height + g * 2f)
                        )
                    }
                }
            }
            .clip(shape)
            .background(Eldoria.sunkenBrush())
    ) {
        content()

        Canvas(modifier = Modifier.matchParentSize()) {
            val sw = strokeWidth.toPx()
            val c = corner.toPx()

            // 1. Sombra exterior del filo (da grosor al metal)
            drawEldoriaBevelOutline(c, sw * 1.7f, SolidColor(Color.Black.copy(alpha = 0.55f)))
            // 2. Filo metálico con gradiente vertical real
            drawEldoriaBevelOutline(c, sw, edge.brush())
            // 3. Bisel interno claro arriba / sombra abajo
            drawEldoriaInnerBevel(c)

            if (rivets) {
                val rr = (sw * 1.25f).coerceIn(1.5f, 7f)
                val m = c * 0.55f + rr * 1.6f
                val pts = listOf(
                    Offset(m, m),
                    Offset(size.width - m, m),
                    Offset(m, size.height - m),
                    Offset(size.width - m, size.height - m)
                )
                pts.forEach { o ->
                    drawCircle(Color.Black.copy(alpha = 0.6f), radius = rr * 1.3f, center = o)
                    drawCircle(
                        brush = Brush.radialGradient(
                            0f to Color.White.copy(alpha = 0.8f).compositeOver(edge.mid),
                            1f to edge.bottom,
                            center = Offset(o.x - rr * 0.35f, o.y - rr * 0.35f),
                            radius = rr * 1.6f
                        ),
                        radius = rr,
                        center = o
                    )
                }
            }
        }

        if (filigree) {
            EldoriaFiligreeCorners(
                modifier = Modifier.matchParentSize(),
                color = edge.top,
                inset = 5.dp,
                armLength = 22.dp,
                strokeWidth = 1.4.dp
            )
        }
    }
}

/** Cabecera de pantalla: arte JPG existente + scrim + título serif + subtítulo + acción a la derecha. */
@Composable
fun EldoriaBanner(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    artRes: Int? = null,
    height: Dp = 128.dp,
    edge: EldoriaEdge = EldoriaEdge.Gold,
    crestSeed: Int? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null
) {
    val shape = eldoriaBevelShape(14.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(Eldoria.sunkenBrush())
            .border(Eldoria.StrokeMed, edge.brush(), shape)
    ) {
        if (artRes != null) {
            Image(
                painter = painterResource(id = artRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.62f,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Eldoria.Abyss.copy(alpha = 0.55f),
                            Eldoria.Abyss.copy(alpha = 0.30f),
                            Eldoria.Abyss.copy(alpha = 0.92f)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (crestSeed != null) {
                EldoriaCrest(
                    seed = crestSeed,
                    modifier = Modifier
                        .size(width = (height * 0.44f), height = (height * 0.52f)),
                    primary = edge.top,
                    secondary = Eldoria.IronDeep,
                    ornate = true
                )
                Spacer(Modifier.width(Eldoria.S12))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = EldoriaType.title,
                    color = Eldoria.TextGold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(Eldoria.S4))
                    Text(
                        text = subtitle,
                        style = EldoriaType.small,
                        color = Eldoria.TextMid,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (trailing != null) {
                Spacer(Modifier.width(Eldoria.S8))
                trailing()
            }
        }

        // filo dorado inferior
        Canvas(
            modifier = Modifier
                .matchParentSize()
        ) {
            val y = size.height - 1.5f
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, edge.mid.copy(alpha = 0.9f), Color.Transparent)
                ),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 3f
            )
        }
    }
}

/** Regla ornamentada con rombo central (sustituye a OrnateDivider en pantallas nuevas). */
@Composable
fun EldoriaDivider(
    modifier: Modifier = Modifier,
    color: Color = Eldoria.Gold,
    ornament: Boolean = true,
    thickness: Dp = Eldoria.StrokeThin
) {
    val h = if (ornament) 14.dp else thickness.coerceAtLeast(1.dp)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(h)
    ) {
        if (size.width <= 0f) return@Canvas
        val y = size.height / 2f
        val t = thickness.toPx().coerceAtLeast(0.75f)
        val gap = if (ornament) 11.dp.toPx() else 0f
        val half = size.width / 2f
        val leftEnd = (half - gap).coerceAtLeast(0f)
        val rightStart = (half + gap).coerceAtMost(size.width)

        if (leftEnd > 0f) {
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, color.copy(alpha = 0.85f)),
                    startX = 0f,
                    endX = leftEnd
                ),
                start = Offset(0f, y),
                end = Offset(leftEnd, y),
                strokeWidth = t,
                cap = StrokeCap.Round
            )
        }
        if (rightStart < size.width) {
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(color.copy(alpha = 0.85f), Color.Transparent),
                    startX = rightStart,
                    endX = size.width
                ),
                start = Offset(rightStart, y),
                end = Offset(size.width, y),
                strokeWidth = t,
                cap = StrokeCap.Round
            )
        }

        if (ornament) {
            val r = 5.dp.toPx()
            drawPath(eldoriaDiamondPath(half, y, r * 1.7f), color.copy(alpha = 0.16f))
            drawPath(eldoriaDiamondPath(half, y, r), color.copy(alpha = 0.95f))
            drawPath(eldoriaDiamondPath(half, y, r * 0.42f), Eldoria.Abyss.copy(alpha = 0.85f))
            drawLine(color.copy(alpha = 0.5f), Offset(half - r * 2.6f, y), Offset(half - r * 1.7f, y), t)
            drawLine(color.copy(alpha = 0.5f), Offset(half + r * 1.7f, y), Offset(half + r * 2.6f, y), t)
        }
    }
}

@Composable
fun EldoriaSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accent: Color = Eldoria.Gold,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
        ) {
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.95f), accent.copy(alpha = 0.15f))
                )
            )
        }
        Spacer(Modifier.width(Eldoria.S8))
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(17.dp)
            )
            Spacer(Modifier.width(Eldoria.S6))
        }
        Text(
            text = text,
            style = EldoriaType.heading,
            color = Eldoria.TextHi,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(Eldoria.S8))
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
        ) {
            if (size.width <= 1f) return@Canvas
            val y = size.height / 2f
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(accent.copy(alpha = 0.55f), Color.Transparent),
                    startX = 0f,
                    endX = size.width
                ),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        if (trailing != null) {
            Spacer(Modifier.width(Eldoria.S8))
            trailing()
        }
    }
}

/** Pergamino: fondo Parchment, texto ParchmentInk, bordes rasgados dibujados con Path. */
@Composable
fun EldoriaScrollSheet(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.matchParentSize()) {
            if (size.minDimension <= 0f) return@Canvas
            val rnd = Random(20250814)
            val teeth = 14
            val amp = 5.dp.toPx()
            val path = Path()
            path.moveTo(0f, amp)
            for (i in 1..teeth) {
                val x = size.width * i / teeth
                val y = amp + (rnd.nextFloat() - 0.5f) * amp * 1.6f
                path.lineTo(x, y)
            }
            path.lineTo(size.width, size.height - amp)
            for (i in teeth - 1 downTo 0) {
                val x = size.width * i / teeth
                val y = size.height - amp + (rnd.nextFloat() - 0.5f) * amp * 1.6f
                path.lineTo(x, y)
            }
            path.close()

            drawPath(
                path,
                brush = Brush.verticalGradient(
                    listOf(Eldoria.Parchment, Eldoria.ParchmentDim, Eldoria.Parchment)
                )
            )
            // manchas de té deterministas
            val stainRnd = Random(77)
            repeat(5) {
                val cx = stainRnd.nextFloat() * size.width
                val cy = stainRnd.nextFloat() * size.height
                val r = (0.06f + stainRnd.nextFloat() * 0.10f) * min(size.width, size.height)
                drawCircle(
                    brush = Brush.radialGradient(
                        0f to Eldoria.ParchmentInk.copy(alpha = 0.07f),
                        1f to Color.Transparent,
                        center = Offset(cx, cy),
                        radius = r
                    ),
                    radius = r,
                    center = Offset(cx, cy)
                )
            }
            drawPath(
                path,
                color = Eldoria.ParchmentInk.copy(alpha = 0.35f),
                style = Stroke(width = 1.2.dp.toPx(), join = StrokeJoin.Round)
            )
        }

        CompositionLocalProvider(LocalContentColor provides Eldoria.ParchmentInk) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                content = content
            )
        }
    }
}

@Composable
fun EldoriaEmptyState(
    title: String,
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    /**
     * Lámina para el hueco. El estado vacío era un círculo dibujado a mano con
     * un icono de Material dentro — correcto pero mudo, y el único sitio del
     * juego donde no había arte. Con [artKey] se resuelve una ilustración; sin
     * él se mantiene el icono, que sigue siendo el respaldo si falta el recurso.
     */
    artKey: String? = "empty_generic",
    accent: Color = Eldoria.Gold,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    testTag: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Eldoria.S24, vertical = Eldoria.S32)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val artRes = artKey?.let { EldoriaArt.of(it) }
        Box(
            modifier = Modifier.size(if (artRes != null) 132.dp else 84.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val r = size.minDimension / 2f
                drawCircle(
                    brush = Brush.radialGradient(
                        0f to accent.copy(alpha = 0.14f),
                        1f to Color.Transparent,
                        center = center,
                        radius = r
                    ),
                    radius = r,
                    center = center
                )
                drawCircle(
                    color = accent.copy(alpha = 0.42f),
                    radius = r * 0.74f,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )
                drawPath(eldoriaDiamondPath(center.x, center.y - r * 0.74f, 3.5.dp.toPx()), accent.copy(alpha = 0.8f))
                drawPath(eldoriaDiamondPath(center.x, center.y + r * 0.74f, 3.5.dp.toPx()), accent.copy(alpha = 0.8f))
            }
            if (artRes != null) {
                Image(
                    painter = painterResource(id = artRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent.copy(alpha = 0.85f),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(Modifier.height(Eldoria.S16))
        Text(
            text = title,
            style = EldoriaType.title,
            color = Eldoria.TextHi,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Eldoria.S8))
        Text(
            text = message,
            style = EldoriaType.body,
            color = Eldoria.TextMid,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(Eldoria.S20))
            EldoriaButton(
                text = actionLabel,
                onClick = onAction,
                tone = EldoriaTone.Gold,
                size = EldoriaButtonSize.Medium
            )
        }
    }
}

/** Fila de estadística clave/valor con icono. */
@Composable
fun EldoriaKeyValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueColor: Color = Eldoria.TextGold
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Eldoria.S4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Eldoria.TextLow,
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(Eldoria.S6))
        }
        Text(
            text = label,
            style = EldoriaType.small,
            color = Eldoria.TextMid,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(Eldoria.S8))
        Text(
            text = value,
            style = EldoriaType.numeric,
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ───────────────────────── helpers de dibujo (file-private) ───────────────────

/** Contorno octogonal (esquinas biseladas) pintado con un Brush. */
private fun DrawScope.drawEldoriaBevelOutline(
    cut: Float,
    strokeWidth: Float,
    brush: Brush
) {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return
    val c = cut.coerceAtMost(min(w, h) / 2f)
    val i = strokeWidth / 2f
    val p = Path()
    p.moveTo(c + i, i)
    p.lineTo(w - c - i, i)
    p.lineTo(w - i, c + i)
    p.lineTo(w - i, h - c - i)
    p.lineTo(w - c - i, h - i)
    p.lineTo(c + i, h - i)
    p.lineTo(i, h - c - i)
    p.lineTo(i, c + i)
    p.close()
    drawPath(p, brush = brush, style = Stroke(width = strokeWidth, join = StrokeJoin.Miter))
}

/** Bisel interno: filo claro pegado al borde superior, sombra pegada al inferior. */
private fun DrawScope.drawEldoriaInnerBevel(cut: Float) {
    val w = size.width
    val h = size.height
    if (w <= 4f || h <= 4f) return
    val c = cut.coerceAtMost(min(w, h) / 2f)
    val t = 1.2f
    drawLine(
        brush = Brush.horizontalGradient(
            listOf(Color.Transparent, Color.White.copy(alpha = 0.16f), Color.Transparent)
        ),
        start = Offset(c + 2f, 2.5f),
        end = Offset(w - c - 2f, 2.5f),
        strokeWidth = t
    )
    drawLine(
        color = Color.White.copy(alpha = 0.07f),
        start = Offset(2.5f, c + 2f),
        end = Offset(2.5f, h - c - 2f),
        strokeWidth = t
    )
    drawLine(
        brush = Brush.horizontalGradient(
            listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f), Color.Transparent)
        ),
        start = Offset(c + 2f, h - 2.5f),
        end = Offset(w - c - 2f, h - 2.5f),
        strokeWidth = t * 1.4f
    )
    drawLine(
        color = Color.Black.copy(alpha = 0.35f),
        start = Offset(w - 2.5f, c + 2f),
        end = Offset(w - 2.5f, h - c - 2f),
        strokeWidth = t
    )
}
