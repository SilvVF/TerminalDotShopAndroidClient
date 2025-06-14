package ios.silv.tdshop.net

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import ios.silv.tdshop.di.AppScope
import ios.silv.tdshop.types.UiProduct
import ios.silv.tdshop.ui.compose.SafeLaunchedEffect
import ios.silv.tdshop.ui.compose.safeCollectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import logcat.logcat
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.errors.TerminalInvalidDataException

@AppScope
@Inject
class ProductRepo(
    private val shopClient: ShopClient
) {

    private val loadingFlow = MutableStateFlow(false)
    private val productsFlow = MutableStateFlow(emptyList<UiProduct>())

    @Throws(TerminalInvalidDataException::class)
    suspend fun refresh() {
        loadingFlow.value = true
        productsFlow.value = shopClient
            .getProductList()
            .also { loadingFlow.value = false }
            .getOrThrow()
            .map(::UiProduct)
            .also { logcat { "refreshed products $it" } }
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
    fun loading(): Boolean {
        val loading by loadingFlow.safeCollectAsState()
        return loading
    }

    @Composable
    fun products(): List<UiProduct> {
        val products by productsFlow.safeCollectAsState()

        SafeLaunchedEffect(Unit) {
            if (products.isEmpty()) {
                refresh()
            }
        }

        return products
    }
}