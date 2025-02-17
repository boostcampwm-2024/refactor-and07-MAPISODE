package com.boostcamp.mapisode.episode

import com.boostcamp.mapisode.episode.state.EpisodeEffect
import com.boostcamp.mapisode.episode.state.EpisodeIntent
import com.boostcamp.mapisode.episode.state.EpisodeState
import com.boostcamp.mapisode.ui.base.RevisedBaseViewModel
import com.boostcamp.mapisode.ui.base.retainFirstIfNavigating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor() :
	RevisedBaseViewModel<EpisodeIntent, EpisodeState, EpisodeEffect>(EpisodeState()) {

	override suspend fun reducer(intent: SharedFlow<EpisodeIntent>) {
		intent.retainFirstIfNavigating()
			.collect {
				when (it) {
					EpisodeIntent.OnBackClick -> { navigateToBack() }
					is EpisodeIntent.OnCompletePhotoPicker -> { completePhotoPicker(it.imageUrls) }
				}
			}
	}

	private fun navigateToBack() {
		sendEffect { EpisodeEffect.NavigateToPreviousScreen }
	}

	private fun completePhotoPicker(imageUrls: List<String>) {
		sendState { copy(imageUrls = imageUrls.toPersistentList()) }
		sendEffect { EpisodeEffect.NavigateToInfoScreen }
	}

	override fun onCleared() {
		super.onCleared()
		Timber.e("EpisodeViewModel onCleared")
	}
}
