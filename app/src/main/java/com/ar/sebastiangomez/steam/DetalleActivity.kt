package com.ar.sebastiangomez.steam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.utils.ThemeManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class GameDetail(val id: String, val name: String)
class DetalleActivity : AppCompatActivity() {

    private lateinit var themeManager: ThemeManager
    override fun onCreate(savedInstanceState: Bundle?) {
        themeManager = ThemeManager(this)
        themeManager.applyTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var ID = intent.extras!!.getInt("ID")
        Log.d("ID GAME SELECCIONADO:", ID.toString())

        fetchGameDetails(ID.toString())
    }

    fun onChangeThemeButtonClick(view: View) {
        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") // Obtén el tema actual
        val newTheme = if (currentTheme == "light") "dark" else "light" // Cambia el tema al opuesto del actual
        themeManager.changeTheme(newTheme)
    }

    fun onBookmarkClick(view: View) {
        var intent = Intent(this, BookmarkActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onHomeClick(view: View) {
        var intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchGameDetails(gameId: String) {
        val client = OkHttpClient()
        val url = "https://store.steampowered.com/api/appdetails?appids=$gameId"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                // Procesar la respuesta aquí
                Log.d("DETALLE JUEGO", responseData ?: "Empty response")
                // Puedes usar Gson u otra biblioteca para convertir la respuesta JSON a objetos Kotlin
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("ERROR", "Failed to fetch game details: ${e.message}")
            }
        })
    }

}