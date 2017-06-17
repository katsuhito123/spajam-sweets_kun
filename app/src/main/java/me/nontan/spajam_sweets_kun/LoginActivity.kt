package me.nontan.spajam_sweets_kun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import me.nontan.spajam_sweets_kun.models.AuthenticationResponse
import me.nontan.spajam_sweets_kun.models.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    var userIdForm: EditText? = null
    var passwordForm: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST)
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST)

        userIdForm = findViewById(R.id.user_id) as EditText
        passwordForm = findViewById(R.id.password) as EditText

        val preferences = getPreferences(Context.MODE_PRIVATE)
        userIdForm?.setText(preferences.getString("EMAIL", ""), TextView.BufferType.EDITABLE)
        passwordForm?.setText(preferences.getString("PASSWORD", ""), TextView.BufferType.EDITABLE)

        val loginBtn = findViewById(R.id.login) as Button
        loginBtn.setOnClickListener {

            val userId = userIdForm.let { it?.text.toString() }
            val password = passwordForm.let { it?.text.toString() }

            try {
                val token_call: Call<AuthenticationResponse> = sharedAPIInstance.login(LoginRequest(userId, password))
                val intent : Intent = Intent(this, MainMapsActivity::class.java)
                token_call.enqueue(object: Callback<AuthenticationResponse> {
                    override fun onResponse(call: Call<AuthenticationResponse>, response: Response<AuthenticationResponse>) {
                        Log.d("authentication", "Response: " + response.body().toString());
                        Log.d("authentication", "Error: " + response.errorBody()?.string().toString())

                        response.body()?.authentication?.let { authentication ->
                            intent.putExtra("user_id", authentication.user_id)
                            intent.putExtra("token", authentication.token)

                            accessToken = authentication.token

                            val preferences = getPreferences(Context.MODE_PRIVATE)
                            val editor = preferences.edit()
                            editor.putString("EMAIL", userId)
                            editor.putString("PASSWORD", password)
                            editor.apply()

                            startActivity(intent)
                        }
                    }
                    override fun onFailure(call: Call<AuthenticationResponse>?, t: Throwable?) {
                        Log.d("authentication", t.toString())
                    }
                })
            } catch (e: NumberFormatException) {
                Toast.makeText(this, R.string.login_error_msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

}

