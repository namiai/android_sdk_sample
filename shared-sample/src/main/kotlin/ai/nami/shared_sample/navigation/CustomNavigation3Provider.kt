package ai.nami.shared_sample.navigation

import ai.nami.sdk_ui_extensions.ui.navigation.NavFlow
import ai.nami.sdk_ui_extensions.ui.navigation.SDUINavKey
import ai.nami.sdk_ui_navigation.NamiNavigationEvent
import ai.nami.sdk_ui_navigation.NamiNavigationProvider
import ai.nami.sdk_ui_navigation.NamiNavigationSession
import ai.nami.sdk_ui_navigation.handleNavigationException
import ai.nami.sdk_ui_navigation.handleNavigationEvents
import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/**
 * Example of an application-owned Navigation 3 provider discovered through `ServiceLoader`.
 *
 * Applications can customize the entry decorators, animations, and back-stack policy here
 * without changing the SDK integration point.
 */
@Keep
class CustomNavigation3Provider : NamiNavigationProvider {
    /** @return A new independent Navigation 3 session for one SDK instance. */
    override fun create(): NamiNavigationSession = CustomNavigation3Session()
}

@Serializable
private sealed interface CustomNavigation3Key : NavKey

@Serializable
private data object CustomNavigation3Loading : CustomNavigation3Key

@Serializable
private data class CustomNavigation3Destination(
    val value: SDUINavKey,
) : CustomNavigation3Key

private val savedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(CustomNavigation3Loading::class, CustomNavigation3Loading.serializer())
            subclass(
                CustomNavigation3Destination::class,
                CustomNavigation3Destination.serializer(),
            )
        }
    }
}

private class CustomNavigation3Session : NamiNavigationSession {
    private val events = Channel<NamiNavigationEvent>(Channel.UNLIMITED)

    /** Queues one SDK event for this session's Navigation 3 host. */
    override fun dispatch(event: NamiNavigationEvent): Unit {
        check(events.trySend(event).isSuccess) { "This custom navigation session is closed." }
    }

    /** Renders and owns the Navigation 3 back stack for this SDK instance. */
    @Composable
    override fun Host(
        modifier: Modifier,
        initialDestination: SDUINavKey,
        onExitAtRoot: () -> Unit,
        onError: (Throwable) -> Unit,
        loadingContent: @Composable () -> Unit,
        destinationContent: @Composable (SDUINavKey) -> Unit,
    ): Unit {
        val backStack = rememberNavBackStack(savedStateConfiguration, CustomNavigation3Loading)

        LaunchedEffect(initialDestination.entryId) {
            if (backStack.size == 1 && backStack.firstOrNull() == CustomNavigation3Loading) {
                backStack.clear()
                backStack.add(CustomNavigation3Destination(initialDestination))
            }
        }
        LaunchedEffect(backStack, onExitAtRoot, onError) {
            events.receiveAsFlow()
                .handleNavigationEvents(onError) { event ->
                    handleNavigationEvent(backStack, event, onExitAtRoot)
                }
                .collect()
        }

        NavDisplay(
            modifier = modifier,
            backStack = backStack,
            onBack = {
                try {
                    handleNavigationEvent(backStack, NamiNavigationEvent.Back, onExitAtRoot)
                } catch (throwable: Exception) {
                    handleNavigationException(throwable, onError)
                }
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<CustomNavigation3Loading> { loadingContent() }
                entry<CustomNavigation3Destination>(
                    clazzContentKey = { destination -> destination.value.entryId },
                ) { destination ->
                    destinationContent(destination.value)
                }
            },
        )
    }

    /** Closes this session's event queue. */
    override fun close(): Unit {
        events.close()
    }
}

/** Applies one exhaustive SDK event to the custom Navigation 3 back stack. */
private fun handleNavigationEvent(
    backStack: MutableList<NavKey>,
    event: NamiNavigationEvent,
    onExitAtRoot: () -> Unit,
): Unit {
    when (event) {
        is NamiNavigationEvent.Navigate -> Snapshot.withMutableSnapshot {
            event.destination.replacingFlow?.let { replacingFlow ->
                val index = backStack.indexOfFirstFlowScreen(replacingFlow)
                if (index >= 0) backStack.subList(index, backStack.size).clear()
            }
            backStack.add(CustomNavigation3Destination(event.destination))
        }

        NamiNavigationEvent.Back -> {
            if (backStack.size <= 1) onExitAtRoot() else backStack.removeLastOrNull()
        }

        is NamiNavigationEvent.BackTo -> {
            val navFlow = event.navFlow
            when {
                navFlow != null -> {
                    val index = backStack.indexOfFirstFlowScreen(navFlow)
                    if (index > 0) backStack.subList(index, backStack.size).clear()
                    else if (index == 0) {
                        backStack.clear()
                        onExitAtRoot()
                    }
                }

                event.screenName != null -> {
                    val index = backStack.indexOfLast { key ->
                        key is CustomNavigation3Destination && key.value.url == event.screenName
                    }
                    when {
                        index < 0 -> Unit
                        index == backStack.lastIndex -> backStack.removeLastOrNull()
                        else -> backStack.subList(index + 1, backStack.size).clear()
                    }
                }

                else -> {
                    if (backStack.size <= 1) onExitAtRoot() else backStack.removeLastOrNull()
                }
            }
        }

        NamiNavigationEvent.Clear -> backStack.clear()

        is NamiNavigationEvent.UpdateFlows -> Snapshot.withMutableSnapshot {
            val index = backStack.indexOfFirst { key ->
                key is CustomNavigation3Destination && key.value.entryId == event.entryId
            }
            if (index >= 0) {
                val current = (backStack[index] as CustomNavigation3Destination).value
                val updatedFlows = (current.flows + event.flows).distinct()
                if (updatedFlows != current.flows) {
                    backStack[index] = CustomNavigation3Destination(
                        current.copy(flows = updatedFlows),
                    )
                }
            }
        }
    }
}

/** Returns the first custom destination index belonging to [flow], or `-1`. */
private fun List<NavKey>.indexOfFirstFlowScreen(flow: NavFlow): Int = indexOfFirst { key ->
    key is CustomNavigation3Destination && flow in key.value.flows
}
