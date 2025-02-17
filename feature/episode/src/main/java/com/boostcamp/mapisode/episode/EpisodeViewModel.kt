package com.boostcamp.mapisode.episode

import com.boostcamp.mapisode.episode.state.EpisodeEffect
import com.boostcamp.mapisode.episode.state.EpisodeIntent
import com.boostcamp.mapisode.episode.state.EpisodeState
import com.boostcamp.mapisode.ui.base.RevisedBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor() :
	RevisedBaseViewModel<EpisodeIntent, EpisodeState, EpisodeEffect>(EpisodeState()) {

	override suspend fun reducer(intent: SharedFlow<EpisodeIntent>) {
	}

	override fun onCleared() {
		super.onCleared()
		Timber.e("EpisodeViewModel onCleared")
	}
}
