package com.ar.sebastiangomez.steam.ui.adapter

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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamesAdapter(
    private val context: Context,
    private var gamesList: List<Game>,
    private val onItemClick: (position: Int, gameId: String) -> Unit
) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

    private val gamesRepository: GamesRepository = GamesRepository()
    private val tag = "LOG-GAMES-LIST"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_game_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gamesList.getOrNull(position)
        if (game != null) {
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
                CoroutineScope(Dispatchers.Main).launch {
                    val isBookmarked = gamesRepository.exists(game.id)
                    if (isBookmarked) {
                        gamesRepository.removeGameCached(context, game.id, game.name, "HomeActivity")
                    } else {
                        Log.d(tag, "Log Button Add Bookmark - ID Position: $position, Game ID: ${game.id}")
                        val isSuccess = gamesRepository.isGameSuccess(game.id)
                        if (isSuccess == true) {
                            val imageUrl = gamesRepository.getImage(game.id)
                            val cachedGame = GameCached(game.id, game.name, imageUrl.toString(), getCurrentUserId())
                            gamesRepository.saveGameCached(context, cachedGame)
                        } else {
                            Toast.makeText(context, "El juego - ${game.name} - no tiene datos.", Toast.LENGTH_LONG).show()
                        }
                    }
                    updateBookmarkButton(holder, game)
                }
            }
        } else {
            Log.e(tag, "Invalid index: $position, Size: ${gamesList.size}")
        }
    }

    override fun onViewRecycled(holder: GameViewHolder) {
        super.onViewRecycled(holder)
        // Restablecer el estado del botón cuando la vista se recicla
        holder.imageButton.setImageResource(R.drawable.bookmarkadd)
        holder.imageButton.setBackgroundColor(Color.parseColor("#495d92"))
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    fun updateItems(lista: List<Game>) {
        gamesList = lista
        notifyDataSetChanged()
    }

    private fun updateBookmarkButton(holder: GameViewHolder, game: Game) {
        CoroutineScope(Dispatchers.Main).launch {
            val isBookmarked = gamesRepository.exists(game.id)
            if (isBookmarked) {
                holder.imageButton.setImageResource(R.drawable.bookmarkdel)
                holder.imageButton.setBackgroundColor(Color.parseColor("#9A4040"))
            } else {
                holder.imageButton.setImageResource(R.drawable.bookmarkadd)
                holder.imageButton.setBackgroundColor(Color.parseColor("#495d92"))
            }
        }
    }

    private fun getCurrentUserId(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid ?: "default_user"
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textList)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonList)
        fun bind(game: Game) {
            textView.text = game.name
        }
    }
}