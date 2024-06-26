package com.ar.sebastiangomez.steam.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.data.GamesRepository
import com.ar.sebastiangomez.steam.model.GameCached
import com.ar.sebastiangomez.steam.ui.BookmarkActivity
import com.ar.sebastiangomez.steam.ui.DetalleActivity
import com.ar.sebastiangomez.steam.utils.GamesCache
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkAdapter(private val context: Context,
                      private var gamesList: List<GameCached>,
                      private val onItemClick: (position: Int, gameId: String) -> Unit) : RecyclerView.Adapter<BookmarkAdapter.GameViewHolder>() {

    private val gamesRepository: GamesRepository = GamesRepository()
    private lateinit var gamesCache: GamesCache
    private val tag = "LOG-BOOKMARK-LIST"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bookmark_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        gamesCache = GamesCache()
        val game = gamesList[position]
        holder.bind(game, position, itemCount)

        holder.itemView.setOnClickListener {
            onItemClick.invoke(position, game.id) // Pasar el ID del juego al onItemClick
            val selectID = game.id.toInt()
            Log.d(tag, "ID Position: ${game.id} Select ID: $selectID")

            val intent = Intent(holder.itemView.context, DetalleActivity::class.java)
            intent.putExtra("ID", selectID)
            holder.itemView.context.startActivity(intent)
            (holder.itemView.context as BookmarkActivity).finish()
        }

        holder.imageButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                gamesRepository.removeGameFirestore(context, game.id, game.name, "BookmarkActivity")
                holder.imageButton.setImageResource(R.drawable.bookmarkadd)
                holder.imageButton.setBackgroundColor(Color.parseColor("#495d92"))
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(lista: List<GameCached>) {
        gamesList = lista
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //private val textView: TextView = itemView.findViewById(R.id.textList)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonList)
        private val textNro: TextView = itemView.findViewById(R.id.textNro)
        private val imageView: ImageView = itemView.findViewById(R.id.imageBookmark)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(game: GameCached, position: Int, totalItems: Int) {
            // Mostrar el ProgressBar antes de cargar la imagen
            progressBar.visibility = View.VISIBLE
            //textNro.text = game.name
            textNro.text = (totalItems - position).toString()

            // Cargar la imagen usando Glide con un listener para manejar la visibilidad del ProgressBar
            Glide.with(itemView.context)
                .load(game.image)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Ocultar el ProgressBar si la carga de la imagen falla
                        progressBar.visibility = View.INVISIBLE
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Ocultar el ProgressBar cuando la imagen haya sido cargada
                        progressBar.visibility = View.INVISIBLE
                        return false
                    }
                })
                .into(imageView)
        }
    }
}