package com.crm.chatflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = NeonGreen,
    background = DarkDeep,
    surface = BentoCard,
    onPrimary = TextWhite,
    onSecondary = DarkDeep,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun ChatFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
