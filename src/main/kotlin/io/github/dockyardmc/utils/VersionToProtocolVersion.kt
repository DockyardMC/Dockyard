package io.github.dockyardmc.utils

object VersionToProtocolVersion  {

    val map = mutableMapOf<String, Int>()

    init {
        val res = Resources.getText("versions.yard")
        val split = res.split("|")
        split.forEach {
            val inSplit = it.split(":")
            map[inSplit[0]] = inSplit[1].toInt()
        }
    }
}