package io.github.dockyardmc.protocol

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.packets.registry.ServerPacketRegistry
import io.netty.buffer.ByteBuf
import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMemberFunctions

object PacketParser {

    var idAndStatePairToPacketClass = mutableMapOf<Pair<Int, ProtocolState>, KClass<*>>()

    // Gets data from annotation in each ServerboundPackets class
    // This is way better and easier approach than listing all the packets here in when() or in a map

    // It does this process in MainKt, and reads actual annotations using reflection in AnnotationProcessor.getServerboundPacketClassInfo()
    fun parse(id: Int, buffer: ByteBuf, protocolState: ProtocolState): ServerboundPacket? {
        try {
            val packetClass = ServerPacketRegistry.getFromIdOrNull(id, protocolState) ?: return null
            val companionObject = packetClass.companionObject ?: return null
            val readFunction = companionObject.declaredMemberFunctions.find { it.name == "read" } ?: return null

            return readFunction.call(companionObject.objectInstance, buffer) as ServerboundPacket
        } catch (ex: Exception) {
            log("Failed to read packet: $ex", LogType.ERROR)
            log(ex)
            if(ex.cause != null) log(ex.cause!! as Exception)
            return null
        }
    }
}