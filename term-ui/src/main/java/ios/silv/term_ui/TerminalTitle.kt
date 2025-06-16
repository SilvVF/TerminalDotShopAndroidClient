package ios.silv.term_ui

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
fun InfiniteTransition.animateBlinkAlpha() = animateFloat(
    initialValue = 1f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
        tween(durationMillis = 500, delayMillis = 100),
        repeatMode = RepeatMode.Reverse
    )
)

@Composable
fun TerminalTitle(modifier: Modifier = Modifier, text: String = "Terminal") {
    val transition = rememberInfiniteTransition()
    val alphaTransition by transition.animateBlinkAlpha()
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()

    val result = remember(textStyle) {
        textMeasurer.measure(text, style = textStyle)
    }

    val boxHeight = result.lastBaseline - result.firstBaseline

    val density = LocalDensity.current
    val heightDp = with(density) { boxHeight.toDp() - 6.dp }
    val baselineDp = with(density) { result.firstBaseline.toDp() }

    Row(modifier) {
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.alignByBaseline()
        )
        Spacer(Modifier.width(6.dp))
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .alignBy { it[FirstBaseline] }
                .graphicsLayer {
                    this.alpha = alphaTransition.coerceIn(0f..1f)
                }
        ) {
            Box(
                Modifier
                    .padding(top = baselineDp)
                    .height(heightDp)
                    .width(8.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewTerminalTitle() {
    MaterialTheme {
        Surface {
            TerminalTitle()
        }
    }
}