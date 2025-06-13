package ios.silv.tdshop.ui.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.graphics.toColorInt


const val MutedAlpha = 0.78f

fun String.toColor(): Color {
    return Color((if (this.startsWith("#")) this else "#$this").toColorInt())
}

fun Color.isLight(): Boolean {
    return this.luminance() > 0.5
}