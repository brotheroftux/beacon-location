package org.brotheroftux.locationservice.domain.db.connection

import kotlinx.coroutines.Dispatchers
import org.brotheroftux.locationservice.domain.db.schemas.BeaconCoords
import org.brotheroftux.locationservice.domain.db.schemas.EventQueue
import org.brotheroftux.locationservice.domain.db.schemas.ReceiverCoords
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

private const val jdbcURL = "jdbc:h2:file:./build/db"
private const val driverClassName = "org.h2.Driver"

object DbConnection {
    val db by lazy {
        Database.connect(jdbcURL, driverClassName)
    }

    fun init() {
        transaction(db) {
            SchemaUtils.create(EventQueue, BeaconCoords, ReceiverCoords)
        }
    }

    suspend fun <T> dbQuery(transactionBody: suspend Transaction.() -> T): T =
        newSuspendedTransaction(context = Dispatchers.IO, db = db, statement = transactionBody)
}
