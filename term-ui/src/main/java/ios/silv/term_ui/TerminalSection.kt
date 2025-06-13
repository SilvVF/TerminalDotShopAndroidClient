package ios.silv.term_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.dp

object TerminalSectionDefaults {

    @Composable
    fun Label(
        text: String,
        style: TextStyle = MaterialTheme.typography.labelLarge,
        color: Color = MaterialTheme.colorScheme.background
    ) {
        Surface(
            color = color,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 2.dp),
                style = style
            )
        }
    }
}

private class TerminalLayoutMeasurePolicy(
    val labelOffset: DpOffset,
) : MeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        require(measurables.size == 3) {
            "Expected exactly 3 measurables: content, label, wrapper"
        }

        val (
            contentMeasurable,
            labelMeasurable,
            wrapperMeasurable
        ) = measurables

        val relaxedConstraints = constraints.copyMaxDimensions()

        val labelPlaceable = labelMeasurable.measure(relaxedConstraints)
        val labelHalfHeight = labelPlaceable.height / 2

        val contentPlaceable = contentMeasurable.measure(
            relaxedConstraints.copy(
                maxHeight = (constraints.maxHeight - labelPlaceable.height - labelHalfHeight)
                    .coerceAtLeast(0)
            )
        )

        val layoutWidth = maxOf(
            labelOffset.x.roundToPx() * 2 + labelPlaceable.width,
            contentPlaceable.width
        )
            .coerceIn(constraints.minWidth, constraints.maxWidth)

        val layoutHeight = (
                labelOffset.y.roundToPx() +
                        labelPlaceable.height +
                        labelHalfHeight +
                        contentPlaceable.height
                ).coerceIn(constraints.minHeight, constraints.maxHeight)

        val wrapperPlaceable = wrapperMeasurable.measure(
            Constraints(
                maxWidth = layoutWidth,
                minWidth = layoutWidth,
                maxHeight = layoutHeight - labelHalfHeight,
                minHeight = layoutHeight - labelHalfHeight
            )
        )

        val labelY = labelOffset.y.roundToPx()

        val contentX = (layoutWidth - contentPlaceable.width) / 2
        val contentY = ((layoutHeight - contentPlaceable.height + labelHalfHeight) / 2)

        return layout(layoutWidth, layoutHeight) {
            wrapperPlaceable.place(0, labelHalfHeight)
            labelPlaceable.place(labelOffset.x.roundToPx(), labelY)
            contentPlaceable.place(contentX, contentY)
        }
    }
}

@Composable
fun  TerminalSectionButton(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.background,
    labelOffset: DpOffset = DpOffset(12.dp, 0.dp),
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier.minimumInteractiveComponentSize(),
        content = {
            CompositionLocalProvider(
                LocalContentColor provides contentColorFor(containerColor)
            ) {
                Box(Modifier.wrapContentSize(), Alignment.Center) {
                    content()
                }
            }
            label()
            Box(
                Modifier
                    .fillMaxSize()
                    .border(1.dp, borderColor)
                    .background(containerColor)
                    .clickable(
                        enabled = enabled,
                        role = Role.Button
                    ) {
                        onClick()
                    }
            )
        },
        measurePolicy = remember(labelOffset) {
            TerminalLayoutMeasurePolicy(labelOffset)
        }
    )
}

@Composable
fun TerminalSection(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    labelOffset: DpOffset = DpOffset(12.dp, 0.dp),
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = {
            content()
            label()
            Box(
                Modifier
                    .fillMaxSize()
                    .border(1.dp, borderColor)
            )
        },
        measurePolicy = remember(labelOffset) {
            TerminalLayoutMeasurePolicy(labelOffset)
        }
    )
}
