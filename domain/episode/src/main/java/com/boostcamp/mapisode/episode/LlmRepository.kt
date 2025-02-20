package com.boostcamp.mapisode.episode

interface LlmRepository {
	fun setLlmInference()
	fun generateLlm(text: String): String
	fun close()
}
