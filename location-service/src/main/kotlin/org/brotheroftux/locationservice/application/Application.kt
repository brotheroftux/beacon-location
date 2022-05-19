package org.brotheroftux.locationservice.application

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.brotheroftux.locationservice.application.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        configureSockets()
    }.start(wait = true)
}
