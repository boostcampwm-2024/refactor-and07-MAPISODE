package com.boostcamp.mapisode.episode

import com.boostcamp.mapisode.model.EpisodeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class UploadNewEpisodeUseCase @Inject constructor(
	private val episodeRepository: EpisodeRepository,
	private val imageCaptionRepository: ImageCaptionRepository,
	private val llmRepository: LlmRepository,
	private val translationRepository: TranslationRepository,
	private val databaseRepository: DatabaseRepository,
) {
	fun invoke(episodeModel: EpisodeModel) {
		CoroutineScope(Dispatchers.IO).launch {
			val dbJob = launch { databaseRepository.cacheEpisode(episodeModel) }
			val translationJobModelDownloadJob = launch { translationRepository.downloadModel() }
			val aiDeferred = async { processAI(episodeModel.imageUrls, translationJobModelDownloadJob) }
			val storageDeferred = async {
				episodeRepository.uploadImagesToStorage(
					episodeModel.group,
					episodeModel.imageUrls,
				)
			}
			val aiRe = aiDeferred.await()
			dbJob.join()
			val updatedEpisode = episodeModel.copy(
				title = aiRe[0],
				tags = aiRe[1].split(","),
				content = aiRe[2],
			)
			launch { databaseRepository.cacheEpisode(updatedEpisode) }
			val storageUrls = storageDeferred.await()
			launch { episodeRepository.createEpisode(updatedEpisode, storageUrls) }
		}
	}

	// 이미지의 개수 = 리스트 사이즈, 하나의 이미지 전체 캡션 결과는 하나의 문자열
	private suspend fun processAI(imageUrls: List<String>, translationJobModelDownloadJob: Job): List<String> {
		val aiResult = coroutineScope {
			imageUrls.map { imageUrl ->
				async(Dispatchers.IO) {
					imageCaptionRepository.generateImageCaption(imageUrl)
				}
			}.awaitAll()
		}
		val llm = llmRepository.generateLlm(aiResult.joinToString("\n") { it.joinToString("\n") })
		translationJobModelDownloadJob.join()
		return translationRepository.translate(llm)
	}
}
