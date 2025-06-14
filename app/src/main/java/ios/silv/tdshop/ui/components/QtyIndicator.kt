package ios.silv.tdshop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ios.silv.tdshop.ui.compose.MutedAlpha

@Composable
fun QtyIndicator(
    qty: Int,
    add: () -> Unit,
    dec: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = dec
        ) {
            Text(
                "-",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current.copy(alpha = MutedAlpha)
                )
            )
        }
        Text("$qty")
        TextButton(
            onClick = add,
        ) {
            Text(
                "+",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current.copy(alpha = MutedAlpha)
                )
            )
        }
    }
}