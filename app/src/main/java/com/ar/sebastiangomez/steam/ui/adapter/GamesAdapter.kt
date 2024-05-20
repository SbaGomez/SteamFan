package com.ar.sebastiangomez.steam.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.ar.sebastiangomez.steam.ui.DetalleActivity
import com.ar.sebastiangomez.steam.utils.GamesCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamesAdapter(private val context: Context,
                   private var gamesList: List<Game>,
                   private val onItemClick: (position: Int, gameId: String) -> Unit) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

    private lateinit var gamesCache: GamesCache
    private val tag = "LOG-GAMES-LIST"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_game_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        gamesCache = GamesCache()
        val game = gamesList[position]
        holder.bind(game)
        updateBookmarkButton(holder, game)

        holder.itemView.setOnClickListener {
            onItemClick.invoke(position, game.id)
            val selectID = game.id.toInt()
            Log.d(tag, "ID Position: ${game.id} Select ID: $selectID")

            val intent = Intent(holder.itemView.context, DetalleActivity::class.java)
            intent.putExtra("ID", selectID)
            holder.itemView.context.startActivity(intent)
        }

        holder.imageButton.setOnClickListener {
            val isBookmarked = gamesCache.exists(context, game.id)

            if (isBookmarked) {
                gamesCache.removeGameToCache(context, game.id, "HomeActivity")
                updateBookmarkButton(holder, game)
            } else {
                Log.d(tag, "Log Button Add Bookmark - ID Position: $position, Game ID: ${game.id}")
                val gamesRepository = GamesRepository()
                CoroutineScope(Dispatchers.Main).launch {
                    val detail = gamesRepository.getDetails(game.id)
                    if(detail != null)
                    {
                        val imageUrl = gamesRepository.getImage(game.id)
                        val cachedGame = GameCached(game.id, game.name, imageUrl.toString())
                        gamesCache.addGameToCache(context, cachedGame)
                        updateBookmarkButton(holder, game)
                    }
                    else{
                        Toast.makeText(context, "El juego - ${game.name} - no tiene datos.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(lista: List<Game>) {
        gamesList = lista
        this.notifyDataSetChanged()
    }

    private fun updateBookmarkButton(holder: GameViewHolder, game: Game) {
        val isBookmarked = gamesCache.exists(context, game.id)
        if (isBookmarked) {
            holder.imageButton.setImageResource(R.drawable.bookmarkdel)
            holder.imageButton.setBackgroundColor(Color.parseColor("#9A4040"))
        } else {
            holder.imageButton.setImageResource(R.drawable.bookmarkadd)
            holder.imageButton.setBackgroundColor(Color.parseColor("#495d92"))
        }
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textList)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonList)
        fun bind(game: Game) {
            textView.text = game.name
        }
    }
}
