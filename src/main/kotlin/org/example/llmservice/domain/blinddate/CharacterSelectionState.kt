package org.example.llmservice.domain.blinddate

enum class GenderOption(val label: String) {
    MALE("남성"),
    FEMALE("여성"),
    ANDROGYNOUS("중성적"),
    UNSPECIFIED("비지정"),
    RANDOM("랜덤")
}

enum class AgeRangeOption(val label: String) {
    TWENTIES_EARLY("20대 초반"),
    TWENTIES_LATE("20대 후반"),
    THIRTIES_EARLY("30대 초반"),
    THIRTIES_LATE("30대 중반"),
    RANDOM("랜덤")
}

enum class PersonalityOption(val label: String) {
    EXTROVERT("외향적"),
    INTROVERT("내향적"),
    TSUNDERE("츤데레"),
    EMPATHIC("공감형"),
    HUMOROUS("유머러스"),
    LOGICAL("이성적"),
    EMOTIONAL("감정적"),
    RANDOM("랜덤")
}

enum class ConversationStyleOption(val label: String) {
    SERIOUS("진지함"),
    PLAYFUL("장난스러움"),
    OBSERVER("관찰자형"),
    REACTIVE("리액션형"),
    LOGIC_DRIVEN("논리형"),
    RANDOM("랜덤")
}

enum class SpeakingToneOption(val label: String) {
    FORMAL("존댓말"),
    SEMI_FORMAL("반존대"),
    CASUAL("반말"),
    CASUAL_HUMOR("유머 섞인 캐주얼"),
    POETIC("서정적"),
    RANDOM("랜덤")
}

enum class DatingSituationOption(val label: String) {
    FIRST_MEET("첫 만남"),
    AFTER_WORK_BEER("퇴근 후 맥주"),
    WEEKEND_CAFE("주말 카페"),
    CINEMA("영화관"),
    LUNCH_DATE("점심 소개팅"),
    RANDOM("랜덤")
}

enum class InterestOption(val label: String) {
    MUSIC("음악"),
    MOVIE("영화"),
    GAME("게임"),
    TRAVEL("여행"),
    GOURMET("미식"),
    WORK("일"),
    HOBBY("취미"),
    PET("반려동물"),
    TECH("기술"),
    ART("예술"),
    BOOK("독서"),
    RANDOM("랜덤") // ["RANDOM"] 하나만 들어오면 '관심사 랜덤 선택' 의미
}

enum class DifficultyOption(val label: String) {
    NORMAL("기본형"),
    ACTIVE("활발형"),
    HARD("고난이도형(츤데레·밀당 강함)"),
    PASSIVE("수동형(리액션 적음)"),
    RANDOM("랜덤")
}

data class CharacterSelectionState(
    val selectionId: String,
    var gender: GenderOption? = null,
    var ageRange: AgeRangeOption? = null,
    var personalityType: PersonalityOption? = null,
    var conversationStyle: ConversationStyleOption? = null,
    var speakingTone: SpeakingToneOption? = null,
    var datingSituation: DatingSituationOption? = null,
    var interests: MutableList<InterestOption> = mutableListOf(),
    var difficulty: DifficultyOption? = null
) {
    fun isComplete(): Boolean =
        gender != null &&
                ageRange != null &&
                personalityType != null &&
                conversationStyle != null &&
                speakingTone != null &&
                datingSituation != null &&
                difficulty != null &&
                interests.isNotEmpty()
}