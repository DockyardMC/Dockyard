package io.github.dockyardmc.protocol

import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMemberFunctions

interface NetworkWritable <T> {

    fun write(buffer: ByteBuf)

    fun read(buffer: ByteBuf): T

}


inline fun <reified T> NetworkWritable<T>.read(buffer: ByteBuf): T {
    val clazz = T::class
    val companionObject = clazz.companionObject ?: throw IllegalStateException("Class ${T::class.simpleName} does not have companion object with read method!")
    val readFunction = companionObject.declaredMemberFunctions.find { it.name == "read" } ?: throw IllegalStateException("Class ${T::class.simpleName} does not have companion object with read method!")

    return readFunction.call(companionObject.objectInstance, buffer) as T
}
