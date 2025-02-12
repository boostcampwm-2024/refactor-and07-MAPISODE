plugins {
	alias(libs.plugins.mapisode.data)
	alias(libs.plugins.ksp)
	alias(libs.plugins.room)
}

android {
	namespace = "com.boostcamp.mapisode.database"

	room {
		schemaDirectory("$projectDir/schemas")
	}
}

dependencies {
	implementation(libs.room.runtime)
	ksp(libs.room.compiler)
	implementation(libs.room.ktx)
	implementation(projects.domain.episode)
}
