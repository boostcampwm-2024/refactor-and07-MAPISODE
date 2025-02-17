package com.boostcamp.mapisode.episode.state

import androidx.compose.runtime.Immutable
import com.boostcamp.mapisode.ui.base.SideEffect

@Immutable
sealed class EpisodeEffect : SideEffect {
	// 공통
	data class ShowToast(val message: String) : EpisodeEffect()
	data object NavigateToPreviousScreen : EpisodeEffect()

	// 포토 피커
	data object NavigateToInfoScreen : EpisodeEffect()

	// 그룹 선택
	data object NavigateToContentScreen : EpisodeEffect()
}
