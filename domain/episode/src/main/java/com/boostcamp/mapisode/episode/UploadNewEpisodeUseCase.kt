package com.boostcamp.mapisode.episode

import com.boostcamp.mapisode.model.EpisodeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class UploadNewEpisodeUseCase @Inject constructor(
	private val episodeRepository: EpisodeRepository,
	private val imageCaptionRepository: ImageCaptionRepository,
	private val llmRepository: LlmRepository,
	private val translationRepository: TranslationRepository,
	private val databaseRepository: DatabaseRepository,
	private val logger: Logger,
) {
	private var translated = ""
	fun invoke(episodeModel: EpisodeModel) {
		CoroutineScope(Dispatchers.IO).launch {
			val dbJob = launch {
				databaseRepository.cacheEpisode(episodeModel)
			}
			val translationJobModelDownloadJob = launch {
				translationRepository.downloadModel()
			}
			val aiDeferred =
				async {
					val callback: (String) -> Unit = { it -> translated = it }
					processAI(episodeModel.imageUrls, translationJobModelDownloadJob, callback)
				}
			val storageDeferred = async {
				val tmp = episodeRepository.uploadImagesToStorage(
					episodeModel.group,
					episodeModel.imageUrls,
				)
				tmp
			}
			val aiResult = aiDeferred.await()
			while(translated.isBlank()) {
				delay(100)
				logger.e("Waiting for translation")
			}
			val attributeMap = mutableMapOf(
				"title" to "",
				"tags" to "",
				"content" to "",
			)

			val sections = translated.split("##").drop(1)
			sections.forEach { section ->
				when {
					section.contains("제목 :") -> {
						attributeMap["title"] = section.substringAfter("제목 :").trim()
					}
					section.contains("태그 :") -> {
						val tags = section.substringAfter("태그 :").trim()
						attributeMap["tags"] = tags
					}
					section.contains("내용 :") -> {
						attributeMap["content"] = section.substringAfter("내용 :").trim()
					}
				}
			}
			dbJob.join()
			val updatedEpisode = episodeModel.copy(
				title = attributeMap["title"]!!,
				tags = attributeMap["tags"]!!.split(", "),
				content = attributeMap["content"]!!,
			)
			launch { databaseRepository.cacheEpisode(updatedEpisode) }
			val storageUrls = storageDeferred.await()
			launch { episodeRepository.createEpisode(updatedEpisode, storageUrls) }
		}
	}

	// 이미지의 개수 = 리스트 사이즈, 하나의 이미지 전체 캡션 결과는 하나의 문자열
	private suspend fun processAI(
		imageUrls: List<String>,
		translationJobModelDownloadJob: Job,
		callback: (String) -> Unit,
	): List<String> {
		val aiResult = coroutineScope {
			imageUrls.map { imageUrl ->
				async(Dispatchers.IO) {
					imageCaptionRepository.generateImageCaption(imageUrl)
				}
			}.awaitAll()
		}
		val llm = llmRepository.generateLlm(aiResult.joinToString(", ") { it.joinToString("\n") })
		translationJobModelDownloadJob.join()
		return translationRepository.translate(llm, callback)
	}
}
