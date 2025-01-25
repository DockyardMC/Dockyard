import io.github.dockyardmc.scroll.LegacyTextColor
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtraTests {

    @Test
    fun testTextColorOrdinal() {
        assertEquals(0, LegacyTextColor.BLACK.ordinal)
        assertEquals(1, LegacyTextColor.DARK_BLUE.ordinal)
        assertEquals(2, LegacyTextColor.GREEN.ordinal)
        assertEquals(3, LegacyTextColor.CYAN.ordinal)
        assertEquals(4, LegacyTextColor.DARK_RED.ordinal)
        assertEquals(5, LegacyTextColor.PURPLE.ordinal)
        assertEquals(6, LegacyTextColor.ORANGE.ordinal)
        assertEquals(7, LegacyTextColor.GRAY.ordinal)
        assertEquals(8, LegacyTextColor.DARK_GRAY.ordinal)
        assertEquals(9, LegacyTextColor.BLUE.ordinal)
        assertEquals(10, LegacyTextColor.LIME.ordinal)
        assertEquals(11, LegacyTextColor.AQUA.ordinal)
        assertEquals(12, LegacyTextColor.RED.ordinal)
        assertEquals(13, LegacyTextColor.PINK.ordinal)
        assertEquals(14, LegacyTextColor.YELLOW.ordinal)
        assertEquals(15, LegacyTextColor.WHITE.ordinal)
    }
}