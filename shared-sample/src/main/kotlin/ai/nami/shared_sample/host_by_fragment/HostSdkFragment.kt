package ai.nami.shared_sample.host_by_fragment

import ai.nami.shared_sample.MainNavHost
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.compose.rememberNavController

class HostSdkFragment : Fragment() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.e("debug-adc","HostSdkFragment onCreate")
//    }
//
//    override fun onDestroy() {
//        Log.e("debug-adc","HostSdkFragment onDestroy")
//        (activity as XmlActivity).setGlobalImePaddingEnabled(true)
//        super.onDestroy()
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // (activity as XmlActivity).setGlobalImePaddingEnabled(false)
        Log.e("debug-adc", "HostSdkFragment onCreateView")
        val composeView = ComposeView(requireContext())
        composeView.setContent {
            val navController = rememberNavController()
            MainNavHost(navController)
        }
        return composeView
    }

}