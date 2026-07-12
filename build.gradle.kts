import org.gradle.api.internal.tasks.testing.filter.DefaultTestFilter
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") version "2.0.21"
    kotlin("plugin.compose") version "2.0.21"
    id("com.android.application") version "9.2.1"
    id("org.jetbrains.compose") version "1.7.3"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvmToolchain(21)

    androidTarget()

    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.activity:activity-compose:1.8.2")
                implementation("androidx.core:core-ktx:1.12.0")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.xerial:sqlite-jdbc:3.45.1.0")
                implementation("org.slf4j:slf4j-nop:1.7.36")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val desktopTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                implementation("org.junit.platform:junit-platform-launcher")
            }
        }
    }
}

android {
    namespace = "boardgame_tracker_desktop.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "boardgame_tracker_desktop.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

compose.desktop {
    application {
        mainClass = "boardgame_tracker_desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Dmg, TargetFormat.Deb)
            packageName = "BattleshipApp"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    filter.isFailOnNoMatchingTests = false

    doFirst {
        val fqFilter = filter as DefaultTestFilter
        val cliPatterns = fqFilter.commandLineIncludePatterns.toList()
        if (cliPatterns.isNotEmpty()) {
            val transformed = cliPatterns.map { p ->
                when {
                    p.endsWith(".*") -> "*${p.removeSuffix(".*")}*"
                    !p.contains("*") -> {
                        val parts = p.split(".")
                        if (parts.size > 1) "*${parts.last()}*" else p
                    }
                    else -> p
                }
            }
            fqFilter.setCommandLineIncludePatterns(transformed)
        }
    }
}
