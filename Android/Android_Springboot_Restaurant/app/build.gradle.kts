plugins {
    id("com.android.application")
}

android {
    namespace = "com.archive.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.archive.app"
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
}

dependencies {


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    implementation("com.github.getActivity:XXPermissions:8.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment:1.8.1")


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")

    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation ("com.google.android.exoplayer:exoplayer:2.15.1")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("com.alibaba:fastjson:1.2.76")

    implementation ("androidx.navigation:navigation-fragment:2.4.1")
    implementation ("androidx.navigation:navigation-ui:2.4.1")

    implementation ("androidx.work:work-runtime:2.8.1")
    // implementation ("com.prolificinteractive:material-calendarview:1.4.3")
    implementation ("com.kizitonwose.calendar:view:2.6.0")


    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")


}