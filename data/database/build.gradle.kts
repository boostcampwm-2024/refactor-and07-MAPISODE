plugins {
	alias(libs.plugins.mapisode.data)
}

android {
	namespace = "com.boostcamp.mapisode.database"
}

dependencies {
	implementation(libs.room.runtime)
	ksp(libs.room.compiler)
	implementation(libs.room.ktx)
}
