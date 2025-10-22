package org.example.askchain.service

import org.example.askchain.dto.QaResponse
import org.example.askchain.dto.StartRequest
import org.example.askchain.dto.StartResponse
import org.example.askchain.game.GameStore
import org.example.askchain.llm.LlmCall
import org.springframework.stereotype.Service

@Service
class AskChainGameService(
    private val llmCall: LlmCall,
    private val gameStore: GameStore
) {
    fun gameStart(req: StartRequest): StartResponse {
        val word = llmCall.generateWord(req.difficulty.toString())
        val state = gameStore.create(secret = word ?: "")

        val resultMap = try {
            StartResponse(
                success = true,
                //word = word,
                gameId = state.id
            )
        }catch (e : Exception){
            StartResponse(
                success = false,
                error = e.toString()
            )
        }
        return resultMap
    }

    fun ask(gameId: String, question: String): QaResponse {
        val state = gameStore.get(gameId) ?: return QaResponse(
            success = false, gameId = null, word = null, question = question, answer = "", error = "INVALID_GAME"
        )
        // 만료 검사(있으면)
        if (state.isExpired()) {
            gameStore.remove(gameId)
            return QaResponse(
                success = false,
                gameId = gameId,
                word = "",
                question = question,
                answer = "",
                error = "GAME_EXPIRED"
            )
        }

        state.questionCount++
        val remain = (state.maxQuestions - state.questionCount).coerceAtLeast(0)
        val ans = llmCall.processTurn(state.secretWord, question)

        // 20번째(=maxQuestions) 질문 처리 후에는 정답 공개 및 게임 제거
        return if (state.questionCount >= state.maxQuestions) {
            val secret = state.secretWord
            gameStore.remove(gameId) // 게임 종료
            QaResponse(
                cnt = state.questionCount,
                success = true,
                gameId = gameId,
                word = state.secretWord,                // 굳이 안보여주면 빈값
                question = question,
                answer = ans,
                remain = 0,
                gameOver = true,
                error = null
            )
        } else {
            // 아직 게임 계속
            gameStore.save(state)
            QaResponse(
                cnt = state.questionCount,
                success = true,
                gameId = gameId,
                word = "",
                question = question,
                answer = ans,
                remain = remain,
                gameOver = false,
                error = null
            )
        }
    }
}