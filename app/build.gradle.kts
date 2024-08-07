plugins {
    alias(libs.plugins.android.application)
    //alias(libs.plugins.google.gms.google.services)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.prm_shop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.prm_shop"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(libs.retrofit)
    implementation(libs.gsonConverter)
    implementation(
        fileTree(
            mapOf(
                "dir" to "..\\ZaloPayLib",
                "include" to listOf("*.aar", "*.jar"),
                "exclude" to listOf("")
            )
        )
    )

    implementation(libs.firebase.firestore)


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.auth0.android:jwtdecode:2.0.0")
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.android.material:material:1.4.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

    implementation("com.google.firebase:firebase-messaging:22.0.0")

    implementation("androidx.multidex:multidex:2.0.1")

}
