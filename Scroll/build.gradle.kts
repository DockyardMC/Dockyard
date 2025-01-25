import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

group = "io.github.dockyardmc.scroll"
version = parent!!.version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("net.kyori:adventure-api:4.18.0")
    testImplementation("net.kyori:adventure-text-minimessage:4.18.0")
    compileOnly("net.kyori:adventure-api:4.18.0")
    implementation(libs.bundles.hephaistos)
    implementation(libs.bundles.kotlinx)
    compileOnly("net.kyori:adventure-api:4.18.0")
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    useJUnitPlatform()
    val javaToolchains = project.extensions.getByType<JavaToolchainService>()
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
    })
}

tasks.withType<PublishToMavenRepository> {
    dependsOn("test")
}

sourceSets["main"].resources.srcDir("${buildDir}/generated/resources/")

sourceSets["main"].java.srcDir("src/main/kotlin")

publishing {
    repositories {
        maven {
            url = uri("https://mvn.devos.one/releases")
            credentials {
                username = System.getenv()["MAVEN_USER"]
                password = System.getenv()["MAVEN_PASS"]
            }
        }
    }

    publications {
        register<MavenPublication>("maven") {
            groupId = "io.github.dockyardmc"
            artifactId = "scroll"
            version = version
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
    val target = if(version.toString().endsWith("-SNAPSHOT")) "https://mvn.devos.one/snapshots" else "https://mvn.devos.one/releases"
    val color = if(version.toString().endsWith("-SNAPSHOT")) 16742912 else 65290
    val title = if(version.toString().endsWith("-SNAPSHOT")) "Snapshot Published to Maven" else "Published to Maven"
    return """
        {
          "content": null,
          "embeds": [
            {
              "title": "$title",
              "description": "`io.github.dockyardmc:scroll:$version` was successfully published to maven **$target**!",
              "color": $color
            }
          ],
          "username": "Mavenboo",
          "avatar_url": "https://ae01.alicdn.com/kf/Sa4eadafccb024c72a386eff7dfac2c61n.jpg",
          "attachments": []
        }
    """.trimIndent()
}
