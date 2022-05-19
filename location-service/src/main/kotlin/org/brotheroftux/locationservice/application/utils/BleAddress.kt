package org.brotheroftux.locationservice.application.utils

fun ByteArray.bleAddrToString(): String {
    check(size == 6)

    return reversed().joinToString(separator = ":") { "%02x".format(it) }
}
