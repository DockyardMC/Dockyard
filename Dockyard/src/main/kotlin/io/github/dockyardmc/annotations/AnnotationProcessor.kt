package io.github.dockyardmc.annotations

import io.github.dockyardmc.protocol.packets.ProtocolState
import org.reflections.Reflections
import kotlin.reflect.KClass

object AnnotationProcessor {

    val clientboundPacketMap = mutableMapOf<String, Pair<Int, ProtocolState>>()

    fun getServerboundPacketClassInfo(): MutableMap<Pair<Int, ProtocolState>, KClass<*>> {
        val annotationValues = mutableMapOf<Pair<Int, ProtocolState>, KClass<*>>()
        val reflections = Reflections("io.github.dockyardmc.protocol.packets")
        val annotatedClasses = reflections.getTypesAnnotatedWith(ServerboundPacketInfo::class.java)
        
        annotatedClasses.forEach { loopClass ->
            val annotation = loopClass.getAnnotation(ServerboundPacketInfo::class.java)
            val id = annotation.id
            val state = annotation.state
            annotationValues[id to state] = loopClass.kotlin
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

            clientboundPacketMap[loopClass.kotlin.simpleName!!] = id to state
        }
    }
}