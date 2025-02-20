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

class ObjectDetectionRepositoryImpl(private val context: Context) : ObjectDetectionRepository {
	val options by lazy {
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
			parseDetectionResults(it.toString())
		} ?: emptyList()
	}

	private fun parseDetectionResults(resultString: String): List<DetectionResult> {
		val detectionResults = mutableListOf<DetectionResult>()

		// "Detection #"을 기준으로 각각의 블록으로 나눔
		val detectionBlocks = resultString.split("Detection #").drop(1)

		// Box 정보 추출 정규식: (x: 숫자, y: 숫자, w: 숫자, h: 숫자)
		val boxRegex = Regex("""Box:\s*\(x:\s*(\d+),\s*y:\s*(\d+),\s*w:\s*(\d+),\s*h:\s*(\d+)\)""")
		// score 정보 추출 정규식
		val scoreRegex = Regex("""score\s*:\s*([0-9.]+)""")
		// class name 정보 추출 정규식
		val classNameRegex = Regex("""class name\s*:\s*(\S+)""")

		for (block in detectionBlocks) {
			val boxMatch = boxRegex.find(block)
			val scoreMatch = scoreRegex.find(block)
			val classNameMatch = classNameRegex.find(block)

			if (boxMatch != null && scoreMatch != null && classNameMatch != null) {
				val x = boxMatch.groupValues[1].toInt()
				val y = boxMatch.groupValues[2].toInt()
				val w = boxMatch.groupValues[3].toInt()
				val h = boxMatch.groupValues[4].toInt()
				val score = scoreMatch.groupValues[1].toFloat()
				val className = classNameMatch.groupValues[1]

				detectionResults.add(
					DetectionResult(
						className = className,
						score = score,
						x = x,
						y = y,
						width = w,
						height = h,
					),
				)
			}
		}
		return detectionResults
	}

	override fun close() {
		objectDetector?.close()
		objectDetector = null
	}
}
