package ai.nami.demo_sdk_fragment

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sysInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // apply top/bottom padding so content isn't obscured
            v.updatePadding(top = sysInsets.top, bottom = sysInsets.bottom)
            // return the insets (or insets) — don't consume unless you intentionally want to
            insets
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