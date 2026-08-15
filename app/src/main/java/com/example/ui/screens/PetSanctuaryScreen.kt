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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.GameJsonParser
import com.example.data.GameScreen
import com.example.data.GameViewModel
import com.example.data.Item
import com.example.data.content.EldoriaMaterials
import com.example.data.content.EldoriaPets
import com.example.data.model.PetRecord
import com.example.data.model.PetSpecies
import com.example.data.model.PetTrait
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBanner
import com.example.ui.design.EldoriaBarTone
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaConfirmDialog
import com.example.ui.design.EldoriaCounter
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmptyState
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaItemCard
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaProgressRing
import com.example.ui.design.EldoriaRarityGem
import com.example.ui.design.EldoriaResourceBar
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaSegmentedTabs
import com.example.ui.design.EldoriaSheet
import com.example.ui.design.EldoriaSlotFrame
import com.example.ui.design.EldoriaStatPill
import com.example.ui.design.EldoriaSwap
import com.example.ui.design.EldoriaToggleChip
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.getItemImageRes

// ──────────────────────────────────────────────────────────────────────────────
//  SANTUARIO DE BESTIAS
//  Compañero · Establo · Adiestramiento · Altar
// ──────────────────────────────────────────────────────────────────────────────

private const val PET_SLOT_WEAPON = "PET_WEAPON"
private const val PET_SLOT_ARMOR = "PET_ARMOR"
private const val PET_SLOT_ACCESSORY = "PET_ACCESSORY"

private data class PetFoodStack(val sample: Item, val count: Int)

@Composable
fun PetSanctuaryScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val roster by viewModel.systems.petRoster.collectAsState()
    val activePet by viewModel.systems.activePet.collectAsState()
    val materials by viewModel.systems.materials.collectAsState()

    var tabIndex by remember { mutableStateOf(0) }
    var focusId by remember { mutableStateOf("") }
    var renameTarget by remember { mutableStateOf<PetRecord?>(null) }
    var releaseTarget by remember { mutableStateOf<PetRecord?>(null) }
    var traitSheet by remember { mutableStateOf<PetTrait?>(null) }
    var gearSlot by remember { mutableStateOf<String?>(null) }
    var confirmEvolve by remember { mutableStateOf(false) }
    var fuseSelectedId by remember { mutableStateOf("") }
    var confirmFuse by remember { mutableStateOf(false) }

    val gold = progress?.charGold ?: 0
    val focus = roster.firstOrNull { it.id == focusId } ?: activePet ?: roster.firstOrNull()
    val focusSpecies = remember(focus?.speciesId) { focus?.let { EldoriaPets.species(it.speciesId) } }

    val inventoryJson = progress?.inventoryJson ?: "[]"
    val inventory = remember(inventoryJson) { GameJsonParser.listFromJson<Item>(inventoryJson) }
    val foods = remember(inventory) {
        inventory
            .filter { it.type.equals("PET_FOOD", ignoreCase = true) }
            .groupBy { it.name }
            .map { entry -> PetFoodStack(entry.value.first(), entry.value.size) }
            .sortedBy { it.sample.name }
    }

    val weaponJson = progress?.petEquippedWeaponJson ?: ""
    val armorJson = progress?.petEquippedArmorJson ?: ""
    val accessoryJson = progress?.petEquippedAccessoryJson ?: ""
    val petWeapon = remember(weaponJson) { GameJsonParser.fromJson<Item>(weaponJson) }
    val petArmor = remember(armorJson) { GameJsonParser.fromJson<Item>(armorJson) }
    val petAccessory = remember(accessoryJson) { GameJsonParser.fromJson<Item>(accessoryJson) }

    val bannerArt = remember(focus?.imageResName) {
        val art = focus?.imageResName ?: ""
        if (art.isNotBlank()) getItemImageRes(art, "PET") else null
    }

    EldoriaScreen(
        depth = 1,
        embers = true,
        fog = true,
        vignetteStrength = 0.5f,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            EldoriaBanner(
                title = "Santuario de Bestias",
                subtitle = "${roster.size} bestias vinculadas · ${EldoriaPets.SPECIES.size} especies conocidas en Eldoria",
                artRes = bannerArt,
                height = 120.dp,
                edge = EldoriaEdge.Gold,
                crestSeed = focus?.sigilSeed ?: 90_101,
                trailing = {
                    Column(horizontalAlignment = Alignment.End) {
                        EldoriaIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al mapa",
                            onClick = { viewModel.changeScreen(GameScreen.WORLD_MAP) },
                            tone = EldoriaTone.Iron,
                            size = 36.dp
                        )
                        Spacer(Modifier.height(Eldoria.S6))
                        EldoriaCounter(
                            value = gold.toLong(),
                            icon = Icons.Filled.MonetizationOn,
                            accent = Eldoria.TextGold
                        )
                    }
                }
            )

            Spacer(Modifier.height(Eldoria.S12))

            EldoriaSegmentedTabs(
                options = listOf("COMPAÑERO", "ESTABLO", "ADIESTRAR", "ALTAR"),
                selectedIndex = tabIndex,
                onSelect = { tabIndex = it },
                accent = Eldoria.Gold,
                testTagPrefix = "pet_tab_"
            )

            Spacer(Modifier.height(Eldoria.S12))

            EldoriaSwap(
                targetState = tabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { tab ->
                val current = focus
                if (current == null) {
                    PetEmptySanctuary(
                        onExplore = { viewModel.changeScreen(GameScreen.WORLD_MAP) }
                    )
                } else {
                    when (tab) {
                        1 -> PetStableTab(
                            roster = roster,
                            activeId = activePet?.id ?: "",
                            focusId = current.id,
                            gold = gold,
                            adoptCost = viewModel.systems.adoptionCost(),
                            capacity = viewModel.systems.rosterCapacity(),
                            onAdopt = { viewModel.systems.buyRandomPet() },
                            onSelect = { focusId = it.id },
                            onEquip = { viewModel.systems.setActivePet(it.id) },
                            onRename = { renameTarget = it },
                            onRelease = { releaseTarget = it }
                        )

                        2 -> PetTrainingTab(
                            record = current,
                            gold = gold,
                            costOf = { discipline -> viewModel.systems.trainingCost(current.id, discipline) },
                            onMinigame = { discipline ->
                                viewModel.systems.startPetTrainingMinigame(current.id, discipline)
                            },
                            onRoutine = { discipline ->
                                viewModel.systems.trainPetDiscipline(current.id, discipline, 40)
                            },
                            onBulkRoutine = { discipline ->
                                repeat(5) { viewModel.systems.trainPetDiscipline(current.id, discipline, 40) }
                            }
                        )

                        3 -> PetAltarTab(
                            record = current,
                            roster = roster,
                            gold = gold,
                            materials = materials,
                            requirements = viewModel.systems.evolutionRequirements(current.id),
                            canEvolve = viewModel.systems.canEvolve(current.id),
                            fuseSelectedId = fuseSelectedId,
                            onFuseSelect = { fuseSelectedId = it },
                            onEvolve = { confirmEvolve = true },
                            onFuse = { confirmFuse = true }
                        )

                        else -> PetCompanionTab(
                            record = current,
                            species = focusSpecies,
                            weapon = petWeapon,
                            armor = petArmor,
                            accessory = petAccessory,
                            foods = foods,
                            onRename = { renameTarget = current },
                            onTraitClick = { traitSheet = it },
                            onSlotClick = { gearSlot = it },
                            onSlotLongClick = { viewModel.unequipPetGear(it) },
                            onFeed = { food -> viewModel.systems.feedPetRecord(current.id, food.id) },
                            onShop = { viewModel.changeScreen(GameScreen.SHOP) }
                        )
                    }
                }
            }
        }
    }

    // ── Diálogos y hojas ──────────────────────────────────────────────────────

    val renaming = renameTarget
    if (renaming != null) {
        PetRenameDialog(
            current = renaming.name,
            onDismiss = { renameTarget = null },
            onConfirm = { newName ->
                viewModel.systems.renamePet(renaming.id, newName)
                renameTarget = null
            }
        )
    }

    val releasing = releaseTarget
    if (releasing != null) {
        EldoriaConfirmDialog(
            title = "¿Liberar a ${releasing.name}?",
            message = "Nivel ${releasing.level} · Etapa ${petStageRoman(releasing.stage)} · ${releasing.rarity}.\n" +
                "La perderás para siempre junto a sus disciplinas y rasgos. " +
                "Sólo recibirás una pequeña ofrenda de oro por su despedida.",
            confirmLabel = "LIBERAR",
            onConfirm = {
                viewModel.systems.releasePet(releasing.id)
                if (fuseSelectedId == releasing.id) fuseSelectedId = ""
                releaseTarget = null
            },
            onDismiss = { releaseTarget = null },
            tone = EldoriaTone.Blood,
            testTagPrefix = "pet_release_dialog_"
        )
    }

    val evolving = focus
    if (confirmEvolve && evolving != null) {
        val nextStage = (evolving.stage + 1).coerceAtMost(3)
        val nextName = EldoriaPets.stageName(evolving.speciesId, nextStage).ifBlank { evolving.name }
        EldoriaConfirmDialog(
            title = "Rito de evolución",
            message = "${evolving.name} ascenderá a «$nextName» (etapa ${petStageRoman(nextStage)}).\n" +
                "El oro y los materiales del rito se consumen para siempre.",
            confirmLabel = "EVOLUCIONAR",
            onConfirm = {
                viewModel.systems.evolvePet(evolving.id)
                confirmEvolve = false
            },
            onDismiss = { confirmEvolve = false },
            tone = EldoriaTone.Arcane,
            testTagPrefix = "pet_evolve_dialog_"
        )
    }

    val fusingHost = focus
    val fusingSacrifice = roster.firstOrNull { it.id == fuseSelectedId }
    if (confirmFuse && fusingHost != null && fusingSacrifice != null) {
        EldoriaConfirmDialog(
            title = "Fusión en el altar",
            message = "${fusingSacrifice.name} será consumida y desaparecerá del establo para siempre.\n" +
                "${fusingHost.name} heredará 1 rasgo que aún no tenga y el 50 % de las disciplinas del sacrificio.",
            confirmLabel = "FUNDIR",
            onConfirm = {
                viewModel.systems.fusePets(fusingHost.id, fusingSacrifice.id)
                fuseSelectedId = ""
                confirmFuse = false
            },
            onDismiss = { confirmFuse = false },
            tone = EldoriaTone.Ember,
            testTagPrefix = "pet_fuse_dialog_"
        )
    }

    val shownTrait = traitSheet
    if (shownTrait != null) {
        EldoriaSheet(
            visible = true,
            title = shownTrait.name,
            onDismiss = { traitSheet = null },
            edge = EldoriaEdge.Arcane
        ) {
            Text(
                text = shownTrait.description,
                style = EldoriaType.body,
                color = Eldoria.TextHi
            )
            Spacer(Modifier.height(Eldoria.S12))
            EldoriaChip(
                text = "Magnitud ${shownTrait.magnitude}",
                color = petPaletteOf(shownTrait.tone).first,
                filled = true
            )
            Spacer(Modifier.height(Eldoria.S8))
            Text(
                text = "Los rasgos actúan solos: no gastan turno ni maná.",
                style = EldoriaType.caption,
                color = Eldoria.TextLow
            )
        }
    }

    val openSlot = gearSlot
    if (openSlot != null) {
        val candidates = remember(inventory, openSlot) {
            when (openSlot) {
                PET_SLOT_WEAPON -> inventory.filter { it.type.uppercase() in listOf("WEAPON", "STAFF") }
                PET_SLOT_ARMOR -> inventory.filter {
                    it.type.uppercase() in listOf("ARMOR", "SHIELD", "HELMET", "GLOVES", "BOOTS")
                }
                else -> inventory.filter { it.type.uppercase() in listOf("RING", "EARRING", "RELIC") }
            }
        }
        EldoriaSheet(
            visible = true,
            title = when (openSlot) {
                PET_SLOT_WEAPON -> "Arma de la bestia"
                PET_SLOT_ARMOR -> "Armadura de la bestia"
                else -> "Accesorio de la bestia"
            },
            onDismiss = { gearSlot = null },
            edge = EldoriaEdge.Gold
        ) {
            if (candidates.isEmpty()) {
                EldoriaEmptyState(
                    title = "Nada que ceder",
                    message = "No tienes objetos de esta categoría en la mochila. " +
                        "Sácalos de un calabozo o cómpralos en la tienda.",
                    icon = Icons.Filled.Pets,
                    accent = Eldoria.Silver
                )
            } else {
                Column(
                    modifier = Modifier
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    candidates.forEach { item ->
                        EldoriaItemCard(
                            name = item.name,
                            rarity = item.rarity,
                            level = item.itemLevel,
                            stats = item.getStatDescription(),
                            imageRes = getItemImageRes(item.imageResName, item.type),
                            subtitle = item.type,
                            onClick = {
                                viewModel.equipPetGear(item, openSlot)
                                gearSlot = null
                            }
                        )
                        Spacer(Modifier.height(Eldoria.S6))
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  PESTAÑA 1 · COMPAÑERO
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun PetCompanionTab(
    record: PetRecord,
    species: PetSpecies?,
    weapon: Item?,
    armor: Item?,
    accessory: Item?,
    foods: List<PetFoodStack>,
    onRename: () -> Unit,
    onTraitClick: (PetTrait) -> Unit,
    onSlotClick: (String) -> Unit,
    onSlotLongClick: (String) -> Unit,
    onFeed: (Item) -> Unit,
    onShop: () -> Unit
) {
    val traits = remember(record.traits) { record.traits.mapNotNull { EldoriaPets.trait(it) } }
    val levelCap = EldoriaPets.levelCapForStage(record.stage)
    val disciplineCap = EldoriaPets.disciplineCap(record.level)
    val expNeed = petExpNeed(record)
    val rarityColor = Eldoria.rarityColor(record.rarity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PetPortrait(record = record, size = 200.dp, animated = true, framed = true)
        }

        Spacer(Modifier.height(Eldoria.S12))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EldoriaRarityGem(rarity = record.rarity, size = 16.dp)
                    Spacer(Modifier.width(Eldoria.S6))
                    Text(
                        text = record.name,
                        style = EldoriaType.title,
                        color = rarityColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = species?.title ?: "Bestia vinculada",
                    style = EldoriaType.caption,
                    color = Eldoria.TextMid,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            EldoriaIconButton(
                icon = Icons.Filled.Edit,
                contentDescription = "Renombrar",
                onClick = onRename,
                tone = EldoriaTone.Iron,
                size = 40.dp
            )
        }

        Spacer(Modifier.height(Eldoria.S8))

        Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)) {
            EldoriaStatPill(
                label = "Nivel",
                value = "${record.level}/$levelCap",
                modifier = Modifier.weight(1f),
                accent = Eldoria.Gold
            )
            EldoriaStatPill(
                label = "Etapa",
                value = "${petStageRoman(record.stage)}/III",
                modifier = Modifier.weight(1f),
                accent = Eldoria.ArcaneBright
            )
            EldoriaStatPill(
                label = "Rareza",
                value = record.rarity,
                modifier = Modifier.weight(1f),
                accent = rarityColor
            )
        }

        if (species != null) {
            Spacer(Modifier.height(Eldoria.S8))
            Text(
                text = species.lore,
                style = EldoriaType.lore,
                color = Eldoria.TextMid
            )
        }

        Spacer(Modifier.height(Eldoria.S12))

        EldoriaPanel(edge = EldoriaEdge.Iron, corner = Eldoria.R12) {
            EldoriaSectionTitle(text = "Constantes vitales", accent = Eldoria.Gold)
            Spacer(Modifier.height(Eldoria.S8))
            EldoriaResourceBar(
                current = record.exp,
                max = expNeed,
                tone = EldoriaBarTone.Experience,
                label = "EXPERIENCIA"
            )
            Spacer(Modifier.height(Eldoria.S6))
            EldoriaResourceBar(
                current = record.satiety,
                max = 100,
                tone = EldoriaBarTone.Satiety,
                label = "SACIEDAD",
                dangerPulse = record.satiety < 20
            )
            Spacer(Modifier.height(Eldoria.S6))
            EldoriaResourceBar(
                current = record.bond,
                max = 100,
                tone = EldoriaBarTone.Bond,
                label = "VÍNCULO"
            )
            if (record.injuries > 0) {
                Spacer(Modifier.height(Eldoria.S8))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Eldoria.Danger,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(Eldoria.S6))
                    Text(
                        text = "Heridas: ${record.injuries} · rinde un " +
                            "${(record.injuries * 5).coerceAtMost(45)} % menos. Aliméntala para curarla.",
                        style = EldoriaType.small,
                        color = Eldoria.Danger
                    )
                }
            }
        }

        Spacer(Modifier.height(Eldoria.S16))
        EldoriaSectionTitle(
            text = "Disciplinas",
            icon = Icons.Filled.FitnessCenter,
            accent = Eldoria.Ember
        )
        Spacer(Modifier.height(Eldoria.S12))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PetRingColumn(PET_DISC_FURY, record.disciplineAtk, disciplineCap)
            PetRingColumn(PET_DISC_BASTION, record.disciplineDef, disciplineCap)
            PetRingColumn(PET_DISC_VITALITY, record.disciplineVit, disciplineCap)
        }
        Spacer(Modifier.height(Eldoria.S8))
        Text(
            text = "El tope de cada disciplina es ${disciplineCap} a nivel ${record.level}: sube de nivel para ampliarlo.",
            style = EldoriaType.caption,
            color = Eldoria.TextLow
        )

        Spacer(Modifier.height(Eldoria.S16))
        EldoriaSectionTitle(
            text = "Rasgos",
            icon = Icons.Filled.AutoAwesome,
            accent = Eldoria.ArcaneBright
        )
        Spacer(Modifier.height(Eldoria.S8))
        if (traits.isEmpty()) {
            Text(
                text = "Todavía no ha despertado ningún rasgo. Fusiona otra bestia en el Altar para legarle el suyo.",
                style = EldoriaType.small,
                color = Eldoria.TextLow
            )
        } else {
            traits.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)) {
                    pair.forEach { trait ->
                        PetTraitChip(
                            trait = trait,
                            modifier = Modifier.weight(1f),
                            onClick = { onTraitClick(trait) }
                        )
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(Eldoria.S6))
            }
            Text(
                text = "Toca un rasgo para leer qué hace exactamente.",
                style = EldoriaType.caption,
                color = Eldoria.TextLow
            )
        }

        Spacer(Modifier.height(Eldoria.S16))
        EldoriaSectionTitle(
            text = "Equipo de la bestia",
            icon = Icons.Filled.Shield,
            accent = Eldoria.Silver
        )
        Spacer(Modifier.height(Eldoria.S12))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PetGearSlot(
                label = "Arma",
                slot = PET_SLOT_WEAPON,
                item = weapon,
                emptyIcon = Icons.Filled.Bolt,
                onClick = onSlotClick,
                onLongClick = onSlotLongClick
            )
            PetGearSlot(
                label = "Armadura",
                slot = PET_SLOT_ARMOR,
                item = armor,
                emptyIcon = Icons.Filled.Shield,
                onClick = onSlotClick,
                onLongClick = onSlotLongClick
            )
            PetGearSlot(
                label = "Accesorio",
                slot = PET_SLOT_ACCESSORY,
                item = accessory,
                emptyIcon = Icons.Filled.Star,
                onClick = onSlotClick,
                onLongClick = onSlotLongClick
            )
        }
        Spacer(Modifier.height(Eldoria.S6))
        Text(
            text = "Toca una ranura para equipar · mantén pulsado para retirar la pieza.",
            style = EldoriaType.caption,
            color = Eldoria.TextLow
        )

        Spacer(Modifier.height(Eldoria.S16))
        EldoriaSectionTitle(
            text = "Comedero",
            icon = Icons.Filled.Restaurant,
            accent = Eldoria.VitaeBright
        )
        Spacer(Modifier.height(Eldoria.S8))
        if (foods.isEmpty()) {
            EldoriaEmptyState(
                title = "El comedero está vacío",
                message = "No te queda ninguna ración de mascota en la mochila. " +
                    "Compra raciones bestiales, místicas, de dragón o celestiales en la tienda.",
                icon = Icons.Filled.Restaurant,
                accent = Eldoria.VitaeBright,
                actionLabel = "IR A LA TIENDA",
                onAction = onShop
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
            ) {
                foods.forEach { stack ->
                    val favorite = species != null &&
                        species.favoriteFood.equals(petFoodKeyOf(stack.sample), ignoreCase = true)
                    PetFoodCard(
                        stack = stack,
                        favorite = favorite,
                        full = record.satiety >= 100,
                        onFeed = { onFeed(stack.sample) }
                    )
                }
            }
            Spacer(Modifier.height(Eldoria.S6))
            Text(
                text = if (record.satiety >= 100) {
                    "${record.name} está saciada: no probará bocado hasta que entrene o combata."
                } else {
                    "La ración favorita de la especie otorga un +50 % de vínculo."
                },
                style = EldoriaType.caption,
                color = if (record.satiety >= 100) Eldoria.Warning else Eldoria.TextLow
            )
        }

        Spacer(Modifier.height(Eldoria.S32))
    }
}

@Composable
private fun PetRingColumn(discipline: String, value: Int, cap: Int) {
    val accent = petDisciplineColor(discipline)
    val ratio = if (cap <= 0) 0f else value.toFloat() / cap.toFloat()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        EldoriaProgressRing(
            progress = ratio,
            size = 78.dp,
            stroke = 7.dp,
            accent = accent,
            centerLabel = value.toString()
        )
        Spacer(Modifier.height(Eldoria.S6))
        Text(
            text = petDisciplineLabel(discipline).uppercase(),
            style = EldoriaType.label,
            color = accent,
            maxLines = 1
        )
        Text(
            text = "$value / $cap",
            style = EldoriaType.caption,
            color = Eldoria.TextLow,
            maxLines = 1
        )
    }
}

@Composable
private fun PetGearSlot(
    label: String,
    slot: String,
    item: Item?,
    emptyIcon: ImageVector,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        EldoriaSlotFrame(
            rarity = item?.rarity,
            level = item?.itemLevel,
            size = 74.dp,
            emptyIcon = if (item == null) emptyIcon else null,
            emptyLabel = if (item == null) "Vacío" else null,
            onClick = { onClick(slot) },
            onLongClick = { onLongClick(slot) },
            testTag = "pet_gear_slot_$slot"
        ) {
            if (item != null) {
                Image(
                    painter = painterResource(id = getItemImageRes(item.imageResName, item.type)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(Modifier.height(Eldoria.S4))
        Text(
            text = label,
            style = EldoriaType.caption,
            color = Eldoria.TextMid,
            maxLines = 1
        )
        Text(
            text = item?.name ?: "—",
            style = EldoriaType.caption,
            color = if (item != null) Eldoria.rarityColor(item.rarity) else Eldoria.TextLow,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 92.dp)
        )
    }
}

@Composable
private fun PetFoodCard(
    stack: PetFoodStack,
    favorite: Boolean,
    full: Boolean,
    onFeed: () -> Unit
) {
    val item = stack.sample
    val accent = if (favorite) Eldoria.VitaeBright else Eldoria.Silver
    EldoriaPanel(
        modifier = Modifier.width(154.dp),
        edge = if (favorite) EldoriaEdge.Vitae else EldoriaEdge.Iron,
        corner = Eldoria.R12,
        padding = PaddingValues(10.dp),
        glow = favorite
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .clip(CutCornerShape(7.dp))
                .background(Brush.verticalGradient(listOf(Eldoria.PanelSunken, Eldoria.Abyss)))
                .border(Eldoria.StrokeThin, accent.copy(alpha = 0.5f), CutCornerShape(7.dp))
        ) {
            Image(
                painter = painterResource(id = getItemImageRes(item.imageResName, "PET_FOOD")),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .clip(CutCornerShape(4.dp))
                    .background(Eldoria.Abyss.copy(alpha = 0.86f))
                    .border(Eldoria.StrokeHair, accent.copy(alpha = 0.7f), CutCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "×${stack.count}",
                    style = EldoriaType.caption,
                    color = Eldoria.TextHi,
                    maxLines = 1
                )
            }
        }
        Spacer(Modifier.height(Eldoria.S6))
        Text(
            text = item.name,
            style = EldoriaType.caption,
            color = Eldoria.TextHi,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(Eldoria.S4))
        Text(
            text = if (favorite) "FAVORITA (+50 % vínculo)" else "Saciedad +${item.conBonus.coerceAtLeast(15)}",
            style = EldoriaType.caption,
            color = if (favorite) Eldoria.VitaeBright else Eldoria.TextLow,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(Eldoria.S8))
        EldoriaButton(
            text = if (full) "SACIADA" else "ALIMENTAR",
            onClick = onFeed,
            fullWidth = true,
            enabled = !full,
            tone = if (favorite) EldoriaTone.Vitae else EldoriaTone.Iron,
            size = EldoriaButtonSize.Small,
            testTag = "feed_pet_item_${item.id}"
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  PESTAÑA 2 · ESTABLO
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun PetStableTab(
    roster: List<PetRecord>,
    activeId: String,
    focusId: String,
    gold: Int,
    adoptCost: Int,
    capacity: Int,
    onAdopt: () -> Unit,
    onSelect: (PetRecord) -> Unit,
    onEquip: (PetRecord) -> Unit,
    onRename: (PetRecord) -> Unit,
    onRelease: (PetRecord) -> Unit
) {
    val activeName = roster.firstOrNull { it.id == activeId }?.name ?: "Ninguna"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        EldoriaPanel(edge = EldoriaEdge.Gold, corner = Eldoria.R12, filigree = true) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Establo real",
                        style = EldoriaType.heading,
                        color = Eldoria.TextGold
                    )
                    Text(
                        text = "Sólo la bestia activa te acompaña al combate. Toca una ficha para inspeccionarla.",
                        style = EldoriaType.small,
                        color = Eldoria.TextMid
                    )
                }
                Spacer(Modifier.width(Eldoria.S8))
                EldoriaStatPill(
                    label = "Activa",
                    value = activeName,
                    icon = Icons.Filled.Pets,
                    accent = Eldoria.Gold
                )
            }
        }

        // Segunda vía de doma: sin ella el establo se quedaba con una sola ficha para
        // siempre y la fusión del altar era contenido muerto.
        Spacer(Modifier.height(Eldoria.S8))
        val stableFull = roster.size >= capacity
        EldoriaButton(
            text = if (stableFull) "ESTABLO COMPLETO ($capacity)"
            else "ADOPTAR BESTIA · $adoptCost DE ORO",
            onClick = onAdopt,
            enabled = !stableFull && gold >= adoptCost,
            tone = EldoriaTone.Vitae,
            size = EldoriaButtonSize.Small,
            icon = Icons.Filled.Pets,
            fullWidth = true,
            testTag = "pet_adopt_btn"
        )
        if (!stableFull && gold < adoptCost) {
            Spacer(Modifier.height(Eldoria.S4))
            Text(
                text = "Te faltan ${adoptCost - gold} de oro para la próxima adopción.",
                style = EldoriaType.caption,
                color = Eldoria.TextLow
            )
        }

        Spacer(Modifier.height(Eldoria.S12))
        EldoriaSectionTitle(
            text = "Tus bestias (${roster.size})",
            icon = Icons.Filled.Pets,
            accent = Eldoria.Gold
        )
        Spacer(Modifier.height(Eldoria.S12))

        roster.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8),
                verticalAlignment = Alignment.Top
            ) {
                pair.forEach { record ->
                    PetRosterCell(
                        record = record,
                        isActive = record.id == activeId,
                        isFocused = record.id == focusId,
                        onSelect = { onSelect(record) },
                        onEquip = { onEquip(record) },
                        onRename = { onRename(record) },
                        onRelease = { onRelease(record) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(Eldoria.S8))
        }

        Spacer(Modifier.height(Eldoria.S24))
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  PESTAÑA 3 · ADIESTRAMIENTO
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun PetTrainingTab(
    record: PetRecord,
    gold: Int,
    costOf: (String) -> Int,
    onMinigame: (String) -> Unit,
    onRoutine: (String) -> Unit,
    onBulkRoutine: (String) -> Unit
) {
    val cap = EldoriaPets.disciplineCap(record.level)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        EldoriaPanel(edge = EldoriaEdge.Ember, corner = Eldoria.R12, glow = true, filigree = true) {
            EldoriaSectionTitle(
                text = "Cómo se adiestra",
                icon = Icons.Filled.FitnessCenter,
                accent = Eldoria.Ember
            )
            Spacer(Modifier.height(Eldoria.S8))
            Text(
                text = "El minijuego de adiestramiento mide tu pulso: cuanto mejor lo ejecutes, más sube la " +
                    "disciplina, hasta 9 puntos en una sola sesión. La rutina básica se resuelve sola, " +
                    "pero rinde sólo el 40 %: 4 puntos fijos.",
                style = EldoriaType.body,
                color = Eldoria.TextMid
            )
            Spacer(Modifier.height(Eldoria.S8))
            Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)) {
                EldoriaChip(text = "MINIJUEGO · hasta 100 %", color = Eldoria.Gold, filled = true)
                EldoriaChip(text = "RUTINA · 40 %", color = Eldoria.Silver)
            }
            Spacer(Modifier.height(Eldoria.S8))
            Text(
                text = "Cada sesión gasta 10 de saciedad, cuesta oro y estrecha el vínculo con la bestia.",
                style = EldoriaType.caption,
                color = Eldoria.TextLow
            )
        }

        Spacer(Modifier.height(Eldoria.S12))

        EldoriaPanel(edge = EldoriaEdge.Iron, corner = Eldoria.R12, padding = PaddingValues(11.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PetPortrait(record = record, size = 58.dp, animated = false, framed = false)
                Spacer(Modifier.width(Eldoria.S8))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.name,
                        style = EldoriaType.subheading,
                        color = Eldoria.rarityColor(record.rarity),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(Eldoria.S4))
                    EldoriaResourceBar(
                        current = record.satiety,
                        max = 100,
                        tone = EldoriaBarTone.Satiety,
                        label = "SACIEDAD",
                        height = 11.dp,
                        dangerPulse = record.satiety < 20
                    )
                }
            }
        }

        Spacer(Modifier.height(Eldoria.S12))

        PetTrainingCardFor(
            record = record,
            discipline = PET_DISC_FURY,
            icon = Icons.Filled.LocalFireDepartment,
            cap = cap,
            gold = gold,
            routineTestTag = "train_pet_attack",
            costOf = costOf,
            onMinigame = onMinigame,
            onRoutine = onRoutine,
            onBulkRoutine = onBulkRoutine
        )
        Spacer(Modifier.height(Eldoria.S12))
        PetTrainingCardFor(
            record = record,
            discipline = PET_DISC_BASTION,
            icon = Icons.Filled.Shield,
            cap = cap,
            gold = gold,
            routineTestTag = "train_pet_defense",
            costOf = costOf,
            onMinigame = onMinigame,
            onRoutine = onRoutine,
            onBulkRoutine = onBulkRoutine
        )
        Spacer(Modifier.height(Eldoria.S12))
        PetTrainingCardFor(
            record = record,
            discipline = PET_DISC_VITALITY,
            icon = Icons.Filled.Favorite,
            cap = cap,
            gold = gold,
            routineTestTag = "train_pet_vitality",
            costOf = costOf,
            onMinigame = onMinigame,
            onRoutine = onRoutine,
            onBulkRoutine = onBulkRoutine
        )

        Spacer(Modifier.height(Eldoria.S24))
    }
}

@Composable
private fun PetTrainingCardFor(
    record: PetRecord,
    discipline: String,
    icon: ImageVector,
    cap: Int,
    gold: Int,
    routineTestTag: String,
    costOf: (String) -> Int,
    onMinigame: (String) -> Unit,
    onRoutine: (String) -> Unit,
    onBulkRoutine: (String) -> Unit
) {
    val cost = costOf(discipline)
    val bulkCost = petBulkTrainingCost(record, discipline, 5, 40)
    val blockReason = petTrainBlockReason(record, discipline, gold, cost)
    val bulkReason = when {
        blockReason != null -> blockReason
        record.satiety < 50 -> "Rutina ×5 bloqueada: necesita 50 de saciedad (tiene ${record.satiety})."
        gold < bulkCost -> "Rutina ×5 bloqueada: cuesta $bulkCost de oro y te faltan ${bulkCost - gold}."
        else -> null
    }

    PetDisciplineCard(
        discipline = discipline,
        icon = icon,
        value = petDisciplineValue(record, discipline),
        cap = cap,
        cost = cost,
        bulkCost = bulkCost,
        blockReason = blockReason,
        bulkBlockReason = bulkReason,
        onMinigame = { onMinigame(discipline) },
        onRoutine = { onRoutine(discipline) },
        onBulkRoutine = { onBulkRoutine(discipline) },
        routineTestTag = routineTestTag
    )
}

// ──────────────────────────────────────────────────────────────────────────────
//  PESTAÑA 4 · ALTAR
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun PetAltarTab(
    record: PetRecord,
    roster: List<PetRecord>,
    gold: Int,
    materials: Map<String, Int>,
    requirements: Map<String, Int>,
    canEvolve: Boolean,
    fuseSelectedId: String,
    onFuseSelect: (String) -> Unit,
    onEvolve: () -> Unit,
    onFuse: () -> Unit
) {
    val finalForm = record.stage >= 3
    val nextStage = (record.stage + 1).coerceAtMost(3)
    val needBond = EldoriaPets.bondForStage(nextStage)
    val nextName = EldoriaPets.stageName(record.speciesId, nextStage).ifBlank { record.name }
    val goldCost = requirements["gold"] ?: 0
    val materialCost = requirements.filterKeys { it != "gold" }
    val candidates = roster.filter { it.id != record.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── EVOLUCIÓN ────────────────────────────────────────────────────────
        EldoriaPanel(edge = EldoriaEdge.Arcane, corner = Eldoria.R12, glow = true, filigree = true) {
            EldoriaSectionTitle(
                text = "Rito de evolución",
                icon = Icons.Filled.AutoAwesome,
                accent = Eldoria.ArcaneBright
            )
            Spacer(Modifier.height(Eldoria.S12))
            Row(verticalAlignment = Alignment.CenterVertically) {
                PetPortrait(record = record, size = 72.dp, animated = false, framed = false)
                Spacer(Modifier.width(Eldoria.S12))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.name,
                        style = EldoriaType.subheading,
                        color = Eldoria.rarityColor(record.rarity),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (finalForm) {
                            "Etapa ${petStageRoman(record.stage)} · forma final"
                        } else {
                            "Etapa ${petStageRoman(record.stage)} → ${petStageRoman(nextStage)}: «$nextName»"
                        },
                        style = EldoriaType.small,
                        color = Eldoria.TextMid
                    )
                }
            }

            Spacer(Modifier.height(Eldoria.S12))
            EldoriaDivider(color = Eldoria.Arcane)
            Spacer(Modifier.height(Eldoria.S12))

            if (finalForm) {
                Text(
                    text = "${record.name} ya alcanzó su forma final: no hay rito que la lleve más lejos. " +
                        "Sigue subiendo sus disciplinas y su vínculo.",
                    style = EldoriaType.body,
                    color = Eldoria.TextGold
                )
            } else {
                Text(
                    text = "Evolucionar sube el tope de nivel, refuerza todas sus estadísticas y le devuelve saciedad.",
                    style = EldoriaType.small,
                    color = Eldoria.TextMid
                )
                Spacer(Modifier.height(Eldoria.S8))
                PetRequirementRow(
                    label = "Vínculo",
                    have = record.bond,
                    need = needBond
                )
                PetRequirementRow(
                    label = "Oro",
                    have = gold,
                    need = goldCost
                )
                materialCost.forEach { (id, qty) ->
                    PetRequirementRow(
                        label = EldoriaMaterials.name(id),
                        have = materials[id] ?: 0,
                        need = qty
                    )
                }

                Spacer(Modifier.height(Eldoria.S12))
                EldoriaButton(
                    text = "EVOLUCIONAR",
                    onClick = onEvolve,
                    fullWidth = true,
                    enabled = canEvolve,
                    tone = EldoriaTone.Arcane,
                    size = EldoriaButtonSize.Large,
                    icon = Icons.Filled.AutoAwesome,
                    testTag = "pet_evolve_btn"
                )
                if (!canEvolve) {
                    Spacer(Modifier.height(Eldoria.S6))
                    Text(
                        text = when {
                            record.bond < needBond ->
                                "Requiere $needBond de vínculo (tiene ${record.bond}). Aliméntala y entrénala."
                            gold < goldCost -> "Sin oro suficiente: te faltan ${goldCost - gold}."
                            else -> "Te faltan materiales del rito: consíguelos en expediciones y contratos."
                        },
                        style = EldoriaType.small,
                        color = Eldoria.Warning
                    )
                }
            }
        }

        Spacer(Modifier.height(Eldoria.S16))

        // ── FUSIÓN ───────────────────────────────────────────────────────────
        EldoriaPanel(edge = EldoriaEdge.Ember, corner = Eldoria.R12, glow = candidates.isNotEmpty()) {
            EldoriaSectionTitle(
                text = "Fusión de bestias",
                icon = Icons.Filled.LocalFireDepartment,
                accent = Eldoria.Ember
            )
            Spacer(Modifier.height(Eldoria.S8))
            Text(
                text = "${record.name} heredará 1 rasgo que aún no tenga y el 50 % de las disciplinas de la " +
                    "bestia sacrificada, además de +6 de vínculo. La sacrificada se destruye para siempre.",
                style = EldoriaType.body,
                color = Eldoria.TextMid
            )
            Spacer(Modifier.height(Eldoria.S12))

            if (candidates.isEmpty()) {
                EldoriaEmptyState(
                    title = "No hay a quién fundir",
                    message = "Necesitas al menos dos bestias en el establo. " +
                        "Doma más criaturas en expediciones y contratos de doma.",
                    icon = Icons.Filled.Pets,
                    accent = Eldoria.Ember
                )
            } else {
                Text(
                    text = "Elige el sacrificio:",
                    style = EldoriaType.label,
                    color = Eldoria.TextMid
                )
                Spacer(Modifier.height(Eldoria.S8))
                candidates.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)) {
                        pair.forEach { candidate ->
                            EldoriaToggleChip(
                                text = "${candidate.name} · N${candidate.level}",
                                selected = candidate.id == fuseSelectedId,
                                onClick = { onFuseSelect(candidate.id) },
                                modifier = Modifier.weight(1f),
                                accent = Eldoria.Ember,
                                testTag = "pet_fuse_pick_${candidate.id}"
                            )
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(Eldoria.S6))
                }

                Spacer(Modifier.height(Eldoria.S6))
                EldoriaButton(
                    text = "FUNDIR EN EL ALTAR",
                    onClick = onFuse,
                    fullWidth = true,
                    enabled = fuseSelectedId.isNotBlank() && candidates.any { it.id == fuseSelectedId },
                    tone = EldoriaTone.Blood,
                    size = EldoriaButtonSize.Large,
                    icon = Icons.Filled.LocalFireDepartment,
                    testTag = "pet_fuse_btn"
                )
                if (fuseSelectedId.isBlank()) {
                    Spacer(Modifier.height(Eldoria.S6))
                    Text(
                        text = "Selecciona primero la bestia que vas a sacrificar.",
                        style = EldoriaType.small,
                        color = Eldoria.Warning
                    )
                }
            }
        }

        Spacer(Modifier.height(Eldoria.S24))
    }
}

@Composable
private fun PetRequirementRow(label: String, have: Int, need: Int) {
    val ok = have >= need
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Eldoria.S4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CutCornerShape(2.dp))
                .background(if (ok) Eldoria.Success else Eldoria.Danger)
        )
        Spacer(Modifier.width(Eldoria.S8))
        Text(
            text = label,
            style = EldoriaType.small,
            color = Eldoria.TextMid,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "$have / $need",
            style = EldoriaType.numeric,
            color = if (ok) Eldoria.Success else Eldoria.Danger,
            maxLines = 1
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  ESTADO VACÍO Y DIÁLOGO DE RENOMBRAR
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun PetEmptySanctuary(onExplore: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        EldoriaEmptyState(
            title = "El santuario está en silencio",
            message = "Todavía no has vinculado ninguna bestia. Las criaturas se doman en las expediciones, " +
                "en los contratos de doma y en los altares de los calabozos profundos.",
            icon = Icons.Filled.Pets,
            accent = Eldoria.Gold,
            actionLabel = "SALIR A BUSCAR UNA",
            onAction = onExplore,
            testTag = "pet_empty_sanctuary"
        )
    }
}

@Composable
private fun PetRenameDialog(
    current: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(current) }
    val shape = CutCornerShape(8.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Eldoria.Scrim),
            contentAlignment = Alignment.Center
        ) {
            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .widthIn(max = 420.dp),
                edge = EldoriaEdge.Gold,
                corner = Eldoria.R12,
                padding = PaddingValues(18.dp),
                glow = true,
                filigree = true,
                testTag = "pet_rename_dialog"
            ) {
                Text(
                    text = "Nuevo nombre",
                    style = EldoriaType.title,
                    color = Eldoria.TextGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Eldoria.S8))
                EldoriaDivider(color = Eldoria.Gold)
                Spacer(Modifier.height(Eldoria.S12))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(Brush.verticalGradient(listOf(Eldoria.PanelSunken, Eldoria.Abyss)))
                        .border(Eldoria.StrokeThin, Eldoria.goldEdge(), shape)
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = { value -> text = value.take(24) },
                        singleLine = true,
                        textStyle = EldoriaType.bodyStrong.copy(color = Eldoria.TextHi),
                        cursorBrush = SolidColor(Eldoria.Gold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pet_rename_field")
                    )
                    if (text.isEmpty()) {
                        Text(
                            text = "Escribe un nombre…",
                            style = EldoriaType.body,
                            color = Eldoria.TextLow
                        )
                    }
                }
                Spacer(Modifier.height(Eldoria.S6))
                Text(
                    text = "Máximo 24 caracteres.",
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow
                )
                Spacer(Modifier.height(Eldoria.S16))
                Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)) {
                    EldoriaButton(
                        text = "Cancelar",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        tone = EldoriaTone.Iron,
                        size = EldoriaButtonSize.Medium
                    )
                    EldoriaButton(
                        text = "BAUTIZAR",
                        onClick = { onConfirm(text.trim()) },
                        modifier = Modifier.weight(1f),
                        enabled = text.trim().isNotEmpty(),
                        tone = EldoriaTone.Gold,
                        size = EldoriaButtonSize.Medium,
                        testTag = "pet_rename_confirm"
                    )
                }
            }
        }
    }
}
