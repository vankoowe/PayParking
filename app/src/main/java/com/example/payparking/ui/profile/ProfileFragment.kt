package com.example.payparking.ui.profile

import android.content.ContentValues
import android.graphics.Color
import android.graphics.Insets.add
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.payparking.R
import com.example.payparking.viewmodel.ProfileViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.profile_fragment.*
import java.util.*


class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel

    private lateinit var map: GoogleMap


    private var polygon: Polygon? = null
    private var latLngList: MutableList<LatLng?> = ArrayList()
    private var markerList: MutableList<Marker?> = ArrayList()
    private var mAuth: FirebaseAuth? = null
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }


    override fun onStart() {
        super.onStart()
        mAuth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        val userId = auth.currentUser!!.uid

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users").child(userId)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val name = dataSnapshot.child("Name").value.toString()
                val email = dataSnapshot.child("E-mail").value.toString()
                profile_name.text = name
                profile_email.text = "@"+email
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference!!.addValueEventListener(postListener)

        profile_support.setOnClickListener {
            //val email = edtResetEmail.text.toString().trim()
            auth = Firebase.auth
            val userId = auth.currentUser!!.uid
            mDatabase = FirebaseDatabase.getInstance()
            mDatabaseReference = mDatabase!!.reference.child("Users").child(userId)
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val email = dataSnapshot.child("E-mail").value.toString()
                    mAuth!!.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Support sent email", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Support failed to sent email", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }
            mDatabaseReference!!.addValueEventListener(postListener)
        }
        profile_payments.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_payments))
        profile_cars.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_mycars))
    }
}
