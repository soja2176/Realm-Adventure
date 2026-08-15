package com.example.eldoria.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.TextSecondary

// ============================================================
// Eldoria Chronicles — Reusable Medieval-Themed UI Components
// ============================================================

/**
 * 1. MedievalScreenBackground
 * Full-screen background with dark gradient + subtle vignette effect.
 */
@Composable
fun MedievalScreenBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A0B10),
                            Color(0xFF0D0E16),
                            Color(0xFF111320),
                            Color(0xFF0A0B10)
                        )
                    )
                )
        )
        // Vignette overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                        center = Offset(0.5f, 0.5f),
                        radius = 800f
                    )
                )
        )
    }
}

/**
 * 2. OrnateDivider
 * Decorative horizontal divider with gold diamond in center.
 */
@Composable
fun OrnateDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, MedievalGold.copy(alpha = 0.6f))
                    )
                )
        )
        Icon(
            Icons.Default.Diamond,
            null,
            tint = MedievalGold,
            modifier = Modifier.size(12.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(MedievalGold.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
        )
    }
}

/**
 * 3. MedievalStatBar
 * Styled stat bar (HP/MP/XP) with label, animated fill, and value text.
 */
@Composable
fun MedievalStatBar(
    label: String,
    current: Int,
    max: Int,
    barColor: Color,
    darkColor: Color = Color.Black,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    showNumbers: Boolean = true,
    height: androidx.compose.ui.unit.Dp = 10.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (max > 0) (current.toFloat() / max).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = label
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, label, tint = barColor, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
        } else {
            Text(
                label,
                color = barColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(20.dp)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(height)
                .clip(RoundedCornerShape(5.dp))
                .background(darkColor)
                .border(0.5.dp, barColor.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(
                        Brush.verticalGradient(
                            listOf(barColor, barColor.copy(alpha = 0.7f))
                        )
                    )
            )
            // Shine effect
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(3.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )
        }
        if (showNumbers) {
            Spacer(Modifier.width(6.dp))
            Text(
                "${formatGameNumber(current)}/${formatGameNumber(max)}",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 4. GoldCounter
 * Gold display with coin icon.
 */
@Composable
fun GoldCounter(gold: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(1.dp, MedievalGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            Icons.Default.MonetizationOn,
            "Oro",
            tint = MedievalGold,
            modifier = Modifier.size(16.dp)
        )
        Text(
            formatGameNumber(gold),
            color = MedievalGold,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

/**
 * 5. SectionHeader
 * Section title with decorative underline.
 */
@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, tint = MedievalGold, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = title,
                color = MedievalGold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                style = TextStyle(letterSpacing = 0.5.sp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(MedievalGold, Color.Transparent)
                    )
                )
        )
    }
}

/**
 * 6. RarityBorder
 * Modifier extension that applies a colored border based on item rarity.
 */
fun Modifier.rarityBorder(rarity: String): Modifier {
    val color = when (rarity.uppercase()) {
        "UNIVERSAL" -> Color(0xFF00E5FF)
        "ARCANO" -> Color(0xFFD500F9)
        "LEGENDARIO", "LEGENDARY" -> Color(0xFFFF8F00)
        "ÉPICO", "EPIC" -> Color(0xFF8E24AA)
        "RARO", "RARE" -> Color(0xFF1E88E5)
        else -> Color(0xFF90A4AE)
    }
    return this
        .border(2.dp, color, RoundedCornerShape(8.dp))
        .background(
            Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.1f), Color.Transparent),
                radius = 100f
            )
        )
}

/**
 * 7. MedievalButton
 * Styled button with gold border and gradient.
 */
@Composable
fun MedievalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(44.dp)
            .border(
                width = 1.5.dp,
                brush = if (enabled)
                    Brush.verticalGradient(listOf(Color(0xFFFFE082), Color(0xFFC79100)))
                else
                    Brush.verticalGradient(listOf(Color.Gray, Color.DarkGray)),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Color(0xFF2A1F00) else Color(0xFF1A1A1A),
            disabledContainerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (icon != null) {
            Icon(
                icon,
                null,
                tint = if (enabled) MedievalGold else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text,
            color = if (enabled) MedievalGold else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

/**
 * 8. ItemCard
 * Card for displaying an inventory item with rarity glow.
 */
@Composable
fun ItemCard(
    itemName: String,
    itemType: String,
    rarity: String,
    level: Int,
    stats: String,
    imageRes: Int,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rarityColor = when (rarity.uppercase()) {
        "UNIVERSAL" -> Color(0xFF00E5FF)
        "ARCANO" -> Color(0xFFD500F9)
        "LEGENDARIO", "LEGENDARY" -> Color(0xFFFF8F00)
        "ÉPICO", "EPIC" -> Color(0xFF8E24AA)
        "RARO", "RARE" -> Color(0xFF1E88E5)
        else -> Color(0xFF90A4AE)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .rarityBorder(rarity)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161A26)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = itemName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    itemName,
                    color = rarityColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "$itemType • Nv.$level",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
                Text(
                    stats,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 9. AchievementBadge
 * Small badge for showing achievement progress.
 */
@Composable
fun AchievementBadge(
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
            .background(
                if (isUnlocked) Color(0xFF2A1F00).copy(alpha = 0.8f)
                else Color(0xFF161A26).copy(alpha = 0.8f)
            )
            .border(
                1.dp,
                if (isUnlocked) MedievalGold else Color.Gray.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            title,
            color = if (isUnlocked) MedievalGold else TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        if (!isUnlocked) {
            Spacer(Modifier.height(2.dp))
            @Suppress("DEPRECATION")
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MedievalGold,
                trackColor = Color.Black
            )
        }
    }
}
