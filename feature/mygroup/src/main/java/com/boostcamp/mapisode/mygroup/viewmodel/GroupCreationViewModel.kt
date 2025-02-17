package com.boostcamp.mapisode.mygroup.viewmodel

import androidx.lifecycle.viewModelScope
import com.boostcamp.mapisode.datastore.UserPreferenceDataStore
import com.boostcamp.mapisode.episode.repository.GroupRepository
import com.boostcamp.mapisode.mygroup.R
import com.boostcamp.mapisode.mygroup.intent.GroupCreationIntent
import com.boostcamp.mapisode.mygroup.sideeffect.GroupCreationSideEffect
import com.boostcamp.mapisode.mygroup.state.GroupCreationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class GroupCreationViewModel @Inject constructor(
	private val groupRepository: GroupRepository,
	private val userPreferenceDataStore: UserPreferenceDataStore,
) : GroupBaseViewModel<GroupCreationIntent, GroupCreationState, GroupCreationSideEffect>(
	GroupCreationState(),
) {
	private val userId: ConcurrentHashMap<String, String> = ConcurrentHashMap()

	@OptIn(FlowPreview::class)
	override suspend fun reducer(intent: SharedFlow<GroupCreationIntent>) {
		intent.debounce(100L)
			.flatMapLatest { value ->
				flowOf(value).onEach { delay(300) }
			}
			.collectLatest { uiIntent ->
				when (uiIntent) {
					GroupCreationIntent.Initialize -> {
						initializeCreatingGroup()
					}

					GroupCreationIntent.OnBackClick -> {
						sendEffect { GroupCreationSideEffect.NavigateToGroupScreen }
					}

					is GroupCreationIntent.OnGroupCreationClick -> {
						checkGroupEdit(uiIntent.title, uiIntent.content, uiIntent.imageUrl)
					}

					is GroupCreationIntent.OnPhotoPickerClick -> {
						sendState { copy(isSelectingGroupImage = true) }
					}

					is GroupCreationIntent.OnGroupImageSelect -> {
						imageApply(uiIntent.imageUrl)
					}

					is GroupCreationIntent.OnBackToGroupCreation -> {
						sendState { copy(isSelectingGroupImage = false) }
					}
				}
			}
	}

	private fun initializeCreatingGroup() {
		viewModelScope.launch {
			userId["userId"] = userPreferenceDataStore.getUserId().first() ?: ""
			sendState {
				copy(
					isInitializing = true,
					group = group.copy(
						id = UUID.randomUUID().toString().replace("-", ""),
						adminUser = userId["userId"] ?: "",
						createdAt = Date(),
						members = persistentListOf(userId["userId"] ?: ""),
					),
				)
			}
		}
	}

	private fun imageApply(imageUrl: String) {
		viewModelScope.launch {
			sendState {
				copy(
					isSelectingGroupImage = false,
					group = group.copy(imageUrl = imageUrl),
				)
			}
		}
	}

	private fun checkGroupEdit(title: String, content: String, imageUrl: String) {
		viewModelScope.launch {
			try {
				if (title.length !in 2..24) {
					sendEffect {
						GroupCreationSideEffect.ShowToast(R.string.message_error_title_length)
					}
				} else if (content.isEmpty()) {
					sendEffect {
						GroupCreationSideEffect.ShowToast(R.string.message_error_content_empty)
					}
				} else if (imageUrl.isBlank()) {
					sendEffect {
						GroupCreationSideEffect.ShowToast(R.string.message_error_image_url_blank)
					}
				} else {
					sendState {
						copy(
							group = group.copy(
								name = title,
								description = content,
								imageUrl = imageUrl,
							),
						)
					}
					Timber.e("Group: ${currentState.group}")
					groupRepository.createGroup(currentState.group.toGroupModel())
					sendEffect {
						GroupCreationSideEffect.ShowToast(
							R.string.message_success_creation_group,
						)
					}
					delay(100)
					sendEffect { GroupCreationSideEffect.NavigateToGroupScreen }
				}
			} catch (e: Exception) {
				sendEffect {
					GroupCreationSideEffect.ShowToast(R.string.message_error_creation_group)
				}
				delay(100)
				sendEffect { GroupCreationSideEffect.Idle }
			}
		}
	}
}
