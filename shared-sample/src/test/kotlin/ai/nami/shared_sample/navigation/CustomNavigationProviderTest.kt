package ai.nami.shared_sample.navigation

import ai.nami.sdk_ui_navigation.NamiNavigationEvent
import ai.nami.sdk_ui_navigation.NamiNavigationProvider
import ai.nami.sdk_ui_navigation3.Navigation3Provider
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.ServiceLoader

class CustomNavigationProviderTest {
    /** The customer-facing sample loads only its explicitly declared Navigation 3 provider. */
    @Test
    fun declaredServiceDescriptorLoadsOnlyNavigation3Provider(): Unit {
        val providers = ServiceLoader.load(
            NamiNavigationProvider::class.java,
            requireNotNull(NamiNavigationProvider::class.java.classLoader),
        ).toList()

        assertThat(providers.map { provider -> provider.javaClass })
            .containsExactly(Navigation3Provider::class.java)
    }

    /** Both examples create isolated sessions rather than sharing application navigation state. */
    @Test
    fun customProvidersCreateIndependentSessions(): Unit {
        val providers = listOf(CustomNavigation2Provider(), CustomNavigation3Provider())

        providers.forEach { provider ->
            assertThat(provider.create()).isNotSameInstanceAs(provider.create())
        }
    }

    /** Both examples reject events after their owning SDK session is closed. */
    @Test
    fun closedCustomSessionsRejectFurtherEvents(): Unit {
        val providers = listOf(CustomNavigation2Provider(), CustomNavigation3Provider())

        providers.forEach { provider ->
            val session = provider.create()
            session.close()

            assertThrows(IllegalStateException::class.java) {
                session.dispatch(NamiNavigationEvent.Clear)
            }
        }
    }
}
