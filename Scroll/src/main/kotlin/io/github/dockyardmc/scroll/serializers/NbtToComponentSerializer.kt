package io.github.dockyardmc.scroll.serializers

import io.github.dockyardmc.scroll.*
import org.jglrxavpok.hephaistos.nbt.NBT
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTString

object NbtToComponentSerializer {

    fun serializeNbt(nbt: NBTCompound): Component {
        val component = Component()

        component.color = nbt.getString("color")
        component.bold = nbt.getBoolean("bold")
        component.font = nbt.getString("font")
        component.italic = nbt.getBoolean("italic")
        component.insertion = nbt.getString("insertion")
        component.keybind = nbt.getString("keybind")
        component.obfuscated = nbt.getBoolean("obfuscated")
        component.strikethrough = nbt.getBoolean("strikethrough")
        component.text = nbt.getString("text")
        component.translate = nbt.getString("translate")
        component.underlined = nbt.getBoolean("underlined")

        val hover = nbt.getCompound("hoverEvent")
        if (hover != null) {
            val action = hover.getString("action")!!
            val content = hover.getCompound("contents")!!

            component.hoverEvent = HoverEvent(HoverAction.valueOf(action.uppercase()), serializeNbt(content))
        }

        val click = nbt.getCompound("clickEvent")
        if (click != null) {
            val action = click.getString("action")!!
            val value = click.getString("value")!!

            component.clickEvent = ClickEvent(ClickAction.valueOf(action.uppercase()), value)
        }

        val list = nbt.getList<NBT>("extra")

        val listOut = mutableListOf<Component>()
        list?.forEach {
            if (it is NBTCompound) {
                listOut.add(serializeNbt(it))
            }
            if(it is NBTString) {
                listOut.add(Component(text = it.value))
            }
        }
        if (listOut.isNotEmpty()) component.extra = listOut

        return component
    }
}