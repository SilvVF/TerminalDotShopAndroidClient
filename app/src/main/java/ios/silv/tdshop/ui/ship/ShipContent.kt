package ios.silv.tdshop.ui.ship

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
        val backStack = LocalBackStack.current
        val state = shipPresenter()

        rememberScaffoldState(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = LocalNavAnimatedContentScope.current
        ).PersistentScaffold(
            topBar = {
                PoppableDestinationTopAppBar(
                    visible = backStack.canPop,
                    onBackPressed = { backStack.pop() },
                    title = { TerminalTitle(text = "Ship") },
                )
            }
        ) { paddingValues ->
           ShipContent(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun ShipContent(modifier: Modifier = Modifier) {

}

@Preview
@Composable
private fun PreviewShipContent() {
    ShipContent()
}