import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

plugins {
    `maven-publish`
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
    implementation("io.github.dockyardmc:scroll:1.8")
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
    implementation("cz.lukynka:kotlin-bindables:1.1")
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

publishing {
    repositories {
        maven {
            url = if(dockyardVersion.toString().endsWith("-SNAPSHOT")) {
                uri("https://mvn.devos.one/snapshots")
            } else {
                uri("https://mvn.devos.one/releases")
            }
            credentials {
                username = System.getenv()["MAVEN_USER"]
                password = System.getenv()["MAVEN_PASS"]
            }
        }
    }
    publications {
        register<MavenPublication>("maven") {
            groupId = "io.github.dockyardmc"
            artifactId = "dockyard"
            version = dockyardVersion.toString()
            from(components["java"])
        }
    }
}

tasks.publish {
    finalizedBy("sendPublishWebhook")
}

task("sendPublishWebhook") {
    group = "publishing"
    description = "Sends a webhook message after publishing to Maven."

    doLast {
        sendWebhookToDiscord(System.getenv("DISCORD_DOCKYARD_WEBHOOK"))
    }
}

fun sendWebhookToDiscord(webhookUrl: String) {
    val httpClient = HttpClient.newHttpClient()

    val requestBody = embed()
    val request = HttpRequest.newBuilder()
        .uri(URI(webhookUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build()

    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())

        .thenRun { println("Webhook sent successfully!") }
        .exceptionally { throwable ->
            throwable.printStackTrace()
            null
        }
}


fun embed(): String {
    val target = if(dockyardVersion.toString().endsWith("-SNAPSHOT")) "https://mvn.devos.one/snapshots" else "https://mvn.devos.one/releases"
    return """
        {
          "content": null,
          "embeds": [
            {
              "title": "Published to Maven",
              "description": "`io.github.dockyardmc:dockyard:$dockyardVersion` was successfully published to maven **$target**!",
              "color": 5046022
            }
          ],
          "username": "Mavenboo",
          "avatar_url": "https://storage.moemate.io/9edcfd27fd20abe29e93bf904f633d61b4fccadc/3f1c4383-1ba3-43f9-891e-f6a96abbe970.webp",
          "attachments": []
        }
    """.trimIndent()
}