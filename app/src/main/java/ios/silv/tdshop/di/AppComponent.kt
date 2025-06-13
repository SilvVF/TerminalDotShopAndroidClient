package ios.silv.tdshop.di

import android.content.Context
import ios.silv.tdshop.EncryptedSettingsStore
import ios.silv.tdshop.SettingsStore
import ios.silv.tdshop.encryptedStore
import ios.silv.tdshop.net.ProductRepo
import ios.silv.tdshop.net.ShopClient
import ios.silv.tdshop.settingsStore
import ios.silv.tdshop.ui.compose.UserMessageStateHolder
import ios.silv.tdshop.ui.compose.UserMessageStateHolderImpl
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope

@AppScope
@Component
abstract class AppComponent(
    @get:Provides val context: Context
) {
    @AppScope
    @Provides
    fun provideProductRepo(shopClient: ShopClient): ProductRepo = ProductRepo(shopClient)

    @AppScope
    @Provides
    fun providerUserMessageStateHolder(): UserMessageStateHolder = UserMessageStateHolderImpl()

    @AppScope
    @Provides
    fun provideShopClient(store: EncryptedSettingsStore): ShopClient = ShopClient(store, Dispatchers.IO,)

    @AppScope
    @Provides
    fun provideSettingsStore(context: Context): SettingsStore = SettingsStore(context.settingsStore)

    @AppScope
    @Provides
    fun provideEncryptedSettingsStore(context: Context): EncryptedSettingsStore = EncryptedSettingsStore(context.encryptedStore)
}

@Scope
annotation class AppScope