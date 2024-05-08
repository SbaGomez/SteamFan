package com.ar.sebastiangomez.steam

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

class BookmarkActivity : AppCompatActivity() {
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

    @Suppress("UNUSED_PARAMETER")
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