@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.schema.ProtoBufSchemaGenerator
import org.brotheroftux.locationservice.domain.model.*
import java.io.File

val descriptors = listOf(ScanEventDescriptor.serializer().descriptor, ScanEventList.serializer().descriptor)
val schema = ProtoBufSchemaGenerator.generateSchemaText(descriptors)

var dist = System.getenv("DIST")
var file = File(dist)

file.bufferedWriter().use { it.write(schema) }

println("Wrote schema to ${file.absolutePath}")
