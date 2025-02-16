package com.boostcamp.mapisode.episode

import com.boostcamp.mapisode.model.EpisodeModel
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
	}
}
