plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.9.23"
    application
}

group = "io.github.dockyardmc"
version = properties["dockyard.version"]!!

val githubUsername: String = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USER")
val githubPassword: String = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")

repositories {
    mavenCentral()
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/LukynkaCZE/PrettyLog")
            credentials { username = githubUsername; password = githubPassword }
        }
    }
}

dependencies {
    implementation("io.ktor:ktor-server-netty:2.3.10")
    implementation("io.ktor:ktor-network:2.3.10")
    implementation("cz.lukynka:pretty-log:1.2.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

}


kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("io.github.dockyard.MainKt")
}