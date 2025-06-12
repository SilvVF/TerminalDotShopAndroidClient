package ios.silv.tdshop.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import ios.silv.tdshop.R
import ios.silv.tdshop.di.requireAppComponent
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.nav.SnapshotStateStack
import ios.silv.tdshop.ui.compose.AppError.InternetConnectionException
import kotlin.reflect.KClass

private fun Throwable.toApplicationErrorMessage(
    composeResourceErrorMessages: List<ComposeResourceErrorMessage>? = null,
): String {
    composeResourceErrorMessages?.forEach {
        if (it.appErrorClass.isInstance(this)) return it.message
    }
    return message ?: ""
}

@Composable
fun <T> providePresenterDefaults(
    userMessageStateHolder: UserMessageStateHolder = requireAppComponent().userMessageStateHolder,
    backStack: SnapshotStateStack<Screen> = LocalBackStack.current,
    block: @Composable (UserMessageStateHolder, SnapshotStateStack<Screen>) -> T,
): T {
    val composeResourceErrorMessages = composeResourceErrorMessages()
    val handler = remember(userMessageStateHolder) {
        object : ComposeEffectErrorHandler {
            override suspend fun emit(throwable: Throwable) {
                val message = throwable.toApplicationErrorMessage(composeResourceErrorMessages)
                userMessageStateHolder.showMessage(
                    message = message,
                    actionLabel = null,
                )
            }
        }
    }
    return compositionLocalProviderWithReturnValue(LocalComposeEffectErrorHandler provides handler) {
        block(userMessageStateHolder, backStack)
    }
}

/**
 * If you want to display composeResource error messages instead of error messages from the API, define them here
 */
@Composable
private fun composeResourceErrorMessages(): List<ComposeResourceErrorMessage> = listOf(
    ComposeResourceErrorMessage(
        InternetConnectionException::class,
        stringResource(R.string.connection_failed),
    ),
)

public sealed class AppError : RuntimeException {
    private constructor(message: String?, cause: Throwable?) : super(message, cause)
    private constructor(cause: Throwable?) : super(cause)

    public sealed class ApiException(cause: Throwable?) : AppError(cause = cause) {
        public class NetworkException(cause: Throwable?) : ApiException(cause = cause)
        public class ServerException(cause: Throwable?) : ApiException(cause = cause)
        public class TimeoutException(cause: Throwable?) : ApiException(cause = cause)
        public class SessionNotFoundException(cause: Throwable?) : AppError(cause = cause)
        public class UnknownException(cause: Throwable?) : AppError(cause = cause)
    }

    public sealed class ExternalIntegrationError(cause: Throwable?) : AppError(cause = cause) {
        public class NoCalendarIntegrationFoundException(cause: Throwable?) :
            ExternalIntegrationError(cause)
    }

    public class UnknownException(cause: Throwable?) : AppError(cause = cause)
    public class InternetConnectionException(cause: Throwable?) : AppError(cause)
}


private data class ComposeResourceErrorMessage(
    val appErrorClass: KClass<out AppError>,
    val message: String,
)