package com.boostcamp.mapisode.episode.state

import androidx.compose.runtime.Immutable
import com.boostcamp.mapisode.ui.base.UiIntent

@Immutable
sealed class EpisodeIntent : UiIntent {
	data object OnBackClick : EpisodeIntent()
	data class OnCompletePhotoPicker(val imageUrls: List<String>) : EpisodeIntent()
}
