package com.boostcamp.ai

interface LlmRepository{
	fun generateLlm(text: String): String
}
