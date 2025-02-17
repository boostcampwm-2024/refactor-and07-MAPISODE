package com.boostcamp.mapisode.episode

import androidx.lifecycle.viewModelScope
import com.boostcamp.mapisode.datastore.UserPreferenceDataStore
import com.boostcamp.mapisode.episode.common.NewEpisodeConstant.MAP_DEFAULT_ZOOM
import com.boostcamp.mapisode.episode.repository.GroupRepository
import com.boostcamp.mapisode.episode.state.EpisodeEffect
import com.boostcamp.mapisode.episode.state.EpisodeIntent
import com.boostcamp.mapisode.episode.state.EpisodeState
import com.boostcamp.mapisode.network.repository.NaverMapsRepository
import com.boostcamp.mapisode.ui.base.RevisedBaseViewModel
import com.boostcamp.mapisode.ui.base.retainFirstIfNavigating
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
	private val userPreferenceDataStore: UserPreferenceDataStore,
	private val groupRepository: GroupRepository,
	private val naverMapsRepository: NaverMapsRepository,
) : RevisedBaseViewModel<EpisodeIntent, EpisodeState, EpisodeEffect>(EpisodeState()) {

	override suspend fun reducer(intent: SharedFlow<EpisodeIntent>) {
		intent.retainFirstIfNavigating()
			.collect { event ->
				when (event) {
					EpisodeIntent.OnBackClick -> {
						navigateToBack()
					}

					is EpisodeIntent.OnCompletePhotoPicker -> {
						completePhotoPicker(event.imageUrls)
					}

					EpisodeIntent.OnLoadMyGroups -> {
						loadMyGroups()
					}

					is EpisodeIntent.OnGroupClick -> {
						sendState { copy(selectedGroups = event.groups.toPersistentList()) }
					}

					is EpisodeIntent.SetIsCameraMoving -> {
						setIsCameraMoving(event.isCameraMoving)
					}

					is EpisodeIntent.SetEpisodeAddress -> {
						getAddress(event.latLng)
					}

					is EpisodeIntent.SetEpisodeLocation -> {
						getLocation(event.latLng)
					}

					EpisodeIntent.OnCompleteInfoClick -> {
						navigateToInfoScreen()
					}
				}
			}
	}

	private fun navigateToBack() {
		sendEffect { EpisodeEffect.NavigateToPreviousScreen }
	}

	private fun completePhotoPicker(imageUrls: List<String>) {
		sendState {
			copy(
				imageUrls = imageUrls.toPersistentList(),
				isLoading = true,
			)
		}
		viewModelScope.launch(Dispatchers.IO) {
			while (true) {
				delay(100)
				if (state.value.groups.isNotEmpty()) {
					sendEffect { EpisodeEffect.NavigateToInfoScreen }
					sendState { copy(isLoading = false) }
					break
				}
			}
		}
	}

	private fun loadMyGroups() {
		viewModelScope.launch {
			val userId = userPreferenceDataStore.getUserId().firstOrNull()
			userId?.let {
				val myGroups = groupRepository.getGroupsByUserId(userId)
				sendState { copy(groups = myGroups.toPersistentList()) }
			}
		}
	}

	private fun setIsCameraMoving(isCameraMoving: Boolean) {
		sendState { copy(isCameraMoving = isCameraMoving) }
	}

	private fun getAddress(latLng: LatLng) {
		viewModelScope.launch {
			val coord = "${latLng.longitude},${latLng.latitude}"
			val address = naverMapsRepository.reverseGeoCode(coord).getOrDefault("")
			sendState { copy(episodeAddress = address) }
		}
	}

	private fun getLocation(latLng: LatLng) {
		sendState {
			copy(cameraPosition = CameraPosition(latLng, MAP_DEFAULT_ZOOM))
		}
	}

	private fun navigateToInfoScreen() {
		if (currentState.episodeAddress.isNotBlank() && currentState.selectedGroups.isNotEmpty()) {
			sendEffect { EpisodeEffect.NavigateToContentScreen }
		} else {
			sendEffect { EpisodeEffect.ShowToast("위치와 그룹을 선택해주세요.") }
		}
	}

	override fun onCleared() {
		super.onCleared()
		Timber.e("EpisodeViewModel onCleared")
	}
}
