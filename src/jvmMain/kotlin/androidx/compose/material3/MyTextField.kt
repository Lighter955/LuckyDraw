package androidx.compose.material3

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.util.fastAll
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current.merge(TextStyle(fontFamily = FontFamily(Font(resource = "LXGWWenKaiLite-Bold.ttf")))),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.filledShape,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) = TextField(
    value,
    onValueChange,
    modifier,
    enabled,
    readOnly,
    textStyle,
    label,
    placeholder,
    leadingIcon,
    trailingIcon,
    supportingText,
    isError,
    visualTransformation,
    keyboardOptions,
    keyboardActions,
    singleLine,
    maxLines,
    interactionSource,
    shape,
    colors
)

fun Modifier.navigate(navigator: Navigator, scene: String): Modifier {
    return pointerInput(Unit) {
        awaitEachGesture {
            var event: PointerEvent
            do {
                event = awaitPointerEvent(PointerEventPass.Initial)
            } while (
                !event.changes.fastAll { it.changedToUp() }
            )
            navigator.navigate("/setting/$scene")
        }
    }
}