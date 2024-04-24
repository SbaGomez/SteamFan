package com.ar.sebastiangomez.steam.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ar.sebastiangomez.steam.GameAdapter
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.utils.SpaceItemDecoration
import com.ar.sebastiangomez.steam.utils.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class Game(val id: String, val name: String)

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var themeManager: ThemeManager
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        themeManager = ThemeManager(this)
        themeManager.applyTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val spaceHeight = resources.getDimensionPixelSize(R.dimen.item_space) // Altura del espacio entre elementos
        val paddingHorizontal = resources.getDimensionPixelSize(R.dimen.item_padding_horizontal) // Padding horizontal
        recyclerView.addItemDecoration(SpaceItemDecoration(spaceHeight, paddingHorizontal))

        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val gamesList = fetchGames()
                val adapter = GameAdapter(gamesList) { position, gameId ->
                    // Acciones a realizar cuando se hace clic en un elemento de la lista
                    val gameName = gamesList[position].name
                    Log.d("Game ID:", gameId)
                    Log.d("Game Name:", gameName)
                    // Aquí puedes enviar el ID a otra pantalla o realizar otras acciones relacionadas con el juego
                }
                recyclerView.adapter = adapter
            } catch (e: IOException) {
                e.printStackTrace()
                // En caso de error, ocultar el ProgressBar
                progressBar.visibility = View.INVISIBLE
            } finally {
                // Asegurarse de ocultar el ProgressBar después de la carga, ya sea exitosa o no
                progressBar.visibility = View.INVISIBLE
            }
        }
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

    private suspend fun fetchGames(): List<Game> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val url = "https://api.steampowered.com/ISteamApps/GetAppList/v2/"
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseData = response.body!!.string()
            Log.d("List Game:", responseData)
            parseResponse(responseData)
        }
    }

    private fun parseResponse(response: String): List<Game> {
        val gamesList = mutableListOf<Game>()
        val jsonObject = JSONObject(response)
        val appsArray = jsonObject.getJSONObject("applist").getJSONArray("apps")

        for (i in 0 until appsArray.length()) {
            val appObject = appsArray.getJSONObject(i)
            val id = appObject.getString("appid")
            val name = appObject.getString("name")
            if (name.isNotEmpty()) {
                gamesList.add(Game(id, name))
            }
        }
        return gamesList
    }
}

