package me.nontan.spajam_sweets_kun

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import me.nontan.spajam_sweets_kun.models.ReviewCreateRequest
import me.nontan.spajam_sweets_kun.models.ReviewCreateResponse
import me.nontan.spajam_sweets_kun.utilities.accessToken
import me.nontan.spajam_sweets_kun.utilities.iconNumberToResource
import me.nontan.spajam_sweets_kun.utilities.sharedAPIInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewActivity : AppCompatActivity() {
    lateinit var img: ImageView
    lateinit var rateBar: RatingBar
    lateinit var swtSpn: Spinner
    lateinit var review: EditText
    lateinit var sendBtn: Button
    lateinit var sweets: ImageView

    var id: Int = 0
    var rate: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        img = findViewById(R.id.Image) as ImageView
        rateBar = findViewById(R.id.ratingBar) as RatingBar
        swtSpn = findViewById(R.id.spinner) as Spinner
        review = findViewById(R.id.editText) as EditText
        sendBtn = findViewById(R.id.button) as Button
        sweets = findViewById(R.id.sweets) as ImageView

        val intent = this.intent
        id = intent.getIntExtra("shop_id", 0)
        rate = 3;

        val activity = this

        rateBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            rate = rating.toInt()
        }

        sendBtn.setOnClickListener {
            val shop_id = id
            val rateNum = rate
            val review_text = review.text.toString()
            val sweet_type = swtSpn.selectedItemPosition

            val request = ReviewCreateRequest(shop_id, rateNum, review_text, sweet_type)
            val call = sharedAPIInstance.reviewCreate("Bearer " + accessToken, request)
            call.enqueue(object : Callback<ReviewCreateResponse> {
                override fun onResponse(call: Call<ReviewCreateResponse>, response: Response<ReviewCreateResponse>) {
                    Log.d("reviewcreate", "Response: " + response.body().toString());
                    Log.d("reviewcreate", "Error: " + response.errorBody()?.string().toString())

                    val intent = Intent(activity, MainMapsActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onFailure(call: Call<ReviewCreateResponse>?, t: Throwable?) {
                    Log.d("reviewcreate", t.toString())
                }
            })
        }

        swtSpn.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                val resourceId = iconNumberToResource(pos)
                sweets.setImageResource(resourceId)
            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }

        }
    }
}