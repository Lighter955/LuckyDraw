package scenes.setting

import AppState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Class
import model.Group
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun ClassSettingScene(
    appState: AppState,
    navigator: Navigator,
    classList: SnapshotStateList<Class> = appState.classList
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navigator.navigate("/setting/class/add") },
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "") },
                text = { MyText("添加班级") }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        if (classList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyText("还没有班级先添加一个吧")
            }
        } else {
            val dialogState = remember { mutableStateOf(-1) }
            if (dialogState.value > -1) {
                AlertDialog(
                    onDismissRequest = { dialogState.value = -1 },
                    buttons = {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { dialogState.value = -1 }
                            ) {
                                MyText("取消")
                            }
                            Spacer(Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    classList.removeAt(dialogState.value)
                                    appState.settings.putString("class", Json.encodeToString<List<Class>>(classList))
                                    dialogState.value = -1
                                }
                            ) {
                                MyText("确定")
                            }
                        }
                    },
                    text = {
                        if (dialogState.value > -1) {
                            MyText(
                                text = "确定要删除 ${classList[dialogState.value].name} 吗？",
                                modifier = Modifier.width(300.dp).padding(horizontal = 4.dp, vertical = 8.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
                    dialogProvider = MyDialogProvider
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(240.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(classList) { index, it ->
                    val deleteButtonVisibilityState = remember { mutableStateOf(false) }
                    Card(
                        onClick = { navigator.navigate("/setting/class/$index/edit") },
                        modifier = Modifier.height(100.dp)
                            .onPointerEvent(PointerEventType.Enter) { deleteButtonVisibilityState.value = true }
                            .onPointerEvent(PointerEventType.Exit) { deleteButtonVisibilityState.value = false },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                MyText(
                                    text = it.name,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                MyText(
                                    text = "共有 ${it.groups.size} 个小组",
                                    color = MaterialTheme.colorScheme.outline,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            if (deleteButtonVisibilityState.value) {
                                IconButton(
                                    onClick = { dialogState.value = index }
                                ) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddClassScene(
    appState: AppState,
    onCloseRequest: () -> Unit,
    classList: SnapshotStateList<Class> = appState.classList
) {
    val className = remember { mutableStateOf("") }
    val groupsString = remember { mutableStateOf("") }
    val isClassNameError = remember { mutableStateOf(false) }
    val isGroupsStringError = remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyText(
                text = "添加班级",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
            Column(
                modifier = Modifier.fillMaxSize(0.8f)
            ) {
                MyTextField(
                    value = className.value,
                    onValueChange = {
                        className.value = it
                        isClassNameError.value = it.isBlank()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { MyText("班级名") },
                    supportingText = { if (isClassNameError.value) { MyText("班级名不能为空") } },
                    isError = isClassNameError.value,
                    singleLine = true
                )
                Spacer(Modifier.height(32.dp))
                MyTextField(
                    value = groupsString.value,
                    onValueChange = {
                        groupsString.value = it.replaceTabToSpace()
                        isGroupsStringError.value = it.checkToGroupsError()
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = { MyText("小组名单") },
                    supportingText = {
                        Row {
                            MyText("输入正确的")
                            GroupFormatTip()
                        }
                    },
                    isError = isGroupsStringError.value
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onCloseRequest
                ) {
                    MyText("取消", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        val groups: MutableList<Group> = groupsString.value.toGroups()
                        val newClass = Class(
                            name = className.value,
                            groups = groups
                        )
                        classList.add(newClass)
                        appState.settings.putString("class", Json.encodeToString<List<Class>>(classList))
                        onCloseRequest()
                    },
                    enabled = !isClassNameError.value && !isGroupsStringError.value && className.value.isNotBlank()
                ) {
                    MyText("完成", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun EditClassScene(
    index: Int,
    appState: AppState,
    navigator: Navigator,
    onCloseRequest: () -> Unit,
    classList: SnapshotStateList<Class> = appState.classList,
    groupList: SnapshotStateList<Group> = classList[index].groups.toMutableStateList()
) {
    val className = remember { mutableStateOf(classList[index].name) }
    val dialogState = remember { mutableStateOf(-1) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyText(
                text = "编辑班级",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
            Column(
                modifier = Modifier.fillMaxSize(0.8f)
            ) {
                MyTextField(
                    value = className.value,
                    onValueChange = { className.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { MyText("班级名") },
                    supportingText = { if (className.value.isBlank()) { MyText("班级名不能为空") } },
                    isError = className.value.isBlank(),
                    singleLine = true
                )
                Spacer(Modifier.height(32.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(240.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            onClick = { navigator.navigate("/setting/class/$index/group/add") },
                            modifier = Modifier.height(50.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                MyText(
                                    text = "添加小组",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                    itemsIndexed(groupList) { groupIndex, it ->
                        val deleteButtonVisibilityState = remember { mutableStateOf(false) }
                        Card(
                            onClick = {
                                navigator.navigate("/setting/class/group/edit?class=$index&group=$groupIndex")
                            },
                            modifier = Modifier.height(100.dp)
                                .onPointerEvent(PointerEventType.Enter) { deleteButtonVisibilityState.value = true }
                                .onPointerEvent(PointerEventType.Exit) { deleteButtonVisibilityState.value = false },
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    MyText(
                                        text = it.name,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    MyText(
                                        text = it.members.joinToString("、", transform = { it.name }),
                                        color = MaterialTheme.colorScheme.outline,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                Box(
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    if (deleteButtonVisibilityState.value) {
                                        IconButton(
                                            onClick = {
                                                dialogState.value = groupIndex
                                            }
                                        ) {
                                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onCloseRequest
                ) {
                    MyText("取消", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        val newClass = Class(
                            name = className.value,
                            groups = groupList
                        )
                        classList.removeAt(index)
                        classList.add(index, newClass)
                        appState.settings.putString("class", Json.encodeToString<List<Class>>(classList))
                        onCloseRequest()
                    },
                    enabled = className.value.isNotBlank()
                ) {
                    MyText("完成", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
    if (dialogState.value > -1) {
        AlertDialog(
            onDismissRequest = { dialogState.value = -1 },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { dialogState.value = -1 }
                    ) {
                        MyText("取消")
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = {
                            classList[index].groups.removeAt(dialogState.value)
                            appState.settings.putString("class", Json.encodeToString<List<Class>>(classList))
                            dialogState.value = -1
                        }
                    ) {
                        MyText("确定")
                    }
                }
            },
            text = {
                if (dialogState.value > -1) {
                    MyText(
                        text = "确定要删除 ${classList[index].groups[dialogState.value].name} 吗？",
                        modifier = Modifier.width(300.dp).padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
            dialogProvider = MyDialogProvider
        )
    }
}