package com.boostcamp.mapisode.episode.ai

import android.content.Context
import android.content.res.AssetManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import org.tensorflow.lite.Interpreter
import timber.log.Timber
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class KoLLM(private val context: Context) {
	// TFLite Interpreter 인스턴스
	private var interpreter: Interpreter? = null
	var isInitialized = false
		private set

	/** 백그라운드에서 추론 작업을 수행할 Executor */
	private val executorService: ExecutorService = Executors.newCachedThreadPool()

	// 모델 관련 파라미터 (모델에 맞게 수정 필요)
	private var modelInputSize: Int = 128 // 예: 최대 토큰 길이
	private var vocabSize: Int = 10000 // 예: 어휘 사전 크기

	/**
	 * TFLite 모델 초기화를 비동기적으로 수행.
	 */
	fun initialize(): Task<Void?> {
		val task = TaskCompletionSource<Void?>()
		executorService.execute {
			try {
				initializeInterpreter()
				task.setResult(null)
			} catch (e: IOException) {
				task.setException(e)
			}
		}
		return task.task
	}

	@Throws(IOException::class)
	private fun initializeInterpreter() {
		// assets에서 TFLite 모델 파일을 로드하고 interpreter를 초기화.
		val modelBuffer = loadModelFile(context.assets, "llm_model.tflite")
		val options = Interpreter.Options()
		// 필요한 경우 추가 옵션 설정
		interpreter = Interpreter(modelBuffer, options)

		// 모델 메타데이터를 통해 modelInputSize, vocabSize 등 값 읽어오기
		// 예시로 위의 기본값을 사용함.

		isInitialized = true
		Timber.d("Initialized LLM TFLite interpreter.")
	}

	@Throws(IOException::class)
	private fun loadModelFile(assetManager: AssetManager, filename: String): ByteBuffer {
		val fileDescriptor = assetManager.openFd(filename)
		val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
		val fileChannel = inputStream.channel
		val startOffset = fileDescriptor.startOffset
		val declaredLength = fileDescriptor.declaredLength
		return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
	}

	/**
	 * 주어진 프롬프트 텍스트를 바탕으로 모델이 텍스트를 생성하도록 합니다.
	 *
	 * @param inputText 프롬프트 텍스트
	 * @return 생성된 텍스트
	 */
	private fun generate(inputText: String): String {
		check(isInitialized) { "TFLite Interpreter is not initialized yet." }

		// 1. 입력 텍스트를 토큰화 (모델의 토크나이저에 맞게 구현 필요)
		val inputTokens = tokenize(inputText)

		// 2. 모델의 입력 형상에 맞게 입력 버퍼를 준비합니다.
		//    여기서는 [1, modelInputSize] 형태의 int 배열을 가정합니다.
		val inputBuffer = ByteBuffer.allocateDirect(modelInputSize * INT_TYPE_SIZE)
		inputBuffer.order(ByteOrder.nativeOrder())
		// 입력 토큰을 채우고, 부족한 부분은 패딩(예: 0)으로 채웁니다.
		for (i in 0 until modelInputSize) {
			val token = if (i < inputTokens.size) inputTokens[i] else 0
			inputBuffer.putInt(token)
		}
		inputBuffer.rewind()

		// 3. 출력 버퍼 준비
		//    예를 들어 [1, modelInputSize] 형태로 모델이 토큰 ID를 반환한다고 가정합니다.
		val outputBuffer = ByteBuffer.allocateDirect(modelInputSize * INT_TYPE_SIZE)
		outputBuffer.order(ByteOrder.nativeOrder())

		// 4. 모델 추론 실행
		interpreter?.run(inputBuffer, outputBuffer)
		outputBuffer.rewind()

		// 5. 출력 버퍼에서 토큰을 읽어옵니다.
		val outputTokens = IntArray(modelInputSize)
		for (i in 0 until modelInputSize) {
			outputTokens[i] = outputBuffer.int
		}

		// 6. 토큰을 다시 텍스트로 디토크나이즈 (모델의 디토크나이저에 맞게 구현 필요)
		val generatedText = detokenize(outputTokens)
		return generatedText
	}

	/**
	 * 비동기 텍스트 생성 함수.
	 */
	fun generateAsync(inputText: String): Task<String> {
		val task = TaskCompletionSource<String>()
		executorService.execute {
			val result = generate(inputText)
			task.setResult(result)
		}
		return task.task
	}

	/**
	 * 사용이 끝난 후 리소스 정리.
	 */
	fun close() {
		executorService.execute {
			interpreter?.close()
			interpreter = null
			Timber.d("Closed LLM TFLite interpreter.")
		}
	}

	/**
	 * 더미 토크나이저: 문자열을 정수 토큰 ID 목록으로 변환합니다.
	 * 실제 모델의 토크나이저 로직에 맞게 구현해야 합니다.
	 */
	private fun tokenize(text: String): List<Int> {
		// 예시: 각 문자를 유니코드 값으로 변환 (실제 사용시에는 BPE, WordPiece 등 사용)
		return text.map { it.toInt() }
	}

	/**
	 * 더미 디토크나이저: 토큰 ID 목록을 문자열로 변환합니다.
	 * 실제 모델의 디토크나이저 로직에 맞게 구현해야 합니다.
	 */
	private fun detokenize(tokens: IntArray): String {
		// 예시: 패딩(0) 토큰은 건너뛰고, 각 토큰을 문자로 변환
		val sb = StringBuilder()
		for (token in tokens) {
			if (token != 0) {
				sb.append(token.toChar())
			}
		}
		return sb.toString()
	}

	companion object {
		private const val TAG = "LLMTFLite"
		private const val INT_TYPE_SIZE = 4 // int는 4바이트
	}
}
