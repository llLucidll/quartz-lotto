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
        targetSdk = 33
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

    testOptions {

        unitTests {
            isIncludeAndroidResources = true
        }
    }
}


dependencies {
    implementation(libs.firebase.messaging)  // Add Firebase Messaging
    implementation(libs.appcompat)
    implementation(platform(libs.firebase.bom)) // Check the latest version on Firebase docs
    implementation(libs.material)
    implementation(libs.activity)
    implementation (libs.firebase.auth.v2301)

    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.storage)
    implementation(libs.zxing)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.ext.junit)
    implementation(libs.rules)
    implementation(libs.espresso.intents)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    // Mockito Inline for mocking static methods
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoInline)
    implementation(libs.circleimageview)
    implementation(libs.glide)
    testImplementation(libs.core)
    testImplementation(libs.ext.junit)
    testImplementation(libs.espresso.core)
    testImplementation(libs.espresso.core)
    testImplementation(libs.espresso.core)
    testImplementation(libs.espresso.core)
    testImplementation(libs.espresso.core)
    testImplementation(libs.espresso.core)
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
    implementation("org.osmdroid:osmdroid-android:6.1.15")
    implementation("com.squareup.picasso:picasso:2.71828")


}