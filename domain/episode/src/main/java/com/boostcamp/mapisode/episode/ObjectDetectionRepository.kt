package com.boostcamp.mapisode.episode

import com.boostcamp.mapisode.model.DetectionResult

interface ObjectDetectionRepository {
	fun detect(uri: String): List<DetectionResult>
	fun setObjectDetector()
	fun close()
}
