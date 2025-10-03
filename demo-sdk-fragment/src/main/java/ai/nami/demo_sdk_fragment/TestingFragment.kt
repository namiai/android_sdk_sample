package ai.nami.demo_sdk_fragment

import ai.nami.demo_sdk_fragment.databinding.FragmentTestingBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.navigation.compose.rememberNavController

class TestingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = FragmentTestingBinding.inflate(inflater, container, false)
        val composeView = dataBinding.composeView
        composeView.setContent {
            val navController = rememberNavController()
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                MainNavHost(navController)
            }
        }
        return dataBinding.root
    }

}