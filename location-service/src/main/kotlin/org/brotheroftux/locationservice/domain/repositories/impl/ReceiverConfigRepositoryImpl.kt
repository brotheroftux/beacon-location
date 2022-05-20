package org.brotheroftux.locationservice.domain.repositories.impl

import org.brotheroftux.locationservice.application.utils.bleAddrToByteArray
import org.brotheroftux.locationservice.application.utils.bleAddrToString
import org.brotheroftux.locationservice.domain.db.connection.DbConnection
import org.brotheroftux.locationservice.domain.db.schemas.ReceiverCoords
import org.brotheroftux.locationservice.domain.model.service.ReceiverConfig
import org.brotheroftux.locationservice.domain.repositories.ReceiverConfigRepository
import org.jetbrains.exposed.sql.*

object ReceiverConfigRepositoryImpl : ReceiverConfigRepository {
    private fun rowToReceiverConfig(row: ResultRow) = ReceiverConfig(
        addr = row[ReceiverCoords.addr].bleAddrToString(),
        posX = row[ReceiverCoords.posX],
        posY = row[ReceiverCoords.posY],
    )

    override suspend fun allConfiguredReceiverCoords(): List<ReceiverConfig> = DbConnection.dbQuery {
        ReceiverCoords.selectAll().map(::rowToReceiverConfig)
    }

    override suspend fun configuredReceiverCoordsByAddress(address: String): ReceiverConfig? = DbConnection.dbQuery {
        ReceiverCoords.select { ReceiverCoords.addr eq address.bleAddrToByteArray() }.map(::rowToReceiverConfig)
            .singleOrNull()
    }

    override suspend fun updateConfiguredReceiverCoords(newConfig: ReceiverConfig): Unit = DbConnection.dbQuery {
        ReceiverCoords.update({ ReceiverCoords.addr eq newConfig.addr.bleAddrToByteArray() }) {
            it[posX] = newConfig.posX
            it[posY] = newConfig.posY
        }
    }

    override suspend fun batchUpdateConfiguredReceiverCoords(updated: List<ReceiverConfig>) {
        DbConnection.dbQuery {
            ReceiverCoords.batchReplace(updated) {
                this[ReceiverCoords.addr] = it.addr.bleAddrToByteArray()
                this[ReceiverCoords.posX] = it.posX
                this[ReceiverCoords.posY] = it.posY
            }
        }
    }
}
