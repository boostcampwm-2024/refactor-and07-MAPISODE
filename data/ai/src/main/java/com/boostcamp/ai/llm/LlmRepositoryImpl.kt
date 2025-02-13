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
		$description
		Based on the above, please summarize what happened during the day.
		Please return the results in three categories in order of title, tag, and content, separated by one-line spacing.
		You should return only the result containing the title, tag, and content with "\n" as the separator.
		The number of title's characters should be no more than 8 characters, the tag should be 5 with "," as the separator, and the content should be around 200 characters.
	""".trimIndent()

	private val llmInference: LlmInference = LlmInference.createFromOptions(context, options)

	override fun generateLlm(text: String): String {
		val result = llmInference.generateResponse(inputPrompt(text))
		Timber.e("Llm result: $result")
		return result
	}
}
