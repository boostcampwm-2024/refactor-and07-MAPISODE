package com.boostcamp.ai.imagecaption

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageAnalysisResponse(
	@SerialName("denseCaptionsResult") val denseCaptionsResult: DenseCaptionsResult,
	@SerialName("metadata") val metadata: Metadata,
	@SerialName("modelVersion") val modelVersion: String,
)

@Serializable
data class DenseCaptionsResult(
	@SerialName("values") val values: List<Value>,
)

@Serializable
data class Value(
	@SerialName("boundingBox") val boundingBox: BoundingBox,
	@SerialName("confidence") val confidence: Double,
	@SerialName("text") val text: String,
)

@Serializable
data class BoundingBox(
	@SerialName("h") val h: Int,
	@SerialName("w") val w: Int,
	@SerialName("x") val x: Int,
	@SerialName("y") val y: Int,
)

@Serializable
data class Metadata(
	@SerialName("height") val height: Int,
	@SerialName("width") val width: Int,
)
