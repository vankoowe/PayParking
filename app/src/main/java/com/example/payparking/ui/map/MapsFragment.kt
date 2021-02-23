package com.example.payparking.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.payparking.R
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_maps.*



@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MapsFragment : Fragment() {

    companion object {
        fun newInstance() = MapsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
        }*/

    }

    override fun onStart() {
        super.onStart()

        //maps_back.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_home))
        maps_heat_map.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_heatmap))
        maps_zones.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_zones))
    }

}