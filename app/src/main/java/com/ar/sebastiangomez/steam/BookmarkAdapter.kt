package com.ar.sebastiangomez.steam

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookmarkAdapter(private val context: Context, private val gamesList: List<Game>, private val onItemClick: (position: Int, gameId: String) -> Unit) : RecyclerView.Adapter<BookmarkAdapter.GameViewHolder>() {

    private val tag = "LOG-LIST"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_detail_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
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
            // Obtener la lista de juegos en caché
            val cachedGames = getGamesFromCache(context)

            // Encontrar el índice del juego a eliminar en la lista
            val indexToRemove = cachedGames.indexOfFirst { it.id == game.id }

            if (indexToRemove != -1) {
                // Remover el juego de la lista en caché
                cachedGames.removeAt(indexToRemove)

                // Guardar la lista actualizada en la caché
                saveGamesToCache(context, cachedGames)

                // Log de la acción y cualquier otra acción necesaria
                Log.d(tag, "Removed game from cache - ID: ${game.id}, Name: ${game.name}")

                // Iniciar la actividad de marcadores (o cualquier otra acción necesaria)
                val intent = Intent(holder.itemView.context, BookmarkActivity::class.java)
                holder.itemView.context.startActivity(intent)
            } else {
                // El juego no se encontró en la lista en caché
                Log.d(tag, "Game not found in cache - ID: ${game.id}, Name: ${game.name}")
            }
        }
    }

    private fun getGamesFromCache(context: Context): MutableList<Game> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("bookmark_data", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("games", "[]")
        val type = object : TypeToken<MutableList<Game>>() {}.type
        return Gson().fromJson(json, type)
    }
    private fun saveGamesToCache(context: Context, games: MutableList<Game>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("bookmark_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(games)
        editor.putString("games", json)
        editor.apply()
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