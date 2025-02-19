import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
	alias(libs.plugins.mapisode.feature)
}

android {
	namespace = "com.boostcamp.mapisode.episode"

	defaultConfig {
		val googleGenerativeAi =
			gradleLocalProperties(rootDir, providers).getProperty("GOOGLE_GENERATIVE_AI") ?: ""

		if (googleGenerativeAi.isEmpty()) {
			throw GradleException("GOOGLE_GENERATIVE_AI Key is not set.")
		}
		buildConfigField("String", "GOOGLE_GENERATIVE_AI", "\"$googleGenerativeAi\"")

		ndk {
			abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
		}
	}

	buildFeatures {
		buildConfig = true
		mlModelBinding = true
	}
}

dependencies {
	implementation(project.libs.bundles.naverMap)
	implementation(project.libs.bundles.coil)
	implementation(project.libs.google.cloud.generativeai)

	implementation(libs.tensorflow.lite.support)
	implementation(libs.tensorflow.lite.metadata)
	implementation(libs.tensorflow.lite.gpu)

	implementation(projects.core.network)
	implementation(projects.domain.episode)
	implementation(projects.domain.mygroup)
}
