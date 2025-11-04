package org.example.llmservice.dto.blinddate

data class DatingCharacterProfile(
    val name: String,            // 무조건 GPT 랜덤 생성
    val age: Int,                // ageRange에 맞는 세부 나이
    val gender: String,          // "남성" / "여성" ...
    val ageRange: String,        // "20대 후반" 등
    val situation: String,       // "퇴근 후 맥주집에서 첫 소개팅" 같은 한 줄 설명
    val personality: String,     // 1~2문장 성격 설명
    val tone: String,            // "반존대, 가벼운 농담 섞임" 같은 설명
    val interests: List<String>, // 텍스트화된 관심사
    val difficulty: String,      // 분위기 난이도 설명
    val quirks: String,          // 말버릇/습관
    val firstLine: String,       // 첫 대사
    val behaviorGuideline: String // 대화 내내 유지해야 할 태도 설명
)