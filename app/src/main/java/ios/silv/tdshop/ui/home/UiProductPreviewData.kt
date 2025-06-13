package ios.silv.tdshop.ui.home

import ios.silv.tdshop.BuildConfig
import shop.terminal.api.models.product.Product.Subscription

val previewUiProducts = if (!BuildConfig.DEBUG) emptyList() else listOf(
    UiProduct(
        id = "prd_01JD0E7PD4H3XDZA5P5VXSDPQC",
        description = "Subscribe to cron, our monthly coffee subscription. Each month you'll receive a special flavor-of-the-month blend - first box ships Feb 1st. And now we can add our MRR to the Terminal X bio!",
        name = "cron",
        variants = listOf(
            UiProduct.Variant(name = "12oz", price = 3000)
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
            UiProduct.Variant(name = "12oz", price = 2200)
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
            UiProduct.Variant(name = "Hybrid | 12oz | Whole Beans", price = 2200)
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
        name = "origin",
        variants = listOf(
            UiProduct.Variant(name = "Light Roast | 12oz | Whole Beans", price = 2200)
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
            UiProduct.Variant(name = "Medium Roast | 12oz | Whole Beans", price = 2200)
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
            UiProduct.Variant(name = "Dark Roast | 12oz | Whole Beans", price = 2200)
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
            UiProduct.Variant(name = "Decaf | 12oz | Whole Beans", price = 2200)
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

