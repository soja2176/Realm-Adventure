package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.GameViewModel
import com.example.data.model.ExpeditionRoom
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaConfirmDialog
import com.example.ui.design.EldoriaCrackedStone
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmberField
import com.example.ui.design.EldoriaEmptyState
import com.example.ui.design.EldoriaFogLayer
import com.example.ui.design.EldoriaKeyValueRow
import com.example.ui.design.EldoriaLeyLines
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaScanlines
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaSheet
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaTorchLight
import com.example.ui.design.EldoriaType
import com.example.ui.design.EldoriaVignette

// ══════════════════════════════════════════════════════════════════════════════
//  DESCENSO — la pantalla que materializa el cambio total de UI del calabozo
//
//  A sangre total: nada de barra superior, nada de navegación, nada del gradiente
//  estándar. La antorcha es a la vez mecánica y ambientación: según se consume,
//  la penumbra se cierra sobre el jugador y el mapa se apaga.
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun ExpeditionRunScreen(viewModel: GameViewModel) {
    val run by viewModel.systems.expedition.collectAsState()
    val offer by viewModel.systems.expeditionOffer.collectAsState()
    val progress by viewModel.progressState.collectAsState()
    val autoExpedition by viewModel.isAutoExpedition.collectAsState()

    var askAbandon by remember { mutableStateOf(false) }

    val p = progress
    if (p == null || !run.active) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Eldoria.depthBrush(0)),
            contentAlignment = Alignment.Center
        ) {
            EldoriaVignette(modifier = Modifier.fillMaxSize(), strength = 0.7f)
            EldoriaEmptyState(
                title = "No hay ningún descenso activo",
                message = "La antorcha está apagada y el abismo, cerrado. Vuelve a las Puertas para elegir destino.",
                icon = Icons.Filled.Explore,
                accent = Eldoria.Ember,
                actionLabel = "VOLVER A LAS PUERTAS",
                onAction = { viewModel.systems.returnToExpeditionMap() },
                testTag = "expedition_inactive_state"
            )
        }
        return
    }

    val torchRatio = (run.torch / 100f).coerceIn(0f, 1f)
    val palette = expeditionPalette(run.paletteKey)
    val light = palette.first
    val dark = palette.second
    val lit = run.torch >= 40

    val blueprint = remember(run.dungeonId) { viewModel.systems.blueprintFor(run.dungeonId) }
    val floorLabels = blueprint?.floorLabels ?: emptyList()

    val rows = remember(run.rooms) {
        val deepest = run.rooms.maxOfOrNull { it.depth } ?: 0
        (0..deepest).map { d -> run.rooms.filter { it.depth == d }.sortedBy { it.column } }
    }

    val currentRoomLabel = remember(run.rooms, run.currentRoomId) {
        run.rooms.firstOrNull { it.id == run.currentRoomId }?.label ?: ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Eldoria.depthBrush(run.depth))
            .testTag("expedition_run_root")
    ) {
        // ── capas de ambiente (por debajo del contenido) ─────────────────────
        EldoriaCrackedStone(
            modifier = Modifier.fillMaxSize(),
            seed = (run.seed % 9_973L).toInt() + 11,
            color = dark,
            density = 18,
            alpha = 0.55f
        )
        EldoriaFogLayer(
            modifier = Modifier.fillMaxSize(),
            tint = dark,
            alpha = 0.22f
        )
        EldoriaEmberField(
            modifier = Modifier.fillMaxSize(),
            count = 30,
            tint = Eldoria.Ember,
            seed = 23 + run.depth
        )

        // ── contenido ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            ExpeditionHud(
                dungeonId = run.dungeonId,
                dungeonName = run.dungeonName,
                depth = run.depth,
                maxDepth = run.maxDepth,
                roomLabel = currentRoomLabel,
                torch = run.torch,
                hp = p.currentHp,
                maxHp = p.maxHp,
                mp = p.currentMp,
                maxMp = p.maxMp,
                shards = run.shards,
                keys = run.keys,
                boonIds = run.boons,
                sealIds = run.seals
            )

            Spacer(Modifier.height(Eldoria.S8))

            if (!lit) {
                ExpeditionRequirementNote(
                    text = "La antorcha agoniza: sólo distingues las salas contiguas.",
                    accent = Eldoria.Ember
                )
                Spacer(Modifier.height(Eldoria.S6))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                reverseLayout = true,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                rows.forEachIndexed { depthIndex, roomsAtDepth ->
                    item(key = "expedition_row_$depthIndex") {
                        ExpeditionMapRow(
                            rooms = roomsAtDepth,
                            depthIndex = depthIndex,
                            floorLabel = floorLabels.getOrNull(depthIndex) ?: "Profundidad ${depthIndex + 1}",
                            currentRoomId = run.currentRoomId,
                            availableIds = run.availableRoomIds,
                            lit = lit,
                            dungeonId = run.dungeonId,
                            onEnter = { id -> viewModel.systems.enterRoom(id) }
                        )
                    }
                    if (depthIndex < rows.lastIndex) {
                        item(key = "expedition_link_$depthIndex") {
                            ExpeditionConnector(
                                lower = roomsAtDepth,
                                upper = rows[depthIndex + 1],
                                availableIds = run.availableRoomIds,
                                accent = light,
                                animated = lit
                            )
                        }
                    }
                }
                item(key = "expedition_map_tail") { Spacer(Modifier.height(Eldoria.S16)) }
            }

            Spacer(Modifier.height(Eldoria.S6))

            ExpeditionRunLog(lines = run.log.takeLast(2))

            Spacer(Modifier.height(Eldoria.S8))

            // Con el descenso ya cerrado no se puede abandonar nada: el resumen manda.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SALAS LIMPIADAS ${run.roomsCleared} · ÉLITES ${run.elitesCleared}",
                        style = EldoriaType.caption,
                        color = Eldoria.TextLow,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = when {
                            run.availableRoomIds.isEmpty() -> "Sin salidas: el descenso se cierra."
                            autoExpedition -> "Marcha automática en curso…"
                            else -> "Elige la siguiente sala."
                        },
                        style = EldoriaType.small,
                        color = if (autoExpedition) Eldoria.Vitae else expeditionDepthAccent(run.depth),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!run.finished) {
                    Spacer(Modifier.width(Eldoria.S8))
                    EldoriaButton(
                        text = if (autoExpedition) "DETENER" else "AUTOMÁTICO",
                        onClick = { viewModel.toggleAutoExpedition() },
                        tone = if (autoExpedition) EldoriaTone.Vitae else EldoriaTone.Arcane,
                        size = EldoriaButtonSize.Small,
                        icon = Icons.Filled.DirectionsWalk,
                        testTag = "expedition_auto_btn"
                    )
                    Spacer(Modifier.width(Eldoria.S6))
                    EldoriaButton(
                        text = "ABANDONAR",
                        onClick = { askAbandon = true },
                        tone = EldoriaTone.Iron,
                        size = EldoriaButtonSize.Small,
                        icon = Icons.Filled.Warning,
                        testTag = "expedition_abandon_btn"
                    )
                }
            }
            if (autoExpedition && !run.finished) {
                Spacer(Modifier.height(Eldoria.S4))
                Text(
                    text = "El piloto entra solo en las salas y pelea por ti. Se detiene cuando " +
                        "haya que decidir algo y sigue en cuanto elijas.",
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow
                )
            }
        }

        // ── capa de ambiente (por encima del contenido) ──────────────────────
        // Sin foco de antorcha: molestaba a la vista y oscurecía el mapa según se
        // consumía la luz. Queda la caída de cenizas y un viñeteado fijo y suave.
        EldoriaVignette(
            modifier = Modifier.fillMaxSize(),
            strength = 0.45f,
            centerBiasY = 0.46f
        )

        // ── resumen final del descenso ───────────────────────────────────────
        if (run.finished) {
            ExpeditionSummaryOverlay(
                dungeonName = run.dungeonName,
                victory = run.victory,
                roomsCleared = run.roomsCleared,
                elitesCleared = run.elitesCleared,
                shards = run.shards,
                lootCount = run.runLoot.size,
                depthReached = run.depth + 1,
                maxDepth = run.maxDepth,
                onClaim = { viewModel.systems.claimExpeditionRewards() }
            )
        }
    }

    // ── oferta de sala ───────────────────────────────────────────────────────
    val activeOffer = offer
    EldoriaSheet(
        visible = activeOffer != null && !run.finished,
        title = activeOffer?.title ?: "",
        onDismiss = { viewModel.systems.dismissOffer() },
        edge = if (activeOffer?.kind == "BOON") EldoriaEdge.Arcane else EldoriaEdge.Ember
    ) {
        if (activeOffer != null) {
            val isBoon = activeOffer.kind == "BOON"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 430.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (activeOffer.description.isNotBlank()) {
                    Text(
                        text = activeOffer.description,
                        style = EldoriaType.lore,
                        color = Eldoria.TextMid
                    )
                    Spacer(Modifier.height(Eldoria.S12))
                }
                activeOffer.optionIds.forEachIndexed { index, optionId ->
                    ExpeditionOfferCard(
                        title = activeOffer.optionTitles.getOrNull(index) ?: optionId,
                        subtitle = activeOffer.optionSubtitles.getOrNull(index) ?: "",
                        tone = activeOffer.optionTones.getOrNull(index) ?: "GOLD",
                        isBoon = isBoon,
                        seed = optionId.hashCode(),
                        onClick = {
                            if (isBoon) viewModel.systems.chooseBoon(optionId)
                            else viewModel.systems.resolveRoomChoice(optionId)
                        },
                        testTag = "expedition_offer_option_$optionId",
                        modifier = Modifier.padding(bottom = Eldoria.S8)
                    )
                }
            }
        }
    }

    if (askAbandon) {
        EldoriaConfirmDialog(
            title = "¿Abandonar el descenso?",
            message = "Subes a la superficie con la MITAD del botín y la mitad de los fragmentos. " +
                "Todo lo demás se queda en ${run.dungeonName}.",
            confirmLabel = "Abandonar",
            onConfirm = {
                askAbandon = false
                viewModel.systems.abandonExpedition()
            },
            onDismiss = { askAbandon = false },
            tone = EldoriaTone.Blood,
            testTagPrefix = "expeditionAbandon"
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  FILA DE NODOS DE UNA PROFUNDIDAD
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionMapRow(
    rooms: List<ExpeditionRoom>,
    depthIndex: Int,
    floorLabel: String,
    currentRoomId: Int,
    availableIds: List<Int>,
    lit: Boolean,
    dungeonId: Int,
    onEnter: (Int) -> Unit
) {
    val accent = expeditionDepthAccent(depthIndex)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EldoriaDivider(
                modifier = Modifier.weight(1f),
                color = accent.copy(alpha = 0.45f),
                ornament = false
            )
            Text(
                text = "  ${depthIndex + 1} · ${floorLabel.uppercase()}  ",
                style = EldoriaType.caption,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            EldoriaDivider(
                modifier = Modifier.weight(1f),
                color = accent.copy(alpha = 0.45f),
                ornament = false
            )
        }
        Spacer(Modifier.height(2.dp))
        // Cada profundidad reúne DOS filas del grafo (y la última, además, al JEFE):
        // 5–7 nodos de 86.dp no caben en un móvil. Sin scroll, los últimos se medían
        // a 0 px y quedaban fuera del hit-testing: la run podía bloquearse.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Eldoria.S4, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top
        ) {
            rooms.forEach { room ->
                val state = when {
                    room.id == currentRoomId && !room.cleared -> ExpeditionNodeState.Current
                    room.id in availableIds -> ExpeditionNodeState.Available
                    room.cleared -> ExpeditionNodeState.Cleared
                    lit && room.revealed -> ExpeditionNodeState.Locked
                    else -> ExpeditionNodeState.Veiled
                }
                ExpeditionNode(
                    kind = room.kind,
                    label = room.label,
                    state = state,
                    onClick = { onEnter(room.id) },
                    testTag = "expedition_room_${room.id}",
                    dungeonId = dungeonId,
                    roomId = room.id
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  CONEXIONES ENTRE PROFUNDIDADES
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionConnector(
    lower: List<ExpeditionRoom>,
    upper: List<ExpeditionRoom>,
    availableIds: List<Int>,
    accent: Color,
    animated: Boolean
) {
    if (lower.isEmpty() || upper.isEmpty()) {
        Spacer(Modifier.height(10.dp))
        return
    }
    val nodes = remember(lower, upper) {
        val up = upper.indices.map { i -> Offset((i + 0.5f) / upper.size, 0.06f) }
        val down = lower.indices.map { i -> Offset((i + 0.5f) / lower.size, 0.94f) }
        up + down
    }
    val edges = remember(lower, upper, availableIds) {
        val list = ArrayList<Triple<Int, Int, Boolean>>()
        lower.forEachIndexed { j, room ->
            room.next.forEach { nextId ->
                val k = upper.indexOfFirst { it.id == nextId }
                if (k >= 0) {
                    val target = upper[k]
                    val open = target.id in availableIds || target.cleared
                    list.add(Triple(upper.size + j, k, open))
                }
            }
        }
        list
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        EldoriaLeyLines(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            edges = edges,
            color = accent,
            strokeWidth = 2.dp,
            animated = animated
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  REGISTRO CORTO
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionRunLog(lines: List<String>) {
    if (lines.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Eldoria.Abyss.copy(alpha = 0.55f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        lines.forEach { line ->
            Text(
                text = line,
                style = EldoriaType.caption,
                color = Eldoria.TextMid,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  RESUMEN FINAL
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionSummaryOverlay(
    dungeonName: String,
    victory: Boolean,
    roomsCleared: Int,
    elitesCleared: Int,
    shards: Int,
    lootCount: Int,
    depthReached: Int,
    maxDepth: Int,
    onClaim: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Eldoria.Scrim)
            // El velo tiene que COMER los toques: sin esto se pulsaba ABANDONAR (y
            // los nodos del mapa) a través de él y se perdía la mitad del botín.
            .pointerInput(Unit) { detectTapGestures { } },
        contentAlignment = Alignment.Center
    ) {
        EldoriaPanel(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 460.dp)
                .padding(horizontal = 4.dp),
            edge = if (victory) EldoriaEdge.Gold else EldoriaEdge.Iron,
            corner = Eldoria.R16,
            padding = PaddingValues(18.dp),
            glow = victory,
            filigree = true
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Eldoria.S8),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (victory) Icons.Filled.EmojiEvents else Icons.Filled.Inbox,
                    contentDescription = null,
                    tint = if (victory) Eldoria.GoldBright else Eldoria.Silver,
                    modifier = Modifier.size(44.dp)
                )
            }
            Text(
                text = if (victory) "DESCENSO COMPLETADO" else "DESCENSO INTERRUMPIDO",
                style = EldoriaType.title,
                color = if (victory) Eldoria.TextGold else Eldoria.TextHi,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = dungeonName,
                style = EldoriaType.small,
                color = Eldoria.TextMid,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Eldoria.S12))
            EldoriaSectionTitle(text = "BOTÍN DE LA RUN", icon = Icons.Filled.Diamond, accent = Eldoria.Gold)
            Spacer(Modifier.height(Eldoria.S4))

            EldoriaKeyValueRow(
                label = "Salas limpiadas",
                value = "$roomsCleared",
                icon = Icons.Filled.Explore
            )
            EldoriaKeyValueRow(
                label = "Élites derrotadas",
                value = "$elitesCleared",
                icon = Icons.Filled.MilitaryTech
            )
            EldoriaKeyValueRow(
                label = "Fragmentos de ánima",
                value = "$shards",
                icon = Icons.Filled.Diamond,
                valueColor = Eldoria.ArcaneBright
            )
            EldoriaKeyValueRow(
                label = "Piezas de botín",
                value = "$lootCount",
                icon = Icons.Filled.Inbox
            )
            EldoriaKeyValueRow(
                label = "Profundidad alcanzada",
                value = "$depthReached / ${maxDepth.coerceAtLeast(1)}",
                icon = Icons.Filled.LocalFireDepartment,
                valueColor = Eldoria.Ember
            )

            Spacer(Modifier.height(Eldoria.S16))
            ExpeditionPrimaryButton(
                text = "RECLAMAR Y SUBIR A LA SUPERFICIE",
                onClick = onClaim,
                tone = EldoriaTone.Gold,
                icon = Icons.Filled.EmojiEvents,
                testTag = "expedition_claim_btn"
            )
        }
    }
}
