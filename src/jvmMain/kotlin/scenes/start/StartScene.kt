package scenes.start

import AppState
import androidx.compose.material3.MyText
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.Class
import model.QuestionSet
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun StartScene(
    appState: AppState,
    navigator: Navigator,
    classList: SnapshotStateList<Class> = appState.classList,
    questionSetList: SnapshotStateList<QuestionSet> = appState.questionSetList
) = MaterialTheme {
    val selectedClass = rememberSaveable {
        mutableStateOf(
            if (classList.isNotEmpty()) classList[0] else null
        )
    }
    val selectedQuestionSet = rememberSaveable {
        mutableStateOf(
            if (questionSetList.isNotEmpty()) questionSetList[0] else null
        )
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyText(
                "Lucky Draw",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(64.dp))
            ClassSelector(navigator, classList, selectedClass)
            Spacer(Modifier.height(32.dp))
            QuestionSetSelector(navigator, questionSetList, selectedQuestionSet)
            Spacer(Modifier.height(64.dp))
            Row {
                TextButton(
                    onClick = { navigator.navigate("/setting") }
                ) {
                    MyText("设置", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.width(32.dp))
                Button(
                    onClick = {
                        navigator.navigate(
                            route = "/main?class=${classList.indexOf(selectedClass.value)}&questionset=${questionSetList.indexOf(selectedQuestionSet.value)}"
                        )
                    },
                    enabled = appState.classList.isNotEmpty()
                        && appState.questionSetList.isNotEmpty()
                        && selectedClass.value?.groups?.isNotEmpty() ?: false
                        && selectedQuestionSet.value?.questions?.isNotEmpty() ?: false
                ) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "", modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    MyText("开始抽奖", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun ClassSelector(
    navigator: Navigator,
    classList: SnapshotStateList<Class>,
    selectedClass: MutableState<Class?>
) {
    val expanded = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(selectedClass) {
        selectedClass.value = if (classList.isNotEmpty()) {
            classList[0]
        } else {
            null
        }
    }
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value }
    ) {
        if (selectedClass.value != null) {
            MyTextField(
                modifier = Modifier.menuAnchor(),
                value = selectedClass.value!!.name,
                onValueChange = { },
                readOnly = true,
                label = { MyText("班级") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                supportingText = { if (selectedClass.value!!.groups.isEmpty()) { MyText("该班级内没有小组") } },
                isError = selectedClass.value!!.groups.isEmpty()
            )
            ExposedDropdownMenu(expanded = expanded) {
                classList.forEach {
                    DropdownMenuItem(
                        text = { MyText(it.name) },
                        onClick = {
                            selectedClass.value = it
                            expanded.value = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        } else {
            MyTextField(
                modifier = Modifier.navigate(navigator, "class"),
                value = "在设置中添加班级",
                onValueChange = { },
                readOnly = true,
                label = { MyText("班级") },
                trailingIcon = { Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "") }
            )
        }
    }
}

@Composable
fun QuestionSetSelector(
    navigator: Navigator,
    questionSetList: SnapshotStateList<QuestionSet>,
    selectedQuestionSet: MutableState<QuestionSet?>
) {
    val expanded = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(questionSetList) {
        selectedQuestionSet.value = if (questionSetList.isNotEmpty()) {
            questionSetList[0]
        } else {
            null
        }
    }
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value }
    ) {
        if (selectedQuestionSet.value != null) {
            MyTextField(
                modifier = Modifier.menuAnchor(),
                value = selectedQuestionSet.value!!.name,
                onValueChange = {},
                readOnly = true,
                label = { MyText("问题集") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                supportingText = { if (selectedQuestionSet.value!!.questions.isEmpty()) { MyText("该问题集内没有问题") } },
                isError = selectedQuestionSet.value!!.questions.isEmpty()
            )
            ExposedDropdownMenu(expanded = expanded) {
                questionSetList.forEach {
                    DropdownMenuItem(
                        text = { MyText(it.name) },
                        onClick = {
                            selectedQuestionSet.value = it
                            expanded.value = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        } else {
            MyTextField(
                modifier = Modifier.navigate(navigator, "questionset"),
                value = "在设置中添加问题集",
                onValueChange = { },
                readOnly = true,
                label = { MyText("问题集") },
                trailingIcon = { Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "") }
            )
        }
    }
}