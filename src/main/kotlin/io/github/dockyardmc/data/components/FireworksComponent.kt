package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.netty.buffer.ByteBuf

data class FireworksComponent(val flightDuration: Float, val explosions: List<FireworkExplosionComponent>) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(flightDuration)
        buffer.writeList(explosions, FireworkExplosionComponent::write)
    }

    override fun hashStruct(): HashHolder {
        return unsupported(this)
    }

    companion object : NetworkReadable<FireworksComponent> {
        override fun read(buffer: ByteBuf): FireworksComponent {
            return FireworksComponent(buffer.readFloat(), buffer.readList(FireworkExplosionComponent::read))
        }
    }
}