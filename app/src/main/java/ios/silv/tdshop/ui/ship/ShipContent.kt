package ios.silv.tdshop.ui.ship

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ios.silv.tdshop.nav.AddShipDest
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.nav.Ship
import ios.silv.tdshop.ui.compose.MutedAlpha
import ios.silv.tdshop.ui.theme.TdshopTheme
import ios.silv.term_ui.PersistentCustomFab
import ios.silv.term_ui.PersistentScaffold
import ios.silv.term_ui.PoppableDestinationTopAppBar
import ios.silv.term_ui.ScaffoldState
import ios.silv.term_ui.TerminalSectionButton
import ios.silv.term_ui.TerminalSectionDefaults
import ios.silv.term_ui.TerminalTextField
import ios.silv.term_ui.TerminalTitle
import ios.silv.term_ui.rememberScaffoldState


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

fun EntryProviderBuilder<Screen>.addShipDestEntry(
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<AddShipDest> {
        val state = shipPresenter()

        CreateShippingDestination(
            state,
            sharedTransitionScope,
            LocalNavAnimatedContentScope.current,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ShipContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    fab: @Composable ScaffoldState.() -> Unit,
    content: @Composable () -> Unit,
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
        },
        floatingActionButton = {
            fab()
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            content()
        }
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
    ShipContent(
        sharedTransitionScope,
        animatedContentScope,
        fab = {
            PersistentCustomFab {
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
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 2.dp)
        ) {
            Text(
                "select shipping address",
                color = LocalContentColor.current.copy(alpha = MutedAlpha)
            )
            state.addresses.fastForEach {
                Text("$it")
            }
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
}

@Composable
private fun CreateShippingDestination(
    state: ShipState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier
) {
    ShipContent(
        sharedTransitionScope,
        animatedContentScope,
        fab = {
            PersistentCustomFab {
                TerminalSectionButton(
                    label = {
                        TerminalSectionDefaults.Label("Create")
                    },
                    onClick = {
                        state.events(ShipEvent.CreateDest)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        },
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
        ) {
            TerminalTextField(
                text = state.destState.name,
                onValueChange = {
                    state.events(ShipEvent.UpdateDest(state.destState.copy(name = it)))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") },
                error = state.destState.nameErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.destState.street1,
                onValueChange = {
                    state.events(ShipEvent.UpdateDest(state.destState.copy(street1 = it)))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Street 1") },
                error = state.destState.street1Err,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.destState.street2,
                onValueChange = {
                    state.events(ShipEvent.UpdateDest(state.destState.copy(street2 = it)))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Street 2") },
                error = state.destState.street2Err,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.destState.city,
                onValueChange = {
                    state.events(ShipEvent.UpdateDest(state.destState.copy(city = it)))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("City") },
                error = state.destState.cityErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.destState.state,
                onValueChange = {
                    state.events(ShipEvent.UpdateDest(state.destState.copy(state = it)))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("State") },
                error = state.destState.stateErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.destState.country,
                onValueChange = {
                    state.events(ShipEvent.UpdateDest(state.destState.copy(country = it)))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Country") },
                error = state.destState.countryErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.destState.phone,
                onValueChange = {
                    state.events(ShipEvent.UpdateDest(state.destState.copy(phone = it)))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone") },
                error = state.destState.phoneErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone
                )
            )
            TerminalTextField(
                text = state.destState.postalCode,
                onValueChange = {
                    state.events(ShipEvent.UpdateDest(state.destState.copy(postalCode = it)))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Postal code") },
                error = state.destState.postalCodeErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }
}

@Preview
@Composable
private fun PreviewViewShipContent() {
    TdshopTheme {
        SharedTransitionLayout {
            AnimatedContent(true) { scope ->
                scope
                SelectShippingDestination(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@AnimatedContent,
                    state = ShipSelectState(
                        emptyList()
                    ){},
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCreateShipContent() {
    TdshopTheme {
        SharedTransitionLayout {
            AnimatedContent(true) { scope ->
                scope
                CreateShippingDestination(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@AnimatedContent,
                    state = ShipState(
                        false,
                        CreateDestinationState(),
                    ){},
                )
            }
        }
    }
}