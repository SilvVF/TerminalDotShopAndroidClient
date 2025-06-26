package ios.silv.tdshop.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ios.silv.tdshop.ui.theme.TdshopTheme

class PreviewScope(
    val transitionScope: SharedTransitionScope,
    val animatedContentScope: AnimatedVisibilityScope
) : SharedTransitionScope by transitionScope, AnimatedVisibilityScope by animatedContentScope

@Composable
fun ProvidePreviewDefaults(
    content: @Composable PreviewScope.() -> Unit
) {
    TdshopTheme {
        SharedTransitionLayout {
            AnimatedVisibility(true) {
                PreviewScope(this@SharedTransitionLayout, this).content()
            }
        }
    }
}