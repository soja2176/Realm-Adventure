package com.example.ui.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Escala tipográfica de Eldoria.
 *
 * Reglas duras:
 *  - Nada por debajo de 11.sp.
 *  - Serif para display / título / heading / lore (voz épica).
 *  - SansSerif para cuerpo, etiquetas y botones (legibilidad).
 *  - Monospace SÓLO para cifras (los numerales no bailan al animarse).
 *
 * Todos los estilos llevan `color = Color.Unspecified`: el llamante fija el color.
 */
object EldoriaType {

    val displayXl: TextStyle = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Black,
        fontSize = 34.sp,
        letterSpacing = 2.4.sp,
        lineHeight = 40.sp,
        color = Color.Unspecified
    )

    val display: TextStyle = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 27.sp,
        letterSpacing = 1.8.sp,
        lineHeight = 33.sp,
        color = Color.Unspecified
    )

    val title: TextStyle = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        letterSpacing = 1.1.sp,
        lineHeight = 27.sp,
        color = Color.Unspecified
    )

    val heading: TextStyle = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        letterSpacing = 0.8.sp,
        lineHeight = 23.sp,
        color = Color.Unspecified
    )

    val subheading: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        letterSpacing = 0.3.sp,
        lineHeight = 20.sp,
        color = Color.Unspecified
    )

    val body: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = Color.Unspecified
    )

    val bodyStrong: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = Color.Unspecified
    )

    val small: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        color = Color.Unspecified
    )

    /** Usar siempre con texto en MAYÚSCULAS. */
    val label: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.4.sp,
        lineHeight = 16.sp,
        color = Color.Unspecified
    )

    val caption: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.4.sp,
        lineHeight = 15.sp,
        color = Color.Unspecified
    )

    val numeric: TextStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 19.sp,
        color = Color.Unspecified
    )

    val numericBig: TextStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        fontSize = 22.sp,
        letterSpacing = 0.8.sp,
        lineHeight = 27.sp,
        color = Color.Unspecified
    )

    val lore: TextStyle = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        color = Color.Unspecified
    )

    val button: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp,
        letterSpacing = 1.0.sp,
        lineHeight = 18.sp,
        color = Color.Unspecified
    )

    val buttonSmall: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 12.sp,
        letterSpacing = 0.8.sp,
        lineHeight = 16.sp,
        color = Color.Unspecified
    )
}
