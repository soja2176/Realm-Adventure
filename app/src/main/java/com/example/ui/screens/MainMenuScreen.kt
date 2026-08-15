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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.GameProgress
import com.example.data.GameScreen
import com.example.data.GameViewModel
import com.example.data.KingdomGenerator
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaConfirmDialog
import com.example.ui.design.EldoriaCounter
import com.example.ui.design.EldoriaCrest
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmptyState
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaScrollSheet
import com.example.ui.design.EldoriaSheet
import com.example.ui.design.EldoriaSlotFrame
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaFloat
import com.example.ui.design.eldoriaPulse
import com.example.ui.getCharacterPortrait

// ══════════════════════════════════════════════════════════════════════════════
//  MENÚ PRINCIPAL — la primera impresión de Eldoria.
//  Pantalla a sangre: aplica sus propios insets, sin barra superior ni nav.
// ══════════════════════════════════════════════════════════════════════════════

private enum class MenuSheetKind { NONE, HEROES, LORE, BACKUP, CREDITS }

private class MenuLoreRegion(
    val name: String,
    val subtitle: String,
    val text: String
)

private val MENU_LORE_REGIONS: List<MenuLoreRegion> = listOf(
    MenuLoreRegion(
        name = "Eldoria",
        subtitle = "El Valle del Alba",
        text = "Cuna del mundo y último refugio de la magia primordial. Sus praderas esmeralda crecen sobre los huesos de dioses dormidos, y cada amanecer las ruinas cantan en una lengua que nadie recuerda haber aprendido. Quien nace aquí nace debiendo algo."
    ),
    MenuLoreRegion(
        name = "Drakenhold",
        subtitle = "Los Cañones de Obsidiana",
        text = "Un reino forjado dentro de la garganta de un volcán vivo. Los señores dracónicos midieron su nobleza en cicatrices y sellaron sus pactos con hierro al rojo. Dicen que bajo la última fragua duerme el Primer Wyrm, y que su respiración es el temblor que nunca cesa."
    ),
    MenuLoreRegion(
        name = "Frostgard",
        subtitle = "La Mordaza Blanca",
        text = "Más allá de los pasos helados, el silencio pesa más que la nieve. Sus clanes tallan sus crónicas en el hielo porque el pergamino se pudre y la memoria miente. Allí el invierno no es una estación: es un juez, y dicta sentencia cada noche."
    ),
    MenuLoreRegion(
        name = "Aethelgard",
        subtitle = "El Sepulcro de Reyes",
        text = "Ciudades enteras hundidas en la ciénaga, con las torres asomando como dedos de ahogados. Aquí los muertos conservaron sus títulos y siguen cobrando tributo a los vivos. Ningún mapa honrado marca sus caminos: sólo los cuenta quien volvió, y volvieron pocos."
    ),
    MenuLoreRegion(
        name = "Solaria",
        subtitle = "El Yunque del Sol",
        text = "Desiertos de vidrio donde la luz misma corta. Sus templos-observatorio miden el paso de eras enteras y su orden de guardianes jura no dormir hasta que la última sombra sea nombrada. El oro abunda; el agua, no. Ambas cosas explican su historia."
    ),
    MenuLoreRegion(
        name = "Aetheria",
        subtitle = "Los Peldaños del Cielo",
        text = "Islas suspendidas sobre el vacío, unidas por puentes de luz que sólo aguantan al que no duda. Fue el último trono del Renacer, y su caída arrancó la magia de raíz del mundo de abajo. Subir hasta allí es la promesa que da nombre a esta crónica."
    )
)

@Composable
fun MainMenuScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val characters by viewModel.allCharactersState.collectAsState()
    val expedition by viewModel.systems.expedition.collectAsState()
    val backupStatus by viewModel.backupStatus.collectAsState()
    val dailyState by viewModel.dailyRewardState.collectAsState()

    val hero = progress?.takeIf { it.hasActiveChar }
    val canClaimDaily = remember(dailyState) { viewModel.canClaimDailyRewardNow() }

    var sheet by remember { mutableStateOf(MenuSheetKind.NONE) }
    var confirmNewGame by remember { mutableStateOf(false) }
    var confirmRestore by remember { mutableStateOf(false) }
    var heroPendingDeletion by remember { mutableStateOf<Int?>(null) }

    val glow = eldoriaPulse(periodMs = 2600, from = 0.30f, to = 1f, label = "menuTitleGlow")
    val crestLift = eldoriaFloat(periodMs = 4400, amplitude = 10.dp, label = "menuCrestFloat")

    EldoriaScreen(
        depth = 1,
        embers = true,
        fog = true,
        vignetteStrength = 0.78f,
        backgroundArtRes = R.drawable.img_medieval_map,
        backgroundArtAlpha = 0.30f,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            // Sin insets propios: el Scaffold ya los inyecta en `innerPadding` cuando
            // la pantalla va sin cromo, y aplicarlos aquí los duplicaba.
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Eldoria.S24))

            // ───────────────── LOGOTIPO ─────────────────
            Box(
                modifier = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val r = size.minDimension / 2f
                    if (r <= 0f) return@Canvas
                    drawCircle(
                        brush = Brush.radialGradient(
                            0.00f to Eldoria.GoldBright.copy(alpha = 0.22f * glow),
                            0.45f to Eldoria.Gold.copy(alpha = 0.13f * glow),
                            1.00f to Color.Transparent,
                            center = center,
                            radius = r
                        ),
                        radius = r,
                        center = center
                    )
                }
                EldoriaCrest(
                    seed = 1337,
                    modifier = Modifier
                        .size(96.dp)
                        .offset(y = crestLift),
                    primary = Eldoria.GoldBright,
                    secondary = Eldoria.IronDeep,
                    ornate = true
                )
            }

            Text(
                text = "ELDORIA",
                style = EldoriaType.displayXl.copy(
                    brush = Brush.verticalGradient(
                        listOf(Eldoria.GoldBright, Eldoria.Gold, Eldoria.GoldDeep)
                    ),
                    shadow = Shadow(
                        color = Eldoria.Gold.copy(alpha = 0.25f + 0.45f * glow),
                        offset = Offset(0f, 0f),
                        blurRadius = 28f
                    )
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(Modifier.height(Eldoria.S6))
            Text(
                text = "CRÓNICAS DEL RENACER",
                style = EldoriaType.label,
                color = Eldoria.TextGold.copy(alpha = 0.82f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(Modifier.height(Eldoria.S12))
            EldoriaDivider(color = Eldoria.Gold.copy(alpha = 0.75f))
            Spacer(Modifier.height(Eldoria.S20))

            // ───────────────── CONTINUAR / INVITACIÓN ─────────────────
            if (hero != null) {
                MenuContinueCard(
                    hero = hero,
                    expeditionActive = expedition.active,
                    expeditionDepth = expedition.depth,
                    expeditionName = expedition.dungeonName,
                    showDailyBadge = canClaimDaily,
                    onContinue = {
                        if (expedition.active) {
                            val resumed = viewModel.systems.resumeExpedition()
                            if (!resumed) viewModel.changeScreen(GameScreen.WORLD_MAP)
                        } else {
                            viewModel.changeScreen(GameScreen.WORLD_MAP)
                        }
                    }
                )
                Spacer(Modifier.height(Eldoria.S16))
            } else {
                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = EldoriaEdge.Iron,
                    filigree = true,
                    padding = PaddingValues(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    EldoriaEmptyState(
                        title = "No hay ningún héroe en pie",
                        message = "Las seis coronas siguen vacantes. Forja un campeón y reclama la primera de ellas.",
                        icon = Icons.Default.Person,
                        accent = Eldoria.Gold
                    )
                }
                Spacer(Modifier.height(Eldoria.S16))
                EldoriaButton(
                    text = "NUEVA PARTIDA",
                    onClick = { viewModel.startNewCharacterCreator() },
                    modifier = Modifier.fillMaxWidth(),
                    tone = EldoriaTone.Gold,
                    size = EldoriaButtonSize.Large,
                    icon = Icons.Default.PersonAdd,
                    fullWidth = true,
                    testTag = "menu_new_game_btn"
                )
                Spacer(Modifier.height(Eldoria.S16))
            }

            // ───────────────── BOTONES SECUNDARIOS ─────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
            ) {
                if (hero != null) {
                    EldoriaButton(
                        text = "NUEVA PARTIDA",
                        onClick = { confirmNewGame = true },
                        modifier = Modifier.weight(1f),
                        tone = EldoriaTone.Ember,
                        size = EldoriaButtonSize.Medium,
                        icon = Icons.Default.PersonAdd,
                        testTag = "menu_new_game_btn"
                    )
                }
                EldoriaButton(
                    text = "HÉROES",
                    onClick = { sheet = MenuSheetKind.HEROES },
                    modifier = Modifier.weight(1f),
                    tone = EldoriaTone.Silver,
                    size = EldoriaButtonSize.Medium,
                    icon = Icons.Default.People,
                    testTag = "menu_heroes_btn"
                )
                if (hero == null) {
                    EldoriaButton(
                        text = "AJUSTES",
                        onClick = { viewModel.changeScreen(GameScreen.SETTINGS) },
                        modifier = Modifier.weight(1f),
                        tone = EldoriaTone.Iron,
                        size = EldoriaButtonSize.Medium,
                        icon = Icons.Default.Settings,
                        testTag = "menu_settings_btn"
                    )
                }
            }
            Spacer(Modifier.height(Eldoria.S8))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
            ) {
                if (hero != null) {
                    EldoriaButton(
                        text = "AJUSTES",
                        onClick = { viewModel.changeScreen(GameScreen.SETTINGS) },
                        modifier = Modifier.weight(1f),
                        tone = EldoriaTone.Iron,
                        size = EldoriaButtonSize.Medium,
                        icon = Icons.Default.Settings,
                        testTag = "menu_settings_btn"
                    )
                }
                EldoriaButton(
                    text = "CRÓNICA",
                    onClick = { sheet = MenuSheetKind.LORE },
                    modifier = Modifier.weight(1f),
                    tone = EldoriaTone.Arcane,
                    size = EldoriaButtonSize.Medium,
                    icon = Icons.Default.MenuBook,
                    testTag = "menu_lore_btn"
                )
            }
            Spacer(Modifier.height(Eldoria.S8))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
            ) {
                EldoriaButton(
                    text = "COPIA DE SEGURIDAD",
                    onClick = { sheet = MenuSheetKind.BACKUP },
                    modifier = Modifier.weight(1.5f),
                    tone = EldoriaTone.Iron,
                    size = EldoriaButtonSize.Medium,
                    icon = Icons.Default.Backup,
                    testTag = "menu_backup_btn"
                )
                EldoriaButton(
                    text = "CRÉDITOS",
                    onClick = { sheet = MenuSheetKind.CREDITS },
                    modifier = Modifier.weight(1f),
                    tone = EldoriaTone.Iron,
                    size = EldoriaButtonSize.Medium,
                    icon = Icons.Default.Info
                )
            }

            Spacer(Modifier.height(Eldoria.S20))
            EldoriaDivider(color = Eldoria.IronEdge, ornament = true)
            Spacer(Modifier.height(Eldoria.S8))
            Text(
                text = "Renacer de Eldoria · Crónica II",
                style = EldoriaType.caption,
                color = Eldoria.TextLow,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(Modifier.height(Eldoria.S24))
        }
    }

    // ═════════════════════════ HOJA DE HÉROES ═════════════════════════
    EldoriaSheet(
        visible = sheet == MenuSheetKind.HEROES,
        title = "SALÓN DE HÉROES",
        onDismiss = { sheet = MenuSheetKind.NONE },
        edge = EldoriaEdge.Gold
    ) {
        if (characters.isEmpty()) {
            EldoriaEmptyState(
                title = "El salón está vacío",
                message = "Ningún nombre grabado todavía en los muros. Crea tu primer héroe para abrir la crónica.",
                icon = Icons.Default.People,
                accent = Eldoria.Gold,
                actionLabel = "NUEVA PARTIDA",
                onAction = {
                    sheet = MenuSheetKind.NONE
                    viewModel.startNewCharacterCreator()
                }
            )
        } else {
            Text(
                text = "Selecciona un héroe para retomar su historia. Mantén la casa en orden: sólo uno puede estar en pie a la vez.",
                style = EldoriaType.small,
                color = Eldoria.TextMid
            )
            Spacer(Modifier.height(Eldoria.S12))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Eldoria.S8)
            ) {
                characters.forEach { character ->
                    MenuHeroSlot(
                        character = character,
                        onSelect = {
                            sheet = MenuSheetKind.NONE
                            viewModel.selectCharacter(character.id)
                        },
                        onDelete = { heroPendingDeletion = character.id }
                    )
                }
            }
        }
    }

    // ═════════════════════════ HOJA DE CRÓNICA ═════════════════════════
    EldoriaSheet(
        visible = sheet == MenuSheetKind.LORE,
        title = "CRÓNICA DE LOS SEIS REINOS",
        onDismiss = { sheet = MenuSheetKind.NONE },
        edge = EldoriaEdge.Arcane
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 440.dp)
                .verticalScroll(rememberScrollState())
        ) {
            EldoriaScrollSheet(padding = PaddingValues(horizontal = 20.dp, vertical = 22.dp)) {
                MENU_LORE_REGIONS.forEachIndexed { index, region ->
                    MenuLoreBlock(region = region)
                    if (index != MENU_LORE_REGIONS.lastIndex) {
                        Spacer(Modifier.height(Eldoria.S12))
                        EldoriaDivider(color = Eldoria.ParchmentInk.copy(alpha = 0.45f))
                        Spacer(Modifier.height(Eldoria.S12))
                    }
                }
            }
        }
    }

    // ═════════════════════════ HOJA DE COPIA DE SEGURIDAD ═════════════════════════
    EldoriaSheet(
        visible = sheet == MenuSheetKind.BACKUP,
        title = "COPIA DE SEGURIDAD",
        onDismiss = { sheet = MenuSheetKind.NONE },
        edge = EldoriaEdge.Silver
    ) {
        LaunchedEffect(Unit) { viewModel.refreshBackupStatus() }

        EldoriaPanel(
            modifier = Modifier.fillMaxWidth(),
            edge = EldoriaEdge.Iron,
            padding = PaddingValues(12.dp)
        ) {
            Text(
                text = "ESTADO DEL ARCHIVO",
                style = EldoriaType.label,
                color = Eldoria.TextLow,
                maxLines = 1
            )
            Spacer(Modifier.height(Eldoria.S4))
            Text(
                text = if (backupStatus.isBlank()) "Todavía no hay ninguna copia registrada en este dispositivo." else backupStatus,
                style = EldoriaType.body,
                color = Eldoria.TextMid
            )
        }
        Spacer(Modifier.height(Eldoria.S12))
        Text(
            text = "Exportar guarda una instantánea de tu héroe en el almacenamiento seguro de la aplicación. Restaurar sobrescribe la partida activa con esa instantánea.",
            style = EldoriaType.small,
            color = Eldoria.TextLow
        )
        Spacer(Modifier.height(Eldoria.S12))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
        ) {
            EldoriaButton(
                text = "EXPORTAR",
                onClick = { viewModel.exportManualBackup() },
                modifier = Modifier.weight(1f),
                tone = EldoriaTone.Vitae,
                size = EldoriaButtonSize.Medium,
                icon = Icons.Default.Save,
                enabled = hero != null
            )
            EldoriaButton(
                text = "RESTAURAR",
                onClick = { confirmRestore = true },
                modifier = Modifier.weight(1f),
                tone = EldoriaTone.Blood,
                size = EldoriaButtonSize.Medium,
                icon = Icons.Default.Restore
            )
        }
        if (hero == null) {
            Spacer(Modifier.height(Eldoria.S6))
            Text(
                text = "Exportar requiere una partida activa.",
                style = EldoriaType.caption,
                color = Eldoria.Warning
            )
        }
    }

    // ═════════════════════════ HOJA DE CRÉDITOS ═════════════════════════
    EldoriaSheet(
        visible = sheet == MenuSheetKind.CREDITS,
        title = "CRÉDITOS",
        onDismiss = { sheet = MenuSheetKind.NONE },
        edge = EldoriaEdge.Iron
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 380.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EldoriaCrest(
                seed = 4242,
                modifier = Modifier.size(width = 64.dp, height = 72.dp),
                primary = Eldoria.GoldBright,
                secondary = Eldoria.IronDeep,
                ornate = true
            )
            Spacer(Modifier.height(Eldoria.S12))
            Text(
                text = "ELDORIA CHRONICLES",
                style = EldoriaType.title,
                color = Eldoria.TextGold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Eldoria.S4))
            Text(
                text = "Renacer de Eldoria · Crónica II",
                style = EldoriaType.caption,
                color = Eldoria.TextLow,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Eldoria.S16))
            EldoriaDivider(color = Eldoria.Gold.copy(alpha = 0.6f))
            Spacer(Modifier.height(Eldoria.S16))
            Text(
                text = "Diseño, sistemas y dirección de arte: un solo taller obstinado.\n" +
                    "Bestiario, expediciones, santuario y contratos escritos a mano, criatura a criatura.\n" +
                    "Todo el ornamento metálico, las brasas y los blasones de esta interfaz están dibujados en tiempo real: ni una sola imagen descargada.",
                style = EldoriaType.lore,
                color = Eldoria.TextMid,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Eldoria.S16))
            Text(
                text = "Gracias por jugar. Que las brasas te alumbren el descenso.",
                style = EldoriaType.bodyStrong,
                color = Eldoria.TextGold,
                textAlign = TextAlign.Center
            )
        }
    }

    // ═════════════════════════ CONFIRMACIONES ═════════════════════════
    if (confirmNewGame) {
        EldoriaConfirmDialog(
            title = "¿Empezar de cero?",
            message = "Tu héroe actual seguirá guardado en el Salón de Héroes y podrás retomarlo cuando quieras. " +
                "Sólo dejará de ser el héroe activo.",
            confirmLabel = "CREAR HÉROE",
            onConfirm = {
                confirmNewGame = false
                viewModel.startNewCharacterCreator()
            },
            onDismiss = { confirmNewGame = false },
            tone = EldoriaTone.Ember
        )
    }

    if (confirmRestore) {
        EldoriaConfirmDialog(
            title = "Restaurar copia",
            message = "Se sobrescribirá la partida activa con la última copia de seguridad guardada. Esta acción no se puede deshacer.",
            confirmLabel = "RESTAURAR",
            onConfirm = {
                confirmRestore = false
                sheet = MenuSheetKind.NONE
                viewModel.restoreManualBackup()
            },
            onDismiss = { confirmRestore = false },
            tone = EldoriaTone.Blood
        )
    }

    val pendingId = heroPendingDeletion
    if (pendingId != null) {
        val pendingName = remember(pendingId, characters) {
            characters.firstOrNull { it.id == pendingId }?.charName ?: "este héroe"
        }
        EldoriaConfirmDialog(
            title = "Borrar héroe",
            message = "«$pendingName» será borrado para siempre junto a su inventario, sus mascotas y su crónica. No hay resurrección posible.",
            confirmLabel = "BORRAR",
            onConfirm = {
                heroPendingDeletion = null
                viewModel.deleteCharacter(pendingId)
            },
            onDismiss = { heroPendingDeletion = null },
            tone = EldoriaTone.Blood
        )
    }
}

// ──────────────────────────── PIEZAS PRIVADAS ────────────────────────────

@Composable
private fun MenuContinueCard(
    hero: GameProgress,
    expeditionActive: Boolean,
    expeditionDepth: Int,
    expeditionName: String,
    showDailyBadge: Boolean,
    onContinue: () -> Unit
) {
    val kingdom = remember(hero.currentX, hero.currentY) {
        KingdomGenerator.getKingdomForCoords(hero.currentX, hero.currentY)
    }
    val className = remember(hero.hasAdvancedClass, hero.advancedClassName, hero.charClass) {
        if (hero.hasAdvancedClass && hero.advancedClassName.isNotBlank()) hero.advancedClassName
        else hero.charClass
    }
    val portrait = remember(hero.charRace, hero.charClass, hero.hasAdvancedClass, hero.charLevel) {
        getCharacterPortrait(hero.charRace, hero.charClass, hero.hasAdvancedClass, hero.charLevel)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        EldoriaPanel(
            modifier = Modifier.fillMaxWidth(),
            edge = EldoriaEdge.Gold,
            padding = PaddingValues(14.dp),
            glow = true,
            filigree = true
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EldoriaFrame(
                    modifier = Modifier.size(78.dp),
                    edge = EldoriaEdge.Gold,
                    corner = 10.dp,
                    filigree = false,
                    rivets = true,
                    glowPulse = showDailyBadge
                ) {
                    Image(
                        painter = painterResource(id = portrait),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.width(Eldoria.S12))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hero.charName.ifBlank { "Héroe sin nombre" },
                        style = EldoriaType.title,
                        color = Eldoria.TextHi,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(Eldoria.S4))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EldoriaChip(
                            text = "NIVEL ${hero.charLevel}",
                            color = Eldoria.Gold,
                            filled = true
                        )
                        Spacer(Modifier.width(Eldoria.S6))
                        Text(
                            text = className,
                            style = EldoriaType.small,
                            color = Eldoria.ArcaneBright,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(Eldoria.S6))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Eldoria.TextLow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(Eldoria.S4))
                        Text(
                            text = kingdom.name,
                            style = EldoriaType.small,
                            color = Eldoria.TextMid,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(Modifier.width(Eldoria.S8))
                EldoriaCounter(
                    value = hero.charGold.toLong(),
                    icon = Icons.Default.MonetizationOn,
                    accent = Eldoria.TextGold
                )
            }

            if (expeditionActive) {
                Spacer(Modifier.height(Eldoria.S12))
                MenuEmberBand(
                    text = "EXPEDICIÓN EN CURSO · PROFUNDIDAD $expeditionDepth",
                    detail = expeditionName.ifBlank { "Destino desconocido" }
                )
            }

            Spacer(Modifier.height(Eldoria.S12))
            EldoriaButton(
                text = if (expeditionActive) "REANUDAR EXPEDICIÓN" else "CONTINUAR",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                tone = if (expeditionActive) EldoriaTone.Ember else EldoriaTone.Gold,
                size = EldoriaButtonSize.Large,
                icon = Icons.Default.PlayArrow,
                fullWidth = true,
                testTag = "menu_continue_btn"
            )

            if (showDailyBadge) {
                Spacer(Modifier.height(Eldoria.S6))
                Text(
                    text = "Tienes una recompensa diaria esperándote en el reino.",
                    style = EldoriaType.caption,
                    color = Eldoria.BloodBright,
                    maxLines = 2
                )
            }
        }

        if (showDailyBadge) {
            MenuAlertBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
            )
        }
    }
}

@Composable
private fun MenuAlertBadge(modifier: Modifier = Modifier) {
    val pulse = eldoriaPulse(periodMs = 1100, from = 0.45f, to = 1f, label = "menuBadge")
    Canvas(modifier = modifier.size(22.dp)) {
        val r = size.minDimension / 2f
        if (r <= 0f) return@Canvas
        drawCircle(
            color = Eldoria.BloodBright.copy(alpha = 0.30f * pulse),
            radius = r,
            center = center
        )
        drawCircle(
            color = Eldoria.Blood,
            radius = r * 0.58f,
            center = center
        )
        drawCircle(
            color = Eldoria.BloodBright.copy(alpha = 0.55f + 0.45f * pulse),
            radius = r * 0.28f,
            center = center
        )
    }
}

@Composable
private fun MenuEmberBand(text: String, detail: String) {
    val flicker = eldoriaPulse(periodMs = 900, from = 0.55f, to = 1f, label = "menuEmberBand")
    val shape = CutCornerShape(6.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        Eldoria.EmberDeep.copy(alpha = 0.55f),
                        Eldoria.EmberShadow.copy(alpha = 0.40f),
                        Eldoria.EmberDeep.copy(alpha = 0.20f)
                    )
                ),
                shape = shape
            )
            .border(Eldoria.StrokeThin, Eldoria.emberEdge(), shape)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = Eldoria.EmberCore,
            modifier = Modifier
                .size(17.dp)
                .alpha(flicker)
        )
        Spacer(Modifier.width(Eldoria.S8))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = text,
                style = EldoriaType.label,
                color = Eldoria.EmberCore,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = detail,
                style = EldoriaType.caption,
                color = Eldoria.Ember.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MenuHeroSlot(
    character: GameProgress,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val portrait = remember(
        character.charRace,
        character.charClass,
        character.hasAdvancedClass,
        character.charLevel
    ) {
        getCharacterPortrait(
            character.charRace,
            character.charClass,
            character.hasAdvancedClass,
            character.charLevel
        )
    }
    val className = if (character.hasAdvancedClass && character.advancedClassName.isNotBlank()) {
        character.advancedClassName
    } else {
        character.charClass
    }

    EldoriaPanel(
        modifier = Modifier.fillMaxWidth(),
        edge = if (character.isActiveChar) EldoriaEdge.Gold else EldoriaEdge.Iron,
        padding = PaddingValues(10.dp),
        onClick = onSelect,
        testTag = "menu_hero_slot_${character.id}"
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EldoriaSlotFrame(
                level = character.charLevel,
                size = 58.dp
            ) {
                Image(
                    painter = painterResource(id = portrait),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.width(Eldoria.S12))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = character.charName.ifBlank { "Héroe sin nombre" },
                        style = EldoriaType.subheading,
                        color = Eldoria.TextHi,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (character.isActiveChar) {
                        Spacer(Modifier.width(Eldoria.S6))
                        EldoriaChip(text = "ACTIVO", color = Eldoria.Vitae, filled = true)
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$className · ${character.charRace}",
                    style = EldoriaType.caption,
                    color = Eldoria.TextMid,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = Eldoria.TextGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(Eldoria.S4))
                    Text(
                        text = "${character.charGold}",
                        style = EldoriaType.caption,
                        color = Eldoria.TextGold,
                        maxLines = 1
                    )
                }
            }
            Spacer(Modifier.width(Eldoria.S8))
            EldoriaIconButton(
                icon = Icons.Default.Delete,
                contentDescription = "Borrar héroe",
                onClick = onDelete,
                tone = EldoriaTone.Blood,
                size = 38.dp
            )
        }
    }
}

@Composable
private fun MenuLoreBlock(region: MenuLoreRegion) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = region.name.uppercase(),
            style = EldoriaType.heading,
            color = Eldoria.ParchmentInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = region.subtitle,
            style = EldoriaType.caption,
            color = Eldoria.ParchmentInk.copy(alpha = 0.68f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(Eldoria.S6))
        Text(
            text = region.text,
            style = EldoriaType.lore,
            color = Eldoria.ParchmentInk.copy(alpha = 0.90f)
        )
    }
}
