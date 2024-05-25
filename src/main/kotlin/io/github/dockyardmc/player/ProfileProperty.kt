package io.github.dockyardmc.player

import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf
import java.util.*

class ProfileProperty(val name: String, val value: String, val isSigned: Boolean, val signature: String?)
class ProfilePropertyMap(val name: String, val properties: MutableList<ProfileProperty>)
class PlayerUpdateProfileProperty(val name: String, val properties: MutableList<ProfileProperty>)

fun ByteBuf.writeProfileProperties(propertyMap: ProfilePropertyMap) {

    this.writeUtf(propertyMap.name)
    this.writeVarInt(propertyMap.properties.size)

    // Properties
    propertyMap.properties.forEach {
        this.writeUtf(it.name)
        this.writeUtf(it.value)
        this.writeBoolean(it.isSigned)
        if(it.isSigned && it.signature != null) {
            this.writeUtf(it.signature)
        }
    }
}

fun ByteBuf.writeProfileProperties(propertyMap: PlayerUpdateProfileProperty) {

    this.writeUtf(propertyMap.name)
    this.writeVarInt(propertyMap.properties.size)

    // Properties
    propertyMap.properties.forEach {
        this.writeUtf(it.name)
        this.writeUtf(it.value)
        this.writeBoolean(it.isSigned)
        if(it.isSigned && it.signature != null) {
            this.writeUtf(it.signature)
        }
    }
}