package org.brotheroftux.locationservice.domain.repositories

import org.brotheroftux.locationservice.domain.model.service.QueueEvent

interface EventQueueRepository {
    suspend fun push(events: List<QueueEvent>)
    suspend fun poll(): List<QueueEvent>
}
