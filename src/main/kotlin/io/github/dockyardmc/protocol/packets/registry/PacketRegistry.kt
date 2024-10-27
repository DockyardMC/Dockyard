package io.github.dockyardmc.protocol.packets.registry

import io.github.dockyardmc.protocol.packets.ProtocolState
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

abstract class PacketRegistry {

    fun getIdAndStateOrThrow(packet: KClass<*>): Pair<Int, ProtocolState> {
        val handshakePacket = handshakePackets[packet]
        val statusPacket = statusPackets[packet]
        val loginPacket = loginPackets[packet]
        val configurationPacket = configurationPackets[packet]
        val playPacket = playPackets[packet]
        if(handshakePacket != null) return handshakePacket to ProtocolState.HANDSHAKE
        if(statusPacket != null) return statusPacket to ProtocolState.STATUS
        if(loginPacket != null) return loginPacket to ProtocolState.LOGIN
        if(configurationPacket != null) return configurationPacket to ProtocolState.CONFIGURATION
        if(playPacket != null) return playPacket to ProtocolState.PLAY

        throw IllegalArgumentException("Packet class ${packet.simpleName} does not have assigned protocol id!")
    }

    abstract fun load()

    internal val handshakeCounter = AtomicInteger()
    internal val statusCounter = AtomicInteger()
    internal val loginCounter = AtomicInteger()
    internal val configurationCounter = AtomicInteger()
    internal val playCounter = AtomicInteger()

    internal val handshakePackets: MutableMap<KClass<*>, Int> = mutableMapOf()
    internal val statusPackets: MutableMap<KClass<*>, Int> = mutableMapOf()
    internal val loginPackets: MutableMap<KClass<*>, Int> = mutableMapOf()
    internal val configurationPackets: MutableMap<KClass<*>, Int> = mutableMapOf()
    internal val playPackets: MutableMap<KClass<*>, Int> = mutableMapOf()

    internal fun skipHandshake(string: String) { handshakeCounter.getAndIncrement() }
    internal fun skipStatus(string: String) { statusCounter.getAndIncrement() }
    internal fun skipLogin(string: String) { loginCounter.getAndIncrement() }
    internal fun skipConfiguration(string: String) { configurationCounter.getAndIncrement() }
    internal fun skipPlay(string: String) { playCounter.getAndIncrement() }

    internal fun addHandshake(packet: KClass<*>) {
        handshakePackets[packet] = handshakeCounter.getAndIncrement()
    }

    internal fun addStatus(packet: KClass<*>) {
        statusPackets[packet] = statusCounter.getAndIncrement()
    }

    internal fun addLogin(packet: KClass<*>) {
        loginPackets[packet] = loginCounter.getAndIncrement()
    }

    internal fun addConfiguration(packet: KClass<*>) {
        configurationPackets[packet] = configurationCounter.getAndIncrement()
    }

    internal fun addPlay(packet: KClass<*>) {
        playPackets[packet] = playCounter.getAndIncrement()
    }
}