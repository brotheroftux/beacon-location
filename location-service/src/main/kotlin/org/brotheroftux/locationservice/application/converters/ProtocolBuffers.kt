@file:OptIn(ExperimentalSerializationApi::class)

package org.brotheroftux.locationservice.application.converters

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.util.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.*

class ProtocolBuffersConverter(private val protobufInstance: ProtoBuf = ProtoBuf) : ContentConverter {
    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any? {
        return withContext(Dispatchers.IO) {
            val byteArray = content.toByteArray()

            protobufInstance.decodeFromByteArray(
                byteArray,
                typeInfo.kotlinType
                    ?: throw TypeCastException("Couldn't get the object's KType")
            )
        }
    }

    override suspend fun serialize(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any
    ): OutgoingContent {
        val byteArray = protobufInstance.encodeToByteArray(
            value,
            typeInfo.kotlinType ?: throw TypeCastException("Couldn't get the object's KType")
        )

        return ByteArrayContent(byteArray, contentType.withCharsetIfNeeded(charset))
    }
}

fun ContentNegotiationConfig.protocolBuffers(
    contentType: ContentType = ContentType.Application.OctetStream,
    builderAction: ProtoBufBuilder.() -> Unit = {}
) {
    val converter = ProtocolBuffersConverter(ProtoBuf(builderAction = builderAction))

    register(contentType, converter)
}
