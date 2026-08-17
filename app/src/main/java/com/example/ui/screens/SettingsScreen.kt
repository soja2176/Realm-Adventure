package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameScreen
import com.example.data.GameViewModel
import com.example.data.model.GameSettings
import com.example.ui.design.Eldoria
import com.example.ui.design.EldoriaBanner
import com.example.ui.design.EldoriaButton
import com.example.ui.design.EldoriaButtonSize
import com.example.ui.design.EldoriaConfirmDialog
import com.example.ui.design.EldoriaDivider
import com.example.ui.design.EldoriaEdge
import com.example.ui.design.EldoriaIconButton
import com.example.ui.design.EldoriaMotion
import com.example.ui.design.EldoriaPanel
import com.example.ui.design.EldoriaScreen
import com.example.ui.design.EldoriaSectionTitle
import com.example.ui.design.EldoriaSegmentedTabs
import com.example.ui.design.EldoriaTone
import com.example.ui.design.EldoriaType
import com.example.ui.design.eldoriaGlowLayer
import com.example.ui.design.eldoriaPressable

// ══════════════════════════════════════════════════════════════════════════════
//  AJUSTES — cuatro secciones, controles dibujados a mano, nada de Material puro.
// ══════════════════════════════════════════════════════════════════════════════

private val MENU_TEXT_SCALE_STEPS = listOf(85, 100, 115, 130)

@Composable
fun SettingsScreen(viewModel: GameViewModel) {
    val settings by viewModel.systems.settings.collectAsState()
    val progress by viewModel.progressState.collectAsState()
    val backupStatus by viewModel.backupStatus.collectAsState()

    val hasHero = progress?.hasActiveChar == true

    var confirmRestore by remember { mutableStateOf(false) }
    var deleteStage by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { viewModel.refreshBackupStatus() }

    val applySettings: (GameSettings) -> Unit = { next -> viewModel.systems.updateSettings(next) }

    EldoriaScreen(
        depth = 0,
        embers = settings.embersEnabled,
        fog = true,
        vignetteStrength = 0.62f,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            // El `innerPadding` del Scaffold ya trae el inset inferior en las
            // pantallas sin cromo: repetirlo dejaba un hueco muerto del doble.
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Eldoria.S12))

            Box(modifier = Modifier.fillMaxWidth()) {
                EldoriaBanner(
                    title = "AJUSTES",
                    subtitle = "Sonido, presentación, combate y datos de la partida",
                    artRes = R.drawable.img_talents_bg_1784603912942,
                    height = 120.dp,
                    edge = EldoriaEdge.Gold,
                    crestSeed = 909
                )
                EldoriaIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver al menú principal",
                    onClick = { viewModel.changeScreen(GameScreen.MAIN_MENU) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    tone = EldoriaTone.Iron,
                    size = 38.dp,
                    testTag = "settings_back_btn"
                )
            }

            // ───────────────────────── SONIDO ─────────────────────────
            Spacer(Modifier.height(Eldoria.S20))
            EldoriaSectionTitle(
                text = "SONIDO",
                icon = Icons.Default.MusicNote,
                accent = Eldoria.Gold
            )
            Spacer(Modifier.height(Eldoria.S8))
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = EldoriaEdge.Iron,
                padding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                MenuToggleRow(
                    title = "Música",
                    description = "Temas ambientales de reinos, calabozos y combate.",
                    checked = settings.musicEnabled,
                    accent = Eldoria.Gold,
                    testTag = "settings_musicEnabled_toggle",
                    onToggle = { applySettings(settings.copy(musicEnabled = !settings.musicEnabled)) }
                )
                MenuRowSeparator()
                MenuToggleRow(
                    title = "Efectos de sonido",
                    description = "Golpes, monedas, hechizos y clics de interfaz.",
                    checked = settings.sfxEnabled,
                    accent = Eldoria.Gold,
                    testTag = "settings_sfxEnabled_toggle",
                    onToggle = { applySettings(settings.copy(sfxEnabled = !settings.sfxEnabled)) }
                )
                MenuRowSeparator()
                MenuToggleRow(
                    title = "Vibración",
                    description = "Respuesta háptica en críticos, subidas de nivel y botín.",
                    checked = settings.hapticsEnabled,
                    accent = Eldoria.Gold,
                    testTag = "settings_hapticsEnabled_toggle",
                    onToggle = { applySettings(settings.copy(hapticsEnabled = !settings.hapticsEnabled)) }
                )
            }

            // ───────────────────────── PRESENTACIÓN ─────────────────────────
            Spacer(Modifier.height(Eldoria.S20))
            EldoriaSectionTitle(
                text = "PRESENTACIÓN",
                icon = Icons.Default.Visibility,
                accent = Eldoria.Arcane
            )
            Spacer(Modifier.height(Eldoria.S8))
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = EldoriaEdge.Arcane,
                padding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                MenuToggleRow(
                    title = "Brasas y niebla",
                    description = "Partículas de fondo en todas las pantallas. Desactívalas si el dispositivo va justo.",
                    checked = settings.embersEnabled,
                    accent = Eldoria.Ember,
                    testTag = "settings_embersEnabled_toggle",
                    onToggle = { applySettings(settings.copy(embersEnabled = !settings.embersEnabled)) }
                )
                MenuRowSeparator()
                MenuToggleRow(
                    title = "Números de daño",
                    description = "Cifras flotantes sobre héroe y enemigo en cada impacto.",
                    checked = settings.damageNumbers,
                    accent = Eldoria.BloodBright,
                    testTag = "settings_damageNumbers_toggle",
                    onToggle = { applySettings(settings.copy(damageNumbers = !settings.damageNumbers)) }
                )
                MenuRowSeparator()
                MenuToggleRow(
                    title = "Sacudida de pantalla",
                    description = "Golpe de cámara en críticos y ataques de jefe.",
                    checked = settings.screenShake,
                    accent = Eldoria.Ember,
                    testTag = "settings_screenShake_toggle",
                    onToggle = { applySettings(settings.copy(screenShake = !settings.screenShake)) }
                )
                MenuRowSeparator()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "Tamaño del texto",
                        style = EldoriaType.subheading,
                        color = Eldoria.TextHi,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Escala global de la interfaz. Afecta a menús, combate y crónicas.",
                        style = EldoriaType.small,
                        color = Eldoria.TextMid
                    )
                    Spacer(Modifier.height(Eldoria.S8))
                    EldoriaSegmentedTabs(
                        options = MENU_TEXT_SCALE_STEPS.map { "$it %" },
                        selectedIndex = MENU_TEXT_SCALE_STEPS
                            .indexOf(settings.textScale)
                            .let { if (it < 0) 1 else it },
                        onSelect = { index ->
                            applySettings(settings.copy(textScale = MENU_TEXT_SCALE_STEPS[index]))
                        },
                        modifier = Modifier.testTag("settings_textScale_toggle"),
                        accent = Eldoria.Arcane,
                        testTagPrefix = "settings_textScale_opt_"
                    )
                    Spacer(Modifier.height(Eldoria.S8))
                    Text(
                        text = "«Las brasas alumbran el descenso.»",
                        style = EldoriaType.lore.copy(
                            fontSize = (14f * settings.textScale / 100f).coerceAtLeast(11f).sp
                        ),
                        color = Eldoria.TextGold.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // ───────────────────────── COMBATE ─────────────────────────
            Spacer(Modifier.height(Eldoria.S20))
            EldoriaSectionTitle(
                text = "COMBATE",
                icon = Icons.Default.SportsMartialArts,
                accent = Eldoria.Blood
            )
            Spacer(Modifier.height(Eldoria.S8))
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = EldoriaEdge.Blood,
                padding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                MenuToggleRow(
                    title = "Combate automático por defecto",
                    description = "Cada batalla arranca con el piloto automático ya activo.",
                    checked = settings.autoCombatDefault,
                    accent = Eldoria.Vitae,
                    testTag = "settings_autoCombatDefault_toggle",
                    onToggle = { applySettings(settings.copy(autoCombatDefault = !settings.autoCombatDefault)) }
                )
                MenuRowSeparator()
                MenuToggleRow(
                    title = "Asistencia de reacción",
                    description = "Amplía la ventana para contraatacar de 1100 ms a 1600 ms. Recomendado si el combate te resulta demasiado exigente.",
                    checked = settings.reactionAssist,
                    accent = Eldoria.Info,
                    testTag = "settings_reactionAssist_toggle",
                    onToggle = { applySettings(settings.copy(reactionAssist = !settings.reactionAssist)) }
                )
            }

            // ───────────────────────── DATOS ─────────────────────────
            Spacer(Modifier.height(Eldoria.S20))
            EldoriaSectionTitle(
                text = "DATOS",
                icon = Icons.Default.Storage,
                accent = Eldoria.Silver
            )
            Spacer(Modifier.height(Eldoria.S8))
            EldoriaPanel(
                modifier = Modifier.fillMaxWidth(),
                edge = EldoriaEdge.Silver,
                padding = PaddingValues(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Copia de seguridad",
                        style = EldoriaType.subheading,
                        color = Eldoria.TextHi,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    EldoriaIconButton(
                        icon = Icons.Default.Backup,
                        contentDescription = "Copia de seguridad",
                        onClick = { viewModel.refreshBackupStatus() },
                        tone = EldoriaTone.Iron,
                        size = 34.dp
                    )
                }
                Spacer(Modifier.height(Eldoria.S4))
                Text(
                    text = if (backupStatus.isBlank()) {
                        "Todavía no hay ninguna copia registrada en este dispositivo."
                    } else {
                        backupStatus
                    },
                    style = EldoriaType.small,
                    color = Eldoria.TextMid
                )
                Spacer(Modifier.height(Eldoria.S12))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Eldoria.S8)
                ) {
                    EldoriaButton(
                        text = "EXPORTAR",
                        onClick = { viewModel.exportManualBackup() },
                        modifier = Modifier.weight(1f),
                        enabled = hasHero,
                        tone = EldoriaTone.Vitae,
                        size = EldoriaButtonSize.Medium,
                        icon = Icons.Default.Save,
                        testTag = "settings_backup_export_btn"
                    )
                    EldoriaButton(
                        text = "RESTAURAR",
                        onClick = { confirmRestore = true },
                        modifier = Modifier.weight(1f),
                        tone = EldoriaTone.Ember,
                        size = EldoriaButtonSize.Medium,
                        icon = Icons.Default.Restore,
                        testTag = "settings_backup_restore_btn"
                    )
                }
                if (!hasHero) {
                    Spacer(Modifier.height(Eldoria.S6))
                    Text(
                        text = "Exportar requiere una partida activa.",
                        style = EldoriaType.caption,
                        color = Eldoria.Warning
                    )
                }

                Spacer(Modifier.height(Eldoria.S16))
                EldoriaDivider(color = Eldoria.Blood.copy(alpha = 0.65f))
                Spacer(Modifier.height(Eldoria.S12))

                Text(
                    text = "Zona de peligro",
                    style = EldoriaType.subheading,
                    color = Eldoria.BloodBright,
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Borrar el personaje activo elimina su nivel, su inventario, sus mascotas y toda su crónica. Requiere doble confirmación.",
                    style = EldoriaType.small,
                    color = Eldoria.TextMid
                )
                Spacer(Modifier.height(Eldoria.S12))
                EldoriaButton(
                    text = "BORRAR PERSONAJE",
                    onClick = { deleteStage = 1 },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = hasHero,
                    tone = EldoriaTone.Blood,
                    size = EldoriaButtonSize.Medium,
                    icon = Icons.Default.Delete,
                    fullWidth = true,
                    testTag = "settings_delete_character_btn"
                )
                if (!hasHero) {
                    Spacer(Modifier.height(Eldoria.S6))
                    Text(
                        text = "No hay ningún héroe activo que borrar.",
                        style = EldoriaType.caption,
                        color = Eldoria.TextLow
                    )
                }
            }

            // ─────────────── CONSOLA DEL ARCANISTA (desarrollo) ───────────────
            DevConsoleSection(viewModel = viewModel, unlocked = settings.devUnlocked)

            Spacer(Modifier.height(Eldoria.S24))
            EldoriaButton(
                text = "VOLVER AL MENÚ PRINCIPAL",
                onClick = { viewModel.changeScreen(GameScreen.MAIN_MENU) },
                modifier = Modifier.fillMaxWidth(),
                tone = EldoriaTone.Gold,
                size = EldoriaButtonSize.Large,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                fullWidth = true,
                testTag = "settings_back_menu_btn"
            )
            Spacer(Modifier.height(Eldoria.S32))
        }
    }

    if (confirmRestore) {
        EldoriaConfirmDialog(
            title = "Restaurar copia",
            message = "Se sobrescribirá la partida activa con la última copia de seguridad guardada. Esta acción no se puede deshacer.",
            confirmLabel = "RESTAURAR",
            onConfirm = {
                confirmRestore = false
                viewModel.restoreManualBackup()
            },
            onDismiss = { confirmRestore = false },
            tone = EldoriaTone.Ember
        )
    }

    if (deleteStage == 1) {
        EldoriaConfirmDialog(
            title = "Borrar personaje",
            message = "Vas a borrar al héroe activo y todo su progreso. Te pediremos una confirmación más.",
            confirmLabel = "CONTINUAR",
            onConfirm = { deleteStage = 2 },
            onDismiss = { deleteStage = 0 },
            tone = EldoriaTone.Blood
        )
    }

    if (deleteStage == 2) {
        EldoriaConfirmDialog(
            title = "¿Seguro del todo?",
            message = "Última advertencia: nivel, oro, equipo, mascotas y crónica se pierden para siempre. No hay copia automática de esto.",
            confirmLabel = "BORRAR PARA SIEMPRE",
            onConfirm = {
                deleteStage = 0
                viewModel.deleteCharacterAndReset()
            },
            onDismiss = { deleteStage = 0 },
            tone = EldoriaTone.Blood
        )
    }
}

// ──────────────────────────── PIEZAS PRIVADAS ────────────────────────────

@Composable
private fun MenuRowSeparator() {
    EldoriaDivider(
        modifier = Modifier.padding(vertical = 2.dp),
        color = Eldoria.IronEdge.copy(alpha = 0.55f),
        ornament = false
    )
}

@Composable
private fun MenuToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    accent: Color,
    testTag: String,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = EldoriaType.subheading,
                color = if (checked) Eldoria.TextHi else Eldoria.TextMid,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = description,
                style = EldoriaType.small,
                color = Eldoria.TextMid
            )
        }
        Spacer(Modifier.width(Eldoria.S12))
        MenuRuneSwitch(
            checked = checked,
            accent = accent,
            testTag = testTag,
            onToggle = onToggle
        )
    }
}

/** Interruptor propio: canal de piedra hundido, gema móvil y halo cuando está activo. */
@Composable
private fun MenuRuneSwitch(
    checked: Boolean,
    accent: Color,
    testTag: String,
    onToggle: () -> Unit
) {
    val t by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 240, easing = EldoriaMotion.easeOut),
        label = "menuRuneSwitch"
    )

    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 30.dp)
            .then(
                if (checked) Modifier.eldoriaGlowLayer(
                    color = accent.copy(alpha = 0.55f),
                    alpha = 0.22f,
                    corner = 15.dp,
                    spread = 5.dp
                ) else Modifier
            )
            .eldoriaPressable(onClick = onToggle)
            .testTag(testTag)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 2f || h <= 2f) return@Canvas
            val r = h / 2f

            // canal hundido
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Eldoria.Abyss, Eldoria.PanelSunken)),
                size = Size(w, h),
                cornerRadius = CornerRadius(r, r)
            )
            // relleno activo
            if (t > 0.01f) {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            accent.copy(alpha = 0.95f),
                            accent.copy(alpha = 0.60f),
                            accent.copy(alpha = 0.28f)
                        )
                    ),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(r, r),
                    alpha = t
                )
                // brillo especular superior
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.26f), Color.Transparent),
                        startY = 0f,
                        endY = h * 0.5f
                    ),
                    size = Size(w, h * 0.5f),
                    cornerRadius = CornerRadius(r, r),
                    alpha = t
                )
            }
            // marco de hierro
            drawRoundRect(
                color = Eldoria.IronEdge.copy(alpha = 0.9f),
                size = Size(w, h),
                cornerRadius = CornerRadius(r, r),
                style = Stroke(width = 1.4f)
            )

            // gema móvil
            val pad = h * 0.16f
            val kr = (h / 2f) - pad
            if (kr <= 0.5f) return@Canvas
            val cx = (pad + kr) + (w - 2f * (pad + kr)) * t
            val cy = h / 2f

            drawCircle(
                color = Color.Black.copy(alpha = 0.55f),
                radius = kr * 1.14f,
                center = Offset(cx, cy + 1.2f)
            )
            if (t > 0.01f) {
                drawCircle(
                    color = accent.copy(alpha = 0.35f * t),
                    radius = kr * 1.75f,
                    center = Offset(cx, cy)
                )
            }
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Eldoria.Silver, Eldoria.SilverDeep),
                    startY = cy - kr,
                    endY = cy + kr
                ),
                radius = kr,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = if (t > 0.5f) accent else Eldoria.IronDeep,
                radius = kr * 0.34f,
                center = Offset(cx, cy)
            )
        }
    }
}
