plugins {
    kotlin("jvm")
}

group = "io.github.dockyardmc.socket"
version = parent!!.version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly(project(":common"))
    compileOnly(project(":Protocol"))
    compileOnly(libs.prettylog)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}