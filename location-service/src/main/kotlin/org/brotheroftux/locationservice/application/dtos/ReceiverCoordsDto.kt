package org.brotheroftux.locationservice.application.dtos

@kotlinx.serialization.Serializable
data class ReceiverCoordsDto(
    val receiverAddress: String,
    val posX: Double,
    val posY: Double,
)
