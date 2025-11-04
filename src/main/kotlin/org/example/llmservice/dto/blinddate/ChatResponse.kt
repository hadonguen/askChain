package org.example.llmservice.dto.blinddate

data class ChatResponse(
    val characterReply: String,
    val affinity: Int  // 이번 턴 이후의 현재 호감도
)