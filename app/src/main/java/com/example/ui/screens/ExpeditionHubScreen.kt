package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.DUNGEONS_LIST
import com.example.data.GameJsonParser
import com.example.data.GameViewModel
import com.example.data.Item
import com.example.data.content.EldoriaExpeditions
import com.example.data.model.DungeonBlueprint
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBanner
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaConfirmDialog
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmptyState
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaLeyLines
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaSegmentedTabs
import com.example.ui.design.EldoriaSheet
import com.example.ui.design.EldoriaStatPill
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaPulse
import com.example.ui.getEnemyPortraitRes

// ══════════════════════════════════════════════════════════════════════════════
//  PUERTAS DEL ABISMO — vestíbulo de expediciones
//
//  Sustituye visualmente a la antigua lista de tarjetas de calabozo. Aquí se
//  elige destino, se eligen sellos y se enciende la antorcha. La progresión
//  vuelve a existir: si no tienes nivel, el botón está BLOQUEADO de verdad.
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun ExpeditionHubScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val torchStock by viewModel.systems.torchStock.collectAsState()
    val materials by viewModel.systems.materials.collectAsState()
    val run by viewModel.systems.expedition.collectAsState()

    val p = progress ?: return

    val blueprints = remember { viewModel.systems.availableBlueprints() }
    val seals = remember { viewModel.systems.availableSeals() }
    val treasures = remember { DUNGEONS_LIST.associateBy { it.id } }
    val completedIds = remember(p.completedDungeonsJson) {
        GameJsonParser.listFromJson<Int>(p.completedDungeonsJson).toSet()
    }

    var tab by remember { mutableStateOf(0) }
    var prepareId by remember { mutableStateOf<Int?>(null) }
    var chosenSeals by remember { mutableStateOf(listOf<String>()) }
    var askAbandon by remember { mutableStateOf(false) }

    val keys = materials["sealed_key"] ?: 0
    val shards = materials["anima_shard"] ?: 0

    val destinations = remember(tab, blueprints) {
        when (tab) {
            0 -> blueprints.filter { !it.isAbyss }
            1 -> blueprints.filter { it.isAbyss }
            else -> emptyList()
        }
    }

    EldoriaScreen(
        depth = 1,
        embers = true,
        fog = true,
        vignetteStrength = 0.52f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            EldoriaBanner(
                title = "PUERTAS DEL ABISMO",
                subtitle = "Veinte descensos. Una sola antorcha por bajada.",
                artRes = R.drawable.img_dungeon_door_1784674104372,
                height = 140.dp,
                edge = EldoriaEdge.Ember,
                crestSeed = 4181
            )

            Spacer(Modifier.height(Eldoria.S8))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
            ) {
                ExpeditionResourcePill(
                    icon = Icons.Filled.LocalFireDepartment,
                    value = "$torchStock",
                    label = "Antorchas",
                    accent = Eldoria.Ember,
                    modifier = Modifier.weight(1f)
                )
                ExpeditionResourcePill(
                    icon = Icons.Filled.Lock,
                    value = "$keys",
                    label = "Llaves",
                    accent = Eldoria.Silver,
                    modifier = Modifier.weight(1f)
                )
                ExpeditionResourcePill(
                    icon = Icons.Filled.Diamond,
                    value = "$shards",
                    label = "Fragmentos",
                    accent = Eldoria.ArcaneBright,
                    modifier = Modifier.weight(1f)
                )
            }

            // Única vía del juego para reponer antorchas: sin ella, tres descensos
            // dejaban el sistema de expediciones cerrado para siempre.
            Spacer(Modifier.height(Eldoria.S6))
            EldoriaButton(
                text = "COMPRAR ANTORCHA · ${viewModel.systems.torchPrice()} DE ORO",
                onClick = { viewModel.systems.buyTorches(1) },
                tone = EldoriaTone.Ember,
                size = EldoriaButtonSize.Small,
                icon = Icons.Filled.LocalFireDepartment,
                fullWidth = true,
                testTag = "expedition_buy_torch_btn"
            )

            if (run.active) {
                Spacer(Modifier.height(Eldoria.S8))
                ExpeditionResumeBand(
                    dungeonName = run.dungeonName,
                    depth = run.depth,
                    maxDepth = run.maxDepth,
                    torch = run.torch,
                    onResume = { viewModel.systems.resumeExpedition() },
                    onAbandon = { askAbandon = true }
                )
            }

            Spacer(Modifier.height(Eldoria.S12))

            EldoriaSegmentedTabs(
                options = listOf("CALABOZOS", "ABISMOS", "SELLOS"),
                selectedIndex = tab,
                onSelect = { tab = it },
                accent = if (tab == 1) Eldoria.Arcane else Eldoria.Gold,
                testTagPrefix = "expedition_tab_"
            )

            Spacer(Modifier.height(Eldoria.S8))

            if (tab == 2) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Eldoria.S8)
                ) {
                    item {
                        EldoriaSectionTitle(
                            text = "CÓDICE DE SELLOS",
                            icon = Icons.Filled.Lock,
                            accent = Eldoria.Arcane
                        )
                    }
                    item {
                        Text(
                            text = "Cada sello endurece el descenso y engorda la recompensa. " +
                                "Puedes llevar hasta tres a la vez.",
                            style = EldoriaType.lore,
                            color = Eldoria.TextMid
                        )
                    }
                    items(seals) { seal ->
                        ExpeditionSealCodexRow(
                            name = seal.name,
                            description = seal.description,
                            danger = seal.dangerMult,
                            reward = seal.rewardMult,
                            tone = seal.tone
                        )
                    }
                }
            } else if (destinations.isEmpty()) {
                EldoriaEmptyState(
                    title = "Ningún destino disponible",
                    message = "Los mapas del gremio están en blanco. Vuelve cuando el abismo respire otra vez.",
                    icon = Icons.Filled.Explore,
                    accent = Eldoria.Ember,
                    modifier = Modifier.weight(1f),
                    testTag = "expedition_empty_state"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Eldoria.S12)
                ) {
                    item {
                        EldoriaSectionTitle(
                            text = if (tab == 0) "CALABOZOS DEL REINO" else "ABISMOS SIN FONDO",
                            icon = if (tab == 0) Icons.Filled.Explore else Icons.Filled.Star,
                            accent = if (tab == 0) Eldoria.Gold else Eldoria.Arcane
                        )
                    }
                    items(destinations) { bp ->
                        val locked = p.charLevel < bp.levelReq
                        val status = when {
                            run.active && run.dungeonId == bp.dungeonId -> "EN CURSO"
                            completedIds.contains(bp.dungeonId) -> "CONQUISTADO"
                            else -> "NUEVO"
                        }
                        ExpeditionDestinationCard(
                            blueprint = bp,
                            treasure = treasures[bp.dungeonId]?.uniqueTreasure,
                            charLevel = p.charLevel,
                            locked = locked,
                            status = status,
                            onPrepare = {
                                prepareId = bp.dungeonId
                                chosenSeals = emptyList()
                            }
                        )
                    }
                    item { Spacer(Modifier.height(Eldoria.S16)) }
                }
            }
        }
    }

    // ── Hoja de preparación ──────────────────────────────────────────────────
    val target = blueprints.firstOrNull { it.dungeonId == prepareId }
    EldoriaSheet(
        visible = target != null,
        title = "PREPARAR EL DESCENSO",
        onDismiss = { prepareId = null },
        edge = if (target?.isAbyss == true) EldoriaEdge.Arcane else EldoriaEdge.Ember
    ) {
        if (target != null) {
            ExpeditionPrepareSheetBody(
                blueprint = target,
                charLevel = p.charLevel,
                torchStock = torchStock,
                runActive = run.active,
                seals = seals.map { Triple(it.id, it.name, it.description) },
                sealTones = seals.associate { it.id to it.tone },
                chosen = chosenSeals,
                onToggleSeal = { id ->
                    chosenSeals = if (chosenSeals.contains(id)) {
                        chosenSeals - id
                    } else {
                        val mandatory = EldoriaExpeditions.mandatorySeals(target.dungeonId)
                        if ((chosenSeals + mandatory).distinct().size >= 3) chosenSeals else chosenSeals + id
                    }
                },
                dangerOf = { list -> viewModel.systems.dangerMultiplier(list) },
                rewardOf = { list -> viewModel.systems.rewardMultiplier(list) },
                onStart = {
                    viewModel.systems.startExpedition(target.dungeonId, chosenSeals)
                    prepareId = null
                }
            )
        }
    }

    if (askAbandon) {
        EldoriaConfirmDialog(
            title = "¿Abandonar el descenso?",
            message = "Sales del abismo con la mitad del botín y la mitad de los fragmentos. " +
                "La expedición en ${run.dungeonName} se pierde para siempre.",
            confirmLabel = "Abandonar",
            onConfirm = {
                askAbandon = false
                viewModel.systems.abandonExpedition()
            },
            onDismiss = { askAbandon = false },
            tone = EldoriaTone.Blood,
            testTagPrefix = "expeditionHubAbandon"
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  BANDA DE EXPEDICIÓN EN CURSO
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionResumeBand(
    dungeonName: String,
    depth: Int,
    maxDepth: Int,
    torch: Int,
    onResume: () -> Unit,
    onAbandon: () -> Unit
) {
    val pulse = eldoriaPulse(periodMs = 1700, from = 0.45f, to = 1f, label = "resumeBand")
    EldoriaPanel(
        modifier = Modifier.fillMaxWidth(),
        edge = EldoriaEdge.Ember,
        corner = Eldoria.R12,
        padding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        glow = true
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = Eldoria.EmberCore.copy(alpha = (0.5f + 0.5f * pulse).coerceIn(0f, 1f)),
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.width(Eldoria.S8))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "EXPEDICIÓN EN CURSO",
                    style = EldoriaType.label,
                    color = Eldoria.Ember,
                    maxLines = 1
                )
                Text(
                    text = "$dungeonName · Profundidad ${(depth + 1).coerceAtMost(maxDepth.coerceAtLeast(1))}" +
                        "/${maxDepth.coerceAtLeast(1)} · Antorcha $torch",
                    style = EldoriaType.small,
                    color = Eldoria.TextMid,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.height(Eldoria.S8))
        Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)) {
            EldoriaButton(
                text = "REANUDAR",
                onClick = onResume,
                modifier = Modifier.weight(1f),
                tone = EldoriaTone.Ember,
                size = EldoriaButtonSize.Medium,
                icon = Icons.Filled.PlayArrow,
                testTag = "resume_expedition_btn"
            )
            EldoriaButton(
                text = "ABANDONAR",
                onClick = onAbandon,
                modifier = Modifier.weight(1f),
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Medium,
                testTag = "expedition_hub_abandon_btn"
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  TARJETA DE DESTINO
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionDestinationCard(
    blueprint: DungeonBlueprint,
    treasure: Item?,
    charLevel: Int,
    locked: Boolean,
    status: String,
    onPrepare: () -> Unit
) {
    val (light, dark) = expeditionPalette(blueprint.paletteKey)
    val edge = when {
        locked -> EldoriaEdge.Iron
        blueprint.isAbyss -> EldoriaEdge.Arcane
        status == "CONQUISTADO" -> EldoriaEdge.Gold
        else -> EldoriaEdge.Ember
    }
    val statusColor = when (status) {
        "CONQUISTADO" -> Eldoria.Gold
        "EN CURSO" -> Eldoria.Ember
        else -> Eldoria.Vitae
    }
    val depths = EldoriaExpeditions.defaultMaxDepth(blueprint.dungeonId).coerceAtLeast(1)

    Box(modifier = Modifier
        .fillMaxWidth()
        .testTag("expedition_card_${blueprint.dungeonId}")
    ) {
        EldoriaFrame(
            modifier = Modifier.fillMaxWidth(),
            edge = edge,
            corner = Eldoria.R16,
            filigree = !locked,
            rivets = true,
            glowPulse = !locked && status == "NUEVO"
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(94.dp)
                            .clip(CutCornerShape(10.dp))
                            .background(Eldoria.Abyss)
                            .border(Eldoria.StrokeMed, edge.brush(), CutCornerShape(10.dp))
                    ) {
                        Image(
                            painter = painterResource(id = getEnemyPortraitRes(blueprint.finalBossName, true)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            alpha = if (locked) 0.30f else 0.95f,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            dark.copy(alpha = 0.30f),
                                            Eldoria.Abyss.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )
                        if (locked) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = Eldoria.Silver.copy(alpha = 0.85f),
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(Eldoria.S12))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = blueprint.name,
                            style = EldoriaType.heading,
                            color = if (locked) Eldoria.TextLow else Eldoria.TextHi,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "${blueprint.species} · ${blueprint.ambience.lowercase()}",
                            style = EldoriaType.caption,
                            color = light.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(Eldoria.S6))
                        Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S4)) {
                            EldoriaChip(
                                text = "Nivel ${blueprint.levelReq}",
                                color = if (locked) Eldoria.Danger else Eldoria.Vitae,
                                icon = Icons.Filled.MilitaryTech
                            )
                            EldoriaChip(
                                text = status.lowercase().replaceFirstChar { it.uppercase() },
                                color = statusColor,
                                filled = status != "NUEVO"
                            )
                        }
                        if (blueprint.isAbyss) {
                            Spacer(Modifier.height(Eldoria.S4))
                            EldoriaChip(
                                text = "ABISMO · regla propia",
                                color = Eldoria.ArcaneBright,
                                icon = Icons.Filled.Star,
                                filled = true
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Eldoria.S8))
                Text(
                    text = blueprint.loreShort,
                    style = EldoriaType.lore,
                    color = Eldoria.TextMid,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(Eldoria.S8))
                EldoriaDivider(color = edge.mid.copy(alpha = 0.7f))
                Spacer(Modifier.height(Eldoria.S6))

                // Riel de profundidades
                ExpeditionDepthRail(
                    labels = blueprint.floorLabels.take(depths),
                    accent = if (locked) Eldoria.Iron else light,
                    unlocked = !locked
                )

                Spacer(Modifier.height(Eldoria.S8))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = if (locked) Eldoria.TextLow else Eldoria.Gold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(Eldoria.S6))
                    Text(
                        text = "Tesoro único: " + (treasure?.name ?: "Reliquia sellada del abismo"),
                        style = EldoriaType.small,
                        color = if (locked) Eldoria.TextLow else Eldoria.TextGold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Jefe final: ${blueprint.finalBossTitle}",
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(Eldoria.S12))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                ) {
                    EldoriaButton(
                        text = if (locked) "Requiere Nivel ${blueprint.levelReq}" else "DESCENDER",
                        onClick = onPrepare,
                        modifier = Modifier.weight(1f),
                        enabled = !locked,
                        tone = if (blueprint.isAbyss) EldoriaTone.Arcane else EldoriaTone.Ember,
                        size = EldoriaButtonSize.Medium,
                        icon = if (locked) Icons.Filled.Lock else Icons.Filled.Explore,
                        testTag = "start_dungeon_${blueprint.dungeonId}_button"
                    )
                    EldoriaIconButton(
                        icon = Icons.Filled.Build,
                        contentDescription = "Preparar sellos de ${blueprint.name}",
                        onClick = onPrepare,
                        tone = EldoriaTone.Iron,
                        size = 46.dp,
                        enabled = !locked,
                        testTag = "expedition_prepare_${blueprint.dungeonId}"
                    )
                }

                if (locked) {
                    Spacer(Modifier.height(Eldoria.S8))
                    ExpeditionRequirementNote(
                        text = "Requiere Nivel ${blueprint.levelReq}. Vas por el $charLevel: " +
                            "te faltan ${(blueprint.levelReq - charLevel).coerceAtLeast(1)} niveles.",
                        accent = Eldoria.Danger
                    )
                }
            }
        }
    }
}

/** Riel de profundidades dibujado con líneas ley: entrada → fondo. */
@Composable
private fun ExpeditionDepthRail(
    labels: List<String>,
    accent: Color,
    unlocked: Boolean
) {
    val n = labels.size.coerceAtLeast(1)
    val nodes = remember(n) { List(n) { i -> Offset((i + 0.5f) / n, 0.5f) } }
    val edges = remember(n, unlocked) {
        (0 until n - 1).map { i -> Triple(i, i + 1, unlocked) }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        ) {
            EldoriaLeyLines(
                modifier = Modifier.fillMaxSize(),
                nodes = nodes,
                edges = edges,
                color = accent,
                strokeWidth = 2.2.dp,
                animated = unlocked
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  HOJA DE PREPARACIÓN
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionPrepareSheetBody(
    blueprint: DungeonBlueprint,
    charLevel: Int,
    torchStock: Int,
    runActive: Boolean,
    seals: List<Triple<String, String, String>>,
    sealTones: Map<String, String>,
    chosen: List<String>,
    onToggleSeal: (String) -> Unit,
    dangerOf: (List<String>) -> Float,
    rewardOf: (List<String>) -> Float,
    onStart: () -> Unit
) {
    val mandatory = remember(blueprint.dungeonId) { EldoriaExpeditions.mandatorySeals(blueprint.dungeonId) }
    val effective = remember(chosen, mandatory) { (mandatory + chosen).distinct().take(3) }
    val danger = dangerOf(effective)
    val reward = rewardOf(effective)

    val blockers = buildList {
        if (runActive) add("Ya tienes una expedición en curso: reanúdala o abandónala primero.")
        if (charLevel < blueprint.levelReq) add("Requiere Nivel ${blueprint.levelReq} y vas por el $charLevel.")
        if (torchStock < 1) add("Necesitas al menos 1 antorcha: cómprala con el botón del vestíbulo antes de bajar.")
    }
    val canStart = blockers.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 430.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = blueprint.name,
            style = EldoriaType.heading,
            color = Eldoria.TextHi,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = blueprint.loreShort,
            style = EldoriaType.lore,
            color = Eldoria.TextMid
        )

        Spacer(Modifier.height(Eldoria.S12))

        Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)) {
            EldoriaStatPill(
                label = "Peligro",
                value = "+${((danger - 1f) * 100f).toInt()} %",
                icon = Icons.Filled.MilitaryTech,
                accent = Eldoria.Danger,
                modifier = Modifier.weight(1f)
            )
            EldoriaStatPill(
                label = "Recompensa",
                value = "+${((reward - 1f) * 100f).toInt()} %",
                icon = Icons.Filled.Diamond,
                accent = Eldoria.Gold,
                modifier = Modifier.weight(1f)
            )
            EldoriaStatPill(
                label = "Coste",
                value = "1 🕯️",
                icon = Icons.Filled.LocalFireDepartment,
                accent = Eldoria.Ember,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(Eldoria.S12))
        EldoriaSectionTitle(
            text = "SELLOS · ${effective.size}/3",
            icon = Icons.Filled.Lock,
            accent = Eldoria.Arcane
        )
        Spacer(Modifier.height(Eldoria.S6))

        if (mandatory.isNotEmpty()) {
            ExpeditionRequirementNote(
                text = "Este destino impone " +
                    mandatory.joinToString(", ") { EldoriaExpeditions.seal(it)?.name ?: it } +
                    ": no se puede quitar.",
                accent = Eldoria.Arcane
            )
            Spacer(Modifier.height(Eldoria.S6))
        }

        seals.forEach { (id, name, description) ->
            val forced = mandatory.contains(id)
            val selected = forced || chosen.contains(id)
            val accent = expeditionAccentOf(sealTones[id] ?: "IRON")
            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Eldoria.S6),
                edge = if (selected) EldoriaEdge.tone(expeditionToneOf(sealTones[id] ?: "IRON")) else EldoriaEdge.Iron,
                corner = Eldoria.R8,
                padding = PaddingValues(horizontal = 11.dp, vertical = 9.dp),
                onClick = if (forced) null else ({ onToggleSeal(id) }),
                testTag = "expedition_seal_$id"
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val r = size.minDimension / 2f
                            drawCircle(
                                color = if (selected) accent else Eldoria.Iron,
                                radius = r * 0.9f,
                                center = center,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.6.dp.toPx())
                            )
                            if (selected) {
                                drawCircle(color = accent, radius = r * 0.48f, center = center)
                            }
                        }
                    }
                    Spacer(Modifier.width(Eldoria.S8))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name + if (forced) " · obligatorio" else "",
                            style = EldoriaType.bodyStrong,
                            color = if (selected) accent else Eldoria.TextMid,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = description,
                            style = EldoriaType.caption,
                            color = Eldoria.TextLow,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Eldoria.S8))

        blockers.forEach { reason ->
            ExpeditionRequirementNote(text = reason, accent = Eldoria.Danger)
            Spacer(Modifier.height(Eldoria.S4))
        }

        Spacer(Modifier.height(Eldoria.S8))
        ExpeditionPrimaryButton(
            text = if (canStart) "ENCENDER LA ANTORCHA Y DESCENDER" else "DESCENSO BLOQUEADO",
            onClick = onStart,
            enabled = canStart,
            tone = if (blueprint.isAbyss) EldoriaTone.Arcane else EldoriaTone.Ember,
            icon = Icons.Filled.LocalFireDepartment,
            testTag = "expedition_start_btn"
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  CÓDICE DE SELLOS
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionSealCodexRow(
    name: String,
    description: String,
    danger: Float,
    reward: Float,
    tone: String
) {
    val accent = expeditionAccentOf(tone)
    EldoriaPanel(
        modifier = Modifier.fillMaxWidth(),
        edge = EldoriaEdge.tone(expeditionToneOf(tone)),
        corner = Eldoria.R8,
        padding = PaddingValues(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                style = EldoriaType.subheading,
                color = accent,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            EldoriaChip(text = "Peligro +${((danger - 1f) * 100f).toInt()} %", color = Eldoria.Danger)
            Spacer(Modifier.width(Eldoria.S4))
            EldoriaChip(text = "Botín +${((reward - 1f) * 100f).toInt()} %", color = Eldoria.Gold)
        }
        Spacer(Modifier.height(Eldoria.S4))
        Text(
            text = description,
            style = EldoriaType.small,
            color = Eldoria.TextMid
        )
    }
}
