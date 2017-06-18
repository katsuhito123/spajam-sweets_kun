package me.nontan.spajam_sweets_kun.views

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import me.nontan.spajam_sweets_kun.R
import me.nontan.spajam_sweets_kun.models.Review
import me.nontan.spajam_sweets_kun.models.ShopResponse
import me.nontan.spajam_sweets_kun.utilities.sharedAPIInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SweetsInfoLayout: LinearLayout {
    var shopNameTextView: TextView
    var reviewTextView: TextView
    var ratingBar: RatingBar

    constructor(context: Context): super(context) {
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.sweets_info_layout, this)

        shopNameTextView = findViewById(R.id.shopNameTextView) as TextView
        reviewTextView = findViewById(R.id.reviewTextView) as TextView
        ratingBar = findViewById(R.id.ratingBar) as RatingBar
    }

    fun showReview(review: Review) {
        reviewTextView.text = review.review_text
        ratingBar.rating = review.rating.toFloat()
        shopNameTextView.text = "..."

        val handler = Handler()
        val shopCall = sharedAPIInstance.shop(review.shop_id)
        shopCall.enqueue(object: Callback<ShopResponse> {
            override fun onResponse(call: Call<ShopResponse>, response: Response<ShopResponse>) {
                Log.d("shop", "Response: " + response.body().toString())
                Log.d("shop", "Error: " + response.errorBody()?.string().toString())

                handler.post {
                    val name = response.body()?.shop?.name ?: return@post
                    shopNameTextView.text = name
                }
            }

            override fun onFailure(call: Call<ShopResponse>?, t: Throwable?) {
                Log.d("shop", t.toString())
            }
        })
    }
}
