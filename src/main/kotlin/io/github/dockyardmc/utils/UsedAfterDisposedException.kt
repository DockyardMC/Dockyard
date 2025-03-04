package io.github.dockyardmc.utils

import java.lang.Exception

class UsedAfterDisposedException(klass: Any): Exception() {
    override val message: String = "${klass::class.simpleName} is already disposed"
}