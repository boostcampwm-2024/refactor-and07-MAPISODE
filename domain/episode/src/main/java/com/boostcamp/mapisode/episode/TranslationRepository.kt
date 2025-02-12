package com.boostcamp.mapisode.episode

interface TranslationRepository {
	fun translate(text: String): List<String>
	fun downloadModel()
	fun deleteModel()
	fun close()
}
