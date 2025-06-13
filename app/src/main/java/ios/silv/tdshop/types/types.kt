package ios.silv.tdshop.types

import ios.silv.tdshop.ui.compose.toColor
import shop.terminal.api.models.cart.Cart
import shop.terminal.api.models.cart.Cart.Item
import shop.terminal.api.models.product.Product
import shop.terminal.api.models.product.Product.Subscription

data class UiCart(
    val amount: Amount = Amount(),
    val items: List<UiCartItem> = emptyList(),
    val subtotal: Long = 0,
    val addressId: String? = null,
    val cardId: String? = null,
    val shipping: Ship? = null,
    val initialized: Boolean = false,
) {
    data class Amount(
        val subtotal: Long = 0,
        val shipping: Long? = null,
        val total: Long? = null,
    )

    data class Ship(
        val service: String? = null,
        val timeframe: String? = null,
    )

    constructor(cart: Cart) : this(
        amount = Amount(
            cart.amount().subtotal(),
            cart.amount().shipping(),
            cart.amount().total()
        ),
        items = cart.items().map(::UiCartItem),
        subtotal = cart.subtotal(),
        addressId = cart.addressId(),
        cardId = cart.cardId(),
        shipping = cart.shipping()?.let {
            Ship(
                service = it.service(),
                timeframe = it.timeframe(),
            )
        },
        initialized = true
    )
}

data class UiCartItem(
    val id: String,
    val productVariantId: String,
    val quantity: Long,
    val subtotal: Long,
) {

    constructor(item: Item) : this(
        id = item.id(),
        productVariantId = item.productVariantId(),
        quantity = item.quantity(),
        subtotal = item.subtotal(),
    )
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
        val price: Long,
        val id: String,
    ) {
        val usd = price / 100
    }

    val color = colorString.takeIf { it.isNotEmpty() }?.toColor()

    constructor(product: Product) : this(
        id = product.id(),
        description = product.description(),
        name = product.name(),
        variants = product.variants().filter { it.isValid() }.map {
            Variant(it.name(), it.price(), it.id())
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