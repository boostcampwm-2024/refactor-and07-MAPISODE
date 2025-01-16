package com.boostcamp.mapisode.mygroup.viewmodel

import androidx.lifecycle.viewModelScope
import com.boostcamp.mapisode.datastore.UserPreferenceDataStore
import com.boostcamp.mapisode.mygroup.GroupRepository
import com.boostcamp.mapisode.mygroup.R
import com.boostcamp.mapisode.mygroup.intent.GroupJoinIntent
import com.boostcamp.mapisode.mygroup.model.toGroupCreationModel
import com.boostcamp.mapisode.mygroup.sideeffect.GroupJoinSideEffect
import com.boostcamp.mapisode.mygroup.state.GroupJoinState
import com.boostcamp.mapisode.ui.base.UiIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupJoinViewModel @Inject constructor(
	private val groupRepository: GroupRepository,
	private val userPreferenceDataStore: UserPreferenceDataStore,
) : GroupBaseViewModel<GroupJoinIntent, GroupJoinState, GroupJoinSideEffect>(GroupJoinState()) {
	private val myId: MutableStateFlow<String> = MutableStateFlow("")

	init {
		observeUserId()
	}

	private fun observeUserId() {
		viewModelScope.launch {
			userPreferenceDataStore.getUserId().collect { userId ->
				myId.value = userId ?: ""
			}
		}
	}

	override suspend fun reducer(intent: SharedFlow<GroupJoinIntent>) {
		viewModelScope.launch {
			intent.collectLatest { intent ->

				when (intent) {
					is GroupJoinIntent.TryGetGroup -> {
						tryGetGroupByGroupId(intent.inviteCode)
					}

					is GroupJoinIntent.OnJoinClick -> {
						joinGroup()
					}

					is GroupJoinIntent.OnBackClick -> {
						sendEffect { GroupJoinSideEffect.NavigateToGroupScreen }
					}
				}
			}
		}
	}

	private fun tryGetGroupByGroupId(inviteCodes: String) {
		viewModelScope.launch {
			sendState { copy(isGroupLoading = true) }
			try {
				val group = groupRepository.getGroupByInviteCodes(inviteCodes)
				sendState { copy(isGroupExist = true, group = group.toGroupCreationModel()) }
			} catch (e: Exception) {
				sendState { copy(isGroupExist = false) }
				sendEffect { GroupJoinSideEffect.ShowToast(R.string.group_join_not_exist) }
			}
		}
	}

	private fun joinGroup() {
		viewModelScope.launch {
			val userId = myId.value
			val group = currentState.group ?: return@launch
			sendState { copy(isGroupLoading = true) }
			try {
				groupRepository.joinGroup(userId, group.id)
				sendState { copy(isJoinedSuccess = true) }
				sendEffect { GroupJoinSideEffect.ShowToast(R.string.group_join_success) }
			} catch (e: NullPointerException) {
				sendState { copy(isGroupLoading = false, isJoinedSuccess = false, group = null) }
				sendEffect { GroupJoinSideEffect.ShowToast(R.string.message_already_joined) }
			} catch (e: Exception) {
				sendState { copy(isGroupLoading = false, isJoinedSuccess = false, group = null) }
				sendEffect { GroupJoinSideEffect.ShowToast(R.string.group_join_failure) }
			}
			delay(100)
			sendEffect { GroupJoinSideEffect.NavigateToGroupScreen }
		}
	}
}
