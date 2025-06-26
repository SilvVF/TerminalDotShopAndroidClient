package ios.silv.tdshop.ui.payment

import android.graphics.Bitmap
import android.graphics.Color
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import ios.silv.tdshop.nav.AddShipDest
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Payment
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.ui.ProvidePreviewDefaults
import ios.silv.tdshop.ui.components.CartBreadCrumbs
import ios.silv.tdshop.ui.compose.MutedAlpha
import ios.silv.term_ui.PersistentScaffold
import ios.silv.term_ui.PoppableDestinationTopAppBar
import ios.silv.term_ui.TerminalSectionButton
import ios.silv.term_ui.TerminalSectionDefaults
import ios.silv.term_ui.TerminalTitle
import ios.silv.term_ui.rememberScaffoldState
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.core.graphics.toColor
import ios.silv.term_ui.TerminalSection
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


fun EntryProviderBuilder<Screen>.paymentEntry(
    sharedTransitionScope: SharedTransitionScope
) {
    entry<Payment> {
        val state = paymentPresenter()

        PaymentContent(
            sharedTransitionScope,
            LocalNavAnimatedContentScope.current,
            state = state,
        )
    }
}

@Composable
private fun PaymentContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: PaymentState,
    modifier: Modifier = Modifier
) {
    val backStack = LocalBackStack.current
    rememberScaffoldState(
        animatedVisibilityScope,
        sharedTransitionScope,
    ).PersistentScaffold(
        modifier = modifier,
        topBar = {
            PoppableDestinationTopAppBar(
                title = {
                    TerminalTitle(text = "Payment")
                }
            ) {
                backStack.pop()
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            CartBreadCrumbs(modifier = Modifier.fillMaxWidth())

            BackHandler(
                enabled = state.method != null
            ) {
                state.events(PaymentEvent.SetMethod(null))
            }

            AnimatedContent(state.method, modifier = Modifier.fillMaxSize()) { method ->
                when (method) {
                    PaymentMethod.SSH -> SSHPaymentContent(state)
                    PaymentMethod.Browser -> BrowserPaymentContent(state)
                    null -> PaymentOptionSelect(state)
                }
            }
        }
    }
}

@Composable
private fun PaymentOptionSelect(
    state: PaymentState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Text("Select a payment method", color = LocalContentColor.current.copy(alpha = MutedAlpha))
        PaymentOptionButton(
            onClick = {
                state.events(PaymentEvent.SetMethod(PaymentMethod.SSH))
            },
            title = "add payment information via ssh",
            label = {
                TerminalSectionDefaults.Label("SSH")
            }
        )
        PaymentOptionButton(
            onClick = {
                state.events(PaymentEvent.SetMethod(PaymentMethod.Browser))
            },
            title = "add payment information via browser",
            label = {
                TerminalSectionDefaults.Label("Browser")
            }
        )
        state.cards.fastForEach { card ->
            TerminalSection(
                label = {
                    TerminalSectionDefaults.Label(
                        card.last4()
                    )
                }
            ) {
                Text("Card")
            }
        }
    }
}

@Composable
private fun PaymentOptionButton(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    TerminalSectionButton(
        onClick = onClick,
        modifier = Modifier.height(90.dp),
        label = {
            label()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, Modifier.weight(1f))
                Text("enter", maxLines = 1)
            }
        }
    }
}

@Composable
private fun produceBitmap(
    url: String?,
    size: IntSize
): ImageBitmap? {

    val bmp by produceState<Bitmap?>(null, url, size) {

        if (url == null) return@produceState

        try {
            val minSize = minOf(size.width, size.height)

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, minSize, minSize)

            val bmp = createBitmap(minSize, minSize, Bitmap.Config.RGB_565)
            for (x in 0 until minSize) {
                for (y in 0 until minSize) {
                    bmp[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }

            if (value != null) {
                value?.recycle()
            }
            value = bmp
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
    }

    return bmp?.asImageBitmap()
}

@Composable
private fun BrowserPaymentContent(
    state: PaymentState,
    modifier: Modifier = Modifier
) {
    val containerSize = LocalWindowInfo.current.containerSize

    val size by remember {
        derivedStateOf {
            val height = containerSize.height * 0.4f
            val width = containerSize.width * 0.6f
            IntSize(width.roundToInt(), height.roundToInt())
        }
    }

    val bitmap = produceBitmap(state.url, size)

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(
                    with(LocalDensity.current) {
                        val minSize = minOf(size.width, size.height).toDp()
                        DpSize(
                            width = minSize,
                            height = minSize
                        )
                    }
                )
                .background(
                    if (bitmap == null) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        androidx.compose.ui.graphics.Color.Transparent
                    }
                )
        ) {
            if (bitmap != null) {
                Image(
                    contentDescription = null,
                    bitmap = bitmap,
                )
            }
        }
        TextButton(
            onClick = {
                state.events(PaymentEvent.PayByUrl)
            }
        ) {
            Text(state.url.orEmpty())
        }
        Button(
            onClick = {
                state.events(PaymentEvent.RefreshUrl)
            },
            enabled = state.canRegenerate
        ) {
            Text("refresh url")
            Spacer(Modifier.width(4.dp))
            if (!state.canRegenerate) {
                AnimatedContent(
                    state.regenerateTimer,
                    transitionSpec = {
                        slideInVertically(
                            initialOffsetY = { -it },
                        ) togetherWith slideOutVertically(
                            targetOffsetY = { it }
                        )
                    }
                ) { time ->
                    Text("$time")
                }
            }
        }
    }
}

@Composable
private fun SSHPaymentContent(
    state: PaymentState,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize()) {
        TextButton(onClick = {
            state.events(PaymentEvent.SetMethod(PaymentMethod.Browser))
        }, modifier = Modifier.align(Alignment.Center)) {
            Text("Unavailable create a card via the browser")
        }
    }
}

@Preview
@Composable
private fun PaymentContentPreview() {
    ProvidePreviewDefaults {

        var method by remember {
            mutableStateOf<PaymentMethod?>(null)
        }

        PaymentContent(
            this,
            this,
            PaymentState(
                method = method,
            ) {
                when (it) {
                    is PaymentEvent.SetMethod -> method = it.method
                    else -> Unit
                }
            }
        )
    }
}
