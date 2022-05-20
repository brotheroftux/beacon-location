package org.brotheroftux.locationservice.application.routes

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.brotheroftux.locationservice.application.dtos.BatchUpdateReceiverCoordsRqDto
import org.brotheroftux.locationservice.application.dtos.ReceiverCoordsDto

import org.brotheroftux.locationservice.application.dtos.ReceiverCoordsListDto
import org.brotheroftux.locationservice.application.dtos.UpdateReceiverCoordsDto
import org.brotheroftux.locationservice.application.dtos.converters.toDto
import org.brotheroftux.locationservice.application.dtos.converters.toReceiverConfig
import org.brotheroftux.locationservice.domain.model.service.ReceiverConfig
import org.brotheroftux.locationservice.domain.repositories.impl.ReceiverConfigRepositoryImpl

@kotlinx.serialization.Serializable
@Resource("/configuration")
class ConfiguredReceiverCoords {
    @kotlinx.serialization.Serializable
    @Resource("{address}")
    class ByReceiverAddress(
        val parent: ConfiguredReceiverCoords = ConfiguredReceiverCoords(),
        val address: String,
    )
}

fun Route.configureReceiverConfigRoutes() = route("/receivers") {
    get<ConfiguredReceiverCoords> {
        val configuredReceiverCoords = ReceiverConfigRepositoryImpl.allConfiguredReceiverCoords()

        call.respond(ReceiverCoordsListDto(receivers = configuredReceiverCoords.map(ReceiverConfig::toDto)))
    }
    get<ConfiguredReceiverCoords.ByReceiverAddress> {
        val configuredReceiverCoords =
            ReceiverConfigRepositoryImpl.configuredReceiverCoordsByAddress(it.address) ?: return@get call.respond(
                HttpStatusCode.NotFound
            )

        call.respond(configuredReceiverCoords.toDto())
    }

    put<ConfiguredReceiverCoords> {
        call.receive<BatchUpdateReceiverCoordsRqDto>().updatedCoordsList.map(ReceiverCoordsDto::toReceiverConfig).let {
            ReceiverConfigRepositoryImpl.batchUpdateConfiguredReceiverCoords(it)
        }

        call.respond(HttpStatusCode.OK)
    }
    put<ConfiguredReceiverCoords.ByReceiverAddress> { resource ->
        call.receive<UpdateReceiverCoordsDto>()
            .let { ReceiverConfig(addr = resource.address, posX = it.posX, posY = it.posY) }
            .let { ReceiverConfigRepositoryImpl.updateConfiguredReceiverCoords(it) }

        call.respond(HttpStatusCode.OK)
    }
}
