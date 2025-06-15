package ios.silv.tdshop.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.net.CartRepo
import ios.silv.tdshop.net.ProductRepo
import ios.silv.tdshop.types.UiCart
import ios.silv.tdshop.types.UiProduct
import ios.silv.tdshop.ui.compose.EventEffect
import ios.silv.tdshop.ui.compose.EventFlow
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

sealed interface MainEvent {
    data object Refresh : MainEvent
    data class ViewProduct(val product: UiProduct) : MainEvent
    data class AddToCart(val variantId: String, val qty: Int) : MainEvent
    data class Subscribe(val product: UiProduct, val variant: UiProduct.Variant) : MainEvent
}


data class MainState(
    val loading: Boolean,
    val products: List<UiProduct>,
    val selectedProduct: UiProduct?,
    val cart: UiCart,
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
    cartRepo: CartRepo,
    @Assisted eventFlow: EventFlow<MainEvent>,
): MainState = providePresenterDefaults { userMessageHolder, backstack ->

    val cart = cartRepo.cart()

    val loading = productRepo.loading()
    val products = productRepo.products()

    var selectedProduct by remember { mutableStateOf<UiProduct?>(products.firstOrNull()) }

    EventEffect(eventFlow) { event ->
        when (event) {
            MainEvent.Refresh -> productRepo.refresh()
            is MainEvent.ViewProduct -> selectedProduct = event.product
            is MainEvent.AddToCart -> cartRepo.addItem {
                productVariantId(event.variantId)
                quantity(event.qty.toLong())
            }
            is MainEvent.Subscribe -> TODO()
        }
    }

    MainState(
        loading = loading,
        products = products,
        selectedProduct = selectedProduct,
        cart = cart
    )
}