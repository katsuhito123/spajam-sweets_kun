package me.nontan.spajam_sweets_kun

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.telecom.Call
import android.view.View
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    var userIdForm: EditText? = null
    var passwordForm: EditText? = null
    var loginService: LoginService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val retrofit= Retrofit.Builder()
                .baseUrl("https://private.turenar.xyz/sweetskun/")
                .build()

        loginService = retrofit.create(LoginService::class.java)
    }

    fun onClickLoginButton(view: View) {
        userIdForm = findViewById(R.id.user_id) as EditText
        passwordForm = findViewById(R.id.password) as EditText
        var userId: String? = null
        var password: String? = null

        try {
            userId = userIdForm!!.text.toString()
            password = passwordForm!!.text.toString()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, R.string.input_error_msg, Toast.LENGTH_SHORT).show()
        }

        try {
            val token: Call<Token> = loginService.login(userId, password)
            val intent : Intent = Intent(this, HogeActivity::class.java)
            intent.putExtra("token", token.token)
            startActivity(intent)
        } catch (e: NumberFormatException) {
            Toast.makeText(this, R.string.login_error_msg, Toast.LENGTH_SHORT).show()
        }
    }
}
