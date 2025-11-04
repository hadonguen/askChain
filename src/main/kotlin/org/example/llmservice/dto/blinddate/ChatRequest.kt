package org.example.llmservice.dto.blinddate

data class ChatRequest(
    val sessionId: String,
    val userMessage: String
)