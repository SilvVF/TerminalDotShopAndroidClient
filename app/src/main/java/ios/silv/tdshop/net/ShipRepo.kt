package ios.silv.tdshop.net

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import ios.silv.tdshop.di.AppScope
import ios.silv.tdshop.ui.compose.SafeLaunchedEffect
import ios.silv.tdshop.ui.compose.safeCollectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.errors.TerminalInvalidDataException
import shop.terminal.api.models.address.Address
import shop.terminal.api.models.address.AddressCreateParams
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

    suspend fun createAddress(params: AddressCreateParams.Builder.() -> Unit) {
        client.createAddress(params).onSuccess { response ->

            val created = client
                .getAddress(response.data())
                .getOrThrow()

            addressesFlow.update { addresses ->
                addresses?.let {
                    val other = addresses.filter { address -> address.id() != created.id() }

                    buildList {
                        addAll(other)
                        add(created)
                    }
                }
            }
        }
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