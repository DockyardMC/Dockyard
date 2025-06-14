package io.github.dockyardmc.data.components

import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.HashHolder
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.NetworkReadable
import io.netty.buffer.ByteBuf

class WeaponComponent(val itemDamagePerAttack: Int, val disableBlockingForSeconds: Float) : DataComponent() {

    override fun write(buffer: ByteBuf) {
        buffer.writeVarInt(itemDamagePerAttack)
        buffer.writeFloat(disableBlockingForSeconds)
    }

    override fun hashStruct(): HashHolder {
        return CRC32CHasher.of {
            default("item_damage_per_attack", 1, itemDamagePerAttack, CRC32CHasher::ofInt)
            default("disable_blocking_for_seconds", 0f, disableBlockingForSeconds, CRC32CHasher::ofFloat)
        }
    }

    companion object : NetworkReadable<WeaponComponent> {
        val DEFAULT: WeaponComponent = WeaponComponent(1, 0.0f)

        override fun read(buffer: ByteBuf): WeaponComponent {
            return WeaponComponent(buffer.readVarInt(), buffer.readFloat())
        }
    }
}