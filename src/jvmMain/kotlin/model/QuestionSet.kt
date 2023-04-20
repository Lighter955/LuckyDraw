package model

import kotlinx.serialization.Serializable

@Serializable
data class QuestionSet(
    val name: String,
    val questions: List<Question>
)

@Serializable
data class Question(
    val text: String
)