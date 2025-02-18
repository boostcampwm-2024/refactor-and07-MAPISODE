package com.boostcamp.mapisode.home.common

import com.naver.maps.geometry.LatLng

data class ResultEpisode(
	val id: String,
	val owner: String,
	val distance: String = "",
	val reason: String = "",
	val thumbnail: String,
	val coordinates: LatLng
)
