package ios.silv.tdshop.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.net.ProductRepo
import ios.silv.tdshop.ui.compose.EventEffect
import ios.silv.tdshop.ui.compose.EventFlow
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import ios.silv.tdshop.ui.compose.toColor
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.models.product.Product
import shop.terminal.api.models.product.Product.Subscription

sealed interface MainEvent {
    data object Refresh : MainEvent
    data class ViewProduct(val product: UiProduct) : MainEvent
}

data class UiProduct(
    val id: String,
    val description: String,
    val name: String,
    val variants: List<Variant>,
    val order: Long?,
    val subscription: Subscription?,
    val featured: Boolean,
    val app: String,
    val colorString: String,
    val marketEu: Boolean,
    val marketGlobal: Boolean,
    val marketNa: Boolean,
) {

    data class Variant(
        val name: String,
        val price: Long
    ) {
        val usd = price / 100
    }

    val color = colorString.takeIf { it.isNotEmpty() }?.toColor()

    constructor(product: Product) : this(
        id = product.id(),
        description = product.description(),
        name = product.name(),
        variants = product.variants().filter { it.isValid() }.map {
            Variant(it.name(), it.price())
        },
        order = product.order(),
        subscription = product.subscription(),
        featured = product.tags()?.featured() == true,
        app = product.tags()?.app().orEmpty(),
        colorString = product.tags()?.color().orEmpty(),
        marketNa = product.tags()?.marketNa() == true,
        marketEu = product.tags()?.marketEu() == true,
        marketGlobal = product.tags()?.marketGlobal() == true
    )
}


data class MainState(
    val loading: Boolean,
    val products: List<UiProduct>,
    val selectedProduct: UiProduct?,
) {

    val featured = products.filter { it.featured }.sortedBy { it.order }
    val nonFeatured = products.filterNot { it.featured }.sortedBy { it.order }
}

@Composable
fun mainPresenter(
    eventFlow: EventFlow<MainEvent>,
): MainState = requireActivityComponent().mainPresenterProvider(eventFlow)

typealias mainPresenterProvider = @Composable (eventFlow: EventFlow<MainEvent>) -> MainState

@Composable
@Inject
fun mainPresenterProvider(
    productRepo: ProductRepo,
    @Assisted eventFlow: EventFlow<MainEvent>,
): MainState = providePresenterDefaults { userMessageHolder, backstack ->

    var loading by remember { mutableStateOf(false) }
    val products by rememberUpdatedState(productRepo.products())

    var selectedProduct by remember { mutableStateOf<UiProduct?>(products.firstOrNull()) }

    EventEffect(eventFlow) {
        when (it) {
            MainEvent.Refresh -> {
                loading = true
                productRepo.refresh()
                loading = false
            }

            is MainEvent.ViewProduct -> selectedProduct = it.product
        }
    }

    MainState(
        loading = loading,
        products = products,
        selectedProduct = selectedProduct
    )
}