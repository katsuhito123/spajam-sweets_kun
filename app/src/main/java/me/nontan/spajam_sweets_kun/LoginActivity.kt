package me.nontan.spajam_sweets_kun

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import me.nontan.spajam_sweets_kun.models.Authentication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginActivity : AppCompatActivity() {
    var userIdForm: EditText? = null
    var passwordForm: EditText? = null
    var loginService: LoginService = Retrofit
            .Builder()
            .baseUrl("https://private.turenar.xyz/sweetskun/")
            .build()
            .create(LoginService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn = findViewById(R.id.login) as Button
        loginBtn.setOnClickListener {
            userIdForm = findViewById(R.id.user_id) as EditText
            passwordForm = findViewById(R.id.password) as EditText

            val userId = userIdForm.let { it?.text.toString() }
            val password = passwordForm.let { it?.text.toString() }

            try {
                val token_call: Call<Authentication> = loginService.login(userId, password)
                val intent : Intent = Intent(this, HogeActivity::class.java)
                token_call.enqueue(object: Callback<Authentication> {
                    override fun onResponse(call: Call<Authentication>, response: Response<Authentication>) {
                        val gson = Gson()
                        val authentication = gson.fromJson(response.body().toString(), Authentication::class.java)
                        intent.putExtra("token", authentication.token)
                        startActivity(intent)
                    }
                    override fun onFailure(call: Call<Authentication>?, t: Throwable?) {
                        Log.d("authentication", "error")
                    }
                })
            } catch (e: NumberFormatException) {
                Toast.makeText(this, R.string.login_error_msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

