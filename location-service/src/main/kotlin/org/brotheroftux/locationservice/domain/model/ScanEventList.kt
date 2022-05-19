package org.brotheroftux.locationservice.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ScanEventList constructor(
    @ProtoNumber(1) val events: List<ScanEventDescriptor> = emptyList()
)
