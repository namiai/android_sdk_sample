package ai.nami.sdk.sample.pairing.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ai.nami.sdk.sample.R
import ai.nami.sdk.sample.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController


class HomeFragment: Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater,container,false)
        homeBinding.lifecycleOwner = viewLifecycleOwner

        homeBinding.btnPairDevice.setOnClickListener {
            val sessionCode = homeBinding.edtSessionCode.text.toString()
            val roomId = homeBinding.edtRoomId.text.toString()
            if(sessionCode.isNotEmpty() && roomId.isNotEmpty()){
                navigateToNamiPairing(sessionCode = sessionCode, roomId = roomId)
            }
        }

        return homeBinding.root
    }

    private fun navigateToNamiPairing(sessionCode: String, roomId: String) {
        this.findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToNamiPairingFragment(sessionCode = sessionCode,roomId = roomId)
        )
    }
}