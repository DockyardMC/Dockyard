import io.github.dockyardmc.scroll.ScrollUtil
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.toComponent
import org.jglrxavpok.hephaistos.nbt.NBT
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NbtSerializationTests {

    @Test
    fun nbtToComponentTest(){
        val nbt = NBT.Compound {
            it.put("hoverEvent", NBT.Compound { hover ->
                hover.put("action", "show_text")
                hover.put("contents", NBT.Compound { hoverIn ->
                    hoverIn.put("extra", NBT.Compound {
                        it.put("extra", NBT.Compound { extrahover ->
                            extrahover.put("text", "Click to open the store URL")
                            extrahover.put("color", ScrollUtil.colorTags["<yellow>"])
                        })
                        it.put("text", "")
                    })

                })
            })
            it.put("text", "CLICK HERE")
            it.put("bold", true)
            it.put("clickEvent", NBT.Compound { click ->
                click.put("action", "open_url")
                click.put("value", "https://store.mccisland.net")
            })
            it.put("underline", true)
            it.put("color", ScrollUtil.colorTags["<lime>"])
        }
        val expected = "<lime><b><u><hover:show_text:'<yellow>Click to open the store URL'><click:open_url:https://store.mccisland.net>CLICK HERE".toComponent()
        val final = NBT.Compound {
            it.put("extra", mutableListOf(nbt))
            it.put("text", "")
        }
        assertEquals(expected.getAllComponents().size, final.toComponent().getAllComponents().size)
    }


    @Test
    fun hoverAndClickEventTest() {
        val input = "<lime><b><u><hover|'<yellow>Click to open the store URL'><click|open_url|https://store.mccisland.net>CLICK HERE"
        val expected = mutableListOf(
            NBT.Compound {
                it.put("hoverEvent", NBT.Compound { hover ->
                    hover.put("action", "show_text")
                    hover.put("contents", NBT.Compound { hoverIn ->
                        hoverIn.put("extra", NBT.Compound {
                            it.put("extra", NBT.Compound { extrahover ->
                                extrahover.put("text", "Click to open the store URL")
                                extrahover.put("color", ScrollUtil.colorTags["<yellow>"])
                            })
                            it.put("text", "")
                        })

                    })
                })
                it.put("text", "CLICK HERE")
                it.put("bold", true)
                it.put("clickEvent", NBT.Compound { click ->
                    click.put("action", "open_url")
                    click.put("value", "https://store.mccisland.net")
                })
                it.put("underline", true)
                it.put("color", ScrollUtil.colorTags["<lime>"])
            }
        )
        val final = NBT.Compound {
            it.put("extra", expected)
            it.put("text", "")
        }

        val nbt = input.toComponent().toNBT()
        assertEquals(final.size, nbt.size)
        final.keys.forEach { assertTrue(nbt.keys.contains(it)) }
    }

    @Test
    fun testStringToNbt() {
        val input = "<#ff54aa>omg haiiii bestie AsoDesu_ <pink><b>:3<r> <keybind:key.jump> to jump. <translate:advMode.mode> should be advMode.mode"
        val expected = mutableListOf(
            NBT.Compound {
                it.put("text", "omg haiiii bestie AsoDesu_ ")
                it.put("color", "#ff54aa")
            },
            NBT.Compound {
                it.put("color", ScrollUtil.colorTags["<pink>"])
                it.put("bold", true)
                it.put("text", ":3")
            },
            NBT.Compound {
                it.put("text", " ")
            },
            NBT.Compound {
                it.put("keybind", "key.jump")
            },
            NBT.Compound {
                it.put("text", " to jump. ")
            },
            NBT.Compound {
                it.put("translate", "advMode.mode")
            },
            NBT.Compound {
                it.put("text", " should be advMode.mode")
            })

        assertEquals(
            NBT.Compound {
                it.put("extra", expected)
                it.put("text", "")
            }.toSNBT(),
            input.toComponent().toNBT().toSNBT())
    }
}