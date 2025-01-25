package io.github.dockyardmc.spark

import io.github.dockyardmc.DockyardServer
import me.lucko.spark.common.platform.PlatformInfo

class SparkPlatformInfo: PlatformInfo {

    override fun getType(): PlatformInfo.Type {
        return PlatformInfo.Type.SERVER
    }

    override fun getName(): String {
        return "DockyardMC"
    }

    override fun getBrand(): String {
        return "DockyardMC"
    }

    override fun getVersion(): String {
        return DockyardServer.versionInfo.dockyardVersion
    }

    override fun getMinecraftVersion(): String {
        return DockyardServer.minecraftVersion.versionName
    }
}
