package com.example.payparking.ui.home

import android.Manifest
import android.annotation.SuppressLint
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
import android.os.Handler
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
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.cardinalcommerce.shared.userinterfaces.ProgressDialog
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
import kotlinx.android.synthetic.main.home_fragment.*
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

import kotlin.random.Random

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()

        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    }

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    private lateinit var viewModel: HomeViewModel
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabaseReference4: DatabaseReference? = null

    private var mDatabase: FirebaseDatabase? = null

    val CHANNEL_ID = "channelId"
    val CHANNEL_NAME = "channelName"
    val NOTIFICATION__ID =0
    val handler: Handler = Handler()
    val delay = 1000 // 1000 milliseconds == 1 second
    var i = 1

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


    @SuppressLint("SimpleDateFormat")
    override fun onStart() {
        super.onStart()

        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(dialogView)
        val progressDialog = builder.create()
        progressDialog.show()

        Handler().postDelayed({progressDialog.dismiss()}, 10000)

        auth = Firebase.auth
        var locations: ArrayList<String>? = ArrayList()

        val userId = auth.currentUser!!.uid
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference4 = mDatabase!!.reference.child("Free")
        val postListenerr = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                createNotificationChannel()
                val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"
                val SUMMARY_ID = 0

                val intent = Intent(requireContext(), MainActivity::class.java)
                val pendingIntent = TaskStackBuilder.create(requireContext()).run {
                    addNextIntentWithParentStack(intent)
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                /*val pendingIntent = NavDeepLinkBuilder(requireContext())
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.nav_fragment_search)
                    //.setArguments(bundle)
                    .createPendingIntent()*/

                val notManager = NotificationManagerCompat.from(requireContext())

                for (childSnapshot in dataSnapshot.children) {
                    //if(childSnapshot.key.toString()=="1"){
                    locations!!.add(childSnapshot.key.toString())

                    val notification = NotificationCompat.Builder(
                        requireContext(),
                        CHANNEL_ID
                    )
                        .setContentTitle("Pay Parking")
                        .setContentText(childSnapshot.key.toString()+": "+childSnapshot.value.toString())
                        .setSmallIcon(R.drawable.logo)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        .build()

                    val summaryNotification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                        .setContentTitle("Summary")
                        //set content text to support devices running API level < 24
                        .setContentText(i.toString() + " new messages")
                        .setSmallIcon(R.drawable.logo)

                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)

                    notManager.notify(i, notification)
                    notManager.notify(SUMMARY_ID, summaryNotification.build())
                    i++
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference4!!.addValueEventListener(postListenerr)

        lateinit var address: String
        //val userId = auth.currentUser!!.uid
        mDatabaseReference = mDatabase!!.reference

        val currentUserDb = mDatabaseReference!!.child("Heat_Map").child(userId)
        val c = mDatabaseReference!!.child("Free")
        val postListenerre = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lat = dataSnapshot.child("lat").value
                val long = dataSnapshot.child("long").value

                if (long!=null && lat !=null) {

                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(context, Locale.getDefault())

                    addresses = geocoder.getFromLocation(
                        lat as Double, long as Double,
                        1
                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                    address =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        currentUserDb!!.addValueEventListener(postListenerre)


        // try to touch View of UI thread*/


        auth = Firebase.auth
        //val userId = auth.currentUser!!.uid
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
                    home_park!!.text = getString(R.string.end)
                    home_pay.isEnabled = true
                    home_pay.setBackgroundColor(Color.parseColor("#5BC236"))
                    progressDialog.dismiss()
                } else {
                    home_park!!.text = getString(R.string.park)
                    home_pay.isEnabled = false
                    home_pay.setBackgroundColor(Color.parseColor("#808080"))
                    progressDialog.dismiss()
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
            findNavController().navigate(R.id.nav_fragment_car)
        }

        home_pay.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_payment))
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
                    home_park!!.text = getString(R.string.end)
                    home_pay.isEnabled = true
                    home_pay.setBackgroundColor(Color.parseColor("#5BC236"))

                }
            }else if(home_park.text.equals("END PARK")){
                auth = Firebase.auth
                mDatabase = FirebaseDatabase.getInstance()
                mDatabaseReference = mDatabase!!.reference

                currentUserDb.child("lat").removeValue()
                currentUserDb.child("long").removeValue()
                currentUserDb.child("zone").removeValue()
                //mDatabaseReference = mDatabase!!.reference.child("Free")
                val currentDateTime =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalDateTime.now()
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }
                val time =
                    currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        .toString()
                val c = mDatabaseReference!!.child("Free")
                c.child(time).setValue(address)

                home_park!!.text = getString(R.string.park)
                home_pay.isEnabled = false
                home_pay.setBackgroundColor(Color.parseColor("#808080"))
            }
        }
        home_share.setOnClickListener{

            mDatabaseReference = mDatabase!!.reference.child("Users").child(userId)
            val usersId: java.util.ArrayList<String> = java.util.ArrayList()
            //var name: String? = null
            val postListener2 = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI

                    for(childSnapshot in dataSnapshot.child("News").children){
                        if(verifyNews(childSnapshot.key.toString())){
                            mDatabaseReference!!.child("News").child(childSnapshot.key.toString()).removeValue()
                        }
                    }


                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(
                        ContentValues.TAG,
                        "loadPost:onCancelled",
                        databaseError.toException()
                    )
                }
            }
            mDatabaseReference!!.addValueEventListener(postListener2)
            findNavController().navigate(R.id.nav_fragment_main_share)
        }

        home_friends.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_friends))
        mDatabaseReference4!!.removeValue()

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

                    createNotificationChannel()

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    val pendingIntent = TaskStackBuilder.create(requireContext()).run {
                        addNextIntentWithParentStack(intent)
                        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                    }

                    when {
                        containsb -> {
                            Toast.makeText(context, "You are in blue zone", Toast.LENGTH_LONG).show()

                            val notification = NotificationCompat.Builder(
                                requireContext(),
                                CHANNEL_ID
                            )
                                .setContentTitle("Pay Parking")
                                .setContentText("Паркира в синя зона. Платете чрез PayPal или SMS. 2лв./ч.")
                                .setSmallIcon(R.drawable.logo)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentIntent(pendingIntent)
                                .build()
                            val notManager = NotificationManagerCompat.from(requireContext())
                            notManager.notify(NOTIFICATION__ID, notification)

                            currentUserDb.child("zone").setValue("2")
                        }
                        containsg -> {
                            Toast.makeText(context, "You are in green zone", Toast.LENGTH_LONG).show()

                            val notification = NotificationCompat.Builder(
                                requireContext(),
                                CHANNEL_ID
                            )
                                .setContentTitle("Pay Parking")
                                .setContentText("Паркира в зелена зона. Платете чрез PayPal или SMS. 1лв./ч.")
                                .setSmallIcon(R.drawable.logo)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentIntent(pendingIntent)
                                .build()
                            val notManager = NotificationManagerCompat.from(requireContext())
                            notManager.notify(NOTIFICATION__ID, notification)

                            currentUserDb.child("zone").setValue("3")
                        }
                        else -> {
                            Toast.makeText(context, "You are not in zone", Toast.LENGTH_LONG).show()

                            val notification = NotificationCompat.Builder(
                                requireContext(),
                                CHANNEL_ID
                            )
                                .setContentTitle("Pay Parking")
                                .setContentText("Паркирахте извън зоната. Няма нужда да плащате.")
                                .setSmallIcon(R.drawable.logo)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentIntent(pendingIntent)
                                .build()
                            val notManager = NotificationManagerCompat.from(requireContext())
                            notManager.notify(NOTIFICATION__ID, notification)

                            currentUserDb.child("zone").setValue("1")
                        }
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

    @SuppressLint("SimpleDateFormat")
    private fun verifyNews(newsDate: String): Boolean {
        //val toyBornTime = "2014-06-18 12:56:50"
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss"
        )
        val oldDate: Date? = dateFormat.parse(newsDate)

        val currentDate = Date()

        val diff = currentDate.time - oldDate!!.time
        val seconds = diff / 1000
        val minutes = seconds / 60


        if (oldDate.before(currentDate)) {
            if(minutes>15){
                return true
            }
        }
        return false
    }

}