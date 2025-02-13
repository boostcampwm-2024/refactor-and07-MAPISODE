package com.boostcamp.ai.logger

import com.boostcamp.mapisode.episode.Logger
import timber.log.Timber

class LoggerImpl : Logger {
	override fun d(message: String) = Timber.d(message)
	override fun e(message: String) = Timber.e(message)
	override fun i(message: String) = Timber.i(message)
}
