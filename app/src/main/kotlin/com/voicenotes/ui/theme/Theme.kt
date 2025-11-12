package com.voicenotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = NeonPurple,
    secondary = NeonBlue,
    background = BackgroundDark,
    onPrimary = Color.White
)

@Composable
fun VoiceNotesTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else DarkColors
    MaterialTheme(colorScheme = colors, content = content)
}
