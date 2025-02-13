package com.boostcamp.ai.llm

import android.content.Context
import com.boostcamp.mapisode.episode.LlmRepository
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import timber.log.Timber

class LlmRepositoryImpl(context: Context) : LlmRepository {
	private val options = LlmInference.LlmInferenceOptions.builder()
		.setModelPath("/data/local/tmp/llm/gemma-2b-it-gpu-int4.bin")
		.setMaxTokens(10000)
		.setTopK(40)
		.setTemperature(0.8f)
		.setRandomSeed(101)
		.build()

	private fun inputPrompt(description: String) = """
		Description: [$description]

		You are an AI assistant that analyzes descriptions and infers the overall activity or event that likely took place. Your goal is to piece together these individual observations into a coherent story or activity.

		Please follow these guidelines:
		1. Analyze the objects, people, actions, and environment mentioned in the description
		2. Make logical inferences about the activities that took place
		3. Present your analysis in the following format:
		   ## title: A concise heading that captures the main activity/event (1-5 words)
		   ## tags: 2-4 relevant keywords or categories (separated by commas)
		   ## content: A 2-3 sentence summary of what likely happened, based on the description

		""".trimIndent()

	private val llmInference: LlmInference = LlmInference.createFromOptions(context, options)

	override fun generateLlm(text: String): String {
		val result = llmInference.generateResponse(inputPrompt(text))
		Timber.e("Llm result: $result")
		return result
	}
}
