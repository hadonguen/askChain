package org.example.llmservice.dto.blinddate

import java.time.Instant

class CharacterSessionResponse(
    val sessionId: String,
    val name: String,
    val age: Int,
    val config: CharacterGenerationRequest,
    val createdAt: Instant = Instant.now()
)