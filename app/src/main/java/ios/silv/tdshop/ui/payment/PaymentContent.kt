package ios.silv.tdshop.ui.payment

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ios.silv.tdshop.nav.AddShipDest
import ios.silv.tdshop.nav.LocalBackStack
import ios.silv.tdshop.nav.Payment
import ios.silv.tdshop.nav.Screen
import ios.silv.tdshop.ui.components.CartBreadCrumbs
import ios.silv.term_ui.PersistentScaffold
import ios.silv.term_ui.PoppableDestinationTopAppBar
import ios.silv.term_ui.TerminalSectionButton
import ios.silv.term_ui.TerminalSectionDefaults
import ios.silv.term_ui.TerminalTitle
import ios.silv.term_ui.rememberScaffoldState


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
    animatedContentScope: AnimatedContentScope,
    state: PaymentState,
    modifier: Modifier = Modifier
) {
    val backStack = LocalBackStack.current
    rememberScaffoldState(
        animatedContentScope,
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
        Box(Modifier
            .fillMaxSize()
            .padding(it)) {
            Column {
                CartBreadCrumbs(modifier = Modifier.fillMaxWidth())
                Text("Payment")
                PaymentOptionButton(
                    onClick = {},
                    title = "add payment information via ssh",
                    label = {
                        TerminalSectionDefaults.Label("SSH")
                    }
                )
                PaymentOptionButton(
                    onClick = {},
                    title = "add payment information via browser",
                    label = {
                        TerminalSectionDefaults.Label("Browser")
                    }
                )
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
        onClick =  onClick,
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
                Text(title)
                Text("enter")
            }
        }
    }
}