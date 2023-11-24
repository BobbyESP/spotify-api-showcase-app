plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.bobbyesp.spotifyapishowcaseapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bobbyesp.spotifyapishowcaseapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        manifestPlaceholders["redirectHostName"] = "showcaseapp"
        manifestPlaceholders["redirectSchemeName"] = "placeholder"
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String", "CLIENT_ID", "\"${project.properties["CLIENT_ID"]}\""
            )
            buildConfigField(
                "String", "CLIENT_SECRET", "\"${project.properties["CLIENT_SECRET"]}\""
            )
            buildConfigField(
                "String", "SPOTIFY_REDIRECT_URI_PKCE", "\"showcaseapp://spotify-pkce\""
            )
        }
        debug {
            isMinifyEnabled = false
            buildConfigField(
                "String", "CLIENT_ID", "\"${project.properties["CLIENT_ID"]}\""
            )
            buildConfigField(
                "String", "CLIENT_SECRET", "\"${project.properties["CLIENT_SECRET"]}\""
            )
            buildConfigField(
                "String", "SPOTIFY_REDIRECT_URI_PKCE", "\"showcaseapp://spotify-pkce\""
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.accompanist)

    //Serialization
    implementation(libs.kotlin.serialization.json)

    //Image loading
    implementation(libs.coil.compose)

    //DI (Dependency Injection - Hilt)
    implementation(libs.bundles.hilt)
    implementation(libs.androidx.material3.windowsizeclass)
    ksp(libs.bundles.hilt.ksp)

    //Coroutines
    implementation(libs.bundles.coroutines)

    //Pagination
    implementation(libs.bundles.paging)

    //Chrome Custom Tabs
    implementation(libs.chrome.custom.tabs)

    //MD Parser
    implementation(libs.markdown)

    //Spotify API
    implementation(libs.spotify.api.android)

    //MMKV (Key-Value storage)
    implementation(libs.mmkv)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}