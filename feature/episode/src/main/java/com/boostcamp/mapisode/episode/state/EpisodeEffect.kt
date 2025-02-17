package com.boostcamp.mapisode.episode.state

import androidx.compose.runtime.Immutable
import com.boostcamp.mapisode.ui.base.SideEffect

@Immutable
sealed class EpisodeEffect : SideEffect {
	data class ShowToast(val messageResId: Int) : EpisodeEffect()
	data object NavigateToInfoScreen : EpisodeEffect()
	data object NavigateToContentScreen : EpisodeEffect()
	data object NavigateToPreviousScreen : EpisodeEffect()
}
