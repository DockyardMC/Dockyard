package io.github.dockyardmc.utils

import java.io.File

object Resources {

    fun getText(path: String): String {
        return File(ClassLoader.getSystemResource(path).file).readText()
    }

}