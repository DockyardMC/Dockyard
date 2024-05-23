package io.github.dockyardmc.utils

import org.jglrxavpok.hephaistos.parser.SNBTParser
import java.io.File
import java.io.FileReader
import java.io.InputStream

object Resources {

    val registry = SNBTParser(FileReader(getFile("registry.snbt"))).parse()

    fun getFile(path: String): File {
        return File(ClassLoader.getSystemResource(path).file)
    }

    fun getText(path: String): String {
        return getFile(path).readText()
    }

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