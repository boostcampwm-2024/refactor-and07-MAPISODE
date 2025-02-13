package com.boostcamp.ai.imagecaption

import android.content.Context
import android.net.Uri
import com.boostcamp.mapisode.ai.BuildConfig
import com.boostcamp.mapisode.episode.ImageCaptionRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import timber.log.Timber

class ImageCaptionRepositoryImpl(private val context: Context) : ImageCaptionRepository {
	private val endPoint: String = BuildConfig.AZURE_ENDPOINT
	private val subscriptionKey: String = BuildConfig.AZURE_SUBSCRIPTION_KEY

	private val json = Json {
		ignoreUnknownKeys = true
		coerceInputValues = true
		encodeDefaults = true
		isLenient = true
		prettyPrint = true
	}

	private val retrofit = Retrofit.Builder()
		.baseUrl(endPoint)
		.addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
		.build()

	private val api = retrofit.create(ComputerVisionApi::class.java)

//    suspend fun analyzeImage(
//        image: String
//    ): ImageAnalysisResponse {
//        return withContext(Dispatchers.IO) {
//            try {
//                api.analyzeImage(
//                    subscriptionKey = subscriptionKey,
//                    request = ImageAnalysisRequest(image)
//                )
//            } catch (e: Exception) {
//                throw when (e) {
//                    is HttpException -> when (e.code()) {
//                        400 -> IllegalArgumentException("잘못된 요청입니다: ${e.message()}")
//                        401 -> SecurityException("인증에 실패했습니다. API 키를 확인해주세요.")
//                        404 -> NoSuchElementException("리소스를 찾을 수 없습니다.")
//                        else -> e
//                    }
//
//                    else -> e
//                }
//            }
//        }
//    }

	override suspend fun generateImageCaption(imagePath: String): List<String> {
		return try {
			val result = context.contentResolver.openInputStream(Uri.parse(imagePath))?.readBytes()?.let { imageBytes ->
				val requestBody = imageBytes.toRequestBody("application/octet-stream".toMediaType())
				api.analyzeImage(
					subscriptionKey = subscriptionKey,
					request = requestBody,
				).denseCaptionsResult.values.map { it.text }
			} ?: emptyList()
			Timber.e("Image caption result: $result")
			result
		} catch (e: Exception) {
			throw when (e) {
				is HttpException -> when (e.code()) {
					400 -> IllegalArgumentException("잘못된 요청입니다: ${e.message()}")
					401 -> SecurityException("인증에 실패했습니다. API 키를 확인해주세요.")
					404 -> NoSuchElementException("리소스를 찾을 수 없습니다.")
					else -> e
				}
				else -> e
			}
		}
	}
}
