package io.github.dockyardmc.utils

import io.github.dockyardmc.DockyardServer.Companion.versionInfo
import io.github.dockyardmc.registry.registries.MinecraftVersion
import java.io.InputStream
import java.net.URL

object Resources {

    fun getFile(path: String): URL = ClassLoader.getSystemResource(path) ?: throw IllegalStateException("File with that path does not exist in resources!")

    @Deprecated("please use getFile()", ReplaceWith("getFile(path)", "io.github.dockyardmc.utils.Resources.getFile"))
    fun getText(path: String): String = getFile(path).readText()

    fun getStream(path: String): InputStream = object {}.javaClass.getResourceAsStream(path)!!

    private fun readVersion(): String {
        val inputStream: InputStream? = object {}.javaClass.getResourceAsStream("/dock.yard")
        return inputStream!!.bufferedReader().use { it.readText() }.trim()
    }

    fun getDockyardVersion(): DockyardVersionInfo {
        val data = readVersion().split("|")
        return DockyardVersionInfo(
            dockyardVersion = data[0],
            gitBranch = data[2],
            gitCommit = data[3]
        )
    }

    data class DockyardVersionInfo(
        val dockyardVersion: String,
        val gitBranch: String,
        val gitCommit: String
    ) {
        fun getFormatted(minecraftVersion: MinecraftVersion): String {
            return "${versionInfo.dockyardVersion} (${versionInfo.gitCommit}@${versionInfo.gitBranch} for MC ${minecraftVersion.versionName})"
        }

        fun getFormatted(): String {
            return "${versionInfo.dockyardVersion} (${versionInfo.gitCommit}@${versionInfo.gitBranch})"
        }
    }
}