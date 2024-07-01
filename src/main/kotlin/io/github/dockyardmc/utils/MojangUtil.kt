package io.github.dockyardmc.utils

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.player.ProfileProperty
import io.github.dockyardmc.player.SkinManager.skinCache
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

object MojangUtil {

    private val httpClient: HttpClient = HttpClient.newHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    fun getUUIDFromUsername(username: String): UUID? {

        try {
            log("Getting uuid of $username from Mojang API", LogType.NETWORK)
            val request = HttpRequest.newBuilder()
                .uri(URI("https://api.mojang.com/users/profiles/minecraft/$username"))
                .GET()
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            val decodedResponse = json.decodeFromString<ProfileResponse>(response.body())
            log("$decodedResponse", LogType.SUCCESS)

            val uuid = UUID.fromString(getFullUUIDFromTrimmedUUID(decodedResponse.id))
            log(uuid.toString())
            return uuid

        } catch (ex: Exception) {
            log(ex)
            return null
        }
    }

    fun getSkinFromUUID(uuid: UUID, forceUpdate: Boolean = false): ProfileProperty {

        if(skinCache.containsKey(uuid) && !forceUpdate) return skinCache[uuid]!!

        val request = HttpRequest.newBuilder()
            .uri(URI("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false"))
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        val decodedResponse = json.decodeFromString<SkinResponseRoot>(response.body())

        val property = ProfileProperty("textures", decodedResponse.properties[0].value, true, decodedResponse.properties[0].signature)
        skinCache[uuid] = property
        return property
    }

    @Serializable
    private data class ProfileResponse(
        val id: String,
        val name: String
    )

    @Serializable
    private data class SkinResponseRoot(
        val id: String,
        val name: String,
        val properties: List<SkinResponseProperty>,
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