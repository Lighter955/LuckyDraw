package scenes.setting

import AppState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import model.Group

@Composable
fun AddGroupsScene(
    index: Int,
    appState: AppState,
    onCloseRequest: () -> Unit
) {
    val groupsString = remember { mutableStateOf("") }
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
                text = "添加小组",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
            Column(
                modifier = Modifier.fillMaxSize(0.8f)
            ) {
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
                        appState.scope.launch {
                            val groupList: MutableList<Group> = groupsString.value.toGroups()
                            appState.addGroups(index, groupList)
                            onCloseRequest()
                        }
                    },
                    enabled = !isGroupsStringError.value
                ) {
                    MyText("完成", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun EditGroupScene(
    classIndex: Int,
    groupIndex: Int,
    appState: AppState,
    onCloseRequest: () -> Unit
) {
    val groupString = remember { mutableStateOf(appState.classList[classIndex].groups[groupIndex].formatToString()) }
    val isGroupStringError = remember { mutableStateOf(false) }
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
                text = "编辑小组",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
            Column(
                modifier = Modifier.fillMaxSize(0.8f)
            ) {
                MyTextField(
                    value = groupString.value,
                    onValueChange = {
                        groupString.value = it.replaceTabToSpace()
                        isGroupStringError.value = it.checkToGroupError()
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = { MyText("小组名单") },
                    supportingText = {
                        Row {
                            MyText("输入正确的")
                            GroupFormatTip()
                        }
                    },
                    isError = isGroupStringError.value
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
                        appState.scope.launch {
                            val group: Group = groupString.value.toGroup()
                            appState.updateGroup(classIndex, groupIndex, group)
                            onCloseRequest()
                        }
                    },
                    enabled = !isGroupStringError.value
                ) {
                    MyText("完成", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupFormatTip() {
    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "例如：\n[第一组]\n20212123001 张三\n20212123002 李四\n20212123003 王五",
                    modifier = Modifier.padding(10.dp)
                )
            }
        },
        delayMillis = 500,
    ) {
        MyText(
            text = "格式",
            color = MaterialTheme.colorScheme.primary
        )
    }
}