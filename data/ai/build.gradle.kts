import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
	alias(libs.plugins.mapisode.data)
}

android {
	namespace = "com.boostcamp.mapisode.ai"

	defaultConfig {
		val azureSubscriptionKey =
			gradleLocalProperties(rootDir, providers).getProperty("AZURE_SUBSCRIPTION_KEY") ?: ""
		val azureEndpoint =
			gradleLocalProperties(rootDir, providers).getProperty("AZURE_ENDPOINT") ?: ""
		if (azureSubscriptionKey.isEmpty() || azureEndpoint.isEmpty()) {
			throw GradleException("AZURE_SUBSCRIPTION_KEY or AZURE_ENDPOINT is not set.")
		}
		buildConfigField("String", "AZURE_SUBSCRIPTION_KEY", "\"$azureSubscriptionKey\"")
		buildConfigField("String", "AZURE_ENDPOINT", "\"$azureEndpoint\"")
	}

	buildFeatures {
		buildConfig = true
	}
}

dependencies {
	implementation(projects.domain.ai)
	implementation(libs.mediapipe.tasks.genai)
	implementation(libs.translate)
}
