package ios.silv.tdshop.ui.cart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.net.CartRepo
import ios.silv.tdshop.net.ProductRepo
import ios.silv.tdshop.types.UiCart
import ios.silv.tdshop.types.UiCartItem
import ios.silv.tdshop.types.UiProduct
import ios.silv.tdshop.ui.compose.EventEffect
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import ios.silv.tdshop.ui.compose.rememberEventFlow
import kotlinx.coroutines.flow.combine
import me.tatarka.inject.annotations.Inject

typealias GroupedItem = Triple<UiCartItem, UiProduct, UiProduct.Variant>

data class CartState(
    val cart: UiCart,
    val selectedItem: GroupedItem?,
    val products: List<UiProduct>,
    val groupedItems: List<GroupedItem>,
    val events: (CartEvent) -> Unit
) {

}

sealed interface CartEvent {
    data class DecQty(val item: UiCartItem) : CartEvent
    data class IncQty(val item: UiCartItem) : CartEvent
    data class Remove(val item: UiCartItem) : CartEvent
    data object Clear : CartEvent
    data class ViewProduct(val item: GroupedItem?) : CartEvent
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

    val cart by rememberUpdatedState(cartRepo.cart())
    val products by rememberUpdatedState(productRepo.products())

    var selectedVariantId by remember { mutableStateOf<String?>(null) }

    val groupedItems by produceState(emptyList()) {
        combine(
            snapshotFlow { products },
            snapshotFlow { cart },
            ::Pair,
        ).collect { (products, cart) ->
            val variantIdToProducts = products
                .map { p -> p.variants.map { v -> p to v } }
                .flatten()
                .groupBy { it.second.id }
                .mapValues { it.value.first() }

            value = cart.items.mapNotNull { item ->
                val (product, variant) = variantIdToProducts[item.productVariantId]
                    ?: return@mapNotNull null
                Triple(
                    item, product, variant
                )
            }
        }
    }

    val selectedItem by remember {
        derivedStateOf {
            groupedItems.firstOrNull { it.first.productVariantId == selectedVariantId }
        }
    }


    EventEffect(events) { event ->
        when (event) {
            CartEvent.Clear -> cartRepo.clear()
            is CartEvent.DecQty -> cartRepo.addItem {
                productVariantId(event.item.productVariantId)
                quantity((event.item.quantity - 1).coerceAtLeast(0))
            }

            is CartEvent.IncQty -> cartRepo.addItem {
                productVariantId(event.item.productVariantId)
                quantity(event.item.quantity + 1)
            }

            is CartEvent.Remove -> cartRepo.addItem {
                productVariantId(event.item.productVariantId)
                quantity(0)
            }

            is CartEvent.ViewProduct -> selectedVariantId = event.item?.first?.productVariantId
        }
    }

    CartState(
        cart = cart,
        products = products,
        selectedItem = selectedItem,
        groupedItems = groupedItems,
    ) {
        events.tryEmit(it)
    }
}