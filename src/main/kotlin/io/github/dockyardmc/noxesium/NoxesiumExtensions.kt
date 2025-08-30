
package io.github.dockyardmc.noxesium

import io.github.dockyardmc.noxesium.protocol.NoxesiumPacket
import io.github.dockyardmc.noxesium.rules.NoxesiumServerRule
import io.github.dockyardmc.protocol.packets.configurations.ClientboundPlayPluginMessagePacket
import io.netty.buffer.ByteBuf

fun Collection<NoxesiumPacket>.toPluginMessagePackets(): Collection<ClientboundPlayPluginMessagePacket> {
    val list = mutableListOf<ClientboundPlayPluginMessagePacket>()
    this.forEach { noxesiumPacket ->
        list.add(noxesiumPacket.getPluginMessagePacket())
    }
    return list
}

@Suppress("UNCHECKED_CAST")
fun Map<Int, NoxesiumServerRule<*>>.getWriters(): Map<Int, (ByteBuf) -> Unit> {
    return this.mapValues { (_, rule) -> { buffer: ByteBuf -> (rule as NoxesiumServerRule<Any?>).write(rule.value, buffer) } }
}