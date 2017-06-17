package me.nontan.spajam_sweets_kun

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast

class ShopsListActivity : AppCompatActivity() {
    var lv: ListView? = null
    var shop_id:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shops_list)

        val arrayName:Array<String> = arrayOf("家家家","やーやーやー","huousehouse")
        val temp_array:Array<String> = arrayOf("shopName","address")
        val arrayAddress:Array<String> = arrayOf("本郷","駒場","柏")
        lv = findViewById(R.id.lv) as ListView?

        val listData = ArrayList<Map<String, String>>()
        for (i in 0..2){
            val item = HashMap<String,String>()
            item.put("shopName",arrayName[i])
            item.put("address",arrayAddress[i])
            listData.add(item)
        }

        var adapter = SimpleAdapter(this,listData,R.layout.shops_list_sub,
                temp_array, intArrayOf(R.id.shopName,R.id.shopAddress))
        lv!!.adapter = adapter

        lv!!.setOnItemClickListener { parent, view, position, id ->
            var intent = Intent(this,ReviewActivity::class.java)
            intent.putExtra("shop_id",0)
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