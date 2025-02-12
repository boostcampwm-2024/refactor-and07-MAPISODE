package com.boostcamp.mapisode.episode.aiEpisode

import coil3.Uri
import com.boostcamp.mapisode.ui.base.UiState

data class AiEpisodeState(
	val showPhotoPicker: Boolean = true,
	val aiText: String = "",
	val myGroups: List<GroupInfo> = emptyList(),
	val selectedGroups: List<GroupInfo> = emptyList(),
	val images: List<Uri> = emptyList(),
) : UiState

data class GroupInfo(
	val name: String,
	val groupId: String,
	val imageUri: Uri,
)
