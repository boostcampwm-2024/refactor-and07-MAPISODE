package com.boostcamp.mapisode.episode.aiEpisode

import androidx.lifecycle.viewModelScope
import coil3.toUri
import com.boostcamp.mapisode.datastore.UserPreferenceDataStore
import com.boostcamp.mapisode.episode.EpisodeRepository
import com.boostcamp.mapisode.mygroup.GroupRepository
import com.boostcamp.mapisode.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AiEpisodeViewModel @Inject constructor(
	private val episodeRepository: EpisodeRepository,
	private val groupRepository: GroupRepository,
	private val userPreferenceDataStore: UserPreferenceDataStore,
) : BaseViewModel<AiEpisodeIntent, AiEpisodeState, AiEpisodeSideEffect>(AiEpisodeState()) {
	override fun onIntent(intent: AiEpisodeIntent) {
		when (intent) {
			is AiEpisodeIntent.LoadMyGroups -> loadMyGroups()
			is AiEpisodeIntent.ShowPhotoPicker -> showPhotoPicker()
			is AiEpisodeIntent.HidePhotoPicker -> hidePhotoPicker()
			is AiEpisodeIntent.BackToHome -> backToHome()
			is AiEpisodeIntent.SetImages -> setImages(intent.images)
			is AiEpisodeIntent.AddGroup -> addGroup(intent.group)
			is AiEpisodeIntent.SubtractGroup -> subtractGroup(intent.group)
			is AiEpisodeIntent.AddAllGroup -> addAllGroup()
			is AiEpisodeIntent.ClearGroup -> clearGroup()
			is AiEpisodeIntent.SetAiText -> setAiText(intent.aiText)
			is AiEpisodeIntent.SubmitAiEpisode -> submitAiEpisode()
		}
	}

	private fun loadMyGroups() {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val userId = userPreferenceDataStore.getUserId().firstOrNull()
					?: throw RuntimeException("유저 id를 찾을 수 없습니다.")
				val myGroups = groupRepository.getGroupsByUserId(userId)
					.map { groupModel ->
						GroupInfo(
							name = groupModel.name,
							groupId = groupModel.id,
							imageUri = groupModel.imageUrl.toUri(),
						)
					}
				intent {
					copy(
						myGroups = myGroups,
					)
				}
			} catch (e: Exception) {
				throw e
			}
		}
	}

	private fun showPhotoPicker() {
		intent {
			copy(
				showPhotoPicker = true,
			)
		}
	}

	private fun hidePhotoPicker() {
		intent {
			copy(
				showPhotoPicker = false,
			)
		}
	}

	private fun backToHome() {
		postSideEffect(AiEpisodeSideEffect.NavigateToHome)
	}

	private fun setImages(images: List<String>) {
		val uris = images.map { it.toUri() }
		intent {
			copy(
				images = uris,
			)
		}
	}

	private fun addGroup(group: GroupInfo) {
		intent {
			copy(
				selectedGroups = selectedGroups + group,
			)
		}
	}

	private fun subtractGroup(group: GroupInfo) {
		intent {
			copy(
				selectedGroups = selectedGroups - group,
			)
		}
	}

	private fun addAllGroup() {
		intent {
			copy(
				selectedGroups = myGroups,
			)
		}
	}

	private fun clearGroup() {
		intent {
			copy(
				selectedGroups = emptyList(),
			)
		}
	}

	private fun setAiText(aiText: String) {
		intent {
			copy(
				aiText = aiText,
			)
		}
	}

	private fun submitAiEpisode() {
		// ai 작업
		Timber.e(
			"""
			aiText = ${currentState.aiText}
			selectedGroups = ${currentState.selectedGroups.map { it.name }.joinToString(", ")}
			images = ${currentState.images.joinToString(", ")}
			""".trimIndent(),
		)
		postSideEffect(AiEpisodeSideEffect.NavigateToHome)
	}

}
