plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    // Main class generated for top-level 'main' in Kotlin file is '<package>.MainKt'
    mainClass.set("huertohogar.MainKt")
}

// Ensure `./gradlew run` forwards stdin so the console app can read input interactively
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

// Configure shadowJar to produce an executable fat JAR
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("huertohogar-movil")
    archiveClassifier.set("")
    archiveVersion.set("1.0-SNAPSHOT")
    manifest {
        attributes(mapOf("Main-Class" to "huertohogar.MainKt"))
    }
}