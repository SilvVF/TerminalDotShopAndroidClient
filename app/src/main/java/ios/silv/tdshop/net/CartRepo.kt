package ios.silv.tdshop.net

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import ios.silv.tdshop.di.AppScope
import ios.silv.tdshop.types.UiCart
import ios.silv.tdshop.ui.compose.SafeLaunchedEffect
import ios.silv.tdshop.ui.compose.safeCollectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.asLog
import logcat.logcat
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.errors.TerminalInvalidDataException
import shop.terminal.api.models.cart.CartClearResponse
import shop.terminal.api.models.cart.CartSetAddressParams
import shop.terminal.api.models.cart.CartSetItemParams
import kotlin.jvm.Throws


@AppScope
@Inject
class CartRepo(
    private val shopClient: ShopClient
) {
    private val cartFlow = MutableStateFlow<UiCart>(UiCart())

    @Throws(TerminalInvalidDataException::class)
    suspend fun refresh() {
        logcat { "refreshing cart" }
        val uiCart =  shopClient
            .getCart()
            .onSuccess { logcat { "refreshed cart $it" } }
            .onFailure { logcat { it.asLog() } }
            .getOrThrow()
            .let(::UiCart)
        logcat { "mapped cart to $uiCart" }

        cartFlow.value = uiCart
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

    suspend fun setAddress(params: CartSetAddressParams.Builder.() -> Unit ) {
        shopClient.setCartAddress(params)
            .onSuccess { refresh() }
    }

    private val addMutex = Mutex()

    @Throws(TerminalInvalidDataException::class)
    suspend fun addItem(params: CartSetItemParams.Builder.() -> Unit) {
        logcat { "adding to cart" }
        addMutex.withLock {
            val response = shopClient.addItemToCart(params).getOrThrow()
            cartFlow.value  = response.data().let(::UiCart)
            logcat { "mapped cart to ${cartFlow.value}" }
        }
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
            if (!cart.initialized) {
                refresh()
            }
        }

        return cart
    }
}