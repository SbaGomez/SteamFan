package com.ar.sebastiangomez.steam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.utils.ThemeManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import com.bumptech.glide.Glide

class DetalleActivity : AppCompatActivity() {

    private lateinit var themeManager: ThemeManager
    private lateinit var progressBar : ProgressBar
    lateinit var TitleTxt : TextView
    lateinit var DescripcionTxt : TextView
    lateinit var LayoutDetalle : LinearLayout
    lateinit var CardView : CardView
    lateinit var headerImg : ImageView

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

        bindViewObject()
        var ID = intent.extras!!.getInt("ID")
        Log.d("ID GAME SELECCIONADO:", ID.toString())
        fetchGameDetails(ID.toString())
    }

    fun bindViewObject() {
        TitleTxt = findViewById(R.id.titleGame)
        DescripcionTxt = findViewById(R.id.shortDescription)
        LayoutDetalle = findViewById(R.id.layoutDetalle)
        CardView = findViewById(R.id.cardError)
        headerImg = findViewById(R.id.imageDetalle)
        progressBar = findViewById(R.id.progressBarDetail)
    }

    private fun fetchGameDetails(gameId: String) {
        progressBar.visibility = View.VISIBLE
        val client = OkHttpClient()
        val url = "https://store.steampowered.com/api/appdetails?appids=$gameId"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    Log.d("GAME DETAIL", responseData ?: "Empty response")
                    progressBar.visibility = View.INVISIBLE
                    if (responseData != null) {
                        val gameDetailMap = Gson().fromJson<Map<String, GameDetail.GameDetailResponse>>(
                            responseData,
                            object : TypeToken<Map<String, GameDetail.GameDetailResponse>>() {}.type
                        )
                        val gameDetail = gameDetailMap[gameId]?.data
                        if (gameDetail != null) {
                            // Verifica si las propiedades son nulas antes de usarlas
                            Log.d(
                                "GAME DETAIL",
                                "ID: ${gameDetail.steam_appid}, Name: ${gameDetail.name}, Type: ${gameDetail.type}, Short description: ${gameDetail.short_description}"
                            )

                            TitleTxt.text = gameDetail.name
                            DescripcionTxt.text = gameDetail.short_description.replace(Regex("<br />|&quot;"), "")
                            // Cargar la imagen utilizando Glide
                            Glide.with(this@DetalleActivity)
                                .load(gameDetail.header_image) // URL de la imagen
                                .placeholder(R.drawable.steamdb) // Placeholder mientras se carga la imagen (opcional)
                                .error(R.drawable.error) // Imagen de error en caso de falla de carga (opcional)
                                .into(headerImg) // Establecer la imagen en el ImageView
                            LayoutDetalle.visibility = View.VISIBLE

                        } else {
                            CardView.visibility = View.VISIBLE
                            Log.e("ERROR", "Game detail not found for ID: $gameId")

                        }
                    } else {
                        Log.e("ERROR", "Empty response body")
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("ERROR", "Failed to fetch game details: ${e.message}")
            }
        })
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

}