package ios.silv.tdshop.ui.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalUriHandler
import ios.silv.tdshop.di.requireActivityComponent
import ios.silv.tdshop.nav.mutableStateStackOf
import ios.silv.tdshop.net.PaymentRepo
import ios.silv.tdshop.ui.compose.EventEffect
import ios.silv.tdshop.ui.compose.providePresenterDefaults
import ios.silv.tdshop.ui.compose.rememberEventFlow
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.models.card.Card
import shop.terminal.api.models.card.CardCreateParams
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

enum class PaymentMethod {
    SSH,
    Browser
}

data class PaymentState(
    val method: PaymentMethod?,
    val regenerateTimer: Int = -1,
    val canRegenerate: Boolean = true,
    val url: String? = null,
    val cards: List<Card> = emptyList(),
    val events: (PaymentEvent) -> Unit,
)

sealed interface PaymentEvent {
    data class SetMethod(val method: PaymentMethod?) : PaymentEvent
    data object RefreshUrl : PaymentEvent
    data object PayByUrl : PaymentEvent
}

@Composable
fun paymentPresenter() = requireActivityComponent().paymentPresenterProvider()

typealias paymentPresenterProvider = @Composable () -> PaymentState

@Inject
@Composable
fun paymentPresenterProvider(
    paymentRepo: PaymentRepo,
): PaymentState = providePresenterDefaults { userMessageHolder, backstack ->

    val uriHandler = LocalUriHandler.current
    val events = rememberEventFlow<PaymentEvent>()
    var method by rememberSaveable { mutableStateOf<PaymentMethod?>(null) }
    val cards = paymentRepo.cards()

    val url by rememberUpdatedState(
        if (method == PaymentMethod.Browser) {
            paymentRepo.collectUrl()
        } else {
            null
        }
    )

    val genTimeout by produceState(0) {
        paymentRepo.lastGeneratedTs.collectLatest { ts ->
            val timeoutSeconds = paymentRepo.timeout.inWholeSeconds
            while (isActive) {
                val timeSinceLast = Clock.System.now().epochSeconds - ts
                value = (timeoutSeconds - timeSinceLast)
                    .coerceIn(0..timeoutSeconds)
                    .toInt()

                if (value <= 0) {
                    return@collectLatest
                }

                delay(1000)
            }
        }
    }

    val canRegenerate by remember {
        derivedStateOf { genTimeout == 0 }
    }

    EventEffect(events) { event ->
        when (event) {
            is PaymentEvent.SetMethod -> method = event.method
            is PaymentEvent.RefreshUrl -> paymentRepo.refreshCollectUrl()
            is PaymentEvent.PayByUrl -> {
                uriHandler.openUri(url.orEmpty())
            }
        }
    }

    PaymentState(
        method = method,
        url = url,
        cards = cards,
        regenerateTimer = genTimeout,
        canRegenerate = canRegenerate,
        events = events::tryEmit
    )
}