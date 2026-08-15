package com.example.data.model

import com.example.data.Item

// ═══════════════════════════════════════════════════════════════════════════
//  ELDORIA CHRONICLES — MODELOS DE DATOS DEL OVERHAUL "RENACER DE ELDORIA"
//
//  Regla dura: todas las clases persistidas usan SÓLO String / Int / Boolean /
//  Float / List<String> / List<Int> / Map<String,Int> / List<Item> y TODOS sus
//  campos llevan valor por defecto (Moshi reflexivo + backups antiguos).
//  Ningún enum vive dentro de un modelo serializado.
//  Los catálogos (PetSpecies, EnemySpecies, ...) no se persisten.
// ═══════════════════════════════════════════════════════════════════════════

// ─── MASCOTAS ───

/** Instancia viva de una mascota del jugador. Persistida. */
data class PetRecord(
    val id: String = "", val speciesId: String = "", val name: String = "",
    val rarity: String = "COMÚN", val level: Int = 1, val exp: Int = 0,
    val satiety: Int = 100, val bond: Int = 0,
    val disciplineAtk: Int = 0, val disciplineDef: Int = 0, val disciplineVit: Int = 0,
    val stage: Int = 1, val traits: List<String> = emptyList(),
    val imageResName: String = "", val sigilSeed: Int = 0, val paletteKey: String = "EMBER",
    val injuries: Int = 0, val bornAt: Long = 0L
)

/** Plantilla de catálogo. No se persiste. */
data class PetSpecies(
    val id: String, val name: String, val title: String, val lore: String,
    val rarity: String, val paletteKey: String, val sigilSeed: Int,
    val imageResName: String = "", val signatureTrait: String,
    val baseAtk: Int, val baseDef: Int, val baseVit: Int,
    val favoriteFood: String, val evolutionNames: List<String>
)

/** Rasgo de mascota. Catálogo, no se persiste. */
data class PetTrait(val id: String, val name: String, val description: String, val magnitude: Int, val tone: String)

/** Proyección de combate de la mascota activa. Persistible. */
data class PetCombatProfile(
    val petId: String = "", val name: String = "", val stage: Int = 1,
    val attack: Int = 0, val guard: Int = 0, val heal: Int = 0,
    val commandCooldown: Int = 3, val traits: List<String> = emptyList(),
    val paletteKey: String = "EMBER", val sigilSeed: Int = 0, val imageResName: String = ""
)

// ─── EXPEDICIONES ───

/** Nodo del grafo de una expedición. Persistido dentro de ExpeditionState. */
data class ExpeditionRoom(
    val id: Int = 0, val kind: String = "COMBATE", val depth: Int = 0, val column: Int = 0,
    val label: String = "", val next: List<Int> = emptyList(),
    val cleared: Boolean = false, val revealed: Boolean = false,
    val locked: Boolean = false, val payload: String = ""
)

/** Estado completo de la expedición en curso. Persistido. */
data class ExpeditionState(
    val active: Boolean = false, val dungeonId: Int = 0, val dungeonName: String = "",
    val speciesLabel: String = "", val paletteKey: String = "EMBER", val artResName: String = "",
    val seed: Long = 0L, val rooms: List<ExpeditionRoom> = emptyList(),
    val currentRoomId: Int = -1, val availableRoomIds: List<Int> = emptyList(),
    val depth: Int = 0, val maxDepth: Int = 3,
    val torch: Int = 100, val seals: List<String> = emptyList(), val boons: List<String> = emptyList(),
    val runLoot: List<Item> = emptyList(), val shards: Int = 0, val keys: Int = 0,
    val persistentHp: Int = 0, val persistentMp: Int = 0,
    val roomsCleared: Int = 0, val elitesCleared: Int = 0,
    val awaitingChoice: Boolean = false, val finished: Boolean = false, val victory: Boolean = false,
    val log: List<String> = emptyList()
)

/** Bendición de expedición. Catálogo. */
data class ExpeditionBoon(
    val id: String, val name: String, val description: String, val tone: String,
    val atkPct: Int = 0, val defPct: Int = 0, val hpPct: Int = 0, val critPct: Int = 0,
    val goldPct: Int = 0, val torchPct: Int = 0, val lootTiers: Int = 0, val drawback: String = ""
)

/** Sello (modificador de run). Catálogo. */
data class ExpeditionSeal(val id: String, val name: String, val description: String, val dangerMult: Float, val rewardMult: Float, val tone: String)

/** Destino de expedición. Catálogo. */
data class DungeonBlueprint(
    val dungeonId: Int, val name: String, val species: String, val levelReq: Int,
    val paletteKey: String, val artResName: String, val ambience: String,
    val floorLabels: List<String>, val finalBossName: String, val finalBossTitle: String,
    val loreShort: String, val isAbyss: Boolean = false
)

/** Oferta modal dentro de la expedición (bendición, botín, evento, mercader). */
data class ExpeditionOffer(
    val kind: String = "",            // "BOON" | "LOOT" | "EVENT" | "MERCHANT" | "REST" | "GATE"
    val title: String = "", val description: String = "",
    val optionIds: List<String> = emptyList(),
    val optionTitles: List<String> = emptyList(),
    val optionSubtitles: List<String> = emptyList(),
    val optionTones: List<String> = emptyList()
)

// ─── BESTIARIO ───

/** Especie de enemigo del bestiario. Catálogo. */
data class EnemySpecies(
    val id: String, val name: String, val kingdomId: String, val archetype: String,
    val signatureMove: String, val artKey: String, val tier: Int,
    val lore: String, val weakness: String, val resistance: String
)

/** Arquetipo de comportamiento y escalado. Catálogo. */
data class EnemyArchetype(val id: String, val name: String, val description: String, val hpMult: Float, val atkMult: Float, val defMult: Float, val behaviour: String, val tone: String)

/** Afijo de élite. Catálogo. */
data class EnemyAffix(val id: String, val name: String, val description: String, val tone: String, val dangerWeight: Int)

/** Fila del códice de bestiario ya resuelta para la UI. */
data class BestiaryEntry(val species: EnemySpecies, val kills: Int, val discovered: Boolean, val archetypeName: String)

/** Devuelto por el controlador para decorar un Combatant en startCombat. */
data class EnemyDecoration(
    val speciesId: String = "", val displayName: String = "", val archetype: String = "BRUTO",
    val affixes: List<String> = emptyList(), val hpMult: Float = 1f, val atkMult: Float = 1f,
    val defMult: Float = 1f, val signatureMove: String = "", val artKey: String = ""
)

// ─── MINIJUEGOS ───

data class MinigameRequest(
    val id: String = "", val difficulty: Int = 1, val title: String = "",
    val contextJson: String = "", val rewardScale: Float = 1f, val originScreen: String = ""
)

data class MinigameResult(
    val id: String = "", val success: Boolean = false, val score: Int = 0,
    val perfect: Int = 0, val rating: String = "", val contextJson: String = ""
)

// ─── CONTRATOS ───

/** Plantilla de contrato. Catálogo. */
data class ContractDef(
    val id: String, val title: String, val description: String, val kind: String,
    val target: String, val amount: Int, val goldReward: Int, val expReward: Int,
    val materialReward: String, val materialQty: Int, val tier: Int
)

/** Contrato aceptado por el jugador. Persistido. */
data class ContractProgress(
    val id: String = "", val defId: String = "", val title: String = "", val description: String = "",
    val kind: String = "", val target: String = "", val progress: Int = 0, val amount: Int = 1,
    val goldReward: Int = 0, val expReward: Int = 0,
    val materialReward: String = "", val materialQty: Int = 0,
    val completed: Boolean = false, val claimed: Boolean = false, val tier: Int = 1
)

// ─── MATERIALES / AJUSTES / ESTADÍSTICAS ───

/** Material de artesanía. Catálogo. */
data class MaterialDef(val id: String, val name: String, val rarity: String, val description: String, val imageResName: String = "", val paletteKey: String = "IRON")

data class GameSettings(
    val musicEnabled: Boolean = true, val sfxEnabled: Boolean = true, val hapticsEnabled: Boolean = true,
    val damageNumbers: Boolean = true, val embersEnabled: Boolean = true,
    val reactionAssist: Boolean = false, val autoCombatDefault: Boolean = false,
    val screenShake: Boolean = true, val textScale: Int = 100
)

data class RunStats(
    val totalKills: Int = 0, val bossKills: Int = 0, val dungeonsCleared: Int = 0,
    val deepestDepth: Int = 0, val petsOwned: Int = 0, val speciesDiscovered: Int = 0
)

data class EldoriaToastMessage(val message: String = "", val tone: String = "GOLD", val stamp: Long = 0L)
