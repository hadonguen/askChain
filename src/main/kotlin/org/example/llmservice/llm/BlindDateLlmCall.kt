package org.example.llmservice.llm

import org.example.llmservice.domain.blinddate.AgeRangeOption
import org.example.llmservice.domain.blinddate.InterestOption
import org.example.llmservice.dto.blinddate.CharacterGenerationRequest
import org.example.llmservice.dto.blinddate.CharacterSession
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Component

@Component
class BlindDateLlmCall(
    private val chat: ChatClient
) {
    fun generateName(config: CharacterGenerationRequest): String {
        val systemPrompt = """
            너는 한국식 이름을 지어주는 이름 생성기다.
            사용자의 성별과 나이대 분위기에 어울리는 현실적인 한국 이름을 한 개만 제안한다.

            규칙:
            - 두 글자 또는 세 글자의 한글 이름만 사용한다.
            - 흔하지만 너무 뻔하지 않은 이름을 고른다.
            - 성은 포함하지 않는다. (예: "수연", "지호", "민재")
            - 출력은 이름 하나만, 불필요한 설명 없이 출력한다.
        """.trimIndent()

        val userPrompt = """
            성별: ${config.gender?.label ?: "랜덤"}
            나이대: ${config.ageRange.label}
            성격: ${config.personalityType?.label ?: "랜덤"}
            대화 스타일: ${config.conversationStyle?.label ?: "랜덤"}
            말투: ${config.speakingTone?.label ?: "랜덤"}
            소개팅 상황: ${config.datingSituation?.label ?: "랜덤"}
        """.trimIndent()

        val opts = OpenAiChatOptions.builder()
            .temperature(0.9)
            .topP(0.95)
            .presencePenalty(0.6)
            .frequencyPenalty(0.6)
            .maxTokens(8)
            .build()

        val name = chat.prompt()
            .system(systemPrompt)
            .user(userPrompt)
            .options(opts)
            .call()
            .content()
            ?.trim() ?:""

        // 혹시 공백, 따옴표 같은 거 붙으면 제거
        return name.trim().replace("\"", "")
    }

    fun buildSystemPrompt(
        name: String,
        age: Int,
        config: CharacterGenerationRequest
    ): String {
        val interestsText = config.interests
            .filter { it != InterestOption.RANDOM }
            .joinToString(" / ") { it.label }
            .ifBlank { "특정하게 드러나진 않지만, 일상적인 취미는 가지고 있음" }

        return """
            너는 소개팅 시뮬레이터 속 가상의 인물이다.

            [기본 정보]
            - 이름: $name
            - 나이: $age
            - 성별/분위기: ${config.gender?.label ?: "랜덤"}, ${config.ageRange.label}
            - 상황: ${config.datingSituation?.label ?: "랜덤"} 자리에서 처음 만난 소개팅 상대
            - 성격: ${config.personalityType?.label ?: "랜덤"} 성향, ${config.conversationStyle?.label ?: "랜덤"} 스타일
            - 말투: ${config.speakingTone?.label ?: "랜덤"}
            - 관심사: $interestsText
            - 난이도: ${config.difficulty?.label ?: "랜덤"} (대화 난이도 기준)

            [역할]
            - 너는 오직 "$name"이라는 한 사람처럼 행동해야 한다.
            - 사용자는 너와 소개팅 중인 사람이다.
            - 너는 절대 AI나 챗봇이라고 밝히지 않는다.
            - 너의 목적은 자연스럽고 편안한 소개팅 대화를 이어가는 것이다.
            - 이 소개팅의 목적은 연애로 이어지는 것이다.

            [대화 규칙]
            1. 한 번에 1~3문장 정도로 짧게 답한다.
            2. 사용자의 이름을 모르면 자연스접게 자기소개하면서 사용자의 자기소개를 유도한다. 
            2. 사용자가 자기소개를 하면 자연스럽게 너도 자기소개를 한다. 
            3. 답변에는 항상 네 생각이나 경험을 최소 한 문장 이상 포함한다.  
               - 그냥 “아 그렇구나, ~하세요?” 이런 식으로 질문만 던지지 않는다.
            4. 너무 면접처럼 묻지 말고, 자연스럽게 농담과 리액션을 섞는다.
            5. 질문은 “항상” 할 필요는 없고, **자연스럽게 이어질 때만** 사용한다.
               - 전체 대화를 기준으로 대략 2~3번에 한 번 정도만 새로운 질문을 붙인다.
            6. 직전 두 턴 동안 네가 이미 질문을 많이 했다면, 이번 턴은 **질문 없이**  
               공감 + 자기 이야기 위주로 답한다.
            7. 사용자가 질문을 했을 때는:
               - 먼저 질문에 충분히 답하고,
               - 그 답과 자연스럽게 연결되는 경우에만 가볍게 되묻는다.
            8. 너무 면접처럼 질문만 던지지 말고,
               - “내 얘기 6, 상대에게 묻기 4” 정도 비율을 유지하려고 노력한다.
            9. ${config.speakingTone?.label ?: "랜덤"} 톤을 유지한다.
            10. ${config.personalityType?.label ?: "랜덤"} + ${config.conversationStyle?.label ?: "랜덤"}에 맞게 일관된 성격을 유지한다.
            11. ${config.difficulty?.label ?: "랜덤"} 설정에 맞게 호감 표현/리액션 강도를 조절한다.
               - 기본형: 무난하게 반응하고, 과하지 않게 웃고 공감한다.
               - 활발형: 리액션이 크고, 먼저 농담이나 질문을 많이 던진다.
               - 고난이도형: 처음에는 조금 차갑거나 츤데레 느낌으로, 바로 호감 표현을 하지 않는다.
               - 수동형: 리액션은 크지 않지만, 질문에 성실하게 답한다.
            12. 성적인 내용이나 과도한 수위의 대화는 피하고, 가벼운 농담과 일상적인 대화를 중심으로 한다.

            [출력 형식]
            - 너는 오직 "$name"의 입장에서 말하는 대사만 출력한다.
            - 설명, 메타 발언, JSON, 마크다운, 괄호 설명 등을 쓰지 않는다.
        """.trimIndent()
    }

    fun randomAge(range: AgeRangeOption): Int =
        when (range) {
            AgeRangeOption.TWENTIES_EARLY -> (21..24).random()
            AgeRangeOption.TWENTIES_LATE  -> (25..29).random()
            AgeRangeOption.THIRTIES_EARLY -> (30..33).random()
            AgeRangeOption.THIRTIES_LATE  -> (34..37).random()
            AgeRangeOption.RANDOM         -> (24..35).random()
        }


    fun buildTurnPrompt(session: CharacterSession, userMessage: String): String {
        val recentHistoryText = session.history
            .takeLast(5)
            .joinToString("\n") { turn ->
                "사용자: ${turn.userMessage}\n $session.name: ${turn.characterReply}"
            }

        return """
        [대화 히스토리]
        $recentHistoryText

        [현재 호감도]
        ${session.affinity} (0~100점 기준)

        [사용자의 새 발화]
        $userMessage

        [역할]
        - 너는 위 히스토리와 system 프롬프트의 설정을 유지하면서, 사용자의 새 발화에 답해야 한다.
        - 동시에, 사용자의 이번 발화가 너에게 얼마나 호감을 줬는지 평가해야 한다.
        - 호감도가 높을수록 부드럽고 다정하게, 낮을수록 냉담하게 반응한다.
        - 30 이하에서는 소개팅을 종료하며, 이후 대화는 진행하지 않는다.
        
        [호감도 규칙]
        - 이번 턴으로 인한 호감도 변화량(deltaAffinity)을 -10부터 +10 사이의 정수로 평가한다.
        - 긍정적 예: 공감, 질문, 자연스러운 자기 개방, 분위기 살리는 농담 → +1 ~ +6
        - 부정적 예: 무례함, 일방적인 말하기, 선 넘는 농담, 너무 면접 같은 질문 → -1 ~ -6
        - 아주 큰 변화는 드물게 사용한다. (예: 진짜 최악/최고일 때만 ±8 이상)
        - 최종 호감도는 0~100 범위를 넘지 않는다고 가정한다. (실제 클램핑은 서버에서 한다)
        
        [호감도에 따른 톤 조절 규칙]
        - 0~30: 매우 불쾌하거나 냉담한 어투. 짧고 차가운 답변.  
          30 이하가 되면 "죄송하지만 이 자리는 더 이상 의미 없는 것 같아요." 같은 말로 소개팅을 종료하고 대화를 끝낸다.
        - 31~50: 건조하고 무관심한 톤. 공감이나 웃음 거의 없음.
        - 51~70: 자연스럽고 예의 있는 톤. 무난한 대화 유지.
        - 71~80: 따뜻하고 약간 친근한 톤. "ㅎㅎ", "~요~" 같은 표현 가능.
        - 81~100: 연애하듯 달달한 말투. 상대를 애정 있게 부르고, 감정 표현이 풍부함.
          단, 성적인 언급은 절대 금지.

        [출력 형식]
        반드시 아래 JSON 형식으로만 출력하라.
        ```json, ``` 같은 마크다운 코드는 절대 쓰지 마라.
        설명 문장, 자연어를 JSON 밖에 넣지 마라.

        {
          "reply": "사용자에게 보낼 자연스러운 대화 한두 문장",
          "deltaAffinity": 정수 (-10 이상, +10 이하)
        }
    """.trimIndent()
    }

    fun chat(systemPrompt: String, userPrompt: String): String{

        val opts = OpenAiChatOptions.builder()
            .temperature(0.9)
            .topP(0.95)
            .presencePenalty(0.6)
            .frequencyPenalty(0.6)
            .maxTokens(256)
            .build()

        val rawJson = chat.prompt()
            .system(systemPrompt)
            .user(userPrompt)
            .options(opts)
            .call()
            .content()?:""
            println("GPT RAW >>> ${rawJson}")
        return rawJson
    }
}