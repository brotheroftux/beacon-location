package org.brotheroftux.locationservice.domain.model.firmware

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class ScanEventList constructor(
    @ProtoNumber(1) val events: List<ScanEventDescriptor> = emptyList(),
    @ProtoNumber(2) val ts: Long,
    @ProtoNumber(3) val addr: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScanEventList

        if (events != other.events) return false
        if (ts != other.ts) return false
        if (!addr.contentEquals(other.addr)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = events.hashCode()
        result = 31 * result + ts.hashCode()
        result = 31 * result + addr.contentHashCode()
        return result
    }
}
