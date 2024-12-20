import io.grpc.internal.SharedResourceHolder.release

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
  //  alias(libs.plugins.compose.compiler)
    id ("maven-publish")
}
group = "com.github.jitpack"
version = "1.0"

android {
    namespace = "com.joni.pdfexpress"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

}

dependencies {
    implementation(libs.androidx.compose.bom)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
}


   publishing {
       publications {
           // Creates a Maven publication called "release".
           register<MavenPublication>("release") {
               afterEvaluate {
       from(components["release"])
                   groupId = "com.github.jitpack"
                   artifactId = "android-example"
                   version = "1.0.0"
               }
           }
       }
   }
