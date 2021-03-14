package com.example.payparking.ui.map.zonesmap

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.payparking.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.custom_fragment.*
import kotlinx.android.synthetic.main.zones_fragment.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ZonesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 1
    val result: MutableList<LatLng?> = ArrayList()
    private var custom: Polygon? = null
    private var markerList: MutableList<Marker?> = ArrayList()

    companion object {
        fun newInstance() = ZonesFragment()
    }

    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }
        else
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled = true
            }
            else {
                Toast.makeText(activity, "User has not granted location access permission", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        val greenZone = map.addPolygon(
            PolygonOptions()
                .clickable(true)
                .addAll(
                    readZones(R.raw.greenzones))
                .strokeColor(Color.GREEN)
                .fillColor(Color.GREEN))

        val blueZone = map.addPolygon(PolygonOptions()
            .clickable(true)
            .addAll(
                readZones(R.raw.bluezones))
            .strokeColor(Color.BLUE)
            .fillColor(Color.BLUE))

        map.setOnPolygonClickListener(GoogleMap.OnPolygonClickListener {
            if(it == blueZone) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Синя зона")
                builder.setMessage("Работно време:\n" +
                        "Максимална продължителност на паркиране до 2 (два) часа;\n" +
                        "Цена - 2 лева на час;\n" +
                        "Работни дни в часовия диапазон от 08.30 до 19.30 часа;\n" +
                        "Събота - в часовия диапазон от 10.00 до 18.00 часа.")
                val mDialog = builder.create()
                mDialog.show()
            } else if(it == greenZone) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Зелена зона")
                builder.setMessage("Работно време:\n" +
                        "Максимална продължителност на паркиране до 4 (четири) часа;\n" +
                        "Цена - 1 лева на час\n" +
                        "Работни дни в часовия диапазон от 08.30 до 19.30 часа;\n" +
                        "Събота - в часовия диапазон от 10.00 до 18.00 часа.")
                val mDialog = builder.create()
                mDialog.show()
            }

        })
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(42.70, 23.33), 11f))

        //map = googleMap
        getLocationAccess()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.zones_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapzone) as SupportMapFragment?
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
                                result.add(
                                    LatLng(
                                        childSnapshot.child("lat").value as Double,
                                        childSnapshot.child("long").value as Double
                                    )
                                )
                                /*val marker: Marker = map.addMarker(
                                    MarkerOptions()
                                        .position(
                                            LatLng(
                                                childSnapshot.child("lat").value as Double,
                                                childSnapshot.child("long").value as Double
                                            )
                                        )

                                    //.clickable(true)
                                )
                                markerList?.add(marker)*/

                                //string.add(childSnapshot.child("lat").value.toString())
                            }
                        }
                        if(!result.isEmpty()) {
                            custom = map.addPolygon(
                                PolygonOptions()
                                    .clickable(true)
                                    .addAll(
                                        result
                                    )
                                    .strokeColor(Color.GREEN)
                                    .fillColor(Color.RED)
                            )
                        }
                        //ggggg.text = "ertyjk"
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }
            currentUserDb!!.addValueEventListener(postListener)

    }
}