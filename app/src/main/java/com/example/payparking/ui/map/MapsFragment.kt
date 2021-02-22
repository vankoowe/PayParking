package com.example.payparking.ui.map

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.payparking.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import androidx.annotation.RawRes
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MapsFragment : Fragment() {

    private lateinit var mGoogleMap: GoogleMap

    companion object {
        fun newInstance() = MapsFragment()
    }

    private val callback = OnMapReadyCallback { googleMap ->

        var latLngs: List<LatLng?>? = null

        try {
            latLngs = readItems()
        } catch (e: JSONException) {
            Toast.makeText(context, "Problem reading list of locations.", Toast.LENGTH_LONG)
                .show()
        }

        val colors = intArrayOf(
            Color.rgb(102, 225, 0),  // green
            Color.rgb(255, 0, 0) // red
        )
        val startPoints = floatArrayOf(0.2f, 1f)
        val gradient = Gradient(colors, startPoints)

        val provider = HeatmapTileProvider.Builder()
            .data(latLngs)
            .gradient(gradient)
            .build()

        val tileOverlay = googleMap?.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(provider)
        )
        provider.setOpacity(0.7)
        tileOverlay?.clearTileCache()

        val greenZone = googleMap.addPolygon(PolygonOptions()
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

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(42.70, 23.33), 6f))
    }

    @Throws(JSONException::class)
    private fun readItems(): List<LatLng?> {
        val result: MutableList<LatLng?> = ArrayList()

        result.add(LatLng(-35.016, 143.321))
        result.add(LatLng(-34.747, 145.592))
        result.add(LatLng(-34.364, 147.891))
        result.add(LatLng(-33.501, 150.217))
        result.add(LatLng(-32.306, 149.248))
        result.add(LatLng(-32.491, 147.309))
        return result
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
