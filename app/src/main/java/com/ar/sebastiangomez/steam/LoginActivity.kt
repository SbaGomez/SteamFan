package com.ar.sebastiangomez.steam

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.utils.ThemeManager

class LoginActivity : AppCompatActivity() {

    private lateinit var themeManager: ThemeManager
    override fun onCreate(savedInstanceState: Bundle?) {
        themeManager = ThemeManager(this)
        themeManager.applyTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bindViewObject()
    }

    private fun bindViewObject()
    {

    }

    @Suppress("UNUSED_PARAMETER")
    fun onLoginClick(view: View)
    {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}