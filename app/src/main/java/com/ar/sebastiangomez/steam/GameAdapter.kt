package com.ar.sebastiangomez.steam

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameAdapter(private val gamesList: List<Game>, private val onItemClick: (position: Int, gameId: String) -> Unit) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    private val tag = "LOG-LIST"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_custom_item, parent, false)
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
            Log.d(tag, "Log Button Add Bookmark - ID Position: $position, Game ID: ${game.id}")
            val intent = Intent(holder.itemView.context, BookmarkActivity::class.java)
            intent.putExtra("game_id", game.id)
            intent.putExtra("game_name", game.name)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val textView: TextView = itemView.findViewById(android.R.id.text1)
        private val textView: TextView = itemView.findViewById(R.id.textList)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonList)
        fun bind(game: Game) {
            textView.text = game.name
        }
    }
}