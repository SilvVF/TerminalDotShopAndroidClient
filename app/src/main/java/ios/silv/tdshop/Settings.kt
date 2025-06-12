package ios.silv.tdshop

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ios.silv.tdshop.di.requireAppComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import logcat.logcat
import me.tatarka.inject.annotations.Inject

val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

sealed interface SettingsEvent {
    data class Edit(val transform: suspend (MutablePreferences) -> Unit): SettingsEvent
}

data class SettingsState(
    val initialized: Boolean,
    val token: String,
    val events: (SettingsEvent) -> Unit
)

object Keys {
    val TOKEN = stringPreferencesKey("token")
}

class SettingsStore(
    private val store: DataStore<Preferences>
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    var initialized by mutableStateOf(false)
        private set

    private val initialValues: Preferences by lazy {
        runBlocking { store.data.first() }
    }

    init {
        scope.launch {
            initialValues.let {
                initialized = true
                logcat { "initialized settings" }
            }
        }
    }

    val tokenFlow by preferenceStateFlow(Keys.TOKEN, "")

    fun <KeyType, StateType> preferenceStateFlow(
        key: Preferences.Key<KeyType>,
        defaultValue: StateType,
        transform: ((KeyType) -> StateType?),
    ): Lazy<StateFlow<StateType>> = lazy {
        val initialValue = initialValues[key]?.let(transform) ?: defaultValue
        val stateFlow = MutableStateFlow(initialValue)
        scope.launch {
            store.data
                .map { preferences -> preferences[key]?.let(transform) ?: defaultValue }
                .collect(stateFlow::emit)
        }
        stateFlow
    }

    fun <KeyType> preferenceStateFlow(
        key: Preferences.Key<KeyType>,
        defaultValue: KeyType,
    ): Lazy<StateFlow<KeyType>> {
        return preferenceStateFlow(key, defaultValue) { keyType -> keyType }
    }

    suspend fun edit(
        transform: suspend (MutablePreferences) -> Unit
    ) {
        try {
            store.edit(transform)
        } catch (e: Exception) {
            logcat { e.stackTraceToString() }
        }
    }
}

typealias settingsPresenterProvider = @Composable () -> SettingsState

@Inject
@Composable
fun settingsPresenterProvider(
    store: SettingsStore
): SettingsState {

    val scope = rememberCoroutineScope()
    val token by store.tokenFlow.collectAsState()

    return SettingsState(
        initialized = store.initialized,
        token = token
    ) { event ->
        when(event) {
            is SettingsEvent.Edit -> {
                scope.launch {
                    store.edit(event.transform)
                }
            }
        }
    }
}
