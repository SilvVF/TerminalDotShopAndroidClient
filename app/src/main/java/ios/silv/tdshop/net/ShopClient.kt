package ios.silv.tdshop.net

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import shop.terminal.api.client.TerminalClientAsync
import shop.terminal.api.models.product.Product

class ShopClient(
    private val ioDispatcher: CoroutineDispatcher
) {

    private var _client: TerminalClientAsync? = null
        set(value) {
            field?.close()
            field = value
        }

    val client: TerminalClientAsync get() = requireNotNull(_client)

    suspend fun getProductList(): List<Product> {
        return withContext(ioDispatcher) {
            client.product().list()
        }
            .data()
    }
}