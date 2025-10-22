package org.example.askchain.dto

data class StartRequest(
    val difficulty: Difficulty    // 사용자가 던진 질문
) {
    enum class Difficulty {
        어려움, 중간, 쉬움
    }
}

data class StartResponse(
    val success: Boolean,
    val gameId : String? = null,
    val word: String? = null,
    val error: String? = null
)

