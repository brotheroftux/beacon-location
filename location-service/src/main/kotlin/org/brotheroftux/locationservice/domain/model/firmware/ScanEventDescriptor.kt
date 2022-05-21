package org.brotheroftux.locationservice.domain.model.firmware

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.*

@Serializable
data class ScanEventDescriptor @OptIn(ExperimentalSerializationApi::class) constructor(
    @ProtoNumber(1) val addr: ByteArray,
    @ProtoNumber(2) val ts: Long,
    @ProtoNumber(3) val distance: Double,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScanEventDescriptor

        if (!addr.contentEquals(other.addr)) return false
        if (ts != other.ts) return false
        if (distance != other.distance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = addr.contentHashCode()
        result = 31 * result + ts.hashCode()
        result = 31 * result + distance.hashCode()
        return result
    }
}
