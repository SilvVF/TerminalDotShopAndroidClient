package ios.silv.tdshop.ui.ship

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ios.silv.tdshop.nav.AddShipDest
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.ui.ProvidePreviewDefaults
import ios.silv.tdshop.ui.components.CartBreadCrumbs
import ios.silv.tdshop.ui.theme.TdshopTheme
import ios.silv.term_ui.PersistentCustomFab
import ios.silv.term_ui.TerminalSectionButton
import ios.silv.term_ui.TerminalSectionDefaults
import ios.silv.term_ui.TerminalTextField

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
private fun CreateShippingDestination(
    state: ShipCreateState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    ShipBaseScaffold(
        sharedTransitionScope,
        animatedVisibilityScope,
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
                    AnimatedContent(state.creating) { creating ->
                        if (creating) {
                            CircularProgressIndicator()
                        } else {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        },
    ) {
        ShipAddressView(
            state = state.destState,
            creating = state.creating,
            readOnly = false,
            events = state.events,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ShipAddressView(
    state: CreateDestinationState,
    creating: Boolean,
    readOnly: Boolean,
    events: (ShipEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        TerminalTextField(
            text = state.name,
            enabled = !creating,
            onValueChange = {
                events(ShipEvent.UpdateDest(state.copy(name = it)))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Name") },
            error = state.nameErr,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        TerminalTextField(
            text = state.street1,
            enabled = !creating,
            onValueChange = {
                events(ShipEvent.UpdateDest(state.copy(street1 = it)))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Street 1") },
            error = state.street1Err,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        TerminalTextField(
            text = state.street2,
            enabled = !creating,
            onValueChange = {
                events(ShipEvent.UpdateDest(state.copy(street2 = it)))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Street 2") },
            error = state.street2Err,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        TerminalTextField(
            text = state.city,
            enabled = !creating,
            onValueChange = {
                events(ShipEvent.UpdateDest(state.copy(city = it)))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("City") },
            error = state.cityErr,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        TerminalTextField(
            text = state.state,
            enabled = !creating,
            onValueChange = {
                events(ShipEvent.UpdateDest(state.copy(state = it)))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("State") },
            error = state.stateErr,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        TerminalTextField(
            text = state.country,
            enabled = !creating,
            onValueChange = {
                events(ShipEvent.UpdateDest(state.copy(country = it)))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Country") },
            error = state.countryErr,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )
        TerminalTextField(
            text = state.phone,
            enabled = !creating,
            onValueChange = {
                events(ShipEvent.UpdateDest(state.copy(phone = it)))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Phone") },
            error = state.phoneErr,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone
            )
        )
        TerminalTextField(
            text = state.postalCode,
            enabled = !creating,
            onValueChange = {
                events(ShipEvent.UpdateDest(state.copy(postalCode = it)))
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Postal code") },
            error = state.postalCodeErr,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
    }
}

@Preview
@Composable
private fun PreviewCreateShipContent() {
    ProvidePreviewDefaults {
        CreateShippingDestination(
            sharedTransitionScope = this,
            animatedVisibilityScope = this,
            state = ShipCreateState(
                false,
                CreateDestinationState(),
            ) {},
        )
    }
}