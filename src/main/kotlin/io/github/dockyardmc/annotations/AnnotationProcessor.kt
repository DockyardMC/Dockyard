package io.github.dockyardmc.annotations

import LogType
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import log
import org.reflections.Reflections
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject

object AnnotationProcessor {

    fun getServerboundPacketClassInfo(): MutableMap<Pair<Int, ProtocolState>, KClass<*>> {
        val annotationValues = mutableMapOf<Pair<Int, ProtocolState>, KClass<*>>()
        val reflections = Reflections("io.github.dockyardmc")
        val annotatedClasses = reflections.getTypesAnnotatedWith(ServerboundPacketInfo::class.java)
        
        annotatedClasses.forEach { loopClass ->
            log(loopClass.simpleName, LogType.RUNTIME)
            val annotation = loopClass.getAnnotation(ServerboundPacketInfo::class.java)
            val id = annotation.id
            val state = annotation.state
            log("   - $id $state")
            annotationValues[Pair(id, state)] = loopClass.kotlin
        }
        return annotationValues
    }
}