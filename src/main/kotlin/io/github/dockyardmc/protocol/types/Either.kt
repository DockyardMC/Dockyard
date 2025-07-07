package io.github.dockyardmc.protocol.types

import io.netty.buffer.ByteBuf


interface Either<L, R> {

    data class Left<L, R>(val value: L) : Either<L, R>

    data class Right<L, R>(val value: R) : Either<L, R>

    companion object {
        fun <L, R> left(value: L): Either<L, R> {
            return Left(value)
        }

        fun <L, R> right(value: R): Either<L, R> {
            return Right(value)
        }
    }
}

inline fun <L, R> ByteBuf.readEither(leftReader: (ByteBuf) -> L, rightReader: (ByteBuf) -> R): Either<L, R> {
    return if (this.readBoolean()) {
        Either.left(leftReader.invoke(this))
    } else {
        Either.right(rightReader.invoke(this))
    }
}

inline fun <L, R> ByteBuf.writeEither(either: Either<L, R>, leftWriter: (ByteBuf, L) -> Unit, rightWriter: (ByteBuf, R) -> Unit) {
    when (either) {
        is Either.Left -> {
            this.writeBoolean(true)
            leftWriter.invoke(this, either.value)
        }

        is Either.Right -> {
            this.writeBoolean(false)
            rightWriter.invoke(this, either.value)
        }
    }
}