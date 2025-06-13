package ios.silv.tdshop.net

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import ios.silv.tdshop.BuildConfig
import ios.silv.tdshop.EncryptedSettingsStore
import ios.silv.tdshop.ui.compose.SafeLaunchedEffect
import ios.silv.tdshop.ui.compose.safeCollectAsState
import ios.silv.tdshop.ui.home.UiProduct
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.client.TerminalClientAsync
import shop.terminal.api.client.okhttp.TerminalOkHttpClientAsync
import shop.terminal.api.models.product.Product

@Inject
class ProductRepo(
    private val shopClient: ShopClient
) {

    private val productsFlow = MutableStateFlow(emptyList<UiProduct>())

    suspend fun refresh() {
        productsFlow.value = shopClient
            .getProductList()
            .getOrThrow()
            .map(::UiProduct)
    }


    fun productsAsFlow(): Flow<List<UiProduct>> {
        return productsFlow.onStart {
            if (productsFlow.value.isEmpty()) {
                refresh()
            }
        }
            .catch {
                logcat { "Failed to refresh in productsFlow() $it" }
                emit(productsFlow.value)
            }
    }

    @Composable
    fun products(): List<UiProduct> {
        val products by productsFlow.safeCollectAsState()

        SafeLaunchedEffect(Unit) {
            if (products.isEmpty()) {
                refresh()
            }
        }

        return products.also { logcat { it.toString() } }
    }
}

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

    val client: TerminalClientAsync get() = requireNotNull(_client)

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

    suspend fun getProductList(): Result<List<Product>> {
        return runCatching {
            withContext(ioDispatcher) {
                client.product().list()
            }
                .data()
        }
    }
}