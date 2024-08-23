package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import io.github.dockyardmc.extentions.toRgbInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.extensions.toComponent

class TextDisplay(location: Location): DisplayEntityBase(location) {

    override var type: EntityType = EntityTypes.TEXT_DISPLAY
    val text: Bindable<String> = Bindable("")
    val lineWidth: Bindable<Int> = Bindable(200)
    val backgroundColor: Bindable<CustomColor> = Bindable(CustomColor(64, 0, 0))
    val opacity: Bindable<Int> = Bindable(255)
    val hasShadow: Bindable<Boolean> = Bindable(false)
    val isSeeThrough: Bindable<Boolean> = Bindable(false)
    val useDefaultBackgroundColor: Bindable<Boolean> = Bindable(true)
    val alignment: Bindable<TextDisplayAlignment> = Bindable(TextDisplayAlignment.CENTER)

    init {
        billboard.value = DisplayBillboard.CENTER
        text.valueChanged {
            val type = EntityMetadataType.TEXT_DISPLAY_TEXT
            metadata[type] = EntityMetadata(type, EntityMetaValue.TEXT_COMPONENT, it.newValue.toComponent())
        }
        lineWidth.valueChanged {
            val type = EntityMetadataType.TEXT_DISPLAY_LINE_WIDTH
            metadata[type] = EntityMetadata(type, EntityMetaValue.VAR_INT, it.newValue)
        }
        backgroundColor.valueChanged {
            val type = EntityMetadataType.TEXT_DISPLAY_BACKGROUND_COLOR
            metadata[type] = EntityMetadata(type, EntityMetaValue.VAR_INT, it.newValue.toRgbInt())
        }
        opacity.valueChanged {
            val type = EntityMetadataType.TEXT_DISPLAY_TEXT_OPACITY
            metadata[type] = EntityMetadata(type, EntityMetaValue.BYTE, it.newValue)
        }
        hasShadow.valueChanged { updateTextDisplayFormatting() }
        isSeeThrough.valueChanged { updateTextDisplayFormatting() }
        useDefaultBackgroundColor.valueChanged { updateTextDisplayFormatting() }
        alignment.valueChanged { updateTextDisplayFormatting() }
        interpolationDelay.triggerUpdate()
    }

    private fun updateTextDisplayFormatting() {
        val type = EntityMetadataType.TEXT_DISPLAY_FORMATTING
        metadata[type] = getTextDisplayFormatting(this)
    }
}

enum class TextDisplayAlignment(val mask: Byte) {
    CENTER(0x00),
    LEFT(0x08),
    RIGHT(0x016)
}