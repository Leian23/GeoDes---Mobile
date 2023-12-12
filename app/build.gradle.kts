plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}


android {

    namespace = "com.example.geodes_mobile"
    compileSdk = 33

    packagingOptions {
        jniLibs.pickFirsts.add("lib/**/libc++_shared.so")
    }   //



    defaultConfig {
        applicationId = "com.example.geodes_mobile"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        getByName("main") {
            res {
                srcDirs("src\\main\\res", "src\\main\\res\\layout\\useraccess")
            }
        }
    }
}


dependencies {


    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.firebase:firebase-auth:22.1.2")
    implementation("com.google.firebase:firebase-database:20.2.2")
    implementation("com.google.firebase:firebase-firestore:24.8.1")
    implementation("com.google.firebase:firebase-inappmessaging:20.3.5")
    implementation ("androidx.preference:preference:1.1.1")
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.material:material:1.9.0.")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-auth:21.0.1")
    implementation ("com.google.firebase:firebase-database:20.0.0")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))

    implementation("org.osmdroid:osmdroid-android:6.1.6")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.6")
    implementation("org.osmdroid:osmdroid-wms:6.1.6")

    implementation ("androidx.preference:preference:1.1.1")
    implementation ("org.osmdroid:osmdroid-android:6.1.10")
    implementation ("org.osmdroid:osmdroid-mapsforge:6.1.10")
    implementation ("com.loopj.android:android-async-http:1.4.10")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")



    implementation ("com.google.android.material:material:1.4.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("com.loopj.android:android-async-http:1.4.11")
    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("com.google.android.gms:play-services-location:21.0.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.google.firebase:firebase-storage")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

















}