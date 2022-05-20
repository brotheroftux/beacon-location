package org.brotheroftux.locationservice.application.dtos.converters

import org.brotheroftux.locationservice.application.dtos.ReceiverCoordsDto
import org.brotheroftux.locationservice.domain.model.service.ReceiverConfig

fun ReceiverConfig.toDto() = ReceiverCoordsDto(receiverAddress = this.addr, posX = this.posX, posY = this.posY)
fun ReceiverCoordsDto.toReceiverConfig() =
    ReceiverConfig(addr = this.receiverAddress, posX = this.posX, posY = this.posY)
