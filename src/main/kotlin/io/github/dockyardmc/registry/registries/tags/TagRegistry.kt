package io.github.dockyardmc.registry.registries.tags

import io.github.dockyardmc.protocol.packets.configurations.clientbound.Tag
import io.github.dockyardmc.registry.DataDrivenRegistry

abstract class TagRegistry : DataDrivenRegistry<Tag>()