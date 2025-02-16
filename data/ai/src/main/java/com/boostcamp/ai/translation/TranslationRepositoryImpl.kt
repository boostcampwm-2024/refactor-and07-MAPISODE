package com.boostcamp.ai.translation

import com.boostcamp.mapisode.episode.TranslationRepository
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import timber.log.Timber

class TranslationRepositoryImpl : TranslationRepository {

	private val options = TranslatorOptions.Builder()
		.setSourceLanguage(TranslateLanguage.ENGLISH)
		.setTargetLanguage(TranslateLanguage.KOREAN)
		.build()
	private val conditions = DownloadConditions.Builder()
		// Wi-Fi is only accepted for downloading the model
		.requireWifi()
		.build()
	private val modelManager = RemoteModelManager.getInstance()
	private var isModelReady = false
	private val englishKoreanTranslator = Translation.getClient(options)

	override fun downloadModel() {
		englishKoreanTranslator.downloadModelIfNeeded(conditions)
			.addOnSuccessListener {
				Timber.e("Model downloaded successfully")
				isModelReady = true
			}
			.addOnFailureListener {
				Timber.e("Model download failed: $it")
				isModelReady = false
			}
	}

	override fun deleteModel() {
		val koreanModel = TranslateRemoteModel.Builder(TranslateLanguage.KOREAN).build()
		modelManager.deleteDownloadedModel(koreanModel)
			.addOnSuccessListener {
				Timber.e("Model deleted")
			}
			.addOnFailureListener {
				Timber.e("Model deletion failed")
			}
	}

	override fun translate(
		text: String,
		callback: (String) -> Unit,
	): List<String> {
		var result = ""
		if (isModelReady) {
			englishKoreanTranslator.translate(text)
				.addOnSuccessListener {
					Timber.e("Translation result for 1 line: $it")
					callback(it)
					result = it
				}
				.addOnFailureListener { exception ->
					result = "Translation failed: $exception"
				}
		} else {
			result = "Model is not ready"
		}
		return result.split("##")
	}

	override fun close() {
		englishKoreanTranslator.close()
	}
}
