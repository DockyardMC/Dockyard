plugins {
    kotlin("jvm")
}

group = "io.github.dockyardmc.common"
version = "0.7.21_213cb551@restructure/monorepo_mc1.21.3"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}