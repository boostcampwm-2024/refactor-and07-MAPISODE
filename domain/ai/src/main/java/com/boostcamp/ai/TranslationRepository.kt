package com.boostcamp.ai

interface TranslationRepository {
	fun translate(text: String): String
	fun downloadModel()
	fun deleteModel()
	fun close()
}
