plugins {
   alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)


    // Add the Google services Gradle plugin
  //  id("com.google.gms.google-services")
   // id("com.android.application")
}

android {
    namespace = "com.example.cafeteriaapplication";
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {

        buildConfigField(
            "String",
            "OPEN_API_KEY",
            "\"${project.properties["OPEN_API_KEY"]}\""
        )
        applicationId = "com.example.cafeteriaapplication"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL" // or 'SYMBOL_TABLE'
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
//gif
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    implementation(libs.firebase.database)
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation ("com.squareup.retrofit2:converter-jackson:2.7.2")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.10.3")
    implementation ("com.fasterxml.jackson.core:jackson-core:2.10.3")
    implementation ("com.fasterxml.jackson.core:jackson-annotations:2.10.3")
    implementation ("androidx.core:core:1.12.0") // 또는 최신 버전
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}