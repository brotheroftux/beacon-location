package org.brotheroftux.locationservice.domain.db.schemas

import org.jetbrains.exposed.sql.Table

object ReceiverCoords : Table() {
    val addr = binary("addr", 6)
    val posX = double("x")
    val posY = double("y")

    override val primaryKey = PrimaryKey(addr)
}
