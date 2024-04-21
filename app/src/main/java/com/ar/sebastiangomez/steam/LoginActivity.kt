package com.ar.sebastiangomez.steam

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var prefs = getSharedPreferences("com.ar.sebastiangomez.SteamFan.sharedpref", Context.MODE_PRIVATE)
        var userName = prefs.getString("user", "")
        val tag = "LOG-USER"
        Log.d(tag, userName.toString())

        //Leer ID del Intent
        var ID = intent.extras!!.getInt("ID")
        Log.d(tag, ID.toString())

    }
}