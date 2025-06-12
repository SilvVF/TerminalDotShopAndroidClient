package ios.silv.tdshop.nav

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

val LocalBackStack = compositionLocalOf<SnapshotStateStack<Screen>> { error("") }

sealed interface Screen: NavKey, java.io.Serializable

@Serializable
data object Home: Screen {
    private fun readResolve(): Any = Home
}