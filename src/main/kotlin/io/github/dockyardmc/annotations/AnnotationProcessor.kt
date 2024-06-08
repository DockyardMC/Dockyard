package io.github.dockyardmc.annotations

import io.github.dockyardmc.protocol.packets.ProtocolState
import org.reflections.Reflections
import kotlin.reflect.KClass

object AnnotationProcessor {

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
}