package ios.silv.tdshop.ui.ship

import android.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import ios.silv.term_ui.animateBlinkAlpha
import ios.silv.term_ui.rememberScaffoldState


fun EntryProviderBuilder<Screen>.shipScreenEntry(
    sharedTransitionScope: SharedTransitionScope,
) {
    entry<Ship> {
        val state = shipPresenter()

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
    state: ShipState,
    content: @Composable (state: ShipState) -> Unit,
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
            content(state)
        }
    }
}


@Composable
private fun SelectShippingDestination(
    animatedContentScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope,
    state: ShipState,
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
        },
        state
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
    val backStack = LocalBackStack.current
    ShipContent(
        sharedTransitionScope,
        animatedContentScope,
        fab = {
        },
        state,
    ) { _ ->
        val state = remember { CreateShipDestStateHolder() }
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
        ) {
            TerminalTextField(
                text = state.name,
                onValueChange = { state.name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") },
                error = state.nameErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.street1,
                onValueChange = { state.street1 = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Street 1") },
                error = state.street1Err,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.street2,
                onValueChange = { state.street2 = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Street 2") },
                error = state.street2Err,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.city,
                onValueChange = { state.city = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("City") },
                error = state.cityErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.state,
                onValueChange = { state.state = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("State") },
                error = state.stateErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.country,
                onValueChange = { state.country = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Country") },
                error = state.countryErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            TerminalTextField(
                text = state.phone,
                onValueChange = { state.phone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone") },
                error = state.phoneErr,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone
                )
            )
            TerminalTextField(
                text = state.postalCode,
                onValueChange = { state.postalCode = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Postal code") },
                error = state.postalCodeErr,
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
            AnimatedContent(true) { _ ->
                SelectShippingDestination(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@AnimatedContent,
                    state = ShipState(""),
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
            AnimatedContent(true) { _ ->
                CreateShippingDestination(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@AnimatedContent,
                    state = ShipState(""),
                )
            }
        }
    }
}