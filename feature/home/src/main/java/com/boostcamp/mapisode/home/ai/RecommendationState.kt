package com.boostcamp.mapisode.home.ai

import com.boostcamp.mapisode.home.common.OptionType
import com.boostcamp.mapisode.home.common.ResultEpisode
import com.boostcamp.mapisode.home.common.ResultViewType
import com.boostcamp.mapisode.ui.base.UiState

data class RecommendationState (
	val type: OptionType = OptionType.NONE,
	val isOptionSelected: Boolean = false,
	val resultViewType: ResultViewType = ResultViewType.LIST_VIEW,
	val entireEpisodes: List<ResultEpisode> = emptyList(),
	val resultEpisodes: List<ResultEpisode> = emptyList(),
) : UiState
