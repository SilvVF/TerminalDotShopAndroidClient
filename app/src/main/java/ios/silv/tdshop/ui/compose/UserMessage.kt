package ios.silv.tdshop.ui.compose

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

data class UserMessage(
    val message: String,
    val actionLabel: String? = null,
    val duration: SnackbarDuration? = null,
    val id: Int = UUID.randomUUID().hashCode(),
    val userMessageResult: UserMessageResult? = null,
)

class UserMessageStateHolderImpl : UserMessageStateHolder {

    private val _messages = mutableStateListOf<UserMessage>()
    override val messages get() = _messages

    val snackBarHostState = SnackbarHostState()

    override suspend fun showMessage(
        message: String,
        actionLabel: String?,
        duration: SnackbarDuration?
    ): UserMessageResult {
        val msg = UserMessage(message, actionLabel = actionLabel, duration = duration)
        _messages.add(msg)

        val result = if (msg.duration == null) {
            snackBarHostState.showSnackbar(
                msg.message,
                msg.actionLabel
            )
        } else {
            snackBarHostState.showSnackbar(
                msg.message,
                msg.actionLabel,
                duration = msg.duration
            )
        }

        return when (result) {
            SnackbarResult.Dismissed -> UserMessageResult.Dismissed
            SnackbarResult.ActionPerformed -> UserMessageResult.ActionPerformed
        }
            .also { messages.remove(msg) }
    }
}

enum class UserMessageResult {
    Dismissed,
    ActionPerformed,
}

interface UserMessageStateHolder {
    val messages: List<UserMessage>
    suspend fun showMessage(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration? = null,
    ): UserMessageResult
}