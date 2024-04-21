package com.ar.sebastiangomez.steam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var userName = "sgomez"
        var selectID = 1234

        //global sharedpreferences
        var prefs = getSharedPreferences("com.ar.sebastiangomez.SteamFan.sharedpref", Context.MODE_PRIVATE)
        prefs.edit().putString("user", userName).apply()

        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("ID", selectID)
            startActivity(intent)
            finish()
        },4000)
    }
}