plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt") // Add this line
}

android {
    namespace = "com.sc.aipdriver"
    compileSdk = 36

    signingConfigs {
        create("release") {
            keyAlias = "sldriver"
            keyPassword = "Sldriver@1234"
            storeFile = file("/Users/kc/AndroidStudioProjects/BitBucket/AIPDriver/driver.jks")
            storePassword = "Sldriver@1234"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    defaultConfig {
        applicationId = "com.sc.aipdriver"
        minSdk = 24
        targetSdk = 35
        versionCode = 32
        versionName = "1.0.32"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding= true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.drawerlayout:drawerlayout:1.0.0")
    implementation("androidx.activity:activity:1.13.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.google.android.gms:play-services-maps:19.2.0") // Replace with the latest version
    implementation("com.karumi:dexter:6.2.3")
    implementation ("com.google.android.gms:play-services-location:21.3.0") // Replace with the latest version
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.github.jd-alexander:library:1.1.0")
    //noinspection GradleCompatible
    implementation ("com.android.support:cardview-v7:26.1.0")
    implementation ("at.blogc:expandabletextview:1.0.3")
  //  implementation ("com.wang.avi:library:2.1.3")

        implementation ("com.google.android.play:app-update:2.1.0")

    implementation ("com.google.maps.android:android-maps-utils:2.3.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation(project(":libraryprogress"))
    implementation("io.github.maitrungduc1410:AVLoadingIndicatorView:2.1.4")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // ROOM DATABASE
    implementation("androidx.room:room-runtime:2.7.0")
    kapt("androidx.room:room-compiler:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")

    // WORK MANAGER
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // LIFECYCLE (for lifecycleScope)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // COROUTINES
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.google.android.material:material:1.11.0")

}