package scenes.setting

import AppState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.navigation.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScene(
    scene: String = "class",
    appState: AppState,
    navigateBack: () -> Unit
) = MaterialTheme {
    val navigator = rememberNavigator()
    val currentPath = remember { mutableStateOf("/setting/$scene") }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        PermanentNavigationDrawer(
            modifier = Modifier.fillMaxSize(),
            drawerContent = {
                DrawerContent(
                    navigator = navigator,
                    navigateBack = {
                        if (currentPath.value == "/setting/class" || currentPath.value == "/setting/questionset") {
                            navigateBack()
                        } else {
                            navigator.goBack()
                        }
                    },
                    currentPath = currentPath
                )
            }
        ) {
            Column {
                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navigator = navigator,
                    initialRoute = "/setting/$scene"
                ) {
                    scene("/setting/class") {
                        ClassSettingScene(
                            appState = appState,
                            navigator = navigator
                        )
                        currentPath.value = it.path
                    }
                    scene("/setting/class/add") {
                        AddClassScene(
                            appState = appState
                        ) { navigator.goBack() }
                        currentPath.value = it.path
                    }
                    scene("/setting/class/{index}/edit") {
                        val index: Int? = it.path<Int>("index")
                        if (index != null) {
                            EditClassScene(
                                index = index,
                                appState = appState,
                                navigator = navigator,
                                onCloseRequest = { navigator.goBack() }
                            )
                            currentPath.value = it.path
                        }
                    }
                    scene("/setting/class/{index}/group/add") {
                        val index: Int? = it.path<Int>("index")
                        if (index != null) {
                            AddGroupsScene(
                                index = index,
                                appState = appState
                            ) { navigator.goBack() }
                            currentPath.value = it.path
                        }
                    }
                    scene("/setting/class/group/edit") {
                        val classIndex: Int? = it.query<Int>("class")
                        val groupIndex: Int? = it.query<Int>("group")
                        if (classIndex != null && groupIndex != null) {
                            EditGroupScene(
                                classIndex = classIndex,
                                groupIndex = groupIndex,
                                appState = appState
                            ) { navigator.goBack() }
                            currentPath.value = it.path
                        }
                    }
                    scene("/setting/questionset") {
                        QuestionSetSettingScene(appState, navigator)
                        currentPath.value = it.path
                    }
                    scene("/setting/questionset/add") {
                        AddQuestionSetScene(
                            appState = appState
                        ) { navigator.goBack() }
                        currentPath.value = it.path
                    }
                    scene("/setting/questionset/{index}/edit") {
                        val index: Int? = it.path<Int>("index")
                        if (index != null) {
                            EditQuestionSetScene(
                                index = index,
                                appState = appState,
                                onCloseRequest = { navigator.goBack() }
                            )
                            currentPath.value = it.path
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerContent(
    navigator: Navigator,
    navigateBack: () -> Unit,
    currentPath: MutableState<String>
) {
    PermanentDrawerSheet {
        Column(
            modifier = Modifier.wrapContentWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyText(
                    text = "设置",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
                    onClick = { navigateBack() }
                ) {
                    Icon(imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = "")
                }
            }
            NavigationDrawerItem(
                label = { MyText("班级") },
                selected = currentPath.value.startsWith("/setting/class"),
                onClick = {
                    navigator.navigate("/setting/class")
                },
                icon = {
                    Icon(
                        imageVector = if (currentPath.value.startsWith("/setting/class")) Icons.Filled.Face else Icons.Outlined.Face,
                        contentDescription = "")
                    },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
            )
            NavigationDrawerItem(
                label = { MyText("问题集") },
                selected = currentPath.value.startsWith("/setting/questionset"),
                onClick = {
                    navigator.navigate("/setting/questionset")
                },
                icon = {
                    Icon(
                        imageVector = if (currentPath.value.startsWith("/setting/questionset")) Icons.Filled.Quiz else Icons.Outlined.Quiz,
                        contentDescription = ""
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
            )
        }
    }
}