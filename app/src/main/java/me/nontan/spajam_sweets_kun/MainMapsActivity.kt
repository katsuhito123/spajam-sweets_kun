package me.nontan.spajam_sweets_kun

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentActivity
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import me.nontan.spajam_sweets_kun.models.SweetsKun
import me.nontan.spajam_sweets_kun.views.PopupView
import me.nontan.spajam_sweets_kun.views.SweetsInfoLayout
import java.util.*
import kotlin.concurrent.timer

class MainMapsActivity : FragmentActivity(), OnMapReadyCallback {
    private val handler: Handler = Handler()
    private var mMap: GoogleMap? = null
    private var sweetsKuns: Array<SweetsKun> = arrayOf()
    private var sweetsKunMarker: HashMap<SweetsKun, Marker> = hashMapOf()
    private var sweetsKunPosition: HashMap<SweetsKun, LatLng> = hashMapOf()
    private var popupView: PopupView? = null
    private var floatingActionButton: FloatingActionButton? = null

    private var userId: Int = 0
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        popupView = findViewById(R.id.popup_view) as PopupView
        floatingActionButton = findViewById(R.id.map_floating_action_button) as FloatingActionButton
        floatingActionButton?.setOnClickListener {
            onClickFloatingActionButton()
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()

        val userId = intent.getIntExtra("user_id", 0)
        val token = intent.getStringExtra("token") ?: throw Exception("No token")

        this.userId = userId
        this.token = token
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

        googleMap.setOnMarkerClickListener { marker ->
            this.onMarkerClick(marker)
        }

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

    fun kokunoaruRandom(): Double {
        var sum = 0.0
        for (i in 1..5) {
            sum += Math.random() * 0.001
        }

        return sum / 5
    }

    fun randomWalkSweetsKun() {
        handler.post {
            for (sweetsKun in sweetsKuns) {
                val marker = sweetsKunMarker[sweetsKun].let { it } ?: continue
                val currentPos = sweetsKunPosition[sweetsKun].let { it } ?: continue
                val newLatitude = currentPos.latitude + kokunoaruRandom()
                val newLongitude = currentPos.longitude + kokunoaruRandom()
                val newPos = LatLng(newLatitude, newLongitude)

                marker.position = newPos
            }
        }
    }

    fun onMarkerClick(marker: Marker): Boolean {
        val googleMap = mMap?.let { it } ?: return false

        val position = marker.position
        val screenLocation = googleMap.projection.toScreenLocation(position)
        val x = screenLocation.x
        val y = screenLocation.y

        val sweetsInfoLayout = SweetsInfoLayout(this)
        sweetsInfoLayout.setBackgroundColor(Color.WHITE)
        sweetsInfoLayout.visibility = View.VISIBLE
        popupView?.setMaxHeight(800)
        popupView?.setContentView(sweetsInfoLayout)
        popupView?.setDismissOnTouchOutside(true)
        popupView?.show(Rect(x, y, x, y), PopupView.AnchorGravity.AUTO, 300, 0)
        return true
    }

    fun onClickFloatingActionButton() {
        val intent = Intent(this, ShopsListActivity::class.java)
        startActivity(intent)
    }
}
