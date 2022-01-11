enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Define locations for build logic
pluginManagement {
	resolutionStrategy {
		eachPlugin {
			when (requested.id.id) {
				"com.android.application", "com.android.library" -> useModule("com.android.tools.build:gradle:${requested.version}")
				"kotlin-android" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
			}
		}
	}
	repositories {
		gradlePluginPortal()
		google()
		mavenCentral()
		jcenter()
		maven(url = "https://jitpack.io")
		maven(url = "https://plugins.gradle.org/m2/")
	}
}

// Define locations for components
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		jcenter()
		mavenCentral()
		maven(url = "https://jitpack.io")
	}
}

rootProject.name = ("Pano360")

include(":filepicker")
include(":pano360demo")
include(":vrlib")
