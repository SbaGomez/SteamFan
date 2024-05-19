package com.ar.sebastiangomez.steam.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.data.GamesRepository
import com.ar.sebastiangomez.steam.model.Game
import com.ar.sebastiangomez.steam.model.GameCached
import com.ar.sebastiangomez.steam.ui.BookmarkActivity
import com.ar.sebastiangomez.steam.ui.DetalleActivity
import com.ar.sebastiangomez.steam.utils.GamesCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamesAdapter(private val context: Context, private val gamesList: List<Game>, private val onItemClick: (position: Int, gameId: String) -> Unit) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

    private lateinit var gamesCache: GamesCache
    private val tag = "LOG-GAMES-LIST"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_custom_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        gamesCache = GamesCache()
        val game = gamesList[position]
        holder.bind(game)

        holder.itemView.setOnClickListener {
            onItemClick.invoke(position, game.id) // Pasar el ID del juego al onItemClick
            val selectID = game.id.toInt()
            Log.d(tag,"ID Position: ${game.id} Select ID: $selectID")

            val intent = Intent(holder.itemView.context, DetalleActivity::class.java)
            intent.putExtra("ID", selectID)
            holder.itemView.context.startActivity(intent)
        }

        holder.imageButton.setOnClickListener {
            Log.d(tag, "Log Button Add Bookmark - ID Position: $position, Game ID: ${game.id}")
            val gamesRepository = GamesRepository()
            CoroutineScope(Dispatchers.Main).launch {
                val imageUrl = gamesRepository.getImage(game.id)
                val cachedGame = GameCached(game.id, game.name, imageUrl.toString())
                // Agregar el juego a la lista en caché
                gamesCache.addGameToCache(context, cachedGame)
                Toast.makeText(context, "Agregaste - ${game.name} - a favoritos.", Toast.LENGTH_LONG).show()
                val intent = Intent(holder.itemView.context, BookmarkActivity::class.java)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textList)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonList)
        fun bind(game: Game) {
            textView.text = game.name
        }
    }
}