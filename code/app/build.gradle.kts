plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<Test>{
        useJUnitPlatform()
    }
}

dependencies {
    implementation("com.google.firebase:firebase-messaging:23.1.0")  // Add Firebase Messaging
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.storage)
    implementation(libs.zxing)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)

    // Mockito for mocking
    testImplementation("org.mockito:mockito-core:4.0.0")

    // Robolectric for Android UI testing
    testImplementation("org.robolectric:robolectric:4.8.1")

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.0.1")
    testImplementation(libs.junit.jupiter)


    // JUnit Vintage to allow JUnit 4 tests to run on JUnit 5 platform
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.8.1")

    implementation(libs.circleimageview)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.guava)
    implementation(libs.coroutines.android)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.google.maps)
    implementation(libs.play.services.location)

}
