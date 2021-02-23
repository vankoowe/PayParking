package com.example.payparking.ui.friends

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.payparking.R
import com.example.payparking.viewmodel.FriendsViewModel
import kotlinx.android.synthetic.main.friends_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*

class FriendsFragment : Fragment() {

    companion object {
        fun newInstance() = FriendsFragment()
    }

    private lateinit var viewModel: FriendsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.friends_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FriendsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        friends_search.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_search))
        friends_requests.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_requests))
        friends.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_all))

    }

}