package com.boostcamp.mapisode.home.ai

import com.boostcamp.mapisode.home.common.OptionType
import com.boostcamp.mapisode.home.common.ResultViewType
import com.boostcamp.mapisode.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecommendationViewmodel @Inject constructor(

) : BaseViewModel<RecommendationIntent, RecommendationState, RecommendationSideEffect>(
	RecommendationState(),
) {
	override fun onIntent(intent: RecommendationIntent) {
		when (intent) {
			is RecommendationIntent.OptionClick -> handleOptionClick(intent.optionId)
			RecommendationIntent.ShowListType -> showListType()
			RecommendationIntent.ShowMapType -> showMapType()
		}
	}

	private fun handleOptionClick(type: OptionType) {
		intent {
			copy(
                type = type,
                isOptionSelected = true,
            )
		}
	}

	private fun showListType() {
		Timber.e("ResultViewType.LIST_VIEW")
		intent {
			copy(
                resultViewType = ResultViewType.LIST_VIEW,
            )
		}
	}

	private fun showMapType() {

		Timber.e("ResultViewType.MAP_VIEW")
		intent {
			copy(
                resultViewType = ResultViewType.MAP_VIEW,
            )
		}
	}
}
