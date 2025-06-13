package ios.silv.term_ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout

@Composable
fun TerminalSplitButton(
    modifier: Modifier = Modifier,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    label: @Composable () -> Unit,
    left: @Composable () -> Unit,
    right: @Composable () -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->

        val labelPlaceable = subcompose("label") { label() }
            .first()
            .measure(constraints)

        val leftPlaceable = subcompose("left") {
            TerminalSectionButton(
                onClick = onLeftClick,
                label = {
                    label()
                }
            ) {
                left()
            }
        }
            .first()
            .measure(constraints)

        val leftWidth = leftPlaceable.width
        val leftHeight = leftPlaceable.height

        val rightPlaceable = subcompose("right") {
            TerminalSectionButton(
                onClick = onRightClick,
                label = {
                    Box(Modifier.size(labelPlaceable.width.toDp(), labelPlaceable.height.toDp()))
                },
                content = right
            )
        }.first().measure(
            constraints.copy(
                minWidth = leftWidth,
                maxWidth = leftWidth,
                minHeight = leftHeight,
                maxHeight = leftHeight
            )
        )
        layout(
            width = leftPlaceable.width + rightPlaceable.width,
            height = maxOf(leftPlaceable.height, rightPlaceable.height)
        ) {
            var x = 0
            leftPlaceable.placeRelative(x, 0)
            x += leftPlaceable.width

            rightPlaceable.placeRelative(x, 0)
        }
    }
}