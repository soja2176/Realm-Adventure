package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class Item(
    val id: String,
    val name: String,
    val type: String, // "WEAPON", "ARMOR", "RING", "SHIELD", "POTION"
    val rarity: String, // "COMÚN", "RARO", "ÉPICO", "LEGENDARIO", "ARCANO", "UNIVERSAL"
    val strBonus: Int = 0,
    val dexBonus: Int = 0,
    val intBonus: Int = 0,
    val conBonus: Int = 0,
    val dmgBonus: Int = 0,
    val defBonus: Int = 0,
    val hpRegen: Int = 0,
    val description: String = "",
    val itemLevel: Int = 1,
    val imageResName: String = ""
) {
    fun getStatDescription(): String {
        val list = mutableListOf<String>()
        if (strBonus > 0) list.add("STR +$strBonus")
        if (dexBonus > 0) list.add("DEX +$dexBonus")
        if (intBonus > 0) list.add("INT +$intBonus")
        if (conBonus > 0) list.add("CON +$conBonus")
        if (dmgBonus > 0) list.add("Daño +$dmgBonus")
        if (defBonus > 0) list.add("Def +$defBonus")
        if (hpRegen > 0) list.add("Reg.HP +$hpRegen")
        return if (list.isEmpty()) description.ifEmpty { "Equipo Místico" } else list.joinToString(" • ")
    }
}

fun getItemSellValue(item: Item): Int {
    val baseMultiplier = when (item.rarity.uppercase()) {
        "UNIVERSAL" -> 500
        "ARCANO" -> 350
        "LEGENDARIO", "LEGENDARY" -> 220
        "ÉPICO", "EPIC" -> 120
        "RARO", "RARE" -> 60
        else -> 20
    }
    return maxOf(10, baseMultiplier + (item.itemLevel * 15))
}

data class Talent(
    val id: String,
    val name: String,
    val description: String,
    val maxRank: Int = 3,
    val currentRank: Int = 0,
    val category: String, // "COMBAT", "MAGIC", "SHADOW"
    val prerequisiteId: String? = null,
    val row: Int,
    val col: Int
)

data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val manaCost: Int = 10,
    val minLevel: Int = 1,
    val cooldown: Int = 0,
    val damageMultiplier: Double = 1.0,
    val healingMultiplier: Double = 0.0,
    val isUltimate: Boolean = false,
    val classRestriction: String? = null
)

data class Quest(
    val id: String,
    val name: String,
    val description: String,
    val levelReq: Int,
    val expReward: Int,
    val goldReward: Int,
    val monsterName: String,
    val monsterHp: Int,
    val monsterAtk: Int,
    val monsterDef: Int,
    val monsterRarity: String = "NORMAL", // "NORMAL", "ELITE", "BOSS"
    val isCompleted: Boolean = false,
    val mapX: Int,
    val mapY: Int
)

object GameJsonParser {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    inline fun <reified T> toJson(data: T): String {
        return try {
            val adapter = moshi.adapter(T::class.java)
            adapter.toJson(data)
        } catch (e: Exception) {
            ""
        }
    }

    inline fun <reified T> fromJson(json: String): T? {
        if (json.isEmpty()) return null
        return try {
            val adapter = moshi.adapter(T::class.java)
            adapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    inline fun <reified T> listToJson(list: List<T>): String {
        return try {
            val type = Types.newParameterizedType(List::class.java, T::class.java)
            val adapter = moshi.adapter<List<T>>(type)
            adapter.toJson(list)
        } catch (e: Exception) {
            "[]"
        }
    }

    inline fun <reified T> listFromJson(json: String): List<T> {
        if (json.isEmpty() || json == "[]") return emptyList()
        return try {
            val type = Types.newParameterizedType(List::class.java, T::class.java)
            val adapter = moshi.adapter<List<T>>(type)
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

data class PlayerStats(
    val hp: Int,
    val mp: Int,
    val strength: Int,
    val agility: Int,
    val maxHp: Int,
    val maxMp: Int
)

