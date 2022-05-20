package org.brotheroftux.locationservice.domain.db.schemas

import org.jetbrains.exposed.dao.id.IntIdTable

@OptIn(ExperimentalUnsignedTypes::class)
object EventQueue : IntIdTable() {
    val receiverAddress = binary("receiver_addr", 6)
    val beaconAddress = binary("beacon_addr", 6)
    val distance = double("distance")
    val eventTimestamp = ulong("ts")
}
