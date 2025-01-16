package com.boostcamp.mapisode.mygroup.viewmodel

import androidx.lifecycle.viewModelScope
import com.boostcamp.mapisode.mygroup.GroupRepository
import com.boostcamp.mapisode.mygroup.R
import com.boostcamp.mapisode.mygroup.intent.GroupEditIntent
import com.boostcamp.mapisode.mygroup.model.toGroupCreationModel
import com.boostcamp.mapisode.mygroup.sideeffect.GroupEditSideEffect
import com.boostcamp.mapisode.mygroup.state.GroupEditState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupEditViewModel @Inject constructor(private val groupRepository: GroupRepository) :
	GroupBaseViewModel<GroupEditIntent, GroupEditState, GroupEditSideEffect>(GroupEditState()) {

	@OptIn(FlowPreview::class)
	override suspend fun reducer(intent: SharedFlow<GroupEditIntent>) {
		intent.debounce(100L)
			.flatMapLatest { value ->
				flowOf(value).onEach { delay(300) }
			}
			.collect { uiIntent ->
				when (uiIntent) {
					is GroupEditIntent.Initialize -> {
						initializeCreatingGroup(uiIntent.groupId)
					}

					is GroupEditIntent.OnBackClick -> {
						sendEffect { GroupEditSideEffect.NavigateToGroupDetailScreen }
					}

					is GroupEditIntent.OnGroupEditClick -> {
						checkGroupEdit(uiIntent.title, uiIntent.content, uiIntent.imageUrl)
					}

					GroupEditIntent.DenyPhotoPermission -> sendEffect {
						GroupEditSideEffect.ShowToast(R.string.message_error_permission_denied)
					}

					is GroupEditIntent.OnGroupImageSelect -> {
						imageApply(uiIntent.imageUrl)
					}

					GroupEditIntent.OnPhotoPickerClick -> {
						sendState { copy(isSelectingGroupImage = true) }
					}

					GroupEditIntent.OnBackToGroupCreation -> sendState { copy(isSelectingGroupImage = false) }
				}
			}
	}

	private fun initializeCreatingGroup(groupId: String) {
		viewModelScope.launch {
			try {
				val group = groupRepository.getGroupByGroupId(groupId).toGroupCreationModel()
				sendState {
					copy(
						isInitializing = true,
						group = group,
					)
				}
			} catch (e: Exception) {
				sendEffect { GroupEditSideEffect.ShowToast(R.string.group_load_failure) }
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
						GroupEditSideEffect.ShowToast(R.string.message_error_title_length)
					}
				} else if (content.isEmpty()) {
					sendEffect {
						GroupEditSideEffect.ShowToast(R.string.message_error_content_empty)
					}
				} else if (imageUrl.isBlank()) {
					sendEffect {
						GroupEditSideEffect.ShowToast(R.string.message_error_image_url_blank)
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
					groupRepository.updateGroup(currentState.group.toGroupModel())
					sendEffect {
						GroupEditSideEffect.ShowToast(R.string.message_success_edit_group)
					}
					delay(100)
					sendEffect { GroupEditSideEffect.NavigateToGroupDetailScreen }
				}
			} catch (e: Exception) {
				sendEffect { GroupEditSideEffect.ShowToast(R.string.message_error_edit_group) }
				delay(100)
				sendEffect { GroupEditSideEffect.Idle }
			}
		}
	}
}
