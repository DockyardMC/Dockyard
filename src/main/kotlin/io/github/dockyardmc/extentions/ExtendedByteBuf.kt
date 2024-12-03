package io.github.dockyardmc.extentions

import cz.lukynka.prettylog.log
import io.github.dockyardmc.item.*
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.AppliedPotionEffectSettings
import io.github.dockyardmc.registry.registries.*
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.sounds.readSoundEvent
import io.github.dockyardmc.sounds.writeSoundEvent
import io.github.dockyardmc.utils.positiveCeilDiv
import io.github.dockyardmc.utils.vectors.*
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.DecoderException
import org.jglrxavpok.hephaistos.nbt.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import kotlin.experimental.inv

private const val SEGMENT_BITS: Byte = 0x7F
private const val CONTINUE_BIT = 0x80

fun ByteBuf.writeOptional(item: Any?, unit: (ByteBuf) -> Unit) {
    val isPresent = item != null
    this.writeBoolean(isPresent)
    if (isPresent) {
        unit.invoke(this)
    }
}

fun ByteBuf.writeTextComponent(component: Component) {
    component.italic = false
    this.writeNBT(component.toNBT())
}

fun ByteBuf.writeTextComponent(text: String) {
    this.writeTextComponent(text.toComponent())
}

fun ByteBuf.writeItemStackList(list: Collection<ItemStack>) {
    this.writeVarInt(list.size)
    list.forEach {
        this.writeItemStack(it)
    }
}

fun ByteBuf.readUUID(): UUID {
    val most = this.readLong()
    val least = this.readLong()
    return UUID(most, least)
}

fun ByteBuf.writeUUID(uuid: UUID) {
    this.writeLong(uuid.mostSignificantBits)
    this.writeLong(uuid.leastSignificantBits)
}

fun ByteBuf.writeUUIDArray(uuids: Collection<UUID>) {
    this.writeVarInt(uuids.size)
    uuids.forEach { this.writeUUID(it) }
}

fun ByteBuf.writeByteArray(bs: ByteArray) {
    this.writeVarInt(bs.size)
    this.writeBytes(bs)
}

fun ByteBuf.writeVarIntArray(array: List<Int>) {
    this.writeVarInt(array.size)
    array.forEach { this.writeVarInt(it) }
}

fun ByteBuf.writeLongArray(array: LongArray) {
    this.writeLongArray(array.toList())
}

fun ByteBuf.writeLongArray(array: List<Long>) {
    this.writeVarInt(array.size)
    array.forEach { this.writeLong(it) }
}

fun ByteBuf.readByteArray(): ByteArray {
    val len = this.readVarInt()
    return readBytes(len).toByteArraySafe()
}

fun ByteBuf.readNBT(): NBT {
    val buffer = this
    val nbtReader = NBTReader(object : InputStream() {

        override fun read(): Int = buffer.readByte().toInt() and 0xFF
        override fun available(): Int = buffer.readableBytes()

    }, CompressedProcesser.NONE)
    return try {
        val tagId: Byte = buffer.readByte()
        if (tagId.toInt() == NBTType.TAG_End.ordinal) NBTEnd else nbtReader.readRaw(tagId.toInt())
    } catch (e: IOException) {
        throw java.lang.RuntimeException(e)
    } catch (e: NBTException) {
        throw java.lang.RuntimeException(e)
    }
}


fun ByteBuf.writeNBT(nbt: NBT, truncateRootTag: Boolean = true) {

    val outputStream = ByteArrayOutputStream()
    try {
        val writer = NBTWriter(outputStream, CompressedProcesser.NONE)
        writer.writeNamed("", nbt)
        writer.close()
    } finally {
        if (truncateRootTag) {
            var outData = outputStream.toByteArray()

            // Since 1.20.2 (Protocol 764) NBT sent over the network has been updated to exclude the name from the root TAG_COMPOUND
            // ┌───────────┬────────┬────────────────┬──────────────┬───────────┐
            // │  Version  │ TypeID │ Length of Name │     Name     │  Payload  │
            // ├───────────┼────────┼────────────────┼──────────────┼───────────┤
            // │ < 1.20.2  │ 0x0a   │ 0x00 0x00      │ Empty String │ 0x02 0x09 │
            // │ >= 1.20.2 │ 0x0a   │ N/A            │ N/A          │ 0x02 0x09 │
            // └───────────┴────────┴────────────────┴──────────────┴───────────┘

            // Thanks to Kev (kev_dev) for pointing this out because I think I would have gone mad otherwise
            val list = outData.toMutableList()
            list.removeAt(1)
            list.removeAt(1)
            outData = list.toByteArray()
            writeBytes(outData)
        } else {
            writeBytes(outputStream.toByteArray())
        }
    }
}

fun ByteBuf.readFixedBitSet(i: Int): BitSet {
    val bs = ByteArray(positiveCeilDiv(i, 8))
    this.readBytes(bs)
    return BitSet.valueOf(bs)
}

fun ByteBuf.readInstant(): Instant = Instant.ofEpochMilli(this.readLong())

fun ByteBuf.writeVarLong(long: Long): ByteBuf {
    var modLong = long
    while (true) {
        if (modLong and -0x80L == 0L) {
            this.writeByte(modLong.toInt())
        }
        this.writeByte((modLong and 0x7FL).toInt() or 0x80)
        modLong = modLong ushr 7
    }
}

fun ByteBuf.readVarLong(): Long {
    var b: Byte
    var long = 0L
    var iteration = 0
    do {
        b = this.readByte()
        long = long or ((b.toInt() and 0x7F).toLong() shl iteration++ * 7)
        if (iteration <= 10) continue
        throw RuntimeException("VarLong too big")
    } while (hasContinuationBit(b))
    return long
}

fun hasContinuationBit(byte: Byte): Boolean = byte.toInt() and 0x80 == 128

inline fun <reified T : Enum<T>> ByteBuf.readVarIntEnum(): T = T::class.java.enumConstants[readVarInt()]
inline fun <reified T : Enum<T>> ByteBuf.readByteEnum(): T = T::class.java.enumConstants[readByte().toInt()]

inline fun <reified T : Enum<T>> ByteBuf.writeVarIntEnum(value: T) {
    this.writeVarInt(value.ordinal)
}

fun <T : Enum<T>> ByteBuf.writeByteEnum(value: T) {
    this.writeByte(value.ordinal)
}


fun ByteBuf.readVarInt(): Int {
    var value = 0
    var position = 0
    var currentByte: Byte
    while (this.isReadable) {
        currentByte = readByte()
        value = value or (currentByte.toInt() and SEGMENT_BITS.toInt() shl position)
        if (currentByte.toInt() and CONTINUE_BIT == 0) break
        position += 7
        if (position >= 32) throw java.lang.RuntimeException("VarInt is too big")
    }
    return value
}

fun ByteBuf.writeStringArray(list: Collection<String>) {
    writeVarInt(list.size)
    list.forEach { writeString(it) }
}


fun ByteBuf.writeVarInt(int: Int) {
    var value = int
    while (true) {
        if (value and SEGMENT_BITS.inv().toInt() == 0) {
            writeByte(value)
            return
        }
        writeByte(value and SEGMENT_BITS.toInt() or CONTINUE_BIT)
        value = value ushr 7
    }
}

fun ByteBuf.readString() = readString(Short.MAX_VALUE.toInt())
fun ByteBuf.readUtfAndLength() = readUtfAndLength(Short.MAX_VALUE.toInt())
fun ByteBuf.readString(i: Int): String {
    val maxSize = i * 3
    val size = this.readVarInt()
    if (size > maxSize) throw DecoderException("The received string was longer than the allowed $maxSize ($size > $maxSize)")
    if (size < 0) throw DecoderException("The received string's length was smaller than 0")
    val string = this.toString(this.readerIndex(), size, StandardCharsets.UTF_8)
    this.readerIndex(this.readerIndex() + size)
    if (string.length > i) throw DecoderException("The received string was longer than the allowed (${string.length} > $i)")
    return string
}

fun ByteBuf.readStringList(): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until this.readVarInt()) {
        list.add(this.readString())
    }
    return list
}

fun ByteBuf.readUtfAndLength(i: Int): Pair<String, Int> {
    val maxSize = i * 3
    val size = this.readVarInt()
    if (size > maxSize) throw DecoderException("The received string was longer than the allowed $maxSize ($size > $maxSize)")
    if (size < 0) throw DecoderException("The received string's length was smaller than 0")
    val string = this.toString(this.readerIndex(), size, StandardCharsets.UTF_8)
    this.readerIndex(this.readerIndex() + size)
    if (string.length > i) throw DecoderException("The received string was longer than the allowed (${string.length} > $i)")
    return string to size
}

fun ByteBuf.writeString(text: String) {
    val utf8Bytes = text.toByteArray(StandardCharsets.UTF_8)
    val length = utf8Bytes.size
    this.writeVarInt(length)
    this.writeBytes(utf8Bytes)
}

fun ByteBuf.toByteArraySafe(): ByteArray {
    if (this.hasArray()) {
        return this.array()
    }

    val bytes = ByteArray(this.readableBytes())
    this.getBytes(this.readerIndex(), bytes)

    return bytes
}

fun ByteBuf.readAppliedPotionEffect(): AppliedPotionEffect {
    val id = this.readVarInt()
    val settings = AppliedPotionEffectSettings.read(this)

    val effect = PotionEffectRegistry.getByProtocolId(id)

    return AppliedPotionEffect(effect, settings)
}

fun ByteBuf.writeAppliedPotionEffect(effect: AppliedPotionEffect) {
    this.writeVarInt(PotionEffectRegistry[effect.effect.identifier].getProtocolId())
    effect.settings.write(this)
}

fun ByteBuf.readAppliedPotionEffectsList(): List<AppliedPotionEffect> {
    val list = mutableListOf<AppliedPotionEffect>()
    for (i in 0 until this.readVarInt()) {
        list.add(this.readAppliedPotionEffect())
    }
    return list
}


fun ByteArray.toByteBuf(): ByteBuf = Unpooled.copiedBuffer(this)

inline fun <reified T : Any> ByteBuf.readOptionalOrDefault(default: T): T {
    val optional = this.readOptionalOrNull<T>() ?: return default
    return optional
}

inline fun <reified T : Any> ByteBuf.readOptionalOrNull(): T? {
    val isPresent = this.readBoolean()
    if (!isPresent) return null
    return when (T::class) {
        Int::class -> this.readVarInt() as T
        String::class -> this.readString() as T
        Boolean::class -> this.readBoolean() as T
        Float::class -> this.readFloat() as T
        Double::class -> this.readDouble() as T
        Long::class -> this.readLong() as T
        UUID::class -> this.readUUID() as T
        ItemStack::class -> this.readItemStack() as T
        Byte::class -> this.readByte() as T
        Vector3::class -> this.readVector3() as T
        Vector3d::class -> this.readVector3d() as T
        Vector3f::class -> this.readVector3f() as T
        NBT::class -> (this.readNBT() as NBTCompound) as T
        NBTCompound::class -> this.readNBT() as T
        Sound::class -> Sound(this.readSoundEvent()) as T
        EntityType::class -> EntityTypeRegistry[this.readString()] as T
        PotionEffect::class -> PotionEffectRegistry.getByProtocolId(this.readVarInt()) as T
        CustomColor::class -> CustomColor.fromRGBInt(this.readInt()) as T
        else -> throw IllegalArgumentException("This primitive doesn't have serializer")
    }
}


fun ByteBuf.readItemList(): MutableList<Item> {
    val size = this.readVarInt()
    val list = mutableListOf<Item>()
    for (i in 0 until size) {
        list.add(ItemRegistry[this.readString()])
    }
    return list
}

fun ByteBuf.readConsumeEffects(): List<ConsumeEffect> {
    val size = this.readVarInt()
    val effects = mutableListOf<ConsumeEffect>()
    for (i in 0 until size) {
        val type = this.readVarInt()
        val effect = when (type) {
            0 -> ApplyEffectsConsumeEffect(this.readAppliedPotionEffectsList(), this.readFloat())
            1 -> readRemoveEffectsConsumeEffect()
            2 -> ClearAllEffectsConsumeEffect()
            3 -> TeleportRandomlyConsumeEffect(this.readFloat())
            4 -> PlaySoundConsumeEffect(Sound(this.readSoundEvent()))
            else -> throw IllegalStateException("Invalid consume effect $type")
        }
    }
    return effects
}

fun ByteBuf.readRemoveEffectsConsumeEffect(): RemoveEffectsConsumeEffect {
    val type = this.readVarInt() - 1
    if (type == -1) {
        val identifier = this.readString()
        return RemoveEffectsConsumeEffect(listOf())
    } else {
        val list = mutableListOf<PotionEffect>()
        for (i in 0 until type) {
            list.add(this.readPotionEffectHolder())
        }
        return RemoveEffectsConsumeEffect(list)
    }
}

fun ByteBuf.readPotionEffectHolder(): PotionEffect {
    val type = this.readVarInt()
    if (type == 0) {
        val identifier = this.readString()
        return PotionEffectRegistry[identifier]
    } else {
        return PotionEffectRegistry.getByProtocolId(type - 1)
    }
}

fun ByteBuf.writeAppliedPotionEffectsList(list: Collection<AppliedPotionEffect>) {
    this.writeVarInt(list.size)
    list.forEach { this.writeAppliedPotionEffect(it) }
}

fun ByteBuf.writeConsumeEffects(effects: List<ConsumeEffect>) {
    this.writeVarInt(effects.size)
    effects.forEach { effect ->
        when (effect) {
            is ApplyEffectsConsumeEffect -> {
                this.writeVarInt(0)
                this.writeAppliedPotionEffectsList(effect.effects)
                this.writeFloat(effect.probability)
            }

            is RemoveEffectsConsumeEffect -> {
                throw NotImplementedError()
//                this.writeVarInt(1)
//                this.writeAppliedPotionEffectsList(effect.effects)
            }

            is ClearAllEffectsConsumeEffect -> {
                this.writeVarInt(2)
            }

            is TeleportRandomlyConsumeEffect -> {
                this.writeVarInt(3)
                this.writeFloat(effect.diameter)
            }

            is PlaySoundConsumeEffect -> {
                this.writeVarInt(4)
                this.writeSoundEvent(effect.sound.identifier)
            }

            else -> throw IllegalStateException("Invalid consume effect")
        }
    }
}

fun ByteBuf.readCustomColor(): CustomColor {
    return CustomColor.fromRGBInt(this.readInt())
}

fun ByteBuf.readCustomColorList(): List<CustomColor> {
    val list = mutableListOf<CustomColor>()
    for (i in 0 until this.readVarInt()) {
        list.add(this.readCustomColor())
    }
    return list
}

fun ByteBuf.writeCustomColorList(list: Collection<CustomColor>) {
    this.writeVarInt(list.size)
    list.forEach {
        this.writeInt(it.toRgbInt())
    }
}

fun ByteBuf.writeFireworkExplosion(component: FireworkExplosionItemComponent) {
    this.writeVarIntEnum<FireworkShape>(component.shape)
    this.writeCustomColorList(component.colors)
    this.writeCustomColorList(component.fadeColors)
    this.writeBoolean(component.hasTrail)
    this.writeBoolean(component.hasTwinkle)
}

fun ByteBuf.readFireworkExplosionList(): List<FireworkExplosionItemComponent> {
    val list = mutableListOf<FireworkExplosionItemComponent>()
    for (i in 0 until this.readVarInt()) {
        list.add(this.readFireworkExplosion())
    }
    return list
}


fun ByteBuf.readFireworkExplosion(): FireworkExplosionItemComponent {
    return FireworkExplosionItemComponent(
        this.readVarIntEnum<FireworkShape>(),
        this.readCustomColorList(),
        this.readCustomColorList(),
        this.readBoolean(),
        this.readBoolean()
    )
}

fun ByteBuf.readEntityTypes(): List<EntityType> {
    val present = this.readBoolean()
    if (!present) return emptyList()

    val type = this.readVarInt() - 1
    if (type == -1) {
        val identifier = this.readString()
        return listOf(EntityTypeRegistry[identifier])
    } else {
        val list = mutableListOf<EntityType>()
        for (i in 0 until type) {
            list.add(readEntityTypeHolder())
        }
        return list
    }
}

fun ByteBuf.readEntityTypeHolder(): EntityType {
    val type = this.readVarInt()
    if (type == 0) {
        val identifier = this.readString()
        return EntityTypeRegistry[identifier]
    } else {
        return EntityTypeRegistry.getByProtocolId(type - 1)
    }
}

fun ByteBuf.readRepairable(): List<Item> {
    val type = this.readVarInt() - 1
    if (type == -1) {
        val identifier = this.readString()
        return listOf() //TODO tag registry
    } else {
        val list = mutableListOf<Item>()
        for (i in 0 until type) {
            list.add(readItemHolder())
        }
        return list
    }
}


fun ByteBuf.readItemHolder(): Item {
    val type = this.readVarInt()
    if (type == 0) {
        val identifier = this.readString()
        return ItemRegistry[identifier]
    } else {
        return ItemRegistry.getByProtocolId(type - 1)
    }
}