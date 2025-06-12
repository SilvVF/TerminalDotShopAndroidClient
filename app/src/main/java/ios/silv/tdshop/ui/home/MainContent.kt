package ios.silv.tdshop.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import ios.silv.tdshop.nav.Home
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.ui.compose.EventFlow
import ios.silv.tdshop.ui.compose.rememberEventFlow
import ios.silv.term_ui.DraggableNavLayout
import ios.silv.term_ui.TerminalSection
import ios.silv.term_ui.TerminalSectionDefaults

fun EntryProviderBuilder<Screen>.mainScreenEntry() {
    entry<Home> {
        val events = rememberEventFlow<MainEvent>()
        val state = mainPresenter(events)

        MainContent(state, events)
    }
}

@Composable
private fun MainContent(
    state: MainState,
    events: EventFlow<MainEvent>,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        DraggableNavLayout(
            modifier = Modifier
                .padding(2.dp)
                .padding(innerPadding),
            nav = {
                TerminalSection(
                    label = { TerminalSectionDefaults.Label("Nav") },
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        ) {
            TerminalSection(
                label = {
                    TerminalSectionDefaults.Label("Greeting")
                }
            ) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(
                        items = state.products
                    ) {
                        Text(it)
                    }
                }
            }
        }
    }
}