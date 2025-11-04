package org.example.llmservice.dto.blinddate

import org.example.llmservice.domain.blinddate.*

data class CharacterConfig(
    val gender: GenderOption,
    val ageRange: AgeRangeOption,
    val personalityType: PersonalityOption,
    val conversationStyle: ConversationStyleOption,
    val speakingTone: SpeakingToneOption,
    val datingSituation: DatingSituationOption,
    val interests: List<InterestOption>,
    val difficulty: DifficultyOption
)