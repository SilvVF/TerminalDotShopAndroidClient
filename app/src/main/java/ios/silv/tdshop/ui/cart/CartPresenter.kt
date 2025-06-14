package ios.silv.tdshop.ui.cart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.net.CartRepo
import ios.silv.tdshop.net.ProductRepo
import ios.silv.tdshop.types.UiCart
import ios.silv.tdshop.types.UiCartItem
import ios.silv.tdshop.types.UiProduct
import ios.silv.tdshop.ui.compose.EventEffect
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import ios.silv.tdshop.ui.compose.rememberEventFlow
import me.tatarka.inject.annotations.Inject

data class CartState(
    val cart: UiCart,
    val products: List<UiProduct>,
    val events: (CartEvent) -> Unit
) {

    val variantIdToProduct: Map<String, Pair<UiProduct, UiProduct.Variant>> = products
        .map { p -> p.variants.map { v -> p to v } }
        .flatten()
        .groupBy { it.second.id }
        .mapValues { it.value.first() }

    val groupedItems: List<Triple<UiCartItem, UiProduct, UiProduct.Variant>> =
        cart.items.mapNotNull { item ->
            val (product, variant) = variantIdToProduct[item.productVariantId]
                ?: return@mapNotNull null
            Triple(
                item, product, variant
            )
        }
}

sealed interface CartEvent {
    data class DecQty(val item: UiCartItem) : CartEvent
    data class IncQty(val item: UiCartItem) : CartEvent
    data class Remove(val item: UiCartItem) : CartEvent
}

@Composable
fun cartPresenter(): CartState = requireActivityComponent().cartPresenterProvider()

typealias cartPresenterProvider = @Composable () -> CartState

@Inject
@Composable
fun cartPresenterProvider(
    cartRepo: CartRepo,
    productRepo: ProductRepo
): CartState = providePresenterDefaults { userMessageHolder, backstack ->

    val events = rememberEventFlow<CartEvent>()

    val cart = cartRepo.cart()
    val products = productRepo.products()

    EventEffect(events) {
        when (it) {
            else -> {

            }
        }
    }

    CartState(
        cart = cart,
        products = products
    ) {
        events.tryEmit(it)
    }
}