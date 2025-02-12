package com.boostcamp.mapisode.episode

interface ImageCaptionRepository {
	suspend fun generateImageCaption(imagePath: String): List<String>
}
