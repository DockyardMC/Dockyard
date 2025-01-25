plugins {
    kotlin("jvm")
}

group = "io.github.dockyardmc.sailboat"
version = parent!!.version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":Protocol"))
    implementation(project(":Socket"))
    implementation(libs.prettylog)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}