package org.brotheroftux.locationservice.application.dtos

@kotlinx.serialization.Serializable
data class ReceiverCoordsListDto(
    val receivers: List<ReceiverCoordsDto>,
)
