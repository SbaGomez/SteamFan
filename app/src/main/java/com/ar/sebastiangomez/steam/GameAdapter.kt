package com.ar.sebastiangomez.steam

import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameAdapter(private val gamesList: List<Game>, private val onItemClick: (position: Int, gameId: String) -> Unit) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_custom_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gamesList[position]
        holder.bind(game)

        holder.textView.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.title_games_dark_background))
        holder.textView.setTextColor(holder.itemView.context.resources.getColor(R.color.md_theme_dark_background))
        holder.textView.setTypeface(null, Typeface.BOLD)

        holder.itemView.setOnClickListener {
            onItemClick.invoke(position, game.id) // Pasar el ID del juego al onItemClick
            var selectID = game.id.toInt()
            Log.d("ID POSICION:", game.id)
            Log.d("SELECT ID:", selectID.toString())

            val intent = Intent(holder.itemView.context, DetalleActivity::class.java)
            intent.putExtra("ID", selectID)
            holder.itemView.context.startActivity(intent)
        }

        holder.imageButton.setOnClickListener {
            // Aquí se ejecuta la acción cuando se hace clic en el ImageButton
            // Puedes llamar a una función o realizar cualquier acción necesaria
            Log.d("ImageButton Clicked", "Position: $position, Game ID: ${game.id}")
        }
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val textView: TextView = itemView.findViewById(android.R.id.text1)
        val textView: TextView = itemView.findViewById(R.id.textList)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonList)
        fun bind(game: Game) {
            textView.text = game.name
        }
    }
}