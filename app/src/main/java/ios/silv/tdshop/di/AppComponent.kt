package ios.silv.tdshop.di

import android.content.Context
import ios.silv.tdshop.SettingsStore
import ios.silv.tdshop.net.ShopClient
import ios.silv.tdshop.settingsPresenterProvider
import ios.silv.tdshop.settingsStore
import ios.silv.tdshop.ui.compose.UserMessageStateHolder
import ios.silv.tdshop.ui.compose.UserMessageStateHolderImpl
import ios.silv.tdshop.ui.home.mainPresenterProvider
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides


@Component
abstract class AppComponent(
    private val context: Context
) {

    abstract val mainPresenterProvider: mainPresenterProvider
    abstract val settingsPresenterProvider: settingsPresenterProvider
    abstract val userMessageStateHolder: UserMessageStateHolder

    @Provides
    fun providerUserMessageStateHolder(): UserMessageStateHolder = UserMessageStateHolderImpl()

    @Provides
    fun provideShopClient(): ShopClient = ShopClient(Dispatchers.IO)

    @Provides
    fun provideSettingsStore(): SettingsStore = SettingsStore(context.settingsStore)
}