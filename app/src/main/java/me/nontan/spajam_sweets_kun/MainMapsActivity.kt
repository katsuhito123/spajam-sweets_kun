package me.nontan.spajam_sweets_kun

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import me.nontan.spajam_sweets_kun.models.Bounds
import me.nontan.spajam_sweets_kun.models.Review
import me.nontan.spajam_sweets_kun.models.ReviewSearchResponse
import me.nontan.spajam_sweets_kun.utilities.iconNumberToResource
import me.nontan.spajam_sweets_kun.utilities.sharedAPIInstance
import me.nontan.spajam_sweets_kun.utilities.shrinkIcon
import me.nontan.spajam_sweets_kun.views.PopupView
import me.nontan.spajam_sweets_kun.views.SweetsInfoLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.timer

class MainMapsActivity : FragmentActivity(), OnMapReadyCallback {
    private val handler: Handler = Handler()
    private lateinit var googleMap: GoogleMap
    private var reviews: Array<Review> = arrayOf()
    private var reviewMarker: HashMap<Review, Marker> = hashMapOf()
    private var markerReview: HashMap<Marker, Review> = hashMapOf()
    private var reviewPosition: HashMap<Review, LatLng> = hashMapOf()
    private lateinit var popupView: PopupView
    private lateinit var floatingActionButton: FloatingActionButton

    private var userId: Int = 0
    private var token: String = ""

    private var iconBitmaps: Array<BitmapDescriptor> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        popupView = findViewById(R.id.popup_view) as PopupView
        floatingActionButton = findViewById(R.id.map_floating_action_button) as FloatingActionButton
        floatingActionButton.setOnClickListener {
            onClickFloatingActionButton()
        }

        val maxSize = 48
        for (i in 0..5) {
            val resourceId = iconNumberToResource(i)
            val rawBitmap = BitmapFactory.decodeResource(resources, resourceId)
            val resizedBitmap = shrinkIcon(rawBitmap, maxSize)
            val resizedBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
            iconBitmaps = iconBitmaps.plusElement(resizedBitmapDescriptor)
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
        this.googleMap = googleMap

        googleMap.setOnMarkerClickListener { marker ->
            this.onMarkerClick(marker)
        }

        googleMap.setOnCameraIdleListener {
            this.onCameraIdle()
        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        googleMap.isIndoorEnabled = false
        googleMap.isTrafficEnabled = false
        googleMap.isBuildingsEnabled = false
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        onSweetsViewsUpdated()
    }

    fun onSweetsViewsUpdated() {
        for (sweetsKun in reviews) {
            val pos = LatLng(sweetsKun.latitude, sweetsKun.longitude)
            val markerOpt = MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.cake_kun))
            val marker = googleMap.addMarker(markerOpt)

            reviewMarker[sweetsKun] = marker
            reviewPosition[sweetsKun] = pos
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
            if (popupView.isShowing) {
                return@post
            }

            for (sweetsKun in reviews) {
                val marker = reviewMarker[sweetsKun].let { it } ?: continue
                val currentPos = reviewPosition[sweetsKun].let { it } ?: continue
                val newLatitude = currentPos.latitude + kokunoaruRandom()
                val newLongitude = currentPos.longitude + kokunoaruRandom()
                val newPos = LatLng(newLatitude, newLongitude)

                marker.position = newPos
            }
        }
    }

    fun onMarkerClick(marker: Marker): Boolean {
        val position = marker.position
        val screenLocation = googleMap.projection.toScreenLocation(position)
        val x = screenLocation.x
        val y = screenLocation.y

        val sweetsInfoLayout = SweetsInfoLayout(this)
        markerReview[marker]?.let {
            sweetsInfoLayout.showReview(it)
        }
        sweetsInfoLayout.setBackgroundColor(Color.WHITE)
        sweetsInfoLayout.visibility = View.VISIBLE
        popupView.setMaxHeight(600)
        popupView.setContentView(sweetsInfoLayout)
        popupView.show(Rect(x, y, x, y), PopupView.AnchorGravity.AUTO, 300, 0)
        return true
    }

    fun onClickFloatingActionButton() {
        val bounds = getBounds()

        val intent = Intent(this, ShopsListActivity::class.java)
        intent.putExtra("lat_min", bounds.lat_min)
        intent.putExtra("lat_max", bounds.lat_max)
        intent.putExtra("long_min", bounds.long_min)
        intent.putExtra("long_max", bounds.long_max)

        startActivity(intent)
    }

    fun getBounds(): Bounds {
        val bounds = googleMap.projection.visibleRegion.latLngBounds
        val northeast = bounds.northeast
        val southwest = bounds.southwest

        val lat_max = northeast.latitude
        val lat_min = southwest.latitude

        val long_max = northeast.longitude
        val long_min = southwest.longitude

        return Bounds(lat_max, lat_min, long_max, long_min)
    }

    fun onCameraIdle() {
        val bounds = getBounds()

        val lat_min = bounds.lat_min
        val lat_max = bounds.lat_max
        val long_min = bounds.long_min
        val long_max = bounds.long_max

        val reviewSearchCall = sharedAPIInstance.reviewSearch(lat_min, lat_max, long_min, long_max)
        reviewSearchCall.enqueue(object: Callback<ReviewSearchResponse> {
            override fun onResponse(call: Call<ReviewSearchResponse>, response: Response<ReviewSearchResponse>) {
                Log.d("reviewsearch", "Response: " + response.body().toString());
                Log.d("reviewsearch", "Error: " + response.errorBody()?.string().toString())

                response.body()?.review?.let { newReviews ->
                    Log.d("reviewsearch", "num newReviews: " + newReviews.size)
                    handler.post {
                        val oldReviews = reviews.clone()
                        for (newReview in newReviews) {
                            val findResult = oldReviews.find { it.review_id == newReview.review_id }
                            if (findResult == null) { // newly appeared
                                val pos = LatLng(newReview.latitude, newReview.longitude)
                                val markerOpt = MarkerOptions().position(pos).icon(iconBitmaps[newReview.sweet_type])
                                val marker = googleMap.addMarker(markerOpt)

                                reviewMarker[newReview] = marker
                                markerReview[marker] = newReview
                                reviewPosition[newReview] = pos
                            }
                        }

                        for (oldReview in oldReviews) {
                            val findResult = newReviews.find { it.review_id == oldReview.review_id }
                            if (findResult == null) { // disappeared
                                reviewMarker[oldReview]?.let {
                                    markerReview.remove(it)
                                }
                                reviewMarker[oldReview]?.remove()
                                reviewMarker.remove(oldReview)
                                reviewPosition.remove(oldReview)
                            }
                        }
                        reviews = newReviews.clone()
                    }
                }
            }

            override fun onFailure(call: Call<ReviewSearchResponse>?, t: Throwable?) {
                Log.d("reviewsearch", t.toString())
            }
        })
    }
}
