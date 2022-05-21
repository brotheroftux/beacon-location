package org.brotheroftux.locationservice.application

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import org.brotheroftux.locationservice.application.plugins.configureRouting
import org.brotheroftux.locationservice.application.plugins.configureSerialization
import org.brotheroftux.locationservice.application.plugins.configureSockets
import org.brotheroftux.locationservice.application.services.queuemanager.launchEventQueueConsumer
import org.brotheroftux.locationservice.application.services.queuemanager.makeEventChannelProducer
import org.brotheroftux.locationservice.domain.db.connection.DbConnection

fun main(): Unit = runBlocking {
    val eventChannel = makeEventChannelProducer()
    launchEventQueueConsumer(eventChannel)

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        DbConnection.init()
        configureRouting()
        configureSerialization()
        configureSockets()
    }.start(wait = true)
}
