package org.example.askchain.dto

data class QaRequest(
    val word: String? = null,       // 비밀 단어 (예: "망원경")
    val gameId: String,
    val question: String    // 사용자가 던진 질문
)

data class QaResponse(
    val success: Boolean,
    val cnt: Int? = null,
    val gameId: String? = null,
    val word: String? = null,
    val question: String? = null,
    val answer: String? = null,
    val gameOver: Boolean = false,
    val remain: Int? = null,
    val error: String? = null
)

data class GuessRequest(val gameId: String, val guess: String)
data class GuessResponse(
    val success: Boolean,
    val gameId: String? = null,
    val correct: Boolean? = null,
    val error: String? = null
)