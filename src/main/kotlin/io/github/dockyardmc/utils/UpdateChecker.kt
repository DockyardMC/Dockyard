package io.github.dockyardmc.utils

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

class UpdateChecker {

    companion object {
        val LOG_TYPE = CustomLogType("⚠\uFE0F Update Checker", AnsiPair.ORANGE)
    }
//
//    init {
//        val client = HttpClient.newHttpClient()
//        val request = HttpRequest.newBuilder().uri(URI("https://mvn.devos.one/api/maven/details/releases/io/github/dockyardmc/dockyard")).build()
//        client.sendAsync(request, BodyHandlers.ofString()).thenAccept { res ->
//            val response = Json.decodeFromString<ReposliteResponse>(res.body())
//            val latestVersion = response.files.last { file -> file.type == "DIRECTORY" }.name
//            val publishedVersionContainCurrentVersion = response.files.firstOrNull { file -> file.name == DockyardServer.versionInfo.dockyardVersion } != null
//
//            if(latestVersion != DockyardServer.versionInfo.dockyardVersion) {
//                if(!publishedVersionContainCurrentVersion) {
//                    log("You are currently running an outdated DockyardMC version. Consider updating to the latest ($latestVersion)", LOG_TYPE)
//                } else {
//                    log("You are currently running a developer version of DockyardMC. Things might be VERY broken", LOG_TYPE)
//                }
//            }
//        }
//    }

    @Serializable
    data class ReposliteResponse(
        val name: String,
        val files: List<File>,
        val type: String,
    )

    @Serializable
    data class File(
        val name: String,
        val type: String,
        val contentType: String? = null,
        val contentLength: Long? = null,
        val lastModifiedTime: Double? = null,
    )
}

