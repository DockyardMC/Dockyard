package io.github.dockyardmc.extentions

import io.netty.util.CharsetUtil

fun String.byteSize(): Int {
    return this.toByteArray(CharsetUtil.UTF_8).size
}