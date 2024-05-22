package com.ar.sebastiangomez.steam.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
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
import androidx.lifecycle.lifecycleScope
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.data.DolarRepository
import com.ar.sebastiangomez.steam.data.GamesRepository
import com.ar.sebastiangomez.steam.model.Dolar
import com.ar.sebastiangomez.steam.model.GameCached
import com.ar.sebastiangomez.steam.model.GameDetail
import com.ar.sebastiangomez.steam.model.PcRequirement
import com.ar.sebastiangomez.steam.model.PcRequirements
import com.ar.sebastiangomez.steam.utils.GamesCache
import com.ar.sebastiangomez.steam.utils.ThemeHelper
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class DetalleActivity : AppCompatActivity() {
    private lateinit var gamesCache: GamesCache
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

    private lateinit var linearInformacion : LinearLayout
    private lateinit var linearPrincipal : LinearLayout
    private lateinit var linearDescuento : LinearLayout
    private lateinit var linearPrecioUSD : LinearLayout
    private lateinit var linearPrecioARG : LinearLayout
    private lateinit var linearLanzamiento : LinearLayout
    private lateinit var textFechaLanzamiento : TextView
    private lateinit var textDescuento : TextView
    private lateinit var textPrecioUSD : TextView
    private lateinit var textPrecioARG : TextView
    private lateinit var textDolarTarjeta : TextView
    private lateinit var buttonComprar : Button

    private val tag = "LOG-DETAIL"

    private val gamesRepository: GamesRepository = GamesRepository()
    private val dolarRepository: DolarRepository = DolarRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        gamesCache = GamesCache()
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
        getImageTheme() //Obtener valor del theme y cambiar el icono.
        val id = getId() //Obtener ID de HomeActivity por intent y hacer fetch de detalles.
        getDetails(id.toString()) //Fetch game details del ID.
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

        linearInformacion = findViewById(R.id.linearInformacion)
        linearDescuento = findViewById(R.id.linearDescuento)
        linearPrincipal = findViewById(R.id.linearPrincipal)
        linearPrecioARG = findViewById(R.id.linearPrecioARG)
        linearPrecioUSD = findViewById(R.id.linearPrecioUSD)
        linearLanzamiento = findViewById(R.id.linearLanzamiento)
        textFechaLanzamiento = findViewById(R.id.textFechaLanzamiento)
        textDescuento = findViewById(R.id.textDescuento)
        textPrecioUSD = findViewById(R.id.textPrecioUSD)
        textPrecioARG = findViewById(R.id.textPrecioARG)
        textDolarTarjeta = findViewById(R.id.textDolarTarjeta)
        buttonComprar = findViewById(R.id.buttonComprar)
    }

    private fun getId(): Int {
        val id = intent.extras!!.getInt("ID")
        Log.d(tag, "ID Game: $id")
        return id
    }

    private fun getDetails(gameId: String) {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            val gameDetail = gamesRepository.getDetails(gameId)
            if (gameDetail != null) {
                setData(gameDetail)
                Handler(Looper.getMainLooper()).postDelayed({
                    progressBar.visibility = View.INVISIBLE
                    layoutDetalle.visibility = View.VISIBLE
                }, 2000)
            } else {
                cardView.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private suspend fun setData(gameDetail : GameDetail){
        val pcRequirements = parsePcRequirements(gameDetail.pc_requirements.toString())
        val cachedGames = gamesCache.getGamesFromCache(this@DetalleActivity)
        var indexID = cachedGames.indexOfFirst { it.id == gameDetail.steam_appid.toString() }

        // Función para actualizar la interfaz de usuario según sea necesario
        fun updateUI(isInFavorites: Boolean) {
            if (isInFavorites) {
                imageButtonBookmark.setImageResource(R.drawable.bookmarkdetail)
                imageButtonBookmark.imageTintList = ColorStateList.valueOf(Color.parseColor("#F9F4FB"))
            } else {
                imageButtonBookmark.setImageResource(R.drawable.bookmarkdetailremove)
                imageButtonBookmark.imageTintList = ColorStateList.valueOf(Color.parseColor("#914040"))
            }
        }

        // Actualiza el UI inicial
        updateUI(indexID == -1)

        // Listener de clic para agregar o eliminar de favoritos
        imageButtonBookmark.setOnClickListener {
            try {
                if (indexID != -1) { // Si el juego está en la lista de favoritos, eliminarlo
                    cachedGames.removeAt(indexID)
                    // Guardar la lista actualizada en la caché
                    gamesCache.saveGamesToCache(this@DetalleActivity, cachedGames)
                    // Actualizar el UI
                    updateUI(true)
                    // Actualizar el índice
                    indexID = -1
                } else { // Si el juego no está en la lista de favoritos, agregarlo
                    Log.d(tag, "Log Button Add Bookmark - ID Game Add: ${gameDetail.steam_appid}, Game Name: ${gameDetail.name}")
                    val cachedGame = GameCached(
                        gameDetail.steam_appid.toString(),
                        gameDetail.name,
                        gameDetail.header_image
                    )
                    // Agregar el juego a la lista en caché
                    gamesCache.addGameToCache(this@DetalleActivity, cachedGame)
                    val intent = Intent(this@DetalleActivity, BookmarkActivity::class.java)
                    startActivity(intent)
                    finish()
                    // Después de agregar, actualiza el índice
                    indexID = cachedGames.indexOfFirst { it.id == gameDetail.steam_appid.toString() }
                    // Actualizar el UI
                    updateUI(false)
                }
            } catch (e: Exception) {
                Log.e(tag, "Error al actualizar la lista de favoritos: ${e.message}")
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
        if(gameDetail.release_date.date.isEmpty())
        {
            linearInformacion.removeView(linearLanzamiento)
        }else {
            textFechaLanzamiento.text = gameDetail.release_date.date
        }

        if(gameDetail.price_overview.discount_percent > 0)
        {
            textDescuento.text = "${gameDetail.price_overview.discount_percent}%"
        }
        else{
            linearPrecioARG.removeView(linearDescuento)
        }

        val priceOverview =  gameDetail.price_overview
        if(gameDetail.is_free || gameDetail.release_date.coming_soon || priceOverview == null)
        {
            linearInformacion.removeView(linearPrecioARG)
            linearPrincipal.removeView(linearPrecioUSD)
        }
        else{
            Log.d(tag, "Log Price Overview - ID Game: ${gameDetail.steam_appid} - ${gameDetail.price_overview}")
            val priceDolar = gameDetail.price_overview.final_formatted
            val pricePattern = """\$\s?([0-9]+(?:\.[0-9]+)?)\s?(?:USD)?""".toRegex()
            val priceDouble = pricePattern.find(gameDetail.price_overview.final_formatted)?.groups?.get(1)?.value?.toDoubleOrNull() ?: 0.0
            val dolartarjeta: Dolar? = dolarRepository.getDolarTarjeta()
            val ventaTarjetaNoNulo: Double = dolartarjeta?.venta ?: 0.0
            textDolarTarjeta.text = "$ $ventaTarjetaNoNulo ARS"
            val priceArg = priceDouble * ventaTarjetaNoNulo
            val priceArgFormatted = String.format("%.2f", priceArg)
            textPrecioARG.text = "$ $priceArgFormatted ARS"
            textPrecioUSD.text = priceDolar
        }

        buttonComprar.setOnClickListener {
            val url = "https://store.steampowered.com/app/${gameDetail.steam_appid}"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        // Cargar la imagen utilizando Glide
        runOnUiThread {
            // Verifica si la actividad aún es válida antes de cargar la imagen
            if (!isDestroyed && !isFinishing) {
                Glide.with(this@DetalleActivity)
                    .load(gameDetail.header_image) // URL de la imagen
                    .placeholder(R.drawable.progressbar) // Placeholder mientras se carga la imagen (opcional)
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
    }

    private fun parsePcRequirements(pcRequirementsString: String): PcRequirements? {
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
        fun createPcRequirement(requirementString: String): PcRequirement? {
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
                PcRequirement(
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
            return PcRequirements(minimumRequirement, recommendedRequirement)
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
        themeHelper.changeTheme(newTheme, this)
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
    fun onBackClick(view: View) {
        super.onBackPressed()
    }
}