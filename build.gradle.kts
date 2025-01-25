import java.io.IOException

plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("io.ktor.plugin") version "2.2.3"
    application
}

val minecraftVersion = "1.21.3"
val dockyardVersion = properties["dockyard.version"]!!
val gitBranch = "git rev-parse --abbrev-ref HEAD".runCommand()
val gitCommit = "git rev-parse --short=8 HEAD".runCommand()

group = "io.github.dockyardmc"
version = "${dockyardVersion}_${gitCommit}@${gitBranch}_mc${minecraftVersion}"

repositories {
    mavenCentral()
    maven("https://mvn.devos.one/releases")
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.lucko.me/")
}

subprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven("https://mvn.devos.one/releases")
        maven("https://jitpack.io")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://repo.lucko.me/")
    }

    kotlin {
        jvmToolchain(21)
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        withSourcesJar()
        withJavadocJar()
    }
}


dependencies {

}

tasks.test {
    useJUnitPlatform()
}

fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String = ProcessBuilder(split("\\s(?=(?:[^'\"`]*(['\"`])[^'\"`]*\\1)*[^'\"`]*$)".toRegex()))
    .directory(workingDir)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start()
    .apply { waitFor(timeoutAmount, timeoutUnit) }
    .run {
        val error = errorStream.bufferedReader().readText().trim()

        if (error.isNotEmpty()) {
            throw IOException(error)
        }

        inputStream.bufferedReader().readText().trim()
    }

