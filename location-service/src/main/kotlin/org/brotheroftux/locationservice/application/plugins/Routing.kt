package org.brotheroftux.locationservice.application.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.brotheroftux.locationservice.application.routes.configureEventSinkRoutes
import org.brotheroftux.locationservice.application.routes.configureReceiverConfigRoutes

fun Application.configureRouting() {
    routing {
        configureEventSinkRoutes()
        configureReceiverConfigRoutes()
    }
}
