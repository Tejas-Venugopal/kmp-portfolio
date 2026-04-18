import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

// On Netlify (and most CI systems) CI=true is set. Skip Android entirely in CI.
val isCI = System.getenv("CI") == "true"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    if (!System.getenv("CI").equals("true")) {
        alias(libs.plugins.androidApplication)
    }
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    // ---------- Android ----------
    if (!isCI) {
        androidTarget {
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }

    // ---------- iOS ----------
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    // ---------- Web (Wasm) ----------
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp" // produces composeApp.js — must match index.html <script src>
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    // ---------- Source sets ----------
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.kotlinx.coroutines.core)

            // Coil 3 — KMP image loader
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.core)
        }

        androidMain.dependencies {
            if (!isCI) {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core.ktx)
                implementation(libs.ktor.client.okhttp)
            }
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

if (!isCI) {
    android {
        namespace = "com.portfolio"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        defaultConfig {
            applicationId = "com.portfolio"
            minSdk = libs.versions.android.minSdk.get().toInt()
            targetSdk = libs.versions.android.targetSdk.get().toInt()
            versionCode = 1
            versionName = "1.0.0"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        buildFeatures {
            compose = true
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
            }
        }
    }
}
