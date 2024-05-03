package ai.nami.sdk.sample.utils

const val URN_SEPARATOR = ":"
private fun String.lastUrn(): String = split(URN_SEPARATOR).last()

fun formatDeviceUrn(deviceUrn: String, isLowerCase: Boolean): String {
    return if (deviceUrn.contains("urn:")) {
        // urn:namiai:device:dc3643b00af9
        val lastUrn = deviceUrn.lastUrn()
        macAddressFormat(lastUrn, isLowerCase)
    } else if (deviceUrn.contains(":")) {
        // dc:36:43:b0:12:f7
        if (isLowerCase)
            deviceUrn.lowercase() else deviceUrn.uppercase()
    } else {
        // dc3643b012f7
        macAddressFormat(deviceUrn, isLowerCase)
    }
}

fun macAddressFormat(urn: String, isLowerCase: Boolean) =
    urn.chunked(2)
        .joinToString(separator = ":") { if (isLowerCase) it.lowercase() else it.uppercase() }