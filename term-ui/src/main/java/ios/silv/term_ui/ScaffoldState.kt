package ios.silv.term_ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass

// https://www.tunjid.com/articles/ui-layer-architecture-for-persistent-ui-elements-68248e8ecc8e85f53ce1aa46

class ScaffoldState internal constructor(
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    internal val isMediumScreenWidthOrWider: State<Boolean>,
) : AnimatedVisibilityScope by animatedVisibilityScope,
    SharedTransitionScope by sharedTransitionScope {

    internal val canShowBottomNavigation get() = !isMediumScreenWidthOrWider.value

    internal val canShowNavRail get() = isMediumScreenWidthOrWider.value
            // implementation omitted && isAtDeviceEdge

}

@Composable
fun ScaffoldState.PoppableDestinationTopAppBar(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    onBackPressed: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        navigationIcon = {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(),
                exit = fadeOut(),
                content = {
                    IconButton(
                        modifier = Modifier,
                        onClick = onBackPressed,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        title = title,
        actions = actions,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun rememberScaffoldState(
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) : ScaffoldState {
    val isMediumScreenWidthOrWider = isMediumScreenWidthOrWider()
    return remember {
        ScaffoldState(
            animatedVisibilityScope = animatedVisibilityScope,
            sharedTransitionScope = sharedTransitionScope,
            isMediumScreenWidthOrWider = isMediumScreenWidthOrWider,
        )
    }
}

@Composable
private fun isMediumScreenWidthOrWider(): State<Boolean> {
    val isMediumScreenWidthOrWider = currentWindowAdaptiveInfo()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    return rememberUpdatedState(isMediumScreenWidthOrWider)
}

@Composable
fun ScaffoldState.PersistentScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable ScaffoldState.() -> Unit = {},
    floatingActionButton: @Composable ScaffoldState.() -> Unit = {},
    navigationBar: @Composable ScaffoldState.() -> Unit = {},
    navigationRail: @Composable ScaffoldState.() -> Unit = {},
    content: @Composable ScaffoldState.(PaddingValues) -> Unit,
) {
    NavigationRailScaffold(
        modifier = modifier,
        navigationRail = { navigationRail() },
        content = {
            Scaffold(
                modifier = modifier
                    .animateBounds(lookaheadScope = this),
                topBar = {
                    topBar()
                },
                floatingActionButton = {
                    floatingActionButton()
                },
                bottomBar = {
                    navigationBar()
                },
                content = { paddingValues ->
                    content(paddingValues)
                },
            )
        }
    )
}

@Composable
private inline fun NavigationRailScaffold(
    modifier: Modifier = Modifier,
    navigationRail: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier
                    .widthIn(max = 80.dp)
                    .zIndex(2f),
            ) {
                navigationRail()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
            ) {
                content()
            }
        },
    )
}

@Composable
fun ScaffoldState.PersistentNavigationRail(
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = slideInHorizontally(initialOffsetX = { it }),
    exitTransition: ExitTransition = slideOutHorizontally(targetOffsetX = { it }),
    content: @Composable ColumnScope.() -> Unit
) {
    AnimatedVisibility(
        modifier = modifier
            .sharedElement(
                sharedContentState = rememberSharedContentState(
                    BottomNavSharedElementKey
                ),
                animatedVisibilityScope = this,
                zIndexInOverlay = BottomNavSharedElementZIndex,
            ),
        visible = canShowBottomNavigation,
        enter = enterTransition,
        exit = exitTransition,
        content = {
            NavigationRail {
                content()
            }
        }
    )
}

@Composable
fun ScaffoldState.PersistentNavigationBar(
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = slideInVertically(initialOffsetY = { it }),
    exitTransition: ExitTransition = slideOutVertically(targetOffsetY = { it }),
    content: @Composable RowScope.() -> Unit
) {
    AnimatedVisibility(
        modifier = modifier
            .sharedElement(
                sharedContentState = rememberSharedContentState(
                    BottomNavSharedElementKey
                ),
                animatedVisibilityScope = this,
                zIndexInOverlay = BottomNavSharedElementZIndex,
            ),
        visible = canShowBottomNavigation,
        enter = enterTransition,
        exit = exitTransition,
        content = {
            NavigationBar {
                content()
            }
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ScaffoldState.PersistentCustomFab(
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = slideInVertically(initialOffsetY = { it }),
    exitTransition: ExitTransition = slideOutVertically(targetOffsetY = { it }),
    visible: Boolean = true,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier
            .sharedBounds(
                sharedContentState = rememberSharedContentState(
                    FabSharedElementKey
                ),
                animatedVisibilityScope = this,
                zIndexInOverlay = BottomNavSharedElementZIndex,
            ),
        visible = visible == true,
        enter = enterTransition,
        exit = exitTransition,
        content = {
            Box(Modifier.animateContentSize()) {
                content()
            }
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ScaffoldState.PersistentFab(
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = slideInVertically(initialOffsetY = { it }),
    exitTransition: ExitTransition = slideOutVertically(targetOffsetY = { it }),
    text: String,
    icon: ImageVector?,
    expanded: Boolean,
    visible: Boolean? = null,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier
            .sharedElement(
                sharedContentState = rememberSharedContentState(
                    FabSharedElementKey
                ),
                animatedVisibilityScope = this,
                zIndexInOverlay = BottomNavSharedElementZIndex,
            ),
        visible = visible == true,
        enter = enterTransition,
        exit = exitTransition,
        content = {
            // The material3 ExtendedFloatingActionButton does not allow for placing
            // Modifier.animateContentSize() on its row.
            FloatingActionButton(
                modifier = Modifier,
                onClick = onClick,
                shape = RectangleShape,
                content = {
                    Row(
                        modifier = Modifier
                            .animateFabSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (icon != null) FabIcon(icon)
                        if (icon == null || expanded) {
                            if (icon != null) Spacer(modifier = Modifier.width(8.dp))
                            AnimatedContent(targetState = text) { text ->
                                Text(
                                    text = text,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            )
        }
    )
}

@Composable
private fun FabIcon(icon: ImageVector) {
    val rotationAnimatable = remember { Animatable(initialValue = 0f) }
    val animationSpec = remember {
        spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh,
            visibilityThreshold = 0.1f
        )
    }

    androidx.compose.material3.Icon(
        modifier = Modifier.rotate(rotationAnimatable.value),
        imageVector = icon,
        contentDescription = null
    )

    LaunchedEffect(icon) {
        rotationAnimatable.animateTo(targetValue = 30f, animationSpec = animationSpec)
        rotationAnimatable.animateTo(targetValue = 0f, animationSpec = animationSpec)
    }
}

private data object FabSharedElementKey
private data object BottomNavSharedElementKey

private const val BottomNavSharedElementZIndex = 2f
internal const val FabSharedElementZIndex = 4f

private val NavigationRailBoundsTransform = BoundsTransform { _, _ -> snap() }
