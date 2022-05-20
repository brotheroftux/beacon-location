package org.brotheroftux.locationservice.domain.db.schemas

import org.jetbrains.exposed.dao.id.IntIdTable

@OptIn(ExperimentalUnsignedTypes::class)
object BeaconCoords : IntIdTable() {
    val timestamp = ulong("ts")
    val beaconAddress = binary("addr", 6)
    val posX = double("x")
    val posY = double("y")

    init {
        index(true, timestamp, beaconAddress)
    }
}
