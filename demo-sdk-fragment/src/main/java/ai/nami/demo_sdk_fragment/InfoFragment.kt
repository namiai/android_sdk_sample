package ai.nami.demo_sdk_fragment

import ai.nami.demo_sdk_fragment.databinding.FragmentInfoBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class InfoFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = FragmentInfoBinding.inflate(inflater, container, false)
        dataBinding.btnOpenSDK.setOnClickListener {
            //action_infoFragment_to_testingFragment
            findNavController().navigate(R.id.action_infoFragment_to_testingFragment)
        }
        return dataBinding.root
    }

}