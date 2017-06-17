package me.nontan.spajam_sweets_kun

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import me.nontan.spajam_sweets_kun.models.ReviewCreateRequest
import me.nontan.spajam_sweets_kun.models.ReviewCreateResponse
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
                }

                override fun onFailure(call: Call<ReviewCreateResponse>?, t: Throwable?) {
                    Log.d("reviewcreate", t.toString())
                }
            })
        }

        swtSpn!!.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                when(pos){
                    0 -> {
                        sweets!!.setImageResource(R.drawable.cake)
                    }
                    1 -> {
                        sweets!!.setImageResource(R.drawable.crepe)
                    }
                    2 -> {
                        sweets!!.setImageResource(R.drawable.icecream)
                    }
                    3 -> {
                        sweets!!.setImageResource(R.drawable.kakigoori)
                    }
                    4 -> {
                        sweets!!.setImageResource(R.drawable.pafe)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }

        }
    }
}