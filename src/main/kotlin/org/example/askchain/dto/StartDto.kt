package org.example.askchain.dto

import io.swagger.v3.oas.annotations.media.Schema

data class StartRequest(
    @Schema(description = "난이도 [어려움, 중간, 쉬움]")
    val difficulty: Difficulty    // 사용자가 던진 질문
) {
    enum class Difficulty {
        어려움, 중간, 쉬움
    }
}

data class StartResponse(
    @Schema(description = "성공 여부")
    val success: Boolean,
    @Schema(description = "게임 ID")
    val gameId : String? = null,
    //val word: String? = null,
    @Schema(description = "에러")
    val error: String? = null
)

