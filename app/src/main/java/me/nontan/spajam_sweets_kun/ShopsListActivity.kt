package me.nontan.spajam_sweets_kun

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import android.widget.SimpleAdapter
import me.nontan.spajam_sweets_kun.models.ShopSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopsListActivity : AppCompatActivity() {
    lateinit var lv: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shops_list)

        lv = findViewById(R.id.lv) as ListView
    }

    override fun onResume() {
        super.onResume()

        val lat_min = intent.getDoubleExtra("lat_min", 0.0)
        val lat_max = intent.getDoubleExtra("lat_max", 0.0)
        val long_min = intent.getDoubleExtra("long_min", 0.0)
        val long_max = intent.getDoubleExtra("long_max", 0.0)

        val activity = this

        println("$lat_min, $lat_max, $long_min, $long_max")
        val shopSearchCall = sharedAPIInstance.shopSearch(lat_min, lat_max, long_min, long_max)
        // val shopSearchCall = sharedAPIInstance.shopSearch("35.697372", "35.697379", "139.7100", "139.750139")
        shopSearchCall.enqueue(object: Callback<ShopSearchResponse> {
            override fun onResponse(call: Call<ShopSearchResponse>, response: Response<ShopSearchResponse>) {
                Log.d("shopsearch", "Response: " + response.body().toString());
                Log.d("shopsearch", "Error: " + response.errorBody()?.string().toString())

                response.body()?.shop?.let { shops ->
                    val listData = ArrayList<Map<String, String>>()
                    val temp_array = arrayOf("shopName","address")

                    for (shop in shops) {
                        val item = HashMap<String, String>()
                        item.put("shopName", shop.name)
                        item.put("address", shop.address)
                        listData.add(item)
                    }

                    val adapter = SimpleAdapter(activity, listData, R.layout.shops_list_sub,
                            temp_array, intArrayOf(R.id.shopName,R.id.shopAddress))
                    lv.adapter = adapter

                    lv.setOnItemClickListener { parent, view, position, id ->
                        val intent = Intent(activity, ReviewActivity::class.java)
                        intent.putExtra("shop_id", shops[position].shop_id)
                        Log.d("shopslist", "" + shops[position].shop_id)
                        startActivity(intent)
                    }
                }
            }
            override fun onFailure(call: Call<ShopSearchResponse>?, t: Throwable?) {
                Log.d("shopsearch", t.toString())
            }
        })
    }
}
