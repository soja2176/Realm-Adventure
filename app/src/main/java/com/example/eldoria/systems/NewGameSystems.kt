package com.example.eldoria.systems

import com.example.data.Item

// ============================================================
// SISTEMA DE LOGROS
// ============================================================

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val category: AchievementCategory,
    val requirement: Int,
    val currentProgress: Int = 0,
    val rewardGold: Int,
    val rewardXp: Int,
    val isUnlocked: Boolean = false
)

enum class AchievementCategory {
    COMBAT, EXPLORATION, COLLECTION, PROGRESSION, MASTERY
}

/**
 * Definiciones de todos los logros disponibles en Eldoria Chronicles.
 * Cada logro tiene un requisito numérico, recompensas en oro y experiencia,
 * y pertenece a una categoría específica.
 */
object AchievementDefinitions {
    val ALL_ACHIEVEMENTS: List<Achievement> = listOf(
        // --- COMBATE ---
        Achievement(
            id = "first_blood",
            title = "Primer Sangre",
            description = "Gana tu primer combate",
            icon = "ic_sword",
            category = AchievementCategory.COMBAT,
            requirement = 1,
            rewardGold = 100,
            rewardXp = 50
        ),
        Achievement(
            id = "beast_hunter",
            title = "Cazador de Bestias",
            description = "Mata 50 enemigos",
            icon = "ic_beast",
            category = AchievementCategory.COMBAT,
            requirement = 50,
            rewardGold = 500,
            rewardXp = 250
        ),
        Achievement(
            id = "exterminator",
            title = "Exterminador",
            description = "Mata 500 enemigos",
            icon = "ic_skull",
            category = AchievementCategory.COMBAT,
            requirement = 500,
            rewardGold = 5000,
            rewardXp = 2500
        ),
        Achievement(
            id = "dragon_slayer",
            title = "Asesino de Dragones",
            description = "Mata 10 jefes",
            icon = "ic_dragon",
            category = AchievementCategory.COMBAT,
            requirement = 10,
            rewardGold = 2000,
            rewardXp = 1000
        ),
        Achievement(
            id = "elite_warrior",
            title = "Guerrero Élite",
            description = "Mata 25 enemigos de élite",
            icon = "ic_elite",
            category = AchievementCategory.COMBAT,
            requirement = 25,
            rewardGold = 3000,
            rewardXp = 1500
        ),

        // --- EXPLORACIÓN ---
        Achievement(
            id = "novice_explorer",
            title = "Explorador Novato",
            description = "Explora 25 casillas",
            icon = "ic_compass",
            category = AchievementCategory.EXPLORATION,
            requirement = 25,
            rewardGold = 200,
            rewardXp = 100
        ),
        Achievement(
            id = "cartographer",
            title = "Cartógrafo",
            description = "Explora 100 casillas",
            icon = "ic_map",
            category = AchievementCategory.EXPLORATION,
            requirement = 100,
            rewardGold = 1000,
            rewardXp = 500
        ),
        Achievement(
            id = "world_traveler",
            title = "Viajero del Mundo",
            description = "Explora 500 casillas",
            icon = "ic_globe",
            category = AchievementCategory.EXPLORATION,
            requirement = 500,
            rewardGold = 5000,
            rewardXp = 2500
        ),
        Achievement(
            id = "valley_king",
            title = "Rey del Valle",
            description = "Explora todo el reino de Eldoria",
            icon = "ic_crown",
            category = AchievementCategory.EXPLORATION,
            requirement = 50,
            rewardGold = 2000,
            rewardXp = 1000
        ),
        Achievement(
            id = "kingdom_conqueror",
            title = "Conquistador de Reinos",
            description = "Visita los 6 reinos",
            icon = "ic_kingdom",
            category = AchievementCategory.EXPLORATION,
            requirement = 6,
            rewardGold = 10000,
            rewardXp = 5000
        ),

        // --- PROGRESIÓN ---
        Achievement(
            id = "adventurer",
            title = "Aventurero",
            description = "Alcanza el nivel 10",
            icon = "ic_star",
            category = AchievementCategory.PROGRESSION,
            requirement = 10,
            rewardGold = 500,
            rewardXp = 300
        ),
        Achievement(
            id = "hero_of_eldoria",
            title = "Héroe de Eldoria",
            description = "Alcanza el nivel 50",
            icon = "ic_hero",
            category = AchievementCategory.PROGRESSION,
            requirement = 50,
            rewardGold = 10000,
            rewardXp = 5000
        ),
        Achievement(
            id = "living_legend",
            title = "Leyenda Viva",
            description = "Alcanza el nivel 100",
            icon = "ic_legend",
            category = AchievementCategory.PROGRESSION,
            requirement = 100,
            rewardGold = 50000,
            rewardXp = 25000
        ),
        Achievement(
            id = "ascended",
            title = "Ascendido",
            description = "Avanza a clase avanzada",
            icon = "ic_ascend",
            category = AchievementCategory.PROGRESSION,
            requirement = 1,
            rewardGold = 5000,
            rewardXp = 2500
        ),

        // --- COLECCIÓN ---
        Achievement(
            id = "collector",
            title = "Coleccionista",
            description = "Posee 20 objetos",
            icon = "ic_bag",
            category = AchievementCategory.COLLECTION,
            requirement = 20,
            rewardGold = 300,
            rewardXp = 150
        ),
        Achievement(
            id = "epic_treasure",
            title = "Tesoro Épico",
            description = "Posee 5 objetos épicos",
            icon = "ic_epic",
            category = AchievementCategory.COLLECTION,
            requirement = 5,
            rewardGold = 1000,
            rewardXp = 500
        ),
        Achievement(
            id = "legendary_equipment",
            title = "Leyenda Equipada",
            description = "Posee 10 objetos legendarios",
            icon = "ic_legendary",
            category = AchievementCategory.COLLECTION,
            requirement = 10,
            rewardGold = 5000,
            rewardXp = 2500
        ),
        Achievement(
            id = "millionaire",
            title = "Millonario",
            description = "Acumula 100000 de oro en total",
            icon = "ic_gold",
            category = AchievementCategory.COLLECTION,
            requirement = 100000,
            rewardGold = 10000,
            rewardXp = 5000
        ),
        Achievement(
            id = "merchant",
            title = "Comerciante",
            description = "Compra 30 objetos de la tienda",
            icon = "ic_shop",
            category = AchievementCategory.COLLECTION,
            requirement = 30,
            rewardGold = 1000,
            rewardXp = 500
        ),

        // --- MAESTRÍA ---
        Achievement(
            id = "beast_tamer",
            title = "Domador de Bestias",
            description = "Sube tu mascota al nivel 20",
            icon = "ic_pet",
            category = AchievementCategory.MASTERY,
            requirement = 20,
            rewardGold = 1000,
            rewardXp = 500
        ),
        Achievement(
            id = "forge_master",
            title = "Maestro de la Forja",
            description = "Fabrica 10 objetos",
            icon = "ic_anvil",
            category = AchievementCategory.MASTERY,
            requirement = 10,
            rewardGold = 2000,
            rewardXp = 1000
        ),
        Achievement(
            id = "dungeon_king",
            title = "Rey de las Mazmorras",
            description = "Completa 10 mazmorras",
            icon = "ic_dungeon",
            category = AchievementCategory.MASTERY,
            requirement = 10,
            rewardGold = 3000,
            rewardXp = 1500
        ),
        Achievement(
            id = "dungeon_conqueror",
            title = "Conquistador de Mazmorras",
            description = "Completa 20 mazmorras",
            icon = "ic_dungeon_master",
            category = AchievementCategory.MASTERY,
            requirement = 20,
            rewardGold = 10000,
            rewardXp = 5000
        ),
        Achievement(
            id = "supreme_talent",
            title = "Talento Supremo",
            description = "Maximiza 5 talentos",
            icon = "ic_talent",
            category = AchievementCategory.MASTERY,
            requirement = 5,
            rewardGold = 2000,
            rewardXp = 1000
        ),
        Achievement(
            id = "survivor",
            title = "Superviviente",
            description = "Completa 5 mazmorras sin morir",
            icon = "ic_shield",
            category = AchievementCategory.MASTERY,
            requirement = 5,
            rewardGold = 3000,
            rewardXp = 1500
        ),
        Achievement(
            id = "daily_devotee",
            title = "Devoto Diario",
            description = "Reclama 7 recompensas diarias",
            icon = "ic_calendar",
            category = AchievementCategory.MASTERY,
            requirement = 7,
            rewardGold = 1500,
            rewardXp = 750
        )
    )
}

// ============================================================
// SISTEMA DE RECOMPENSAS DIARIAS
// ============================================================

data class DailyRewardState(
    val currentDay: Int = 1,
    val lastClaimTimestamp: Long = 0L,
    val cycleRewards: List<DailyReward> = generateDailyCycle(),
    val isCycleComplete: Boolean = false
)

data class DailyReward(
    val day: Int,
    val rewards: List<DailyRewardItem>,
    val isClaimed: Boolean = false
)

data class DailyRewardItem(
    val type: String,
    val amount: Int,
    val name: String,
    val rarity: String = "COMÚN"
)

/**
 * Genera el ciclo de 7 días de recompensas diarias con dificultad escalada.
 * Cada día ofrece mejores recompensas para incentivar la conexión diaria.
 */
fun generateDailyCycle(): List<DailyReward> {
    return listOf(
        // Día 1: Oro básico para comenzar
        DailyReward(
            day = 1,
            rewards = listOf(
                DailyRewardItem(type = "gold", amount = 500, name = "Oro")
            )
        ),
        // Día 2: Oro más pociones para mantener al jugador
        DailyReward(
            day = 2,
            rewards = listOf(
                DailyRewardItem(type = "gold", amount = 1000, name = "Oro"),
                DailyRewardItem(type = "potion", amount = 2, name = "Poción de Vida")
            )
        ),
        // Día 3: Oro y material raro
        DailyReward(
            day = 3,
            rewards = listOf(
                DailyRewardItem(type = "gold", amount = 2000, name = "Oro"),
                DailyRewardItem(type = "material", amount = 1, name = "Cristal de Maná", rarity = "RARO")
            )
        ),
        // Día 4: Oro y objeto épico
        DailyReward(
            day = 4,
            rewards = listOf(
                DailyRewardItem(type = "gold", amount = 3000, name = "Oro"),
                DailyRewardItem(type = "equipment", amount = 1, name = "Amuleto de Poder", rarity = "ÉPICO")
            )
        ),
        // Día 5: Oro y materiales épicos
        DailyReward(
            day = 5,
            rewards = listOf(
                DailyRewardItem(type = "gold", amount = 5000, name = "Oro"),
                DailyRewardItem(type = "material", amount = 2, name = "Esencia Arcana", rarity = "ÉPICO")
            )
        ),
        // Día 6: Oro y material legendario
        DailyReward(
            day = 6,
            rewards = listOf(
                DailyRewardItem(type = "gold", amount = 8000, name = "Oro"),
                DailyRewardItem(type = "material", amount = 1, name = "Fragmento de Estrella", rarity = "LEGENDARIO")
            )
        ),
        // Día 7: Gran recompensa final del ciclo
        DailyReward(
            day = 7,
            rewards = listOf(
                DailyRewardItem(type = "gold", amount = 15000, name = "Oro"),
                DailyRewardItem(type = "equipment", amount = 1, name = "Corona del Conquistador", rarity = "LEGENDARIO"),
                DailyRewardItem(type = "xp", amount = 5000, name = "Experiencia")
            )
        )
    )
}

/**
 * Verifica si el jugador puede reclamar la recompensa diaria.
 * Deben haber pasado al menos 24 horas desde la última reclamación.
 */
fun canClaimDailyReward(state: DailyRewardState): Boolean {
    // Si el ciclo está completo, no se puede reclamar hasta reiniciar
    if (state.isCycleComplete) return false

    // Si nunca ha reclamado, puede hacerlo
    if (state.lastClaimTimestamp == 0L) return true

    // Verificar si han pasado al menos 24 horas (86400000 milisegundos)
    val currentTime = System.currentTimeMillis()
    val twentyFourHoursMs = 24L * 60L * 60L * 1000L
    return (currentTime - state.lastClaimTimestamp) >= twentyFourHoursMs
}

/**
 * Reclama la recompensa diaria actual y avanza al siguiente día del ciclo.
 * Si se completa el ciclo de 7 días, marca el ciclo como completo.
 */
fun claimDailyReward(state: DailyRewardState): DailyRewardState {
    if (!canClaimDailyReward(state)) return state

    val currentDayIndex = state.currentDay - 1
    if (currentDayIndex < 0 || currentDayIndex >= state.cycleRewards.size) return state

    // Marcar la recompensa del día actual como reclamada
    val updatedRewards = state.cycleRewards.toMutableList()
    updatedRewards[currentDayIndex] = updatedRewards[currentDayIndex].copy(isClaimed = true)

    val nextDay = state.currentDay + 1
    val isCycleNowComplete = nextDay > state.cycleRewards.size

    return state.copy(
        currentDay = if (isCycleNowComplete) state.cycleRewards.size else nextDay,
        lastClaimTimestamp = System.currentTimeMillis(),
        cycleRewards = updatedRewards,
        isCycleComplete = isCycleNowComplete
    )
}

// ============================================================
// SISTEMA DE CRAFTING / FORJA
// ============================================================

data class CraftingRecipe(
    val id: String,
    val name: String,
    val description: String,
    val resultItemName: String,
    val resultItemType: String,
    val resultRarity: String,
    val resultLevel: Int,
    val materials: List<CraftingMaterial>,
    val requiredLevel: Int,
    val goldCost: Int
)

data class CraftingMaterial(
    val id: String,
    val name: String,
    val quantity: Int
)

/**
 * Catálogo completo de recetas de crafteo disponibles en Eldoria Chronicles.
 * Organizado en tres tiers: Básico, Avanzado y Maestro.
 */
object CraftingRecipes {
    val ALL_RECIPES: List<CraftingRecipe> = listOf(

        // ============================
        // TIER BÁSICO (nivel 1-10)
        // ============================

        CraftingRecipe(
            id = "iron_sword",
            name = "Espada de Hierro",
            description = "Una espada forjada en hierro sólido. Resistente y confiable.",
            resultItemName = "Espada de Hierro",
            resultItemType = "WEAPON",
            resultRarity = "RARO",
            resultLevel = 5,
            materials = listOf(
                CraftingMaterial(id = "iron", name = "Hierro", quantity = 3),
                CraftingMaterial(id = "leather", name = "Cuero", quantity = 1)
            ),
            requiredLevel = 1,
            goldCost = 200
        ),
        CraftingRecipe(
            id = "leather_armor",
            name = "Armadura de Cuero",
            description = "Armadura ligera hecha de cuero curtido. Ofrece protección básica.",
            resultItemName = "Armadura de Cuero",
            resultItemType = "ARMOR",
            resultRarity = "RARO",
            resultLevel = 3,
            materials = listOf(
                CraftingMaterial(id = "leather", name = "Cuero", quantity = 5)
            ),
            requiredLevel = 1,
            goldCost = 300
        ),
        CraftingRecipe(
            id = "greater_health_potion",
            name = "Poción de Vida Mayor",
            description = "Una poción que restaura una cantidad significativa de vida.",
            resultItemName = "Poción de Vida Mayor",
            resultItemType = "POTION",
            resultRarity = "RARO",
            resultLevel = 1,
            materials = listOf(
                CraftingMaterial(id = "herbs", name = "Hierbas", quantity = 2)
            ),
            requiredLevel = 1,
            goldCost = 100
        ),
        CraftingRecipe(
            id = "wooden_shield",
            name = "Escudo de Madera",
            description = "Escudo reforzado con hierro. Protege contra ataques básicos.",
            resultItemName = "Escudo de Madera",
            resultItemType = "SHIELD",
            resultRarity = "COMÚN",
            resultLevel = 2,
            materials = listOf(
                CraftingMaterial(id = "wood", name = "Madera", quantity = 4),
                CraftingMaterial(id = "iron", name = "Hierro", quantity = 2)
            ),
            requiredLevel = 1,
            goldCost = 150
        ),
        CraftingRecipe(
            id = "light_boots",
            name = "Botas Ligeras",
            description = "Botas de cuero que permiten moverse con agilidad.",
            resultItemName = "Botas Ligeras",
            resultItemType = "BOOTS",
            resultRarity = "RARO",
            resultLevel = 4,
            materials = listOf(
                CraftingMaterial(id = "leather", name = "Cuero", quantity = 3),
                CraftingMaterial(id = "iron", name = "Hierro", quantity = 1)
            ),
            requiredLevel = 1,
            goldCost = 200
        ),

        // ============================
        // TIER AVANZADO (nivel 15-35)
        // ============================

        CraftingRecipe(
            id = "great_steel_sword",
            name = "Gran Espada de Acero",
            description = "Una imponente espada de acero forjado. Brilla con un filo mortal.",
            resultItemName = "Gran Espada de Acero",
            resultItemType = "WEAPON",
            resultRarity = "ÉPICO",
            resultLevel = 20,
            materials = listOf(
                CraftingMaterial(id = "steel", name = "Acero", quantity = 5),
                CraftingMaterial(id = "crystal", name = "Cristal", quantity = 2),
                CraftingMaterial(id = "leather", name = "Cuero", quantity = 3)
            ),
            requiredLevel = 15,
            goldCost = 1000
        ),
        CraftingRecipe(
            id = "arcane_robe",
            name = "Túnica Arcana",
            description = "Túnica imbuida con energía arcana. Amplifica el poder mágico.",
            resultItemName = "Túnica Arcana",
            resultItemType = "ARMOR",
            resultRarity = "ÉPICO",
            resultLevel = 18,
            materials = listOf(
                CraftingMaterial(id = "mystic_silk", name = "Seda Mística", quantity = 4),
                CraftingMaterial(id = "crystal", name = "Cristal", quantity = 2)
            ),
            requiredLevel = 15,
            goldCost = 800
        ),
        CraftingRecipe(
            id = "power_ring",
            name = "Anillo de Poder",
            description = "Un anillo que canaliza energía pura. Aumenta todas las estadísticas.",
            resultItemName = "Anillo de Poder",
            resultItemType = "RING",
            resultRarity = "ÉPICO",
            resultLevel = 22,
            materials = listOf(
                CraftingMaterial(id = "gold_ore", name = "Oro", quantity = 2),
                CraftingMaterial(id = "blood_gem", name = "Gema de Sangre", quantity = 1)
            ),
            requiredLevel = 20,
            goldCost = 1500
        ),
        CraftingRecipe(
            id = "guardian_helmet",
            name = "Casco del Guardián",
            description = "Casco forjado para los protectores del reino. Resistencia excepcional.",
            resultItemName = "Casco del Guardián",
            resultItemType = "HELMET",
            resultRarity = "ÉPICO",
            resultLevel = 25,
            materials = listOf(
                CraftingMaterial(id = "steel", name = "Acero", quantity = 4),
                CraftingMaterial(id = "leather", name = "Cuero", quantity = 3)
            ),
            requiredLevel = 20,
            goldCost = 1200
        ),
        CraftingRecipe(
            id = "elven_gloves",
            name = "Guantes Élficos",
            description = "Guantes tejidos con seda mística por artesanos élficos.",
            resultItemName = "Guantes Élficos",
            resultItemType = "GLOVES",
            resultRarity = "ÉPICO",
            resultLevel = 20,
            materials = listOf(
                CraftingMaterial(id = "mystic_silk", name = "Seda Mística", quantity = 3),
                CraftingMaterial(id = "leather", name = "Cuero", quantity = 2)
            ),
            requiredLevel = 18,
            goldCost = 900
        ),

        // ============================
        // TIER MAESTRO (nivel 40+)
        // ============================

        CraftingRecipe(
            id = "dragon_blade",
            name = "Hoja del Dragón",
            description = "Forjada con escamas de dragón y acero ancestral. Arde con fuego dracónico.",
            resultItemName = "Hoja del Dragón",
            resultItemType = "WEAPON",
            resultRarity = "LEGENDARIO",
            resultLevel = 45,
            materials = listOf(
                CraftingMaterial(id = "dragon_scale", name = "Escama de Dragón", quantity = 3),
                CraftingMaterial(id = "steel", name = "Acero", quantity = 2),
                CraftingMaterial(id = "shadow_essence", name = "Esencia de Sombra", quantity = 1)
            ),
            requiredLevel = 40,
            goldCost = 5000
        ),
        CraftingRecipe(
            id = "phoenix_aegis",
            name = "Égida del Fénix",
            description = "Armadura bendecida por el Fénix. Renace de las cenizas con su portador.",
            resultItemName = "Égida del Fénix",
            resultItemType = "ARMOR",
            resultRarity = "LEGENDARIO",
            resultLevel = 50,
            materials = listOf(
                CraftingMaterial(id = "phoenix_feather", name = "Pluma de Fénix", quantity = 2),
                CraftingMaterial(id = "dragon_scale", name = "Escama de Dragón", quantity = 4),
                CraftingMaterial(id = "crystal", name = "Cristal", quantity = 3)
            ),
            requiredLevel = 45,
            goldCost = 8000
        ),
        CraftingRecipe(
            id = "void_scepter",
            name = "Cetro del Vacío",
            description = "Un cetro que manipula la esencia del vacío entre dimensiones.",
            resultItemName = "Cetro del Vacío",
            resultItemType = "WEAPON",
            resultRarity = "LEGENDARIO",
            resultLevel = 55,
            materials = listOf(
                CraftingMaterial(id = "shadow_essence", name = "Esencia de Sombra", quantity = 3),
                CraftingMaterial(id = "pure_crystal", name = "Cristal Puro", quantity = 2),
                CraftingMaterial(id = "ancient_relic", name = "Reliquia Antigua", quantity = 1)
            ),
            requiredLevel = 50,
            goldCost = 6000
        ),
        CraftingRecipe(
            id = "seraph_wings",
            name = "Alas del Serafín",
            description = "Alas divinas que otorgan poder celestial a su portador.",
            resultItemName = "Alas del Serafín",
            resultItemType = "WINGS",
            resultRarity = "LEGENDARIO",
            resultLevel = 60,
            materials = listOf(
                CraftingMaterial(id = "phoenix_feather", name = "Pluma de Fénix", quantity = 5),
                CraftingMaterial(id = "gold_ore", name = "Oro", quantity = 2)
            ),
            requiredLevel = 55,
            goldCost = 10000
        ),
        CraftingRecipe(
            id = "cosmos_relic",
            name = "Reliquia del Cosmos",
            description = "Un artefacto que contiene el poder de las estrellas y el cosmos infinito.",
            resultItemName = "Reliquia del Cosmos",
            resultItemType = "RELIC",
            resultRarity = "ARCANO",
            resultLevel = 70,
            materials = listOf(
                CraftingMaterial(id = "infinite_diamond", name = "Diamante Infinito", quantity = 3),
                CraftingMaterial(id = "shadow_essence", name = "Esencia de Sombra", quantity = 2),
                CraftingMaterial(id = "pure_crystal", name = "Cristal Puro", quantity = 1)
            ),
            requiredLevel = 65,
            goldCost = 15000
        )
    )
}

/**
 * Obtiene la lista de recetas disponibles según el nivel del jugador.
 * Solo devuelve recetas cuyo nivel requerido sea menor o igual al nivel del jugador.
 */
fun getAvailableRecipes(playerLevel: Int): List<CraftingRecipe> {
    return CraftingRecipes.ALL_RECIPES.filter { it.requiredLevel <= playerLevel }
}

/**
 * Verifica si el jugador tiene los materiales y el oro suficiente para fabricar una receta.
 * Comprueba los materiales en el inventario y los conteos de materiales separados.
 */
fun canCraftRecipe(
    recipe: CraftingRecipe,
    inventory: List<Item>,
    gold: Int,
    materials: Map<String, Int>
): Boolean {
    // Verificar oro suficiente
    if (gold < recipe.goldCost) return false

    // Combinar conteos de materiales del inventario y el mapa de materiales
    val totalMaterials = getMaterialCounts(inventory).toMutableMap()
    materials.forEach { (key, value) ->
        totalMaterials[key] = (totalMaterials[key] ?: 0) + value
    }

    // Verificar que se tengan todos los materiales requeridos en cantidad suficiente
    return recipe.materials.all { material ->
        (totalMaterials[material.id] ?: 0) >= material.quantity
    }
}

/**
 * Cuenta los materiales disponibles en el inventario del jugador.
 * Agrupa los objetos por su nombre y cuenta las cantidades.
 */
fun getMaterialCounts(inventory: List<Item>): Map<String, Int> {
    return inventory.groupingBy { it.name }.eachCount()
}
