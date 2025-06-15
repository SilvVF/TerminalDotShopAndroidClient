package ios.silv.tdshop.ui.ship

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.nav.Ship
import ios.silv.term_ui.PersistentScaffold
import ios.silv.term_ui.PoppableDestinationTopAppBar
import ios.silv.term_ui.TerminalTitle
import ios.silv.term_ui.rememberScaffoldState


fun EntryProviderBuilder<Screen>.shipScreenEntry(
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<Ship> {
        val state = shipPresenter()

        ShipContent(
            sharedTransitionScope,
            LocalNavAnimatedContentScope.current,
            state
        )
    }
}

@Composable
private fun ShipContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    state: ShipState,
) {
    val backStack = LocalBackStack.current

    rememberScaffoldState(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedContentScope
    ).PersistentScaffold(
        topBar = {
            PoppableDestinationTopAppBar(
                visible = backStack.canPop,
                onBackPressed = { backStack.pop() },
                title = { TerminalTitle(text = "Ship") },
            )
        }
    ) { paddingValues ->

    }
}

@Preview
@Composable
private fun PreviewShipContent() {
    SharedTransitionLayout {
        AnimatedContent(true) { _ ->
            ShipContent(
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedContentScope = this@AnimatedContent,
                state = ShipState("")
            )
        }
    }
}