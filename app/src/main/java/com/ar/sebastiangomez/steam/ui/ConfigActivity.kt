package com.ar.sebastiangomez.steam.ui
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ConfigActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "MyPrefsFile"
        const val LANGUAGE_KEY = "Language"
        const val DEFAULT_LANGUAGE = "es" // Establece "es" como el valor predeterminado
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imagePerfil: ImageView
    private lateinit var textPerfil: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_config)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        imagePerfil = findViewById(R.id.imagePerfil)
        textPerfil = findViewById(R.id.textPerfil)
        val radioGroupLanguages = findViewById<RadioGroup>(R.id.radioGroupLanguages)

        // Configura Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Verifica si el usuario ya está logueado
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            // Usuario ya logueado, obtén la URL de la foto de perfil
            loadProfileImage(account)
        } else {
            // Usuario no logueado, inicia el proceso de inicio de sesión
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Obtener el idioma guardado en las SharedPreferences
        val currentLanguage = sharedPreferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE)

        // Obtener el RadioButton correspondiente al idioma guardado
        val selectedRadioButtonId = when (currentLanguage) {
            "en" -> R.id.radioButtonEnglish
            "es" -> R.id.radioButtonSpanish
            "pt" -> R.id.radioButtonPortuguese
            else -> R.id.radioButtonEnglish // Idioma predeterminado si no se encuentra ninguno
        }

        // Marcar el RadioButton correspondiente al idioma guardado
        radioGroupLanguages.check(selectedRadioButtonId)

        radioGroupLanguages.setOnCheckedChangeListener { group, checkedId ->
            val selectedLanguage = when (checkedId) {
                R.id.radioButtonEnglish -> "en"
                R.id.radioButtonSpanish -> "es"
                R.id.radioButtonPortuguese -> "pt"
                else -> DEFAULT_LANGUAGE // En caso de que no se seleccione ninguno, se utiliza el idioma predeterminado
            }

            setLocale(selectedLanguage)

            // Guardar el idioma seleccionado en las SharedPreferences
            sharedPreferences.edit().putString(LANGUAGE_KEY, selectedLanguage).apply()

            Toast.makeText(this, getString(R.string.selected_language_toast, getLanguageName(selectedLanguage)), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLanguageName(languageCode: String): String {
        return when (languageCode) {
            "en" -> getString(R.string.english)
            "es" -> getString(R.string.spanish)
            "pt" -> getString(R.string.portuguese)
            else -> "Unknown Language" // Otra opción para manejar códigos de idioma desconocidos
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Inicio de sesión exitoso, obtén la URL de la foto de perfil
            if (account != null) {
                loadProfileImage(account)
            }
        } catch (e: ApiException) {
            // El inicio de sesión falló, maneja el error
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.sign_in_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileImage(account: GoogleSignInAccount) {
        val personPhoto = account.photoUrl
        val personName = account.displayName
        if (personPhoto != null) {
            // Usa Glide para cargar la imagen en el ImageView, omitiendo la caché
            Glide.with(this)
                .load(personPhoto)
                .skipMemoryCache(true) // Omite la caché de memoria
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Omite la caché de disco
                .into(imagePerfil)
        } else {
            Toast.makeText(this, getString(R.string.no_profile_image), Toast.LENGTH_SHORT).show()
        }
        if (personName != null) {
            textPerfil.text = getString(R.string.welcome_message, personName)
        } else {
            textPerfil.text = getString(R.string.default_welcome_message)
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

        // Cerrar sesión con Google
        val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
        googleSignInClient.signOut().addOnCompleteListener {
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
    }

    @Suppress("UNUSED_PARAMETER", "DEPRECATION")
    fun onBackClick(view: View) {
        super.onBackPressed()
    }
}
