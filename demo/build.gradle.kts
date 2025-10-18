plugins {
    kotlin("jvm")
}

group = "io.github.dockyardmc.demo"
version = parent!!.version

repositories {
    mavenCentral()
    maven("https://mvn.devos.one/releases")
    maven("https://mvn.devos.one/snapshots")
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.viaversion.com")
    maven("https://maven.noxcrew.com/public")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}