package com.voicenotes.ui.screens.waveform

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun WaveformComposable(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition()
    val anim = infinite.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse))
    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        val count = 40
        val spacing = w / count
        val brush = Brush.horizontalGradient(listOf(Color(0xFF8A2BE2), Color(0xFF00B0FF)))
        for (i in 0 until count) {
            val x = i * spacing + spacing/2
            val scale = (kotlin.math.abs(kotlin.math.sin((i / count.toDouble() + anim.value) * Math.PI * 2)) ).toFloat()
            val y = h * 0.2f + h * 0.6f * scale
            drawLine(brush = brush, start = Offset(x, h/2 - y/2), end = Offset(x, h/2 + y/2), strokeWidth = spacing*0.6f)
        }
    }
}
