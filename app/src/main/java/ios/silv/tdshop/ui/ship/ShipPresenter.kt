package ios.silv.tdshop.ui.ship

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.nav.Ship
import ios.silv.tdshop.net.CartRepo
import ios.silv.tdshop.net.ShipRepo
import ios.silv.tdshop.ui.compose.EventEffect
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import ios.silv.tdshop.ui.compose.rememberEventFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.models.address.Address
import shop.terminal.api.models.address.AddressCreateParams

data class ShipSelectState(
    val addresses: List<Address>,
    val previewAddress: Address? = null,
    val selectedAddressId: String?,
    val events: (ShipSelectEvent) -> Unit,
)

sealed interface ShipSelectEvent {
    data class SetAddress(val address: Address) : ShipSelectEvent
    data class PreviewAddress(val address: Address?) : ShipSelectEvent
}

@Composable
fun shipSelectPresenter() = requireActivityComponent().shipSelectPresenterProvider()

typealias shipSelectPresenterProvider = @Composable () -> ShipSelectState

@Inject
@Composable
fun shipSelectPresenterProvider(
    shipRepo: ShipRepo,
    cartRepo: CartRepo,
) = providePresenterDefaults { userMessageHolder, backStack ->

    val events = rememberEventFlow<ShipSelectEvent>()

    var previewAddress by remember {
        mutableStateOf<Address?>(null)
    }

    val cart = cartRepo.cart()
    val addresses = shipRepo.addresses()

    EventEffect(events) { event ->
        when (event) {
            is ShipSelectEvent.SetAddress -> {
                cartRepo.setAddress {
                    addressId(event.address.id())
                }
            }
            is ShipSelectEvent.PreviewAddress -> previewAddress = event.address
        }
    }

    ShipSelectState(
        addresses,
        previewAddress = previewAddress,
        selectedAddressId = cart.addressId,
        events = events::tryEmit
    )
}


