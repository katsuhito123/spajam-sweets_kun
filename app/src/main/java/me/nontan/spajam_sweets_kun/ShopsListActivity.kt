package me.nontan.spajam_sweets_kun

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import com.google.gson.Gson

class ShopsListActivity : AppCompatActivity() {
    var lv: ListView? = null
    var shop_id:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shops_list)
//
//        data class Shop(
//                val shop_id:Int,
//                val name:String,
//                val address:String,
//                val latitude:Double,
//                val longitude:Double,
//                val review:String

        val s = """{
                    "shop_id": 123,
                    "name": "name0",
                    "address": "address0",
                    "latitude": "latitude0",
                    "longtitude": "longtitude0"
                    "review": "review0"
                    }"""
        val gson = Gson()
        val x:Array<Shop> = gson.fromJson(s, Array<Shop>::class.java)

        val listData = ArrayList<Map<String, String>>()
        val temp_array:Array<String> = arrayOf("shopName","address")

//        val arrayName:Array<String> = arrayOf("家家家","やーやーやー","huousehouse")
//        val arrayAddress:Array<String> = arrayOf("本郷","駒場","柏")
//        for (i in 0..2){
//            val item = HashMap<String,String>()
//            item.put("shopName",arrayName[i])
//            item.put("address",arrayAddress[i])
//            listData.add(item)
//        }

        for(i in x.indices){
            val item = HashMap<String,String>()
            item.put("shopName",x[i].name)
            item.put("address",x[i].address)
            listData.add(item)
        }

        lv = findViewById(R.id.lv) as ListView?
        var adapter = SimpleAdapter(this,listData,R.layout.shops_list_sub,
                temp_array, intArrayOf(R.id.shopName,R.id.shopAddress))
        lv!!.adapter = adapter

        lv!!.setOnItemClickListener { parent, view, position, id ->
            var intent = Intent(this,ReviewActivity::class.java)
            intent.putExtra("shop_id",x[position].shop_id)
            Toast.makeText(this,"shop_id = "+shop_id,Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }
}

//
//class DownloadShopsList : AsyncTask<Void, Void, String>(){
//    override fun onPreExecute() {
//        super.onPreExecute()
//    }
//
//    override fun doInBackground(vararg params: Void?): String? {
//        var con: HttpURLConnection? = null
//        var url: URL? = null
//        val urlSt = ""
//        try {
//            // URLの作成
//            url = URL(urlSt)
//            // 接続用HttpURLConnectionオブジェクト作成
//            con = url.openConnection() as HttpURLConnection
//            // リクエストメソッドの設定
//            con.requestMethod = "GET"
//            // リダイレクトを自動で許可しない設定
//            con.instanceFollowRedirects = false
//            // URL接続からデータを読み取る場合はtrue
//            con.doInput = true
//            // URL接続にデータを書き込む場合はtrue
//            con.doOutput = true
//
//            // 接続
//            con.connect() // ①
//
//            var input = con.inputStream
//
//
//            fun readInputStream(input: InputStream):String{
//                var sb:StringBuffer = StringBuffer()
//                var st:String? = ""
//                val br = BufferedReader(InputStreamReader(input, "UTF-8"))
//                while (true) {
//                    sb.append(st)
//                    st=br.readLine()
//                    if(st==null) break
//                }
//                try {
//                    input.close()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                return sb.toString()
//            }
//            var readSt:String = readInputStream(input)
//
//
//        } catch (e: MalformedURLException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//
//
//        return null
//
//    }
//
//    override fun onPostExecute(result: String?) {
//        super.onPostExecute(result)
//    }
//}