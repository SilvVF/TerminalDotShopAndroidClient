package ios.silv.tdshop.ui.ship

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
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ios.silv.tdshop.nav.AddShipDest
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Payment
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.nav.Ship
import ios.silv.tdshop.types.UiCartItem
import ios.silv.tdshop.types.UiProduct
import ios.silv.tdshop.ui.cart.CartEvent
import ios.silv.tdshop.ui.components.CartBreadCrumbs
import ios.silv.tdshop.ui.components.QtyIndicator
import ios.silv.tdshop.ui.compose.MutedAlpha
import ios.silv.tdshop.ui.theme.TdshopTheme
import ios.silv.term_ui.PersistentCustomFab
import ios.silv.term_ui.TerminalSection
import ios.silv.term_ui.TerminalSectionButton
import ios.silv.term_ui.TerminalSectionDefaults
import shop.terminal.api.models.address.Address


fun EntryProviderBuilder<Screen>.shipScreenEntry(
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<Ship> {
        val state = shipSelectPresenter()

        SelectShippingDestination(
            LocalNavAnimatedContentScope.current,
            sharedTransitionScope,
            state,
        )
    }
}


@Composable
private fun SelectShippingDestination(
    animatedContentScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope,
    state: ShipSelectState,
    modifier: Modifier = Modifier
) {
    val backStack = LocalBackStack.current
    ShipBaseScaffold(
        sharedTransitionScope,
        animatedContentScope,
        fab = {
            PersistentCustomFab {
                if (state.selectedAddressId != null) {
                    TerminalSectionButton(
                        onClick = {
                            backStack.push(Payment)
                        },
                        modifier = Modifier.size(64.dp),
                        label = {
                            TerminalSectionDefaults.Label("Pay")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null
                        )
                    }
                } else {
                    TerminalSectionButton(
                        onClick = {
                            backStack.push(AddShipDest)
                        },
                        modifier = Modifier.size(64.dp),
                        label = {
                            TerminalSectionDefaults.Label("Add")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) {
        BackHandler(state.previewAddress != null) {
            state.events(ShipSelectEvent.PreviewAddress(null))
        }

        LazyColumn(modifier.fillMaxSize()) {
            item {
                Text(
                    "select shipping address",
                    color = LocalContentColor.current.copy(alpha = MutedAlpha)
                )
            }
            items(
                state.addresses,
                key = { it.id() }
            ) { address ->
                AnimatedVisibility(
                    visible = address.id() != state.previewAddress?.id(),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.animateItem()
                ) {
                    ShipListItem(
                        item = address,
                        selected = address.id() == state.selectedAddressId,
                        events = state.events,
                        modifier = Modifier
                            .heightIn(min = 120.dp, max = 240.dp)
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = address.id()),
                                animatedVisibilityScope = this@AnimatedVisibility
                            )
                            .padding(horizontal = 2.dp)
                            .combinedClickable(
                                onLongClick = {
                                    state.events(
                                        ShipSelectEvent.PreviewAddress(address)
                                    )
                                },
                                onClick = {
                                    state.events(
                                        ShipSelectEvent.SetAddress(address)
                                    )
                                }
                            )
                    )
                }
            }
            item {
                TerminalSectionButton(
                    onClick = {
                        backStack.push(AddShipDest)
                    },
                    modifier = Modifier.height(90.dp),
                    label = {
                        TerminalSectionDefaults.Label("add address")
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Row(
                            Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("add new address")
                            Text("enter")
                        }
                    }
                }
            }
        }
        AddressPreview(state)
    }
}

@Composable
fun ShipListItem(
    item: Address,
    selected: Boolean,
    events: (ShipSelectEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    TerminalSection(
        label = {
            TerminalSectionDefaults.Label(
                item.name(),
                style = MaterialTheme.typography.labelLarge.copy(
                    textDecoration = if (selected) {
                        TextDecoration.Underline
                    } else {
                        TextDecoration.None
                    }
                )
            )
        },
        borderStroke = if (selected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
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
                Text(
                    item.city(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    item.street1(),
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
                    item.country(),
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }
    }
}

@Composable
fun SharedTransitionScope.AddressPreview(state: ShipSelectState, modifier: Modifier = Modifier) {
    AnimatedContent(
        modifier = modifier.fillMaxSize(),
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        // key by only these two items the cart quantity changing would cause this state to change otherwise
        targetState = state.previewAddress,
        contentKey = { item -> item?.id() },
        label = "ProductDetails"
    ) { address ->
        if (address != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(remember(::MutableInteractionSource), null) {
                            state.events(ShipSelectEvent.PreviewAddress(null))
                        }
                )
                TerminalSection(
                    label = {
                        TerminalSectionDefaults.Label(
                            address.name(),
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight(0.5f)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = address.id()),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(RectangleShape)
                        )
                        .clip(RectangleShape)
                        .clickable(remember(::MutableInteractionSource), null) {}
                ) {
                    ShipAddressView(
                        state = remember(address) {
                            CreateDestinationState(address)
                        },
                        creating = true,
                        readOnly = true,
                        events = {},
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewViewShipContent() {
    TdshopTheme {
        SharedTransitionLayout {
            AnimatedContent(true) { target ->
                target
                SelectShippingDestination(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@AnimatedContent,
                    state = ShipSelectState(
                        emptyList(),
                        null,
                        null,
                        {}
                    )
                )
            }
        }
    }
}

