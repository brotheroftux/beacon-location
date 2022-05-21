package org.brotheroftux.locationservice.application.services.queuemanager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.brotheroftux.locationservice.application.services.multilateration.multilaterateBeaconStates
import org.brotheroftux.locationservice.domain.db.connection.DbConnection
import org.brotheroftux.locationservice.domain.model.service.QueueEvent
import org.brotheroftux.locationservice.domain.repositories.impl.BeaconStateRepositoryImpl
import org.brotheroftux.locationservice.domain.repositories.impl.ReceiverConfigRepositoryImpl

fun CoroutineScope.launchEventQueueConsumer(channel: ReceiveChannel<List<QueueEvent>>) =
    launch(Dispatchers.Default) {
        channel.consumeEach { events ->
            multilaterateBeaconStates(
                events,
                events.getReceiverSetup()
            ).also { BeaconStateRepositoryImpl.store(it) }
        }
    }

suspend fun List<QueueEvent>.getReceiverSetup() = DbConnection.dbQuery {
    distinctBy { it.receiverAddress }.map { it.receiverAddress }
        .let { ReceiverConfigRepositoryImpl.configuredReceiversByAddresses(it) }

}
