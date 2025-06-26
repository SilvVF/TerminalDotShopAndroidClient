package ios.silv.tdshop.net

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ios.silv.tdshop.di.AppScope
import ios.silv.tdshop.ui.compose.safeCollectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.runtime.getValue
import ios.silv.tdshop.BuildConfig
import ios.silv.tdshop.ui.compose.SafeLaunchedEffect
import me.tatarka.inject.annotations.Inject
import shop.terminal.api.models.card.Card
import java.time.LocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@AppScope
@Inject
class PaymentRepo(
    private val client: ShopClient,
) {

    val timeout = 30.seconds
    val collectUrlFlow = MutableStateFlow<String?>(
        if (BuildConfig.DEBUG) "https://dev.trm.sh/u7_38kt2" else null
    )

    val lastGeneratedTs = MutableStateFlow<Long>(0L)
    val cardsFlow = MutableStateFlow(emptyList<Card>())

    @OptIn(ExperimentalTime::class)
    suspend fun refreshCollectUrl() {
        val secsSinceLast = Clock.System.now().epochSeconds - lastGeneratedTs.value
        if (secsSinceLast < timeout.inWholeSeconds) {
            val remaining = 30 - secsSinceLast
            error("must wait $remaining before generating")
        }

        val url = client
            .collectCard()
            .getOrThrow()
            .url()

        lastGeneratedTs.value = Clock.System.now().epochSeconds
        collectUrlFlow.value = url
    }

    suspend fun refreshCards() {
        val cards = client.cardList().getOrThrow()
        cardsFlow.value = cards
    }

    @Composable
    fun cards(): List<Card> {
        val cards by cardsFlow.safeCollectAsState()

        SafeLaunchedEffect(Unit) {
            if (cards.isEmpty()) {
                refreshCards()
            }
        }

        return cards
    }

    @Composable
    fun collectUrl(): String? {
        val url by collectUrlFlow.safeCollectAsState()

        SafeLaunchedEffect(Unit) {
            if (url == null) {
                refreshCollectUrl()
            }
        }

        return url
    }
}
