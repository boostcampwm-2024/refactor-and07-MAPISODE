package com.boostcamp.mapisode.episode.UseCase

import com.boostcamp.mapisode.episode.repository.DatabaseRepository
import com.boostcamp.mapisode.episode.repository.EpisodeRepository
import com.boostcamp.mapisode.model.EpisodeModel
import javax.inject.Inject

class UploadNewEpisodeUseCase @Inject constructor(
	private val episodeRepository: EpisodeRepository,
	private val databaseRepository: DatabaseRepository,
) {
	suspend fun invoke(episodeModel: EpisodeModel) {
		databaseRepository.insertEpisode(episodeModel)
		val urls = episodeRepository.uploadImagesToStorage(episodeModel.id, episodeModel.imageUrls)
		episodeRepository.createEpisode(episodeModel, urls)
		episodeRepository.updateEpisode(episodeModel)
	}
}
