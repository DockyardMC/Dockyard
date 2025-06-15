package io.github.dockyardmc.extentions

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.maths.positiveCeilDiv
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.maths.vectors.Vector3d
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.AppliedPotionEffectSettings
import io.github.dockyardmc.registry.Registry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.registries.*
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.sounds.SoundEvent
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.Unpooled
import io.netty.handler.codec.DecoderException
import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import kotlin.experimental.inv

private const val SEGMENT_BITS: Byte = 0x7F
private const val CONTINUE_BIT = 0x80

fun ByteBuf.writeOptionalOLD(item: Any?, unit: (ByteBuf) -> Unit) {
    val isPresent = item != null
    this.writeBoolean(isPresent)
    if (isPresent) {
        unit.invoke(this)
    }
}

fun <T> ByteBuf.readList(reader: (ByteBuf) -> T): List<T> {
    val list = mutableListOf<T>()
    val size = this.readVarInt()
    for (i in 0 until size) {
        list.add(reader.invoke(this))
    }
    return list.toList()
}

fun ByteBuf.writeTextComponent(component: Component) {
    component.italic = false
    this.writeNBT(component.toNBT())
}

fun ByteBuf.writeTextComponent(text: String) {
    this.writeTextComponent(text.toComponent())
}

fun ByteBuf.readTextComponent(): Component {
    return this.readNBTCompound().toComponent()
}

fun ByteBuf.writeItemStackList(list: Collection<ItemStack>) {
    this.writeVarInt(list.size)
    list.forEach {
        it.write(this)
    }
}

fun ByteBuf.writeColor(color: CustomColor) {
    this.writeInt(color.getPackedInt())
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

object Buffer {
    fun makeArray(writer: (ByteBuf) -> Unit): ByteArray {
        val tempBuffer = Unpooled.buffer()
        writer.invoke(tempBuffer)
        return tempBuffer.array()
    }
}

fun ByteBuf.writeLongArray(array: List<Long>) {
    this.writeVarInt(array.size)
    array.forEach { this.writeLong(it) }
}

fun ByteBuf.readByteArray(): ByteArray {
    val len = this.readVarInt()
    val buffer = readBytes(len)
    val byteArray = buffer.toByteArraySafe()
    buffer.release()
    return byteArray
}

fun ByteBuf.readNBT(): BinaryTag {
    try {
        val inputStream = ByteBufInputStream(this) // bro is greedy and takes ALL THE BYTES to himself >:(
        val nbt = BinaryTagIO.reader().readNameless(inputStream as InputStream, BinaryTagIO.Compression.NONE)

        // read the rest of the bytes leftover, put them back and reset the reader index
        val rest = inputStream.readAllBytes()
        this.writeBytes(rest)
        this.resetReaderIndex()

        return nbt
    } catch (ex: Exception) {
        log("Failed to read NBT ($ex). Buffer state: readableBytes=${this.readableBytes()}", LogType.FATAL)
        throw ex
    }
}

fun ByteBuf.readNBTCompound(): CompoundBinaryTag {
    return this.readNBT() as CompoundBinaryTag
}

fun ByteBuf.writeNBT(nbt: BinaryTag) {
    this.writeNBT(nbt as CompoundBinaryTag)
}

fun ByteBuf.writeNBT(nbt: CompoundBinaryTag) {

    val outputStream = ByteArrayOutputStream()
    try {
        BinaryTagIO.writer().writeNameless(nbt, outputStream, BinaryTagIO.Compression.NONE)
    } finally {
        this.writeBytes(outputStream.toByteArray())
    }
}

fun ByteBuf.readFixedBitSet(i: Int): BitSet {
    val bs = ByteArray(positiveCeilDiv(i, 8))
    this.readBytes(bs)
    return BitSet.valueOf(bs)
}

fun ByteBuf.readInstant(): kotlinx.datetime.Instant = kotlinx.datetime.Instant.fromEpochMilliseconds(this.readLong())

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

inline fun <reified T : Enum<T>> ByteBuf.readEnum(): T = T::class.java.enumConstants[readVarInt()]
inline fun <reified T : Enum<T>> ByteBuf.readByteEnum(): T = T::class.java.enumConstants[readByte().toInt()]

inline fun <reified T : Enum<T>> ByteBuf.writeEnum(value: T) {
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

fun <T : RegistryEntry> ByteBuf.readRegistryEntry(registry: Registry): T {
    return registry.getByProtocolId(this.readVarInt()) as T
}

fun ByteBuf.writeRegistryEntry(entry: RegistryEntry) {
    this.writeVarInt(entry.getProtocolId())
}

fun ByteBuf.readStringList(): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until this.readVarInt()) {
        list.add(this.readString())
    }
    return list
}

fun ByteBuf.readRemainingBytesAsByteArray(): ByteArray {
    val bytes = ByteArray(this.readableBytes())
    this.readBytes(bytes)
    return bytes
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

fun ByteBuf.writeString(text: String): ByteBuf {
    val utf8Bytes = text.toByteArray(StandardCharsets.UTF_8)
    val length = utf8Bytes.size
    this.writeVarInt(length)
    this.writeBytes(utf8Bytes)
    return this
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
        ItemStack::class -> ItemStack.read(this) as T
        Byte::class -> this.readByte() as T
        Vector3::class -> Vector3.read(this) as T
        Vector3d::class -> Vector3d.read(this) as T
        Vector3f::class -> Vector3f.read(this) as T
        BinaryTag::class -> (this.readNBT() as CompoundBinaryTag) as T
        CompoundBinaryTag::class -> this.readNBTCompound() as T
        Sound::class -> Sound(SoundEvent.read(this).identifier) as T
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

fun ByteBuf.readPotionEffectHolder(): PotionEffect {
    val type = this.readVarInt()
    if (type == 0) {
        val identifier = this.readString()
        return PotionEffectRegistry[identifier]
    }
    return PotionEffectRegistry.getByProtocolId(type - 1)
}

fun ByteBuf.writeAppliedPotionEffectsList(list: Collection<AppliedPotionEffect>) {
    this.writeVarInt(list.size)
    list.forEach { this.writeAppliedPotionEffect(it) }
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
        this.writeInt(it.getPackedInt())
    }
}

fun ByteBuf.readEntityTypes(): List<EntityType> {
    val present = this.readBoolean()
    if (!present) return emptyList()

    val type = this.readVarInt() - 1
    if (type == -1) {
        val identifier = this.readString()
        return listOf(EntityTypeRegistry[identifier])
    }
    val list = mutableListOf<EntityType>()
    for (i in 0 until type) {
        list.add(readEntityTypeHolder())
    }
    return list
}

fun ByteBuf.readEntityTypeHolder(): EntityType {
    val type = this.readVarInt()
    if (type == 0) {
        val identifier = this.readString()
        return EntityTypeRegistry[identifier]
    }
    return EntityTypeRegistry.getByProtocolId(type - 1)
}

fun ByteBuf.readRepairable(): List<Item> {
    val type = this.readVarInt() - 1
    if (type == -1) {
        val identifier = this.readString()
        return listOf() //TODO tag registry
    }
    val list = mutableListOf<Item>()
    for (i in 0 until type) {
        list.add(readItemHolder())
    }
    return list
}


fun ByteBuf.readItemHolder(): Item {
    val type = this.readVarInt()
    if (type == 0) {
        val identifier = this.readString()
        return ItemRegistry[identifier]
    }
    return ItemRegistry.getByProtocolId(type - 1)
}

fun ByteBuf.writeByte(byte: Byte) {
    this.writeByte(byte.toInt())
}

fun Byte.toBoolean(): Boolean {
    return this == 1.toByte()
}

fun Boolean.toByte(): Byte {
    return if (this) 1.toByte() else 0.toByte()
}