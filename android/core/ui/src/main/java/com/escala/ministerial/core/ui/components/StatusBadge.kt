package com.escala.ministerial.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.escala.ministerial.core.ui.theme.*

enum class BadgeVariant { SUCCESS, WARNING, ERROR, INFO, NEUTRAL, PRIMARY, SECONDARY }

@Composable
fun StatusBadge(
    text: String,
    variant: BadgeVariant = BadgeVariant.NEUTRAL,
    modifier: Modifier = Modifier,
) {
    val dark = isSystemInDarkTheme()
    val (bg, fg) = badgeColors(variant, dark)

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(99.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(Modifier.size(6.dp).background(fg, CircleShape))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
        )
    }
}

fun escalaBadgeVariant(status: String): BadgeVariant = when (status.uppercase()) {
    "APROVADA"   -> BadgeVariant.PRIMARY
    "CONFIRMADA" -> BadgeVariant.SUCCESS
    "CANCELADA"  -> BadgeVariant.ERROR
    "PROPOSTA"   -> BadgeVariant.WARNING
    "ATIVO"      -> BadgeVariant.SUCCESS
    "CONCLUIDO"  -> BadgeVariant.NEUTRAL
    else         -> BadgeVariant.NEUTRAL
}

fun badgeColors(variant: BadgeVariant, dark: Boolean): Pair<Color, Color> = when (variant) {
    BadgeVariant.SUCCESS   -> if (dark) DarkSuccessContainer   to DarkSuccess   else LightSuccessContainer   to LightSuccess
    BadgeVariant.WARNING   -> if (dark) DarkAmberContainer     to DarkAmber     else LightAmberContainer     to LightAmber
    BadgeVariant.ERROR     -> if (dark) DarkErrorContainer     to DarkError     else LightErrorContainer     to LightError
    BadgeVariant.INFO      -> if (dark) DarkInfoContainer      to DarkInfo      else LightInfoContainer      to LightInfo
    BadgeVariant.NEUTRAL   -> if (dark) DarkNeutralContainer   to DarkNeutral   else LightNeutralContainer   to LightNeutral
    BadgeVariant.PRIMARY   -> if (dark) DarkPrimaryContainer   to DarkPrimary   else LightPrimaryContainer   to LightPrimary
    BadgeVariant.SECONDARY -> if (dark) DarkSecondaryContainer to DarkSecondary else LightSecondaryContainer to LightSecondary
}
