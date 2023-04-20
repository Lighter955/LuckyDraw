import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.russhwolf.settings.Settings
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.Class
import model.QuestionSet

class AppState(
    val settings: Settings,
    val classList: SnapshotStateList<Class>,
    val questionSetList: SnapshotStateList<QuestionSet>
)

@Composable
fun rememberAppState(
    settings: Settings = Settings(),
    classListJson: String = settings.getString("class", "[]"),
    classList: SnapshotStateList<Class> = Json.decodeFromString<List<Class>>(classListJson).toMutableStateList(),
    questionSetListJson: String = settings.getString("questionset", "[]"),
    questionSetList: SnapshotStateList<QuestionSet> = Json.decodeFromString<List<QuestionSet>>(questionSetListJson).toMutableStateList()
): AppState {
    return rememberSaveable(settings) { AppState(settings, classList, questionSetList) }
}