package com.ar.sebastiangomez.steam.ui

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.ui.adapter.BookmarkAdapter
import com.ar.sebastiangomez.steam.utils.GamesCache
import com.ar.sebastiangomez.steam.utils.SearchHelper
import com.ar.sebastiangomez.steam.utils.ThemeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class BookmarkActivity : AppCompatActivity() {
    private lateinit var searchHelper: SearchHelper
    private lateinit var gamesCache: GamesCache
    private lateinit var recyclerView: RecyclerView
    private lateinit var themeHelper: ThemeHelper
    private lateinit var themeButton : ImageButton
    private lateinit var searchView : SearchView
    private lateinit var linearSearch : LinearLayout
    private lateinit var cardSearch : CardView
    private lateinit var buttonSearch : Button
    private lateinit var linearSearchButton : LinearLayout
    private lateinit var linearErrorSearchButton : LinearLayout
    private lateinit var textErrorSearch : TextView
    private lateinit var textCountGames : TextView
    private lateinit var progressBar : ProgressBar
    private val tag = "LOG-BOOKMARK"

    override fun onCreate(savedInstanceState: Bundle?) {
        searchHelper = SearchHelper()
        gamesCache = GamesCache()
        themeHelper = ThemeHelper(this)
        themeHelper.applyTheme()
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
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        themeButton = findViewById(R.id.themeButton)
        searchView = findViewById(R.id.searchInput)
        linearSearch = findViewById(R.id.linearSearch)
        cardSearch = findViewById(R.id.cardSearch)
        buttonSearch = findViewById(R.id.buttonSearch)
        linearSearchButton = findViewById(R.id.linearSearchButton)
        linearErrorSearchButton = findViewById(R.id.linearErrorSearchButton)
        textErrorSearch = findViewById(R.id.textErrorSearch)
        textCountGames = findViewById(R.id.textCountGames)

        recyclerView.layoutManager = LinearLayoutManager(this)

        linearSearch.removeView(linearSearchButton) //Remove search buttons
        linearSearch.removeView(linearErrorSearchButton) //Remove Error Search

        getImageTheme()
        showButtonSearch() // Mostrar el boton buscar al abrir el search
        getAll()

    }

    private fun getAll()
    {
        //Obtener la cantidad de juegos favoritos
        textCountGames.text = gamesCache.countAllGames(this).toString()

        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val gamesList = withContext(Dispatchers.IO) {
                    gamesCache.getGamesFromCache(applicationContext).toMutableList().apply {
                        reverse()
                    }
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
            } finally {
                // Asegurarse de ocultar el ProgressBar después de la carga, ya sea exitosa o no
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private fun showButtonSearch()
    {
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                linearSearch.addView(linearSearchButton)
            }
        }
    }

    fun onFilterGamesBySearchClick(view: View) {
        hideKeyboard(view)
        linearSearch.removeView(linearErrorSearchButton) //Remove Error Search

        val searchTerm = searchView.query.toString().trim()

        if (searchTerm.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    recyclerView.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE

                    val gamesList = gamesCache.getGamesFromCache(this@BookmarkActivity)
                    val filteredGamesList = searchHelper.filterGamesBySearchTerm(gamesList, searchTerm)

                    if (filteredGamesList.isEmpty()) {
                        showError(getString(R.string.error1))
                    } else {
                        val sortedList = searchHelper.sortFilteredGamesList(filteredGamesList, searchTerm)
                        runOnUiThread {
                            val adapter = BookmarkAdapter(this@BookmarkActivity, sortedList) { position, gameId ->
                                val gameName = sortedList[position].name
                                Log.d(tag, "Game ID: $gameId | Game Name: $gameName")
                                // Aquí puedes enviar el ID a otra pantalla o realizar otras acciones relacionadas con el juego
                            }
                            recyclerView.visibility = View.VISIBLE
                            recyclerView.adapter = adapter
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    showError(getString(R.string.error3))
                } finally {
                    progressBar.visibility = View.INVISIBLE
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

    private fun showError(errorMessage: String) {
        runOnUiThread {
            linearSearch.addView(linearErrorSearchButton)
            textErrorSearch.text = errorMessage
        }
    }

    private fun getImageTheme() {
        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") ?: "light" // Obtén el tema actual
        themeButton.setImageTintList(ColorStateList.valueOf(Color.parseColor(if (currentTheme == "dark") "#914040" else "#EAC69C")))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onChangeThemeButtonClick(view: View) {
        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") // Obtén el tema actual
        val newTheme = if (currentTheme == "light") "dark" else "light" // Cambia el tema al opuesto del actual
        themeHelper.changeTheme(newTheme)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onHomeClick(view: View) {
        val intent = Intent(
            this,
            HomeActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
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
        searchView.setQuery("", false)
        recreate()
    }
}