plugins {
    kotlin("jvm")
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
    api(project(":Scroll"))
    compileOnly(project(":common"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}