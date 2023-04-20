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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Question
import model.QuestionSet
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun QuestionSetSettingScene(
    appState: AppState,
    navigator: Navigator,
    questionSetList: SnapshotStateList<QuestionSet> = appState.questionSetList
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navigator.navigate("/setting/questionset/add") },
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "") },
                text = { MyText("添加问题集") }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        if (questionSetList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyText("还没有问题集先添加一个吧")
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
                                    questionSetList.removeAt(dialogState.value)
                                    appState.settings.putString("questionset", Json.encodeToString<List<QuestionSet>>(questionSetList))
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
                                text = "确定要删除 ${questionSetList[dialogState.value].name} 吗？",
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
                itemsIndexed(questionSetList) { index, it ->
                    val deleteButtonVisibilityState = remember { mutableStateOf(false) }
                    Card(
                        onClick = { navigator.navigate("/setting/questionset/$index/edit") },
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
                                MyText(it.name, style = MaterialTheme.typography.titleMedium)
                                MyText("共有 ${it.questions.size} 个问题", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.outline)
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
fun AddQuestionSetScene(
    appState: AppState,
    onCloseRequest: () -> Unit,
    questionSetList: SnapshotStateList<QuestionSet> = appState.questionSetList
) {
    val questionSetName = remember { mutableStateOf("") }
    val questionsString = remember { mutableStateOf("") }
    val isQuestionSetNameError = remember { mutableStateOf(false) }
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
                text = "添加问题集",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
            Column(
                modifier = Modifier.fillMaxSize(0.8f)
            ) {
                MyTextField(
                    value = questionSetName.value,
                    onValueChange = {
                        questionSetName.value = it
                        isQuestionSetNameError.value = it.isBlank()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { MyText("问题集名") },
                    supportingText = { if (isQuestionSetNameError.value) { MyText("问题集名不能为空") } },
                    isError = isQuestionSetNameError.value,
                    singleLine = true
                )
                Spacer(Modifier.height(32.dp))
                MyTextField(
                    value = questionsString.value,
                    onValueChange = { questionsString.value = it },
                    modifier = Modifier.fillMaxSize(),
                    label = { MyText("问题集内容") },
                    supportingText = { MyText("一个问题换一行") }
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
                        val questions: MutableList<Question> = mutableListOf()
                        questionsString.value.split("\n").forEach {
                            if (it != "") {
                                questions.add(Question(it))
                            }
                        }
                        val questionSet = QuestionSet(
                            name = questionSetName.value,
                            questions = questions
                        )
                        questionSetList.add(questionSet)
                        appState.settings.putString("questionset", Json.encodeToString<List<QuestionSet>>(questionSetList))
                        onCloseRequest()
                    },
                    enabled = !isQuestionSetNameError.value && questionSetName.value.isNotBlank()
                ) {
                    MyText("完成", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun EditQuestionSetScene(
    index: Int,
    appState: AppState,
    onCloseRequest: () -> Unit,
    questionSetList: SnapshotStateList<QuestionSet> = appState.questionSetList
) {
    val questionSetName = remember { mutableStateOf(questionSetList[index].name) }
    val isQuestionSetNameError = remember { mutableStateOf(false) }
    val questionsString = remember { mutableStateOf(questionSetList[index].questions.joinToString("\n", transform = { it.text })) }
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
                text = "编辑问题集",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
            Column(
                modifier = Modifier.fillMaxSize(0.8f)
            ) {
                MyTextField(
                    value = questionSetName.value,
                    onValueChange = {
                        questionSetName.value = it
                        isQuestionSetNameError.value = it.isBlank()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { MyText("问题集名") },
                    supportingText = { if (isQuestionSetNameError.value) { MyText("问题集名不能为空") } },
                    isError = isQuestionSetNameError.value,
                    singleLine = true
                )
                Spacer(Modifier.height(32.dp))
                MyTextField(
                    value = questionsString.value,
                    onValueChange = { questionsString.value = it },
                    modifier = Modifier.fillMaxSize(),
                    label = { MyText("问题集内容") },
                    supportingText = { MyText("一个问题换一行") }
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
                        val questions: MutableList<Question> = mutableListOf()
                        questionsString.value.split("\n").forEach {
                            if (it != "") {
                                questions.add(Question(it))
                            }
                        }
                        val questionSet = QuestionSet(
                            name = questionSetName.value,
                            questions = questions
                        )
                        questionSetList.removeAt(index)
                        questionSetList.add(index, questionSet)
                        appState.settings.putString("questionset", Json.encodeToString<List<QuestionSet>>(questionSetList))
                        onCloseRequest()
                    },
                    enabled = !isQuestionSetNameError.value && questionSetName.value.isNotBlank()
                ) {
                    MyText("完成", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}