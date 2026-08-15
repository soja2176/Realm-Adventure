package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.GameScreen
import com.example.data.GameViewModel
import com.example.data.model.MinigameRequest
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBanner
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaCounter
import com.example.ui.design.EldoriaCrest
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaKeyValueRow
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaStatPill
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaFloat

// ═══════════════════════════════════════════════════════════════════════════
//  LA TABERNA DEL GRIFO DORADO — seis mesas, seis maneras de ganarse la cena.
//  Cada mesa lleva su propio blasón procedural, su récord, su entrada y su
//  recompensa. Si no llevas oro encima, la mesa te lo dice a la cara.
// ═══════════════════════════════════════════════════════════════════════════

private class TavernTable(
    val id: String,
    val title: String,
    val tagline: String,
    val howTo: String,
    val reward: String,
    val icon: ImageVector,
    val crestSeed: Int,
    val edge: EldoriaEdge,
    val accent: Color
)

private val TAVERN_TABLES: List<TavernTable> = listOf(
    TavernTable(
        id = "YUNQUE",
        title = "El Yunque de Grommash",
        tagline = "El viejo orco no habla: golpea, y espera que sigas su compás.",
        howTo = "Toca al ritmo del martillo y clava cada golpe en la ventana ardiente.",
        reward = "Calidad de forja: cada golpe perfecto empuja la rareza de la pieza hacia arriba.",
        icon = Icons.Filled.Build,
        crestSeed = 1101,
        edge = EldoriaEdge.Ember,
        accent = Eldoria.Ember
    ),
    TavernTable(
        id = "GANZUA",
        title = "Ganzúa del Ladrón",
        tagline = "Tres ganzúas, un cerrojo y ninguna segunda oportunidad honesta.",
        howTo = "Detén la aguja dentro del sector estrecho antes de que la ganzúa se parta.",
        reward = "Sube un nivel la rareza del cofre y duplica el oro que guarda.",
        icon = Icons.Filled.VpnKey,
        crestSeed = 2202,
        edge = EldoriaEdge.Silver,
        accent = Eldoria.Silver
    ),
    TavernTable(
        id = "GLIFOS",
        title = "Glifos Rúnicos",
        tagline = "La mesa del mago errante, que cobra por enseñar lo que ya olvidaste.",
        howTo = "Memoriza la secuencia de runas encendidas y repítela sin fallar el orden.",
        reward = "Hierbas, cristal y pociones sacadas de la despensa del hechicero.",
        icon = Icons.Filled.AutoAwesome,
        crestSeed = 3303,
        edge = EldoriaEdge.Arcane,
        accent = Eldoria.ArcaneBright
    ),
    TavernTable(
        id = "EXCAVACION",
        title = "Excavación de la Cripta",
        tagline = "Ocho picadas compradas al enterrador. Lo que salga, tuyo es.",
        howTo = "Pica las celdas de la rejilla siguiendo las vetas hasta desenterrar el filón.",
        reward = "Materiales de artesanía escalados a la profundidad del calabozo.",
        icon = Icons.Filled.Explore,
        crestSeed = 4404,
        edge = EldoriaEdge.Iron,
        accent = Eldoria.IronEdge
    ),
    TavernTable(
        id = "ADIESTRAMIENTO",
        title = "Adiestramiento de Bestias",
        tagline = "El corral de atrás huele a fiera y a apuesta perdida.",
        howTo = "Arrastra a tu bestia entre los tres carriles esquivando los obstáculos.",
        reward = "Experiencia, disciplina y vínculo para la mascota que entrenes.",
        icon = Icons.Filled.Pets,
        crestSeed = 5505,
        edge = EldoriaEdge.Vitae,
        accent = Eldoria.VitaeBright
    ),
    TavernTable(
        id = "VIGILIA",
        title = "Vigilia del Campamento",
        tagline = "Nadie duerme gratis cuando el bosque respira al otro lado del fuego.",
        howTo = "Toca cada chispa que salta de la hoguera antes de que se apague sola.",
        reward = "Porcentaje de vida y maná restaurados en la próxima hoguera.",
        icon = Icons.Filled.Visibility,
        crestSeed = 6606,
        edge = EldoriaEdge.Gold,
        accent = Eldoria.GoldBright
    )
)

@Composable
fun MinigameHubScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val lastResult by viewModel.systems.lastMinigameResult.collectAsState()

    val gold = progress?.charGold ?: 0
    val heroLevel = (progress?.charLevel ?: 1).coerceAtLeast(1)
    val difficulty = (1 + heroLevel / 8).coerceIn(1, 12)

    val costs = remember(heroLevel) {
        TAVERN_TABLES.associate { it.id to viewModel.systems.minigameEntryCost(it.id) }
    }
    val scores = remember(lastResult, heroLevel) {
        TAVERN_TABLES.associate { it.id to viewModel.systems.bestScore(it.id) }
    }
    val playable = TAVERN_TABLES.count { gold >= (costs[it.id] ?: 0) }

    EldoriaScreen(
        depth = 0,
        embers = true,
        fog = false,
        vignetteStrength = 0.5f,
        backgroundArtRes = R.drawable.img_pet_grifo_dorado_1785007680820,
        backgroundArtAlpha = 0.13f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Eldoria.S12),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EldoriaIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver al mapa",
                        onClick = { viewModel.changeScreen(GameScreen.WORLD_MAP) },
                        tone = EldoriaTone.Iron,
                        size = 42.dp,
                        testTag = "minigames_back_btn"
                    )
                    Spacer(Modifier.width(Eldoria.S8))
                    EldoriaBanner(
                        title = "LA TABERNA DEL GRIFO DORADO",
                        subtitle = "Seis mesas abiertas · $playable a tu alcance con el oro que llevas",
                        modifier = Modifier.weight(1f),
                        artRes = R.drawable.img_shop_merchant_1784605357079,
                        height = 122.dp,
                        edge = EldoriaEdge.Gold,
                        crestSeed = 4711,
                        trailing = {
                            Column(horizontalAlignment = Alignment.End) {
                                EldoriaCounter(
                                    value = gold.toLong(),
                                    icon = Icons.Filled.MonetizationOn,
                                    accent = Eldoria.TextGold
                                )
                                Spacer(Modifier.height(Eldoria.S4))
                                Text(
                                    text = "Nivel $heroLevel",
                                    style = EldoriaType.caption,
                                    color = Eldoria.TextMid,
                                    maxLines = 1
                                )
                            }
                        }
                    )
                }
            }

            item {
                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = EldoriaEdge.Iron,
                    padding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    filigree = true
                ) {
                    EldoriaSectionTitle(
                        text = "Reglas de la casa",
                        icon = Icons.Filled.LocalBar,
                        accent = Eldoria.Gold
                    )
                    Spacer(Modifier.height(Eldoria.S6))
                    Text(
                        text = "El tabernero escala cada mesa a tu leyenda: cuanto más alto tu nivel, " +
                            "más rápido va el juego y más gorda la bolsa.",
                        style = EldoriaType.small,
                        color = Eldoria.TextMid
                    )
                    Spacer(Modifier.height(Eldoria.S8))
                    EldoriaKeyValueRow(
                        label = "Dificultad de esta noche",
                        value = "Grado $difficulty",
                        icon = Icons.Filled.TrendingUp,
                        valueColor = Eldoria.TextGold
                    )
                    EldoriaKeyValueRow(
                        label = "Oro en la bolsa",
                        value = gold.toString(),
                        icon = Icons.Filled.MonetizationOn,
                        valueColor = if (gold > 0) Eldoria.TextGold else Eldoria.Danger
                    )

                    val result = lastResult
                    if (result != null && result.id.isNotBlank()) {
                        Spacer(Modifier.height(Eldoria.S8))
                        EldoriaDivider(color = Eldoria.GoldDeep)
                        Spacer(Modifier.height(Eldoria.S8))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (result.success) Icons.Filled.EmojiEvents else Icons.Filled.SportsEsports,
                                contentDescription = null,
                                tint = if (result.success) Eldoria.Gold else Eldoria.TextLow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(Eldoria.S8))
                            Text(
                                text = if (result.success) {
                                    "Última partida en ${tavernTitleOf(result.id)}: ${result.score} puntos" +
                                        if (result.rating.isNotBlank()) " · ${result.rating}" else ""
                                } else {
                                    "La casa ganó la última mano en ${tavernTitleOf(result.id)}. Vuelve a sentarte."
                                },
                                style = EldoriaType.small,
                                color = if (result.success) Eldoria.TextHi else Eldoria.TextMid,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            item {
                EldoriaSectionTitle(
                    text = "Mesas de juego",
                    icon = Icons.Filled.SportsEsports,
                    accent = Eldoria.Gold,
                    trailing = {
                        Text(
                            text = "${TAVERN_TABLES.size} mesas",
                            style = EldoriaType.caption,
                            color = Eldoria.TextLow,
                            maxLines = 1
                        )
                    }
                )
            }

            items(
                items = TAVERN_TABLES,
                key = { it.id }
            ) { table ->
                val cost = costs[table.id] ?: 0
                val best = scores[table.id] ?: 0
                val affordable = gold >= cost
                TavernTableCard(
                    table = table,
                    cost = cost,
                    bestScore = best,
                    difficulty = difficulty,
                    affordable = affordable,
                    missingGold = (cost - gold).coerceAtLeast(0),
                    onPlay = {
                        viewModel.systems.openMinigame(
                            MinigameRequest(
                                id = table.id,
                                difficulty = difficulty,
                                title = table.title,
                                originScreen = "HUB"
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TavernTableCard(
    table: TavernTable,
    cost: Int,
    bestScore: Int,
    difficulty: Int,
    affordable: Boolean,
    missingGold: Int,
    onPlay: () -> Unit
) {
    val lift = eldoriaFloat(periodMs = 3600 + table.crestSeed % 700, amplitude = 4.dp, label = "crest_${table.id}")

    EldoriaPanel(
        modifier = Modifier.fillMaxWidth(),
        edge = table.edge,
        padding = PaddingValues(14.dp),
        glow = affordable,
        filigree = true,
        testTag = "minigame_card_${table.id}"
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .offset(y = lift)
                    .size(width = 66.dp, height = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                EldoriaCrest(
                    seed = table.crestSeed,
                    modifier = Modifier.fillMaxSize(),
                    primary = table.accent,
                    secondary = Eldoria.IronDeep,
                    ornate = true
                )
            }
            Spacer(Modifier.width(Eldoria.S12))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = table.icon,
                        contentDescription = null,
                        tint = table.accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(Eldoria.S6))
                    Text(
                        text = table.title,
                        style = EldoriaType.heading,
                        color = Eldoria.TextGold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(Eldoria.S4))
                Text(
                    text = table.tagline,
                    style = EldoriaType.lore,
                    color = Eldoria.TextLow,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(Eldoria.S8))
                Text(
                    text = table.howTo,
                    style = EldoriaType.small,
                    color = Eldoria.TextMid,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(Eldoria.S12))
        Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)) {
            EldoriaChip(
                text = if (bestScore > 0) "Récord $bestScore" else "Sin récord",
                icon = Icons.Filled.EmojiEvents,
                color = if (bestScore > 0) Eldoria.Gold else Eldoria.TextLow,
                filled = bestScore > 0
            )
            EldoriaChip(
                text = if (cost > 0) "Entrada $cost" else "Entrada libre",
                icon = Icons.Filled.MonetizationOn,
                color = when {
                    cost <= 0 -> Eldoria.Vitae
                    affordable -> Eldoria.TextGold
                    else -> Eldoria.Danger
                }
            )
        }

        Spacer(Modifier.height(Eldoria.S8))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(7.dp))
                .background(Eldoria.sunkenBrush())
                .border(Eldoria.StrokeThin, table.accent.copy(alpha = 0.32f), CutCornerShape(7.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = table.accent,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(Eldoria.S6))
                Text(
                    text = table.reward,
                    style = EldoriaType.small,
                    color = Eldoria.TextMid,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(Eldoria.S12))
        EldoriaDivider(color = table.accent.copy(alpha = 0.65f))
        Spacer(Modifier.height(Eldoria.S12))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EldoriaStatPill(
                label = "Grado",
                value = difficulty.toString(),
                icon = Icons.Filled.TrendingUp,
                accent = table.accent
            )
            Spacer(Modifier.width(Eldoria.S8))
            EldoriaButton(
                text = if (affordable) "JUGAR" else "Sin oro",
                onClick = onPlay,
                modifier = Modifier.weight(1f),
                enabled = affordable,
                tone = EldoriaTone.Gold,
                size = EldoriaButtonSize.Medium,
                icon = if (affordable) Icons.Filled.PlayArrow else null,
                testTag = "minigame_play_${table.id}"
            )
        }

        if (!affordable) {
            Spacer(Modifier.height(Eldoria.S6))
            Text(
                text = "La entrada cuesta $cost de oro y te faltan $missingGold. " +
                    "Vende botín o vuelve del calabozo con la bolsa llena.",
                style = EldoriaType.caption,
                color = Eldoria.Danger,
                maxLines = 3
            )
        }
    }
}

private fun tavernTitleOf(id: String): String =
    TAVERN_TABLES.firstOrNull { it.id.equals(id, ignoreCase = true) }?.title ?: "la taberna"
