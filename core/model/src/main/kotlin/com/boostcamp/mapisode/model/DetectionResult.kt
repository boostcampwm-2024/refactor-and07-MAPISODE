package com.boostcamp.mapisode.model

data class DetectionResult(
	val className: String,
	val score: Float,
	val x: Int, // 왼쪽 하단 x 좌표
	val y: Int, // 왼쪽 하단 y 좌표
	val width: Int,
	val height: Int,
)
