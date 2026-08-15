package com.example.ui.design

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

object Eldoria {

    // ───────── SUPERFICIES (fondo → primer plano) ─────────
    val Abyss        = Color(0xFF05060B)
    val Ink          = Color(0xFF090B12)
    val Slate        = Color(0xFF0F121C)
    val Panel        = Color(0xFF151926)
    val PanelHi      = Color(0xFF1D2231)
    val PanelSunken  = Color(0xFF0B0D15)
    val Parchment    = Color(0xFFE7DBBE)
    val ParchmentDim = Color(0xFFC5B694)
    val ParchmentInk = Color(0xFF2B2114)

    // ───────── METALES ─────────
    val GoldBright   = Color(0xFFFFEDB0)
    val Gold         = Color(0xFFD9A441)
    val GoldDeep     = Color(0xFF8A6420)
    val GoldShadow   = Color(0xFF43300F)
    val Silver       = Color(0xFFCBD5E1)
    val SilverDeep   = Color(0xFF6C7889)
    val Iron         = Color(0xFF39404F)
    val IronDeep     = Color(0xFF212734)
    val IronEdge     = Color(0xFF4E586B)

    // ───────── BRASA / ANTORCHA (identidad de calabozo) ─────────
    val EmberCore    = Color(0xFFFFD79A)
    val Ember        = Color(0xFFFF8A3D)
    val EmberDeep    = Color(0xFFB33A0A)
    val EmberShadow  = Color(0xFF3B1503)
    val Ash          = Color(0xFF4A4038)

    // ───────── SANGRE / VIDA ─────────
    val Blood        = Color(0xFFC62828)
    val BloodBright  = Color(0xFFFF5A5A)
    val BloodDeep    = Color(0xFF52090D)

    // ───────── MANÁ ─────────
    val Mana         = Color(0xFF3D7BD6)
    val ManaBright   = Color(0xFF8FB9FF)
    val ManaDeep     = Color(0xFF0C2657)

    // ───────── VITALIDAD / EXPERIENCIA ─────────
    val Vitae        = Color(0xFF3FBF6F)
    val VitaeBright  = Color(0xFF83EAA9)
    val VitaeDeep    = Color(0xFF0D3520)

    // ───────── ARCANO ─────────
    val Arcane       = Color(0xFF9B5DE5)
    val ArcaneBright = Color(0xFFD7ADFF)
    val ArcaneDeep   = Color(0xFF260F42)

    // ───────── RAREZAS (idénticas a las vigentes: NO cambiar) ─────────
    val RarityCommon    = Color(0xFF90A4AE)
    val RarityRare      = Color(0xFF1E88E5)
    val RarityEpic      = Color(0xFF8E24AA)
    val RarityLegendary = Color(0xFFFF8F00)
    val RarityArcano    = Color(0xFFD500F9)
    val RarityUniversal = Color(0xFF00E5FF)

    // ───────── TEXTO ─────────
    val TextHi   = Color(0xFFF4F1E8)
    val TextMid  = Color(0xFFB7BFCC)
    val TextLow  = Color(0xFF7C8697)
    val TextGold = Color(0xFFE9C46A)
    val TextOnGold = Color(0xFF16120A)

    // ───────── ESTADOS ─────────
    val Success = Color(0xFF3FBF6F)
    val Warning = Color(0xFFE9A13B)
    val Danger  = Color(0xFFE04B4B)
    val Info    = Color(0xFF4FC3F7)

    // ───────── VELOS / BRILLOS ─────────
    val Scrim      = Color(0xD905060B)
    val ScrimSoft  = Color(0x9905060B)
    val ScrimGlass = Color(0x66000000)
    val GlowGold   = Color(0x33D9A441)
    val GlowEmber  = Color(0x40FF8A3D)
    val GlowArcane = Color(0x339B5DE5)

    // ───────── RADIOS ─────────
    val R4: Dp = 4.dp;  val R8: Dp = 8.dp;   val R12: Dp = 12.dp
    val R16: Dp = 16.dp; val R20: Dp = 20.dp; val R28: Dp = 28.dp

    // ───────── ESPACIADO (escala 4) ─────────
    val S2: Dp = 2.dp;  val S4: Dp = 4.dp;  val S6: Dp = 6.dp;  val S8: Dp = 8.dp
    val S12: Dp = 12.dp; val S16: Dp = 16.dp; val S20: Dp = 20.dp
    val S24: Dp = 24.dp; val S32: Dp = 32.dp; val S40: Dp = 40.dp

    // ───────── TRAZOS ─────────
    val StrokeHair: Dp = 0.75.dp
    val StrokeThin: Dp = 1.dp
    val StrokeMed: Dp  = 1.5.dp
    val StrokeBold: Dp = 2.5.dp
    val StrokeHeavy: Dp = 4.dp

    // ───────── TAMAÑO MÍNIMO DE TEXTO (regla dura) ─────────
    val MinFontSize: TextUnit = 11.sp

    // ───────── BRUSHES ─────────
    fun screenBrush(): Brush =
        Brush.verticalGradient(listOf(Abyss, Ink, Slate, Ink, Abyss))

    fun panelBrush(): Brush =
        Brush.verticalGradient(listOf(PanelHi, Panel, PanelSunken))

    fun sunkenBrush(): Brush =
        Brush.verticalGradient(listOf(PanelSunken, Abyss))

    /** depth 0..3 → tinte creciente de brasa para expediciones. */
    fun depthBrush(depth: Int): Brush = when (depth.coerceIn(0, 3)) {
        0 -> Brush.verticalGradient(listOf(Color(0xFF0B0E16), Color(0xFF090B12), Color(0xFF05060B)))
        1 -> Brush.verticalGradient(listOf(Color(0xFF141018), Color(0xFF0C0910), Color(0xFF060409)))
        2 -> Brush.verticalGradient(listOf(Color(0xFF1A0F0C), Color(0xFF100807), Color(0xFF070303)))
        else -> Brush.verticalGradient(listOf(Color(0xFF1E0B06), Color(0xFF120503), Color(0xFF080101)))
    }

    fun goldEdge(): Brush   = Brush.verticalGradient(listOf(GoldBright, Gold, GoldDeep))
    fun ironEdge(): Brush   = Brush.verticalGradient(listOf(IronEdge, Iron, IronDeep))
    fun emberEdge(): Brush  = Brush.verticalGradient(listOf(EmberCore, Ember, EmberDeep))
    fun arcaneEdge(): Brush = Brush.verticalGradient(listOf(ArcaneBright, Arcane, ArcaneDeep))
    fun bloodEdge(): Brush  = Brush.verticalGradient(listOf(BloodBright, Blood, BloodDeep))
    fun vitaeEdge(): Brush  = Brush.verticalGradient(listOf(VitaeBright, Vitae, VitaeDeep))
    fun silverEdge(): Brush = Brush.verticalGradient(listOf(Color(0xFFEFF4FA), Silver, SilverDeep))

    /** Acepta ES e EN (COMÚN/RARO/ÉPICO/LEGENDARIO/ARCANO/UNIVERSAL + RARE/EPIC/LEGENDARY). */
    fun rarityColor(rarity: String): Color = when (rarity.uppercase()) {
        "UNIVERSAL" -> RarityUniversal
        "ARCANO", "ARCANE" -> RarityArcano
        "LEGENDARIO", "LEGENDARY" -> RarityLegendary
        "ÉPICO", "EPICO", "EPIC" -> RarityEpic
        "RARO", "RARE" -> RarityRare
        else -> RarityCommon
    }

    fun rarityBrush(rarity: String): Brush {
        val c = rarityColor(rarity)
        return Brush.verticalGradient(listOf(c.copy(alpha = 0.95f), c.copy(alpha = 0.55f), c.copy(alpha = 0.25f)))
    }

    fun toneColor(tone: EldoriaTone): Color = when (tone) {
        EldoriaTone.Gold -> Gold
        EldoriaTone.Iron -> IronEdge
        EldoriaTone.Ember -> Ember
        EldoriaTone.Blood -> Blood
        EldoriaTone.Arcane -> Arcane
        EldoriaTone.Vitae -> Vitae
        EldoriaTone.Silver -> Silver
    }

    fun barColors(tone: EldoriaBarTone): Triple<Color, Color, Color> = when (tone) {
        EldoriaBarTone.Health     -> Triple(BloodBright, Blood, BloodDeep)
        EldoriaBarTone.Mana       -> Triple(ManaBright, Mana, ManaDeep)
        EldoriaBarTone.Experience -> Triple(VitaeBright, Vitae, VitaeDeep)
        EldoriaBarTone.Torch      -> Triple(EmberCore, Ember, EmberShadow)
        EldoriaBarTone.Bond       -> Triple(Color(0xFFFFB3C7), Color(0xFFE0567F), Color(0xFF45101F))
        EldoriaBarTone.Satiety    -> Triple(Color(0xFFFFD79A), Color(0xFFD9963F), Color(0xFF3E2A0C))
        EldoriaBarTone.Momentum   -> Triple(ArcaneBright, Arcane, ArcaneDeep)
        EldoriaBarTone.Threat     -> Triple(Color(0xFFFF9E7A), Color(0xFFD1442A), Color(0xFF3A0C05))
    }
}

enum class EldoriaTone { Gold, Iron, Ember, Blood, Arcane, Vitae, Silver }
enum class EldoriaBarTone { Health, Mana, Experience, Torch, Bond, Satiety, Momentum, Threat }
enum class EldoriaButtonSize { Small, Medium, Large }

/** Borde con datos (permite rareza dinámica sin explotar el enum). */
data class EldoriaEdge(
    val top: Color,
    val mid: Color,
    val bottom: Color,
    val glow: Color
) {
    fun brush(): Brush = Brush.verticalGradient(listOf(top, mid, bottom))

    companion object {
        val Gold   = EldoriaEdge(Eldoria.GoldBright, Eldoria.Gold, Eldoria.GoldDeep, Eldoria.GlowGold)
        val Iron   = EldoriaEdge(Eldoria.IronEdge, Eldoria.Iron, Eldoria.IronDeep, Color(0x2255627A))
        val Ember  = EldoriaEdge(Eldoria.EmberCore, Eldoria.Ember, Eldoria.EmberDeep, Eldoria.GlowEmber)
        val Blood  = EldoriaEdge(Eldoria.BloodBright, Eldoria.Blood, Eldoria.BloodDeep, Color(0x33C62828))
        val Arcane = EldoriaEdge(Eldoria.ArcaneBright, Eldoria.Arcane, Eldoria.ArcaneDeep, Eldoria.GlowArcane)
        val Vitae  = EldoriaEdge(Eldoria.VitaeBright, Eldoria.Vitae, Eldoria.VitaeDeep, Color(0x333FBF6F))
        val Silver = EldoriaEdge(Color(0xFFEFF4FA), Eldoria.Silver, Eldoria.SilverDeep, Color(0x22CBD5E1))

        fun rarity(rarity: String): EldoriaEdge {
            val c = Eldoria.rarityColor(rarity)
            return EldoriaEdge(c.copy(alpha = 1f), c.copy(alpha = 0.72f), c.copy(alpha = 0.35f), c.copy(alpha = 0.22f))
        }

        fun tone(tone: EldoriaTone): EldoriaEdge = when (tone) {
            EldoriaTone.Gold -> Gold
            EldoriaTone.Iron -> Iron
            EldoriaTone.Ember -> Ember
            EldoriaTone.Blood -> Blood
            EldoriaTone.Arcane -> Arcane
            EldoriaTone.Vitae -> Vitae
            EldoriaTone.Silver -> Silver
        }
    }
}
