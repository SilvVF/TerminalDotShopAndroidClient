package ios.silv.tdshop.ui.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.nav.mutableStateStackOf
import ios.silv.tdshop.ui.compose.EventEffect
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import ios.silv.tdshop.ui.compose.rememberEventFlow
import me.tatarka.inject.annotations.Inject

enum class PaymentMethod {
    SSH,
    Browser
}

data class PaymentState(
    val method: PaymentMethod?,
    val events: (PaymentEvent) -> Unit,
)

sealed interface PaymentEvent {
    data class SetMethod(val method: PaymentMethod?): PaymentEvent
}

@Composable
fun paymentPresenter() = requireActivityComponent().paymentPresenterProvider()

typealias paymentPresenterProvider = @Composable () -> PaymentState

@Inject
@Composable
fun paymentPresenterProvider(): PaymentState = providePresenterDefaults { userMessageHolder, backstack ->

    val events = rememberEventFlow<PaymentEvent>()
    var method by rememberSaveable { mutableStateOf<PaymentMethod?>(null) }

    EventEffect(events) { event ->
        when(event) {
            is PaymentEvent.SetMethod -> method = event.method
        }
    }

    PaymentState(
        method = method,
        events = events::tryEmit
    )
}