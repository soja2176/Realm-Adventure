package com.example.ui.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.GameProgress
import com.example.data.GameScreen
import com.example.data.formatGameNumber
import com.example.data.getRequiredExpForLevel
import com.example.data.model.EldoriaToastMessage
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBarTone
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaCrest
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaCounter
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaResourceBar
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaSheet
import com.example.ui.design.EldoriaToastCard
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaFloat
import com.example.ui.design.eldoriaGlowLayer
import com.example.ui.design.eldoriaPressable
import com.example.ui.design.eldoriaPulse
import com.example.ui.getCharacterPortrait
import kotlinx.coroutines.delay

// ══════════════════════════════════════════════════════════════════════════════
//  CROMO COMPARTIDO DE ELDORIA
//  Barra superior de héroe, navegación inferior de 5 destinos, hoja "Más",
//  banda de avisos efímeros y modal de notificación.
//  Todo el metal es gradiente + bisel; nada de componentes Material planos.
// ══════════════════════════════════════════════════════════════════════════════

private const val SHELL_TOAST_MS = 2600L

// ─────────────────────────────── TOP HUD ──────────────────────────────────────

/**
 * Cabecera persistente: retrato del héroe con anillo dorado, identidad, barras de
 * Vida / Maná / Experiencia, oro y los tres accesos de cromo (Menú, Ajustes, Ayuda).
 */
@Composable
fun EldoriaTopHud(
    progress: GameProgress,
    onHelp: () -> Unit,
    onMenu: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heroName = if (progress.charName.isBlank()) "Aventurero" else progress.charName
    val className = if (progress.hasAdvancedClass && progress.advancedClassName.isNotBlank()) {
        progress.advancedClassName
    } else if (progress.charClass.isBlank()) {
        "Sin clase"
    } else {
        progress.charClass
    }
    val portraitRes = remember(
        progress.charRace,
        progress.charClass,
        progress.hasAdvancedClass,
        progress.charLevel
    ) {
        getCharacterPortrait(
            progress.charRace,
            progress.charClass,
            progress.hasAdvancedClass,
            progress.charLevel
        )
    }
    val expRequired = remember(progress.charLevel) { getRequiredExpForLevel(progress.charLevel) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Eldoria.Ink, Eldoria.Slate, Eldoria.PanelSunken, Eldoria.Abyss)
                )
            )
            .statusBarsPadding()
            .drawBehind {
                // filo dorado inferior: separa el cromo del contenido de la pantalla
                val y = size.height - 1f
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, Eldoria.Gold.copy(alpha = 0.85f), Color.Transparent)
                    ),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2.5f
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 68.dp)
                .padding(horizontal = 10.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EldoriaShellHeroPortrait(portraitRes = portraitRes, level = progress.charLevel)

            Spacer(Modifier.width(Eldoria.S8))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = heroName,
                        style = EldoriaType.subheading,
                        color = Eldoria.TextHi,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(Eldoria.S6))
                    Text(
                        text = "Nv.${progress.charLevel} · $className",
                        style = EldoriaType.caption,
                        color = Eldoria.TextGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(3.dp))

                EldoriaShellBarRow(
                    current = progress.currentHp,
                    max = progress.maxHp,
                    tone = EldoriaBarTone.Health,
                    barHeightDp = 12,
                    numeralColor = Eldoria.BloodBright,
                    dangerPulse = true
                )

                Spacer(Modifier.height(2.dp))

                EldoriaShellBarRow(
                    current = progress.currentMp,
                    max = progress.maxMp,
                    tone = EldoriaBarTone.Mana,
                    barHeightDp = 10,
                    numeralColor = Eldoria.ManaBright,
                    dangerPulse = false
                )

                Spacer(Modifier.height(2.dp))

                EldoriaResourceBar(
                    current = progress.charExp,
                    max = expRequired,
                    tone = EldoriaBarTone.Experience,
                    modifier = Modifier.fillMaxWidth(),
                    height = 6.dp,
                    showNumbers = false
                )
            }

            Spacer(Modifier.width(Eldoria.S8))

            Column(horizontalAlignment = Alignment.End) {
                EldoriaCounter(
                    value = progress.charGold.toLong(),
                    icon = Icons.Filled.MonetizationOn,
                    accent = Eldoria.TextGold
                )
                Spacer(Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S4)) {
                    EldoriaIconButton(
                        icon = Icons.Filled.Menu,
                        contentDescription = "Menú principal",
                        onClick = onMenu,
                        tone = EldoriaTone.Iron,
                        size = 32.dp,
                        testTag = "hud_menu_btn"
                    )
                    EldoriaIconButton(
                        icon = Icons.Filled.Settings,
                        contentDescription = "Ajustes",
                        onClick = onSettings,
                        tone = EldoriaTone.Iron,
                        size = 32.dp,
                        testTag = "hud_settings_btn"
                    )
                    EldoriaIconButton(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Ayuda",
                        onClick = onHelp,
                        tone = EldoriaTone.Gold,
                        size = 32.dp,
                        testTag = "hud_help_btn"
                    )
                }
            }
        }
    }
}

/** Retrato circular de 46dp con anillo dorado, halo latente y chapa de nivel. */
@Composable
private fun EldoriaShellHeroPortrait(portraitRes: Int, level: Int) {
    val pulse = eldoriaPulse(periodMs = 2600, from = 0.35f, to = 0.85f, label = "hudPortrait")
    Box(
        modifier = Modifier.size(46.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    0f to Eldoria.Gold.copy(alpha = 0.28f * pulse),
                    1f to Color.Transparent,
                    center = center,
                    radius = r
                ),
                radius = r,
                center = center
            )
        }
        Image(
            painter = painterResource(id = portraitRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f
            // sombra del filo: da grosor al metal
            drawCircle(
                color = Color.Black.copy(alpha = 0.55f),
                radius = r * 0.87f,
                center = center,
                style = Stroke(width = 3.2.dp.toPx())
            )
            // anillo dorado con gradiente vertical real
            drawCircle(
                brush = Brush.verticalGradient(
                    listOf(Eldoria.GoldBright, Eldoria.Gold, Eldoria.GoldDeep)
                ),
                radius = r * 0.87f,
                center = center,
                style = Stroke(width = 1.8.dp.toPx())
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(4.dp))
                .background(Eldoria.Abyss.copy(alpha = 0.92f))
                .border(0.75.dp, Eldoria.Gold.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = "$level",
                style = EldoriaType.caption,
                color = Eldoria.GoldBright,
                maxLines = 1
            )
        }
    }
}

/** Barra compacta con el numeral a la derecha (evita la cabecera alta de la barra estándar). */
@Composable
private fun EldoriaShellBarRow(
    current: Int,
    max: Int,
    tone: EldoriaBarTone,
    barHeightDp: Int,
    numeralColor: Color,
    dangerPulse: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        EldoriaResourceBar(
            current = current,
            max = max,
            tone = tone,
            modifier = Modifier.weight(1f),
            height = barHeightDp.dp,
            showNumbers = false,
            dangerPulse = dangerPulse
        )
        Spacer(Modifier.width(Eldoria.S6))
        Text(
            text = "${formatGameNumber(current)}/${formatGameNumber(max)}",
            style = EldoriaType.caption,
            color = numeralColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ────────────────────────────── BOTTOM NAV ────────────────────────────────────

private class EldoriaShellNavItem(
    val screen: GameScreen?,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

private val ELDORIA_SHELL_NAV_ITEMS: List<EldoriaShellNavItem> = listOf(
    EldoriaShellNavItem(GameScreen.WORLD_MAP, "Mapa", Icons.Filled.Map, "nav_tab_mapa"),
    EldoriaShellNavItem(GameScreen.DUNGEON, "Calabozos", Icons.Filled.Castle, "nav_tab_calabozos"),
    EldoriaShellNavItem(GameScreen.CHARACTER_SCREEN, "Héroe", Icons.Filled.Person, "nav_tab_heroe"),
    EldoriaShellNavItem(GameScreen.PET_SANCTUARY, "Santuario", Icons.Filled.Pets, "nav_tab_santuario"),
    EldoriaShellNavItem(null, "Más", Icons.Filled.Apps, "nav_tab_mas")
)

/**
 * Cinco destinos exactos repartidos con weight(1f). El activo se marca con acento
 * dorado, indicador superior y resplandor tenue; los demás quedan en TextLow.
 */
@Composable
fun EldoriaBottomNav(
    current: GameScreen,
    onSelect: (GameScreen) -> Unit,
    onMore: () -> Unit,
    badges: kotlin.collections.Map<GameScreen, Int> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val moreActive = ELDORIA_SHELL_MORE_ENTRIES.any { it.screen == current }
    val moreBadge = ELDORIA_SHELL_MORE_ENTRIES.sumOf { badges[it.screen] ?: 0 }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Eldoria.Abyss, Eldoria.PanelSunken, Eldoria.Ink)
                )
            )
            .drawBehind {
                // filo metálico superior
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, Eldoria.Gold.copy(alpha = 0.55f), Color.Transparent)
                    ),
                    start = Offset(0f, 1f),
                    end = Offset(size.width, 1f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.Black.copy(alpha = 0.6f),
                    start = Offset(0f, 3f),
                    end = Offset(size.width, 3f),
                    strokeWidth = 2f
                )
            }
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ELDORIA_SHELL_NAV_ITEMS.forEach { item ->
                val screen = item.screen
                if (screen == null) {
                    EldoriaShellNavCell(
                        modifier = Modifier.weight(1f),
                        label = item.label,
                        icon = item.icon,
                        active = moreActive,
                        badge = moreBadge,
                        testTag = item.testTag,
                        onClick = onMore
                    )
                } else {
                    EldoriaShellNavCell(
                        modifier = Modifier.weight(1f),
                        label = item.label,
                        icon = item.icon,
                        active = current == screen,
                        badge = badges[screen] ?: 0,
                        testTag = item.testTag,
                        onClick = { onSelect(screen) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EldoriaShellNavCell(
    label: String,
    icon: ImageVector,
    active: Boolean,
    badge: Int,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pulse = if (active) {
        eldoriaPulse(periodMs = 2400, from = 0.55f, to = 1f, label = "navGlow")
    } else {
        0f
    }
    val tint = if (active) Eldoria.GoldBright else Eldoria.TextLow
    val labelColor = if (active) Eldoria.TextGold else Eldoria.TextLow

    Box(
        modifier = modifier
            .fillMaxHeight()
            .eldoriaPressable(onClick = onClick)
            .testTag(testTag)
            .drawBehind {
                if (!active) return@drawBehind
                // halo ascendente del destino activo
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Eldoria.Gold.copy(alpha = 0.16f * pulse),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.9f
                    )
                )
                // indicador superior
                val inset = size.width * 0.24f
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, Eldoria.GoldBright, Color.Transparent),
                        startX = inset,
                        endX = size.width - inset
                    ),
                    start = Offset(inset, 2f),
                    end = Offset(size.width - inset, 2f),
                    strokeWidth = 3.5f,
                    cap = StrokeCap.Round
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = if (active) {
                        Modifier
                            .size(26.dp)
                            .eldoriaGlowLayer(
                                Eldoria.Gold.copy(alpha = 0.55f),
                                alpha = 0.20f * pulse,
                                corner = 13.dp,
                                spread = 5.dp
                            )
                    } else {
                        Modifier.size(26.dp)
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                if (badge > 0) {
                    EldoriaShellBadge(
                        count = badge,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 9.dp, y = (-4).dp)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = EldoriaType.caption,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Punto rojo con numeral. Nunca baja de 11.sp. */
@Composable
private fun EldoriaShellBadge(count: Int, modifier: Modifier = Modifier) {
    if (count <= 0) return
    val text = if (count > 99) "99+" else "$count"
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(
                Brush.verticalGradient(listOf(Eldoria.BloodBright, Eldoria.Blood, Eldoria.BloodDeep))
            )
            .border(1.dp, Eldoria.Abyss.copy(alpha = 0.85f), RoundedCornerShape(9.dp))
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = EldoriaType.caption,
            color = Eldoria.TextHi,
            maxLines = 1
        )
    }
}

// ───────────────────────────── HOJA "MÁS" ─────────────────────────────────────

private class EldoriaShellMoreEntry(
    val screen: GameScreen,
    val label: String,
    val icon: ImageVector,
    val slug: String
)

private val ELDORIA_SHELL_MORE_ENTRIES: List<EldoriaShellMoreEntry> = listOf(
    EldoriaShellMoreEntry(GameScreen.INVENTORY, "Inventario", Icons.Filled.Backpack, "inventario"),
    EldoriaShellMoreEntry(GameScreen.SHOP, "Tienda", Icons.Filled.Storefront, "tienda"),
    EldoriaShellMoreEntry(GameScreen.CRAFTING, "Forja", Icons.Filled.Hardware, "forja"),
    EldoriaShellMoreEntry(GameScreen.MINIGAMES, "Minijuegos", Icons.Filled.Casino, "minijuegos"),
    EldoriaShellMoreEntry(GameScreen.BESTIARY, "Bestiario", Icons.AutoMirrored.Filled.MenuBook, "bestiario"),
    EldoriaShellMoreEntry(GameScreen.CONTRACTS, "Contratos", Icons.AutoMirrored.Filled.Assignment, "contratos"),
    EldoriaShellMoreEntry(GameScreen.TALENTS, "Talentos", Icons.Filled.AutoAwesome, "talentos"),
    EldoriaShellMoreEntry(GameScreen.ACHIEVEMENTS, "Logros", Icons.Filled.EmojiEvents, "logros"),
    EldoriaShellMoreEntry(GameScreen.DAILY_REWARDS, "Recompensas diarias", Icons.Filled.CardGiftcard, "recompensas"),
    EldoriaShellMoreEntry(GameScreen.HELP_SCREEN, "Ayuda", Icons.AutoMirrored.Filled.HelpOutline, "ayuda"),
    EldoriaShellMoreEntry(GameScreen.SETTINGS, "Ajustes", Icons.Filled.Settings, "ajustes"),
    EldoriaShellMoreEntry(GameScreen.MAIN_MENU, "Menú principal", Icons.AutoMirrored.Filled.Logout, "menu")
)

/** Rejilla de 3 columnas con las 12 entradas secundarias, agrupadas en dos secciones. */
@Composable
fun EldoriaMoreSheet(
    visible: Boolean,
    current: GameScreen,
    onSelect: (GameScreen) -> Unit,
    onDismiss: () -> Unit,
    badges: kotlin.collections.Map<GameScreen, Int> = emptyMap()
) {
    EldoriaSheet(
        visible = visible,
        title = "MÁS OPCIONES",
        onDismiss = onDismiss,
        edge = EldoriaEdge.Gold
    ) {
        val scroll = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 460.dp)
                .verticalScroll(scroll)
        ) {
            EldoriaSectionTitle(text = "AVENTURA", icon = Icons.Filled.Backpack)
            Spacer(Modifier.height(Eldoria.S8))
            EldoriaShellMoreGrid(
                entries = ELDORIA_SHELL_MORE_ENTRIES.subList(0, 6),
                current = current,
                badges = badges,
                onSelect = onSelect
            )

            Spacer(Modifier.height(Eldoria.S12))
            EldoriaDivider(color = Eldoria.GoldDeep)
            Spacer(Modifier.height(Eldoria.S12))

            EldoriaSectionTitle(text = "PROGRESO Y SISTEMA", icon = Icons.Filled.AutoAwesome)
            Spacer(Modifier.height(Eldoria.S8))
            EldoriaShellMoreGrid(
                entries = ELDORIA_SHELL_MORE_ENTRIES.subList(6, 12),
                current = current,
                badges = badges,
                onSelect = onSelect
            )
        }
    }
}

@Composable
private fun EldoriaShellMoreGrid(
    entries: List<EldoriaShellMoreEntry>,
    current: GameScreen,
    badges: kotlin.collections.Map<GameScreen, Int>,
    onSelect: (GameScreen) -> Unit
) {
    entries.chunked(3).forEachIndexed { rowIndex, row ->
        if (rowIndex > 0) Spacer(Modifier.height(Eldoria.S12))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
        ) {
            row.forEach { entry ->
                EldoriaShellMoreCell(
                    modifier = Modifier.weight(1f),
                    entry = entry,
                    active = current == entry.screen,
                    badge = badges[entry.screen] ?: 0,
                    onClick = { onSelect(entry.screen) }
                )
            }
            // rellena la fila incompleta para que la rejilla no se descuadre
            repeat(3 - row.size) {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun EldoriaShellMoreCell(
    entry: EldoriaShellMoreEntry,
    active: Boolean,
    badge: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val edge = if (active) EldoriaEdge.Gold else EldoriaEdge.Iron
    val tint = if (active) Eldoria.GoldBright else Eldoria.TextMid
    val shape = CutCornerShape(9.dp)

    Column(
        modifier = modifier
            .eldoriaPressable(onClick = onClick)
            .testTag("more_tab_${entry.slug}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .then(
                        if (active) {
                            Modifier.eldoriaGlowLayer(
                                Eldoria.Gold.copy(alpha = 0.5f),
                                alpha = 0.22f,
                                corner = 9.dp,
                                spread = 6.dp
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clip(shape)
                    .background(
                        Brush.verticalGradient(
                            if (active) {
                                listOf(
                                    Eldoria.Gold.copy(alpha = 0.26f),
                                    Eldoria.PanelSunken,
                                    Eldoria.Abyss
                                )
                            } else {
                                listOf(Eldoria.PanelHi, Eldoria.PanelSunken, Eldoria.Abyss)
                            }
                        )
                    )
                    .border(
                        if (active) Eldoria.StrokeMed else Eldoria.StrokeThin,
                        edge.brush(),
                        shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = entry.icon,
                    contentDescription = entry.label,
                    tint = tint,
                    modifier = Modifier.size(26.dp)
                )
            }
            if (badge > 0) {
                EldoriaShellBadge(
                    count = badge,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-6).dp)
                )
            }
        }
        Spacer(Modifier.height(Eldoria.S6))
        Text(
            text = entry.label,
            style = EldoriaType.caption,
            color = if (active) Eldoria.TextGold else Eldoria.TextMid,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────── TOAST HOST ───────────────────────────────────

private fun eldoriaShellToneOf(tone: String): EldoriaTone = when (tone.uppercase()) {
    "EMBER" -> EldoriaTone.Ember
    "BLOOD" -> EldoriaTone.Blood
    "VITAE" -> EldoriaTone.Vitae
    "ARCANE" -> EldoriaTone.Arcane
    "IRON" -> EldoriaTone.Iron
    "SILVER" -> EldoriaTone.Silver
    else -> EldoriaTone.Gold
}

private fun eldoriaShellToastIcon(tone: String): ImageVector = when (tone.uppercase()) {
    "EMBER" -> Icons.Filled.LocalFireDepartment
    "BLOOD" -> Icons.Filled.Favorite
    "VITAE" -> Icons.Filled.Eco
    "ARCANE" -> Icons.Filled.AutoAwesome
    "IRON" -> Icons.Filled.Shield
    "SILVER" -> Icons.Filled.Info
    else -> Icons.Filled.MonetizationOn
}

/** Banda superior efímera. Entra deslizando desde arriba y se retira sola a los 2600 ms. */
@Composable
fun EldoriaToastHost(
    toast: EldoriaToastMessage?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Se conserva el último mensaje para que la animación de salida tenga contenido.
    var shown by remember { mutableStateOf<EldoriaToastMessage?>(null) }

    LaunchedEffect(toast?.stamp) {
        val active = toast
        if (active != null && active.message.isNotBlank()) {
            shown = active
            delay(SHELL_TOAST_MS)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = toast != null && toast.message.isNotBlank(),
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 320)
        ) + fadeIn(animationSpec = tween(durationMillis = 320)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 220)
        ) + fadeOut(animationSpec = tween(durationMillis = 220))
    ) {
        val payload = shown
        if (payload != null) {
            EldoriaToastCard(
                message = payload.message,
                tone = eldoriaShellToneOf(payload.tone),
                icon = eldoriaShellToastIcon(payload.tone),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .eldoriaPressable(onClick = onDismiss, sound = false)
            )
        }
    }
}

// ───────────────────────────── NOTICE DIALOG ──────────────────────────────────

/**
 * Modal de aviso del juego. Sustituye visualmente al antiguo AlertDialog de Material.
 * El testTag del botón (`dismiss_notification_button`) es contrato con los tests.
 */
@Composable
fun EldoriaNoticeDialog(message: String?, onDismiss: () -> Unit) {
    if (message.isNullOrBlank()) return

    val lift = eldoriaFloat(periodMs = 3600, amplitude = 5.dp, label = "noticeCrest")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Eldoria.Scrim)
                .pointerInput(Unit) { detectTapGestures { onDismiss() } },
            contentAlignment = Alignment.Center
        ) {
            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .widthIn(max = 420.dp)
                    .pointerInput(Unit) { detectTapGestures { } },
                edge = EldoriaEdge.Gold,
                corner = Eldoria.R12,
                glow = true,
                filigree = true
            ) {
                EldoriaCrest(
                    seed = 1337,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .offset(y = lift)
                        .size(width = 52.dp, height = 60.dp),
                    primary = Eldoria.Gold,
                    secondary = Eldoria.IronDeep,
                    ornate = true
                )
                Spacer(Modifier.height(Eldoria.S8))
                Text(
                    text = "AVISO DEL REINO",
                    style = EldoriaType.label,
                    color = Eldoria.TextGold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Eldoria.S6))
                EldoriaDivider(color = Eldoria.Gold)
                Spacer(Modifier.height(Eldoria.S8))

                val scroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(scroll)
                ) {
                    Text(
                        text = message,
                        style = EldoriaType.body,
                        color = Eldoria.TextHi,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(Eldoria.S20))
                EldoriaButton(
                    text = "Continuar",
                    onClick = onDismiss,
                    tone = EldoriaTone.Gold,
                    size = EldoriaButtonSize.Medium,
                    fullWidth = true,
                    testTag = "dismiss_notification_button"
                )
            }
        }
    }
}
