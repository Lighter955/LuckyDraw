package model

import kotlinx.serialization.Serializable

@Serializable
data class Class(
    val name: String,
    val groups: MutableList<Group>
)

@Serializable
data class Group(
    val name: String,
    val members: List<Student>
)

@Serializable
data class Student(
    val num: Long,
    val name: String
)