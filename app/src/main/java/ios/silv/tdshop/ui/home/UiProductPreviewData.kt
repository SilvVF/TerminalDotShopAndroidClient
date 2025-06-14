package ios.silv.tdshop.ui.home

import ios.silv.tdshop.BuildConfig
import ios.silv.tdshop.types.UiCart
import ios.silv.tdshop.types.UiCart.Amount
import ios.silv.tdshop.types.UiCart.Ship
import ios.silv.tdshop.types.UiCartItem
import ios.silv.tdshop.types.UiProduct
import ios.silv.tdshop.types.UiProduct.Variant
import shop.terminal.api.models.product.Product.Subscription

val cartPreviewData = UiCart(
    amount = Amount(
        subtotal = 11_000,
        shipping = null,
        total = 11_000
    ),
    items = listOf(
        UiCartItem(
            id = "itm_01JXNMETR6Y6T1R5JHKFYB4XP9",
            productVariantId = "var_01J1JFDMNBXB5GJCQF6C3AEBCQ",
            quantity = 1,
            subtotal = 2_200
        ),
        UiCartItem(
            id = "itm_01JXNMF2R4PKAF9YN3MGXZPFS2",
            productVariantId = "var_01J1JFE53306NT180RC4HGPWH8",
            quantity = 1,
            subtotal = 2_200
        ),
        UiCartItem(
            id = "itm_01JXNMTNM5BS7097FB022PZSWK",
            productVariantId = "var_01J1JFF4D5PBGT0W2RJ7FREHRR",
            quantity = 1,
            subtotal = 2_200
        ),
        UiCartItem(
            id = "itm_01JXNMQDP1EKFR7KQ07PV7FQFT",
            productVariantId = "var_01J5RJYPPNQMBANJ8XKPF4GV00",
            quantity = 1,
            subtotal = 2_200
        ),
        UiCartItem(
            id = "itm_01JXNMNNVHJ6F2AQKZ20TJ1QJH",
            productVariantId = "var_01JNH7GTF9FBA62Y0RT0WMK3BT",
            quantity = 1,
            subtotal = 2_200
        )
    ),
    subtotal = 11_000,
    addressId = null,
    cardId = null,
    shipping = Ship(
        service = null,
        timeframe = null
    ),
    initialized = true
)


val previewUiProducts = listOf(
    UiProduct(
        id = "prd_01JD0E7PD4H3XDZA5P5VXSDPQC",
        description = "Subscribe to cron, our monthly coffee subscription. Each month you'll receive a special flavor-of-the-month blend - first box ships Feb 1st. And now we can add our MRR to the Terminal X bio!",
        name = "cron",
        variants = listOf(
            Variant(name = "12oz", price = 3000, id = "var_01JD0E87SB7K9MB5KGFPVJ1N7A")
        ),
        order = 1,
        subscription = Subscription.REQUIRED,
        featured = true,
        app = "",
        colorString = "",
        marketEu = false,
        marketGlobal = false,
        marketNa = true
    ),
    UiProduct(
        id = "prd_01JNH7GKWYRHX45GPRZS3M7A4X",
        description = "A light roast from the Sidama region of Ethiopia. Featuring notes of red berries, tropical fruits, and caramel, this is the best way to get in the flow — the perfect state of productivity.",
        name = "flow",
        variants = listOf(
            Variant(name = "12oz", price = 2200, id = "var_01JNH7GTF9FBA62Y0RT0WMK3BT")
        ),
        order = 1,
        subscription = null,
        featured = true,
        app = "raycast",
        colorString = "#000000",
        marketEu = false,
        marketGlobal = false,
        marketNa = false
    ),
    UiProduct(
        id = "prd_01J5RJY32F6SP103Z19TSSFCK5",
        description = "Working the backend and frontend of the palette, this new artisinal infusion is a collaboration with Laravel from Colombia and Papua New Guinea and is full stack in flavour and texture.",
        name = "artisan",
        variants = listOf(
            Variant(name = "Hybrid | 12oz | Whole Beans", price = 2200, id = "var_01J5RJYPPNQMBANJ8XKPF4GV00")
        ),
        order = 2,
        subscription = Subscription.ALLOWED,
        featured = true,
        app = "",
        colorString = "#EB4432",
        marketEu = false,
        marketGlobal = false,
        marketNa = true
    ),
    UiProduct(
        id = "prd_01J1JFDYPMKTESS6FQFMTTMJYK",
        description = "The interpolation of Caturra and Castillo varietals from Las Cochitas creates this refreshing citrusy and complex coffee.",
        name = "citrus blend",
        variants = listOf(
            Variant(name = "Light Roast | 12oz | Whole Beans", price = 2200, id = "var_01J1JFE53306NT180RC4HGPWH8")
        ),
        order = 100,
        subscription = Subscription.ALLOWED,
        featured = false,
        app = "",
        colorString = "#F5BB1D",
        marketEu = false,
        marketGlobal = false,
        marketNa = true
    ),
    UiProduct(
        id = "prd_01J1JFDEE3MYS0TA1G0SKQ46CM",
        description = "A savory yet sweet blend created from a natural fault in the coffee cherry that causes it to develop one bean instead of two.",
        name = "segfault",
        variants = listOf(
            Variant(name = "Medium Roast | 12oz | Whole Beans", price = 2200, id = "var_01J1JFDMNBXB5GJCQF6C3AEBCQ")
        ),
        order = 200,
        subscription = Subscription.ALLOWED,
        featured = false,
        app = "",
        colorString = "#169FC1",
        marketEu = false,
        marketGlobal = false,
        marketNa = true
    ),
    UiProduct(
        id = "prd_01J1JFEYGWX8RH8E1T9E087ZYQ",
        description = "A dark roast from the Cerrado region of Brazil, an expansive lush and tropical savanna that creates a dark chocolate blend with hints of almond.",
        name = "dark mode",
        variants = listOf(
            Variant(name = "Dark Roast | 12oz | Whole Beans", price = 2200, id = "var_01J1JFF4D5PBGT0W2RJ7FREHRR")
        ),
        order = 300,
        subscription = Subscription.ALLOWED,
        featured = false,
        app = "",
        colorString = "#118B39",
        marketEu = false,
        marketGlobal = false,
        marketNa = true
    ),
    UiProduct(
        id = "prd_01J1JFEG67PXYK510FHV3VQW9P",
        description = "A flavorful decaf coffee processed in the mountain waters of Brazil to create a dark chocolatey blend.",
        name = "404",
        variants = listOf(
            Variant(name = "Decaf | 12oz | Whole Beans", price = 2200, id = "var_01J1JFEP8WXK5MKXNBTR2FJ1YC")
        ),
        order = 400,
        subscription = Subscription.ALLOWED,
        featured = false,
        app = "",
        colorString = "#D53C81",
        marketEu = false,
        marketGlobal = false,
        marketNa = true
    )
)


