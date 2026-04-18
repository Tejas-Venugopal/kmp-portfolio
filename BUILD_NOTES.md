# Build & dependency reference

These snippets show the minimum Gradle wiring required for the code in this
repository (Coil 3 KMP, the `expect`/`actual` URL opener, and Wasm output).

> Adjust group/version coordinates to whatever is current when you build.

## `gradle/libs.versions.toml`

```toml
[versions]
kotlin = "2.0.21"
compose = "1.7.0"
coil = "3.0.4"
ktor = "3.0.0"
coroutines = "1.9.0"
agp = "8.5.2"

[libraries]
coil-compose       = { module = "io.coil-kt.coil3:coil-compose", version.ref = "coil" }
coil-network-ktor3 = { module = "io.coil-kt.coil3:coil-network-ktor3", version.ref = "coil" }
ktor-client-core   = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-client-js     = { module = "io.ktor:ktor-client-js", version.ref = "ktor" }
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
```

## `composeApp/build.gradle.kts` (excerpt)

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget()
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { it.binaries.framework { baseName = "ComposeApp" } }

    @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"          // produces composeApp.js — matches index.html
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.kotlinx.coroutines.core)

            // Coil 3 – KMP image loader
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.core)
        }
        androidMain.dependencies { implementation(libs.ktor.client.okhttp) }
        iosMain.dependencies     { implementation(libs.ktor.client.darwin) }
        wasmJsMain.dependencies  { implementation(libs.ktor.client.js)     }
    }
}
```

## `AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.INTERNET" />

<application
    android:name=".PortfolioApplication"
    ... >
    <activity android:name=".MainActivity"
              android:exported="true"
              android:theme="@style/Theme.Material3.DayNight.NoActionBar">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

## `PortfolioApplication.kt` (Android)

```kotlin
class PortfolioApplication : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        com.portfolio.platform.AppContextHolder.context = applicationContext
    }
}
```

## `MainActivity.kt` (Android)

```kotlin
class MainActivity : androidx.activity.ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        androidx.activity.compose.setContent {
            com.portfolio.ui.theme.PortfolioTheme {
                com.portfolio.ui.screen.MainScreen()
            }
        }
    }
}
```

## iOS entry point (`iosApp/iosApp/iOSApp.swift`)

```swift
import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup { ComposeView().ignoresSafeArea() }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController =
        MainViewControllerKt.MainViewController()
    func updateUIViewController(_ vc: UIViewController, context: Context) {}
}
```

…with a `MainViewController.kt` in `iosMain`:

```kotlin
fun MainViewController() = androidx.compose.ui.window.ComposeUIViewController {
    com.portfolio.ui.theme.PortfolioTheme { com.portfolio.ui.screen.MainScreen() }
}
```

