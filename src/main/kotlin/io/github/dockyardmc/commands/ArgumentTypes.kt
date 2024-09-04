package io.github.dockyardmc.commands

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarIntEnum
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Item
import io.github.dockyardmc.registry.Particle
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.sounds.Sound
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
    val type: BrigadierStringType = BrigadierStringType.SINGLE_WORD,
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

class SoundArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Sound::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.RESOURCE

    override fun write(buffer: ByteBuf) {
        buffer.writeUtf("minecraft:sound")
    }
}

class RegistryResourceArgument(val registryResource: String): CommandArgument {
    override var expectedType: KClass<*> = String::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.RESOURCE

    override fun write(buffer: ByteBuf) {
        buffer.writeUtf(registryResource)
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
    var onlyAllowPlayer: Boolean = true,
): CommandArgument {
    override var expectedType: KClass<*> = Entity::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.ENTITY

    override fun write(buffer: ByteBuf) {
        var mask: Byte = 0x00
        mask = mask or 0x01
        if(onlyAllowPlayer) mask = mask or 0x02
        buffer.writeByte(mask.toInt())
    }
}

class BlockArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Block::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.BLOCK

    override fun write(buffer: ByteBuf) {} // No extra data
}

class BlockStateArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Block::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.BLOCK_STATE

    override fun write(buffer: ByteBuf) {} // No extra data
}

class ItemArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Item::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.ITEM

    override fun write(buffer: ByteBuf) {} // No extra data
}


class LegacyTextColorArgument(
): CommandArgument {
    override var expectedType: KClass<*> = LegacyTextColor::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.LEGACY_TEXT_COLOR

    override fun write(buffer: ByteBuf) {} // No extra data
}

class ParticleArgument(
): CommandArgument {
    override var expectedType: KClass<*> = Particle::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.PARTICLE

    override fun write(buffer: ByteBuf) {} // No extra data
}

class IntArgument(
    var min: Int? = null,
    var max: Int? = null,
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
): CommandArgument {
    override var expectedType: KClass<*> = Boolean::class
    override var parser: ArgumentCommandNodeParser = ArgumentCommandNodeParser.BOOL

    override fun write(buffer: ByteBuf) {} // No extra data
}

class LongArgument(
    var min: Long? = null,
    var max: Long? = null,
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
    var expectedReturnValueType: KClass<*>,
    var suggestions: CommandSuggestions? = null
)
