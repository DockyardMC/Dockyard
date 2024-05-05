package io.github.dockyardmc.player

class ProfileProperty(val name: String, val value: String, val isSigned: Boolean, val signature: String?) {

}

class ProfilePropertyMap(val name: String, val properties: MutableList<ProfileProperty>) {

}