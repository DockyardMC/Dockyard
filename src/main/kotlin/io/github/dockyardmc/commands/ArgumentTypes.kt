package io.github.dockyardmc.commands

import io.github.dockyardmc.extentions.writeVarIntEnum
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Item
import io.github.dockyardmc.registry.Particle
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.utils.Vector2
import io.github.dockyardmc.utils.Vector2f
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.World
import io.netty.buffer.ByteBuf
import java.util.*
import kotlin.experimental.or
import kotlin.reflect.KClass


interface CommandArgument {
    var expectedType: KClass<*>
    var parser: ArgumentCommandNodeParser
    fun write(buffer: ByteBuf)
}

class StringArgument(
    val type: BrigadierStringType,
    val staticCompletions: MutableList<String> = mutableListOf(),
): CommandArgument {
    override var expectedType: KClass<*> = String::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.STRING

    override fun write(buffer: ByteBuf) {
        buffer.writeVarIntEnum<BrigadierStringType>(type)
    }
}

class WorldArgument(
): CommandArgument {
    override var expectedType: KClass<*> = World::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.STRING

    override fun write(buffer: ByteBuf) {
        buffer.writeVarIntEnum<BrigadierStringType>(BrigadierStringType.SINGLE_WORD)
    }
}

class PlayerArgument(
    var onlySinglePlayer: Boolean = true,
): CommandArgument {
    override var expectedType: KClass<*> = Player::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.ENTITY

    override fun write(buffer: ByteBuf) {
        var mask: Byte = 0x00
        if(onlySinglePlayer) mask = mask or 0x01
        mask = mask or 0x02 // only allow player
        buffer.writeByte(mask.toInt())
    }
}

//TODO Implement in handler
class EntityArgument(
    var onlySingleEntity: Boolean = true,
    var onlyAllowPlayer: Boolean = true,
): CommandArgument {
    override var expectedType: KClass<*> = Player::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.ENTITY

    override fun write(buffer: ByteBuf) {
        var mask: Byte = 0x00
        if(onlySingleEntity) mask = mask or 0x01
        if(onlyAllowPlayer) mask = mask or 0x02
        buffer.writeByte(mask.toInt())
    }
}

//TODO Implement in handler
class GameProfileArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Player::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.GAME_PROFILE

    override fun write(buffer: ByteBuf) {} // No extra data
}

//TODO Implement in handler
class Vector3fArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Vector3f::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.VECTOR_3

    override fun write(buffer: ByteBuf) {} // No extra data
}

//TODO Implement in handler
class Vector3Argument(
): CommandArgument {
    override var expectedType: KClass<*> = Vector3::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.VECTOR_3

    override fun write(buffer: ByteBuf) {} // No extra data
}

//TODO Implement in handler
class Vector2Argument(
): CommandArgument {
    override var expectedType: KClass<*> = Vector2::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.VECTOR_2

    override fun write(buffer: ByteBuf) {} // No extra data
}

//TODO Implement in handler
class Vector2fArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Vector2f::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.VECTOR_2

    override fun write(buffer: ByteBuf) {} // No extra data
}

//TODO Implement in handler
class BlockArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Block::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.BLOCK

    override fun write(buffer: ByteBuf) {} // No extra data
}

//TODO Implement in handler
class BlockStateArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Block::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.BLOCK_STATE

    override fun write(buffer: ByteBuf) {} // No extra data
}

//TODO Implement in handler
class ItemArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Item::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.ITEM

    override fun write(buffer: ByteBuf) {} // No extra data
}


//TODO Implement in handler
class LegacyTextColorArgument(
): CommandArgument {
    override var expectedType: KClass<*> = LegacyTextColor::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.LEGACY_TEXT_COLOR

    override fun write(buffer: ByteBuf) {} // No extra data
}

//TODO Implement in handler
class ParticleArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Particle::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.PARTICLE

    override fun write(buffer: ByteBuf) {} // No extra data
}

class IntArgument(
    var min: Int? = null,
    var max: Int? = null,
    var staticCompletions: MutableList<Int> = mutableListOf(),
): CommandArgument {
    override var expectedType: KClass<*> = Int::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.INTEGER

    override fun write(buffer: ByteBuf) {
        var mask: Byte = 0x00
        if(min != null) mask = mask or 0x01
        if(max != null) mask = mask or 0x01
        buffer.writeByte(mask.toInt())
        if(min != null) buffer.writeInt(min!!)
        if(max != null) buffer.writeInt(max!!)
    }
}

class DoubleArgument(
    var min: Double? = null,
    var max: Double? = null,
    val staticCompletions: MutableList<Double> = mutableListOf(),
): CommandArgument {
    override var expectedType: KClass<*> = Double::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.DOUBLE

    override fun write(buffer: ByteBuf) {
        var mask: Byte = 0x00
        if(min != null) mask = mask or 0x01
        if(max != null) mask = mask or 0x01
        buffer.writeByte(mask.toInt())
        if(min != null) buffer.writeDouble(min!!)
        if(max != null) buffer.writeDouble(max!!)
    }
}

class FloatArgument(
    var min: Float? = null,
    var max: Float? = null,
    val staticCompletions: MutableList<Float> = mutableListOf(),
): CommandArgument {
    override var expectedType: KClass<*> = Float::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.FLOAT

    override fun write(buffer: ByteBuf) {
        var mask: Byte = 0x00
        if(min != null) mask = mask or 0x01
        if(max != null) mask = mask or 0x01
        buffer.writeByte(mask.toInt())
        if(min != null) buffer.writeFloat(min!!)
        if(max != null) buffer.writeFloat(max!!)
    }
}

class BooleanArgument(
    val staticCompletions: MutableList<Boolean> = mutableListOf(true, false),
): CommandArgument {
    override var expectedType: KClass<*> = Boolean::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.BOOL

    override fun write(buffer: ByteBuf) {} // No extra data
}

class LongArgument(
    var min: Long? = null,
    var max: Long? = null,
    val staticCompletions: MutableList<Long> = mutableListOf(),
): CommandArgument {
    override var expectedType: KClass<*> = Long::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.LONG

    override fun write(buffer: ByteBuf) {
        var mask: Byte = 0x00
        if(min != null) mask = mask or 0x01
        if(max != null) mask = mask or 0x01
        buffer.writeByte(mask.toInt())
        if(min != null) buffer.writeLong(min!!)
        if(max != null) buffer.writeLong(max!!)
    }
}

class UUIDArgument(
): CommandArgument {
    override var expectedType: KClass<*> = UUID::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.UUID

    override fun write(buffer: ByteBuf) {} // No extra data
}

class EnumArgument(
    val enumType: KClass<*>,
): CommandArgument {
    override var expectedType: KClass<*> = String::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.STRING
    override fun write(buffer: ByteBuf) {
        buffer.writeVarIntEnum<BrigadierStringType>(BrigadierStringType.SINGLE_WORD)
    }
}


class CommandArgumentData(
    val argument: CommandArgument,
    val optional: Boolean = false,
    var returnedValue: Any? = null,
    var expectedReturnValueType: KClass<*>
)
