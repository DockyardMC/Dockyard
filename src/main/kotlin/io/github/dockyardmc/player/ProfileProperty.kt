package io.github.dockyardmc.player

import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf

data class ProfileProperty(val name: String, val value: String, val isSigned: Boolean, val signature: String?)
data class ProfilePropertyMap(val name: String, val properties: MutableList<ProfileProperty>)

fun ByteBuf.writeProfileProperties(propertyMap: ProfilePropertyMap, disableUtf: Boolean = false) {

    if(!disableUtf) this.writeString(propertyMap.name)
    this.writeVarInt(propertyMap.properties.size)

    propertyMap.properties.forEach {
        this.writeString(it.name)
        this.writeString(it.value)
        this.writeBoolean(it.isSigned)
        if(it.isSigned && it.signature != null) {
            this.writeString(it.signature)
        }
    }
}

fun ByteBuf.writeProfileProperties(propertyMap: ProfilePropertyMap, disableUtf: Boolean = false) {

    if(!disableUtf) this.writeString(propertyMap.name)
    this.writeVarInt(propertyMap.properties.size)

    propertyMap.properties.forEach {
        this.writeString(it.name)
        this.writeString(it.value)
        this.writeBoolean(it.isSigned)
        if(it.isSigned && it.signature != null) {
            this.writeString(it.signature)
        }
    }
}

fun ByteBuf.readProfilePropertyMap(): ProfilePropertyMap {

    val username = this.readString()

    val propertyCount = this.readVarInt()
    val properties = mutableMapOf<String, ProfileProperty>()

    for (i in 0 until propertyCount) {
        val name = this.readString()
        val value = this.readString()
        val isSigned = this.readBoolean()
        val signature: String? = if (isSigned) {
            this.readString()
        } else {
            null
        }

        properties[name] = ProfileProperty(name, value, isSigned, signature)
    }

    return ProfilePropertyMap(username, properties.values.toMutableList())
}