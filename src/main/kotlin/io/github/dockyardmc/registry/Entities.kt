package io.github.dockyardmc.registry
import io.github.dockyardmc.utils.Resources
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.Vector3f
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// THIS CLASS IS AUTO-GENERATED
// DATA FROM MINECRAFT 1.20.4
// https://github.com/DockyardMC/RegistryClassesGenerators

object Entities {
    private val idToEntityMap by lazy {
        val json = Json { ignoreUnknownKeys = true }
        val entities = json.decodeFromString<List<EntityType>>(Resources.getText("data/entities.json"))
        entities.associateBy { it.id }
    }
    fun getEntityById(id: Int): EntityType {
        return idToEntityMap[id] ?: error("Entity ID $id not found")
    }
    val ALLAY = getEntityById(0)
    val AREA_EFFECT_CLOUD = getEntityById(1)
    val ARMOR_STAND = getEntityById(2)
    val ARROW = getEntityById(3)
    val AXOLOTL = getEntityById(4)
    val BAT = getEntityById(5)
    val BEE = getEntityById(6)
    val BLAZE = getEntityById(7)
    val BLOCK_DISPLAY = getEntityById(8)
    val BOAT = getEntityById(9)
    val BREEZE = getEntityById(10)
    val CAMEL = getEntityById(11)
    val CAT = getEntityById(12)
    val CAVE_SPIDER = getEntityById(13)
    val CHEST_BOAT = getEntityById(14)
    val CHEST_MINECART = getEntityById(15)
    val CHICKEN = getEntityById(16)
    val COD = getEntityById(17)
    val COMMAND_BLOCK_MINECART = getEntityById(18)
    val COW = getEntityById(19)
    val CREEPER = getEntityById(20)
    val DOLPHIN = getEntityById(21)
    val DONKEY = getEntityById(22)
    val DRAGON_FIREBALL = getEntityById(23)
    val DROWNED = getEntityById(24)
    val EGG = getEntityById(25)
    val ELDER_GUARDIAN = getEntityById(26)
    val END_CRYSTAL = getEntityById(27)
    val ENDER_DRAGON = getEntityById(28)
    val ENDER_PEARL = getEntityById(29)
    val ENDERMAN = getEntityById(30)
    val ENDERMITE = getEntityById(31)
    val EVOKER = getEntityById(32)
    val EVOKER_FANGS = getEntityById(33)
    val EXPERIENCE_BOTTLE = getEntityById(34)
    val EXPERIENCE_ORB = getEntityById(35)
    val EYE_OF_ENDER = getEntityById(36)
    val FALLING_BLOCK = getEntityById(37)
    val FIREWORK_ROCKET = getEntityById(38)
    val FOX = getEntityById(39)
    val FROG = getEntityById(40)
    val FURNACE_MINECART = getEntityById(41)
    val GHAST = getEntityById(42)
    val GIANT = getEntityById(43)
    val GLOW_ITEM_FRAME = getEntityById(44)
    val GLOW_SQUID = getEntityById(45)
    val GOAT = getEntityById(46)
    val GUARDIAN = getEntityById(47)
    val HOGLIN = getEntityById(48)
    val HOPPER_MINECART = getEntityById(49)
    val HORSE = getEntityById(50)
    val HUSK = getEntityById(51)
    val ILLUSIONER = getEntityById(52)
    val INTERACTION = getEntityById(53)
    val IRON_GOLEM = getEntityById(54)
    val ITEM = getEntityById(55)
    val ITEM_DISPLAY = getEntityById(56)
    val ITEM_FRAME = getEntityById(57)
    val FIREBALL = getEntityById(58)
    val LEASH_KNOT = getEntityById(59)
    val LIGHTNING_BOLT = getEntityById(60)
    val LLAMA = getEntityById(61)
    val LLAMA_SPIT = getEntityById(62)
    val MAGMA_CUBE = getEntityById(63)
    val MARKER = getEntityById(64)
    val MINECART = getEntityById(65)
    val MOOSHROOM = getEntityById(66)
    val MULE = getEntityById(67)
    val OCELOT = getEntityById(68)
    val PAINTING = getEntityById(69)
    val PANDA = getEntityById(70)
    val PARROT = getEntityById(71)
    val PHANTOM = getEntityById(72)
    val PIG = getEntityById(73)
    val PIGLIN = getEntityById(74)
    val PIGLIN_BRUTE = getEntityById(75)
    val PILLAGER = getEntityById(76)
    val POLAR_BEAR = getEntityById(77)
    val POTION = getEntityById(78)
    val PUFFERFISH = getEntityById(79)
    val RABBIT = getEntityById(80)
    val RAVAGER = getEntityById(81)
    val SALMON = getEntityById(82)
    val SHEEP = getEntityById(83)
    val SHULKER = getEntityById(84)
    val SHULKER_BULLET = getEntityById(85)
    val SILVERFISH = getEntityById(86)
    val SKELETON = getEntityById(87)
    val SKELETON_HORSE = getEntityById(88)
    val SLIME = getEntityById(89)
    val SMALL_FIREBALL = getEntityById(90)
    val SNIFFER = getEntityById(91)
    val SNOW_GOLEM = getEntityById(92)
    val SNOWBALL = getEntityById(93)
    val SPAWNER_MINECART = getEntityById(94)
    val SPECTRAL_ARROW = getEntityById(95)
    val SPIDER = getEntityById(96)
    val SQUID = getEntityById(97)
    val STRAY = getEntityById(98)
    val STRIDER = getEntityById(99)
    val TADPOLE = getEntityById(100)
    val TEXT_DISPLAY = getEntityById(101)
    val TNT = getEntityById(102)
    val TNT_MINECART = getEntityById(103)
    val TRADER_LLAMA = getEntityById(104)
    val TRIDENT = getEntityById(105)
    val TROPICAL_FISH = getEntityById(106)
    val TURTLE = getEntityById(107)
    val VEX = getEntityById(108)
    val VILLAGER = getEntityById(109)
    val VINDICATOR = getEntityById(110)
    val WANDERING_TRADER = getEntityById(111)
    val WARDEN = getEntityById(112)
    val WIND_CHARGE = getEntityById(113)
    val WITCH = getEntityById(114)
    val WITHER = getEntityById(115)
    val WITHER_SKELETON = getEntityById(116)
    val WITHER_SKULL = getEntityById(117)
    val WOLF = getEntityById(118)
    val ZOGLIN = getEntityById(119)
    val ZOMBIE = getEntityById(120)
    val ZOMBIE_HORSE = getEntityById(121)
    val ZOMBIE_VILLAGER = getEntityById(122)
    val ZOMBIFIED_PIGLIN = getEntityById(123)
    val PLAYER = getEntityById(124)
    val FISHING_BOBBER = getEntityById(125)
}
@Serializable
data class EntityType(
    val id: Int,
    @SerialName("displayName")
    val name: String,
    @SerialName("name")
    val namespace: String,
    val width: Float,
    val height: Float,
    val type: String,
    val category: String,
    val metadataKeys: MutableList<String>,
)