package com.boostcamp.mapisode.home.common

import androidx.annotation.DrawableRes

data class AiOption(
	val type: OptionType,
	@DrawableRes val icon: Int,
	val text: String,
	val prompt: String,
)
