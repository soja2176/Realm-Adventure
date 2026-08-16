package com.example.ui.talents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.GameJsonParser
import com.example.data.GameViewModel
import com.example.data.Talent
import com.example.data.content.EldoriaTalentEngine
import com.example.data.content.EldoriaTalents
import com.example.data.content.TalentBranch
import com.example.data.content.TalentDef
import com.example.data.formatGameNumber
import com.example.ui.art.EldoriaArt
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBanner
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaCrest
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmberField
import com.example.ui.design.EldoriaEmptyState
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaProgressRing
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaToggleChip
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaGlowLayer
import com.example.ui.design.eldoriaPressable

// ══════════════════════════════════════════════════════════════════════════════
//  RED DE TALENTOS — cien nodos por raza
//
//  EL PROBLEMA
//  El mapa celestial anterior colocaba nueve nodos en coordenadas escritas a
//  mano dentro de una Column con scroll. Con cien por raza eso es imposible por
//  dos motivos: no hay sitio en pantalla para un mapa libre de cien nodos, y
//  componer los cien a la vez tira los fotogramas del scroll.
//
//  LA SOLUCIÓN — dos ejes de navegación en vez de un mapa
//  · Eje horizontal: la RAMA (8 sendas). Sólo se mira una a la vez, así que en
//    pantalla nunca hay más de ~13 nodos.
//  · Eje vertical: el ESCALÓN (`tier`). Un escalón por fila dentro de una
//    LazyColumn, que además sólo compone lo visible.
//  El detalle se abre encima, anclado abajo, para que el árbol no pierda su
//  posición de scroll al tocar un nodo — con cien nodos, volver a encontrar
//  dónde estabas sería el peor castigo posible.
// ══════════════════════════════════════════════════════════════════════════════

/** Color de cada senda. Sirve de código visual: el jugador ubica la rama por el tono antes de leer. */
private fun branchColor(branch: TalentBranch): Color = when (branch) {
    TalentBranch.ARMAS -> Eldoria.EmberCore
    TalentBranch.DEFENSA -> Eldoria.Silver
    TalentBranch.ARCANO -> Eldoria.ArcaneBright
    TalentBranch.SOMBRA -> Eldoria.ManaBright
    TalentBranch.SANGRE -> Eldoria.BloodBright
    TalentBranch.FORTUNA -> Eldoria.GoldBright
    TalentBranch.BESTIA -> Eldoria.VitaeBright
    TalentBranch.LEGADO -> Eldoria.TextGold
}

private fun branchEdge(branch: TalentBranch): EldoriaEdge = when (branch) {
    TalentBranch.ARMAS -> EldoriaEdge.Ember
    TalentBranch.DEFENSA -> EldoriaEdge.Silver
    TalentBranch.ARCANO -> EldoriaEdge.Arcane
    TalentBranch.SOMBRA -> EldoriaEdge.Arcane
    TalentBranch.SANGRE -> EldoriaEdge.Blood
    TalentBranch.FORTUNA -> EldoriaEdge.Gold
    TalentBranch.BESTIA -> EldoriaEdge.Vitae
    TalentBranch.LEGADO -> EldoriaEdge.Gold
}

private fun branchTone(branch: TalentBranch): EldoriaTone = when (branch) {
    TalentBranch.ARMAS -> EldoriaTone.Ember
    TalentBranch.DEFENSA -> EldoriaTone.Silver
    TalentBranch.ARCANO, TalentBranch.SOMBRA -> EldoriaTone.Arcane
    TalentBranch.SANGRE -> EldoriaTone.Blood
    TalentBranch.BESTIA -> EldoriaTone.Vitae
    TalentBranch.FORTUNA, TalentBranch.LEGADO -> EldoriaTone.Gold
}

private fun branchIcon(branch: TalentBranch): ImageVector = when (branch) {
    TalentBranch.ARMAS -> Icons.Default.Gavel
    TalentBranch.DEFENSA -> Icons.Default.Shield
    TalentBranch.ARCANO -> Icons.Default.AutoAwesome
    TalentBranch.SOMBRA -> Icons.Default.DarkMode
    TalentBranch.SANGRE -> Icons.Default.Bloodtype
    TalentBranch.FORTUNA -> Icons.Default.Casino
    TalentBranch.BESTIA -> Icons.Default.Pets
    TalentBranch.LEGADO -> Icons.Default.AutoStories
}

/** Nivel al que se abre cada evolución. Son los mismos umbrales que ya usaban las razas. */
private fun evolutionLevel(tier: Int): Int = when (tier) {
    1 -> 20
    2 -> 50
    else -> 100
}

/** Todo lo que la pantalla necesita saber de un nodo, resuelto una sola vez. */
private data class NodeState(
    val def: TalentDef,
    val rank: Int,
    val unlocked: Boolean,
    val prereqRank: Int,
    val prereqName: String?
) {
    val isMax: Boolean get() = rank >= def.maxRank
    val prereqMet: Boolean get() = def.prerequisiteId == null || prereqRank >= 1
    fun canUpgrade(points: Int): Boolean = unlocked && prereqMet && !isMax && points > 0
}

@Composable
fun EldoriaTalentTreeScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val p = progress ?: return

    val race = p.charRace
    val defs = remember(race) { EldoriaTalents.forRace(race) }
    val branches = remember(race) { EldoriaTalents.branchesOf(race) }

    // Los rangos siguen viviendo en `talentsJson`; el árbol sólo los lee por id.
    // Así la pantalla no necesita ningún método nuevo del ViewModel.
    val ranks: Map<String, Int> = remember(p.talentsJson) {
        GameJsonParser.listFromJson<Talent>(p.talentsJson).associate { it.id to it.currentRank }
    }

    // La rama sobrevive a la rotación: perder la senda elegida en un árbol de
    // cien nodos obliga a volver a buscarla a mano.
    var selectedBranchName by rememberSaveable { mutableStateOf(branches.firstOrNull()?.name ?: "") }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedBranch = branches.firstOrNull { it.name == selectedBranchName } ?: branches.firstOrNull()

    val spentRanks = defs.sumOf { ranks[it.id] ?: 0 }
    val totalRanks = defs.sumOf { it.maxRank }.coerceAtLeast(1)
    val hasPoints = p.talentPointsAvailable > 0
    val evoTier = EldoriaTalentEngine.evolutionTierFor(p.charLevel)

    EldoriaScreen(
        depth = 1,
        embers = false,
        fog = true,
        vignetteStrength = 0.66f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        EldoriaEmberField(
            modifier = Modifier.matchParentSize(),
            count = 22,
            tint = Eldoria.ArcaneBright,
            periodMs = 11000,
            seed = 19,
            maxAlpha = 0.4f
        )

        if (defs.isEmpty()) {
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                edge = EldoriaEdge.Iron,
                padding = PaddingValues(4.dp)
            ) {
                EldoriaEmptyState(
                    title = "Sin red propia",
                    message = "La estirpe $race todavía no tiene árbol de talentos escrito.",
                    icon = Icons.Default.Schema,
                    accent = Eldoria.Arcane
                )
            }
            return@EldoriaScreen
        }

        // El árbol entero es UNA LazyColumn: cabecera, selectores y escalones son
        // items suyos. Metidos en una Column con scroll, la LazyColumn interna no
        // podría medirse y volveríamos a componer los cien nodos de golpe.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Eldoria.S8)
        ) {
            item(key = "banner") {
                EldoriaBanner(
                    title = "RED DE TALENTOS",
                    subtitle = "${EldoriaTalentEngine.evolutionName(race, evoTier)} · $spentRanks/$totalRanks rangos canalizados",
                    artRes = R.drawable.talent_tree_banner_1784843563984,
                    height = 112.dp,
                    edge = EldoriaEdge.Arcane,
                    crestSeed = 5150,
                    trailing = {
                        EldoriaProgressRing(
                            progress = spentRanks.toFloat() / totalRanks.toFloat(),
                            size = 62.dp,
                            stroke = 6.dp,
                            accent = Eldoria.Arcane,
                            centerLabel = "$spentRanks"
                        )
                    }
                )
            }

            item(key = "points") {
                EldoriaPanel(
                    edge = if (hasPoints) EldoriaEdge.Gold else EldoriaEdge.Iron,
                    corner = Eldoria.R12,
                    padding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    glow = hasPoints
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (hasPoints) Eldoria.TextGold else Eldoria.TextLow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(Eldoria.S8))
                            Column {
                                Text("PUNTOS SIN GASTAR", style = EldoriaType.label, color = Eldoria.TextMid)
                                Text(
                                    text = formatGameNumber(p.talentPointsAvailable),
                                    style = EldoriaType.numericBig,
                                    color = if (hasPoints) Eldoria.TextGold else Eldoria.TextLow
                                )
                            }
                        }
                        EldoriaButton(
                            text = "AUTO-ASIGNAR",
                            onClick = { viewModel.autoAllocateTalentPoints() },
                            enabled = hasPoints,
                            tone = EldoriaTone.Gold,
                            size = EldoriaButtonSize.Small,
                            icon = Icons.Default.Bolt,
                            testTag = "auto_assign_talents_button"
                        )
                    }
                }
            }

            // Selectores de rama. En fila desplazable porque ocho pastillas no
            // caben en el ancho de un móvil sin partirlas en dos alturas.
            item(key = "branches") {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S6),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(branches, key = { it.name }) { branch ->
                        val invested = defs.filter { it.branch == branch }.sumOf { ranks[it.id] ?: 0 }
                        EldoriaToggleChip(
                            text = if (invested > 0) "${branch.display} · $invested" else branch.display,
                            selected = branch == selectedBranch,
                            onClick = {
                                selectedBranchName = branch.name
                                selectedId = null
                            },
                            accent = branchColor(branch),
                            icon = branchIcon(branch),
                            testTag = "talent_branch_${branch.name}"
                        )
                    }
                }
            }

            if (selectedBranch != null) {
                val branchDefs = EldoriaTalents.branch(race, selectedBranch)
                val tiers = branchDefs.groupBy { it.tier }.toSortedMap()

                tiers.forEach { (tier, tierDefs) ->
                    item(key = "tier_${selectedBranch.name}_$tier") {
                        TierRow(
                            tier = tier,
                            defs = tierDefs,
                            allDefs = defs,
                            branch = selectedBranch,
                            ranks = ranks,
                            heroLevel = p.charLevel,
                            selectedId = selectedId,
                            onSelect = { selectedId = it }
                        )
                    }
                }
            }

            // Colchón para que el último escalón no quede debajo del detalle.
            item(key = "tail") { Spacer(Modifier.height(200.dp)) }
        }

        // ─── Detalle ───
        // Anclado abajo y por encima del árbol: al tocar un nodo no se pierde el
        // scroll, que con cien talentos es lo que más cuesta recuperar.
        val selectedDef = selectedId?.let { id -> defs.firstOrNull { it.id == id } }
        if (selectedDef != null) {
            TalentDetailPanel(
                state = nodeStateOf(selectedDef, defs, ranks, p.charLevel),
                race = race,
                points = p.talentPointsAvailable,
                modifier = Modifier.align(Alignment.BottomCenter),
                onClose = { selectedId = null },
                onUpgrade = { viewModel.allocateTalentPoint(selectedDef.id) }
            )
        }
    }
}

private fun nodeStateOf(
    def: TalentDef,
    defs: List<TalentDef>,
    ranks: Map<String, Int>,
    heroLevel: Int
): NodeState {
    val prereq = def.prerequisiteId?.let { id -> defs.firstOrNull { it.id == id } }
    return NodeState(
        def = def,
        rank = ranks[def.id] ?: 0,
        unlocked = EldoriaTalents.isUnlocked(def, heroLevel),
        prereqRank = prereq?.let { ranks[it.id] ?: 0 } ?: 0,
        prereqName = prereq?.name
    )
}

/** Un escalón completo: rótulo a la izquierda y sus nodos repartidos a lo ancho. */
@Composable
private fun TierRow(
    tier: Int,
    defs: List<TalentDef>,
    // El prerrequisito casi siempre vive en un escalon anterior, asi que hay que
    // resolverlo contra el arbol entero y no contra los nodos de esta fila.
    allDefs: List<TalentDef>,
    branch: TalentBranch,
    ranks: Map<String, Int>,
    heroLevel: Int,
    selectedId: String?,
    onSelect: (String) -> Unit
) {
    val accent = branchColor(branch)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EldoriaChip(text = "ESCALÓN $tier", color = accent)
            Spacer(Modifier.width(Eldoria.S8))
            EldoriaDivider(modifier = Modifier.weight(1f), color = accent.copy(alpha = 0.35f))
        }
        Spacer(Modifier.height(Eldoria.S6))

        // Máximo cuatro nodos por fila: por debajo de eso la lámina deja de
        // leerse en pantallas de móvil.
        defs.chunked(4).forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = Eldoria.S6),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
            ) {
                chunk.forEach { def ->
                    TalentNode(
                        state = nodeStateOf(def, allDefs, ranks, heroLevel),
                        branch = branch,
                        selected = def.id == selectedId,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelect(def.id) }
                    )
                }
                // Huecos vacíos para que un escalón de dos nodos no los estire al doble.
                repeat(4 - chunk.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

/**
 * Nodo del árbol.
 *
 * El arte todavía no existe (lo escribe otro equipo a partir de
 * [EldoriaTalents.artKeyManifest]), así que la lámina es opcional y el respaldo
 * NO es un hueco gris: es un blasón procedural sembrado con el id del talento,
 * que da a cada nodo una figura estable y distinta. La pantalla se ve terminada
 * hoy y no cambia de forma el día que lleguen los PNG.
 */
@Composable
private fun TalentNode(
    state: NodeState,
    branch: TalentBranch,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val def = state.def
    val accent = branchColor(branch)
    val locked = !state.unlocked
    val tint = when {
        locked -> Eldoria.TextLow
        state.isMax -> Eldoria.GoldBright
        state.rank > 0 -> accent
        else -> Eldoria.TextMid
    }
    val edge = when {
        locked -> EldoriaEdge.Iron
        state.isMax -> EldoriaEdge.Gold
        else -> branchEdge(branch)
    }
    val art = EldoriaArt.of(def.artKey)

    Column(
        modifier = modifier
            .then(if (selected) Modifier.eldoriaGlowLayer(accent, alpha = 0.30f, corner = Eldoria.R8, spread = 6.dp) else Modifier)
            .eldoriaPressable(onClick = onClick)
            .testTag("talent_node_${def.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EldoriaFrame(
            modifier = Modifier.fillMaxWidth().height(58.dp),
            edge = edge,
            corner = Eldoria.R8,
            strokeWidth = if (selected) Eldoria.StrokeBold else Eldoria.StrokeMed,
            filigree = false,
            glowPulse = false
        ) {
            Box(
                modifier = Modifier.matchParentSize().background(Eldoria.PanelSunken),
                contentAlignment = Alignment.Center
            ) {
                if (art != null) {
                    Image(
                        painter = painterResource(id = art),
                        contentDescription = def.name,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop,
                        colorFilter = if (locked) ColorFilter.tint(Eldoria.IronDeep) else null
                    )
                } else {
                    EldoriaCrest(
                        seed = def.id.hashCode(),
                        modifier = Modifier.size(34.dp),
                        primary = tint,
                        secondary = if (locked) Eldoria.IronDeep else Eldoria.Iron,
                        ornate = false
                    )
                }

                if (locked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = Eldoria.TextLow,
                        modifier = Modifier.align(Alignment.TopEnd).padding(3.dp).size(13.dp)
                    )
                } else if (state.isMax) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Eldoria.GoldBright,
                        modifier = Modifier.align(Alignment.TopEnd).padding(3.dp).size(13.dp)
                    )
                }

                // El rango va SIEMPRE escrito además de dibujado: con cien nodos,
                // contar rombos de un vistazo deja de ser viable.
                Text(
                    text = "${state.rank}/${def.maxRank}",
                    style = EldoriaType.caption,
                    color = if (locked) Eldoria.TextLow else Eldoria.TextHi,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(Eldoria.Abyss.copy(alpha = 0.75f))
                        .padding(horizontal = 5.dp)
                )
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text = def.name,
            style = EldoriaType.caption,
            color = if (locked) Eldoria.TextLow else Eldoria.TextMid,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/** Detalle del nodo elegido: lo que hace, lo que le falta y el botón de gastar el punto. */
@Composable
private fun TalentDetailPanel(
    state: NodeState,
    race: String,
    points: Int,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onUpgrade: () -> Unit
) {
    val def = state.def
    val accent = branchColor(def.branch)
    val locked = !state.unlocked
    val canUpgrade = state.canUpgrade(points)

    EldoriaPanel(
        modifier = modifier.fillMaxWidth(),
        edge = if (locked) EldoriaEdge.Iron else if (state.isMax) EldoriaEdge.Gold else branchEdge(def.branch),
        corner = Eldoria.R12,
        padding = PaddingValues(13.dp),
        glow = canUpgrade,
        filigree = true,
        testTag = "talent_detail_panel"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailSigil(state = state, accent = accent, locked = locked)
            Spacer(Modifier.width(Eldoria.S12))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = def.name,
                    style = EldoriaType.heading,
                    color = if (locked) Eldoria.TextMid else Eldoria.TextHi,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${def.branch.display} · escalón ${def.tier} · rango ${state.rank}/${def.maxRank}",
                    style = EldoriaType.caption,
                    color = accent
                )
            }
            Spacer(Modifier.width(Eldoria.S8))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar detalle",
                tint = Eldoria.TextLow,
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .eldoriaPressable(onClick = onClose)
                    .testTag("talent_detail_close")
            )
        }

        Spacer(Modifier.height(Eldoria.S8))
        EldoriaDivider(color = accent.copy(alpha = 0.6f))
        Spacer(Modifier.height(Eldoria.S8))

        Text(text = def.description, style = EldoriaType.body, color = Eldoria.TextMid)

        Spacer(Modifier.height(Eldoria.S12))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (def.evolutionTier > 0) {
                    RequirementRow(
                        met = state.unlocked,
                        text = "Exige ser ${EldoriaTalentEngine.evolutionName(race, def.evolutionTier)} " +
                            "(nivel ${evolutionLevel(def.evolutionTier)})"
                    )
                }
                if (state.prereqName != null) {
                    RequirementRow(
                        met = state.prereqMet,
                        text = "Requiere ${state.prereqName} (rango ≥ 1)"
                    )
                } else {
                    RequirementRow(met = true, text = "Sin requisitos previos")
                }
                if (state.isMax) {
                    RequirementRow(met = true, text = "Rango máximo alcanzado")
                } else {
                    RequirementRow(met = points > 0, text = "Cuesta 1 punto de talento")
                }
            }

            Spacer(Modifier.width(Eldoria.S8))

            EldoriaButton(
                text = when {
                    state.isMax -> "COMPLETADO"
                    locked -> "BLOQUEADO"
                    else -> "CANALIZAR"
                },
                onClick = { if (canUpgrade) onUpgrade() },
                enabled = canUpgrade,
                tone = if (state.isMax) EldoriaTone.Gold else branchTone(def.branch),
                size = EldoriaButtonSize.Medium,
                icon = when {
                    state.isMax -> Icons.Default.Star
                    locked -> Icons.Default.Lock
                    else -> Icons.Default.Bolt
                },
                testTag = "talent_upgrade_btn"
            )
        }
    }
}

/** Lámina del detalle, con el mismo respaldo procedural que el nodo. */
@Composable
private fun DetailSigil(state: NodeState, accent: Color, locked: Boolean) {
    val art = EldoriaArt.of(state.def.artKey)
    EldoriaFrame(
        modifier = Modifier.size(46.dp),
        edge = if (locked) EldoriaEdge.Iron else branchEdge(state.def.branch),
        corner = Eldoria.R8,
        strokeWidth = Eldoria.StrokeMed,
        filigree = false
    ) {
        Box(
            modifier = Modifier.matchParentSize().background(Eldoria.PanelSunken),
            contentAlignment = Alignment.Center
        ) {
            if (art != null) {
                Image(
                    painter = painterResource(id = art),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                    colorFilter = if (locked) ColorFilter.tint(Eldoria.IronDeep) else null
                )
            } else {
                EldoriaCrest(
                    seed = state.def.id.hashCode(),
                    modifier = Modifier.size(30.dp),
                    primary = if (locked) Eldoria.TextLow else accent,
                    secondary = Eldoria.Iron,
                    ornate = false
                )
            }
        }
    }
}

/** Línea de requisito: marca verde si se cumple, aspa roja si no. */
@Composable
private fun RequirementRow(met: Boolean, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (met) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (met) Eldoria.Success else Eldoria.Danger,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(Eldoria.S6))
        Text(
            text = text,
            style = EldoriaType.caption,
            color = if (met) Eldoria.TextMid else Eldoria.Danger,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
