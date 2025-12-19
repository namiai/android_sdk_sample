package ai.nami.demo_sdk_fragment

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class SdkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdk)
        val root = findViewById<LinearLayout>(R.id.container)
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, windowInsets ->
            val imeVisible = windowInsets.isVisible(WindowInsetsCompat.Type.ime())
            val orientation = resources.configuration.orientation
            val isLandscapeMode = orientation == Configuration.ORIENTATION_LANDSCAPE
            Log.e("debug-adc", "ImeVisible $imeVisible isLandscape mode : $isLandscapeMode")
            if (imeVisible) {
                // Keyboard is Open -> HIDE the Bar
                val insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                            WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime()
                )
                view.updatePadding(
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
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Nami SDK Sample"
        setSupportActionBar(toolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_main) as NavHostFragment

        val navController = navHostFragment.navController  //findNavController(R.id.nav_host_main)
        bottomNav.setupWithNavController(navController)

        // Hide bottom bar on FragmentE
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val visibility = if (destination.id == R.id.testingFragment) View.GONE else View.VISIBLE
            bottomNav.visibility = visibility
            toolbar.visibility = visibility
        }


    }
}