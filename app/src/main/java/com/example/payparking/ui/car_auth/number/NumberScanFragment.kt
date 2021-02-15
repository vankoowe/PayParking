package com.example.payparking.ui.car_auth.number

import android.content.pm.PackageManager
import android.graphics.Camera
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.payparking.R
import com.example.payparking.viewmodel.CarViewModel
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.util.jar.Manifest

class NumberScanFragment : Fragment() {

    companion object {
        fun newInstance() = NumberScanFragment()
    }

    private lateinit var viewModel: CarViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.number_scan_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CarViewModel::class.java)
        // TODO: Use the ViewModel
    }


}