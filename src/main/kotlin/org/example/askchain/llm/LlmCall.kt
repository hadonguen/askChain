package org.example.askchain.llm

import org.example.askchain.service.RecentWords
import org.example.askchain.util.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Component
import java.util.*

@Component
class LlmCall (
    private val chat: ChatClient,
    private val recentWords: RecentWords
){
    private val log = LoggerFactory.getLogger(LlmCall::class.java)

    fun generateWord(difficulty: String): String? {//문제단어 추출
        val ban = recentWords.snapshot().joinToString(", ")

        val systemPrompt = """
            너는 스무고개 게임의 출제자다.
            아래 규칙에 맞는 한국어 ‘일반 명사’ 한 단어를 무작위로 고른다.

            [난이도 지침]
            - 쉬움: 매우 흔한 일상어 금지
            - 중간: 비교적 친숙하지만 즉답은 어려운 어휘
            - 어려움: 비교적 드물지만 일반인이 아는 구체 명사
            이번 요청은 난이도: $difficulty 으로 선택하라.

            [출력 규칙]
            1) 한 단어만, 공백/따옴표/기호 없이 출력
            2) 추상/감정/학문명 금지
            3) 과도한 전문용어 금지
            4) 고유명사 원칙적 금지(예외는 널리 아는 것만)
            5) 금지목록: $ban
            [nonce:${java.util.UUID.randomUUID()}]
        """.trimIndent()

        val opts = OpenAiChatOptions.builder()
            .temperature(0.9)
            .topP(0.95)
            .presencePenalty(0.6)
            .frequencyPenalty(0.6)
            .maxTokens(8)
            .build()

        repeat(5) {
            val word = chat.prompt()
                .system(systemPrompt)
                .user("단어 하나만 출력해.")
                .options(opts)
                .call()
                .content()
                ?.trim() ?:""

            if (StringUtils.isValidWord(word) && !recentWords.contains(word)) {
                recentWords.add(word)
                return word
            }
        }
        return "보온병"
    }

    /**
     * 입력이 '정답시도'인지/ '질문'인지 판별 후,
     * - 정답시도: 로컬에서 정답/오답 판정
     * - 질문: 예/아니오/가이드 문구로 응답
     */
    enum class GuessClass { GUESS, QUESTION, AMBIGUOUS }
    enum class Mode { RULE_FIRST, LLM_FIRST }

    fun processTurn(secretWord: String, userInput: String, mode: Mode = Mode.LLM_FIRST): String {
        val cls = when (mode) {
            Mode.RULE_FIRST -> {
                val r = isGuessRule(userInput)
                if (r == GuessClass.AMBIGUOUS) classifyByLlm(userInput) else r
            }
            Mode.LLM_FIRST -> {
                // 아주 확실한 두 케이스만 규칙에서 처리하고, 나머지는 전부 LLM에 위임
                val quick = quickCheck(userInput)
                if (quick == GuessClass.AMBIGUOUS) classifyByLlm(userInput) else quick
            }
        }

        return when (cls) {
            GuessClass.GUESS -> {
                if (normalize(userInput) == normalize(secretWord)) "정답입니다!"
                else "오답입니다."
            }
            GuessClass.QUESTION -> answerYesNoByLlm(secretWord, userInput)
            GuessClass.AMBIGUOUS -> answerYesNoByLlm(secretWord, userInput) // 안전하게 질문 취급
        }
    }


    /** LLM-first용 초간단 체크: 확실한 경우만 걸러내고 나머지는 LLM에게 */
    private fun quickCheck(input: String): GuessClass {
        val s = input.trim()
        if (s.isEmpty()) return GuessClass.AMBIGUOUS

        val lower = s.lowercase()
        // 1) 물음표 있으면 질문
        if (s.contains('?')) return GuessClass.QUESTION

        // 2) 명시적 정답 시도 문구 포함 시 GUESS
        val explicitGuess = listOf("정답은", "정답:", "정답 ", "답은", "답:", "answer is")
            .any { lower.contains(it) }
        if (explicitGuess) return GuessClass.GUESS

        // 나머지는 LLM에게
        return GuessClass.AMBIGUOUS
    }

    // ──────────────────────────────────────────────────────────────────────
    // 1) 규칙 기반 판단 (빠르고 공짜)
    // ──────────────────────────────────────────────────────────────────────

    private fun isGuessRule(input: String): GuessClass {
        val s = input.trim()
        log.info("질문/답 1차  룰베이스 판단- $s")

        if (s.isEmpty()) return GuessClass.AMBIGUOUS

        val lower = s.lowercase(Locale.getDefault())

        // 명시적 정답 트리거
        val explicit = listOf("정답", "정답은", "답:", "답은", "answer is", "정답:", "is the answer")
            .any { lower.contains(it) }
        if (explicit) return GuessClass.GUESS

        // 물음표 있으면 질문 취급
        if (s.contains('?')) return GuessClass.QUESTION

        // 의문형 어미로 끝나면 질문
        val interrogativeEndings = listOf("냐", "냐요", "냐고", "니", "니요", "나요", "입니까", "인가요", "인가", "습니까", "야?", "가?")
        if (interrogativeEndings.any { lower.endsWith(it) }) return GuessClass.QUESTION

        // 단답 명사(1~2 토큰)면 정답 시도로 간주
        val tokens = s.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (tokens.size <= 2) {
            val norm = normalize(s)
            val looksLikeNoun = norm.matches(Regex("^[가-힣a-z0-9]{2,16}$"))
            if (looksLikeNoun) return GuessClass.GUESS
        }

        return GuessClass.AMBIGUOUS
    }

    private fun normalize(s: String): String =
        s.trim()
            .lowercase(Locale.getDefault())
            .replace(Regex("[\\p{Punct}\\s]+"), "")
            .replace("입니다", "")
            .replace("이다", "")
            .replace("요", "")
            .replace("정답은", "")
            .replace("정답", "")
            .replace("답은", "")
            .replace("답", "")

    // ──────────────────────────────────────────────────────────────────────
    // 2) LLM 분류기 (질문/정답시도)
    // ──────────────────────────────────────────────────────────────────────

    private fun classifyByLlm(userInput: String): GuessClass {
        val systemPrompt = """
            너는 스무고개 게임의 판정관이다.
            아래 입력이 '질문'인지 '정답시도'인지 분류하라.
            
            규칙:
            - 물음표가 있거나 의문형이면 '질문'
            - '정답은', '답은', 'answer is' 등 정답 서술 표현이 있거나, 명사 단답이면 '정답시도'
            - 오직 다음 중 하나만 출력: 질문 | 정답시도
        """.trimIndent()

        val opts = OpenAiChatOptions.builder()
            .model("gpt-4o-mini")
            .temperature(0.0)
            .maxTokens(4)
            .build()

        val out = chat.prompt()
            .system(systemPrompt)
            .user(userInput)
            .options(opts)
            .call()
            .content()
            ?.trim().orEmpty()

        log.info("질문/답 2차  LLM 판단- $out")

        return when (out) {
            "정답시도" -> GuessClass.GUESS
            "질문" -> GuessClass.QUESTION
            else -> GuessClass.AMBIGUOUS
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 3) 예/아니오 LLM 응답
    // ──────────────────────────────────────────────────────────────────────

    private fun answerYesNoByLlm(secretWord: String, question: String): String {
        val systemPrompt = """
            너는 스무고개 게임의 진행자다.
            비밀 정답은 "${secretWord}" 이다. 절대로 정답 단어를 직접 말하지 마라.
            사용자의 질문에 대해 다음 3가지 중 하나로만, 정확히 그 단어로만 답한다:
            - 예
            - 아니오
            - 모르겠어요 (질문이 애매하거나 예/아니오로 대답할 수 없는 경우)
            
            규칙:
            1) 예/아니오로 대답할 수 없는 질문이면 "모르겠어요"라고 답하라.
            2) 사족, 설명, 이모지 금지. 위 세 단어 외에는 어떤 텍스트도 출력하지 마라.
            3) 정답 노출 금지. 정답을 유추할 수 있는 힌트 문장 금지.
        """.trimIndent()

        val opts = OpenAiChatOptions.builder()
            .model("gpt-4o-mini")
            .temperature(0.2)
            .maxTokens(6)
            .build()

        val raw = chat.prompt()
            .system(systemPrompt)
            .user(question)
            .options(opts)
            .call()
            .content()
            ?.trim().orEmpty()

        return when (raw) {
            "예", "아니오" -> raw
            "모르겠어요" -> "예, 아니오로 답할 수 있는 질문만 가능합니다."
            else -> "예, 아니오로 답할 수 있는 질문만 가능합니다."
        }
    }
    //배포 테스트
}