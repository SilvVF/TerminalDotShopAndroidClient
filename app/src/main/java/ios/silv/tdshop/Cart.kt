package ios.silv.tdshop

import ios.silv.tdshop.net.ShopClient
import me.tatarka.inject.annotations.Inject


@Inject
class CartRepo(
    private val shopClient: ShopClient
) {


}