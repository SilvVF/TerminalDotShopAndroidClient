package ios.silv.tdshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import ios.silv.tdshop.di.MainActivityComponent
import ios.silv.tdshop.di.create
import ios.silv.tdshop.nav.Home
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.nav.rememberStateStack
import ios.silv.tdshop.ui.cart.cartScreenEntry
import ios.silv.tdshop.ui.home.mainScreenEntry
import ios.silv.tdshop.ui.payment.paymentEntry
import ios.silv.tdshop.ui.ship.addShipDestEntry
import ios.silv.tdshop.ui.ship.shipScreenEntry
import ios.silv.tdshop.ui.theme.TdshopTheme
import ios.silv.term_ui.LocalSharedTransitionScope
import kotlin.math.min

class MainActivity : ComponentActivity() {

    lateinit var mainComponent: MainActivityComponent

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainComponent = MainActivityComponent::class.create((application as App).appComponent)

        enableEdgeToEdge()
        setContent {

            val backStack = rememberStateStack<Screen>(Home, minSize = 1)

            TdshopTheme {
                Surface {
                    SharedTransitionLayout {
                        CompositionLocalProvider(
                            LocalBackStack provides backStack,
                            LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        ) {
                            NavDisplay(
                                backStack = backStack.items,
                                entryDecorators = listOf(
                                    // Add the default decorators for managing scenes and saving state
                                    rememberSceneSetupNavEntryDecorator(),
                                    rememberSavedStateNavEntryDecorator(),
                                ),
                                onBack = { backStack.pop() },
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                predictivePopTransitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                entryProvider = entryProvider {
                                    mainScreenEntry(
                                        sharedTransitionScope = this@SharedTransitionLayout
                                    )
                                    cartScreenEntry(
                                        sharedTransitionScope = this@SharedTransitionLayout
                                    )
                                    shipScreenEntry(
                                        sharedTransitionScope = this@SharedTransitionLayout
                                    )
                                    addShipDestEntry(
                                        sharedTransitionScope = this@SharedTransitionLayout
                                    )
                                    paymentEntry(
                                        sharedTransitionScope = this@SharedTransitionLayout
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
