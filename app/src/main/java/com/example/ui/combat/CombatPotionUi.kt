package com.example.ui.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import com.example.data.content.PotionEffect
import com.example.data.content.PotionSpec
import com.example.ui.art.EldoriaArt
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaType

// ══════════════════════════════════════════════════════════════════════════════
//  PANEL DE POCIONES Y ACCIONES DE COMBATE
//
//  Antes había UN botón de poción que bebía el primer frasco del inventario.
//  Con un solo tipo de poción eso bastaba; con seis, elegir cuál bebes ES la
//  jugada — curar a tope, aguantar con regeneración o apostar por la furia — y
//  un botón ciego se la comía entera.
// ══════════════════════════════════════════════════════════════════════════════

/** Frascos iguales agrupados: el inventario guarda uno por unidad. */
data class PotionStack(val spec: PotionSpec, val count: Int)

/** Color por familia de efecto: se lee el papel del frasco antes que su nombre. */
fun potionAccent(effect: PotionEffect): Color = when (effect) {
    PotionEffect.RESTORE -> Eldoria.VitaeBright
    PotionEffect.REGEN -> Eldoria.Vitae
    PotionEffect.DAMAGE -> Eldoria.Ember
    PotionEffect.EVASION -> Eldoria.ArcaneBright
    PotionEffect.DEFENSE -> Eldoria.Silver
}

fun potionIcon(effect: PotionEffect): ImageVector = when (effect) {
    PotionEffect.RESTORE -> Icons.Default.Favorite
    PotionEffect.REGEN -> Icons.Default.AutoAwesome
    PotionEffect.DAMAGE -> Icons.Default.LocalFireDepartment
    PotionEffect.EVASION -> Icons.Default.VisibilityOff
    PotionEffect.DEFENSE -> Icons.Default.Shield
}

/**
 * Cajón de pociones: la lista de lo que llevas, con lo que hace cada frasco.
 *
 * Se enseña la DESCRIPCIÓN completa y no sólo el nombre, porque la decisión
 * ("¿me curo o pego más fuerte?") depende del número, y obligar a recordarlo de
 * memoria entre seis frascos convertiría la elección en adivinanza.
 */
@Composable
fun CombatPotionDrawer(
    stacks: List<PotionStack>,
    enabled: Boolean,
    onPick: (PotionSpec) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    EldoriaPanel(
        modifier = modifier.fillMaxWidth(),
        edge = EldoriaEdge.Vitae,
        glow = true,
        filigree = true
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TU ZURRÓN",
                style = EldoriaType.heading,
                color = Eldoria.TextGold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Cerrar",
                style = EldoriaType.small,
                color = Eldoria.TextMid,
                modifier = Modifier
                    .clip(RoundedCornerShape(Eldoria.R8))
                    .clickable { onDismiss() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(Eldoria.S8))

        if (stacks.isEmpty()) {
            Text(
                text = "No te queda ni un frasco. El mercader vende, y en el calabozo se agradece.",
                style = EldoriaType.lore,
                color = Eldoria.TextLow
            )
            return@EldoriaPanel
        }

        LazyColumn(
            modifier = Modifier.heightIn(max = 260.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(stacks) { stack ->
                PotionRow(stack = stack, enabled = enabled, onPick = onPick)
            }
        }
    }
}

@Composable
private fun PotionRow(stack: PotionStack, enabled: Boolean, onPick: (PotionSpec) -> Unit) {
    val accent = potionAccent(stack.spec.effect)
    val art = EldoriaArt.of(stack.spec.artKey)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Eldoria.R8))
            .background(Eldoria.Ink.copy(alpha = 0.5f))
            .border(1.dp, accent.copy(alpha = if (enabled) 0.5f else 0.2f), RoundedCornerShape(Eldoria.R8))
            .clickable(enabled = enabled) { onPick(stack.spec) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(Eldoria.R8))
                .background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            if (art != null) {
                Image(
                    painter = painterResource(id = art),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(Eldoria.R8))
                )
            } else {
                Icon(
                    imageVector = potionIcon(stack.spec.effect),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(Modifier.width(Eldoria.S8))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stack.spec.name,
                    style = EldoriaType.body,
                    color = if (enabled) Eldoria.TextHi else Eldoria.TextLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "×${stack.count}",
                    style = EldoriaType.small,
                    color = accent
                )
            }
            Text(
                text = stack.spec.description,
                style = EldoriaType.lore,
                color = Eldoria.TextLow,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Distintivos de los efectos activos, con los turnos que les quedan.
 *
 * Sin esto un buff de cuatro turnos es invisible: se bebe, aparece una línea en
 * el registro y ya no vuelve a saberse si sigue puesto. Los turnos restantes
 * son justo el dato que decide si vale la pena beber otro frasco.
 */
@Composable
fun CombatBuffChips(
    regenTurns: Int,
    damageTurns: Int,
    damagePotency: Double,
    evasionTurns: Int,
    evasionPotency: Double,
    wardTurns: Int,
    wardPotency: Double,
    modifier: Modifier = Modifier
) {
    val chips = buildList {
        if (regenTurns > 0) add(Triple("REGEN", regenTurns, PotionEffect.REGEN))
        if (damageTurns > 0) {
            add(Triple("+${(damagePotency * 100).toInt()} % DAÑO", damageTurns, PotionEffect.DAMAGE))
        }
        if (evasionTurns > 0) {
            add(Triple("+${(evasionPotency * 100).toInt()} % EVA", evasionTurns, PotionEffect.EVASION))
        }
        if (wardTurns > 0) {
            add(Triple("-${(wardPotency * 100).toInt()} % RECIBIDO", wardTurns, PotionEffect.DEFENSE))
        }
    }
    if (chips.isEmpty()) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        chips.forEach { (label, turns, effect) ->
            val accent = potionAccent(effect)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(Eldoria.R8))
                    .background(accent.copy(alpha = 0.16f))
                    .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(Eldoria.R8))
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = potionIcon(effect),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(text = "$label · ${turns}t", style = EldoriaType.small, color = accent)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  IDENTIDAD DE CLASE EN LA BARRA DE ACCIONES
//
//  El golpe básico se llamaba "Ataque Físico" con el mismo icono para los
//  cuatro héroes. Un Mago que "ataca físicamente" con un puño dibujado es la
//  clase de detalle que hace que la barra parezca de prueba: el juego ya sabe
//  qué eres, sólo no lo estaba diciendo.
// ══════════════════════════════════════════════════════════════════════════════

data class ClassAction(val label: String, val badge: String, val icon: ImageVector, val accent: Color)

fun basicAttackFor(charClass: String): ClassAction = when (charClass) {
    "Mago" -> ClassAction("Impacto Arcano", "Bastón", Icons.Default.AutoAwesome, Eldoria.ArcaneBright)
    "Pícaro" -> ClassAction("Puñalada", "Dagas", Icons.Default.VisibilityOff, Eldoria.Silver)
    "Clérigo" -> ClassAction("Maza Sagrada", "Maza", Icons.Default.WbSunny, Eldoria.GoldBright)
    "Guerrero" -> ClassAction("Tajo", "Espada", Icons.Default.Gavel, Eldoria.Ember)
    else -> ClassAction("Ataque Físico", "Ataque", Icons.Default.SportsMartialArts, Eldoria.Ember)
}

/** Lema de la clase para la cabecera de la barra. */
fun classMotto(charClass: String): String = when (charClass) {
    "Guerrero" -> "Acero y aguante"
    "Mago" -> "El maná es tu munición"
    "Pícaro" -> "Golpea antes de que te vean"
    "Clérigo" -> "Sostén la línea"
    else -> "Que la fortuna te acompañe"
}

/** Icono de frío para completar el juego de elementos de las habilidades. */
val FrostActionIcon: ImageVector = Icons.Default.AcUnit
