package com.ar.sebastiangomez.steam

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.utils.GamesFromCache
import com.ar.sebastiangomez.steam.utils.ThemeHelper
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class DetalleActivity : AppCompatActivity() {
    private lateinit var gamesFromCache: GamesFromCache
    private lateinit var themeHelper: ThemeHelper
    private lateinit var progressBar : ProgressBar
    private lateinit var titleTxt : TextView
    private lateinit var descripcionTxt : TextView
    private lateinit var layoutDetalle : LinearLayout
    private lateinit var cardView : CardView
    private lateinit var cardDescription : CardView
    private lateinit var cardMin : CardView
    private lateinit var cardRec : CardView
    private lateinit var headerImg : ImageView
    private lateinit var themeButton : ImageButton
    private lateinit var textSO : TextView
    private lateinit var textProcesador : TextView
    private lateinit var textMemoria : TextView
    private lateinit var textGraficos : TextView
    private lateinit var textAlmacenamiento : TextView
    private lateinit var textSORec : TextView
    private lateinit var textProcesadorRec : TextView
    private lateinit var textMemoriaRec : TextView
    private lateinit var textGraficosRec : TextView
    private lateinit var textAlmacenamientoRec : TextView
    private lateinit var imageType : ImageView
    private lateinit var layoutSOMin : LinearLayout
    private lateinit var layoutProMin : LinearLayout
    private lateinit var layoutMemMin : LinearLayout
    private lateinit var layoutGrafMin : LinearLayout
    private lateinit var layoutAlmMin : LinearLayout
    private lateinit var layoutSORec : LinearLayout
    private lateinit var layoutProRec : LinearLayout
    private lateinit var layoutMemRec : LinearLayout
    private lateinit var layoutGrafRec : LinearLayout
    private lateinit var layoutAlmRec : LinearLayout
    private lateinit var layoutMin : LinearLayout
    private lateinit var layoutRec : LinearLayout
    private lateinit var imageButtonBookmark : ImageButton
    private val tag = "LOG-DETAIL"

    override fun onCreate(savedInstanceState: Bundle?) {
        gamesFromCache = GamesFromCache()
        themeHelper = ThemeHelper(this)
        themeHelper.applyTheme()
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
        imageButtonBookmark = findViewById(R.id.imageButtonBookmark)
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
        textSORec = findViewById(R.id.textSORec)
        textProcesadorRec = findViewById(R.id.textProcesadorRec)
        textMemoriaRec = findViewById(R.id.textMemoriaRec)
        textGraficosRec = findViewById(R.id.textGraficosRec)
        textAlmacenamientoRec = findViewById(R.id.textAlmacenamientoRec)
        imageType = findViewById(R.id.imageType)
        cardDescription = findViewById(R.id.cardDescription)
        cardMin = findViewById(R.id.cardMin)
        cardRec = findViewById(R.id.cardRec)

        layoutMin = findViewById(R.id.layoutMin)
        layoutRec = findViewById(R.id.layoutRec)

        layoutSOMin = findViewById(R.id.layoutSOMin)
        layoutProMin = findViewById(R.id.layoutProMin)
        layoutMemMin = findViewById(R.id.layoutMemMin)
        layoutGrafMin = findViewById(R.id.layoutGrafMin)
        layoutAlmMin = findViewById(R.id.layoutAlmMin)
        layoutSORec = findViewById(R.id.layoutSORec)
        layoutProRec = findViewById(R.id.layoutProRec)
        layoutMemRec = findViewById(R.id.layoutMemRec)
        layoutGrafRec = findViewById(R.id.layoutGrafRec)
        layoutAlmRec = findViewById(R.id.layoutAlmRec)

        getImageTheme() //Obtener valor del theme y cambiar el icono.
        val id = getId() //Obtener ID de HomeActivity por intent y hacer fetch de detalles.
        fetchGameDetails(id.toString()) //Fetch game details del ID.
    }

    private fun getId(): Int {
        val id = intent.extras!!.getInt("ID")
        Log.d(tag, "ID Game: $id")
        return id
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
                            Log.d(tag,"GAME DETAIL - ID: ${gameDetail.steam_appid}, Name: ${gameDetail.name}, Type: ${gameDetail.type}, ${gameDetail.header_image},Short description: ${gameDetail.short_description}")
                            Log.d(tag, "PC Requirements: ${gameDetail.pc_requirements}")
                            Log.d(tag, "PC Requirements Filter: $pcRequirements")

                            val cachedGames = gamesFromCache.getGamesFromCache(this@DetalleActivity)
                            val indexID = cachedGames.indexOfFirst { it.id == gameId }

                            if (indexID != -1) { // Si el juego está en la lista de favoritos
                                imageButtonBookmark.setImageResource(R.drawable.bookmarkdetailremove)
                                imageButtonBookmark.imageTintList = ColorStateList.valueOf(Color.parseColor("#914040"))
                                imageButtonBookmark.setOnClickListener {
                                    cachedGames.removeAt(indexID)
                                    // Guardar la lista actualizada en la caché
                                    gamesFromCache.saveGamesToCache(this@DetalleActivity, cachedGames)
                                    // Actualizar la interfaz de usuario según sea necesario
                                    imageButtonBookmark.setImageResource(R.drawable.bookmarkdetail)
                                    imageButtonBookmark.imageTintList = ColorStateList.valueOf(Color.parseColor("#F9F4FB"))
                                }
                            } else { // Si el juego no está en la lista de favoritos
                                imageButtonBookmark.setOnClickListener {
                                    imageButtonBookmark.setImageResource(R.drawable.bookmarkdetailremove)
                                    imageButtonBookmark.imageTintList = ColorStateList.valueOf(Color.parseColor("#914040"))
                                    Log.d(tag, "Log Button Add Bookmark - ID Game Add: ${gameDetail.steam_appid}, Game Name: ${gameDetail.name}")
                                    val intent = Intent(this@DetalleActivity, BookmarkActivity::class.java)
                                    intent.putExtra("game_id", gameDetail.steam_appid.toString())
                                    intent.putExtra("game_name", gameDetail.name)
                                    startActivity(intent)
                                }
                            }

                            // Titulo del juego
                            titleTxt.text = gameDetail.name

                            //Type Game/Dlc/Demo/Music
                            when (gameDetail.type) {
                                "dlc" -> {
                                    imageType.setImageResource(R.drawable.tagdlc)
                                }
                                "demo" -> {
                                    imageType.setImageResource(R.drawable.tagdemo)
                                }
                                "game" -> {
                                    imageType.setImageResource(R.drawable.taggame)
                                }
                                "music" -> {
                                    imageType.setImageResource(R.drawable.tagmusic)
                                }
                            }

                            // Cargar la imagen utilizando Glide
                            runOnUiThread {
                                // Verifica si la actividad aún es válida antes de cargar la imagen
                                if (!isDestroyed && !isFinishing) {
                                    Glide.with(this@DetalleActivity)
                                        .load(gameDetail.header_image) // URL de la imagen
                                        .placeholder(R.drawable.steamdb) // Placeholder mientras se carga la imagen (opcional)
                                        .error(R.drawable.error) // Imagen de error en caso de falla de carga (opcional)
                                        .into(headerImg) // Establecer la imagen en el ImageView
                                }
                            }

                            // Descripcion
                            if(gameDetail.short_description.isEmpty())
                            {
                                layoutDetalle.removeView(cardDescription)
                            }
                            descripcionTxt.text = gameDetail.short_description.replace(Regex("<br />|&quot;"), "")

                            // Requerimientos Minimos
                            if(pcRequirements?.minimum != null)
                            {
                                pcRequirements.minimum.os.takeIf { it.isNotEmpty() }?.let { textSO.text = it } ?: layoutMin.removeView(layoutSOMin)
                                pcRequirements.minimum.processor.takeIf { it.isNotEmpty() }?.let { textProcesador.text = it } ?: layoutMin.removeView(layoutProMin)
                                pcRequirements.minimum.memory.takeIf { it.isNotEmpty() }?.let { textMemoria.text = it } ?: layoutMin.removeView(layoutMemMin)
                                pcRequirements.minimum.graphics.takeIf { it.isNotEmpty() }?.let { textGraficos.text = it } ?: layoutMin.removeView(layoutGrafMin)
                                pcRequirements.minimum.storage.takeIf { it.isNotEmpty() }?.let { textAlmacenamiento.text = it } ?: layoutMin.removeView(layoutAlmMin)
                            }
                            else{
                                layoutDetalle.removeView(cardMin)
                            }
                            // Requerimientos Recomendados
                            if(pcRequirements?.recommended != null)
                            {
                                if(pcRequirements.recommended != pcRequirements.minimum)
                                {
                                    pcRequirements.recommended.os.takeIf { it.isNotEmpty() }?.let { textSORec.text = it } ?: layoutRec.removeView(layoutSORec)
                                    pcRequirements.recommended.processor.takeIf { it.isNotEmpty() }?.let { textProcesadorRec.text = it }?: layoutRec.removeView(layoutProRec)
                                    pcRequirements.recommended.memory.takeIf { it.isNotEmpty() }?.let { textMemoriaRec.text = it } ?: layoutRec.removeView(layoutMemRec)
                                    pcRequirements.recommended.graphics.takeIf { it.isNotEmpty() }?.let { textGraficosRec.text = it } ?: layoutRec.removeView(layoutGrafRec)
                                    pcRequirements.recommended.storage.takeIf { it.isNotEmpty() }?.let { textAlmacenamientoRec.text = it }?: layoutRec.removeView(layoutAlmRec)
                                }
                                else{
                                    layoutDetalle.removeView(cardRec)
                                }
                            }
                            else{
                                layoutDetalle.removeView(cardRec)
                            }

                            //Handler para ocultar progressBar y mostrar el layoutDetalle
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
            .replace("<br><ul class=\"bb_ul\"><li>Requires a 64-bit processor and operating system<br></li><li>", " ")
            .replace("OS *:", "OS:")
            .replace("minimum=", "")
            .replace("recommended=", "")
            .replace(Regex("<.*?>"), "") // Elimina etiquetas HTML
            .replace(",  Recommended:Requires a 64-bit processor and operating system", " Recommended: OS: Requires a 64-bit processor and operating system")
            .replace(Regex("Sound Card:"), "Sound:") // Elimina etiquetas HTML
            .replace(Regex(" space,"), "") // Elimina etiquetas HTML
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
            val soundcard = extractRequirement(requirementString, "Sound:")
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
        val recommendedRequirement = if (requirementParts.size > 1)
        {
            requirementParts.getOrNull(1)?.let { recommended ->
                createPcRequirement(recommended)
            }
        } else {
            // Si no hay requisitos recomendados, utiliza los mismos requisitos mínimos
            minimumRequirement
        }

        if (minimumRequirement != null || recommendedRequirement != null) {
            // Crea el objeto PcRequirements con los requisitos mínimos y recomendados
            return GameDetail.PcRequirements(minimumRequirement, recommendedRequirement)
        }
        return null
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

    @Suppress("UNUSED_PARAMETER", "DEPRECATION")
    fun onBackClick(view: View) {
        super.onBackPressed()
    }
}