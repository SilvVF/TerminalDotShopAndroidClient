package ios.silv.tdshop.ui.ship

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import me.tatarka.inject.annotations.Inject

data class ShipState(
    val address: String
)

sealed interface ShipEvent

@Composable
fun shipPresenter() = requireActivityComponent().shipPresenterProvider()

typealias shipPresenterProvider = @Composable () -> ShipState

class CreateShipDestStateHolder {

    var name by mutableStateOf("")
    var street1 by mutableStateOf("")
    var street2 by mutableStateOf("")
    var city by mutableStateOf("")
    var state by mutableStateOf("")
    var country by mutableStateOf("")
    var phone by mutableStateOf("")
    var postalCode by mutableStateOf("")

    var nameErr by mutableStateOf<String?>(null)
    var street1Err by mutableStateOf<String?>(null)
    var street2Err by mutableStateOf<String?>(null)
    var cityErr by mutableStateOf<String?>(null)
    var stateErr by mutableStateOf<String?>(null)
    var countryErr by mutableStateOf<String?>(null)
    var phoneErr by mutableStateOf<String?>(null)
    var postalCodeErr by mutableStateOf<String?>(null)
}

@Inject
@Composable
fun shipPresenterProvider(): ShipState = providePresenterDefaults { userMessageHolder, backstack ->



    ShipState("")
}