package ai.nami.shared_sample.host_by_fragment

import ai.nami.shared_sample.R
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment


class XmlActivity : AppCompatActivity() {

    private var applyGlobalImePadding: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("debug-adc", "xmActivity onCreate $applyGlobalImePadding")

        enableEdgeToEdge()
        setContentView(R.layout.xml_activity)
        val rootView = findViewById<LinearLayout>(R.id.root_container)

        //rootView.applyEdgeToEdgePadding(handleIme = applyGlobalImePadding)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_main) as NavHostFragment

        val navController = navHostFragment.navController
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        val bottomNav = findViewById<LinearLayout>(R.id.ll_bottom_nav)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            val imeVisible = windowInsets.isVisible(WindowInsetsCompat.Type.ime())
            val orientation = resources.configuration.orientation
            val isHeightConstrainedMode = orientation == Configuration.ORIENTATION_LANDSCAPE
            Log.e(
                "debug-adc",
                "ImeVisible $imeVisible isHeightConstrainedMode mode : $isHeightConstrainedMode"
            )
            if (imeVisible) {
                // Keyboard is Open -> HIDE the Bar
                val insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                            WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime()
                )
                rootView.updatePadding(
                    bottom = insets.bottom,
                    left = insets.left,
                    right = insets.right,
                    top = insets.top
                )
            } else {
                // Keyboard is Closed -> SHOW the Bar & Restore Padding
                val insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                            WindowInsetsCompat.Type.displayCutout()
                )
                view.updatePadding(
                    bottom = insets.bottom,
                    left = insets.left,
                    right = insets.right,
                    top = insets.top
                )
            }

            windowInsets
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val visibility = if (destination.id == R.id.hostSdkFragment) View.GONE else View.VISIBLE
            toolbar.visibility = visibility
            bottomNav.visibility = visibility
        }
    }

    override fun onDestroy() {
        Log.e("debug-adc", "xmActivity onDestroy $applyGlobalImePadding")
        super.onDestroy()
    }

    fun setGlobalImePaddingEnabled(enabled: Boolean) {
        if (applyGlobalImePadding != enabled) {
            applyGlobalImePadding = enabled

            // Force the system to run the listener above again immediately
            val rootView = findViewById<View>(R.id.root_container)
            ViewCompat.requestApplyInsets(rootView)
        }
    }
}