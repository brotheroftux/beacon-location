package org.brotheroftux.locationservice.domain.repositories.impl

import org.brotheroftux.locationservice.application.utils.bleAddrToByteArray
import org.brotheroftux.locationservice.application.utils.bleAddrToString
import org.brotheroftux.locationservice.domain.db.connection.DbConnection
import org.brotheroftux.locationservice.domain.db.schemas.EventQueue
import org.brotheroftux.locationservice.domain.model.service.QueueEvent
import org.brotheroftux.locationservice.domain.repositories.EventQueueRepository
import org.jetbrains.exposed.sql.*

object EventQueueRepositoryImpl : EventQueueRepository {
    private fun rowToQueueEvent(row: ResultRow) = QueueEvent(
        receiverAddress = row[EventQueue.receiverAddress].bleAddrToString(),
        beaconAddress = row[EventQueue.beaconAddress].bleAddrToString(),
        distance = row[EventQueue.distance],
        eventTimestamp = row[EventQueue.eventTimestamp],
    )

    override suspend fun push(events: List<QueueEvent>) {
        DbConnection.dbQuery {
            EventQueue.batchInsert(events) {
                this[EventQueue.receiverAddress] = it.receiverAddress.bleAddrToByteArray()
                this[EventQueue.beaconAddress] = it.beaconAddress.bleAddrToByteArray()
                this[EventQueue.distance] = it.distance
                this[EventQueue.eventTimestamp] = it.eventTimestamp
            }
        }
    }
}
