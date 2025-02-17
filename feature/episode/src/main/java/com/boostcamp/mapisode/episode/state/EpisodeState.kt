package com.boostcamp.mapisode.episode.state

import androidx.compose.runtime.Immutable
import com.boostcamp.mapisode.episode.common.NewEpisodeConstant.MAP_DEFAULT_ZOOM
import com.boostcamp.mapisode.model.GroupModel
import com.boostcamp.mapisode.ui.base.UiState
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class EpisodeState(
	// 공통
	val isLoading: Boolean = false,
	val imageUrls: PersistentList<String> = persistentListOf(),
	val groups: PersistentList<GroupModel> = persistentListOf(),
	val selectedGroups: PersistentList<GroupModel> = persistentListOf(),

	// 지도 위치 선택
	val cameraPosition: CameraPosition = CameraPosition(
		LatLng(
			37.49083317052349,
			127.03343085967185,
		),
		MAP_DEFAULT_ZOOM,
	),
	val isCameraMoving: Boolean = false,
	val episodeAddress: String = "",
) : UiState
