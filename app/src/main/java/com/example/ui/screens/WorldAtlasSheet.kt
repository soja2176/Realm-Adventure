package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.KingdomGenerator
import com.example.data.content.KingdomAtlas
import com.example.data.content.KingdomEntry
import com.example.data.formatGameNumber
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaCrest
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmberField
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaScrollSheet
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaStatPill
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.EldoriaVignette
import com.example.ui.design.eldoriaDiamondPath
import com.example.ui.design.eldoriaPressable
import com.example.ui.design.eldoriaPulse
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════════════════
//  MAPA DEL MUNDO
//
//  El tablero de juego sólo enseña 5×5 casillas alrededor del héroe: es una
//  linterna, no un mapa. Esta vista es el mapa de verdad — los seis reinos
//  dibujados como lo que realmente son, anillos concéntricos alrededor del
//  Santuario, con el héroe marcado en su posición real.
//
//  Desde aquí se lee el lore de cada tierra y se contrata la caravana, que
//  sólo cruza fronteras si el héroe tiene nivel y oro para el pasaje.
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun WorldAtlasSheet(
    visible: Boolean,
    playerX: Int,
    playerY: Int,
    playerLevel: Int,
    playerGold: Int,
    discoveredIds: Set<String>,
    onTravel: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val currentEntry = KingdomAtlas.entryForCoords(playerX, playerY)
    var selectedId by remember(currentEntry.id) { mutableStateOf(currentEntry.id) }
    val selected = KingdomAtlas.byId(selectedId) ?: currentEntry
    val selectedData = remember(selected.id) { KingdomAtlas.dataOf(selected) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Eldoria.Scrim)
        ) {
            EldoriaEmberField(
                modifier = Modifier.fillMaxSize(),
                count = 20,
                tint = Eldoria.Gold,
                periodMs = 12000,
                seed = 33,
                maxAlpha = 0.30f
            )
            EldoriaVignette(
                modifier = Modifier.fillMaxSize(),
                strength = 0.72f,
                tint = Eldoria.Abyss,
                centerBiasY = 0.45f
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(Eldoria.S8)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "MAPA DEL MUNDO",
                                style = EldoriaType.display,
                                color = Eldoria.TextGold
                            )
                            Text(
                                "Seis reinos en anillos alrededor del Santuario",
                                style = EldoriaType.small,
                                color = Eldoria.TextMid
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CutCornerShape(7.dp))
                                .background(Eldoria.PanelSunken)
                                .border(Eldoria.StrokeThin, Eldoria.ironEdge(), CutCornerShape(7.dp))
                                .eldoriaPressable(onClick = onDismiss),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar el atlas",
                                tint = Eldoria.TextMid,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }

                // ─── La rosa de reinos ───
                item {
                    EldoriaFrame(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        edge = EldoriaEdge.Gold,
                        corner = Eldoria.R16,
                        strokeWidth = Eldoria.StrokeBold,
                        filigree = true,
                        rivets = true
                    ) {
                        KingdomRings(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            playerX = playerX,
                            playerY = playerY,
                            selectedId = selected.id,
                            discoveredIds = discoveredIds,
                            playerLevel = playerLevel,
                            onSelect = { selectedId = it }
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
                    ) {
                        EldoriaStatPill(
                            label = "ESTÁS EN",
                            value = KingdomGenerator.getKingdomForCoords(playerX, playerY)
                                .name.replace("Reino de ", "").replace("Reino Celestial de ", ""),
                            icon = Icons.Default.Place,
                            accent = Eldoria.Gold,
                            modifier = Modifier.weight(1f)
                        )
                        EldoriaStatPill(
                            label = "TU ORO",
                            value = formatGameNumber(playerGold),
                            icon = Icons.Default.MonetizationOn,
                            accent = Eldoria.TextGold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ─── Ficha del reino seleccionado ───
                item {
                    val discovered = selected.id in discoveredIds
                    val levelOk = playerLevel >= selected.requiredLevel
                    val goldOk = playerGold >= selected.travelCost
                    val isHere = selected.id == currentEntry.id
                    val accent = Color(android.graphics.Color.parseColor(selectedData.colorHex))

                    EldoriaPanel(
                        modifier = Modifier.fillMaxWidth(),
                        edge = if (levelOk) EldoriaEdge.Gold else EldoriaEdge.Iron,
                        corner = Eldoria.R12,
                        padding = PaddingValues(14.dp),
                        glow = levelOk && !isHere,
                        filigree = true
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            EldoriaFrame(
                                modifier = Modifier.size(width = 52.dp, height = 62.dp),
                                edge = if (levelOk) EldoriaEdge.Gold else EldoriaEdge.Iron,
                                corner = Eldoria.R8,
                                strokeWidth = Eldoria.StrokeMed,
                                filigree = false,
                                rivets = true
                            ) {
                                EldoriaCrest(
                                    seed = selected.id.hashCode(),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(5.dp),
                                    primary = if (levelOk) accent else Eldoria.IronEdge,
                                    secondary = Eldoria.IronDeep,
                                    ornate = true
                                )
                            }
                            Spacer(Modifier.width(Eldoria.S12))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    selectedData.name,
                                    style = EldoriaType.heading,
                                    color = if (levelOk) Eldoria.TextGold else Eldoria.TextMid,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    selectedData.subtitle,
                                    style = EldoriaType.caption,
                                    color = Eldoria.TextLow,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)) {
                                    EldoriaChip(
                                        text = "TIER ${selected.tier}",
                                        color = accent,
                                        filled = true
                                    )
                                    EldoriaChip(
                                        text = if (discovered) "DESCUBIERTO" else "SIN PISAR",
                                        color = if (discovered) Eldoria.Success else Eldoria.TextLow,
                                        icon = if (discovered) Icons.Default.CheckCircle else Icons.Default.Explore
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(Eldoria.S12))

                        // El lore va en pergamino: es la voz del bardo, no una ficha técnica.
                        EldoriaScrollSheet(padding = PaddingValues(15.dp)) {
                            Text(
                                text = selected.lore,
                                style = EldoriaType.lore,
                                color = Eldoria.ParchmentInk
                            )
                        }

                        Spacer(Modifier.height(Eldoria.S12))

                        AtlasFactRow(
                            icon = Icons.Default.Warning,
                            label = "PELIGRO",
                            text = selected.threat,
                            accent = Eldoria.Danger
                        )
                        Spacer(Modifier.height(Eldoria.S6))
                        AtlasFactRow(
                            icon = Icons.Default.AutoAwesome,
                            label = "COSTUMBRE",
                            text = selected.custom,
                            accent = Eldoria.ArcaneBright
                        )
                        Spacer(Modifier.height(Eldoria.S6))
                        AtlasFactRow(
                            icon = Icons.Default.Place,
                            label = "HITO",
                            text = "${selected.landmarkName} — ${selected.landmarkBoon}",
                            accent = Eldoria.TextGold
                        )

                        Spacer(Modifier.height(Eldoria.S12))
                        EldoriaDivider(color = accent.copy(alpha = 0.7f))
                        Spacer(Modifier.height(Eldoria.S12))

                        // ─── Pasaje de caravana ───
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
                        ) {
                            EldoriaStatPill(
                                label = "NIVEL EXIGIDO",
                                value = "${selected.requiredLevel}",
                                icon = if (levelOk) Icons.Default.CheckCircle else Icons.Default.Lock,
                                accent = if (levelOk) Eldoria.Success else Eldoria.Danger,
                                modifier = Modifier.weight(1f)
                            )
                            EldoriaStatPill(
                                label = "PASAJE",
                                value = if (selected.travelCost == 0) "GRATIS" else formatGameNumber(selected.travelCost),
                                icon = Icons.Default.MonetizationOn,
                                accent = if (goldOk) Eldoria.TextGold else Eldoria.Danger,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(Eldoria.S12))

                        when {
                            isHere -> {
                                Text(
                                    "Ya caminas por estas tierras.",
                                    style = EldoriaType.lore,
                                    color = Eldoria.TextLow,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            !levelOk -> {
                                EldoriaButton(
                                    text = "EXIGE NIVEL ${selected.requiredLevel}",
                                    onClick = {},
                                    enabled = false,
                                    tone = EldoriaTone.Iron,
                                    size = EldoriaButtonSize.Large,
                                    icon = Icons.Default.Lock,
                                    fullWidth = true
                                )
                                Spacer(Modifier.height(Eldoria.S4))
                                Text(
                                    "La caravana no lleva a nadie a una muerte segura. Te faltan ${selected.requiredLevel - playerLevel} niveles.",
                                    style = EldoriaType.caption,
                                    color = Eldoria.TextLow,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            !goldOk -> {
                                EldoriaButton(
                                    text = "PASAJE INSUFICIENTE",
                                    onClick = {},
                                    enabled = false,
                                    tone = EldoriaTone.Iron,
                                    size = EldoriaButtonSize.Large,
                                    icon = Icons.Default.MonetizationOn,
                                    fullWidth = true
                                )
                                Spacer(Modifier.height(Eldoria.S4))
                                Text(
                                    "Te faltan ${formatGameNumber(selected.travelCost - playerGold)} de oro.",
                                    style = EldoriaType.caption,
                                    color = Eldoria.TextLow,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            else -> {
                                EldoriaButton(
                                    text = "VIAJAR A ${selected.capitalName.uppercase()}",
                                    onClick = {
                                        onTravel(selected.id)
                                        onDismiss()
                                    },
                                    tone = EldoriaTone.Gold,
                                    size = EldoriaButtonSize.Large,
                                    icon = Icons.Default.DirectionsWalk,
                                    fullWidth = true,
                                    testTag = "travel_to_${selected.id}"
                                )
                                Spacer(Modifier.height(Eldoria.S4))
                                Text(
                                    "Llegarás descansado a (${selected.capitalX}, ${selected.capitalY}).",
                                    style = EldoriaType.caption,
                                    color = Eldoria.TextLow,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                item {
                    EldoriaSectionTitle(
                        text = "LOS SEIS REINOS",
                        icon = Icons.Default.Explore,
                        accent = Eldoria.Gold
                    )
                }

                items(KingdomAtlas.ALL) { entry ->
                    KingdomRow(
                        entry = entry,
                        selected = entry.id == selected.id,
                        isCurrent = entry.id == currentEntry.id,
                        discovered = entry.id in discoveredIds,
                        playerLevel = playerLevel,
                        onClick = { selectedId = entry.id }
                    )
                }
            }
        }
    }
}

/** Dato del atlas: rótulo corto en color y una frase. */
@Composable
private fun AtlasFactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    text: String,
    accent: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(Eldoria.S6))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = EldoriaType.label, color = accent)
            Text(text, style = EldoriaType.small, color = Eldoria.TextMid)
        }
    }
}

/** Fila compacta de reino en la lista inferior. */
@Composable
private fun KingdomRow(
    entry: KingdomEntry,
    selected: Boolean,
    isCurrent: Boolean,
    discovered: Boolean,
    playerLevel: Int,
    onClick: () -> Unit
) {
    val data = remember(entry.id) { KingdomAtlas.dataOf(entry) }
    val accent = Color(android.graphics.Color.parseColor(data.colorHex))
    val levelOk = playerLevel >= entry.requiredLevel

    EldoriaPanel(
        modifier = Modifier.fillMaxWidth(),
        edge = when {
            selected -> EldoriaEdge.Gold
            levelOk -> EldoriaEdge.Silver
            else -> EldoriaEdge.Iron
        },
        corner = Eldoria.R8,
        padding = PaddingValues(10.dp),
        onClick = onClick,
        testTag = "atlas_row_${entry.id}"
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CutCornerShape(6.dp))
                    .background(accent.copy(alpha = if (levelOk) 0.22f else 0.08f))
                    .border(
                        Eldoria.StrokeThin,
                        accent.copy(alpha = if (levelOk) 0.85f else 0.3f),
                        CutCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${entry.tier}",
                    style = EldoriaType.numeric,
                    color = if (levelOk) accent else Eldoria.TextLow
                )
            }
            Spacer(Modifier.width(Eldoria.S8))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    data.name.replace("Reino de ", "").replace("Reino Celestial de ", ""),
                    style = EldoriaType.subheading,
                    color = if (levelOk) Eldoria.TextHi else Eldoria.TextLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Anillo ${entry.minDist}–${entry.maxDist} · nivel ${entry.requiredLevel}",
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow,
                    maxLines = 1
                )
            }
            when {
                isCurrent -> EldoriaChip(text = "AQUÍ", color = Eldoria.Gold, filled = true)
                !levelOk -> Icon(
                    Icons.Default.Lock,
                    contentDescription = "Bloqueado",
                    tint = Eldoria.TextLow,
                    modifier = Modifier.size(16.dp)
                )
                discovered -> Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Descubierto",
                    tint = Eldoria.Success,
                    modifier = Modifier.size(16.dp)
                )
                else -> EldoriaChip(text = "ABIERTO", color = Eldoria.Success)
            }
        }
    }
}

/**
 * Los seis reinos como anillos concéntricos. No es una metáfora: el mundo se
 * reparte EXACTAMENTE así por distancia de Chebyshev al Santuario, de modo que
 * este dibujo es el mapa real, no una ilustración.
 */
@Composable
private fun KingdomRings(
    modifier: Modifier,
    playerX: Int,
    playerY: Int,
    selectedId: String,
    discoveredIds: Set<String>,
    playerLevel: Int,
    onSelect: (String) -> Unit
) {
    val rings = KingdomAtlas.ALL
    val outerDist = rings.last().maxDist.toFloat()
    val pulse = eldoriaPulse(periodMs = 1600, from = 0.45f, to = 1f, label = "atlasHero")

    // Colores y estado se resuelven fuera del Canvas: dentro no se puede componer.
    val ringInfo = rings.map { entry ->
        val data = KingdomAtlas.dataOf(entry)
        Triple(
            Color(android.graphics.Color.parseColor(data.colorHex)),
            entry.id in discoveredIds,
            playerLevel >= entry.requiredLevel
        )
    }

    val playerDist = KingdomAtlas.distanceOf(playerX, playerY).toFloat()
    val playerAngle = if (playerX == 0 && playerY == 0) -PI.toFloat() / 2f
    else atan2(playerY.toFloat(), playerX.toFloat())

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .eldoriaPressable(
                    onClick = {
                        // Un toque recorre los reinos: sin gestos finos, el
                        // círculo es demasiado pequeño para acertar un anillo.
                        val idx = rings.indexOfFirst { it.id == selectedId }
                        onSelect(rings[(idx + 1) % rings.size].id)
                    },
                    sound = false
                )
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val maxR = size.minDimension / 2f * 0.94f

            // Anillos, de fuera hacia dentro para que el centro quede encima.
            for (i in rings.indices.reversed()) {
                val entry = rings[i]
                val (color, discovered, unlocked) = ringInfo[i]
                val rOuter = maxR * (entry.maxDist.toFloat() / outerDist)
                val rInner = maxR * (entry.minDist.toFloat() / outerDist)
                val isSelected = entry.id == selectedId

                val fillAlpha = when {
                    !unlocked -> 0.07f
                    discovered -> 0.24f
                    else -> 0.14f
                }
                drawCircle(
                    brush = Brush.radialGradient(
                        0f to color.copy(alpha = fillAlpha * 0.4f),
                        1f to color.copy(alpha = fillAlpha),
                        center = Offset(cx, cy),
                        radius = rOuter
                    ),
                    radius = rOuter,
                    center = Offset(cx, cy)
                )

                // Filo del anillo. El seleccionado se marca con trazo grueso.
                drawCircle(
                    color = if (unlocked) color.copy(alpha = if (isSelected) 1f else 0.65f)
                            else Eldoria.IronEdge.copy(alpha = 0.55f),
                    radius = rOuter,
                    center = Offset(cx, cy),
                    style = Stroke(width = if (isSelected) 3.2.dp.toPx() else 1.4.dp.toPx())
                )

                // Marca del anillo: un rombo en el borde superior con su tier.
                if (rOuter - rInner > 6.dp.toPx()) {
                    val markR = (rOuter + rInner) / 2f
                    drawPath(
                        eldoriaDiamondPath(cx, cy - markR, if (isSelected) 6.dp.toPx() else 4.dp.toPx()),
                        color = if (unlocked) color else Eldoria.IronDeep
                    )
                }
            }

            // Santuario: el centro exacto del mundo.
            drawCircle(Eldoria.GoldBright, radius = 3.5.dp.toPx(), center = Offset(cx, cy))
            drawCircle(
                Eldoria.Gold.copy(alpha = 0.5f),
                radius = 7.dp.toPx(),
                center = Offset(cx, cy),
                style = Stroke(width = 1.dp.toPx())
            )

            // El héroe, en su radio y su ángulo reales.
            val pr = (maxR * (playerDist / outerDist)).coerceIn(0f, maxR)
            val px = cx + cos(playerAngle) * pr
            val py = cy + sin(playerAngle) * pr
            drawCircle(
                Eldoria.GoldBright.copy(alpha = 0.30f * pulse),
                radius = 13.dp.toPx(),
                center = Offset(px, py)
            )
            drawCircle(Eldoria.Abyss, radius = 6.dp.toPx(), center = Offset(px, py))
            drawCircle(Eldoria.GoldBright, radius = 4.dp.toPx(), center = Offset(px, py))

            // Radio guía del héroe al centro: cuánto ha caminado desde el origen.
            drawLine(
                color = Eldoria.Gold.copy(alpha = 0.35f),
                start = Offset(cx, cy),
                end = Offset(px, py),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        Text(
            text = "Toca el mapa para recorrer los reinos",
            style = EldoriaType.caption,
            color = Eldoria.TextLow,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}
