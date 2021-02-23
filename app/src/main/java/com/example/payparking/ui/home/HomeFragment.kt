package com.example.payparking.ui.home

import android.Manifest
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RawRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.payparking.MainActivity
import com.example.payparking.R
import com.example.payparking.viewmodel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.car_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()

        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    }

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null

    private lateinit var viewModel: HomeViewModel
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    val CHANNEL_ID = "channelId"
    val CHANNEL_NAME = "channelName"
    val NOTIFICATION__ID =0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }

    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //val name = getString(R.string.channel_name)
            //val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "descriptionText"
                lightColor = Color.GREEN
                enableLights(true)
            }
            // Register the channel with the system
            val notification =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notification.createNotificationChannel(channel)
        }
    }
    override fun onStart() {
        super.onStart()
        /*createNotificationChannel()

        val intent = Intent(requireContext(), MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(requireContext()).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setContentTitle("Samo Levski")
            .setContentText("aswertyu")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notManager = NotificationManagerCompat.from(requireContext())

        notManager.notify(NOTIFICATION__ID, notification)*/
        auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Heat_Map")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                var flag = false
                for (childSnapshot in dataSnapshot.children) {
                    if (childSnapshot.key.toString() == userId) {
                        flag = true
                    }
                }
                if (flag) {
                    home_park.setText("END PARK")
                } else {
                    home_park.setText("Park")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference!!.addValueEventListener(postListener)

        home_log_out?.setOnClickListener{signOut()}
        home_change_car?.setOnClickListener{
            findNavController().navigate(R.id.nav_fragment_login)
        }

        home_maps.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_map))
        home_profile.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_profile))

        home_park.setOnClickListener{
            if(home_park.text.equals("Park")){
                    if (!checkPermissions()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions()
                        }
                    } else {
                        getLastLocation()
                        //home_park.isEnabled = false
                        home_park.setText("END PARK")
                    }
            }else if(home_park.text.equals("END PARK")){
                auth = Firebase.auth
                mDatabase = FirebaseDatabase.getInstance()
                mDatabaseReference = mDatabase!!.reference.child("Heat_Map")
                val userId = auth.currentUser!!.uid
                val currentUserDb = mDatabaseReference!!.child(userId)

                currentUserDb.child("lat").removeValue()
                currentUserDb.child("long").removeValue()
                currentUserDb.child("zone").removeValue()
                home_park.setText("Park")

            }
        }
        home_share.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {}else {
                fusedLocationClient?.lastLocation!!.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        lastLocation = task.result
                        val geocoder: Geocoder
                        val addresses: List<Address>
                        geocoder = Geocoder(context, Locale.getDefault())

                        addresses = geocoder.getFromLocation(
                            (lastLocation)!!.latitude,
                            (lastLocation)!!.longitude,
                            1
                        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                        val address: String =
                            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                        val city: String = addresses[0].getLocality()
                        val state: String = addresses[0].getAdminArea()
                        val country: String = addresses[0].getCountryName()
                        Toast.makeText(context, address, Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
        home_friends.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_friends))
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {}else{

            fusedLocationClient?.lastLocation!!.addOnCompleteListener{ task ->
                if (task.isSuccessful && task.result != null) {
                    lastLocation = task.result
                    auth = Firebase.auth
                    mDatabase = FirebaseDatabase.getInstance()
                    mDatabaseReference = mDatabase!!.reference.child("Heat_Map")
                    val userId = auth.currentUser!!.uid
                    val currentUserDb = mDatabaseReference!!.child(userId)

                    currentUserDb.child("lat").setValue((lastLocation)!!.latitude)
                    currentUserDb.child("long").setValue((lastLocation)!!.longitude)
                    val position = LatLng((lastLocation)!!.latitude, (lastLocation)!!.longitude)

                    val containsg =
                        PolyUtil.containsLocation(
                            position.latitude,
                            position.longitude,
                            readZones(R.raw.greenzones),
                            true
                        )
                    val containsb =
                        PolyUtil.containsLocation(
                            position.latitude,
                            position.longitude,
                            readZones(R.raw.bluezones),
                            true
                        )
                    //println("contains1: $contains1")
                    if(containsb){
                        Toast.makeText(context, "You are in blue zone", Toast.LENGTH_LONG).show()
                        currentUserDb.child("zone").setValue("2")
                    }else if(containsg){
                        Toast.makeText(context, "You are in green zone", Toast.LENGTH_LONG).show()
                        currentUserDb.child("zone").setValue("3")
                    }else{
                        Toast.makeText(context, "You are not in zone", Toast.LENGTH_LONG).show()
                        currentUserDb.child("zone").setValue("1")
                    }
                }
                else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)
                }
            }
        }

    }
    @Throws(JSONException::class)
    private fun readZones(@RawRes resource: Int): MutableList<LatLng?> {
        val result: MutableList<LatLng?> = ArrayList()
        val inputStream = context?.resources!!.openRawResource(resource)
        val json = Scanner(inputStream).useDelimiter("\\A").next()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val `object` = array.getJSONObject(i)
            val lat = `object`.getDouble("lat")
            val lng = `object`.getDouble("lng")
            result.add(LatLng(lat, lng))
        }

        return result
    }


    private fun showSnackbar(
        mainTextStringId: String, actionStringId: String,
        listener: View.OnClickListener
    ) {
        Toast.makeText(context, mainTextStringId, Toast.LENGTH_LONG).show()
    }
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }
    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar("Location permission is needed for core functionality", "Okay",
                View.OnClickListener {
                    startLocationPermissionRequest()
                })
        }
        else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted.
                    getLastLocation()
                }
                else -> {
                    showSnackbar("Permission was denied", "Settings",
                        View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                Build.DISPLAY, null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    private fun signOut() {
        auth.signOut()
        findNavController().navigate(R.id.nav_fragment_login)
    }

    override fun onDetach() {
        super.onDetach()
    }

}