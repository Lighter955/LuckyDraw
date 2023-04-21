import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.extensions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import model.Class
import model.Group
import model.QuestionSet
import net.harawata.appdirs.AppDirsFactory

class AppState(
    private val classStore: KStore<List<Class>>,
    private val questionSetStore: KStore<List<QuestionSet>>,
    val scope: CoroutineScope,
    val classList: SnapshotStateList<Class> = runBlocking {
        classStore.getOrEmpty().toMutableStateList()
    },
    val questionSetList: SnapshotStateList<QuestionSet> = runBlocking {
        questionSetStore.getOrEmpty().toMutableStateList()
    }
) {
    suspend fun addClass(c: Class) {
        classList.add(c)
        classStore.plus(c)
    }

    suspend fun removeClass(c: Class) {
        classList.remove(c)
        classStore.minus(c)
    }

    suspend fun updateClass(index: Int, newClass: Class) {
        classList.removeAt(index)
        classList.add(index, newClass)
        classStore.mapIndexed { i, c ->
            if (i == index) newClass else c
        }
    }

    suspend fun addGroups(classIndex: Int, groups: MutableList<Group>) {
        val newClass = classStore.get(classIndex)!!
        newClass.groups.addAll(groups)
        updateClass(classIndex, newClass)
    }

    suspend fun removeGroup(classIndex: Int, groupIndex: Int) {
        classList[classIndex].groups.removeAt(groupIndex)
        val newClass = classStore.get(classIndex)!!
        newClass.groups.removeAt(groupIndex)
        classStore.mapIndexed { i, c ->
            if (i == classIndex) newClass else c
        }
    }

    suspend fun updateGroup(classIndex: Int, groupIndex: Int, newGroup: Group) {
        classList[classIndex].groups.removeAt(groupIndex)
        classList[classIndex].groups.add(groupIndex, newGroup)
        val newClass = classStore.get(classIndex)!!
        newClass.groups[groupIndex] = newGroup
        classStore.mapIndexed { i, c ->
            if (i == classIndex) newClass else c
        }
    }

    suspend fun addQuestionSet(q: QuestionSet) {
        questionSetList.add(q)
        questionSetStore.plus(q)
    }

    suspend fun removeQuestionSet(q: QuestionSet) {
        questionSetList.remove(q)
        questionSetStore.minus(q)
    }

    suspend fun updateQuestionSet(index: Int, newQuestionSet: QuestionSet) {
        questionSetList.removeAt(index)
        questionSetList.add(index, newQuestionSet)
        questionSetStore.mapIndexed { i, q ->
            if (i == index) newQuestionSet else q
        }
    }
}

@Composable
fun rememberAppState(
    appDir: String = AppDirsFactory.getInstance().getUserDataDir("LuckyDraw", "Data", "LightDev"),
    classStore: KStore<List<Class>> = listStoreOf<Class>("$appDir + class.json"),
    questionSetStore: KStore<List<QuestionSet>> = listStoreOf<QuestionSet>("$appDir + question.json"),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AppState {
    return rememberSaveable(classStore, questionSetStore) {
        AppState(
            classStore,
            questionSetStore,
            coroutineScope
        )
    }
}