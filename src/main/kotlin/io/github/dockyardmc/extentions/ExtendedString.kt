package io.github.dockyardmc.extentions

import com.google.common.hash.Hashing
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.ComponentSerializer
import io.netty.util.CharsetUtil
import java.security.MessageDigest
import java.util.*


fun String.byteSize(): Int {
    return this.toByteArray(CharsetUtil.UTF_8).size
}

fun String.properStrictCase(): String {
    return this.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun String.component(): Component {
    return ComponentSerializer().serialize(this)
}

fun String.SHA256Long(): Long {
    return Hashing.sha256().hashString(this, CharsetUtil.UTF_8).asLong()
}

fun String.SHA256String(): String {
    return Hashing.sha256().hashString(this, CharsetUtil.UTF_8).toString()
}