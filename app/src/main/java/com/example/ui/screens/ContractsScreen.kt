package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.GameScreen
import com.example.data.GameViewModel
import com.example.data.content.EldoriaBestiary
import com.example.data.content.EldoriaMaterials
import com.example.data.content.KingdomAtlas
import com.example.data.model.ContractDef
import com.example.data.model.ContractProgress
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBanner
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaConfirmDialog
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaMotion
import com.example.ui.design.EldoriaRuneGlyph
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaScrollSheet
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaPulse
import com.example.ui.getItemImageRes

// ═══════════════════════════════════════════════════════════════════════════
//  TABLÓN DE CONTRATOS — pergamino clavado en la pared del gremio.
//  Dentro del pergamino todo se escribe con tinta oscura: ningún texto claro.
//  Hasta 3 encargos en curso, tablero de 6 disponibles, y nada de botones
//  muertos: si no puedes aceptar, el pergamino te dice por qué.
// ═══════════════════════════════════════════════════════════════════════════

private const val CONTRACTS_MAX_ACTIVE = 3

private val ContractsInk = Eldoria.ParchmentInk
private val ContractsInkSoft = Color(0xFF5B4A32)
private val ContractsInkGold = Color(0xFF7C5A12)
private val ContractsInkGoldDeep = Color(0xFF4B3607)
private val ContractsInkGreen = Color(0xFF2F5A28)
private val ContractsInkRed = Color(0xFF7A2015)
private val ContractsInkBlue = Color(0xFF1F4468)

@Composable
fun ContractsScreen(viewModel: GameViewModel) {
    val contracts by viewModel.systems.contracts.collectAsState()
    val board by viewModel.systems.contractBoard.collectAsState()
    val kingdomBoard by viewModel.systems.kingdomBoard.collectAsState()
    val progress by viewModel.progressState.collectAsState()
    val heroLevel = progress?.charLevel ?: 1

    val kingdomShortName = remember(progress?.currentX, progress?.currentY) {
        val x = progress?.currentX ?: 0
        val y = progress?.currentY ?: 0
        KingdomAtlas.dataForCoords(x, y).name
            .replace("Reino de ", "")
            .replace("Reino Celestial de ", "")
    }

    LaunchedEffect(heroLevel) {
        viewModel.systems.refreshContracts(false)
        viewModel.systems.refreshKingdomBoard(
            progress?.currentX ?: 0,
            progress?.currentY ?: 0,
            heroLevel
        )
    }

    val active = remember(contracts) { contracts.filter { !it.claimed } }
    val sealedOnes = remember(contracts) { contracts.filter { it.claimed }.takeLast(3).asReversed() }
    val takenDefIds = remember(active) { active.map { it.defId }.toSet() }
    val slotsFree = (CONTRACTS_MAX_ACTIVE - active.size).coerceAtLeast(0)

    var abandonTarget by remember { mutableStateOf<ContractProgress?>(null) }

    EldoriaScreen(
        depth = 0,
        embers = false,
        fog = true,
        vignetteStrength = 0.58f,
        backgroundArtRes = R.drawable.merchant_stall_banner_1784845825754,
        backgroundArtAlpha = 0.16f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EldoriaIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver al mapa",
                    onClick = { viewModel.changeScreen(GameScreen.WORLD_MAP) },
                    tone = EldoriaTone.Iron,
                    size = 42.dp,
                    testTag = "contracts_back_btn"
                )
                Spacer(Modifier.width(Eldoria.S8))
                EldoriaBanner(
                    title = "TABLÓN DE CONTRATOS",
                    subtitle = "Gremio de Ciudad Alba · ${active.size}/$CONTRACTS_MAX_ACTIVE encargos en curso",
                    modifier = Modifier.weight(1f),
                    artRes = R.drawable.wandering_merchant_1784845746333,
                    height = 112.dp,
                    edge = EldoriaEdge.Gold,
                    crestSeed = 3307,
                    trailing = {
                        EldoriaIconButton(
                            icon = Icons.Filled.Refresh,
                            contentDescription = "Renovar el tablón",
                            onClick = {
                                viewModel.systems.refreshContracts(true)
                                viewModel.systems.refreshKingdomBoard(
                                    progress?.currentX ?: 0,
                                    progress?.currentY ?: 0,
                                    heroLevel,
                                    force = true
                                )
                            },
                            tone = EldoriaTone.Gold,
                            size = 44.dp,
                            testTag = "contracts_refresh_btn"
                        )
                    }
                )
            }

            Spacer(Modifier.height(Eldoria.S12))

            EldoriaScrollSheet(
                modifier = Modifier.fillMaxWidth(),
                padding = PaddingValues(horizontal = 18.dp, vertical = 22.dp)
            ) {
                Text(
                    text = "TABLÓN DE CONTRATOS",
                    style = EldoriaType.display,
                    color = ContractsInk,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Eldoria.S4))
                Text(
                    text = "«Se paga al entregar. No se pregunta cómo.»",
                    style = EldoriaType.lore,
                    color = ContractsInkSoft,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Eldoria.S12))
                ContractsInkRule()
                Spacer(Modifier.height(Eldoria.S16))

                ContractsInkSectionTitle(
                    text = "Encargos en curso",
                    trailing = "${active.size} / $CONTRACTS_MAX_ACTIVE"
                )
                Spacer(Modifier.height(Eldoria.S8))

                if (active.isEmpty()) {
                    ContractsInkEmpty(
                        title = "El zurrón está vacío",
                        message = "No llevas ningún encargo firmado. Elige hasta tres del tablero de abajo: " +
                            "el gremio paga en oro, experiencia y materiales de forja."
                    )
                } else {
                    active.forEachIndexed { index, contract ->
                        if (index > 0) Spacer(Modifier.height(Eldoria.S12))
                        ContractsActiveRow(
                            contract = contract,
                            onClaim = { viewModel.systems.claimContract(contract.id) },
                            onAbandon = { abandonTarget = contract }
                        )
                    }
                }

                Spacer(Modifier.height(Eldoria.S20))
                ContractsInkRule()
                Spacer(Modifier.height(Eldoria.S16))

                ContractsInkSectionTitle(
                    text = "Tablero del gremio",
                    trailing = "${board.size} disponibles"
                )
                Spacer(Modifier.height(Eldoria.S8))

                if (board.isEmpty()) {
                    ContractsInkEmpty(
                        title = "El tablón está desnudo",
                        message = "Nadie ha clavado encargos nuevos todavía. Toca el sello de renovación " +
                            "de la cabecera para que el gremio vuelva a llenarlo."
                    )
                } else {
                    board.forEachIndexed { index, def ->
                        if (index > 0) Spacer(Modifier.height(Eldoria.S12))
                        val alreadyTaken = def.id in takenDefIds
                        ContractsBoardRow(
                            def = def,
                            heroLevel = heroLevel,
                            slotsFree = slotsFree,
                            alreadyTaken = alreadyTaken,
                            onAccept = { viewModel.systems.acceptContract(def.id) }
                        )
                    }
                }

                // ─── Encargos del reino que se pisa ───
                // Son locales: apuntan a las bestias y jefes de ESTA tierra y se
                // rehacen al cruzar una frontera.
                Spacer(Modifier.height(Eldoria.S20))
                ContractsInkRule()
                Spacer(Modifier.height(Eldoria.S16))

                ContractsInkSectionTitle(
                    text = "Encargos de $kingdomShortName",
                    trailing = "${kingdomBoard.size} locales"
                )
                Spacer(Modifier.height(Eldoria.S4))
                Text(
                    text = "Sólo valen aquí. Al cruzar una frontera, el alguacil del " +
                        "reino siguiente clavará los suyos.",
                    style = EldoriaType.caption,
                    color = ContractsInkSoft,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Eldoria.S8))

                if (kingdomBoard.isEmpty()) {
                    ContractsInkEmpty(
                        title = "Sin encargos locales",
                        message = "Este reino no tiene nada que pedirte ahora mismo. " +
                            "Renueva el tablón desde la cabecera o cruza a otras tierras."
                    )
                } else {
                    kingdomBoard.forEachIndexed { index, def ->
                        if (index > 0) Spacer(Modifier.height(Eldoria.S12))
                        ContractsBoardRow(
                            def = def,
                            heroLevel = heroLevel,
                            slotsFree = slotsFree,
                            alreadyTaken = def.id in takenDefIds,
                            onAccept = { viewModel.systems.acceptKingdomContract(def.id) }
                        )
                    }
                }

                if (sealedOnes.isNotEmpty()) {
                    Spacer(Modifier.height(Eldoria.S20))
                    ContractsInkRule()
                    Spacer(Modifier.height(Eldoria.S16))
                    ContractsInkSectionTitle(text = "Sellados y cobrados", trailing = null)
                    Spacer(Modifier.height(Eldoria.S8))
                    sealedOnes.forEach { done ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = ContractsInkGreen,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(Eldoria.S6))
                            Text(
                                text = done.title,
                                style = EldoriaType.small,
                                color = ContractsInkSoft,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "COBRADO",
                                style = EldoriaType.caption,
                                color = ContractsInkGreen,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Eldoria.S8))
            }

            Spacer(Modifier.height(Eldoria.S24))
        }
    }

    val target = abandonTarget
    if (target != null) {
        EldoriaConfirmDialog(
            title = "¿Romper el contrato?",
            message = "«${target.title}» volverá al tablón y perderás el progreso " +
                "(${target.progress}/${target.amount}). El gremio no olvida a quien se raja.",
            confirmLabel = "Abandonar",
            onConfirm = {
                viewModel.systems.abandonContract(target.id)
                abandonTarget = null
            },
            onDismiss = { abandonTarget = null },
            dismissLabel = "Seguir",
            tone = EldoriaTone.Blood,
            testTagPrefix = "contract_abandon_dialog_"
        )
    }
}

// ───────────────────────── contrato activo (en el zurrón) ─────────────────────

@Composable
private fun ContractsActiveRow(
    contract: ContractProgress,
    onClaim: () -> Unit,
    onAbandon: () -> Unit
) {
    val done = contract.completed || contract.progress >= contract.amount
    val accent = contractsKindColor(contract.kind)
    val pulse = if (done) eldoriaPulse(periodMs = 1500, from = 0.35f, to = 1f, label = "claimReady") else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF1E7CE).copy(alpha = 0.55f),
                        Color(0xFFDCCDA6).copy(alpha = 0.35f)
                    )
                )
            )
            .border(
                if (done) Eldoria.StrokeMed else Eldoria.StrokeThin,
                if (done) ContractsInkGold.copy(alpha = (0.55f + 0.45f * pulse).coerceIn(0f, 1f))
                else ContractsInk.copy(alpha = 0.42f),
                CutCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = contractsKindIcon(contract.kind),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(Eldoria.S6))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contract.title,
                        style = EldoriaType.heading,
                        color = ContractsInk,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${contract.kind.uppercase()} · ${contractsTargetLabel(contract.kind, contract.target)}",
                        style = EldoriaType.caption,
                        color = accent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(Eldoria.S8))
                ContractsInkSeal(tier = contract.tier)
            }

            Spacer(Modifier.height(Eldoria.S8))
            Text(
                text = contract.description,
                style = EldoriaType.small,
                color = ContractsInkSoft,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(Eldoria.S12))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (done) "COMPLETADO" else "PROGRESO",
                    style = EldoriaType.caption,
                    color = if (done) ContractsInkGreen else ContractsInkSoft,
                    maxLines = 1
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${contract.progress.coerceAtMost(contract.amount)} / ${contract.amount}",
                    style = EldoriaType.numeric,
                    color = if (done) ContractsInkGreen else ContractsInk,
                    maxLines = 1
                )
            }
            Spacer(Modifier.height(Eldoria.S4))
            ContractsInkProgressBar(
                current = contract.progress,
                max = contract.amount,
                complete = done
            )

            Spacer(Modifier.height(Eldoria.S12))
            ContractsRewardStrip(
                gold = contract.goldReward,
                exp = contract.expReward,
                materialId = contract.materialReward,
                materialQty = contract.materialQty
            )

            Spacer(Modifier.height(Eldoria.S12))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EldoriaButton(
                    text = "Abandonar",
                    onClick = onAbandon,
                    modifier = Modifier.weight(1f),
                    tone = EldoriaTone.Iron,
                    size = EldoriaButtonSize.Small,
                    testTag = "contract_abandon_${contract.id}"
                )
                EldoriaButton(
                    text = if (done) "RECLAMAR" else "En curso",
                    onClick = onClaim,
                    modifier = Modifier.weight(1.4f),
                    enabled = done,
                    tone = EldoriaTone.Gold,
                    size = EldoriaButtonSize.Small,
                    icon = if (done) Icons.Filled.CheckCircle else null,
                    testTag = "contract_claim_${contract.id}"
                )
            }
            if (!done) {
                Spacer(Modifier.height(Eldoria.S4))
                Text(
                    text = "Faltan ${(contract.amount - contract.progress).coerceAtLeast(1)} para poder cobrarlo.",
                    style = EldoriaType.caption,
                    color = ContractsInkSoft,
                    maxLines = 2
                )
            }
        }
    }
}

// ─────────────────────────── contrato del tablero ─────────────────────────────

@Composable
private fun ContractsBoardRow(
    def: ContractDef,
    heroLevel: Int,
    slotsFree: Int,
    alreadyTaken: Boolean,
    onAccept: () -> Unit
) {
    val accent = contractsKindColor(def.kind)
    val blockedReason = when {
        alreadyTaken -> "Ya está firmado y en tu zurrón."
        slotsFree <= 0 -> "Ya llevas $CONTRACTS_MAX_ACTIVE encargos: abandona o cobra uno antes."
        else -> null
    }
    val hardTier = def.tier >= 5 && heroLevel < 30

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(8.dp))
            .background(Color(0xFFEFE4C6).copy(alpha = 0.34f))
            .border(Eldoria.StrokeThin, ContractsInk.copy(alpha = 0.30f), CutCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = contractsKindIcon(def.kind),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(Eldoria.S6))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = def.title,
                        style = EldoriaType.subheading,
                        color = ContractsInk,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${def.kind.uppercase()} · ${contractsTargetLabel(def.kind, def.target)} ×${def.amount}",
                        style = EldoriaType.caption,
                        color = accent,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(Eldoria.S8))
                ContractsInkSeal(tier = def.tier)
            }

            Spacer(Modifier.height(Eldoria.S6))
            Text(
                text = def.description,
                style = EldoriaType.small,
                color = ContractsInkSoft,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(Eldoria.S8))
            ContractsRewardStrip(
                gold = def.goldReward,
                exp = def.expReward,
                materialId = def.materialReward,
                materialQty = def.materialQty
            )

            if (hardTier && blockedReason == null) {
                Spacer(Modifier.height(Eldoria.S6))
                Text(
                    text = "Aviso del gremio: encargo de rango ${def.tier}. Con nivel $heroLevel te va a costar sangre.",
                    style = EldoriaType.caption,
                    color = ContractsInkRed,
                    maxLines = 2
                )
            }

            Spacer(Modifier.height(Eldoria.S12))
            EldoriaButton(
                text = if (alreadyTaken) "Ya firmado" else "ACEPTAR",
                onClick = onAccept,
                fullWidth = true,
                enabled = blockedReason == null,
                tone = EldoriaTone.Vitae,
                size = EldoriaButtonSize.Small,
                testTag = "contract_accept_${def.id}"
            )
            if (blockedReason != null) {
                Spacer(Modifier.height(Eldoria.S4))
                Text(
                    text = blockedReason,
                    style = EldoriaType.caption,
                    color = ContractsInkRed,
                    maxLines = 2
                )
            }
        }
    }
}

// ───────────────────────────── piezas de pergamino ────────────────────────────

@Composable
private fun ContractsRewardStrip(gold: Int, exp: Int, materialId: String, materialQty: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContractsRewardTag(
            icon = Icons.Filled.MonetizationOn,
            label = gold.toString(),
            color = ContractsInkGold
        )
        Spacer(Modifier.width(Eldoria.S8))
        ContractsRewardTag(
            icon = Icons.Filled.Star,
            label = "$exp EXP",
            color = ContractsInkBlue
        )
        if (materialId.isNotBlank() && materialQty > 0) {
            Spacer(Modifier.width(Eldoria.S8))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(ContractsInk.copy(alpha = 0.09f))
                    .border(0.75.dp, ContractsInk.copy(alpha = 0.32f), RoundedCornerShape(50))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                ContractsMaterialIcon(materialId)
                Spacer(Modifier.width(Eldoria.S4))
                Text(
                    text = "${EldoriaMaterials.name(materialId)} ×$materialQty",
                    style = EldoriaType.caption,
                    color = ContractsInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ContractsRewardTag(icon: ImageVector, label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.13f))
            .border(0.75.dp, color.copy(alpha = 0.42f), RoundedCornerShape(50))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(13.dp)
        )
        Spacer(Modifier.width(Eldoria.S4))
        Text(
            text = label,
            style = EldoriaType.caption,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun ContractsMaterialIcon(materialId: String) {
    val def = remember(materialId) { EldoriaMaterials.def(materialId) }
    val art = def?.imageResName ?: ""
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(ContractsInk.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        if (art.isNotBlank()) {
            Image(
                painter = painterResource(id = getItemImageRes(art, "MATERIAL")),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            EldoriaRuneGlyph(
                seed = materialId.hashCode(),
                modifier = Modifier.size(14.dp),
                color = ContractsInkGoldDeep,
                strokeWidth = 1.dp,
                animated = false
            )
        }
    }
}

@Composable
private fun ContractsInkProgressBar(current: Int, max: Int, complete: Boolean) {
    val safeMax = max.coerceAtLeast(1)
    val ratio = (current.toFloat() / safeMax.toFloat()).coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(durationMillis = 460, easing = EldoriaMotion.easeOut),
        label = "contractInkBar"
    )
    val fillTop = if (complete) ContractsInkGreen else ContractsInkGold
    val fillBottom = if (complete) Color(0xFF1E3B1A) else ContractsInkGoldDeep

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(13.dp)
    ) {
        val w = size.width
        val h = size.height
        if (w <= 1f || h <= 1f) return@Canvas
        val radius = CornerRadius(3f, 3f)

        drawRoundRect(
            color = ContractsInk.copy(alpha = 0.13f),
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = radius
        )
        if (animated > 0.004f) {
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(fillTop, fillBottom)),
                topLeft = Offset.Zero,
                size = Size(w * animated, h),
                cornerRadius = radius
            )
        }
        // muescas de recuento, como marcas de tiza
        val ticks = safeMax.coerceAtMost(12)
        if (ticks > 1) {
            for (i in 1 until ticks) {
                val x = w * i / ticks
                drawLine(
                    color = ContractsInk.copy(alpha = 0.22f),
                    start = Offset(x, h * 0.22f),
                    end = Offset(x, h * 0.78f),
                    strokeWidth = 1f,
                    cap = StrokeCap.Round
                )
            }
        }
        drawRoundRect(
            color = ContractsInk.copy(alpha = 0.55f),
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = radius,
            style = Stroke(width = 1.2f)
        )
    }
}

@Composable
private fun ContractsInkSeal(tier: Int) {
    val t = tier.coerceIn(1, 6)
    Box(
        modifier = Modifier.size(34.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f
            drawCircle(color = ContractsInkRed.copy(alpha = 0.82f), radius = r, center = center)
            drawCircle(
                color = Color(0xFF4A1109).copy(alpha = 0.8f),
                radius = r * 0.86f,
                center = center,
                style = Stroke(width = 1.4f)
            )
        }
        Text(
            text = "T$t",
            style = EldoriaType.caption,
            color = Color(0xFFF6E7C8),
            maxLines = 1
        )
    }
}

@Composable
private fun ContractsInkRule() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        if (size.width <= 2f) return@Canvas
        val y = size.height / 2f
        drawLine(
            brush = Brush.horizontalGradient(
                listOf(Color.Transparent, ContractsInk.copy(alpha = 0.55f), Color.Transparent)
            ),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.4f,
            cap = StrokeCap.Round
        )
        val cx = size.width / 2f
        drawCircle(color = ContractsInk.copy(alpha = 0.62f), radius = 2.6f, center = Offset(cx, y))
        drawCircle(color = ContractsInk.copy(alpha = 0.40f), radius = 2f, center = Offset(cx - 12f, y))
        drawCircle(color = ContractsInk.copy(alpha = 0.40f), radius = 2f, center = Offset(cx + 12f, y))
    }
}

@Composable
private fun ContractsInkSectionTitle(text: String, trailing: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(17.dp)
                .background(ContractsInk.copy(alpha = 0.7f))
        )
        Spacer(Modifier.width(Eldoria.S8))
        Text(
            text = text,
            style = EldoriaType.heading,
            color = ContractsInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = EldoriaType.caption,
                color = ContractsInkSoft,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ContractsInkEmpty(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(8.dp))
            .background(ContractsInk.copy(alpha = 0.06f))
            .border(Eldoria.StrokeThin, ContractsInk.copy(alpha = 0.24f), CutCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EldoriaRuneGlyph(
            seed = title.hashCode(),
            modifier = Modifier.size(38.dp),
            color = ContractsInkSoft,
            strokeWidth = 1.4.dp,
            animated = true
        )
        Spacer(Modifier.height(Eldoria.S8))
        Text(
            text = title,
            style = EldoriaType.subheading,
            color = ContractsInk,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Spacer(Modifier.height(Eldoria.S4))
        Text(
            text = message,
            style = EldoriaType.small,
            color = ContractsInkSoft,
            textAlign = TextAlign.Center
        )
    }
}

// ────────────────────────────────── helpers ───────────────────────────────────

private fun contractsKindIcon(kind: String): ImageVector = when (kind.uppercase()) {
    "CAZA" -> Icons.Filled.Bolt
    "EXPEDICION" -> Icons.Filled.Explore
    "RECOLECCION" -> Icons.Filled.Inventory2
    "DOMA" -> Icons.Filled.Pets
    "REINO" -> Icons.Filled.Explore
    else -> Icons.Filled.Assignment
}

private fun contractsKindColor(kind: String): Color = when (kind.uppercase()) {
    "CAZA" -> ContractsInkRed
    "EXPEDICION" -> ContractsInkBlue
    "RECOLECCION" -> ContractsInkGreen
    "DOMA" -> ContractsInkGold
    "REINO" -> ContractsInkGold
    else -> ContractsInkSoft
}

private fun contractsTargetLabel(kind: String, target: String): String = when (kind.uppercase()) {
    // Los encargos del reino apuntan a su id: se traduce al nombre de la tierra.
    "REINO" -> KingdomAtlas.byId(target)
        ?.let { KingdomAtlas.dataOf(it).name }
        ?: contractsHumanize(target)
    "CAZA" -> EldoriaBestiary.ARCHETYPES.firstOrNull { it.id == target }?.name ?: contractsHumanize(target)
    "RECOLECCION" -> EldoriaMaterials.name(target)
    "EXPEDICION" -> when {
        target.startsWith("ROOMS:") -> "Salas despejadas"
        target.startsWith("DEPTH:") -> "Alcanzar profundidad ${target.removePrefix("DEPTH:")}"
        target == "BOSS" -> "Jefes de calabozo"
        target == "TORCH_LOW" -> "Terminar con la antorcha agonizando"
        target.startsWith("SEALS:") -> "Expedición con ${target.removePrefix("SEALS:")} sellos"
        target == "ABYSS" -> "Conquistar un Abismo"
        else -> contractsHumanize(target)
    }
    "DOMA" -> when {
        target == "ADOPTAR" -> "Adoptar una bestia"
        target.startsWith("ENTRENAR:") -> "Entrenar ${contractsHumanize(target.removePrefix("ENTRENAR:"))}"
        target == "ALIMENTAR_FAVORITA" -> "Alimentar con su comida favorita"
        target == "EVOLUCIONAR" -> "Evolucionar una mascota"
        target.startsWith("ROSTER:") -> "Tener ${target.removePrefix("ROSTER:")} mascotas en el establo"
        else -> contractsHumanize(target)
    }
    else -> contractsHumanize(target)
}

private fun contractsHumanize(raw: String): String {
    val clean = raw.trim()
    if (clean.isEmpty()) return "—"
    // Un objetivo con espacios ya es un nombre propio («Duende Silvestre»,
    // «Dragón Dorado de Eldoria»): destrozarlo a minúsculas sería peor.
    if (clean.contains(' ')) return clean
    return clean.split('_', ':')
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.substring(0, 1).uppercase() + part.substring(1).lowercase()
        }
}
