package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundCustomClickActionPacket
import org.jglrxavpok.hephaistos.nbt.NBT

/**
 * @see ServerboundCustomClickActionPacket
 */
class ServerboundConfigurationCustomClickActionPacket(id: String, payload: NBT?) : ServerboundCustomClickActionPacket(id, payload)