plugins {
	alias(libs.plugins.mapisode.data)
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

	implementation(projects.core.firebase)
	implementation(platform(libs.firebase.bom))
	implementation(libs.firebase.firestore)
	implementation(libs.firebase.storage)

	implementation(projects.domain.episode)
}
