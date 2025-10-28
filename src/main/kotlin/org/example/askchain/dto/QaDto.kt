package org.example.askchain.dto

import io.swagger.v3.oas.annotations.media.Schema

data class QaTestRequest(
    val word: String? = null,       // 비밀 단어 (예: "망원경")
    val gameId: String,
    val question: String    // 사용자가 던진 질문
)

data class QaRequest(
    //val word: String? = null,       // 비밀 단어 (예: "망원경")
    @Schema(description = "게임 ID")
    val gameId: String,
    @Schema(description = "질문 or 정답")
    val question: String    // 사용자가 던진 질문
)

data class QaResponse(
    @Schema(description = "성공 여부")
    val success: Boolean,
    @Schema(description = "진행한 질문 갯수")
    val cnt: Int? = null,
    @Schema(description = "게임 ID")
    val gameId: String? = null,
    @Schema(description = "정답 단어 : 마지막 질문 또는 정답 시에 노출")
    val word: String? = null,
    @Schema(description = "질문 내용")
    val question: String? = null,
    @Schema(description = "대답 예/아니요")
    val answer: String? = null,
    @Schema(description = "게임 종료 여부")
    val gameOver: Boolean = false,
    @Schema(description = "남은 질문 갯수")
    val remain: Int? = null,
    @Schema(description = "에러")
    val error: String? = null
)

data class GuessRequest(val gameId: String, val guess: String)
data class GuessResponse(
    val success: Boolean,
    val gameId: String? = null,
    val correct: Boolean? = null,
    val error: String? = null
)