package ai.nami.shared_sample.host_by_fragment


import ai.nami.shared_sample.databinding.HomeFragmentBinding
import ai.nami.shared_sample.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = HomeFragmentBinding.inflate(inflater, container, false)
        dataBinding.btnLaunchSdk.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_hostSdkFragment)
        }
        return dataBinding.root
    }

}