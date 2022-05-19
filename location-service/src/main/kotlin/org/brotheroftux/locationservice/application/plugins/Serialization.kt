package org.brotheroftux.locationservice.application.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import kotlinx.serialization.ExperimentalSerializationApi
import org.brotheroftux.locationservice.application.converters.protocolBuffers

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
        protocolBuffers()
    }
}
