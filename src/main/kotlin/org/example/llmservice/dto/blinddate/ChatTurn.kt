package org.example.llmservice.dto.blinddate

import java.time.Instant

data class ChatTurn (
    val userMessage: String,
    val characterReply: String,
    val affinityAfter: Int,
    val createdAt: Instant = Instant.now()
)