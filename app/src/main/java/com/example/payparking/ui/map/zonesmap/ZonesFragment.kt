package com.example.payparking.ui.map.zonesmap

import android.app.AlertDialog
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RawRes
import com.example.payparking.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ZonesFragment : Fragment() {

    companion object {
        fun newInstance() = ZonesFragment()
    }

    private val callback = OnMapReadyCallback { googleMap ->

        val greenZone = googleMap.addPolygon(
            PolygonOptions()
            .clickable(true)
            .addAll(
                readZones(R.raw.greenzones))
            .strokeColor(Color.GREEN)
            .fillColor(Color.GREEN))

        val blueZone = googleMap.addPolygon(PolygonOptions()
            .clickable(true)
            .addAll(
                readZones(R.raw.bluezones))
            .strokeColor(Color.BLUE)
            .fillColor(Color.BLUE))

        googleMap.setOnPolygonClickListener(GoogleMap.OnPolygonClickListener {
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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(42.70, 23.33), 11f))
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

}