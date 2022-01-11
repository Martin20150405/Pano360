plugins {
	id("com.android.library")
}

android {
	compileSdk = libs.versions.compileSdk.get().toInt()

	defaultConfig {
		minSdk = libs.versions.minSdk.get().toInt()
		compileSdk = libs.versions.compileSdk.get().toInt()
		targetSdk = libs.versions.targetSdk.get().toInt()
	}
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
}
