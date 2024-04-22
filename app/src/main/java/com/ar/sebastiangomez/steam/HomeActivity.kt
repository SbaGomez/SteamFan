package com.ar.sebastiangomez.steam

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class Game(val id: String, val name: String)

class HomeActivity : AppCompatActivity() {

    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.listView)

        lifecycleScope.launch {
            try {
                val gamesList = fetchGames()
                val namesList = gamesList.map { it.name }
                val adapter = ArrayAdapter(this@HomeActivity, android.R.layout.simple_list_item_1, namesList)
                listView.adapter = adapter

                listView.setOnItemClickListener { _, _, position, _ ->
                    val gameId = gamesList[position].id
                    val gameName = gamesList[position].name
                    // Aquí puedes enviar el ID a otra pantalla
                    Log.d("Game ID:", gameId)
                    Log.d("Game Name:", gameName)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
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
