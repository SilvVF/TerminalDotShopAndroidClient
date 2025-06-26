package ios.silv.tdshop.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEachIndexed
import ios.silv.tdshop.nav.Cart
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Payment
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.nav.Ship
import ios.silv.tdshop.nav.SnapshotStateStack
import ios.silv.tdshop.ui.compose.MutedAlpha

private val dests = listOf(
    Cart,
    Ship,
    Payment
)

fun destDisplayName(dest: Screen): String? {
    return when (dest) {
        Payment -> "payment"
        Ship -> "shipping"
        Cart -> "cart"
        else -> null
    }
}

@Composable
fun CartBreadCrumbs(
    modifier: Modifier = Modifier,
    backStack: SnapshotStateStack<Screen> = LocalBackStack.current
) {
    val selectedDest = backStack.lastItemOrNull
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dests.fastForEachIndexed { i, screen ->

            val selected = screen == selectedDest
            val name = destDisplayName(screen) ?: return@fastForEachIndexed

            TextButton(
                onClick = {
                    if (!backStack.popUntil { it == screen }) {
                        backStack.push(
                            dests.slice(0..i)
                        )
                    }
                }
            ) {
                Text(
                    text = name,
                    color = LocalContentColor.current.copy(
                        alpha = if (selected) 1f else MutedAlpha
                    )
                )
            }
            if (i != dests.lastIndex) {
                SlashSeparator()
            }
        }
    }
}

@Composable
private fun SlashSeparator(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "/",
        color = LocalContentColor.current.copy(alpha = MutedAlpha)
    )
}