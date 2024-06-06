package com.ar.sebastiangomez.steam.ui

import android.annotation.SuppressLint
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
import com.ar.sebastiangomez.steam.model.GameCached
import com.ar.sebastiangomez.steam.model.GameDetail
import com.ar.sebastiangomez.steam.utils.GamesCache
import com.ar.sebastiangomez.steam.utils.ThemeHelper
import com.ar.sebastiangomez.steam.utils.Utils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DetalleActivity : AppCompatActivity() {
    private lateinit var utils: Utils
    private lateinit var gamesCache: GamesCache
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
    private lateinit var linearButtons : LinearLayout
    private lateinit var linearDescuento : LinearLayout
    private lateinit var linearPrecioUSD : LinearLayout
    private lateinit var linearPrecioARG : LinearLayout
    private lateinit var linearLanzamiento : LinearLayout
    private lateinit var textFechaLanzamiento : TextView
    private lateinit var textDescuento : TextView
    private lateinit var textPrecioUSD : TextView
    private lateinit var textPrecioARG : TextView
    private lateinit var textDolarTarjeta : TextView
    private lateinit var textTitlePrecioUSD : TextView
    private lateinit var buttonComprar : Button
    private lateinit var buttonVer : Button
    private var firebaseAuth = FirebaseAuth.getInstance()

    private val tag = "LOG-DETAIL"

    private val gamesRepository: GamesRepository = GamesRepository()
    private val dolarRepository: DolarRepository = DolarRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        gamesCache = GamesCache()
        utils = Utils()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViewObject()
        ThemeHelper.setButtonImageBasedOnTheme(themeButton, this)
        themeButton.setOnClickListener {
            ThemeHelper.toggleTheme(this)
        }
        getDetails(getId().toString()) //Fetch game details del ID.
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
        linearButtons = findViewById(R.id.linearButtons)
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
        textTitlePrecioUSD = findViewById(R.id.textTitlePrecioUSD)
        buttonComprar = findViewById(R.id.buttonComprar)
        buttonVer = findViewById(R.id.buttonVer)
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
            val gameDetail = gamesRepository.getDetails(gameId, this@DetalleActivity)
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

    // Función para actualizar la interfaz de usuario según sea necesario
    private fun updateUI(isInFavorites: Boolean) {
        val imageResource = if (isInFavorites) {
            R.drawable.bookmarkadd
        } else {
            R.drawable.bookmarkdel
        }
        val imageTint = if (isInFavorites) {
            Color.parseColor("#F9F4FB")
        } else {
            Color.parseColor("#914040")
        }
        imageButtonBookmark.setImageResource(imageResource)
        imageButtonBookmark.imageTintList = ColorStateList.valueOf(imageTint)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private suspend fun setData(gameDetail : GameDetail){
        val pcRequirements = utils.parsePcRequirements(gameDetail.pcRequirements.toString())
        lifecycleScope.launch {
            val exists = gamesRepository.exists(gameDetail.steamAppId.toString())

            // Actualiza el UI inicial
            updateUI(!exists)

            // Listener de clic para agregar o eliminar de favoritos
            imageButtonBookmark.setOnClickListener {
                lifecycleScope.launch {
                    val existsGame = gamesRepository.exists(gameDetail.steamAppId.toString())
                    try {
                        if (existsGame) { // Si el juego está en la lista de favoritos, eliminarlo
                            gamesRepository.removeGameCached(
                                this@DetalleActivity,
                                gameDetail.steamAppId.toString(),
                                gameDetail.name,
                                null
                            )

                            // Actualizar el UI
                            updateUI(true)
                        } else { // Si el juego no está en la lista de favoritos, agregarlo
                            Log.d(
                                tag,
                                "Log Button Add Bookmark - ID Game Add: ${gameDetail.steamAppId}, Game Name: ${gameDetail.name}"
                            )
                            val userId = firebaseAuth.currentUser?.uid
                            val cachedGame = userId?.let {
                                GameCached(
                                    gameDetail.steamAppId.toString(),
                                    gameDetail.name,
                                    gameDetail.headerImage,
                                    it
                                )
                            }
                            // Agregar el juego a la lista en caché
                            if (cachedGame != null) {
                                gamesRepository.saveGameCached(this@DetalleActivity, cachedGame)
                            }
                            // Después de agregar, actualiza el índice
                            // Actualizar el UI
                            updateUI(false)
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "Error al actualizar la lista de favoritos: ${e.message}")
                    }
                }
            }
        }

        // Titulo del juego
        titleTxt.text = gameDetail.name

        //Type Game/Dlc/Demo/Music
        when (gameDetail.type) {
            "dlc" -> {
                imageType.setImageResource(R.drawable.dlc)
            }
            "demo" -> {
                imageType.setImageResource(R.drawable.demo)
            }
            "game" -> {
                imageType.setImageResource(R.drawable.game)
            }
            "music" -> {
                imageType.setImageResource(R.drawable.music)
            }
        }

        if(gameDetail.releaseDate.date.isEmpty())
        {
            linearInformacion.removeView(linearLanzamiento)
        }else {
            textFechaLanzamiento.text = gameDetail.releaseDate.date
        }

        if(gameDetail.isFree)
        {
            when (gameDetail.type) {
                "dlc" -> {
                    buttonVer.text = "Ver dlc"
                    buttonVer.setBackgroundColor(Color.parseColor("#A454B0"))
                }
                "demo" -> {
                    buttonVer.text = "Ver demo"
                    buttonVer.setBackgroundColor(Color.parseColor("#55B269"))
                }
                "game" -> {
                    buttonVer.text = "Ver juego"
                    buttonVer.setBackgroundColor(Color.parseColor("#5B7DD3"))
                }
                "music" -> {
                    buttonVer.text = "Ver Soundtrack"
                    buttonVer.setBackgroundColor(Color.parseColor("#B2555B"))
                }
            }
        }

        if(gameDetail.releaseDate.comingSoon)
        {
            if (gameDetail.type == "game")
            {
                buttonVer.text = "Ver juego"
                buttonVer.setBackgroundColor(Color.parseColor("#5B7DD3"))
            }
        }

        buttonVer.setOnClickListener {
            val url = "https://store.steampowered.com/app/${gameDetail.steamAppId}"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        if(gameDetail.isFree || gameDetail.releaseDate.comingSoon || gameDetail.priceOverview == null)
        {
            linearInformacion.removeView(linearPrecioARG)
            linearPrincipal.removeView(linearPrecioUSD)
        }
        else{
            linearPrincipal.removeView(linearButtons)
            //Log.d(tag, "Log Price Overview - ID Game: ${gameDetail.steam_appid} - ${gameDetail.price_overview}")
            val pricePattern = """\$\s?([0-9]+(?:\.[0-9]+)?)\s?(?:USD)?""".toRegex()
            val priceDouble = pricePattern.find(gameDetail.priceOverview.finalFormatted)?.groups?.get(1)?.value?.toDoubleOrNull() ?: 0.0
            val dolartarjeta = dolarRepository.getDolarTarjeta()
            val ventaTarjetaNoNulo: Double = dolartarjeta?.venta ?: 0.0
            textDolarTarjeta.text = "$ $ventaTarjetaNoNulo ARS"
            val priceArg = priceDouble * ventaTarjetaNoNulo
            val priceArgFormatted = String.format("%.2f", priceArg)
            textPrecioARG.text = "$ $priceArgFormatted ARS"
            if (gameDetail.priceOverview.initialFormatted.isEmpty()) {
                textPrecioUSD.text = gameDetail.priceOverview.finalFormatted
                textTitlePrecioUSD.text = "Precio en USD:"
            } else {
                textPrecioUSD.text = gameDetail.priceOverview.initialFormatted
            }
            if(gameDetail.priceOverview.discountPercent > 0)
            {
                textDescuento.text = gameDetail.priceOverview.finalFormatted
            }
            else{
                linearPrecioARG.removeView(linearDescuento)
            }
            when (gameDetail.type) {
                "dlc" -> {
                    buttonComprar.text = "Comprar dlc"
                    buttonComprar.setBackgroundColor(Color.parseColor("#A454B0"))
                }
                "music" -> {
                    buttonComprar.text = "Comprar Soundtrack"
                    buttonComprar.setBackgroundColor(Color.parseColor("#B2555B"))
                }
            }
        }

        buttonComprar.setOnClickListener {
            val url = "https://store.steampowered.com/app/${gameDetail.steamAppId}"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        // Cargar la imagen utilizando Glide
        runOnUiThread {
            // Verifica si la actividad aún es válida antes de cargar la imagen
            if (!isDestroyed && !isFinishing) {
                Glide.with(this@DetalleActivity)
                    .load(gameDetail.headerImage) // URL de la imagen
                    .placeholder(R.drawable.progressbar) // Placeholder mientras se carga la imagen (opcional)
                    .error(R.drawable.error) // Imagen de error en caso de falla de carga (opcional)
                    .into(headerImg) // Establecer la imagen en el ImageView
            }
        }

        // Descripcion
        if(gameDetail.shortDescription.isEmpty())
        {
            layoutDetalle.removeView(cardDescription)
        }
        descripcionTxt.text = gameDetail.shortDescription.replace(Regex("<br />|&quot;"), "")

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