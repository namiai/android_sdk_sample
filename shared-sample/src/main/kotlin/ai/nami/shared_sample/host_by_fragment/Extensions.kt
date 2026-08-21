package ai.nami.shared_sample.host_by_fragment

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Applies System Bar insets as padding to this view.
 * @param handleIme If true, also adds padding for the Keyboard (simulates adjustResize).
 */
fun View.applyEdgeToEdgePadding(handleIme: Boolean = true) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        // 1. Determine which insets to look for
        val typeMask = if (handleIme) {
            WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout() or
                    WindowInsetsCompat.Type.ime()
        } else {
            WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout()
        }

        val insets = windowInsets.getInsets(typeMask)

        // 2. Update padding safely (preserving original padding if needed)
        view.updatePadding(
            left = insets.left,
            top = insets.top,
            right = insets.right,
            bottom = insets.bottom
        )

        // 3. Return CONSUMED so children don't re-apply it (optional, depends on layout)
        // usually returning windowInsets (unconsumed) is safer for complex hierarchies
//        WindowInsetsCompat.CONSUMED
        windowInsets
    }
}