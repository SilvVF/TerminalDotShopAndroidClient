package ios.silv.tdshop.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ios.silv.tdshop.BuildConfig
import ios.silv.tdshop.nav.Cart
import ios.silv.tdshop.nav.Home
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.types.UiCartItem
import ios.silv.tdshop.types.UiProduct
import ios.silv.tdshop.ui.components.QtyIndicator
import ios.silv.tdshop.ui.compose.EventFlow
import ios.silv.tdshop.ui.compose.MutedAlpha
import ios.silv.tdshop.ui.compose.isLight
import ios.silv.tdshop.ui.compose.rememberEventFlow
import ios.silv.tdshop.ui.theme.TdshopTheme
import ios.silv.term_ui.DraggableNavLayout
import ios.silv.term_ui.NavLayoutDragState
import ios.silv.term_ui.PersistentCustomFab
import ios.silv.term_ui.PersistentScaffold
import ios.silv.term_ui.PoppableDestinationTopAppBar
import ios.silv.term_ui.ScaffoldState
import ios.silv.term_ui.TerminalSection
import ios.silv.term_ui.TerminalSectionButton
import ios.silv.term_ui.TerminalSectionDefaults
import ios.silv.term_ui.TerminalSplitButton
import ios.silv.term_ui.TerminalTitle
import ios.silv.term_ui.rememberNavLayoutDraggableState
import ios.silv.term_ui.rememberScaffoldState
import shop.terminal.api.models.product.Product

fun EntryProviderBuilder<Screen>.mainScreenEntry(
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<Home> {
        val events = rememberEventFlow<MainEvent>()
        val state = mainPresenter(events)

        MainScaffold(
            LocalNavAnimatedContentScope.current,
            sharedTransitionScope,
            state,
            events
        )
    }
}

@Composable
private fun MainScaffold(
    animatedContentScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope,
    state: MainState,
    events: EventFlow<MainEvent>,
) {
    rememberScaffoldState(
        animatedVisibilityScope = animatedContentScope,
        sharedTransitionScope = sharedTransitionScope,
    ).PersistentScaffold(
        topBar = {
            ProductTopBar(state)
        },
        floatingActionButton = {
            PersistentCustomFab {
                if (state.selectedProduct?.subscription == Product.Subscription.REQUIRED) {
                    TerminalSectionButton(
                        onClick = {},
                        label = { TerminalSectionDefaults.Label("Subscribe") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null
                        )
                    }
                } else {
                    CartEdit()
                }
            }
        }
    ) { paddingValues ->
        MainContent(
            state,
            events,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun ScaffoldState.ProductTopBar(
    state: MainState,
    modifier: Modifier = Modifier
) {
    val backStack = LocalBackStack.current
    PoppableDestinationTopAppBar(
        modifier = modifier,
        visible = backStack.canPop,
        title = {
            TerminalTitle()
        },
        onBackPressed = { backStack.pop() },
        actions = {
            TerminalSectionButton(
                onClick = {
                    backStack.push(Cart)
                },
                label = {
                    TerminalSectionDefaults.Label("Cart")
                }
            ) {
                BadgedBox(
                    badge = {
                        Badge {
                            Text("${state.cart.items.sumOf { it.quantity }}")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    state: MainState,
    events: EventFlow<MainEvent>,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val navDragState = rememberNavLayoutDraggableState()
    PullToRefreshBox(
        onRefresh = { events.tryEmit(MainEvent.Refresh) },
        isRefreshing = state.loading,
    ) {
        DraggableNavLayout(
            modifier = modifier
                .padding(paddingValues)
                .padding(2.dp),
            state = navDragState,
            nav = {
                ProductNavBar(state, events)
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
                        ProductDetails(
                            product = selected,
                            events = events,
                            modifier = Modifier.fillMaxSize(),
                            cartItem = state.cart.items.firstOrNull {
                                it.productVariantId == selected.variants.first().id
                            }
                        )
                    } else {
                        EmptyProductScreen(state, navDragState, Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductNavBar(
    state: MainState,
    events: EventFlow<MainEvent>,
    modifier: Modifier = Modifier
) {
    TerminalSection(
        modifier = modifier,
        label = { TerminalSectionDefaults.Label("Products") },
    ) {
        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            if (state.featured.isNotEmpty()) {
                item {
                    ProductNavLabel(
                        "~ Featured ~",
                        selected = state.selectedProduct?.featured == true,
                        Modifier.padding(vertical = 12.dp)
                    )
                    HorizontalDivider()
                }
                items(state.featured) { product ->
                    ProductNavItem(
                        product = product,
                        selectedProduct = state.selectedProduct,
                        onView = { events.tryEmit(MainEvent.ViewProduct(product)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider()
                }
            }
            item {
                ProductNavLabel(
                    "~ Originals ~",
                    selected = state.selectedProduct?.featured != true,
                    Modifier.padding(vertical = 12.dp)
                )
                HorizontalDivider()
            }
            items(state.nonFeatured) { product ->
                ProductNavItem(
                    product = product,
                    selectedProduct = state.selectedProduct,
                    onView = { events.tryEmit(MainEvent.ViewProduct(product)) },
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ProductNavLabel(
    name: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    BasicText(
        text = name,
        style = MaterialTheme.typography.labelMedium.copy(
            textAlign = TextAlign.Center,
            textDecoration = if (selected) {
                TextDecoration.Underline
            } else {
                TextDecoration.None
            },
            color = LocalContentColor.current
        ),
        maxLines = 1,
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.labelMedium.fontSize)
    )
}

@Composable
private fun ProductNavItem(
    onView: () -> Unit,
    product: UiProduct,
    selectedProduct: UiProduct?,
    modifier: Modifier = Modifier
) {
    val productColor = product.color
    val containerColor = if (selectedProduct == product) {
        productColor ?: MaterialTheme.colorScheme.primary
    } else {
        Color.Unspecified
    }

    TextButton(
        onClick = onView,
        colors = ButtonDefaults.textButtonColors(
            containerColor = containerColor,
            contentColor = when {
                selectedProduct != product -> productColor ?: Color.Unspecified
                productColor == null -> contentColorFor(containerColor)
                productColor.isLight() -> Color.Black
                else -> Color.White
            }
        ),
        shape = RectangleShape,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = product.name)
    }
}

@Composable
private fun ProductDetails(
    product: UiProduct,
    cartItem: UiCartItem?,
    events: EventFlow<MainEvent>,
    modifier: Modifier = Modifier,
) {
    SelectionContainer {
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            var variant by remember { mutableStateOf(product.variants.first()) }
            val productColor = product.color ?: MaterialTheme.colorScheme.primary

            Text(product.name, style = MaterialTheme.typography.titleMedium)
            product.variants.fastForEach {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { variant = it }
                ) {
                    Text(
                        it.name,
                        textDecoration = if (variant == it) {
                            TextDecoration.Underline
                        } else {
                            TextDecoration.None
                        },
                        color = LocalContentColor.current.copy(
                            alpha = MutedAlpha
                        )
                    )
                    Text(
                        "$${it.usd}",
                        color = productColor,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
            Text(
                product.description,
                color = LocalContentColor.current.copy(
                    alpha = MutedAlpha
                )
            )

            if (product.subscription?.isValid() == true && product.subscription.value() == Product.Subscription.Value.REQUIRED) {
                SubscriptionIndicator(
                    subscribe = {
                        events.tryEmit(MainEvent.Subscribe(product, variant))
                    },
                    product = product
                )
            } else {
                QtyIndicator(
                    qty = cartItem?.quantity?.toInt() ?: 0,
                    add = {
                        events.tryEmit(
                            MainEvent.AddToCart(
                                variant.id,
                                (cartItem?.quantity?.toInt() ?: 0) + 1
                            )
                        )
                    },
                    dec = {
                        events.tryEmit(
                            MainEvent.AddToCart(
                                variant.id,
                                (cartItem?.quantity?.toInt() ?: 0) - 1
                            )
                        )
                    }
                )
            }
            if (BuildConfig.DEBUG) {
                Text("$cartItem")
                Spacer(Modifier.height(12.dp))
                Text("$product")
                Spacer(Modifier.height(12.dp))
                Text("$variant")
            }
        }
    }
}

@Composable
fun EmptyProductScreen(
    state: MainState,
    navDragState: NavLayoutDragState,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        if (state.products.isEmpty()) {
            Text("Loading...", Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    color = LocalContentColor.current.copy(
                        alpha = MutedAlpha
                    ),
                    modifier = Modifier.width(300.dp),
                    textAlign = TextAlign.Center,
                    text = "Open the nav bar to view products list"
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    shape = RectangleShape,
                    onClick = {
                        navDragState.show()
                    },
                ) {
                    Text(
                        "Products",
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionIndicator(
    subscribe: () -> Unit,
    product: UiProduct,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            shape = RectangleShape,
            onClick = subscribe,
            colors = ButtonDefaults.buttonColors(
                containerColor = product.color ?: MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Subscribe",
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            "Enter", style = LocalTextStyle.current.copy(
                color = LocalContentColor.current.copy(alpha = MutedAlpha)
            )
        )
    }
}


@Composable
private fun CartEdit(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TerminalSplitButton(
            label = {
                TerminalSectionDefaults.Label("+/- qty")
            },
            onRightClick = {},
            onLeftClick = {},
            left = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null
                )
            },
            right = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        )
    }
}

@Preview
@Composable
private fun PreviewMainScreen() {
    TdshopTheme {

        val events = rememberEventFlow<MainEvent>()
        var state by remember {
            mutableStateOf(
                MainState(
                    loading = false,
                    products = previewUiProducts,
                    selectedProduct = null,
                    cart = cartPreviewData
                )
            )
        }
        AnimatedContent(true) { _ ->
            SharedTransitionLayout {
                MainScaffold(
                    this@AnimatedContent,
                    this@SharedTransitionLayout,
                    state = state,
                    events = events
                )
            }
        }
    }
}