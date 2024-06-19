package io.github.dockyardmc.annotations

import io.github.dockyardmc.protocol.packets.ProtocolState
import org.reflections.Reflections
import kotlin.reflect.KClass

object AnnotationProcessor {

    val clientboundPacketMap = mutableMapOf<String, Pair<Int, ProtocolState>>()

    fun getServerboundPacketClassInfo(): MutableMap<Pair<Int, ProtocolState>, KClass<*>> {
        val annotationValues = mutableMapOf<Pair<Int, ProtocolState>, KClass<*>>()
        val reflections = Reflections("io.github.dockyardmc")
        val annotatedClasses = reflections.getTypesAnnotatedWith(ServerboundPacketInfo::class.java)
        
        annotatedClasses.forEach { loopClass ->
            val annotation = loopClass.getAnnotation(ServerboundPacketInfo::class.java)
            val id = annotation.id
            val state = annotation.state
            annotationValues[Pair(id, state)] = loopClass.kotlin
        }
        return annotationValues
    }

    fun addIdsToClientboundPackets() {
        val reflections = Reflections("io.github.dockyardmc")
        val annotatedClasses = reflections.getTypesAnnotatedWith(ClientboundPacketInfo::class.java)

        annotatedClasses.forEach { loopClass ->
            val annotation = loopClass.getAnnotation(ClientboundPacketInfo::class.java)
            val id = annotation.id
            val state = annotation.state

            clientboundPacketMap[loopClass.kotlin.simpleName!!] = Pair(id, state)
        }
    }
}