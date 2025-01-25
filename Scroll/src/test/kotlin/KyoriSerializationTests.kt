import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.serializers.KyoriToScrollSerializer.toScroll
import kotlin.test.Test
import kotlin.test.assertEquals

class KyoriSerializationTests {

    @Test
    fun testSimpleKyoriToScroll() {
        val input = net.kyori.adventure.text.Component.text("testing")
        val expected = Component(text = "testing")

        assertEquals(expected.toJson(), input.toScroll().toJson())
    }
}