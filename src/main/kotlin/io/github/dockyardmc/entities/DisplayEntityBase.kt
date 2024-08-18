package io.github.dockyardmc.entities

import cz.lukynka.Bindable
import io.github.dockyardmc.extentions.toRgbInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityType
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.World

open class DisplayEntityBase(
    override var location: Location,
    override var world: World,
) : Entity() {
    override var type: EntityType = EntityTypes.TEXT_DISPLAY
    override var health: Bindable<Float> = Bindable(0f)
    override var inventorySize: Int = 0

    val interpolationDelay: Bindable<Int> = Bindable(0)
    val transformInterpolation: Bindable<Int> = Bindable(0)
    val translationInterpolation: Bindable<Int> = Bindable(0)

    val translation: Bindable<Vector3f> = Bindable(Vector3f())
    val scale: Bindable<Vector3f> = Bindable(Vector3f(1f))
    val rotation: Bindable<Vector3f> = Bindable(Vector3f())
    val billboard: Bindable<DisplayBillboard> = Bindable(DisplayBillboard.FIXED)
    val brightness: Bindable<Int> = Bindable(-1)
    val viewRange: Bindable<Float> = Bindable(1f)
    val shadowRadius: Bindable<Float> = Bindable(0f)
    val shadowStrength: Bindable<Float> = Bindable(1f)
    val glowColor: Bindable<CustomColor> = Bindable(CustomColor.fromHex("#FFFFFF"))

    init {
        interpolationDelay.valueChanged {
            val type = EntityMetadataType.DISPLAY_INTERPOLATION_DELAY
            metadata[type] = EntityMetadata(type, EntityMetaValue.VAR_INT, it.newValue)
        }
        transformInterpolation.valueChanged {
            val type = EntityMetadataType.DISPLAY_TRANSFORM_INTERPOLATION
            metadata[type] = EntityMetadata(type, EntityMetaValue.VAR_INT, it.newValue)
        }
        translationInterpolation.valueChanged {
            val type = EntityMetadataType.DISPLAY_TRANSLATION_INTERPOLATION
            metadata[type] = EntityMetadata(type, EntityMetaValue.VAR_INT, it.newValue)
        }
        translation.valueChanged {
            val type = EntityMetadataType.DISPLAY_TRANSLATION
            metadata[type] = EntityMetadata(type, EntityMetaValue.VECTOR3, it.newValue)
        }
        scale.valueChanged {
            val type = EntityMetadataType.DISPLAY_SCALE
            metadata[type] = EntityMetadata(type, EntityMetaValue.VECTOR3, it.newValue)
        }
        rotation.valueChanged {
            val type = EntityMetadataType.DISPLAY_ROTATION_LEFT
            val quaternion = MathUtils.eulerToQuaternion(it.newValue)
            metadata[type] = EntityMetadata(type, EntityMetaValue.QUATERNION, quaternion)
        }
        billboard.valueChanged {
            val type = EntityMetadataType.DISPLAY_BILLBOARD
            metadata[type] = EntityMetadata(type, EntityMetaValue.BYTE, it.newValue.ordinal)
        }
        brightness.valueChanged {
            val type = EntityMetadataType.DISPLAY_BRIGHTNESS
            metadata[type] = EntityMetadata(type, EntityMetaValue.VAR_INT, it.newValue)
        }
        viewRange.valueChanged {
            val type = EntityMetadataType.DISPLAY_VIEW_RANGE
            metadata[type] = EntityMetadata(type, EntityMetaValue.FLOAT, it.newValue)
        }
        shadowRadius.valueChanged {
            val type = EntityMetadataType.DISPLAY_SHADOW_RADIUS
            metadata[type] = EntityMetadata(type, EntityMetaValue.FLOAT, it.newValue)
        }
        shadowStrength.valueChanged {
            val type = EntityMetadataType.DISPLAY_SHADOW_STRENGTH
            metadata[type] = EntityMetadata(type, EntityMetaValue.FLOAT, it.newValue)
        }
        glowColor.valueChanged {
            val type = EntityMetadataType.DISPLAY_GLOW_COLOR
            metadata[type] = EntityMetadata(type, EntityMetaValue.VAR_INT, it.newValue.toRgbInt())
        }
    }
}



enum class DisplayBillboard {
    FIXED,
    VERTICAL,
    HORIZONTAL,
    CENTER
}