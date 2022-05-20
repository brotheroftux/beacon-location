package org.brotheroftux.locationservice.application.routes

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.brotheroftux.locationservice.application.utils.bleAddrToString
import org.brotheroftux.locationservice.domain.model.converters.toQueueEventList
import org.brotheroftux.locationservice.domain.model.firmware.ScanEventList
import org.brotheroftux.locationservice.domain.repositories.impl.EventQueueRepositoryImpl

@kotlinx.serialization.Serializable
@Resource("/events")
class EventSink

fun Route.configureEventSinkRoutes() = put<EventSink> {
    call.receive<ScanEventList>().let {
        call.application.environment.log.info("Receiver with address = ${it.addr.bleAddrToString()} pushed ${it.events.size} event(s)")
        EventQueueRepositoryImpl.push(it.toQueueEventList())
    }

    call.respond(HttpStatusCode.OK)
}
