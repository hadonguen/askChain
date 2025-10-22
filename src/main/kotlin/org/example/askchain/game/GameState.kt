package org.example.askchain.game

import java.time.Instant
import java.util.UUID

data class GameState(
    val id: String = UUID.randomUUID().toString(),
    val secretWord: String,
    val startedAt: Instant = Instant.now(),
    var questionCount: Int = 0,
    val maxQuestions: Int = 20,
    val ttlSeconds: Long = 1800 // 30분
) {
    fun isExpired(now: Instant = Instant.now()) =
        now.isAfter(startedAt.plusSeconds(ttlSeconds))
}