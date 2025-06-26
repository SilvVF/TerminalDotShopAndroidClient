package ios.silv.tdshop.ui.ship

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.net.ShipRepo
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.models.address.Address
import shop.terminal.api.models.address.AddressCreateParams

data class CreateDestinationState(
    val name: String = "",
    val street1: String = "",
    val street2: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val phone: String = "",
    val postalCode: String = "",

    val nameErr: String? = null,
    val street1Err: String? = null,
    val street2Err: String? = null,
    val cityErr: String? = null,
    val stateErr: String? = null,
    val countryErr: String? = null,
    val phoneErr: String? = null,
    val postalCodeErr: String? = null,
) : java.io.Serializable {

    constructor(address: Address): this(
        name = address.name(),
        street1 = address.street1(),
        street2 =  address.street2().orEmpty(),
        city = address.city(),
        state = address.province().orEmpty(),
        country = address.country(),
        phone = address.phone().orEmpty(),
        postalCode = address.zip(),
    )
}

data class ShipCreateState(
    val creating: Boolean,
    val destState: CreateDestinationState,
    val events: (ShipEvent) -> Unit
)

sealed interface ShipEvent {
    data object CreateDest : ShipEvent
    data class UpdateDest(val state: CreateDestinationState) : ShipEvent
}

fun CreateDestinationState.isValid(): Boolean {
    return listOf(
        nameErr,
        street1Err,
        cityErr,
        stateErr,
        countryErr,
        phoneErr,
        postalCodeErr
    ).all { it == null }
}

fun CreateDestinationState.toParams(): AddressCreateParams.Builder.() -> Unit = {
    this.name(name)
    this.street1(street1)
    this.street2(street2)
    this.city(city)
    this.province(state)
    this.country(country)
    this.phone(phone)
    this.zip(postalCode)
}

fun CreateDestinationState.validate(): CreateDestinationState {
    return copy(
        nameErr = if (name.isBlank()) "Name is required" else null,
        street1Err = if (street1.isBlank()) "Street is required" else null,
        cityErr = if (city.isBlank()) "City is required" else null,
        stateErr = if (state.isBlank()) "State is required" else null,
        countryErr = if (country.isBlank()) "Country is required" else null,
        phoneErr = if (!phone.matches(Regex("^\\+?[0-9]{7,15}$"))) "Invalid phone number" else null,
        postalCodeErr = if (!postalCode.matches(Regex("^[A-Za-z0-9 -]{3,10}$"))) "Invalid postal code" else null
    )
}

@Composable
fun shipPresenter() = requireActivityComponent().shipPresenterProvider()

typealias shipPresenterProvider = @Composable () -> ShipCreateState

@Inject
@Composable
fun shipPresenterProvider(
    shipRepo: ShipRepo
): ShipCreateState = providePresenterDefaults { userMessageHolder, backStack ->

    val scope = rememberCoroutineScope()

    var creating by remember { mutableStateOf(false) }
    var destState by rememberSaveable { mutableStateOf(CreateDestinationState()) }

    ShipCreateState(
        destState = destState,
        creating = creating
    ) { event ->
        when (event) {
            ShipEvent.CreateDest -> {
                if (creating) return@ShipCreateState

                creating = true
                destState = destState.validate()

                if (destState.isValid()) {
                    scope.launch {
                        runCatching {
                            shipRepo.createAddress(destState.toParams())
                        }

                        creating = false
                    }
                }
            }

            is ShipEvent.UpdateDest -> destState = event.state
        }
    }
}