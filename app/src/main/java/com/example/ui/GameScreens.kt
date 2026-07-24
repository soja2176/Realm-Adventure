package com.example.ui

import com.example.audio.SoundManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import kotlin.math.abs

// Style Palette Constants
val MedievalDarkBg = Color(0xFF0F111A)
val MedievalCardBg = Color(0xFF161A26)
val MedievalGold = Color(0xFFFFC107)
val MedievalGoldDark = Color(0xFFC79100)
val MedievalCrimson = Color(0xFFE53935)
val MedievalManaBlue = Color(0xFF1E88E5)
val MedievalXpGreen = Color(0xFF4CAF50)

val CommonColor = Color(0xFF90A4AE)
val RareColor = Color(0xFF1E88E5)
val EpicColor = Color(0xFF8E24AA)
val LegendaryColor = Color(0xFFFF8F00)

fun getRarityColor(rarity: String): Color {
    return when (rarity.uppercase()) {
        "UNIVERSAL" -> Color(0xFF00E5FF) // Celeste Cósmico
        "ARCANO" -> Color(0xFFD500F9) // Púrpura Neón Místico
        "LEGENDARIO", "LEGENDARY" -> LegendaryColor
        "ÉPICO", "EPIC" -> EpicColor
        "RARO", "RARE" -> RareColor
        else -> CommonColor
    }
}

fun getItemImageRes(imageResName: String, itemType: String): Int {
    return when (imageResName) {
        "img_item_sword_1784593548868" -> R.drawable.img_item_sword_1784593548868
        "img_item_staff_1784593558118" -> R.drawable.img_item_staff_1784593558118
        "img_item_dagger_1784593567531" -> R.drawable.img_item_dagger_1784593567531
        "img_item_plate_1784593577913" -> R.drawable.img_item_plate_1784593577913
        "img_item_robe_1784593587883" -> R.drawable.img_item_robe_1784593587883
        "img_item_ring_1784593597914" -> R.drawable.img_item_ring_1784593597914
        "img_item_shield_1784593608106" -> R.drawable.img_item_shield_1784593608106
        "img_item_potion_1784593618142" -> R.drawable.img_item_potion_1784593618142
        "img_item_helmet_1784658214656" -> R.drawable.img_item_helmet_1784658214656
        "img_item_wings_1784658202673" -> R.drawable.img_item_wings_1784658202673
        "img_item_gloves_1784658226142" -> R.drawable.img_item_gloves_1784658226142
        "img_item_boots_1784658239207" -> R.drawable.img_item_boots_1784658239207
        "img_item_earring_1784658263366" -> R.drawable.img_item_earring_1784658263366
        "img_item_relic_1784658251007" -> R.drawable.img_item_relic_1784658251007
        else -> {
            when (itemType.uppercase()) {
                "HELMET" -> R.drawable.img_item_helmet_1784658214656
                "WINGS" -> R.drawable.img_item_wings_1784658202673
                "WEAPON" -> R.drawable.img_item_sword_1784593548868
                "ARMOR" -> R.drawable.img_item_plate_1784593577913
                "GLOVES" -> R.drawable.img_item_gloves_1784658226142
                "BOOTS" -> R.drawable.img_item_boots_1784658239207
                "RING" -> R.drawable.img_item_ring_1784593597914
                "EARRING" -> R.drawable.img_item_earring_1784658263366
                "RELIC" -> R.drawable.img_item_relic_1784658251007
                "SHIELD" -> R.drawable.img_item_shield_1784593608106
                else -> R.drawable.img_item_potion_1784593618142
            }
        }
    }
}

fun getCharacterPortrait(race: String, cls: String): Int {
    val r = race.trim()
    val c = cls.trim()
    return when (r) {
        "Humano" -> when (c) {
            "Guerrero" -> R.drawable.img_portrait_humano_guerrero_1784507309143
            "Mago" -> R.drawable.img_portrait_humano_mago_1784507318980
            "Pícaro" -> R.drawable.img_portrait_humano_picaro_1784507327963
            else -> R.drawable.img_portrait_humano_clerigo_1784507343785
        }
        "Elfo" -> when (c) {
            "Guerrero" -> R.drawable.img_portrait_elfo_guerrero_1784507353139
            "Mago" -> R.drawable.img_portrait_elfo_mago_1784507362479
            "Pícaro" -> R.drawable.img_portrait_elfo_picaro_1784507372605
            else -> R.drawable.img_portrait_elfo_clerigo_1784507380857
        }
        "Enano" -> when (c) {
            "Guerrero" -> R.drawable.img_portrait_enano_guerrero_1784507393580
            "Mago" -> R.drawable.img_portrait_enano_mago_1784507404164
            "Pícaro" -> R.drawable.img_portrait_enano_picaro_1784507414525
            else -> R.drawable.img_portrait_enano_clerigo_1784507424242
        }
        else -> when (c) { // Orco
            "Guerrero" -> R.drawable.img_portrait_orco_guerrero_1784507433308
            "Mago" -> R.drawable.img_portrait_orco_mago_1784507441780
            "Pícaro" -> R.drawable.img_portrait_orco_picaro_1784507451567
            else -> R.drawable.img_portrait_orco_clerigo_1784507461591
        }
    }
}

fun getClassPortrait(cls: String): Int {
    return getCharacterPortrait("Humano", cls)
}

@Composable
fun EldoriaMainContainer(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val screen by viewModel.screenState.collectAsState()
    val notification by viewModel.notification.collectAsState()

    val p = progress

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MedievalDarkBg),
        topBar = {
            if (p != null && p.hasActiveChar && screen != GameScreen.CREATING_CHARACTER) {
                val autoNavActive by viewModel.isAutoNavigation.collectAsState()
                Column {
                    GameTopHeader(progress = p, onHelpClick = { viewModel.changeScreen(GameScreen.HELP_SCREEN) })
                    if (autoNavActive) {
                        Surface(
                            color = MedievalCrimson,
                            contentColor = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsWalk,
                                        contentDescription = "Autonavegación Activa",
                                        modifier = Modifier.size(16.dp),
                                        tint = MedievalGold
                                    )
                                    Text(
                                        text = "Navegación Automática Activa...",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Button(
                                    onClick = { viewModel.toggleAutoNavigation() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MedievalGold,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier
                                        .height(24.dp)
                                        .testTag("btn_stop_auto_nav")
                                ) {
                                    Text(
                                        text = "DETENER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (p != null && p.hasActiveChar && screen != GameScreen.CREATING_CHARACTER && screen != GameScreen.COMBAT) {
                GameBottomNav(currentScreen = screen, onTabSelect = { viewModel.changeScreen(it) })
            }
        },
        containerColor = MedievalDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen router
            when (screen) {
                GameScreen.CREATING_CHARACTER -> CharacterCreatorScreen(viewModel)
                GameScreen.WORLD_MAP -> WorldMapScreen(viewModel)
                GameScreen.DUNGEON -> DungeonScreen(viewModel)
                GameScreen.COMBAT -> CombatScreen(viewModel)
                GameScreen.CHARACTER_SCREEN -> CharacterScreen(viewModel)
                GameScreen.TALENTS -> TalentsScreen(viewModel)
                GameScreen.INVENTORY -> InventoryScreen(viewModel)
                GameScreen.SHOP -> ShopScreen(viewModel)
                GameScreen.HELP_SCREEN -> HelpGuideScreen(onBack = { viewModel.changeScreen(GameScreen.WORLD_MAP) })
            }

            // Centralized Notification Banner overlay
            notification?.let { message ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = true, onClick = { viewModel.dismissNotification() })
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
                        border = BorderStroke(2.dp, MedievalGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = true, onClick = { viewModel.dismissNotification() })
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Aviso",
                                tint = MedievalGold,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = message,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.dismissNotification() },
                                colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("dismiss_notification_button")
                            ) {
                                Text("Aceptar", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameTopHeader(progress: GameProgress, onHelpClick: () -> Unit) {
    Surface(
        color = MedievalCardBg,
        border = BorderStroke(width = 1.dp, color = MedievalGold.copy(alpha = 0.5f)),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Image(
                    painter = painterResource(id = getCharacterPortrait(progress.charRace, progress.charClass)),
                    contentDescription = "Portrait",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MedievalGold, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = progress.charName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Lvl ${progress.charLevel} ${progress.charRace} ${progress.charClass}",
                        color = MedievalGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Stats Quick Bars
            Column(
                modifier = Modifier.width(110.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // HP Bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("HP", color = MedievalCrimson, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(18.dp), maxLines = 1)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(7.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.Black)
                    ) {
                        val hpPercent = if (progress.maxHp > 0) progress.currentHp.toFloat() / progress.maxHp else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(hpPercent)
                                .background(MedievalCrimson)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${progress.currentHp}/${progress.maxHp}", color = Color.White, fontSize = 8.sp, maxLines = 1)
                }

                // MP Bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("MP", color = MedievalManaBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(18.dp), maxLines = 1)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(7.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.Black)
                    ) {
                        val mpPercent = if (progress.maxMp > 0) progress.currentMp.toFloat() / progress.maxMp else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(mpPercent)
                                .background(MedievalManaBlue)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${progress.currentMp}/${progress.maxMp}", color = Color.White, fontSize = 8.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Gold counter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Oro",
                    tint = MedievalGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${progress.charGold}",
                    color = MedievalGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                IconButton(
                    onClick = onHelpClick,
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Help,
                        contentDescription = "Ayuda",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GameBottomNav(currentScreen: GameScreen, onTabSelect: (GameScreen) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF08090C))
            .border(
                BorderStroke(
                    2.dp,
                    Brush.verticalGradient(listOf(Color(0xFF8A98A8), Color(0xFF3A424C), Color(0xFF1E2228)))
                )
            )
    ) {
        // Warcraft 3 Reign of Chaos Wood & Metal Riveted Panel Background
        Image(
            painter = painterResource(id = R.drawable.warcraft3_hud_panel_1784669998817),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.65f
        )

        // Dark Vignette Overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color(0xFF0A0C12).copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Top Riveted Iron Strip Accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2A303A), Color(0xFF8A9AAB), Color(0xFFFFD700), Color(0xFF8A9AAB), Color(0xFF2A303A))
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple(GameScreen.CHARACTER_SCREEN, "Héroe", Icons.Default.Person),
                Triple(GameScreen.WORLD_MAP, "Mapa", Icons.Default.Map),
                Triple(GameScreen.DUNGEON, "Dungeon", Icons.Default.Castle),
                Triple(GameScreen.INVENTORY, "Inventario", Icons.Default.ShoppingBag),
                Triple(GameScreen.SHOP, "Tienda", Icons.Default.ShoppingCart)
            )

            tabs.forEach { (tab, label, icon) ->
                val active = currentScreen == tab

                // Warcraft 3 Beveled Metallic Blue Button Styling
                val buttonBgGradient = if (active) {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF00388A),
                            Color(0xFF001F52),
                            Color(0xFF000F2E)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1C222D),
                            Color(0xFF0D111A),
                            Color(0xFF05070C)
                        )
                    )
                }

                val frameBorderBrush = if (active) {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFF1A8),
                            Color(0xFFFFD700),
                            Color(0xFFB8860B)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF7A8A9B),
                            Color(0xFF3A4552),
                            Color(0xFF1A2028)
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .testTag("nav_tab_${label.lowercase()}")
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(buttonBgGradient)
                        .border(
                            width = if (active) 2.dp else 1.5.dp,
                            brush = frameBorderBrush,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = {
                                SoundManager.playButtonClick()
                                onTabSelect(tab)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Beveled Inner Highlight Shadow Box (Warcraft 3 3D Button Inset)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(1.5.dp)
                            .border(
                                width = 1.dp,
                                color = if (active) Color(0xFF64B5F6).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )

                    // Warcraft 3 Metal Rivet Accents on Button Corners
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(3.dp)
                            .offset(x = 3.dp, y = 3.dp)
                            .background(if (active) MedievalGold else Color(0xFF8A9AAB), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(3.dp)
                            .offset(x = (-3).dp, y = 3.dp)
                            .background(if (active) MedievalGold else Color(0xFF8A9AAB), CircleShape)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                    ) {
                        if (tab == GameScreen.DUNGEON) {
                            Image(
                                painter = painterResource(id = R.drawable.img_dungeon_door_1784674104372),
                                contentDescription = label,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                contentScale = ContentScale.Crop,
                                colorFilter = if (!active) ColorFilter.tint(Color(0xFFB0C4DE), androidx.compose.ui.graphics.BlendMode.Modulate) else null
                            )
                        } else {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (active) Color(0xFFFFEA7A) else Color(0xFFB0C4DE),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            color = if (active) Color(0xFFFFEA7A) else Color(0xFFCCCCCC),
                            fontSize = 10.sp,
                            fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Bold,
                            maxLines = 1,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = if (active) androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black,
                                    blurRadius = 4f
                                ) else null
                            )
                        )
                    }
                }
            }
        }
    }
}

// --- CHARACTER CREATOR ---
@Composable
fun CharacterCreatorScreen(viewModel: GameViewModel) {
    val name by viewModel.creatorName.collectAsState()
    val race by viewModel.creatorRace.collectAsState()
    val cls by viewModel.creatorClass.collectAsState()
    val pointsAvailable by viewModel.creatorPointsAvailable.collectAsState()
    val statStr by viewModel.creatorStr.collectAsState()
    val statDex by viewModel.creatorDex.collectAsState()
    val statInt by viewModel.creatorInt.collectAsState()
    val statCon by viewModel.creatorCon.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "ELDORIA CHRONICLES",
                color = MedievalGold,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Crea tu Personaje de Fantasía",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic
            )
        }

        // Selected portrait display
        item {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(3.dp, MedievalGold, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getCharacterPortrait(race, cls)),
                    contentDescription = "Portrait",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Name field
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Nombre del Personaje", color = MedievalGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = name,
                    onValueChange = { viewModel.updateCreatorName(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("char_name_input"),
                    placeholder = { Text("Escribe el nombre de tu héroe...", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = MedievalCardBg,
                        unfocusedContainerColor = MedievalCardBg,
                        focusedIndicatorColor = MedievalGold,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Race selector
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Seleccionar Raza", color = MedievalGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val races = listOf("Humano", "Elfo", "Enano", "Orco")
                    races.forEach { r ->
                        val active = race == r
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (active) MedievalGold else MedievalCardBg
                            ),
                            border = BorderStroke(1.5.dp, if (active) Color.White else MedievalGold.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectRace(r) }
                                .testTag("race_$r"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = r,
                                color = if (active) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Class selector
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Seleccionar Clase", color = MedievalGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val classes = listOf("Guerrero", "Mago", "Pícaro", "Clérigo")
                    classes.forEach { c ->
                        val active = cls == c
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (active) MedievalGold else MedievalCardBg
                            ),
                            border = BorderStroke(1.5.dp, if (active) Color.White else MedievalGold.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectClass(c) }
                                .testTag("class_$c"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = c,
                                color = if (active) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Race & Class Perks Informational Panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
                border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rasgos de Selección",
                        color = MedievalGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Race Passive Perk info
                    Text(
                        text = "Raza: $race",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = when (race) {
                            "Humano" -> "• Inicial: Determinación Humana (+10% Oro en batallas, +5% Probabilidad Crítica).\n• Evolución (Lvl 5+): Campeón Imperial (+15% Oro, regeneras un 8% de tu salud máxima cada turno)."
                            "Elfo" -> "• Inicial: Sentidos Élficos (+10% Maná Máximo, +5% Probabilidad Crítica).\n• Evolución (Lvl 5+): Guardián Astral (+15% Crítico, reduce el coste de maná de tus hechizos en 20%)."
                            "Enano" -> "• Inicial: Piel de Piedra (+10% Salud Máxima, +5 Defensa).\n• Evolución (Lvl 5+): Señor de las Runas (+15% Salud Máxima, +10 Defensa, devuelves 10% del daño recibido)."
                            "Orco" -> "• Inicial: Furia Berserker (+10% Daño infligido total).\n• Evolución (Lvl 5+): Devastador Berserker (+25% Daño infligido, te sanas un 12% del daño de tus ataques básicos)."
                            else -> ""
                        },
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MedievalGold.copy(alpha = 0.2f)))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Class Perk info
                    Text(
                        text = "Clase: $cls",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = when (cls) {
                            "Guerrero" -> "• Rol: Combatiente cuerpo a cuerpo pesado.\n• Atributo principal: Fuerza (Física).\n• Habilidad: Golpe de Escudo / Corte de Batalla.\n• Maestría: Recibe 1 punto extra de defensa por nivel de Constitución."
                            "Mago" -> "• Rol: Hechicero destructor de largo alcance.\n• Atributo principal: Inteligencia (Mágica).\n• Habilidad: Descarga de Escarcha / Bola de Fuego.\n• Maestría: Convierte el 50% de tu Inteligencia en bono de daño de hechizo."
                            "Pícaro" -> "• Rol: Asesino rápido y sigiloso.\n• Atributo principal: Destreza (Física).\n• Habilidad: Puñalada / Golpe de Sombras.\n• Maestría: Aumenta la probabilidad de esquiva y crítico por cada punto de Destreza."
                            "Clérigo" -> "• Rol: Protector y sanador sagrado.\n• Atributo principal: Inteligencia / Fuerza.\n• Habilidad: Martillo Sagrado / Luz de Sanación.\n• Maestría: Combina salud máxima alta con curaciones potentes de bajo costo."
                            else -> ""
                        },
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
            }
        }

        // Stats Points Spender
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
                border = BorderStroke(1.5.dp, MedievalGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estadísticas de Atributo",
                            color = MedievalGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (pointsAvailable > 0) {
                                TextButton(
                                    onClick = { viewModel.autoAllocateCreatorStats() },
                                    modifier = Modifier.testTag("btn_auto_allocate_creator")
                                ) {
                                    Text("Auto Asignar", color = MedievalGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .background(MedievalCrimson, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Puntos: $pointsAvailable",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val stats = listOf(
                        Triple("STR", "Fuerza (Daño físico)", statStr),
                        Triple("DEX", "Destreza (Crítico/Esquiva)", statDex),
                        Triple("INT", "Inteligencia (Hechizos/Mana)", statInt),
                        Triple("CON", "Constitución (Salud)", statCon)
                    )

                    stats.forEach { (code, desc, valCurrent) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(code, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(desc, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.modifyStat(code, -1) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                        .testTag("btn_minus_$code")
                                ) {
                                    Icon(Icons.Default.Remove, "Disminuir", tint = Color.White, modifier = Modifier.size(16.dp))
                                }

                                Text(
                                    text = "$valCurrent",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.width(30.dp),
                                    textAlign = TextAlign.Center
                                )

                                IconButton(
                                    onClick = { viewModel.modifyStat(code, 1) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MedievalGold, CircleShape)
                                        .testTag("btn_plus_$code")
                                ) {
                                    Icon(Icons.Default.Add, "Aumentar", tint = Color.Black, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Play Button
        item {
            Button(
                onClick = { viewModel.submitCharacter() },
                colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("create_char_submit_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Iniciar", tint = Color.Black)
                    Text(
                        "INICIAR AVENTURA",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
// --- WORLD MAP SCREEN (PROCEDURAL GENERATOR VIEW) ---
@Composable
fun CastleDialog(castleState: CastleState, viewModel: GameViewModel) {
    if (!castleState.active) return

    AlertDialog(
        onDismissRequest = { viewModel.closeCastleDialog() },
        confirmButton = {},
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🏰", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = castleState.castleName, color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(text = castleState.kingdomName, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = castleState.description,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Divider(color = MedievalGold.copy(alpha = 0.3f))

                Button(
                    onClick = { viewModel.claimCastleBlessing() },
                    enabled = !castleState.blessingClaimed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedievalXpGreen,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("castle_blessing_btn")
                ) {
                    Text(
                        text = if (castleState.blessingClaimed) "✨ Bendición Real Reclamada" else "✨ Solicitar Bendición Real (+100% HP/MP + 150 Oro)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { viewModel.challengeCastleBoss() },
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalCrimson),
                    modifier = Modifier.fillMaxWidth().testTag("castle_challenge_btn")
                ) {
                    Text(
                        text = "⚔️ Desafiar al Campeón del Reino",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.closeCastleDialog() },
                    border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("castle_close_btn")
                ) {
                    Text("Salir del Castillo", color = MedievalGold, fontSize = 11.sp)
                }
            }
        },
        containerColor = MedievalCardBg,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun SpecialMerchantDialog(merchantState: SpecialMerchantState, viewModel: GameViewModel, playerGold: Int) {
    if (!merchantState.active) return

    AlertDialog(
        onDismissRequest = { viewModel.closeSpecialMerchantDialog() },
        confirmButton = {},
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.wandering_merchant_1784845746333),
                            contentDescription = "Mercader Ambulante",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(2.dp, MedievalGold, RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = merchantState.merchantName, color = MedievalGold, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text(text = "Mercader Viajante de ${merchantState.kingdomName}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                        border = BorderStroke(1.dp, MedievalGold)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, "Oro", tint = MedievalGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$playerGold", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .border(0.8.dp, MedievalGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "«${merchantState.dialogue}»",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 14.sp
                    )
                }

                Divider(color = MedievalGold.copy(alpha = 0.3f))

                if (merchantState.items.isEmpty()) {
                    Text(
                        text = "¡El mercader ha agotado todo su inventario por hoy!",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(merchantState.items) { itemSpec ->
                            val canAfford = playerGold >= itemSpec.discountPrice
                            val rarityColor = getRarityColor(itemSpec.item.rarity)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                                border = BorderStroke(1.5.dp, rarityColor.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Item AI Image Thumbnail
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black)
                                            .border(1.5.dp, rarityColor, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = getItemImageRes(itemSpec.item.imageResName, itemSpec.item.type)),
                                            contentDescription = itemSpec.item.name,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(bottomEnd = 4.dp))
                                                .padding(horizontal = 3.dp, vertical = 1.dp)
                                        ) {
                                            Text("Niv.${itemSpec.item.itemLevel}", color = MedievalGold, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = itemSpec.item.name,
                                                color = rarityColor,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(rarityColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .border(0.5.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(itemSpec.item.rarity.uppercase(), color = rarityColor, fontSize = 7.sp, fontWeight = FontWeight.ExtraBold)
                                            }
                                        }

                                        Text(
                                            text = itemSpec.item.getStatDescription(),
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 9.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${itemSpec.originalPrice}",
                                                color = Color.Gray,
                                                fontSize = 10.sp,
                                                style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.MonetizationOn, "Oro", tint = MedievalGold, modifier = Modifier.size(11.dp))
                                            Text(
                                                text = " ${itemSpec.discountPrice}",
                                                color = MedievalGold,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(MedievalXpGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "-${itemSpec.discountPercent}% OFF",
                                                    color = MedievalXpGreen,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 8.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Button(
                                        onClick = { viewModel.buySpecialMerchantItem(itemSpec) },
                                        enabled = canAfford,
                                        colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp).testTag("buy_merchant_item_${itemSpec.item.id}")
                                    ) {
                                        Text("Comprar", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.closeSpecialMerchantDialog() },
                    border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("close_merchant_btn")
                ) {
                    Text("Cerrar Mercado Viajante", color = MedievalGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = MedievalCardBg,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun WorldMapScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val proceduralMap by viewModel.proceduralMap.collectAsState()
    val castleState by viewModel.castleState.collectAsState()
    val specialMerchantState by viewModel.specialMerchantState.collectAsState()

    val p = progress ?: return

    val currentKingdom = KingdomGenerator.getKingdomForCoords(p.currentX, p.currentY)

    CastleDialog(castleState = castleState, viewModel = viewModel)
    SpecialMerchantDialog(merchantState = specialMerchantState, viewModel = viewModel, playerGold = p.charGold)

    // Keep track of the currently selected tile in our medieval UI
    var selectedTile by remember(p.currentX, p.currentY) {
        mutableStateOf<MapTile?>(proceduralMap.find { it.x == p.currentX && it.y == p.currentY })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Decorative Hero Medieval Overland Map Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.5.dp, MedievalGold.copy(alpha = 0.7f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_medieval_map),
                    contentDescription = "Map Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentKingdom.name,
                            color = MedievalGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                            border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = currentKingdom.subtitle,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Posición del Héroe: (X: ${p.currentX}, Y: ${p.currentY})",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // AUTO MODES HUD PANEL
        val autoCombatActive by viewModel.isAutoCombat.collectAsState()
        val autoNavActive by viewModel.isAutoNavigation.collectAsState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Auto Combat Toggle
            Button(
                onClick = { viewModel.toggleAutoCombat() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (autoCombatActive) MedievalGold else Color(0xFF263238),
                    contentColor = if (autoCombatActive) Color.Black else Color.White
                ),
                border = BorderStroke(1.dp, if (autoCombatActive) Color.White else MedievalGold.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("auto_combat_toggle")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(
                        imageVector = if (autoCombatActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Auto Combate",
                        modifier = Modifier.size(16.dp),
                        tint = if (autoCombatActive) Color.Black else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (autoCombatActive) "COMBATE: AUTO" else "COMBATE: MANUAL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // Auto Navigation Toggle
            Button(
                onClick = { viewModel.toggleAutoNavigation() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (autoNavActive) MedievalGold else Color(0xFF263238),
                    contentColor = if (autoNavActive) Color.Black else Color.White
                ),
                border = BorderStroke(1.dp, if (autoNavActive) Color.White else MedievalGold.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("auto_navigation_toggle")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(
                        imageVector = if (autoNavActive) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Auto Navegación",
                        modifier = Modifier.size(16.dp),
                        tint = if (autoNavActive) Color.Black else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (autoNavActive) "NAVEGACIÓN: AUTO" else "NAVEGACIÓN: MANUAL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid Representation Styled as a Medieval Map Board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, MedievalGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_medieval_map),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.3f,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .padding(8.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Dynamic grid around player coordinates (X-2 to X+2, Y-2 to Y+2)
                    val currentCx = p.currentX
                    val currentY = p.currentY

                    val sortedTiles = mutableListOf<MapTile>()
                    for (dy in 2 downTo -2) {
                        for (dx in -2..2) {
                            val targetX = currentCx + dx
                            val targetY = currentY + dy
                            val found = proceduralMap.find { it.x == targetX && it.y == targetY }
                            if (found != null) {
                                sortedTiles.add(found)
                            } else {
                                sortedTiles.add(MapTile(targetX, targetY, "Vacío", false, false))
                            }
                        }
                    }

                    items(sortedTiles) { tile ->
                        val isPlayerHere = tile.x == p.currentX && tile.y == p.currentY
                        val isSelected = selectedTile?.x == tile.x && selectedTile?.y == tile.y
                        val distance = abs(p.currentX - tile.x) + abs(p.currentY - tile.y)
                        val isAdjacent = distance == 1

                        val borderColor = when {
                            isPlayerHere -> MedievalGold
                            isSelected -> MedievalGold
                            tile.encounterType == "CASTLE" -> Color(0xFFFFD700)
                            tile.encounterType == "SPECIAL_MERCHANT" -> Color(0xFF00E676)
                            tile.isObstacle -> Color(0xFF5D4037)
                            tile.isBossLair -> MedievalCrimson
                            tile.isEnemySpawn && !tile.explored -> Color(0xFF673AB7)
                            isAdjacent -> MedievalXpGreen.copy(alpha = 0.6f)
                            else -> Color.White.copy(alpha = 0.08f)
                        }

                        val tileImageRes = when {
                            tile.isObstacle -> R.drawable.img_tile_obstacle_1784470907788
                            tile.isBossLair -> R.drawable.img_tile_enemy_1784470940695
                            tile.encounterType == "CHEST" || tile.encounterType == "TREASURE" && !tile.explored -> R.drawable.img_tile_chest_1784470917774
                            tile.encounterType == "SHRINE" && !tile.explored -> R.drawable.img_tile_shrine_1784470929381
                            tile.isEnemySpawn && !tile.explored -> R.drawable.img_tile_enemy_1784470940695
                            else -> R.drawable.img_tile_grass_1784470894787
                        }

                        val tileBadge = when {
                            tile.encounterType == "CASTLE" -> "🏰"
                            tile.encounterType == "SPECIAL_MERCHANT" -> "🧙‍♂️"
                            tile.encounterType == "TREASURE" && !tile.explored -> "💰"
                            tile.encounterType == "SHRINE" && !tile.explored -> "🔮"
                            tile.isBossLair -> "👑"
                            tile.isEnemySpawn && !tile.explored -> "⚔️"
                            else -> null
                        }

                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .border(
                                    width = if (isPlayerHere || isSelected) 2.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedTile = tile
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = tileImageRes),
                                    contentDescription = tile.biome,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                if (tile.biome == "Vacío") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.75f))
                                    )
                                }

                                if (tileBadge != null && !isPlayerHere) {
                                    Text(
                                        text = tileBadge,
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                            .padding(2.dp)
                                    )
                                }

                                if (isPlayerHere) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.4f))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                            .border(1.5.dp, MedievalGold, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsWalk,
                                            contentDescription = "Héroe Aquí",
                                            tint = MedievalGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else {
                                    if (tile.explored && tile.biome != "Vacío" && tile.biome != "Santuario Inicial") {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Despejado",
                                            tint = MedievalXpGreen,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .align(Alignment.TopEnd)
                                                .padding(2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "${tile.x},${tile.y}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                                        .padding(horizontal = 2.dp, vertical = 0.5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // DIRECTIONAL D-PAD CONTROLS FOR INFINITE WORLD TRAVERSAL
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.35f)),
            colors = CardDefaults.cardColors(containerColor = MedievalCardBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Controles de Dirección (Navegación Infinita)",
                    color = MedievalGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // North (Y + 1)
                    IconButton(
                        onClick = { viewModel.moveDirection(0, 1) },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF263238), CircleShape)
                            .border(1.dp, MedievalGold, CircleShape)
                            .testTag("nav_dpad_north")
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Norte", tint = MedievalGold)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // West (X - 1)
                        IconButton(
                            onClick = { viewModel.moveDirection(-1, 0) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF263238), CircleShape)
                                .border(1.dp, MedievalGold, CircleShape)
                                .testTag("nav_dpad_west")
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Oeste", tint = MedievalGold)
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .border(1.dp, MedievalGold.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Explore, contentDescription = "Brújula", tint = MedievalGold, modifier = Modifier.size(18.dp))
                        }

                        // East (X + 1)
                        IconButton(
                            onClick = { viewModel.moveDirection(1, 0) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF263238), CircleShape)
                                .border(1.dp, MedievalGold, CircleShape)
                                .testTag("nav_dpad_east")
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Este", tint = MedievalGold)
                        }
                    }

                    // South (Y - 1)
                    IconButton(
                        onClick = { viewModel.moveDirection(0, -1) },
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF263238), CircleShape)
                            .border(1.dp, MedievalGold, CircleShape)
                            .testTag("nav_dpad_south")
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Sur", tint = MedievalGold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // INTERACTIVE MAP SELECTION PREVIEW CARD
        selectedTile?.let { tile ->
            val isPlayerHere = tile.x == p.currentX && tile.y == p.currentY
            val distance = abs(p.currentX - tile.x) + abs(p.currentY - tile.y)
            val isAdjacent = distance == 1

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(containerColor = MedievalCardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = MedievalGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (tile.specialName.isNotEmpty()) tile.specialName else tile.biome,
                                    color = MedievalGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                text = "Coordenadas: (X: ${tile.x}, Y: ${tile.y}) • ${tile.kingdomName}",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (tile.explored || isPlayerHere) Color(0xFF1B5E20).copy(alpha = 0.4f) else Color(0xFFE53935).copy(alpha = 0.2f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (tile.explored || isPlayerHere) MedievalXpGreen.copy(alpha = 0.6f) else MedievalCrimson.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (tile.explored || isPlayerHere) "Región Segura" else "Amenaza Activa",
                                color = if (tile.explored || isPlayerHere) MedievalXpGreen else MedievalCrimson,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        val description = when (tile.encounterType) {
                            "CASTLE" -> "🏰 Bastión y Castillo de ${tile.kingdomName}. Puedes solicitar la Bendición Real o desafiar al Campeón."
                            "SPECIAL_MERCHANT" -> "🧙‍♂️ Mercader Ambulante del Reino. Vende artefactos raros y únicos con precios con descuento especial."
                            "TREASURE" -> if (tile.cleared) "📦 Gran Tesoro Real (Reclamado)." else "💰 Gran Tesoro Real. Oculta cantidades de oro abundante y equipo legendario."
                            "SHRINE" -> if (tile.cleared) "✨ Santuario Místico Agotado (Energías consumidas)." else "🔮 Santuario Ancestral que restaura vida y maná."
                            "CHEST" -> if (tile.cleared) "📦 Cofre Abierto (Saqueado)." else "🎁 Cofre del Tesoro Oculto."
                            "BOSS" -> if (tile.cleared) "👑 Guarida de Jefe Derrotado." else "👑 Guarida de Jefe Imperial. Enemigo temible de alto nivel con botín legendario asegurado."
                            else -> "Terreno de ${tile.biome} habitado por criaturas y bestias de ${tile.kingdomName}."
                        }
                        Text(
                            text = description,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (tile.isBossLair && !tile.cleared) {
                            Text(
                                text = "⚠️ ADVERTENCIA: JEFE IMPERIAL (Nivel ${tile.levelRequirement})",
                                color = MedievalCrimson,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        } else if (tile.isEnemySpawn && !tile.cleared) {
                            Text(
                                text = "⚔️ Nivel de Amenaza: Nivel ${tile.levelRequirement}",
                                color = MedievalGold,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        when {
                            isPlayerHere -> {
                                when {
                                    tile.encounterType == "CASTLE" -> {
                                        Button(
                                            onClick = { viewModel.selectTileAndExplore(tile) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(38.dp)
                                        ) {
                                            Text("🏰 INGRESAR AL CASTILLO DE ${tile.kingdomName}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    tile.encounterType == "SPECIAL_MERCHANT" -> {
                                        Button(
                                            onClick = { viewModel.selectTileAndExplore(tile) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(38.dp)
                                        ) {
                                            Text("🧙‍♂️ HABLAR CON MERCADER", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    tile.encounterType == "SHRINE" && !tile.cleared -> {
                                        Button(
                                            onClick = { viewModel.selectTileAndExplore(tile) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(38.dp)
                                        ) {
                                            Text("🔮 ACTIVAR SANTUARIO ANCESTRAL", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    (tile.encounterType == "CHEST" || tile.encounterType == "TREASURE") && !tile.cleared -> {
                                        Button(
                                            onClick = { viewModel.selectTileAndExplore(tile) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(38.dp)
                                        ) {
                                            Text("💰 ABRIR Y RECLAMAR TESORO", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    tile.cleared && (tile.encounterType == "SHRINE" || tile.encounterType == "CHEST" || tile.encounterType == "TREASURE") -> {
                                        Text(
                                            text = "✨ Lugar explorado. Las riquezas y energías de esta casilla han sido consumidas.",
                                            color = Color.White.copy(alpha = 0.5f),
                                            fontSize = 11.sp,
                                            fontStyle = FontStyle.Italic,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = "Estás parado sobre esta casilla de forma segura.",
                                            color = Color.White.copy(alpha = 0.5f),
                                            fontSize = 11.sp,
                                            fontStyle = FontStyle.Italic,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                            tile.isObstacle -> {
                                Row(
                                    modifier = Modifier.align(Alignment.Center),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = MedievalCrimson, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Terreno intransitable.",
                                        color = MedievalCrimson,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            isAdjacent -> {
                                val buttonLabel = when {
                                    tile.encounterType == "CASTLE" -> "INGRESAR AL CASTILLO"
                                    tile.encounterType == "SPECIAL_MERCHANT" -> "HABLAR CON MERCADER"
                                    tile.encounterType == "TREASURE" && !tile.cleared -> "RECLAMAR GRAN TESORO REAL"
                                    tile.encounterType == "CHEST" && !tile.cleared -> "ABRIR COFRE DEL TESORO"
                                    tile.encounterType == "SHRINE" && !tile.cleared -> "ACTIVAR ALTAR SAGRADO"
                                    tile.isBossLair && !tile.cleared -> "DESAFIAR JEFE (Nivel ${tile.levelRequirement})"
                                    tile.isEnemySpawn && !tile.cleared -> "ATACAR ENEMIGO (Nivel ${tile.levelRequirement})"
                                    else -> "VIAJAR Y EXPLORAR"
                                }
                                val buttonColor = when {
                                    tile.encounterType == "CASTLE" -> Color(0xFFFFD700)
                                    tile.encounterType == "SPECIAL_MERCHANT" -> Color(0xFF00E676)
                                    (tile.isBossLair || tile.isEnemySpawn) && !tile.cleared -> MedievalCrimson
                                    else -> MedievalGold
                                }

                                Button(
                                    onClick = {
                                        viewModel.selectTileAndExplore(tile)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                        .testTag("travel_btn")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = buttonLabel,
                                            color = if (buttonColor == MedievalGold || buttonColor == Color(0xFFFFD700) || buttonColor == Color(0xFF00E676)) Color.Black else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                            else -> {
                                Button(
                                    onClick = {
                                        viewModel.selectTileAndExplore(tile)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .testTag("travel_far_btn")
                                ) {
                                    Text(
                                        text = "VIAJAR HACIA ESTA CASILLA",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- MEDIEVAL MARKET / SHOP SCREEN ---
@Composable
fun ShopScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val shopItems by viewModel.shopItems.collectAsState()
    val p = progress ?: return

    val rawInventory = GameJsonParser.listFromJson<Item>(p.inventoryJson).filter { it.type != "EMPTY" }

    var searchQuery by remember { mutableStateOf("") }
    var selectedRarityFilter by remember { mutableStateOf("Todas") }
    var selectedTypeFilter by remember { mutableStateOf("Todos") }
    var selectedLevelFilter by remember { mutableStateOf("Todos") }
    var showMassSellConfirmation by remember { mutableStateOf(false) }

    val filteredInventory = remember(rawInventory, searchQuery, selectedRarityFilter, selectedTypeFilter, selectedLevelFilter) {
        filterInventory(rawInventory, selectedRarityFilter, selectedTypeFilter, selectedLevelFilter, searchQuery)
    }

    val massSellTotalPrice = remember(filteredInventory) {
        filteredInventory.sumOf { viewModel.calculateSellPrice(it) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Decorative Medieval Market Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, MedievalGold)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.merchant_stall_banner_1784845825754),
                        contentDescription = "Mercado Real",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Black.copy(alpha = 0.85f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Mercado Real & Mercaderes Viajantes",
                            color = MedievalGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Intercambia tu oro por armas legendarias o vende tus tesoros de mazmorra.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Wandering Merchant Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
                border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wandering_merchant_1784845746333),
                        contentDescription = "Grommar el Mercader Viajante",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(2.dp, MedievalGold, RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Grommar, Mercader & Viajante de la Corona",
                            color = MedievalGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "«¡Saludos viajero! Recorro los reinos ofreciendo reliquias místicas, pociones sagradas y comprando botines de guerra.»",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // Miniscule Potion & Refresh Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Buy Potion Button
                Button(
                    onClick = { viewModel.buyPotion() },
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalCardBg),
                    border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("buy_potion_btn"),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalPharmacy, "Poción", tint = MedievalCrimson, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+50% HP/MP", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.MonetizationOn, "Oro", tint = MedievalGold, modifier = Modifier.size(14.dp))
                        Text(" 40", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MedievalGold)
                    }
                }

                // Refresh Shop Button
                Button(
                    onClick = { viewModel.refreshShop() },
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalCardBg),
                    border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("refresh_shop_btn"),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, "Reabastecer", tint = MedievalGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reabastecer", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.MonetizationOn, "Oro", tint = MedievalGold, modifier = Modifier.size(14.dp))
                        Text(" 20", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MedievalGold)
                    }
                }
            }
        }

        // Buy Section Title
        item {
            Text(
                text = "Armas y Equipo Disponibles (Comprar)",
                color = MedievalGold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        // Shop Items list
        if (shopItems.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "El mercado está agotado. ¡Reabastece de mercancías!",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(shopItems) { item ->
                val cost = when (item.rarity.uppercase()) {
                    "UNIVERSAL" -> 600
                    "ARCANO" -> 450
                    "LEGENDARIO", "LEGENDARY" -> 300
                    "ÉPICO", "EPIC" -> 160
                    "RARO", "RARE" -> 80
                    else -> 30
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
                    border = BorderStroke(1.5.dp, getRarityColor(item.rarity).copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Prominent Hero Asset Image
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .border(2.dp, getRarityColor(item.rarity), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = getItemImageRes(item.imageResName, item.type)),
                                contentDescription = item.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Level Badge Overlay on Asset
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(bottomEnd = 6.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("Niv.${item.itemLevel}", color = MedievalGold, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    item.name,
                                    color = getRarityColor(item.rarity),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(getRarityColor(item.rarity).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .border(0.5.dp, getRarityColor(item.rarity).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(item.rarity.uppercase(), color = getRarityColor(item.rarity), fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            val stats = remember(item) {
                                val list = mutableListOf<String>()
                                if (item.strBonus > 0) list.add("STR +${item.strBonus}")
                                if (item.dexBonus > 0) list.add("DEX +${item.dexBonus}")
                                if (item.intBonus > 0) list.add("INT +${item.intBonus}")
                                if (item.conBonus > 0) list.add("CON +${item.conBonus}")
                                if (item.dmgBonus > 0) list.add("Daño +${item.dmgBonus}")
                                if (item.defBonus > 0) list.add("Def +${item.defBonus}")
                                if (item.hpRegen > 0) list.add("Reg.HP +${item.hpRegen}")
                                list
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                stats.take(3).forEach { statText ->
                                    val isRegen = statText.contains("Reg")
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isRegen) MedievalXpGreen.copy(alpha = 0.15f)
                                                else MedievalGold.copy(alpha = 0.12f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .border(
                                                0.5.dp,
                                                if (isRegen) MedievalXpGreen.copy(alpha = 0.3f)
                                                else MedievalGold.copy(alpha = 0.3f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = statText,
                                            color = if (isRegen) MedievalXpGreen else MedievalGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = { viewModel.buyItem(item, cost) },
                            colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Comprar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MonetizationOn, "Oro", tint = Color.Black, modifier = Modifier.size(11.dp))
                                    Text(" $cost", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sell Section Mass Sell Button
        if (filteredInventory.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            SoundManager.playButtonClick()
                            showMassSellConfirmation = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedievalCrimson),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(12.dp))
                            Text("Venta Masiva ($massSellTotalPrice 🪙)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Filter Bar in Shop
        item {
            InventoryFilterBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedRarity = selectedRarityFilter,
                onSelectRarity = { selectedRarityFilter = it },
                selectedType = selectedTypeFilter,
                onSelectType = { selectedTypeFilter = it },
                selectedLevel = selectedLevelFilter,
                onSelectLevel = { selectedLevelFilter = it }
            )
        }

        // Inventory Items for selling
        if (filteredInventory.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (rawInventory.isEmpty()) "No tienes equipo para vender en tu mochila." else "No hay objetos que coincidan con los filtros seleccionados.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredInventory) { item ->
                val sellPrice = viewModel.calculateSellPrice(item)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MedievalCardBg.copy(alpha = 0.7f)),
                    border = BorderStroke(1.dp, getRarityColor(item.rarity).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = getItemImageRes(item.imageResName, item.type)),
                            contentDescription = item.name,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, getRarityColor(item.rarity), RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${item.name} [Niv.${item.itemLevel}]", color = getRarityColor(item.rarity), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Precio: ", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                                Icon(Icons.Default.MonetizationOn, "Oro", tint = MedievalGold, modifier = Modifier.size(11.dp))
                                Text(" $sellPrice", color = MedievalGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Button(
                            onClick = { viewModel.sellItem(item) },
                            colors = ButtonDefaults.buttonColors(containerColor = MedievalCrimson),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Vender +", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                Icon(Icons.Default.MonetizationOn, "Oro", tint = Color.Yellow, modifier = Modifier.size(11.dp))
                                Text("$sellPrice", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showMassSellConfirmation) {
        AlertDialog(
            onDismissRequest = { showMassSellConfirmation = false },
            containerColor = MedievalCardBg,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MedievalGold, modifier = Modifier.size(20.dp))
                    Text("⚡ Venta Masiva de Mochila", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Vas a vender ${filteredInventory.size} objetos filtrados por un total de $massSellTotalPrice 🪙 monedas de oro.",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Filtros aplicados:", color = MedievalGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    if (searchQuery.isNotBlank()) Text("• Búsqueda: \"$searchQuery\"", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    Text("• Rareza: $selectedRarityFilter", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    Text("• Tipo: $selectedTypeFilter", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    Text("• Nivel: $selectedLevelFilter", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.massSellItems(filteredInventory)
                        showMassSellConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalCrimson),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Vender Todo ($massSellTotalPrice 🪙)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showMassSellConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Cancelar", color = Color.White, fontSize = 11.sp)
                }
            }
        )
    }
}



fun getThreeJsHtmlContent(): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <style>
                body { margin: 0; overflow: hidden; background-color: #0F111A; font-family: sans-serif; }
                canvas { display: block; width: 100vw; height: 100vh; }
                #info {
                    position: absolute;
                    bottom: 10px;
                    left: 50%;
                    transform: translateX(-50%);
                    color: #FFC107;
                    font-size: 11px;
                    text-align: center;
                    pointer-events: none;
                    background: rgba(15, 17, 26, 0.8);
                    padding: 4px 10px;
                    border-radius: 4px;
                    border: 1px solid rgba(255, 193, 7, 0.3);
                }
            </style>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
        </head>
        <body>
            <div id="info">Arrastra para explorar • Toca cualquier casilla para viajar</div>
            <div id="canvas-container"></div>
            <script>
                let scene, camera, renderer, raycaster;
                let tiles = [];
                let playerMesh;
                let playerPos = { x: 0, y: 0 };
                let targetCamPos = { x: 0, y: 0 };
                let isDragging = false;
                let previousPointerPosition = { x: 0, y: 0 };
                
                function init() {
                    scene = new THREE.Scene();
                    scene.background = new THREE.Color(0x0F111A);
                    
                    const aspect = window.innerWidth / window.innerHeight;
                    const size = 6;
                    camera = new THREE.OrthographicCamera(
                        -size * aspect, size * aspect,
                        size, -size,
                        0.1, 1000
                    );
                    camera.position.set(0, 0, 10);
                    camera.lookAt(0, 0, 0);
                    
                    renderer = new THREE.WebGLRenderer({ antialias: true });
                    renderer.setSize(window.innerWidth, window.innerHeight);
                    renderer.setPixelRatio(window.devicePixelRatio);
                    document.body.appendChild(renderer.domElement);
                    
                    raycaster = new THREE.Raycaster();
                    
                    const ambientLight = new THREE.AmbientLight(0xffffff, 0.8);
                    scene.add(ambientLight);
                    
                    const dirLight = new THREE.DirectionalLight(0xffffff, 0.5);
                    dirLight.position.set(5, 5, 10);
                    scene.add(dirLight);
                    
                    setupInput();
                    animate();
                }
                
                function animate() {
                    requestAnimationFrame(animate);
                    camera.position.x += (targetCamPos.x - camera.position.x) * 0.08;
                    camera.position.y += (targetCamPos.y - camera.position.y) * 0.08;
                    
                    if (playerMesh) {
                        playerMesh.position.y = (playerPos.y * 2) + Math.sin(Date.now() * 0.005) * 0.15;
                        playerMesh.rotation.y += 0.02;
                        playerMesh.rotation.x += 0.01;
                    }
                    
                    renderer.render(scene, camera);
                }
                
                function updateMapState(currentX, currentY, tilesJsonString) {
                    playerPos.x = currentX;
                    playerPos.y = currentY;
                    targetCamPos.x = currentX * 2;
                    targetCamPos.y = currentY * 2;
                    
                    let mapData = [];
                    try {
                        mapData = JSON.parse(tilesJsonString);
                    } catch(e) {
                        console.error("Failed to parse map JSON", e);
                    }
                    
                    tiles.forEach(tile => scene.remove(tile.mesh));
                    tiles = [];
                    
                    if (playerMesh) scene.remove(playerMesh);
                    
                    mapData.forEach(tile => {
                        const isPlayerHere = tile.x === currentX && tile.y === currentY;
                        const distance = Math.abs(currentX - tile.x) + Math.abs(currentY - tile.y);
                        const isAdjacent = distance === 1;
                        
                        let geometry;
                        let color = 0x171A24;
                        let borderCol = 0x333b52;
                        let zPos = 0;
                        
                        if (tile.isObstacle) {
                            geometry = new THREE.BoxGeometry(1.7, 1.7, 1.0);
                            color = 0x37474F;
                            borderCol = 0x546E7A;
                            zPos = 0.5;
                        } else {
                            geometry = new THREE.PlaneGeometry(1.7, 1.7);
                            if (isPlayerHere) {
                                color = 0xFFC107;
                            } else if (tile.isBossLair) {
                                color = 0xE53935;
                                borderCol = 0xFFC107;
                            } else if (tile.isEnemySpawn) {
                                color = 0x421E1E;
                                borderCol = 0xE53935;
                            } else if (tile.explored) {
                                color = 0x2C3140;
                            } else if (isAdjacent) {
                                color = 0x1F4D32;
                                borderCol = 0x4CAF50;
                            }
                        }
                        
                        const material = new THREE.MeshPhongMaterial({
                            color: color,
                            shininess: 30,
                            flatShading: true
                        });
                        
                        const mesh = new THREE.Mesh(geometry, material);
                        mesh.position.set(tile.x * 2, tile.y * 2, zPos);
                        
                        const borderGeo = tile.isObstacle ? new THREE.BoxHelper(mesh, borderCol) : new THREE.EdgesGeometry(geometry);
                        let wireframe;
                        if (tile.isObstacle) {
                            wireframe = borderGeo;
                        } else {
                            const borderMat = new THREE.LineBasicMaterial({ color: borderCol, linewidth: 2 });
                            wireframe = new THREE.LineSegments(borderGeo, borderMat);
                        }
                        mesh.add(wireframe);
                        
                        scene.add(mesh);
                        tiles.push({ mesh: mesh, tileData: tile });
                    });
                    
                    const playerGeo = new THREE.SphereGeometry(0.5, 16, 16);
                    const playerMat = new THREE.MeshPhongMaterial({
                        color: 0xFFC107,
                        emissive: 0x3a2b00,
                        shininess: 80
                    });
                    playerMesh = new THREE.Mesh(playerGeo, playerMat);
                    playerMesh.position.set(currentX * 2, currentY * 2, 0.5);
                    scene.add(playerMesh);
                }
                
                function setupInput() {
                    const dom = renderer.domElement;
                    
                    const getPointerPos = (e) => {
                        if (e.touches && e.touches.length > 0) {
                            return { x: e.touches[0].clientX, y: e.touches[0].clientY };
                        }
                        return { x: e.clientX, y: e.clientY };
                    };
                    
                    const onDown = (e) => {
                        isDragging = false;
                        const pos = getPointerPos(e);
                        previousPointerPosition = pos;
                        dom.startX = pos.x;
                        dom.startY = pos.y;
                    };
                    
                    const onMove = (e) => {
                        const pos = getPointerPos(e);
                        const dx = pos.x - previousPointerPosition.x;
                        const dy = pos.y - previousPointerPosition.y;
                        
                        if (dom.startX && (Math.abs(pos.x - dom.startX) > 5 || Math.abs(pos.y - dom.startY) > 5)) {
                            isDragging = true;
                        }
                        
                        if (isDragging) {
                            const aspect = window.innerWidth / window.innerHeight;
                            const size = 6;
                            const worldWidth = size * aspect * 2;
                            const worldHeight = size * 2;
                            
                            targetCamPos.x -= (dx / window.innerWidth) * worldWidth;
                            targetCamPos.y += (dy / window.innerHeight) * worldHeight;
                        }
                        
                        previousPointerPosition = pos;
                    };
                    
                    const onUp = (e) => {
                        if (!isDragging) {
                            const clickX = dom.startX;
                            const clickY = dom.startY;
                            
                            if (clickX !== undefined && clickY !== undefined) {
                                const mouse = new THREE.Vector2();
                                mouse.x = (clickX / window.innerWidth) * 2 - 1;
                                mouse.y = -(clickY / window.innerHeight) * 2 + 1;
                                
                                raycaster.setFromCamera(mouse, camera);
                                const tileMeshes = tiles.map(t => t.mesh);
                                const intersects = raycaster.intersectObjects(tileMeshes);
                                
                                if (intersects.length > 0) {
                                    const tappedMesh = intersects[0].object;
                                    const matchedTile = tiles.find(t => t.mesh === tappedMesh);
                                    
                                    if (matchedTile) {
                                        const tile = matchedTile.tileData;
                                        if (window.AndroidBridge) {
                                            window.AndroidBridge.exploreTile(tile.x, tile.y);
                                        }
                                    }
                                }
                            }
                        }
                        isDragging = false;
                    };
                    
                    dom.addEventListener('mousedown', onDown);
                    window.addEventListener('mousemove', onMove);
                    window.addEventListener('mouseup', onUp);
                    
                    dom.addEventListener('touchstart', onDown, { passive: true });
                    window.addEventListener('touchmove', onMove, { passive: true });
                    window.addEventListener('touchend', onUp);
                }
                
                window.addEventListener('resize', () => {
                    const aspect = window.innerWidth / window.innerHeight;
                    const size = 6;
                    camera.left = -size * aspect;
                    camera.right = size * aspect;
                    camera.top = size;
                    camera.bottom = -size;
                    camera.updateProjectionMatrix();
                    renderer.setSize(window.innerWidth, window.innerHeight);
                });
                
                init();
            </script>
        </body>
        </html>
    """.trimIndent()
}

// --- COMBAT TEXTURED BUTTON ---
@Composable
fun CombatTexturedButton(
    onClick: () -> Unit,
    enabled: Boolean,
    backgroundImageId: Int,
    testTag: String,
    modifier: Modifier = Modifier,
    borderColor: Color = MedievalGold,
    content: @Composable () -> Unit
) {
    val alpha = if (enabled) 1f else 0.45f
    Box(
        modifier = modifier
            .alpha(alpha)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black)
            .border(1.dp, if (enabled) borderColor.copy(alpha = 0.8f) else Color.Gray.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = backgroundImageId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = if (enabled) 0.6f else 0.2f
        )
        // Semi-transparent overlay to ensure text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.55f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

enum class SkillGlassTheme(
    val glowColor: Color,
    val centerColor: Color,
    val baseDarkColor: Color
) {
    TURQUOISE(Color(0xFF00E5FF), Color(0xFF80DEEA), Color(0xFF003840)),
    AMBER(Color(0xFFFFD700), Color(0xFFFFE082), Color(0xFF4A3200)),
    CRIMSON(Color(0xFFFF1744), Color(0xFFFF8A80), Color(0xFF4A000A)),
    EMERALD(Color(0xFF00E676), Color(0xFFA5D6A7), Color(0xFF003310)),
    PURPLE(Color(0xFFE040FB), Color(0xFFEA80FC), Color(0xFF33004A))
}

@Composable
fun StainedGlassSkillSlot(
    title: String,
    badgeLabel: String,
    costText: String,
    icon: ImageVector,
    glassTheme: SkillGlassTheme,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        // Square Stained Glass Action Box
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (enabled) {
                        Brush.radialGradient(
                            colors = listOf(glassTheme.centerColor.copy(alpha = 0.5f), glassTheme.baseDarkColor, Color(0xFF0A0C10))
                        )
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFF1F222A), Color(0xFF0E1015)))
                    }
                )
                .border(
                    width = if (enabled) 2.dp else 1.dp,
                    brush = if (enabled) {
                        Brush.verticalGradient(
                            listOf(
                                glassTheme.glowColor,
                                glassTheme.glowColor.copy(alpha = 0.7f),
                                Color(0xFF1E222A)
                            )
                        )
                    } else {
                        SolidColor(Color(0xFF3A424C))
                    },
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable(enabled = enabled) {
                    SoundManager.playButtonClick()
                    onClick()
                }
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            // Stained Glass Lattice Pattern Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val lineColor = if (enabled) glassTheme.glowColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f)

                drawLine(lineColor, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(w, h), strokeWidth = 1.2f)
                drawLine(lineColor, androidx.compose.ui.geometry.Offset(w, 0f), androidx.compose.ui.geometry.Offset(0f, h), strokeWidth = 1.2f)
                drawLine(lineColor, androidx.compose.ui.geometry.Offset(w / 2f, 0f), androidx.compose.ui.geometry.Offset(w / 2f, h), strokeWidth = 1.2f)
                drawLine(lineColor, androidx.compose.ui.geometry.Offset(0f, h / 2f), androidx.compose.ui.geometry.Offset(w, h / 2f), strokeWidth = 1.2f)

                val facetPath = Path().apply {
                    moveTo(w / 2f, 4f)
                    lineTo(w - 4f, h / 2f)
                    lineTo(w / 2f, h - 4f)
                    lineTo(4f, h / 2f)
                    close()
                }
                drawPath(facetPath, lineColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2f))
            }

            // Center Icon
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (enabled) glassTheme.centerColor else Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )

            // Lower-Right Cost Overlay Tag
            if (costText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(3.dp))
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = costText,
                        color = if (enabled) Color.White else Color.Gray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black,
                                blurRadius = 3f
                            )
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Action Hotkey Badge Below Slot
        Box(
            modifier = Modifier
                .background(
                    brush = if (enabled) {
                        Brush.verticalGradient(
                            listOf(Color(0xFF2C323E), Color(0xFF141820))
                        )
                    } else {
                        SolidColor(Color(0xFF12141A))
                    },
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (enabled) glassTheme.glowColor.copy(alpha = 0.6f) else Color(0xFF2A303A),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 5.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badgeLabel,
                color = if (enabled) glassTheme.glowColor else Color.Gray,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

// --- COMBAT SCREEN ---
@Composable
fun CombatScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val combatState by viewModel.combatState.collectAsState()
    val dungeonRun by viewModel.dungeonRunState.collectAsState()

    val p = progress ?: return
    if (combatState.enemy == null) return

    val enemy = combatState.enemy!!

    val activeAnim = combatState.activeAnimation

    // Animation: Player Offset X (Attacking slides right)
    val playerOffsetX by animateDpAsState(
        targetValue = when (activeAnim) {
            "PLAYER_ATTACK" -> 45.dp
            else -> 0.dp
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "PlayerOffsetX"
    )

    // Animation: Player Scale (Heal or potion expands a bit)
    val playerScale by animateFloatAsState(
        targetValue = when (activeAnim) {
            "PLAYER_HEAL", "PLAYER_POTION" -> 1.15f
            else -> 1.0f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "PlayerScale"
    )

    // Animation: Player Shake X (when enemy hit player)
    val playerShakeX by animateDpAsState(
        targetValue = when (activeAnim) {
            "ENEMY_ATTACK", "ENEMY_SKILL" -> 10.dp
            else -> 0.dp
        },
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(durationMillis = 80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PlayerShakeX"
    )

    val playerTotalOffsetX = playerOffsetX + playerShakeX

    // Animation: Enemy Offset X (Attacking slides left)
    val enemyOffsetX by animateDpAsState(
        targetValue = when (activeAnim) {
            "ENEMY_ATTACK" -> (-45).dp
            else -> 0.dp
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "EnemyOffsetX"
    )

    // Animation: Enemy Scale (Casting skills expands a bit)
    val enemyScale by animateFloatAsState(
        targetValue = when (activeAnim) {
            "ENEMY_SKILL" -> 1.15f
            else -> 1.0f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "EnemyScale"
    )

    // Animation: Enemy Shake X (when player hit enemy)
    val enemyShakeX by animateDpAsState(
        targetValue = when (activeAnim) {
            "PLAYER_ATTACK", "PLAYER_MAGIC" -> (-10).dp
            else -> 0.dp
        },
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(durationMillis = 80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "EnemyShakeX"
    )

    val enemyTotalOffsetX = enemyOffsetX + enemyShakeX

    val autoCombatActive by viewModel.isAutoCombat.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP Header: Auto Combat Toggle HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Combate en Progreso",
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            
            Button(
                onClick = { viewModel.toggleAutoCombat() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (autoCombatActive) MedievalGold else Color(0xFF263238),
                    contentColor = if (autoCombatActive) Color.Black else Color.White
                ),
                border = BorderStroke(1.dp, if (autoCombatActive) Color.White else MedievalGold.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(160.dp)
                    .height(36.dp)
                    .testTag("combat_screen_auto_combat_toggle")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(
                        imageVector = if (autoCombatActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Auto Combate",
                        modifier = Modifier.size(14.dp),
                        tint = if (autoCombatActive) Color.Black else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (autoCombatActive) "COMBATE: AUTO" else "COMBATE: MANUAL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // TOP: Sleek Compact 2-Row Enemy Header Bar (strictly restricted height)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
            border = BorderStroke(
                1.5.dp,
                if (enemy.rarity == "LEGENDARY") MedievalGold else if (enemy.rarity == "CHAMPION") Color(0xFFFF9800) else if (enemy.rarity == "ELITE") Color(0xFF0288D1) else MedievalCrimson
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            val enemyColor = when (enemy.rarity) {
                "LEGENDARY" -> MedievalGold
                "CHAMPION" -> Color(0xFFFF9800)
                "ELITE" -> Color(0xFF0288D1)
                else -> Color.White
            }
            val rarityLabel = when (enemy.rarity) {
                "LEGENDARY" -> "👑 JEFE LEGENDARIO"
                "CHAMPION" -> "👑 CAMPEÓN"
                "ELITE" -> "⭐ ÉLITE"
                else -> "MONSTRUO"
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column: Row 1 = Enemy Name, Row 2 = Rarity Badge + Level
                Column(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = enemy.name,
                        color = enemyColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(enemyColor.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                                .border(0.5.dp, enemyColor, RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = rarityLabel,
                                color = enemyColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Nivel ${enemy.level}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right Column: Enemy HP Bar
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(100.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("HP ", color = MedievalCrimson, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("${enemy.currentHp}/${enemy.maxHp}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.Black)
                    ) {
                        val enemyHpPercent = if (enemy.maxHp > 0) enemy.currentHp.toFloat() / enemy.maxHp else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(enemyHpPercent)
                                .background(MedievalCrimson)
                        )
                    }
                }
            }
        }

        // MID: Grand Hero Battle Arena (Maximizes Asset Relevance)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0E14)),
            border = BorderStroke(1.5.dp, MedievalGold.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Gradient Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF0F121C), Color(0xFF231212).copy(alpha = 0.5f), Color(0xFF0A0B10))
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // HERO PLAYER COLUMN
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(110.dp)
                            .offset(x = playerTotalOffsetX)
                            .scale(playerScale)
                    ) {
                        // Floating Combat Feedback on Player (Above portrait)
                        Box(modifier = Modifier.height(24.dp), contentAlignment = Alignment.Center) {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = combatState.damageFeedbackPlayer != null,
                                enter = fadeIn(tween(150)) + slideInVertically(initialOffsetY = { -it }),
                                exit = fadeOut(tween(200))
                            ) {
                                combatState.damageFeedbackPlayer?.let { f ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                            .border(1.dp, if (f.contains("+")) MedievalXpGreen else MedievalCrimson, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(f, color = if (f.contains("+")) MedievalXpGreen else MedievalCrimson, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }

                        // Player Portrait Frame (LARGE 96dp Asset)
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black)
                                    .border(3.dp, if (activeAnim == "PLAYER_HEAL" || activeAnim == "PLAYER_POTION") MedievalXpGreen else MedievalGold, RoundedCornerShape(16.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = getCharacterPortrait(p.charRace, p.charClass)),
                                    contentDescription = "Player Portrait",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Attack Hit Overlay on Player
                            if (activeAnim == "ENEMY_ATTACK") {
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.Red.copy(alpha = 0.45f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.FlashOn, "Impacto", tint = Color.White, modifier = Modifier.size(42.dp))
                                }
                            }
                            if (activeAnim == "ENEMY_SKILL") {
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF8E24AA).copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.BlurOn, "Hechizo Enemigo", tint = Color.White, modifier = Modifier.size(42.dp))
                                }
                            }
                            if (activeAnim == "PLAYER_HEAL" || activeAnim == "PLAYER_POTION") {
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.Green.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, "Sanación", tint = Color.White, modifier = Modifier.size(42.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(p.charName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        val evolvedTitle = viewModel.getEvolvedRaceName(p.charRace, p.charLevel)
                        Text(evolvedTitle, color = MedievalGold, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)

                        Spacer(modifier = Modifier.height(6.dp))

                        // Player HP Bar
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text("HP", color = MedievalXpGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black)
                            ) {
                                val hpPercent = if (p.maxHp > 0) combatState.playerCurrentHp.toFloat() / p.maxHp else 0f
                                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(hpPercent).background(MedievalXpGreen))
                            }
                            Text(" ${combatState.playerCurrentHp}", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Player MP Bar
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text("MP", color = MedievalManaBlue, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black)
                            ) {
                                val mpPercent = if (p.maxMp > 0) combatState.playerCurrentMp.toFloat() / p.maxMp else 0f
                                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(mpPercent).background(MedievalManaBlue))
                            }
                            Text(" ${combatState.playerCurrentMp}", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // VS CENTER COLUMN
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.width(70.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(2.dp, MedievalCrimson, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("VS", color = MedievalGold, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val passiveTag = when (p.charRace) {
                            "Humano" -> if (p.charLevel >= 5) "🏆 +8% TurnHeal" else "👑 +10% Oro"
                            "Elfo" -> if (p.charLevel >= 5) "🌌 -20% MP Cost" else "👁️ +10% MaxMP"
                            "Enano" -> if (p.charLevel >= 5) "🛡️ 10% Reflect" else "⛰️ +10% MaxHP"
                            "Orco" -> if (p.charLevel >= 5) "🩸 12% Lifesteal" else "⚔️ +10% Daño"
                            else -> ""
                        }

                        if (passiveTag.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, MedievalGold.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(passiveTag, color = MedievalGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // ENEMY MONSTER COLUMN
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(110.dp)
                            .offset(x = enemyTotalOffsetX)
                            .scale(enemyScale)
                    ) {
                        // Floating Combat Feedback on Enemy (Above portrait)
                        Box(modifier = Modifier.height(24.dp), contentAlignment = Alignment.Center) {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = combatState.damageFeedbackEnemy != null,
                                enter = fadeIn(tween(150)) + slideInVertically(initialOffsetY = { -it }),
                                exit = fadeOut(tween(200))
                            ) {
                                combatState.damageFeedbackEnemy?.let { feedback ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                            .border(1.dp, MedievalGold, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(feedback, color = MedievalGold, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }

                        // Enemy Portrait Frame (LARGE 96dp Asset)
                        Box(contentAlignment = Alignment.Center) {
                            val enemyBorderColor = when (enemy.rarity) {
                                "LEGENDARY" -> MedievalGold
                                "CHAMPION" -> Color(0xFFFF9800)
                                "ELITE" -> Color(0xFF0288D1)
                                else -> MedievalCrimson
                            }
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black)
                                    .border(3.dp, enemyBorderColor, RoundedCornerShape(16.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = getEnemyPortraitRes(enemy.name, enemy.isBoss)),
                                    contentDescription = "Enemy Portrait",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Attack Hit Overlay on Enemy
                            if (activeAnim == "PLAYER_ATTACK") {
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.Red.copy(alpha = 0.45f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.FlashOn, "Hit", tint = Color.White, modifier = Modifier.size(42.dp))
                                }
                            }
                            if (activeAnim == "PLAYER_MAGIC") {
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF1E88E5).copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Bolt, "Magia", tint = Color.White, modifier = Modifier.size(42.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(enemy.name.split(",").first(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            text = "Lvl ${enemy.level} " + when (enemy.rarity) {
                                "LEGENDARY" -> "Jefe"
                                "CHAMPION" -> "Campeón"
                                "ELITE" -> "Élite"
                                else -> "Salvaje"
                            },
                            color = when (enemy.rarity) {
                                "LEGENDARY" -> MedievalGold
                                "CHAMPION" -> Color(0xFFFF9800)
                                "ELITE" -> Color(0xFF0288D1)
                                else -> MedievalCrimson
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Enemy HP Bar
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text("HP", color = MedievalCrimson, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black)
                            ) {
                                val enemyHpPercent = if (enemy.maxHp > 0) enemy.currentHp.toFloat() / enemy.maxHp else 0f
                                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(enemyHpPercent).background(MedievalCrimson))
                            }
                            Text(" ${enemy.currentHp}", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // BOTTOM: Combat Logs Console
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
            border = BorderStroke(width = 1.dp, color = Color.White.copy(alpha = 0.15f))
        ) {
            val scrollState = rememberScrollState()
            LaunchedEffect(combatState.combatLogs.size) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(8.dp)
            ) {
                combatState.combatLogs.forEach { log ->
                    val logColor = when {
                        log.contains("derrotado") || log.contains("SUBISTE") || log.contains("Encontraste") || log.contains("VICTORIA") -> MedievalGold
                        log.contains("sana") || log.contains("recuperas") || log.contains("rejuvenecedora") -> MedievalXpGreen
                        log.contains("Corte Sanguinolento") || log.contains("te ataca e inflige") || log.contains("puntos de daño físico") -> MedievalCrimson
                        log.contains("Drenaje de Vida") -> Color(0xFFE91E63)
                        log.contains("Maldición de Maná") || log.contains("Maná") || log.contains("MP") -> MedievalManaBlue
                        log.contains("Piel de Espinas") || log.contains("Escudo Rúnico") || log.contains("esquivas") || log.contains("ESQUIVADO") -> Color(0xFF03A9F4)
                        else -> Color.White.copy(alpha = 0.9f)
                    }
                    Text(
                        text = log,
                        color = logColor,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Actions Controls - Stained Glass Gothic Action HUD Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (combatState.victory != null) {
                if (combatState.victory == true && dungeonRun.inDungeonRun) {
                    if (dungeonRun.dungeonCompletedJustNow) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🏆 ¡¡CALABOZO CONQUISTADO!!",
                                color = MedievalGold,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "¡Derrotaste al Jefe Final y ganaste su Tesoro Único Inmortal!",
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.exitDungeonRun() },
                                colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .testTag("claim_dungeon_treasure_button")
                            ) {
                                Text("🏆 Reclamar Tesoro y Salir", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (dungeonRun.stageVictoryPending) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⚔️ Subjefe ${dungeonRun.currentStage}/9 Derrotado",
                                color = MedievalGold,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Salud restante para la siguiente etapa: ${dungeonRun.persistentHp} HP",
                                color = Color(0xFFFF8A8A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { viewModel.advanceDungeonStage() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .testTag("advance_dungeon_stage_button")
                            ) {
                                Text("🔥 Enfrentar Siguiente Subjefe (${dungeonRun.currentStage + 1}/10)", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.exitDungeonRun() },
                                border = BorderStroke(1.dp, Color.Gray),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(0.7f)
                            ) {
                                Text("🚪 Retirarse con el Botín", color = Color.LightGray)
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "¡¡VICTORIA HEROICA!!",
                                color = MedievalGold,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Button(
                                onClick = { viewModel.exitCombatScreen() },
                                colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .testTag("exit_combat_button")
                            ) {
                                Text("Regresar al Mapa", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (combatState.victory == true) "¡¡VICTORIA HEROICA!!" else "¡HAS CAÍDO!",
                            color = if (combatState.victory == true) MedievalGold else MedievalCrimson,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Button(
                            onClick = {
                                if (dungeonRun.inDungeonRun) {
                                    viewModel.exitDungeonRun()
                                } else {
                                    viewModel.exitCombatScreen()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .testTag("exit_combat_button")
                        ) {
                            Text(if (dungeonRun.inDungeonRun) "Salir del Calabozo" else "Regresar al Mapa", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                val inventory = GameJsonParser.listFromJson<Item>(p.inventoryJson)
                val potionCount = inventory.count { it.type == "POTION" }
                val classSkills = GameJsonParser.listFromJson<Skill>(p.skillsJson)

                // Stained Glass Frame HUD Panel Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF090A0E))
                        .border(
                            BorderStroke(
                                2.dp,
                                Brush.verticalGradient(
                                    listOf(Color(0xFF8A9AAB), Color(0xFF3A424C), Color(0xFF1E222A))
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    // Stained Glass Dark Action Bar Background Texture
                    Image(
                        painter = painterResource(id = R.drawable.gothic_skill_bar_bg_1784670759745),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.55f
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Basic Attack Slot (Crimson Stained Glass)
                        StainedGlassSkillSlot(
                            title = "Ataque Físico",
                            badgeLabel = "Ataque",
                            costText = "0",
                            icon = Icons.Default.SportsMartialArts,
                            glassTheme = SkillGlassTheme.CRIMSON,
                            enabled = combatState.playerTurn,
                            testTag = "combat_attack_button",
                            onClick = { viewModel.executeBasicAttack() }
                        )

                        // 2. Class Skills (Turquoise / Amber / Purple Stained Glass)
                        classSkills.forEachIndexed { index, skill ->
                            val isSkillEnabled = combatState.playerTurn && combatState.playerCurrentMp >= skill.manaCost
                            val skillTheme = when (skill.id) {
                                "g_1", "m_2", "c_2" -> SkillGlassTheme.AMBER
                                "m_1", "p_1" -> SkillGlassTheme.TURQUOISE
                                "c_1", "g_2" -> SkillGlassTheme.EMERALD
                                else -> SkillGlassTheme.PURPLE
                            }
                            val skillIcon = when (skill.id) {
                                "g_1" -> Icons.Default.Gavel
                                "g_2" -> Icons.Default.Shield
                                "m_1" -> Icons.Default.FlashOn
                                "m_2" -> Icons.Default.LocalFireDepartment
                                "p_1" -> Icons.Default.Gavel
                                "p_2" -> Icons.Default.VisibilityOff
                                "c_1" -> Icons.Default.AutoAwesome
                                "c_2" -> Icons.Default.MilitaryTech
                                else -> Icons.Default.Bolt
                            }

                            StainedGlassSkillSlot(
                                title = skill.name,
                                badgeLabel = if (index == 0) "Sk 1" else "Sk 2",
                                costText = "${skill.manaCost}",
                                icon = skillIcon,
                                glassTheme = skillTheme,
                                enabled = isSkillEnabled,
                                testTag = "skill_${skill.id}",
                                onClick = { viewModel.executeSkill(skill) }
                            )
                        }

                        // 3. Potion Slot (Emerald Green Stained Glass)
                        StainedGlassSkillSlot(
                            title = "Usar Poción",
                            badgeLabel = "Poción",
                            costText = "x$potionCount",
                            icon = Icons.Default.LocalPharmacy,
                            glassTheme = SkillGlassTheme.EMERALD,
                            enabled = combatState.playerTurn && potionCount > 0,
                            testTag = "combat_potion_button",
                            onClick = { viewModel.usePotionCombat() }
                        )

                        // 4. Flee Slot (Dark Violet Stained Glass)
                        StainedGlassSkillSlot(
                            title = "Huir",
                            badgeLabel = "Huir",
                            costText = "",
                            icon = Icons.Default.DirectionsRun,
                            glassTheme = SkillGlassTheme.PURPLE,
                            enabled = combatState.playerTurn,
                            testTag = "combat_flee_button",
                            onClick = { viewModel.fleeCombat() }
                        )
                    }
                }
            }
        }
    }
}

// --- CHARACTER PROFILE SCREEN ---
@Composable
fun CharacterScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val p = progress ?: return
    val playerStats by viewModel.playerStats.collectAsState()
    val allCharacters by viewModel.allCharactersState.collectAsState()

    val unspentStats = viewModel.getUnspentStatPoints(p)
    var showCharSelectionDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
                border = BorderStroke(2.dp, MedievalGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Image(
                            painter = painterResource(id = getCharacterPortrait(p.charRace, p.charClass)),
                            contentDescription = "Portrait",
                            modifier = Modifier
                                .size(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(2.5.dp, MedievalGold, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.charName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text("${p.charRace} ${p.charClass}", color = MedievalGold, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Nivel ${p.charLevel}", color = MedievalCrimson, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)

                            Spacer(modifier = Modifier.height(10.dp))

                            // HP Bar
                            val hpRatio = if (p.maxHp > 0) (p.currentHp.toFloat() / p.maxHp).coerceIn(0f, 1f) else 1f
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("HP", color = MedievalCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("${p.currentHp} / ${p.maxHp}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black)
                                        .border(0.8.dp, MedievalCrimson.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(hpRatio)
                                            .background(MedievalCrimson)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // MP Bar
                            val mpRatio = if (p.maxMp > 0) (p.currentMp.toFloat() / p.maxMp).coerceIn(0f, 1f) else 1f
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("MP", color = Color(0xFF29B6F6), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("${p.currentMp} / ${p.maxMp}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black)
                                        .border(0.8.dp, Color(0xFF0288D1).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(mpRatio)
                                            .background(Color(0xFF0288D1))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // EXP Bar (Unified & Full-Width for high visibility and legibility)
                    val nextLvlExp = p.charLevel * 100
                    val expRatio = if (nextLvlExp > 0) (p.charExp.toFloat() / nextLvlExp).coerceIn(0f, 1f) else 0f
                    val expPercent = (expRatio * 100).toInt()
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("EXPERIENCIA Y PROGRESO", color = MedievalXpGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${p.charExp} / $nextLvlExp ($expPercent%)", color = MedievalXpGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(18.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Color.Black)
                                .border(1.dp, MedievalXpGreen.copy(alpha = 0.6f), RoundedCornerShape(9.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(expRatio)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xFF2E7D32), MedievalXpGreen)
                                        )
                                    )
                            )
                            Text(
                                text = "EXP: ${p.charExp} / $nextLvlExp",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                onClick = { viewModel.changeScreen(GameScreen.TALENTS) },
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, MedievalGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("open_talents_tree_button")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.talent_tree_banner_1784843563984),
                        contentDescription = "Fondo Árbol de Talentos",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.8f),
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Schema,
                            contentDescription = "Talentos",
                            tint = MedievalGold,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ÁRBOL DE TALENTOS" + if (p.talentPointsAvailable > 0) " (${p.talentPointsAvailable} PTS DISPONIBLES)" else "",
                            color = MedievalGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        item {
            val evolvedRace = viewModel.getEvolvedRaceName(p.charRace, p.charLevel)
            val passiveDesc = viewModel.getRacePassiveDescription(p.charRace, p.charLevel)
            val evolvedIcon = when (p.charRace) {
                "Humano" -> "🏆"
                "Elfo" -> "🌌"
                "Enano" -> "🛡️"
                "Orco" -> "🩸"
                else -> "⭐"
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
                border = BorderStroke(1.5.dp, if (p.charLevel >= 5) MedievalGold else Color.Gray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Evolución Racial",
                            color = if (p.charLevel >= 5) MedievalGold else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (p.charLevel >= 5) MedievalGold else Color(0xFF424242)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (p.charLevel >= 5) "EVOLUCIONADO" else "FASE BÁSICA",
                                color = if (p.charLevel >= 5) Color.Black else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = "Raza actual: $evolvedIcon $evolvedRace",
                        color = if (p.charLevel >= 5) MedievalGold else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = passiveDesc,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    
                    if (p.charLevel < 5) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Progreso de Evolución (Nivel ${p.charLevel} / 5)",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color.Black)
                            ) {
                                val progressPercent = p.charLevel.toFloat() / 5f
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(progressPercent)
                                        .background(MedievalGold)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${(p.charLevel * 100) / 5}%",
                                color = MedievalGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MedievalGold.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "✨",
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "¡Felicidades! Has alcanzado la forma racial definitiva. Tus habilidades pasivas ahora operan a su máximo potencial.",
                                    color = MedievalGold,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
                border = BorderStroke(1.5.dp, MedievalGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MilitaryTech, "Atributos", tint = MedievalGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Atributos de Combate", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        if (unspentStats > 0) {
                            Button(
                                onClick = { viewModel.autoAllocateAllStatPoints() },
                                colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_auto_allocate_hero")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoMode, "Auto", tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Auto Asignar", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (unspentStats > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MedievalCrimson),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "Puntos Disponibles para Asignar: $unspentStats",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val statRows = listOf(
                        Quadruple("STR", "Fuerza (+Daño físico)", p.statStr, Icons.Default.FitnessCenter),
                        Quadruple("DEX", "Destreza (+Crítico/Evasión)", p.statDex, Icons.Default.DirectionsRun),
                        Quadruple("INT", "Inteligencia (+Daño Mágico/MP)", p.statInt, Icons.Default.Psychology),
                        Quadruple("CON", "Constitución (+Salud Máxima)", p.statCon, Icons.Default.Favorite)
                    )

                    statRows.forEach { (code, name, value, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(icon, code, tint = MedievalGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(code, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(name, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$value",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 12.dp)
                                )

                                if (unspentStats > 0) {
                                    IconButton(
                                        onClick = { viewModel.allocateStatPoint(code) },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(MedievalGold, CircleShape)
                                            .testTag("allocate_stat_$code")
                                    ) {
                                        Icon(Icons.Default.Add, "Asignar", tint = Color.Black, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Multi-Character Management Section (Replaces Old Reset Game Button)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.startNewCharacterCreator() },
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("create_new_character_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PersonAdd, "Crear Personaje", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CREAR OTRO PERSONAJE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = { showCharSelectionDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalCardBg),
                    border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("switch_character_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, "Mis Personajes", tint = MedievalGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("MIS PERSONAJES / CAMBIAR HÉROE (${allCharacters.size})", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Modal Dialog for Multi-Character Switch / Selection
    if (showCharSelectionDialog) {
        AlertDialog(
            onDismissRequest = { showCharSelectionDialog = false },
            containerColor = MedievalCardBg,
            title = {
                Text(
                    "Seleccionar Personaje / Partida",
                    color = MedievalGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Elige qué héroe deseas controlar o elimina una partida antigua:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )

                    allCharacters.forEach { charItem ->
                        val isCurrent = charItem.id == p.id
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) MedievalGold.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isCurrent) MedievalGold else Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            charItem.charName,
                                            color = if (isCurrent) MedievalGold else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        if (isCurrent) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("(ACTIVO)", color = MedievalXpGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text(
                                        "${charItem.charRace} ${charItem.charClass} - Niv.${charItem.charLevel}",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 10.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (!isCurrent) {
                                        Button(
                                            onClick = {
                                                viewModel.selectCharacter(charItem.id)
                                                showCharSelectionDialog = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Jugar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }

                                    if (allCharacters.size > 1) {
                                        IconButton(
                                            onClick = { viewModel.deleteCharacter(charItem.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, "Borrar", tint = MedievalCrimson, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCharSelectionDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Cerrar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// --- TALENTS TREE SCREEN ---
fun getTalentIcon(id: String): ImageVector {
    return when (id) {
        "t_1" -> Icons.Default.Bolt
        "t_2" -> Icons.Default.Shield
        "t_3" -> Icons.Default.SportsMartialArts
        "t_4" -> Icons.Default.FlashOn
        "t_5" -> Icons.Default.BlurOn
        "t_6" -> Icons.Default.Shield
        "t_7" -> Icons.Default.DirectionsWalk
        "t_8" -> Icons.Default.Gavel
        "t_9" -> Icons.Default.MonetizationOn
        else -> Icons.Default.Star
    }
}

@Composable
fun TalentsScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val p = progress ?: return

    val talentList = GameJsonParser.listFromJson<Talent>(p.talentsJson)
    var selectedTalent by remember { mutableStateOf<Talent?>(null) }
    
    // Find active selected talent to keep it reactive when allocating points
    val activeSelectedTalent = selectedTalent?.let { sel -> talentList.find { it.id == sel.id } }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
            border = BorderStroke(width = 1.dp, color = MedievalGold.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Red de Talentos Celestiales", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Conecta runas mágicas y canaliza tu poder", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MedievalManaBlue)
                ) {
                    Text(
                        "Puntos: ${p.talentPointsAvailable}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Constellation Box
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MedievalGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .background(Color.Black)
        ) {
            // Generative cosmic space background image
            Image(
                painter = painterResource(id = R.drawable.img_talents_bg_1784603912942),
                contentDescription = "Cosmo celestial",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.45f
            )

            val width = maxWidth
            val height = maxHeight

            val p1 = talentList.find { it.id == "t_1" }
            val p2 = talentList.find { it.id == "t_2" }
            val p3 = talentList.find { it.id == "t_3" }
            val p4 = talentList.find { it.id == "t_4" }
            val p5 = talentList.find { it.id == "t_5" }
            val p6 = talentList.find { it.id == "t_6" }
            val p7 = talentList.find { it.id == "t_7" }
            val p8 = talentList.find { it.id == "t_8" }
            val p9 = talentList.find { it.id == "t_9" }

            // Helper for connection status mapping
            fun getConnectionStyle(
                endActive: Boolean,
                isPrereqMet: Boolean,
                activeColor: Color,
                dimColor: Color,
                lockedColor: Color
            ): Pair<Color, Boolean> {
                return when {
                    endActive -> Pair(activeColor, false)
                    isPrereqMet -> Pair(dimColor, true)
                    else -> Pair(lockedColor, false)
                }
            }

            // Connection Lines Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                fun drawGlowingConnection(startX: Float, startY: Float, endX: Float, endY: Float, style: Pair<Color, Boolean>) {
                    val (color, isDashed) = style
                    val pathEffect = if (isDashed) {
                        androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                    } else null

                    // Glow line behind
                    drawLine(
                        color = color.copy(alpha = 0.18f),
                        start = androidx.compose.ui.geometry.Offset(startX, startY),
                        end = androidx.compose.ui.geometry.Offset(endX, endY),
                        strokeWidth = 10.dp.toPx()
                    )
                    
                    // Solid/Dashed line
                    drawLine(
                        color = color,
                        start = androidx.compose.ui.geometry.Offset(startX, startY),
                        end = androidx.compose.ui.geometry.Offset(endX, endY),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = pathEffect
                    )
                }

                // Connections from Origin (0.50f, 0.08f)
                p1?.let {
                    val style = getConnectionStyle(it.currentRank > 0, true, MedievalCrimson, MedievalCrimson.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.2f))
                    drawGlowingConnection(w * 0.50f, h * 0.08f, w * 0.18f, h * 0.28f, style)
                }
                p4?.let {
                    val style = getConnectionStyle(it.currentRank > 0, true, MedievalManaBlue, MedievalManaBlue.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.2f))
                    drawGlowingConnection(w * 0.50f, h * 0.08f, w * 0.50f, h * 0.28f, style)
                }
                p7?.let {
                    val style = getConnectionStyle(it.currentRank > 0, true, MedievalGold, MedievalGold.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.2f))
                    drawGlowingConnection(w * 0.50f, h * 0.08f, w * 0.82f, h * 0.28f, style)
                }

                // Combat Path: t_1 -> t_2
                if (p1 != null && p2 != null) {
                    val isPrereqMet = p1.currentRank >= 3
                    val style = getConnectionStyle(p2.currentRank > 0, isPrereqMet, MedievalCrimson, MedievalCrimson.copy(alpha = 0.5f), Color(0xFF3E1F25))
                    drawGlowingConnection(w * 0.18f, h * 0.28f, w * 0.18f, h * 0.56f, style)
                }
                // Magic Path: t_4 -> t_5
                if (p4 != null && p5 != null) {
                    val isPrereqMet = p4.currentRank >= 3
                    val style = getConnectionStyle(p5.currentRank > 0, isPrereqMet, MedievalManaBlue, MedievalManaBlue.copy(alpha = 0.5f), Color(0xFF1B2A3E))
                    drawGlowingConnection(w * 0.50f, h * 0.28f, w * 0.50f, h * 0.56f, style)
                }
                // Shadow Path: t_7 -> t_8
                if (p7 != null && p8 != null) {
                    val isPrereqMet = p7.currentRank >= 3
                    val style = getConnectionStyle(p8.currentRank > 0, isPrereqMet, MedievalGold, MedievalGold.copy(alpha = 0.5f), Color(0xFF3E351D))
                    drawGlowingConnection(w * 0.82f, h * 0.28f, w * 0.82f, h * 0.56f, style)
                }

                // Combat Path: t_2 -> t_3
                if (p2 != null && p3 != null) {
                    val isPrereqMet = p2.currentRank >= 3
                    val style = getConnectionStyle(p3.currentRank > 0, isPrereqMet, MedievalCrimson, MedievalCrimson.copy(alpha = 0.5f), Color(0xFF3E1F25))
                    drawGlowingConnection(w * 0.18f, h * 0.56f, w * 0.18f, h * 0.84f, style)
                }
                // Magic Path: t_5 -> t_6
                if (p5 != null && p6 != null) {
                    val isPrereqMet = p5.currentRank >= 3
                    val style = getConnectionStyle(p6.currentRank > 0, isPrereqMet, MedievalManaBlue, MedievalManaBlue.copy(alpha = 0.5f), Color(0xFF1B2A3E))
                    drawGlowingConnection(w * 0.50f, h * 0.56f, w * 0.50f, h * 0.84f, style)
                }
                // Shadow Path: t_8 -> t_9
                if (p8 != null && p9 != null) {
                    val isPrereqMet = p8.currentRank >= 3
                    val style = getConnectionStyle(p9.currentRank > 0, isPrereqMet, MedievalGold, MedievalGold.copy(alpha = 0.5f), Color(0xFF3E351D))
                    drawGlowingConnection(w * 0.82f, h * 0.56f, w * 0.82f, h * 0.84f, style)
                }
            }

            // Path Titles
            Box(modifier = Modifier.align(Alignment.TopStart).padding(start = 12.dp, top = 65.dp)) {
                Text("ACERO", color = MedievalCrimson.copy(alpha = 0.8f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 65.dp)) {
                Text("ÉTER", color = MedievalManaBlue.copy(alpha = 0.8f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(end = 12.dp, top = 65.dp)) {
                Text("SOMBRAS", color = MedievalGold.copy(alpha = 0.8f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }

            // Render Central Hub Node (Hero Origin)
            Box(
                modifier = Modifier
                    .offset(
                        x = width * 0.50f - 24.dp,
                        y = height * 0.08f - 24.dp
                    )
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(2.dp, MedievalGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getCharacterPortrait(p.charRace, p.charClass)),
                    contentDescription = "Origen del Héroe",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // Frame border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )
            }

            // Render All 9 Talent Nodes
            val nodes = listOf(
                Pair(p1, Pair(0.18f, 0.28f)),
                Pair(p4, Pair(0.50f, 0.28f)),
                Pair(p7, Pair(0.82f, 0.28f)),
                Pair(p2, Pair(0.18f, 0.56f)),
                Pair(p5, Pair(0.50f, 0.56f)),
                Pair(p8, Pair(0.82f, 0.56f)),
                Pair(p3, Pair(0.18f, 0.84f)),
                Pair(p6, Pair(0.50f, 0.84f)),
                Pair(p9, Pair(0.82f, 0.84f))
            )

            nodes.forEach { (talent, coords) ->
                if (talent != null) {
                    val (fx, fy) = coords
                    val hasPrereq = talent.prerequisiteId == null ||
                                    (talentList.find { it.id == talent.prerequisiteId }?.currentRank ?: 0) >= 3

                    val isMax = talent.currentRank >= talent.maxRank
                    val isActive = talent.currentRank > 0

                    val nodeThemeColor = when (talent.category) {
                        "COMBAT" -> MedievalCrimson
                        "MAGIC" -> MedievalManaBlue
                        else -> MedievalGold
                    }

                    val borderColor = when {
                        isMax -> MedievalGold
                        isActive -> nodeThemeColor
                        hasPrereq -> nodeThemeColor.copy(alpha = 0.5f)
                        else -> Color.Gray.copy(alpha = 0.25f)
                    }

                    val glowAlpha = if (isActive) 0.35f else 0.0f
                    val isSelected = activeSelectedTalent?.id == talent.id

                    Box(
                        modifier = Modifier
                            .offset(
                                x = width * fx - 26.dp,
                                y = height * fy - 26.dp
                            )
                            .size(52.dp)
                    ) {
                        // Pulse glow for active/selected node
                        if (isActive || isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                (if (isSelected) MedievalGold else nodeThemeColor).copy(alpha = glowAlpha + 0.15f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        // Round rune node
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF0F121C))
                                .border(
                                    width = if (isSelected) 2.5.dp else if (isMax) 2.dp else 1.5.dp,
                                    color = if (isSelected) MedievalGold else borderColor,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedTalent = talent
                                }
                                .testTag("talent_node_${talent.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getTalentIcon(talent.id),
                                contentDescription = talent.name,
                                tint = if (isActive) Color.White else Color.White.copy(alpha = 0.35f),
                                modifier = Modifier.size(20.dp)
                            )

                            // Rank overlay badge at bottom
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = (2).dp)
                                    .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                                    .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    "${talent.currentRank}/${talent.maxRank}",
                                    color = if (isMax) MedievalGold else if (isActive) Color.White else Color.White.copy(alpha = 0.4f),
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Talent Detail Panel (Saves screen estate and keeps UI clean and elegant)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
            border = BorderStroke(width = 1.dp, color = MedievalGold.copy(alpha = 0.25f))
        ) {
            if (activeSelectedTalent != null) {
                val talent = activeSelectedTalent
                val hasPrereq = talent.prerequisiteId == null ||
                                (talentList.find { it.id == talent.prerequisiteId }?.currentRank ?: 0) >= 3
                val isMax = talent.currentRank >= talent.maxRank
                val canUpgrade = hasPrereq && !isMax && p.talentPointsAvailable > 0

                val categoryName = when (talent.category) {
                    "COMBAT" -> "Fuerza y Acero"
                    "MAGIC" -> "Magia y Éter"
                    else -> "Sombras y Azar"
                }

                val categoryColor = when (talent.category) {
                    "COMBAT" -> MedievalCrimson
                    "MAGIC" -> MedievalManaBlue
                    else -> MedievalGold
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(categoryColor.copy(alpha = 0.15f))
                                    .border(1.dp, categoryColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getTalentIcon(talent.id),
                                    contentDescription = null,
                                    tint = categoryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(talent.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(categoryName, color = categoryColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Text(
                            text = if (isMax) "RANGO MÁXIMO" else "Rango ${talent.currentRank}/${talent.maxRank}",
                            color = if (isMax) MedievalGold else Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = talent.description,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Requirements / Info indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Prerequisite requirement
                            if (talent.prerequisiteId != null) {
                                val prereq = talentList.find { it.id == talent.prerequisiteId }
                                if (prereq != null) {
                                    val met = prereq.currentRank >= 3
                                    Text(
                                        text = "✔ Requiere: ${prereq.name} (Rango 3)",
                                        color = if (met) MedievalXpGreen else MedievalCrimson,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                Text(
                                    text = "✔ Sin requisitos previos",
                                    color = MedievalXpGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Talent Points requirement
                            if (!isMax) {
                                val pointsMet = p.talentPointsAvailable > 0
                                Text(
                                    text = "✔ Requiere: 1 punto de talento",
                                    color = if (pointsMet) MedievalXpGreen else MedievalCrimson,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (canUpgrade) {
                                    viewModel.allocateTalentPoint(talent.id)
                                }
                            },
                            enabled = canUpgrade,
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = categoryColor,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("talent_upgrade_btn")
                        ) {
                            Text(
                                text = if (isMax) "Completado" else "Canalizar",
                                color = if (canUpgrade) Color.Black else Color.White.copy(alpha = 0.4f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                // Empty State Instruction
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schema,
                        contentDescription = null,
                        tint = MedievalGold.copy(alpha = 0.25f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Selecciona una runa estelar",
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Toca cualquier nodo del mapa celestial para canalizar tus puntos de talento y despertar nuevos poderes pasivos.",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MuEquipmentSlot(
    label: String,
    code: String,
    item: Item?,
    modifier: Modifier = Modifier,
    onUnequip: () -> Unit
) {
    Card(
        modifier = modifier
            .size(64.dp)
            .border(
                2.dp,
                if (item != null) getRarityColor(item.rarity) else MedievalGold.copy(alpha = 0.35f),
                RoundedCornerShape(10.dp)
            )
            .clickable(enabled = item != null) { onUnequip() }
            .testTag("equip_slot_$code"),
        colors = CardDefaults.cardColors(
            containerColor = if (item != null) Color.Black else Color(0xFF101218)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            if (item != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = getItemImageRes(item.imageResName, item.type)),
                        contentDescription = item.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(bottomEnd = 6.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("Niv.${item.itemLevel}", color = MedievalGold, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(getRarityColor(item.rarity).copy(alpha = 0.85f), RoundedCornerShape(topStart = 6.dp))
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(item.rarity.take(1), color = Color.Black, fontSize = 7.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val defaultRes = getItemImageRes("", code)
                    Image(
                        painter = painterResource(id = defaultRes),
                        contentDescription = label,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        alpha = 0.28f
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.8f))
                            .padding(vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = MedievalGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun filterInventory(
    inventory: List<Item>,
    rarity: String,
    type: String,
    level: String,
    searchQuery: String = ""
): List<Item> {
    val query = searchQuery.trim().lowercase()
    return inventory.filter { item ->
        val searchOk = if (query.isEmpty()) true else {
            item.name.lowercase().contains(query) ||
            item.description.lowercase().contains(query) ||
            item.type.lowercase().contains(query) ||
            item.rarity.lowercase().contains(query)
        }

        val rarityOk = when (rarity) {
            "Común" -> item.rarity.uppercase() in listOf("COMÚN", "COMMON")
            "Raro" -> item.rarity.uppercase() in listOf("RARO", "RARE")
            "Épico" -> item.rarity.uppercase() in listOf("ÉPICO", "EPIC")
            "Legendario" -> item.rarity.uppercase() in listOf("LEGENDARIO", "LEGENDARY")
            "Arcano/Universal" -> item.rarity.uppercase() in listOf("ARCANO", "UNIVERSAL")
            else -> true
        }

        val typeOk = when (type) {
            "Armas" -> item.type == "WEAPON"
            "Pechera" -> item.type == "ARMOR"
            "Escudo" -> item.type == "SHIELD"
            "Casco" -> item.type == "HELMET"
            "Alas" -> item.type == "WINGS"
            "Guantes" -> item.type == "GLOVES"
            "Botas" -> item.type == "BOOTS"
            "Anillos/Joyas" -> item.type in listOf("RING", "EARRING", "RELIC")
            "Pociones" -> item.type == "POTION"
            else -> true
        }

        val levelOk = when (level) {
            "Niv. 1-5" -> item.itemLevel in 1..5
            "Niv. 6-10" -> item.itemLevel in 6..10
            "Niv. 11-20" -> item.itemLevel in 11..20
            "Niv. 21+" -> item.itemLevel >= 21
            else -> true
        }

        searchOk && rarityOk && typeOk && levelOk
    }
}

@Composable
fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MedievalGold else Color.Black.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = if (isSelected) MedievalGold else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                SoundManager.playButtonClick()
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun InventoryFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedRarity: String,
    onSelectRarity: (String) -> Unit,
    selectedType: String,
    onSelectType: (String) -> Unit,
    selectedLevel: String,
    onSelectLevel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isAnyCategoryFilterActive = selectedRarity != "Todas" || selectedType != "Todos" || selectedLevel != "Todos"
    val isAnyFilterActive = isAnyCategoryFilterActive || searchQuery.isNotBlank()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MedievalCardBg.copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, MedievalGold.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Main Top Bar: Search Input + Toggle Expand + Clear Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Free text search field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, MedievalGold.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MedievalGold,
                            modifier = Modifier.size(14.dp)
                        )
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            textStyle = TextStyle(color = Color.White, fontSize = 11.sp),
                            singleLine = true,
                            cursorBrush = SolidColor(MedievalGold),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("inventory_search_input"),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        "Buscar objeto...",
                                        color = Color.White.copy(alpha = 0.4f),
                                        fontSize = 11.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar búsqueda",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable {
                                        SoundManager.playButtonClick()
                                        onSearchQueryChange("")
                                    }
                            )
                        }
                    }
                }

                // Expand/Collapse Button for detailed category filters
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isExpanded || isAnyCategoryFilterActive) MedievalGold.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.4f))
                        .border(
                            1.dp,
                            if (isAnyCategoryFilterActive) MedievalGold else Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            SoundManager.playButtonClick()
                            isExpanded = !isExpanded
                        }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = if (isAnyCategoryFilterActive) MedievalGold else Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isExpanded) "Filtros ▲" else "Filtros ▼",
                            color = if (isAnyCategoryFilterActive) MedievalGold else Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isAnyFilterActive) {
                    Text(
                        text = "Limpiar",
                        color = MedievalCrimson,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                SoundManager.playButtonClick()
                                onSearchQueryChange("")
                                onSelectRarity("Todas")
                                onSelectType("Todos")
                                onSelectLevel("Todos")
                            }
                            .padding(2.dp)
                    )
                }
            }

            // Collapsible Category Filters
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Row 1: Rareza
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Rareza:", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(42.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Todas", "Común", "Raro", "Épico", "Legendario", "Arcano/Universal").forEach { option ->
                                FilterPill(
                                    label = option,
                                    isSelected = selectedRarity == option,
                                    onClick = { onSelectRarity(option) }
                                )
                            }
                        }
                    }

                    // Row 2: Tipo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Tipo:", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(42.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Todos", "Armas", "Pechera", "Escudo", "Casco", "Alas", "Guantes", "Botas", "Anillos/Joyas", "Pociones").forEach { option ->
                                FilterPill(
                                    label = option,
                                    isSelected = selectedType == option,
                                    onClick = { onSelectType(option) }
                                )
                            }
                        }
                    }

                    // Row 3: Nivel
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Nivel:", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(42.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Todos", "Niv. 1-5", "Niv. 6-10", "Niv. 11-20", "Niv. 21+").forEach { option ->
                                FilterPill(
                                    label = option,
                                    isSelected = selectedLevel == option,
                                    onClick = { onSelectLevel(option) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- DYNAMIC INVENTORY & EQUIPMENT SCREEN ---
@Composable
fun InventoryScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val p = progress ?: return

    val rawInventory = GameJsonParser.listFromJson<Item>(p.inventoryJson).filter { it.type != "EMPTY" }

    var searchQuery by remember { mutableStateOf("") }
    var selectedRarityFilter by remember { mutableStateOf("Todas") }
    var selectedTypeFilter by remember { mutableStateOf("Todos") }
    var selectedLevelFilter by remember { mutableStateOf("Todos") }
    var showMassSellConfirmation by remember { mutableStateOf(false) }

    val filteredInventory = remember(rawInventory, searchQuery, selectedRarityFilter, selectedTypeFilter, selectedLevelFilter) {
        filterInventory(rawInventory, selectedRarityFilter, selectedTypeFilter, selectedLevelFilter, searchQuery)
    }

    val massSellTotalPrice = remember(filteredInventory) {
        filteredInventory.sumOf { viewModel.calculateSellPrice(it) }
    }

    val weapon = GameJsonParser.fromJson<Item>(p.equippedWeaponJson)
    val shield = GameJsonParser.fromJson<Item>(p.equippedShieldJson)
    val armor = GameJsonParser.fromJson<Item>(p.equippedArmorJson)
    val helmet = GameJsonParser.fromJson<Item>(p.equippedHelmetJson)
    val wings = GameJsonParser.fromJson<Item>(p.equippedWingsJson)
    val gloves = GameJsonParser.fromJson<Item>(p.equippedGlovesJson)
    val boots = GameJsonParser.fromJson<Item>(p.equippedBootsJson)
    val ring = GameJsonParser.fromJson<Item>(p.equippedRingJson)
    val earring = GameJsonParser.fromJson<Item>(p.equippedEarringJson)
    val relic = GameJsonParser.fromJson<Item>(p.equippedRelicJson)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Equipo Equipado (Estilo MU Online)",
                color = MedievalGold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Button(
                onClick = { viewModel.autoEquip() },
                colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(28.dp)
                    .testTag("auto_equip_button")
            ) {
                Text("Equipar Auto", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        // MU Online Equipment Matrix Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
            border = BorderStroke(1.5.dp, MedievalGold.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Row 1: Wings, Helmet, Relic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MuEquipmentSlot("Alas", "WINGS", wings) { viewModel.unequipItem("WINGS") }
                    MuEquipmentSlot("Casco", "HELMET", helmet) { viewModel.unequipItem("HELMET") }
                    MuEquipmentSlot("Reliquia", "RELIC", relic) { viewModel.unequipItem("RELIC") }
                }

                // Row 2: Weapon (Primary), Armor, Shield (Secondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MuEquipmentSlot("Arma", "WEAPON", weapon) { viewModel.unequipItem("WEAPON") }
                    MuEquipmentSlot("Pechera", "ARMOR", armor) { viewModel.unequipItem("ARMOR") }
                    MuEquipmentSlot("Escudo", "SHIELD", shield) { viewModel.unequipItem("SHIELD") }
                }

                // Row 3: Gloves, Boots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MuEquipmentSlot("Guantes", "GLOVES", gloves) { viewModel.unequipItem("GLOVES") }
                    MuEquipmentSlot("Botas", "BOOTS", boots) { viewModel.unequipItem("BOOTS") }
                }

                // Row 4: Ring, Earring
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MuEquipmentSlot("Anillo", "RING", ring) { viewModel.unequipItem("RING") }
                    MuEquipmentSlot("Pendientes", "EARRING", earring) { viewModel.unequipItem("EARRING") }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredInventory.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        showMassSellConfirmation = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalCrimson),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("mass_sell_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(13.dp))
                        Text("Venta Masiva ($massSellTotalPrice 🪙)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        InventoryFilterBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            selectedRarity = selectedRarityFilter,
            onSelectRarity = { selectedRarityFilter = it },
            selectedType = selectedTypeFilter,
            onSelectType = { selectedTypeFilter = it },
            selectedLevel = selectedLevelFilter,
            onSelectLevel = { selectedLevelFilter = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .border(1.dp, MedievalGold.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(6.dp)
        ) {
            if (filteredInventory.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Inbox,
                        contentDescription = "Mochila Vacía",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        if (rawInventory.isEmpty()) "Tu mochila está vacía." else "No hay objetos que coincidan con los filtros.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    if (selectedRarityFilter != "Todas" || selectedTypeFilter != "Todos" || selectedLevelFilter != "Todos") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                selectedRarityFilter = "Todas"
                                selectedTypeFilter = "Todos"
                                selectedLevelFilter = "Todos"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Restablecer Filtros", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredInventory) { item ->
                        var expandedDetail by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .border(
                                    1.5.dp,
                                    getRarityColor(item.rarity),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    SoundManager.playButtonClick()
                                    expandedDetail = true
                                }
                                .testTag("inv_item_${item.id}"),
                            colors = CardDefaults.cardColors(containerColor = MedievalCardBg)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = getItemImageRes(item.imageResName, item.type)),
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                // Item Level overlay badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(bottomEnd = 6.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("Niv.${item.itemLevel}", color = MedievalGold, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                                // Name overlay at the bottom
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        item.name,
                                        color = getRarityColor(item.rarity),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        if (expandedDetail) {
                            val canEquip = item.itemLevel <= p.charLevel
                            val itemSellPrice = viewModel.calculateSellPrice(item)

                            AlertDialog(
                                onDismissRequest = { expandedDetail = false },
                                containerColor = MedievalCardBg,
                                title = {
                                    Text(
                                        item.name,
                                        color = getRarityColor(item.rarity),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(item.description, color = Color.White, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Tipo: ${item.type}", color = MedievalGold, fontSize = 11.sp)
                                        Text("Rareza: ${item.rarity.uppercase()}", color = getRarityColor(item.rarity), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            "Nivel requerido: ${item.itemLevel}",
                                            color = if (canEquip) MedievalGold else MedievalCrimson,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        if (item.strBonus > 0) Text("Fuerza: +${item.strBonus}", color = Color.White, fontSize = 11.sp)
                                        if (item.dexBonus > 0) Text("Destreza: +${item.dexBonus}", color = Color.White, fontSize = 11.sp)
                                        if (item.intBonus > 0) Text("Inteligencia: +${item.intBonus}", color = Color.White, fontSize = 11.sp)
                                        if (item.conBonus > 0) Text("Constitución: +${item.conBonus}", color = Color.White, fontSize = 11.sp)
                                        if (item.dmgBonus > 0) Text("Daño: +${item.dmgBonus}", color = Color.White, fontSize = 11.sp)
                                        if (item.defBonus > 0) Text("Defensa: +${item.defBonus}", color = Color.White, fontSize = 11.sp)
                                    }
                                },
                                confirmButton = {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (item.type != "POTION") {
                                            Button(
                                                onClick = {
                                                    if (canEquip) {
                                                        viewModel.equipItem(item)
                                                        expandedDetail = false
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (canEquip) MedievalGold else Color.Gray
                                                ),
                                                shape = RoundedCornerShape(4.dp),
                                                enabled = canEquip,
                                                modifier = Modifier.testTag("equip_item_btn")
                                            ) {
                                                Text(
                                                    if (canEquip) "Equipar" else "Niv. Insuficiente",
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.sellItem(item)
                                                expandedDetail = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MedievalCrimson),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.testTag("sell_item_btn")
                                        ) {
                                            Text("Vender (+$itemSellPrice 🪙)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = {
                                            viewModel.discardItem(item)
                                            expandedDetail = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.testTag("discard_item_btn")
                                    ) {
                                        Text("Descartar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showMassSellConfirmation) {
            AlertDialog(
                onDismissRequest = { showMassSellConfirmation = false },
                containerColor = MedievalCardBg,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MedievalGold, modifier = Modifier.size(20.dp))
                        Text("⚡ Venta Masiva de Mochila", color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "Vas a vender ${filteredInventory.size} objetos filtrados por un total de $massSellTotalPrice 🪙 monedas de oro.",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Filtros aplicados:", color = MedievalGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        if (searchQuery.isNotBlank()) Text("• Búsqueda: \"$searchQuery\"", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                        Text("• Rareza: $selectedRarityFilter", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                        Text("• Tipo: $selectedTypeFilter", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                        Text("• Nivel: $selectedLevelFilter", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.massSellItems(filteredInventory)
                            showMassSellConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedievalCrimson),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Vender Todo ($massSellTotalPrice 🪙)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showMassSellConfirmation = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Cancelar", color = Color.White, fontSize = 11.sp)
                    }
                }
            )
        }
    }
}

// --- GAME GUIDE / HELP SCREEN ---
@Composable
fun HelpGuideScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MedievalCardBg),
            border = BorderStroke(1.5.dp, MedievalGold)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Manual de Aventurero de Eldoria",
                    color = MedievalGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                val guideLines = listOf(
                    "🗺️ Exploración: Viaja a casillas adyacentes de tu posición en el mapa. Cada casilla tiene desafíos y monstruos escalados por nivel de distancia.",
                    "⚔️ Combate: Enfréntate a enemigos y jefes legendarios en un sistema táctico por turnos utilizando tus habilidades de clase y pociones.",
                    "🎖️ Progresión: Al subir de nivel ganas +5 puntos de atributo y +1 punto de talento.",
                    "🧬 Árbol de Talentos: Invierte tus puntos de talento en cualquiera de las tres ramas profundas (Fuerza, Magia o Evasión) para especializarte.",
                    "📦 Inventario Dinámico: Equípate armas, armaduras, escudos y anillos procedurales de hasta nivel de rareza Leyendario.",
                    "☠️ Derrota: Si cae en batalla, resucitas en el santuario inicial con una penalización del 15% de tu oro acumulado."
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(280.dp)
                ) {
                    items(guideLines) { line ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(line, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = MedievalGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("back_from_help_btn")
                ) {
                    Text("Entendido, Continuar Aventuras", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Kotlin helper classes for data structures
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

fun getEnemyPortraitRes(name: String, isBoss: Boolean): Int {
    val cleanName = name.lowercase()
    return when {
        // Specific bosses
        cleanName.contains("hobgoblin") -> R.drawable.img_boss_hobgoblin_1784674116743
        cleanName.contains("vampiro") -> R.drawable.img_boss_high_vampire_1784674139269
        cleanName.contains("igdrasil") || cleanName.contains("máquinas") || cleanName.contains("yggdrasil") -> R.drawable.img_boss_yggdrasil_machine_1784674150126
        cleanName.contains("dragon oscuro") || cleanName.contains("dragón oscuro") || cleanName.contains("calamidad") -> R.drawable.img_boss_dark_dragon_1784674128719
        
        // Dragons, Wyrms & Wyverns
        cleanName.contains("dragón") || cleanName.contains("dragon") || cleanName.contains("wyrm") || 
        cleanName.contains("wyvern") || cleanName.contains("drake") || cleanName.contains("drakoniano") || cleanName.contains("hidra") -> R.drawable.enemy_dragon_1784850948333

        // Goblins
        cleanName.contains("goblin") || cleanName.contains("duende") -> R.drawable.enemy_goblin_1784850794614

        // Wolves, Canines & Beasts
        cleanName.contains("lobo") || cleanName.contains("fenrir") || cleanName.contains("warg") || 
        cleanName.contains("chacal") || cleanName.contains("licántropo") || cleanName.contains("perro") || cleanName.contains("alfa") -> R.drawable.enemy_wolf_1784850801847

        // Ghosts, Spectres & Astral Entities
        cleanName.contains("espectro") || cleanName.contains("alma") || cleanName.contains("sombra") || 
        cleanName.contains("poltergeist") || cleanName.contains("orbe") || cleanName.contains("lamento") || cleanName.contains("fantas") -> R.drawable.enemy_spectre_1784850809472

        // Treants & Nature Creatures
        cleanName.contains("treant") || cleanName.contains("árbol") || cleanName.contains("planta") || cleanName.contains("bosque") -> R.drawable.enemy_treant_1784850817186

        // Bandits, Rogues & Human Mercenaries
        cleanName.contains("bandido") || cleanName.contains("ladrón") || cleanName.contains("ladron") || cleanName.contains("asesino") || 
        cleanName.contains("mercenario") || cleanName.contains("matón") || cleanName.contains("capitán") || cleanName.contains("pirata") || 
        cleanName.contains("infiltrador") || cleanName.contains("verdugo") || cleanName.contains("ballestero") || cleanName.contains("envenenador") -> R.drawable.enemy_bandit_1784850826788

        // Elementals & Fire Creatures
        cleanName.contains("elemental") || cleanName.contains("fuego") || cleanName.contains("magma") || 
        cleanName.contains("llama") || cleanName.contains("azufre") || cleanName.contains("ceniza") || cleanName.contains("pyros") || 
        cleanName.contains("ignis") || cleanName.contains("fatuo") -> R.drawable.enemy_elemental_1784850835033

        // Cultists
        cleanName.contains("cultista") -> R.drawable.enemy_cultist_1784850844974

        // Yetis, Ice & Snow Creatures
        cleanName.contains("yeti") || cleanName.contains("glacial") || cleanName.contains("escarcha") || 
        cleanName.contains("ventisquero") || cleanName.contains("glacius") || cleanName.contains("freya") || cleanName.contains("tundra") -> R.drawable.enemy_yeti_1784850855217

        // Undead, Zombies, Skeletons & Ghouls
        cleanName.contains("zombi") || cleanName.contains("ghoul") || cleanName.contains("necrófago") || 
        cleanName.contains("peste") || cleanName.contains("esqueleto") || cleanName.contains("muerte") || 
        cleanName.contains("no-muerto") || cleanName.contains("hueso") -> R.drawable.enemy_zombie_1784850868957

        // Witches, Sorceresses & Mages
        cleanName.contains("bruja") || cleanName.contains("mago") || cleanName.contains("ilusionista") || 
        cleanName.contains("hechicero") || cleanName.contains("arcan") || cleanName.contains("chamán") || cleanName.contains("chaman") -> R.drawable.enemy_witch_1784850877826

        // Liches & Necromancers
        cleanName.contains("lich") || cleanName.contains("necromancer") || cleanName.contains("necromante") || cleanName.contains("filacteria") -> R.drawable.enemy_lich_1784850885522

        // Anubis, Pharaohs & Solar Egyptian Guardians
        cleanName.contains("anubis") || cleanName.contains("esfinge") || cleanName.contains("solaria") || 
        cleanName.contains("ra-horakhty") || cleanName.contains("sacerdote solar") -> R.drawable.enemy_anubis_1784850895657

        // Mummies
        cleanName.contains("momia") -> R.drawable.enemy_mummy_1784850903429

        // Archangels & Celestial Beings
        cleanName.contains("archángel") || cleanName.contains("arcángel") || cleanName.contains("seraphiel") || 
        cleanName.contains("celestial") || cleanName.contains("astral") || cleanName.contains("sentinela") || 
        cleanName.contains("aetherion") || cleanName.contains("firmamento") -> R.drawable.enemy_archangel_1784850912318

        // Orcs & Ogres
        cleanName.contains("orco") || cleanName.contains("ogro") || cleanName.contains("berserker") || 
        cleanName.contains("demoledor") || cleanName.contains("gladiador") || cleanName.contains("warlord") -> R.drawable.enemy_orc_1784850920168

        // Naga, Tritons & Sea Ocean Monsters
        cleanName.contains("naga") || cleanName.contains("sireno") || cleanName.contains("tritón") || 
        cleanName.contains("neptuno") || cleanName.contains("océano") || cleanName.contains("oceano") || 
        cleanName.contains("coral") || cleanName.contains("mareas") || cleanName.contains("leviatán") || cleanName.contains("fosas") -> R.drawable.enemy_naga_1784850928739

        // Automatons, Robots & Machines
        cleanName.contains("autómata") || cleanName.contains("automata") || cleanName.contains("engranaje") || 
        cleanName.contains("célula") || cleanName.contains("dron") || cleanName.contains("láser") || cleanName.contains("titanio") || 
        cleanName.contains("coloso") || cleanName.contains("plasma") || cleanName.contains("ejecutor") || cleanName.contains("sintétic") -> R.drawable.enemy_automaton_1784850938702

        // Basilisks, Snakes & Serpents
        cleanName.contains("basilisco") || cleanName.contains("cobra") || cleanName.contains("víbora") || 
        cleanName.contains("vibora") || cleanName.contains("gorgona") || cleanName.contains("cascabel") || 
        cleanName.contains("anaconda") || cleanName.contains("serpiente") || cleanName.contains("salamandra") || cleanName.contains("viperino") -> R.drawable.enemy_basilisk_1784850958621

        // Scorpions & Desert Monsters
        cleanName.contains("escorpión") || cleanName.contains("escorpion") || cleanName.contains("manta") -> R.drawable.enemy_scorpion_1784850968611

        // Spiders
        cleanName.contains("araña") || cleanName.contains("tarántula") -> R.drawable.img_enemy_spider_1784386956688

        // Golems & Mud Slimes
        cleanName.contains("golem") || cleanName.contains("gólem") || cleanName.contains("fango") || cleanName.contains("lodo") || cleanName.contains("ciénaga") -> R.drawable.img_enemy_mud_golem_1784386930907

        // Boss fallback vs Normal fallback
        isBoss -> R.drawable.img_enemy_boss_1784386985144
        else -> R.drawable.img_enemy_ogre_1784386944311
    }
}

// --- DUNGEON SCREEN ---
@Composable
fun DungeonScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val p = progress ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F14))
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_talents_bg_1784603912942),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.35f
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A1508), Color(0xFF140B04), Color(0xFF090402))
                            )
                        )
                        .border(
                            BorderStroke(2.dp, Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFF8B6508)))),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_dungeon_door_1784674104372),
                                contentDescription = "Dungeon Door",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "🏛️ CALABOZOS DE ELDORIA",
                                color = MedievalGold,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "10 Calabozos Ancestrales • 9 Subjefes • 1 Jefe Final • Tesoros Únicos Inmortales",
                            color = Color(0xFFFFD54F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF330B0B))
                                .border(1.dp, Color(0xFFE53935), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "⚠️ VIDA PERSISTENTE: Tu salud y maná NO se regeneran entre combates. Permaneces con la vida restante tras derrotar a cada subjefe.",
                                color = Color(0xFFFFCDD2),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            items(com.example.data.DUNGEONS_LIST) { dungeon ->
                val isUnlocked = dungeon.id <= p.highestUnlockedDungeon
                val hasLevelReq = p.charLevel >= dungeon.levelReq
                val isAvailable = isUnlocked && hasLevelReq

                val completedList = com.example.data.GameJsonParser.listFromJson<Int>(p.completedDungeonsJson)
                val isCompleted = completedList.contains(dungeon.id)

                val cardBorderBrush = if (isCompleted) {
                    Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFF1B5E20)))
                } else if (isAvailable) {
                    Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFB8860B)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFF555555), Color(0xFF222222)))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1B1E28), Color(0xFF11131B), Color(0xFF0A0C11))
                            )
                        )
                        .border(BorderStroke(1.5.dp, cardBorderBrush), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CALABOZO ${dungeon.id}: ${dungeon.species.uppercase()}",
                                    color = if (isAvailable) MedievalGold else Color.Gray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = dungeon.name,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (hasLevelReq) Color(0xFF1B3B22) else Color(0xFF4A1212))
                                    .border(1.dp, if (hasLevelReq) Color(0xFF4CAF50) else Color(0xFFE53935), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Nivel ${dungeon.levelReq}+",
                                    color = if (hasLevelReq) Color(0xFFA5D6A7) else Color(0xFFFFCDD2),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        val imageRes = getEnemyPortraitRes(dungeon.finalBossName, true)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF3A424C), RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = dungeon.finalBossName,
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop,
                                alpha = if (isAvailable) 0.9f else 0.4f
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "👑 JEFE FINAL: ${dungeon.finalBossName}",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = dungeon.finalBossTitle,
                                    color = Color.LightGray,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF14101A))
                                .border(1.dp, Color(0xFFBA68C8), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🏆", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = dungeon.uniqueTreasure.name,
                                            color = Color(0xFFE1BEE7),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "[${dungeon.uniqueTreasure.rarity}]",
                                            color = MedievalGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Text(
                                        text = dungeon.uniqueTreasure.description,
                                        color = Color.Gray,
                                        fontSize = 9.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }

                        Text(
                            text = "⚔️ 9 Subjefes: ${dungeon.subBosses.take(3).joinToString(", ")} ... y 6 más.",
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )

                        if (!isUnlocked) {
                            Button(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF222222))
                            ) {
                                Text("🔒 BLOQUEADO (Completa Calabozo ${dungeon.id - 1})", color = Color.Gray, fontSize = 11.sp)
                            }
                        } else if (!hasLevelReq) {
                            Button(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF331515))
                            ) {
                                Text("🔒 REQUIERE NIVEL ${dungeon.levelReq}", color = Color(0xFFEF9A9A), fontSize = 11.sp)
                            }
                        } else {
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    viewModel.startDungeonRun(dungeon.id)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCompleted) Color(0xFF2E7D32) else MedievalGold
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_dungeon_${dungeon.id}_button")
                            ) {
                                Text(
                                    text = if (isCompleted) "✨ DESAFIAR DE NUEVO (Calabozo Conquistado)" else "⚔️ ENTRAR AL CALABOZO (Etapa 1/10)",
                                    color = if (isCompleted) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

