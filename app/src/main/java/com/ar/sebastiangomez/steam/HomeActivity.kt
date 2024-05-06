package com.ar.sebastiangomez.steam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ar.sebastiangomez.steam.utils.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class Game(val id: String, val name: String)

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var themeManager: ThemeManager
    private lateinit var progressBar : ProgressBar
    private lateinit var themeButton : ImageButton
    private lateinit var searchView : SearchView
    private lateinit var linearSearch : LinearLayout
    private lateinit var cardSearch : CardView
    private lateinit var buttonSearch : Button
    private lateinit var linearSearchButton : LinearLayout
    private lateinit var linearErrorSearchButton : LinearLayout
    private lateinit var textErrorSearch : TextView
    private val tag = "LOG-HOME"

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

        bindViewObject()
    }

    private fun bindViewObject() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        themeButton = findViewById(R.id.themeButton)
        searchView = findViewById(R.id.searchInput)
        linearSearch = findViewById(R.id.linearSearch)
        cardSearch = findViewById(R.id.cardSearch)
        buttonSearch = findViewById(R.id.buttonSearch)
        linearSearchButton = findViewById(R.id.linearSearchButton)
        linearErrorSearchButton = findViewById(R.id.linearErrorSearchButton)
        textErrorSearch = findViewById(R.id.textErrorSearch)
        recyclerView.layoutManager = LinearLayoutManager(this)

        linearSearch.removeView(linearSearchButton) //Remove search buttons
        linearSearch.removeView(linearErrorSearchButton) //Remove Error Search

        getImageTheme()

        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val gamesList = fetchGames()
                val adapter = GameAdapter(gamesList) { position, gameId ->
                    // Acciones a realizar cuando se hace clic en un elemento de la lista
                    val gameName = gamesList[position].name
                    Log.d(tag, "Game ID: $gameId | Game Name: $gameName")
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

        // Mostrar el boton buscar al abrir el search
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                linearSearch.addView(linearSearchButton)
            }
        }
    }

    private fun getImageTheme() {
        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") // Obtén el tema actual
        Log.d(tag, "Current Theme: " + currentTheme.toString())
        if (currentTheme.toString() == "dark") {
            themeButton.setImageResource(R.drawable.themedarktab)
        } else {
            themeButton.setImageResource(R.drawable.themelighttab)
        }
        return
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
            Log.d(tag, "List Game: $responseData")
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

    fun onFilterGamesBySearchClick(view: View) {
        linearSearch.removeView(linearErrorSearchButton) //Remove Error Search
        // Cerrar el teclado del dispositivo móvil
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

        val searchTerm = searchView.query.toString().trim()

        if (searchTerm.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    recyclerView.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                    val gamesList = fetchGames()
                    val searchTerm =
                        searchView.query.toString()
                            .lowercase(Locale.getDefault()) // Obtener el término de búsqueda del SearchView y convertirlo a minúsculas
                    val filteredGamesList = gamesList.filter {
                        it.name.lowercase(Locale.getDefault()).contains(searchTerm)
                    } // Filtrar los juegos basados en el término de búsqueda

                    val adapter = GameAdapter(filteredGamesList) { position, gameId ->
                        // Acciones a realizar cuando se hace clic en un elemento de la lista
                        val gameName = filteredGamesList[position].name
                        Log.d(tag, "Game ID: $gameId | Game Name: $gameName")
                        // Aquí puedes enviar el ID a otra pantalla o realizar otras acciones relacionadas con el juego
                    }

                    recyclerView.visibility = View.VISIBLE
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
        else{
            linearSearch.addView(linearErrorSearchButton)
            textErrorSearch.text = "El campo de busqueda esta vacio."
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onChangeThemeButtonClick(view: View) {
        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") // Obtén el tema actual
        val newTheme = if (currentTheme == "light") "dark" else "light" // Cambia el tema al opuesto del actual

        Log.d(tag, "New Theme: $newTheme")
        themeManager.changeTheme(newTheme)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onBookmarkClick(view: View) {
        val intent = Intent(this, BookmarkActivity::class.java)
        startActivity(intent)
        finish()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSearchCloseClick(view: View) {
        linearSearch.removeView(linearSearchButton) //Remove search buttons
        searchView.clearFocus() // Quita el foco del SearchView
    }

}

