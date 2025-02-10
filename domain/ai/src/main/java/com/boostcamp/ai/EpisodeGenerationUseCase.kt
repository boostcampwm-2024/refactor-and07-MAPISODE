package com.boostcamp.ai

class EpisodeGenerationUseCase(
	private val imageCaptionRepository: ImageCaptionRepository,
	private val llmRepository: LlmRepository,
	private val translationRepository: TranslationRepository
) {
	suspend fun invoke(imageUri: String, textInput: String): String {
		val imageText = imageCaptionRepository.generateImageCaption(imageUri)

		val generatedText = llmRepository.generateLlm(imageText.joinToString(" ") + textInput)

		return translationRepository.translate(generatedText)
	}
}
