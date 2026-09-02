package ai.nami.shared_sample

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomEntryPointTest {
    /** Verifies that the sample passes custom template paths to the SDK as relative URIs. */
    @Test
    fun customEntryPointKeepsTemplatePathRelative(): Unit {
        val relativePath = "update_wifi_credentials_select_zone.json"

        val entryPoint = CustomEntryPoint(relativePath)

        assertEquals(relativePath, entryPoint.uri.toString())
    }
}
