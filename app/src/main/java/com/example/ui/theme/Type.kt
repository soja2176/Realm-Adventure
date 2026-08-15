package com.example.ui.theme

import androidx.compose.material3.Typography
import com.example.ui.design.EldoriaType

// === ELDORIA CHRONICLES - TIPOGRAFÍA MEDIEVAL ===
//
// La escala Material3 se construye a partir de `com.example.ui.design.EldoriaType`,
// de forma que el texto Material heredado y el texto de los componentes Eldoria
// nuevos compartan familia, peso e interlineado.
//
// Regla dura heredada de EldoriaType: ningún estilo baja de 11.sp
// (el menor de la escala es `caption`, exactamente 11.sp).
//
// Nombre público `Typography` intacto: lo consume MyApplicationTheme.

val Typography = Typography(
    // ─── Display: serif de portada y cinemáticas ───
    displayLarge = EldoriaType.displayXl,     // 34.sp Serif Black
    displayMedium = EldoriaType.display,      // 27.sp Serif Bold
    displaySmall = EldoriaType.title,         // 21.sp Serif Bold

    // ─── Headline: cabeceras de pantalla y de sección ───
    headlineLarge = EldoriaType.title,        // 21.sp Serif Bold
    headlineMedium = EldoriaType.heading,     // 17.sp Serif SemiBold
    headlineSmall = EldoriaType.subheading,   // 15.sp SansSerif Bold

    // ─── Title: cabeceras de tarjeta y de fila ───
    titleLarge = EldoriaType.subheading,      // 15.sp SansSerif Bold
    titleMedium = EldoriaType.bodyStrong,     // 14.sp SansSerif SemiBold
    titleSmall = EldoriaType.label,           // 12.sp SansSerif Bold, MAYÚSCULAS

    // ─── Body: prosa y descripciones ───
    bodyLarge = EldoriaType.body,             // 14.sp SansSerif Normal
    bodyMedium = EldoriaType.small,           // 12.sp SansSerif Normal
    bodySmall = EldoriaType.caption,          // 11.sp SansSerif Medium

    // ─── Label: botones, chips y pies de dato ───
    labelLarge = EldoriaType.label,           // 12.sp SansSerif Bold
    labelMedium = EldoriaType.caption,        // 11.sp SansSerif Medium
    labelSmall = EldoriaType.caption          // 11.sp — suelo duro de la escala
)
