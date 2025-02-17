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
	}

	buildFeatures {
		buildConfig = true
	}
}

dependencies {
	implementation(project.libs.bundles.naverMap)
	implementation(project.libs.bundles.coil)
	implementation(project.libs.google.cloud.generativeai)

	implementation(projects.core.network)
	implementation(projects.domain.episode)
	implementation(projects.domain.mygroup)
}
