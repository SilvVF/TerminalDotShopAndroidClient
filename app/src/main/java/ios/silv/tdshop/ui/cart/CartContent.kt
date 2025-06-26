package ios.silv.tdshop.ui.cart

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.data.Group
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.Dialog
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ios.silv.tdshop.nav.Cart
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.nav.Ship
import ios.silv.tdshop.types.UiCartItem
import ios.silv.tdshop.types.UiProduct
import ios.silv.tdshop.ui.components.CartBreadCrumbs
import ios.silv.tdshop.ui.components.QtyIndicator
import ios.silv.tdshop.ui.compose.EventEffect
import ios.silv.tdshop.ui.compose.MutedAlpha
import ios.silv.tdshop.ui.compose.rememberEventFlow
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
        val state = cartPresenter()

        CartScaffold(
            sharedTransitionScope,
            LocalNavAnimatedContentScope.current,
            state
        )
    }
}

@Composable
private fun CartScaffold(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    state: CartState,
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
                title = { TerminalTitle(text = "Cart") },
                actions = {
                    TerminalSection(
                        label = {
                            TerminalSectionDefaults.Label(
                                "total",
                                color = MaterialTheme.colorScheme.background,
                            )
                        }
                    ) {
                        Text("$${state.cart.subtotal / 100}")
                    }
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
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CartBreadCrumbs(modifier = Modifier.fillMaxWidth())
            Box(Modifier.fillMaxSize()) {
                CartContent(
                    state = state,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun SharedTransitionScope.CartContent(
    state: CartState,
    modifier: Modifier
) {
    BackHandler(state.selectedItem != null) {
        state.events(CartEvent.ViewProduct(null))
    }

    LazyColumn(modifier.fillMaxSize()) {
        items(state.groupedItems) { (item, product, variant) ->
            AnimatedVisibility(
                visible = variant.id != state.selectedItem?.third?.id,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.animateItem()
            ) {
                CartListItem(
                    item,
                    product,
                    variant,
                    state.events,
                    Modifier
                        .heightIn(min = 120.dp, max = 240.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = variant.id),
                            animatedVisibilityScope = this@AnimatedVisibility
                        )
                        .padding(horizontal = 2.dp)
                        .combinedClickable(
                            onLongClick = {
                                state.events(
                                    CartEvent.ViewProduct(
                                        Triple(
                                            item,
                                            product,
                                            variant
                                        )
                                    )
                                )
                            },
                            onClick = {}
                        )
                )
            }
        }
    }
    ProductDetails(state)
}

@Composable
private fun SharedTransitionScope.ProductDetails(
    state: CartState,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        modifier = modifier.fillMaxSize(),
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        // key by only these two items the cart quantity changing would cause this state to change otherwise
        targetState = state.selectedItem,
        contentKey = { item -> item?.first?.id },
        label = "ProductDetails"
    ) { cartItem ->
        if (cartItem != null) {
            val (item, product, variant) = cartItem

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(remember(::MutableInteractionSource), null) {
                            state.events(CartEvent.ViewProduct(null))
                        }
                )
                TerminalSection(
                    label = {
                        TerminalSectionDefaults.Label(
                            product.name,
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight(0.5f)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = variant.id),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(RectangleShape)
                        )
                        .clip(RectangleShape)
                        .clickable(remember(::MutableInteractionSource), null) {}
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(product.name, style = MaterialTheme.typography.titleMedium)
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                variant.name,
                                color = LocalContentColor.current.copy(
                                    alpha = MutedAlpha
                                )
                            )
                            Text(
                                "$${item.subtotal / 100}",
                                color = product.color ?: MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                        Text(
                            product.description,
                            color = LocalContentColor.current.copy(
                                alpha = MutedAlpha
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            QtyIndicator(
                                qty = item.quantity.toInt(),
                                add = {
                                    state.events(CartEvent.IncQty(item))
                                },
                                dec = {
                                    state.events(CartEvent.DecQty(item))
                                }
                            )
                            Text(
                                "$${item.subtotal / 100}",
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }
                }
            }
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
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .padding(start = 6.dp),
                horizontalAlignment = Alignment.Start
            ) {
                val productColor =
                    product.color ?: MaterialTheme.colorScheme.primary
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
                Text(
                    "$${item.subtotal / 100}",
                    modifier = Modifier.padding(end = 12.dp)
                )
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
        val events = rememberEventFlow<CartEvent>()

        var state by remember {
            mutableStateOf(
                CartState(
                    cart = cartPreviewData,
                    products = previewUiProducts,
                    selectedItem = null,
                    groupedItems = run {
                        val variantIdToProducts = previewUiProducts
                            .map { p -> p.variants.map { v -> p to v } }
                            .flatten()
                            .groupBy { it.second.id }
                            .mapValues { it.value.first() }

                        cartPreviewData.items.mapNotNull { item ->
                            val (product, variant) = variantIdToProducts[item.productVariantId]
                                ?: return@mapNotNull null
                            Triple(
                                item, product, variant
                            )
                        }
                    },
                    events = events::tryEmit
                )
            )
        }

        EventEffect(events) {
            when (it) {
                is CartEvent.ViewProduct -> state = state.copy(
                    selectedItem = it.item
                )

                else -> Unit
            }
        }

        SharedTransitionLayout {
            AnimatedContent(true) { _ ->
                CartScaffold(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@AnimatedContent,
                    state = state
                )
            }
        }
    }
}