package io.github.dockyardmc.player

import LogType
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.runnables.AsyncRunnable
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import log
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

object SkinManager {

    val skinCache = mutableMapOf<UUID, ProfileProperty>()
    private val httpClient: HttpClient = HttpClient.newHttpClient()

    fun getSkinOf(uuid: UUID, forceUpdate: Boolean = false): ProfileProperty {

        if(skinCache.containsKey(uuid) && !forceUpdate) return skinCache[uuid]!!

        val request = HttpRequest.newBuilder()
            .uri(URI("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false"))
            .GET()
            .build()

        log("Requesting skin of $uuid from mojang servers..", LogType.DEBUG)
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        val json = Json { ignoreUnknownKeys = true }
        val decodedResponse = json.decodeFromString<Root>(response.body())

        log(decodedResponse.toString())
        val property = ProfileProperty("textures", decodedResponse.properties[0].value, true, decodedResponse.properties[0].signature)
        skinCache[uuid] = property
        return property
    }

    fun updateSkinOf(player: Player) {
        val asyncRunnable = AsyncRunnable {
            val skin = getSkinOf(player.uuid)
            player.profile!!.properties[0] = skin
        }
        asyncRunnable.runAfterFinished = {
            DockyardServer.broadcastMessage("<lime>Updated skin of $player")
//            player.updateSkin()
            player.updateDisplayedSkinParts()
        }

        asyncRunnable.start()
    }
}

@Serializable
private data class Root(
    val id: String,
    val name: String,
    val properties: List<Property>,
)

@Serializable
private data class Property(
    val name: String,
    val value: String,
    val signature: String
)
