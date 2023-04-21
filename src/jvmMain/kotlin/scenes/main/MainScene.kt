package scenes.main

import AppState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import model.Group
import model.Question
import model.Student
import kotlin.math.abs

@Composable
fun MainScene(
    appState: AppState,
    classIndex: Int,
    questionsetIndex: Int,
    navigateBack: () -> Unit,
    groups: MutableList<Group> = appState.classList[classIndex].groups.toMutableList(),
    questions: List<Question> = appState.questionSetList[questionsetIndex].questions
) = MaterialTheme {
    val pickCount = remember { mutableStateOf(0) }
    val canAddPickCount = remember { mutableStateOf(true) }
    val allStudents = remember { groups.getAllStudents() }
    val firstOffset = remember { mutableStateOf<Int?>(null) }
    val pickGroupAnimateState = remember { mutableStateOf(false) }
    val selectedGroupIndex = remember { mutableStateOf<Int?>(null) }
    val allStudentsAnimateState = remember { mutableStateOf(false) }
    val pickStudentAnimateState = remember { mutableStateOf(false) }
    val pickQuestionAnimateState = remember { mutableStateOf(false) }
    val pickStudentStepState = remember { mutableStateOf(1) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Picker(
                    modifier = Modifier.weight(1f),
                    optionList = groups,
                    firstOffset = firstOffset,
                    animateState = pickGroupAnimateState,
                    animationSpec = tween(durationMillis = 8000),
                    beforeAnimation = {
                        selectedGroupIndex.value = it
                    }
                )
                if (pickStudentStepState.value > 1) {
                    Picker(
                        modifier = Modifier.weight(1f),
                        optionList = groups[selectedGroupIndex.value!!].members,
                        isPickStudentStep2 = true,
                        firstOffset = firstOffset,
                        animateState = pickStudentAnimateState,
                        animationSpec = tween(durationMillis = 3000, easing = CubicBezierEasing(0.33f, 1.0f, 0.68f, 1.0f))
                    )
                    LaunchedEffect(Unit) {
                        pickStudentAnimateState.value = true
                    }
                } else {
                    Picker(
                        modifier = Modifier.weight(1f),
                        optionList = allStudents,
                        firstOffset = firstOffset,
                        animateState = allStudentsAnimateState,
                        animationSpec = tween(durationMillis = 5000, easing = CubicBezierEasing(0.12f, 0f, 0.39f, 0f)),
                        afterAnimation = { pickStudentStepState.value = 2 }
                    )
                }
            }
            Picker(
                modifier = Modifier.fillMaxWidth(0.75f),
                optionList = questions,
                firstOffset = firstOffset,
                animateState = pickQuestionAnimateState,
                animationSpec = tween(durationMillis = 8000),
                afterAnimation = { if (canAddPickCount.value) {
                    pickCount.value += 1
                } }
            )
            Row {
                if (pickCount.value != groups.size) {
                    TextButton(
                        onClick = { navigateBack() }
                    ) {
                        MyText("返回", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.width(32.dp))
                }
                if (pickCount.value > 0) {
                    OutlinedButton(
                        onClick = {
                            canAddPickCount.value = false
                            pickStudentStepState.value = 1
                            selectedGroupIndex.value = null
                            pickGroupAnimateState.value = true
                            pickQuestionAnimateState.value = true
                            allStudentsAnimateState.value = true
                        },
                        enabled = !pickQuestionAnimateState.value
                    ) {
                        MyText("重新抽取", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.width(32.dp))
                    if (groups.size > 1) {
                        Button(
                            onClick = {
                                canAddPickCount.value = true
                                pickStudentStepState.value = 1
                                groups.removeAt(selectedGroupIndex.value!!)
                                allStudents.clear()
                                allStudents.addAll(groups.getAllStudents())
                                selectedGroupIndex.value = null
                                pickGroupAnimateState.value = true
                                pickQuestionAnimateState.value = true
                                allStudentsAnimateState.value = true
                            },
                            enabled = !pickQuestionAnimateState.value
                        ) {
                            MyText("抽取下一组", style = MaterialTheme.typography.labelLarge)
                        }
                    } else {
                        Button(
                            onClick = { navigateBack() },
                            enabled = !pickQuestionAnimateState.value
                        ) {
                            MyText("完成", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            pickStudentStepState.value = 1
                            selectedGroupIndex.value = null
                            pickGroupAnimateState.value = true
                            allStudentsAnimateState.value = true
                            pickQuestionAnimateState.value = true
                        },
                        enabled = !pickQuestionAnimateState.value
                    ) {
                        MyText("开始抽取", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun Picker(
    modifier: Modifier,
    optionList: List<Any>,
    isPickStudentStep2: Boolean = false,
    repeatCount: Int =  getRepeatCount(optionList.size, isPickStudentStep2),
    firstOffset: MutableState<Int?>,
    animateState: MutableState<Boolean>,
    animationSpec: AnimationSpec<Float>,
    beforeAnimation: (Int?) -> Unit = { },
    afterAnimation: () -> Unit = { }
) {
    val columnSize = remember { mutableStateOf(IntSize.Zero) }
    val selectedIndex = remember { mutableStateOf<Int?>(null) }
    val lazyListState = rememberLazyListState(0)
    LazyColumn (
        modifier = modifier.height(160.dp).onGloballyPositioned { columnSize.value = it.size },
        state = lazyListState,
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = false
    ) {
        items(optionList.size * repeatCount) { globalIndex ->
            val index = globalIndex % optionList.size
            val p = remember {
                derivedStateOf {
                    val currentItemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == globalIndex } ?: return@derivedStateOf 0.5f
                    val itemSize = currentItemInfo.size / 2
                    val offset = currentItemInfo.offset + itemSize - columnSize.value.height / 2
                    (1f - minOf(1f, abs(offset).toFloat() / itemSize) * 0.5f)
                }
            }
            MyText(
                text = if (optionList[index] is Group) {
                    (optionList[index] as Group).name
                } else if (optionList[index] is Student) {
                    (optionList[index] as Student).name
                } else {
                    (optionList[index] as Question).text
                },
                modifier = Modifier.padding(horizontal = 32.dp).alpha(p.value).scale(p.value),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
    LaunchedEffect(lazyListState.firstVisibleItemScrollOffset, animateState.value) {
        if (optionList.isNotEmpty() && !animateState.value) {
            var currentItemInfo: LazyListItemInfo?
            do {
                currentItemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull {
                    (it.offset + it.size / 2 - columnSize.value.height / 2) >= 0
                }
            }
            while (currentItemInfo == null)
            selectedIndex.value = currentItemInfo.index % optionList.size
            val offset = currentItemInfo.offset + currentItemInfo.size / 2 - columnSize.value.height / 2
            lazyListState.scrollBy(offset.toFloat())
            if (firstOffset.value == null) {
                firstOffset.value = offset
            }
        }
    }
    LaunchedEffect(animateState.value) {
        if (optionList.isNotEmpty() && animateState.value) {
            var currentItemInfo: LazyListItemInfo?
            do {
                currentItemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull {
                    (it.offset + it.size / 2 - columnSize.value.height / 2) >= 0
                }
            }
            while (currentItemInfo == null)
            while (firstOffset.value == null) {
                delay(50)
            }
            val index = optionList.size * (repeatCount - 1) - 1 + (0..optionList.lastIndex).random()
            beforeAnimation((index) % optionList.size)
            lazyListState.scrollToItem(0, firstOffset.value!!)
            lazyListState.animateScrollBy((index - 1) * currentItemInfo!!.size * 1f, animationSpec)
            afterAnimation()
            animateState.value = false
        } else {
            animateState.value = false
        }
    }
}