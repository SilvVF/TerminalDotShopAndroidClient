package ios.silv.tdshop.di

import ios.silv.tdshop.settingsPresenterProvider
import ios.silv.tdshop.ui.cart.cartPresenterProvider
import ios.silv.tdshop.ui.compose.UserMessageStateHolder
import ios.silv.tdshop.ui.home.mainPresenterProvider
import ios.silv.tdshop.ui.ship.shipPresenterProvider
import ios.silv.tdshop.ui.ship.shipSelectPresenterProvider
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Scope

@ActivityScope
@Component
abstract class MainActivityComponent(
    @Component val appComponent: AppComponent
) {
    abstract val cartPresenterProvider: cartPresenterProvider

    abstract val settingsPresenterProvider: settingsPresenterProvider

    abstract val mainPresenterProvider: mainPresenterProvider

    abstract val userMessageStateHolder: UserMessageStateHolder

    abstract val shipPresenterProvider: shipPresenterProvider

    abstract val shipSelectPresenterProvider: shipSelectPresenterProvider
}

@Scope
annotation class ActivityScope