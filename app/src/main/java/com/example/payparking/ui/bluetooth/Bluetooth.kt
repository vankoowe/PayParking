package com.example.payparking.ui.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.payparking.R
import kotlinx.android.synthetic.main.bluetooth_fragment.*

class Bluetooth : Fragment() {

    private val REQUEST_CODE_ENABLE_BT: Int = 1
    private val REQUEST_CODE_DISCOVERABLE_BT: Int = 2
    lateinit var bAdapter: BluetoothAdapter

    companion object {
        fun newInstance() = Bluetooth()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bluetooth_fragment, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()


        bAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bAdapter == null) {
            if(bluetoothStatusTv!=null) bluetoothStatusTv!!.text = "Bluetooth is not available"
        } else {
            if(bluetoothStatusTv!=null) bluetoothStatusTv.text = "Bluetooth is available"
        }

        if (bAdapter.isEnabled) {
            //bluetooth on
            if(bluetoothIv!=null) bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
        } else {
            //bluetooth off
             if(bluetoothIv!=null) bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
        }

        //turn on bluetooth
        if(turnOnBtn!=null) {
            turnOnBtn.setOnClickListener {
                if (bAdapter.isEnabled) {
                    //already enabled
                    Toast.makeText(activity, "Already on", Toast.LENGTH_LONG).show()
                } else {
                    //turn on bluetooth
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)

                }
            }
        }
        //turn off bluetooth
        if(turnOffBtn!=null) {
            turnOffBtn.setOnClickListener {
                if (!bAdapter.isEnabled) {
                    //already enabled
                    Toast.makeText(activity, "Already off", Toast.LENGTH_LONG).show()
                } else {
                    //turn on bluetooth
                    bAdapter.disable()
                    if (bluetoothIv != null) bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
                    Toast.makeText(activity, "Bluetooth turned off", Toast.LENGTH_LONG).show()
                }
            }
        }
        //discoverable the bluetooth
        if(discoverableBtn!=null) {
            discoverableBtn.setOnClickListener {
                if (!bAdapter.isDiscovering) {
                    Toast.makeText(activity, "Making Your device discoverable", Toast.LENGTH_LONG)
                        .show()
                    val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                    startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
                }
            }
        }
        //get list of paired devices
        if(pairedBtn!=null) {
            pairedBtn.setOnClickListener {
                if (bAdapter.isEnabled) {
                    pairedTv.text = "Paired Devices"
                    val devices = bAdapter.bondedDevices
                    for (device in devices) {
                        val deviceName = device.name
                        val deviceAddress = device
                        pairedTv.append("\nDevice: $deviceName, $device")
                    }
                } else {
                    Toast.makeText(activity, "Turn on bluetooth first", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
                    Toast.makeText(activity, "Bluetooth is on",Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "Could not on bluetooth",Toast.LENGTH_LONG).show()
                }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
