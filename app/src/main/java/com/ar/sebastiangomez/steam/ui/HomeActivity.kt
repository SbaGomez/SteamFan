package com.ar.sebastiangomez.steam.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
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
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.data.GamesRepository
import com.ar.sebastiangomez.steam.model.Game
import com.ar.sebastiangomez.steam.ui.adapter.GamesAdapter
import com.ar.sebastiangomez.steam.utils.SearchHelper
import kotlinx.coroutines.*
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHelper: SearchHelper
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

    private val searchJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + searchJob)
    private var filterJob: Job? = null
    private val filteredGames: LiveData<List<Game>> = MutableLiveData()

    private val gamesRepository: GamesRepository = GamesRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        searchHelper = SearchHelper()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViewObject()
        setButtonImageBasedOnTheme()
        themeButton.setOnClickListener {
            toggleTheme()
        }
        showButtonSearch() // Mostrar el boton buscar al abrir el search
        getGames()
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

        setupSearchBar()

        filteredGames.observe(this) { filteredGames ->
            (recyclerView.adapter as? GamesAdapter)?.updateItems(filteredGames)
        }
    }

    private fun setButtonImageBasedOnTheme() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val currentTheme = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) "dark" else "light"
        themeButton.setImageTintList(ColorStateList.valueOf(Color.parseColor(if (currentTheme == "dark") "#914040" else "#EAC69C")))
    }


    private fun toggleTheme() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun getGames()
    {
        val adapter = GamesAdapter(this@HomeActivity, displayedGamesList) { position, gameId ->
            val gameName = displayedGamesList.getOrNull(position)?.name
            if (gameName != null) {
                Log.d(tag, "Game ID: $gameId | Game Name: $gameName")
                searchView.clearFocus() // Remove Focus from SearchView
            } else {
                Log.e(tag, "Invalid position: $position")
            }
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
        val currentSize = displayedGamesList.size
        displayedGamesList.addAll(nextPageItems)
        recyclerView.adapter?.notifyItemRangeInserted(currentSize, nextPageItems.size)
        currentPage++
        isLoading = false
    }

    private fun setupSearchBar() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    performFiltering(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    debounceFilter(newText)
                }
                return true
            }
        })
        searchView.setOnCloseListener {
            // Aquí puedes realizar acciones cuando el usuario cierra la búsqueda
            displayedGamesList.clear()
            false // Devuelve 'false' para permitir que el SearchView se cierre normalmente después de ejecutar tu acción personalizada
        }
    }

    private fun debounceFilter(query: String) {
        filterJob?.cancel() // Cancela el trabajo anterior si existe
        filterJob = uiScope.launch {
            try {
                delay(200) // Espera 200ms antes de ejecutar el filtro
                performFiltering(query)
            } catch (e: CancellationException) {
                // Maneja la cancelación si es necesaria
                Log.d("HomeActivity", "Filtrado cancelado")
            }
        }
    }

    private fun performFiltering(query: String) {
        val currentFilterJob = filterJob // capture the current job
        lifecycleScope.launch {
            try {
                if (::allGamesList.isInitialized) {
                    val filteredList = withContext(Dispatchers.Default) {
                        if (query.isBlank()) {
                            allGamesList // If search query is empty, return all games
                        } else {
                            searchHelper.filterGamesBySearchTerm(allGamesList, query)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        if (currentFilterJob == filterJob) { // check if this is still the active job
                            if (filteredList.isEmpty()) {
                                showError(getString(R.string.error1))
                            } else {
                                clearError()
                                (filteredGames as MutableLiveData).value = ArrayList(filteredList)
                            }
                        }
                    }
                } else {
                    Log.e("HomeActivity", "allGamesList is not initialized yet.")
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error filtering games", e)
            }
        }
    }

    private fun clearError() {
        linearSearch.removeView(linearErrorSearchButton)
        textErrorSearch.text = ""
    }

    fun onFilterGamesBySearchClick(view: View) {
        hideKeyboard()
        linearSearch.removeView(linearErrorSearchButton)

        val searchTerm = searchView.query.toString().trim()

        if (searchTerm.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    recyclerView.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE

                    val gamesList = gamesRepository.getGames()
                    val filteredGamesList = searchHelper.filterExactMatchGames(gamesList, searchTerm)

                    if (filteredGamesList.isEmpty()) {
                        showError(getString(R.string.error1))
                    } else {
                        runOnUiThread {
                            val adapter = GamesAdapter(this@HomeActivity, filteredGamesList) { position, gameId ->
                                val gameName = filteredGamesList[position].name
                                Log.d(tag, "Game ID: $gameId | Game Name: $gameName")
                                // Aquí puedes enviar el ID a otra pantalla o realizar otras acciones relacionadas con el juego
                                hideKeyboard()
                                onSearchCloseClick(view)
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

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun showError(errorMessage: String) {
        uiScope.launch {
            withContext(Dispatchers.Main) {
                linearSearch.addView(linearErrorSearchButton)
                textErrorSearch.text = errorMessage
            }
        }
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
        searchView.clearFocus() // Remove Focus from SearchView
    }
}
