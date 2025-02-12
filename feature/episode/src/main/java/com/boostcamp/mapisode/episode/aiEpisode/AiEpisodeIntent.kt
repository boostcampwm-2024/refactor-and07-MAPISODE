package com.boostcamp.mapisode.episode.aiEpisode

import com.boostcamp.mapisode.ui.base.UiIntent

sealed class AiEpisodeIntent : UiIntent {
	data object LoadMyGroups : AiEpisodeIntent()
	data object ShowPhotoPicker : AiEpisodeIntent()
	data object HidePhotoPicker : AiEpisodeIntent()
	data object BackToHome : AiEpisodeIntent()
	data class SetImages(val images: List<String>) : AiEpisodeIntent()
	data class AddGroup(val group: GroupInfo) : AiEpisodeIntent()
	data class SubtractGroup(val group: GroupInfo) : AiEpisodeIntent()
	data object AddAllGroup : AiEpisodeIntent()
	data object ClearGroup : AiEpisodeIntent()
	data class SetAiText(val aiText: String) : AiEpisodeIntent()
	data object SubmitAiEpisode : AiEpisodeIntent()
}
