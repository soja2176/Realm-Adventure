package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import com.example.data.content.EldoriaExpeditions
import com.example.ui.art.EldoriaArt
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBarTone
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaResourceBar
import com.example.ui.design.EldoriaRuneGlyph
import com.example.ui.design.EldoriaSlotFrame
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaPaletteOf
import com.example.ui.design.eldoriaPulse
import kotlin.math.abs

// ══════════════════════════════════════════════════════════════════════════════
//  PIEZAS COMPARTIDAS DE LA EXPEDICIÓN
//
//  Todo lo que comparten ExpeditionHubScreen, ExpeditionRunScreen y
//  ExpeditionCombatScreen vive aquí con el prefijo obligatorio `Expedition*`.
//  Ningún símbolo de este fichero es público.
// ══════════════════════════════════════════════════════════════════════════════

/** Estado visual de un nodo del mapa de expedición. */
internal enum class ExpeditionNodeState { Current, Available, Cleared, Locked, Veiled }

/** Icono por tipo de sala (las 11 clases de `EldoriaExpeditions`). */
internal fun expeditionRoomIcon(kind: String): ImageVector = when (kind.uppercase()) {
    EldoriaExpeditions.KIND_COMBAT -> Icons.Filled.SportsMartialArts
    EldoriaExpeditions.KIND_ELITE -> Icons.Filled.MilitaryTech
    EldoriaExpeditions.KIND_BOSS -> Icons.Filled.EmojiEvents
    EldoriaExpeditions.KIND_TREASURE -> Icons.Filled.Inbox
    EldoriaExpeditions.KIND_CAMPFIRE -> Icons.Filled.LocalFireDepartment
    EldoriaExpeditions.KIND_SHRINE -> Icons.Filled.Star
    EldoriaExpeditions.KIND_TRAP -> Icons.Filled.Warning
    EldoriaExpeditions.KIND_MERCHANT -> Icons.Filled.ShoppingBag
    EldoriaExpeditions.KIND_EVENT -> Icons.AutoMirrored.Filled.Help
    EldoriaExpeditions.KIND_GATE -> Icons.Filled.Lock
    EldoriaExpeditions.KIND_VOID -> Icons.Filled.Visibility
    else -> Icons.Filled.SportsMartialArts
}

/** Tono metálico dominante de cada tipo de sala. */
internal fun expeditionRoomTone(kind: String): EldoriaTone = when (kind.uppercase()) {
    EldoriaExpeditions.KIND_COMBAT -> EldoriaTone.Silver
    EldoriaExpeditions.KIND_ELITE -> EldoriaTone.Blood
    EldoriaExpeditions.KIND_BOSS -> EldoriaTone.Gold
    EldoriaExpeditions.KIND_TREASURE -> EldoriaTone.Gold
    EldoriaExpeditions.KIND_CAMPFIRE -> EldoriaTone.Ember
    EldoriaExpeditions.KIND_SHRINE -> EldoriaTone.Arcane
    EldoriaExpeditions.KIND_TRAP -> EldoriaTone.Blood
    EldoriaExpeditions.KIND_MERCHANT -> EldoriaTone.Vitae
    EldoriaExpeditions.KIND_EVENT -> EldoriaTone.Arcane
    EldoriaExpeditions.KIND_GATE -> EldoriaTone.Iron
    EldoriaExpeditions.KIND_VOID -> EldoriaTone.Arcane
    else -> EldoriaTone.Iron
}

/** Rareza equivalente de cada sala: alimenta el borde del `EldoriaSlotFrame`. */
internal fun expeditionRoomRarity(kind: String): String = when (kind.uppercase()) {
    EldoriaExpeditions.KIND_COMBAT -> "RARO"
    EldoriaExpeditions.KIND_ELITE -> "ÉPICO"
    EldoriaExpeditions.KIND_BOSS -> "LEGENDARIO"
    EldoriaExpeditions.KIND_TREASURE -> "ARCANO"
    EldoriaExpeditions.KIND_CAMPFIRE -> "LEGENDARIO"
    EldoriaExpeditions.KIND_SHRINE -> "ARCANO"
    EldoriaExpeditions.KIND_MERCHANT -> "RARO"
    EldoriaExpeditions.KIND_EVENT -> "ÉPICO"
    EldoriaExpeditions.KIND_VOID -> "UNIVERSAL"
    else -> "COMÚN"
}

/** Par (claro, oscuro) de la paleta de un destino. */
internal fun expeditionPalette(paletteKey: String): Pair<Color, Color> = eldoriaPaletteOf(paletteKey)

/** Color de acento a partir de las claves de tono del catálogo ("EMBER", "BLOOD", …). */
internal fun expeditionAccentOf(tone: String): Color = when (tone.uppercase()) {
    "GOLD" -> Eldoria.Gold
    "EMBER" -> Eldoria.Ember
    "IRON" -> Eldoria.IronEdge
    "BLOOD" -> Eldoria.BloodBright
    "ARCANE" -> Eldoria.Arcane
    "VITAE" -> Eldoria.Vitae
    "SILVER" -> Eldoria.Silver
    "MANA" -> Eldoria.Mana
    else -> Eldoria.Gold
}

/** Traducción de las claves de tono al enum de controles. */
internal fun expeditionToneOf(tone: String): EldoriaTone = when (tone.uppercase()) {
    "GOLD" -> EldoriaTone.Gold
    "EMBER" -> EldoriaTone.Ember
    "IRON" -> EldoriaTone.Iron
    "BLOOD" -> EldoriaTone.Blood
    "ARCANE", "MANA" -> EldoriaTone.Arcane
    "VITAE" -> EldoriaTone.Vitae
    "SILVER" -> EldoriaTone.Silver
    else -> EldoriaTone.Gold
}

/** Acento creciente por profundidad: cuanto más hondo, más brasa. */
internal fun expeditionDepthAccent(depth: Int): Color = when (depth.coerceIn(0, 3)) {
    0 -> Eldoria.Silver
    1 -> Eldoria.Gold
    2 -> Eldoria.Ember
    else -> Eldoria.BloodBright
}

/** Nombre legible de un tipo de sala. */
internal fun expeditionKindName(kind: String): String = EldoriaExpeditions.roomKindName(kind)

/** Traduce la rareza interna del combate ("ELITE", "CHAMPION", …) a la escala de Eldoria. */
internal fun expeditionEnemyRarityLabel(rarity: String): String = when (rarity.uppercase()) {
    "LEGENDARY", "LEGENDARIO" -> "LEGENDARIO"
    "CHAMPION" -> "ÉPICO"
    "ELITE" -> "RARO"
    "UNIVERSAL" -> "UNIVERSAL"
    else -> "COMÚN"
}

/** Nombre legible del arquetipo de enemigo. */
internal fun expeditionArchetypeName(archetype: String): String = when (archetype.uppercase()) {
    "BRUTO" -> "Bruto"
    "ACECHADOR" -> "Acechador"
    "CONJURADOR" -> "Conjurador"
    "BALUARTE" -> "Baluarte"
    "ENJAMBRE" -> "Enjambre"
    "TIRANO" -> "Tirano"
    else -> archetype.lowercase().replaceFirstChar { it.uppercase() }
}

// ──────────────────────────────────────────────────────────────────────────────
//  HUD DE EXPEDICIÓN
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Emblema del calabozo: su lámina de jefe dedicada dentro de un marco biselado.
 * Si el destino no tiene lámina propia (el Abismo), cae a un glifo de llama para
 * no dejar un hueco negro.
 */
@Composable
internal fun ExpeditionDungeonEmblem(
    dungeonId: Int,
    accent: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val shape = CutCornerShape(6.dp)
    val emblem = EldoriaArt.dungeonEmblem(dungeonId)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Eldoria.Abyss)
            .border(Eldoria.StrokeThin, accent.copy(alpha = 0.75f), shape),
        contentAlignment = Alignment.Center
    ) {
        if (emblem != null) {
            Image(
                painter = painterResource(id = emblem),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Velo inferior: el rótulo de al lado gana contraste sobre la lámina.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Eldoria.Abyss.copy(alpha = 0.55f))
                        )
                    )
            )
        } else {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}

/**
 * Cabecera de la expedición: emblema y nombre del destino, antorcha (el reloj real
 * de la run), vida y maná persistentes, fragmentos y bendiciones activas.
 */
@Composable
internal fun ExpeditionHud(
    dungeonName: String,
    depth: Int,
    maxDepth: Int,
    roomLabel: String,
    torch: Int,
    hp: Int,
    maxHp: Int,
    mp: Int,
    maxMp: Int,
    shards: Int,
    keys: Int,
    boonIds: List<String>,
    sealIds: List<String>,
    modifier: Modifier = Modifier,
    dungeonId: Int = 0
) {
    val accent = expeditionDepthAccent(depth)
    EldoriaPanel(
        modifier = modifier.fillMaxWidth(),
        edge = if (torch < 25) EldoriaEdge.Blood else EldoriaEdge.Ember,
        corner = Eldoria.R12,
        padding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        glow = torch < 25,
        filigree = true
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Emblema del destino: saber en qué calabozo estás de un vistazo, sin
            // leer el rótulo.
            ExpeditionDungeonEmblem(dungeonId = dungeonId, accent = accent, size = 42.dp)
            Spacer(Modifier.width(Eldoria.S8))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dungeonName.uppercase(),
                    style = EldoriaType.title,
                    color = Eldoria.TextGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Profundidad ${(depth + 1).coerceAtMost(maxDepth.coerceAtLeast(1))} de ${maxDepth.coerceAtLeast(1)}" +
                        if (roomLabel.isNotBlank()) " · $roomLabel" else "",
                    style = EldoriaType.caption,
                    color = accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(Eldoria.S8))
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Diamond,
                        contentDescription = null,
                        tint = Eldoria.ArcaneBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "$shards",
                        style = EldoriaType.numeric,
                        color = Eldoria.ArcaneBright,
                        maxLines = 1
                    )
                }
                if (keys > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Eldoria.Silver,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "$keys",
                            style = EldoriaType.caption,
                            color = Eldoria.Silver,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Eldoria.S8))

        EldoriaResourceBar(
            current = torch.coerceIn(0, 100),
            max = 100,
            tone = EldoriaBarTone.Torch,
            label = if (torch < 25) "ANTORCHA — SE APAGA" else "ANTORCHA",
            icon = Icons.Filled.LocalFireDepartment,
            height = 13.dp,
            dangerPulse = true
        )

        Spacer(Modifier.height(Eldoria.S6))

        Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)) {
            EldoriaResourceBar(
                current = hp.coerceAtLeast(0),
                max = maxHp.coerceAtLeast(1),
                tone = EldoriaBarTone.Health,
                modifier = Modifier.weight(1f),
                label = "VIDA",
                icon = Icons.Filled.Favorite,
                height = 11.dp,
                dangerPulse = true
            )
            EldoriaResourceBar(
                current = mp.coerceAtLeast(0),
                max = maxMp.coerceAtLeast(1),
                tone = EldoriaBarTone.Mana,
                modifier = Modifier.weight(1f),
                label = "MANÁ",
                icon = Icons.Filled.Bolt,
                height = 11.dp
            )
        }

        if (boonIds.isNotEmpty() || sealIds.isNotEmpty()) {
            Spacer(Modifier.height(Eldoria.S8))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S6),
                verticalAlignment = Alignment.CenterVertically
            ) {
                boonIds.take(4).forEach { id ->
                    val boon = EldoriaExpeditions.boon(id)
                    ExpeditionBoonBadge(
                        name = boon?.name ?: id,
                        tone = boon?.tone ?: "GOLD",
                        seed = id.hashCode()
                    )
                }
                sealIds.take(3).forEach { id ->
                    val seal = EldoriaExpeditions.seal(id)
                    EldoriaChip(
                        text = seal?.name?.removePrefix("Sello de ")?.removePrefix("Sello del ") ?: id,
                        color = expeditionAccentOf(seal?.tone ?: "IRON"),
                        icon = Icons.Filled.Lock
                    )
                }
            }
        }
    }
}

/** Insignia de bendición: glifo rúnico procedural + nombre. */
@Composable
internal fun ExpeditionBoonBadge(
    name: String,
    tone: String,
    seed: Int,
    modifier: Modifier = Modifier
) {
    val accent = expeditionAccentOf(tone)
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.24f), accent.copy(alpha = 0.07f))
                )
            )
            .border(Eldoria.StrokeThin, accent.copy(alpha = 0.7f), shape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EldoriaRuneGlyph(
            seed = seed,
            modifier = Modifier.size(13.dp),
            color = accent,
            strokeWidth = 1.4.dp,
            animated = true
        )
        Spacer(Modifier.width(Eldoria.S4))
        Text(
            text = name,
            style = EldoriaType.caption,
            color = accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  NODO DEL MAPA
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Nodo circular del mapa de salas. El anillo exterior lleva el tono del tipo de sala;
 * el marco interior reutiliza `EldoriaSlotFrame` para el metal y el bisel.
 */
@Composable
internal fun ExpeditionNode(
    kind: String,
    label: String,
    state: ExpeditionNodeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    dungeonId: Int = 0,
    roomId: Int = 0
) {
    val veiled = state == ExpeditionNodeState.Veiled
    val selectable = state == ExpeditionNodeState.Available
    val accent = when (state) {
        ExpeditionNodeState.Veiled -> Eldoria.IronDeep
        ExpeditionNodeState.Locked -> Eldoria.Iron
        ExpeditionNodeState.Cleared -> Eldoria.SilverDeep
        else -> Eldoria.toneColor(expeditionRoomTone(kind))
    }
    val pulse = if (selectable) eldoriaPulse(periodMs = 1500, from = 0.35f, to = 1f, label = "node") else 0f

    Column(
        modifier = modifier.width(86.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(70.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension / 2f
                if (r <= 2f) return@Canvas
                // halo del nodo disponible
                if (selectable) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            0f to accent.copy(alpha = 0.30f * pulse),
                            1f to Color.Transparent,
                            center = center,
                            radius = r
                        ),
                        radius = r,
                        center = center
                    )
                }
                // anillo exterior
                drawCircle(
                    color = Color.Black.copy(alpha = 0.65f),
                    radius = r * 0.93f,
                    center = center,
                    style = Stroke(width = 3.2.dp.toPx())
                )
                drawCircle(
                    color = accent.copy(alpha = if (veiled) 0.30f else 0.85f),
                    radius = r * 0.93f,
                    center = center,
                    style = Stroke(width = 1.6.dp.toPx())
                )
                // muescas cardinales
                if (!veiled) {
                    val ticks = 4
                    for (i in 0 until ticks) {
                        val ang = (i * 90f + 45f) * (Math.PI / 180.0).toFloat()
                        val cx = center.x + kotlin.math.cos(ang) * r * 0.93f
                        val cy = center.y + kotlin.math.sin(ang) * r * 0.93f
                        drawCircle(accent.copy(alpha = 0.9f), radius = 1.8.dp.toPx(), center = Offset(cx, cy))
                    }
                }
            }

            EldoriaSlotFrame(
                rarity = if (veiled || state == ExpeditionNodeState.Locked || state == ExpeditionNodeState.Cleared) null
                else expeditionRoomRarity(kind),
                size = 50.dp,
                selected = state == ExpeditionNodeState.Current,
                onClick = if (selectable) onClick else null,
                testTag = testTag
            ) {
                // Lámina real de la sala. Las de pelea enseñan una criatura del
                // elenco de ESE calabozo en vez del mismo monigote de artes
                // marciales en los dieciséis. Velada sigue siendo interrogante, y
                // si no hay lámina se cae al icono vectorial de siempre.
                val nodeArt = if (veiled) null
                else remember(dungeonId, kind, roomId) {
                    EldoriaArt.expeditionNodeArt(dungeonId, kind, roomId)
                }

                if (nodeArt != null) {
                    Image(
                        painter = painterResource(id = nodeArt),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CutCornerShape(4.dp))
                            .alpha(
                                when (state) {
                                    ExpeditionNodeState.Locked -> 0.45f
                                    ExpeditionNodeState.Cleared -> 0.35f
                                    else -> 1f
                                }
                            )
                    )
                    // Las salas ya limpias se apagan hacia gris para que la vista
                    // vaya sola a las que quedan por hacer.
                    if (state == ExpeditionNodeState.Cleared) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Eldoria.Abyss.copy(alpha = 0.45f))
                        )
                    }
                } else {
                    Icon(
                        imageVector = if (veiled) Icons.AutoMirrored.Filled.Help else expeditionRoomIcon(kind),
                        contentDescription = null,
                        tint = when (state) {
                            ExpeditionNodeState.Veiled -> Eldoria.Iron
                            ExpeditionNodeState.Locked -> Eldoria.TextLow
                            ExpeditionNodeState.Cleared -> Eldoria.SilverDeep
                            else -> accent
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
                if (state == ExpeditionNodeState.Cleared) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val s = size.minDimension
                        drawLine(
                            color = Eldoria.Vitae.copy(alpha = 0.9f),
                            start = Offset(s * 0.26f, s * 0.54f),
                            end = Offset(s * 0.44f, s * 0.72f),
                            strokeWidth = 2.6.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Eldoria.Vitae.copy(alpha = 0.9f),
                            start = Offset(s * 0.44f, s * 0.72f),
                            end = Offset(s * 0.76f, s * 0.28f),
                            strokeWidth = 2.6.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(3.dp))
        Text(
            text = if (veiled) "· · ·" else expeditionKindName(kind).uppercase(),
            style = EldoriaType.caption,
            color = if (veiled) Eldoria.TextLow else accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        if (!veiled && label.isNotBlank()) {
            Text(
                text = label,
                style = EldoriaType.caption,
                color = Eldoria.TextLow,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  TARJETA DE OFERTA
// ──────────────────────────────────────────────────────────────────────────────

/** Carta de opción de una sala (bendición, botín, evento, mercader, hoguera, puerta). */
@Composable
internal fun ExpeditionOfferCard(
    title: String,
    subtitle: String,
    tone: String,
    isBoon: Boolean,
    seed: Int,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val accent = expeditionAccentOf(tone)
    EldoriaPanel(
        modifier = modifier.fillMaxWidth(),
        edge = EldoriaEdge.tone(expeditionToneOf(tone)),
        corner = Eldoria.R12,
        padding = PaddingValues(12.dp),
        glow = isBoon,
        filigree = isBoon,
        onClick = onClick,
        testTag = testTag
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.minDimension / 2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            0f to accent.copy(alpha = 0.30f),
                            1f to Color.Transparent,
                            center = center,
                            radius = r
                        ),
                        radius = r,
                        center = center
                    )
                    drawCircle(
                        color = accent.copy(alpha = 0.45f),
                        radius = r * 0.86f,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
                EldoriaRuneGlyph(
                    seed = seed,
                    modifier = Modifier.size(26.dp),
                    color = accent,
                    strokeWidth = 1.8.dp,
                    animated = isBoon
                )
            }
            Spacer(Modifier.width(Eldoria.S12))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = EldoriaType.subheading,
                    color = accent,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = EldoriaType.small,
                        color = Eldoria.TextMid,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  ANILLO DE REACCIÓN
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Anillo que se contrae en [durationMs]. El jugador toca la pantalla y la calidad
 * sale de lo cerca que esté el anillo del aro dorado en ese instante.
 * Si expira sin toque, se resuelve como "FALLO".
 */
@Composable
internal fun ExpeditionReactionRing(
    deadline: Long,
    onResult: (String) -> Unit,
    modifier: Modifier = Modifier,
    durationMs: Int = 1100,
    assist: Boolean = false,
    accent: Color = Eldoria.EmberCore
) {
    val anim = remember(deadline) { Animatable(0f) }
    val resolved = remember(deadline) { mutableStateOf(false) }
    // Bandas estrechas a propósito. Con la anterior (0.21) casi la mitad del aro
    // contaba como bloqueo y pulsar en cualquier momento tardío salía bien: no
    // había forma de fallar y el golpe enemigo dejó de dar miedo.
    val perfectBand = if (assist) 0.075f else 0.045f
    val goodBand = if (assist) 0.17f else 0.11f
    val target = 0.80f

    LaunchedEffect(deadline) {
        anim.snapTo(0f)
        anim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMs.coerceAtLeast(200), easing = LinearEasing)
        )
        if (!resolved.value) {
            resolved.value = true
            onResult("FALLO")
        }
    }

    val t = anim.value

    Box(
        modifier = modifier
            .size(190.dp)
            .clip(RoundedCornerShape(50))
            .background(Eldoria.Abyss.copy(alpha = 0.55f))
            .pointerInput(deadline) {
                detectTapGestures {
                    if (!resolved.value) {
                        resolved.value = true
                        val d = abs(anim.value - target)
                        onResult(
                            when {
                                d <= perfectBand -> "PERFECTO"
                                d <= goodBand -> "BUENO"
                                else -> "FALLO"
                            }
                        )
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val half = size.minDimension / 2f
            if (half <= 4f) return@Canvas
            val rMax = half * 0.94f
            val rMin = half * 0.20f
            fun radiusAt(v: Float): Float = rMax + (rMin - rMax) * v.coerceIn(0f, 1f)

            // aro objetivo + banda perfecta
            val rTarget = radiusAt(target)
            val rGoodOuter = radiusAt(target - goodBand)
            val rGoodInner = radiusAt(target + goodBand)
            drawCircle(
                color = Eldoria.Gold.copy(alpha = 0.10f),
                radius = (rGoodOuter + rGoodInner) / 2f,
                center = center,
                style = Stroke(width = (rGoodOuter - rGoodInner).coerceAtLeast(1f))
            )
            val rPerfOuter = radiusAt(target - perfectBand)
            val rPerfInner = radiusAt(target + perfectBand)
            drawCircle(
                color = Eldoria.VitaeBright.copy(alpha = 0.22f),
                radius = (rPerfOuter + rPerfInner) / 2f,
                center = center,
                style = Stroke(width = (rPerfOuter - rPerfInner).coerceAtLeast(1f))
            )
            drawCircle(
                color = Eldoria.GoldBright.copy(alpha = 0.95f),
                radius = rTarget,
                center = center,
                style = Stroke(width = 2.2.dp.toPx())
            )

            // anillo que se contrae
            val rNow = radiusAt(t)
            drawCircle(
                color = accent.copy(alpha = 0.18f),
                radius = rNow,
                center = center,
                style = Stroke(width = 9.dp.toPx())
            )
            drawCircle(
                color = accent,
                radius = rNow,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )
            // marcas del anillo móvil
            for (i in 0 until 6) {
                val ang = (i * 60f) * (Math.PI / 180.0).toFloat()
                val cx = center.x + kotlin.math.cos(ang) * rNow
                val cy = center.y + kotlin.math.sin(ang) * rNow
                drawCircle(Eldoria.EmberCore, radius = 2.6.dp.toPx(), center = Offset(cx, cy))
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "¡PARA!",
                style = EldoriaType.display,
                color = Eldoria.GoldBright,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Toca cuando el anillo\ncruce el aro dorado",
                style = EldoriaType.caption,
                color = Eldoria.TextMid,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  PIEZAS MENORES
// ──────────────────────────────────────────────────────────────────────────────

/** Píldora de recurso del vestíbulo: icono, cifra y etiqueta corta. */
@Composable
internal fun ExpeditionResourcePill(
    icon: ImageVector,
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val shape = CutCornerShape(7.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Eldoria.PanelHi, Eldoria.PanelSunken)))
            .border(Eldoria.StrokeThin, accent.copy(alpha = 0.5f), shape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(Eldoria.S6))
        Column {
            Text(
                text = value,
                style = EldoriaType.numeric,
                color = accent,
                maxLines = 1
            )
            Text(
                text = label.uppercase(),
                style = EldoriaType.caption,
                color = Eldoria.TextLow,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Aviso de requisito no cumplido: explica por qué el botón está muerto. */
@Composable
internal fun ExpeditionRequirementNote(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = Eldoria.Warning
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CutCornerShape(6.dp))
            .background(accent.copy(alpha = 0.10f))
            .border(Eldoria.StrokeThin, accent.copy(alpha = 0.45f), CutCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(Eldoria.S6))
        Text(
            text = text,
            style = EldoriaType.small,
            color = accent,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Botón de acción ancho reutilizado por el vestíbulo y el resumen de run. */
@Composable
internal fun ExpeditionPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tone: EldoriaTone = EldoriaTone.Gold,
    icon: ImageVector? = null,
    testTag: String? = null
) {
    EldoriaButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        tone = tone,
        size = EldoriaButtonSize.Medium,
        icon = icon,
        fullWidth = true,
        testTag = testTag
    )
}

/** Marca visual de "sin tocar el testTag": envuelve un bloque con una etiqueta de prueba. */
@Composable
internal fun ExpeditionTagged(
    tag: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.testTag(tag)) { content() }
}
