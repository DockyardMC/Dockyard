import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

plugins {
    `maven-publish`
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("io.ktor.plugin") version "2.2.3"
    application
}

val minecraftVersion = "1.21.4"
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
    maven("https://mvn.devos.one/snapshots")
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.viaversion.com")
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("com.akuleshov7:ktoml-core:0.5.1")
    implementation("com.akuleshov7:ktoml-file:0.5.1")

    api("io.github.dockyardmc:bytesocks-client-java:1.0-SNAPSHOT") {
        exclude(module = "slf4j-api")
    }
    api("com.google.protobuf:protobuf-javalite:4.28.2")

    // Minecraft
    api("io.github.jglrxavpok.hephaistos:common:2.2.0")
    api("io.github.jglrxavpok.hephaistos:gson:2.2.0")
    api("io.github.dockyardmc:scroll:2.8")
    implementation("io.github.dockyardmc:wikivg-datagen:1.3")

    // Pathfinding
    api("com.github.Metaphoriker.pathetic:pathetic-engine:4.0")
    api("com.github.Metaphoriker.pathetic:pathetic-api:4.0")
    api("com.github.Metaphoriker.pathetic:pathetic-provider:4.0")

    // Networking
    api("io.ktor:ktor-server-netty:3.1.2")
    api("io.github.dockyardmc:tide:1.6")

    // Logging
    implementation("org.slf4j:slf4j-nop:2.0.9")
    api("cz.lukynka:pretty-log:1.5")

    // Other
    api("org.reflections:reflections:0.9.12")
    implementation("com.google.guava:guava:33.3.1-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    api("it.unimi.dsi:fastutil:8.5.13")
    api("cz.lukynka:kotlin-bindables:2.0")

    api("io.github.dockyardmc:spark-api:1.12-SNAPSHOT")
    api("io.github.dockyardmc:spark-common:1.12-SNAPSHOT")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
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

sourceSets["main"].java.srcDir("src/main/kotlin")

tasks {
    val sourcesJar by creating(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier = "sources"
        from(sourceSets["main"].allSource)
    }

    artifacts {
        add("archives", sourcesJar)
    }
}

tasks.withType<PublishToMavenRepository> {
    if(!version.toString().endsWith("-SNAPSHOT")) {
        dependsOn("test")
    }
}

java {
    withSourcesJar()
    withJavadocJar()
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