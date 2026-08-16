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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.Item
import com.example.data.content.EldoriaPets
import com.example.data.model.PetRecord
import com.example.data.model.PetSpecies
import com.example.data.model.PetTrait
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBarTone
import com.example.ui.design.EldoriaBeastSigil
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaRarityGem
import com.example.ui.design.EldoriaResourceBar
import com.example.ui.design.EldoriaRuneGlyph
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaFloat
import com.example.ui.design.eldoriaPaletteOf
import com.example.ui.design.eldoriaPressable
import com.example.ui.design.eldoriaPulse
import com.example.ui.art.EldoriaArt
import com.example.ui.getItemImageRes

// ──────────────────────────────────────────────────────────────────────────────
//  Piezas compartidas del Santuario de Bestias.
//  Todo lo `internal` lleva el prefijo Pet* / pet* para no chocar con el resto de
//  agentes que escriben en com.example.ui.screens.
// ──────────────────────────────────────────────────────────────────────────────

/** Ids canónicos de las tres disciplinas del motor. */
internal const val PET_DISC_FURY = "FURIA"
internal const val PET_DISC_BASTION = "BASTION"
internal const val PET_DISC_VITALITY = "VITALIDAD"

/** Par (claro, oscuro) de la paleta de la especie. Delegado en el arte procedural. */
internal fun petPaletteOf(key: String): Pair<Color, Color> = eldoriaPaletteOf(key)

/** Especie del registro (o null si el catálogo cambió bajo los pies del jugador). */
internal fun petSpeciesOf(record: PetRecord): PetSpecies? = EldoriaPets.species(record.speciesId)

internal fun petStageRoman(stage: Int): String = when (stage.coerceIn(1, 3)) {
    1 -> "I"
    2 -> "II"
    else -> "III"
}

/** Experiencia necesaria para el siguiente nivel (o el tope si ya está al máximo de la etapa). */
internal fun petExpNeed(record: PetRecord): Int {
    val cap = EldoriaPets.levelCapForStage(record.stage)
    return if (record.level >= cap) EldoriaPets.expForLevel(cap) else EldoriaPets.expForLevel(record.level + 1)
}

internal fun petDisciplineValue(record: PetRecord, discipline: String): Int =
    when (discipline.uppercase()) {
        PET_DISC_BASTION -> record.disciplineDef
        PET_DISC_VITALITY -> record.disciplineVit
        else -> record.disciplineAtk
    }

internal fun petDisciplineLabel(discipline: String): String = when (discipline.uppercase()) {
    PET_DISC_BASTION -> "Bastión"
    PET_DISC_VITALITY -> "Vitalidad"
    else -> "Furia"
}

internal fun petDisciplineColor(discipline: String): Color = when (discipline.uppercase()) {
    PET_DISC_BASTION -> Eldoria.ManaBright
    PET_DISC_VITALITY -> Eldoria.VitaeBright
    else -> Eldoria.Ember
}

internal fun petDisciplineBarTone(discipline: String): EldoriaBarTone = when (discipline.uppercase()) {
    PET_DISC_BASTION -> EldoriaBarTone.Mana
    PET_DISC_VITALITY -> EldoriaBarTone.Experience
    else -> EldoriaBarTone.Threat
}

/**
 * Motivo EXPLÍCITO por el que no se puede entrenar, o null si sí se puede.
 * Reproduce las tres barreras reales del controlador: tope, saciedad y oro.
 */
internal fun petTrainBlockReason(
    record: PetRecord,
    discipline: String,
    gold: Int,
    cost: Int
): String? {
    val cap = EldoriaPets.disciplineCap(record.level)
    val value = petDisciplineValue(record, discipline)
    return when {
        value >= cap -> "Tope de la disciplina alcanzado ($cap). Sube de nivel para ampliarlo."
        record.satiety < 10 -> "Sin saciedad (${record.satiety} %). Aliméntala en el comedero."
        gold < cost -> "Sin oro suficiente: te faltan ${cost - gold} de oro."
        else -> null
    }
}

/** Coste acumulado de N rutinas seguidas (el precio sube con cada punto ganado). */
internal fun petBulkTrainingCost(
    record: PetRecord,
    discipline: String,
    sessions: Int,
    quality: Int
): Int {
    val cap = EldoriaPets.disciplineCap(record.level)
    val stage = record.stage.coerceIn(1, 3)
    val gain = 1 + (quality.coerceIn(0, 100) * 8) / 100
    var current = petDisciplineValue(record, discipline)
    var total = 0
    repeat(sessions.coerceAtLeast(1)) {
        if (current < cap) {
            total += (120 + current * 40) * stage
            current = (current + gain).coerceAtMost(cap)
        }
    }
    return total
}

/** Clave de alimento equivalente a la del motor (BESTIAL / MISTICA / DRAGON / CELESTIAL). */
internal fun petFoodKeyOf(food: Item): String {
    val art = food.imageResName.lowercase()
    return when {
        art.contains("bestial") -> "BESTIAL"
        art.contains("mistica") -> "MISTICA"
        art.contains("dragon") -> "DRAGON"
        art.contains("celestial") -> "CELESTIAL"
        food.name.contains("Bestial", true) -> "BESTIAL"
        food.name.contains("Mística", true) -> "MISTICA"
        food.name.contains("Imperial", true) -> "DRAGON"
        else -> "CELESTIAL"
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  RETRATO
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Retrato de la bestia: JPG real si la especie lo tiene, sigilo procedural si no.
 * [framed] añade el marco metálico por rareza (el estelar de la pestaña Compañero).
 */
@Composable
internal fun PetPortrait(
    record: PetRecord,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    animated: Boolean = true,
    framed: Boolean = true
) {
    val species = remember(record.speciesId) { EldoriaPets.species(record.speciesId) }
    // Lámina por ESPECIE Y ETAPA. Se resuelve aquí, en la pieza que dibuja
    // todos los retratos de mascota del juego — tarjetas del santuario, foco,
    // listas — porque haberlo hecho sólo en la pantalla del foco dejaba las
    // miniaturas con el sigilo procedural y parecía que faltaban los assets.
    val stageArt = remember(record.speciesId, record.stage) {
        EldoriaArt.of("${record.speciesId}_s${record.stage.coerceIn(1, 3)}")
    }
    val art = record.imageResName.ifBlank { species?.imageResName ?: "" }
    val paletteKey = record.paletteKey.ifBlank { species?.paletteKey ?: "EMBER" }
    val palette = petPaletteOf(paletteKey)
    val seed = if (record.sigilSeed != 0) {
        record.sigilSeed
    } else {
        species?.sigilSeed ?: record.speciesId.hashCode()
    }
    val bob = if (animated) eldoriaFloat(periodMs = 3600, amplitude = 7.dp, label = "petPortraitBob") else 0.dp
    val halo = if (animated) eldoriaPulse(periodMs = 2600, from = 0.35f, to = 0.85f, label = "petPortraitHalo") else 0.6f

    if (framed) {
        EldoriaFrame(
            modifier = modifier.size(size),
            edge = EldoriaEdge.rarity(record.rarity),
            corner = Eldoria.R16,
            strokeWidth = Eldoria.StrokeBold,
            filigree = size >= 110.dp,
            rivets = size >= 150.dp,
            glowPulse = animated
        ) {
            PetPortraitBody(
                artRes = stageArt,
                art = art,
                seed = seed,
                stage = record.stage,
                primary = palette.first,
                secondary = palette.second,
                bob = bob,
                halo = halo,
                animated = animated,
                size = size
            )
        }
    } else {
        val shape = CutCornerShape(7.dp)
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(Eldoria.PanelSunken, Eldoria.Abyss)))
                .border(Eldoria.StrokeThin, EldoriaEdge.rarity(record.rarity).brush(), shape)
        ) {
            PetPortraitBody(
                artRes = stageArt,
                art = art,
                seed = seed,
                stage = record.stage,
                primary = palette.first,
                secondary = palette.second,
                bob = bob,
                halo = halo,
                animated = animated,
                size = size
            )
        }
    }
}

@Composable
private fun PetPortraitBody(
    /** Lámina de la etapa ya resuelta. Manda sobre [art] si existe. */
    artRes: Int?,
    art: String,
    seed: Int,
    stage: Int,
    primary: Color,
    secondary: Color,
    bob: Dp,
    halo: Float,
    animated: Boolean,
    size: Dp
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (this.size.minDimension <= 1f) return@Canvas
            val r = this.size.minDimension * 0.66f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primary.copy(alpha = 0.26f * halo), Color.Transparent),
                    center = center,
                    radius = r
                ),
                radius = r,
                center = center
            )
        }
        val resolved = artRes ?: art.takeIf { it.isNotBlank() }?.let { getItemImageRes(it, "PET") }
        if (resolved != null) {
            Image(
                painter = painterResource(id = resolved),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = bob)
            )
        } else {
            EldoriaBeastSigil(
                seed = seed,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(size * 0.09f)
                    .offset(y = bob),
                primary = primary,
                secondary = secondary,
                stage = stage,
                animated = animated
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  RASGOS
// ──────────────────────────────────────────────────────────────────────────────

/** Rasgo con glifo rúnico. Al tocarlo, la pantalla abre la hoja con su descripción. */
@Composable
internal fun PetTraitChip(
    trait: PetTrait,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val color = petPaletteOf(trait.tone).first
    val shape = CutCornerShape(6.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(color.copy(alpha = 0.22f), Eldoria.PanelSunken)
                )
            )
            .then(if (onClick != null) Modifier.eldoriaPressable(onClick = onClick) else Modifier)
            .border(Eldoria.StrokeThin, color.copy(alpha = 0.62f), shape)
            .padding(horizontal = 9.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EldoriaRuneGlyph(
            seed = trait.id.hashCode(),
            modifier = Modifier.size(15.dp),
            color = color,
            strokeWidth = 1.2.dp,
            animated = false
        )
        Spacer(Modifier.width(Eldoria.S6))
        Text(
            text = trait.name,
            style = EldoriaType.caption,
            color = Eldoria.TextHi,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  CELDA DE ESTABLO
// ──────────────────────────────────────────────────────────────────────────────

/** Ficha de una bestia del establo: retrato, nivel, etapa, rareza, rasgos y acciones. */
@Composable
internal fun PetRosterCell(
    record: PetRecord,
    isActive: Boolean,
    isFocused: Boolean,
    onSelect: () -> Unit,
    onEquip: () -> Unit,
    onRename: () -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val species = remember(record.speciesId) { EldoriaPets.species(record.speciesId) }
    val traits = remember(record.traits) { record.traits.mapNotNull { EldoriaPets.trait(it) } }
    val levelCap = EldoriaPets.levelCapForStage(record.stage)

    EldoriaPanel(
        modifier = modifier.fillMaxWidth(),
        edge = if (isActive) EldoriaEdge.Gold else EldoriaEdge.rarity(record.rarity),
        corner = Eldoria.R12,
        padding = PaddingValues(11.dp),
        glow = isActive || isFocused,
        filigree = isActive,
        onClick = onSelect
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PetPortrait(
                record = record,
                size = 62.dp,
                animated = isActive,
                framed = false
            )
            Spacer(Modifier.width(Eldoria.S8))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EldoriaRarityGem(rarity = record.rarity, size = 12.dp)
                    Spacer(Modifier.width(Eldoria.S4))
                    Text(
                        text = record.name,
                        style = EldoriaType.subheading,
                        color = Eldoria.rarityColor(record.rarity),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Nivel ${record.level}/$levelCap · Etapa ${petStageRoman(record.stage)}",
                    style = EldoriaType.caption,
                    color = Eldoria.TextMid,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = species?.title ?: record.rarity,
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(Eldoria.S8))
        EldoriaResourceBar(
            current = record.bond,
            max = 100,
            tone = EldoriaBarTone.Bond,
            label = "VÍNCULO",
            height = 11.dp,
            showNumbers = true
        )
        Spacer(Modifier.height(Eldoria.S4))
        EldoriaResourceBar(
            current = record.satiety,
            max = 100,
            tone = EldoriaBarTone.Satiety,
            label = "SACIEDAD",
            height = 11.dp,
            showNumbers = true
        )

        if (traits.isNotEmpty()) {
            Spacer(Modifier.height(Eldoria.S8))
            Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S4)) {
                traits.take(2).forEach { trait ->
                    EldoriaChip(
                        text = trait.name,
                        color = petPaletteOf(trait.tone).first,
                        filled = false
                    )
                }
                if (traits.size > 2) {
                    EldoriaChip(text = "+${traits.size - 2}", color = Eldoria.Silver)
                }
            }
        }

        Spacer(Modifier.height(Eldoria.S8))
        EldoriaDivider(color = if (isActive) Eldoria.Gold else Eldoria.Iron, ornament = false)
        Spacer(Modifier.height(Eldoria.S8))

        Row(verticalAlignment = Alignment.CenterVertically) {
            EldoriaButton(
                text = if (isActive) "EN COMBATE" else "EQUIPAR",
                onClick = onEquip,
                modifier = Modifier.weight(1f),
                enabled = !isActive,
                tone = if (isActive) EldoriaTone.Vitae else EldoriaTone.Gold,
                size = EldoriaButtonSize.Small,
                testTag = "pet_equip_${record.id}"
            )
            Spacer(Modifier.width(Eldoria.S6))
            EldoriaIconButton(
                icon = Icons.Filled.Edit,
                contentDescription = "Renombrar a ${record.name}",
                onClick = onRename,
                tone = EldoriaTone.Iron,
                size = 34.dp
            )
            Spacer(Modifier.width(Eldoria.S4))
            EldoriaIconButton(
                icon = Icons.Filled.Delete,
                contentDescription = "Liberar a ${record.name}",
                onClick = onRelease,
                tone = EldoriaTone.Blood,
                size = 34.dp,
                testTag = "pet_release_${record.id}"
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
//  TARJETA DE DISCIPLINA
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Disciplina entrenable: valor/tope, coste real y las dos vías de adiestramiento.
 * Cuando no se puede entrenar NO se pinta un botón muerto: se explica el motivo.
 */
@Composable
internal fun PetDisciplineCard(
    discipline: String,
    icon: ImageVector,
    value: Int,
    cap: Int,
    cost: Int,
    bulkCost: Int,
    blockReason: String?,
    bulkBlockReason: String?,
    onMinigame: () -> Unit,
    onRoutine: () -> Unit,
    onBulkRoutine: () -> Unit,
    routineTestTag: String,
    modifier: Modifier = Modifier
) {
    val accent = petDisciplineColor(discipline)
    val label = petDisciplineLabel(discipline)
    val enabled = blockReason == null

    EldoriaPanel(
        modifier = modifier.fillMaxWidth(),
        edge = if (enabled) {
            EldoriaEdge(
                top = accent,
                mid = accent.copy(alpha = 0.70f),
                bottom = accent.copy(alpha = 0.32f),
                glow = accent.copy(alpha = 0.22f)
            )
        } else {
            EldoriaEdge.Iron
        },
        corner = Eldoria.R12,
        padding = PaddingValues(13.dp),
        glow = enabled
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) accent else Eldoria.TextLow,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(Eldoria.S8))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label.uppercase(),
                    style = EldoriaType.label,
                    color = if (enabled) accent else Eldoria.TextLow,
                    maxLines = 1
                )
                Text(
                    text = when (discipline.uppercase()) {
                        PET_DISC_BASTION -> "Absorbe daño con la orden Guardia."
                        PET_DISC_VITALITY -> "Cura con la orden Aliento."
                        else -> "Daño de la orden Embestida."
                    },
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "$value/$cap",
                style = EldoriaType.numeric,
                color = if (enabled) Eldoria.TextHi else Eldoria.TextLow,
                maxLines = 1
            )
        }

        Spacer(Modifier.height(Eldoria.S8))
        EldoriaResourceBar(
            current = value,
            max = cap,
            tone = petDisciplineBarTone(discipline),
            height = 12.dp,
            showNumbers = false
        )

        Spacer(Modifier.height(Eldoria.S8))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Sesión: $cost de oro · −10 saciedad",
                style = EldoriaType.small,
                color = Eldoria.TextMid,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!enabled) {
            Spacer(Modifier.height(Eldoria.S8))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Eldoria.Warning,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(Eldoria.S6))
                Text(
                    text = blockReason ?: "",
                    style = EldoriaType.small,
                    color = Eldoria.Warning,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(Eldoria.S12))
        EldoriaButton(
            text = "ENTRENAR (MINIJUEGO)",
            onClick = onMinigame,
            fullWidth = true,
            enabled = enabled,
            tone = EldoriaTone.Gold,
            size = EldoriaButtonSize.Medium,
            icon = Icons.Filled.Casino,
            testTag = "pet_train_minigame_${discipline.uppercase()}"
        )
        Spacer(Modifier.height(Eldoria.S6))
        Row(verticalAlignment = Alignment.CenterVertically) {
            EldoriaButton(
                text = "Rutina básica",
                onClick = onRoutine,
                modifier = Modifier.weight(1f),
                enabled = enabled,
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Small,
                testTag = routineTestTag
            )
            Spacer(Modifier.width(Eldoria.S6))
            EldoriaButton(
                text = "Rutina ×5",
                onClick = onBulkRoutine,
                modifier = Modifier.weight(1f),
                enabled = bulkBlockReason == null,
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Small
            )
        }
        Spacer(Modifier.height(Eldoria.S6))
        Text(
            text = bulkBlockReason ?: "Rutina básica: 40 % de eficacia. Rutina ×5: $bulkCost de oro y 50 de saciedad.",
            style = EldoriaType.caption,
            color = if (bulkBlockReason != null) Eldoria.Warning else Eldoria.TextLow,
            textAlign = TextAlign.Start,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
