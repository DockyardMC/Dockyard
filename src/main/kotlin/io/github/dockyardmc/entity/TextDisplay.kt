package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.extentions.getPackedInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.extensions.toComponent

class TextDisplay(location: Location): DisplayEntity(location) {

    override var type: EntityType = EntityTypes.TEXT_DISPLAY
    val text: Bindable<String> = bindablePool.provideBindable("")
    val lineWidth: Bindable<Int> = bindablePool.provideBindable(200)
    val backgroundColor: Bindable<CustomColor> = bindablePool.provideBindable(CustomColor(64, 0, 0))
    val opacity: Bindable<Int> = bindablePool.provideBindable(255)
    val hasShadow: Bindable<Boolean> = bindablePool.provideBindable(false)
    val isSeeThrough: Bindable<Boolean> = bindablePool.provideBindable(false)
    val useDefaultBackgroundColor: Bindable<Boolean> = bindablePool.provideBindable(true)
    val alignment: Bindable<Alignment> = bindablePool.provideBindable(Alignment.CENTER)

    enum class Alignment(val left: Boolean, val right: Boolean) {
        CENTER(false, false),
        LEFT(true, false),
        RIGHT(false, true)
    }

    init {
        billboard.value = BillboardConstraints.CENTER
        text.valueChanged { event ->
            metadata[Metadata.TextDisplay.TEXT] = event.newValue.toComponent()
        }
        lineWidth.valueChanged { event ->
            metadata[Metadata.TextDisplay.LINE_WIDTH] = event.newValue
        }
        backgroundColor.valueChanged { event ->
            metadata[Metadata.TextDisplay.BACKGROUND_COLOR] = event.newValue.getPackedInt()
        }
        opacity.valueChanged { event ->
            metadata[Metadata.TextDisplay.TEXT_OPACITY] = event.newValue.toByte()
        }
        hasShadow.valueChanged { event ->
            metadata[Metadata.TextDisplay.HAS_SHADOW] = event.newValue
        }
        isSeeThrough.valueChanged { event ->
            metadata[Metadata.TextDisplay.IS_SEE_THROUGH] = event.newValue
        }
        useDefaultBackgroundColor.valueChanged { event ->
            metadata[Metadata.TextDisplay.USE_DEFAULT_BACKGROUND] = event.newValue
        }
        alignment.valueChanged { event ->
            metadata[Metadata.TextDisplay.ALIGN_LEFT] = event.newValue.left
            metadata[Metadata.TextDisplay.ALIGN_RIGHT] = event.newValue.right
        }
        interpolationDelay.triggerUpdate()
    }
}

