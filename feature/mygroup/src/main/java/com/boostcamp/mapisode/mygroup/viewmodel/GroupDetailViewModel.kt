package com.boostcamp.mapisode.mygroup.viewmodel

import androidx.lifecycle.viewModelScope
import com.boostcamp.mapisode.datastore.UserPreferenceDataStore
import com.boostcamp.mapisode.episode.repository.EpisodeRepository
import com.boostcamp.mapisode.episode.repository.GroupRepository
import com.boostcamp.mapisode.mygroup.R
import com.boostcamp.mapisode.mygroup.intent.GroupDetailIntent
import com.boostcamp.mapisode.mygroup.model.GroupUiMemberModel
import com.boostcamp.mapisode.mygroup.model.toGroupUiEpisodeModel
import com.boostcamp.mapisode.mygroup.model.toGroupUiModel
import com.boostcamp.mapisode.mygroup.sideeffect.GroupDetailSideEffect
import com.boostcamp.mapisode.mygroup.state.GroupDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
	private val groupRepository: GroupRepository,
	private val episodeRepository: EpisodeRepository,
	private val userPreferenceDataStore: UserPreferenceDataStore,
) : GroupBaseViewModel<GroupDetailIntent, GroupDetailState, GroupDetailSideEffect>(GroupDetailState()) {

	override suspend fun reducer(intent: SharedFlow<GroupDetailIntent>) {
		intent.retainFirstIfNavigating()
			.collect { uiIntent ->
				when (uiIntent) {
					is GroupDetailIntent.InitializeGroupDetail -> {
						tryGetGroup(uiIntent.groupId)
					}

					is GroupDetailIntent.TryGetUserInfo -> {
						setGroupMembersInfo()
					}

					is GroupDetailIntent.OnEditClick -> {
						navigateToGroupEditScreen(currentState.group.id)
					}

					is GroupDetailIntent.OnBackClick -> {
						sendEffect { GroupDetailSideEffect.NavigateToGroupScreen }
					}

					is GroupDetailIntent.OnEpisodeClick -> {
						navigateToEpisode(uiIntent.episodeId)
					}

					is GroupDetailIntent.OnIssueCodeClick -> {
						issueInvitationCode()
					}

					is GroupDetailIntent.OnGroupOutClick -> {
						sendEffect { GroupDetailSideEffect.WarnGroupOut }
					}

					is GroupDetailIntent.OnGroupOutConfirm -> {
						sendEffect { GroupDetailSideEffect.RemoveDialog }
						delay(100)
						leaveGroup()
					}

					is GroupDetailIntent.OnGroupOutCancel -> {
						sendEffect { GroupDetailSideEffect.RemoveDialog }
					}

					is GroupDetailIntent.TryGetGroupEpisodes -> {
						getGroupEpisodes()
					}
				}
			}
	}

	private fun navigateToGroupEditScreen(groupId: String) {
		sendEffect { GroupDetailSideEffect.NavigateToGroupEditScreen(groupId) }
	}

	private fun navigateToEpisode(episodeId: String) {
		sendEffect { GroupDetailSideEffect.NavigateToEpisode(episodeId) }
	}

	private fun tryGetGroup(groupId: String) {
		viewModelScope.launch {
			try {
				val group = groupRepository.getGroupByGroupId(groupId)

				if (group.adminUser == userPreferenceDataStore.getUserId().first()) {
					sendState {
						copy(
							isGroupOwner = true,
							group = group.toGroupUiModel(),
							isGroupLoaded = true,
						)
					}
				} else {
					sendState {
						copy(
							group = group.toGroupUiModel(),
							isGroupLoaded = true,
						)
					}
				}
			} catch (e: Exception) {
				sendEffect { GroupDetailSideEffect.ShowToast(R.string.message_group_not_found) }
			}
		}
	}

	private fun issueInvitationCode() {
		viewModelScope.launch {
			try {
				val code = groupRepository.issueInvitationCode(currentState.group.id)
				sendEffect { GroupDetailSideEffect.IssueInvitationCode(code) }
			} catch (e: Exception) {
				sendEffect { GroupDetailSideEffect.ShowToast(R.string.message_issue_code_fail) }
			}
		}
	}

	private fun setGroupMembersInfo() {
		val group = currentState.group
		val members = group.members

		viewModelScope.launch {
			val memberInfo = mutableListOf<GroupUiMemberModel>()
			members.forEach { member ->
				val userModel = groupRepository.getUserInfoByUserId(member)
				val userEpisodeModel = groupRepository.getEpisodesByGroupIdAndUserId(
					groupId = currentState.group.id,
					userId = member,
				)
				val latestCreatedAt = userEpisodeModel.maxByOrNull { it.createdAt.time }?.createdAt
				val numberOfEpisode = userEpisodeModel.size
				memberInfo.add(
					GroupUiMemberModel(
						id = userModel.id,
						name = userModel.name,
						email = userModel.email,
						profileUrl = userModel.profileUrl,
						joinedAt = userModel.joinedAt,
						groups = userModel.groups,
						recentCreatedAt = latestCreatedAt,
						countEpisode = numberOfEpisode,
					),
				)
			}

			sendState {
				copy(
					membersInfo = memberInfo.toImmutableList(),
				)
			}
		}
	}

	private fun leaveGroup() {
		viewModelScope.launch {
			val userId = userPreferenceDataStore.getUserId().first() ?: throw Exception()
			val groupId = currentState.group.id
			try {
				groupRepository.leaveGroup(userId, groupId)
				sendEffect { GroupDetailSideEffect.ShowToast(R.string.message_group_out_success) }
				delay(100)
				sendEffect { GroupDetailSideEffect.NavigateToGroupScreen }
			} catch (e: Exception) {
				sendEffect { GroupDetailSideEffect.ShowToast(R.string.message_group_out_fail) }
			}
		}
	}

	private fun getGroupEpisodes() {
		viewModelScope.launch {
			try {
				val episodes = episodeRepository.getEpisodesByGroup(currentState.group.id)
				sendState {
					copy(
						episodes = episodes.map {
							val name = currentState.membersInfo.firstOrNull { member ->
								member.id == it.createdBy
							}?.name ?: ""
							it.toGroupUiEpisodeModel(name)
						}.toImmutableList(),
					)
				}
			} catch (e: Exception) {
				sendEffect { GroupDetailSideEffect.ShowToast(R.string.message_group_not_found) }
			}
		}
	}
}

fun Flow<GroupDetailIntent>.retainFirstIfNavigating() = channelFlow {
	var isFirst = true
	collectLatest { value ->
		if (isFirst) {
			launch(Dispatchers.IO) {
				isFirst = false
				send(value)
				if (value is GroupDetailIntent.OnEditClick || value is GroupDetailIntent.OnEpisodeClick) {
					delay(300)
				}
				isFirst = true
			}
		}
	}
}
