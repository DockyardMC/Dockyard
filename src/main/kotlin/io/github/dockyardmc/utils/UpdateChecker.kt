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
        const val URL = "https://mvn.devos.one/api/maven/latest/details/releases/io/github/dockyardmc/dockyard"


        fun compareVersions(version1: String, version2: String): CurrentVersionStatus {
            val v1 = version1.split('.').map { it.toInt() }
            val v2 = version2.split('.').map { it.toInt() }

            val maxLength = maxOf(v1.size, v2.size)

            val paddedV1 = v1 + List(maxLength - v1.size) { 0 }
            val paddedV2 = v2 + List(maxLength - v2.size) { 0 }

            for (i in paddedV1.indices) {
                when {
                    paddedV1[i] > paddedV2[i] -> return CurrentVersionStatus.DEV_VERSION
                    paddedV1[i] < paddedV2[i] -> return CurrentVersionStatus.OUTDATED
                }
            }
            return CurrentVersionStatus.UP_TO_DATE
        }
    }

    enum class CurrentVersionStatus {
        OUTDATED,
        UP_TO_DATE,
        DEV_VERSION
    }

    init {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder().uri(URI(URL)).build()

        client.sendAsync(request, BodyHandlers.ofString()).thenAccept { res ->
            val latestVersion = Json.decodeFromString<ReposliteResponse>(res.body()).name.replace("dockyard-", "").replace(".jar", "")
            val status = compareVersions(DockyardServer.versionInfo.dockyardVersion, latestVersion)
            log("$status, ${DockyardServer.versionInfo.dockyardVersion} - $latestVersion ")
            when(status) {
                CurrentVersionStatus.OUTDATED -> {
                    log("You are currently running an outdated DockyardMC version. Consider updating to the latest ($latestVersion)", LOG_TYPE)
                }
                CurrentVersionStatus.UP_TO_DATE -> {}
                CurrentVersionStatus.DEV_VERSION -> {
                    log("You are currently running a dev version of DockyardMC. Things maybe be broken", LOG_TYPE)
                }
            }
        }
    }

    @Serializable
    data class ReposliteResponse(
        val name: String,
        val type: String,
        val contentType: String? = null,
        val contentLength: Long? = null,
        val lastModifiedTime: Double? = null,
    )
}