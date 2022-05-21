package org.brotheroftux.locationservice.domain.repositories.impl

import org.brotheroftux.locationservice.application.utils.bleAddrToByteArray
import org.brotheroftux.locationservice.domain.db.connection.DbConnection
import org.brotheroftux.locationservice.domain.db.schemas.BeaconCoords
import org.brotheroftux.locationservice.domain.model.service.BeaconState
import org.brotheroftux.locationservice.domain.repositories.BeaconStateRepository
import org.jetbrains.exposed.sql.batchInsert

object BeaconStateRepositoryImpl : BeaconStateRepository {
    override suspend fun store(beaconStates: List<BeaconState>): Unit = DbConnection.dbQuery {
        BeaconCoords.batchInsert(beaconStates) {
            this[BeaconCoords.beaconAddress] = it.beaconAddress.bleAddrToByteArray()
            this[BeaconCoords.posX] = it.posX
            this[BeaconCoords.posY] = it.posY
            this[BeaconCoords.timestamp] = it.timestamp
        }
    }
}
