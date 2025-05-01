package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.tide.Codec
import io.netty.buffer.ByteBuf

class WeaponComponent(val itemDamagePerAttack: Int, val disableBlockingForSeconds: Float): DataComponent() {
    override fun getCodec(): Codec<out DataComponent> {
        TODO("Not yet implemented")
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(itemDamagePerAttack)
        buffer.writeFloat(disableBlockingForSeconds)
    }

    companion object: NetworkReadable<WeaponComponent> {
        override fun read(buffer: ByteBuf): WeaponComponent {
            return WeaponComponent(buffer.readVarInt(), buffer.readFloat())
        }
    }
}