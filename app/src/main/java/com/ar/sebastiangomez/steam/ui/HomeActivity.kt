package com.ar.sebastiangomez.steam.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.data.GamesRepository
import com.ar.sebastiangomez.steam.model.Game
import com.ar.sebastiangomez.steam.ui.adapter.GamesAdapter
import com.ar.sebastiangomez.steam.utils.SearchHelper
import com.ar.sebastiangomez.steam.utils.ThemeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHelper: SearchHelper
    private lateinit var themeHelper: ThemeHelper
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

    private lateinit var allGamesList: List<Game>
    private val displayedGamesList = mutableListOf<Game>()
    private val itemsPerPage = 10
    private var currentPage = 0
    private var isLoading = false

    private var filteredGames: MutableLiveData<List<Game>> = MutableLiveData<List<Game>>()

    private val gamesRepository: GamesRepository = GamesRepository()

    private val themeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Recargar la actividad para aplicar el nuevo tema
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        searchHelper = SearchHelper()
        themeHelper = ThemeHelper(this)
        themeHelper.applyTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Registrar el receptor del broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(
            themeChangeReceiver, IntentFilter("com.example.ACTION_THEME_CHANGED")
        )

        bindViewObject()
        getImageTheme()
        showButtonSearch() // Mostrar el boton buscar al abrir el search
        getGames()

        setupSearchBar()

        filteredGames.observe(this) { filteredGames ->
            (recyclerView.adapter as? GamesAdapter)?.updateItems(filteredGames)
        }
    }

    private fun setupSearchBar() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    filterGames(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterGames(newText)
                }
                return true
            }
        })
    }

    fun filterGames(query: String) {
        val filteredList = allGamesList.filter {
            it.name.contains(query, ignoreCase = true)
        }
        filteredGames.value = ArrayList(filteredList)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Desregistrar el receptor del broadcast
        LocalBroadcastManager.getInstance(this).unregisterReceiver(themeChangeReceiver)
    }

    private fun bindViewObject() {
        progressBar = findViewById(R.id.progressBar)
        themeButton = findViewById(R.id.themeButton)
        searchView = findViewById(R.id.searchInput)
        linearSearch = findViewById(R.id.linearSearch)
        cardSearch = findViewById(R.id.cardSearch)
        buttonSearch = findViewById(R.id.buttonSearch)
        linearSearchButton = findViewById(R.id.linearSearchButton)
        linearErrorSearchButton = findViewById(R.id.linearErrorSearchButton)
        textErrorSearch = findViewById(R.id.textErrorSearch)

        linearSearch.removeView(linearSearchButton) //Remove search buttons
        linearSearch.removeView(linearErrorSearchButton) //Remove Error Search
        
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getGames()
    {
        val adapter = GamesAdapter(this@HomeActivity, displayedGamesList) { position, gameId ->
            val gameName = displayedGamesList[position].name
            Log.d(tag, "Game ID: $gameId | Game Name: $gameName")
        }
        
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    loadMoreGames()
                }
            }
        })

        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                allGamesList = withContext(Dispatchers.IO) {
                    gamesRepository.getGames()
                }
                gamesRepository.countGames()
                loadMoreGames()
            } catch (e: IOException) {
                e.printStackTrace()
                linearSearch.addView(linearErrorSearchButton)
                textErrorSearch.text = getString(R.string.error3)
            } finally {
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMoreGames() {
        if (isLoading) return
        isLoading = true
        val start = currentPage * itemsPerPage
        val end = minOf(start + itemsPerPage, allGamesList.size)
        val nextPageItems = allGamesList.subList(start, end)
        displayedGamesList.addAll(nextPageItems)
        recyclerView.adapter?.notifyDataSetChanged()
        currentPage++
        isLoading = false
    }

    fun onFilterGamesBySearchClick(view: View) {
        hideKeyboard(view)
        linearSearch.removeView(linearErrorSearchButton)

        val searchTerm = searchView.query.toString().trim()

        if (searchTerm.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    recyclerView.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE

                    val gamesList = gamesRepository.getGames()
                    val filteredGamesList = searchHelper.filterGamesBySearchTerm(gamesList, searchTerm)

                    if (filteredGamesList.isEmpty()) {
                        showError(getString(R.string.error1))
                    } else {
                        val sortedList = searchHelper.sortFilteredGamesList(filteredGamesList, searchTerm)

                        runOnUiThread {
                            val adapter = GamesAdapter(this@HomeActivity, sortedList) { position, gameId ->
                                val gameName = sortedList[position].name
                                Log.d(tag, "Game ID: $gameId | Game Name: $gameName")
                                // Aquí puedes enviar el ID a otra pantalla o realizar otras acciones relacionadas con el juego
                                searchView.clearFocus() // Quita el foco del SearchView
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
        val currentTheme = preferences.getString("theme", "light") ?: "light" // Obtén el tema actual
        themeButton.setImageTintList(ColorStateList.valueOf(Color.parseColor(if (currentTheme == "dark") "#914040" else "#EAC69C")))
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

    @Suppress("UNUSED_PARAMETER")
    fun onChangeThemeButtonClick(view: View) {
        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") // Obtén el tema actual
        val newTheme = if (currentTheme == "light") "dark" else "light" // Cambia el tema al opuesto del actual

        Log.d(tag, "New Theme: $newTheme")
        themeHelper.changeTheme(newTheme)
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

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("UNUSED_PARAMETER")
    fun onReloadHomeClick(view: View) {
        searchView.setQuery("", false)
        searchView.clearFocus()

        // Reset pagination variables
        currentPage = 0
        isLoading = false
        displayedGamesList.clear()

        // Notify the adapter that the data set has changed
        recyclerView.adapter?.notifyDataSetChanged()

        // Fetch games again
        getGames()
    }

}

