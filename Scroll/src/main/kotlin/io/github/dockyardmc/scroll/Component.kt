package io.github.dockyardmc.scroll

import io.github.dockyardmc.scroll.serializers.ComponentToJsonSerializer
import io.github.dockyardmc.scroll.serializers.ComponentToNbtSerializer
import kotlinx.serialization.Serializable
import org.jglrxavpok.hephaistos.nbt.NBTCompound

@Serializable
open class Component(
    open var extra: MutableList<Component>? = null,
    open var keybind: String? = null,
    open var text: String? = null,
    open var translate: String? = null,
    open var color: String? = null,
    open var bold: Boolean? = null,
    open var italic: Boolean? = null,
    open var underlined: Boolean? = null,
    open var strikethrough: Boolean? = null,
    open var obfuscated: Boolean? = null,
    open var font: String? = null,
    open var insertion: String? = null,
    open var hoverEvent: HoverEvent? = null,
    open var clickEvent: ClickEvent? = null
) {
    companion object {
        fun compound(components: MutableList<Component>): Component {
            return Component(
                text = "",
                extra = components
            )
        }
    }

    override fun toString(): String {
        return this.stripStyling()
    }

    fun toNBT(): NBTCompound {
        return ComponentToNbtSerializer.serializeComponent(this)
    }

    fun toJson(): String {
        return ComponentToJsonSerializer.serialize(this)
    }

    fun stripStyling(): String {
        return buildString {
            getAllComponents().forEach {
                append(it.text)
            }
        }
    }

    fun getAllComponents(): MutableList<Component> {
        val recursiveComponentList = mutableListOf<Component>()
        getComponentRecursive(this, recursiveComponentList)
        return recursiveComponentList
    }

    private fun getComponentRecursive(component: Component, componentList: MutableList<Component>) {
        component.extra?.forEach {
            componentList.add(it)
            getComponentRecursive(it, componentList)
        }
    }

    fun resetFormatting(includingFont: Boolean) {
        this.color = null
        this.strikethrough = null
        this.underlined = null
        this.font = null
        this.italic = null
        this.bold = null

        if(includingFont) {
            this.font = null
            this.hoverEvent = null
            this.obfuscated = null
            this.insertion = null
            this.keybind = null
            this.translate = null
            this.clickEvent = null
        }
    }
}