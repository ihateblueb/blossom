plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.0"
    kotlin("plugin.serialization") version "2.3.0"
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

    implementation("net.dv8tion:JDA:6.3.0") {
        exclude(module = "opus-java")
        exclude(module = "tink")
    }

    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("ch.qos.logback:logback-classic:1.5.20")
    implementation("org.slf4j:slf4j-api:2.0.17")

    implementation(project(":blossom-formatter"))
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