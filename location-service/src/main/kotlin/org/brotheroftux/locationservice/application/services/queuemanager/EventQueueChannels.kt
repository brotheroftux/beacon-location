package org.brotheroftux.locationservice.application.services.queuemanager

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.ticker
import org.brotheroftux.locationservice.domain.repositories.impl.EventQueueRepositoryImpl

const val tickerDelay: Long = 15 * 1000

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
fun CoroutineScope.makeEventChannelProducer() = produce (Dispatchers.Default) {
    val tickerChannel = ticker(delayMillis = tickerDelay, initialDelayMillis = 0)

    for (tick in tickerChannel) {
        this.send(EventQueueRepositoryImpl.poll())
    }
}
