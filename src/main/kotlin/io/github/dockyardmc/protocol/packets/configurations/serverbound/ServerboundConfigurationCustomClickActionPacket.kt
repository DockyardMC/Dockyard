package io.github.dockyardmc.protocol.packets.configurations.serverbound

import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundCustomClickActionPacket
import net.kyori.adventure.nbt.CompoundBinaryTag

/**
 * @see ServerboundCustomClickActionPacket
 */
class ServerboundConfigurationCustomClickActionPacket(id: String, payload: CompoundBinaryTag?) : ServerboundCustomClickActionPacket(id, payload)