buildscript {
	val kotlinVersion: String by project
	println(kotlinVersion)

	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}

	// TODO: use version catalogs version access when gradle allows it in buildscript
	dependencies {
		classpath("com.android.tools.build:gradle:7.0.4")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
	}
}
