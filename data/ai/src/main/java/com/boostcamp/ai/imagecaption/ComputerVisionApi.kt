package com.boostcamp.ai.imagecaption

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ComputerVisionApi {
	@POST("computervision/imageanalysis:analyze")
	suspend fun analyzeImage(
		@Header("Ocp-Apim-Subscription-Key") subscriptionKey: String,
		@Header("Content-Type") contentType: String = "application/octet-stream",
		@Query("api-version") apiVersion: String = "2024-02-01",
		@Query("features") features: String = "denseCaptions",
		@Query("language") language: String = "en",
		@Body request: RequestBody,
	): ImageAnalysisResponse
}
