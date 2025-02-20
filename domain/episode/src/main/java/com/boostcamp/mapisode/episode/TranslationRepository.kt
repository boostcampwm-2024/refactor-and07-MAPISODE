package com.boostcamp.mapisode.episode

interface TranslationRepository {
	fun setEnglishKoreanTranslator()
	fun setKoreanEnglishTranslator()
	var isModelReady: Boolean
	fun translate(
		text: String,
		onSuccess: (String) -> Unit,
		onFailure: (String) -> Unit,
		onComplete: () -> Unit,
	)
	fun downloadModel()
	fun downloadReverseModel()
	fun deleteModel(onDeleteSuccess: () -> Unit, onDeleteFailure: (String) -> Unit)
	fun close()
}
