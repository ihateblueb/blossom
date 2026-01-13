plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.0"
}

group = "site.remlit"
version = "0.2.0"

repositories {
    mavenCentral()

    flatDir {
        dirs("libs")
    }
}

dependencies {
    compileOnly(":HytaleServer")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

kotlin {
    jvmToolchain(21)
}

tasks.processResources {
    filesMatching("manifest.json") {
        filter { line ->
            line.replace("%version%", project.provider { project.version.toString() }.get())
        }
    }
}

tasks.build {
    dependsOn("shadowJar")
}