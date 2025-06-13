package ios.silv.term_ui

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf


val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> { error("not provided") }