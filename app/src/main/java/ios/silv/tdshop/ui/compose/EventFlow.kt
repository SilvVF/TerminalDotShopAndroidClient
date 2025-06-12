package ios.silv.tdshop.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@Stable
class EventFlow<T>(
    extraBufferCapacity: Int = 20,
): MutableSharedFlow<T> by MutableSharedFlow(extraBufferCapacity = extraBufferCapacity)

@Composable
fun <T> rememberEventFlow(): EventFlow<T> {
    return remember { EventFlow() }
}

@Composable
fun <EVENT> EventEffect(
    eventFlow: EventFlow<EVENT>,
    block: suspend CoroutineScope.(EVENT) -> Unit,
) {
    SafeLaunchedEffect(eventFlow) {
        supervisorScope {
            eventFlow.collect { event ->
                launch {
                    block(event)
                }
            }
        }
    }
}