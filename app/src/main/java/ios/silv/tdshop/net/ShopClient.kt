package ios.silv.tdshop.net

import ios.silv.tdshop.BuildConfig
import ios.silv.tdshop.EncryptedSettingsStore
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
import shop.terminal.api.models.product.Product

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