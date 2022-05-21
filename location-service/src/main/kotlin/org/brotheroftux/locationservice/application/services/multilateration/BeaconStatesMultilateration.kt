package org.brotheroftux.locationservice.application.services.multilateration

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver
import com.lemmingapex.trilateration.TrilaterationFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer
import org.brotheroftux.locationservice.domain.model.service.BeaconState
import org.brotheroftux.locationservice.domain.model.service.QueueEvent
import org.brotheroftux.locationservice.domain.model.service.ReceiverConfig

data class NormalizedQueueEvent(
    val beaconAddress: String,
    val receiverPosX: Double,
    val receiverPosY: Double,
    val distance: Double,
    val eventTimestamp: ULong,
)

data class MultilaterationResult(
    val posX: Double,
    val posY: Double,
)

private const val minEntriesToTrilaterate = 3

suspend fun multilaterateBeaconStates(
    events: List<QueueEvent>,
    receiverSetup: List<ReceiverConfig>,
): List<BeaconState> =
    coroutineScope {
        val receiverCoordsMap = receiverSetup.associateBy { it.addr }
        val beaconToEvents =
            events
                .filter { it.distance > 0 }
                .map { it.normalize(receiverCoordsMap) }
                .groupBy { it.beaconAddress }
                .filter { it.value.size >= minEntriesToTrilaterate }

        val deferreds = beaconToEvents.map { (k, v) ->
            async(context = Dispatchers.Default) {
                multilaterateSingleBeacon(v).let {
                    BeaconState(
                        beaconAddress = k,
                        timestamp = v.getBeaconStateTimestamp(),
                        posX = it.posX,
                        posY = it.posY
                    )
                }
            }
        }

        deferreds.awaitAll()
    }


private fun multilaterateSingleBeacon(
    beaconEvents: List<NormalizedQueueEvent>,
): MultilaterationResult {
    val positions = beaconEvents.map { doubleArrayOf(it.receiverPosX, it.receiverPosY) }.toTypedArray()
    val distances = beaconEvents.map { it.distance }.toDoubleArray()

    val solver = NonLinearLeastSquaresSolver(TrilaterationFunction(positions, distances), LevenbergMarquardtOptimizer())
    val optimum = solver.solve()

    val (x, y) = optimum.point.toArray()

    return MultilaterationResult(posX = x, posY = y)
}

private fun QueueEvent.normalize(receiverCoordsMap: Map<String, ReceiverConfig>) =
    receiverCoordsMap.getValue(receiverAddress)
        .let {
            NormalizedQueueEvent(
                beaconAddress = beaconAddress,
                receiverPosX = it.posX,
                receiverPosY = it.posY,
                distance = distance,
                eventTimestamp = eventTimestamp
            )
        }

// Can't be bothered for now, probably can average this
private fun List<NormalizedQueueEvent>.getBeaconStateTimestamp() = first().eventTimestamp
