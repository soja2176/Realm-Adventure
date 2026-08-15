package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.GameScreen
import com.example.data.GameViewModel
import com.example.data.content.EldoriaBestiary
import com.example.data.model.BestiaryEntry
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBanner
import com.example.ui.design.EldoriaBeastSigil
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmptyState
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaKeyValueRow
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaProgressRing
import com.example.ui.design.EldoriaRuneGlyph
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaSheet
import com.example.ui.design.EldoriaStatPill
import com.example.ui.design.EldoriaToggleChip
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaFloat
import com.example.ui.design.eldoriaPressable
import com.example.ui.getEnemyPortraitRes

// ═══════════════════════════════════════════════════════════════════════════
//  CÓDICE DE BESTIAS — la pantalla del coleccionista.
//  72 fichas. Las descubiertas enseñan retrato, arquetipo, movimiento firma,
//  debilidad, resistencia, bajas y lore. Las que faltan enseñan una silueta
//  negra con un glifo rúnico latiendo y "???": el hueco es la recompensa.
// ═══════════════════════════════════════════════════════════════════════════

private const val BESTIARY_FILTER_ALL = "__ALL__"

private val BESTIARY_KINGDOMS: List<Pair<String, String>> = listOf(
    "eldoria" to "Eldoria",
    "drakenhold" to "Drakenhold",
    "frostgard" to "Frostgard",
    "aethelgard" to "Aethelgard",
    "solaria" to "Solaria",
    "aetheria" to "Aetheria"
)

@Composable
fun BestiaryScreen(viewModel: GameViewModel) {
    val bestiaryMap by viewModel.systems.bestiary.collectAsState()
    val entries = remember(bestiaryMap) { viewModel.systems.bestiaryEntries() }

    val total = entries.size.coerceAtLeast(1)
    val discovered = entries.count { it.discovered }
    val totalKills = remember(bestiaryMap) { bestiaryMap.values.sum() }
    val progress = discovered.toFloat() / total.toFloat()
    val percent = (progress * 100f).toInt()

    var kingdomFilter by rememberSaveable { mutableStateOf(BESTIARY_FILTER_ALL) }
    var archetypeFilter by rememberSaveable { mutableStateOf(BESTIARY_FILTER_ALL) }
    var onlyDiscovered by rememberSaveable { mutableStateOf(false) }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }

    val kingdomScroll = rememberScrollState()
    val archetypeScroll = rememberScrollState()

    val filtered = remember(entries, kingdomFilter, archetypeFilter, onlyDiscovered) {
        entries.filter { entry ->
            (kingdomFilter == BESTIARY_FILTER_ALL || entry.species.kingdomId == kingdomFilter) &&
                (archetypeFilter == BESTIARY_FILTER_ALL || entry.species.archetype == archetypeFilter) &&
                (!onlyDiscovered || entry.discovered)
        }
    }

    val kingdomProgress = remember(entries) {
        entries.groupBy { it.species.kingdomId }
            .mapValues { (_, list) -> list.count { it.discovered } to list.size }
    }

    val selected = remember(selectedId, entries) {
        entries.firstOrNull { it.species.id == selectedId && it.discovered }
    }

    EldoriaScreen(
        depth = 1,
        embers = true,
        fog = true,
        vignetteStrength = 0.62f,
        backgroundArtRes = R.drawable.img_enemy_boss_1784386985144,
        backgroundArtAlpha = 0.15f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(Eldoria.S8),
            verticalArrangement = Arrangement.spacedBy(Eldoria.S8),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EldoriaIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver al mapa",
                        onClick = { viewModel.changeScreen(GameScreen.WORLD_MAP) },
                        tone = EldoriaTone.Iron,
                        size = 42.dp,
                        testTag = "bestiary_back_btn"
                    )
                    Spacer(Modifier.width(Eldoria.S8))
                    EldoriaBanner(
                        title = "CÓDICE DE BESTIAS",
                        subtitle = "Descubiertos $discovered/$total · el que conoce a la bestia elige el terreno",
                        modifier = Modifier.weight(1f),
                        artRes = R.drawable.enemy_dragon_1784850948333,
                        height = 118.dp,
                        edge = EldoriaEdge.Gold,
                        crestSeed = 8821,
                        trailing = {
                            EldoriaProgressRing(
                                progress = progress,
                                size = 62.dp,
                                stroke = 6.dp,
                                accent = Eldoria.Gold,
                                centerLabel = "$percent%"
                            )
                        }
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                ) {
                    EldoriaStatPill(
                        label = "Fichas",
                        value = "$discovered / $total",
                        icon = Icons.Filled.MenuBook,
                        accent = Eldoria.Gold
                    )
                    EldoriaStatPill(
                        label = "Bajas totales",
                        value = totalKills.toString(),
                        icon = Icons.Filled.Bolt,
                        accent = Eldoria.Blood
                    )
                    EldoriaStatPill(
                        label = "Reinos completos",
                        value = "${kingdomProgress.count { it.value.first >= it.value.second && it.value.second > 0 }} / ${BESTIARY_KINGDOMS.size}",
                        icon = Icons.Filled.CheckCircle,
                        accent = Eldoria.Vitae
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = EldoriaEdge.Iron,
                    padding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    EldoriaSectionTitle(
                        text = "Filtros del códice",
                        icon = Icons.Filled.FilterList,
                        accent = Eldoria.Gold,
                        trailing = {
                            Text(
                                text = "${filtered.size} fichas",
                                style = EldoriaType.caption,
                                color = Eldoria.TextLow,
                                maxLines = 1
                            )
                        }
                    )
                    Spacer(Modifier.height(Eldoria.S8))

                    Text(
                        text = "REINO",
                        style = EldoriaType.caption,
                        color = Eldoria.TextLow,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(Eldoria.S4))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(kingdomScroll),
                        horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
                    ) {
                        EldoriaToggleChip(
                            text = "TODOS",
                            selected = kingdomFilter == BESTIARY_FILTER_ALL,
                            onClick = { kingdomFilter = BESTIARY_FILTER_ALL },
                            accent = Eldoria.Gold,
                            testTag = "bestiary_filter_kingdom_all"
                        )
                        BESTIARY_KINGDOMS.forEach { (id, label) ->
                            val pair = kingdomProgress[id] ?: (0 to 0)
                            EldoriaToggleChip(
                                text = "${label.uppercase()} ${pair.first}/${pair.second}",
                                selected = kingdomFilter == id,
                                onClick = { kingdomFilter = if (kingdomFilter == id) BESTIARY_FILTER_ALL else id },
                                accent = bestiaryKingdomAccent(id),
                                testTag = "bestiary_filter_$id"
                            )
                        }
                    }

                    Spacer(Modifier.height(Eldoria.S12))
                    Text(
                        text = "ARQUETIPO",
                        style = EldoriaType.caption,
                        color = Eldoria.TextLow,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(Eldoria.S4))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(archetypeScroll),
                        horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
                    ) {
                        EldoriaToggleChip(
                            text = "TODOS",
                            selected = archetypeFilter == BESTIARY_FILTER_ALL,
                            onClick = { archetypeFilter = BESTIARY_FILTER_ALL },
                            accent = Eldoria.Gold,
                            testTag = "bestiary_filter_archetype_all"
                        )
                        EldoriaBestiary.ARCHETYPES.forEach { archetype ->
                            EldoriaToggleChip(
                                text = archetype.name.uppercase(),
                                selected = archetypeFilter == archetype.id,
                                onClick = {
                                    archetypeFilter =
                                        if (archetypeFilter == archetype.id) BESTIARY_FILTER_ALL else archetype.id
                                },
                                accent = bestiaryToneColor(archetype.tone),
                                testTag = "bestiary_filter_${archetype.id.lowercase()}"
                            )
                        }
                    }

                    Spacer(Modifier.height(Eldoria.S12))
                    EldoriaToggleChip(
                        text = "SÓLO DESCUBIERTAS",
                        selected = onlyDiscovered,
                        onClick = { onlyDiscovered = !onlyDiscovered },
                        accent = Eldoria.Vitae,
                        icon = Icons.Filled.Search,
                        testTag = "bestiary_filter_discovered"
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(Modifier.height(Eldoria.S4))
                    EldoriaSectionTitle(
                        text = bestiarySectionLabel(kingdomFilter, archetypeFilter),
                        icon = Icons.Filled.MenuBook,
                        accent = Eldoria.Gold
                    )
                    EldoriaDivider(color = Eldoria.GoldDeep)
                }
            }

            if (filtered.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EldoriaEmptyState(
                        title = "Ninguna ficha coincide",
                        message = "Ese cruce de reino y arquetipo aún no tiene entradas en tu códice. " +
                            "Afloja los filtros o sal a cazar: el pergamino se escribe con sangre ajena.",
                        icon = Icons.Filled.Search,
                        accent = Eldoria.Gold,
                        actionLabel = "Quitar filtros",
                        onAction = {
                            kingdomFilter = BESTIARY_FILTER_ALL
                            archetypeFilter = BESTIARY_FILTER_ALL
                            onlyDiscovered = false
                        },
                        testTag = "bestiary_empty_state"
                    )
                }
            } else {
                items(
                    items = filtered,
                    key = { it.species.id }
                ) { entry ->
                    BestiaryTile(
                        entry = entry,
                        onClick = { selectedId = entry.species.id }
                    )
                }
            }
        }
    }

    EldoriaSheet(
        visible = selected != null,
        title = selected?.species?.name ?: "",
        onDismiss = { selectedId = null },
        edge = if (selected != null) {
            bestiaryToneEdge(EldoriaBestiary.archetype(selected.species.archetype).tone)
        } else EldoriaEdge.Gold
    ) {
        if (selected != null) {
            BestiaryDetail(entry = selected)
        }
    }
}

// ───────────────────────────── ficha de la rejilla ────────────────────────────

@Composable
private fun BestiaryTile(entry: BestiaryEntry, onClick: () -> Unit) {
    val species = entry.species
    val archetype = EldoriaBestiary.archetype(species.archetype)
    val edge = if (entry.discovered) bestiaryToneEdge(archetype.tone) else EldoriaEdge.Iron
    val accent = if (entry.discovered) bestiaryToneColor(archetype.tone) else Eldoria.Iron

    EldoriaFrame(
        modifier = Modifier
            .fillMaxWidth()
            .height(182.dp)
            .then(if (entry.discovered) Modifier.eldoriaPressable(onClick = onClick) else Modifier)
            .testTag("bestiary_entry_${species.id}"),
        edge = edge,
        corner = 10.dp,
        strokeWidth = Eldoria.StrokeMed,
        filigree = entry.discovered,
        rivets = false,
        glowPulse = false
    ) {
        if (entry.discovered) {
            Image(
                painter = painterResource(id = getEnemyPortraitRes(species.name, false)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Eldoria.Abyss.copy(alpha = 0.10f),
                                Eldoria.Abyss.copy(alpha = 0.55f),
                                Eldoria.Abyss.copy(alpha = 0.96f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text(
                    text = species.name,
                    style = EldoriaType.subheading,
                    color = Eldoria.TextHi,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = archetype.name.uppercase(),
                    style = EldoriaType.caption,
                    color = accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Eldoria.Abyss.copy(alpha = 0.88f))
                    .border(0.75.dp, accent.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "×${entry.kills}",
                    style = EldoriaType.caption,
                    color = Eldoria.TextGold,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Eldoria.Abyss.copy(alpha = 0.88f))
                    .border(0.75.dp, Eldoria.GoldDeep, RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "T${species.tier}",
                    style = EldoriaType.caption,
                    color = Eldoria.TextMid,
                    maxLines = 1
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0A0C13), Color(0xFF04050A), Color.Black)
                        )
                    )
            )
            EldoriaBeastSigil(
                seed = species.id.hashCode(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 22.dp)
                    .size(width = 108.dp, height = 96.dp),
                primary = Color(0xFF171B25),
                secondary = Color(0xFF05060B),
                stage = 1,
                animated = false
            )
            EldoriaRuneGlyph(
                seed = species.id.hashCode() + species.tier,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 26.dp)
                    .size(46.dp),
                color = Eldoria.Arcane.copy(alpha = 0.85f),
                strokeWidth = 1.6.dp,
                animated = true
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "???",
                    style = EldoriaType.title,
                    color = Eldoria.TextLow,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = bestiaryKingdomName(species.kingdomId).uppercase(),
                    style = EldoriaType.caption,
                    color = Eldoria.Iron,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─────────────────────────────── hoja de detalle ──────────────────────────────

@Composable
private fun BestiaryDetail(entry: BestiaryEntry) {
    val species = entry.species
    val archetype = EldoriaBestiary.archetype(species.archetype)
    val accent = bestiaryToneColor(archetype.tone)
    val lift = eldoriaFloat(periodMs = 4200, amplitude = 5.dp, label = "codexPortrait")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 520.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.Top) {
            EldoriaFrame(
                modifier = Modifier
                    .offset(y = lift)
                    .size(width = 108.dp, height = 124.dp),
                edge = bestiaryToneEdge(archetype.tone),
                corner = 10.dp,
                strokeWidth = Eldoria.StrokeBold,
                filigree = true,
                rivets = true,
                glowPulse = true
            ) {
                Image(
                    painter = painterResource(id = getEnemyPortraitRes(species.name, false)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Eldoria.Abyss.copy(alpha = 0.45f))
                            )
                        )
                )
            }
            Spacer(Modifier.width(Eldoria.S12))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = archetype.name.uppercase(),
                    style = EldoriaType.label,
                    color = accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(Eldoria.S6))
                Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)) {
                    EldoriaChip(
                        text = bestiaryKingdomName(species.kingdomId),
                        color = bestiaryKingdomAccent(species.kingdomId),
                        filled = true
                    )
                    EldoriaChip(
                        text = "Rango ${species.tier}",
                        color = Eldoria.Gold
                    )
                }
                Spacer(Modifier.height(Eldoria.S8))
                Text(
                    text = archetype.description,
                    style = EldoriaType.small,
                    color = Eldoria.TextMid,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(Eldoria.S12))
        EldoriaDivider(color = accent)
        Spacer(Modifier.height(Eldoria.S8))

        EldoriaPanel(
            modifier = Modifier.fillMaxWidth(),
            edge = EldoriaEdge.Iron,
            padding = PaddingValues(12.dp)
        ) {
            EldoriaKeyValueRow(
                label = "Movimiento firma",
                value = bestiaryPrettyMove(species.signatureMove),
                icon = Icons.Filled.Bolt,
                valueColor = accent
            )
            EldoriaKeyValueRow(
                label = "Debilidad",
                value = bestiaryPrettyElement(species.weakness),
                icon = Icons.Filled.Whatshot,
                valueColor = bestiaryElementColor(species.weakness)
            )
            EldoriaKeyValueRow(
                label = "Resistencia",
                value = bestiaryPrettyElement(species.resistance),
                icon = Icons.Filled.Shield,
                valueColor = bestiaryElementColor(species.resistance)
            )
            EldoriaKeyValueRow(
                label = "Bajas registradas",
                value = entry.kills.toString(),
                icon = Icons.Filled.CheckCircle,
                valueColor = Eldoria.TextGold
            )
        }

        Spacer(Modifier.height(Eldoria.S12))
        EldoriaSectionTitle(text = "Comportamiento en combate", accent = accent)
        Spacer(Modifier.height(Eldoria.S6))
        Text(
            text = archetype.behaviour,
            style = EldoriaType.body,
            color = Eldoria.TextMid
        )

        Spacer(Modifier.height(Eldoria.S12))
        EldoriaSectionTitle(text = "Del códice", accent = Eldoria.Gold)
        Spacer(Modifier.height(Eldoria.S6))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(8.dp))
                .background(Eldoria.sunkenBrush())
                .border(Eldoria.StrokeThin, Eldoria.GoldDeep.copy(alpha = 0.55f), CutCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "«${species.lore}»",
                style = EldoriaType.lore,
                color = Eldoria.ParchmentDim
            )
        }
        Spacer(Modifier.height(Eldoria.S8))
    }
}

// ────────────────────────────────── helpers ───────────────────────────────────

private fun bestiaryKingdomName(id: String): String =
    BESTIARY_KINGDOMS.firstOrNull { it.first == id }?.second ?: "Desconocido"

private fun bestiaryKingdomAccent(id: String): Color = when (id) {
    "eldoria" -> Eldoria.Vitae
    "drakenhold" -> Eldoria.Ember
    "frostgard" -> Eldoria.ManaBright
    "aethelgard" -> Eldoria.Arcane
    "solaria" -> Eldoria.GoldBright
    "aetheria" -> Eldoria.RarityUniversal
    else -> Eldoria.Gold
}

private fun bestiaryToneColor(tone: String): Color = when (tone.uppercase()) {
    "BLOOD" -> Eldoria.BloodBright
    "ARCANE" -> Eldoria.ArcaneBright
    "IRON" -> Eldoria.Silver
    "SILVER" -> Eldoria.Silver
    "VITAE" -> Eldoria.VitaeBright
    "EMBER" -> Eldoria.Ember
    else -> Eldoria.Gold
}

private fun bestiaryToneEdge(tone: String): EldoriaEdge = when (tone.uppercase()) {
    "BLOOD" -> EldoriaEdge.Blood
    "ARCANE" -> EldoriaEdge.Arcane
    "IRON" -> EldoriaEdge.Iron
    "SILVER" -> EldoriaEdge.Silver
    "VITAE" -> EldoriaEdge.Vitae
    "EMBER" -> EldoriaEdge.Ember
    else -> EldoriaEdge.Gold
}

private fun bestiaryPrettyElement(raw: String): String {
    val clean = raw.trim()
    if (clean.isEmpty()) return "—"
    return clean.substring(0, 1).uppercase() + clean.substring(1).lowercase()
}

private fun bestiaryPrettyMove(raw: String): String {
    val clean = raw.trim()
    if (clean.isEmpty()) return "—"
    return clean.split('_')
        .filter { it.isNotBlank() }
        .joinToString(" ") { bestiaryPrettyElement(it) }
}

private fun bestiaryElementColor(raw: String): Color = when (raw.uppercase()) {
    "FUEGO" -> Eldoria.Ember
    "HIELO" -> Eldoria.ManaBright
    "RAYO" -> Eldoria.Info
    "ARCANO" -> Eldoria.ArcaneBright
    "SAGRADO" -> Eldoria.GoldBright
    "SOMBRA" -> Eldoria.Arcane
    "VENENO" -> Eldoria.VitaeBright
    "FÍSICO" -> Eldoria.Silver
    else -> Eldoria.TextMid
}

private fun bestiarySectionLabel(kingdomFilter: String, archetypeFilter: String): String {
    val kingdom = if (kingdomFilter == BESTIARY_FILTER_ALL) "Todos los reinos"
    else bestiaryKingdomName(kingdomFilter)
    val archetype = if (archetypeFilter == BESTIARY_FILTER_ALL) null
    else EldoriaBestiary.ARCHETYPES.firstOrNull { it.id == archetypeFilter }?.name
    return if (archetype == null) kingdom else "$kingdom · $archetype"
}
