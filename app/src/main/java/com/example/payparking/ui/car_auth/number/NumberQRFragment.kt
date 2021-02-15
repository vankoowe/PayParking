package com.example.payparking.ui.car_auth.number

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.payparking.R
import com.example.payparking.viewmodel.CarViewModel

class NumberQRFragment : Fragment() {

    companion object {
        fun newInstance() = NumberQRFragment()
    }

    private lateinit var viewModel: CarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.number_q_r_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CarViewModel::class.java)
        // TODO: Use the ViewModel
    }

}