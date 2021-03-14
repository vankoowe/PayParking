package com.example.payparking.ui.profile

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.payparking.R
import com.example.payparking.module.Coordinates
import com.example.payparking.viewmodel.CustomViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.custom_fragment.*
import kotlinx.android.synthetic.main.number_scan_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.*
import org.json.JSONObject
import java.io.*
import java.util.*


class CustomFragment : Fragment() {

    companion object {
        fun newInstance() = CustomFragment()
    }

    private lateinit var viewModel: CustomViewModel

    private lateinit var map: GoogleMap
    val result: MutableList<LatLng?> = ArrayList()
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var polygon: Polygon? = null
    private var latLngList: MutableList<LatLng?> = ArrayList()
    private var markerList: MutableList<Marker?> = ArrayList()
var v =true
    private val callback = OnMapReadyCallback { googleMap ->

        map = googleMap
        map.setOnMapClickListener {
            map.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
                override fun onMapClick(latlng: LatLng?) {

                    val marker: Marker = map.addMarker(
                        MarkerOptions()
                            .position(latlng!!)
                        //.clickable(true)
                    )

                    latLngList?.add(latlng)
                    markerList?.add(marker)
                }
            })

        }
        map.setOnMarkerClickListener { marker ->
            marker.remove()
            latLngList.remove(marker.position)
            markerList?.remove(marker)

            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.custom_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CustomViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        mDatabase = FirebaseDatabase.getInstance()
        val userId = auth.currentUser!!.uid
        mDatabaseReference = mDatabase!!.reference.child("Users").child(userId)

        val currentUserDb = mDatabaseReference!!.child("Custom")


            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    var string: MutableList<String?> = ArrayList()
                    if(dataSnapshot.value!=null) {

                        for (childSnapshot in dataSnapshot.children) {
                            if (childSnapshot.child("lat").value != null && childSnapshot.child("long").value != null) {
                                latLngList.add(
                                    LatLng(
                                        childSnapshot.child("lat").value as Double,
                                        childSnapshot.child("long").value as Double
                                    )
                                )
                                val marker: Marker = map.addMarker(
                                    MarkerOptions()
                                        .position(
                                            LatLng(
                                                childSnapshot.child("lat").value as Double,
                                                childSnapshot.child("long").value as Double
                                            )
                                        )
                                )
                                markerList?.add(marker)
                            }

                        }


                    }
                    //ggggg.text = result.toString()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }
            currentUserDb!!.addValueEventListener(postListener)



        bt_draw.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View?) {
                if (polygon != null) polygon!!.remove()

                polygon = map.addPolygon(
                    PolygonOptions()
                        .clickable(true)
                        .addAll(latLngList)
                        .strokeColor(Color.rgb(45, 4, 120))
                        .fillColor(Color.rgb(45, 4, 120))
                )
            }

        })


        submit.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View?) {


                var i = 0
                currentUserDb.removeValue()
                for (lanlng in latLngList) {
                    if (lanlng != null) {
                        currentUserDb.child(i.toString()).child("lat").setValue(lanlng.latitude)
                    }
                    if (lanlng != null) {
                        currentUserDb.child(i.toString()).child("long").setValue(lanlng.longitude)
                    }
                    i++

                }
            }

        })
    }
}

