package org.brotheroftux.locationservice.application.utils

private val re = """^([a-f\d]{2}:){5}[a-f\d]{2}$""".toRegex(RegexOption.IGNORE_CASE)

fun ByteArray.bleAddrToString(): String {
    check(size == 6)

    return reversed().joinToString(separator = ":") { "%02x".format(it) }
}

fun String.bleAddrToByteArray(): ByteArray {
    check(re.matches(this))

    return split(":").map { it.toInt(radix = 16).toByte() }.reversed().toByteArray()
}
