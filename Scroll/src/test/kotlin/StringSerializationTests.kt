import io.github.dockyardmc.scroll.*
import io.github.dockyardmc.scroll.extensions.scrollSanitized
import io.github.dockyardmc.scroll.extensions.toComponent
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class StringSerializationTests {

    @Test
    fun testBasicSerialization() {
        val input = "<red>This text should be red"
        val expected = Component.compound(mutableListOf(
            Component(text = "This text should be red", color = "#FF5555")
        ))

        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testFontResettingColor() {
        val input = "<#ff54aa><font:ranyth>testing testing <r>testing2"
        val expected = Component.compound(mutableListOf(
            Component(text = "testing testing ", color = "#ff54aa", font = "ranyth"),
            Component(text = "testing2")
        ))
        assertEquals(expected.toJson(), input.toComponent().toJson())
    }

    @Test
    fun testCustomColorSerialization() {
        val input = "<#ff54aa>This text should be cute pink color :3"
        val expected = Component.compound(mutableListOf(
            Component(text ="This text should be cute pink color :3", color = "#ff54aa")
        ))

        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testMultipleSerializationComponents() {
        val input = "<red>This <orange>text <yellow>should <lime>be <aqua>multi <pink>colored"
        val expected = Component.compound(mutableListOf(
            Component(text ="This ", color = ScrollUtil.colorTags["<red>"]),
            Component(text ="text ", color = ScrollUtil.colorTags["<orange>"]),
            Component(text ="should ", color = ScrollUtil.colorTags["<yellow>"]),
            Component(text ="be ", color = ScrollUtil.colorTags["<lime>"]),
            Component(text ="multi ", color = ScrollUtil.colorTags["<aqua>"]),
            Component(text ="colored", color = ScrollUtil.colorTags["<pink>"]),
        ))
        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testClosingTags() {
        val input = "<bold><underlined><italics><strikethrough><obfuscated>test </obfuscated>test </strikethrough>test </italics>test </underlined>test </bold>test"
        val expected = Component.compound(mutableListOf(
            Component(text ="test ", bold = true, underlined = true, italic = true, strikethrough = true, obfuscated = true),
            Component(text ="test ", bold = true, underlined = true, italic = true, strikethrough = true),
            Component(text ="test ", bold = true, underlined = true, italic = true),
            Component(text ="test ", bold = true, underlined = true),
            Component(text ="test ", bold = true),
            Component(text ="test"),
        ))
        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testClosingTagsMini() {
        val input = "<b><u><i><s><o>test </o>test </s>test </i>test </u>test </b>test"
        val expected = Component.compound(mutableListOf(
            Component(text ="test ", bold = true, underlined = true, italic = true, strikethrough = true, obfuscated = true),
            Component(text ="test ", bold = true, underlined = true, italic = true, strikethrough = true),
            Component(text ="test ", bold = true, underlined = true, italic = true),
            Component(text ="test ", bold = true, underlined = true),
            Component(text ="test ", bold = true),
            Component(text ="test"),
        ))
        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testFormatting() {
        val inputToExpectedOutput = mutableMapOf<String, Component>(
            "<b>test" to Component(text ="test", bold = true),
            "<bold>test" to Component(text ="test", bold = true),
            "<i>test" to Component(text ="test", italic = true),
            "<italic>test" to Component(text ="test", italic = true),
            "<u>test" to Component(text ="test", underlined = true),
            "<underline>test" to Component(text ="test", underlined = true),
            "<o>test" to Component(text ="test", obfuscated = true),
            "<obfuscated>test" to Component(text ="test", obfuscated = true),
            "<bold><italic><underline><obfuscated>test" to Component(text ="test",
                bold = true,
                italic = true,
                underlined = true,
                obfuscated = true,
                ),
            "<b><i><u><o>test" to Component(text ="test",
                bold = true,
                italic = true,
                underlined = true,
                obfuscated = true,
            ),
            "<bold><italic><underline><obfuscated><reset>test" to Component(text ="test"),
            "<b><i><u><o><r>test" to Component(text ="test"),
        )

        inputToExpectedOutput.forEach { assertEquals(Scroll.parse(it.key).extra!![0].toJson(), it.value.toJson()) }
    }

    @Test
    fun testKeybind() {
        val input = "<keybind:key.jump>"
        val expected = Component.compound(mutableListOf(
            Component(keybind = "key.jump")
        ))

        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testBritish() {
        val input = "<aqua>bo'ohw'o'wo'er"
        val expected = Component.compound(mutableListOf(
            Component(text = "bo'ohw'o'wo'er", color = "#55FFFF")
        ))
        assertEquals(expected.toJson(), input.toComponent().toJson())
    }

    @Test
    fun testClick() {
        val input = "<click:run_command:/seed>Click for Seed"
        val expected = Component.compound(mutableListOf(
            Component(text = "Click for Seed", clickEvent = ClickEvent(ClickAction.RUN_COMMAND, "/seed"))
        ))

        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testHover() {
        val input = "<hover:show_text:'<red><bold><i>bucket o' fish'>hover over meeeee <r>do not hover over me"
        val expected = Component.compound(mutableListOf(
            Component(text = "hover over meeeee ", hoverEvent = HoverEvent(HoverAction.SHOW_TEXT, "<red><bold><i>bucket o' fish".toComponent())),
            Component(text = "do not hover over me")
        ))

        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testClickWithLink() {
        val input = "<click:open_url:'https://lukynka.cloud'><aqua><underline>https://lukynka.cloud"

        val expected = Component.compound(mutableListOf(
            Component(
                text = "https://lukynka.cloud",
                clickEvent = ClickEvent(ClickAction.OPEN_URL, "https://lukynka.cloud"),
                color = "#55FFFF",
                underlined = true
            ),
        ))

        assertEquals(expected.toJson(), input.toComponent().toJson())
    }

//    @Test
//    fun testGay() {
//        val input = "<rainbow>gayyyy"
//        val expected = Component.compound(mutableListOf(
//            Component(text ="g", color = "#FF0000"),
//            Component(text ="a", color = "#FF4D00"),
//            Component(text ="y", color = "#FF9900"),
//            Component(text ="y", color = "#FFE600"),
//            Component(text ="y", color = "#CCFF00"),
//            Component(text ="y", color = "#80FF00"),
//        ))
//        assertEquals(expected.toJson(), ScrollManager.toComponent(input).toJson())
//    }

    @Test
    fun testTranslation() {
        val input = "<translate:advancements.husbandry.safely_harvest_honey.description>"
        val expected = Component.compound(mutableListOf(
            Component(translate = "advancements.husbandry.safely_harvest_honey.description")
        ))

        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testEscapes() {
        val input = "Missing argument \"mode\": /gamemode \\<mode> \\<bold>test</bold>"
        val expected = Component.compound(mutableListOf(
            Component(text ="Missing argument \"mode\": /gamemode <mode> <bold>test")
        ))

        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testScrollSanitization() {
        val input = "Player123: Did you know you can type in <red>red and <bold>bold</bold><yellow> and <rainbow>rainboooowww"
        val expected = Component.compound(mutableListOf(
            Component(text ="Player123: Did you know you can type in <red>red and <bold>bold</bold><yellow> and <rainbow>rainboooowww")
        ))
        assertEquals(expected.toJson(), Scroll.parse(input.scrollSanitized()).toJson())
    }

    @Test
    fun testBigBoi() {
        val input = "<white><i>hello <yellow><b>hello <aqua><u>hello <dark_red><o>hello<reset>! Welcome, <#ff54aa>LukynkaCZE<r>! <keybind:key.jump> to jump. <translate:advMode.mode> should be advMode.mode"
        val expected = Component.compound(mutableListOf(
            Component(
                text = "hello ",
                color = ScrollUtil.colorTags["<white>"],
                italic = true
            ),
            Component(
                text = "hello ",
                color = ScrollUtil.colorTags["<yellow>"],
                bold = true
            ),
            Component(
                text = "hello ",
                color = ScrollUtil.colorTags["<aqua>"],
                underlined = true
            ),
            Component(
                text = "hello",
                color = ScrollUtil.colorTags["<dark_red>"],
                obfuscated = true
            ),
            Component(
                text = "! Welcome, "
            ),
            Component(
                text = "LukynkaCZE",
                color = "#ff54aa"
            ),
            Component(
                text = "! ",
            ),
            Component(
                keybind = "key.jump"
            ),
            Component(
                text = " to jump. "
            ),
            Component(
                translate = "advMode.mode"
            ),
            Component(
                text = " should be advMode.mode"
            )
            ))

        assertEquals(expected.toJson(), Scroll.parse(input).toJson())
    }

    @Test
    fun testStyleStripping() {
        val input = "<yellow>hello there! <rainbow><i>LukynkaCZE<yellow>! <lime><u>How are you doing this fine evening?<r>"
        val expected = "hello there! <rainbow>LukynkaCZE! How are you doing this fine evening?"

        assertEquals(expected, input.toComponent().stripStyling())
    }
}