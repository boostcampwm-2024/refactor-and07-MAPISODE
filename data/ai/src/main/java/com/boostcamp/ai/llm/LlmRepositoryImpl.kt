package com.boostcamp.ai.llm

import android.content.Context
import com.boostcamp.ai.LlmRepository
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import javax.inject.Inject

class LlmRepositoryImpl @Inject constructor(context: Context) : LlmRepository {
	private val options = LlmInference.LlmInferenceOptions.builder()
		.setModelPath("/data/local/tmp/llm/gemma-2b-it-gpu-int4.bin")
		.setMaxTokens(10000)
		.setTopK(40)
		.setTemperature(0.8f)
		.setRandomSeed(101)
		.build()

	private fun inputPrompt(description: String) =
		"""Make a story about the description below. Under 300 words.
		$description
		""".trimMargin()

	private val llmInference: LlmInference = LlmInference.createFromOptions(context, options)

	override fun generateLlm(text: String): String {
		return llmInference.generateResponse(inputPrompt(text))
	}
}
