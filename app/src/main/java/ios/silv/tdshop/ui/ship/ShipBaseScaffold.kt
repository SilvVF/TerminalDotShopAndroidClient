package ios.silv.tdshop.ui.ship

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.ui.components.CartBreadCrumbs
import ios.silv.term_ui.PersistentScaffold
import ios.silv.term_ui.PoppableDestinationTopAppBar
import ios.silv.term_ui.ScaffoldState
import ios.silv.term_ui.TerminalTitle
import ios.silv.term_ui.rememberScaffoldState

@Composable
fun ShipBaseScaffold(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    fab: @Composable ScaffoldState.() -> Unit,
    content: @Composable ScaffoldState.() -> Unit,
) {
    val backStack = LocalBackStack.current

    rememberScaffoldState(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    ).PersistentScaffold(
        topBar = {
            PoppableDestinationTopAppBar(
                visible = backStack.canPop,
                onBackPressed = { backStack.pop() },
                title = { TerminalTitle(text = "Ship") },
            )
        },
        floatingActionButton = {
            fab()
        }
    ) { paddingValues ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            CartBreadCrumbs(modifier = Modifier.fillMaxWidth())
            Box(Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}