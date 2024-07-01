package io.github.dockyardmc.utils

import io.github.dockyardmc.utils.Resources.getFile

object VersionToProtocolVersion  {

    val map = mutableMapOf<String, Int>()

    init {
        val res = getFile("versions.yard")
        val split = res.split("|")
        split.forEach {
            val inSplit = it.split(":")
            map[inSplit[0]] = inSplit[1].toInt()
        }
    }
}