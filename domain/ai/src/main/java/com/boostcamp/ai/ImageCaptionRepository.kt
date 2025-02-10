package com.boostcamp.ai

interface ImageCaptionRepository {
	suspend fun generateImageCaption(imagePath: String): List<String>
}
