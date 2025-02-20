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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
	private val translationRepository: TranslationRepository,
	private val llmRepository: LlmRepository,
	private val objectDetectionRepository: ObjectDetectionRepository,
) : RevisedBaseViewModel<EpisodeIntent, EpisodeState, EpisodeEffect>(EpisodeState()) {

	private val gemini = GenerativeModel(
		modelName = "gemini-1.5-flash",
		apiKey = BuildConfig.GOOGLE_GENERATIVE_AI,
	)

	private val _generatedResult = MutableStateFlow("")
	val generatedResult = _generatedResult.asStateFlow()

	init {
		viewModelScope.launch(Dispatchers.IO) {
			launch { objectDetectionRepository.setObjectDetector() }
			launch { translationRepository.setEnglishKoreanTranslator() }
			launch { llmRepository.setLlmInference() }
		}
		viewModelScope.launch {
			generatedResult.collectLatest {
				sendState {
					copy(
						generatedEpisodes = it.split("##").drop(1).map { it.trim() }.toPersistentList(),
					)
				}
			}
		}
	}

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
			setTranslationInstance()
			llmRepository.setLlmInference()
			sendEffect { EpisodeEffect.NavigateToContentScreen }
		} else {
			sendEffect { EpisodeEffect.ShowToast("위치와 그룹을 선택해주세요.") }
		}
	}

	private fun getImageCaption(imageUrls: List<String>) {
		viewModelScope.launch {
			// azure image captioning api
// 			val imageCaption = imageUrls.map {
// 				imageCaptionRepository.generateImageCaption(it)
// 			}.joinToString("\n") { it.joinToString(",") }

			// google on-device object detection
			objectDetectionRepository.setObjectDetector()
			val objectDetectionResult = imageUrls.map {
				objectDetectionRepository.detect(it).filter { detectionResult ->
					detectionResult.score > 0.6
				}.joinToString(",") { detectionObject ->
					"${detectionObject.className} is detected on the photo."
				}
			}.joinToString("\n")

			sendState { copy(imageCaption = objectDetectionResult) }
			Timber.e("imageCaption: $objectDetectionResult")
		}
	}

	private fun setTranslationInstance() {
		translationRepository.setEnglishKoreanTranslator()
		if (!translationRepository.isModelReady) {
			translationRepository.downloadModel()
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
		viewModelScope.launch(Dispatchers.IO) {
			val prompt = """
"situation of the picture" : $imageCaption
The above is an objective explanation of the "situation of the picture" I filmed.

The post below is what I felt on the day I filmed it. This might be empty.
However, if this article exists, please **** be sure to create a diary summary around it.
The situation in the picture is only used as background information, and the key is to reflect what I wrote.

Please write a diary summary based on the article below "What I've been through":
"What I've been through" : $userInputText

**Requirements**:
- Please make sure to **write it based on my writing.
- **The situation in the picture is only supplementary**, the core content is taken from my writing.
- Don't tell me about the photos.
- Please set the speaker to me.
- Output format: Put '##' at the beginning of each line and write about 40 characters per line. Write a total of three lines. Each line should be independent.

**Additional Processing**:
- If my writing is empty, please write by inferring your feelings or thoughts based on the photo situation.
- However, the inferred content should include an emotional interpretation instead of repeating the **photographic description as it is.
			""".trimIndent()
// 			val episodes =
// 				gemini.generateContent(prompt).text?.split("##")?.drop(1)?.map { it.trim() }
// 					?: emptyList()
			translationRepository.translate(
				text = llmRepository.generateLlm(prompt),
				onSuccess = { translatedText ->
					_generatedResult.value = translatedText
				},
				onFailure = { error ->
					Timber.e("Translation failed: $error")
				},
				onComplete = {
					sendState { copy(isLoading = false) }
				},
			)
		}
	}

	override fun onCleared() {
		super.onCleared()
		Timber.e("EpisodeViewModel onCleared")
		llmRepository.close()
		objectDetectionRepository.close()
		translationRepository.close()
	}
}
