package io.github.dockyardmc.utils

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.player.SkinManager.usernameToUuidCache
import io.github.dockyardmc.player.SkinManager.uuidToSkinCache
import io.github.dockyardmc.protocol.types.GameProfile
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object MojangUtil {

    const val BASE_AUTH_URL: String = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s"
    const val PREVENT_PROXY_CONNECTIONS_AUTH_URL: String = "$BASE_AUTH_URL&ip=%s"

    private val httpClient: HttpClient = HttpClient.newHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun authenticateSession(loginUsername: String, serverId: String): GameProfileResponse {
        val username = URLEncoder.encode(loginUsername, StandardCharsets.UTF_8)

        val url = String.format(BASE_AUTH_URL, username, serverId)
        val request = HttpRequest.newBuilder()
            .uri(URI(url))
            .GET()
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.body().isEmpty()) throw IOException("Mojang API down?")
        return json.decodeFromString<GameProfileResponse>(response.body())
    }

    fun getUUIDFromUsername(username: String): CompletableFuture<UUID?> {
        val future = CompletableFuture<UUID?>()

        if (usernameToUuidCache.containsKey(username)) {
            future.complete(usernameToUuidCache[username]!!)
        } else {
            val request = HttpRequest.newBuilder()
                .uri(URI("https://api.mojang.com/users/profiles/minecraft/$username"))
                .GET()
                .build()

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept { response ->
                if (response.body().isEmpty()) {
                    future.complete(null)
                    return@thenAccept
                }
                val decodedResponse = json.decodeFromString<ProfileResponse>(response.body())

                val uuid = UUID.fromString(getFullUUIDFromTrimmedUUID(decodedResponse.id))
                future.complete(uuid)
            }
        }
        return future
    }

    fun getSkinFromUsername(username: String): CompletableFuture<GameProfile.Property?> {
        val future = CompletableFuture<GameProfile.Property?>()

        getUUIDFromUsername(username).thenAccept { uuid ->
            if (uuid == null) {
                future.complete(null)
                return@thenAccept
            }

            getSkinFromUUID(uuid).thenAccept { property ->
                future.complete(property)
            }
        }

        return future
    }

    fun getSkinFromUUID(uuid: UUID, forceUpdate: Boolean = false): CompletableFuture<GameProfile.Property?> {
        val future = CompletableFuture<GameProfile.Property?>()

        if (uuidToSkinCache.containsKey(uuid) && !forceUpdate) future.complete(uuidToSkinCache[uuid]!!)

        val request = HttpRequest.newBuilder()
            .uri(URI("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false"))
            .GET()
            .timeout(5.seconds.toJavaDuration())
            .build()

        log("Fetched skin of $uuid from Mojang API..", LogType.NETWORK)
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept { response ->
            if (response.body().isEmpty()) {
                future.complete(null)
                return@thenAccept
            }
            val decodedResponse = json.decodeFromString<GameProfileResponse>(response.body())
            val property = GameProfile.Property("textures", decodedResponse.properties[0].value, decodedResponse.properties[0].signature)
            uuidToSkinCache[uuid] = property
            future.complete(property)

        }.exceptionally { throwable -> future.complete(null); null }

        return future
    }

    @Serializable
    private data class ProfileResponse(
        val id: String,
        val name: String
    )

    @Serializable
    data class GameProfileResponse(
        val id: String,
        val name: String,
        val properties: List<GameProfile.Property>,
    )

    // I'm going to quote Swordington here:
    // "Is this code bad? Yes, Do I care? Not really"
    private fun getFullUUIDFromTrimmedUUID(uuid: String): String =
        "${uuid.subSequence(0, 8)}-${uuid.subSequence(8, 12)}-${uuid.subSequence(12, 16)}-${uuid.subSequence(16, 20)}-${uuid.substring(20)}"
}