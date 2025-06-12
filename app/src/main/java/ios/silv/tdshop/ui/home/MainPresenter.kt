package ios.silv.tdshop.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import ios.silv.tdshop.SettingsState
import ios.silv.tdshop.di.requireAppComponent
import ios.silv.tdshop.net.ShopClient
import ios.silv.tdshop.settingsPresenterProvider
import ios.silv.tdshop.ui.compose.EventEffect
import ios.silv.tdshop.ui.compose.EventFlow
import ios.silv.tdshop.ui.compose.SafeLaunchedEffect
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

sealed interface MainEvent {
    data object Refresh : MainEvent
}

data class MainState(
    val loading: Boolean,
    val products: List<String>,
    val settingsState: SettingsState,
)

@Composable
fun mainPresenter(
    eventFlow: EventFlow<MainEvent>,
): MainState = requireAppComponent().mainPresenterProvider(eventFlow)

typealias mainPresenterProvider = @Composable (eventFlow: EventFlow<MainEvent>) -> MainState

@Composable
@Inject
fun mainPresenterProvider(
    client: ShopClient,
    settingsPresenterProvider: Lazy<settingsPresenterProvider>,
    @Assisted eventFlow: EventFlow<MainEvent>,
): MainState = providePresenterDefaults { userMessageHolder, backstack ->

    val settingsState = settingsPresenterProvider.value.invoke()

    var fetchId by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(false) }
    var products by remember { mutableStateOf(emptyList<String>()) }

    SafeLaunchedEffect(Unit) {
        loading = true
        val names = client.getProductList().map { it.name() }

        Snapshot.withMutableSnapshot {
            loading = false
            products = names
        }
    }

    EventEffect(eventFlow) {
        when (it) {
            MainEvent.Refresh -> fetchId++
        }
    }

    MainState(
        loading = loading,
        products = products,
        settingsState = settingsState
    )
}