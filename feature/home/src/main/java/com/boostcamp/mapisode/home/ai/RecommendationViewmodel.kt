package com.boostcamp.mapisode.home.ai

import androidx.lifecycle.viewModelScope
import com.boostcamp.mapisode.episode.repository.EpisodeRepository
import com.boostcamp.mapisode.home.common.OptionType
import com.boostcamp.mapisode.home.common.ResultEpisode
import com.boostcamp.mapisode.home.common.ResultViewType
import com.boostcamp.mapisode.model.EpisodeModel
import com.boostcamp.mapisode.ui.base.BaseViewModel
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewmodel @Inject constructor(
	private val episodeRepository: EpisodeRepository,
) : BaseViewModel<RecommendationIntent, RecommendationState, RecommendationSideEffect>(
	RecommendationState(),
) {
	override fun onIntent(intent: RecommendationIntent) {
		when (intent) {
			is RecommendationIntent.Initialize -> initialize(intent.episodes)
			is RecommendationIntent.OptionClick -> handleOptionClick(intent.optionId)
			RecommendationIntent.ShowListType -> showListType()
			RecommendationIntent.ShowMapType -> showMapType()
		}
	}

	private fun initialize(episodes: List<String>) {
		viewModelScope.launch(Dispatchers.IO) {
			val episodeDetail = episodes.map {
				episodeRepository.getEpisodeById(it) ?: EpisodeModel()
			}

			intent {
				copy(
					entireEpisodes = episodeDetail.map {
						ResultEpisode(
							id = it.id,
							owner = it.createdBy,
							thumbnail = it.imageUrls.first(),
							coordinates = LatLng(it.location.latitude, it.location.longitude),
						)
					},
				)
			}
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
		intent {
			copy(
				resultViewType = ResultViewType.LIST_VIEW,
			)
		}
	}

	private fun showMapType() {
		intent {
			copy(
				resultViewType = ResultViewType.MAP_VIEW,
			)
		}
	}
}
