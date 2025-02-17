package com.boostcamp.mapisode.episode.state

import androidx.compose.runtime.Immutable
import com.boostcamp.mapisode.model.GroupModel
import com.boostcamp.mapisode.ui.base.UiState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class EpisodeState(
	val isInitializing: Boolean = true,
	val groups: PersistentList<GroupModel> = persistentListOf(),
) : UiState
