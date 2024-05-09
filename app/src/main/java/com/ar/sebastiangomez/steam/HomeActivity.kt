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
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException
import java.util.Locale

// Define el modelo para la respuesta JSON
data class SteamAppListResponse(
    @SerializedName("applist") val appList: AppList
)

data class AppList(
    @SerializedName("apps") val apps: List<SteamApp>
)

data class SteamApp(
    @SerializedName("appid") val id: String,
    @SerializedName("name") val name: String
)

// Define la interfaz Retrofit para el servicio web
interface SteamApiService {
    @GET("ISteamApps/GetAppList/v2/")
    suspend fun getAppList(): SteamAppListResponse
}

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
    private lateinit var linearReloadHome : LinearLayout
    private lateinit var textErrorSearch : TextView
    private lateinit var buttonReloadHome : Button
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
        linearReloadHome = findViewById(R.id.linearReloadHome)
        textErrorSearch = findViewById(R.id.textErrorSearch)
        buttonReloadHome = findViewById(R.id.buttonReloadHome)

        recyclerView.layoutManager = LinearLayoutManager(this)

        linearSearch.removeView(linearSearchButton) //Remove search buttons
        linearSearch.removeView(linearErrorSearchButton) //Remove Error Search
        linearSearch.removeView(linearReloadHome) //Remove Reload Home Button

        getImageTheme()

        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE

                val gamesList = withContext(Dispatchers.IO) {
                    fetchGames()
                }

                val adapter = GameAdapter(gamesList) { position, gameId ->
                    // Acciones a realizar cuando se hace clic en un elemento de la lista
                    val gameName = gamesList[position].name
                    Log.d(tag, "Game ID: $gameId | Game Name: $gameName")
                    // Aquí puedes enviar el ID a otra pantalla o realizar otras acciones relacionadas con el juego
                }

                recyclerView.adapter = adapter

            } catch (e: IOException) {
                e.printStackTrace()
                // En caso de error, mostrar el mensaje de error adecuado
                linearSearch.addView(linearErrorSearchButton)
                textErrorSearch.text = getString(R.string.error3)
                linearSearch.addView(linearReloadHome)
            } finally {
                // Asegurarse de ocultar el ProgressBar después de la carga, ya sea exitosa o no
                progressBar.visibility = View.INVISIBLE
            }
        }

        showButtonSearch() // Mostrar el boton buscar al abrir el search
    }

    private fun showButtonSearch()
    {
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

    // Función para crear el servicio Retrofit
    private fun createSteamApiService(): SteamApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.steampowered.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(SteamApiService::class.java)
    }

    // Método optimizado para obtener la lista de juegos utilizando Retrofit y Gson
    private suspend fun fetchGames(): List<Game> {
        val service = createSteamApiService()
        val response = service.getAppList()
        return response.appList.apps
            .map { steamApp -> Game(steamApp.id, steamApp.name) }
            .filter { game -> game.name.isNotEmpty() }
    }

    fun onFilterGamesBySearchClick(view: View) {
        hideKeyboard(view)
        hideLinear()

        val searchTerm = searchView.query.toString().trim()

        if (searchTerm.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    showProgressBar()

                    val gamesList = fetchGames()
                    val filteredGamesList = filterGamesBySearchTerm(gamesList, searchTerm)

                    if (filteredGamesList.isEmpty()) {
                        showError(getString(R.string.error1))
                        linearSearch.addView(linearReloadHome)
                    } else {
                        val sortedList = sortFilteredGamesList(filteredGamesList, searchTerm)
                        showFilteredGames(sortedList)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    showError(getString(R.string.error3))
                    linearSearch.addView(linearReloadHome)
                } finally {
                    hideProgressBar()
                }
            }
        } else {
            showError(getString(R.string.error2))
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showProgressBar() {
        recyclerView.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun hideLinear()
    {
        linearSearch.removeView(linearErrorSearchButton) //Remove Error Search
        linearSearch.removeView(linearReloadHome) //Remove Reload Home Button
    }

    private fun showError(errorMessage: String) {
        runOnUiThread {
            linearSearch.addView(linearErrorSearchButton)
            textErrorSearch.text = errorMessage
        }
    }

    private suspend fun filterGamesBySearchTerm(gamesList: List<Game>, searchTerm: String): List<Game> {
        return withContext(Dispatchers.Default) {
            gamesList.filter { game ->
                Regex("\\b${searchTerm.lowercase(Locale.getDefault())}\\b").find(game.name.lowercase(Locale.getDefault())) != null
            }
        }
    }

    private fun sortFilteredGamesList(filteredGamesList: List<Game>, searchTerm: String): List<Game> {
        val exactMatch = mutableListOf<Game>()
        val partialMatchWithoutExact = mutableListOf<Game>()

        // Verificar si searchTerm no es nulo antes de usarlo
        searchTerm?.let { term ->
            for (game in filteredGamesList) {
                val lowerCaseName = game.name.lowercase(Locale.getDefault())
                if (lowerCaseName == term.lowercase(Locale.getDefault())) {
                    exactMatch.add(game)
                } else if (lowerCaseName.contains(term.lowercase(Locale.getDefault()))) {
                    partialMatchWithoutExact.add(game)
                }
            }
        }

        return exactMatch + partialMatchWithoutExact
    }

    private fun showFilteredGames(filteredGamesList: List<Game>) {
        runOnUiThread {
            val adapter = GameAdapter(filteredGamesList) { position, gameId ->
                val gameName = filteredGamesList[position].name
                Log.d(tag, "Game ID: $gameId | Game Name: $gameName")
                // Aquí puedes enviar el ID a otra pantalla o realizar otras acciones relacionadas con el juego
            }

            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = adapter
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
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSearchCloseClick(view: View) {
        linearSearch.removeView(linearSearchButton) //Remove search buttons
        linearSearch.removeView(linearErrorSearchButton) //Remove Error Search
        searchView.clearFocus() // Quita el foco del SearchView
    }

    @Suppress("UNUSED_PARAMETER")
    fun onReloadHomeClick(view: View) {
        searchView.setQuery("", false)
        recreate()
    }

}

