package org.brotheroftux.locationservice.domain.repositories

import org.brotheroftux.locationservice.domain.model.service.BeaconState

interface BeaconStateRepository {
    suspend fun store(beaconStates: List<BeaconState>)
}
