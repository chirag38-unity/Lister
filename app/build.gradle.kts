import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp") version ("2.0.20-1.0.24")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
//    id("com.google.gms.google-services")
}

android {
    signingConfigs {
        create("release") {
            storeFile =
                file("C:\\Users\\redij\\AndroidStudioProjects\\Lister\\keystore\\lister_signing.jks")
            storePassword = "Listerrr@123"
            keyAlias = "key0"
            keyPassword = "Listerrr@123"
        }
    }
    namespace = "com.chirag_redij.lister"
    compileSdk = 34

    fun loadLocalProperties(rootDir: File): Properties {
        val propertiesFile = File(rootDir, "local.properties")
        val properties = Properties()
        if (propertiesFile.exists()) {
            propertiesFile.inputStream().use { inputStream ->
                properties.load(inputStream)
            }
        }
        return properties
    }

// Load properties
    val localProperties = loadLocalProperties(rootDir)

    val key: String = localProperties.getProperty("supabaseKey")
    val serviceKey: String = localProperties.getProperty("supabaseServiceKey")
    val url: String = localProperties.getProperty("supabaseUrl")
    val googleClientId: String = localProperties.getProperty("googleClientId")

    defaultConfig {
//        applicationId = "com.chirag_redij.lister"
        applicationId = "com.mciedusofttech.listerrr"
        minSdk = 25
        targetSdk = 34
        versionCode = 7
        versionName = "7.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String","supabaseKey","\"$key\"")
        buildConfigField("String","supabaseServiceKey","\"$serviceKey\"")
        buildConfigField("String","supabaseUrl","\"$url\"")
        buildConfigField("String","googleClientId","\"$googleClientId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.09.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Compose Navigation UI
    implementation("androidx.navigation:navigation-ui-ktx:2.8.0")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Compose - Destinations
    implementation("io.github.raamcosta.compose-destinations:core:1.11.6")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.11.6")

    // Firebase Auth & Firestore
//    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
//    implementation("com.google.firebase:firebase-firestore:24.10.3")

    // Coil & Lottie
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.google.accompanist:accompanist-coil:0.15.0")
    implementation("com.airbnb.android:lottie-compose:6.4.1")

    // Supabase
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:compose-auth:2.6.1")
    implementation("io.github.jan-tennert.supabase:compose-auth-ui:2.6.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.6.1")
    implementation("io.ktor:ktor-client-cio:2.3.12")

    // Serialiser
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.2")

    // Dagger-Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Google InApp Update
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    // Google InApp Review
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")

}