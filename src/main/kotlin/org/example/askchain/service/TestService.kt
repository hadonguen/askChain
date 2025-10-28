package org.example.askchain.service

import org.example.askchain.dto.QaRequest
import org.example.askchain.dto.QaResponse
import org.example.askchain.dto.QaTestRequest
import org.example.askchain.llm.LlmCall
import org.springframework.ai.chat.model.ChatModel
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TestService(
    private val chatModel: ChatModel,
    private val llmCall: LlmCall

) {
    fun askTest(prompt: String): String{
        return chatModel.call(prompt)
    }

    fun testGenerateWord(): Map<String, Any?> {
        val resultMap = try {
            val difficulty = "어려움"
            val word = llmCall.generateWord("어려움")
            return mapOf(
                "success" to true,
                "word" to word,
                "difficulty" to difficulty,
                "timestamp" to Instant.now().toString()
            )
        }catch (e: Exception){
           mapOf("success" to false, "error" to e.message)
        }
        return resultMap
    }

    fun testAnswerYesNoForWord(req: QaTestRequest): QaResponse {

        val answer = req.word?.let { llmCall.processTurn(it.trim(), req.question.trim()) }

        val resultMap = try {
            QaResponse(
                success = true,
                word = req.word,
                question = req.question,
                answer = answer
            )
        }catch (e : Exception){
            QaResponse(
                success = false,
                word = req.word,
                question = req.question,
                answer = answer,
                error = e.toString()
            )
        }
        return resultMap
    }


}