package io.github.dockyardmc.utils

import org.jglrxavpok.hephaistos.parser.SNBTParser
import java.io.InputStream
import java.io.StringReader

object Resources {

    val registry = SNBTParser(StringReader(getFile("registry.snbt"))).parse()

    fun getFile(path: String): String = ClassLoader.getSystemResource(path).readText()

    @Deprecated("please use getFile()", ReplaceWith("getFile(path)", "io.github.dockyardmc.utils.Resources.getFile"))
    fun getText(path: String): String = getFile(path)

    private fun readVersion(): String {
        val inputStream: InputStream? = object {}.javaClass.getResourceAsStream("/dock.yard")
        return inputStream!!.bufferedReader().use { it.readText() }.trim()
    }

    fun getDockyardVersion(): DockyardVersionInfo {
        val data = readVersion().split("|")
        return DockyardVersionInfo(
            dockyardVersion = data[0],
            minecraftVersion = data[1],
            protocolVersion = VersionToProtocolVersion.map[data[1]] ?: 0,
            gitBranch = data[2],
            gitCommit = data[3]
        )
    }

    data class DockyardVersionInfo(
        val dockyardVersion: String,
        val minecraftVersion: String,
        val protocolVersion: Int,
        val gitBranch: String,
        val gitCommit: String
    )
}