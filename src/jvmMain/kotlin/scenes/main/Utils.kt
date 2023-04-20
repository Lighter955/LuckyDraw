package scenes.main

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import model.Group
import model.Student
import kotlin.math.max


fun List<Group>.getAllStudents(): SnapshotStateList<Student> {
    val allStudents = mutableListOf<Student>()
    forEach {
        allStudents += it.members
    }
    return allStudents.toMutableStateList()
}

fun getRepeatCount(optionListSize: Int, isPickStudentStep2: Boolean): Int {
    var count = 100 / optionListSize
    if (isPickStudentStep2) { count /= 2 }
    return max(2, count)
}
