package com.boostcamp.ai.objectdetection

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import com.boostcamp.mapisode.episode.ObjectDetectionRepository
import com.boostcamp.mapisode.model.DetectionResult
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector.ObjectDetectorOptions
import timber.log.Timber

class ObjectDetectionRepositoryImpl(private val context: Context) : ObjectDetectionRepository {
	private val options: ObjectDetectorOptions by lazy {
		ObjectDetectorOptions.builder()
			.setBaseOptions(
				BaseOptions.builder().setModelAssetPath("efficientdet_lite2.tflite").build(),
			)
			.setRunningMode(RunningMode.IMAGE)
			.setMaxResults(5)
			.build()
	}

	private var objectDetector: ObjectDetector? = null

	override fun setObjectDetector() {
		if (objectDetector == null) {
			objectDetector = ObjectDetector.createFromOptions(context, options)
		}
	}

	override fun detect(uri: String): List<DetectionResult> {
		val image = context.contentResolver.openInputStream(uri.toUri())?.use { inputStream ->
			BitmapFactory.decodeStream(inputStream)
		}
		val mpImage = BitmapImageBuilder(image).build()
		return objectDetector?.detect(mpImage)?.let {
			Timber.e(it.toString())
			parseDetectionString(it.toString())
		} ?: emptyList()
	}

	private fun parseDetectionString(input: String): List<DetectionResult> {
		// 정규식 패턴들
		val categoryPattern = """Category "([^"]+)"[^)]*score=([0-9.]+)""".toRegex()
		val boundingBoxPattern = """RectF\(([0-9.]+), ([0-9.]+), ([0-9.]+), ([0-9.]+)\)""".toRegex()

		// Detection 블록들을 찾기
		val detectionBlocks = input.split("Detection{")
			.drop(1) // 첫 번째 요소는 헤더이므로 제외

		return detectionBlocks.mapNotNull { block ->
			try {
				// Category 정보 추출
				val categoryMatch = categoryPattern.find(block) ?: return@mapNotNull null
				val className = categoryMatch.groupValues[1]
				val score = categoryMatch.groupValues[2].toFloat()

				// BoundingBox 정보 추출
				val boundingBoxMatch = boundingBoxPattern.find(block) ?: return@mapNotNull null
				val left = boundingBoxMatch.groupValues[1].toFloat().toInt()
				val top = boundingBoxMatch.groupValues[2].toFloat().toInt()
				val right = boundingBoxMatch.groupValues[3].toFloat().toInt()
				val bottom = boundingBoxMatch.groupValues[4].toFloat().toInt()

				// width와 height 계산
				val width = right - left
				val height = bottom - top

				DetectionResult(
					className = className,
					score = score,
					x = left,
					y = bottom, // 왼쪽 하단 y좌표이므로 bottom 값 사용
					width = width,
					height = height,
				)
			} catch (e: Exception) {
				null // 파싱 실패 시 null 반환하여 필터링
			}
		}
	}

	override fun close() {
		objectDetector?.close()
		objectDetector = null
	}
}
