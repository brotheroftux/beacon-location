package org.brotheroftux.locationservice.application.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.brotheroftux.locationservice.application.utils.bleAddrToString
import org.brotheroftux.locationservice.domain.model.converters.toQueueEventList
import org.brotheroftux.locationservice.domain.model.firmware.ScanEventList
import org.brotheroftux.locationservice.domain.repositories.impl.EventQueueRepositoryImpl

fun Route.configureEventSinkRoutes() = put<ScanEventList>("/events") {
    println("Receiver with address = ${it.addr.bleAddrToString()} pushed events = $it")

    val eventsToPush = it.toQueueEventList()

    EventQueueRepositoryImpl.push(eventsToPush)
    call.respond(HttpStatusCode.OK)
}
