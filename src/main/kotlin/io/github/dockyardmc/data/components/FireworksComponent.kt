package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.types.readList
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

data class FireworksComponent(val flightDuration: Float, val explosions: List<FireworkExplosionComponent>) : DataComponent() {
    override fun getHashCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeFloat(flightDuration)
        buffer.writeList(explosions, FireworkExplosionComponent::write)
    }

    companion object : NetworkReadable<FireworksComponent> {
        override fun read(buffer: ByteBuf): FireworksComponent {
            return FireworksComponent(buffer.readFloat(), buffer.readList(FireworkExplosionComponent::read))
        }
    }
}