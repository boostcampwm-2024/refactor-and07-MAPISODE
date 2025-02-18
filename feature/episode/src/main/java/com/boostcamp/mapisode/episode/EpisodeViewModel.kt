package com.boostcamp.mapisode.episode

import androidx.lifecycle.viewModelScope
import com.boostcamp.mapisode.common.util.UuidGenerator
import com.boostcamp.mapisode.datastore.UserPreferenceDataStore
import com.boostcamp.mapisode.episode.UseCase.UploadNewEpisodeUseCase
import com.boostcamp.mapisode.episode.common.NewEpisodeConstant.MAP_DEFAULT_ZOOM
import com.boostcamp.mapisode.episode.repository.GroupRepository
import com.boostcamp.mapisode.episode.state.EpisodeEffect
import com.boostcamp.mapisode.episode.state.EpisodeIntent
import com.boostcamp.mapisode.episode.state.EpisodeState
import com.boostcamp.mapisode.model.EpisodeLatLng
import com.boostcamp.mapisode.model.EpisodeModel
import com.boostcamp.mapisode.network.repository.NaverMapsRepository
import com.boostcamp.mapisode.ui.base.RevisedBaseViewModel
import com.boostcamp.mapisode.ui.base.retainFirstIfNavigating
import com.google.ai.client.generativeai.GenerativeModel
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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
	private val userPreferenceDataStore: UserPreferenceDataStore,
	private val uploadNewEpisodeUseCase: UploadNewEpisodeUseCase,
	private val groupRepository: GroupRepository,
	private val naverMapsRepository: NaverMapsRepository,
	private val imageCaptionRepository: ImageCaptionRepository,
) : RevisedBaseViewModel<EpisodeIntent, EpisodeState, EpisodeEffect>(EpisodeState()) {

	private val gemini = GenerativeModel(
		modelName = "gemini-1.5-flash",
		apiKey = BuildConfig.GOOGLE_GENERATIVE_AI,
	)

	override suspend fun reducer(intent: SharedFlow<EpisodeIntent>) {
		intent.retainFirstIfNavigating(
			EpisodeIntent.OnBackClick::class,
			EpisodeIntent.OnCompletePhotoPicker::class,
			EpisodeIntent.OnCompleteInfoClick::class,
			EpisodeIntent.OnCompleteInfoPick::class,
		).collect { event ->
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

				is EpisodeIntent.OnUserInputChange -> {
					sendState { copy(userInput = event.userInput) }
				}

				EpisodeIntent.OnGenerateLLMClick -> {
					dealWithLLM()
				}

				is EpisodeIntent.OnSelectEpisodeClick -> {
					sendState {
						copy(
							selectedEpisode = event.generatedEpisode,
							selfTypedEpisode = "",
							isEpisodeSelected = true,
						)
					}
				}

				is EpisodeIntent.OnSelfTypedEpisodeChange -> {
					sendState {
						copy(
							selectedEpisode = "",
							selfTypedEpisode = event.selfTypedEpisode,
							isEpisodeSelected = event.selfTypedEpisode.isNotBlank(),
						)
					}
				}

				EpisodeIntent.OnCompleteInfoPick -> {
					sendState { copy(isLoading = true) }
					val episodeId = UuidGenerator.generate()
					viewModelScope.launch {
						currentState.selectedGroups.forEach { groupModel ->
							val userPreference =
								userPreferenceDataStore.getUserPreferencesFlow().firstOrNull()
							userPreference?.let {
								uploadNewEpisodeUseCase.invoke(
									EpisodeModel(
										id = episodeId,
										category = "see",
										content = currentState.userInput,
										createdBy = it.userId ?: "",
										createdByName = it.username ?: "",
										group = groupModel.id,
										imageUrls = currentState.imageUrls,
										imageUrlsUsedForOnlyUpdate = emptyList(),
										address = currentState.episodeAddress,
										location = EpisodeLatLng(
											latitude = currentState.cameraPosition.target.latitude,
											longitude = currentState.cameraPosition.target.longitude,
										),
										memoryDate = Date(),
										tags = emptyList(),
										title = currentState.selectedEpisode,
										createdAt = Date(),
									),
								)
							}
						}
						sendState { copy(isLoading = false) }
						sendEffect { EpisodeEffect.NavigateBackToHomeScreen }
					}
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
			getImageCaption(currentState.imageUrls)
			sendEffect { EpisodeEffect.NavigateToContentScreen }
		} else {
			sendEffect { EpisodeEffect.ShowToast("위치와 그룹을 선택해주세요.") }
		}
	}

	private fun getImageCaption(imageUrls: List<String>) {
		viewModelScope.launch {
			val imageCaption = imageUrls.map {
				imageCaptionRepository.generateImageCaption(it)
			}.joinToString("\n") { it.joinToString(",") }
			sendState { copy(imageCaption = imageCaption) }
			Timber.e("imageCaption: $imageCaption")
		}
	}

	private fun dealWithLLM() {
		viewModelScope.launch {
			sendState {
				copy(
					selectedEpisode = "",
					isLoading = true,
					isEpisodeSelected = false,
				)
			}
			if (currentState.imageCaption.isNotBlank()) {
				generateStories(
					currentState.imageCaption,
					currentState.userInput,
				)
			} else {
				while (currentState.imageCaption.isBlank()) {
					delay(100)
					if (currentState.imageCaption.isNotBlank()) {
						generateStories(
							currentState.imageCaption,
							currentState.userInput,
						)
					}
				}
			}
		}
	}

	private fun generateStories(imageCaption: String, userInputText: String) {
		viewModelScope.launch {
			val prompt = """
$imageCaption
위의 글은 내가 촬영한 "사진의 상황"에 대한 객관적인 설명이야.

아래의 글은 내가 촬영한 당일에 느낀 생각을 작성한 글이야. 이 글은 비어있을 수도 있어.
그러나, 이 글이 존재하는 경우 **반드시** 이를 중심으로 일기 요약을 생성해줘.
사진의 상황은 배경 정보로만 활용하고, 핵심은 내가 작성한 글을 반영하는 거야.

"내가 작성한 글"인 아래 글을 기반으로 일기 요약을 작성해줘:
$userInputText

**요구사항**:
- 반드시 **내가 작성한 글을 기반으로** 작성해줘.
- **사진의 상황은 보조 자료일 뿐**, 핵심 내용은 내가 작성한 글에서 가져와.
- 사진 찍은 이야기는 하지마.
- 화자는 나로 설정해줘.
- 문장의 어미를 "~다"로 설정해서 작성해줘.
- 출력 형식: 각 줄의 맨 앞에 `## `를 붙이고, 한 줄에 40자 내외로 작성해줘. 총 세 줄 작성해줘. 각 줄은 독립적인 내용이어야 해.

**추가 처리**:
- 만약 내가 작성한 글이 비어 있다면, 사진 상황을 기반으로 감정이나 생각을 추론해서 작성해줘.
- 단, 추론한 내용은 **사진 설명을 그대로 반복하지 말고**, 감정적인 해석을 포함해야 해.
			""".trimIndent()
			val episodes =
				gemini.generateContent(prompt).text?.split("##")?.drop(1)?.map { it.trim() }
					?: emptyList()
			sendState {
				copy(
					generatedEpisodes = episodes.toPersistentList(),
					isLoading = false,
				)
			}
		}
	}

	override fun onCleared() {
		super.onCleared()
		Timber.e("EpisodeViewModel onCleared")
	}
}
