package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ui.design.Eldoria

// === ELDORIA CHRONICLES - TEMA MEDIEVAL OSCURO ===
//
// El esquema Material3 se deriva de los tokens de `com.example.ui.design.Eldoria`
// para que los componentes Material heredados (Scaffold, Card, Button, Dialog…)
// se apoyen exactamente en la misma paleta que los componentes Eldoria nuevos.
// Nombre público `MyApplicationTheme` intacto: lo consume MainActivity.

private val EldoriaColorScheme = darkColorScheme(
    // Metal noble: la voz de la acción principal.
    primary = Eldoria.Gold,
    onPrimary = Eldoria.TextOnGold,
    primaryContainer = Eldoria.GoldDeep,
    onPrimaryContainer = Eldoria.GoldBright,

    // Maná: acción secundaria / informativa.
    secondary = Eldoria.Mana,
    onSecondary = Eldoria.TextHi,
    secondaryContainer = Eldoria.ManaDeep,
    onSecondaryContainer = Eldoria.ManaBright,

    // Arcano: acento terciario.
    tertiary = Eldoria.Arcane,
    onTertiary = Eldoria.TextHi,
    tertiaryContainer = Eldoria.ArcaneDeep,
    onTertiaryContainer = Eldoria.ArcaneBright,

    // Peligro.
    error = Eldoria.Danger,
    onError = Eldoria.TextHi,
    errorContainer = Eldoria.BloodDeep,
    onErrorContainer = Eldoria.BloodBright,

    // Superficies: del abismo al panel iluminado.
    background = Eldoria.Abyss,
    onBackground = Eldoria.TextHi,
    surface = Eldoria.Panel,
    onSurface = Eldoria.TextHi,
    surfaceVariant = Eldoria.PanelHi,
    onSurfaceVariant = Eldoria.TextMid,
    surfaceTint = Eldoria.Gold,

    // Bordes.
    outline = Eldoria.GoldDeep,
    outlineVariant = Eldoria.Iron,

    // Inversos y velo.
    inverseSurface = Eldoria.Parchment,
    inverseOnSurface = Eldoria.ParchmentInk,
    inversePrimary = Eldoria.GoldShadow,
    scrim = Color.Black
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EldoriaColorScheme,
        typography = Typography,
        content = content
    )
}
