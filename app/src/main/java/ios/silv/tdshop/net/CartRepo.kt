package ios.silv.tdshop.net

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import ios.silv.tdshop.types.UiCart
import ios.silv.tdshop.ui.compose.SafeLaunchedEffect
import ios.silv.tdshop.ui.compose.safeCollectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import logcat.logcat
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.errors.TerminalInvalidDataException
import shop.terminal.api.models.cart.CartClearResponse
import shop.terminal.api.models.cart.CartSetItemParams
import kotlin.jvm.Throws


@Inject
class CartRepo(
    private val shopClient: ShopClient
) {
    private val cartFlow = MutableStateFlow<UiCart>(UiCart())

    @Throws(TerminalInvalidDataException::class)
    suspend fun refresh() {
        cartFlow.value = shopClient
            .getCart()
            .getOrThrow()
            .let(::UiCart)
    }

    @Throws(TerminalInvalidDataException::class)
    suspend fun clear() {
        shopClient.clearCart()
            .onSuccess { response ->
                when (response.data().value()) {
                    CartClearResponse.Data.Value._UNKNOWN -> error("unknown cart response")
                    CartClearResponse.Data.Value.OK -> refresh()
                }
            }
    }

    @Throws(TerminalInvalidDataException::class)
    suspend fun addItem(params: CartSetItemParams.Builder.() -> Unit) {
        shopClient.addItemToCart(params)
            .onSuccess { response ->
                cartFlow.value = response.data().let(::UiCart)
            }
            .getOrThrow()
    }

    fun cartAsFlow(): Flow<UiCart> {
        return cartFlow.onStart {
            if (!cartFlow.value.initialized) {
                refresh()
            }
        }
            .catch {
                logcat { "Failed to refresh in productsFlow() $it" }
                emit(cartFlow.value)
            }
    }

    @Composable
    fun cart(): UiCart {
        val cart by cartFlow.safeCollectAsState()

        SafeLaunchedEffect(Unit) {
            if (cart.initialized) {
                refresh()
            }
        }

        return cart
    }
}