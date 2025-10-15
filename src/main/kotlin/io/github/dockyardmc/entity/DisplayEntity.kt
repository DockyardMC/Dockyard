package io.github.dockyardmc.entity

import cz.lukynka.bindables.Bindable
import io.github.dockyardmc.entity.metadata.Metadata
import io.github.dockyardmc.extentions.getPackedInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.Quaternion
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.registries.EntityType
import io.github.dockyardmc.scroll.CustomColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

open class DisplayEntity(location: Location) : Entity(location) {
    override var type: EntityType = EntityTypes.TEXT_DISPLAY
    override val health: Bindable<Float> = bindablePool.provideBindable(0f)
    override var inventorySize: Int = 0

    val interpolationDelay: Bindable<Int> = bindablePool.provideBindable(0)
    val transformInterpolation: Bindable<Int> = bindablePool.provideBindable(0)
    val translationInterpolation: Bindable<Int> = bindablePool.provideBindable(0)

    val translation: Bindable<Vector3f> = bindablePool.provideBindable(Vector3f())
    val scale: Bindable<Vector3f> = bindablePool.provideBindable(Vector3f(1f))
    val rotation: Bindable<Vector3f> = bindablePool.provideBindable(Vector3f())
    val billboard: Bindable<BillboardConstraints> = bindablePool.provideBindable(BillboardConstraints.FIXED)
    val brightness: Bindable<Int> = bindablePool.provideBindable(-1)
    val viewRange: Bindable<Float> = bindablePool.provideBindable(1f)
    val shadowRadius: Bindable<Float> = bindablePool.provideBindable(0f)
    val shadowStrength: Bindable<Float> = bindablePool.provideBindable(1f)
    val glowColor: Bindable<CustomColor> = bindablePool.provideBindable(CustomColor.fromHex("#FFFFFF"))

    enum class BillboardConstraints {
        FIXED,
        VERTICAL,
        HORIZONTAL,
        CENTER
    }

    init {
        interpolationDelay.valueChanged { event ->
            metadata[Metadata.Display.INTERPOLATION_DELAY] = event.newValue
        }
        transformInterpolation.valueChanged { event ->
            metadata[Metadata.Display.TRANSFORMATION_INTERPOLATION_DURATION] = event.newValue
        }
        translationInterpolation.valueChanged { event ->
            metadata[Metadata.Display.TRANSFORMATION_INTERPOLATION_DURATION] = event.newValue
        }
        translation.valueChanged { event ->
            metadata[Metadata.Display.TRANSLATION] = event.newValue
        }
        scale.valueChanged { event ->
            metadata[Metadata.Display.SCALE] = event.newValue
        }
        rotation.valueChanged { event ->
            val quaternion = Quaternion.fromAxis(event.newValue)
            metadata[Metadata.Display.ROTATION_LEFT] = quaternion
        }
        billboard.valueChanged { event ->
            metadata[Metadata.Display.BILLBOARD_CONSTRAINTS] = event.newValue.ordinal.toByte()
        }
        brightness.valueChanged { event ->
            metadata[Metadata.Display.BRIGHTNESS_OVERRIDE] = event.newValue
        }
        viewRange.valueChanged { event ->
            metadata[Metadata.Display.VIEW_RANGE] = event.newValue
        }
        shadowRadius.valueChanged { event ->
            metadata[Metadata.Display.SHADOW_RADIUS] = event.newValue
        }
        shadowStrength.valueChanged { event ->
            metadata[Metadata.Display.SHADOW_STRENGHT] = event.newValue
        }
        glowColor.valueChanged { event ->
            metadata[Metadata.Display.GLOW_COLOR_OVERRIDE] = event.newValue.getPackedInt()
        }
        interpolationDelay.value = -1
    }

    fun setRightRotationFromQuaternion(quaternion: Quaternion) {
        metadata[Metadata.Display.ROTATION_RIGHT] = quaternion
    }

    fun setLeftRotationFromQuaternion(quaternion: Quaternion) {
        metadata[Metadata.Display.ROTATION_LEFT] = quaternion
    }

    fun scaleTo(x: Float, y: Float, z: Float, interpolation: Int? = null) {
        if (interpolation != null) transformInterpolation.value = interpolation
        scale.value = Vector3f(x, y, z)
    }

    fun scaleTo(vector3f: Vector3f, interpolation: Int? = null) {
        scaleTo(vector3f.x, vector3f.y, vector3f.z, interpolation)
    }

    fun scaleTo(all: Float, interpolation: Int? = null) {
        scaleTo(all, all, all, interpolation)
    }

    fun translateTo(x: Float, y: Float, z: Float, interpolation: Int? = null) {
        if (interpolation != null) translationInterpolation.value = interpolation
        translation.value = Vector3f(x, y, z)
    }

    fun translateTo(vector3f: Vector3f, interpolation: Int?) {
        translateTo(vector3f.x, vector3f.y, vector3f.z, interpolation)
    }

    fun rotateTo(x: Float, y: Float, z: Float, interpolation: Int? = null) {
        if (interpolation != null) transformInterpolation.value = interpolation
        rotation.value = Vector3f(x, y, z)
    }

    fun rotateTo(vector3f: Vector3f, interpolation: Int?) {
        rotateTo(vector3f.x, vector3f.y, vector3f.z, interpolation)
    }

    fun rotateBy(x: Float, y: Float, z: Float, interpolation: Int? = null) {
        if (interpolation != null) transformInterpolation.value = interpolation
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

