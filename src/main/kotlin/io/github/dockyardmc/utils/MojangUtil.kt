package io.github.dockyardmc.utils

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.player.SkinManager.skinCache
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

object MojangUtil {

    const val BASE_AUTH_URL: String = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s"
    const val PREVENT_PROXY_CONNECTIONS_AUTH_URL: String = "$BASE_AUTH_URL&ip=%s"

    private val httpClient: HttpClient = HttpClient.newHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun authenticateSession(loginUsername: String, serverId: String): GameProfileResponse {
        val username = URLEncoder.encode(loginUsername, StandardCharsets.UTF_8)

        //TODO auth prevent proxy connection
        val url = String.format(BASE_AUTH_URL, username, serverId)
        val request = HttpRequest.newBuilder()
            .uri(URI(url))
            .GET()
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.body().isEmpty()) throw IOException("Mojang API down?")
        broadcastMessage("$response")
        return json.decodeFromString<GameProfileResponse>(response.body())
    }

    fun getUUIDFromUsername(username: String): UUID? {

        try {
            val request = HttpRequest.newBuilder()
                .uri(URI("https://api.mojang.com/users/profiles/minecraft/$username"))
                .GET()
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            val decodedResponse = json.decodeFromString<ProfileResponse>(response.body())

            val uuid = UUID.fromString(getFullUUIDFromTrimmedUUID(decodedResponse.id))
            log("Fetched uuid of $username from Mojang API", LogType.NETWORK)
            return uuid

        } catch (ex: Exception) {
            log(ex)
            return null
        }
    }

    fun getSkinFromUUID(uuid: UUID, forceUpdate: Boolean = false): GameProfile.Property {

        if (skinCache.containsKey(uuid) && !forceUpdate) return skinCache[uuid]!!

        val request = HttpRequest.newBuilder()
            .uri(URI("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false"))
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        val decodedResponse = json.decodeFromString<GameProfileResponse>(response.body())

        log("Fetched skin of $uuid from Mojang API", LogType.NETWORK)
        val property = GameProfile.Property("textures", decodedResponse.properties[0].value, decodedResponse.properties[0].signature)
        skinCache[uuid] = property
        return property
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

    @Serializable
    private data class SkinResponseProperty(
        val name: String,
        val value: String,
        val signature: String
    )

    // I'm going to quote Swordington here:
    // "Is this code bad? Yes, Do I care? Not really"
    private fun getFullUUIDFromTrimmedUUID(uuid: String): String =
        "${uuid.subSequence(0, 8)}-${uuid.subSequence(8, 12)}-${uuid.subSequence(12, 16)}-${uuid.subSequence(16, 20)}-${uuid.substring(20)}"
}