package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.GameViewModel
import com.example.data.content.EldoriaMaterials
import com.example.data.content.EldoriaPotions
import com.example.data.formatGameNumber
import com.example.data.getRequiredExpForLevel
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaStatPill
import com.example.ui.design.EldoriaToggleChip
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType

// ══════════════════════════════════════════════════════════════════════════════
//  CONSOLA DEL ARCANISTA — panel de desarrollo dentro de Ajustes.
//
//  Se abre escribiendo el código de desarrollador y, una vez abierta, queda
//  desbloqueada en los ajustes de la partida hasta que se cierre a mano. Todo
//  lo que hace salta el balance a propósito: es una herramienta de pruebas.
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Código de desarrollador. Se compara ignorando mayúsculas, espacios y guiones,
 * y en ningún sitio de la interfaz se insinúa cuál es: el campo va vacío.
 */
private const val DEV_CODE = "WTFISTHIS219"

/** "eldoria-1337", "Eldoria 1337" y "ELDORIA1337" son el mismo código. */
private fun normalizeCode(raw: String): String =
    raw.uppercase().filter { it.isLetterOrDigit() }

private val DEV_SLOT_LABELS: Map<String, String> = mapOf(
    "WEAPON" to "Arma",
    "SHIELD" to "Escudo",
    "HELMET" to "Casco",
    "ARMOR" to "Armadura",
    "GLOVES" to "Guantes",
    "BOOTS" to "Botas",
    "RING" to "Anillo",
    "EARRING" to "Pendiente",
    "WINGS" to "Alas",
    "RELIC" to "Reliquia"
)

/**
 * Punto de entrada desde Ajustes: enseña el cerrojo o la consola entera según
 * el estado guardado en la partida.
 */
@Composable
fun DevConsoleSection(viewModel: GameViewModel, unlocked: Boolean) {
    var askCode by remember { mutableStateOf(false) }

    Spacer(Modifier.height(Eldoria.S20))
    EldoriaSectionTitle(
        text = if (unlocked) "CONSOLA DEL ARCANISTA" else "CÁMARA SELLADA",
        icon = if (unlocked) Icons.Default.LockOpen else Icons.Default.Lock,
        accent = if (unlocked) Eldoria.Arcane else Eldoria.Iron
    )
    Spacer(Modifier.height(Eldoria.S8))

    if (!unlocked) {
        EldoriaPanel(
            modifier = Modifier.fillMaxWidth(),
            edge = EldoriaEdge.Iron,
            padding = PaddingValues(14.dp)
        ) {
            Text(
                text = "Tras esta puerta hay herramientas que rompen el juego a propósito: " +
                    "nivel, oro, atributos y equipo a medida. Hace falta el código de desarrollador.",
                style = EldoriaType.small,
                color = Eldoria.TextMid
            )
            Spacer(Modifier.height(Eldoria.S12))
            EldoriaButton(
                text = "INTRODUCIR CÓDIGO",
                onClick = { askCode = true },
                modifier = Modifier.fillMaxWidth(),
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Medium,
                icon = Icons.Default.Lock,
                fullWidth = true,
                testTag = "settings_dev_unlock_btn"
            )
        }
    } else {
        DevConsoleBody(viewModel)
    }

    if (askCode) {
        DevCodeDialog(
            onDismiss = { askCode = false },
            onAccept = {
                askCode = false
                viewModel.devSetUnlocked(true)
                viewModel.showNotification("🔓 Consola del arcanista desbloqueada.")
            }
        )
    }
}

// ──────────────────────────── CUERPO DE LA CONSOLA ────────────────────────────

@Composable
private fun DevConsoleBody(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()

    val level = progress?.charLevel ?: 1
    val gold = progress?.charGold ?: 0

    // Los campos arrancan con el valor real del héroe y se quedan donde el
    // usuario los deje: escribir "120" y tocar tres botones no debe resetearlos.
    var levelField by remember(level) { mutableStateOf(level.toString()) }
    var goldField by remember { mutableStateOf("") }
    var forgeType by remember { mutableStateOf("WEAPON") }
    var forgeRarity by remember { mutableStateOf("UNIVERSAL") }
    var forgeLevelField by remember(level) { mutableStateOf(level.toString()) }
    var forgeCountField by remember { mutableStateOf("1") }
    var potionId by remember { mutableStateOf(EldoriaPotions.ALL.first().id) }
    var potionQtyField by remember { mutableStateOf("10") }

    val forgeLevel = forgeLevelField.toIntOrNull() ?: level
    val forgeCount = forgeCountField.toIntOrNull() ?: 1

    // ───────────────────────── ESTADO ACTUAL ─────────────────────────
    EldoriaPanel(
        modifier = Modifier.fillMaxWidth(),
        edge = EldoriaEdge.Arcane,
        padding = PaddingValues(14.dp)
    ) {
        Text(
            text = "Cada cambio se guarda en la partida al instante. No hay deshacer: " +
                "exporta una copia en DATOS antes de trastear.",
            style = EldoriaType.small,
            color = Eldoria.Warning
        )
        Spacer(Modifier.height(Eldoria.S12))
        Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)) {
            EldoriaStatPill(
                label = "Nivel",
                value = level.toString(),
                icon = Icons.Default.Person,
                accent = Eldoria.Arcane,
                modifier = Modifier.weight(1f)
            )
            EldoriaStatPill(
                label = "Oro",
                value = formatGameNumber(gold),
                icon = Icons.Default.Diamond,
                accent = Eldoria.Gold,
                modifier = Modifier.weight(1f)
            )
            EldoriaStatPill(
                label = "EXP",
                value = "${progress?.charExp ?: 0}/${getRequiredExpForLevel(level)}",
                icon = Icons.Default.Bolt,
                accent = Eldoria.Info,
                modifier = Modifier.weight(1f)
            )
        }
    }

    // ───────────────────────── HÉROE ─────────────────────────
    Spacer(Modifier.height(Eldoria.S12))
    DevBlock(title = "HÉROE", icon = Icons.Default.Person, accent = Eldoria.Vitae, edge = EldoriaEdge.Vitae) {
        DevFieldRow(
            label = "Nivel objetivo",
            value = levelField,
            onValueChange = { levelField = it },
            hint = "1 – 500",
            actionLabel = "FIJAR",
            testTag = "dev_level_field",
            onAction = { viewModel.devSetLevel(levelField.toIntOrNull() ?: level) }
        )
        Spacer(Modifier.height(Eldoria.S8))
        DevQuickRow(
            listOf(
                "−10" to { viewModel.devSetLevel(level - 10) },
                "−1" to { viewModel.devSetLevel(level - 1) },
                "+1" to { viewModel.devSetLevel(level + 1) },
                "+10" to { viewModel.devSetLevel(level + 10) },
                "+50" to { viewModel.devSetLevel(level + 50) }
            )
        )

        DevSeparator()

        DevFieldRow(
            label = "Oro exacto",
            value = goldField,
            onValueChange = { goldField = it },
            hint = formatGameNumber(gold),
            actionLabel = "FIJAR",
            testTag = "dev_gold_field",
            onAction = { goldField.toIntOrNull()?.let { viewModel.devSetGold(it) } }
        )
        Spacer(Modifier.height(Eldoria.S8))
        DevQuickRow(
            listOf(
                "+10 K" to { viewModel.devAddGold(10_000) },
                "+100 K" to { viewModel.devAddGold(100_000) },
                "+1 M" to { viewModel.devAddGold(1_000_000) },
                "A CERO" to { viewModel.devSetGold(0) }
            )
        )

        DevSeparator()

        Text(
            text = "Puntos sin gastar: ${progress?.statPointsAvailable ?: 0} de atributo · " +
                "${progress?.talentPointsAvailable ?: 0} de talento",
            style = EldoriaType.caption,
            color = Eldoria.TextLow
        )
        Spacer(Modifier.height(Eldoria.S8))
        DevQuickRow(
            listOf(
                "+10 ATRIB." to { viewModel.devGrantPoints(10, 0) },
                "+50 ATRIB." to { viewModel.devGrantPoints(50, 0) },
                "+5 TALENTO" to { viewModel.devGrantPoints(0, 5) },
                "+25 TALENTO" to { viewModel.devGrantPoints(0, 25) }
            )
        )

        DevSeparator()

        Text(
            text = "Atributos base: FUE ${progress?.statStr ?: 0} · DES ${progress?.statDex ?: 0} · " +
                "INT ${progress?.statInt ?: 0} · CON ${progress?.statCon ?: 0}",
            style = EldoriaType.caption,
            color = Eldoria.TextLow
        )
        Spacer(Modifier.height(Eldoria.S8))
        listOf("STR" to "FUERZA", "DEX" to "DESTREZA", "INT" to "INTELECTO", "CON" to "CONSTITUCIÓN")
            .forEach { (key, label) ->
                DevAttributeRow(
                    label = label,
                    current = when (key) {
                        "STR" -> progress?.statStr ?: 0
                        "DEX" -> progress?.statDex ?: 0
                        "INT" -> progress?.statInt ?: 0
                        else -> progress?.statCon ?: 0
                    },
                    onSet = { viewModel.devSetAttribute(key, it) }
                )
            }

        DevSeparator()

        EldoriaButton(
            text = "VIDA Y MANÁ AL MÁXIMO",
            onClick = { viewModel.devFullRestore() },
            modifier = Modifier.fillMaxWidth(),
            tone = EldoriaTone.Vitae,
            size = EldoriaButtonSize.Medium,
            icon = Icons.Default.Favorite,
            fullWidth = true,
            testTag = "dev_full_restore_btn"
        )
    }

    // ───────────────────────── FORJA DE EQUIPO ─────────────────────────
    Spacer(Modifier.height(Eldoria.S12))
    DevBlock(title = "FORJA DE EQUIPO", icon = Icons.Default.Build, accent = Eldoria.Ember, edge = EldoriaEdge.Ember) {
        Text(text = "Hueco", style = EldoriaType.label, color = Eldoria.TextMid)
        Spacer(Modifier.height(Eldoria.S6))
        DevChipGrid(
            options = viewModel.devEquipSlots.map { it to (DEV_SLOT_LABELS[it] ?: it) },
            selected = forgeType,
            accent = Eldoria.Ember,
            perRow = 3,
            testTagPrefix = "dev_forge_type_",
            onSelect = { forgeType = it }
        )

        Spacer(Modifier.height(Eldoria.S12))
        Text(text = "Rareza", style = EldoriaType.label, color = Eldoria.TextMid)
        Spacer(Modifier.height(Eldoria.S6))
        DevChipGrid(
            options = viewModel.devRarities.map { it to it },
            selected = forgeRarity,
            accent = Eldoria.Gold,
            perRow = 3,
            testTagPrefix = "dev_forge_rarity_",
            onSelect = { forgeRarity = it },
            colorOf = { Eldoria.rarityColor(it) }
        )

        Spacer(Modifier.height(Eldoria.S12))
        Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)) {
            DevNumberField(
                label = "Nivel del objeto",
                value = forgeLevelField,
                onValueChange = { forgeLevelField = it },
                hint = "1 – 500",
                testTag = "dev_forge_level_field",
                modifier = Modifier.weight(1f)
            )
            DevNumberField(
                label = "Cantidad",
                value = forgeCountField,
                onValueChange = { forgeCountField = it },
                hint = "1 – 20",
                testTag = "dev_forge_count_field",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(Eldoria.S12))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
        ) {
            EldoriaButton(
                text = "AL ZURRÓN",
                onClick = { viewModel.devForgeItem(forgeType, forgeRarity, forgeLevel, forgeCount, equip = false) },
                modifier = Modifier.weight(1f),
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Medium,
                testTag = "dev_forge_bag_btn"
            )
            EldoriaButton(
                text = "FORJAR Y EQUIPAR",
                onClick = { viewModel.devForgeItem(forgeType, forgeRarity, forgeLevel, forgeCount, equip = true) },
                modifier = Modifier.weight(1f),
                tone = EldoriaTone.Ember,
                size = EldoriaButtonSize.Medium,
                testTag = "dev_forge_equip_btn"
            )
        }
        Spacer(Modifier.height(Eldoria.S8))
        EldoriaButton(
            text = "SET COMPLETO EN LOS 10 HUECOS",
            onClick = { viewModel.devForgeFullSet(forgeRarity, forgeLevel) },
            modifier = Modifier.fillMaxWidth(),
            tone = EldoriaTone.Gold,
            size = EldoriaButtonSize.Large,
            icon = Icons.Default.Shield,
            fullWidth = true,
            testTag = "dev_forge_set_btn"
        )
        Spacer(Modifier.height(Eldoria.S6))
        Text(
            text = "El equipo forjado se pone aunque su nivel supere al del héroe; " +
                "lo que llevabas puesto vuelve al inventario.",
            style = EldoriaType.caption,
            color = Eldoria.TextLow
        )
    }

    // ───────────────────────── CONSUMIBLES ─────────────────────────
    Spacer(Modifier.height(Eldoria.S12))
    DevBlock(title = "SUMINISTROS", icon = Icons.Default.Science, accent = Eldoria.Mana, edge = EldoriaEdge.Silver) {
        Text(text = "Frasco", style = EldoriaType.label, color = Eldoria.TextMid)
        Spacer(Modifier.height(Eldoria.S6))
        DevChipGrid(
            options = EldoriaPotions.ALL.map { it.id to it.name },
            selected = potionId,
            accent = Eldoria.Vitae,
            perRow = 2,
            testTagPrefix = "dev_potion_",
            onSelect = { potionId = it }
        )
        Spacer(Modifier.height(Eldoria.S8))
        DevFieldRow(
            label = "Cantidad de frascos",
            value = potionQtyField,
            onValueChange = { potionQtyField = it },
            hint = "1 – 99",
            actionLabel = "AÑADIR",
            testTag = "dev_potion_qty_field",
            onAction = { viewModel.devGrantPotions(potionId, potionQtyField.toIntOrNull() ?: 1) }
        )

        DevSeparator()

        DevQuickRow(
            listOf(
                "+10 ANTORCHAS" to { viewModel.devGrantTorches(10) },
                "+50 ANTORCHAS" to { viewModel.devGrantTorches(50) }
            )
        )
        Spacer(Modifier.height(Eldoria.S8))
        DevQuickRow(
            listOf(
                "MATERIALES ×50" to { viewModel.devGrantAllMaterials(50) },
                "MATERIALES ×500" to { viewModel.devGrantAllMaterials(500) }
            )
        )
        Spacer(Modifier.height(Eldoria.S6))
        Text(
            text = "Los materiales se reparten entre los ${EldoriaMaterials.ALL.size} tipos del catálogo.",
            style = EldoriaType.caption,
            color = Eldoria.TextLow
        )
    }

    // ───────────────────────── MUNDO ─────────────────────────
    Spacer(Modifier.height(Eldoria.S12))
    DevBlock(title = "MUNDO", icon = Icons.Default.AutoAwesome, accent = Eldoria.Gold, edge = EldoriaEdge.Gold) {
        EldoriaButton(
            text = "DESBLOQUEAR TODOS LOS CALABOZOS",
            onClick = { viewModel.devUnlockAllDungeons() },
            modifier = Modifier.fillMaxWidth(),
            tone = EldoriaTone.Gold,
            size = EldoriaButtonSize.Medium,
            icon = Icons.Default.LocalFireDepartment,
            fullWidth = true,
            testTag = "dev_unlock_dungeons_btn"
        )
    }

    // ───────────────────────── CERRAR ─────────────────────────
    Spacer(Modifier.height(Eldoria.S12))
    EldoriaButton(
        text = "CERRAR LA CONSOLA",
        onClick = { viewModel.devSetUnlocked(false) },
        modifier = Modifier.fillMaxWidth(),
        tone = EldoriaTone.Blood,
        size = EldoriaButtonSize.Medium,
        icon = Icons.Default.Lock,
        fullWidth = true,
        testTag = "dev_lock_btn"
    )
    Spacer(Modifier.height(Eldoria.S6))
    Text(
        text = "Al cerrarla vuelve a pedir el código. Lo que hayas cambiado se queda.",
        style = EldoriaType.caption,
        color = Eldoria.TextLow
    )
}

// ──────────────────────────── PIEZAS PRIVADAS ────────────────────────────

@Composable
private fun DevBlock(
    title: String,
    icon: ImageVector,
    accent: Color,
    edge: EldoriaEdge,
    content: @Composable ColumnScope.() -> Unit
) {
    EldoriaSectionTitle(text = title, icon = icon, accent = accent)
    Spacer(Modifier.height(Eldoria.S6))
    EldoriaPanel(
        modifier = Modifier.fillMaxWidth(),
        edge = edge,
        padding = PaddingValues(14.dp),
        content = content
    )
}

@Composable
private fun DevSeparator() {
    Spacer(Modifier.height(Eldoria.S12))
    EldoriaDivider(color = Eldoria.IronEdge.copy(alpha = 0.55f), ornament = false)
    Spacer(Modifier.height(Eldoria.S12))
}

/** Campo numérico con etiqueta encima, al estilo de los paneles hundidos del juego. */
@Composable
private fun DevNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val shape = CutCornerShape(6.dp)
    Column(modifier = modifier) {
        Text(
            text = label,
            style = EldoriaType.caption,
            color = Eldoria.TextMid,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(Eldoria.S4))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(Brush.verticalGradient(listOf(Eldoria.PanelSunken, Eldoria.Abyss)))
                .border(Eldoria.StrokeThin, Eldoria.ironEdge(), shape)
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            BasicTextField(
                // Sólo dígitos: un campo de nivel con letras sólo produce nulos.
                value = value,
                onValueChange = { next -> onValueChange(next.filter { it.isDigit() }.take(7)) },
                singleLine = true,
                textStyle = EldoriaType.numeric.copy(color = Eldoria.TextHi),
                cursorBrush = SolidColor(Eldoria.Gold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag)
            )
            if (value.isEmpty()) {
                Text(text = hint, style = EldoriaType.body, color = Eldoria.TextLow)
            }
        }
    }
}

/** Campo numérico + botón de acción en la misma línea. */
@Composable
private fun DevFieldRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    actionLabel: String,
    testTag: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        DevNumberField(
            label = label,
            value = value,
            onValueChange = onValueChange,
            hint = hint,
            testTag = testTag,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(Eldoria.S8))
        EldoriaButton(
            text = actionLabel,
            onClick = onAction,
            enabled = value.isNotBlank(),
            tone = EldoriaTone.Gold,
            size = EldoriaButtonSize.Medium,
            testTag = "${testTag}_action"
        )
    }
}

/** Fila de botones pequeños repartidos a partes iguales. */
@Composable
private fun DevQuickRow(actions: List<Pair<String, () -> Unit>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
    ) {
        actions.forEach { (label, action) ->
            EldoriaButton(
                text = label,
                onClick = action,
                modifier = Modifier.weight(1f),
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Small
            )
        }
    }
}

@Composable
private fun DevAttributeRow(
    label: String,
    current: Int,
    onSet: (Int) -> Unit
) {
    var field by remember(current) { mutableStateOf(current.toString()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        DevNumberField(
            label = label,
            value = field,
            onValueChange = { field = it },
            hint = current.toString(),
            testTag = "dev_attr_${label.lowercase()}",
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(Eldoria.S8))
        EldoriaButton(
            text = "FIJAR",
            onClick = { field.toIntOrNull()?.let(onSet) },
            enabled = field.isNotBlank(),
            tone = EldoriaTone.Arcane,
            size = EldoriaButtonSize.Medium
        )
    }
}

/**
 * Rejilla de chips seleccionables en filas de [perRow]. Se hace a mano en vez de
 * con FlowRow para no depender de una API experimental de layout.
 */
@Composable
private fun DevChipGrid(
    options: List<Pair<String, String>>,
    selected: String,
    accent: Color,
    perRow: Int,
    testTagPrefix: String,
    onSelect: (String) -> Unit,
    colorOf: ((String) -> Color)? = null
) {
    options.chunked(perRow).forEach { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Eldoria.S6),
            horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
        ) {
            row.forEach { (id, label) ->
                EldoriaToggleChip(
                    text = label,
                    selected = id == selected,
                    onClick = { onSelect(id) },
                    modifier = Modifier.weight(1f),
                    accent = colorOf?.invoke(id) ?: accent,
                    testTag = "$testTagPrefix$id"
                )
            }
            // Relleno para que la última fila incompleta no estire sus chips.
            repeat(perRow - row.size) {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ──────────────────────────── DIÁLOGO DEL CÓDIGO ────────────────────────────

@Composable
private fun DevCodeDialog(
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var failed by remember { mutableStateOf(false) }
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
                edge = EldoriaEdge.Arcane,
                corner = Eldoria.R12,
                padding = PaddingValues(18.dp),
                glow = true,
                filigree = true,
                testTag = "dev_code_dialog"
            ) {
                Text(
                    text = "Código de desarrollador",
                    style = EldoriaType.title,
                    color = Eldoria.TextGold
                )
                Spacer(Modifier.height(Eldoria.S6))
                Text(
                    text = "Escribe el código para abrir la consola del arcanista.",
                    style = EldoriaType.small,
                    color = Eldoria.TextMid
                )
                Spacer(Modifier.height(Eldoria.S12))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(Brush.verticalGradient(listOf(Eldoria.PanelSunken, Eldoria.Abyss)))
                        .border(
                            Eldoria.StrokeThin,
                            if (failed) SolidColor(Eldoria.Danger) else Eldoria.arcaneEdge(),
                            shape
                        )
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = {
                            text = it.take(24)
                            failed = false
                        },
                        singleLine = true,
                        textStyle = EldoriaType.bodyStrong.copy(color = Eldoria.TextHi),
                        cursorBrush = SolidColor(Eldoria.Arcane),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dev_code_field")
                    )
                    // Sin placeholder a propósito: cualquier pista aquí deja de
                    // hacer secreto el código.
                }
                if (failed) {
                    Spacer(Modifier.height(Eldoria.S6))
                    Text(
                        text = "Ese código no abre nada.",
                        style = EldoriaType.caption,
                        color = Eldoria.Danger
                    )
                }
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
                        text = "ABRIR",
                        onClick = {
                            if (normalizeCode(text) == DEV_CODE) onAccept() else failed = true
                        },
                        modifier = Modifier.weight(1f),
                        enabled = text.isNotBlank(),
                        tone = EldoriaTone.Arcane,
                        size = EldoriaButtonSize.Medium,
                        testTag = "dev_code_confirm"
                    )
                }
            }
        }
    }
}
