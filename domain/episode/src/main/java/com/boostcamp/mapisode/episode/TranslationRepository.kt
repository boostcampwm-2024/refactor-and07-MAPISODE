package com.boostcamp.mapisode.episode

interface TranslationRepository {
	fun translate(text: String, callback: (String) -> Unit): List<String>
	fun downloadModel()
	fun deleteModel()
	fun close()
}
