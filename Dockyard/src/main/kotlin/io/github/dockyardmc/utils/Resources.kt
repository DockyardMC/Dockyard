package io.github.dockyardmc.utils

import java.io.InputStream

object Resources {

    fun getFile(path: String): String = ClassLoader.getSystemResource(path).readText()

    @Deprecated("please use getFile()", ReplaceWith("getFile(path)", "io.github.dockyardmc.utils.Resources.getFile"))
    fun getText(path: String): String = getFile(path)

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
    )
}