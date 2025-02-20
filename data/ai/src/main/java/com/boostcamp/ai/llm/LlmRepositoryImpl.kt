package com.boostcamp.ai.llm

import android.content.Context
import com.boostcamp.mapisode.episode.LlmRepository
import com.google.mediapipe.tasks.genai.llminference.LlmInference

class LlmRepositoryImpl(private val context: Context) : LlmRepository {

	private val options by lazy {
		LlmInference.LlmInferenceOptions.builder()
			.setModelPath("/data/local/tmp/llm/gemma-2b-it-gpu-int4.bin")
			.setMaxTokens(10000)
			.setTopK(40)
			.setTemperature(0.8f)
			.setRandomSeed(101)
			.build()
	}

	private var llmInference: LlmInference? = null

	override fun setLlmInference() {
		if (llmInference == null) {
			llmInference = LlmInference.createFromOptions(context, options)
		}
	}

	override fun generateLlm(text: String): String {
		return llmInference?.generateResponse(text) ?: ""
	}

	override fun close() {
		llmInference?.close()
		llmInference = null
	}
}
