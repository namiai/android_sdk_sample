package ai.nami.demo_sdk_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.compose.rememberNavController


class SdkFragment : Fragment() {
    companion object {
        fun newInstance() = SdkFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_sdk, container, false)
        val composeView = ComposeView(requireContext())
        composeView.setContent {
            val navController = rememberNavController()
            MainNavHost(navController)
        }
        return composeView
    }
}