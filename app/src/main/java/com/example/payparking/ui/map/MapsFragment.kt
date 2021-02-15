package com.example.payparking.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.payparking.R
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MapsFragment : Fragment() {

//    private val mMap: GoogleMap? = null
//    private lateinit var currentLocation: Location
//    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//    private val permissionCode = 101

//    private var mLocationRequest: LocationRequest? = null
//    private val UPDATE_INTERVAL = (10 * 1000).toLong()  /* 10 secs */
//    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
//
//    private var latitude = 0.0
//    private var longitude = 0.0

    private lateinit var mGoogleMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // in onCreate() initialize FusedLocationProviderClient


    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback


    /**
     * call this method in onCreate
     * onLocationResult call when location is changed
     */
    private fun getLocationUpdates()
    {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location =
                        locationResult.lastLocation
                    // use your location object
                    // get latitude , longitude and other info from this
                }


            }
        }
    }

    //start location updates
    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }


    companion object {
        fun newInstance() = MapsFragment()
    }

//    override fun onStart() {
//        super.onStart()
//        startLocationUpdates()
//    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        getLocationUpdates()
//    }

    override fun onStart() {
        super.onStart()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

//        mGoogleMap = googleMap;
//
//        if (mGoogleMap != null) {
//            mGoogleMap!!.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("Current Location"))
//        }
//        val sydney = LatLng(-34.0, 151.0)
//        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
//        val markerOptions = MarkerOptions().position(latLng).title("I am here!")

//        if (ContextCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) !==
//            PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    requireActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            ) {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
//                )
//            } else {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
//                )
//            }
//        }

//        getLastKnownLocation(requireContext())



        //fetchLocation()
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
//        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
//        googleMap?.addMarker(markerOptions)


    }

//    protected fun startLocationUpdates() {
//        // initialize location request object
//        mLocationRequest = LocationRequest.create()
//        mLocationRequest!!.run {
//            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//            setInterval(UPDATE_INTERVAL)
//            setFastestInterval(FASTEST_INTERVAL)
//        }
//
//        // initialize location setting request builder object
//        val builder = LocationSettingsRequest.Builder()
//        builder.addLocationRequest(mLocationRequest!!)
//        val locationSettingsRequest = builder.build()
//
//        // initialize location service object
//        val settingsClient = LocationServices.getSettingsClient(requireActivity())
//        settingsClient!!.checkLocationSettings(locationSettingsRequest)
//
//        // call register location listener
//        registerLocationListner()
//    }
//
//    private fun registerLocationListner() {
//        // initialize location callback object
//        val locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult?) {
//                onLocationChanged(locationResult!!.getLastLocation())
//            }
//        }
//        // 4. add permission if android version is greater then 23
//        if(Build.VERSION.SDK_INT >= 23 && checkPermission()) {
//            LocationServices.getFusedLocationProviderClient(requireActivity()).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
//        }
//    }
//
//    //
//    private fun onLocationChanged(location: Location) {
//        val location = LatLng(location.latitude, location.longitude)
//        mGoogleMap!!.clear()
//        mGoogleMap!!.addMarker(MarkerOptions().position(location).title("Current Location"))
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
//    }
//
//    private fun checkPermission() : Boolean {
//        if (ContextCompat.checkSelfPermission(requireContext() , android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//            requestPermissions()
//            return false
//        }
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(requireActivity(), arrayOf("Manifest.permission.ACCESS_FINE_LOCATION"),1)
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if(requestCode == 1) {
//            if (permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION ) {
//                registerLocationListner()
//            }
//        }
//    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
//                                            grantResults: IntArray) {
//        when (requestCode) {
//            1 -> {
//                if (grantResults.isNotEmpty() && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED) {
//                    if ((ContextCompat.checkSelfPermission(requireContext(),
//                            Manifest.permission.ACCESS_FINE_LOCATION) ===
//                                PackageManager.PERMISSION_GRANTED)) {
//                        Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
//                }
//                return
//            }
//        }
//    }

//    private fun fetchLocation() {
//        if (ContextCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) !==
//            PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    requireActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            ) {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
//                )
//            } else {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
//                )
//            }
//        }
//        val task = fusedLocationProviderClient.lastLocation
//        task.addOnSuccessListener { location ->
//            if (location != null) {
//                currentLocation = location
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
//                                            grantResults: IntArray) {
//        when (requestCode) { permissionCode ->
//            if (grantResults.isNotEmpty() && grantResults[0] ==
//            PackageManager.PERMISSION_GRANTED) {
//                fetchLocation()
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(requireActivity())
//        fetchLocation()
//    }

//    @SuppressLint("MissingPermission")
//    private fun getLastKnownLocation(context: Context) {
// val locationManager: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val providers: List<String> = locationManager.getProviders(true)
//        var location: Location? = null
//        for (i in providers.size - 1 downTo 0) {
//            locationManager.getLastKnownLocation(providers[i]).also { location = it }
//            if (location != null)
//                break
//        }
//        val gps = DoubleArray(2)
//        if (location != null) {
//            gps[0] = location!!.getLatitude()
//            gps[1] = location!!.getLongitude()
//            Log.e("gpsLat",gps[0].toString())
//            Log.e("gpsLong",gps[1].toString())
//
//        }
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}