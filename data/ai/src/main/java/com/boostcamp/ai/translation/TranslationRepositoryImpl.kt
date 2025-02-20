package com.boostcamp.ai.translation

import com.boostcamp.mapisode.episode.TranslationRepository
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import timber.log.Timber

class TranslationRepositoryImpl : TranslationRepository {

	private val options by lazy {
		TranslatorOptions.Builder()
			.setSourceLanguage(TranslateLanguage.ENGLISH)
			.setTargetLanguage(TranslateLanguage.KOREAN)
			.build()
	}

	private val reverseOptions by lazy {
		TranslatorOptions.Builder()
			.setSourceLanguage(TranslateLanguage.KOREAN)
			.setTargetLanguage(TranslateLanguage.ENGLISH)
			.build()
	}

	private val conditions by lazy {
		DownloadConditions.Builder()
			.requireWifi()
			.build()
	}

	private val modelManager by lazy {
		RemoteModelManager.getInstance()
	}

	override var isModelReady = false

	private var englishKoreanTranslator: Translator? = null

	override fun setEnglishKoreanTranslator() {
		if (englishKoreanTranslator == null) {
			englishKoreanTranslator = Translation.getClient(options)
			Timber.e("Translator created $this")
		}
	}

	override fun setKoreanEnglishTranslator() {
		if (englishKoreanTranslator == null) {
			englishKoreanTranslator = Translation.getClient(reverseOptions)
			Timber.e("Translator created")
		}
	}

	override fun downloadModel() {
		englishKoreanTranslator?.run {
			downloadModelIfNeeded(conditions)
				.addOnSuccessListener {
					Timber.e("Model downloaded successfully")
					isModelReady = true
				}
				.addOnFailureListener {
					Timber.e("Model download failed: $it")
					isModelReady = false
				}
		}
	}

	override fun downloadReverseModel() {
		englishKoreanTranslator?.run {
			downloadModelIfNeeded(conditions)
				.addOnSuccessListener {
					Timber.e("Model downloaded successfully")
					isModelReady = true
				}
				.addOnFailureListener {
					Timber.e("Model download failed: $it")
					isModelReady = false
				}
		}
	}

	override fun deleteModel(
		onDeleteSuccess: () -> Unit,
		onDeleteFailure: (String) -> Unit,
	) {
		val koreanModel = TranslateRemoteModel.Builder(TranslateLanguage.KOREAN).build()
		modelManager.deleteDownloadedModel(koreanModel)
			.addOnSuccessListener {
				Timber.e("Model deleted")
				isModelReady = false
			}
			.addOnFailureListener {
				Timber.e("Model deletion failed")
			}
	}

	override fun translate(
		text: String,
		onSuccess: (String) -> Unit,
		onFailrue: (String) -> Unit,
		onComplete: () -> Unit,
	) {
		Timber.e("Translating: $text")
		if (isModelReady) {
			englishKoreanTranslator?.run {
				translate(text)
					.addOnSuccessListener {
						Timber.e("Translation success: $it")
						onSuccess(it)
					}
					.addOnFailureListener { exception ->
						onFailrue(exception.localizedMessage ?: "Translation failed")
					}
					.addOnCompleteListener {
						onComplete()
					}
			}
		} else {
			onFailrue("Model is not ready")
		}
	}

	override fun close() {
		englishKoreanTranslator?.close()
		englishKoreanTranslator = null
	}
}
