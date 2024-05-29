package io.github.dockyardmc.extentions

import com.google.common.hash.Hashing
import io.netty.util.CharsetUtil
import java.util.*


fun String.isUppercase(): Boolean {
    return this.uppercase() == this
}

fun String.isLowercase(): Boolean {
    return this.lowercase() == this
}

fun String.byteSize(): Int {
    return this.toByteArray(CharsetUtil.UTF_8).size
}

fun String.properStrictCase(): String {
    return this.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun String.SHA256Long(): Long {
    return Hashing.sha256().hashString(this, CharsetUtil.UTF_8).asLong()
}

fun String.SHA256String(): String {
    return Hashing.sha256().hashString(this, CharsetUtil.UTF_8).toString()
}