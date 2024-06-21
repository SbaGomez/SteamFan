package com.ar.sebastiangomez.steam.ui
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.R
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ConfigActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "MyPrefsFile"
        const val LANGUAGE_KEY = "Language"
        const val DEFAULT_LANGUAGE = "es" // Establece "en" como el valor predeterminado
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_config)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val radioGroupLanguages = findViewById<RadioGroup>(R.id.radioGroupLanguages)

        // Obtener el idioma guardado en las SharedPreferences
        val currentLanguage = sharedPreferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE)

        // Marcar el RadioButton correspondiente al idioma guardado
        when (currentLanguage) {
            "en" -> radioGroupLanguages.check(R.id.radioButtonEnglish)
            "es" -> radioGroupLanguages.check(R.id.radioButtonSpanish)
            "pt" -> radioGroupLanguages.check(R.id.radioButtonPortuguese)
        }

        radioGroupLanguages.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            val selectedLanguage = radioButton.text.toString()

            // Obtener el idioma actual de la configuración
            val currentLocale = Locale.getDefault().language

            if (currentLocale != selectedLanguage) {
                when (selectedLanguage) {
                    getString(R.string.english) -> setLocale("en")
                    getString(R.string.spanish) -> setLocale("es")
                    getString(R.string.portuguese) -> setLocale("pt")
                }

                // Guardar el idioma seleccionado en las SharedPreferences
                sharedPreferences.edit().putString(LANGUAGE_KEY, selectedLanguage).apply()

                Toast.makeText(this, "Selected: $selectedLanguage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        // Reiniciar la actividad para aplicar el nuevo idioma
        val intent = intent
        finish()
        startActivity(intent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onBookmarkClick(view: View) {
        val intent = Intent(this, BookmarkActivity::class.java)
        startActivity(intent)
        finish()
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

    fun onLogoutClick(view: View) {
        // Cerrar sesión con Firebase
        FirebaseAuth.getInstance().signOut()
        // Actualizar SharedPreferences para reflejar que el usuario no está logueado
        val sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // Crear el intent para la actividad de inicio de sesión
        val intent = Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }

    @Suppress("UNUSED_PARAMETER", "DEPRECATION")
    fun onBackClick(view: View) {
        super.onBackPressed()
    }
}