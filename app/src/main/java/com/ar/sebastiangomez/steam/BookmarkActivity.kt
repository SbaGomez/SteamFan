package com.ar.sebastiangomez.steam

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.utils.ThemeManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var themeManager: ThemeManager
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
    private val tag = "LOG-BOOKMARK"

    override fun onCreate(savedInstanceState: Bundle?) {
        themeManager = ThemeManager(this)
        themeManager.applyTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bookmark)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViewObject()
    }

    private fun bindViewObject() {
        recyclerView = findViewById(R.id.recyclerView)
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

        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") // Obtén el tema actual
        if (currentTheme.toString() == "dark") {
            themeButton.setImageResource(R.drawable.themedarktab)
        } else {
            themeButton.setImageResource(R.drawable.themelighttab)
        }

        // Mostrar el boton buscar al abrir el search
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                linearSearch.addView(linearSearchButton)
            }
        }

        // Obtener el ID del Intent
        val gameId = intent.getStringExtra("game_id")
        val gameName = intent.getStringExtra("game_name")
        // Almacenar el juego en caché
        if (gameId != null && gameName != null) {
            // Crear un objeto CachedGame con el id y el nombre del juego
            val cachedGame = Game(gameId, gameName)
            // Agregar el juego a la lista en caché
            addGameToCache(this, cachedGame)
        }

        lifecycleScope.launch {
            try {

                val gamesList = withContext(Dispatchers.IO) {
                    getGamesFromCache(applicationContext) // Obtener la lista de juegos desde la caché
                }

                val adapter = BookmarkAdapter(this@BookmarkActivity, gamesList) { position, gameId ->
                // Acciones a realizar cuando se hace clic en un elemento de la lista
                    val gameName = gamesList[position].name
                    Log.d(tag, "Game ID: $gameId | Game Name: $gameName")
                }

                recyclerView.adapter = adapter

            } catch (e: Exception) {
                e.printStackTrace()
                // En caso de error, mostrar el mensaje de error adecuado
                linearSearch.addView(linearErrorSearchButton)
                textErrorSearch.text = getString(R.string.error3)
                linearSearch.addView(linearReloadHome)
            } finally {
                // Asegurarse de ocultar el ProgressBar después de la carga, ya sea exitosa o no
            }
        }
    }

    // Función para agregar un ID a la lista en caché
    private fun addGameToCache(context: Context, game: Game) {
        val games = getGamesFromCache(context)
        if (!games.any { it.id == game.id }) {
            games.add(game)
            saveGamesToCache(context, games)
        }
    }

    // Función para obtener la lista de IDs almacenados en caché
     private fun getGamesFromCache(context: Context): MutableList<Game> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("bookmark_data", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("games", "[]")
        val type = object : TypeToken<MutableList<Game>>() {}.type
        return Gson().fromJson(json, type)
    }


    // Función para guardar la lista de IDs en caché
    private fun saveGamesToCache(context: Context, games: MutableList<Game>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("bookmark_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(games)
        editor.putString("games", json)
        editor.apply()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onChangeThemeButtonClick(view: View) {
        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") // Obtén el tema actual
        val newTheme = if (currentTheme == "light") "dark" else "light" // Cambia el tema al opuesto del actual
        themeManager.changeTheme(newTheme)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onHomeClick(view: View) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    @Suppress("UNUSED_PARAMETER","DEPRECATION")
    fun onBackClick(view: View) {
        super.onBackPressed()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSearchCloseClick(view: View) {
        linearSearch.removeView(linearSearchButton) //Remove search buttons
        linearSearch.removeView(linearErrorSearchButton) //Remove Error Search
        searchView.clearFocus() // Quita el foco del SearchView
    }

    @Suppress("UNUSED_PARAMETER")
    fun onReloadHomeClick(view: View) {
        val intent = Intent(this, BookmarkActivity::class.java)
        startActivity(intent)
        finish()
    }
}