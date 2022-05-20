package org.brotheroftux.locationservice.application.dtos

@kotlinx.serialization.Serializable
data class UpdateReceiverCoordsDto(
    val posX: Double,
    val posY: Double,
)

