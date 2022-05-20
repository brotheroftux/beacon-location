package org.brotheroftux.locationservice.domain.repositories

import org.brotheroftux.locationservice.domain.model.service.ReceiverConfig

interface ReceiverConfigRepository {
    suspend fun allConfiguredReceiverCoords(): List<ReceiverConfig>
    suspend fun configuredReceiverCoordsByAddress(address: String): ReceiverConfig?
    suspend fun updateConfiguredReceiverCoords(newConfig: ReceiverConfig)
    suspend fun batchUpdateConfiguredReceiverCoords(updated: List<ReceiverConfig>)
}
