package io.github.dockyardmc.utils

import java.io.File

object Resources {

    fun getFile(path: String): File {
        return File(ClassLoader.getSystemResource(path).file)
    }

    fun getText(path: String): String {
        return getFile(path).readText()
    }

}