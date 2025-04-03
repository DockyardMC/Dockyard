package io.github.dockyardmc.extentions

import cz.lukynka.prettylog.log
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMemberFunctions

fun <T> KClass<*>.asNetworkReadable(): NetworkReadable<T> {
    val companion = this.companionObject ?: throw IllegalStateException("Class $simpleName does not have companion object")

    if (companion !is NetworkReadable<*>) throw IllegalStateException("Companion object of class $simpleName is not of type NetworkReadable")

    @Suppress("UNCHECKED_CAST")
    return companion as NetworkReadable<T>
}

fun <T> KClass<*>.read(buffer: ByteBuf): T {
    val companion = this.companionObject ?: throw IllegalStateException("Class $simpleName does not have companion object")
    val readFunction = companionObject!!.declaredMemberFunctions.find { member -> member.name == "read" } ?: throw IllegalStateException("Class $simpleName does not have a read function")
    try {
        @Suppress("UNCHECKED_CAST")
        return readFunction.call(companion.objectInstance, buffer) as T
    } catch (exception: Exception) {
        log(exception)
        throw IllegalStateException("Calling the read method failed: $exception")
    }
}