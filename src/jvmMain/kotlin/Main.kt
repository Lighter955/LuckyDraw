import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import moe.tlaster.precompose.PreComposeWindow
import java.awt.Dimension

fun main() = application {
    PreComposeWindow(
        onCloseRequest = { exitApplication() },
        state = WindowState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(1024.dp, 720.dp)
        ),
        title = "LuckyDraw",
        icon = painterResource("icon_32.png")
    ) {
        window.minimumSize = Dimension(1024, 600)
        App()
    }
}