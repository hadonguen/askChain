package org.example.llmservice.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.llmservice.domain.blinddate.CharacterSelectionStore
import org.example.llmservice.dto.blinddate.CharacterGenerationRequest
import org.example.llmservice.dto.blinddate.CharacterSession
import org.example.llmservice.dto.blinddate.CharacterSessionResponse
import org.example.llmservice.dto.blinddate.ChatRequest
import org.example.llmservice.dto.blinddate.ChatResponse
import org.example.llmservice.dto.blinddate.ChatTurn
import org.example.llmservice.llm.BlindDateLlmCall
import org.springframework.stereotype.Service
import java.util.*

@Service
class BlindDateService(
    private val blindDateLlmCall: BlindDateLlmCall,
    private val characterSelectionStore: CharacterSelectionStore,
) {
    private val objectMapper = jacksonObjectMapper()

    fun generateCharacter(req: CharacterGenerationRequest): CharacterSessionResponse {
        // 1) 이름 생성 (GPT 호출)
        val name = blindDateLlmCall.generateName(req)

        // 2) 나이 랜덤
        val age = blindDateLlmCall.randomAge(req.ageRange)

        // 3) system 프롬프트 생성
        val systemPrompt = blindDateLlmCall.buildSystemPrompt(
            name = name,
            age = age,
            config = req,
            affinity = 50,
            userName = null
        )


        // 4) 세션 ID 생성 + 메모리에 저장
        val sessionId = UUID.randomUUID().toString()
        val session = CharacterSession(
            sessionId = sessionId,
            name = name,
            age = age,
            config = req,
            userName = null,
            systemPrompt = systemPrompt
        )
        characterSelectionStore.save(session)

        val sessionResponse = CharacterSessionResponse(
            sessionId = sessionId,
            name = name,
            age = age,
            config = req
        )

        // 5) 클라이언트에 sessionId + 캐릭 이름/나이 리턴
        return sessionResponse
    }

    fun chat(req: ChatRequest): ChatResponse {
        val session = characterSelectionStore.findById(req.sessionId)
            ?: throw IllegalArgumentException("세션 없음: ${req.sessionId}")

        // ★ 매 턴마다 현재 호감도 기준으로 system 프롬프트 새로 생성
        val systemPrompt = blindDateLlmCall.buildSystemPrompt(
            name = session.name,
            age = session.age,
            config = session.config,
            affinity = session.affinity,
            userName = null
        )
        val userPrompt = blindDateLlmCall.buildTurnPrompt(session, req.userMessage)

        // 2) GPT 호출 (JSON 문자열 기대)
        val rawJson = blindDateLlmCall.chat(systemPrompt, userPrompt)

        // 3) JSON 파싱
        val parsed = objectMapper.readTree(rawJson)
        val reply = parsed["reply"]?.asText() ?: ""
        val deltaAffinity = parsed["deltaAffinity"]?.asInt() ?: 0

        // 4) 사용자이름 저장
        val callerName = session.userName?: blindDateLlmCall.detectUserNameViaLlm(reply);
        session.userName = callerName

        // 5) 호감도 업데이트 (0~100 클램핑)
        val newAffinity = (session.affinity + deltaAffinity).coerceIn(0, 100)
        session.affinity = newAffinity

        // 6) 히스토리 추가
        session.history.add(
            ChatTurn(
                userMessage = req.userMessage,
                characterReply = reply,
                affinityAfter = newAffinity
            )
        )

        // 7) 세션 저장
        characterSelectionStore.save(session)

        // 8) 클라이언트 응답
        return ChatResponse(
            characterReply = reply,
            affinity = newAffinity
        )
    }

}