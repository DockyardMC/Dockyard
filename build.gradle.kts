import java.io.IOException
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "2.0.0"
    application
}

val minecraftVersion = "1.20.4"
val dockyardVersion = properties["dockyard.version"]!!
val gitBranch = "git rev-parse --abbrev-ref HEAD".runCommand()
val gitCommit = "git rev-parse --short=8 HEAD".runCommand()
val githubUsername: String = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USER")
val githubPassword: String = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")

group = "io.github.dockyardmc"
version = "${dockyardVersion}_${gitCommit}@${gitBranch}_mc${minecraftVersion}"

repositories {
    mavenCentral()
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/LukynkaCZE/PrettyLog")
            credentials { username = githubUsername; password = githubPassword }
        }
        maven {
            url = uri("https://maven.pkg.github.com/DockyardMC/Scroll")
            credentials { username = githubUsername; password = githubPassword }
        }
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

dependencies {
    implementation("io.ktor:ktor-server-netty:2.3.10")
    implementation("io.ktor:ktor-network:2.3.10")
    implementation("cz.lukynka:pretty-log:1.2.2")
    implementation("io.github.dockyardmc:scroll:1.4")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.github.jglrxavpok.hephaistos:common:2.2.0")
    implementation("io.github.jglrxavpok.hephaistos:gson:2.2.0")
    implementation("com.google.guava:guava:33.2.0-jre")
    implementation("it.unimi.dsi:fastutil:8.5.13")
    implementation(kotlin("stdlib-jdk8"))
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

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("io.github.dockyard.MainKt")
}

tasks.register("generateVersionFile") {
    val outputDir = file("$buildDir/generated/resources/")
    val outputFile = file("$outputDir/dock.yard")
    outputs.file(outputFile)
    doLast {
        outputDir.mkdirs()
        outputFile.writeText("${dockyardVersion}|${minecraftVersion}|${gitBranch}|${gitCommit}")
    }
}

tasks.named("processResources") {
    dependsOn("generateVersionFile")
}

sourceSets["main"].resources.srcDir("$buildDir/generated/resources/")
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}