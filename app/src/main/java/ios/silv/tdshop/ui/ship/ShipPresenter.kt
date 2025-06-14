package ios.silv.tdshop.ui.ship

import androidx.compose.runtime.Composable
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import me.tatarka.inject.annotations.Inject

data class ShipState(
    val address: String
)

sealed interface ShipEvent

@Composable
fun shipPresenter() = requireActivityComponent().shipPresenterProvider()

typealias shipPresenterProvider = @Composable () -> ShipState

@Inject
@Composable
fun shipPresenterProvider(): ShipState = providePresenterDefaults { userMessageHolder, backstack ->

    ShipState("")
}