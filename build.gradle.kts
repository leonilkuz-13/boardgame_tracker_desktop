import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") version "1.9.24"
    id("com.android.application") version "8.2.2"
    id("org.jetbrains.compose") version "1.6.11"
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                implementation("org.junit.platform:junit-platform-launcher")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation("androidx.activity:activity-compose:1.9.0")
                
                val room_version = "2.6.1"
                implementation("androidx.room:room-runtime:$room_version")
                implementation("androidx.room:room-ktx:$room_version")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.xerial:sqlite-jdbc:3.45.1.0")
                implementation("org.slf4j:slf4j-nop:1.7.36")
            }
        }

        val desktopTest by getting {
            dependencies {
                // Inherits from commonTest
            }
        }
    }
}

android {
    namespace = "org.example.boardgame"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.example.boardgame"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        lintConfig = file("lint.xml")
        disable += listOf("NewApi", "MissingTranslation")
    }
}

compose.desktop {
    application {
        mainClass = "org.example.boardgame.MainKt"
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

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    add("kspAndroid", "androidx.room:room-compiler:2.6.1")
}
