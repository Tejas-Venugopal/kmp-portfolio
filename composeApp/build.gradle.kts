import com.android.build.gradle.AppExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

// CI=true is set by Netlify, GitHub Actions, etc. Not set on local dev machines.
val isCI = System.getenv("CI") == "true"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // Android plugin applied imperatively below so it can be skipped in CI
}

if (!isCI) {
    apply(plugin = "com.android.application")
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
    if (!isCI) {
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
    }

    // ---------- Web (Wasm) ----------
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
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
            implementation(compose.components.uiToolingPreview)

            implementation(libs.kotlinx.coroutines.core)

            // Coil 3 — KMP image loader
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.core)
        }

        if (!isCI) {
            androidMain.dependencies {
                implementation(compose.uiTooling)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core.ktx)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.coil.gif)          // animated GIF decoder
            }
        }

        if (!isCI) {
            iosMain.dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

if (!isCI) {
    configure<AppExtension> {
        namespace = "com.portfolio"
        compileSdkVersion(libs.versions.android.compileSdk.get().toInt())

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

        buildFeatures.compose = true

        packagingOptions {
            resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
            }
        }
    }
}
