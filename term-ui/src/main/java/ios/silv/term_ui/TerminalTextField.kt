package ios.silv.term_ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private const val LabelAlpha = 0.78f
private val BlockSize = 4.dp

@Composable
fun TerminalTextField(
    text: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    readOnly: Boolean = false,
    enabled: Boolean = true,
) {
    val primary = MaterialTheme.colorScheme.primary
    val contentColor = LocalContentColor.current

    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource)
            .animateContentSize(),
        readOnly = readOnly,
        interactionSource = interactionSource,
        enabled = enabled,
        value = text,
        textStyle = textStyle.copy(
            color = contentColor
        ),
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        onValueChange = onValueChange,
        cursorBrush = SolidColor(primary),
        decorationBox = { innerTextField ->
            Column(
                Modifier
                    .drawBehind {
                        if (isFocused) {
                            drawRect(
                                color = primary,
                                size = size.copy(
                                    width = BlockSize.toPx()
                                )
                            )
                        }
                    }
                    .padding(start = BlockSize)
                    .padding(start = 6.dp)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides LocalContentColor.current.copy(alpha = LabelAlpha)
                ) {
                    label()
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ">",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    innerTextField()
                }
                AnimatedContent(
                    targetState = error,
                    transitionSpec = {
                        slideInVertically { -it } + fadeIn() togetherWith
                                slideOutVertically { -it } + fadeOut()
                    },
                ) { err ->
                    if (err != null) {
                        Text(
                            color = MaterialTheme.colorScheme.error,
                            text = err
                        )
                    }
                }
            }
        }

    )
}