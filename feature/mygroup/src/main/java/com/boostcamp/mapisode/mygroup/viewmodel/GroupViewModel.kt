package com.boostcamp.mapisode.mygroup.viewmodel

import androidx.lifecycle.viewModelScope
import com.boostcamp.mapisode.datastore.UserPreferenceDataStore
import com.boostcamp.mapisode.episode.repository.GroupRepository
import com.boostcamp.mapisode.mygroup.R
import com.boostcamp.mapisode.mygroup.intent.GroupIntent
import com.boostcamp.mapisode.mygroup.sideeffect.GroupSideEffect
import com.boostcamp.mapisode.mygroup.state.GroupState
import com.boostcamp.mapisode.ui.base.RevisedBaseViewModel
import com.boostcamp.mapisode.ui.base.retainFirstIfNavigating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
	private val groupRepository: GroupRepository,
	private val userPreferenceDataStore: UserPreferenceDataStore,
) : RevisedBaseViewModel<GroupIntent, GroupState, GroupSideEffect>(GroupState()) {

	override suspend fun reducer(intent: SharedFlow<GroupIntent>) {
		intent.retainFirstIfNavigating(
			GroupIntent.OnJoinClick::class,
			GroupIntent.OnGroupCreateClick::class,
			GroupIntent.OnGroupDetailClick::class,
		)
			.collect { uiIntent ->
				when (uiIntent) {
					GroupIntent.LoadGroups -> {
						Timber.e("here")
						loadGroups()
					}

					GroupIntent.OnJoinClick -> {
						navigateToGroupJoinScreen()
					}

					GroupIntent.OnGroupCreateClick -> {
						navigateToGroupCreationScreen()
					}

					is GroupIntent.OnGroupDetailClick -> {
						navigateToGroupDetailScreen(uiIntent.groupId)
					}
				}
			}
	}

	private fun loadGroups() {
		viewModelScope.launch {
			try {
				val userId = userPreferenceDataStore.getUserId().first() ?: throw Exception()
				val group = groupRepository
					.getGroupsByUserId(userId)
					.toPersistentList()
				sendState {
					copy(
						isInitializing = false,
						groups = group,
					)
				}
				Timber.e("here")
			} catch (e: Exception) {
				sendEffect { GroupSideEffect.ShowToast(R.string.group_load_failure) }
			}
		}
	}

	private fun navigateToGroupJoinScreen() {
		viewModelScope.launch {
			sendEffect { GroupSideEffect.NavigateToGroupJoinScreen }
			delay(100)
			sendState { copy(isInitializing = true) }
		}
	}

	private fun navigateToGroupCreationScreen() {
		viewModelScope.launch {
			sendEffect { GroupSideEffect.NavigateToGroupCreateScreen }
			delay(100)
			sendState { copy(isInitializing = true) }
		}
	}

	private fun navigateToGroupDetailScreen(groupId: String) {
		viewModelScope.launch {
			Timber.e("here2")
			sendEffect { GroupSideEffect.NavigateToGroupDetailScreen(groupId) }
			delay(100)
			sendState { copy(isInitializing = true) }
		}
	}
}
