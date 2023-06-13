plugins {
    kotlin("jvm") version "1.8.22"
    application
}

group = "cz.lukynka"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-netty:1.6.0")
    implementation("io.ktor:ktor-network:1.6.0")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("cz.lukynka.dockyard.MainKt")
}