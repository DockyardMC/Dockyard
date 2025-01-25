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

group = "io.github.dockyardmc"
version = parent!!.version

val minecraftVersion = "1.21.3"
val dockyardVersion = properties["dockyard.version"]!!
val gitBranch = "git rev-parse --abbrev-ref HEAD".runCommand()
val gitCommit = "git rev-parse --short=8 HEAD".runCommand()

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlinx)


    // Minecraft
    api(libs.bundles.hephaistos)
    api(project(":Scroll"))

    // Networking
    api(libs.bundles.netty)

    // Logging
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.1")
    api(libs.prettylog)

    // Other
    api("org.reflections:reflections:0.9.12")
    implementation("com.google.guava:guava:33.3.1-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    api("it.unimi.dsi:fastutil:8.5.13")
    api(libs.bindables)

    // Spark
    api(libs.spark)
    implementation("com.google.protobuf:protobuf-javalite:4.28.2")
    implementation("me.lucko:bytesocks-java-client:1.0-20230828.145440-5") {
        exclude(module = "slf4j-api")
    }

    testImplementation(kotlin("test"))
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
    withJavadocJar()
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

tasks.processResources {
    dependsOn("generateVersionFile")
}

sourceSets["main"].resources.srcDir("${buildDir}/generated/resources/")

sourceSets["main"].java.srcDir("src/main/kotlin")


tasks.withType<PublishToMavenRepository> {
    dependsOn("test")
}

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
    val color = if(dockyardVersion.toString().endsWith("-SNAPSHOT")) 16742912 else 65290
    val title = if(dockyardVersion.toString().endsWith("-SNAPSHOT")) "Snapshot Published to Maven" else "Published to Maven"
    return """
        {
          "content": null,
          "embeds": [
            {
              "title": "$title",
              "description": "`io.github.dockyardmc:dockyard:$dockyardVersion` was successfully published to maven **$target**!",
              "color": $color
            }
          ],
          "username": "Mavenboo",
          "avatar_url": "https://storage.moemate.io/9edcfd27fd20abe29e93bf904f633d61b4fccadc/3f1c4383-1ba3-43f9-891e-f6a96abbe970.webp",
          "attachments": []
        }
    """.trimIndent()
}