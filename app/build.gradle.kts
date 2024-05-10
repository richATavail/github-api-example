import java.util.Properties

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.jetbrains.kotlin.android)
	kotlin("plugin.serialization")
}

val localProperties = Properties().apply {
	val localPropertiesFile = rootProject.file("local.properties")
	if (localPropertiesFile.exists()) {
		load(localPropertiesFile.inputStream())
	}
}

// We attempt to read the GitHub API token from local.properties, if
// one doesn't exist, we default to an empty string and log a warning.
val githubApiToken = localProperties.getProperty("GITHUB_API_TOKEN") ?: ""
if (githubApiToken.isEmpty()) {
	logger.warn(
		"***********************************************************************\n" +
		"*                             SETUP ISSUE                             *\n" +
		"* GITHUB_API_TOKEN is missing in local.properties. App will run       *\n" +
		"* without the token and may be rate limited by GitHub. See the        *\n" +
		"* README for instructions on how to add the token.                    *\n" +
		"***********************************************************************"
	)
}

android {
	namespace = "com.bitwisearts.android.githubpopularityboards"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.bitwisearts.android.githubpopularityboards"
		minSdk = 31
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
		// We attempt to read the GitHub API token from local.properties, if
		// one doesn't exist, we default to an empty string.
		buildConfigField(
			"String",
			"GITHUB_API_TOKEN",
			"\"$githubApiToken\""
		)
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
	buildFeatures {
		// Enables adding variables from local.properties to the BuildConfig
		buildConfig = true
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.13"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	// Enables the use of the WindowSizeClass API
	implementation(libs.androidx.material3windowsizeclass)
	implementation(libs.androidx.navigation.compose)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.ktor.client.core)
	implementation(libs.ktor.client.cio)
	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.ktor.client.android)
}