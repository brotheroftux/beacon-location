package org.brotheroftux.locationservice.application.converters

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.serializer
import kotlin.reflect.KType

fun BinaryFormat.decodeFromByteArray(bytes: ByteArray, type: KType): Any? =
    decodeFromByteArray(serializersModule.serializer(type), bytes)

fun BinaryFormat.encodeToByteArray(value: Any, type: KType): ByteArray =
    encodeToByteArray(serializersModule.serializer(type), value)
