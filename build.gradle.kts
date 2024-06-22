import java.io.IOException

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("io.ktor.plugin") version "2.2.3"
    application
}

val minecraftVersion = "1.21"
val dockyardVersion = properties["dockyard.version"]!!
val gitBranch = "git rev-parse --abbrev-ref HEAD".runCommand()
val gitCommit = "git rev-parse --short=8 HEAD".runCommand()

group = "io.github.dockyardmc"
version = "${dockyardVersion}_${gitCommit}@${gitBranch}_mc${minecraftVersion}"

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("io.github.dockyard.MainKt")
}

repositories {
    mavenCentral()
    maven("https://mvn.devos.one/releases")
    maven("https://jitpack.io")
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("com.akuleshov7:ktoml-core:0.5.1")
    implementation("com.akuleshov7:ktoml-file:0.5.1")

    // Minecraft
    implementation("io.github.jglrxavpok.hephaistos:common:2.2.0")
    implementation("io.github.jglrxavpok.hephaistos:gson:2.2.0")
    implementation("io.github.dockyardmc:scroll:1.6")
    implementation("io.github.dockyardmc:wikivg-datagen:1.3")

    // Networking
    implementation("io.ktor:ktor-server-netty:2.3.10")
    implementation("io.ktor:ktor-network:2.3.10")

    // Logging
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.1")
    implementation("cz.lukynka:pretty-log:1.4")

    // Other
    implementation("org.reflections:reflections:0.9.12")
    implementation("com.google.guava:guava:33.2.0-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("it.unimi.dsi:fastutil:8.5.13")
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

task("generateVersionFile") {
    val outputDir = file("$buildDir/generated/resources/")
    val outputFile = file("$outputDir/dock.yard")
    outputs.file(outputFile)

    doLast {
        outputDir.mkdirs()
        outputFile.writeText("${dockyardVersion}|${minecraftVersion}|${gitBranch}|${gitCommit}")
    }
}

tasks.processResources {
    dependsOn("generateVersionFile")
}

sourceSets["main"].resources.srcDir("${buildDir}/generated/resources/")
