plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "co.mateocontreras"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

tasks.shadowJar {
    archiveBaseName.set("isis4208")
    archiveVersion.set("")
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "isis4208.Main"
    }
}