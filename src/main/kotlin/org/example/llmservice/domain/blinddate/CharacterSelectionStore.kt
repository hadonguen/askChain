package org.example.llmservice.domain.blinddate

import org.example.llmservice.dto.blinddate.CharacterSession
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

interface CharacterSessionRepository {
    fun save(session: CharacterSession): CharacterSession
    fun findById(sessionId: String): CharacterSession?
}

@Component
class CharacterSelectionStore : CharacterSessionRepository{
    private val store = ConcurrentHashMap<String, CharacterSession>()

    override fun save(session: CharacterSession): CharacterSession {
        store[session.sessionId] = session
        return session
    }

    override fun findById(sessionId: String): CharacterSession? =
        store[sessionId]

}