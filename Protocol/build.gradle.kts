plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.22"
}

group = "io.github.dockyardmc.protocol"
version = parent!!.version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    api(libs.bundles.netty)
    api(libs.bundles.hephaistos)
    api(libs.bundles.kotlinx)
    api(project(":Scroll"))
    implementation(project(":common"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}