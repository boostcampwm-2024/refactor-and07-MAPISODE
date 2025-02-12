package com.boostcamp.mapisode.episode

interface LlmRepository {
	fun generateLlm(text: String): String
}
