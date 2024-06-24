package com.ar.sebastiangomez.steam.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.lifecycle.lifecycleScope
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.data.GamesRepository
import com.ar.sebastiangomez.steam.utils.ThemeHelper
import com.ar.sebastiangomez.steam.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*

class ConfigActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "MyPrefsFile"
        const val LANGUAGE_KEY = "Language"
        const val DEFAULT_LANGUAGE = "es" // Establece "es" como el valor predeterminado
        private const val RC_SIGN_IN = 9001
    }

    private val gamesRepository: GamesRepository = GamesRepository()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imagePerfil: ImageView
    private lateinit var textPerfil: TextView
    private lateinit var switchTheme: SwitchCompat
    private lateinit var buttonClearCache: Button
    private lateinit var radioGroupLanguages: RadioGroup
    private lateinit var utils: Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        utils = Utils()
        setContentView(R.layout.activity_config)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
        val currentLanguage = sharedPreferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) // Obtener el idioma guardado en las SharedPreferences
        googleConfigLogin()

        for (i in 0 until radioGroupLanguages.childCount) {
            val radioButton = radioGroupLanguages.getChildAt(i) as RadioButton
            val colorStateList = ContextCompat.getColorStateList(this, R.color.radio_button_selector)
            CompoundButtonCompat.setButtonTintList(radioButton, colorStateList)
            Handler(Looper.getMainLooper()).postDelayed({
                radioButton.isEnabled = true
            }, 3000)
        }

        // Obtener el RadioButton correspondiente al idioma guardado
        val selectedRadioButtonId = when (currentLanguage) {
            "en" -> R.id.radioButtonEnglish
            "es" -> R.id.radioButtonSpanish
            "pt" -> R.id.radioButtonPortuguese
            else -> R.id.radioButtonEnglish // Idioma predeterminado si no se encuentra ninguno
        }

        // Marcar el RadioButton correspondiente al idioma guardado
        radioGroupLanguages.check(selectedRadioButtonId)

        // Configurar el listener del RadioGroup
        radioGroupLanguages.setOnCheckedChangeListener { _, checkedId ->
            val selectedLanguage = when (checkedId) {
                R.id.radioButtonEnglish -> "en"
                R.id.radioButtonSpanish -> "es"
                R.id.radioButtonPortuguese -> "pt"
                else -> DEFAULT_LANGUAGE // En caso de que no se seleccione ninguno, se utiliza el idioma predeterminado
            }

            setLocale(selectedLanguage)

            Toast.makeText(this, getString(R.string.selected_language_toast, getLanguageName(selectedLanguage)), Toast.LENGTH_SHORT).show()
        }

        switchTheme.isChecked = ThemeHelper.isDarkThemeEnabled(this)

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemeHelper.saveThemeState(this, isChecked)
            ThemeHelper.applyTheme(this)
        }

        buttonClearCache.setOnClickListener {
            lifecycleScope.launch {
                gamesRepository.deleteAllRoom(this@ConfigActivity)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeHelper.applyTheme(this)
    }

    private fun init()
    {
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        imagePerfil = findViewById(R.id.imagePerfil)
        textPerfil = findViewById(R.id.textPerfil)
        switchTheme = findViewById(R.id.switchTheme)
        buttonClearCache = findViewById(R.id.buttonClearCache)
        radioGroupLanguages = findViewById(R.id.radioGroupLanguages)
    }

    private fun getLanguageName(languageCode: String): String {
        return when (languageCode) {
            "en" -> getString(R.string.english)
            "es" -> getString(R.string.spanish)
            "pt" -> getString(R.string.portuguese)
            else -> getString(R.string.unknown_language)
        }
    }

    private fun loadProfileImage(account: GoogleSignInAccount) {
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val savedPhotoUrlKey = "profilePhotoUrl_${account.id}" // Utiliza el ID único de la cuenta para diferenciar las URLs de las fotos
        val savedPhotoUrl = sharedPreferences.getString(savedPhotoUrlKey, null)

        val personPhoto = account.photoUrl?.toString()
        val photoUrlToLoad = savedPhotoUrl ?: personPhoto

        progressBar.visibility = View.VISIBLE

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // Utilizar la estrategia de caché predeterminada
            .skipMemoryCache(false) // Permitir la caché en memoria
            .override(Target.SIZE_ORIGINAL) // Tamaño original de la imagen
            .placeholder(R.drawable.defaultperfil) // Imagen de carga por defecto
            .error(R.drawable.defaultperfil) // Imagen de error por defecto

        Glide.with(this@ConfigActivity)
            .load(photoUrlToLoad)
            .apply(requestOptions)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.INVISIBLE
                    imagePerfil.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.INVISIBLE
                    imagePerfil.visibility = View.VISIBLE

                    // Guardar la URL de la imagen en SharedPreferences si no estaba guardada
                    if (savedPhotoUrl != personPhoto) {
                        sharedPreferences.edit().putString(savedPhotoUrlKey, personPhoto)
                            .apply()
                    }

                    return false
                }
            })
            .into(imagePerfil)

        textPerfil.text = account.displayName?.let {
            getString(R.string.welcome_message, it)
        } ?: getString(R.string.default_welcome_message)
    }

    private fun setLocale(languageCode: String) {
        lifecycleScope.launch {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

            // Guardar el idioma seleccionado en las SharedPreferences
            sharedPreferences.edit().putString(LANGUAGE_KEY, languageCode).apply()

            // Reiniciar la actividad para aplicar el nuevo idioma
            recreate()
        }
    }

    private fun googleConfigLogin()
    {
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

    @Suppress("UNUSED_PARAMETER", "DEPRECATION")
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
