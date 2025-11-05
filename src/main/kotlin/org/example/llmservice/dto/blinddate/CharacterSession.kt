package org.example.llmservice.dto.blinddate

import java.time.Instant

data class CharacterSession(
    val sessionId: String,
    val name: String,
    val age: Int,
    var userName: String? = null,
    val systemPrompt: String,
    val config: CharacterGenerationRequest,
    var affinity: Int = 50, // 0~100 시작점
    val history: MutableList<ChatTurn> = mutableListOf(),
    val createdAt: Instant = Instant.now()
)