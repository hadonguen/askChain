package org.example.askchain.dto

data class QaRequest(
    val word: String,       // 비밀 단어 (예: "망원경")
    val question: String    // 사용자가 던진 질문
)

data class QaResponse(
    val success: Boolean,
    val word: String,
    val question: String,
    val answer: String,     // "예" | "아니오" | "모르겠어요"
    val error: String? = null
)