package com.ar.sebastiangomez.steam.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.model.Game
import com.ar.sebastiangomez.steam.utils.GamesFromCache

class BookmarkAdapter(private val context: Context, private val gamesList: List<Game>, private val onItemClick: (position: Int, gameId: String) -> Unit) : RecyclerView.Adapter<BookmarkAdapter.GameViewHolder>() {

    private lateinit var gamesFromCache: GamesFromCache
    private val tag = "LOG-BOOKMARK-LIST"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_detail_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        gamesFromCache = GamesFromCache()
        val game = gamesList[position]
        holder.bind(game, position, itemCount)

        holder.itemView.setOnClickListener {
            onItemClick.invoke(position, game.id) // Pasar el ID del juego al onItemClick
            val selectID = game.id.toInt()
            Log.d(tag,"ID Position: ${game.id} Select ID: $selectID")

            val intent = Intent(holder.itemView.context, DetalleActivity::class.java)
            intent.putExtra("ID", selectID)
            holder.itemView.context.startActivity(intent)
            (holder.itemView.context as BookmarkActivity).finish()
        }

        holder.imageButton.setOnClickListener {
            // Obtener la lista de juegos en caché
            val cachedGames = gamesFromCache.getGamesFromCache(context)

            // Encontrar el índice del juego a eliminar en la lista
            val indexToRemove = cachedGames.indexOfFirst { it.id == game.id }

            if (indexToRemove != -1) {
                // Remover el juego de la lista en caché
                cachedGames.removeAt(indexToRemove)

                // Guardar la lista actualizada en la caché
                gamesFromCache.saveGamesToCache(context, cachedGames)

                // Log de la acción y cualquier otra acción necesaria
                Log.d(tag, "Removed game from cache - ID: ${game.id}, Name: ${game.name}")

                // Iniciar la actividad de marcadores (o cualquier otra acción necesaria)
                (holder.itemView.context as BookmarkActivity).recreate()
            } else {
                // El juego no se encontró en la lista en caché
                Log.d(tag, "Game not found in cache - ID: ${game.id}, Name: ${game.name}")
            }
        }
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textList)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonList)
        private val textNro: TextView = itemView.findViewById(R.id.textNro)
        fun bind(game: Game, position: Int, totalItems: Int) {
            textView.text = game.name
            textNro.text = (totalItems - position).toString()
        }
    }
}