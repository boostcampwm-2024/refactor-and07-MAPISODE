package com.boostcamp.mapisode.episode.aiEpisode

import com.boostcamp.mapisode.ui.base.SideEffect

sealed class AiEpisodeSideEffect : SideEffect {
	data class ShowToast(val messageResId: Int) : AiEpisodeSideEffect()
	data object NavigateToHome : AiEpisodeSideEffect()

}
