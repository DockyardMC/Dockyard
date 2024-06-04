package io.github.dockyardmc.registry
import io.github.dockyardmc.utils.Resources
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
// THIS CLASS IS AUTO-GENERATED
// DATA FROM MINECRAFT 1.20.4
// https://github.com/DockyardMC/RegistryClassesGenerators

object Biomes {
    private val idToBiomeMap by lazy {
        val json = Json { ignoreUnknownKeys = true }
        val biomes = json.decodeFromString<BiomesSerializable>(Resources.getText("./data/biomes.json")).biomeList.value
        biomes.associateBy { it.id }
    }
    fun getBiomeById(id: Int): Biome {
        return idToBiomeMap[id] ?: error("Biome ID $id not found")
    }
    val BADLANDS = getBiomeById(0)
    val BAMBOO_JUNGLE = getBiomeById(1)
    val BASALT_DELTAS = getBiomeById(2)
    val BEACH = getBiomeById(3)
    val BIRCH_FOREST = getBiomeById(4)
    val CHERRY_GROVE = getBiomeById(5)
    val COLD_OCEAN = getBiomeById(6)
    val CRIMSON_FOREST = getBiomeById(7)
    val DARK_FOREST = getBiomeById(8)
    val DEEP_COLD_OCEAN = getBiomeById(9)
    val DEEP_DARK = getBiomeById(10)
    val DEEP_FROZEN_OCEAN = getBiomeById(11)
    val DEEP_LUKEWARM_OCEAN = getBiomeById(12)
    val DEEP_OCEAN = getBiomeById(13)
    val DESERT = getBiomeById(14)
    val DRIPSTONE_CAVES = getBiomeById(15)
    val END_BARRENS = getBiomeById(16)
    val END_HIGHLANDS = getBiomeById(17)
    val END_MIDLANDS = getBiomeById(18)
    val ERODED_BADLANDS = getBiomeById(19)
    val FLOWER_FOREST = getBiomeById(20)
    val FOREST = getBiomeById(21)
    val FROZEN_OCEAN = getBiomeById(22)
    val FROZEN_PEAKS = getBiomeById(23)
    val FROZEN_RIVER = getBiomeById(24)
    val GROVE = getBiomeById(25)
    val ICE_SPIKES = getBiomeById(26)
    val JAGGED_PEAKS = getBiomeById(27)
    val JUNGLE = getBiomeById(28)
    val LUKEWARM_OCEAN = getBiomeById(29)
    val LUSH_CAVES = getBiomeById(30)
    val MANGROVE_SWAMP = getBiomeById(31)
    val MEADOW = getBiomeById(32)
    val MUSHROOM_FIELDS = getBiomeById(33)
    val NETHER_WASTES = getBiomeById(34)
    val OCEAN = getBiomeById(35)
    val OLD_GROWTH_BIRCH_FOREST = getBiomeById(36)
    val OLD_GROWTH_PINE_TAIGA = getBiomeById(37)
    val OLD_GROWTH_SPRUCE_TAIGA = getBiomeById(38)
    val PLAINS = getBiomeById(39)
    val RIVER = getBiomeById(40)
    val SAVANNA = getBiomeById(41)
    val SAVANNA_PLATEAU = getBiomeById(42)
    val SMALL_END_ISLANDS = getBiomeById(43)
    val SNOWY_BEACH = getBiomeById(44)
    val SNOWY_PLAINS = getBiomeById(45)
    val SNOWY_SLOPES = getBiomeById(46)
    val SNOWY_TAIGA = getBiomeById(47)
    val SOUL_SAND_VALLEY = getBiomeById(48)
    val SPARSE_JUNGLE = getBiomeById(49)
    val STONY_PEAKS = getBiomeById(50)
    val STONY_SHORE = getBiomeById(51)
    val SUNFLOWER_PLAINS = getBiomeById(52)
    val SWAMP = getBiomeById(53)
    val TAIGA = getBiomeById(54)
    val THE_END = getBiomeById(55)
    val THE_VOID = getBiomeById(56)
    val WARM_OCEAN = getBiomeById(57)
    val WARPED_FOREST = getBiomeById(58)
    val WINDSWEPT_FOREST = getBiomeById(59)
    val WINDSWEPT_GRAVELLY_HILLS = getBiomeById(60)
    val WINDSWEPT_HILLS = getBiomeById(61)
    val WINDSWEPT_SAVANNA = getBiomeById(62)
    val WOODED_BADLANDS = getBiomeById(63)
}
@Serializable
data class BiomesSerializable(
    @SerialName("minecraft:worldgen/biome")
    val biomeList: BiomeObjects
)
@Serializable
data class BiomeObjects(
    val type: String,
    val value: MutableList<Biome>
)

@Serializable
data class Biome(
    val name: String,
    val id: Int
)
