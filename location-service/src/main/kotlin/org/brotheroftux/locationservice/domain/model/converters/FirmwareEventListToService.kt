package org.brotheroftux.locationservice.domain.model.converters

import org.brotheroftux.locationservice.application.utils.bleAddrToString
import org.brotheroftux.locationservice.domain.model.firmware.ScanEventList
import org.brotheroftux.locationservice.domain.model.service.QueueEvent

fun ScanEventList.toQueueEventList(): List<QueueEvent> =
    events.map {
        QueueEvent(
            receiverAddress = addr.bleAddrToString(),
            beaconAddress = it.addr.bleAddrToString(),
            distance = it.distance,
            // TODO: This is wrong. Event timestamp should be on ScanEventDescriptor.
            eventTimestamp = ts.toULong()
        )
    }
