package org.example.llmservice.dto.blinddate

import org.example.llmservice.domain.blinddate.*

data class UpdateSelectionRequest(
    val gender: GenderOption? = null,
    val ageRange: AgeRangeOption? = null,
    val personalityType: PersonalityOption? = null,
    val conversationStyle: ConversationStyleOption? = null,
    val speakingTone: SpeakingToneOption? = null,
    val datingSituation: DatingSituationOption? = null,
    val interests: List<InterestOption>? = null,
    val difficulty: DifficultyOption? = null
)