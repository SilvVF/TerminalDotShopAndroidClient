package ios.silv.tdshop.net

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import ios.silv.tdshop.Keys
import ios.silv.tdshop.SettingsStore
import ios.silv.tdshop.di.AppScope
import ios.silv.tdshop.types.UiCart
import ios.silv.tdshop.ui.compose.SafeLaunchedEffect
import ios.silv.tdshop.ui.compose.safeCollectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.errors.TerminalInvalidDataException
import shop.terminal.api.models.address.Address
import shop.terminal.api.models.cart.CartSetAddressParams
import kotlin.jvm.Throws

@AppScope
@Inject
class ShipRepo(
    private val client: ShopClient,
) {

    val addressesFlow = MutableStateFlow<List<Address>?>(null)

    @Throws(TerminalInvalidDataException::class)
    suspend fun refresh() {
        addressesFlow.value = client.getAddresses()
            .getOrThrow()
    }

    suspend fun setAddress(params: CartSetAddressParams.Builder.() -> Unit ) {
        client.setAddress(params)
            .onSuccess { refresh() }
    }

    @Composable
    fun addresses(): List<Address> {
        val addresses by addressesFlow.safeCollectAsState()

        SafeLaunchedEffect(Unit) {
            if (addresses == null) {
                refresh()
            }
        }

        return addresses ?: emptyList()
    }
}