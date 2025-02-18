package com.boostcamp.mapisode.episode.UseCase

import com.boostcamp.mapisode.episode.repository.DatabaseRepository
import com.boostcamp.mapisode.episode.repository.EpisodeRepository
import com.boostcamp.mapisode.model.EpisodeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UploadNewEpisodeUseCase @Inject constructor(
	private val episodeRepository: EpisodeRepository,
	private val databaseRepository: DatabaseRepository,
) {
	suspend fun invoke(episodeModel: EpisodeModel) {
		val urls = episodeRepository.uploadImagesToStorage(episodeModel.id, episodeModel.imageUrls)
		CoroutineScope(Dispatchers.IO).launch {
			episodeRepository.createEpisode(episodeModel, urls)
		}
	}
}
