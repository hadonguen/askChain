package org.example.llmservice.domain.askchain

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class GameStore {
    private val log = LoggerFactory.getLogger(GameStore::class.java)
    private val store = ConcurrentHashMap<String, GameState>()

    fun create(secret: String, maxQ: Int = 20): GameState {
        val state = GameState(secretWord = secret, maxQuestions = maxQ)
        store[state.id] = state
        return state
    }

    fun get(id: String): GameState? = store[id]

    fun save(state: GameState) { store[state.id] = state }

    fun remove(id: String) { store.remove(id) }

    // 1분마다 만료 정리
    @Scheduled(fixedDelay = 60_000)
    fun sweepExpired() {
        val now = Instant.now()
        val removed = store.entries.removeIf { it.value.isExpired(now) }
        if (removed) log.debug("Expired games swept")
    }
}