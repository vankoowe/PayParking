package com.example.payparking.ui.map.heatmap

import android.content.ContentValues
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.payparking.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class HeatMapFragment : Fragment() {


    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    companion object {
        fun newInstance() = HeatMapFragment()
    }

    private val callback = OnMapReadyCallback { googleMap ->

        auth = Firebase.auth
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Heat_Map")
        val coord = ArrayList<LatLng>()
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (childSnapshot in dataSnapshot.children) {
                    if(childSnapshot.child("lat").value !=null && childSnapshot.child("long").value !=null){
                    coord.add(LatLng(childSnapshot.child("lat").value as Double, childSnapshot.child("long").value as Double) )}
                }
                val colors = intArrayOf(
                    Color.rgb(102, 225, 0),  // green
                    Color.rgb(255, 0, 0) // red
                )
                val startPoints = floatArrayOf(0.2f, 1f)
                val gradient = Gradient(colors, startPoints)

                val provider = HeatmapTileProvider.Builder()
                    .data(coord)
                    .gradient(gradient)
                    .build()

                val tileOverlay = googleMap?.addTileOverlay(
                    TileOverlayOptions()
                        .tileProvider(provider)
                )
                provider.setOpacity(0.7)
                tileOverlay?.clearTileCache()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference!!.addValueEventListener(postListener)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(42.70, 23.33), 11f))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.heat_map_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapheat) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}