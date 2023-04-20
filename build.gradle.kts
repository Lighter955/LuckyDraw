import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.20"
    id("org.jetbrains.compose")
}

group = "top.lightdev"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.compose.material3:material3-desktop:1.4.0")
                api(compose.foundation)
                api(compose.animation)
                api("moe.tlaster:precompose:1.3.15")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.0.0")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "LuckyDraw"
            packageVersion = "1.0.1"
            macOS {
                iconFile.set(project.file("/src/jvmMain/resources/icon_256.icns"))
            }
            windows {
                shortcut = true
                dirChooser = true
                iconFile.set(project.file("/src/jvmMain/resources/icon_256.ico"))
            }
            linux {
                iconFile.set(project.file("/src/jvmMain/resources/icon_256.png"))
            }
        }
        buildTypes.release.proguard {
            obfuscate.set(true)
            configurationFiles.from(project.file("proguard-rules.pro"))
        }

    }
}