package ios.silv.tdshop.di

import android.content.Context
import ios.silv.tdshop.EncryptedSettingsStore
import ios.silv.tdshop.SettingsStore
import ios.silv.tdshop.encryptedStore
import ios.silv.tdshop.net.CartRepo
import ios.silv.tdshop.net.PaymentRepo
import ios.silv.tdshop.net.ProductRepo
import ios.silv.tdshop.net.ShopClient
import ios.silv.tdshop.settingsPresenterProvider
import ios.silv.tdshop.settingsStore
import ios.silv.tdshop.ui.cart.cartPresenterProvider
import ios.silv.tdshop.ui.compose.UserMessageStateHolder
import ios.silv.tdshop.ui.compose.UserMessageStateHolderImpl
import ios.silv.tdshop.ui.home.mainPresenterProvider
import ios.silv.tdshop.ui.ship.shipPresenterProvider
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import kotlin.annotation.AnnotationTarget.*

@AppScope
@Component
abstract class AppComponent(
    @get:Provides val context: Context
) {
    @AppScope
    @Provides
    fun provideCartRepo(shopClient: ShopClient): CartRepo = CartRepo(shopClient)

    @AppScope
    @Provides
    fun provideProductRepo(shopClient: ShopClient): ProductRepo = ProductRepo(shopClient)

    @AppScope
    @Provides
    fun providePaymentRepo(shopClient: ShopClient): PaymentRepo = PaymentRepo(shopClient)

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
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class AppScope