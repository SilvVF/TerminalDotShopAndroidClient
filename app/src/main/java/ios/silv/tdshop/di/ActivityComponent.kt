package ios.silv.tdshop.di

import ios.silv.tdshop.settingsPresenterProvider
import ios.silv.tdshop.ui.compose.UserMessageStateHolder
import ios.silv.tdshop.ui.home.mainPresenterProvider
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Scope

@ActivityScope
@Component
abstract class MainActivityComponent(
    @Component val appComponent: AppComponent
) {
    abstract val settingsPresenterProvider: settingsPresenterProvider

    abstract val mainPresenterProvider: mainPresenterProvider

    abstract val userMessageStateHolder: UserMessageStateHolder
}

@Scope
annotation class ActivityScope