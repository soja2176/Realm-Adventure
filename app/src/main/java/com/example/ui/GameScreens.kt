package com.example.ui

import com.example.audio.SoundManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.eldoria.ui.components.MedievalStatBar
import com.example.eldoria.ui.components.OrnateDivider
import com.example.data.*
import com.example.data.engine.EldoriaPassives
// ─── Cascarón Eldoria (com.example.ui.shell) ───
import com.example.ui.shell.EldoriaTopHud
import com.example.ui.shell.EldoriaBottomNav
import com.example.ui.shell.EldoriaMoreSheet
import com.example.ui.shell.EldoriaToastHost
import com.example.ui.shell.EldoriaNoticeDialog
// ─── Pantallas nuevas (com.example.ui.screens) ───
// Imports explícitos a propósito: este fichero declara símbolos top-level propios
// (getRarityColor, MedievalGold, CombatScreen…) y un import comodín podría colisionar.
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ExpeditionHubScreen
import com.example.ui.screens.ExpeditionRunScreen
import com.example.ui.screens.ExpeditionCombatScreen
import com.example.ui.screens.PetSanctuaryScreen
import com.example.ui.screens.MinigameHubScreen
import com.example.ui.screens.BestiaryScreen
import com.example.ui.screens.ContractsScreen
import com.example.ui.screens.WorldAtlasSheet
import com.example.ui.minigames.MinigameHostScreen
import com.example.ui.talents.EldoriaTalentTreeScreen
// ─── Sistema de diseño Eldoria (com.example.ui.design) ───
// Imports explícitos: este fichero define símbolos propios y un comodín colisionaría.
import com.example.ui.art.EldoriaArt
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBanner
import com.example.ui.design.EldoriaBarTone
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.CombatFx
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaCombatFlash
import com.example.ui.design.EldoriaConfirmDialog
import com.example.ui.design.EldoriaSkillFx
import com.example.ui.design.accent
import com.example.ui.design.combatFxForEnemyArchetype
import com.example.ui.design.combatFxForSkill
import com.example.ui.design.EldoriaCounter
import com.example.ui.design.EldoriaCrackedStone
import com.example.ui.design.EldoriaCrest
import com.example.ui.design.EldoriaScrollSheet
import com.example.ui.design.EldoriaDamageFloater
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmberField
import com.example.data.content.EldoriaPotions
import com.example.ui.combat.PotionStack
import com.example.ui.combat.CombatPotionDrawer
import com.example.ui.combat.CombatBuffChips
import com.example.ui.combat.basicAttackFor
import com.example.ui.design.EldoriaEmptyState
import com.example.ui.design.EldoriaRevealImage
import com.example.ui.design.EldoriaFogLayer
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaImpactBurst
import com.example.ui.design.EldoriaItemCard
import com.example.ui.design.EldoriaKeyValueRow
import com.example.ui.design.EldoriaMotion
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaProgressRing
import com.example.ui.design.EldoriaRarityGem
import com.example.ui.design.EldoriaResourceBar
import com.example.ui.design.EldoriaRuneGlyph
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaSegmentedTabs
import com.example.ui.design.EldoriaSlotFrame
import com.example.ui.design.EldoriaStatPill
import com.example.ui.design.EldoriaSwap
import com.example.ui.design.EldoriaToggleChip
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaTorchLight
import com.example.ui.design.EldoriaType
import com.example.ui.design.EldoriaVignette
import com.example.ui.design.eldoriaDiamondPath
import com.example.ui.design.eldoriaFloat
import com.example.ui.design.eldoriaGlowLayer
import com.example.ui.design.eldoriaPressable
import com.example.ui.design.eldoriaPulse
import com.example.ui.design.eldoriaShake
import androidx.activity.compose.BackHandler
import kotlin.math.abs

// Style Palette Constants
val MedievalDarkBg = Color(0xFF0F111A)
val MedievalCardBg = Color(0xFF121620)
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
    // El índice generado manda: cualquier lámina nueva funciona con sólo
    // existir, sin tocar la tabla de abajo. Ésta se conserva porque traduce los
    // alias cortos ("img_mat_cuero") a ficheros con sufijo de fecha, y porque
    // reparte por afinidad los materiales que aún no tienen arte propio.
    EldoriaArt.of(imageResName)?.let { return it }
    EldoriaArt.of("mat_" + imageResName)?.let { return it }

    return when (imageResName) {
        "img_mat_cuero" -> R.drawable.img_mat_cuero_1784901594849
        "img_mat_hierro" -> R.drawable.img_mat_hierro_1784901606157
        "img_mat_oro" -> R.drawable.img_mat_oro_1784901617574
        "img_mat_platino" -> R.drawable.img_mat_platino_1784901629448
        "img_mat_dragondskin" -> R.drawable.img_mat_dragondskin_1784901640557
        "img_mat_diamond_inf" -> R.drawable.img_mat_diamond_inf_1784901652591
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
        "img_pet_fenix_cosmico" -> R.drawable.img_pet_fenix_cosmico_1785007631115
        "img_pet_dragon_sombras" -> R.drawable.img_pet_dragon_sombras_1785007642225
        "img_pet_lobo_celestial" -> R.drawable.img_pet_lobo_celestial_1785007652368
        "img_pet_gato_estelar" -> R.drawable.img_pet_gato_estelar_1785007661828
        "img_pet_titan_cristal" -> R.drawable.img_pet_titan_cristal_1785007671322
        "img_pet_grifo_dorado" -> R.drawable.img_pet_grifo_dorado_1785007680820
        "img_pet_serpiente_astral" -> R.drawable.img_pet_serpiente_astral_1785007692823
        "img_pet_behemoth_vacio" -> R.drawable.img_pet_behemoth_vacio_1785007703732
        "img_food_bestial" -> R.drawable.img_food_bestial_1785008135868
        "img_food_mistica" -> R.drawable.img_food_mistica_1785008148513
        "img_food_dragon" -> R.drawable.img_food_dragon_1785008159001
        "img_food_celestial" -> R.drawable.img_food_celestial_1785008169473

        // ─── Materiales del catálogo EldoriaMaterials (ids crudos) ───
        // Sólo existen 6 JPG de material; los 15 ids se reparten entre ellos
        // por afinidad visual (metal, cuero, oro, plata/acero, escama, cristal).
        "iron" -> R.drawable.img_mat_hierro_1784901606157
        "leather" -> R.drawable.img_mat_cuero_1784901594849
        "wood" -> R.drawable.img_mat_cuero_1784901594849
        "herbs" -> R.drawable.img_mat_cuero_1784901594849
        "steel" -> R.drawable.img_mat_platino_1784901629448
        "crystal" -> R.drawable.img_mat_diamond_inf_1784901652591
        "mystic_silk" -> R.drawable.img_mat_cuero_1784901594849
        "gold_ore" -> R.drawable.img_mat_oro_1784901617574
        "blood_gem" -> R.drawable.img_mat_diamond_inf_1784901652591
        "dragon_scale" -> R.drawable.img_mat_dragondskin_1784901640557
        "shadow_essence" -> R.drawable.img_mat_dragondskin_1784901640557
        "phoenix_feather" -> R.drawable.img_mat_oro_1784901617574
        "pure_crystal" -> R.drawable.img_mat_diamond_inf_1784901652591
        "ancient_relic" -> R.drawable.img_item_relic_1784658251007
        "infinite_diamond" -> R.drawable.img_mat_diamond_inf_1784901652591
        "forge_ember" -> R.drawable.img_mat_hierro_1784901606157
        "anima_shard" -> R.drawable.img_mat_diamond_inf_1784901652591
        "sealed_key" -> R.drawable.img_mat_platino_1784901629448

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
                // Una mascota sin arte reconocida ya no cae en la reliquia.
                "PET" -> R.drawable.img_pet_lobo_celestial_1785007652368
                "FOOD" -> R.drawable.img_food_bestial_1785008135868
                "MATERIAL" -> R.drawable.img_mat_hierro_1784901606157
                "SHIELD" -> R.drawable.img_item_shield_1784593608106
                else -> R.drawable.img_item_potion_1784593618142
            }
        }
    }
}

fun getCharacterPortrait(race: String, cls: String, hasAdvancedClass: Boolean = false, charLevel: Int = 1): Int {
    val c = cls.trim()
    val r = race.trim()

    // 3-Stage Evolution Portraits (Lvl 20, Lvl 50, Lvl 100)
    when {
        charLevel >= 100 -> {
            return when (c) {
                "Guerrero" -> R.drawable.img_evo_guerrero_3_1784901472272
                "Mago" -> R.drawable.img_evo_mago_3_1784901503996
                "Pícaro" -> R.drawable.img_evo_picaro_3_1784901545572
                else -> R.drawable.img_evo_clerigo_3_1784901581001
            }
        }
        charLevel >= 50 -> {
            return when (c) {
                "Guerrero" -> R.drawable.img_evo_guerrero_2_1784901460545
                "Mago" -> R.drawable.img_evo_mago_2_1784901492327
                "Pícaro" -> R.drawable.img_evo_picaro_2_1784901529027
                else -> R.drawable.img_evo_clerigo_2_1784901568583
            }
        }
        charLevel >= 20 -> {
            return when (c) {
                "Guerrero" -> R.drawable.img_evo_guerrero_1_1784901448962
                "Mago" -> R.drawable.img_evo_mago_1_1784901481413
                "Pícaro" -> R.drawable.img_evo_picaro_1_1784901516704
                else -> R.drawable.img_evo_clerigo_1_1784901558922
            }
        }
    }

    if (hasAdvancedClass) {
        return when (c) {
            "Guerrero" -> R.drawable.img_hero_advanced_guerrero_1784856127764
            "Mago" -> R.drawable.img_hero_advanced_mago_1784856138389
            "Pícaro" -> R.drawable.img_hero_advanced_picaro_1784856148296
            else -> R.drawable.img_hero_advanced_clerigo_1784856159204
        }
    }

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

@Composable
fun ClassAdvancementCutsceneModal(
    cls: String,
    onDismiss: () -> Unit
) {
    val cutsceneRes = when (cls.trim()) {
        "Guerrero" -> R.drawable.cutscene_warrior_1784895909697
        "Mago" -> R.drawable.cutscene_mage_1784895923135
        "Pícaro" -> R.drawable.cutscene_rogue_1784895933260
        else -> R.drawable.cutscene_cleric_1784895944730
    }

    val title = when (cls.trim()) {
        "Guerrero" -> "✨ SEÑOR DE LA GUERRA ALADO ✨"
        "Mago" -> "✨ ARCHIMAGO CÓSMICO ✨"
        "Pícaro" -> "✨ SOMBRA CELESTE ✨"
        else -> "✨ SERAFÍN SAGRADO ✨"
    }

    val storyText = when (cls.trim()) {
        "Guerrero" -> "¡El fuego sagrado de la guerra enciende tus alas doradas! Alcanzas la cima del combate marcial: tus atributos se fortalecen al doble (x2) y tus alas cortan los cielos desencadenando furia atronadora."
        "Mago" -> "¡El éter estelar del cosmos se adhiere a tus alas astrales! La sabiduría ancestral duplica tu poder mágico (x2) e invocas tempestades de fuego divino que devoran ejércitos enteros."
        "Pícaro" -> "¡Las sombras de la noche infinita forjan tus alas de misterio! Te desplazas como un relámpago entre penumbras, duplicando tus atributos (x2) y envenenando el alma de tus enemigos."
        else -> "¡La luz sagrada de los dioses te otorga alas de ángel celestial! Duplicas toda tu vitalidad y fe (x2), proyectando rayos de justicia serafín que curan aliados y destruyen la oscuridad."
    }

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
            // Motas doradas subiendo: la ascensión ocurre, no se anuncia.
            EldoriaEmberField(
                modifier = Modifier.fillMaxSize(),
                count = 34,
                tint = Eldoria.GoldBright,
                periodMs = 8000,
                seed = cls.hashCode(),
                maxAlpha = 0.55f
            )
            EldoriaVignette(
                modifier = Modifier.fillMaxSize(),
                strength = 0.75f,
                tint = Eldoria.Abyss,
                centerBiasY = 0.42f
            )

            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .verticalScroll(rememberScrollState()),
                edge = EldoriaEdge.Gold,
                corner = Eldoria.R20,
                padding = PaddingValues(18.dp),
                glow = true,
                filigree = true
            ) {
                Text(
                    text = "ASCENSIÓN",
                    style = EldoriaType.label,
                    color = Eldoria.TextGold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Eldoria.S12))

                EldoriaFrame(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(212.dp),
                    edge = EldoriaEdge.Gold,
                    corner = Eldoria.R16,
                    strokeWidth = Eldoria.StrokeBold,
                    filigree = true,
                    rivets = true,
                    glowPulse = true
                ) {
                    Image(
                        painter = painterResource(id = cutsceneRes),
                        contentDescription = "Cinemática Alada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Eldoria.Abyss.copy(alpha = 0.88f))
                                )
                            )
                    )
                    Text(
                        text = title.replace("✨", "").trim(),
                        style = EldoriaType.display,
                        color = Eldoria.GoldBright,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(Eldoria.S16))

                // El relato va en pergamino: es el momento narrativo del juego.
                EldoriaScrollSheet(padding = PaddingValues(16.dp)) {
                    Text(
                        text = storyText,
                        style = EldoriaType.lore,
                        color = Eldoria.ParchmentInk
                    )
                }

                Spacer(modifier = Modifier.height(Eldoria.S12))

                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = EldoriaEdge.Arcane,
                    corner = Eldoria.R8,
                    padding = PaddingValues(12.dp),
                    background = Eldoria.sunkenBrush()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Eldoria.GoldBright, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.width(Eldoria.S8))
                        Text(
                            "Atributos duplicados: STR, DEX, INT y CON ×2",
                            style = EldoriaType.small,
                            color = Eldoria.VitaeBright
                        )
                    }
                    Spacer(modifier = Modifier.height(Eldoria.S6))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Eldoria.GoldBright, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.width(Eldoria.S8))
                        Text(
                            "Habilidad definitiva desbloqueada: daño ×5",
                            style = EldoriaType.small,
                            color = Eldoria.TextGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Eldoria.S16))

                EldoriaButton(
                    text = "ASCENDER",
                    onClick = onDismiss,
                    tone = EldoriaTone.Gold,
                    size = EldoriaButtonSize.Large,
                    icon = Icons.Default.AutoAwesome,
                    fullWidth = true,
                    testTag = "class_ascend_btn"
                )
            }
        }
    }
}

@Composable
fun EldoriaMainContainer(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val screen by viewModel.screenState.collectAsState()
    val notification by viewModel.notification.collectAsState()
    val classCutscene by viewModel.showClassAdvancementCutscene.collectAsState()

    // Estado de los sistemas nuevos (avisos flotantes, minijuego activo) y del combate.
    val toast by viewModel.systems.toast.collectAsState()
    val minigame by viewModel.systems.minigame.collectAsState()
    val combat by viewModel.combatState.collectAsState()
    var moreOpen by remember { mutableStateOf(false) }

    val p = progress

    // Pantallas a sangre: se dibujan solas, sin HUD superior ni barra inferior.
    // El combate se decide con `combat.inExpedition`, no con la expedición: al morir,
    // la run se cierra en la misma recomposición y el estilo saltaría de golpe.
    val chromeless = screen == GameScreen.CREATING_CHARACTER ||
        screen == GameScreen.MAIN_MENU ||
        screen == GameScreen.SETTINGS ||
        screen == GameScreen.EXPEDITION ||
        (screen == GameScreen.COMBAT && combat.inExpedition)

    // ─── Botón/gesto ATRÁS del sistema ───
    // Sin él, Android termina la Activity desde cualquier pantalla (y el creador de
    // personaje se convertía en una trampa sin salida).
    BackHandler(
        enabled = minigame != null ||
            (screen != GameScreen.MAIN_MENU && screen != GameScreen.WORLD_MAP)
    ) {
        when {
            minigame != null ->
                viewModel.systems.showToast("🎲 Cierra el minijuego con su propia X.", "IRON")
            screen == GameScreen.CREATING_CHARACTER || screen == GameScreen.SETTINGS ->
                viewModel.changeScreen(GameScreen.MAIN_MENU)
            screen == GameScreen.COMBAT ->
                viewModel.systems.showToast("⚔️ Termina el combate antes de retirarte.", "IRON")
            screen == GameScreen.EXPEDITION ->
                viewModel.systems.showToast("🕯️ El descenso se cierra desde sus propios botones.", "EMBER")
            else -> viewModel.changeScreen(GameScreen.WORLD_MAP)
        }
    }

    // Raíz de la pantalla: dentro va el Scaffold (cromo + contenido) y, por encima
    // de todo él, el minijuego a sangre.
    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Eldoria.Abyss),
        topBar = {
            if (p != null && p.hasActiveChar && !chromeless) {
                val autoNavActive by viewModel.isAutoNavigation.collectAsState()
                Column {
                    EldoriaTopHud(
                        progress = p,
                        onHelp = { viewModel.changeScreen(GameScreen.HELP_SCREEN) },
                        onMenu = { viewModel.changeScreen(GameScreen.MAIN_MENU) },
                        onSettings = { viewModel.changeScreen(GameScreen.SETTINGS) }
                    )
                    if (autoNavActive) {
                        // Cinta de marcha automática: late para que no se olvide
                        // que el héroe se está moviendo solo.
                        val marchPulse = eldoriaPulse(periodMs = 1400, from = 0.5f, to = 1f, label = "autoNav")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Eldoria.EmberDeep, Eldoria.EmberShadow)
                                    )
                                )
                                .border(Eldoria.StrokeThin, Eldoria.emberEdge(), RectangleShape)
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsWalk,
                                    contentDescription = "Autonavegación Activa",
                                    modifier = Modifier.size(16.dp),
                                    tint = Eldoria.EmberCore.copy(alpha = marchPulse)
                                )
                                Text(
                                    text = "MARCHA AUTOMÁTICA",
                                    style = EldoriaType.label,
                                    color = Eldoria.TextHi
                                )
                            }
                            EldoriaButton(
                                text = "DETENER",
                                onClick = { viewModel.toggleAutoNavigation() },
                                tone = EldoriaTone.Gold,
                                size = EldoriaButtonSize.Small,
                                testTag = "btn_stop_auto_nav"
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (p != null && p.hasActiveChar && !chromeless && screen != GameScreen.COMBAT) {
                EldoriaBottomNav(
                    current = screen,
                    onSelect = { viewModel.changeScreen(it) },
                    onMore = { moreOpen = true }
                )
            }
        },
        containerColor = Eldoria.Abyss
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ─── Router de pantallas ───
            // Cubre los 20 miembros de GameScreen sin rama `else`: si mañana se
            // añade un destino nuevo, el compilador obliga a enrutarlo aquí en
            // vez de dejar al jugador delante de una pantalla en negro.
            // MAIN_MENU se enruta SIEMPRE, también sin partida ni personaje.
            EldoriaSwap(screen) { s ->
                when (s) {
                    GameScreen.CREATING_CHARACTER -> CharacterCreatorScreen(viewModel)
                    GameScreen.WORLD_MAP -> WorldMapScreen(viewModel)
                    GameScreen.DUNGEON -> ExpeditionHubScreen(viewModel)
                    GameScreen.COMBAT ->
                        if (combat.inExpedition) ExpeditionCombatScreen(viewModel) else CombatScreen(viewModel)
                    GameScreen.CHARACTER_SCREEN -> CharacterScreen(viewModel)
                    GameScreen.TALENTS -> TalentsScreen(viewModel)
                    GameScreen.INVENTORY -> InventoryScreen(viewModel)
                    GameScreen.SHOP -> ShopScreen(viewModel)
                    GameScreen.PET_SCREEN -> PetScreen(viewModel)
                    GameScreen.HELP_SCREEN -> HelpGuideScreen(onBack = { viewModel.changeScreen(GameScreen.WORLD_MAP) })
                    GameScreen.ACHIEVEMENTS -> AchievementsScreen(viewModel)
                    GameScreen.CRAFTING -> CraftingScreen(viewModel)
                    GameScreen.DAILY_REWARDS -> DailyRewardsScreen(viewModel)
                    GameScreen.MAIN_MENU -> MainMenuScreen(viewModel)
                    GameScreen.SETTINGS -> SettingsScreen(viewModel)
                    GameScreen.EXPEDITION -> ExpeditionRunScreen(viewModel)
                    GameScreen.PET_SANCTUARY -> PetSanctuaryScreen(viewModel)
                    GameScreen.MINIGAMES -> MinigameHubScreen(viewModel)
                    GameScreen.BESTIARY -> BestiaryScreen(viewModel)
                    GameScreen.CONTRACTS -> ContractsScreen(viewModel)
                }
            }

            // ─── Superposiciones, de la más profunda a la más prioritaria ───

            // 1. Cinemática de ascensión de clase.
            classCutscene?.let { cls ->
                ClassAdvancementCutsceneModal(
                    cls = cls,
                    onDismiss = { viewModel.dismissClassAdvancementCutscene() }
                )
            }

            // 2. Hoja "Más opciones" de la barra inferior.
            EldoriaMoreSheet(
                visible = moreOpen,
                current = screen,
                onSelect = {
                    viewModel.changeScreen(it)
                    moreOpen = false
                },
                onDismiss = { moreOpen = false }
            )

            // 3. Avisos flotantes no bloqueantes.
            EldoriaToastHost(
                toast = toast,
                onDismiss = { viewModel.systems.dismissToast() }
            )

            // 4. Aviso modal centralizado (conserva testTag dismiss_notification_button).
            EldoriaNoticeDialog(
                message = notification,
                onDismiss = { viewModel.dismissNotification() }
            )
        }
    }

    // 5. Minijuego a sangre: hermano del Scaffold, NO de su contenido, para que
    // tape también el HUD superior y la barra inferior y les corte los toques.
    if (minigame != null) {
        MinigameHostScreen(viewModel)
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

    EldoriaScreen(
        depth = 1,
        embers = true,
        fog = true,
        vignetteStrength = 0.66f,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Eldoria.S12),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ELDORIA",
                    style = EldoriaType.displayXl,
                    color = Eldoria.TextGold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Forja al héroe que cruzará los seis reinos",
                    style = EldoriaType.lore,
                    color = Eldoria.TextMid,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Selected portrait display
        item {
            EldoriaFrame(
                modifier = Modifier.size(152.dp),
                edge = EldoriaEdge.Gold,
                corner = Eldoria.R16,
                strokeWidth = Eldoria.StrokeBold,
                filigree = true,
                rivets = true,
                glowPulse = true
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
                EldoriaSectionTitle(text = "NOMBRE", icon = Icons.Default.Badge, accent = Eldoria.Gold)
                Spacer(modifier = Modifier.height(Eldoria.S6))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(CutCornerShape(8.dp))
                        .background(Eldoria.PanelSunken)
                        .border(Eldoria.StrokeMed, Eldoria.goldEdge(), CutCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = name,
                        onValueChange = { viewModel.updateCreatorName(it) },
                        textStyle = EldoriaType.heading.copy(color = Eldoria.TextHi),
                        singleLine = true,
                        cursorBrush = SolidColor(Eldoria.Gold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("char_name_input"),
                        decorationBox = { innerTextField ->
                            if (name.isEmpty()) {
                                Text(
                                    "El nombre de tu héroe…",
                                    style = EldoriaType.body,
                                    color = Eldoria.TextLow
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }
        }

        // Race selector
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                EldoriaSectionTitle(text = "RAZA", icon = Icons.Default.People, accent = Eldoria.Gold)
                Spacer(modifier = Modifier.height(Eldoria.S6))
                val races = listOf("Humano", "Elfo", "Enano", "Orco")
                EldoriaSegmentedTabs(
                    options = races,
                    selectedIndex = races.indexOf(race).coerceAtLeast(0),
                    onSelect = { viewModel.selectRace(races[it]) },
                    accent = Eldoria.Gold,
                    testTagPrefix = "race"
                )
            }
        }

        // Class selector
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                EldoriaSectionTitle(text = "CLASE", icon = Icons.Default.Shield, accent = Eldoria.Gold)
                Spacer(modifier = Modifier.height(Eldoria.S6))
                val classes = listOf("Guerrero", "Mago", "Pícaro", "Clérigo")
                EldoriaSegmentedTabs(
                    options = classes,
                    selectedIndex = classes.indexOf(cls).coerceAtLeast(0),
                    onSelect = { viewModel.selectClass(classes[it]) },
                    accent = Eldoria.Gold,
                    testTagPrefix = "class"
                )
            }
        }

        // Race & Class Perks Informational Panel
        item {
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = EldoriaEdge.Silver,
                corner = Eldoria.R12,
                padding = PaddingValues(14.dp),
                filigree = true
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    EldoriaSectionTitle(
                        text = "RASGOS",
                        icon = Icons.Default.AutoAwesome,
                        accent = Eldoria.Silver
                    )
                    Spacer(modifier = Modifier.height(Eldoria.S12))

                    // Race Passive Perk info
                    Text(
                        text = race.uppercase(),
                        style = EldoriaType.label,
                        color = Eldoria.TextGold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = when (race) {
                            "Humano" -> "• Inicial: Determinación Humana (+10% Oro en batallas, +5% Probabilidad Crítica).\n• Evolución (Lvl 5+): Campeón Imperial (+15% Oro, regeneras un 8% de tu salud máxima cada turno)."
                            "Elfo" -> "• Inicial: Sentidos Élficos (+10% Maná Máximo, +5% Probabilidad Crítica).\n• Evolución (Lvl 5+): Guardián Astral (+15% Crítico, reduce el coste de maná de tus hechizos en 20%)."
                            "Enano" -> "• Inicial: Piel de Piedra (+10% Salud Máxima, +5 Defensa).\n• Evolución (Lvl 5+): Señor de las Runas (+15% Salud Máxima, +10 Defensa, devuelves 10% del daño recibido)."
                            "Orco" -> "• Inicial: Furia Berserker (+10% Daño infligido total).\n• Evolución (Lvl 5+): Devastador Berserker (+25% Daño infligido, te sanas un 12% del daño de tus ataques básicos)."
                            else -> ""
                        },
                        style = EldoriaType.small,
                        color = Eldoria.TextMid
                    )

                    Spacer(modifier = Modifier.height(Eldoria.S12))
                    EldoriaDivider(color = Eldoria.Silver.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(Eldoria.S12))

                    // Class Perk info
                    Text(
                        text = cls.uppercase(),
                        style = EldoriaType.label,
                        color = Eldoria.TextGold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = when (cls) {
                            "Guerrero" -> "• Rol: Combatiente cuerpo a cuerpo pesado.\n• Atributo principal: Fuerza (Física).\n• Habilidad: Golpe de Escudo / Corte de Batalla.\n• Maestría: Recibe 1 punto extra de defensa por nivel de Constitución."
                            "Mago" -> "• Rol: Hechicero destructor de largo alcance.\n• Atributo principal: Inteligencia (Mágica).\n• Habilidad: Descarga de Escarcha / Bola de Fuego.\n• Maestría: Convierte el 50% de tu Inteligencia en bono de daño de hechizo."
                            "Pícaro" -> "• Rol: Asesino rápido y sigiloso.\n• Atributo principal: Destreza (Física).\n• Habilidad: Puñalada / Golpe de Sombras.\n• Maestría: Aumenta la probabilidad de esquiva y crítico por cada punto de Destreza."
                            "Clérigo" -> "• Rol: Protector y sanador sagrado.\n• Atributo principal: Inteligencia / Fuerza.\n• Habilidad: Martillo Sagrado / Luz de Sanación.\n• Maestría: Combina salud máxima alta con curaciones potentes de bajo costo."
                            else -> ""
                        },
                        style = EldoriaType.small,
                        color = Eldoria.TextMid
                    )
                }
            }
        }

        // Stats Points Spender
        item {
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = if (pointsAvailable > 0) EldoriaEdge.Gold else EldoriaEdge.Silver,
                corner = Eldoria.R12,
                padding = PaddingValues(14.dp),
                glow = pointsAvailable > 0
            ) {
                EldoriaSectionTitle(
                    text = "ATRIBUTOS",
                    icon = Icons.Default.MilitaryTech,
                    accent = Eldoria.Gold,
                    trailing = {
                        if (pointsAvailable > 0) {
                            EldoriaButton(
                                text = "AUTO",
                                onClick = { viewModel.autoAllocateCreatorStats() },
                                tone = EldoriaTone.Gold,
                                size = EldoriaButtonSize.Small,
                                icon = Icons.Default.AutoMode,
                                testTag = "btn_auto_allocate_creator"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(Eldoria.S8))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CutCornerShape(6.dp))
                        .background(if (pointsAvailable > 0) Eldoria.GlowGold else Eldoria.PanelSunken)
                        .border(
                            Eldoria.StrokeThin,
                            if (pointsAvailable > 0) Eldoria.goldEdge() else Eldoria.ironEdge(),
                            CutCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (pointsAvailable > 0) "$pointsAvailable PUNTOS POR REPARTIR" else "TODO REPARTIDO",
                        style = EldoriaType.label,
                        color = if (pointsAvailable > 0) Eldoria.GoldBright else Eldoria.TextLow
                    )
                }

                Spacer(modifier = Modifier.height(Eldoria.S12))

                val stats = listOf(
                    Quadruple("STR", "Fuerza · daño físico", statStr, Eldoria.BloodBright),
                    Quadruple("DEX", "Destreza · crítico y esquiva", statDex, Eldoria.VitaeBright),
                    Quadruple("INT", "Inteligencia · hechizos y maná", statInt, Eldoria.ManaBright),
                    Quadruple("CON", "Constitución · salud", statCon, Eldoria.EmberCore)
                )

                stats.forEach { (code, desc, valCurrent, accent) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(code, style = EldoriaType.subheading, color = accent)
                            Text(
                                desc,
                                style = EldoriaType.caption,
                                color = Eldoria.TextLow,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                        ) {
                            CreatorStatButton(
                                icon = Icons.Default.Remove,
                                description = "Disminuir $code",
                                gold = false,
                                onClick = { viewModel.modifyStat(code, -1) },
                                testTag = "btn_minus_$code"
                            )
                            Text(
                                text = "$valCurrent",
                                style = EldoriaType.numericBig,
                                color = Eldoria.TextHi,
                                modifier = Modifier.width(38.dp),
                                textAlign = TextAlign.Center
                            )
                            CreatorStatButton(
                                icon = Icons.Default.Add,
                                description = "Aumentar $code",
                                gold = true,
                                onClick = { viewModel.modifyStat(code, 1) },
                                testTag = "btn_plus_$code"
                            )
                        }
                    }
                }
            }
        }

        // Play Button
        item {
            EldoriaButton(
                text = "INICIAR AVENTURA",
                onClick = { viewModel.submitCharacter() },
                tone = EldoriaTone.Gold,
                size = EldoriaButtonSize.Large,
                icon = Icons.Default.PlayArrow,
                fullWidth = true,
                testTag = "create_char_submit_button"
            )
        }

        // Salida del creador: sin esto la pantalla no tenía retorno y el Atrás del
        // sistema cerraba el juego.
        item {
            EldoriaButton(
                text = "VOLVER AL MENÚ",
                onClick = { viewModel.changeScreen(GameScreen.MAIN_MENU) },
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Medium,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                fullWidth = true,
                testTag = "create_char_back_button"
            )
        }
    }
    }
}

/** Botón redondo de +1/−1 del creador: oro para sumar, hierro para restar. */
@Composable
private fun CreatorStatButton(
    icon: ImageVector,
    description: String,
    gold: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (gold) Eldoria.goldEdge() else Eldoria.ironEdge())
            .eldoriaPressable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            description,
            tint = if (gold) Eldoria.TextOnGold else Eldoria.TextHi,
            modifier = Modifier.size(17.dp)
        )
    }
}
// --- WORLD MAP SCREEN (PROCEDURAL GENERATOR VIEW) ---
// ═══════════════════════════════════════════════════════════════════════════
//  EVENTOS ESPECIALES DEL MAPA
//  El castillo y el mercader ambulante son las dos paradas con nombre propio
//  del mundo: se presentan con blasón, pergamino y metal, no como un aviso.
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun CastleDialog(castleState: CastleState, viewModel: GameViewModel) {
    if (!castleState.active) return

    Dialog(onDismissRequest = { viewModel.closeCastleDialog() }) {
        EldoriaPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            edge = EldoriaEdge.Gold,
            corner = Eldoria.R16,
            padding = PaddingValues(14.dp),
            glow = true,
            filigree = true
        ) {
            // Blasón del reino: generado a partir del nombre, siempre el mismo
            // escudo para el mismo castillo.
            Row(verticalAlignment = Alignment.CenterVertically) {
                EldoriaFrame(
                    modifier = Modifier.size(width = 52.dp, height = 62.dp),
                    edge = EldoriaEdge.Gold,
                    corner = Eldoria.R8,
                    strokeWidth = Eldoria.StrokeMed,
                    filigree = false,
                    rivets = true
                ) {
                    EldoriaCrest(
                        seed = castleState.castleName.hashCode(),
                        modifier = Modifier
                            .matchParentSize()
                            .padding(5.dp),
                        primary = Eldoria.GoldBright,
                        secondary = Eldoria.IronDeep,
                        ornate = true
                    )
                }
                Spacer(modifier = Modifier.width(Eldoria.S12))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = castleState.castleName,
                        style = EldoriaType.title,
                        color = Eldoria.TextGold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = castleState.kingdomName,
                        style = EldoriaType.caption,
                        color = Eldoria.TextMid,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(Eldoria.S12))

            // La palabra de la corona va en pergamino: es un decreto, no un tooltip.
            EldoriaScrollSheet(padding = PaddingValues(14.dp)) {
                Text(
                    text = castleState.description,
                    style = EldoriaType.lore,
                    color = Eldoria.ParchmentInk
                )
            }

            Spacer(modifier = Modifier.height(Eldoria.S12))

            EldoriaButton(
                text = if (castleState.blessingClaimed) "BENDICIÓN YA CONCEDIDA" else "SOLICITAR BENDICIÓN REAL",
                onClick = { viewModel.claimCastleBlessing() },
                enabled = !castleState.blessingClaimed,
                tone = EldoriaTone.Vitae,
                size = EldoriaButtonSize.Medium,
                icon = if (castleState.blessingClaimed) Icons.Default.CheckCircle else Icons.Default.AutoAwesome,
                fullWidth = true,
                testTag = "castle_blessing_btn"
            )
            if (!castleState.blessingClaimed) {
                Spacer(modifier = Modifier.height(Eldoria.S4))
                Text(
                    text = "Restaura toda tu vida y maná, y añade 150 de oro.",
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(Eldoria.S8))

            EldoriaButton(
                text = "DESAFIAR AL CAMPEÓN",
                onClick = { viewModel.challengeCastleBoss() },
                tone = EldoriaTone.Blood,
                size = EldoriaButtonSize.Medium,
                icon = Icons.Default.Whatshot,
                fullWidth = true,
                testTag = "castle_challenge_btn"
            )

            Spacer(modifier = Modifier.height(Eldoria.S8))

            EldoriaButton(
                text = "SALIR DEL CASTILLO",
                onClick = { viewModel.closeCastleDialog() },
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Medium,
                fullWidth = true,
                testTag = "castle_close_btn"
            )
        }
    }
}

/**
 * Hito del reino: el evento irrepetible de cada tierra. Se presenta como una
 * pieza de lore con su don, no como un cofre más.
 */
@Composable
fun KingdomLandmarkDialog(state: LandmarkState, viewModel: GameViewModel) {
    if (!state.active) return

    Dialog(onDismissRequest = { viewModel.closeLandmarkDialog() }) {
        Box(contentAlignment = Alignment.Center) {
            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                edge = EldoriaEdge.Arcane,
                corner = Eldoria.R16,
                padding = PaddingValues(16.dp),
                glow = !state.claimed,
                filigree = true
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HITO DE ${state.kingdomName.uppercase()}",
                        style = EldoriaType.label,
                        color = Eldoria.ArcaneBright,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(Eldoria.S6))

                    // Glifo rúnico propio del hito: cada reino dibuja el suyo.
                    EldoriaFrame(
                        modifier = Modifier.size(84.dp),
                        edge = EldoriaEdge.Arcane,
                        corner = Eldoria.R12,
                        strokeWidth = Eldoria.StrokeMed,
                        filigree = false,
                        rivets = true,
                        glowPulse = !state.claimed
                    ) {
                        EldoriaRuneGlyph(
                            seed = state.kingdomId.hashCode(),
                            modifier = Modifier
                                .matchParentSize()
                                .padding(16.dp),
                            color = if (state.claimed) Eldoria.IronEdge else Eldoria.ArcaneBright,
                            strokeWidth = 2.5.dp,
                            animated = !state.claimed
                        )
                    }

                    Spacer(modifier = Modifier.height(Eldoria.S8))
                    Text(
                        text = state.name,
                        style = EldoriaType.title,
                        color = Eldoria.TextGold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(Eldoria.S12))

                EldoriaScrollSheet(padding = PaddingValues(15.dp)) {
                    Text(
                        text = state.lore,
                        style = EldoriaType.lore,
                        color = Eldoria.ParchmentInk
                    )
                }

                Spacer(modifier = Modifier.height(Eldoria.S12))

                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = if (state.claimed) EldoriaEdge.Iron else EldoriaEdge.Gold,
                    corner = Eldoria.R8,
                    padding = PaddingValues(11.dp),
                    background = Eldoria.sunkenBrush()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (state.claimed) Icons.Default.CheckCircle else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (state.claimed) Eldoria.Success else Eldoria.GoldBright,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(Eldoria.S8))
                        Text(
                            text = state.boon,
                            style = EldoriaType.small,
                            color = if (state.claimed) Eldoria.TextLow else Eldoria.TextGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Eldoria.S16))

                if (state.claimed) {
                    Text(
                        text = "Ya tomaste lo que este lugar tenía que dar.",
                        style = EldoriaType.lore,
                        color = Eldoria.TextLow,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(Eldoria.S8))
                } else {
                    EldoriaButton(
                        text = "RECLAMAR EL DON",
                        onClick = { viewModel.claimLandmarkBoon() },
                        tone = EldoriaTone.Arcane,
                        size = EldoriaButtonSize.Large,
                        icon = Icons.Default.AutoAwesome,
                        fullWidth = true,
                        testTag = "claim_landmark_btn"
                    )
                    Spacer(modifier = Modifier.height(Eldoria.S8))
                }

                EldoriaButton(
                    text = "MARCHARSE",
                    onClick = { viewModel.closeLandmarkDialog() },
                    tone = EldoriaTone.Iron,
                    size = EldoriaButtonSize.Medium,
                    fullWidth = true
                )
            }
        }
    }
}

@Composable
fun SpecialMerchantDialog(merchantState: SpecialMerchantState, viewModel: GameViewModel, playerGold: Int) {
    if (!merchantState.active) return

    // Ficha completa por pulsación larga: la mercancía rara suele llevar
    // pasivas, y en la tarjeta no caben.
    var inspecting by remember { mutableStateOf<Item?>(null) }
    inspecting?.let { item ->
        InventoryItemDialog(
            item = item,
            playerLevel = 999,
            sellPrice = 0,
            onDismiss = { inspecting = null }
        )
    }

    Dialog(onDismissRequest = { viewModel.closeSpecialMerchantDialog() }) {
        EldoriaPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            edge = EldoriaEdge.Vitae,
            corner = Eldoria.R16,
            padding = PaddingValues(14.dp),
            glow = true,
            filigree = true
        ) {
            // Cabecera: quién es, de dónde viene y cuánto oro llevas encima.
            Row(verticalAlignment = Alignment.CenterVertically) {
                EldoriaFrame(
                    modifier = Modifier.size(64.dp),
                    edge = EldoriaEdge.Gold,
                    corner = Eldoria.R8,
                    strokeWidth = Eldoria.StrokeMed,
                    filigree = false,
                    rivets = true,
                    glowPulse = true
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wandering_merchant_1784845746333),
                        contentDescription = "Mercader Ambulante",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(Eldoria.S12))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = merchantState.merchantName,
                        style = EldoriaType.heading,
                        color = Eldoria.TextGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Viajante de ${merchantState.kingdomName}",
                        style = EldoriaType.caption,
                        color = Eldoria.TextMid,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    EldoriaCounter(
                        value = playerGold.toLong(),
                        icon = Icons.Default.MonetizationOn,
                        accent = Eldoria.TextGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(Eldoria.S12))

            // Su frase, en pergamino: el mercader habla, no notifica.
            EldoriaScrollSheet(padding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)) {
                Text(
                    text = "«${merchantState.dialogue}»",
                    style = EldoriaType.lore,
                    color = Eldoria.ParchmentInk
                )
            }

            Spacer(modifier = Modifier.height(Eldoria.S12))

            EldoriaSectionTitle(
                text = "MERCANCÍA CON DESCUENTO",
                icon = Icons.Default.Storefront,
                accent = Eldoria.Vitae
            )

            Spacer(modifier = Modifier.height(Eldoria.S8))

            if (merchantState.items.isEmpty()) {
                EldoriaEmptyState(
                    artKey = "empty_shop",
                    title = "Alforjas vacías",
                    message = "Ya vendió todo lo que traía. Volverá a cruzarse en tu camino más adelante.",
                    icon = Icons.Default.Storefront,
                    accent = Eldoria.Vitae
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(Eldoria.S8)
                ) {
                    items(merchantState.items) { itemSpec ->
                        val canAfford = playerGold >= itemSpec.discountPrice

                        EldoriaItemCard(
                            name = itemSpec.item.name,
                            rarity = itemSpec.item.rarity,
                            level = itemSpec.item.itemLevel,
                            stats = itemSpec.item.getStatDescription(),
                            imageRes = getItemImageRes(itemSpec.item.imageResName, itemSpec.item.type),
                            subtitle = itemSpec.item.rarity.uppercase(),
                            onLongClick = { inspecting = itemSpec.item },
                            testTag = "merchant_item_${itemSpec.item.id}",
                            trailing = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    // Precio tachado + precio real: el descuento
                                    // es el gancho del mercader, tiene que verse.
                                    Text(
                                        text = formatGameNumber(itemSpec.originalPrice),
                                        style = EldoriaType.caption,
                                        color = Eldoria.TextLow,
                                        textDecoration = TextDecoration.LineThrough,
                                        maxLines = 1
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.MonetizationOn,
                                            contentDescription = "Oro",
                                            tint = if (canAfford) Eldoria.TextGold else Eldoria.Danger,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = formatGameNumber(itemSpec.discountPrice),
                                            style = EldoriaType.numeric,
                                            color = if (canAfford) Eldoria.TextGold else Eldoria.Danger,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    EldoriaChip(
                                        text = "-${itemSpec.discountPercent}%",
                                        color = Eldoria.Success,
                                        filled = true
                                    )
                                    Spacer(modifier = Modifier.height(Eldoria.S6))
                                    EldoriaButton(
                                        text = if (canAfford) "COMPRAR" else "SIN ORO",
                                        onClick = { viewModel.buySpecialMerchantItem(itemSpec) },
                                        enabled = canAfford,
                                        tone = if (canAfford) EldoriaTone.Gold else EldoriaTone.Iron,
                                        size = EldoriaButtonSize.Small,
                                        testTag = "buy_merchant_item_${itemSpec.item.id}"
                                    )
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Eldoria.S12))

            EldoriaButton(
                text = "CERRAR EL TRATO",
                onClick = { viewModel.closeSpecialMerchantDialog() },
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Medium,
                fullWidth = true,
                testTag = "close_merchant_btn"
            )
        }
    }
}

@Composable
fun WorldMapScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val proceduralMap by viewModel.proceduralMap.collectAsState()
    val castleState by viewModel.castleState.collectAsState()
    val specialMerchantState by viewModel.specialMerchantState.collectAsState()

    val p = progress ?: return

    val currentKingdom = KingdomGenerator.getKingdomForCoords(p.currentX, p.currentY)
    val landmarkState by viewModel.landmarkState.collectAsState()

    // El atlas es la vista "mundo": se abre desde el estandarte del reino.
    var atlasOpen by rememberSaveable { mutableStateOf(false) }
    val discovered = remember(p.mapPointsExploredJson, p.currentX, p.currentY) {
        viewModel.discoveredKingdomIds(p)
    }

    CastleDialog(castleState = castleState, viewModel = viewModel)
    SpecialMerchantDialog(merchantState = specialMerchantState, viewModel = viewModel, playerGold = p.charGold)
    KingdomLandmarkDialog(state = landmarkState, viewModel = viewModel)

    WorldAtlasSheet(
        visible = atlasOpen,
        playerX = p.currentX,
        playerY = p.currentY,
        playerLevel = p.charLevel,
        playerGold = p.charGold,
        discoveredIds = discovered,
        onTravel = { viewModel.travelToKingdom(it) },
        onDismiss = { atlasOpen = false }
    )

    // Keep track of the currently selected tile in our medieval UI
    var selectedTile by remember(p.currentX, p.currentY) {
        mutableStateOf<MapTile?>(proceduralMap.find { it.x == p.currentX && it.y == p.currentY })
    }

    EldoriaScreen(
        depth = 0,
        embers = false,
        fog = true,
        vignetteStrength = 0.5f,
        backgroundArtRes = R.drawable.img_medieval_map,
        backgroundArtAlpha = 0.10f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Estandarte del reino: quién manda aquí y dónde estás plantado.
        // Tocarlo abre el Mapa del Mundo — el tablero sólo enseña 5×5 casillas.
        EldoriaBanner(
            title = currentKingdom.name,
            subtitle = "${currentKingdom.subtitle} · toca para ver el mundo",
            modifier = Modifier
                .eldoriaPressable(onClick = { atlasOpen = true })
                .testTag("open_world_atlas"),
            artRes = R.drawable.img_world_map_banner,
            height = 116.dp,
            edge = EldoriaEdge.Gold,
            crestSeed = currentKingdom.name.hashCode(),
            trailing = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    EldoriaStatPill(
                        label = "CASILLA",
                        value = "${p.currentX},${p.currentY}",
                        icon = Icons.Default.Explore,
                        accent = Eldoria.Gold
                    )
                    Spacer(modifier = Modifier.height(Eldoria.S4))
                    EldoriaChip(
                        text = "ATLAS",
                        color = Eldoria.TextGold,
                        icon = Icons.Default.Map,
                        filled = true
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(Eldoria.S8))

        // AUTO MODES HUD PANEL
        val autoCombatActive by viewModel.isAutoCombat.collectAsState()
        val autoNavActive by viewModel.isAutoNavigation.collectAsState()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
        ) {
            EldoriaToggleChip(
                text = if (autoCombatActive) "COMBATE AUTO" else "COMBATE MANUAL",
                selected = autoCombatActive,
                onClick = { viewModel.toggleAutoCombat() },
                modifier = Modifier.weight(1f),
                accent = Eldoria.Ember,
                icon = if (autoCombatActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                testTag = "auto_combat_toggle"
            )
            EldoriaToggleChip(
                text = if (autoNavActive) "MARCHA AUTO" else "MARCHA MANUAL",
                selected = autoNavActive,
                onClick = { viewModel.toggleAutoNavigation() },
                modifier = Modifier.weight(1f),
                accent = Eldoria.Vitae,
                icon = if (autoNavActive) Icons.Default.PlayArrow else Icons.Default.Pause,
                testTag = "auto_navigation_toggle"
            )
        }

        Spacer(modifier = Modifier.height(Eldoria.S8))

        // Grid Representation Styled as a Medieval Map Board
        EldoriaFrame(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            edge = EldoriaEdge.Gold,
            corner = Eldoria.R16,
            strokeWidth = Eldoria.StrokeBold,
            filigree = true,
            rivets = true
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_medieval_map),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.34f,
                modifier = Modifier.matchParentSize()
            )
            EldoriaVignette(
                modifier = Modifier.matchParentSize(),
                strength = 0.55f,
                tint = Eldoria.Abyss,
                centerBiasY = 0.5f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    userScrollEnabled = false
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
                        WorldMapTile(
                            tile = tile,
                            isPlayerHere = tile.x == p.currentX && tile.y == p.currentY,
                            isSelected = selectedTile?.x == tile.x && selectedTile?.y == tile.y,
                            isAdjacent = abs(p.currentX - tile.x) + abs(p.currentY - tile.y) == 1,
                            onClick = { selectedTile = tile }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Eldoria.S8))

        // DIRECTIONAL D-PAD CONTROLS FOR INFINITE WORLD TRAVERSAL
        EldoriaPanel(
            modifier = Modifier.fillMaxWidth(),
            edge = EldoriaEdge.Iron,
            corner = Eldoria.R12,
            padding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RUMBO",
                        style = EldoriaType.label,
                        color = Eldoria.TextGold
                    )
                    Text(
                        text = "El mundo no tiene borde: camina en cualquier dirección.",
                        style = EldoriaType.caption,
                        color = Eldoria.TextLow,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(Eldoria.S12))

                // Rosa de los vientos: cuatro pétalos de metal alrededor de la brújula.
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    WorldMapDpadButton(
                        icon = Icons.Default.KeyboardArrowUp,
                        label = "Norte",
                        onClick = { viewModel.moveDirection(0, 1) },
                        testTag = "nav_dpad_north"
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WorldMapDpadButton(
                            icon = Icons.Default.KeyboardArrowLeft,
                            label = "Oeste",
                            onClick = { viewModel.moveDirection(-1, 0) },
                            testTag = "nav_dpad_west"
                        )

                        Box(
                            modifier = Modifier.size(38.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EldoriaRuneGlyph(
                                seed = 771,
                                modifier = Modifier.matchParentSize(),
                                color = Eldoria.GoldDeep.copy(alpha = 0.55f),
                                strokeWidth = 1.dp,
                                animated = false
                            )
                            Icon(
                                Icons.Default.Explore,
                                contentDescription = "Brújula",
                                tint = Eldoria.Gold,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        WorldMapDpadButton(
                            icon = Icons.Default.KeyboardArrowRight,
                            label = "Este",
                            onClick = { viewModel.moveDirection(1, 0) },
                            testTag = "nav_dpad_east"
                        )
                    }
                    WorldMapDpadButton(
                        icon = Icons.Default.KeyboardArrowDown,
                        label = "Sur",
                        onClick = { viewModel.moveDirection(0, -1) },
                        testTag = "nav_dpad_south"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Eldoria.S8))

        // INTERACTIVE MAP SELECTION PREVIEW CARD
        selectedTile?.let { tile ->
            val isPlayerHere = tile.x == p.currentX && tile.y == p.currentY
            val distance = abs(p.currentX - tile.x) + abs(p.currentY - tile.y)
            val isAdjacent = distance == 1

            val tileEdge = when {
                tile.encounterType == "LANDMARK" -> EldoriaEdge.Arcane
                tile.encounterType == "CASTLE" -> EldoriaEdge.Gold
                tile.encounterType == "SPECIAL_MERCHANT" -> EldoriaEdge.Vitae
                tile.isBossLair && !tile.cleared -> EldoriaEdge.Blood
                tile.isEnemySpawn && !tile.cleared -> EldoriaEdge.Ember
                tile.isObstacle -> EldoriaEdge.Iron
                else -> EldoriaEdge.Silver
            }

            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = tileEdge,
                corner = Eldoria.R12,
                padding = PaddingValues(12.dp),
                glow = isAdjacent && !tile.isObstacle,
                filigree = true
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = worldTileIcon(tile),
                                    contentDescription = null,
                                    tint = tileEdge.mid,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(Eldoria.S6))
                                Text(
                                    text = if (tile.specialName.isNotEmpty()) tile.specialName else tile.biome,
                                    style = EldoriaType.heading,
                                    color = Eldoria.TextGold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "X ${tile.x} · Y ${tile.y} · ${tile.kingdomName}",
                                style = EldoriaType.caption,
                                color = Eldoria.TextLow,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(Eldoria.S8))

                        EldoriaChip(
                            text = if (tile.explored || isPlayerHere) "SEGURA" else "AMENAZA",
                            color = if (tile.explored || isPlayerHere) Eldoria.Success else Eldoria.Danger,
                            icon = if (tile.explored || isPlayerHere) Icons.Default.CheckCircle else Icons.Default.Warning,
                            filled = true
                        )
                    }

                    Spacer(modifier = Modifier.height(Eldoria.S8))
                    EldoriaDivider(color = tileEdge.mid.copy(alpha = 0.65f), ornament = false)
                    Spacer(modifier = Modifier.height(Eldoria.S8))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        val description = when (tile.encounterType) {
                            "LANDMARK" -> "🏛️ ${tile.specialName}: el hito irrepetible de ${tile.kingdomName}. Aquí se guarda algo que no existe en ninguna otra parte del mundo."
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
                            style = EldoriaType.small,
                            color = Eldoria.TextMid
                        )

                        Spacer(modifier = Modifier.height(Eldoria.S6))

                        if (tile.isBossLair && !tile.cleared) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Eldoria.BloodBright,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(Eldoria.S6))
                                Text(
                                    text = "JEFE IMPERIAL · exige nivel ${tile.levelRequirement}",
                                    style = EldoriaType.label,
                                    color = Eldoria.BloodBright
                                )
                            }
                        } else if (tile.isEnemySpawn && !tile.cleared) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Eldoria.Ember,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(Eldoria.S6))
                                Text(
                                    text = "AMENAZA NIVEL ${tile.levelRequirement}",
                                    style = EldoriaType.label,
                                    color = Eldoria.Ember
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Eldoria.S12))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        when {
                            isPlayerHere -> {
                                when {
                                    tile.encounterType == "LANDMARK" -> {
                                        EldoriaButton(
                                            text = "EXAMINAR EL HITO",
                                            onClick = { viewModel.selectTileAndExplore(tile) },
                                            tone = EldoriaTone.Arcane,
                                            size = EldoriaButtonSize.Medium,
                                            icon = Icons.Default.AutoAwesome,
                                            fullWidth = true,
                                            testTag = "inspect_landmark_btn"
                                        )
                                    }
                                    tile.encounterType == "CASTLE" -> {
                                        EldoriaButton(
                                            text = "INGRESAR AL CASTILLO",
                                            onClick = { viewModel.selectTileAndExplore(tile) },
                                            tone = EldoriaTone.Gold,
                                            size = EldoriaButtonSize.Medium,
                                            icon = Icons.Default.Castle,
                                            fullWidth = true
                                        )
                                    }
                                    tile.encounterType == "SPECIAL_MERCHANT" -> {
                                        EldoriaButton(
                                            text = "HABLAR CON EL MERCADER",
                                            onClick = { viewModel.selectTileAndExplore(tile) },
                                            tone = EldoriaTone.Vitae,
                                            size = EldoriaButtonSize.Medium,
                                            icon = Icons.Default.Storefront,
                                            fullWidth = true
                                        )
                                    }
                                    tile.encounterType == "SHRINE" && !tile.cleared -> {
                                        EldoriaButton(
                                            text = "ACTIVAR SANTUARIO",
                                            onClick = { viewModel.selectTileAndExplore(tile) },
                                            tone = EldoriaTone.Arcane,
                                            size = EldoriaButtonSize.Medium,
                                            icon = Icons.Default.AutoAwesome,
                                            fullWidth = true
                                        )
                                    }
                                    (tile.encounterType == "CHEST" || tile.encounterType == "TREASURE") && !tile.cleared -> {
                                        EldoriaButton(
                                            text = "ABRIR Y RECLAMAR",
                                            onClick = { viewModel.selectTileAndExplore(tile) },
                                            tone = EldoriaTone.Gold,
                                            size = EldoriaButtonSize.Medium,
                                            icon = Icons.Default.Inventory2,
                                            fullWidth = true
                                        )
                                    }
                                    tile.cleared && (tile.encounterType == "SHRINE" || tile.encounterType == "CHEST" || tile.encounterType == "TREASURE") -> {
                                        Text(
                                            text = "Lugar ya exprimido: sus riquezas y energías se consumieron.",
                                            style = EldoriaType.lore,
                                            color = Eldoria.TextLow,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .fillMaxWidth()
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = "Pisas terreno firme. Nada te acecha aquí.",
                                            style = EldoriaType.lore,
                                            color = Eldoria.TextLow,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                            }
                            tile.isObstacle -> {
                                Row(
                                    modifier = Modifier.align(Alignment.Center),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = "Bloqueado",
                                        tint = Eldoria.Danger,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(Eldoria.S6))
                                    Text(
                                        text = "TERRENO INTRANSITABLE",
                                        style = EldoriaType.label,
                                        color = Eldoria.Danger
                                    )
                                }
                            }
                            isAdjacent -> {
                                val buttonLabel = when {
                                    tile.encounterType == "LANDMARK" -> "LLEGAR AL HITO"
                                    tile.encounterType == "CASTLE" -> "INGRESAR AL CASTILLO"
                                    tile.encounterType == "SPECIAL_MERCHANT" -> "HABLAR CON MERCADER"
                                    tile.encounterType == "TREASURE" && !tile.cleared -> "RECLAMAR GRAN TESORO"
                                    tile.encounterType == "CHEST" && !tile.cleared -> "ABRIR COFRE"
                                    tile.encounterType == "SHRINE" && !tile.cleared -> "ACTIVAR ALTAR"
                                    tile.isBossLair && !tile.cleared -> "DESAFIAR JEFE (Nv.${tile.levelRequirement})"
                                    tile.isEnemySpawn && !tile.cleared -> "ATACAR (Nv.${tile.levelRequirement})"
                                    else -> "VIAJAR Y EXPLORAR"
                                }
                                val buttonTone = when {
                                    tile.encounterType == "LANDMARK" -> EldoriaTone.Arcane
                                    tile.encounterType == "CASTLE" -> EldoriaTone.Gold
                                    tile.encounterType == "SPECIAL_MERCHANT" -> EldoriaTone.Vitae
                                    (tile.isBossLair || tile.isEnemySpawn) && !tile.cleared -> EldoriaTone.Blood
                                    else -> EldoriaTone.Gold
                                }
                                val buttonIcon = when {
                                    tile.encounterType == "LANDMARK" -> Icons.Default.AutoAwesome
                                    tile.encounterType == "CASTLE" -> Icons.Default.Castle
                                    tile.encounterType == "SPECIAL_MERCHANT" -> Icons.Default.Storefront
                                    (tile.isBossLair || tile.isEnemySpawn) && !tile.cleared -> Icons.Default.Whatshot
                                    else -> Icons.Default.DirectionsWalk
                                }

                                EldoriaButton(
                                    text = buttonLabel,
                                    onClick = { viewModel.selectTileAndExplore(tile) },
                                    tone = buttonTone,
                                    size = EldoriaButtonSize.Medium,
                                    icon = buttonIcon,
                                    fullWidth = true,
                                    testTag = "travel_btn"
                                )
                            }
                            else -> {
                                EldoriaButton(
                                    text = "VIAJAR HACIA ESTA CASILLA",
                                    onClick = { viewModel.selectTileAndExplore(tile) },
                                    tone = EldoriaTone.Iron,
                                    size = EldoriaButtonSize.Medium,
                                    icon = Icons.Default.DirectionsWalk,
                                    fullWidth = true,
                                    testTag = "travel_far_btn"
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Eldoria.S24))
    }
    }
}

/** Icono que resume qué hay en una casilla (sustituye a los emoji del tablero). */
private fun worldTileIcon(tile: MapTile): ImageVector = when {
    tile.encounterType == "LANDMARK" -> Icons.Default.AutoAwesome
    tile.encounterType == "CASTLE" -> Icons.Default.Castle
    tile.encounterType == "SPECIAL_MERCHANT" -> Icons.Default.Storefront
    tile.encounterType == "TREASURE" -> Icons.Default.Diamond
    tile.encounterType == "CHEST" -> Icons.Default.Inventory2
    tile.encounterType == "SHRINE" -> Icons.Default.AutoAwesome
    tile.isBossLair -> Icons.Default.WorkspacePremium
    tile.isEnemySpawn -> Icons.Default.Whatshot
    tile.isObstacle -> Icons.Default.Block
    else -> Icons.Default.Terrain
}

/**
 * Casilla del tablero. Tres estados se leen sin texto:
 * dónde estás (marco dorado que late), a dónde puedes ir de un paso (filo verde)
 * y qué queda fuera de alcance (velo y metal muerto).
 */
@Composable
private fun WorldMapTile(
    tile: MapTile,
    isPlayerHere: Boolean,
    isSelected: Boolean,
    isAdjacent: Boolean,
    onClick: () -> Unit
) {
    val isVoid = tile.biome == "Vacío"

    val edge = when {
        isPlayerHere -> EldoriaEdge.Gold
        isSelected -> EldoriaEdge.Silver
        tile.encounterType == "LANDMARK" -> EldoriaEdge.Arcane
        tile.encounterType == "CASTLE" -> EldoriaEdge.Gold
        tile.encounterType == "SPECIAL_MERCHANT" -> EldoriaEdge.Vitae
        tile.isBossLair && !tile.cleared -> EldoriaEdge.Blood
        tile.isEnemySpawn && !tile.cleared -> EldoriaEdge.Ember
        tile.isObstacle -> EldoriaEdge.Iron
        isAdjacent -> EldoriaEdge.Vitae
        else -> EldoriaEdge.Iron
    }

    val tileImageRes = when {
        tile.isObstacle -> R.drawable.img_tile_obstacle_1784470907788
        tile.isBossLair -> R.drawable.img_tile_enemy_1784470940695
        tile.encounterType == "CHEST" || tile.encounterType == "TREASURE" && !tile.explored -> R.drawable.img_tile_chest_1784470917774
        tile.encounterType == "SHRINE" && !tile.explored -> R.drawable.img_tile_shrine_1784470929381
        tile.isEnemySpawn && !tile.explored -> R.drawable.img_tile_enemy_1784470940695
        else -> R.drawable.img_tile_grass_1784470894787
    }

    val showBadge = !isPlayerHere && !isVoid && when {
        tile.encounterType == "LANDMARK" -> true
        tile.encounterType == "CASTLE" -> true
        tile.encounterType == "SPECIAL_MERCHANT" -> true
        tile.encounterType == "TREASURE" && !tile.explored -> true
        tile.encounterType == "SHRINE" && !tile.explored -> true
        tile.isBossLair -> true
        tile.isEnemySpawn && !tile.explored -> true
        else -> false
    }

    val bob = if (isPlayerHere) eldoriaFloat(periodMs = 2200, amplitude = 3.dp, label = "heroBob") else 0.dp

    EldoriaFrame(
        modifier = Modifier
            .aspectRatio(1f)
            .eldoriaPressable(onClick = onClick)
            .testTag("map_tile_${tile.x}_${tile.y}"),
        edge = edge,
        corner = 7.dp,
        strokeWidth = if (isPlayerHere || isSelected) Eldoria.StrokeMed else Eldoria.StrokeThin,
        filigree = false,
        rivets = false,
        glowPulse = isPlayerHere
    ) {
        Image(
            painter = painterResource(id = tileImageRes),
            contentDescription = tile.biome,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isVoid) 0.20f else if (tile.explored || isAdjacent || isPlayerHere) 1f else 0.72f)
        )

        // Niebla de guerra: lo no explorado y lejano se hunde en sombra.
        if (isVoid) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Eldoria.Abyss.copy(alpha = 0.78f))
            )
        } else if (!tile.explored && !isPlayerHere && !isAdjacent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Eldoria.Abyss.copy(alpha = 0.42f))
            )
        }

        if (tile.isObstacle) {
            EldoriaCrackedStone(
                modifier = Modifier.matchParentSize(),
                seed = tile.x * 31 + tile.y,
                color = Eldoria.IronDeep,
                density = 9,
                alpha = 0.6f
            )
        }

        if (showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Eldoria.Abyss.copy(alpha = 0.82f))
                    .border(1.dp, edge.mid.copy(alpha = 0.85f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = worldTileIcon(tile),
                    contentDescription = null,
                    tint = edge.top,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        if (isPlayerHere) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(Eldoria.GlowGold, Eldoria.Abyss.copy(alpha = 0.45f))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = bob)
                    .size(27.dp)
                    .clip(CircleShape)
                    .background(Eldoria.Abyss.copy(alpha = 0.85f))
                    .border(Eldoria.StrokeMed, Eldoria.goldEdge(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsWalk,
                    contentDescription = "Héroe Aquí",
                    tint = Eldoria.GoldBright,
                    modifier = Modifier.size(17.dp)
                )
            }
        } else if (tile.explored && !isVoid && tile.biome != "Santuario Inicial") {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Despejado",
                tint = Eldoria.Success,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(12.dp)
            )
        }

        if (!isVoid) {
            Text(
                text = "${tile.x},${tile.y}",
                style = EldoriaType.caption,
                color = Eldoria.TextMid,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Eldoria.Abyss.copy(alpha = 0.72f))
                    .padding(horizontal = 3.dp)
            )
        }
    }
}

/** Botón del timón: disco de hierro con filo dorado y el "clac" del juego. */
@Composable
private fun WorldMapDpadButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    listOf(Eldoria.PanelHi, Eldoria.Iron, Eldoria.IronDeep)
                )
            )
            .border(Eldoria.StrokeThin, Eldoria.goldEdge(), CircleShape)
            .eldoriaPressable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Eldoria.GoldBright,
            modifier = Modifier.size(21.dp)
        )
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
    var buyingItemType by remember { mutableStateOf<String?>(null) }
    var quantityText by remember { mutableStateOf("1") }
    var showApothecary by remember { mutableStateOf(false) }

    val filteredInventory = remember(rawInventory, searchQuery, selectedRarityFilter, selectedTypeFilter, selectedLevelFilter) {
        filterInventory(rawInventory, selectedRarityFilter, selectedTypeFilter, selectedLevelFilter, searchQuery)
    }

    val massSellTotalPrice = remember(filteredInventory) {
        filteredInventory.sumOf { viewModel.calculateSellPrice(it) }
    }

    // Dos oficios distintos —comprar y vender— dejan de pelearse por la misma
    // columna infinita: cada uno tiene su pestaña.
    var shopTab by rememberSaveable { mutableIntStateOf(0) }

    // Ficha de objeto abierta con pulsación larga: las tarjetas cortan las
    // estadísticas a dos líneas y no caben las pasivas.
    var inspecting by remember { mutableStateOf<Item?>(null) }
    inspecting?.let { item ->
        InventoryItemDialog(
            item = item,
            playerLevel = p.charLevel,
            sellPrice = viewModel.calculateSellPrice(item),
            onDismiss = { inspecting = null }
        )
    }

    EldoriaScreen(
        depth = 0,
        embers = false,
        fog = true,
        vignetteStrength = 0.58f,
        backgroundArtRes = R.drawable.merchant_stall_banner_1784845825754,
        backgroundArtAlpha = 0.12f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Eldoria.S8),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // Decorative Medieval Market Header
        item {
            EldoriaBanner(
                title = "MERCADO REAL",
                subtitle = "Oro por acero, botín por oro. Grommar no regatea dos veces.",
                artRes = R.drawable.merchant_stall_banner_1784845825754,
                height = 116.dp,
                edge = EldoriaEdge.Gold,
                crestSeed = 4412,
                trailing = {
                    EldoriaCounter(
                        value = p.charGold.toLong(),
                        icon = Icons.Default.MonetizationOn,
                        accent = Eldoria.TextGold
                    )
                }
            )
        }

        item {
            EldoriaSegmentedTabs(
                options = listOf("COMPRAR", "VENDER"),
                selectedIndex = shopTab,
                onSelect = { shopTab = it },
                accent = Eldoria.Gold,
                testTagPrefix = "shop_tab"
            )
        }

        if (shopTab == 0) {
            // Wandering Merchant Card
            item {
                EldoriaPanel(edge = EldoriaEdge.Gold, corner = Eldoria.R12, filigree = true) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EldoriaFrame(
                            modifier = Modifier.size(72.dp),
                            edge = EldoriaEdge.Gold,
                            corner = Eldoria.R8,
                            strokeWidth = Eldoria.StrokeMed,
                            filigree = false,
                            rivets = true
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wandering_merchant_1784845746333),
                                contentDescription = "Grommar el Mercader Viajante",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(Eldoria.S12))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Grommar, Viajante de la Corona",
                                style = EldoriaType.subheading,
                                color = Eldoria.TextGold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "«Recorro los seis reinos. Traigo reliquias, me llevo tu botín… y siempre gano yo.»",
                                style = EldoriaType.lore,
                                color = Eldoria.TextMid,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // La botica: un frasco por fila, con lo que hace y lo que cuesta.
            // Antes había un solo botón "POCIÓN · 40" porque sólo había una.
            if (showApothecary) {
                item {
                    EldoriaPanel(
                        modifier = Modifier.fillMaxWidth(),
                        edge = EldoriaEdge.Vitae,
                        filigree = true
                    ) {
                        Text(
                            text = "BOTICA DEL MERCADER",
                            style = EldoriaType.heading,
                            color = Eldoria.TextGold
                        )
                        Spacer(Modifier.height(Eldoria.S8))
                        EldoriaPotions.ALL.forEach { potion ->
                            val locked = p.charLevel < potion.unlockLevel
                            EldoriaButton(
                                text = if (locked) {
                                    "${potion.name} · nivel ${potion.unlockLevel}"
                                } else {
                                    "${potion.name} · ${potion.price}"
                                },
                                onClick = {
                                    quantityText = "1"
                                    buyingItemType = "POTION:" + potion.id
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !locked,
                                tone = if (locked) EldoriaTone.Iron else EldoriaTone.Vitae,
                                size = EldoriaButtonSize.Small,
                                fullWidth = true,
                                testTag = "buy_" + potion.id
                            )
                            Text(
                                text = potion.description,
                                style = EldoriaType.lore,
                                color = Eldoria.TextLow
                            )
                            Spacer(Modifier.height(Eldoria.S6))
                        }
                    }
                }
            }

            // Miniscule Potion & Refresh Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                ) {
                    EldoriaButton(
                        text = "BOTICA",
                        onClick = { showApothecary = !showApothecary },
                        modifier = Modifier.weight(1f),
                        tone = EldoriaTone.Blood,
                        size = EldoriaButtonSize.Small,
                        icon = Icons.Default.LocalPharmacy,
                        fullWidth = true,
                        testTag = "buy_potion_btn"
                    )
                    EldoriaButton(
                        text = "REABASTECER · 20",
                        onClick = { viewModel.refreshShop() },
                        modifier = Modifier.weight(1f),
                        tone = EldoriaTone.Iron,
                        size = EldoriaButtonSize.Small,
                        icon = Icons.Default.Refresh,
                        fullWidth = true,
                        testTag = "refresh_shop_btn"
                    )
                }
            }

            // Pet Food Section in Shop
            item {
                EldoriaSectionTitle(
                    text = "ALIMENTO DE MASCOTA",
                    icon = Icons.Default.Pets,
                    accent = Eldoria.RarityUniversal,
                    trailing = {
                        EldoriaChip(
                            text = "SANTUARIO",
                            color = Eldoria.TextGold,
                            icon = Icons.Default.ChevronRight,
                            modifier = Modifier.clickable { viewModel.changeScreen(GameScreen.PET_SCREEN) }
                        )
                    }
                )
            }

            item {
                data class FoodItemUi(
                    val type: String,
                    val name: String,
                    val cost: String,
                    val bonus: String,
                    val imgRes: String
                )

                val foodList = listOf(
                    FoodItemUi("BESTIAL", "Ración Bestial", "150", "+25 Sac · +100 EXP", "img_food_bestial"),
                    FoodItemUi("MISTICA", "Galleta Mística", "500", "+50 Sac · +400 EXP", "img_food_mistica"),
                    FoodItemUi("DRAGON", "Manjar Imperial", "2.000", "+80 Sac · +1.8K EXP", "img_food_dragon"),
                    FoodItemUi("CELESTIAL", "Elixir Estelar", "8.000", "+100 Sac · +7K EXP", "img_food_celestial")
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S8),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(foodList) { food ->
                        EldoriaPanel(
                            modifier = Modifier.width(112.dp),
                            edge = EldoriaEdge.Silver,
                            corner = Eldoria.R8,
                            padding = PaddingValues(7.dp),
                            onClick = {
                                quantityText = "1"
                                buyingItemType = food.type
                            },
                            testTag = "buy_pet_food_${food.type}"
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                EldoriaSlotFrame(size = 44.dp) {
                                    Image(
                                        painter = painterResource(id = getItemImageRes(food.imgRes, "PET_FOOD")),
                                        contentDescription = food.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = food.name,
                                    style = EldoriaType.caption,
                                    color = Eldoria.TextHi,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = Eldoria.TextGold,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = food.cost,
                                        style = EldoriaType.numeric,
                                        color = Eldoria.TextGold,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = food.bonus,
                                    style = EldoriaType.caption,
                                    color = Eldoria.RarityUniversal,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Buy Section Title
            item {
                EldoriaSectionTitle(
                    text = "ARMAS Y EQUIPO",
                    icon = Icons.Default.Storefront,
                    accent = Eldoria.Gold
                )
            }

            // Shop Items list
            if (shopItems.isEmpty()) {
                item {
                    EldoriaEmptyState(
                        title = "El puesto está vacío",
                        message = "Grommar ya vendió todo lo que traía. Paga el reabastecimiento y volverá a cargar la mula.",
                        icon = Icons.Default.Storefront,
                        accent = Eldoria.Gold,
                        actionLabel = "Reabastecer por 20",
                        onAction = { viewModel.refreshShop() }
                    )
                }
            } else {
                items(shopItems) { item ->
                    val cost = when (item.rarity.uppercase()) {
                        "UNIVERSAL" -> 12000 + (item.itemLevel * 300)
                        "ARCANO" -> 7000 + (item.itemLevel * 200)
                        "LEGENDARIO", "LEGENDARY" -> 4000 + (item.itemLevel * 150)
                        "ÉPICO", "EPIC" -> 1000 + (item.itemLevel * 50)
                        "RARO", "RARE" -> 350 + (item.itemLevel * 25)
                        else -> 100 + (item.itemLevel * 10)
                    }
                    val affordable = p.charGold >= cost

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

                    EldoriaItemCard(
                        name = item.name,
                        rarity = item.rarity,
                        level = item.itemLevel,
                        stats = stats.take(3).joinToString(" · ").ifEmpty { "Sin bonificaciones" },
                        imageRes = getItemImageRes(item.imageResName, item.type),
                        subtitle = item.rarity.uppercase(),
                        onLongClick = { inspecting = item },
                        testTag = "shop_item_${item.name}",
                        trailing = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.MonetizationOn,
                                        contentDescription = "Oro",
                                        tint = if (affordable) Eldoria.TextGold else Eldoria.Danger,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = formatGameNumber(cost),
                                        style = EldoriaType.numeric,
                                        color = if (affordable) Eldoria.TextGold else Eldoria.Danger
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                EldoriaButton(
                                    text = if (affordable) "COMPRAR" else "SIN ORO",
                                    onClick = { viewModel.buyItem(item, cost) },
                                    enabled = affordable,
                                    tone = if (affordable) EldoriaTone.Gold else EldoriaTone.Iron,
                                    size = EldoriaButtonSize.Small
                                )
                            }
                        }
                    )
                }
            }
        } else {
            // ─── VENDER ───
            item {
                EldoriaPanel(edge = EldoriaEdge.Blood, corner = Eldoria.R12) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "VENTA MASIVA",
                                style = EldoriaType.label,
                                color = Eldoria.TextGold
                            )
                            Text(
                                text = "${filteredInventory.size} objetos filtrados",
                                style = EldoriaType.caption,
                                color = Eldoria.TextLow
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            EldoriaCounter(
                                value = massSellTotalPrice.toLong(),
                                icon = Icons.Default.MonetizationOn,
                                accent = Eldoria.TextGold
                            )
                        }
                        Spacer(modifier = Modifier.width(Eldoria.S8))
                        EldoriaButton(
                            text = "VENDER TODO",
                            onClick = {
                                SoundManager.playButtonClick()
                                showMassSellConfirmation = true
                            },
                            enabled = filteredInventory.isNotEmpty(),
                            tone = EldoriaTone.Blood,
                            size = EldoriaButtonSize.Small,
                            icon = Icons.Default.Sell,
                            testTag = "mass_sell_btn"
                        )
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
                    EldoriaEmptyState(
                        title = if (rawInventory.isEmpty()) "Mochila vacía" else "Nada coincide",
                        message = if (rawInventory.isEmpty())
                            "No llevas equipo que Grommar quiera comprar. Vuelve del calabozo con las manos llenas."
                        else
                            "Ningún objeto pasa los filtros que has puesto. Aflójalos y volverán a aparecer.",
                        icon = Icons.Default.Inventory2,
                        accent = Eldoria.Gold
                    )
                }
            } else {
                items(filteredInventory) { item ->
                    val sellPrice = viewModel.calculateSellPrice(item)
                    EldoriaItemCard(
                        name = item.name,
                        rarity = item.rarity,
                        level = item.itemLevel,
                        stats = "Te pagan ${formatGameNumber(sellPrice)} de oro",
                        imageRes = getItemImageRes(item.imageResName, item.type),
                        subtitle = item.rarity.uppercase(),
                        onLongClick = { inspecting = item },
                        testTag = "sell_item_${item.name}",
                        trailing = {
                            EldoriaButton(
                                text = "VENDER",
                                onClick = { viewModel.sellItem(item) },
                                tone = EldoriaTone.Blood,
                                size = EldoriaButtonSize.Small,
                                icon = Icons.Default.Sell
                            )
                        }
                    )
                }
            }
        }
    }
    }

    if (showMassSellConfirmation) {
        val filterSummary = buildString {
            if (searchQuery.isNotBlank()) append("Búsqueda «$searchQuery» · ")
            append("Rareza $selectedRarityFilter · Tipo $selectedTypeFilter · Nivel $selectedLevelFilter")
        }
        EldoriaConfirmDialog(
            title = "Venta masiva",
            message = "Vas a vender ${filteredInventory.size} objetos por ${formatGameNumber(massSellTotalPrice)} de oro.\n\nFiltros: $filterSummary\n\nEsto no se deshace.",
            confirmLabel = "VENDER TODO",
            onConfirm = {
                viewModel.massSellItems(filteredInventory)
                showMassSellConfirmation = false
            },
            onDismiss = { showMassSellConfirmation = false },
            tone = EldoriaTone.Blood,
            testTagPrefix = "mass_sell"
        )
    }

    buyingItemType?.let { itemType ->
        data class ShopItemSpec(
            val title: String,
            val unitCost: Int,
            val description: String,
            val isPotion: Boolean
        )

        val spec = when (itemType) {
            // Cualquier id del catálogo: "POTION:pot_regen" y demás. La rama
            // "POTION" pelada se mantiene por las pantallas que aún no ofrecen
            // el catálogo entero.
            "POTION" -> EldoriaPotions.spec("pot_menor")!!.let {
                ShopItemSpec(it.name, it.price, it.description, true)
            }
            in POTION_TYPES -> EldoriaPotions.spec(itemType.removePrefix("POTION:"))!!.let {
                ShopItemSpec(it.name, it.price, it.description, true)
            }
            "BESTIAL" -> ShopItemSpec("Ración de Carne Bestial", 150, "Alimento. +25 Saciedad, +100 EXP para tu mascota.", false)
            "MISTICA" -> ShopItemSpec("Galleta Mística de Mascota", 500, "Alimento. +50 Saciedad, +400 EXP para tu mascota.", false)
            "DRAGON" -> ShopItemSpec("Manjar Imperial de Dragón", 2000, "Alimento. +80 Saciedad, +1,800 EXP para tu mascota.", false)
            else -> ShopItemSpec("Elixir Celestial Estelar", 8000, "Alimento. +100 Saciedad, +7,000 EXP para tu mascota.", false)
        }

        val currentQty = quantityText.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val totalCost = spec.unitCost.toLong() * currentQty
        val userGold = p.charGold

        val canAfford = userGold >= totalCost

        Dialog(onDismissRequest = { buyingItemType = null }) {
            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                edge = EldoriaEdge.Gold,
                corner = Eldoria.R16,
                padding = PaddingValues(16.dp),
                glow = true,
                filigree = true
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = spec.title,
                        style = EldoriaType.title,
                        color = Eldoria.TextGold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(Eldoria.S4))
                    Text(
                        text = spec.description,
                        style = EldoriaType.small,
                        color = Eldoria.TextMid,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Eldoria.S8))
                    EldoriaDivider(color = Eldoria.Gold)
                    Spacer(modifier = Modifier.height(Eldoria.S8))

                    EldoriaKeyValueRow(
                        label = "Precio por unidad",
                        value = formatGameNumber(spec.unitCost),
                        icon = Icons.Default.MonetizationOn
                    )

                    Spacer(modifier = Modifier.height(Eldoria.S8))

                    Text(
                        text = "CANTIDAD",
                        style = EldoriaType.label,
                        color = Eldoria.TextMid
                    )
                    Spacer(modifier = Modifier.height(Eldoria.S6))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EldoriaButton(
                            text = "−1",
                            onClick = {
                                val current = quantityText.toIntOrNull() ?: 1
                                if (current > 1) quantityText = (current - 1).toString()
                            },
                            tone = EldoriaTone.Iron,
                            size = EldoriaButtonSize.Small
                        )

                        Spacer(modifier = Modifier.width(Eldoria.S8))

                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() }) {
                                    quantityText = newValue
                                }
                            },
                            modifier = Modifier.width(104.dp),
                            textStyle = EldoriaType.numeric.copy(color = Eldoria.TextHi, textAlign = TextAlign.Center),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Eldoria.Gold,
                                unfocusedBorderColor = Eldoria.IronEdge,
                                focusedContainerColor = Eldoria.PanelSunken,
                                unfocusedContainerColor = Eldoria.PanelSunken
                            )
                        )

                        Spacer(modifier = Modifier.width(Eldoria.S8))

                        EldoriaButton(
                            text = "+1",
                            onClick = {
                                val current = quantityText.toIntOrNull() ?: 1
                                quantityText = (current + 1).toString()
                            },
                            tone = EldoriaTone.Iron,
                            size = EldoriaButtonSize.Small
                        )
                    }

                    Spacer(modifier = Modifier.height(Eldoria.S8))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Eldoria.S6),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(5, 10, 50, 100).forEach { qty ->
                            EldoriaChip(
                                text = "+$qty",
                                color = Eldoria.Gold,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val current = quantityText.toIntOrNull() ?: 0
                                        quantityText = (current + qty).toString()
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Eldoria.S12))

                    EldoriaPanel(
                        modifier = Modifier.fillMaxWidth(),
                        edge = if (canAfford) EldoriaEdge.Vitae else EldoriaEdge.Blood,
                        corner = Eldoria.R8,
                        padding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        background = Eldoria.sunkenBrush()
                    ) {
                        EldoriaKeyValueRow(
                            label = "Coste total",
                            value = formatGameNumber(totalCost.toInt()),
                            icon = Icons.Default.MonetizationOn,
                            valueColor = if (canAfford) Eldoria.TextGold else Eldoria.Danger
                        )
                        EldoriaKeyValueRow(
                            label = "Tu oro",
                            value = formatGameNumber(userGold),
                            icon = Icons.Default.AccountBalanceWallet,
                            valueColor = if (canAfford) Eldoria.Success else Eldoria.Danger
                        )
                    }

                    Spacer(modifier = Modifier.height(Eldoria.S16))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                    ) {
                        EldoriaButton(
                            text = "CANCELAR",
                            onClick = { buyingItemType = null },
                            modifier = Modifier.weight(1f),
                            tone = EldoriaTone.Iron,
                            size = EldoriaButtonSize.Medium,
                            fullWidth = true
                        )
                        EldoriaButton(
                            text = "COMPRAR ×$currentQty",
                            onClick = {
                                if (spec.isPotion) {
                                    viewModel.buyPotion(
                                        currentQty,
                                        if (itemType.startsWith("POTION:")) itemType.removePrefix("POTION:")
                                        else "pot_menor"
                                    )
                                } else {
                                    viewModel.buyPetFood(itemType, currentQty)
                                }
                                buyingItemType = null
                            },
                            modifier = Modifier.weight(1.25f),
                            enabled = canAfford,
                            tone = EldoriaTone.Gold,
                            size = EldoriaButtonSize.Medium,
                            fullWidth = true
                        )
                    }
                }
            }
        }
    }
}




/** Tipos de item de la tienda que son frascos del catálogo. */
private val POTION_TYPES: Set<String> =
    EldoriaPotions.ALL.map { "POTION:" + it.id }.toSet()

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
    modifier: Modifier = Modifier,
    /**
     * Clave de la lámina de la acción. Manda sobre [icon], que se mantiene como
     * respaldo: un rayo de Material no dice "Llama Necrótica", pero es mejor
     * que un hueco si algún día falta el recurso.
     */
    artKey: String? = null
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

            // Center Icon — lámina propia si la hay, icono si no.
            val slotArt = artKey?.let { EldoriaArt.of(it) }
            if (slotArt != null) {
                Image(
                    painter = painterResource(id = slotArt),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    alpha = if (enabled) 1f else 0.4f,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(5.dp))
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (enabled) glassTheme.centerColor else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }

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
                        color = if (enabled) Eldoria.TextHi else Eldoria.TextLow,
                        style = EldoriaType.caption.copy(
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
                style = EldoriaType.caption,
                color = if (enabled) glassTheme.glowColor else Eldoria.TextLow,
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

    // ─── Efectos por habilidad ───
    // El daño del jugador se pinta SOBRE el enemigo; la curación, los gritos y
    // los golpes enemigos, sobre el héroe. `tick` reinicia la animación cada vez
    // que cambia la animación activa, también en auto-combate.
    val classSkillList = remember(p.skillsJson) { GameJsonParser.listFromJson<Skill>(p.skillsJson) }
    val lastSkill = remember(combatState.lastSkillId, classSkillList) {
        classSkillList.firstOrNull { it.id == combatState.lastSkillId }
    }

    val enemyFx = when (activeAnim) {
        "PLAYER_ATTACK" -> CombatFx.PHYSICAL
        "PLAYER_MAGIC" -> combatFxForSkill(
            skillId = combatState.lastSkillId,
            healing = (lastSkill?.healingMultiplier ?: 0.0) > 0.0,
            damaging = (lastSkill?.damageMultiplier ?: 1.0) > 0.0
        )
        else -> CombatFx.NONE
    }
    val playerFx = when (activeAnim) {
        "PLAYER_HEAL" -> if (combatState.lastSkillId.startsWith("c_")) CombatFx.HOLY else CombatFx.HEAL
        "PLAYER_POTION" -> CombatFx.HEAL
        "ENEMY_ATTACK" -> CombatFx.PHYSICAL
        "ENEMY_SKILL" -> combatFxForEnemyArchetype(combatState.enemyArchetype)
        else -> CombatFx.NONE
    }
    // "Grito de Provocación" no daña: su onda va sobre el héroe, no sobre la bestia.
    val selfBuffFx = if (activeAnim == "PLAYER_MAGIC" && (lastSkill?.damageMultiplier ?: 1.0) <= 0.0) {
        combatFxForSkill(combatState.lastSkillId, damaging = false)
    } else CombatFx.NONE

    var enemyFxTick by remember { mutableIntStateOf(0) }
    var playerFxTick by remember { mutableIntStateOf(0) }
    LaunchedEffect(activeAnim) {
        if (activeAnim != null) {
            if (enemyFx != CombatFx.NONE) enemyFxTick++
            if (playerFx != CombatFx.NONE || selfBuffFx != CombatFx.NONE) playerFxTick++
        }
    }

    // El velo de pantalla toma el color del golpe que se acaba de dar.
    val flashFx = when {
        enemyFx != CombatFx.NONE -> enemyFx
        selfBuffFx != CombatFx.NONE -> selfBuffFx
        else -> playerFx
    }
    val flashTick = if (enemyFx != CombatFx.NONE) enemyFxTick else playerFxTick

    val enemyEdge = when (enemy.rarity) {
        "LEGENDARY" -> EldoriaEdge.Gold
        "CHAMPION" -> EldoriaEdge.Ember
        "ELITE" -> EldoriaEdge.Arcane
        else -> EldoriaEdge.Blood
    }

    EldoriaScreen(
        depth = if (enemy.isBoss) 3 else 2,
        embers = true,
        fog = true,
        vignetteStrength = 0.72f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // Antorchas del foso: dos focos cálidos, uno por contendiente.
        EldoriaTorchLight(
            modifier = Modifier.matchParentSize(),
            intensity = 0.55f,
            warm = Eldoria.Ember,
            flicker = true,
            centerX = 0.16f,
            centerY = 0.42f
        )
        EldoriaTorchLight(
            modifier = Modifier.matchParentSize(),
            intensity = 0.55f,
            warm = enemyEdge.mid,
            flicker = true,
            centerX = 0.84f,
            centerY = 0.42f
        )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP Header: Auto Combat Toggle HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Eldoria.S6),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // De quién es el turno: lo único que el jugador necesita saber arriba.
            val turnPulse = eldoriaPulse(periodMs = 1100, from = 0.45f, to = 1f, label = "turnPulse")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(9.dp)) {
                    drawPath(
                        eldoriaDiamondPath(size.width / 2f, size.height / 2f, size.minDimension / 2f),
                        color = if (combatState.playerTurn) Eldoria.Success.copy(alpha = turnPulse)
                        else Eldoria.Danger.copy(alpha = turnPulse)
                    )
                }
                Spacer(modifier = Modifier.width(Eldoria.S6))
                Text(
                    text = if (combatState.playerTurn) "TU TURNO" else "TURNO ENEMIGO",
                    style = EldoriaType.label,
                    color = if (combatState.playerTurn) Eldoria.TextGold else Eldoria.TextMid
                )
            }

            EldoriaToggleChip(
                text = if (autoCombatActive) "AUTO" else "MANUAL",
                selected = autoCombatActive,
                onClick = { viewModel.toggleAutoCombat() },
                accent = Eldoria.Ember,
                icon = if (autoCombatActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                testTag = "combat_screen_auto_combat_toggle"
            )
        }

        // TOP: Sleek Compact 2-Row Enemy Header Bar (strictly restricted height)
        EldoriaPanel(
            modifier = Modifier.fillMaxWidth(),
            edge = enemyEdge,
            corner = Eldoria.R8,
            padding = PaddingValues(horizontal = 11.dp, vertical = 8.dp),
            glow = enemy.isBoss
        ) {
            val enemyColor = enemyEdge.top
            val rarityLabel = when (enemy.rarity) {
                "LEGENDARY" -> "JEFE LEGENDARIO"
                "CHAMPION" -> "CAMPEÓN"
                "ELITE" -> "ÉLITE"
                else -> "MONSTRUO"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = enemy.name,
                        style = EldoriaType.subheading,
                        color = enemyColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
                    ) {
                        EldoriaChip(
                            text = rarityLabel,
                            color = enemyColor,
                            filled = enemy.isBoss
                        )
                        EldoriaChip(text = "NV ${enemy.level}", color = Eldoria.TextLow)
                        if (combatState.enemyAntiHealTurns > 0) {
                            EldoriaChip(
                                text = "SIN CURA ${combatState.enemyAntiHealTurns}t",
                                color = Eldoria.Danger,
                                icon = Icons.Default.Block,
                                filled = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(Eldoria.S8))

                Column(modifier = Modifier.width(116.dp)) {
                    EldoriaResourceBar(
                        current = enemy.currentHp,
                        max = enemy.maxHp,
                        tone = EldoriaBarTone.Health,
                        height = 12.dp,
                        showNumbers = true,
                        dangerPulse = enemy.maxHp > 0 && enemy.currentHp.toFloat() / enemy.maxHp < 0.25f
                    )
                    if (combatState.enemyIntent != null) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = combatState.enemyIntent ?: "",
                            style = EldoriaType.caption,
                            color = Eldoria.Warning,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // MID: Grand Hero Battle Arena (Maximizes Asset Relevance)
        EldoriaFrame(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = Eldoria.S8),
            edge = EldoriaEdge.Iron,
            corner = Eldoria.R16,
            strokeWidth = Eldoria.StrokeBold,
            filigree = true,
            rivets = true
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Suelo del foso: piedra agrietada bajo un gradiente sangriento.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Eldoria.Slate, Eldoria.EmberShadow.copy(alpha = 0.35f), Eldoria.Abyss)
                            )
                        )
                )
                EldoriaCrackedStone(
                    modifier = Modifier.matchParentSize(),
                    seed = enemy.name.hashCode(),
                    color = Eldoria.IronDeep,
                    density = 16,
                    alpha = 0.45f
                )

                // Velo del golpe: tiñe el foso con el color del elemento usado.
                EldoriaCombatFlash(
                    fx = flashFx,
                    trigger = flashTick,
                    modifier = Modifier.matchParentSize(),
                    maxAlpha = 0.22f
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
                                    val healing = f.contains("+")
                                    Box(
                                        modifier = Modifier
                                            .clip(CutCornerShape(5.dp))
                                            .background(Eldoria.Abyss.copy(alpha = 0.88f))
                                            .border(
                                                Eldoria.StrokeThin,
                                                if (healing) Eldoria.Success else Eldoria.BloodBright,
                                                CutCornerShape(5.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            f,
                                            style = EldoriaType.numeric,
                                            color = if (healing) Eldoria.VitaeBright else Eldoria.BloodBright,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        // Player Portrait Frame (LARGE 96dp Asset)
                        Box(contentAlignment = Alignment.Center) {
                            EldoriaFrame(
                                modifier = Modifier.size(96.dp),
                                edge = when (activeAnim) {
                                    "PLAYER_HEAL", "PLAYER_POTION" -> EldoriaEdge.Vitae
                                    "ENEMY_ATTACK", "ENEMY_SKILL" -> EldoriaEdge.Blood
                                    else -> EldoriaEdge.Gold
                                },
                                corner = Eldoria.R12,
                                strokeWidth = Eldoria.StrokeBold,
                                filigree = false,
                                rivets = true,
                                glowPulse = combatState.playerTurn
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = getCharacterPortrait(p.charRace, p.charClass, p.hasAdvancedClass, p.charLevel)
                                    ),
                                    contentDescription = "Player Portrait",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Efecto recibido/lanzado sobre el héroe: curación,
                            // grito propio o el elemento con el que le pegan.
                            EldoriaSkillFx(
                                fx = if (selfBuffFx != CombatFx.NONE) selfBuffFx else playerFx,
                                trigger = playerFxTick,
                                modifier = Modifier.size(112.dp),
                                seed = p.charName.hashCode()
                            )
                        }

                        Spacer(modifier = Modifier.height(Eldoria.S6))
                        Text(
                            p.charName,
                            style = EldoriaType.subheading,
                            color = Eldoria.TextHi,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val evolvedTitle = viewModel.getEvolvedRaceName(p.charRace, p.charLevel)
                        Text(
                            evolvedTitle,
                            style = EldoriaType.caption,
                            color = Eldoria.TextGold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(Eldoria.S6))

                        EldoriaResourceBar(
                            current = combatState.playerCurrentHp,
                            max = p.maxHp,
                            tone = EldoriaBarTone.Health,
                            height = 11.dp,
                            showNumbers = true,
                            dangerPulse = p.maxHp > 0 && combatState.playerCurrentHp.toFloat() / p.maxHp < 0.3f
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        EldoriaResourceBar(
                            current = combatState.playerCurrentMp,
                            max = p.maxMp,
                            tone = EldoriaBarTone.Mana,
                            height = 11.dp,
                            showNumbers = true
                        )

                        // Hero Equipped Pet Visual Badge
                        val playerEquippedPet = remember(p.equippedPetJson) {
                            GameJsonParser.fromJson<Item>(p.equippedPetJson)
                        }
                        if (playerEquippedPet != null) {
                            Spacer(modifier = Modifier.height(Eldoria.S4))
                            CombatPetBadge(
                                imageRes = getItemImageRes(playerEquippedPet.imageResName, "PET"),
                                name = playerEquippedPet.name,
                                level = p.petLevel,
                                accent = Eldoria.RarityUniversal
                            )
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
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Eldoria.PanelHi, Eldoria.Abyss)
                                    )
                                )
                                .border(Eldoria.StrokeMed, Eldoria.bloodEdge(), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "VS",
                                style = EldoriaType.heading,
                                color = Eldoria.TextGold
                            )
                        }

                        Spacer(modifier = Modifier.height(Eldoria.S8))

                        // Ímpetu: sube al parar y multiplica el daño. Merece verse.
                        if (combatState.momentum > 0) {
                            EldoriaResourceBar(
                                current = combatState.momentum,
                                max = 100,
                                tone = EldoriaBarTone.Momentum,
                                height = 9.dp,
                                showNumbers = false
                            )
                            Spacer(modifier = Modifier.height(Eldoria.S6))
                        }

                        val passiveTag = when (p.charRace) {
                            "Humano" -> when {
                                p.charLevel >= 100 -> "✨ +60% Oro"
                                p.charLevel >= 50 -> "⚡ +35% Oro"
                                p.charLevel >= 20 -> "🏆 +20% Oro"
                                else -> "👑 +10% Oro"
                            }
                            "Elfo" -> when {
                                p.charLevel >= 100 -> "✨ -50% MP"
                                p.charLevel >= 50 -> "⚡ -35% MP"
                                p.charLevel >= 20 -> "🌌 -20% MP"
                                else -> "👁️ +10% MaxMP"
                            }
                            "Enano" -> when {
                                p.charLevel >= 100 -> "✨ 35% Reflect"
                                p.charLevel >= 50 -> "⚡ 20% Reflect"
                                p.charLevel >= 20 -> "🛡️ 10% Reflect"
                                else -> "⛰️ +10% MaxHP"
                            }
                            "Orco" -> when {
                                p.charLevel >= 100 -> "✨ 35% Lifesteal"
                                p.charLevel >= 50 -> "⚡ 20% Lifesteal"
                                p.charLevel >= 20 -> "🩸 12% Lifesteal"
                                else -> "⚔️ +10% Daño"
                            }
                            else -> ""
                        }

                        if (passiveTag.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(CutCornerShape(4.dp))
                                    .background(Eldoria.Abyss.copy(alpha = 0.8f))
                                    .border(Eldoria.StrokeHair, Eldoria.Gold.copy(alpha = 0.7f), CutCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    passiveTag,
                                    style = EldoriaType.caption,
                                    color = Eldoria.TextGold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
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
                                            .clip(CutCornerShape(5.dp))
                                            .background(Eldoria.Abyss.copy(alpha = 0.88f))
                                            .border(Eldoria.StrokeThin, enemyFx.accent(), CutCornerShape(5.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            feedback,
                                            style = EldoriaType.numeric,
                                            color = enemyFx.accent(),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        // Enemy Portrait Frame (LARGE 96dp Asset)
                        Box(contentAlignment = Alignment.Center) {
                            EldoriaFrame(
                                modifier = Modifier.size(96.dp),
                                edge = enemyEdge,
                                corner = Eldoria.R12,
                                strokeWidth = Eldoria.StrokeBold,
                                filigree = false,
                                rivets = true,
                                glowPulse = !combatState.playerTurn
                            ) {
                                // Se revela con zoom inverso en vez de recortarse:
                                // el marco es cuadrado y el arte apaisado, así que
                                // `Crop` cortaba a la criatura por los lados.
                                EldoriaRevealImage(
                                    painter = painterResource(
                                        id = getEnemyArtRes(enemy.artKey, enemy.name, enemy.isBoss, enemy.rarity)
                                    ),
                                    contentDescription = "Enemy Portrait",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // El elemento de TU habilidad estalla aquí: fuego,
                            // veneno, runas, sombra o luz según lo que lanzaste.
                            EldoriaSkillFx(
                                fx = enemyFx,
                                trigger = enemyFxTick,
                                modifier = Modifier.size(112.dp),
                                seed = enemy.name.hashCode()
                            )
                        }

                        Spacer(modifier = Modifier.height(Eldoria.S6))
                        Text(
                            enemy.name.split(",").first(),
                            style = EldoriaType.subheading,
                            color = Eldoria.TextHi,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Nv.${enemy.level} " + when (enemy.rarity) {
                                "LEGENDARY" -> "Jefe"
                                "CHAMPION" -> "Campeón"
                                "ELITE" -> "Élite"
                                else -> "Salvaje"
                            },
                            style = EldoriaType.caption,
                            color = enemyEdge.top,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(Eldoria.S6))

                        EldoriaResourceBar(
                            current = enemy.currentHp,
                            max = enemy.maxHp,
                            tone = EldoriaBarTone.Health,
                            height = 11.dp,
                            showNumbers = true,
                            dangerPulse = enemy.maxHp > 0 && enemy.currentHp.toFloat() / enemy.maxHp < 0.25f
                        )

                        // Enemy Pet Visual Badge
                        val enemyPet = enemy.pet
                        if (enemyPet != null) {
                            Spacer(modifier = Modifier.height(Eldoria.S4))
                            CombatPetBadge(
                                imageRes = getItemImageRes(enemyPet.imageResName, "PET"),
                                name = enemyPet.name,
                                level = enemyPet.level,
                                accent = Eldoria.BloodBright
                            )
                        }
                    }
                }
            }
        }

        // BOTTOM: Combat Logs Console
        EldoriaPanel(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp),
            edge = EldoriaEdge.Iron,
            corner = Eldoria.R8,
            padding = PaddingValues(0.dp),
            background = Eldoria.sunkenBrush()
        ) {
            val scrollState = rememberScrollState()
            LaunchedEffect(combatState.combatLogs.size) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                combatState.combatLogs.forEach { log ->
                    val logColor = when {
                        log.contains("derrotado") || log.contains("SUBISTE") || log.contains("Encontraste") || log.contains("VICTORIA") -> Eldoria.TextGold
                        log.contains("sana") || log.contains("recuperas") || log.contains("rejuvenecedora") -> Eldoria.VitaeBright
                        log.contains("Corte Sanguinolento") || log.contains("te ataca e inflige") || log.contains("puntos de daño físico") -> Eldoria.BloodBright
                        log.contains("Drenaje de Vida") -> Color(0xFFFF7BA8)
                        log.contains("Maldición de Maná") || log.contains("Maná") || log.contains("MP") -> Eldoria.ManaBright
                        log.contains("Piel de Espinas") || log.contains("Escudo Rúnico") || log.contains("esquivas") || log.contains("ESQUIVADO") -> Eldoria.Info
                        else -> Eldoria.TextMid
                    }
                    Row(modifier = Modifier.padding(bottom = 3.dp)) {
                        Text(
                            text = "›",
                            style = EldoriaType.small,
                            color = logColor.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = log,
                            style = EldoriaType.small,
                            color = logColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Eldoria.S8))

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
                        CombatOutcomePanel(
                            title = "CALABOZO CONQUISTADO",
                            message = "Derrotaste al Jefe Final. Su tesoro único es tuyo.",
                            tone = EldoriaTone.Gold,
                            edge = EldoriaEdge.Gold
                        ) {
                            EldoriaButton(
                                text = "RECLAMAR TESORO Y SALIR",
                                onClick = { viewModel.exitDungeonRun() },
                                tone = EldoriaTone.Gold,
                                size = EldoriaButtonSize.Large,
                                icon = Icons.Default.WorkspacePremium,
                                fullWidth = true,
                                testTag = "claim_dungeon_treasure_button"
                            )
                        }
                    } else if (dungeonRun.stageVictoryPending) {
                        CombatOutcomePanel(
                            title = "SUBJEFE ${dungeonRun.currentStage}/9 DERROTADO",
                            message = "Bajas a la siguiente etapa con ${dungeonRun.persistentHp} HP. No se regenera.",
                            tone = EldoriaTone.Ember,
                            edge = EldoriaEdge.Ember
                        ) {
                            EldoriaButton(
                                text = "SIGUIENTE SUBJEFE (${dungeonRun.currentStage + 1}/10)",
                                onClick = { viewModel.advanceDungeonStage() },
                                tone = EldoriaTone.Vitae,
                                size = EldoriaButtonSize.Medium,
                                icon = Icons.Default.Whatshot,
                                fullWidth = true,
                                testTag = "advance_dungeon_stage_button"
                            )
                            Spacer(modifier = Modifier.height(Eldoria.S8))
                            EldoriaButton(
                                text = "RETIRARSE CON EL BOTÍN",
                                onClick = { viewModel.exitDungeonRun() },
                                tone = EldoriaTone.Iron,
                                size = EldoriaButtonSize.Medium,
                                icon = Icons.Default.DirectionsRun,
                                fullWidth = true
                            )
                        }
                    } else {
                        CombatOutcomePanel(
                            title = "VICTORIA HEROICA",
                            message = "La bestia ha caído.",
                            tone = EldoriaTone.Gold,
                            edge = EldoriaEdge.Gold
                        ) {
                            EldoriaButton(
                                text = "REGRESAR AL MAPA",
                                onClick = { viewModel.exitCombatScreen() },
                                tone = EldoriaTone.Gold,
                                size = EldoriaButtonSize.Large,
                                fullWidth = true,
                                testTag = "exit_combat_button"
                            )
                        }
                    }
                } else {
                    val won = combatState.victory == true
                    CombatOutcomePanel(
                        title = if (won) "VICTORIA HEROICA" else "HAS CAÍDO",
                        message = if (won) "La bestia ha caído." else "Tus heridas pudieron contigo. Vuelve más fuerte.",
                        tone = if (won) EldoriaTone.Gold else EldoriaTone.Blood,
                        edge = if (won) EldoriaEdge.Gold else EldoriaEdge.Blood
                    ) {
                        EldoriaButton(
                            text = if (dungeonRun.inDungeonRun) "SALIR DEL CALABOZO" else "REGRESAR AL MAPA",
                            onClick = {
                                if (dungeonRun.inDungeonRun) {
                                    viewModel.exitDungeonRun()
                                } else {
                                    viewModel.exitCombatScreen()
                                }
                            },
                            tone = if (won) EldoriaTone.Gold else EldoriaTone.Iron,
                            size = EldoriaButtonSize.Large,
                            fullWidth = true,
                            testTag = "exit_combat_button"
                        )
                    }
                }
            } else {
                val inventory = GameJsonParser.listFromJson<Item>(p.inventoryJson)
                val potionCount = inventory.count { it.type == "POTION" }
                val classSkills = GameJsonParser.listFromJson<Skill>(p.skillsJson)

                // El inventario guarda un Item POR UNIDAD, así que hay que
                // agrupar para enseñar "Poción Menor ×7" y no siete filas.
                val potionStacks = remember(p.inventoryJson) {
                    inventory.filter { it.type == "POTION" }
                        .map { EldoriaPotions.fromItem(it.id, it.name) }
                        .groupingBy { it }
                        .eachCount()
                        .map { (spec, count) -> PotionStack(spec, count) }
                        .sortedBy { it.spec.unlockLevel }
                }
                var potionDrawerOpen by remember { mutableStateOf(false) }
                val heroAction = basicAttackFor(p.charClass)

                // El contenedor de acciones es un Box, y un Box APILA a sus
                // hijos: por eso la barra de habilidades se dibujaba ENCIMA del
                // cajón de pociones en vez de debajo. La Column los ordena.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                // Efectos activos: sin esto un buff de cuatro turnos es
                // invisible en cuanto pasa la línea del registro.
                CombatBuffChips(
                    regenTurns = combatState.regenTurns,
                    damageTurns = combatState.damageBuffTurns,
                    damagePotency = combatState.damageBuffPotency,
                    evasionTurns = combatState.evasionTurns,
                    evasionPotency = combatState.evasionPotency,
                    wardTurns = combatState.wardTurns,
                    wardPotency = combatState.wardPotency,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                if (potionDrawerOpen) {
                    CombatPotionDrawer(
                        stacks = potionStacks,
                        enabled = combatState.playerTurn,
                        onPick = { spec ->
                            potionDrawerOpen = false
                            viewModel.usePotionCombat(spec.id)
                        },
                        onDismiss = { potionDrawerOpen = false },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

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
                        // El golpe básico lleva el nombre y el icono de TU clase:
                        // un Mago no "ataca físicamente" con un puño dibujado.
                        StainedGlassSkillSlot(
                            title = heroAction.label,
                            badgeLabel = heroAction.badge,
                            costText = "0",
                            icon = heroAction.icon,
                            glassTheme = when (p.charClass) {
                                "Mago" -> SkillGlassTheme.PURPLE
                                "Pícaro" -> SkillGlassTheme.TURQUOISE
                                "Clérigo" -> SkillGlassTheme.AMBER
                                else -> SkillGlassTheme.CRIMSON
                            },
                            enabled = combatState.playerTurn,
                            testTag = "combat_attack_button",
                            onClick = { viewModel.executeBasicAttack() },
                            artKey = "action_basic_" + when (p.charClass) {
                                "Mago" -> "mago"
                                "Pícaro" -> "picaro"
                                "Clérigo" -> "clerigo"
                                else -> "guerrero"
                            }
                        )

                        // 2. Class Skills — el color del cristal y el icono siguen
                        // al ELEMENTO de la habilidad, el mismo que verás estallar
                        // sobre el enemigo al pulsarla.
                        classSkills.forEachIndexed { index, skill ->
                            val isSkillEnabled = combatState.playerTurn && combatState.playerCurrentMp >= skill.manaCost
                            val skillFx = combatFxForSkill(
                                skillId = skill.id,
                                healing = skill.healingMultiplier > 0.0,
                                damaging = skill.damageMultiplier > 0.0
                            )
                            val skillTheme = when (skillFx) {
                                CombatFx.BLOOD -> SkillGlassTheme.CRIMSON
                                CombatFx.FIRE -> SkillGlassTheme.AMBER
                                CombatFx.NECROTIC, CombatFx.POISON, CombatFx.HEAL -> SkillGlassTheme.EMERALD
                                CombatFx.HOLY, CombatFx.WARCRY -> SkillGlassTheme.AMBER
                                CombatFx.FROST -> SkillGlassTheme.TURQUOISE
                                else -> SkillGlassTheme.PURPLE
                            }
                            val skillIcon = when (skillFx) {
                                CombatFx.BLOOD -> Icons.Default.Gavel
                                CombatFx.WARCRY -> Icons.Default.Shield
                                CombatFx.ARCANE -> Icons.Default.AutoAwesome
                                CombatFx.NECROTIC, CombatFx.FIRE -> Icons.Default.LocalFireDepartment
                                CombatFx.POISON -> Icons.Default.Science
                                CombatFx.SHADOW -> Icons.Default.VisibilityOff
                                CombatFx.HOLY -> Icons.Default.WbSunny
                                CombatFx.HEAL -> Icons.Default.Favorite
                                CombatFx.FROST -> Icons.Default.AcUnit
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
                                onClick = { viewModel.executeSkill(skill) },
                                artKey = "skill_${skill.id}"
                            )
                        }

                        // 3. Potion Slot (Emerald Green Stained Glass)
                        // Abre el zurrón en vez de beber a ciegas: con seis
                        // frascos, ELEGIR cuál bebes es la jugada.
                        StainedGlassSkillSlot(
                            title = "Zurrón",
                            badgeLabel = "${potionStacks.size} tipos",
                            costText = "x$potionCount",
                            icon = Icons.Default.LocalPharmacy,
                            glassTheme = SkillGlassTheme.EMERALD,
                            enabled = combatState.playerTurn && potionCount > 0,
                            testTag = "combat_potion_button",
                            onClick = { potionDrawerOpen = !potionDrawerOpen },
                            artKey = "action_zurron"
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
                            onClick = { viewModel.fleeCombat() },
                            artKey = "action_huir"
                        )
                    }
                }
                }
            }
        }
    }
    }
}

/** Insignia de mascota en el foso: retrato, nombre corto y nivel. */
@Composable
private fun CombatPetBadge(
    imageRes: Int,
    name: String,
    level: Int,
    accent: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(5.dp))
            .background(Eldoria.Abyss.copy(alpha = 0.85f))
            .border(Eldoria.StrokeThin, accent.copy(alpha = 0.75f), CutCornerShape(5.dp))
            .padding(horizontal = 5.dp, vertical = 3.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(0.75.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(Eldoria.S4))
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = name.split(" ").first(),
                style = EldoriaType.caption,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Nv.$level",
                style = EldoriaType.caption,
                color = Eldoria.TextLow,
                maxLines = 1
            )
        }
    }
}

/** Cierre del combate: victoria, derrota o etapa superada, con su acción. */
@Composable
private fun CombatOutcomePanel(
    title: String,
    message: String,
    tone: EldoriaTone,
    edge: EldoriaEdge,
    actions: @Composable ColumnScope.() -> Unit
) {
    EldoriaPanel(
        modifier = Modifier.fillMaxWidth(),
        edge = edge,
        corner = Eldoria.R12,
        padding = PaddingValues(14.dp),
        glow = true,
        filigree = true
    ) {
        Text(
            text = title,
            style = EldoriaType.title,
            color = Eldoria.toneColor(tone),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(Eldoria.S4))
        Text(
            text = message,
            style = EldoriaType.lore,
            color = Eldoria.TextMid,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(Eldoria.S12))
        actions()
    }
}

// --- CHARACTER PROFILE SCREEN ---
@Composable
fun CharacterScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val p = progress ?: return
    val playerStats by viewModel.playerStats.collectAsState()
    val allCharacters by viewModel.allCharactersState.collectAsState()
    val backupStatus by viewModel.backupStatus.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshBackupStatus()
    }

    val unspentStats = viewModel.getUnspentStatPoints(p)
    var showCharSelectionDialog by remember { mutableStateOf(false) }

    EldoriaScreen(
        depth = 1,
        embers = false,
        fog = true,
        vignetteStrength = 0.6f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // Aura del héroe: dorada de serie, arcana si ya ascendió de clase.
        EldoriaTorchLight(
            modifier = Modifier.matchParentSize(),
            intensity = 0.5f,
            warm = if (p.hasAdvancedClass) Eldoria.ArcaneBright else Eldoria.Gold,
            flicker = false,
            centerX = 0.5f,
            centerY = 0.18f
        )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Eldoria.S12),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        // ── Character Portrait & Identity Card ──
        item {
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = if (p.hasAdvancedClass) EldoriaEdge.Arcane else EldoriaEdge.Gold,
                corner = Eldoria.R16,
                padding = PaddingValues(14.dp),
                glow = true,
                filigree = true
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Retrato: marco metálico con remaches y latido del halo.
                    EldoriaFrame(
                        modifier = Modifier.size(132.dp),
                        edge = if (p.hasAdvancedClass) EldoriaEdge.Arcane else EldoriaEdge.Gold,
                        corner = Eldoria.R12,
                        strokeWidth = Eldoria.StrokeBold,
                        filigree = true,
                        rivets = true,
                        glowPulse = p.hasAdvancedClass
                    ) {
                        Image(
                            painter = painterResource(id = getCharacterPortrait(p.charRace, p.charClass, p.hasAdvancedClass)),
                            contentDescription = "Portrait",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Chapa de nivel remachada en la esquina del retrato.
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(5.dp)
                                .clip(CutCornerShape(5.dp))
                                .background(Eldoria.Abyss.copy(alpha = 0.9f))
                                .border(Eldoria.StrokeThin, Eldoria.goldEdge(), CutCornerShape(5.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "NV ${p.charLevel}",
                                style = EldoriaType.numeric,
                                color = Eldoria.GoldBright
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(Eldoria.S12))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            p.charName,
                            style = EldoriaType.title,
                            color = Eldoria.TextHi,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            if (p.hasAdvancedClass) p.advancedClassName else "${p.charRace} · ${p.charClass}",
                            style = EldoriaType.small,
                            color = if (p.hasAdvancedClass) Eldoria.ArcaneBright else Eldoria.TextGold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(Eldoria.S8))

                        EldoriaResourceBar(
                            current = p.currentHp,
                            max = p.maxHp,
                            tone = EldoriaBarTone.Health,
                            label = "VIDA",
                            icon = Icons.Default.Favorite,
                            height = 15.dp,
                            dangerPulse = p.maxHp > 0 && p.currentHp.toFloat() / p.maxHp < 0.3f
                        )

                        Spacer(modifier = Modifier.height(Eldoria.S6))

                        EldoriaResourceBar(
                            current = p.currentMp,
                            max = p.maxMp,
                            tone = EldoriaBarTone.Mana,
                            label = "MANÁ",
                            icon = Icons.Default.AutoAwesome,
                            height = 15.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Eldoria.S12))
                EldoriaDivider(color = if (p.hasAdvancedClass) Eldoria.Arcane else Eldoria.Gold)
                Spacer(modifier = Modifier.height(Eldoria.S8))

                // EXP Bar — Animated Medieval style
                val nextLvlExp = com.example.data.getRequiredExpForLevel(p.charLevel)
                val expPercent = if (nextLvlExp > 0) ((p.charExp.toFloat() / nextLvlExp) * 100).toInt() else 0
                EldoriaResourceBar(
                    current = p.charExp,
                    max = nextLvlExp,
                    tone = EldoriaBarTone.Experience,
                    label = "EXPERIENCIA · $expPercent% al nivel ${p.charLevel + 1}",
                    icon = Icons.Default.TrendingUp,
                    height = 18.dp
                )
            }
        }

        if (p.charLevel >= 20 || p.hasAdvancedClass) {
            item {
                EldoriaPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("class_advancement_card"),
                    edge = EldoriaEdge.Arcane,
                    corner = Eldoria.R12,
                    padding = PaddingValues(12.dp),
                    glow = !p.hasAdvancedClass,
                    filigree = true,
                    onClick = if (!p.hasAdvancedClass) ({ viewModel.advanceClass() }) else null
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EldoriaFrame(
                            modifier = Modifier.size(64.dp),
                            edge = EldoriaEdge.Arcane,
                            corner = Eldoria.R8,
                            strokeWidth = Eldoria.StrokeMed,
                            filigree = false,
                            glowPulse = !p.hasAdvancedClass
                        ) {
                            Image(
                                painter = painterResource(id = getCharacterPortrait(p.charRace, p.charClass, true)),
                                contentDescription = "Avatar Alado",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(Eldoria.S12))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                if (p.hasAdvancedClass) "ASCENSIÓN COMPLETADA" else "ASCENSIÓN DISPONIBLE",
                                style = EldoriaType.label,
                                color = Eldoria.ArcaneBright
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                if (p.hasAdvancedClass)
                                    "Forma alada: ${p.advancedClassName}. Atributos ×2 y definitiva ×5 activas."
                                else
                                    "Nivel 20 alcanzado: alas, atributos ×2 y habilidad definitiva ×5 te esperan.",
                                style = EldoriaType.small,
                                color = Eldoria.TextMid,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (!p.hasAdvancedClass) {
                            Spacer(modifier = Modifier.width(Eldoria.S8))
                            EldoriaButton(
                                text = "ASCENDER",
                                onClick = { viewModel.advanceClass() },
                                tone = EldoriaTone.Arcane,
                                size = EldoriaButtonSize.Small,
                                icon = Icons.Default.AutoAwesome
                            )
                        }
                    }
                }
            }
        }

        item {
            EldoriaBanner(
                title = "ÁRBOL DE TALENTOS",
                subtitle = if (p.talentPointsAvailable > 0)
                    "${p.talentPointsAvailable} punto${if (p.talentPointsAvailable == 1) "" else "s"} sin gastar"
                else
                    "Todo canalizado. Sube de nivel para seguir.",
                modifier = Modifier
                    .eldoriaPressable(onClick = { viewModel.changeScreen(GameScreen.TALENTS) })
                    .testTag("open_talents_tree_button"),
                artRes = R.drawable.talent_tree_banner_1784843563984,
                height = 82.dp,
                edge = if (p.talentPointsAvailable > 0) EldoriaEdge.Gold else EldoriaEdge.Iron,
                trailing = {
                    if (p.talentPointsAvailable > 0) {
                        EldoriaStatPill(
                            label = "PUNTOS",
                            value = "${p.talentPointsAvailable}",
                            icon = Icons.Default.Bolt,
                            accent = Eldoria.Gold
                        )
                    } else {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Eldoria.TextLow,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
        }

        item {
            val fullEvolvedTitle = viewModel.getFullEvolvedTitle(p.charRace, p.charClass, p.charLevel)
            val passiveDesc = viewModel.getRacePassiveDescription(p.charRace, p.charLevel)
            val isEvolved = p.charLevel >= 20
            val evoPortraitRes = getCharacterPortrait(p.charRace, p.charClass, p.hasAdvancedClass, p.charLevel)
            
            val (nextLevelTarget, levelStageName) = when {
                p.charLevel >= 100 -> Pair(100, "3ª EVOLUCIÓN SUPREMA")
                p.charLevel >= 50 -> Pair(100, "2ª EVOLUCIÓN (Siguiente a Nivel 100)")
                p.charLevel >= 20 -> Pair(50, "1ª EVOLUCIÓN (Siguiente a Nivel 50)")
                else -> Pair(20, "FASE BÁSICA (Siguiente a Nivel 20)")
            }

            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = if (isEvolved) EldoriaEdge.Gold else EldoriaEdge.Iron,
                corner = Eldoria.R12,
                padding = PaddingValues(14.dp),
                filigree = isEvolved
            ) {
                EldoriaSectionTitle(
                    text = "EVOLUCIÓN",
                    icon = Icons.Default.AutoAwesome,
                    accent = if (isEvolved) Eldoria.Gold else Eldoria.IronEdge,
                    trailing = {
                        EldoriaChip(
                            text = levelStageName.uppercase(),
                            color = if (isEvolved) Eldoria.Gold else Eldoria.TextLow,
                            filled = isEvolved
                        )
                    }
                )

                Spacer(modifier = Modifier.height(Eldoria.S12))

                // Hero Evolution Visual Artwork Preview
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EldoriaFrame(
                        modifier = Modifier.size(74.dp),
                        edge = if (isEvolved) EldoriaEdge.Gold else EldoriaEdge.Iron,
                        corner = Eldoria.R8,
                        strokeWidth = Eldoria.StrokeMed,
                        filigree = false,
                        rivets = true
                    ) {
                        Image(
                            painter = painterResource(id = evoPortraitRes),
                            contentDescription = "Evolución de Héroe",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(Eldoria.S12))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fullEvolvedTitle,
                            style = EldoriaType.subheading,
                            color = if (isEvolved) Eldoria.TextGold else Eldoria.TextMid,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Umbrales: nivel 20, 50 y 100.",
                            style = EldoriaType.caption,
                            color = Eldoria.TextLow
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Eldoria.S12))

                Text(
                    text = passiveDesc,
                    style = EldoriaType.lore,
                    color = Eldoria.TextMid
                )

                Spacer(modifier = Modifier.height(Eldoria.S12))

                if (p.charLevel < 100) {
                    val currentProgressInTier = p.charLevel.coerceAtMost(nextLevelTarget)
                    EldoriaResourceBar(
                        current = currentProgressInTier,
                        max = nextLevelTarget,
                        tone = EldoriaBarTone.Momentum,
                        label = "HACIA LA SIGUIENTE FORMA",
                        icon = Icons.Default.TrendingUp,
                        height = 14.dp
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CutCornerShape(8.dp))
                            .background(Eldoria.GlowGold)
                            .border(Eldoria.StrokeThin, Eldoria.goldEdge(), CutCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Eldoria.GoldBright,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(Eldoria.S8))
                        Text(
                            text = "3ª Evolución Suprema alcanzada. No hay forma superior conocida.",
                            style = EldoriaType.small,
                            color = Eldoria.TextGold
                        )
                    }
                }
            }
        }

        item {
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = if (unspentStats > 0) EldoriaEdge.Gold else EldoriaEdge.Silver,
                corner = Eldoria.R12,
                padding = PaddingValues(14.dp),
                glow = unspentStats > 0
            ) {
                EldoriaSectionTitle(
                    text = "ATRIBUTOS",
                    icon = Icons.Default.MilitaryTech,
                    accent = Eldoria.Gold,
                    trailing = {
                        if (unspentStats > 0) {
                            EldoriaButton(
                                text = "AUTO",
                                onClick = { viewModel.autoAllocateAllStatPoints() },
                                tone = EldoriaTone.Gold,
                                size = EldoriaButtonSize.Small,
                                icon = Icons.Default.AutoMode,
                                testTag = "btn_auto_allocate_hero"
                            )
                        }
                    }
                )

                if (unspentStats > 0) {
                    Spacer(modifier = Modifier.height(Eldoria.S8))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CutCornerShape(6.dp))
                            .background(Eldoria.GlowGold)
                            .border(Eldoria.StrokeThin, Eldoria.goldEdge(), CutCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Eldoria.GoldBright,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(Eldoria.S6))
                        Text(
                            "$unspentStats PUNTO${if (unspentStats == 1) "" else "S"} SIN REPARTIR",
                            style = EldoriaType.label,
                            color = Eldoria.GoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Eldoria.S12))

                val statRows = listOf(
                    Quadruple("STR", "Fuerza · daño físico", p.statStr, Icons.Default.FitnessCenter),
                    Quadruple("DEX", "Destreza · crítico y evasión", p.statDex, Icons.Default.DirectionsRun),
                    Quadruple("INT", "Inteligencia · daño mágico y maná", p.statInt, Icons.Default.Psychology),
                    Quadruple("CON", "Constitución · salud máxima", p.statCon, Icons.Default.Favorite)
                )
                val statAccents = mapOf(
                    "STR" to Eldoria.BloodBright,
                    "DEX" to Eldoria.VitaeBright,
                    "INT" to Eldoria.ManaBright,
                    "CON" to Eldoria.EmberCore
                )

                statRows.forEach { (code, name, value, icon) ->
                    val accent = statAccents[code] ?: Eldoria.Gold
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CutCornerShape(6.dp))
                                .background(Eldoria.PanelSunken)
                                .border(Eldoria.StrokeThin, accent.copy(alpha = 0.6f), CutCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, code, tint = accent, modifier = Modifier.size(17.dp))
                        }
                        Spacer(modifier = Modifier.width(Eldoria.S8))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(code, style = EldoriaType.subheading, color = Eldoria.TextHi)
                            Text(
                                name,
                                style = EldoriaType.caption,
                                color = Eldoria.TextLow,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = "$value",
                            style = EldoriaType.numericBig,
                            color = accent,
                            modifier = Modifier.padding(end = Eldoria.S8)
                        )

                        if (unspentStats > 0) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Eldoria.goldEdge())
                                    .eldoriaPressable(onClick = { viewModel.allocateStatPoint(code) })
                                    .testTag("allocate_stat_$code"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    "Asignar",
                                    tint = Eldoria.TextOnGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Multi-Character Management Section (Replaces Old Reset Game Button)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Eldoria.S8)
            ) {
                EldoriaButton(
                    text = "CREAR OTRO PERSONAJE",
                    onClick = { viewModel.startNewCharacterCreator() },
                    tone = EldoriaTone.Gold,
                    size = EldoriaButtonSize.Medium,
                    icon = Icons.Default.PersonAdd,
                    fullWidth = true,
                    testTag = "create_new_character_btn"
                )
                EldoriaButton(
                    text = "MIS HÉROES (${allCharacters.size})",
                    onClick = { showCharSelectionDialog = true },
                    tone = EldoriaTone.Iron,
                    size = EldoriaButtonSize.Medium,
                    icon = Icons.Default.People,
                    fullWidth = true,
                    testTag = "switch_character_btn"
                )
            }
        }

        // Backup & Save Data Recovery Section
        item {
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = EldoriaEdge.Silver,
                corner = Eldoria.R12,
                padding = PaddingValues(14.dp)
            ) {
                EldoriaSectionTitle(
                    text = "COPIA DE SEGURIDAD",
                    icon = Icons.Default.Backup,
                    accent = Eldoria.Silver
                )

                Spacer(modifier = Modifier.height(Eldoria.S8))

                Text(
                    text = "El juego guarda copias automáticas fuera de su carpeta de datos: si reinstalas o actualizas, tu héroe vuelve intacto.",
                    style = EldoriaType.small,
                    color = Eldoria.TextMid
                )

                if (backupStatus.isNotBlank()) {
                    Spacer(modifier = Modifier.height(Eldoria.S8))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Eldoria.Info,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(Eldoria.S6))
                        Text(
                            text = backupStatus,
                            style = EldoriaType.caption,
                            color = Eldoria.Info
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Eldoria.S12))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                ) {
                    EldoriaButton(
                        text = "GUARDAR",
                        onClick = {
                            SoundManager.playButtonClick()
                            viewModel.exportManualBackup()
                        },
                        modifier = Modifier.weight(1f),
                        tone = EldoriaTone.Gold,
                        size = EldoriaButtonSize.Small,
                        icon = Icons.Default.Save,
                        fullWidth = true,
                        testTag = "export_backup_btn"
                    )
                    EldoriaButton(
                        text = "RESTAURAR",
                        onClick = {
                            SoundManager.playButtonClick()
                            viewModel.restoreManualBackup()
                        },
                        modifier = Modifier.weight(1f),
                        tone = EldoriaTone.Vitae,
                        size = EldoriaButtonSize.Small,
                        icon = Icons.Default.Restore,
                        fullWidth = true,
                        testTag = "restore_backup_btn"
                    )
                }
            }
        }
    }

    // Modal Dialog for Multi-Character Switch / Selection
    if (showCharSelectionDialog) {
        Dialog(onDismissRequest = { showCharSelectionDialog = false }) {
            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                edge = EldoriaEdge.Gold,
                corner = Eldoria.R16,
                padding = PaddingValues(14.dp),
                glow = true,
                filigree = true
            ) {
                Text(
                    "MIS HÉROES",
                    style = EldoriaType.title,
                    color = Eldoria.TextGold
                )
                Spacer(modifier = Modifier.height(Eldoria.S4))
                Text(
                    "Elige a quién controlas o borra una partida antigua.",
                    style = EldoriaType.small,
                    color = Eldoria.TextMid
                )

                Spacer(modifier = Modifier.height(Eldoria.S8))
                EldoriaDivider(color = Eldoria.Gold)
                Spacer(modifier = Modifier.height(Eldoria.S8))

                Column(
                    verticalArrangement = Arrangement.spacedBy(Eldoria.S8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    allCharacters.forEach { charItem ->
                        val isCurrent = charItem.id == p.id
                        EldoriaPanel(
                            modifier = Modifier.fillMaxWidth(),
                            edge = if (isCurrent) EldoriaEdge.Gold else EldoriaEdge.Iron,
                            corner = Eldoria.R8,
                            padding = PaddingValues(9.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                EldoriaFrame(
                                    modifier = Modifier.size(42.dp),
                                    edge = if (isCurrent) EldoriaEdge.Gold else EldoriaEdge.Iron,
                                    corner = Eldoria.R4,
                                    strokeWidth = Eldoria.StrokeThin,
                                    filigree = false
                                ) {
                                    Image(
                                        painter = painterResource(
                                            id = getCharacterPortrait(
                                                charItem.charRace,
                                                charItem.charClass,
                                                charItem.hasAdvancedClass,
                                                charItem.charLevel
                                            )
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.width(Eldoria.S8))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            charItem.charName,
                                            style = EldoriaType.subheading,
                                            color = if (isCurrent) Eldoria.TextGold else Eldoria.TextHi,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        if (isCurrent) {
                                            Spacer(modifier = Modifier.width(Eldoria.S6))
                                            EldoriaChip(
                                                text = "ACTIVO",
                                                color = Eldoria.Success,
                                                filled = true
                                            )
                                        }
                                    }
                                    Text(
                                        "${charItem.charRace} ${charItem.charClass} · Nv.${charItem.charLevel}",
                                        style = EldoriaType.caption,
                                        color = Eldoria.TextLow,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (!isCurrent) {
                                    Spacer(modifier = Modifier.width(Eldoria.S6))
                                    EldoriaButton(
                                        text = "JUGAR",
                                        onClick = {
                                            viewModel.selectCharacter(charItem.id)
                                            showCharSelectionDialog = false
                                        },
                                        tone = EldoriaTone.Gold,
                                        size = EldoriaButtonSize.Small
                                    )
                                }

                                if (allCharacters.size > 1) {
                                    Spacer(modifier = Modifier.width(Eldoria.S4))
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CutCornerShape(5.dp))
                                            .background(Eldoria.PanelSunken)
                                            .border(Eldoria.StrokeThin, Eldoria.Blood.copy(alpha = 0.7f), CutCornerShape(5.dp))
                                            .eldoriaPressable(onClick = { viewModel.deleteCharacter(charItem.id) }),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            "Borrar",
                                            tint = Eldoria.BloodBright,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Eldoria.S12))

                EldoriaButton(
                    text = "CERRAR",
                    onClick = { showCharSelectionDialog = false },
                    tone = EldoriaTone.Iron,
                    size = EldoriaButtonSize.Medium,
                    fullWidth = true
                )
            }
        }
    }
    } // end EldoriaScreen wrapper
}

// ═══════════════════════════════════════════════════════════════════════════
//  RED DE TALENTOS
//
//  El árbol vive en `com.example.ui.talents`: con cien nodos por raza dejó de
//  ser una pantalla y pasó a ser un sistema (ramas, escalones, evoluciones,
//  láminas), y arrastrarlo aquí dentro sólo engordaba este fichero.
//  Esta función se mantiene como puerta de entrada para no tocar el enrutador.
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun TalentsScreen(viewModel: GameViewModel) {
    EldoriaTalentTreeScreen(viewModel)
}

@Composable
fun MuEquipmentSlot(
    label: String,
    code: String,
    item: Item?,
    modifier: Modifier = Modifier,
    onUnequip: () -> Unit
) {
    // Engaste de equipo: el marco lleva la rareza, la gema la confirma y el
    // hueco vacío enseña la silueta apagada de lo que le falta.
    EldoriaSlotFrame(
        modifier = modifier,
        rarity = item?.rarity,
        level = item?.itemLevel,
        size = 64.dp,
        onClick = if (item != null) onUnequip else null,
        testTag = "equip_slot_$code"
    ) {
        if (item != null) {
            Image(
                painter = painterResource(id = getItemImageRes(item.imageResName, item.type)),
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Hueco vacío: silueta apagada de la pieza que falta + su nombre.
            Image(
                painter = painterResource(id = getItemImageRes("", code)),
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.16f
            )
            Text(
                text = label,
                style = EldoriaType.caption,
                color = Eldoria.TextLow,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Eldoria.Abyss.copy(alpha = 0.75f))
                    .padding(vertical = 1.dp)
            )
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
            "Mascotas" -> item.type == "PET"
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
    EldoriaToggleChip(
        text = label,
        selected = isSelected,
        onClick = onClick,
        accent = Eldoria.Gold
    )
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

    EldoriaPanel(
        modifier = modifier.fillMaxWidth(),
        edge = if (isAnyFilterActive) EldoriaEdge.Gold else EldoriaEdge.Iron,
        corner = Eldoria.R8,
        padding = PaddingValues(8.dp)
    ) {
        // Main Top Bar: Search Input + Toggle Expand + Clear Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
        ) {
            // Free text search field
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(CutCornerShape(7.dp))
                    .background(Eldoria.PanelSunken)
                    .border(Eldoria.StrokeThin, Eldoria.ironEdge(), CutCornerShape(7.dp))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Eldoria.TextGold,
                    modifier = Modifier.size(15.dp)
                )
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    textStyle = EldoriaType.small.copy(color = Eldoria.TextHi),
                    singleLine = true,
                    cursorBrush = SolidColor(Eldoria.Gold),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("inventory_search_input"),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                "Buscar objeto…",
                                style = EldoriaType.small,
                                color = Eldoria.TextLow
                            )
                        }
                        innerTextField()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpiar búsqueda",
                        tint = Eldoria.TextMid,
                        modifier = Modifier
                            .size(15.dp)
                            .eldoriaPressable(onClick = { onSearchQueryChange("") })
                    )
                }
            }

            // Expand/Collapse Button for detailed category filters
            EldoriaToggleChip(
                text = if (isExpanded) "FILTROS" else "FILTROS",
                selected = isExpanded || isAnyCategoryFilterActive,
                onClick = { isExpanded = !isExpanded },
                accent = Eldoria.Gold,
                icon = Icons.Default.FilterList
            )

            if (isAnyFilterActive) {
                EldoriaChip(
                    text = "LIMPIAR",
                    color = Eldoria.Danger,
                    icon = Icons.Default.Close,
                    modifier = Modifier.eldoriaPressable(
                        onClick = {
                            onSearchQueryChange("")
                            onSelectRarity("Todas")
                            onSelectType("Todos")
                            onSelectLevel("Todos")
                        }
                    )
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
                    .padding(top = Eldoria.S8),
                verticalArrangement = Arrangement.spacedBy(Eldoria.S8)
            ) {
                InventoryFilterRow(
                    label = "RAREZA",
                    options = listOf("Todas", "Común", "Raro", "Épico", "Legendario", "Arcano/Universal"),
                    selected = selectedRarity,
                    onSelect = onSelectRarity
                )
                InventoryFilterRow(
                    label = "TIPO",
                    options = listOf("Todos", "Armas", "Pechera", "Escudo", "Casco", "Alas", "Guantes", "Botas", "Anillos/Joyas", "Mascotas", "Pociones"),
                    selected = selectedType,
                    onSelect = onSelectType
                )
                InventoryFilterRow(
                    label = "NIVEL",
                    options = listOf("Todos", "Niv. 1-5", "Niv. 6-10", "Niv. 11-20", "Niv. 21+"),
                    selected = selectedLevel,
                    onSelect = onSelectLevel
                )
            }
        }
    }
}

/** Fila de filtro: rótulo fijo a la izquierda y píldoras desplazables. */
@Composable
private fun InventoryFilterRow(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = EldoriaType.label,
            color = Eldoria.TextLow
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
        ) {
            options.forEach { option ->
                FilterPill(
                    label = option,
                    isSelected = selected == option,
                    onClick = { onSelect(option) }
                )
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
    val pet = GameJsonParser.fromJson<Item>(p.equippedPetJson)

    EldoriaScreen(
        depth = 1,
        embers = false,
        fog = true,
        vignetteStrength = 0.58f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EldoriaSectionTitle(
            text = "EQUIPO",
            icon = Icons.Default.Shield,
            accent = Eldoria.Gold,
            trailing = {
                EldoriaButton(
                    text = "EQUIPAR AUTO",
                    onClick = { viewModel.autoEquip() },
                    tone = EldoriaTone.Gold,
                    size = EldoriaButtonSize.Small,
                    icon = Icons.Default.AutoMode,
                    testTag = "auto_equip_button"
                )
            }
        )
        Spacer(modifier = Modifier.height(Eldoria.S8))

        // MU Online Equipment Matrix Panel
        EldoriaPanel(
            modifier = Modifier.fillMaxWidth(),
            edge = EldoriaEdge.Gold,
            corner = Eldoria.R12,
            padding = PaddingValues(10.dp),
            filigree = true
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Eldoria.S6)
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

                // Row 4: Ring, Pet, Earring
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MuEquipmentSlot("Anillo", "RING", ring) { viewModel.unequipItem("RING") }
                    MuEquipmentSlot("Mascota", "PET", pet) { viewModel.unequipItem("PET") }
                    MuEquipmentSlot("Pendientes", "EARRING", earring) { viewModel.unequipItem("EARRING") }
                }
            }
        }

        Spacer(modifier = Modifier.height(Eldoria.S12))

        EldoriaSectionTitle(
            text = "MOCHILA",
            icon = Icons.Default.Inventory2,
            accent = Eldoria.Silver,
            trailing = {
                if (filteredInventory.isNotEmpty()) {
                    EldoriaButton(
                        text = "VENDER ${formatGameNumber(massSellTotalPrice)}",
                        onClick = {
                            SoundManager.playButtonClick()
                            showMassSellConfirmation = true
                        },
                        tone = EldoriaTone.Blood,
                        size = EldoriaButtonSize.Small,
                        icon = Icons.Default.Sell,
                        testTag = "mass_sell_button"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(Eldoria.S8))

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

        Spacer(modifier = Modifier.height(Eldoria.S8))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (filteredInventory.isEmpty()) {
                val filtered = selectedRarityFilter != "Todas" || selectedTypeFilter != "Todos" || selectedLevelFilter != "Todos"
                EldoriaEmptyState(
                    modifier = Modifier.align(Alignment.Center),
                    title = if (rawInventory.isEmpty()) "Mochila vacía" else "Nada coincide",
                    message = if (rawInventory.isEmpty())
                        "No llevas nada encima. El botín espera ahí fuera."
                    else
                        "Ningún objeto pasa los filtros que has puesto.",
                    icon = Icons.Default.Inventory2,
                    accent = Eldoria.Gold,
                    actionLabel = if (filtered) "Restablecer filtros" else null,
                    onAction = if (filtered) fun() {
                        selectedRarityFilter = "Todas"
                        selectedTypeFilter = "Todos"
                        selectedLevelFilter = "Todos"
                    } else null
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S6),
                    verticalArrangement = Arrangement.spacedBy(Eldoria.S6),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(filteredInventory) { item ->
                        var expandedDetail by remember { mutableStateOf(false) }

                        // Cada objeto es un engaste con gema de rareza y chapa
                        // de nivel: se distingue el botín bueno sin abrir nada.
                        EldoriaSlotFrame(
                            modifier = Modifier.aspectRatio(1f),
                            rarity = item.rarity,
                            level = item.itemLevel,
                            size = 200.dp,
                            onClick = { expandedDetail = true },
                            // La pulsación larga lleva a la misma ficha: en la
                            // rejilla sólo cabe el nombre, ni stats ni pasivas.
                            onLongClick = { expandedDetail = true },
                            testTag = "inv_item_${item.id}"
                        ) {
                            Image(
                                painter = painterResource(id = getItemImageRes(item.imageResName, item.type)),
                                contentDescription = item.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                item.name,
                                style = EldoriaType.caption,
                                color = Eldoria.rarityColor(item.rarity),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Eldoria.Abyss.copy(alpha = 0.82f))
                                    .padding(vertical = 1.dp, horizontal = 2.dp)
                            )
                        }

                        if (expandedDetail) {
                            InventoryItemDialog(
                                item = item,
                                playerLevel = p.charLevel,
                                sellPrice = viewModel.calculateSellPrice(item),
                                onEquip = {
                                    viewModel.equipItem(item)
                                    expandedDetail = false
                                },
                                onSell = {
                                    viewModel.sellItem(item)
                                    expandedDetail = false
                                },
                                onDiscard = {
                                    viewModel.discardItem(item)
                                    expandedDetail = false
                                },
                                onDismiss = { expandedDetail = false }
                            )
                        }
                    }
                }
            }
        }

        if (showMassSellConfirmation) {
            val filterSummary = buildString {
                if (searchQuery.isNotBlank()) append("Búsqueda «$searchQuery» · ")
                append("Rareza $selectedRarityFilter · Tipo $selectedTypeFilter · Nivel $selectedLevelFilter")
            }
            EldoriaConfirmDialog(
                title = "Venta masiva",
                message = "Vas a vender ${filteredInventory.size} objetos por ${formatGameNumber(massSellTotalPrice)} de oro.\n\nFiltros: $filterSummary\n\nEsto no se deshace.",
                confirmLabel = "VENDER TODO",
                onConfirm = {
                    viewModel.massSellItems(filteredInventory)
                    showMassSellConfirmation = false
                },
                onDismiss = { showMassSellConfirmation = false },
                tone = EldoriaTone.Blood,
                testTagPrefix = "inv_mass_sell"
            )
        }
    }
    }
}

/** Ficha de objeto: descripción, atributos y las tres acciones posibles. */
@Composable
fun InventoryItemDialog(
    item: Item,
    playerLevel: Int,
    sellPrice: Int,
    onEquip: (() -> Unit)? = null,
    onSell: (() -> Unit)? = null,
    onDiscard: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val canEquip = item.itemLevel <= playerLevel
    val rarityColor = Eldoria.rarityColor(item.rarity)

    Dialog(onDismissRequest = onDismiss) {
        EldoriaPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            edge = EldoriaEdge.rarity(item.rarity),
            corner = Eldoria.R16,
            padding = PaddingValues(14.dp),
            glow = true,
            filigree = true
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EldoriaSlotFrame(
                    rarity = item.rarity,
                    level = item.itemLevel,
                    size = 66.dp
                ) {
                    Image(
                        painter = painterResource(id = getItemImageRes(item.imageResName, item.type)),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(Eldoria.S12))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.name,
                        style = EldoriaType.heading,
                        color = rarityColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)) {
                        EldoriaChip(text = item.rarity.uppercase(), color = rarityColor, filled = true)
                        EldoriaChip(
                            text = "Nv.${item.itemLevel}",
                            color = if (canEquip) Eldoria.Success else Eldoria.Danger,
                            icon = if (canEquip) Icons.Default.CheckCircle else Icons.Default.Lock
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Eldoria.S8))
            EldoriaDivider(color = rarityColor.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(Eldoria.S8))

            Text(
                item.description,
                style = EldoriaType.lore,
                color = Eldoria.TextMid
            )

            Spacer(modifier = Modifier.height(Eldoria.S8))

            EldoriaKeyValueRow(label = "Tipo", value = item.type, icon = Icons.Default.Category)
            if (item.strBonus > 0) EldoriaKeyValueRow("Fuerza", "+${item.strBonus}", icon = Icons.Default.FitnessCenter, valueColor = Eldoria.BloodBright)
            if (item.dexBonus > 0) EldoriaKeyValueRow("Destreza", "+${item.dexBonus}", icon = Icons.Default.DirectionsRun, valueColor = Eldoria.VitaeBright)
            if (item.intBonus > 0) EldoriaKeyValueRow("Inteligencia", "+${item.intBonus}", icon = Icons.Default.Psychology, valueColor = Eldoria.ManaBright)
            if (item.conBonus > 0) EldoriaKeyValueRow("Constitución", "+${item.conBonus}", icon = Icons.Default.Favorite, valueColor = Eldoria.EmberCore)
            if (item.dmgBonus > 0) EldoriaKeyValueRow("Daño", "+${item.dmgBonus}", icon = Icons.Default.Whatshot)
            if (item.defBonus > 0) EldoriaKeyValueRow("Defensa", "+${item.defBonus}", icon = Icons.Default.Shield)
            if (item.hpRegen > 0) EldoriaKeyValueRow("Regeneración", "+${item.hpRegen}", icon = Icons.Default.Favorite, valueColor = Eldoria.VitaeBright)

            // ─── Pasivas: sólo desde legendario ───
            // Se deducen del objeto, así que sin enseñarlas aquí el jugador no
            // tendría forma de saber que su reliquia refleja daño.
            val passives = remember(item.id, item.rarity, item.type) { EldoriaPassives.forItem(item) }
            if (passives.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Eldoria.S12))
                EldoriaDivider(color = Eldoria.ArcaneBright.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(Eldoria.S8))
                EldoriaSectionTitle(
                    text = "PASIVAS",
                    icon = Icons.Default.AutoAwesome,
                    accent = Eldoria.ArcaneBright
                )
                Spacer(modifier = Modifier.height(Eldoria.S6))
                passives.forEach { passive ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Eldoria.ArcaneBright,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(Eldoria.S6))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                passive.name,
                                style = EldoriaType.label,
                                color = Eldoria.ArcaneBright
                            )
                            Text(
                                passive.description,
                                style = EldoriaType.small,
                                color = Eldoria.TextMid
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Eldoria.S16))

            if (item.type != "POTION" && onEquip != null) {
                EldoriaButton(
                    text = if (canEquip) "EQUIPAR" else "NIVEL INSUFICIENTE",
                    onClick = onEquip,
                    enabled = canEquip,
                    tone = if (canEquip) EldoriaTone.Gold else EldoriaTone.Iron,
                    size = EldoriaButtonSize.Medium,
                    icon = if (canEquip) Icons.Default.Shield else Icons.Default.Lock,
                    fullWidth = true,
                    testTag = "equip_item_btn"
                )
                Spacer(modifier = Modifier.height(Eldoria.S8))
            }

            if (onSell != null || onDiscard != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                ) {
                    if (onSell != null) {
                        EldoriaButton(
                            text = "VENDER ${formatGameNumber(sellPrice)}",
                            onClick = onSell,
                            modifier = Modifier.weight(1.3f),
                            tone = EldoriaTone.Blood,
                            size = EldoriaButtonSize.Small,
                            icon = Icons.Default.Sell,
                            fullWidth = true,
                            testTag = "sell_item_btn"
                        )
                    }
                    if (onDiscard != null) {
                        EldoriaButton(
                            text = "TIRAR",
                            onClick = onDiscard,
                            modifier = Modifier.weight(1f),
                            tone = EldoriaTone.Iron,
                            size = EldoriaButtonSize.Small,
                            icon = Icons.Default.Delete,
                            fullWidth = true,
                            testTag = "discard_item_btn"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Eldoria.S8))
            }

            EldoriaButton(
                text = "CERRAR",
                onClick = onDismiss,
                tone = EldoriaTone.Iron,
                size = EldoriaButtonSize.Small,
                fullWidth = true
            )
        }
    }
}

// --- GAME GUIDE / HELP SCREEN ---
@Composable
fun HelpGuideScreen(onBack: () -> Unit) {
    // El manual del aventurero es un tomo: portada con blasón y entradas en
    // pergamino, cada una con su icono y su titular.
    val guideEntries = listOf(
        Quadruple(
            "EXPLORACIÓN",
            "Viaja a casillas adyacentes. Cuanto más te alejas del santuario, más duros son los monstruos y mejor el botín.",
            Icons.Default.Explore,
            Eldoria.Gold
        ),
        Quadruple(
            "COMBATE",
            "Sistema táctico por turnos. Cada habilidad de clase golpea con su propio elemento: acero, sangre, fuego, veneno, sombra o luz.",
            Icons.Default.Whatshot,
            Eldoria.BloodBright
        ),
        Quadruple(
            "PROGRESIÓN",
            "Cada nivel te da +5 puntos de atributo y +1 punto de talento. Ningún punto se pierde: puedes guardarlos.",
            Icons.Default.TrendingUp,
            Eldoria.VitaeBright
        ),
        Quadruple(
            "TALENTOS",
            "Tres sendas —Acero, Éter y Sombras—. Cada nodo exige rango 3 en el anterior para abrir el siguiente.",
            Icons.Default.Schema,
            Eldoria.ArcaneBright
        ),
        Quadruple(
            "EQUIPO",
            "Armas, armaduras, escudos y joyas generadas al vuelo, de Común a Universal. La gema del engaste te dice la rareza de un vistazo.",
            Icons.Default.Shield,
            Eldoria.Silver
        ),
        Quadruple(
            "DERROTA",
            "Si caes, resucitas en el santuario inicial y pierdes el 15% del oro acumulado. El equipo no se pierde.",
            Icons.Default.Warning,
            Eldoria.Danger
        )
    )

    EldoriaScreen(
        depth = 1,
        embers = false,
        fog = true,
        vignetteStrength = 0.6f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Eldoria.S8),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                EldoriaBanner(
                    title = "MANUAL DEL AVENTURERO",
                    subtitle = "Todo lo que Eldoria da por sabido",
                    height = 110.dp,
                    edge = EldoriaEdge.Gold,
                    crestSeed = 1207
                )
            }

            items(guideEntries) { (title, body, icon, accent) ->
                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = EldoriaEdge.Iron,
                    corner = Eldoria.R12,
                    padding = PaddingValues(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CutCornerShape(7.dp))
                                .background(Eldoria.PanelSunken)
                                .border(Eldoria.StrokeThin, accent.copy(alpha = 0.6f), CutCornerShape(7.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(19.dp))
                        }
                        Spacer(modifier = Modifier.width(Eldoria.S12))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, style = EldoriaType.label, color = accent)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(body, style = EldoriaType.small, color = Eldoria.TextMid)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(Eldoria.S4))
                EldoriaButton(
                    text = "VOLVER A LA AVENTURA",
                    onClick = onBack,
                    tone = EldoriaTone.Gold,
                    size = EldoriaButtonSize.Large,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    fullWidth = true,
                    testTag = "back_from_help_btn"
                )
            }
        }
    }
}

// Kotlin helper classes for data structures
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Retrato exacto de una especie a partir de su `artKey` del bestiario.
 *
 * Es la vía correcta: el bestiario ya declara qué dibujo le toca a cada
 * criatura. [getEnemyPortraitRes] adivina por palabras dentro del nombre, y con
 * un nombre decorado ("⭐ Musgoso Devorador Feroz Élite") acertaba de casualidad
 * o caía en el ogro genérico. Sólo se recurre a adivinar si no hay artKey —
 * los jefes de reino, que no salen del bestiario.
 */
fun getEnemyArtRes(
    artKey: String,
    name: String,
    isBoss: Boolean,
    rarity: String = "NORMAL"
): Int {
    if (artKey.isNotBlank()) {
        // Cada especie tiene una lamina por rango: un lobo comun y uno
        // legendario no pueden ser la misma imagen con otro numero al lado.
        val rank = when (rarity.uppercase()) {
            "ELITE" -> "elite"
            "CHAMPION" -> "champion"
            "LEGENDARY", "UNIVERSAL" -> "legendary"
            else -> "normal"
        }
        EldoriaArt.of(artKey + "_" + rank)?.let { return it }
        // Sin variante de rango (jefes de reino, arte antiguo): la base sirve.
        EldoriaArt.of(artKey)?.let { return it }
    }
    return getEnemyPortraitRes(name, isBoss)
}

fun getEnemyPortraitRes(name: String, isBoss: Boolean): Int {
    val cleanName = name.lowercase()
    return when {
        // Specific bosses & Vampires
        cleanName.contains("vampiro") -> if (isBoss || cleanName.contains("gran") || cleanName.contains("lord")) R.drawable.img_boss_high_vampire_1784674139269 else R.drawable.enemy_vampire_1784903236424
        cleanName.contains("hobgoblin") -> R.drawable.img_boss_hobgoblin_1784674116743
        cleanName.contains("igdrasil") || cleanName.contains("máquinas") || cleanName.contains("yggdrasil") -> R.drawable.img_boss_yggdrasil_machine_1784674150126
        cleanName.contains("dragon oscuro") || cleanName.contains("dragón oscuro") || cleanName.contains("calamidad") -> R.drawable.img_boss_dark_dragon_1784674128719

        // Demons & Fiends
        cleanName.contains("demonio") || cleanName.contains("demon") || cleanName.contains("fiend") || 
        cleanName.contains("diablo") || cleanName.contains("infernal") || cleanName.contains("azazel") || 
        cleanName.contains("lucifer") || cleanName.contains("vacío") || cleanName.contains("vacio") -> R.drawable.enemy_demon_1784903246195

        // Minotaurs
        cleanName.contains("minotauro") || cleanName.contains("minotaur") || cleanName.contains("tauro") -> R.drawable.enemy_minotaur_1784903256639

        // Kraken & Sea Monsters
        cleanName.contains("kraken") || cleanName.contains("tentáculo") || cleanName.contains("tentaculo") || 
        cleanName.contains("leviatán") || cleanName.contains("leviatan") -> R.drawable.enemy_kraken_1784903268006
        
        // Dragons, Wyrms & Wyverns
        cleanName.contains("dragón") || cleanName.contains("dragon") || cleanName.contains("wyrm") || 
        cleanName.contains("wyvern") || cleanName.contains("drake") || cleanName.contains("drakoniano") || cleanName.contains("hidra") -> R.drawable.enemy_dragon_1784850948333

        // Goblins
        cleanName.contains("goblin") || cleanName.contains("duende") -> R.drawable.enemy_goblin_1784850794614

        // Wolves, Canines & Beasts
        cleanName.contains("lobo") || cleanName.contains("fenrir") || cleanName.contains("warg") || 
        cleanName.contains("chacal") || cleanName.contains("licántropo") || cleanName.contains("licantropo") || 
        cleanName.contains("perro") || cleanName.contains("oso") || cleanName.contains("alfa") -> R.drawable.enemy_wolf_1784850801847

        // Ghosts, Spectres & Astral Entities
        cleanName.contains("espectro") || cleanName.contains("alma") || cleanName.contains("sombra") || 
        cleanName.contains("poltergeist") || cleanName.contains("orbe") || cleanName.contains("lamento") || cleanName.contains("fantas") -> R.drawable.enemy_spectre_1784850809472

        // Treants & Nature Creatures
        cleanName.contains("treant") || cleanName.contains("árbol") || cleanName.contains("arbol") || 
        cleanName.contains("planta") || cleanName.contains("bosque") || cleanName.contains("flor") -> R.drawable.enemy_treant_1784850817186

        // Bandits, Rogues & Human Mercenaries
        cleanName.contains("bandido") || cleanName.contains("ladrón") || cleanName.contains("ladron") || cleanName.contains("asesino") || 
        cleanName.contains("mercenario") || cleanName.contains("matón") || cleanName.contains("capitán") || cleanName.contains("pirata") || 
        cleanName.contains("infiltrador") || cleanName.contains("verdugo") || cleanName.contains("ballestero") || cleanName.contains("envenenador") -> R.drawable.enemy_bandit_1784850826788

        // Elementals & Fire Creatures
        cleanName.contains("elemental") || cleanName.contains("fuego") || cleanName.contains("magma") || 
        cleanName.contains("llama") || cleanName.contains("azufre") || cleanName.contains("ceniza") || cleanName.contains("pyros") || 
        cleanName.contains("ignis") || cleanName.contains("fatuo") || cleanName.contains("forja") -> R.drawable.enemy_elemental_1784850835033

        // Cultists & Acolytes
        cleanName.contains("cultista") || cleanName.contains("acolito") || cleanName.contains("acólito") -> R.drawable.enemy_cultist_1784850844974

        // Yetis, Ice & Snow Creatures
        cleanName.contains("yeti") || cleanName.contains("glacial") || cleanName.contains("escarcha") || 
        cleanName.contains("ventisquero") || cleanName.contains("glacius") || cleanName.contains("freya") || cleanName.contains("tundra") -> R.drawable.enemy_yeti_1784850855217

        // Undead, Zombies, Skeletons & Ghouls
        cleanName.contains("zombi") || cleanName.contains("zombie") || cleanName.contains("ghoul") || cleanName.contains("necrófago") || 
        cleanName.contains("peste") || cleanName.contains("esqueleto") || cleanName.contains("muerte") || 
        cleanName.contains("no-muerto") || cleanName.contains("hueso") -> R.drawable.enemy_zombie_1784850868957

        // Witches, Sorceresses & Mages
        cleanName.contains("bruja") || cleanName.contains("mago") || cleanName.contains("ilusionista") || 
        cleanName.contains("hechicero") || cleanName.contains("arcan") || cleanName.contains("chamán") || cleanName.contains("chaman") -> R.drawable.enemy_witch_1784850877826

        // Liches & Necromancers
        cleanName.contains("lich") || cleanName.contains("necromancer") || cleanName.contains("necromante") || cleanName.contains("filacteria") -> R.drawable.enemy_lich_1784850885522

        // Anubis, Pharaohs & Solar Egyptian Guardians
        cleanName.contains("anubis") || cleanName.contains("esfinge") || cleanName.contains("solaria") || 
        cleanName.contains("ra-horakhty") || cleanName.contains("sacerdote solar") || cleanName.contains("osiris") || cleanName.contains("faraón") -> R.drawable.enemy_anubis_1784850895657

        // Mummies
        cleanName.contains("momia") || cleanName.contains("mummy") -> R.drawable.enemy_mummy_1784850903429

        // Archangels & Celestial Beings
        cleanName.contains("archángel") || cleanName.contains("arcángel") || cleanName.contains("seraphiel") || 
        cleanName.contains("celestial") || cleanName.contains("astral") || cleanName.contains("sentinela") || 
        cleanName.contains("aetherion") || cleanName.contains("firmamento") || cleanName.contains("quimera") -> R.drawable.enemy_archangel_1784850912318

        // Orcs, Ogres & Trolls
        cleanName.contains("orco") || cleanName.contains("ogro") || cleanName.contains("berserker") || 
        cleanName.contains("demoledor") || cleanName.contains("gladiador") || cleanName.contains("warlord") || cleanName.contains("troll") -> R.drawable.enemy_orc_1784850920168

        // Naga, Tritons & Sea Ocean Monsters
        cleanName.contains("naga") || cleanName.contains("sireno") || cleanName.contains("tritón") || 
        cleanName.contains("neptuno") || cleanName.contains("océano") || cleanName.contains("oceano") || 
        cleanName.contains("coral") || cleanName.contains("mareas") -> R.drawable.enemy_naga_1784850928739

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
        cleanName.contains("araña") || cleanName.contains("arana") || cleanName.contains("tarántula") -> R.drawable.img_enemy_spider_1784386956688

        // Golems & Mud Slimes
        cleanName.contains("golem") || cleanName.contains("gólem") || cleanName.contains("fango") || cleanName.contains("lodo") || cleanName.contains("ciénaga") -> R.drawable.img_enemy_mud_golem_1784386930907

        // Boss fallback vs Normal fallback
        isBoss -> R.drawable.img_enemy_boss_1784386985144
        else -> R.drawable.img_enemy_ogre_1784386944311
    }
}


@Composable
fun PetScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val p = progress ?: return

    val equippedPet = remember(p.equippedPetJson) {
        GameJsonParser.fromJson<Item>(p.equippedPetJson)
    }

    val petWpn = remember(p.petEquippedWeaponJson) {
        GameJsonParser.fromJson<Item>(p.petEquippedWeaponJson)
    }

    val petArm = remember(p.petEquippedArmorJson) {
        GameJsonParser.fromJson<Item>(p.petEquippedArmorJson)
    }

    val petAcc = remember(p.petEquippedAccessoryJson) {
        GameJsonParser.fromJson<Item>(p.petEquippedAccessoryJson)
    }

    val inventory = remember(p.inventoryJson) {
        GameJsonParser.listFromJson<Item>(p.inventoryJson).filter { it.type != "EMPTY" }
    }

    val petFoodItems = remember(inventory) {
        inventory.filter { it.type == "PET_FOOD" }
    }

    val ownedPets = remember(inventory) {
        inventory.filter { it.type == "PET" }
    }

    var showEquipDialogForSlot by remember { mutableStateOf<String?>(null) }

    // Ficha de objeto por pulsación larga, compartida por las dos listas de
    // esta pantalla (bestias del inventario y equipo de bestia).
    var inspectingPet by remember { mutableStateOf<Item?>(null) }
    inspectingPet?.let { item ->
        InventoryItemDialog(
            item = item,
            playerLevel = p.charLevel,
            sellPrice = 0,
            onDismiss = { inspectingPet = null }
        )
    }

    val petLevel = p.petLevel
    val petExp = p.petExp
    val reqExp = petLevel * 150 + (petLevel * petLevel * 25)
    val petSatiety = p.petSatiety

    val baseDmg = equippedPet?.dmgBonus ?: 0
    val baseDef = equippedPet?.defBonus ?: 0
    val baseRegen = equippedPet?.hpRegen ?: 0

    val wpnDmg = petWpn?.dmgBonus ?: 0
    val armDef = petArm?.defBonus ?: 0
    val accCon = petAcc?.conBonus ?: 0

    val totalPetDmg = ((baseDmg + wpnDmg + p.charLevel * 6 + petLevel * 18 + (equippedPet?.strBonus ?: 0) * 0.5) * (if (petSatiety >= 50) 1.25f else if (petSatiety > 0) 1.0f else 0.6f)).toInt()
    val totalPetHeal = ((baseRegen * 3 + armDef + accCon + petLevel * 8 + (equippedPet?.conBonus ?: 0) * 0.5) * (if (petSatiety >= 50) 1.25f else if (petSatiety > 0) 1.0f else 0.6f)).toInt()

    EldoriaScreen(
        depth = 1,
        embers = false,
        fog = true,
        vignetteStrength = 0.58f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Eldoria.S8),
        contentPadding = PaddingValues(bottom = 26.dp)
    ) {
        // Top Header
        item {
            if (equippedPet != null) {
                val rarityColor = Eldoria.rarityColor(equippedPet.rarity)
                val petEdge = EldoriaEdge.rarity(equippedPet.rarity)
                // La bestia flota: está viva, no es un icono de inventario.
                val bob = eldoriaFloat(periodMs = 3000, amplitude = 5.dp, label = "petBob")

                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = petEdge,
                    corner = Eldoria.R16,
                    padding = PaddingValues(14.dp),
                    glow = true,
                    filigree = true
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EldoriaFrame(
                            modifier = Modifier
                                .size(104.dp)
                                .offset(y = bob),
                            edge = petEdge,
                            corner = Eldoria.R12,
                            strokeWidth = Eldoria.StrokeBold,
                            filigree = true,
                            rivets = true,
                            glowPulse = petSatiety >= 50
                        ) {
                            Image(
                                painter = painterResource(id = getItemImageRes(equippedPet.imageResName, equippedPet.type)),
                                contentDescription = equippedPet.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(Eldoria.S8))
                        Text(
                            text = equippedPet.name,
                            style = EldoriaType.title,
                            color = rarityColor,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)) {
                            EldoriaChip(text = equippedPet.rarity.uppercase(), color = rarityColor, filled = true)
                            EldoriaChip(text = "NIVEL $petLevel", color = Eldoria.TextGold)
                        }

                        Spacer(modifier = Modifier.height(Eldoria.S12))

                        EldoriaResourceBar(
                            current = petExp,
                            max = reqExp,
                            tone = EldoriaBarTone.Experience,
                            label = "EXPERIENCIA",
                            icon = Icons.Default.TrendingUp,
                            height = 13.dp
                        )

                        Spacer(modifier = Modifier.height(Eldoria.S6))

                        EldoriaResourceBar(
                            current = petSatiety,
                            max = 100,
                            tone = EldoriaBarTone.Satiety,
                            label = when {
                                petSatiety >= 50 -> "SACIEDAD · satisfecho, +25% poder"
                                petSatiety >= 20 -> "SACIEDAD · normal"
                                else -> "SACIEDAD · hambriento, −40% poder"
                            },
                            icon = Icons.Default.Restaurant,
                            height = 13.dp,
                            dangerPulse = petSatiety < 20
                        )

                        Spacer(modifier = Modifier.height(Eldoria.S12))

                        // Pet Stats Breakdown Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
                        ) {
                            EldoriaStatPill(
                                label = "ATAQUE",
                                value = "+$totalPetDmg",
                                icon = Icons.Default.Whatshot,
                                accent = Eldoria.BloodBright,
                                modifier = Modifier.weight(1f)
                            )
                            EldoriaStatPill(
                                label = "DEFENSA",
                                value = "+${baseDef + armDef}",
                                icon = Icons.Default.Shield,
                                accent = Eldoria.ManaBright,
                                modifier = Modifier.weight(1f)
                            )
                            EldoriaStatPill(
                                label = "CURA/TURNO",
                                value = "+$totalPetHeal",
                                icon = Icons.Default.Favorite,
                                accent = Eldoria.VitaeBright,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            } else {
                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = EldoriaEdge.Iron,
                    corner = Eldoria.R12,
                    padding = PaddingValues(4.dp)
                ) {
                    EldoriaEmptyState(
                        title = "Sin compañera",
                        message = "Las mascotas atacan por su cuenta y te curan cada turno. Derrota jefes de calabozo para conseguir una.",
                        icon = Icons.Default.Pets,
                        accent = Eldoria.RarityUniversal
                    )
                }
            }
        }

        // PET EQUIPMENT SLOTS
        if (equippedPet != null) {
            item {
                EldoriaSectionTitle(
                    text = "EQUIPO DE LA BESTIA",
                    icon = Icons.Default.Shield,
                    accent = Eldoria.Gold
                )
            }

            item {
                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = EldoriaEdge.Gold,
                    corner = Eldoria.R12,
                    padding = PaddingValues(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PetSlotCard(
                            label = "Arma",
                            item = petWpn,
                            defaultIcon = Icons.Default.Hardware,
                            onSlotClick = {
                                if (petWpn != null) viewModel.unequipPetGear("PET_WEAPON")
                                else showEquipDialogForSlot = "PET_WEAPON"
                            }
                        )

                        PetSlotCard(
                            label = "Armadura",
                            item = petArm,
                            defaultIcon = Icons.Default.Shield,
                            onSlotClick = {
                                if (petArm != null) viewModel.unequipPetGear("PET_ARMOR")
                                else showEquipDialogForSlot = "PET_ARMOR"
                            }
                        )

                        PetSlotCard(
                            label = "Accesorio",
                            item = petAcc,
                            defaultIcon = Icons.Default.Diamond,
                            onSlotClick = {
                                if (petAcc != null) viewModel.unequipPetGear("PET_ACCESSORY")
                                else showEquipDialogForSlot = "PET_ACCESSORY"
                            }
                        )
                    }
                }
            }

            // FEED PET SECTION
            item {
                EldoriaSectionTitle(
                    text = "ALIMENTAR",
                    icon = Icons.Default.Restaurant,
                    accent = Eldoria.Warning
                )
            }

            item {
                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = EldoriaEdge.Iron,
                    corner = Eldoria.R12,
                    padding = PaddingValues(12.dp)
                ) {
                    if (petFoodItems.isEmpty()) {
                        Text(
                            "No llevas alimento encima. Una bestia hambrienta pega un 40% menos.",
                            style = EldoriaType.small,
                            color = Eldoria.TextMid
                        )
                        Spacer(modifier = Modifier.height(Eldoria.S8))
                        EldoriaButton(
                            text = "COMPRAR EN LA TIENDA",
                            onClick = { viewModel.changeScreen(GameScreen.SHOP) },
                            tone = EldoriaTone.Gold,
                            size = EldoriaButtonSize.Small,
                            icon = Icons.Default.Storefront,
                            fullWidth = true
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)) {
                            items(petFoodItems) { food ->
                                EldoriaPanel(
                                    modifier = Modifier.width(114.dp),
                                    edge = EldoriaEdge.Silver,
                                    corner = Eldoria.R8,
                                    padding = PaddingValues(7.dp),
                                    onClick = { viewModel.feedPet(food) },
                                    testTag = "feed_pet_item_${food.id}"
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        EldoriaSlotFrame(size = 42.dp) {
                                            Image(
                                                painter = painterResource(id = getItemImageRes(food.imageResName, "FOOD")),
                                                contentDescription = food.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Text(
                                            food.name,
                                            style = EldoriaType.caption,
                                            color = Eldoria.TextHi,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            "+${food.conBonus} sac · +${food.strBonus} exp",
                                            style = EldoriaType.caption,
                                            color = Eldoria.VitaeBright,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TRAIN PET SECTION
            item {
                EldoriaSectionTitle(
                    text = "ENTRENAMIENTO",
                    icon = Icons.Default.SportsMartialArts,
                    accent = Eldoria.RarityUniversal
                )
            }

            item {
                val trainCost = petLevel * 50 + 50
                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = EldoriaEdge.Silver,
                    corner = Eldoria.R12,
                    padding = PaddingValues(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Eldoria.S8)
                    ) {
                        EldoriaButton(
                            text = "ENTRENAMIENTO COMPLETO",
                            onClick = { viewModel.autoTrainPet("ATTACK") },
                            tone = EldoriaTone.Arcane,
                            size = EldoriaButtonSize.Medium,
                            icon = Icons.Default.Bolt,
                            fullWidth = true,
                            testTag = "auto_train_master_btn"
                        )

                        PetTrainingRow(
                            title = "Furia Celestial",
                            costText = "$trainCost oro · −10 saciedad",
                            desc = "+${220 + petLevel * 25} EXP · sube el daño directo",
                            icon = Icons.Default.Whatshot,
                            accent = Eldoria.BloodBright,
                            onTrainClick = { viewModel.trainPet("ATTACK") },
                            onAutoTrainClick = { viewModel.autoTrainPet("ATTACK") },
                            testTag = "train_pet_attack"
                        )
                        PetTrainingRow(
                            title = "Bastión Sagrado",
                            costText = "$trainCost oro · −10 saciedad",
                            desc = "+${220 + petLevel * 25} EXP · sube la resistencia",
                            icon = Icons.Default.Shield,
                            accent = Eldoria.ManaBright,
                            onTrainClick = { viewModel.trainPet("DEFENSE") },
                            onAutoTrainClick = { viewModel.autoTrainPet("DEFENSE") },
                            testTag = "train_pet_defense"
                        )
                        PetTrainingRow(
                            title = "Vitalidad Inmortal",
                            costText = "$trainCost oro · −10 saciedad",
                            desc = "+${220 + petLevel * 25} EXP · sube la curación por turno",
                            icon = Icons.Default.Favorite,
                            accent = Eldoria.VitaeBright,
                            onTrainClick = { viewModel.trainPet("VITALITY") },
                            onAutoTrainClick = { viewModel.autoTrainPet("VITALITY") },
                            testTag = "train_pet_vitality"
                        )
                    }
                }
            }
        }

        // SWITCH PET / OWNED PETS SECTION
        item {
            EldoriaSectionTitle(
                text = "OTRAS BESTIAS",
                icon = Icons.Default.Pets,
                accent = Eldoria.RarityUniversal
            )
        }

        item {
            if (ownedPets.isEmpty()) {
                EldoriaPanel(
                    modifier = Modifier.fillMaxWidth(),
                    edge = EldoriaEdge.Iron,
                    corner = Eldoria.R12,
                    padding = PaddingValues(12.dp)
                ) {
                    Text(
                        "No tienes más bestias. Los jefes de calabozo sueltan mascotas de grado Universal.",
                        style = EldoriaType.small,
                        color = Eldoria.TextMid
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(Eldoria.S8)) {
                    ownedPets.forEach { petItem ->
                        EldoriaItemCard(
                            name = petItem.name,
                            rarity = petItem.rarity,
                            level = petItem.itemLevel,
                            stats = petItem.getStatDescription(),
                            imageRes = getItemImageRes(petItem.imageResName, "PET"),
                            subtitle = petItem.rarity.uppercase(),
                            onLongClick = { inspectingPet = petItem },
                            testTag = "owned_pet_${petItem.id}",
                            trailing = {
                                EldoriaButton(
                                    text = "EQUIPAR",
                                    onClick = { viewModel.equipItem(petItem) },
                                    tone = EldoriaTone.Gold,
                                    size = EldoriaButtonSize.Small
                                )
                            }
                        )
                    }
                }
            }
        }
    }
    }

    // Equipment Selector Modal Dialog for Pet
    showEquipDialogForSlot?.let { slot ->
        val candidateItems = remember(inventory, slot) {
            when (slot) {
                "PET_WEAPON" -> inventory.filter { it.type in listOf("WEAPON", "STAFF") }
                "PET_ARMOR" -> inventory.filter { it.type in listOf("ARMOR", "SHIELD", "HELMET", "GLOVES", "BOOTS") }
                else -> inventory.filter { it.type in listOf("RING", "EARRING", "RELIC") }
            }
        }

        Dialog(onDismissRequest = { showEquipDialogForSlot = null }) {
            EldoriaPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                edge = EldoriaEdge.Gold,
                corner = Eldoria.R16,
                padding = PaddingValues(14.dp),
                glow = true,
                filigree = true
            ) {
                Text(
                    text = when (slot) {
                        "PET_WEAPON" -> "ARMA DE LA BESTIA"
                        "PET_ARMOR" -> "ARMADURA DE LA BESTIA"
                        else -> "ACCESORIO DE LA BESTIA"
                    },
                    style = EldoriaType.title,
                    color = Eldoria.TextGold
                )
                Spacer(modifier = Modifier.height(Eldoria.S8))
                EldoriaDivider(color = Eldoria.Gold)
                Spacer(modifier = Modifier.height(Eldoria.S8))

                if (candidateItems.isEmpty()) {
                    EldoriaEmptyState(
                        title = "Nada que encajar",
                        message = "No llevas objetos de esta categoría en la mochila.",
                        icon = Icons.Default.Inventory2,
                        accent = Eldoria.Gold
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(Eldoria.S6)
                    ) {
                        items(candidateItems) { item ->
                            EldoriaItemCard(
                                name = item.name,
                                rarity = item.rarity,
                                level = item.itemLevel,
                                stats = item.getStatDescription(),
                                imageRes = getItemImageRes(item.imageResName, item.type),
                                onLongClick = { inspectingPet = item },
                                testTag = "pet_gear_${item.id}",
                                trailing = {
                                    EldoriaButton(
                                        text = "EQUIPAR",
                                        onClick = {
                                            viewModel.equipPetGear(item, slot)
                                            showEquipDialogForSlot = null
                                        },
                                        tone = EldoriaTone.Gold,
                                        size = EldoriaButtonSize.Small
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Eldoria.S12))
                EldoriaButton(
                    text = "CERRAR",
                    onClick = { showEquipDialogForSlot = null },
                    tone = EldoriaTone.Iron,
                    size = EldoriaButtonSize.Medium,
                    fullWidth = true
                )
            }
        }
    }
}

@Composable
fun PetSlotCard(
    label: String,
    item: Item?,
    defaultIcon: ImageVector,
    onSlotClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = EldoriaType.caption,
            color = Eldoria.TextGold
        )
        Spacer(modifier = Modifier.height(Eldoria.S4))
        EldoriaSlotFrame(
            rarity = item?.rarity,
            level = item?.itemLevel,
            size = 68.dp,
            emptyIcon = if (item == null) defaultIcon else null,
            onClick = onSlotClick,
            testTag = "pet_slot_$label"
        ) {
            if (item != null) {
                Image(
                    painter = painterResource(id = getItemImageRes(item.imageResName, item.type)),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun PetTrainingRow(
    title: String,
    costText: String,
    desc: String,
    icon: ImageVector,
    onTrainClick: () -> Unit,
    onAutoTrainClick: (() -> Unit)? = null,
    testTag: String,
    accent: Color = Eldoria.Gold
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(7.dp))
            .background(Eldoria.PanelSunken)
            .border(Eldoria.StrokeThin, accent.copy(alpha = 0.45f), CutCornerShape(7.dp))
            .padding(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, title, tint = accent, modifier = Modifier.size(21.dp))
        Spacer(modifier = Modifier.width(Eldoria.S8))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = EldoriaType.subheading, color = Eldoria.TextHi, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(desc, style = EldoriaType.caption, color = Eldoria.TextMid, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(costText, style = EldoriaType.caption, color = Eldoria.TextLow, maxLines = 1)
        }
        Spacer(modifier = Modifier.width(Eldoria.S6))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            EldoriaButton(
                text = "ENTRENAR",
                onClick = onTrainClick,
                tone = EldoriaTone.Gold,
                size = EldoriaButtonSize.Small,
                testTag = testTag
            )
            if (onAutoTrainClick != null) {
                EldoriaButton(
                    text = "AUTO",
                    onClick = onAutoTrainClick,
                    tone = EldoriaTone.Arcane,
                    size = EldoriaButtonSize.Small,
                    testTag = "${testTag}_auto"
                )
            }
        }
    }
}

