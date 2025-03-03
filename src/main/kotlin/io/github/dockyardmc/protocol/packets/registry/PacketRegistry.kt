package io.github.dockyardmc.protocol.packets.registry

import io.github.dockyardmc.extentions.reversed
import io.github.dockyardmc.protocol.packets.ProtocolState
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

abstract class PacketRegistry {

    fun getFromIdOrNull(id: Int, state: ProtocolState): KClass<*>? {

        return when(state) {
            ProtocolState.HANDSHAKE -> getReversedHandshake()[id]
            ProtocolState.STATUS -> getReversedStatus()[id]
            ProtocolState.LOGIN -> getReversedLogin()[id]
            ProtocolState.CONFIGURATION -> getReversedConfiguration()[id]
            ProtocolState.PLAY -> getReversedPlay()[id]
        }
    }

    fun getAll(): MutableList<KClass<*>> {
        val list = mutableListOf<KClass<*>>()

        list.addAll(handshakePackets.keys)
        list.addAll(statusPackets.keys)
        list.addAll(loginPackets.keys)
        list.addAll(configurationPackets.keys)
        list.addAll(playPackets.keys)

        return list
    }

    fun getFromId(id: Int, state: ProtocolState): KClass<*> {
        return getFromIdOrNull(id, state) ?: throw IllegalArgumentException("Packet with id $id and state ${state.name} does not have assigned packet class!")
    }

    fun getSkippedFromIdOrNull(id: Int, state: ProtocolState): String? {
        return when(state) {
            ProtocolState.HANDSHAKE -> skippedHandshakePackets[id]
            ProtocolState.STATUS -> skippedStatusPackets[id]
            ProtocolState.LOGIN -> skippedLoginPackets[id]
            ProtocolState.CONFIGURATION -> skippedConfigurationPackets[id]
            ProtocolState.PLAY -> skippedPlayPackets[id]
        }
    }

    fun getIdAndStateOrNull(packet: KClass<*>): Pair<Int, ProtocolState>? {
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

        return null
    }

    fun getIdAndState(packet: KClass<*>): Pair<Int, ProtocolState> {
        return getIdAndStateOrNull(packet) ?: throw IllegalArgumentException("Packet class ${packet.simpleName} does not have assigned protocol id!")
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

    internal val skippedHandshakePackets: MutableMap<Int, String> = mutableMapOf()
    internal val skippedStatusPackets: MutableMap<Int, String> = mutableMapOf()
    internal val skippedLoginPackets: MutableMap<Int, String> = mutableMapOf()
    internal val skippedConfigurationPackets: MutableMap<Int, String> = mutableMapOf()
    internal val skippedPlayPackets: MutableMap<Int, String> = mutableMapOf()

    fun getReversedHandshake(): Map<Int, KClass<*>> {
        if(reversedHandshakePackets.isEmpty()) reversedHandshakePackets = handshakePackets.reversed()
        return reversedHandshakePackets
    }

    fun getReversedStatus(): Map<Int, KClass<*>> {
        if(reversedStatusPackets.isEmpty()) reversedStatusPackets = statusPackets.reversed()
        return reversedStatusPackets
    }

    fun getReversedLogin(): Map<Int, KClass<*>> {
        if(reversedLoginPackets.isEmpty()) reversedLoginPackets = loginPackets.reversed()
        return reversedLoginPackets
    }

    fun getReversedConfiguration(): Map<Int, KClass<*>> {
        if(reversedConfigurationPackets.isEmpty()) reversedConfigurationPackets = configurationPackets.reversed()
        return reversedConfigurationPackets
    }

    fun getReversedPlay(): Map<Int, KClass<*>> {
        if(reversedPlayPackets.isEmpty()) reversedPlayPackets = playPackets.reversed()
        return reversedPlayPackets
    }

    internal var reversedHandshakePackets: Map<Int, KClass<*>> = mutableMapOf()
    internal var reversedStatusPackets: Map<Int, KClass<*>> = mutableMapOf()
    internal var reversedLoginPackets: Map<Int, KClass<*>> = mutableMapOf()
    internal var reversedConfigurationPackets: Map<Int, KClass<*>> = mutableMapOf()
    internal var reversedPlayPackets: Map<Int, KClass<*>> = mutableMapOf()

    internal fun skipHandshake(string: String) { skippedHandshakePackets[handshakeCounter.getAndIncrement()] = string }
    internal fun skipStatus(string: String) { skippedStatusPackets[statusCounter.getAndIncrement()] = string }
    internal fun skipLogin(string: String) { skippedLoginPackets[loginCounter.getAndIncrement()] = string }
    internal fun skipConfiguration(string: String) { skippedConfigurationPackets[configurationCounter.getAndIncrement()] = string }
    internal fun skipPlay(string: String) { skippedPlayPackets[playCounter.getAndIncrement()] = string }

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