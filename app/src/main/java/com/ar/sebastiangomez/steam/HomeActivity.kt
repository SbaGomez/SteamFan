package com.ar.sebastiangomez.steam

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        recyclerView.layoutManager = LinearLayoutManager(this)
        val spaceHeight = resources.getDimensionPixelSize(R.dimen.item_space) // Altura del espacio entre elementos
        val paddingHorizontal = resources.getDimensionPixelSize(R.dimen.item_padding_horizontal) // Padding horizontal
        recyclerView.addItemDecoration(SpaceItemDecoration(spaceHeight, paddingHorizontal))

        lifecycleScope.launch {
            try {
                val gamesList = fetchGames()
                val adapter = GameAdapter(gamesList) { position ->
                    val gameId = gamesList[position].id
                    val gameName = gamesList[position].name
                    // Aquí puedes enviar el ID a otra pantalla
                    Log.d("Game ID:", gameId)
                    Log.d("Game Name:", gameName)
                }
                recyclerView.adapter = adapter
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun onChangeThemeButtonClick(view: View) {
        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") // Obtén el tema actual
        val newTheme = if (currentTheme == "light") "dark" else "light" // Cambia el tema al opuesto del actual
        themeManager.changeTheme(newTheme)
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

