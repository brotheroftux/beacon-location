package org.brotheroftux.locationservice.domain.model.service

data class QueueEvent(
    val receiverAddress: String,
    val beaconAddress: String,
    val distance: Double,
    val eventTimestamp: ULong,
)
