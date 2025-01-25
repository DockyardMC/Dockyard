import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.serializers.JsonToComponentSerializer
import io.github.dockyardmc.scroll.extensions.toComponent
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonSerializationTests {

    @Test
    fun testWrongOrder() {
        val input = "{\"color\":\"#FFAA00\",\"text\":\"Analysis is now complete.\"}"
        val component = JsonToComponentSerializer.serialize(input)
        val expected = "<gold>Analysis is now complete."

        assertEquals(expected.toComponent().toJson(), Component.compound(mutableListOf(component)).toJson())
    }

    @Test
    fun testComponentToJson() {
        val input = "<yellow>Osmanthus wine tastes <i>the same as <orange><bold>I remember<gray><i>... <yellow>But where are those who share <aqua><bold><u>the memory"
        val expected = "{\"extra\":[{\"text\":\"Osmanthus wine tastes \",\"color\":\"#FFFF55\"},{\"text\":\"the same as \",\"color\":\"#FFFF55\",\"italic\":true},{\"text\":\"I remember\",\"color\":\"#FFAA00\",\"bold\":true},{\"text\":\"... \",\"color\":\"#AAAAAA\",\"italic\":true},{\"text\":\"But where are those who share \",\"color\":\"#FFFF55\"},{\"text\":\"the memory\",\"color\":\"#55FFFF\",\"bold\":true,\"underlined\":true}],\"text\":\"\"}"

        assertEquals(expected, input.toComponent().toJson())
    }

    @Test
    fun testJsonToComponent() {
        val input = "{\"extra\":[{\"text\":\"Osmanthus wine tastes \",\"color\":\"#FFFF55\"},{\"text\":\"the same as \",\"color\":\"#FFFF55\",\"italic\":true},{\"text\":\"I remember\",\"color\":\"#FFAA00\",\"bold\":true},{\"text\":\"... \",\"color\":\"#AAAAAA\",\"italic\":true},{\"text\":\"But where are those who share \",\"color\":\"#FFFF55\"},{\"text\":\"the memory\",\"color\":\"#55FFFF\",\"bold\":true,\"underlined\":true}],\"text\":\"\"}"
        val expected = "<yellow>Osmanthus wine tastes <i>the same as <orange><bold>I remember<gray><i>... <yellow>But where are those who share <aqua><bold><u>the memory"

        assertEquals(expected.toComponent().toJson(), JsonToComponentSerializer.serialize(input).toJson())
    }
}