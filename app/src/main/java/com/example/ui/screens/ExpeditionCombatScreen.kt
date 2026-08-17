package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.GameJsonParser
import com.example.data.GameViewModel
import com.example.data.Item
import com.example.data.Skill
import com.example.data.content.EldoriaBestiary
import com.example.data.content.EldoriaExpeditions
import com.example.ui.design.EldoriaRevealImage
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBarTone
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.combat.CombatActionBar
import com.example.ui.combat.CombatPetSlot
import com.example.ui.design.CombatFx
import com.example.ui.design.EldoriaDamageFloater
import com.example.ui.design.EldoriaSkillFx
import com.example.ui.design.accent
import com.example.ui.design.combatFxForEnemyArchetype
import com.example.ui.design.combatFxForSkill
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmberField
import com.example.ui.design.EldoriaEmptyState
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaImpactBurst
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaResourceBar
import com.example.ui.design.EldoriaScanlines
import com.example.ui.design.EldoriaStainedGlass
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaTorchLight
import com.example.ui.design.EldoriaType
import com.example.ui.design.EldoriaVignette
import com.example.ui.design.eldoriaBevel
import com.example.ui.design.eldoriaGlowLayer
import com.example.ui.design.eldoriaPressable
import com.example.ui.design.eldoriaPulse
import com.example.ui.design.eldoriaShake
import com.example.ui.getCharacterPortrait
import com.example.ui.getEnemyArtRes
import com.example.ui.getEnemyPortraitRes

// ══════════════════════════════════════════════════════════════════════════════
//  COMBATE DE PROFUNDIDAD
//
//  Nada que ver con el combate de superficie: sin cabecera de aplicación, con la
//  antorcha cerrándose sobre el tablero, telegrafía de intención enemiga, ventana
//  de reacción y órdenes de bestia. El combate deja de ser ciego.
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun ExpeditionCombatScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val combat by viewModel.combatState.collectAsState()
    val run by viewModel.systems.expedition.collectAsState()
    val settings by viewModel.systems.settings.collectAsState()
    val autoCombat by viewModel.isAutoCombat.collectAsState()

    val p = progress
    val enemy = combat.enemy

    if (p == null || enemy == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Eldoria.depthBrush(1)),
            contentAlignment = Alignment.Center
        ) {
            EldoriaVignette(modifier = Modifier.fillMaxSize(), strength = 0.7f)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EldoriaEmptyState(
                    title = "El eco se ha disipado",
                    message = "No hay ningún enemigo delante de ti. Regresa al mapa del descenso.",
                    icon = Icons.Filled.Warning,
                    accent = Eldoria.Ember,
                    testTag = "expedition_combat_empty_state"
                )
                EldoriaButton(
                    text = "VOLVER AL MAPA",
                    onClick = {
                        viewModel.exitCombatScreen()
                        viewModel.systems.returnToExpeditionMap()
                    },
                    modifier = Modifier.padding(horizontal = 32.dp),
                    tone = EldoriaTone.Ember,
                    size = EldoriaButtonSize.Medium,
                    fullWidth = true,
                    testTag = "exit_combat_button"
                )
            }
        }
        return
    }

    val depth = combat.expeditionDepth
    val torchRatio = (run.torch / 100f).coerceIn(0f, 1f)
    val rarityLabel = expeditionEnemyRarityLabel(enemy.rarity)
    val rarityColor = Eldoria.rarityColor(rarityLabel)

    val skills = remember(p.skillsJson) { GameJsonParser.listFromJson<Skill>(p.skillsJson) }
    val inventory = remember(p.inventoryJson) { GameJsonParser.listFromJson<Item>(p.inventoryJson) }
    val potionCount = remember(inventory) { inventory.count { it.type.uppercase() == "POTION" } }

    var petMenuOpen by remember { mutableStateOf(false) }

    // ── disparadores de feedback visual ─────────────────────────────────────
    var enemyHitTick by remember { mutableStateOf(0) }
    var enemyHitText by remember { mutableStateOf("") }
    var playerHitTick by remember { mutableStateOf(0) }
    var playerHitText by remember { mutableStateOf("") }
    var critTick by remember { mutableStateOf(0) }
    var shakeTick by remember { mutableStateOf(0) }

    LaunchedEffect(combat.damageFeedbackEnemy) {
        val f = combat.damageFeedbackEnemy
        if (!f.isNullOrBlank()) {
            enemyHitText = f
            enemyHitTick += 1
        }
    }
    LaunchedEffect(combat.damageFeedbackPlayer) {
        val f = combat.damageFeedbackPlayer
        if (!f.isNullOrBlank()) {
            playerHitText = f
            playerHitTick += 1
            if (!f.contains("+")) shakeTick += 1
        }
    }
    LaunchedEffect(combat.combatLogs.size) {
        val last = combat.combatLogs.lastOrNull().orEmpty().uppercase()
        if (last.contains("CRÍTICO") || last.contains("CRITICO") || last.contains("PERFECTA")) {
            critTick += 1
        }
    }

    val shakeOffset = if (settings.screenShake) eldoriaShake(shakeTick, magnitude = 7.dp) else 0.dp
    val intentPulse = eldoriaPulse(periodMs = 900, from = 0.45f, to = 1f, label = "intent")

    // ── efecto elemental de la habilidad usada ───────────────────────────────
    // Lo que estalla sobre la bestia cambia con lo que lanzaste: fuego, veneno,
    // runas, sombra o luz. El golpe recibido se pinta sobre el héroe.
    val lastSkill = remember(combat.lastSkillId, skills) {
        skills.firstOrNull { it.id == combat.lastSkillId }
    }
    val enemyFx = when (combat.activeAnimation) {
        "PLAYER_ATTACK" -> CombatFx.PHYSICAL
        "PLAYER_MAGIC" -> combatFxForSkill(
            skillId = combat.lastSkillId,
            healing = (lastSkill?.healingMultiplier ?: 0.0) > 0.0,
            damaging = (lastSkill?.damageMultiplier ?: 1.0) > 0.0
        )
        else -> CombatFx.NONE
    }
    val playerFx = when (combat.activeAnimation) {
        "PLAYER_HEAL" -> if (combat.lastSkillId.startsWith("c_")) CombatFx.HOLY else CombatFx.HEAL
        "PLAYER_POTION" -> CombatFx.HEAL
        "ENEMY_ATTACK" -> CombatFx.PHYSICAL
        "ENEMY_SKILL" -> combatFxForEnemyArchetype(combat.enemyArchetype)
        else -> CombatFx.NONE
    }
    var enemyFxTick by remember { mutableStateOf(0) }
    var playerFxTick by remember { mutableStateOf(0) }
    LaunchedEffect(combat.activeAnimation) {
        if (combat.activeAnimation != null) {
            if (enemyFx != CombatFx.NONE) enemyFxTick += 1
            if (playerFx != CombatFx.NONE) playerFxTick += 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Eldoria.depthBrush(depth))
            .testTag("expedition_combat_root")
    ) {
        EldoriaEmberField(
            modifier = Modifier.fillMaxSize(),
            count = 26,
            tint = Eldoria.Ember,
            seed = 41 + depth
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = shakeOffset)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // ── cinta superior ───────────────────────────────────────────────
            ExpeditionCombatRibbon(
                dungeonId = run.dungeonId,
                depth = depth,
                roomLabel = combat.expeditionRoomLabel.ifBlank { run.dungeonName },
                torch = run.torch,
                sealIds = run.seals,
                autoCombat = autoCombat,
                onToggleAuto = { viewModel.toggleAutoCombat() }
            )

            Spacer(Modifier.height(Eldoria.S8))

            // ── enemigo ──────────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.TopCenter) {
                    EldoriaFrame(
                        modifier = Modifier.size(140.dp),
                        edge = EldoriaEdge.rarity(rarityLabel),
                        corner = Eldoria.R16,
                        strokeWidth = Eldoria.StrokeBold,
                        filigree = true,
                        rivets = true,
                        glowPulse = combat.reactionWindow || enemy.isBoss
                    ) {
                        // Zoom inverso: el marco no recorta a la criatura.
                        EldoriaRevealImage(
                            painter = painterResource(
                                id = getEnemyArtRes(enemy.artKey, enemy.name, enemy.isBoss, enemy.rarity)
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            rarityColor.copy(alpha = 0.12f),
                                            Eldoria.Abyss.copy(alpha = 0.55f)
                                        )
                                    )
                                )
                        )
                    }
                    EldoriaSkillFx(
                        fx = enemyFx,
                        trigger = enemyFxTick,
                        modifier = Modifier.size(150.dp),
                        seed = enemy.name.hashCode()
                    )
                    EldoriaImpactBurst(
                        trigger = critTick,
                        modifier = Modifier.size(140.dp),
                        color = enemyFx.accent(),
                        rays = 14
                    )
                    if (settings.damageNumbers) {
                        EldoriaDamageFloater(
                            text = enemyHitText,
                            trigger = enemyHitTick,
                            modifier = Modifier.size(140.dp),
                            color = Eldoria.GoldBright,
                            big = true
                        )
                    }
                }

                Spacer(Modifier.height(Eldoria.S6))

                Text(
                    text = enemy.name,
                    style = EldoriaType.heading,
                    color = rarityColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(Eldoria.S4))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S4),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EldoriaChip(text = "Nivel ${enemy.level}", color = Eldoria.Silver)
                    EldoriaChip(
                        text = EldoriaBestiary.archetype(combat.enemyArchetype).name,
                        color = expeditionAccentOf(EldoriaBestiary.archetype(combat.enemyArchetype).tone),
                        icon = Icons.Filled.Gavel,
                        filled = true
                    )
                    combat.enemyAffixes.forEach { affixId ->
                        val affix = EldoriaBestiary.affix(affixId)
                        EldoriaChip(
                            text = affix?.name ?: affixId,
                            color = expeditionAccentOf(affix?.tone ?: "BLOOD"),
                            icon = Icons.Filled.AutoAwesome
                        )
                    }
                }

                Spacer(Modifier.height(Eldoria.S6))

                EldoriaResourceBar(
                    current = enemy.currentHp.coerceAtLeast(0),
                    max = enemy.maxHp.coerceAtLeast(1),
                    tone = EldoriaBarTone.Threat,
                    label = "VIDA DEL ENEMIGO",
                    icon = Icons.Filled.Favorite,
                    height = 13.dp
                )

                if (!combat.enemyIntent.isNullOrBlank()) {
                    Spacer(Modifier.height(Eldoria.S6))
                    Box(
                        modifier = Modifier.eldoriaGlowLayer(
                            color = Eldoria.Blood.copy(alpha = 0.55f * intentPulse),
                            alpha = 0.22f * intentPulse,
                            corner = 20.dp,
                            spread = 6.dp
                        )
                    ) {
                        EldoriaChip(
                            text = "⚡ PREPARA: ${combat.enemyIntent}",
                            color = Eldoria.BloodBright,
                            icon = Icons.Filled.Warning,
                            filled = true
                        )
                    }
                }
            }

            Spacer(Modifier.height(Eldoria.S8))

            // ── registro corto ───────────────────────────────────────────────
            ExpeditionCombatLog(lines = combat.combatLogs.takeLast(3))

            Spacer(Modifier.weight(1f))

            // ── jugador ──────────────────────────────────────────────────────
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = EldoriaEdge.Iron,
                corner = Eldoria.R12,
                padding = PaddingValues(horizontal = 11.dp, vertical = 9.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CutCornerShape(8.dp))
                                .background(Eldoria.Abyss)
                                .border(Eldoria.StrokeMed, Eldoria.goldEdge(), CutCornerShape(8.dp))
                        ) {
                            Image(
                                painter = painterResource(
                                    id = getCharacterPortrait(p.charRace, p.charClass, p.hasAdvancedClass, p.charLevel)
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        EldoriaSkillFx(
                            fx = playerFx,
                            trigger = playerFxTick,
                            modifier = Modifier.size(72.dp),
                            seed = p.charName.hashCode()
                        )
                        if (settings.damageNumbers) {
                            EldoriaDamageFloater(
                                text = playerHitText,
                                trigger = playerHitTick,
                                modifier = Modifier.size(58.dp),
                                color = Eldoria.BloodBright
                            )
                        }
                    }
                    Spacer(Modifier.width(Eldoria.S8))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = p.charName,
                            style = EldoriaType.subheading,
                            color = Eldoria.TextHi,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(3.dp))
                        EldoriaResourceBar(
                            current = combat.playerCurrentHp.coerceAtLeast(0),
                            max = p.maxHp.coerceAtLeast(1),
                            tone = EldoriaBarTone.Health,
                            icon = Icons.Filled.Favorite,
                            height = 11.dp,
                            dangerPulse = true
                        )
                        Spacer(Modifier.height(3.dp))
                        EldoriaResourceBar(
                            current = combat.playerCurrentMp.coerceAtLeast(0),
                            max = p.maxMp.coerceAtLeast(1),
                            tone = EldoriaBarTone.Mana,
                            icon = Icons.Filled.Bolt,
                            height = 11.dp
                        )
                    }
                }
                Spacer(Modifier.height(Eldoria.S6))
                EldoriaResourceBar(
                    current = combat.momentum.coerceIn(0, 100),
                    max = 100,
                    tone = EldoriaBarTone.Momentum,
                    label = "ÍMPETU",
                    icon = Icons.Filled.AutoAwesome,
                    height = 12.dp
                )
            }

            Spacer(Modifier.height(Eldoria.S8))

            // ── acciones / desenlace ─────────────────────────────────────────
            if (combat.victory != null) {
                ExpeditionOutcomePanel(
                    victory = combat.victory == true,
                    expeditionActive = run.active,
                    onContinue = {
                        viewModel.exitCombatScreen()
                        viewModel.systems.returnToExpeditionMap()
                    },
                    onExit = {
                        viewModel.exitCombatScreen()
                        viewModel.systems.returnToExpeditionMap()
                    }
                )
            } else {
                if (petMenuOpen) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Eldoria.S6),
                        horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
                    ) {
                        ExpeditionPetCommandButton(
                            id = "EMBESTIDA",
                            label = "EMBESTIDA",
                            icon = Icons.Filled.SportsMartialArts,
                            accent = Eldoria.BloodBright,
                            enabled = combat.playerTurn && combat.petCooldown <= 0,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.executePetCommand("EMBESTIDA")
                                petMenuOpen = false
                            }
                        )
                        ExpeditionPetCommandButton(
                            id = "GUARDIA",
                            label = "GUARDIA",
                            icon = Icons.Filled.Shield,
                            accent = Eldoria.Silver,
                            enabled = combat.playerTurn && combat.petCooldown <= 0,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.executePetCommand("GUARDIA")
                                petMenuOpen = false
                            }
                        )
                        ExpeditionPetCommandButton(
                            id = "ALIENTO",
                            label = "ALIENTO",
                            icon = Icons.Filled.Favorite,
                            accent = Eldoria.VitaeBright,
                            enabled = combat.playerTurn && combat.petCooldown <= 0,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.executePetCommand("ALIENTO")
                                petMenuOpen = false
                            }
                        )
                    }
                }

                // La misma barra de cristales emplomados que la superficie. El
                // descenso sólo añade el hueco de la bestia.
                CombatActionBar(
                    charClass = p.charClass,
                    skills = skills,
                    inventoryJson = p.inventoryJson,
                    combatState = combat,
                    onBasicAttack = { viewModel.executeBasicAttack() },
                    onSkill = { skill -> viewModel.executeSkill(skill) },
                    onPotion = { potionId -> viewModel.usePotionCombat(potionId) },
                    onFlee = { viewModel.fleeCombat() },
                    petSlot = CombatPetSlot(
                        cooldown = combat.petCooldown,
                        onClick = { petMenuOpen = !petMenuOpen }
                    )
                )
            }
        }

        // ── capa de ambiente ─────────────────────────────────────────────────
        // Sin foco de antorcha: el halo que se abría y cerraba con la luz molestaba
        // a la vista. Queda la caída de cenizas y un viñeteado fijo y suave.
        EldoriaVignette(
            modifier = Modifier.fillMaxSize(),
            strength = 0.42f,
            centerBiasY = 0.38f
        )

        // ── ventana de reacción ──────────────────────────────────────────────
        if (combat.reactionWindow) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Eldoria.ScrimGlass),
                contentAlignment = Alignment.Center
            ) {
                ExpeditionReactionRing(
                    deadline = combat.reactionDeadline,
                    onResult = { quality -> viewModel.executeReaction(quality) },
                    modifier = Modifier.testTag("combat_reaction_ring"),
                    assist = settings.reactionAssist,
                    accent = Eldoria.EmberCore
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  CINTA SUPERIOR
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionCombatRibbon(
    dungeonId: Int,
    depth: Int,
    roomLabel: String,
    torch: Int,
    sealIds: List<String>,
    autoCombat: Boolean,
    onToggleAuto: () -> Unit
) {
    val accent = expeditionDepthAccent(depth)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(9.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Eldoria.Abyss.copy(alpha = 0.90f),
                        Eldoria.PanelSunken.copy(alpha = 0.85f),
                        Eldoria.Abyss.copy(alpha = 0.90f)
                    )
                )
            )
            .border(Eldoria.StrokeThin, accent.copy(alpha = 0.55f), CutCornerShape(9.dp))
            .padding(horizontal = 11.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // El mismo emblema que en el mapa: la pelea no se despega del sitio.
            ExpeditionDungeonEmblem(dungeonId = dungeonId, accent = accent, size = 26.dp)
            Spacer(Modifier.width(Eldoria.S6))
            Text(
                text = "PROFUNDIDAD ${depth + 1}",
                style = EldoriaType.label,
                color = accent,
                maxLines = 1
            )
            Spacer(Modifier.width(Eldoria.S6))
            Text(
                text = "· $roomLabel",
                style = EldoriaType.caption,
                color = Eldoria.TextMid,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(Eldoria.S6))
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = if (torch < 25) Eldoria.BloodBright else Eldoria.Ember,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = " $torch",
                style = EldoriaType.numeric,
                color = if (torch < 25) Eldoria.BloodBright else Eldoria.Ember,
                maxLines = 1
            )
            Spacer(Modifier.width(Eldoria.S8))
            Box(
                modifier = Modifier
                    .clip(CutCornerShape(6.dp))
                    .background(
                        if (autoCombat) Eldoria.Gold.copy(alpha = 0.28f)
                        else Eldoria.PanelHi.copy(alpha = 0.8f)
                    )
                    .border(
                        Eldoria.StrokeThin,
                        if (autoCombat) Eldoria.Gold else Eldoria.Iron,
                        CutCornerShape(6.dp)
                    )
                    .eldoriaPressable(onClick = onToggleAuto)
                    .padding(horizontal = 7.dp, vertical = 4.dp)
                    .testTag("expedition_auto_toggle"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoMode,
                    contentDescription = "Combate automático",
                    tint = if (autoCombat) Eldoria.GoldBright else Eldoria.TextLow,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        if (sealIds.isNotEmpty()) {
            Spacer(Modifier.height(Eldoria.S4))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S4)
            ) {
                sealIds.forEach { id ->
                    val seal = EldoriaExpeditions.seal(id)
                    EldoriaChip(
                        text = seal?.name ?: id,
                        color = expeditionAccentOf(seal?.tone ?: "IRON"),
                        icon = Icons.Filled.Lock
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  REGISTRO DE COMBATE (3 LÍNEAS)
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionCombatLog(lines: List<String>) {
    if (lines.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(7.dp))
            .background(Eldoria.Abyss.copy(alpha = 0.62f))
            .border(Eldoria.StrokeHair, Eldoria.IronDeep, CutCornerShape(7.dp))
            .padding(horizontal = 9.dp, vertical = 6.dp)
    ) {
        lines.forEach { line ->
            val color = when {
                line.contains("PERFECTA") || line.contains("CRÍTICO") -> Eldoria.GoldBright
                line.contains("🐾") -> Eldoria.VitaeBright
                line.contains("💢") || line.contains("te ataca") -> Eldoria.BloodBright
                else -> Eldoria.TextMid
            }
            Text(
                text = line,
                style = EldoriaType.caption,
                color = color,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  RANURA DE ACCIÓN (VITRAL)
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionActionSlot(
    label: String,
    cost: String,
    icon: ImageVector,
    accent: Color,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val shape = CutCornerShape(9.dp)
    val tint = if (enabled) accent else Eldoria.TextLow
    Column(
        modifier = Modifier.width(74.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 70.dp, height = 62.dp)
                .then(
                    if (enabled) Modifier.eldoriaGlowLayer(accent.copy(alpha = 0.45f), alpha = 0.16f, corner = 9.dp, spread = 5.dp)
                    else Modifier
                )
                .clip(shape)
                .background(Eldoria.Abyss)
                .then(if (enabled) Modifier.eldoriaPressable(onClick = onClick) else Modifier)
                .border(Eldoria.StrokeMed, if (enabled) accent.copy(alpha = 0.85f) else Eldoria.Iron, shape)
                .eldoriaBevel(corner = 9.dp)
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            EldoriaStainedGlass(
                modifier = Modifier.fillMaxSize(),
                glow = if (enabled) accent else Eldoria.IronDeep,
                base = Eldoria.Abyss,
                facets = 7,
                seed = label.hashCode()
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                    )
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = cost,
                    style = EldoriaType.caption,
                    color = if (enabled) Eldoria.TextHi else Eldoria.TextLow,
                    maxLines = 1
                )
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text = label.uppercase(),
            style = EldoriaType.caption,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  ÓRDENES DE LA BESTIA
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionPetCommandButton(
    id: String,
    label: String,
    icon: ImageVector,
    accent: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = CutCornerShape(7.dp)
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(accent.copy(alpha = if (enabled) 0.30f else 0.08f), Eldoria.PanelSunken)
                )
            )
            .then(if (enabled) Modifier.eldoriaPressable(onClick = onClick) else Modifier)
            .border(Eldoria.StrokeThin, if (enabled) accent.copy(alpha = 0.9f) else Eldoria.Iron, shape)
            .padding(horizontal = 8.dp)
            .testTag("combat_pet_cmd_$id"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) accent else Eldoria.TextLow,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(Eldoria.S4))
        Text(
            text = label,
            style = EldoriaType.buttonSmall,
            color = if (enabled) Eldoria.TextHi else Eldoria.TextLow,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  DESENLACE
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ExpeditionOutcomePanel(
    victory: Boolean,
    expeditionActive: Boolean,
    onContinue: () -> Unit,
    onExit: () -> Unit
) {
    EldoriaPanel(
        modifier = Modifier.fillMaxWidth(),
        edge = if (victory) EldoriaEdge.Gold else EldoriaEdge.Blood,
        corner = Eldoria.R12,
        padding = PaddingValues(14.dp),
        glow = true,
        filigree = true
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (victory) Icons.Filled.EmojiEvents else Icons.Filled.Warning,
                contentDescription = null,
                tint = if (victory) Eldoria.GoldBright else Eldoria.BloodBright,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(Eldoria.S8))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (victory) "SALA DESPEJADA" else "HAS CAÍDO",
                    style = EldoriaType.title,
                    color = if (victory) Eldoria.TextGold else Eldoria.BloodBright,
                    maxLines = 1
                )
                Text(
                    text = if (victory) {
                        if (expeditionActive) "El camino se abre. La antorcha sigue ardiendo."
                        else "El descenso ha terminado."
                    } else {
                        "El abismo se queda con la mitad de tu botín."
                    },
                    style = EldoriaType.small,
                    color = Eldoria.TextMid,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(Eldoria.S12))

        if (victory && expeditionActive) {
            EldoriaButton(
                text = "CONTINUAR EXPEDICIÓN",
                onClick = onContinue,
                tone = EldoriaTone.Gold,
                size = EldoriaButtonSize.Medium,
                icon = Icons.Filled.LocalFireDepartment,
                fullWidth = true,
                testTag = "expedition_continue_btn"
            )
            Spacer(Modifier.height(Eldoria.S6))
            EldoriaButton(
                text = "SALIR DEL COMBATE",
                onClick = onExit,
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Small,
                fullWidth = true,
                testTag = "exit_combat_button"
            )
        } else {
            EldoriaButton(
                text = if (victory) "CONTINUAR EXPEDICIÓN" else "SALIR DEL ABISMO",
                onClick = onExit,
                tone = if (victory) EldoriaTone.Gold else EldoriaTone.Iron,
                size = EldoriaButtonSize.Medium,
                icon = if (victory) Icons.Filled.LocalFireDepartment else Icons.Filled.DirectionsRun,
                fullWidth = true,
                testTag = "exit_combat_button"
            )
            if (victory) {
                Spacer(Modifier.height(Eldoria.S6))
                EldoriaButton(
                    text = "VOLVER AL MAPA",
                    onClick = onContinue,
                    tone = EldoriaTone.Ember,
                    size = EldoriaButtonSize.Small,
                    fullWidth = true,
                    testTag = "expedition_continue_btn"
                )
            }
        }
    }
}
