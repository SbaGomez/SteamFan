package com.ar.sebastiangomez.steam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.utils.ThemeManager

class LoginActivity : AppCompatActivity() {

    private lateinit var themeManager: ThemeManager
    private lateinit var logoSplash: ImageView
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
        logoSplash = findViewById(R.id.logoSplash)

        // Array con los IDs de las imágenes en drawable
        val imagenes = arrayOf(
            /*R.drawable.logov2,
            R.drawable.logov3,
            R.drawable.logov4,
            R.drawable.logov5,
            R.drawable.logov6,
            R.drawable.logov7,
            R.drawable.logov8,
            R.drawable.logov9,
            R.drawable.logov10,*/
            R.drawable.logov11,
        )

        // Generar un índice aleatorio
        val randomIndex = imagenes.indices.random()

        // Asignar la imagen aleatoria al logoSplash
        logoSplash.setImageResource(imagenes[randomIndex])
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLoginClick(view: View)
    {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}