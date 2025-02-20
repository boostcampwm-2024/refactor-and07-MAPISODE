package com.boostcamp.mapisode.episode

interface TranslationRepository {
	fun setEnglishKoreanTranslator()
	var isModelReady: Boolean
	fun translate(
		text: String,
		onSuccess: (String) -> Unit,
		onFailure: (String) -> Unit,
		onComplete: () -> Unit,
	)
	fun downloadModel()
	fun deleteModel(onDeleteSuccess: () -> Unit, onDeleteFailure: (String) -> Unit)
	fun close()
}
