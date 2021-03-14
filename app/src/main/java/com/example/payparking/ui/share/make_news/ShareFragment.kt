package com.example.payparking.ui.share.make_news

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.payparking.R
import com.example.payparking.viewmodel.ShareViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.share_fragment.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ShareFragment : Fragment() {

    companion object {
        fun newInstance() = ShareFragment()
    }

    private lateinit var viewModel: ShareViewModel
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.share_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ShareViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    @SuppressLint("SimpleDateFormat")
    override fun onStart() {
        super.onStart()

        auth = Firebase.auth
        mDatabase = FirebaseDatabase.getInstance()
        //mDatabaseReference = mDatabase!!.reference.child("Users")
        val userId = auth.currentUser!!.uid
        //val currentUserDb = mDatabaseReference!!.child(userId)

        val users: ArrayList<String> = ArrayList()
        var name: String? = null

        val options = resources.getStringArray(R.array.share_spinner)
        if (share_spinner != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, options
            )
            share_spinner.adapter = adapter

            share_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    /*Toast.makeText(requireContext(),
                        options[position], Toast.LENGTH_SHORT).show()*/
                    if(options[position].equals("Only Friends")){
                        mDatabaseReference = mDatabase!!.reference
                        val postListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // Get Post object and use the values to update the UI
                                name = dataSnapshot.child("Users").child(userId).child("Name").value.toString()
                                users.clear()
                                for (childSnapshot in dataSnapshot.child("Friends").child(userId).child(
                                    "Friends"
                                ).children) {
                                    users.add(childSnapshot.key.toString())
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
                        mDatabaseReference!!.addValueEventListener(postListener)
                    }else{
                        mDatabaseReference = mDatabase!!.reference.child("Users")
                        val postListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // Get Post object and use the values to update the UI
                                users.clear()
                                for (childSnapshot in dataSnapshot.children) {
                                    if(childSnapshot.key.toString().equals(userId) == false) {
                                        users.add(childSnapshot.key.toString())
                                    }else{
                                        name = childSnapshot.child("Name").value.toString()
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
                        mDatabaseReference!!.addValueEventListener(postListener)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }



        share_reload.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
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


                        share_address.text = address
                        //Toast.makeText(context, address, Toast.LENGTH_LONG).show()

                    }
                }
            }
        }


        share_submit.setOnClickListener {

            if(validateDescription()) {

                mDatabaseReference = mDatabase!!.reference.child("Users")
                val currentDateTime =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalDateTime.now()
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }
                val time =
                    currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        .toString()
                for (user in users) {
                    mDatabaseReference!!.child(user).child("News").child(time).child("Description")
                        .setValue(
                            share_description!!.text.toString()
                        )
                    mDatabaseReference!!.child(user).child("News").child(time).child("Address")
                        .setValue(
                            share_address!!.text.toString()
                        )
                    mDatabaseReference!!.child(user).child("News").child(time).child("Author")
                        .setValue(
                            name!!.toString()
                        )

                }
                mDatabaseReference!!.child(userId).child("Send_News").child(time)
                    .child("Description").setValue(
                    share_description!!.text.toString()
                )
                mDatabaseReference!!.child(userId).child("Send_News").child(time).child("Address")
                    .setValue(
                        share_address!!.text.toString()
                    )
                mDatabaseReference!!.child(userId).child("Send_News").child(time).child("Author")
                    .setValue(
                        name!!.toString()
                    )

                Navigation.createNavigateOnClickListener(R.id.nav_fragment_main_share)
            }
        }

    }

    private fun validateDescription(): Boolean {
        var valid = true

        val description = share_description.text.toString()
        if (TextUtils.isEmpty(description)) {
            share_description.error = "Required."
            valid = false
        } else {
            share_description.error = null
        }

        return valid
    }

}