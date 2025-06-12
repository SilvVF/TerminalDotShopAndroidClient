package ios.silv.tdshop.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    state: MainState,
    events: EventFlow<MainEvent>,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        PullToRefreshBox(
            onRefresh = { events.tryEmit(MainEvent.Refresh) },
            isRefreshing = state.loading,
        ) {
            DraggableNavLayout(
                modifier = Modifier
                    .padding(2.dp)
                    .padding(innerPadding),
                nav = {
                    TerminalSection(
                        label = { TerminalSectionDefaults.Label("Products") },
                    ) {
                        LazyColumn(
                            Modifier.fillMaxSize()
                        ) {
                            items(state.products) { product ->
                                val containerColor = if (state.selectedProduct == product) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Unspecified
                                }
                                TextButton(
                                    onClick = {
                                        events.tryEmit(MainEvent.ViewProduct(product))
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = containerColor,
                                        contentColor = contentColorFor(containerColor)
                                    ),
                                    shape = RectangleShape,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = product.name)
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            ) {
                TerminalSection(
                    label = {
                        TerminalSectionDefaults.Label(state.selectedProduct?.name ?: "Product")
                    }
                ) {
                    AnimatedContent(
                        state.selectedProduct,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { selected ->
                        if (selected != null) {
                            ProductDetails(selected, Modifier.fillMaxSize())
                        } else {
                            Box(Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDetails(
    product: UiProduct,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("$product")
    }
}