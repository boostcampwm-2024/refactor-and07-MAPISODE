package com.boostcamp.mapisode.episode.state

import android.content.Context
import androidx.compose.runtime.Immutable
import com.boostcamp.mapisode.model.GroupModel
import com.boostcamp.mapisode.ui.base.UiIntent
import com.naver.maps.geometry.LatLng

@Immutable
sealed class EpisodeIntent : UiIntent {
	// 공통
	data object OnBackClick : EpisodeIntent()

	// 포토 피커
	data class OnCompletePhotoPicker(val imageUrls: List<String>, val context: Context) :
		EpisodeIntent()

	// 그룹 선택
	data object OnLoadMyGroups : EpisodeIntent()
	data class OnGroupClick(val groups: List<GroupModel>) : EpisodeIntent()

	// 지도 위치 선택
	data class SetIsCameraMoving(val isCameraMoving: Boolean) : EpisodeIntent()
	data class SetEpisodeAddress(val latLng: LatLng) : EpisodeIntent()
	data class SetEpisodeLocation(val latLng: LatLng) : EpisodeIntent()
	data object OnCompleteInfoClick : EpisodeIntent()

	// 내용 입력
	data class OnUserInputChange(val userInput: String) : EpisodeIntent()
	data object OnGenerateLLMClick : EpisodeIntent()
	data class OnSelectEpisodeClick(val generatedEpisode: String) : EpisodeIntent()
	data class OnSelfTypedEpisodeChange(val selfTypedEpisode: String) : EpisodeIntent()
	data object OnCompleteInfoPick : EpisodeIntent()
}
