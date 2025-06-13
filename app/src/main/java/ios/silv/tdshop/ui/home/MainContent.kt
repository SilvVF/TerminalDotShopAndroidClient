package ios.silv.tdshop.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.rememberComposableLambda
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastSumBy
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import ios.silv.tdshop.nav.Home
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.types.UiCart
import ios.silv.tdshop.types.UiProduct
import ios.silv.tdshop.ui.compose.EventFlow
import ios.silv.tdshop.ui.compose.MutedAlpha
import ios.silv.tdshop.ui.compose.isLight
import ios.silv.tdshop.ui.compose.rememberEventFlow
import ios.silv.tdshop.ui.compose.toColor
import ios.silv.tdshop.ui.home.PoppableDestinationTopAppBar
import ios.silv.tdshop.ui.theme.TdshopTheme
import ios.silv.term_ui.DraggableNavLayout
import ios.silv.term_ui.LocalSharedTransitionScope
import ios.silv.term_ui.NavLayoutDragState
import ios.silv.term_ui.PersistentNavigationBar
import ios.silv.term_ui.PersistentNavigationRail
import ios.silv.term_ui.PersistentScaffold
import ios.silv.term_ui.ScaffoldState
import ios.silv.term_ui.TerminalSection
import ios.silv.term_ui.TerminalSectionButton
import ios.silv.term_ui.TerminalSectionDefaults
import ios.silv.term_ui.TerminalSplitButton
import ios.silv.term_ui.TerminalTitle
import ios.silv.term_ui.rememberNavLayoutDraggableState
import ios.silv.term_ui.rememberScaffoldState
import shop.terminal.api.models.product.Product
import kotlin.Unit

fun EntryProviderBuilder<Screen>.mainScreenEntry(
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<Home> {
        val backStack = LocalBackStack.current
        val events = rememberEventFlow<MainEvent>()
        val state = mainPresenter(events)

        AnimatedVisibility(true) {
            rememberScaffoldState(
                animatedVisibilityScope = this@AnimatedVisibility,
                sharedTransitionScope = sharedTransitionScope,
            ).PersistentScaffold(
                topBar = {
                    PoppableDestinationTopAppBar(
                        title = {
                            TerminalTitle()
                        },
                        onBackPressed = { backStack.pop() }
                    )
                }
            ) { paddingValues ->
                MainContent(
                    state,
                    events,
                    paddingValues = paddingValues
                )
            }
        }
    }
}

@Composable
fun ScaffoldState.PoppableDestinationTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    onBackPressed: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        navigationIcon = {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut(),
                content = {
                    FilledTonalIconButton(
                        modifier = Modifier,
                        onClick = onBackPressed,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        title = title,
        actions = actions,
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
                            qty = state.cart.items.fastFilter { it.id == selected.id }.size,
                            events = events,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        EmptyProductScreen(state, events, navDragState, Modifier.fillMaxSize())
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
            }
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
    qty: Int,
    events: EventFlow<MainEvent>,
    modifier: Modifier = Modifier,
) {
    Box(Modifier.fillMaxSize()) {
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
                    qty = qty,
                    add = {
                        events.tryEmit(MainEvent.AddToCart(product, variant))
                    },
                    dec = {
                        events.tryEmit(MainEvent.RemoveFromCart(product, variant))
                    }
                )
            }
        }

        Box(
            Modifier
                .align(BottomEnd)
                .padding(end = 12.dp)
        ) {
            if (product.subscription == Product.Subscription.REQUIRED) {
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
}

@Composable
fun EmptyProductScreen(
    state: MainState,
    events: EventFlow<MainEvent>,
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
private fun QtyIndicator(
    qty: Int,
    add: () -> Unit,
    dec: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextButton(
            onClick = dec
        ) {
            Text(
                "-",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current.copy(alpha = MutedAlpha)
                )
            )
        }
        Text("$qty")
        TextButton(
            onClick = add,
        ) {
            Text(
                "+",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current.copy(alpha = MutedAlpha)
                )
            )
        }
    }
}

@Composable
private fun CartEdit(
    modifier: Modifier = Modifier,
    qty: Int = 0
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
        AnimatedVisibility(true) {
            SharedTransitionLayout {
                rememberScaffoldState(
                    sharedTransitionScope = this,
                    animatedVisibilityScope = this@AnimatedVisibility
                ).PersistentScaffold(
                    topBar = {
                        PoppableDestinationTopAppBar(
                            title = {
                                TerminalTitle()
                            },
                            onBackPressed = {}
                        )
                    }
                ) {
                    val events = rememberEventFlow<MainEvent>()
                    var selected by remember {
                        mutableStateOf<UiProduct?>(null)
                    }

                    LaunchedEffect(Unit) {
                        events.collect {
                            when (it) {
                                MainEvent.Refresh -> {}
                                is MainEvent.ViewProduct -> selected =
                                    if (selected == it.product) null else it.product

                                else -> Unit
                            }
                        }
                    }

                    MainContent(
                        state = MainState(
                            loading = false,
                            products = previewUiProducts,
                            selectedProduct = selected,
                            cart = UiCart()
                        ),
                        events = events,
                        paddingValues = it
                    )
                }
            }
        }
    }
}