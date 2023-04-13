/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    kotlin("android")
    id("com.android.application")
    @Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
    alias(libs.plugins.ksp)
    id("designcompose.conventions.android-test-devices")
    id("designcompose.features.figma-token-task")
}

var applicationID = "com.android.designcompose.tutorial"
@Suppress("UnstableApiUsage")
android {
    namespace = applicationID
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = applicationID
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        // We use a bundled debug keystore, to allow debug builds from CI to be upgradable
        named("debug") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        getByName("debug") { signingConfig = signingConfigs.getByName("debug") }

        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug")
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

    buildFeatures { compose = true }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }

    packagingOptions { resources { excludes.add("/META-INF/{AL2.0,LGPL2.1}") } }
    kotlinOptions {
        // To enable additional debug reporting from Compose, uncomment the below lines and fill in
        // the <directory> to a valid location. Several files will be created there with information
        // on the composable functions. For more info see:
        // https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/design/compiler-metrics.md
        // Compose Compiler Metrics
        // freeCompilerArgs += [
        //    "-P",
        //    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=<directory>"
        // ]

        // Compose Compiler Report
        // freeCompilerArgs += [
        //    "-P",
        //    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=<directory>"
        // ]
    }
}

dependencies {
    implementation(project(":designcompose"))
    ksp(project(":codegen"))

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.android.gms.ossLicenses)

    testImplementation(libs.junit)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
