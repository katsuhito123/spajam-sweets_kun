package me.nontan.spajam_sweets_kun

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import me.nontan.spajam_sweets_kun.models.SweetsKun
import java.util.*
import kotlin.concurrent.timer

class MainMapsActivity : FragmentActivity(), OnMapReadyCallback {
    private val handler: Handler = Handler()
    private var mMap: GoogleMap? = null
    private var sweetsKuns: Array<SweetsKun> = arrayOf()
    private var sweetsKunMarker: HashMap<SweetsKun, Marker> = hashMapOf()
    private var sweetsKunPosition: HashMap<SweetsKun, LatLng> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        googleMap.isIndoorEnabled = false
        googleMap.isTrafficEnabled = false
        googleMap.isBuildingsEnabled = false
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        sweetsKuns = arrayOf(SweetsKun(0, 0, 35.0, 135.0))
        onSweetsViewsUpdated()
    }

    fun onSweetsViewsUpdated() {
        val googleMap = mMap?.let { it } ?: return
        for (sweetsKun in sweetsKuns) {
            val pos = LatLng(sweetsKun.latitude, sweetsKun.longitude)
            val markerOpt = MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.cake_kun))
            val marker = googleMap.addMarker(markerOpt)

            sweetsKunMarker[sweetsKun] = marker
            sweetsKunPosition[sweetsKun] = pos
        }

        timer("randomWalkTimer", false, 0, 500, {
            randomWalkSweetsKun()
        })
    }

    fun randomWalkSweetsKun() {
        handler.post {
            for (sweetsKun in sweetsKuns) {
                val marker = sweetsKunMarker[sweetsKun].let { it } ?: continue
                val currentPos = sweetsKunPosition[sweetsKun].let { it } ?: continue
                val newLatitude = currentPos.latitude + Math.random() * 0.001
                val newLongitude = currentPos.longitude + Math.random() * 0.001
                val newPos = LatLng(newLatitude, newLongitude)

                marker.position = newPos
            }
        }
    }
}
