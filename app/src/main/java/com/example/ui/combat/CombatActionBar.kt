package com.example.ui.combat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.CombatState
import com.example.data.GameJsonParser
import com.example.data.Item
import com.example.data.Skill
import com.example.data.content.EldoriaPotions
import com.example.ui.SkillGlassTheme
import com.example.ui.StainedGlassSkillSlot
import com.example.ui.design.CombatFx
import com.example.ui.design.combatFxForSkill

// ══════════════════════════════════════════════════════════════════════════════
//  BARRA DE ACCIONES DE COMBATE — una sola, compartida.
//
//  El calabozo tenía su propia barra de botones planos mientras la superficie
//  usaba los cristales emplomados. Dos barras distintas para el mismo combate
//  es una incoherencia que se nota: esta es la única, y el descenso sólo le
//  añade el hueco de la bestia.
// ══════════════════════════════════════════════════════════════════════════════

/** Hueco extra de órdenes de bestia. Sólo lo usa el combate de profundidad. */
data class CombatPetSlot(
    val cooldown: Int,
    val onClick: () -> Unit
)

@Composable
fun CombatActionBar(
    charClass: String,
    skills: List<Skill>,
    inventoryJson: String,
    combatState: CombatState,
    onBasicAttack: () -> Unit,
    onSkill: (Skill) -> Unit,
    onPotion: (String) -> Unit,
    onFlee: () -> Unit,
    modifier: Modifier = Modifier,
    petSlot: CombatPetSlot? = null
) {
    val inventory = remember(inventoryJson) { GameJsonParser.listFromJson<Item>(inventoryJson) }
    val potionCount = remember(inventory) { inventory.count { it.type.uppercase() == "POTION" } }

    // El inventario guarda un Item POR UNIDAD, así que hay que agrupar para
    // enseñar "Poción Menor ×7" y no siete filas.
    val potionStacks = remember(inventoryJson) {
        inventory.filter { it.type.uppercase() == "POTION" }
            .map { EldoriaPotions.fromItem(it.id, it.name) }
            .groupingBy { it }
            .eachCount()
            .map { (spec, count) -> PotionStack(spec, count) }
            .sortedBy { it.spec.unlockLevel }
    }

    var potionDrawerOpen by remember { mutableStateOf(false) }
    val heroAction = basicAttackFor(charClass)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Efectos activos: sin esto un buff de cuatro turnos es invisible en
        // cuanto pasa la línea del registro.
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
                    onPotion(spec.id)
                },
                onDismiss = { potionDrawerOpen = false },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

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
                // 1. Golpe básico: lleva el nombre y el icono de TU clase.
                StainedGlassSkillSlot(
                    title = heroAction.label,
                    badgeLabel = heroAction.badge,
                    costText = "0",
                    icon = heroAction.icon,
                    glassTheme = when (charClass) {
                        "Mago" -> SkillGlassTheme.PURPLE
                        "Pícaro" -> SkillGlassTheme.TURQUOISE
                        "Clérigo" -> SkillGlassTheme.AMBER
                        else -> SkillGlassTheme.CRIMSON
                    },
                    enabled = combatState.playerTurn,
                    testTag = "combat_attack_button",
                    onClick = onBasicAttack,
                    artKey = "action_basic_" + when (charClass) {
                        "Mago" -> "mago"
                        "Pícaro" -> "picaro"
                        "Clérigo" -> "clerigo"
                        else -> "guerrero"
                    }
                )

                // 2. Habilidades: el cristal y el icono siguen al ELEMENTO, el
                // mismo que verás estallar sobre el enemigo al pulsarla.
                skills.forEachIndexed { index, skill ->
                    val skillFx = combatFxForSkill(
                        skillId = skill.id,
                        healing = skill.healingMultiplier > 0.0,
                        damaging = skill.damageMultiplier > 0.0
                    )
                    StainedGlassSkillSlot(
                        title = skill.name,
                        badgeLabel = "Sk ${index + 1}",
                        costText = "${skill.manaCost}",
                        icon = when (skillFx) {
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
                        },
                        glassTheme = when (skillFx) {
                            CombatFx.BLOOD -> SkillGlassTheme.CRIMSON
                            CombatFx.FIRE -> SkillGlassTheme.AMBER
                            CombatFx.NECROTIC, CombatFx.POISON, CombatFx.HEAL -> SkillGlassTheme.EMERALD
                            CombatFx.HOLY, CombatFx.WARCRY -> SkillGlassTheme.AMBER
                            CombatFx.FROST -> SkillGlassTheme.TURQUOISE
                            else -> SkillGlassTheme.PURPLE
                        },
                        enabled = combatState.playerTurn && combatState.playerCurrentMp >= skill.manaCost,
                        testTag = "skill_${skill.id}",
                        onClick = { onSkill(skill) },
                        artKey = "skill_${skill.id}"
                    )
                }

                // 3. Órdenes de bestia: sólo en el descenso.
                if (petSlot != null) {
                    StainedGlassSkillSlot(
                        title = "Bestia",
                        badgeLabel = if (petSlot.cooldown > 0) "${petSlot.cooldown}t" else "Lista",
                        costText = "",
                        icon = Icons.Default.Pets,
                        glassTheme = SkillGlassTheme.EMERALD,
                        enabled = combatState.playerTurn && petSlot.cooldown <= 0,
                        testTag = "combat_pet_command_btn",
                        onClick = petSlot.onClick,
                        artKey = "action_bestia"
                    )
                }

                // 4. Zurrón: abre el cajón en vez de beber a ciegas.
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

                // 5. Huir.
                StainedGlassSkillSlot(
                    title = "Huir",
                    badgeLabel = "Huir",
                    costText = "",
                    icon = Icons.Default.DirectionsRun,
                    glassTheme = SkillGlassTheme.PURPLE,
                    enabled = combatState.playerTurn,
                    testTag = "combat_flee_button",
                    onClick = onFlee,
                    artKey = "action_huir"
                )
            }
        }
    }
}
