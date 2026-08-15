package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameViewModel
import com.example.data.GameProgress
import com.example.data.content.EldoriaMaterials
import com.example.data.formatGameNumber
import com.example.data.model.MinigameRequest
import com.example.eldoria.systems.*
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBanner
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaChip
import com.example.ui.design.EldoriaCounter
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaEmberField
import com.example.ui.design.EldoriaEmptyState
import com.example.ui.design.EldoriaFrame
import com.example.ui.design.EldoriaBarTone
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaProgressRing
import com.example.ui.design.EldoriaResourceBar
import com.example.ui.design.EldoriaRuneGlyph
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaSegmentedTabs
import com.example.ui.design.EldoriaSlotFrame
import com.example.ui.design.EldoriaStatPill
import com.example.ui.design.EldoriaToggleChip
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaTorchLight
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaDiamondPath
import com.example.ui.design.eldoriaPulse

// ============================================================
// PANTALLA DE LOGROS
// ============================================================

@Composable
fun AchievementsScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val p = progress ?: return

    // Estado vivo de logros: refleja los desbloqueos de la partida en curso,
    // no el catálogo estático.
    val achievements by viewModel.achievementState.collectAsState()
    var selectedCategory by remember { mutableStateOf<AchievementCategory?>(null) }

    val filteredAchievements = remember(achievements, selectedCategory) {
        if (selectedCategory != null) achievements.filter { it.category == selectedCategory } else achievements
    }

    val unlockedCount = achievements.count { it.isUnlocked }
    val totalUnlockedGold = achievements.filter { it.isUnlocked }.sumOf { it.rewardGold }

    val total = achievements.size.coerceAtLeast(1)
    val percent = unlockedCount * 100 / total

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
            contentPadding = PaddingValues(bottom = 26.dp)
        ) {
            // Header
            item {
                EldoriaBanner(
                    title = "CRÓNICAS",
                    subtitle = "$unlockedCount de $total hazañas selladas",
                    height = 112.dp,
                    edge = EldoriaEdge.Gold,
                    crestSeed = 9911,
                    trailing = {
                        EldoriaProgressRing(
                            progress = unlockedCount.toFloat() / total.toFloat(),
                            size = 62.dp,
                            stroke = 6.dp,
                            accent = Eldoria.Gold,
                            centerLabel = "$percent%"
                        )
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
                ) {
                    EldoriaStatPill(
                        label = "SELLADOS",
                        value = "$unlockedCount/$total",
                        icon = Icons.Default.WorkspacePremium,
                        accent = Eldoria.Gold,
                        modifier = Modifier.weight(1f)
                    )
                    EldoriaStatPill(
                        label = "ORO GANADO",
                        value = formatGameNumber(totalUnlockedGold),
                        icon = Icons.Default.MonetizationOn,
                        accent = Eldoria.TextGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category filter pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
                ) {
                    CraftFilterPill("TODOS", selectedCategory == null) { selectedCategory = null }
                    AchievementCategory.entries.forEach { cat ->
                        CraftFilterPill(
                            achievementCategoryLabel(cat),
                            selectedCategory == cat
                        ) { selectedCategory = cat }
                    }
                }
            }

            // Achievement cards
            items(filteredAchievements) { achievement ->
                AchievementCard(achievement)
            }
        }
    }
}

private fun achievementCategoryLabel(cat: AchievementCategory): String = when (cat) {
    AchievementCategory.COMBAT -> "COMBATE"
    AchievementCategory.EXPLORATION -> "EXPLORACIÓN"
    AchievementCategory.COLLECTION -> "COLECCIÓN"
    AchievementCategory.PROGRESSION -> "PROGRESO"
    AchievementCategory.MASTERY -> "MAESTRÍA"
}

private fun achievementCategoryAccent(cat: AchievementCategory): Color = when (cat) {
    AchievementCategory.COMBAT -> Eldoria.BloodBright
    AchievementCategory.EXPLORATION -> Eldoria.VitaeBright
    AchievementCategory.COLLECTION -> Eldoria.ManaBright
    AchievementCategory.PROGRESSION -> Eldoria.TextGold
    AchievementCategory.MASTERY -> Eldoria.ArcaneBright
}

private fun achievementCategoryIcon(cat: AchievementCategory): ImageVector = when (cat) {
    AchievementCategory.COMBAT -> Icons.Default.Whatshot
    AchievementCategory.EXPLORATION -> Icons.Default.Explore
    AchievementCategory.COLLECTION -> Icons.Default.Inventory2
    AchievementCategory.PROGRESSION -> Icons.Default.TrendingUp
    AchievementCategory.MASTERY -> Icons.Default.WorkspacePremium
}

@Composable
private fun AchievementCard(achievement: Achievement) {
    val accent = achievementCategoryAccent(achievement.category)
    val unlocked = achievement.isUnlocked

    EldoriaPanel(
        modifier = Modifier.fillMaxWidth(),
        edge = if (unlocked) EldoriaEdge.Gold else EldoriaEdge.Iron,
        corner = Eldoria.R8,
        padding = PaddingValues(11.dp),
        glow = unlocked
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sello del logro: dorado y con lacre cuando está conseguido,
            // hierro apagado y candado cuando aún no.
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CutCornerShape(8.dp))
                    .background(if (unlocked) Eldoria.GlowGold else Eldoria.PanelSunken)
                    .border(
                        Eldoria.StrokeThin,
                        if (unlocked) Eldoria.goldEdge() else Brush.verticalGradient(
                            listOf(accent.copy(alpha = 0.5f), accent.copy(alpha = 0.2f))
                        ),
                        CutCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (unlocked) achievementCategoryIcon(achievement.category) else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (unlocked) Eldoria.GoldBright else Eldoria.TextLow,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(Modifier.width(Eldoria.S12))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    achievement.title,
                    style = EldoriaType.subheading,
                    color = if (unlocked) Eldoria.TextGold else Eldoria.TextHi,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    achievement.description,
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!unlocked) {
                    Spacer(Modifier.height(5.dp))
                    EldoriaResourceBar(
                        current = achievement.currentProgress,
                        max = achievement.requirement.coerceAtLeast(1),
                        tone = EldoriaBarTone.Experience,
                        height = 9.dp,
                        showNumbers = true
                    )
                }
            }

            Spacer(Modifier.width(Eldoria.S8))

            // Rewards
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        null,
                        tint = Eldoria.TextGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        formatGameNumber(achievement.rewardGold),
                        style = EldoriaType.caption,
                        color = Eldoria.TextGold
                    )
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = Eldoria.VitaeBright,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        formatGameNumber(achievement.rewardXp),
                        style = EldoriaType.caption,
                        color = Eldoria.VitaeBright
                    )
                }
            }
        }
    }
}

// ============================================================
// PANTALLA DE FORJA / CRAFTING
// ============================================================

// La Fragua: brasas, yunque y el libro del herrero. Cada receta enseña de un
// vistazo lo único que importa antes de golpear el metal: si tienes el material.
@Composable
fun CraftingScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val p = progress ?: return

    val bag by viewModel.systems.materials.collectAsState()

    val recipes = CraftingRecipes.ALL_RECIPES
    var selectedTier by rememberSaveable { mutableIntStateOf(0) } // 0 Todos · 1 Básico · 2 Avanzado · 3 Maestro
    var onlyCraftable by rememberSaveable { mutableStateOf(false) }

    val tierFiltered = remember(recipes, selectedTier) {
        when (selectedTier) {
            1 -> recipes.filter { it.requiredLevel <= 10 }
            2 -> recipes.filter { it.requiredLevel in 11..35 }
            3 -> recipes.filter { it.requiredLevel > 35 }
            else -> recipes
        }
    }

    fun forgeable(r: CraftingRecipe): Boolean =
        p.charLevel >= r.requiredLevel &&
            p.charGold >= r.goldCost &&
            r.materials.all { (bag[it.id] ?: 0) >= it.quantity }

    val filteredRecipes = remember(tierFiltered, onlyCraftable, bag, p.charLevel, p.charGold) {
        if (onlyCraftable) tierFiltered.filter { forgeable(it) } else tierFiltered
    }
    val readyCount = remember(tierFiltered, bag, p.charLevel, p.charGold) {
        tierFiltered.count { forgeable(it) }
    }

    // Resplandor de la fragua: late despacio bajo todo el contenido.
    val forgeGlow = eldoriaPulse(periodMs = 2600, from = 0.42f, to = 0.78f, label = "forgeGlow")

    EldoriaScreen(
        depth = 2,
        embers = false,
        fog = true,
        vignetteStrength = 0.66f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // Boca del horno: luz cálida pegada al borde inferior.
        EldoriaTorchLight(
            modifier = Modifier.matchParentSize(),
            intensity = forgeGlow,
            warm = Eldoria.Ember,
            flicker = true,
            centerX = 0.5f,
            centerY = 1.02f
        )
        EldoriaEmberField(
            modifier = Modifier.matchParentSize(),
            count = 30,
            tint = Eldoria.EmberCore,
            periodMs = 7000,
            seed = 41,
            maxAlpha = 0.6f
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Eldoria.S8),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item {
                EldoriaBanner(
                    title = "LA FRAGUA",
                    subtitle = "El yunque decide la calidad · $readyCount de ${tierFiltered.size} recetas listas",
                    artRes = R.drawable.img_mat_hierro_1784901606157,
                    height = 118.dp,
                    edge = EldoriaEdge.Ember,
                    crestSeed = 3307,
                    trailing = {
                        EldoriaCounter(
                            value = p.charGold.toLong(),
                            icon = Icons.Default.MonetizationOn,
                            accent = Eldoria.TextGold
                        )
                    }
                )
            }

            // El herrero: retrato rúnico + su frase. Es el ancla narrativa del taller.
            item {
                EldoriaPanel(edge = EldoriaEdge.Ember, corner = Eldoria.R12, filigree = true) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EldoriaFrame(
                            modifier = Modifier.size(58.dp),
                            edge = EldoriaEdge.Ember,
                            corner = Eldoria.R8,
                            strokeWidth = Eldoria.StrokeMed,
                            filigree = false,
                            rivets = true,
                            glowPulse = true
                        ) {
                            EldoriaRuneGlyph(
                                seed = 3307,
                                modifier = Modifier
                                    .matchParentSize()
                                    .padding(12.dp),
                                color = Eldoria.EmberCore,
                                strokeWidth = 2.dp,
                                animated = true
                            )
                        }
                        Spacer(Modifier.width(Eldoria.S12))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Maestro Herrero Grommash",
                                style = EldoriaType.subheading,
                                color = Eldoria.TextGold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "«Trae el material y el oro. El resto lo pone el martillo… y tu pulso.»",
                                style = EldoriaType.small,
                                color = Eldoria.TextMid,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            item {
                EldoriaSegmentedTabs(
                    options = listOf("Todos", "Básico", "Avanzado", "Maestro"),
                    selectedIndex = selectedTier,
                    onSelect = { selectedTier = it },
                    accent = Eldoria.Ember,
                    testTagPrefix = "craft_tier"
                )
            }

            item {
                EldoriaSectionTitle(
                    text = "LIBRO DE RECETAS",
                    icon = Icons.Default.Build,
                    accent = Eldoria.Ember,
                    trailing = {
                        EldoriaChip(
                            text = if (onlyCraftable) "SOLO FORJABLES" else "TODAS",
                            color = if (onlyCraftable) Eldoria.Success else Eldoria.TextLow,
                            icon = Icons.Default.FilterList,
                            filled = onlyCraftable,
                            modifier = Modifier.clickable { onlyCraftable = !onlyCraftable }
                        )
                    }
                )
            }

            if (filteredRecipes.isEmpty()) {
                item {
                    EldoriaEmptyState(
                        title = "El yunque está frío",
                        message = "No hay recetas forjables con lo que llevas encima. Baja a una expedición y vuelve con material.",
                        icon = Icons.Default.Build,
                        accent = Eldoria.Ember,
                        actionLabel = if (onlyCraftable) "Ver todas las recetas" else null,
                        onAction = if (onlyCraftable) fun() { onlyCraftable = false } else null
                    )
                }
            }

            items(filteredRecipes, key = { it.id }) { recipe ->
                CraftingRecipeCard(
                    recipe = recipe,
                    playerLevel = p.charLevel,
                    playerGold = p.charGold,
                    bag = bag,
                    onForge = {
                        // La forja pasa por el minijuego del yunque: el resultado
                        // decide la calidad de la pieza (contexto = id de receta).
                        viewModel.systems.openMinigame(
                            MinigameRequest(
                                id = "YUNQUE",
                                difficulty = recipe.requiredLevel / 15 + 1,
                                title = recipe.resultItemName,
                                contextJson = recipe.id,
                                originScreen = "CRAFTING"
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun CraftingRecipeCard(
    recipe: CraftingRecipe,
    playerLevel: Int,
    playerGold: Int,
    bag: Map<String, Int>,
    onForge: () -> Unit
) {
    val levelOk = playerLevel >= recipe.requiredLevel
    val goldOk = playerGold >= recipe.goldCost
    val missing = recipe.materials.filter { (bag[it.id] ?: 0) < it.quantity }
    val canCraft = levelOk && goldOk && missing.isEmpty()

    val rarityColor = Eldoria.rarityColor(recipe.resultRarity)
    val edge = if (canCraft) EldoriaEdge.rarity(recipe.resultRarity) else EldoriaEdge.Iron

    EldoriaPanel(
        edge = edge,
        corner = Eldoria.R12,
        padding = PaddingValues(11.dp),
        glow = canCraft,
        testTag = "craft_recipe_${recipe.id}"
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EldoriaSlotFrame(
                rarity = recipe.resultRarity,
                level = recipe.resultLevel,
                size = 58.dp
            ) {
                Image(
                    painter = painterResource(
                        id = getItemImageRes("", recipe.resultItemType)
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (canCraft) 1f else 0.45f)
                )
            }
            Spacer(Modifier.width(Eldoria.S12))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    recipe.name,
                    style = EldoriaType.subheading,
                    color = if (canCraft) rarityColor else Eldoria.TextMid,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    recipe.description,
                    style = EldoriaType.caption,
                    color = Eldoria.TextLow,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S6)
                ) {
                    EldoriaChip(
                        text = recipe.resultRarity.uppercase(),
                        color = rarityColor,
                        filled = true
                    )
                    EldoriaChip(
                        text = "Nv.${recipe.requiredLevel}",
                        color = if (levelOk) Eldoria.Success else Eldoria.Danger,
                        icon = if (levelOk) Icons.Default.CheckCircle else Icons.Default.Lock
                    )
                }
            }
        }

        Spacer(Modifier.height(Eldoria.S8))
        EldoriaDivider(color = edge.mid.copy(alpha = 0.7f), ornament = false)
        Spacer(Modifier.height(Eldoria.S8))

        // Materiales: cada uno enseña tenencia/requisito. Verde = lo tienes.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Eldoria.S6),
            verticalAlignment = Alignment.CenterVertically
        ) {
            recipe.materials.forEach { mat ->
                val owned = bag[mat.id] ?: 0
                val enough = owned >= mat.quantity
                val tint = if (enough) Eldoria.Success else Eldoria.Danger
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Eldoria.PanelSunken)
                        .border(0.75.dp, tint.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = getItemImageRes(mat.id, "MATERIAL")),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .alpha(if (enough) 1f else 0.5f)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        EldoriaMaterials.name(mat.id),
                        style = EldoriaType.caption,
                        color = Eldoria.TextMid,
                        maxLines = 1
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "$owned/${mat.quantity}",
                        style = EldoriaType.numeric,
                        color = tint,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(Modifier.height(Eldoria.S8))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MonetizationOn,
                    null,
                    tint = if (goldOk) Eldoria.TextGold else Eldoria.Danger,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    formatGameNumber(recipe.goldCost),
                    style = EldoriaType.numeric,
                    color = if (goldOk) Eldoria.TextGold else Eldoria.Danger
                )
                if (missing.isNotEmpty()) {
                    Spacer(Modifier.width(Eldoria.S8))
                    Text(
                        "Faltan ${missing.size} material${if (missing.size == 1) "" else "es"}",
                        style = EldoriaType.caption,
                        color = Eldoria.Danger,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            EldoriaButton(
                text = if (canCraft) "FORJAR" else "BLOQUEADO",
                onClick = onForge,
                enabled = canCraft,
                tone = if (canCraft) EldoriaTone.Ember else EldoriaTone.Iron,
                size = EldoriaButtonSize.Small,
                icon = if (canCraft) Icons.Default.Build else Icons.Default.Lock,
                testTag = "craft_forge_${recipe.id}"
            )
        }
    }
}

// ============================================================
// PANTALLA DE RECOMPENSAS DIARIAS
// ============================================================

@Composable
fun DailyRewardsScreen(viewModel: GameViewModel) {
    val progress by viewModel.progressState.collectAsState()
    val p = progress ?: return

    val dailyState by viewModel.dailyRewardState.collectAsState()
    val canClaim = canClaimDailyReward(dailyState)

    val currentDay = dailyState.currentDay
    val isCycleComplete = dailyState.isCycleComplete

    EldoriaScreen(
        depth = 1,
        embers = false,
        fog = true,
        vignetteStrength = 0.58f,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Eldoria.S8)
    ) {
        // Header
        EldoriaBanner(
            title = "OFRENDA DIARIA",
            subtitle = if (isCycleComplete) "Ciclo completo · vuelve mañana"
                       else "Día $currentDay de 7 · la racha se paga sola",
            height = 108.dp,
            edge = if (canClaim && !isCycleComplete) EldoriaEdge.Gold else EldoriaEdge.Iron,
            crestSeed = 707,
            trailing = {
                EldoriaProgressRing(
                    progress = (currentDay.coerceIn(0, 7)) / 7f,
                    size = 58.dp,
                    stroke = 6.dp,
                    accent = Eldoria.Gold,
                    centerLabel = "$currentDay/7"
                )
            }
        )

        // Current day indicator
        EldoriaPanel(
            modifier = Modifier.fillMaxWidth(),
            edge = if (canClaim && !isCycleComplete) EldoriaEdge.Gold else EldoriaEdge.Iron,
            corner = Eldoria.R12,
            padding = PaddingValues(14.dp),
            glow = canClaim && !isCycleComplete,
            filigree = true
        ) {
            // Racha en rombos: siete sellos, los cobrados en oro macizo.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..7).forEach { day ->
                    val isClaimed = dailyState.cycleRewards.getOrNull(day - 1)?.isClaimed == true
                    val isCurrentDay = day == currentDay && !isCycleComplete
                    val accent = when {
                        isClaimed -> Eldoria.GoldBright
                        isCurrentDay -> Eldoria.Gold
                        else -> Eldoria.IronEdge
                    }
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val r = size.minDimension / 2f
                            drawPath(eldoriaDiamondPath(cx, cy, r), Eldoria.Abyss)
                            drawPath(
                                eldoriaDiamondPath(cx, cy, r * 0.86f),
                                color = if (isClaimed) accent.copy(alpha = 0.85f) else Eldoria.PanelSunken
                            )
                            drawPath(
                                eldoriaDiamondPath(cx, cy, r * 0.86f),
                                color = accent,
                                style = Stroke(width = if (isCurrentDay) 2.4f else 1.2f)
                            )
                        }
                        if (isClaimed) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Eldoria.Abyss,
                                modifier = Modifier.size(15.dp)
                            )
                        } else {
                            Text(
                                "$day",
                                style = EldoriaType.caption,
                                color = if (isCurrentDay) Eldoria.TextGold else Eldoria.TextLow
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(Eldoria.S12))

            EldoriaButton(
                text = when {
                    isCycleComplete -> "CICLO COMPLETADO"
                    canClaim -> "RECLAMAR DÍA $currentDay"
                    else -> "VUELVE MAÑANA"
                },
                onClick = {
                    if (canClaim) {
                        viewModel.claimDailyRewardNow()
                    }
                },
                enabled = canClaim && !isCycleComplete,
                tone = EldoriaTone.Gold,
                size = EldoriaButtonSize.Large,
                icon = Icons.Default.CardGiftcard,
                fullWidth = true,
                testTag = "claim_daily_reward_btn"
            )
        }

        // 7-day reward list
        EldoriaSectionTitle(
            text = "CALENDARIO",
            icon = Icons.Default.CalendarMonth,
            accent = Eldoria.Gold
        )

        dailyState.cycleRewards.forEach { reward ->
            val isClaimed = reward.isClaimed
            val isCurrentDay = reward.day == currentDay && !isCycleComplete

            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = when {
                    isClaimed -> EldoriaEdge.Vitae
                    isCurrentDay -> EldoriaEdge.Gold
                    else -> EldoriaEdge.Iron
                },
                corner = Eldoria.R8,
                padding = PaddingValues(11.dp),
                glow = isCurrentDay
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CutCornerShape(7.dp))
                            .background(
                                when {
                                    isClaimed -> Eldoria.VitaeDeep
                                    isCurrentDay -> Eldoria.GlowGold
                                    else -> Eldoria.PanelSunken
                                }
                            )
                            .border(
                                Eldoria.StrokeThin,
                                when {
                                    isClaimed -> Eldoria.Vitae
                                    isCurrentDay -> Eldoria.Gold
                                    else -> Eldoria.IronEdge
                                },
                                CutCornerShape(7.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isClaimed) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                tint = Eldoria.VitaeBright,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                "${reward.day}",
                                style = EldoriaType.numeric,
                                color = if (isCurrentDay) Eldoria.TextGold else Eldoria.TextLow
                            )
                        }
                    }

                    Spacer(Modifier.width(Eldoria.S12))

                    // Rewards
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Día ${reward.day}",
                            style = EldoriaType.subheading,
                            color = when {
                                isClaimed -> Eldoria.VitaeBright
                                isCurrentDay -> Eldoria.TextGold
                                else -> Eldoria.TextMid
                            }
                        )
                        Spacer(Modifier.height(3.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(Eldoria.S4),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            reward.rewards.forEach { item ->
                                EldoriaChip(
                                    text = "${item.name} ×${item.amount}",
                                    color = Eldoria.rarityColor(item.rarity)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(Eldoria.S24))
    }
    }
}

// ============================================================
// COMPONENTE AUXILIAR: CraftFilterPill reutilizable
// ============================================================

@Composable
private fun CraftFilterPill(label: String, isActive: Boolean, onClick: () -> Unit) {
    EldoriaToggleChip(
        text = label,
        selected = isActive,
        onClick = onClick,
        accent = Eldoria.Gold
    )
}
