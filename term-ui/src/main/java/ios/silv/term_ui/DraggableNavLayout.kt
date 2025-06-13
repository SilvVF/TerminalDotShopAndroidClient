package ios.silv.term_ui

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Stable
enum class NavLayoutAnchors {
    Start,
    End,
}

private val NavBlockSize = 128.dp
private val BlockPadding = 2.dp

private fun createDraggable(
    density: Density,
    initial: NavLayoutAnchors,
    scope: CoroutineScope,
): NavLayoutDragState = NavLayoutDragState(
    scope,
    AnchoredDraggableState(
        initialValue = initial,
        anchors = DraggableAnchors {
            with(density) {
                NavLayoutAnchors.End at NavBlockSize.toPx() + BlockPadding.toPx()
                NavLayoutAnchors.Start at 0f
            }
        }
    )
)


class NavLayoutDragState(
    private val scope: CoroutineScope,
    internal val anchoredDraggableState: AnchoredDraggableState<NavLayoutAnchors>
) {
    val maxWidth: Float
        get() = anchoredDraggableState.anchors.maxPosition()

    val current: NavLayoutAnchors
        get() = anchoredDraggableState.currentValue

    val target: NavLayoutAnchors
        get() = anchoredDraggableState.targetValue

    fun hide() {
        scope.launch { 
            anchoredDraggableState.animateTo(NavLayoutAnchors.Start)
        }
    }
    
    fun show() {
        scope.launch {
            anchoredDraggableState.animateTo(NavLayoutAnchors.End)
        }
    }

    fun toggle() {
        when(anchoredDraggableState.targetValue) {
            NavLayoutAnchors.Start -> show()
            NavLayoutAnchors.End -> hide()
        }
    }
}

@Composable
fun rememberNavLayoutDraggableState(
    initial: NavLayoutAnchors = NavLayoutAnchors.Start,
    density: Density = LocalDensity.current,
    scope: CoroutineScope = rememberCoroutineScope(),
) = rememberSaveable(
    saver = Saver(
        save = { value -> value.anchoredDraggableState.currentValue.ordinal },
        restore = { saved ->
            createDraggable(
                density, 
                NavLayoutAnchors.entries.getOrElse(saved) { initial },
                scope
            )
        }
    )
) {
    createDraggable(density, initial, scope)
}

@Composable
fun DraggableNavLayout(
    nav: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: NavLayoutDragState = rememberNavLayoutDraggableState(),
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier
            .anchoredDraggable(
                state.anchoredDraggableState,
                Orientation.Horizontal
            ),
        content = {
            nav()
            content()
        }
    ) { measurables, constraints ->
        
        assert(measurables.size == 2) {
            "to many measurables provided to draggable nav layout"
        }
        
        val dragState = state.anchoredDraggableState
        val(navM, contentM) = measurables
        

        val offset = dragState.offset.roundToInt()

        val navP = navM.measure(
            constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = NavBlockSize.roundToPx(),
                maxHeight = constraints.maxHeight
            )
        )
        val stdoutP = contentM.measure(
            constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = constraints.maxWidth - offset,
                maxHeight = constraints.maxHeight
            )
        )

        layout(constraints.maxWidth, constraints.maxHeight) {
            stdoutP.place(offset, 0)
            navP.place(offset - navP.width - BlockPadding.roundToPx(), 0)
        }
    }
}
