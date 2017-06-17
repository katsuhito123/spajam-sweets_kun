package me.nontan.spajam_sweets_kun

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.google.gson.Gson

class ReviewActivity : AppCompatActivity() {
    var img: ImageView? = null
    var rateBar:RatingBar? = null
    var swtSpn:Spinner? = null
    var review:EditText? = null
    var sendBtn:Button? = null
    var id:Int? = null
    var rate:Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        img = findViewById(R.id.Image) as ImageView
        rateBar = findViewById(R.id.ratingBar) as RatingBar
        swtSpn = findViewById(R.id.spinner) as Spinner
        review = findViewById(R.id.editText) as EditText
        sendBtn = findViewById(R.id.button) as Button

        val intent = this.intent
        id = intent.getIntExtra("shop_id",0)
        rate = 3;

        rateBar!!.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            rate = rating.toInt()
        }

        sendBtn!!.setOnClickListener {
            val shop_id = id!!
            val rateNum = rate!!
            val review_text = review!!.text.toString()
            val sweet_type = swtSpn!!.selectedItemPosition
//        intent = Intent(this,MainMapsActivity::class.java)
//        intent.putExtra("shop_id",shop_id)
//            Toast.makeText(this,"id = "+shop_id+",rateNum = "+rateNum+ ",review_text = "+review_text+",sweet_type = "+sweet_type,Toast.LENGTH_SHORT).show()

            //var gson: Gson = Gson()
            //val review:Review = Review(shop_id,rateNum,review_text,sweet_type)
//            gson.toJson(review)
            //Toast.makeText(this,gson.toJson(review).toString(),Toast.LENGTH_LONG).show()
        }
    }
}