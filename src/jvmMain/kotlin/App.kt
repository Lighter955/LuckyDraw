import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.query
import moe.tlaster.precompose.navigation.rememberNavigator
import scenes.main.MainScene
import scenes.setting.SettingScene
import scenes.start.StartScene

@Composable
fun App() {
    val appState = rememberAppState()
    val navigator = rememberNavigator()
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.inverseOnSurface
        ) {
            NavHost(
                navigator = navigator,
                initialRoute = "/start"
            ) {
                scene("/start") {
                    StartScene(
                        appState = appState,
                        navigator = navigator
                    )
                }
                scene("/main") {
                    val classIndex: Int? = it.query<Int>("class")
                    val questionsetIndex: Int? = it.query<Int>("questionset")
                    if (classIndex != null && questionsetIndex != null) {
                        MainScene(
                            appState = appState,
                            classIndex = classIndex,
                            questionsetIndex = questionsetIndex,
                            navigateBack = { navigator.goBack() }
                        )
                    }
                }
                scene("/setting/{scene}?") {
                    val scene: String? = it.path<String>("scene")
                    if (scene == null) {
                        SettingScene(
                            appState = appState,
                            navigateBack = { navigator.goBack() }
                        )
                    } else {
                        SettingScene(
                            scene = scene,
                            appState = appState,
                            navigateBack = { navigator.goBack() }
                        )
                    }
                }
            }
        }
    }
}