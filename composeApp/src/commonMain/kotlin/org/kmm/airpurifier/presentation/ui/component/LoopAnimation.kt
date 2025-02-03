package org.kmm.airpurifier.presentation.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.kmm.airpurifier.util.SecondaryColor

@Composable
fun LoopAnimation() {
    // Create an infinite transition
    val infiniteTransition = rememberInfiniteTransition()

    // Animate the alpha value between 0f and 1f
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000), // 1000ms for fade-in and fade-out
            repeatMode = RepeatMode.Restart
        )
    )

    // Animate the size value between 0.dp and 200.dp
    val size by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000), // 1000ms for size animation
            repeatMode = RepeatMode.Restart
        )
    )

    // Box that continuously fades in/out and changes size
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(SecondaryColor.copy(alpha = alpha))
        )
    }
}