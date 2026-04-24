package com.escala.ministerial.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class BadgeVariant { SUCCESS, WARNING, ERROR, INFO, NEUTRAL }

@Composable
fun StatusBadge(
    text: String,
    variant: BadgeVariant = BadgeVariant.NEUTRAL,
    modifier: Modifier = Modifier,
) {
    val (bgColor, textColor) = when (variant) {
        BadgeVariant.SUCCESS -> Color(0xFF4CAF50) to Color.White
        BadgeVariant.WARNING -> Color(0xFFF59E0B) to Color.White
        BadgeVariant.ERROR -> Color(0xFFB00020) to Color.White
        BadgeVariant.INFO -> Color(0xFF1565C0) to Color.White
        BadgeVariant.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}
