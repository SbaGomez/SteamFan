package com.ar.sebastiangomez.steam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.utils.ThemeManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import com.bumptech.glide.Glide
import android.widget.ImageButton

class DetalleActivity : AppCompatActivity() {

    private lateinit var themeManager: ThemeManager
    private lateinit var progressBar : ProgressBar
    private lateinit var titleTxt : TextView
    private lateinit var descripcionTxt : TextView
    private lateinit var layoutDetalle : LinearLayout
    private lateinit var cardView : CardView
    private lateinit var headerImg : ImageView
    private lateinit var themeButton : ImageButton
    private lateinit var textSO : TextView
    private lateinit var textProcesador : TextView
    private lateinit var textMemoria : TextView
    private lateinit var textGraficos : TextView
    private lateinit var textAlmacenamiento : TextView
    private val tag = "LOG-DETAIL"

    override fun onCreate(savedInstanceState: Bundle?) {
        themeManager = ThemeManager(this)
        themeManager.applyTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViewObject()
    }

    private fun bindViewObject() {
        titleTxt = findViewById(R.id.titleGame)
        descripcionTxt = findViewById(R.id.shortDescription)
        layoutDetalle = findViewById(R.id.layoutDetalle)
        cardView = findViewById(R.id.cardError)
        headerImg = findViewById(R.id.imageDetalle)
        progressBar = findViewById(R.id.progressBarDetail)
        themeButton = findViewById(R.id.themeButton)
        textSO = findViewById(R.id.textSO)
        textProcesador = findViewById(R.id.textProcesador)
        textMemoria = findViewById(R.id.textMemoria)
        textGraficos = findViewById(R.id.textGraficos)
        textAlmacenamiento = findViewById(R.id.textAlmacenamiento)

        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") // Obtén el tema actual
        if(currentTheme.toString() == "dark")
        {
            themeButton.setImageResource(R.drawable.themedarktab)
        }
        else{
            themeButton.setImageResource(R.drawable.themelighttab)
        }

        val id = intent.extras!!.getInt("ID")
        Log.d(tag, "ID Game: $id")
        fetchGameDetails(id.toString())
    }

    private fun fetchGameDetails(gameId: String) {
        progressBar.visibility = View.VISIBLE
        val client = OkHttpClient()
        val url = "https://store.steampowered.com/api/appdetails?appids=$gameId"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    Log.d(tag, ("GAME DETAIL COMPLETE: $responseData"))
                    if (responseData != null) {
                        val gameDetailMap = Gson().fromJson<Map<String, GameDetail.GameDetailResponse>>(
                            responseData,
                            object : TypeToken<Map<String, GameDetail.GameDetailResponse>>() {}.type
                        )
                        val gameDetail = gameDetailMap[gameId]?.data
                        if (gameDetail != null) {
                            // Verifica si las propiedades son nulas antes de usarlas
                            val pcRequirements = parsePcRequirements(gameDetail.pc_requirements.toString())
                            Log.d(tag,"GAME DETAIL - ID: ${gameDetail.steam_appid}, Name: ${gameDetail.name}, Type: ${gameDetail.type}, Short description: ${gameDetail.short_description}, Pc requirements: $pcRequirements")
                            textSO.text = pcRequirements?.minimum?.os?.takeIf { it.isNotEmpty() } ?: "N/A"
                            textProcesador.text = pcRequirements?.minimum?.processor?.takeIf { it.isNotEmpty() } ?: "N/A"
                            textMemoria.text = pcRequirements?.minimum?.memory?.takeIf { it.isNotEmpty() } ?: "N/A"
                            textGraficos.text = pcRequirements?.minimum?.graphics?.takeIf { it.isNotEmpty() } ?: "N/A"
                            textAlmacenamiento.text = pcRequirements?.recommended?.storage?.takeIf { it.isNotEmpty() } ?: "N/A"
                            titleTxt.text = gameDetail.name
                            descripcionTxt.text = gameDetail.short_description.replace(Regex("<br />|&quot;"), "")
                            // Cargar la imagen utilizando Glide
                            Glide.with(this@DetalleActivity)
                                .load(gameDetail.header_image) // URL de la imagen
                                .placeholder(R.drawable.steamdb) // Placeholder mientras se carga la imagen (opcional)
                                .error(R.drawable.error) // Imagen de error en caso de falla de carga (opcional)
                                .into(headerImg) // Establecer la imagen en el ImageView

                            Handler(Looper.getMainLooper()).postDelayed({
                                progressBar.visibility = View.INVISIBLE
                                layoutDetalle.visibility = View.VISIBLE
                            }, 2000)

                        } else {
                            cardView.visibility = View.VISIBLE
                            progressBar.visibility = View.INVISIBLE
                            Log.e(tag,"ERROR: Game detail not found for ID: $gameId")

                        }
                    } else {
                        Log.e(tag,"ERROR: Empty response body")
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(tag, "ERROR: Failed to fetch game details: ${e.message}")
            }
        })
    }

    fun parsePcRequirements(pcRequirementsString: String): GameDetail.PcRequirements? {
        // Elimina las etiquetas HTML y caracteres innecesarios
        val cleanString = pcRequirementsString
            .replace("<strong>", " ")
            .replace(Regex("<.*?>"), "") // Elimina etiquetas HTML
            .replace("Minimum:", "") // Elimina el texto "Minimum:"
            .replace("Recommended:", "") // Elimina el texto "Recommended:"
            .replace(", recommended=", "") // Elimina el texto ", recommended="
            .replace("\n", "") // Elimina saltos de línea

        // Divide la cadena en requisitos mínimos y recomendados
        val requirementParts = cleanString.split("Recommended:")

        // Función auxiliar para extraer un requisito específico de acuerdo a su etiqueta
        fun extractRequirement(requirementString: String, label: String): String {
            val regex =
                Regex("$label\\s*:?\\s*((?:(?!$label\\s*:?)[^:])*)", RegexOption.IGNORE_CASE)
            val matchResult = regex.find(requirementString)
            val content = matchResult?.groupValues?.getOrNull(1)?.trim() ?: ""

            // Eliminar la última palabra del contenido
            val words = content.split(" ")

            return if (words.size > 1) words.dropLast(1).joinToString(" ") else content
        }

        // Función auxiliar para crear un objeto PcRequirement a partir de una cadena de requisito
        fun createPcRequirement(requirementString: String): GameDetail.PcRequirement? {
            val os = extractRequirement(requirementString, "OS:")
            val processor = extractRequirement(requirementString, "Processor:")
            val memory = extractRequirement(requirementString, "Memory:")
            val graphics = extractRequirement(requirementString, "Graphics:")
            val directx = extractRequirement(requirementString, "DirectX:")
            val soundcard = extractRequirement(requirementString, "Sound Card:")
            val network = extractRequirement(requirementString, "Network:")
            val storage = extractRequirement(requirementString, "Storage:")

            return if (os.isNotEmpty() || processor.isNotEmpty() || memory.isNotEmpty() || graphics.isNotEmpty() ||
                directx.isNotEmpty() || soundcard.isNotEmpty() || network.isNotEmpty() || storage.isNotEmpty()) {
                GameDetail.PcRequirement(
                    os = os,
                    processor = processor,
                    memory = memory,
                    graphics = graphics,
                    directx = directx,
                    soundcard = soundcard,
                    network = network,
                    storage = storage
                )
            } else {
                null
            }
        }

        // Obtén los requisitos mínimos si están presentes
        val minimumRequirement = requirementParts.getOrNull(0)?.let { minimum ->
            createPcRequirement(minimum)
        }

        // Obtén los requisitos recomendados si están presentes
        val recommendedRequirement = if (requirementParts.size > 1) {
            requirementParts.getOrNull(1)?.let { recommended ->
                createPcRequirement(recommended)
            }
        } else {
            // Si no hay requisitos recomendados, utiliza los mismos requisitos mínimos
            minimumRequirement
        }

        if (minimumRequirement != null && recommendedRequirement != null) {
            // Crea el objeto PcRequirements con los requisitos mínimos y recomendados
            return GameDetail.PcRequirements(minimumRequirement, recommendedRequirement)
        }

        return null
    }
    @Suppress("UNUSED_PARAMETER")
    fun onChangeThemeButtonClick(view: View) {
        val preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val currentTheme = preferences.getString("theme", "light") // Obtén el tema actual
        val newTheme = if (currentTheme == "light") "dark" else "light" // Cambia el tema al opuesto del actual
        themeManager.changeTheme(newTheme)
    }
    @Suppress("UNUSED_PARAMETER")
    fun onBookmarkClick(view: View) {
        val intent = Intent(this, BookmarkActivity::class.java)
        startActivity(intent)
        finish()
    }
    @Suppress("UNUSED_PARAMETER")
    fun onHomeClick(view: View) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}