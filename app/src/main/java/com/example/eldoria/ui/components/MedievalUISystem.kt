package com.example.eldoria.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.formatGameNumber
import com.example.ui.MedievalGold
import com.example.ui.theme.*

// ============================================================
// ELDORIA CHRONICLES — Medieval UI System
// Consolidated, enhanced reusable components with text overflow
// protection and consistent medieval fantasy styling.
// ============================================================

// ─── RARITY COLORS ──────────────────────────────────────────

fun getRarityColor(rarity: String): Color = when (rarity.uppercase()) {
    "UNIVERSAL" -> UniversalCyan
    "ARCANO" -> ArcanoMagenta
    "LEGENDARIO", "LEGENDARY" -> LegendaryOrange
    "ÉPICO", "EPIC" -> EpicPurple
    "RARO", "RARE" -> RareBlue
    else -> CommonGray
}

// ─── MEDIEVAL TEXT (with overflow protection) ───────────────

@Composable
fun MedievalText(
    text: String,
    color: Color = TextPrimary,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    style: TextStyle = TextStyle()
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign,
        style = style,
        modifier = modifier
    )
}

// ─── MEDIEVAL TITLE ─────────────────────────────────────────

@Composable
fun MedievalTitle(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 22.sp,
    icon: ImageVector? = Icons.Default.Star,
    color: Color = MedievalGold,
    subtitle: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(letterSpacing = 1.sp)
            )
        }
        if (subtitle != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier.fillMaxWidth(0.5f).height(2.dp).background(
                Brush.horizontalGradient(listOf(color, color.copy(alpha = 0f)))
            )
        )
    }
}

// ─── MEDIEVAL SECTION HEADER ────────────────────────────────

@Composable
fun MedievalSectionHeader(
    title: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    color: Color = MedievalGold
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = title,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(letterSpacing = 0.5.sp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier.fillMaxWidth(0.4f).height(2.dp).background(
                Brush.horizontalGradient(listOf(color, Color.Transparent))
            )
        )
    }
}

// ─── ORNATE DIVIDER ─────────────────────────────────────────

@Composable
fun MedievalDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, MedievalGold.copy(alpha = 0.5f)))))
        Box(
            Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(MedievalGold).rotate(45f)
        )
        Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(MedievalGold.copy(alpha = 0.5f), Color.Transparent))))
    }
}

// ─── MEDIEVAL CARD ──────────────────────────────────────────

@Composable
fun MedievalCard(
    modifier: Modifier = Modifier,
    borderColor: Color = MedievalGold.copy(alpha = 0.4f),
    backgroundColor: Color = CardSurface,
    contentPadding: androidx.compose.ui.unit.Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

// ─── MEDIEVAL RESOURCE BAR (compact HP/MP/XP row) ──────────

@Composable
fun MedievalResourceBar(
    label: String,
    current: Int,
    max: Int,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (max > 0) (current.toFloat() / max).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(500, easing = EaseOutCubic),
        label = label
    )

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        MedievalText(label, color = barColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp))
        Spacer(Modifier.width(4.dp))
        Box(
            Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(Color.Black.copy(alpha = 0.8f))
                .border(0.5.dp, barColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        ) {
            Box(
                Modifier.fillMaxHeight().fillMaxWidth(animatedProgress).background(
                    Brush.horizontalGradient(listOf(barColor, barColor.copy(alpha = 0.7f)))
                )
            )
            Box(
                Modifier.fillMaxWidth(animatedProgress).height(2.dp).background(Color.White.copy(alpha = 0.2f))
            )
        }
        Spacer(Modifier.width(4.dp))
        MedievalText("${formatGameNumber(current)}/${formatGameNumber(max)}", color = Color.White.copy(alpha = 0.8f), fontSize = 8.sp, maxLines = 1)
    }
}

// ─── GOLD COUNTER ───────────────────────────────────────────

@Composable
fun MedievalGoldCounter(gold: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(1.dp, MedievalGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(Icons.Default.MonetizationOn, "Oro", tint = MedievalGold, modifier = Modifier.size(16.dp))
        MedievalText(formatGameNumber(gold), color = MedievalGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

// ─── MEDIEVAL ITEM ROW ──────────────────────────────────────

@Composable
fun MedievalItemRow(
    itemName: String,
    itemType: String,
    rarity: String,
    level: Int,
    stats: String,
    imageRes: Int,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rarityColor = getRarityColor(rarity)

    MedievalCard(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        borderColor = rarityColor.copy(alpha = 0.5f),
        backgroundColor = Color(0xFF161A26),
        contentPadding = 10.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(6.dp)).border(1.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(id = imageRes), contentDescription = itemName, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                MedievalText(itemName, color = rarityColor, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                MedievalText("$itemType • Nv.$level", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, maxLines = 1)
                MedievalText(stats, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}

// ─── MEDIEVAL TOOLTIP ───────────────────────────────────────

@Composable
fun MedievalTooltip(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MedievalGold
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        MedievalText(text, color = Color.White, fontSize = 11.sp)
    }
}

// ─── MEDIEVAL NOTIFICATION BANNER ───────────────────────────

@Composable
fun MedievalNotificationBanner(
    message: String,
    icon: ImageVector = Icons.Default.Star,
    backgroundColor: Color = MedievalGold,
    textColor: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        contentColor = textColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, tint = textColor, modifier = Modifier.size(18.dp))
            MedievalText(message, color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 2)
        }
    }
}

// ─── MEDIEVAL CONTENT PADDING WRAPPER ───────────────────────

@Composable
fun MedievalContentColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = verticalArrangement,
        content = content
    )
}

// ─── MEDIEVAL STAT ROW (for character stats display) ────────

@Composable
fun MedievalStatRow(
    label: String,
    value: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    valueColor: Color = MedievalGold
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, label, tint = MedievalGold, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        MedievalText(label, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, modifier = Modifier.weight(1f))
        MedievalText("+$value", color = valueColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

// ─── MEDIEVAL RARITY BADGE ──────────────────────────────────

@Composable
fun MedievalRarityBadge(
    rarity: String,
    modifier: Modifier = Modifier
) {
    val color = getRarityColor(rarity)
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        MedievalText(rarity.uppercase(), color = color, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
    }
}

// ─── MEDIEVAL ACHIEVEMENT BADGE ─────────────────────────────

@Composable
fun MedievalAchievementBadge(
    icon: String,
    title: String,
    progress: Float,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isUnlocked) Color(0xFF2A1F00).copy(alpha = 0.8f) else Color(0xFF161A26).copy(alpha = 0.8f))
            .border(1.dp, if (isUnlocked) MedievalGold else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MedievalText(icon, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        MedievalText(title, color = if (isUnlocked) MedievalGold else TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 2)
        if (!isUnlocked) {
            Spacer(Modifier.height(2.dp))
            @Suppress("DEPRECATION")
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                color = MedievalGold,
                trackColor = Color.Black
            )
        }
    }
}