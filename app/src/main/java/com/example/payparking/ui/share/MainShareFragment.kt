package com.example.payparking.ui.share

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.payparking.R
import com.example.payparking.viewmodel.MainShareViewModel
import kotlinx.android.synthetic.main.main_share_fragment.*

class MainShareFragment : Fragment() {

    companion object {
        fun newInstance() = MainShareFragment()
    }

    private lateinit var viewModel: MainShareViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_share_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainShareViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        main_share_share.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_share))
        main_share_newsfeed.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_newsfeed))

    }

}