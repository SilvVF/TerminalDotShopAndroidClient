package ios.silv.tdshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import ios.silv.tdshop.ui.home.mainScreenEntry
import ios.silv.tdshop.ui.theme.TdshopTheme

class MainActivity : ComponentActivity() {

    lateinit var mainComponent: MainActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainComponent = MainActivityComponent::class.create((application as App).appComponent)

        enableEdgeToEdge()
        setContent {

            val backStack = rememberStateStack<Screen>(Home)

            CompositionLocalProvider(
                LocalBackStack provides backStack
            ) {
                TdshopTheme {
                    NavDisplay(
                        backStack = backStack.items,
                        entryDecorators = listOf(
                            // Add the default decorators for managing scenes and saving state
                            rememberSceneSetupNavEntryDecorator(),
                            rememberSavedStateNavEntryDecorator(),
                        ),
                        onBack = { backStack.pop() },
                        entryProvider = entryProvider {
                            mainScreenEntry()
                        }
                    )
                }
            }
        }
    }
}
