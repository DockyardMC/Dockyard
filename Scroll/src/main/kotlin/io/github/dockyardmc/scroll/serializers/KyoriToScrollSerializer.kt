package io.github.dockyardmc.scroll.serializers

import io.github.dockyardmc.scroll.*
import net.kyori.adventure.text.KeybindComponent
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.TextDecoration

object KyoriToScrollSerializer {

    fun serializeComponent(kyori: net.kyori.adventure.text.Component): Component {
        val component = Component()
        component.color = kyori.color()?.asHexString()
        component.bold = kyori.getTextDecoration(TextDecoration.BOLD)
        component.italic = kyori.getTextDecoration(TextDecoration.ITALIC)
        component.obfuscated = kyori.getTextDecoration(TextDecoration.OBFUSCATED)
        component.strikethrough = kyori.getTextDecoration(TextDecoration.STRIKETHROUGH)
        component.underlined = kyori.getTextDecoration(TextDecoration.UNDERLINED)
        component.font = kyori.font()?.value()
        component.text = if(kyori is TextComponent) kyori.content() else null
        component.translate = if(kyori is TranslatableComponent) kyori.key() else null
        component.keybind = if(kyori is KeybindComponent) kyori.keybind() else null
        component.insertion = kyori.insertion()
        component.clickEvent = kyori.clickEvent()?.toScroll()
        component.hoverEvent = kyori.hoverEvent()?.toScroll()
        kyori.children().forEach { child ->
            if(component.extra == null) component.extra = mutableListOf()
            component.extra!!.add(serializeComponent(child))
        }

        return component
    }

    private fun net.kyori.adventure.text.event.ClickEvent.toScroll(): ClickEvent {
        val action = ClickAction.entries[this.action().ordinal]
        val value = this.value()
        return ClickEvent(action, value)
    }

    private fun net.kyori.adventure.text.event.HoverEvent<*>.toScroll(): HoverEvent? {
        val value = this.value()
        return when(this.value()) {
            is net.kyori.adventure.text.Component -> HoverEvent(HoverAction.SHOW_TEXT, serializeComponent(value as net.kyori.adventure.text.Component))
            else -> null
        }
    }


    private fun net.kyori.adventure.text.Component.getTextDecoration(decoration: TextDecoration): Boolean? {
        return when(this.decoration(decoration)) {
            TextDecoration.State.NOT_SET -> null
            TextDecoration.State.FALSE -> false
            TextDecoration.State.TRUE -> true
            null -> null
        }
    }

    fun net.kyori.adventure.text.Component.toScroll(): Component {
        return KyoriToScrollSerializer.serializeComponent(this)
    }
}