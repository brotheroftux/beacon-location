package org.brotheroftux.locationservice.application.dtos

@kotlinx.serialization.Serializable
data class BatchUpdateReceiverCoordsRqDto(
    val updatedCoordsList: List<ReceiverCoordsDto>,
)
