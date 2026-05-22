import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.24"
    java
    id("org.jetbrains.compose") version "1.6.11"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.slf4j:slf4j-nop:1.7.36")
    implementation(compose.desktop.currentOs)
    implementation(compose.material)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// Create executable JAR with all dependencies
tasks.register<Jar>("fatJar") {
    manifest {
        attributes["Main-Class"] = "boardgame_tracker_desktop.MainKt"
        attributes["Implementation-Version"] = version
    }
    archiveFileName.set("app.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets["main"].output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.isFile }.map { zipTree(it) }
    })
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