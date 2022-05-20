package org.brotheroftux.locationservice.domain.model.service

data class BeaconState(
    val timestamp: ULong,
    val beaconAddress: String,
    val posX: Double,
    val posY: Double,
)
