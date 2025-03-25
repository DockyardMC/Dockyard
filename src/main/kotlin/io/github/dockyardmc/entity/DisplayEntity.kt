package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.extentions.toRgbInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.maths.Quaternion
import io.github.dockyardmc.maths.vectors.Vector3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

open class DisplayEntity(location: Location) : Entity(location) {
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
            val quaternion = Quaternion.fromAxis(it.newValue)
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
        interpolationDelay.value = -1
    }

    fun setRightRotationFromQuaternion(quaternion: Quaternion) {
        val type = EntityMetadataType.DISPLAY_ROTATION_RIGHT
        metadata[type] = EntityMetadata(type, EntityMetaValue.QUATERNION, quaternion)
    }

    fun setLeftRotationFromQuaternion(quaternion: Quaternion) {
        val type = EntityMetadataType.DISPLAY_ROTATION_LEFT
        metadata[type] = EntityMetadata(type, EntityMetaValue.QUATERNION, quaternion)
    }

    fun scaleTo(x: Float, y: Float, z: Float, interpolation: Int? = null) {
        if(interpolation != null) transformInterpolation.value = interpolation
        scale.value = Vector3f(x, y, z)
    }

    fun scaleTo(vector3f: Vector3f, interpolation: Int? = null) {
        scaleTo(vector3f.x, vector3f.y, vector3f.z, interpolation)
    }

    fun scaleTo(all: Float, interpolation: Int? = null) {
        scaleTo(all, all, all, interpolation)
    }

    fun translateTo(x: Float, y: Float, z: Float, interpolation: Int? = null) {
        if(interpolation != null) translationInterpolation.value = interpolation
        translation.value = Vector3f(x, y, z)
    }

    fun translateTo(vector3f: Vector3f, interpolation: Int?) {
        translateTo(vector3f.x, vector3f.y, vector3f.z, interpolation)
    }

    fun rotateTo(x: Float, y: Float, z: Float, interpolation: Int? = null) {
        if(interpolation != null) transformInterpolation.value = interpolation
        rotation.value = Vector3f(x, y, z)
    }

    fun rotateTo(vector3f: Vector3f, interpolation: Int?) {
        rotateTo(vector3f.x, vector3f.y, vector3f.z, interpolation)
    }

    fun rotateBy(x: Float, y: Float, z: Float, interpolation: Int? = null) {
        if(interpolation != null) transformInterpolation.value = interpolation
        rotation.value = rotation.value + Vector3f(x, y, z)
    }

    fun rotateBy(vector3f: Vector3f, interpolation: Int?) {
        rotateBy(vector3f.x, vector3f.y, vector3f.z, interpolation)
    }

    fun getForwardVector(): Vector3f {
        val rotation = this.rotation.value

        val cosPitch = cos(rotation.x * PI / 180.0)
        val sinPitch = sin(rotation.x * PI / 180.0)
        val cosYaw = cos(rotation.y * PI / 180.0)
        val sinYaw = sin(rotation.y * PI / 180.0)

        return Vector3f(
            (cosPitch * sinYaw).toFloat(),
            -sinPitch.toFloat(),
            (cosPitch * cosYaw).toFloat()
        )
    }
}

enum class DisplayBillboard {
    FIXED,
    VERTICAL,
    HORIZONTAL,
    CENTER
}