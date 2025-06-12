package ios.silv.tdshop.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import ios.silv.tdshop.SettingsState
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.net.ShopClient
import ios.silv.tdshop.settingsPresenterProvider
import ios.silv.tdshop.ui.compose.EventEffect
import ios.silv.tdshop.ui.compose.EventFlow
import ios.silv.tdshop.ui.compose.SafeLaunchedEffect
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import logcat.asLog
import logcat.logcat
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.core.JsonField
import shop.terminal.api.core.JsonValue
import shop.terminal.api.models.product.Product
import shop.terminal.api.models.product.Product.Subscription
import shop.terminal.api.models.product.Product.Tags
import shop.terminal.api.models.product.ProductVariant

sealed interface MainEvent {
    data object Refresh : MainEvent
    data class ViewProduct(val product: UiProduct) : MainEvent
}

data class UiProduct(
    val id: String,
    val description: String,
    val name: String,
    val variants: List<ProductVariant>,
    val order: Long?,
    val subscription: Subscription?,
    val tags: Tags?,
    val additionalProperties: Map<String, JsonValue>
) {

    constructor(product: Product) : this(
        id = product.id(),
        description = product.description(),
        name = product.name(),
        variants = product.variants(),
        order = product.order(),
        subscription = product.subscription(),
        tags = product.tags(),
        additionalProperties = product._additionalProperties()
    )
}


data class MainState(
    val loading: Boolean,
    val products: List<UiProduct>,
    val selectedProduct: UiProduct?,
    val settingsState: SettingsState,
)

@Composable
fun mainPresenter(
    eventFlow: EventFlow<MainEvent>,
): MainState = requireActivityComponent().mainPresenterProvider(eventFlow)

typealias mainPresenterProvider = @Composable (eventFlow: EventFlow<MainEvent>) -> MainState

@Composable
@Inject
fun mainPresenterProvider(
    client: ShopClient,
    settingsPresenterProvider: Lazy<settingsPresenterProvider>,
    @Assisted eventFlow: EventFlow<MainEvent>,
): MainState = providePresenterDefaults { userMessageHolder, backstack ->

    val settingsState = settingsPresenterProvider.value()

    var fetchId by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(false) }
    var products by remember { mutableStateOf(emptyList<UiProduct>()) }

    var selectedProduct by remember { mutableStateOf<UiProduct?>(null) }

    SafeLaunchedEffect(fetchId) {
        loading = true
        val results = client
            .getProductList()
            .onFailure { logcat { it.asLog() } }
            .getOrDefault(emptyList<Product>())
            .map { product ->
                UiProduct(product)
            }

        logcat { "received $results" }

        Snapshot.withMutableSnapshot {
            loading = false
            products = results
            selectedProduct = results.firstOrNull()
        }
    }

    EventEffect(eventFlow) {
        when (it) {
            MainEvent.Refresh -> fetchId++
            is MainEvent.ViewProduct -> selectedProduct = it.product
        }
    }

    MainState(
        loading = loading,
        products = products,
        settingsState = settingsState,
        selectedProduct = selectedProduct
    )
}