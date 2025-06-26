package ios.silv.tdshop.nav

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

val LocalBackStack = compositionLocalOf<SnapshotStateStack<Screen>> { mutableStateStackOf<Screen>() }

sealed interface Screen: NavKey, java.io.Serializable

@Serializable
data object Home: Screen {
    private fun readResolve(): Any = Home
}

@Serializable
data object Cart: Screen {
    private fun readResolve(): Any = Cart
}


@Serializable
data object AddShipDest: Screen {
    private fun readResolve(): Any = AddShipDest
}

@Serializable
data object Ship: Screen {
    private fun readResolve(): Any = Ship
}

@Serializable
data object Payment: Screen {
    private fun readResolve(): Any = Payment
}