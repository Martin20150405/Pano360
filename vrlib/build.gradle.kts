plugins {
    id("com.android.library")
    kotlin("android")
}

android {
	compileSdk = libs.versions.compileSdk.get().toInt()

	defaultConfig {
		minSdk = libs.versions.minSdk.get().toInt()
		compileSdk = libs.versions.compileSdk.get().toInt()
		targetSdk = libs.versions.targetSdk.get().toInt()
	}
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.jakewharton.timber:timber:5.0.1")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
}
