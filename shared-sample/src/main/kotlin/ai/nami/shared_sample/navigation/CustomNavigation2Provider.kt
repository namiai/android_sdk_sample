package ai.nami.shared_sample.navigation

import ai.nami.sdk_ui_extensions.ui.navigation.NavFlow
import ai.nami.sdk_ui_extensions.ui.navigation.SDUINavKey
import ai.nami.sdk_ui_navigation.NamiNavigationEvent
import ai.nami.sdk_ui_navigation.NamiNavigationProvider
import ai.nami.sdk_ui_navigation.NamiNavigationSession
import ai.nami.sdk_ui_navigation.handleNavigationEvents
import android.net.Uri
import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json

/**
 * Example of an application-owned Navigation 2 provider discovered through `ServiceLoader`.
 *
 * Applications can replace the session below with their own controller, transitions, analytics,
 * or back-stack rules while keeping the Nami navigation contract unchanged.
 */
@Keep
class CustomNavigation2Provider : NamiNavigationProvider {
    /** @return A new independent Navigation 2 session for one SDK instance. */
    override fun create(): NamiNavigationSession = CustomNavigation2Session()
}

private const val DESTINATION_ROUTE = "custom-nami-sdui"
private const val ENTRY_ID_ARGUMENT = "entryId"
private const val DESTINATION_PATTERN = "$DESTINATION_ROUTE/{$ENTRY_ID_ARGUMENT}"
private val destinationJson = Json { encodeDefaults = true; ignoreUnknownKeys = true }
private val destinationStackSaver = Saver<SnapshotStateList<SDUINavKey>, ArrayList<String>>(
    save = { destinations -> saveDestinationStack(destinations) },
    restore = ::restoreDestinationStack,
)

private class CustomNavigation2Session : NamiNavigationSession {
    private val events = Channel<NamiNavigationEvent>(Channel.UNLIMITED)

    /** Queues one SDK event for this session's Navigation 2 host. */
    override fun dispatch(event: NamiNavigationEvent): Unit {
        check(events.trySend(event).isSuccess) { "This custom navigation session is closed." }
    }

    /** Renders and owns the Navigation 2 back stack for this SDK instance. */
    @Composable
    override fun Host(
        modifier: Modifier,
        initialDestination: SDUINavKey,
        onExitAtRoot: () -> Unit,
        onError: (Throwable) -> Unit,
        loadingContent: @Composable () -> Unit,
        destinationContent: @Composable (SDUINavKey) -> Unit,
    ): Unit {
        val navController = rememberNavController()
        val destinations = rememberSaveable(
            initialDestination.entryId,
            saver = destinationStackSaver,
        ) {
            mutableStateListOf(initialDestination)
        }
        val initialRoute = remember(initialDestination.entryId) {
            routeFor(initialDestination.entryId)
        }

        LaunchedEffect(navController, onExitAtRoot, onError) {
            events.receiveAsFlow()
                .handleNavigationEvents(onError) { event ->
                    handleNavigationEvent(
                        navController = navController,
                        destinations = destinations,
                        event = event,
                        onExitAtRoot = onExitAtRoot,
                    )
                }
                .collect()
        }

        NavHost(
            navController = navController,
            startDestination = initialRoute,
            modifier = modifier,
        ) {
            composable(
                route = DESTINATION_PATTERN,
                arguments = listOf(navArgument(ENTRY_ID_ARGUMENT) { type = NavType.StringType }),
            ) { entry ->
                val entryId = entry.arguments?.getString(ENTRY_ID_ARGUMENT)
                val destination = destinations.resolveRouteDestination(entryId)
                if (destination == null) loadingContent() else destinationContent(destination)
            }
        }
    }

    /** Closes this session's event queue. */
    override fun close(): Unit {
        events.close()
    }
}

/** Applies one exhaustive SDK event to the controller and its matching metadata stack. */
private fun handleNavigationEvent(
    navController: NavHostController,
    destinations: MutableList<SDUINavKey>,
    event: NamiNavigationEvent,
    onExitAtRoot: () -> Unit,
): Unit {
    when (event) {
        is NamiNavigationEvent.Navigate -> {
            event.destination.replacingFlow?.let { replacingFlow ->
                val index = destinations.indexOfFirstFlowScreen(replacingFlow)
                if (index >= 0) popFrom(index, navController, destinations)
            }
            destinations.add(event.destination)
            navController.navigate(routeFor(event.destination.entryId))
        }

        NamiNavigationEvent.Back -> handleBack(destinations, onExitAtRoot) {
            popLast(destinations) { navController.popBackStack() }
        }

        is NamiNavigationEvent.BackTo -> {
            val navFlow = event.navFlow
            when {
                navFlow != null -> {
                    val index = destinations.indexOfFirstFlowScreen(navFlow)
                    if (index > 0) popFrom(index, navController, destinations)
                    else if (index == 0 && clear(navController, destinations)) onExitAtRoot()
                }

                event.screenName != null -> {
                    val index = destinations.indexOfLast { destination ->
                        destination.url == event.screenName
                    }
                    when {
                        index < 0 -> Unit
                        index == destinations.lastIndex ->
                            popFrom(index, navController, destinations)
                        else -> popFrom(index + 1, navController, destinations)
                    }
                }

                else -> handleBack(destinations, onExitAtRoot) {
                    popLast(destinations) { navController.popBackStack() }
                }
            }
        }

        NamiNavigationEvent.Clear -> clear(navController, destinations)

        is NamiNavigationEvent.UpdateFlows -> {
            val index = destinations.indexOfFirst { destination ->
                destination.entryId == event.entryId
            }
            if (index >= 0) {
                val current = destinations[index]
                destinations[index] = current.copy(
                    flows = (current.flows + event.flows).distinct(),
                )
            }
        }
    }
}

/** Calls [onExitAtRoot] at root; otherwise removes the current destination. */
private fun handleBack(
    destinations: MutableList<SDUINavKey>,
    onExitAtRoot: () -> Unit,
    popLast: () -> Unit,
): Unit {
    if (destinations.size <= 1) onExitAtRoot() else popLast()
}

/** Removes the top metadata entry only when the Navigation 2 controller pop succeeds. */
private fun popLast(
    destinations: MutableList<SDUINavKey>,
    popController: () -> Boolean,
): Unit {
    if (popController()) destinations.removeAt(destinations.lastIndex)
}

/** Removes the destination at [index] and every destination above it. */
private fun popFrom(
    index: Int,
    navController: NavHostController,
    destinations: MutableList<SDUINavKey>,
): Boolean = popFrom(index, destinations) { entryId ->
    navController.popBackStack(routeFor(entryId), inclusive = true)
}

/** Synchronizes a multi-entry metadata pop with the controller result. */
private fun popFrom(
    index: Int,
    destinations: MutableList<SDUINavKey>,
    popController: (entryId: String) -> Boolean,
): Boolean {
    val entryId = destinations[index].entryId
    if (!popController(entryId)) return false

    destinations.subList(index, destinations.size).clear()
    return true
}

/** Clears both Navigation 2 stacks and returns whether the controller accepted the pop. */
private fun clear(
    navController: NavHostController,
    destinations: MutableList<SDUINavKey>,
): Boolean = if (destinations.isEmpty()) true else popFrom(0, navController, destinations)

/** Returns a unique Navigation 2 route for immutable occurrence [entryId]. */
private fun routeFor(entryId: String): String = "$DESTINATION_ROUTE/${Uri.encode(entryId)}"

/** Serializes the complete metadata stack for Compose saved-state restoration. */
private fun saveDestinationStack(destinations: List<SDUINavKey>): ArrayList<String> =
    destinations.mapTo(ArrayList(destinations.size)) { destination ->
        destinationJson.encodeToString(SDUINavKey.serializer(), destination)
    }

/** Restores a metadata stack, returning `null` when saved state is invalid. */
private fun restoreDestinationStack(
    encodedDestinations: List<String>,
): SnapshotStateList<SDUINavKey>? = runCatching {
    encodedDestinations.mapTo(mutableStateListOf()) { encoded ->
        destinationJson.decodeFromString(SDUINavKey.serializer(), encoded)
    }
}.getOrNull()

/** Resolves current metadata for an already-decoded Navigation 2 route [entryId]. */
private fun List<SDUINavKey>.resolveRouteDestination(entryId: String?): SDUINavKey? =
    firstOrNull { destination -> destination.entryId == entryId }

/** Returns the first destination index belonging to [flow], or `-1`. */
private fun List<SDUINavKey>.indexOfFirstFlowScreen(flow: NavFlow): Int =
    indexOfFirst { destination -> flow in destination.flows }
