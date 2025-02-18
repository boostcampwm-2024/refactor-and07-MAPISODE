package com.boostcamp.mapisode.home.ai

import com.boostcamp.mapisode.home.common.OptionType
import com.boostcamp.mapisode.ui.base.UiIntent

sealed class RecommendationIntent : UiIntent {
	data class OptionClick(val optionId: OptionType) : RecommendationIntent()
	data object ShowMapType : RecommendationIntent()
	data object ShowListType : RecommendationIntent()
}
