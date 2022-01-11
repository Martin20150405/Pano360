plugins {
	id("com.android.application")
	kotlin("android")
}

android {
	defaultConfig {
		applicationId = "com.martin.ads.pano360demo"
		minSdk = libs.versions.minSdk.get().toInt()
		compileSdk = libs.versions.compileSdk.get().toInt()
		targetSdk = libs.versions.targetSdk.get().toInt()
	}

	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = libs.versions.jetpackCompose.get()
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android.txt"),
				"proguard-rules.pro",
				"proguard-gvr.pro"
			)
		}
	}

	dependencies {
		implementation(libs.bundles.composeAll)
		implementation(libs.androidx.coreKtx)
		implementation(libs.timber)
		implementation(libs.androidx.appcompat)

		implementation(libs.accompanist.permissions)
		implementation(projects.filepicker)
		implementation(projects.vrlib)
	}
}
