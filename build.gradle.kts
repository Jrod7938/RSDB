plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    application
}

group = "com.gepc"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.gepc.BotKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("dev.kord:kord-core:0.14.0")
    implementation("io.ktor:ktor-client-core:2.3.5") // HTTP client for API requests
    implementation("io.ktor:ktor-client-cio:2.3.5") // CIO engine for Ktor client
    implementation("io.ktor:ktor-client-serialization:2.3.5") // Serialization support for Ktor client
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1") // JSON serialization
    implementation("org.jsoup:jsoup:1.16.1") // Jsoup for HTML parsing


    // Logging dependencies
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.14")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}