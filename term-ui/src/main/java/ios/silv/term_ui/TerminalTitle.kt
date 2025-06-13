package ios.silv.term_ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun TerminalTitle(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val alphaTransition by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(),
            repeatMode = RepeatMode.Reverse
        )
    )
    Row(modifier.height(IntrinsicSize.Max), verticalAlignment = Alignment.CenterVertically) {
        Text("Terminal", Modifier.fillMaxHeight())

        Box(
            Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
                .graphicsLayer {
                    alpha = alphaTransition.coerceIn(0f..1f)
                }
        )
    }
}