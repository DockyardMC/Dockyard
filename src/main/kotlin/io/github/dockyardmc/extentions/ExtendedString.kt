package io.github.dockyardmc.extentions

import com.google.common.hash.Hashing
import io.netty.util.CharsetUtil
import java.util.*


fun String.isUppercase(): Boolean = this.uppercase() == this

fun String.isLowercase(): Boolean = this.lowercase() == this

fun String.byteSize(): Int = this.toByteArray(CharsetUtil.UTF_8).size

fun String.properStrictCase(): String =
    this.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

@Suppress("FunctionName")
fun String.SHA256Long(): Long = Hashing.sha256().hashString(this, CharsetUtil.UTF_8).asLong()

@Suppress("FunctionName")
fun String.SHA256String(): String = Hashing.sha256().hashString(this, CharsetUtil.UTF_8).toString()