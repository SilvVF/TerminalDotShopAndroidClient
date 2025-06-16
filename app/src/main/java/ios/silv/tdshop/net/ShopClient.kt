package ios.silv.tdshop.net

import ios.silv.tdshop.BuildConfig
import ios.silv.tdshop.EncryptedSettingsStore
import ios.silv.tdshop.di.AppScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat
import shop.terminal.api.client.TerminalClientAsync
import shop.terminal.api.client.okhttp.TerminalOkHttpClientAsync
import shop.terminal.api.models.address.Address
import shop.terminal.api.models.address.AddressGetParams
import shop.terminal.api.models.cart.Cart
import shop.terminal.api.models.cart.CartClearResponse
import shop.terminal.api.models.cart.CartGetParams
import shop.terminal.api.models.cart.CartSetAddressParams
import shop.terminal.api.models.cart.CartSetAddressResponse
import shop.terminal.api.models.cart.CartSetItemParams
import shop.terminal.api.models.cart.CartSetItemResponse
import shop.terminal.api.models.product.Product

@AppScope
class ShopClient(
    private val store: EncryptedSettingsStore,
    private val ioDispatcher: CoroutineDispatcher
) {
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var _client: TerminalClientAsync? = null
        set(value) {
            field?.close()
            field = value
        }

    private val client: TerminalClientAsync get() = requireNotNull(_client)

    init {
        scope.launch {
            store.tokenFlow
                .filterNot { it.isEmpty() }
                .collectLatest {
                    try {
                        _client = TerminalOkHttpClientAsync.builder()
                            .bearerToken(it)
                            .baseUrl(
                                baseUrl = if (BuildConfig.DEBUG) {
                                    "https://api.dev.terminal.shop"
                                } else {
                                    "https://api.terminal.shop "
                                }
                            )
                            .build()
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        logcat { e.stackTraceToString() }
                    }
                }
        }
    }

    suspend fun setAddress(params: CartSetAddressParams.Builder.() -> Unit): Result<CartSetAddressResponse.Data> {
        return runCatching {
            withContext(ioDispatcher) {
                client.cart().setAddress(
                    params = CartSetAddressParams
                        .builder()
                        .apply(params)
                        .build()
                )
            }
                .data()
        }
    }

    suspend fun getAddresses(): Result<List<Address>> {
        return runCatching {
            withContext(ioDispatcher) {
                client.address().list()
            }
                .data()
        }
    }

    suspend fun clearCart(): Result<CartClearResponse> {
        return runCatching {
            withContext(ioDispatcher) {
                client.cart().clear()
            }
        }
    }

    suspend fun addItemToCart(params: CartSetItemParams.Builder.() -> Unit): Result<CartSetItemResponse> {
        return runCatching {
            withContext(ioDispatcher) {
                client.cart().setItem(
                    params = CartSetItemParams
                        .builder()
                        .apply(params)
                        .build()
                )
            }
        }
    }

    suspend fun getCart(): Result<Cart> {
        return runCatching {
            withContext(ioDispatcher) {
                client.cart().get()
            }
                .data()
        }
    }

    suspend fun getProductList(): Result<List<Product>> {
        return runCatching {
            withContext(ioDispatcher) {
                client.product().list()
            }
                .data()
        }
    }
}