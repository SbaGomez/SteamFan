package com.ar.sebastiangomez.steam.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.utils.ThemeHelper
import com.ar.sebastiangomez.steam.utils.Utils


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var logoSplash: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var utils: Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ThemeHelper.applyTheme(this)

        sharedPreferences = getSharedPreferences(ConfigActivity.PREFS_NAME, MODE_PRIVATE)
        utils = Utils()
        // Obtener el idioma guardado en las SharedPreferences
        val currentLanguage = sharedPreferences.getString(ConfigActivity.LANGUAGE_KEY, ConfigActivity.DEFAULT_LANGUAGE)
        if (currentLanguage != null) {
            utils.setLocale(this, currentLanguage)
        }

        bindViewObject()
    }

    private fun bindViewObject()
    {
        logoSplash = findViewById(R.id.logoSplash)

        // Array con los IDs de las imágenes en drawable
        val imagenes = arrayOf(
            R.drawable.gameslogo
        )

        // Generar un índice aleatorio
        val randomIndex = imagenes.indices.random()

        // Asignar la imagen aleatoria al logoSplash
        logoSplash.setImageResource(imagenes[randomIndex])

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)
    }

}