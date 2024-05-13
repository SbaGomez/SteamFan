package com.ar.sebastiangomez.steam

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.utils.ThemeHelper


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var themeHelper: ThemeHelper
    private lateinit var logoSplash: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {

        themeHelper = ThemeHelper(this)
        themeHelper.applyTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViewObject()
    }

    private fun bindViewObject()
    {
        logoSplash = findViewById(R.id.logoSplash)

        // Array con los IDs de las imágenes en drawable
        val imagenes = arrayOf(
            R.drawable.gameslogo,
            /*R.drawable.logov12,
            R.drawable.logov13,
            R.drawable.logov14,*/
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