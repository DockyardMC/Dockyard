package io.github.dockyardmc.socket

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.protocol.Packet
import io.netty.buffer.ByteBuf
import java.lang.Exception
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMemberFunctions

object PacketParser {

    fun parse(id: Int, buffer: ByteBuf, networkManager: NetworkManager): Packet? {
        try {
            val packetClass = networkManager.serverPacketRegistry.getFromIdOrNull(id, networkManager.protocolState) ?: return null
            val companionObject = packetClass.companionObject ?: return null
            val readFunction = companionObject.declaredMemberFunctions.find { it.name == "read" } ?: return null

            return readFunction.call(companionObject.objectInstance, buffer) as Packet
        } catch (ex: Exception) {
            log("Failed to read packet: $ex", LogType.ERROR)
            log(ex)
            if(ex.cause != null) log(ex.cause!! as Exception)
            return null
        }
    }
}