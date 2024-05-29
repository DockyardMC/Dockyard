package io.github.dockyardmc.commands.nodes

import io.github.dockyardmc.player.Player
import java.util.UUID
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class Command(): Cloneable {
    lateinit var internalExecutorDoNotUse: (CommandExecutor) -> Unit
    var children: MutableMap<String, CommandChildData> = mutableMapOf()
    var permission = ""
    var aliases = mutableListOf<String>()


    fun <T> get(childName: String): T {
        if(children[childName] == null) throw Exception("Child with name $childName does not exist")

        return children[childName]!!.returnedValue as T
    }

    inline fun <reified T : Enum<T>> getEnum(childName: String): T {
        val value = get<String>(childName)
        return T::class.java.enumConstants.firstOrNull { it.name == value.uppercase() } ?: throw Exception("Enum ${T::class.simpleName} does not contain \"${value.uppercase()}\"")
    }

    fun <T> getOrNull(childName: String): T? {
        if(children[childName] == null) return null
        return children[childName]!!.returnedValue as T
    }

    fun addChild(name: String, child: CommandChild) {
        children[name] = CommandChildData(child, false, expectedReturnValueType = child.expectedType)
    }

    fun addOptionalChild(name: String, child: CommandChild) {
        children[name] = CommandChildData(child, true, expectedReturnValueType = child.expectedType)
    }

    fun execute(function: (CommandExecutor) -> Unit) {
        internalExecutorDoNotUse = function
    }

    fun build(): Command {
        return this
    }
}

interface CommandChild {
    var expectedType: KClass<*>
}

class StringArgument(
    val staticCompletions: MutableList<String> = mutableListOf(),
    override var expectedType: KClass<*> = String::class,
): CommandChild

class PlayerArgument(
    override var expectedType: KClass<*> = Player::class,
): CommandChild

class IntArgument(
    var staticCompletions: MutableList<Int> = mutableListOf(),
    override var expectedType: KClass<*> = Int::class,
): CommandChild

class DoubleArgument(
    val staticCompletions: MutableList<Double> = mutableListOf(),
    override var expectedType: KClass<*> = Double::class,
): CommandChild

class LongArgument(
    val staticCompletions: MutableList<Long> = mutableListOf(),
    override var expectedType: KClass<*> = Long::class,
): CommandChild

class UUIDArgument(
    override var expectedType: KClass<*> = UUID::class,
): CommandChild

class EnumArgument(
    val enumType: KClass<*>,
    override var expectedType: KClass<*> = String::class,
): CommandChild


class CommandChildData(
    val child: CommandChild,
    val optional: Boolean = false,
    var returnedValue: Any? = null,
    var expectedReturnValueType: KClass<*>
)

data class CommandExecutor(
    val player: Player? = null,
    val console: ConsoleExecutor? = null,
    val isPlayer: Boolean = player != null
)

class ConsoleExecutor() {
    fun sendMessage(message: String) {
        // bla bla only to console
    }
}