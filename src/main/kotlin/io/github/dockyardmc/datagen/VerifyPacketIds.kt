package io.github.dockyardmc.datagen

import Packet
import WikiVGDataGenerator
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry

class VerifyPacketIds {

    val packets: MutableList<Packet>

    init {
        val data = WikiVGDataGenerator()
        packets = data.packets.toMutableList()
        getServerboundAnnotatedClasses()
    }

    fun getServerboundAnnotatedClasses() {
        val packageToScan = "io.github.dockyardmc"

        val reflections = org.reflections.Reflections(packageToScan)
        val annotatedClasses = reflections.getTypesAnnotatedWith(WikiVGEntry::class.java)

        annotatedClasses.forEach { loopClass ->
            val serverboundAnnotation = loopClass.getAnnotation(ServerboundPacketInfo::class.java) ?: return@forEach
            val wikivgAnnotation = loopClass.getAnnotation(WikiVGEntry::class.java)

            val id = serverboundAnnotation.id
            val header = wikivgAnnotation.header

            val packet = packets.firstOrNull { it.header == header } ?: return@forEach
            if(Integer.decode(packet.id) != id) {
                log("Packet ${loopClass.simpleName} does not have up-to-date id: $id should be ${Integer.decode(packet.id)}", LogType.WARNING)
            }
        }
        annotatedClasses.forEach { loopClass ->
            val clientboundPacketInfo = loopClass.getAnnotation(ClientboundPacketInfo::class.java) ?: return@forEach
            val wikivgAnnotation = loopClass.getAnnotation(WikiVGEntry::class.java)

            val id = clientboundPacketInfo.id
            val header = wikivgAnnotation.header


            val packet = packets.firstOrNull { it.header == header } ?: return@forEach
            if(Integer.decode(packet.id) != id) {
                log("Packet ${loopClass.simpleName} does not have up-to-date id: $id should be ${Integer.decode(packet.id)}", LogType.WARNING)
            }
        }
    }
}