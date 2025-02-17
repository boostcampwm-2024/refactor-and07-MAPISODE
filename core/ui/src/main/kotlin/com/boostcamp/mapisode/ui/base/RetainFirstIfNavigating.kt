package com.boostcamp.mapisode.ui.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

fun Flow<UiIntent>.retainFirstIfNavigating(vararg intents: KClass<out UiIntent>) = channelFlow {
	var isFirst = true
	collectLatest { value ->
		if (isFirst) {
			launch(Dispatchers.IO) {
				isFirst = false
				send(value)
				if (intents.any { it.isInstance(value) }) {
					delay(400)
				}
				isFirst = true
			}
		}
	}
}
