package ios.silv.tdshop.ui.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ios.silv.tdshop.nav.Cart
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.nav.Ship
import ios.silv.tdshop.types.UiCartItem
import ios.silv.tdshop.types.UiProduct
import ios.silv.tdshop.ui.components.QtyIndicator
import ios.silv.tdshop.ui.compose.MutedAlpha
import ios.silv.tdshop.ui.home.cartPreviewData
import ios.silv.tdshop.ui.home.previewUiProducts
import ios.silv.tdshop.ui.theme.TdshopTheme
import ios.silv.term_ui.PersistentCustomFab
import ios.silv.term_ui.PersistentScaffold
import ios.silv.term_ui.PoppableDestinationTopAppBar
import ios.silv.term_ui.TerminalSection
import ios.silv.term_ui.TerminalSectionButton
import ios.silv.term_ui.TerminalSectionDefaults
import ios.silv.term_ui.TerminalTitle
import ios.silv.term_ui.rememberScaffoldState

fun EntryProviderBuilder<Screen>.cartScreenEntry(
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<Cart> {
        val backStack = LocalBackStack.current
        val state = cartPresenter()

        rememberScaffoldState(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = LocalNavAnimatedContentScope.current
        ).PersistentScaffold(
            topBar = {
                PoppableDestinationTopAppBar(
                    visible = backStack.canPop,
                    onBackPressed = { backStack.pop() },
                    title = { TerminalTitle(text = "Cart") },
                    actions = {
                        Text("$${state.cart.subtotal / 100}")
                    },
                )
            },
            floatingActionButton = {
                PersistentCustomFab {
                    TerminalSectionButton(
                        label = {
                            TerminalSectionDefaults.Label("Ship")
                        },
                        onClick = { backStack.push(Ship) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                        )
                    }
                }
            }
        ) { paddingValues ->
            // TODO: implement shared element preview https://developer.android.com/develop/ui/compose/animation/shared-elements#animated-visibility
            CartContent(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@Composable
private fun CartContent(
    state: CartState,
    modifier: Modifier
) {
    LazyColumn(modifier.fillMaxSize()) {
        items(state.groupedItems) { (item, product, variant) ->
            CartListItem(
                item,
                product,
                variant,
                state.events,
                Modifier
                    .padding(horizontal = 2.dp)
                    .height(120.dp)
            )
        }
    }
}

@Composable
fun CartListItem(
    item: UiCartItem,
    product: UiProduct,
    variant: UiProduct.Variant,
    events: (CartEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    TerminalSection(
        label = {
            TerminalSectionDefaults.Label(product.name)
        },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .padding(start = 6.dp),
                horizontalAlignment = Alignment.Start
            ) {
                val productColor = product.color ?: MaterialTheme.colorScheme.primary
                Text(
                    product.name, style = MaterialTheme.typography.titleMedium.copy(
                        color = productColor
                    ),
                    maxLines = 1
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    variant.name,
                    color = LocalContentColor.current.copy(
                        alpha = MutedAlpha
                    ),
                    maxLines = 2
                )

            }
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text("$${item.subtotal / 100}", modifier = Modifier.padding(end = 12.dp))
                QtyIndicator(
                    qty = item.quantity.toInt(),
                    add = {
                        events(CartEvent.IncQty(item))
                    },
                    dec = {
                        events(CartEvent.DecQty(item))
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCartContent() {
    TdshopTheme {
        val backStack = LocalBackStack.current
        val state by remember {
            mutableStateOf(
                CartState(
                    cart = cartPreviewData,
                    products = previewUiProducts,
                    events = {}
                )
            )
        }
        SharedTransitionLayout {
            AnimatedVisibility(true) {
                rememberScaffoldState(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility
                ).PersistentScaffold(
                    topBar = {
                        PoppableDestinationTopAppBar(
                            visible = backStack.canPop,
                            onBackPressed = { backStack.pop() },
                            title = { TerminalTitle(text = "Cart") },
                            actions = {
                                TerminalSection(
                                    label = { TerminalSectionDefaults.Label("total") }
                                ) {
                                    Text("$${state.cart.subtotal / 100}")
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    CartContent(
                        state = state,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
        }
    }
}