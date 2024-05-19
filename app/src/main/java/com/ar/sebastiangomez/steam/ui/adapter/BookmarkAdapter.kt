package com.ar.sebastiangomez.steam.ui.adapter

import android.content.Context
import android.content.Intent
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
import com.ar.sebastiangomez.steam.model.GameCached
import com.ar.sebastiangomez.steam.ui.BookmarkActivity
import com.ar.sebastiangomez.steam.ui.DetalleActivity
import com.ar.sebastiangomez.steam.utils.GamesCache
import com.bumptech.glide.Glide

class BookmarkAdapter(private val context: Context, private val gamesList: List<GameCached>, private val onItemClick: (position: Int, gameId: String) -> Unit) : RecyclerView.Adapter<BookmarkAdapter.GameViewHolder>() {

    private lateinit var gamesCache: GamesCache
    private val tag = "LOG-BOOKMARK-LIST"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_bookmark_item, parent, false)
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
            gamesCache.removeGameToCache(context, game.id, "BookmarkActivity")
        }
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
            // Mostrar el ProgressBar
            progressBar.visibility = View.VISIBLE
            //textView.text = game.name
            textNro.text = (totalItems - position).toString()
            Glide.with(itemView.context)
                .load(game.image)
                .centerCrop()
                .into(imageView)
        }
    }
}