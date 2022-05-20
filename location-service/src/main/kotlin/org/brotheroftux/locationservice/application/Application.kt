package org.brotheroftux.locationservice.application

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.brotheroftux.locationservice.application.plugins.*
import org.brotheroftux.locationservice.domain.db.connection.DbConnection

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        DbConnection.init()
        configureRouting()
        configureSerialization()
        configureSockets()
    }.start(wait = true)
}
