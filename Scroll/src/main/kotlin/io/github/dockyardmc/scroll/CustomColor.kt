package io.github.dockyardmc.scroll

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Serializable
data class CustomColor(val r: Int, val g: Int, val b: Int) {

    companion object {
        fun fromHex(hex: String): CustomColor {
            require(hex[0] == '#' && (hex.length == 7 || hex.length == 4)) {
                "Invalid hex color format (must be #RRGGBB or #RGB)"
            }

            val hexc = if (hex.length == 4) hex.replace(Regex("#[0-9a-fA-F]"), "$0$0") else hex
            val r = hexc.substring(1, 3).toInt(16)
            val g = hexc.substring(3, 5).toInt(16)
            val b = hexc.substring(5, 7).toInt(16)

            return CustomColor(r, g, b)
        }

        fun fromHsv(h: Float, s: Float, v: Float): CustomColor {
            val h_i = (h * 6).toInt()
            val f = h * 6 - h_i
            val p = v * (1 - s)
            val q = v * (1 - f * s)
            val t = v * (1 - (1 - f) * s)
            val (r, g, b) = when (h_i) {
                0 -> Triple(v, t, p)
                1 -> Triple(q, v, p)
                2 -> Triple(p, v, t)
                3 -> Triple(p, q, v)
                4 -> Triple(t, p, v)
                else -> Triple(v, p, q)
            }
            return CustomColor((r * 255).roundToInt(), (g * 255).roundToInt(), (b * 255).roundToInt())
        }
    }

    fun distanceTo(other: CustomColor): Double {
        val deltaR = (this.r - other.r).toDouble()
        val deltaG = (this.g - other.g).toDouble()
        val deltaB = (this.b - other.b).toDouble()
        return sqrt(deltaR * deltaR + deltaG * deltaG + deltaB * deltaB)
    }

    fun toHex(): String {
        require(r in 0..255 && g in 0..255 && b in 0..255) {
            "Invalid RGB values (must be between 0 and 255)"
        }

        return String.format("#%02X%02X%02X", r, g, b)
    }

    fun toHSV(): Triple<Float, Float, Float> {
        val r = this.r / 255f
        val g = this.g / 255f
        val b = this.b / 255f

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        val hue = when (max) {
            r -> 60 * (((g - b) / delta) % 6)
            g -> 60 * (((b - r) / delta) + 2)
            b -> 60 * (((r - g) / delta) + 4)
            else -> 0f
        }

        val saturation = if (max == 0f) 0f else delta / max
        return Triple(hue, saturation, max)
    }

    fun getNearestLegacyTextColor(): LegacyTextColor {
        var closestColor = LegacyTextColor.BLACK
        var smallestDistance = Double.MAX_VALUE

        for (legacyColor in LegacyTextColor.entries) {
            val legacyRgb = fromHex(legacyColor.hex)
            val distance = distanceTo(legacyRgb)

            if (distance < smallestDistance) {
                smallestDistance = distance
                closestColor = legacyColor
            }
        }

        return closestColor
    }

    fun adjustLightness(factor: Float): CustomColor {
        val clampedFactor = factor.coerceIn(0f, 2f)

        val newR = if (clampedFactor >= 1) {
            this.r + ((255 - this.r) * (clampedFactor - 1)).toInt()
        } else {
            (this.r * clampedFactor).toInt()
        }.coerceIn(0, 255)

        val newG = if (clampedFactor >= 1) {
            this.g + ((255 - this.g) * (clampedFactor - 1)).toInt()
        } else {
            (this.g * clampedFactor).toInt()
        }.coerceIn(0, 255)

        val newB = if (clampedFactor >= 1) {
            this.b + ((255 - this.b) * (clampedFactor - 1)).toInt()
        } else {
            (this.b * clampedFactor).toInt()
        }.coerceIn(0, 255)

        return CustomColor(newR, newG, newB)
    }
}

// To make sure it has "color": Hex in the json
object CustomColorSerializer : KSerializer<CustomColor> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CustomColor", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: CustomColor) {
        encoder.encodeString(value.toHex())
    }

    override fun deserialize(decoder: Decoder): CustomColor {
        return CustomColor.fromHex(decoder.decodeString())
    }
}