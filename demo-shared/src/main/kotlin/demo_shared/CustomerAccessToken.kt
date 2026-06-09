package demo_shared

import java.time.Instant
import java.time.format.DateTimeParseException

data class CustomerAccessToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: String
) {
    fun isValid(): Boolean {

        if (accessToken.isBlank() || refreshToken.isBlank() || expiresAt.isBlank()) return false

        val expiryInstant = try {
            Instant.parse(expiresAt) // e.g. 2026-06-03T21:26:13.148Z
        } catch (_: DateTimeParseException) {
            return false
        }

        return expiryInstant.isAfter(Instant.now())
    }
}
