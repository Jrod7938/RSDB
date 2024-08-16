plugins {
    kotlin("jvm") version "2.0.0"
    application
}

group = "com.gepc"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.gepc.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("dev.kord:kord-core:0.14.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}